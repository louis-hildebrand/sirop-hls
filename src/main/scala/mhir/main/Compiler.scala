package mhir.main

import ch.qos.logback.classic.LoggerContext
import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.eval.{EvalException, TestError}
import mhir.gen.CodegenError
import mhir.ir._
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
import mhir.sem.SemanticError
import mhir.typecheck.{NameError, TypeCheck, TypeError}
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
    try {
      // Do not set the log level to DEBUG here, since we don't even know yet
      // what the effective log level should be!
      val (a, argparseTime) = time2("parsing CLI args", Level.TRACE) {
        Args(args.toList)
      }
      compile(a, argparseTime)
    } catch {
      case HelpException =>
        Args.printFullUsage()
      case VersionException =>
        println(Version())
      case exc: BadArgsException =>
        Console.withOut(Console.err) {
          println(
            s"Invalid command-line arguments: ${exc.getMessage}"
          )
          println()
          Args.printShortUsage()
        }
        sys.exit(1)
      case ex @ (_: SyntaxError | _: TypeError | _: NameError |
          _: SemanticError | _: CodegenError | _: EvalException | _: TestError |
          _: FileError) =>
        Console.err.println(ex.getMessage)
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
      case Some(SiropSource(inFile, constOverrides)) =>
        SiropFrontend.compile(
          SiropArgs(
            inFile = inFile,
            constOverrides = constOverrides,
            options = args.options
          ),
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
