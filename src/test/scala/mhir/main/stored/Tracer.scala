package mhir.main.stored

import mhir.debug.Trace
import mhir.gen.vhdl.DirectTestInput
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.main.shared.{CompilerOptions, NullTarget}
import mhir.optimize.{OptimizerOptions, NameSimplifier => NS}

/** Class for tracing the execution of a program for debugging.
  */
object Tracer {

  /** Generates a trace of the execution of the given program.
    *
    * See [[mhir.debug]] for more debugging tools.
    *
    * @param progName
    *   the name of the program (e.g., `conv1d`).
    * @param optFlags
    *   the optimization settings to use.
    */
  def trace(progName: String, optFlags: OptimizerOptions): Trace = {
    val io = ProgramIO(s"${progName}_")
    val args = Args(
      program = Program(progName),
      options = CompilerOptions(
        showFinal = false,
        target = NullTarget,
        optFlags = optFlags
      )
    )
    val f = Compiler.compile(args)
    val stm = io.inputs.foldLeft(f)({
      case (f, in: DirectTestInput) =>
        val inStm = StmLiteral(in.elements.flatten.toSeq: _*)()
          .tchk()
          .asInstanceOf[StmLiteral]
          .toStmBuild
        val Function(x, body) = f
        body.subPreserveType(x -> inStm)
      case _ =>
        throw new IllegalArgumentException(
          s"Cannot trace when input is stored in a file."
        )
    })
    mhir.debug.Tracer.traceAll(NS.simplify(stm))
  }
}
