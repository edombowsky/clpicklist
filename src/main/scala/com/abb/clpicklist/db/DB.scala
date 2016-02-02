package com.abb.clpicklist.db

import java.sql.Connection

import com.typesafe.scalalogging._
import scalikejdbc.globalsettings._
import scalikejdbc.{DB => SDB, _}

import scala.util.Try

/**
 * Configuration required to run a `DB` instance.
  *
  * @param jdbcUrl  : jdbc url to the database eg. "jdbc:h2:mem:test1"
  * @param user     : user name for the database. Can be null or ""
  * @param password : password for the database. Can be null or ""
  * @param driver   : Optional driver name like `oracle.jdbc.OracleDriver`. In the absence
                      of a driver name, the driver to use is derived from the jdbcUrl provided.
  */
case class DBConfig(jdbcUrl: String, user: String, password: String, driver: Option[String] = None) {
  /** Unique name for this config. This unique name is used internally as the connection pool name.*/
  val name = s"$jdbcUrl/$user"
}

object DB extends LazyLogging {

  /**
   * Create a new SQL connection or get a pooled connection. Library users are responsible
   * for adding the appropriate jdbc drivers as a dependency.
   *
   * @param config: database configuration for the connection
   *
   * @return The SQL connection
   */
  def connection(config: DBConfig): Try[java.sql.Connection] = {

    if (!ConnectionPool.isInitialized(config.name)) {
       logger.info(s"creating connection for ${config.name}")

      lazy val derivedDriver = config.jdbcUrl.split(":").take(3) match {
        case Array("jdbc", "sqlserver", _) => "com.microsoft.sqlserver.jdbc.SQLServerDriver"
        case Array("jdbc", "hsqldb"   , _) => "org.hsqldb.jdbcDriver"
        case Array("jdbc", "oracle"   , _) => "oracle.jdbc.OracleDriver"
        case Array("jdbc", "mysql"    , _) => "com.mysql.jdbc.Driver"
        case Array("jdbc", "sqlite"   , _) => "org.sqlite.JDBC"
        case Array("jdbc", unknown    , _) => throw new Exception(s"Unsupported jdbc driver: $unknown")
        case _                             => throw new Exception(s"Invalid jdbc url: ${config.jdbcUrl}")
      }

      Class.forName(config.driver.fold(derivedDriver)(identity))
      ConnectionPool.add(config.name, config.jdbcUrl, config.user, config.password)
    }

    GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
      enabled = false,
      singleLineMode = false,
      printUnprocessedStackTrace = false,
      stackTraceDepth = 15,
      logLevel = 'error,
      warningEnabled = false,
      warningThresholdMillis = 1000L,
      warningLogLevel = 'warn
    )

    GlobalSettings.nameBindingSQLValidator = NameBindingSQLValidatorSettings(
      ignoredParams = NoCheckForIgnoredParams)

    Try(ConnectionPool.borrow(config.name))
  }
}
