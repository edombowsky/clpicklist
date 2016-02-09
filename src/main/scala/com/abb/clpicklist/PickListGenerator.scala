/*
 * Example :
 * C:\Users\carakoo\Documents\NetBeansProjects>java -jar PickListGenerator-1.0-SNAPSHOT-onejar.jar -pickDir "c:\Users\carakoo\Documents\PickList" -outDir "c:\Users\carakoo\Documents\PickList" -dbUser rkn_odb   -dbPassword rkn_odb  -dbInstance vsshp01_r12102
 *
 * Usage:
 * java -jar PickListGenerator  -pickDir <Directory where xls sheet are kept>  -outDir <Output Directory where picklist.db should be generated> -dbUser <DB User Name> -dbPassword <DB Password> -dbInstance <DB Instance name>
 *
 * Design Steps  ( I have to update this as well. I think I have taken some deviation from this while coding)
 *
 *
 * 1. Push all the excel sheet data to Pick_entry table
 *    Parse all the excel sheet and create a List of PickList 
 *             Create the hierarchical data  - PickList->Entries->child entries ->..
 *             where excel sheet name is the AlternativeName for the pick list.
 *             Get the pick list id from Pick_list table
 *             insert the each entries into PICK_ENTRY_VW  (  LIST_ID, AUX1, AUX2, AUX3, AUX4, AUX5, CODE, DESCRIPTION, ENTRY_KEY (PICK_ENTRY_KEY_SEQ.nextVal),
 *             pick_entry_api.create_entry(p_entry_key    => :NEW.entry_key,
 *                                   p_code         => :NEW.code,
 *                                   p_list_id      => :NEW.list_id,
 *                                   p_parent_key   => :NEW.parent_key,
 *                                   p_value        => :NEW.value,
 *                                   p_description  => :NEW.description,
 *                                   p_aux1         => :NEW.aux1,
 *                                   p_aux2         => :NEW.aux2,
 *                                   p_aux3         => :NEW.aux3,
 *                                   p_aux4         => :NEW.aux4,
 *                                   p_aux5         => :NEW.aux5,
 *                                   p_filter_key   => :NEW.filter_key,
 *                                   p_sort_order   => :NEW.sort_order);
 *   Level and version is taken care by this API.
 *
 *
 * 2. Calculate the Delta ( Pick_Delta_APi.Build_Delta )
 * 3. Activate the Pick List ( Pick_Version_APi.Activate )
 * 4. Get all the pick list names from ODB ( Pick_list_api.select_lists)
 * 5. for each list  with condition "IS_MA_DK = 1 or IS_MA_CE = 1 or IS_MA_J2ME = 1"
 *
 *    id -> list_id
 *            version-> current version
 *            level -> ? (0)
 *
 *            Need to group by Compose Defined, Derived , inheritance separately
 *
 *            for each id and version Get the rows from PickList Entry view ( select * from pick_entry_vw where list_id = 1 and is_current = 1)
 *                Write into SQLLiteDB.
 *            end
 *            end
 *
 */
package com.abb.clpicklist

import com.typesafe.scalalogging._
import better.files.File

import com.abb.clpicklist.dao.{Odb, SqlLite, PickEntryHelper}
import com.abb.clpicklist.util.Config
import com.abb.clpicklist.model.PickLists


/**
 * Main entry point for generating Service Suite picklist.db files.
 *
 * To run via sbt command line:
 * {{{
 * export TOP=$PWD
 * mkdir logs
 * sbt "run --db=emd1/emd1@dellr815c_r12102 --pickDir $PWD/picklists"
 * }}}
 */
object PickListGenerator extends LazyLogging {

  def generateSQLLiteDB() {
    SqlLite.createSQLLiteTables()
    val version = Odb.getPickListVersion.getOrElse("")
    val systemId = Odb.getSystemIdentifier.getOrElse("")
    val entries = Odb.getEntries
    SqlLite.createEntries(entries)
    SqlLite.addVersionEntry(version, false, systemId)
    SqlLite.movePickDB()
  }

  def main(argv: Array[String]) {

    // Parse options based on the command line args
    val okayToProceed = Config.parseArguments(argv)

    if (! okayToProceed) sys.exit(0)

    logger.info(raw"""
      Starting command line picklist generator with following parameters:
      pickDir            : ${Config.getValue("pd")}
      outputDir          : ${Config.getValue("od")}
      db_connect_string  : ${Config.getValue("db")}""")

    logger.info("starting Main...")

    val list = new PickLists(Config.getValue("pd", "/yvr/home/caeadom/PickLists"))
    // println("PICKLISTS:: ")
    // println(list)
    list.getAll
    logger.info("Creating pick list entries in ODB")
    PickEntryHelper.createPickList(list)
    logger.info("Activate pick list")
    // Odb.activatePickList()  needs to be un-commented for final version
    logger.info("Generate SQLLite DB")
    generateSQLLiteDB()
  }
}
