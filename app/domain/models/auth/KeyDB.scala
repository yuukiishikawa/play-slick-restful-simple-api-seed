package domain.models.auth

import java.text.SimpleDateFormat

import domain.service.ApiToken

import scala.collection.mutable.Map

/*
* A fake DB to store and load all the data
*/
object KeyDB {

  val dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

  // API KEYS
  val apiKeys = KeyTable(
    1L -> ApiKey(apiKey = "fibrous-axiom-building", name = "ext-js-desktop", active = true)
  )

  // TOKENS
  val tokens = KeyTable[ApiToken]()

  /*
	* Fake table that emulates a SQL table with an auto-increment index
	*/
  case class KeyTable[A](var table: Map[Long, A], var incr: Long) {
    def nextId: Long = {
      if (!table.contains(incr))
        incr
      else {
        incr += 1
        nextId
      }
    }

    def get(id: Long): Option[A] = table.get(id)

    def find(p: A => Boolean): Option[A] = table.values.find(p)

  }

  object KeyTable {
    def apply[A](elements: (Long, A)*): KeyTable[A] = apply(Map(elements: _*), elements.size + 1)
  }

}