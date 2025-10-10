package mhir.main.aetherling

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time2
import mhir.main.shared.{Compiler => SC}
import mhir.parse.AetherlingParser
import org.slf4j.event.Level

/** A compiler for programs written in
  * [[https://dl.acm.org/doi/10.1145/3385412.3385983 Aetherling]]'s space-time
  * language.
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
    logger.debug(s"parsing Aetherling code from ${args.inFile}")
    val (parsed, parseTime) = time2("parsing", Level.DEBUG) {
      val aetherlingCode = os.read(args.inFile)
      AetherlingParser.parse(aetherlingCode)
    }
    SC.compile(parsed, args.options, parseTime = parseTime)
  }
}
