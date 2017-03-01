package domain.service

import java.sql.Timestamp
import java.util.UUID
import javax.inject.{Inject, Singleton}

import api.ApiUtil._
import domain.models.Tables
import models.entity.Tables
import org.joda.time.DateTime
import play.api.cache.CacheApi
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

@Singleton
class AuthRepo @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends Tables with HasDatabaseConfig[JdbcProfile] {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val profile = slick.driver.MySQLDriver

  import dbConfig.driver.api._


  def findByAuthKey(authKey: String): Future[Option[ApiToken]] = {
    for {
      authRow <- db.run(Authtoken.filter(_.authkey === authKey).result.headOption)
    } yield {
      authRow match {
        case None => None
        case Some(row) =>
          Option(ApiToken(row.email))
      }
    }
  }

  def insertAuthToken(authKey: String, email: String) = {
    val anHourLater: Timestamp = new java.sql.Timestamp(DateTime.now.plusMinutes(60 * 24 * 60).getMillis)
    val insertQuery = Authtoken += AuthtokenRow(authKey, email, anHourLater, currentTimestamp)

    db.run(insertQuery)
  }


}

case class ApiToken(email: String)

object ApiToken {

  def getMaybeToken(authKey: String)(implicit cache: CacheApi, authRepo: AuthRepo): Option[ApiToken] = {

    def recreateCache(authKey: String, apiToken: ApiToken)(implicit cache: CacheApi): Unit = {
      cache.set(authKey, ApiToken(apiToken.email))
    }

    val mayBeToken: Option[ApiToken] = cache.get(authKey)
    mayBeToken match {
      case Some(token) => Option(token)
      case None =>
        val anotherMayBeToken = awaitMaybeApiToken(authRepo.findByAuthKey(authKey))
        anotherMayBeToken match {
          case Some(t) => recreateCache(authKey, t)
          case None => ;
        }
        anotherMayBeToken
    }
  }

  private def awaitMaybeApiToken(f: Future[Option[ApiToken]]): Option[ApiToken] = {
    Await.ready(f, Duration.Inf)
    f.value.get match {
      case Success(o) => o
      case Failure(ex) => None
    }
  }

  def createApiToken(email: String, password: String)(implicit cache: CacheApi, authRepo: AuthRepo): String = {
    // Be sure the uuid is not already taken for another authToken
    def newUUID: String = {
      val uuid = UUID.randomUUID().toString
      if (!exists(uuid)) uuid else newUUID
    }

    val authKey = newUUID

    Await.result(authRepo.insertAuthToken(authKey, email), Duration.Inf)
    cache.set(authKey, ApiToken(email))
    authKey
  }

  private def exists(authKey: String)(implicit cache: CacheApi, repo: AuthRepo): Boolean = {
    getMaybeToken(authKey: String) match {
      case Some(x) => true
      case None => false
    }
  }


  def getMaybeEmail(authKey: Option[String]) = "yuki.ishikawa@a-saas.com"

}