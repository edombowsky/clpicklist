package com.abb.clpicklist.util

import java.io.{File => JFile}
import java.util.Properties
import scala.collection.JavaConversions._

import com.typesafe.scalalogging._
import scalikejdbc.{DB => SDB, _}

import com.abb.clpicklist.db._



object Config extends LazyLogging {

  case class myConfig(pickDir: String = "", outDir: String = "", db: String = "" )

  val parser = new scopt.OptionParser[myConfig]("clpicklist") {
    head("clpicklist", "1.x")
    opt[String]('d', "db") required() action { (x, c) =>
      c.copy(db = x) } text("db is the ODB connection string")
    opt[String]('o', "outDir") required() valueName("<file>") action { (x, c) =>
      c.copy(outDir = x) } text("outDir is the directory to put the picklist.db file")
    opt[String]('p', "pickDir") required() valueName("<file>") action { (x, c) =>
      c.copy(pickDir = x) } text("pickDir is the directory containing the picklist sheets (.xls)")
    help("help") text("prints this usage text")
  }


  val XLS_EXTENSIONS = ".xls"
  val PICKLIST_DB_NAME = "picklist.db"

  private val properties = new Properties()

  // This will be configured once command line args are read
  var odbConf = DBConfig("jdbc:oracle:thin:@server:1521:sid", "username", "password")
  // val connection = DB.connection(odbConf)

  val pickDbConfig = DBConfig(s"jdbc:sqlite:$PICKLIST_DB_NAME", "", "")
  DB.connection(pickDbConfig)

  def parseArguments(args: Array[String]): Boolean = {

    properties.clear()

    parser.parse(args, myConfig()) match {
      case Some(config) =>
        logger.info("All systems go...")

        // Parse the connect string for its constituents
        val r = """(.*)/(.*)@(.*)_(.*)""".r
        val r(username, password, server, sid) = config.db

        odbConf = DBConfig(s"jdbc:oracle:thin:@$server:1521:$sid", username, password)
        DB.connection(odbConf)

        properties.setProperty("db", config.db) 
        properties.setProperty("pd", config.pickDir)
        properties.setProperty("od", config.outDir)
        properties.setProperty("server", server)
        properties.setProperty("sid", sid)
        properties.setProperty("username", username)
        properties.setProperty("password", password)

        true

      case None =>
        // arguments are bad, error message will have been displayed
        false
      }

  }

  def getValue(key: String): String = {
    properties.getProperty(key)
  }

  def getValue(key: String, `def`: String): String = {
    properties.getProperty(key, `def`)
  }
}
