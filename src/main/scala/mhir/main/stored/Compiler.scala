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
  def compile(args: Args, argparseTime: Duration): Expr = {
    SC.compile(
      mhir.ir.Program(args.program),
      args.options,
      argparseTime = argparseTime,
      parseTime = Duration.ZERO
    )
  }
}
