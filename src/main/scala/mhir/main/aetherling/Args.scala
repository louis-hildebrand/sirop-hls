package mhir.main.aetherling

import os.Path

/** Parsed command-line arguments.
  *
  * @param inFile
  *   the path to the input Aetherling program.
  * @param outDir
  *   the path to the directory in which to emit the VHDL.
  * @param help
  *   whether to show the help message and exit.
  * @param simplify
  *   whether to simplify the program before generating VHDL.
  * @param showParsed
  *   whether to display the program immediately after parsing.
  * @param showChecked
  *   whether to display the program immediately after type checking.
  * @param showLowered
  *   whether to display the program immediately after lowering.
  * @param showSimplified
  *   whether to display the program immediately after simplification.
  * @param showFinal
  *   whether to display the final program used to generate VHDL.
  */
case class Args(
    inFile: Path,
    outDir: Path,
    help: Boolean = false,
    simplify: Boolean = true,
    showParsed: Boolean = false,
    showChecked: Boolean = false,
    showLowered: Boolean = false,
    showSimplified: Boolean = false,
    showFinal: Boolean = false
)

/** Companion object for [[Args]].
  */
object Args {

  /** Parses the given command-line arguments.
    *
    * @param args
    *   the raw command-line arguments.
    */
  def apply(args: Array[String]): Args = {
    val (srcFilename, outDirName) = args match {
      case Array(src, out, _*) if src.startsWith("-") || out.startsWith("-") =>
        throw new BadArgsException(
          "path to Aetherling benchmark and output directory must come first"
        )
      case Array(src, out, _*) => (src, out)
      case _ =>
        throw new BadArgsException(
          "path to Aetherling benchmark and output directory are required"
        )
    }
    val src = Path(srcFilename, base = os.pwd)
    val out = Path(outDirName, base = os.pwd)
    var help = false
    var simplify = true
    var showParsed = false
    var showChecked = false
    var showLowered = false
    var showSimplified = false
    var showFinal = false
    for (a <- args.drop(2)) {
      a match {
        case "-h" | "--help" =>
          help = true
        case "--no-simplify" =>
          simplify = false
        case "--show-parsed" =>
          showParsed = true
        case "--show-checked" =>
          showChecked = true
        case "--show-lowered" =>
          showLowered = true
        case "--show-simplified" =>
          showSimplified = true
        case "--show-final" =>
          showFinal = true
        case _ =>
          throw new BadArgsException(s"unrecognized argument: $a")
      }
    }
    new Args(
      src,
      out,
      help = help,
      simplify = simplify,
      showParsed = showParsed,
      showChecked = showChecked,
      showLowered = showLowered,
      showSimplified = showSimplified,
      showFinal = showFinal
    )
  }

  private[main] def printShortUsage(): Unit = {
    val cls: String = {
      val fullName = Compiler.getClass.getCanonicalName
      if (fullName.endsWith("$")) {
        fullName.dropRight(1)
      } else {
        fullName
      }
    }
    println(
      s"Usage: runMain $cls SRC OUT [-h|--help] [--no-simplify] [--show-parsed] [--show-checked] [--show-lowered] [--show-simplified] [--show-final]"
    )
  }

  private[main] def printFullUsage(): Unit = {
    this.printShortUsage()
    println()
    println(
      s"""Arguments:
         |  SRC                path to the Aetherling program to compile
         |  OUT                path to the directory in which to emit the VHDL code
         |  -h, --help         print the help message and exit
         |  --no-simplify      do not simplify the program
         |  --show-parsed      show the program after parsing
         |  --show-checked     show the program after type checking
         |  --show-lowered     show the program after lowering
         |  --show-simplified  show the simplified program
         |  --show-final       show the final program right before VHDL generation
         |""".stripMargin.stripTrailing
    )
  }
}
