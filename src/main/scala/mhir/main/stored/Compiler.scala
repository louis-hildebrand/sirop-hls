package mhir.main.stored

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time2
import mhir.main.shared.{Compiler => SC}
import org.slf4j.event.Level

import java.time.Duration

/** A compiler for pre-written programs in the higher-level IR.
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
    logger.debug(s"parsing stored program '${args.program}'")
    val (prog, parseTime) = time2("parsing", Level.DEBUG) {
      mhir.main.stored.StoredProgram(args.program)
    }
    SC.compile(
      prog,
      args.options,
      argparseTime = argparseTime,
      parseTime = parseTime
    )
  }
}
