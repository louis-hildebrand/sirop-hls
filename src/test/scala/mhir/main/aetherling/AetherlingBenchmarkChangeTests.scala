package mhir.main.aetherling

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time
import mhir.main.shared.{CompilerOptions, NullTarget}
import mhir.optimize.{OptimizerOptions, NameSimplifier => NS}
import mhir.parse.sirop.Parser
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

  private val SaveChanges: Boolean = false

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
      "bigsobel",
      "bigmmm"
    )

  os.makeDir.all(SimplifiedAetherlingBenchmarksDir)

  private val AllBenchmarks = os.list(AetherlingBenchmarksDir).map(_.baseName)
  private val ActiveBenchmarks = AllBenchmarks
    .filter(name =>
      Set(
        "map",
        "dot",
        "bigmvm",
        "bigmmm",
        "conv1d",
        "bigconv2d",
        "bigconvb2b",
        "bigsharpen",
        "bigsobel",
        "bigcamera"
      ).exists(prefix => name.startsWith(prefix))
    )
  private val BenchmarksToTest = ActiveBenchmarks

  test("ActiveBenchmarks") {
    assert(ActiveBenchmarks.forall(b => BenchmarksToTest.contains(b)))
  }

  for (benchName <- BenchmarksToTest) {
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
      val newExpr = Compiler.compile(args)
      val expectedPath = SimplifiedAetherlingBenchmarksDir / s"$benchName.txt"
      if (SaveChanges) {
        val simplified = time("simplifying names", Level.DEBUG) {
          NS.simplify(newExpr)
        }
        val str = time("pretty-printing", Level.DEBUG) {
          ExprPrinter.display(simplified)
        }
        time("writing expression to file", Level.DEBUG) {
          os.write.over(expectedPath, str)
        }
      }
      val oldExpr = time("parsing saved expression", Level.DEBUG) {
        Parser.parse(os.read(expectedPath))
      }
      // TODO: Why is this needed? T-T
      val parsedNewExpr =
        time("stringifying and parsing new expression", Level.DEBUG) {
          Parser.parse(ExprPrinter.display(NS.simplify(newExpr)))
        }
      time("comparing result to saved expression", Level.DEBUG) {
        assert(parsedNewExpr == oldExpr)
      }
    }
  }
}
