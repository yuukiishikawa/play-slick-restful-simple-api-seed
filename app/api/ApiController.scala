package api

import api.ApiError._
import domain.models.auth.ApiKey
import domain.service.ApiToken._
import domain.service.{ApiLogService, AuthRepo}
import org.joda.time.DateTime
import play.api.cache.CacheApi
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/*
* Controller for the API
*/
trait ApiController extends Controller with I18nSupport {

  val messagesApi: MessagesApi

  //////////////////////////////////////////////////////////////////////
  // Implicit transformation utilities

  implicit def objectToJson[T](o: T)(implicit tjs: Writes[T]): JsValue = Json.toJson(o)

  implicit def result2FutureResult(r: ApiResult): Future[ApiResult] = Future.successful(r)

  //////////////////////////////////////////////////////////////////////
  // Custom Actions

  def PlainApiAction(action: ApiRequest[Unit] => Future[ApiResult])(implicit apiLogger: ApiLogService, cache: CacheApi) = PlainApiActionWithParser(parse.empty)(action)

  def ApiAction(action: ApiRequest[Unit] => Future[ApiResult])(implicit apiLogger: ApiLogService,cache: CacheApi) = ApiActionWithParser(parse.empty)(action)

  def ApiActionWithBody(action: ApiRequest[JsValue] => Future[ApiResult])(implicit apiLogger: ApiLogService,cache: CacheApi) = ApiActionWithParser(parse.json)(action)

  def SecuredApiAction(action: SecuredApiRequest[Unit] => Future[ApiResult])(implicit cache: CacheApi, authRepo: AuthRepo, apiLogger: ApiLogService) = SecuredApiActionWithParser(parse.empty)(action)

  def SecuredApiActionWithBody(action: SecuredApiRequest[JsValue] => Future[ApiResult])(implicit cache: CacheApi, authRepo: AuthRepo, apiLogger: ApiLogService) = SecuredApiActionWithParser(parse.json)(action)

  // Creates an Action checking that the Request has all the common necessary headers with their correct values (X-Api-Key, Date)
  private def ApiActionCommonBody[A](parser: BodyParser[A])(action: (ApiRequest[A], String, DateTime) => Future[ApiResult])
                                (implicit apiLogger: ApiLogService, cache : CacheApi) = Action.async(parser) { implicit request =>

    implicit val apiRequest = ApiRequest(request)
    val futureApiResult: Future[ApiResult] = apiRequest.maybeApiKey match {
      case None => errorApiKeyNotFound
      case Some(apiKey) => action(apiRequest, apiKey, null)
    }
    futureApiResult.map {
      case error: ApiError => error.saveLog.toResult
      case response: ApiResponse => response.saveLog.toResult
    }
  }

  private def PlainApiActionBody[A](parser: BodyParser[A])(action: (ApiRequest[A], String, DateTime) => Future[ApiResult])
                                    (implicit apiLogger: ApiLogService, cache : CacheApi) = Action.async(parser) { implicit request =>

    implicit val apiRequest = ApiRequest(request)
    val futureApiResult: Future[ApiResult] = action(apiRequest, "", null)

    futureApiResult.map {
      case error: ApiError => error.saveLog.toResult
      case response: ApiResponse => response.toResult
    }

  }

  private def ApiActionWithParser[A](parser: BodyParser[A])(action: ApiRequest[A] => Future[ApiResult])
                                    (implicit apiLogger: ApiLogService, cache : CacheApi) = ApiActionCommonBody(parser) { (apiRequest, apiKey, date) =>
    ApiKey.isActive(apiKey).flatMap {
      _ match {
        case None => errorApiKeyUnknown
        case Some(false) => errorApiKeyDisabled
        case Some(true) => action(apiRequest)
      }
    }
  }

  private def PlainApiActionWithParser[A](parser: BodyParser[A])(action: ApiRequest[A] => Future[ApiResult])
                                        (implicit apiLogger: ApiLogService,cache : CacheApi) = PlainApiActionBody(parser) { (apiRequest, apiKey, date) =>
    action(apiRequest)
  }

  // Secured Api Action that requires authentication. It checks the Request has the correct X-Auth-Token heaader
  private def SecuredApiActionWithParser[A](parser: BodyParser[A])(action: SecuredApiRequest[A] => Future[ApiResult])
                                           (implicit cache: CacheApi, authRepo: AuthRepo,apiLogger: ApiLogService): Action[A] = ApiActionCommonBody(parser) { (apiRequest, apiKey, date) =>
    apiRequest.maybeAuthToken match {
      case None => errorTokenNotFound
      case Some(authKey) => {
        getMaybeToken(authKey) match {
          case None => errorTokenUnknown
          case Some(apiToken) => {
            action(SecuredApiRequest(apiRequest.request, apiKey, date, authKey))
          }
        }
      }
    }
  }


  //////////////////////////////////////////////////////////////////////
  // Auxiliar methods to create ApiResults from writable JSON objects

  def ok[A](obj: A, headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = Future.successful(ApiResponse.ok(obj, headers: _*))

  def ok[A](futObj: Future[A], headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = futObj.map(obj => ApiResponse.ok(obj, headers: _*))

  private def itemOrError[A](opt: Option[A], headers: (String, String)*)(implicit w: Writes[A], req: RequestHeader): ApiResult = opt match {
    case Some(i) => ApiResponse.ok(i, headers: _*)
    case None => ApiError.errorItemNotFound
  }

  def maybeItem[A](opt: Option[A], headers: (String, String)*)(implicit w: Writes[A], req: RequestHeader): Future[ApiResult] = Future.successful(itemOrError(opt, headers: _*))

  def maybeItem[A](futOpt: Future[Option[A]], headers: (String, String)*)(implicit w: Writes[A], req: RequestHeader): Future[ApiResult] = futOpt.map(opt => itemOrError(opt, headers: _*))

  def created[A](obj: A, headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = Future.successful(ApiResponse.created(obj, headers: _*))

  def created[A](futObj: Future[A], headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = futObj.map(obj => ApiResponse.created(obj, headers: _*))

  def created(headers: (String, String)*): Future[ApiResult] = Future.successful(ApiResponse.created(headers: _*))

  def accepted[A](obj: A, headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = Future.successful(ApiResponse.accepted(obj, headers: _*))

  def accepted[A](futObj: Future[A], headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = futObj.map(obj => ApiResponse.accepted(obj, headers: _*))

  def accepted(headers: (String, String)*): Future[ApiResult] = Future.successful(ApiResponse.accepted(headers: _*))

  def noContent(headers: (String, String)*): Future[ApiResult] = Future.successful(ApiResponse.noContent(headers: _*))

  def redirect[A](obj: A, headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = Future.successful(ApiResponse.redirect(obj, headers: _*))


  //////////////////////////////////////////////////////////////////////
  // More auxiliar methods

  // Reads an object from an ApiRequest[JsValue] handling a possible malformed error
  def readFromRequest[T](f: T => Future[ApiResult])(implicit request: ApiRequest[JsValue], rds: Reads[T], req: RequestHeader): Future[ApiResult] = {
    request.body.validate[T].fold(
      errors => errorBodyMalformed(errors),
      readValue => f(readValue)
    )
  }


}