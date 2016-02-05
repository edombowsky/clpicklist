package com.abb.clpicklist.dao

import java.io.{File => JFile}
import java.sql.Connection
import java.sql.SQLException

import scala.util.control.ControlThrowable

import scalikejdbc.{DB => SDB, _}
import com.typesafe.scalalogging._
import better.files.File

import com.abb.clpicklist.util.Config


object SqlLite extends LazyLogging {

  val DB_NAME = "picklist.db"

  val BATCH_SIZE = 500

  def createTable(sql: String) {
    try {
      NamedDB(Config.pickDbConfig.name) localTx { implicit session =>
        SQL(sql).execute.apply()
        logger.info("Table created successfully")
      }
    } catch {
      case e @ (_: SQLException | _: ClassNotFoundException) =>
        logger.error("Table creation failed", e)
    }
  }

  def createSQLLiteTables() {
    try {
      val file = File(DB_NAME)
      if (file.exists()) {
        logger.info("DB Exists deleting the db")
        file.delete()
      }
      createTable(SqlLiteQueries.CREATE_PICK_DELTA)
      createTable(SqlLiteQueries.CREATE_PICK_ENTRY)
      createTable(SqlLiteQueries.CREATE_PICK_LIST)
      createTable(SqlLiteQueries.CREATE_PICK_VERSION)
    } catch {
      case e: Exception => {
        logger.error("Table creation failure", e)
        throw new RuntimeException("Failure during table creation", e)
      }
    }
  }

  def movePickDB() {
    val outDir = Config.getValue("od", "/Users/caeadom/Documents/projects/clpicklist")
    val odir = File(outDir)
    val dest = File(outDir, DB_NAME)
    val source = File(DB_NAME)

    try {
      if (! odir.isDirectory) odir.createDirectory()
      source.moveTo(dest, true)

      source.moveTo(dest, true)
      // Files.move(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING)
      logger.info("picklist.db is created")
    } catch {
      case e: ControlThrowable => logger.error(s"Failed to copy $DB_NAME to $outDir", e)
    }
  }

// batch insert
// val batchInsertQuery = withSQL {
//   insert into Product columns (pc.id, pc.name, pc.price) values (sqls.?, sqls.?, sqls.?)
// }
// batchInsertQuery.batch(Seq(3, "Coffee", 90), Seq(4, "Chocolate", 200)).apply()
  def createEntries(entries: List[List[String]]) {
    var count = 0
    var connection: Connection = null
    try {
      // NamedDB(Config.pickDbConfig.name) localTx { implicit session =>
      //   val conn: java.sql.Connection = ConnectionPool(Config.pickDbConfig.name).borrow()
      //   val statement = conn.prepareCall(callStatement)

      //   SQL(callStatement)
      //     .bindByName('wirelessly_updateable -> false,
      //                 'mandatory -> false,
      //                 'note -> "First version")
      //     .execute.apply()
      // }

      // connection = createConnection()
      // connection.setAutoCommit(false)
      // for (statement <- managed(connection.prepareStatement(SQLLiteQueries.INSERT_PICK_ENTRY))) {
      NamedDB(Config.pickDbConfig.name) localTx { implicit session =>
        connection = ConnectionPool(Config.pickDbConfig.name).borrow()
        val statement = connection.prepareStatement(SqlLiteQueries.INSERT_PICK_ENTRY)

        // SQL(callStatement)
        //   .bindByName('wirelessly_updateable -> false,
        //               'mandatory -> false,
        //               'note -> "First version")
        //   .execute.apply()
        for (entry <- entries) {
          var i = 1
          for (vals <- entry) {
            logger.debug(s"statement arg [$i] set to $vals")
            statement.setString(i, vals)
            i += 1
          }

          statement.addBatch()
          count += 1

          // To avoid the memory issues with large batch we will do it in intervals
          if (count % BATCH_SIZE == 0) {
            logger.debug(s"adding a batch, count= $count")
            val results = statement.executeBatch()
          }
        }

        // Do the remaining ones
        val results = statement.executeBatch()
        // conn.commit()
        logger.info("Successfully created entries")
      }
    } catch {
      case e: Exception => {
        logger.error(null, e)
        if (connection != null) {
          try {
            connection.rollback()
          } catch {
            case ex: SQLException => logger.error(null, ex)
          }
        }
      }
    } finally {
      if (connection != null) {
        try {
          connection.close()
        } catch {
          case ex: SQLException => logger.error(null, ex)
        }
      }
    }
  }

  def addVersionEntry(version: String, isWorking: Boolean, systemIdentifier: String) {
    logger.debug(s"addVersionEntry:: version=$version, isWorking=$isWorking, systemIdentifier=$systemIdentifier")
    try {
      NamedDB(Config.pickDbConfig.name) localTx { implicit session =>
        val callStatement = SqlLiteQueries.INSERT_PICK_VERSION

        SQL(callStatement)
          .bindByName('version_num -> 1,
                      'is_working -> {if (isWorking) 1 else 0},
                      'system_identifier -> systemIdentifier)
          .execute.apply()
        logger.info("Activated pick list")
      }
    } catch {
      case e: Exception => logger.error("Failed to add pick list version", e)
    }
  }
}

