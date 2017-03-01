package domain.models

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Apilog.schema ++ Authtoken.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Apilog
    *  @param datetime Database column datetime SqlType(TIMESTAMP)
    *  @param ip Database column ip SqlType(VARCHAR), Length(255,true)
    *  @param token Database column token SqlType(VARCHAR), Length(255,true), Default(None)
    *  @param method Database column method SqlType(VARCHAR), Length(255,true)
    *  @param uri Database column uri SqlType(VARCHAR), Length(255,true)
    *  @param email Database column email SqlType(VARCHAR), Length(255,true)
    *  @param requestbody Database column requestBody SqlType(TEXT), Default(None)
    *  @param responsestatus Database column responseStatus SqlType(INT)
    *  @param responsebody Database column responseBody SqlType(TEXT), Default(None) */
  case class ApilogRow(datetime: java.sql.Timestamp, ip: String, token: Option[String] = None, method: String, uri: String, email: String, requestbody: Option[String] = None, responsestatus: Int, responsebody: Option[String] = None)
  /** GetResult implicit for fetching ApilogRow objects using plain SQL queries */
  implicit def GetResultApilogRow(implicit e0: GR[java.sql.Timestamp], e1: GR[String], e2: GR[Option[String]], e3: GR[Int]): GR[ApilogRow] = GR{
    prs => import prs._
      ApilogRow.tupled((<<[java.sql.Timestamp], <<[String], <<?[String], <<[String], <<[String], <<[String], <<?[String], <<[Int], <<?[String]))
  }
  /** Table description of table ApiLog. Objects of this class serve as prototypes for rows in queries. */
  class Apilog(_tableTag: Tag) extends Table[ApilogRow](_tableTag, "ApiLog") {
    def * = (datetime, ip, token, method, uri, email, requestbody, responsestatus, responsebody) <> (ApilogRow.tupled, ApilogRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(datetime), Rep.Some(ip), token, Rep.Some(method), Rep.Some(uri), Rep.Some(email), requestbody, Rep.Some(responsestatus), responsebody).shaped.<>({r=>import r._; _1.map(_=> ApilogRow.tupled((_1.get, _2.get, _3, _4.get, _5.get, _6.get, _7, _8.get, _9)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column datetime SqlType(TIMESTAMP) */
    val datetime: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("datetime")
    /** Database column ip SqlType(VARCHAR), Length(255,true) */
    val ip: Rep[String] = column[String]("ip", O.Length(255,varying=true))
    /** Database column token SqlType(VARCHAR), Length(255,true), Default(None) */
    val token: Rep[Option[String]] = column[Option[String]]("token", O.Length(255,varying=true), O.Default(None))
    /** Database column method SqlType(VARCHAR), Length(255,true) */
    val method: Rep[String] = column[String]("method", O.Length(255,varying=true))
    /** Database column uri SqlType(VARCHAR), Length(255,true) */
    val uri: Rep[String] = column[String]("uri", O.Length(255,varying=true))
    /** Database column email SqlType(VARCHAR), Length(255,true) */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true))
    /** Database column requestBody SqlType(TEXT), Default(None) */
    val requestbody: Rep[Option[String]] = column[Option[String]]("requestBody", O.Default(None))
    /** Database column responseStatus SqlType(INT) */
    val responsestatus: Rep[Int] = column[Int]("responseStatus")
    /** Database column responseBody SqlType(TEXT), Default(None) */
    val responsebody: Rep[Option[String]] = column[Option[String]]("responseBody", O.Default(None))

    /** Index over (datetime) (database name idx_log_datetime) */
    val index1 = index("idx_log_datetime", datetime)
    /** Index over (email) (database name idx_log_email) */
    val index2 = index("idx_log_email", email)
    /** Index over (token) (database name idx_log_token) */
    val index3 = index("idx_log_token", token)
  }
  /** Collection-like TableQuery object for table Apilog */
  lazy val Apilog = new TableQuery(tag => new Apilog(tag))

  /** Entity class storing rows of table Authtoken
    *  @param authkey Database column authKey SqlType(VARCHAR), PrimaryKey, Length(40,true)
    *  @param email Database column email SqlType(VARCHAR), Length(255,true)
    *  @param expiresat Database column expiresAt SqlType(DATETIME)
    *  @param updatedat Database column updatedAt SqlType(DATETIME) */
  case class AuthtokenRow(authkey: String, email: String, expiresat: java.sql.Timestamp, updatedat: java.sql.Timestamp)
  /** GetResult implicit for fetching AuthtokenRow objects using plain SQL queries */
  implicit def GetResultAuthtokenRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[AuthtokenRow] = GR{
    prs => import prs._
      AuthtokenRow.tupled((<<[String], <<[String], <<[java.sql.Timestamp], <<[java.sql.Timestamp]))
  }
  /** Table description of table AuthToken. Objects of this class serve as prototypes for rows in queries. */
  class Authtoken(_tableTag: Tag) extends Table[AuthtokenRow](_tableTag, "AuthToken") {
    def * = (authkey, email, expiresat, updatedat) <> (AuthtokenRow.tupled, AuthtokenRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(authkey), Rep.Some(email), Rep.Some(expiresat), Rep.Some(updatedat)).shaped.<>({r=>import r._; _1.map(_=> AuthtokenRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column authKey SqlType(VARCHAR), PrimaryKey, Length(40,true) */
    val authkey: Rep[String] = column[String]("authKey", O.PrimaryKey, O.Length(40,varying=true))
    /** Database column email SqlType(VARCHAR), Length(255,true) */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true))
    /** Database column expiresAt SqlType(DATETIME) */
    val expiresat: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("expiresAt")
    /** Database column updatedAt SqlType(DATETIME) */
    val updatedat: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("updatedAt")
  }
  /** Collection-like TableQuery object for table Authtoken */
  lazy val Authtoken = new TableQuery(tag => new Authtoken(tag))
}
