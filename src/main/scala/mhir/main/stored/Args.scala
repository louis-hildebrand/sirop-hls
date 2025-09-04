package mhir.main.stored

import mhir.ir.Expr
import mhir.main.shared.{BadArgsException, CompilerOptions, HelpException}

/** Parsed command-line options.
  *
  * @param program
  *   the expression to compile.
  * @param options
  *   compiler options.
  */
case class Args(program: Expr, options: CompilerOptions)

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

    val programName = args match {
      case Array(name, _*) if name.startsWith("-") =>
        throw new BadArgsException("program name must come first")
      case Array(name, _*) => name
      case _ =>
        throw new BadArgsException("program name is required")
    }

    val expr = Program(programName)
    val options = CompilerOptions(args.drop(1))
    Args(program = expr, options = options)
  }

  private[stored] def printShortUsage(): Unit = {
    val cls: String = {
      val fullName = Compiler.getClass.getCanonicalName
      if (fullName.endsWith("$")) {
        fullName.dropRight(1)
      } else {
        fullName
      }
    }
    println(s"Usage: runMain $cls PROG [OPTION]... [-h|--help]")
  }

  private[stored] def printFullUsage(): Unit = {
    this.printShortUsage()
    println()
    println(
      s"""Arguments:
         |  PROG               the name of the program to compile
         |  -h, --help         print the help message and exit
         |
         |""".stripMargin
        ++ CompilerOptions.longUsage
    )
  }
}
