package domain.models.auth

import scala.concurrent.Future

/*
* Stores the Api Key information
*/
case class ApiKey(
                   apiKey: String,
                   name: String,
                   active: Boolean)

object ApiKey {

  import KeyDB.apiKeys

  def isActive(apiKey: String): Future[Option[Boolean]] = Future.successful {
    apiKeys.find(_.apiKey == apiKey).map(_.active)
  }

}