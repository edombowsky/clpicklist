package com.abb.clpicklist.util

import java.util.Properties
import scala.collection.JavaConversions._

import com.typesafe.scalalogging._
import scalikejdbc.{DB => SDB, _}

import com.abb.clpicklist.util.ArgsParse._
import com.abb.clpicklist.db._



object Config extends LazyLogging {

  val XLS_EXTENSIONS = ".xls"
  val PICKLIST_DB_NAME = "picklist.db"

  private val properties = new Properties()

  // This will be configured once command line args are read
  var odbConf = DBConfig("jdbc:oracle:thin:@server:1521:sid", "username", "password")
  // val connection = DB.connection(odbConf)

  val pickDbConfig = DBConfig(s"jdbc:sqlite:$PICKLIST_DB_NAME", "", "")
  DB.connection(pickDbConfig)

  def parseArguments(args: Array[String]): Map[Symbol,String] = {

    properties.clear()

    // Required positional arguments by key in options
    val required = List()

    // Options with value
    val optional = Map("--db|-d" -> 'db, "--pickDir|-p" -> 'pd, "--outputDir|-o" -> 'od)

    // Flags
    val flags = Map("--help|-h" -> 'help)

    // Default options that are passed in
    val defaultOptions = Map[Symbol, String]('help -> "false", 'od -> ".")

    // Parse options based on the command line args
    val options = parseOptions(args.toList, required, optional, flags, defaultOptions)

    // for ((k,v) <- options) { properties.setProperty(k.toString().stripPrefix("'"), v) }
    options.foreach {
      keyVal => properties.setProperty(keyVal._1.toString().stripPrefix("'"), keyVal._2)
    }
      // println(keyVal._1 + "=" + keyVal._2)}

    // Parse the connect string for its constituents
    val r = """(.*)/(.*)@(.*)_(.*)""".r
    val r(username, password, server, sid) = options('db)

    // val odbConf = DBConfig(s"jdbc:oracle:thin:@$server:1521:$sid", username, password)
    // val connection = DB.connection(odbConf)

    odbConf = DBConfig(s"jdbc:oracle:thin:@$server:1521:$sid", username, password)
    DB.connection(odbConf)

    properties.setProperty("server", server)
    properties.setProperty("sid", sid)
    properties.setProperty("username", username)
    properties.setProperty("password", password)

    // println(properties)
    options
  }

  def getValue(key: String): String = {
    properties.getProperty(key)
  }

  def getValue(key: String, `def`: String): String = {
    properties.getProperty(key, `def`)
  }
}
