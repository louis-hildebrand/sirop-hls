package mhir.main.stored

import mhir.ir._
import mhir.main.shared.{Compiler => SC}

import java.time.Duration

/** A compiler for pre-written programs in the higher-level IR.
  */
object Compiler {

  /** Runs the compiler.
    *
    * @param args
    *   the parsed command-line arguments.
    * @return
    *   the final program from which VHDL was generated.
    */
  def compile(args: Args): Expr = {
    SC.compile(args.program, args.options, parseTime = Duration.ZERO)
  }
}
