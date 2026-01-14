package mhir.main

import ch.qos.logback.classic.LoggerContext
import mhir.ir._
import mhir.main.aetherling.{
  Args => AetherlingArgs,
  Compiler => AetherlingFrontend
}
import mhir.main.sirop.{Args => SiropArgs, Compiler => SiropFrontend}
import mhir.main.shared.{BadArgsException, HelpException}
import mhir.main.stored.{Args => StoredArgs, Compiler => StoredFrontend}
import org.slf4j.LoggerFactory

/** Main compiler.
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
    args.options.logLevel match {
      case None => ()
      case Some(logLevel) =>
        LoggerFactory.getILoggerFactory
          .asInstanceOf[LoggerContext]
          .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
          .setLevel(logLevel)
    }
    args.src match {
      case SiropSource(inFile) =>
        SiropFrontend.compile(
          SiropArgs(inFile = inFile, options = args.options)
        )
      case AetherlingSource(inFile) =>
        AetherlingFrontend.compile(
          AetherlingArgs(inFile = inFile, options = args.options)
        )
      case StoredSource(program) =>
        StoredFrontend.compile(
          StoredArgs(program = program, options = args.options)
        )
    }
  }
}
