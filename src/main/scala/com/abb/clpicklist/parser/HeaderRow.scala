package com.abb.clpicklist.parser

import java.util.ArrayList
import java.util.Iterator
import java.util.List
import org.apache.poi.ss.usermodel.Cell
import HeaderRow._
import scala.beans.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

/**
 * Represents an XLS header
 */
object HeaderRow {

  def parseRow(row: org.apache.poi.ss.usermodel.Row): HeaderRow = {
    var headerRow: HeaderRow = null
    if (row != null) {
      headerRow = new HeaderRow()
    } else {
      throw new IllegalArgumentException("Header line cannot be empty")
    }
    val cellIterator = row.cellIterator()
    while (cellIterator.hasNext) {
      val cell = cellIterator.next()
      var `val` = cell.getStringCellValue
      var mandatory = false
      if (`val`.startsWith(":")) {
        `val` = `val`.substring(1)
        mandatory = true
      }
      val header = new HeaderRow.Header(`val`, mandatory)
      headerRow.add(header)
    }
    headerRow
  }

  // class Header(@BeanProperty val value: String, @BooleanBeanProperty val mandatory: Boolean)
  class Header(val value: String, val mandatory: Boolean)

}

class HeaderRow private () {

  private val values = new ArrayList[Header]

  private def add(header: Header) {
    values.add(header)
  }

  def getHeaderRow(): List[Header] = values

  override def toString(): String = values.toString
}
