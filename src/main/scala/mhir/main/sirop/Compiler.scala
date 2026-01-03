package mhir.main.sirop

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time2
import mhir.main.shared.{Compiler => SC}
import mhir.parse.sirop.Parser
import org.slf4j.event.Level

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
  def compile(args: Args): Expr = {
    logger.debug(s"parsing Sirop code from ${args.inFile}")
    val (parsed, parseTime) = time2("parsing", Level.DEBUG) {
      val code = os.read(args.inFile)
      Parser.parse(code)
    }
    SC.compile(parsed, args.options, parseTime = parseTime)
  }
}
