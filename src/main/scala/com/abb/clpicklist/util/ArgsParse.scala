package com.abb.clpicklist.util

object ArgsParse {

  implicit class OptionMapImprovements(val m: Map[String, Symbol]) {
    def match_key(opt: String): String = {
      m.keys.find(_.matches(s""".*$opt(\\|.*)?""")).getOrElse("")
    }
    def match_get(opt: String): Option[Symbol] = {
      m.get(m.match_key(opt))
    }
    def match_apply(opt: String): Symbol = {
      m(m.match_key(opt))
    }
  }

  type OptionMap = Map[Symbol, String]
  type OptionMapBuilder = Map[String, Symbol]

  def parseOptions(args:     List[String],
                   required: List[Symbol],
                   optional: OptionMapBuilder,
                   flags:    OptionMapBuilder,
                   options:  OptionMap = Map[Symbol, String](),
                   strict:   Boolean = false
                  ): OptionMap = {
    args match {
      // Empty list
      case Nil => options

      // Options with values
      case key :: value :: tail if optional.match_get(key) != None =>
      parseOptions(tail, required, optional, flags,  options ++ Map(optional.match_apply(key) -> value))

      // Flags
      case key :: tail if flags.match_get(key) != None =>
      parseOptions(tail, required, optional, flags,  options ++ Map(flags.match_apply(key) -> "true"))

      // Positional arguments
      case value :: tail if required != Nil =>
      parseOptions(tail, required.tail, optional, flags,  options ++ Map(required.head -> value))

      // Generate symbols out of remaining arguments
      case value :: tail if !strict => parseOptions(tail, required, optional, flags,  options ++ Map(Symbol(value) -> value))

      case _ if strict =>
        // throw UnknownParam(args.mkString(", "))
        printf("Unknown argument(s): %s\n", args.mkString(", "))
        sys.exit(1)
    }
  }
}
