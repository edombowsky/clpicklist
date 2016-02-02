package com.abb.clpicklist.dao

import java.math.{BigDecimal => JBigDecimal}
import java.sql.SQLException
import java.util.ArrayList
import java.sql.CallableStatement

import scala.math.BigDecimal
import scala.collection.mutable.{ArrayBuffer, HashMap}

import com.typesafe.scalalogging._
import scalikejdbc.{DB => SDB, _}
import oracle.jdbc.driver.OracleCallableStatement
import oracle.jdbc.driver.OracleTypes
import oracle.jdbc.driver.OracleResultSet

import com.abb.clpicklist.db._
import com.abb.clpicklist.util.Config
import com.abb.clpicklist.model.{Entry, PickList}


object Odb extends LazyLogging {

  // TRICKY:: If we plan to add more built-in entries this values needs to be changed.
  // As of now we have only 65 built-in entries
  @volatile private var sequence: Long = 69

  private val SEQUENCE_START = 2600000

  // Number of fields in the pick list select query
  private val FIELD_COUNT = 13

  private val SELECT_PICK_ENTRY = """
    |SELECT entry.entry_key,
    |       p.name,
    |       entry.code,
    |       entry.description,
    |       entry.parent_key,
    |       entry.value,
    |       entry.aux1,
    |       entry.aux2,
    |       entry.aux3,
    |       entry.aux4,
    |       entry.aux5,
    |       entry.filter_key,
    |       entry.sort_order
    |  FROM pick_entry entry,
    |        pick_list p,
    |        pick_branch b,
    |        pick_version_branch vb,
    |        pick_version pv
    |  WHERE p.list_id = b.of_list AND
    |        b.branch_id = vb.contains_branch AND
    |        vb.in_version = pv.version_id AND
    |        entry.of_branch = b.branch_id AND
    |        pv.version_num = (SELECT MAX(version_num) FROM pick_version WHERE is_current = 1) AND
    |        (p.is_da_dk = 1 OR p.is_ma_dk = 1)
    |ORDER BY p.name asc,
    |         entry.code asc""".stripMargin

  logger.info("starting ODB...")

  def nextStaticSequence(): Long = sequence

  // Not used
  def nextSequenceVal: Option[java.math.BigDecimal] = {
    NamedDB(Config.odbConf.name) localTx { implicit session =>
      sql"SELECT pick_entry_key_seq.NEXTVAL FROM dual"
        .map(rs => rs.bigDecimal(1)) // extracts values from rich java.sql.ResultSet
        .single                      // single, list, traversable
        .apply()                     // Side effect!!! runs the SQL using Connection
    }
  }

  // Works but not used for the moment.
  // def pickList = {
  //   NamedDB(odbConf.name) localTx { implicit session =>
  //     val pl = PickList.findAll()
  //     // pl.foreach(println)
  //     pl
  //   }
  // }

  def getPickListVersion(): Option[String] = {
     try {
      val callStatement = "{ ? = call pick_version_api.select_Pick_Version()}"
      NamedDB(Config.odbConf.name) localTx { implicit session =>
        val conn: java.sql.Connection = ConnectionPool(Config.odbConf.name).borrow()
        val statement = conn.prepareCall(callStatement)
        statement.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR)
        statement.execute()
        val resultSet = statement.getObject(1).asInstanceOf[java.sql.ResultSet]
        if (resultSet.next()) Some(resultSet.getString(1)) else None
      }
    } catch {
      case e @ (_: SQLException | _: ClassNotFoundException) =>
        logger.error("PickList Action failed", e)
        None
    }
  }

  def getSystemIdentifier(): Option[String] = {
    try {
      val callStatement = "{ ? = call sw.get_system_id()}"
      NamedDB(Config.odbConf.name) localTx { implicit session =>
        val conn: java.sql.Connection = ConnectionPool(Config.odbConf.name).borrow()
        val statement = conn.prepareCall(callStatement)
        statement.registerOutParameter(1, oracle.jdbc.OracleTypes.VARCHAR)
        statement.execute()
        val sysId = statement.getString(1)
        logger.info(s"System Identifier: $sysId")
        Some(sysId)
      }
    } catch {
      case e @ (_: SQLException | _: ClassNotFoundException) =>
        logger.error("Unable to retrieve system_id", e)
        None
    }
  }

  def activatePickList() {
    try {
      NamedDB(Config.odbConf.name) localTx { implicit session =>
        val callStatement = s"call Pick_Version_APi.Activate({wirelessly_updateable},{mandatory},{note})"

        SQL(callStatement)
          .bindByName('wirelessly_updateable -> false,
                      'mandatory -> false,
                      'note -> "First version")
          .execute.apply()
        logger.info("Activated Pick List")
      }
    } catch {
      case e @ (_: SQLException | _: ClassNotFoundException) =>
        logger.error("activate pick list failed", e)
    }
  }

  def getEntries: List[List[String]] = {
    val entries = new ArrayBuffer[List[String]]()
    var alternateName: String = ""
    var sortOrder = 1

    try {
      NamedDB(Config.odbConf.name) localTx { implicit session =>
        val data = SQL(SELECT_PICK_ENTRY).map(rs => (rs.string(1), rs.string(2),
                                           rs.string(3), rs.string(4),
                                           rs.string(5), rs.string(6),
                                           rs.string(7), rs.string(8),
                                           rs.string(9), rs.string(10),
                                           rs.string(11), rs.string(12),
                                           rs.string(13))).list.apply()
        for (d <- data) {
          val entry = new ArrayBuffer[String](FIELD_COUNT)
          d.productIterator.foreach {
            case e: String => entry += e.toString
            case _ => entry += null
          }
          logger.debug(entry.toString)
          val aName = entry(1)
          if (aName != alternateName) {
              alternateName = aName
              sortOrder = 1
          }
          entry(FIELD_COUNT - 1) = (sortOrder += 1).toString
          entries += entry.toList
        }
      }
    } catch {
      case e: Exception => {
        logger.error("Exception while extracting pick list entries", e)
        throw new RuntimeException("Exception while extracting pick list entries", e)
      }
    }
    entries.toList
  }

  def getEntries(sqlCode: String): List[Entry] = {
    val rows = ArrayBuffer[Entry]()
    try {
      NamedDB(Config.odbConf.name) localTx { implicit session =>
        // logger.debug(sqlCode)
        val data = SQL(sqlCode).map(rs => (rs.string(1), rs.long(2),
                                           rs.string(3), rs.string(4),
                                           rs.string(5), rs.string(6),
                                           rs.string(7))).list.apply()
        for (d <- data) {
          val (code, entryKey, aux1, aux2, description, filterKey, parentCode) = d
          val entry = new Entry()
          entry.setCode(code)
          entry.setEntryKey(entryKey)
          entry.setAux1(aux1)
          entry.setAux2(aux2)
          entry.setDescription(description)
          entry.setFilterKey(filterKey)
          entry.setParentCode(parentCode)
          rows += entry
        }
      }
    } catch {
      case e @ (_: SQLException | _: ClassNotFoundException) =>
        logger.error("get entries for derived pick lists failed ", e)
    }
    rows.toList
  }

  def getPickLists: HashMap[String, PickList]= {

    val p = PickListTable.syntax("p")
    val b = PickBranchTable.syntax("b")
    val c = PickCategoryTable.syntax("c")
    val pickLists = new HashMap[String, PickList]()

    try {
      NamedDB(Config.odbConf.name) localTx { implicit session =>
        val pklists = withSQL {
          select(p.listId, p.name, p.alternateName, c.name, b.branchId, p.sqlCode)
            .from(PickListTable as p)
            .leftJoin(PickBranchTable as b).on(p.listId, b.ofList)
            .leftJoin(PickCategoryTable as c).on(p.inCategory, c.categoryId)
            .where
            .eq(b.isLatest, 1)
            .and.withRoundBracket{_.eq(p.isDaDk, 1).or.eq(p.isMaDk, 1)}
          }.map(rs => (rs.bigDecimal("list_id").toScalaBigDecimal,
                       rs.string("name"), rs.string("alternate_name"),
                       rs.string(4), rs.bigDecimal("branch_id").toScalaBigDecimal,
                       rs.string("sql_code"))).list.apply()
          // println("DEBUG:: PICKLIST RESULT SET::")
          // println(pklists)
          // println("DEBUG:: <BEGIN PICKLISTS>")
          for (p <- pklists) {
            // println(p)
            val pickList = new PickList
            val (listId, name, alternateName, category, branchId, sqlCode) = p
            println(s"$alternateName ($name) -> CATEGORY:: $category")
            pickList.category = category
            pickList.id = listId
            pickList.name = alternateName
            pickList.branchId = branchId
            pickList.sqlCode = sqlCode
            pickLists.put(alternateName, pickList)
            logger.info(s"ALTERNATE NAME : $alternateName")
          }
          // println("DEBUG:: <END PICKLISTS>")
      }
      // println("DEBUG:: PICKLIST HASH MAP")
      // println(pickLists)
      logger.info("select all pick list")
      pickLists
    } catch {
      case e @ (_: SQLException | _: ClassNotFoundException) =>
        logger.error("PickList Action failed", e)
        throw e
    }
  }
}
