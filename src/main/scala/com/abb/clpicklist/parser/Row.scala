package com.abb.clpicklist.parser

import java.util.HashMap
import java.util.Iterator
import java.util.List
import java.util.Map
import org.apache.poi.ss.usermodel.Cell
import Row._
//remove if not needed
import scala.collection.JavaConversions._

/**
 * Represents a line of an XLS file. Parses line into map value pairs,
 */
object Row {

  def parseRow(headerRow: List[HeaderRow.Header], xlRow: org.apache.poi.ss.usermodel.Row): Row = {
    var row: Row = null
    if (xlRow != null) {
      row = new Row()
    } else {
      return row
    }
    val cellIterator = xlRow.cellIterator()
    while (cellIterator.hasNext) {
      val cell = cellIterator.next()
      val header = headerRow.get(cell.getColumnIndex)
      val `val` = getValue(cell)
      if (!isEmpty(`val`)) {
        row.add(header.value, `val`)
      } else {
        if (header.mandatory) {
          throw new RuntimeException("Mandatory attribute value is missing for " + header.value)
        }
      }
    }
    row
  }

  private def getValue(cell: Cell): String = cell.getCellType match {
    case Cell.CELL_TYPE_BLANK => ""
    case Cell.CELL_TYPE_BOOLEAN => "" + cell.getBooleanCellValue
    case Cell.CELL_TYPE_NUMERIC =>
      var `val` = cell.getNumericCellValue
      if ((`val` - `val`.toInt) == 0) {
        "" + `val`.toInt
      } else {
        "" + `val`
      }

    case Cell.CELL_TYPE_FORMULA => "" + cell.getCellFormula
    case Cell.CELL_TYPE_STRING => cell.getStringCellValue
    case _ => ""
  }

  private def isEmpty(`val`: String): Boolean = {
    `val` == null || `val`.trim().isEmpty || `val`.trim().equalsIgnoreCase("NULL")
  }
}

class Row private () {

  private val values = new HashMap[String, String]

  private def add(key: String, `val`: String) {
    values.put(key, `val`)
  }

  def getParams(): Map[String, String] = values

  override def toString(): String = values.toString
}
