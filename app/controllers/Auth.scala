package controllers

import javax.inject.{Inject, Singleton}

import api.ApiError._
import domain.models.adaptor.{AuthSuccess, LoginInfo}
import domain.service.ApiLogService
import domain.service.ApiToken._
import play.api.cache.CacheApi
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json._


@Singleton
class Auth @Inject()(val messagesApi: MessagesApi,
                     implicit val apiLogger: ApiLogService,
                     implicit val cache: CacheApi) extends api.ApiController {

  def create = ApiActionWithBody { implicit request =>
    readFromRequest[LoginInfo] {
      case l if (l.email.nonEmpty && l.password.nonEmpty) => {
        val token = createApiToken(l.email, l.password)
        ok(Json.toJson(AuthSuccess(token)))
      }
      case _ => errorBadRequest(Messages("api.error.signin.param.insufficient"))
    }
  }

}

