package mhir.main.aetherling

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time
import mhir.main.shared.{BadArgsException, HelpException, Compiler => SC}
import mhir.parse.AetherlingParser
import org.slf4j.event.Level

/** A compiler for programs written in
  * [[https://dl.acm.org/doi/10.1145/3385412.3385983 Aetherling]]'s space-time
  * language.
  */
object Compiler {

  private implicit val logger: Logger = Logger(getClass.getName)

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
    logger.info(s"parsing Aetherling code from ${args.inFile}")
    val parsed = time("parsing", Level.INFO) {
      val aetherlingCode = os.read(args.inFile)
      AetherlingParser.parse(aetherlingCode)
    }
    SC.compile(parsed, args.options)
  }
}
