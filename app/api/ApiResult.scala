package api

import api.ApiError._
import api.ApiResponse._
import domain.service.ApiLogService
import models.service.ApiLogService
import play.api.Logger
import play.api.cache.CacheApi
import play.api.i18n.Lang
import play.api.libs.json.Json._
import play.api.libs.json._
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}

/*
* The result of an ApiRequest.
*/
trait ApiResult {
  val status: Int
  val json: JsValue
  val headers: Seq[(String, String)]

  /*
	* Envelopes the resulting JSON in case the API client couldn't access to the headers
	*/
  def envelopedJson(implicit lang: Lang): JsValue = Json.obj(
    "data" -> json,
    "status" -> status,
    "headers" -> JsObject((headers ++ ApiUtil.basicHeaders).map(h => h._1 -> JsString(h._2)))
  )

  def saveLog[R <: RequestHeader](implicit request: ApiRequestHeader[R], apiLogger: ApiLogService, cache:CacheApi) : ApiResult = {
    apiLogger.insert(status, json)
    this
  }

  /*
	* Returns a Result with the ApiResult information.
	* If needed, it envelopes the resulting JSON in case the API client couldn't access to the headers
	*/
  def toResult[R <: RequestHeader](implicit request: R, lang: Lang): Result = {
    val envelope = request.getQueryString("envelope") == Some("true")
    toResult(envelope)
  }

  def toResult(envelope: Boolean = false)(implicit lang: Lang): Result = {
    val js = if (envelope) envelopedJson else json
    (status match {
      case STATUS_CREATED => if (js == JsNull) Created else Created(js)
      case STATUS_ACCEPTED => if (js == JsNull) Accepted else Accepted(js)
      case STATUS_NOCONTENT => NoContent
      case s if s < 300 => if (js == JsNull) Ok else Ok(js)

      case STATUS_BADREQUEST => BadRequest(js)
      case STATUS_UNAUTHORIZED => Unauthorized(js)
      case STATUS_FORBIDDEN => Forbidden(js)
      case STATUS_NOTFOUND => NotFound(js)
      case STATUS_REDIRECT => {
        val uri = (js \ "uri").get.toString().tail.init
        Redirect(uri)
      }
      case s if s > 400 && s < 500 => BadRequest(js)
      case _ => InternalServerError(js)
    }).withHeaders((headers ++ ApiUtil.basicHeaders): _*)
  }
}