package com.abb.clpicklist.parser

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.ArrayList
import java.util.Iterator
import java.util.List

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook

//remove if not needed
// import scala.collection.JavaConversions._

import com.abb.clpicklist.model.PickList

object XlsParser {

  def parse(file: File, pickList: PickList) {
    if (file == null) throw new IllegalArgumentException("file cannot be empty")
    if (!file.exists() || !file.canRead()) throw new FileNotFoundException("File " + file + " does not exists or is not readable")
    val fis = new FileInputStream(file)
    val workbook = new HSSFWorkbook(fis)
    val count = workbook.getNumberOfSheets
    if (count > 0) {
      for (i <- 0 until count) {
        val rows = parseSheet(workbook.getSheetAt(i))
        pickList.addEntries(rows)
      }
    }
    // println(pickList)
  }

  private def parseSheet(sheet: Sheet): List[Row] = {
    val rows = new ArrayList[Row]()
    val rowIterator = sheet.iterator()
    rowIterator.next()
    val headerRow = HeaderRow.parseRow(rowIterator.next())
    while (rowIterator.hasNext) {
      val row = Row.parseRow(headerRow.getHeaderRow, rowIterator.next())
      if (row != null && !row.getParams.isEmpty) {
        rows.add(row)
      }
    }
    rows
  }
}
