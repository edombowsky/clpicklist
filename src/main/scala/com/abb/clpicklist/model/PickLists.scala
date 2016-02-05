package com.abb.clpicklist.model

import com.typesafe.scalalogging._
import better.files.File

import com.abb.clpicklist.dao.Odb
import com.abb.clpicklist.util.{ArgsParse, Config, FileUtil}


object PickLists extends LazyLogging {

}

class PickLists(directoryName: String) extends LazyLogging{

  private val pickLists = Odb.getPickLists

  buildPickList(directoryName)

  def getPickList(name: String): Option[PickList] = pickLists.get(name)

  def getAll = pickLists.values

  def buildDerivedPickList(pickList: PickList) {
    val entries = Odb.getEntries(pickList.sqlCode)
    // println(s"\n\n$entries\n\n")
    pickList.addDerivedEntries(entries)
    logger.info(s"Added derived pick-list entry ${pickList.name}")
  }

  def buildPickList(directoryName: String) {
    for (p <- pickLists.values if p.isDerived) buildDerivedPickList(p)

    val okFileExtensions: List[String] = List(Config.XLS_EXTENSIONS)
    val dir: File = File(directoryName)

    if (! dir.isDirectory || ! dir.isOwnerReadable) {
      throw new IllegalArgumentException(directoryName + " is not accessible...")
    }

    val files = FileUtil.getListOfFiles(directoryName, okFileExtensions)
    for (file <- files) {
      // val f = BFile(file.toString())
      val pickList = getPickList(file.nameWithoutExtension)
      logger.info(s"PickList : $pickList")
      logger.info(s"Parsing file ${file.name}")
      pickList match {
        case Some(pl) => if (! pl.isDerived) pl.initilize(file.toJava)
        case None => throw new RuntimeException(s"${file.nameWithoutExtension} does not not exists in ODB")
      }
      // println(pickList)
      logger.info(s"Parsing completed for ${file.name}")
    }
  }
}
