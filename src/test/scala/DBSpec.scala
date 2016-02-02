/*
 * Copyright (C) 2015 ABB.  All rights reserved.
 */
package com.abb.scalalikejdbc_example

import com.abb.scalalikejdbc.dao._

import org.scalatest.{BeforeAndAfter, FunSpec}
import java.sql.Statement
import org.h2.jdbc.JdbcSQLException
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success}


class JdbcSpec extends FunSpec with BeforeAndAfter {
  val connectionInfo = new Jdbc.ConnectionInfo("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", "", "")
  val sql = "SELECT ID, DESCRIPTION FROM EXAMPLE ORDER BY DESCRIPTION"

  before {
    Jdbc.withStatement(connectionInfo, (stmt: Statement) => {
      stmt.execute("CREATE TABLE EXAMPLE(ID INT PRIMARY KEY, DESCRIPTION VARCHAR)")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(0, 'Zero')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(1, 'One')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(2, 'Two')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(3, 'Three')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(4, 'Four')")
    })
  }

  describe("withConnection") {
    it ("provides a valid JDBC Connection") {
      assert(Jdbc.withConnection(connectionInfo, c => {
        assert(c.getMetaData != null)
      }).isSuccess)
    }
