package mhir.main.aetherling

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time
import mhir.main.shared.{CompilerOptions, NullTarget}
import mhir.optimize.{OptimizerOptions, NameSimplifier => NS}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tagobjects.Slow
import org.slf4j.event.Level

/** Tests to ensure the simplified minimalist IR expressions for the Aetherling
  * benchmarks don't change unexpectedly.
  *
  * If the expression for a given benchmark changes, the measurements (e.g.,
  * resource usage) should be re-run, since they may have changed.
  */
class AetherlingBenchmarkChangeTests extends AnyFunSuite {
  private val AetherlingBenchmarksDir =
    os.pwd / "src" / "test" / "resources" / "aetherling_benchmarks" / "original"
  private val SimplifiedAetherlingBenchmarksDir =
    os.pwd / "src" / "test" / "resources" / "aetherling_benchmarks" / "simplified"

  private implicit val logger: Logger = Logger(getClass.getName)

  private val SlowBenchmarks: Set[String] =
    Set(
      "smallconv2d",
      "bigconv2d",
      "smallconvb2b",
      "bigconvb2b",
      "smallsharpen",
      "bigsharpen",
      "smallcamera",
      "bigcamera",
      "sqrt",
      "bigsobel"
    )

  os.makeDir.all(SimplifiedAetherlingBenchmarksDir)

  for (f <- os.list(AetherlingBenchmarksDir)) {
    val benchName = f.baseName
    val isSlow =
      SlowBenchmarks.exists(name => benchName.startsWith(s"${name}_"))
    val tags = if (isSlow) Seq(Slow) else Seq()

    test(benchName, tags: _*) {
      mhir.ir.reset()
      val inFile = AetherlingBenchmarksDir / s"$benchName.txt"
      val args = Args(
        inFile = inFile,
        options = CompilerOptions(
          targets = Set(NullTarget),
          optFlags = OptimizerOptions.all(
            assumeThroughputsMatch = true,
            maxLetStmBufSize = None
          )
        )
      )
      val f = Compiler.compile(args)
      val actual = time("simplifying names and printing", Level.DEBUG) {
        ExprPrinter.display(NS.simplify(f))
      }
      val expectedPath = SimplifiedAetherlingBenchmarksDir / s"$benchName.txt"
      os.write.over(expectedPath, actual)
    }
  }
}
