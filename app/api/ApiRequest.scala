package api

import api.ApiUtil._
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc._

import scala.util.Try

/*
* Wrapped Request with additional information for the API (headers: Api Key, Date, Auth-Token, ...)
*/
trait ApiRequestHeader[R <: RequestHeader] {
  val request: R
  val maybeApiKey: Option[String] = request.headers.get(HEADER_API_KEY)
  val maybeDateTry: Option[Try[DateTime]] = request.headers.get(HEADER_DATE).map { dateStr =>
    Try(parseHeaderDate(dateStr))
  }
  val maybeDate: Option[DateTime] = maybeDateTry.filter(_.isSuccess).map(_.get)
  val maybeAuthToken: Option[String] = request.headers.get(HEADER_AUTH_TOKEN)

  def dateOrNow: DateTime = maybeDate.getOrElse(new DateTime())

  def remoteAddress: String = request.remoteAddress

  def method: String = request.method

  def uri: String = request.uri

  def maybeBody: Option[String] = None
}

case class ApiRequestHeaderImpl(request: RequestHeader) extends ApiRequestHeader[RequestHeader]

/*
* ApiRequestHeader for requests that don't require authentication
*/
class ApiRequest[A](val request: Request[A]) extends WrappedRequest[A](request) with ApiRequestHeader[Request[A]] {
  override def remoteAddress = request.remoteAddress

  override def method = request.method

  override def uri = request.uri

  override def maybeBody: Option[String] = request.body match {
    case body: JsValue => Some(Json.prettyPrint(body))
    case body: String => if (body.length > 0) Some(body) else None
    case body => Some(body.toString)
  }
}

object ApiRequest {
  def apply[A](request: Request[A]): ApiRequest[A] = new ApiRequest[A](request)
}

/*
* ApiRequest for authenticated requests
*/
case class SecuredApiRequest[A](override val request: Request[A], apiKey: String, date: DateTime, authKey: String) extends ApiRequest[A](request)

