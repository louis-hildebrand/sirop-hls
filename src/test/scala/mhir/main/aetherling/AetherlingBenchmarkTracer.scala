package mhir.main.aetherling

import mhir.debug.{Trace, Tracer}
import mhir.gen.vhdl.DirectTestInput
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.{NameSimplifier => NS}

/** Class for tracing the execution of an Aetherling benchmark for debugging.
  */
object AetherlingBenchmarkTracer {
  private val AetherlingBenchmarksDir =
    os.pwd / "src" / "test" / "resources" / "aetherling_benchmarks" / "original"

  /** Generates a trace of the execution of the simplified VHDL version of a
    * given Aetherling benchmark.
    *
    * See [[mhir.debug]] for more debugging tools.
    *
    * @param benchName
    *   the name of the benchmark (e.g., `conv1d_2`).
    */
  def trace(benchName: String): Trace = {
    val io = AetherlingBenchmarkIO.vhdlIO(benchName)
    val inFile = AetherlingBenchmarksDir / s"$benchName.txt"
    val args =
      Args(inFile = inFile, outDir = os.pwd / "deleteme", emitHdl = false)
    val f = Compiler.compile(args)
    val stm = io.inputs.foldLeft(f)({
      case (f, in: DirectTestInput) =>
        val inStm = StmLiteral(in.elems.flatten: _*)()
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
    Tracer.traceAll(NS.simplify(stm))
  }
}
