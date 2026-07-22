package mhir.main.sirop

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time2
import mhir.main.shared.{Compiler => SC}
import mhir.parse.sirop.Parser
import org.slf4j.event.Level

import java.time.Duration

/** A compiler for programs written in Sirop.
  */
object Compiler {

  private implicit val logger: Logger = Logger(getClass.getName)

  /** Runs the compiler.
    *
    * @param args
    *   the parsed command-line arguments.
    * @return
    *   the final program from which VHDL was generated.
    */
  def compile(args: Args, argparseTime: Duration): Expr = {
    logger.debug(s"parsing Sirop code from ${args.inFile}")
    val (parsed, parseTime) = time2("parsing", Level.DEBUG) {
      Parser.parse(args.inFile, args.constOverrides)
    }
    SC.compile(
      parsed,
      args.options,
      argparseTime = argparseTime,
      parseTime = parseTime
    )
  }
}
