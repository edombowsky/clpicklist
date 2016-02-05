package com.abb.clpicklist.model

import java.io.File
import java.util.ArrayList
import java.util.Collection
import java.util.HashMap
import java.util.List
import java.util.TreeSet

import scala.math.BigDecimal
//remove if not needed
import scala.collection.JavaConversions._
import com.typesafe.scalalogging._

import com.abb.clpicklist.parser.{Row, XlsParser}
import com.abb.clpicklist.dao.Odb

object PickList {

  val COMPOSE_DEFINED = "ComposeDefined"
  val DERIVED = "Derived"
}

/**
 * Class holds Picklist Data imported from Excel sheets.
 */
class PickList extends LazyLogging {

  // TRICKY:: These fields are updated from the database
  var name: String = _
  var id: BigDecimal = _
  var category: String = _
  var branchId: BigDecimal = _
  var sqlCode: String = _

  private val entryMap = new HashMap[String, Entry]

  val entries = new ArrayList[Entry]

  def isComposeDefined(): Boolean = PickList.COMPOSE_DEFINED.equalsIgnoreCase(category)

  def isDerived(): Boolean = PickList.DERIVED.equalsIgnoreCase(category)

  def addEntries(rows: List[Row]) {
    rows.foreach(addEntry(_))
    // for (row <- rows) {
    //     addEntry(row)
    // }
  }

  private def updateSortOrder() {
    // println("DEBUG:: entering updateSortOrder...")
    val sortedSet = new TreeSet[String](entryMap.keySet)
    // println(s"DEBUG:: sortedSet= $sortedSet")
    var sortOrder: Int = 0
    sortedSet.foreach { key =>
      // println(s"DEBUG: setting sort order to $sortOrder...")
      // entryMap.get(key).setSortOrder("" + (sortOrder += 1))
      entryMap.get(key).setSortOrder(s"${sortOrder += 1; sortOrder}")
      // println(s"DEBUG:: incrementing sort order to: $sortOrder")
    }

    // for (key <- sortedSet) {
    //   println(s"DEBUG: updating sort order ($sortOrder)...")
    //   entryMap.get(key).setSortOrder("" + (sortOrder += 1))
    //   println(s"DEBUG:: set sort order to: $sortOrder")
    // }
  }

  def initilize(file: File) {
    XlsParser.parse(file, this)
    // println(this)
    updateSortOrder()
  }

  def addEntry(row: Row) {
    val entry = new Entry(row, isComposeDefined)
    if (entry.getParentCode == null) {
      entries.add(entry)
    } else {
      val parent = entryMap.get(entry.getParentCode)
      parent.addChildEntry(entry)
    }
    entryMap.put(entry.getCode, entry)
  }

  def addDerivedEntries(entries: scala.collection.immutable.List[Entry]) {
    for (e <- entries) {
      entryMap.put(e.code, e)
    }
  }

  def getAllEntries(level: Int): Collection[Entry] = {
    val entries = new ArrayList[Entry]()
    for (entry <- entryMap.values if entry.level == level) entries.add(entry)
    entries
  }

  def getEntryCount(): Int = entries.size

  override def toString(): String = {
    s" Name:: $name, Id:: $id, Category:: $category, BranchId:: $branchId, Entries:: "
    // val builder = new StringBuilder()
    // builder.append(" Name:: ")
    //   .append(name).append(", Id:: ")
    //   .append(id)
    //   .append(", Category:: ")
    //   .append(category)
    //   .append(", BranchId:: ")
    //   .append(branchId)
    //   .append(", Entries:: ")
    //   // The following takes up too much space
    //   // .append("\n")
    //   // entries.foreach(builder.append(_))
    // builder.toString
  }
}
