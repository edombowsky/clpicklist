/*
 * Copyright (C) 2015 ABB.  All rights reserved.
 */
package com.abb.scalalikejdbc_example

import com.abb.scalalikejdbc_example.util.ArgsParse._

import collection.mutable.Stack
import org.scalatest._

class ArgsParseSpec extends FlatSpec with Matchers {

  // Required positional arguments by key in options
  val required = List('arg0, 'arg1, 'arg2)

  // Options with value
  val optional = Map("--db|-d" -> 'db)

  // Flags
  val flags = Map("--help|-h" -> 'help)

  // Default options that are passed in
  var defaultOptions = Map[Symbol, String]('help -> "false", 'verbose -> "true")

  behavior of "ArgsParse"

  it should "read the correct values" in {
    val argv = List("--help", "--db", "emd/emd@dellr815c_r12102")
    val options = parseOptions(argv, required, optional, flags, defaultOptions)
    options('help).toBoolean should be(true)
    options('db).toString should be("emd/emd@dellr815c_r12102")
  }

  it should "accept short names" in {
    val argv = List("-h")
    val options = parseOptions(argv, required, optional, flags, defaultOptions)
    options('help).toBoolean should be(true)
  }

  it should "keep the correct default values" in {
    val argv = List("--db", "emd/emd@dellr815c_r12102")
    val options = parseOptions(argv, required, optional, flags, defaultOptions)
    options('help).toBoolean should be(false)
    options('verbose).toBoolean should be(true)
  }

  it should "keep the correct number of args" in {
    val argv = List("--db", "emd/emd@dellr815c_r12102", "arg00", "arg01", "arg02")
    val options = parseOptions(argv, required, optional, flags, defaultOptions)
    options('arg0) should be("arg00")
    options('arg1) should be("arg01")
    options('arg2) should be("arg02")
  }

  // it should "reject unknown options with UnknownParam" in {
  //   val argv = List("unknown abc def --abc")
  //   intercept[.UnknownParam] {
  //     val options = .parseOptions(argv, required, optional, flags, defaultOptions, true)
  //   }
  // }
}




// import org.scalatest.FunSuite

// import ArgsOps._

// class ArgsOpsTest extends FunSpec with Matchers {

//     val parser = ArgsOpsParser("--someInt|-i" -> 4, "--someFlag|-f", "--someWord|-w" -> "hello")

//     test("the ArgsOps parser rejects unknow options") {
//         intercept[UnknownParam](parser <<| Array("--unknown"))
//     }

//     test("the ArgsOps parser rejects options without args"){
//         intercept[OptionWithoutExpectedParam](parser <<| Array("--someInt","--someFlag"))
//         intercept[OptionWithoutExpectedParam](parser <<| Array("--someInt", "3x"))
//         intercept[OptionWithoutExpectedParam](parser <<| Array("--someInt"))
//         intercept[OptionWithoutExpectedParam](parser <<| Array("--someWord", "--someFlag"))
//         intercept[OptionWithoutExpectedParam](parser <<| Array("--someWord"))
//     }

//     test("the ArgsOps parser reads the correct values"){
//         val argsOps = parser <<| Array("--someInt", "3", "--someFlag", "--someWord", "goodbye")
//         assert(argsOps("--someInt").asInt == 3)
//         assert(argsOps("--someFlag").asBoolean == true)
//         assert(argsOps("--someWord").asString == "goodbye")
//     }

//     test("the ArgsOps parser accepts shorted name"){
//         val argsOps = parser <<| Array("-i", "3", "-f", "-w", "goodbye")
//         assert(argsOps("-i").asInt == 3)
//         assert(argsOps("-f").asBoolean == true)
//         assert(argsOps("-w").asString == "goodbye")
//     }

//     test("the ArgsOps parser is coherent between shorted and complet names"){
//         val argsOps = parser <<| Array("-i", "3", "-f", "-w", "goodbye")
//         assert(argsOps("--someInt") == argsOps("-i"))
//         assert(argsOps("--someFlag") == argsOps("-f"))
//         assert(argsOps("--someWord") == argsOps("-w"))
//     }

//     test("the ArgsOps parser keeps the correct default values"){
//         val argsOps = parser <<| Array("arg")
//         val someInt : Int = argsOps("--someInt")
//         val someFlag : Boolean = argsOps("--someFlag")
//         val someWord : String = argsOps("--someWord")
//         assert(someInt == 4)
//         assert(someFlag == false)
//         assert(someWord == "hello")
//     }

//     test("the ArgsOps parser keeps the correct number of arg"){
//         val argsOps = parser <<| Array("--someInt", "3", "--someFlag", "--someWord", "goodbye", "arg0", "arg1", "arg2")
//         assert(argsOps.args(0) == "arg0")
//         assert(argsOps.args(1) == "arg1")
//         assert(argsOps.args(2) == "arg2")
//     }

// }

