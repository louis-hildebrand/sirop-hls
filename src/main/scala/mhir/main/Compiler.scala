package mhir.main

import ch.qos.logback.classic.LoggerContext
import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time2
import mhir.main.aetherling.{
  Args => AetherlingArgs,
  Compiler => AetherlingFrontend
}
import mhir.main.sirop.{Args => SiropArgs, Compiler => SiropFrontend}
import mhir.main.shared.{BadArgsException, HelpException}
import mhir.main.stored.{Args => StoredArgs, Compiler => StoredFrontend}
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

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
    val (a, argparseTime) = time2("parsing CLI args", Level.DEBUG) {
      val a =
        try {
          Args(args.toList)
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
    compile(a, argparseTime)
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
      case SiropSource(inFile) =>
        SiropFrontend.compile(
          SiropArgs(inFile = inFile, options = args.options),
          argparseTime = argparseTime
        )
      case AetherlingSource(inFile) =>
        AetherlingFrontend.compile(
          AetherlingArgs(
            inFile = inFile,
            options = args.options
          ),
          argparseTime = argparseTime
        )
      case StoredSource(program) =>
        StoredFrontend.compile(
          StoredArgs(program = program, options = args.options),
          argparseTime = argparseTime
        )
    }
  }
}
