package com.abb.clpicklist.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.ArrayList
import java.util.List

//remove if not needed
import scala.collection.JavaConversions._

import com.typesafe.scalalogging._

import com.abb.clpicklist.model.{PickLists, Entry, PickList}


object PickEntryHelper extends LazyLogging {

  val ADD_ENTRY = "{ ? = call pick_entry_api." + "create_entry(?,?,?,?,?,?,?,?,?,?,?,?,?)}"

  val ADD_ENTRY_SEQ = "{ ? = call pick_entry_api." +
    "create_entry(?,PICK_ENTRY_KEY_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?)}"

  val INSERT_PICK_ENTRY = "       insert into pick_entry ( OF_BRANCH , " + "              entry_id," +
    "              entry_key," +
    "              Code, " +
    "              entry_level, " +
    "              parent_key," +
    "              value," +
    "              description," +
    "              aux1," +
    "              aux2," +
    "              filter_key)" +
    "        values (?, PICK_ENTRY_SEQ.NEXTVAL, " +
    "               PICK_ENTRY_KEY_SEQ.Nextval, " +
    "               ? , ?, ? , ? , ? , ? , ? , ?)"

  val INSERT_PICK_ENTRY_NOSEQ = "       insert into pick_entry ( OF_BRANCH , " + "              entry_id," +
    "              entry_key," +
    "              Code, " +
    "              entry_level, " +
    "              parent_key," +
    "              value," +
    "              description," +
    "              aux1," +
    "              aux2," +
    "              filter_key)" +
    "        values (?, PICK_ENTRY_SEQ.NEXTVAL, " +
    "               ?, ? , ?, ? , ? , ? , ? , ? , ?)"

  private val BATCH_SIZE = 200

  private var count: Int = 0

  def createPickList(pickLists: PickLists) {
    val composeDefPickLists = new ArrayList[PickList]()
    val oPickLists = new ArrayList[PickList]()
    for (p <- pickLists.getAll) {
      if (p.isComposeDefined) {
        composeDefPickLists.add(p)
      } else {
        oPickLists.add(p)
      }
    }
    for (level <- 1 until 6) {
      createPickList(composeDefPickLists, INSERT_PICK_ENTRY_NOSEQ, level)
      createPickList(oPickLists, INSERT_PICK_ENTRY, level)
    }
  }

  private def createPickList(pickLists: List[PickList],
                             query: String, level: Int) {
    try {
      // connection = ODBUtils.getODBConnection
      // connection.setAutoCommit(false)
      // for (statement <- managed(connection.prepareStatement(query))) {
      println(query)
        for (pickList <- pickLists) {
          println(pickList)
          // createEntries(statement, pickList, level)
        }
        // val results = statement.executeBatch()
        // LOGGER.log(Level.INFO, " Added {0} Entries", results.length)
      // }
      // connection.commit()
      // LOGGER.info("Successfully created entries")
    } catch {
      case e: Exception => {
        // LOGGER.log(Level.SEVERE, null, e)
        // if (connection != null) {
        //   try {
        //     connection.rollback()
        //   } catch {
        //     case ex: SQLException => println(ex) //LOGGER.log(Level.SEVERE, null, ex)
        //   }
        // }
        throw new RuntimeException(e.getMessage, e)
      }
    } finally {
      // if (connection != null) {
      //   try {
      //     connection.close()
      //   } catch {
      //     case ex: SQLException => println(ex) //LOGGER.log(Level.SEVERE, null, ex)
         // }
      // }
    }
  }

  private def createEntries(statement: PreparedStatement,
                            list: PickList, level: Int) {
    for (e <- list.getAllEntries(level)) {
      var i = 1
      statement.setLong(i, list.branchId.toLong)
      i += 1
      if (list.isComposeDefined) {
        statement.setLong(i, e.entryKey)
        i += 1
      }
      statement.setString(i, e.code)
      i += 1
      statement.setLong(i, e.level)
      i += 1
      if (e.parentKey != 0) {
        statement.setLong(i, e.parentKey)
      } else {
        statement.setNull(i, java.sql.Types.INTEGER)
      }
      i += 1
      statement.setString(i, e.value)
      i += 1
      statement.setString(i, e.description)
      i += 1
      statement.setString(i, e.aux1)
      i += 1
      statement.setString(i, e.aux2)
      i += 1
      statement.setString(i, e.filterKey)
      statement.addBatch()

      if (count % BATCH_SIZE == 0) {
        logger.info(s"Executing batch count $count")
        val results = statement.executeBatch()
        logger.info(s"Added ${results.length} entries")
      }
    }
  }
}

