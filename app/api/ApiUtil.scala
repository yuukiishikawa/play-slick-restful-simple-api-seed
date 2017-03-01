package api

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.i18n.Lang
import play.api.mvc.{Call, RequestHeader}


/*
* Set of general values and methods for the API
*/
object ApiUtil {

  //////////////////////////////////////////////////////////////////////
  // Headers

  final val HEADER_CONTENT_TYPE = "Content-Type"
  final val HEADER_CONTENT_LANGUAGE = "Content-Language"
  final val HEADER_ACCEPT_LANGUAGE = "Accept-Language"
  final val HEADER_CACHE_CONRTROL = "Cache-Control"

  final val HEADER_DATE = "Date"
  final val HEADER_LOCATION = "Location"
  final val HEADER_API_KEY = "X-Api-Key"
  final val HEADER_AUTH_TOKEN = "X-Auth-Token"

  final val HEADER_PAGE = "X-Page"
  final val HEADER_PAGE_FROM = "X-Page-From"
  final val HEADER_PAGE_SIZE = "X-Page-Size"
  final val HEADER_PAGE_TOTAL = "X-Page-Total"


  final val HEADER_URLENCODED = "application/x-www-form-urlencoded"
  final val HEADER_AUTHORIZATION = "Authorization"

  final val HEADER_GRANT_TYPE = "grant_type"
  final val HEADER_REFRESH_TOKEN = "refresh_token"

  final val EMPTY_STRING = ""

  def basicHeaders(implicit lang: Lang) = Seq(
    HEADER_DATE -> printHeaderDate(new DateTime()),
    HEADER_CONTENT_LANGUAGE -> lang.language,
    HEADER_CACHE_CONRTROL -> "no-cache"
  )

  def locationHeader(uri: String): (String, String) = HEADER_LOCATION -> uri

  def locationHeader(call: Call)(implicit request: RequestHeader): (String, String) = locationHeader(call.absoluteURL())

  def parseHeaderDate(dateStr: String): DateTime = longDateTimeFormatter.parseDateTime(dateStr)

  def printHeaderDate(date: DateTime): String = longDateTimeFormatter.print(date)


  def parseDateTime(dateStr: String): Date = dateTimeFormatter.parse(dateStr)

  private final val longDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.ENGLISH).withZoneUTC()

  private final val dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  private final val dateTimeZoneFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")

  private final val dateFormatter = new SimpleDateFormat("yyyy-MM-dd")

  def parseDateTimeZone(dateStr: String): Timestamp = {
    val dirtyDate = dateTimeZoneFormatter.parseDateTime(dateStr)
    val stringDate = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(dirtyDate)
    Timestamp.valueOf(stringDate)
  }

  def printTimestamp(timestamp: Timestamp): String = dateTimeFormatter.format(timestamp)

  def dateToString(date: Date): String = dateTimeFormatter.format(date)

  def parseDate(dateStr: String): Date = dateFormatter.parse(dateStr)

  def printDate(date: Date): String = dateFormatter.format(date)

  def currentTimestamp: Timestamp = new java.sql.Timestamp(new java.util.Date().getTime)

  def addBearer(body: String): String = {
    "Bearer " + body
  }


  //////////////////////////////////////////////////////////////////////
  // Sorting

  object Sorting {
    final val ASC = false
    final val DESC = true
  }

}