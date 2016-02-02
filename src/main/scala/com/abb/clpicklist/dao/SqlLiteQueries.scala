package com.abb.clpicklist.dao


object SqlLiteQueries {

  val CREATE_PICK_LIST =
    """CREATE TABLE Pick_List(Name TEXT CONSTRAINT PK PRIMARY KEY, List_Class INTEGER);"""

  val CREATE_PICK_ENTRY = """
    |CREATE TABLE Pick_Entry(Entry_Key TEXT CONSTRAINT pk PRIMARY KEY,
    |                        In_List_Name TEXT,
    |                        Code TEXT,
    |                        Description TEXT,
    |                        Parent_Key TEXT,
    |                        Value TEXT,
    |                        Aux1 TEXT,
    |                        Aux2 TEXT,
    |                        Aux3 TEXT,
    |                        Aux4 TEXT,
    |                        Aux5 TEXT,
    |                        Filter_Key TEXT,
    |                        Sort_Order INTEGER);""".stripMargin

  val CREATE_PICK_DELTA = """
    |CREATE TABLE Pick_Delta(Entry_Key TEXT CONSTRAINT pk PRIMARY KEY,
    |                        In_List_Name TEXT,
    |                        Code TEXT,
    |                        Description TEXT,
    |                        Parent_Key TEXT,
    |                        Value TEXT,
    |                        Aux1 TEXT,
    |                        Aux2 TEXT,
    |                        Aux3 TEXT,
    |                        Aux4 TEXT,
    |                        Aux5 TEXT,
    |                        Filter_Key TEXT,
    |                        Op_Code TEXT);""".stripMargin

  val CREATE_PICK_VERSION = """
    |CREATE TABLE Pick_Version(Version_Num TEXT CONSTRAINT PK PRIMARY KEY,
    |                          Is_Working INTEGER,
    |                          System_Identifier TEXT);""".stripMargin

  val INSERT_PICK_ENTRY = """
    |INSERT INTO Pick_Entry(Entry_Key,
    |                       In_List_Name,
    |                       Code,
    |                       Description,
    |                       Parent_Key,
    |                       Value,
    |                       Aux1,
    |                       Aux2,
    |                       Aux3,
    |                       Aux4,
    |                       Aux5,
    |                       filter_key,
    |                       sort_order)
    |                       VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);""".stripMargin

  val INSERT_PICK_VERSION = """
    |INSERT INTO Pick_Version(Version_Num,
    |                         Is_Working,
    |                         System_Identifier)
    |                         VALUES ({version_num},{is_working},{system_identifier})""".stripMargin
}
