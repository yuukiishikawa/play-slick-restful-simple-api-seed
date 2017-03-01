package domain.models.adaptor

import play.api.libs.json.Json

case class LoginInfo (
  email: String,
  password: String
                     )

object LoginInfo {
  implicit val loginInfo = Json.format[LoginInfo]
}


case class AuthSuccess (
                       token: String
                     )

object AuthSuccess {
  implicit val authSuccess = Json.format[AuthSuccess]
}
