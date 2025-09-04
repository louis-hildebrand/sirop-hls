package mhir.main.stored

import mhir.ir._
import mhir.main.shared.{BadArgsException, HelpException, Compiler => SC}

/** A compiler for pre-written programs in the higher-level IR.
  */
object Compiler {

  /** The program entry point.
    *
    * @param args
    *   the command-line arguments.
    */
  def main(args: Array[String]): Unit = {
    val a =
      try {
        Args(args)
      } catch {
        case HelpException =>
          Args.printFullUsage()
          return
        case exc: BadArgsException =>
          println(s"Invalid command-line arguments: ${exc.getMessage}")
          println()
          Args.printShortUsage()
          return
      }
    compile(a)
  }

  /** Runs the compiler.
    *
    * @param args
    *   the parsed command-line arguments.
    * @return
    *   the final program from which VHDL was generated.
    */
  def compile(args: Args): Expr = {
    SC.compile(args.program, args.options)
  }
}
