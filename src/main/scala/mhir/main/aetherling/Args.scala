package mhir.main.aetherling

import mhir.main.shared.{BadArgsException, CompilerOptions, HelpException}
import os.Path

/** Parsed command-line arguments.
  *
  * @param inFile
  *   the path to the input Aetherling program.
  * @param options
  *   options to pass to the compiler.
  */
case class Args(inFile: Path, options: CompilerOptions)

/** Companion object for [[Args]].
  */
object Args {

  /** Parses the given command-line arguments.
    *
    * @param args
    *   the raw command-line arguments.
    */
  def apply(args: Array[String]): Args = {
    if (args.contains("-h") || args.contains("--help")) {
      throw HelpException
    }

    val srcFilename = args match {
      case Array(src, _*) if src.startsWith("-") =>
        throw new BadArgsException("path to Aetherling program must come first")
      case Array(src, _*) => src
      case _ =>
        throw new BadArgsException("path to Aetherling program is required")
    }
    val src = Path(srcFilename, base = os.pwd)
    val options = CompilerOptions(args.drop(1))
    Args(
      inFile = src,
      // The Aetherling compiler guarantees that each producer/consumer pair
      // has the same "time."
      options = options.copy(optFlags =
        options.optFlags.copy(assumeThroughputsMatch = true)
      )
    )
  }

  private[aetherling] def printShortUsage(): Unit = {
    val cls: String = {
      val fullName = Compiler.getClass.getCanonicalName
      if (fullName.endsWith("$")) {
        fullName.dropRight(1)
      } else {
        fullName
      }
    }
    println(s"Usage: runMain $cls SRC [OPTION]... [-h|--help]")
  }

  private[aetherling] def printFullUsage(): Unit = {
    this.printShortUsage()
    println()
    println(
      s"""Arguments:
         |  SRC            path to the Aetherling program to compile
         |  -h, --help     print the help message and exit
         |
         |""".stripMargin
        ++ CompilerOptions.longUsage
    )
  }
}
