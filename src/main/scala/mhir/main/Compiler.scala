package mhir.main

import ch.qos.logback.classic.LoggerContext
import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.typecheck.{TypeCheck, TypeError}
import mhir.logging.time2
import mhir.main.aetherling.{
  Args => AetherlingArgs,
  Compiler => AetherlingFrontend
}
import mhir.main.repl.Repl
import mhir.main.shared.{
  BadArgsException,
  HelpException,
  Version,
  VersionException
}
import mhir.main.sirop.{Args => SiropArgs, Compiler => SiropFrontend}
import mhir.main.stored.{Args => StoredArgs, Compiler => StoredFrontend}
import mhir.parse.SyntaxError
import org.slf4j.LoggerFactory

import java.time.Duration

/** Main compiler.
  */
object Compiler {

  private implicit val logger: Logger = Logger(getClass.getName)

  /** The program entry point.
    *
    * @param args
    *   the command-line arguments.
    */
  def main(args: Array[String]): Unit = {
    val (a, argparseTime) = time2("parsing CLI args") {
      val a =
        try {
          Args(args.toList)
        } catch {
          case HelpException =>
            Args.printFullUsage()
            return
          case VersionException =>
            println(Version())
            return
          case exc: BadArgsException =>
            println(s"Invalid command-line arguments: ${exc.getMessage}")
            println()
            Args.printShortUsage()
            return
        }
      a.options.logLevel match {
        case None => ()
        case Some(logLevel) =>
          LoggerFactory.getILoggerFactory
            .asInstanceOf[LoggerContext]
            .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
            .setLevel(logLevel)
      }
      a
    }
    try {
      compile(a, argparseTime)
    } catch {
      case ex: SyntaxError =>
        println(ex.getMessage)
        sys.exit(1)
      case ex: TypeError =>
        println(ex.getMessage)
        sys.exit(1)
    }
  }

  /** Runs the compiler.
    *
    * @param args
    *   the parsed command-line arguments.
    * @return
    *   the final program from which VHDL was generated.
    */
  def compile(args: Args, argparseTime: Duration): Expr = {
    args.options.logLevel match {
      case None => ()
      case Some(logLevel) =>
        LoggerFactory.getILoggerFactory
          .asInstanceOf[LoggerContext]
          .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
          .setLevel(logLevel)
    }
    args.src match {
      case None =>
        Repl.run()
        Tuple()().tchk()
      case Some(SiropSource(inFile)) =>
        SiropFrontend.compile(
          SiropArgs(inFile = inFile, options = args.options),
          argparseTime = argparseTime
        )
      case Some(AetherlingSource(inFile)) =>
        AetherlingFrontend.compile(
          AetherlingArgs(
            inFile = inFile,
            options = args.options
          ),
          argparseTime = argparseTime
        )
      case Some(StoredSource(program)) =>
        StoredFrontend.compile(
          StoredArgs(program = program, options = args.options),
          argparseTime = argparseTime
        )
    }
  }
}
