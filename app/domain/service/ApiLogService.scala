package domain.service

import javax.inject.Inject

import api.ApiRequestHeader
import api.ApiUtil._
import ApiToken._
import domain.models.Tables
import play.api.cache.CacheApi
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.libs.json.{JsNull, JsValue, Json}
import play.api.mvc.RequestHeader
import slick.driver.JdbcProfile


/**
  * Created by ishikawayuuki on 2016/05/23.
  */
class ApiLogService @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends Tables with HasDatabaseConfig[JdbcProfile] {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val profile = slick.driver.MySQLDriver

  import dbConfig.driver.api._

  def insert[R <: RequestHeader](status: Int, json: JsValue)(implicit cache: CacheApi,request: ApiRequestHeader[R]) = {

    val row = ApilogRow(
      datetime = currentTimestamp,
      ip = request.remoteAddress,
      token = request.maybeAuthToken,
      method = request.method,
      uri = request.uri,
      requestbody = request.maybeBody,
      responsestatus = status,
      responsebody = if (json == JsNull) None else Some(Json.prettyPrint(json)),
      email = getMaybeEmail(request.maybeAuthToken)
    )

    def token(implicit request: ApiRequestHeader[R]): String = {
      request.maybeAuthToken match {
        case Some(t) => t
        case None => EMPTY_STRING
      }
    }
    db.run(Apilog += row)
  }

}

