package com.abb.clpicklist.model

import java.util.ArrayList
import java.util.List
import java.util.Map

import scala.beans.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

import com.typesafe.scalalogging._

import com.abb.clpicklist.dao.Odb
import com.abb.clpicklist.parser.Row

object Entry {

  var CODE: String = "code"

  var KEY: String = "entry_key"

  var LEVEL: String = "entry_level"

  var PARENT_KEY: String = "parent_key"

  var VALUE: String = "value"

  var DESCRIPTION: String = "description"

  var AUX1: String = "aux1"

  var AUX2: String = "aux2"

  var FILTER_KEY: String = "filter_key"
}

class Entry extends LazyLogging {

  @BeanProperty var code: String = _
  @BeanProperty var parentCode: String = _
  @BeanProperty var description: String = _
  @BeanProperty var aux1: String = _
  @BeanProperty var aux2: String = _
  @BeanProperty var filterKey: String = _
  @BeanProperty val childEntries = new ArrayList[Entry]

  // TRICKY:: these fields are not parsed from xls sheet it is
  // Updated later for inserting into DB.
  // entryKey for composeDefined fields are defined by the internal
  // static counter which starts from 70. ( Just to avoid the clash with
  // internal sequence. Internal Sequences are added with a constant value )q
  @BeanProperty var entryKey: Long = _
  @BeanProperty var parentKey: Long = _
  @BeanProperty var sortOrder: String = _
  @BeanProperty var value: String = _
  @BeanProperty var level: Int = 1
  // For built-in and compose-defined value is equal to code
  // Built-In are not loaded through the excel sheet so ignoring those.
  @BooleanBeanProperty var composeDefined: Boolean = false

  def this(row: Row, isComposeDefined: Boolean) {
    this()
    val params = row.getParams
    code = params.get("Code")
    description = params.get("Description")
    aux1 = params.get("Aux1")
    aux2 = params.get("Aux2")
    filterKey = params.get("Filter_Key")
    parentCode = params.get("Parent")
    this.composeDefined = isComposeDefined
    entryKey = if (isComposeDefined) Odb.nextStaticSequence() else Odb.nextStaticSequence() + 260000
    // For Buit-In and Compose-Defined value is equal to code
    // Built-In are not loaded through the excel sheet so ignoring those.
    if (isComposeDefined) {
      value = code
    }
    // println("Entry auxiliary constructor...")
  }

  def addChildEntry(entry: Entry) {
    childEntries.add(entry)
    entry.setParentKey(entryKey)
    entry.level = level + 1
    if (entry.level > 5) throw new IllegalArgumentException("Level cannot be more than 5")
  }

  override def toString(): String = {
    val str = s"""
      |Code: $code
      |parentCode: $parentCode
      |Aux1 : $aux1
      |Aux2 : $aux2
      |Description : $description
      |filterKey : $filterKey
      |EnterKey  : $entryKey
      |ParentKey : $parentKey
      |sortOrder : $sortOrder
      |value : $value""".stripMargin.replaceAll("\n", " ")
    val builder = new StringBuilder(str)
    if (!childEntries.isEmpty) {
      for (e <- childEntries) {
        builder.append("Child : ").append(e)
      }
    }
    builder.toString
  }
}
