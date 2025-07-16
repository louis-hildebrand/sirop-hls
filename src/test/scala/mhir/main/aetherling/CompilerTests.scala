package mhir.main.aetherling

import mhir.gen.verilog.{VerilogTestRunner, VerilogTestbenchGenerator}
import mhir.gen.{TestInput, TestPassed}
import mhir.gen.vhdl.{VhdlTestRunner, VhdlTestbenchGenerator}
import mhir.ir._
import mhir.testing.HardwareTest
import org.scalatest.funsuite.AnyFunSuite

/** Tests for [[mhir.main.aetherling.Compiler]].
  */
@HardwareTest
class CompilerTests extends AnyFunSuite {
  private val BenchmarksDir =
    os.pwd / "src" / "test" / "resources" / "aetherling_benchmarks" / "original"
  private val VerilogBenchmarksDir =
    os.pwd / "src" / "test" / "resources" / "aetherling_benchmarks" / "verilog"
  private val VhdlDir = os.pwd / "src" / "test" / "vhdl"
  private val VerilogDir = os.pwd / "src" / "test" / "verilog"

  private def runTest(
      benchmark: String,
      simplify: Boolean,
      input: StmLiteral,
      expectedOutput: StmLiteral
  ): Unit = {
    val inFile = BenchmarksDir / s"$benchmark.txt"
    val outDir = VhdlDir / s"aetherling_${benchmark}_test"
    if (os.exists(outDir)) os.remove.all(outDir)
    val args = Args(inFile = inFile, outDir = outDir, simplify = simplify)
    val f = Compiler.compile(args)
    val testInput = TestInput(input.elems.map(e => Some(e)))
    VhdlTestbenchGenerator.makeTestbench(
      inputsByVar = Map(f.asInstanceOf[Function].param -> testInput),
      out = expectedOutput,
      dir = outDir
    )
    assert(VhdlTestRunner.testExistingProject(outDir) == TestPassed)
  }

  // TODO: Generalize all these tests
  test("Aetherling map_1") {
    val projectDir = VerilogDir / "aetherling_map_1_test"
    if (os.exists(projectDir)) os.remove.all(projectDir)
    os.makeDir.all(projectDir)
    os.copy(VerilogBenchmarksDir / "map_1.v", projectDir / "Top.v")
    val inputs = TestInput((0 until 200).map(i => Some(C(i)(U8))))
    val outputs = StmLiteral((0 until 200).map(i => C(i + 5)(U8)): _*)()
    VerilogTestbenchGenerator.makeTestbench(inputs, outputs, projectDir)
    assert(VerilogTestRunner.testExistingProject(projectDir) == TestPassed)
  }

  test("map:1") {
    runTest(
      "map_1",
      simplify = true,
      input = StmLiteral((0 until 200).map(t => C((t * t) % 250)(U8)): _*)(),
      expectedOutput =
        StmLiteral((0 until 200).map(t => C(5 + (t * t) % 250)(U8)): _*)()
    )
  }

  test("map:1:no-simplify") {
    runTest(
      "map_1",
      simplify = false,
      input = StmLiteral((0 until 200).map(t => C((t * t) % 250)(U8)): _*)(),
      expectedOutput =
        StmLiteral((0 until 200).map(t => C(5 + (t * t) % 250)(U8)): _*)()
    )
  }

  test("map:20") {
    runTest(
      "map_20",
      simplify = true,
      input = StmLiteral(
        (0 until 10).map(t =>
          VecLiteral((0 until 20).map(i => C(i + t)(U8)): _*)()
        ): _*
      )(),
      expectedOutput = StmLiteral(
        (0 until 10).map(t =>
          VecLiteral((0 until 20).map(i => C(i + t + 5)(U8)): _*)()
        ): _*
      )()
    )
  }

  test("map:20:no-simplify") {
    runTest(
      "map_20",
      simplify = false,
      input = StmLiteral(
        (0 until 10).map(t =>
          VecLiteral((0 until 20).map(i => C(i + t)(U8)): _*)()
        ): _*
      )(),
      expectedOutput = StmLiteral(
        (0 until 10).map(t =>
          VecLiteral((0 until 20).map(i => C(i + t + 5)(U8)): _*)()
        ): _*
      )()
    )
  }

  test("map:200") {
    runTest(
      "map_200",
      simplify = true,
      input = StmLiteral(VecLiteral((0 until 200).map(i => C(i)(U8)): _*)())(),
      expectedOutput = StmLiteral(
        VecLiteral((0 until 200).map(i => C(i + 5)(U8)): _*)()
      )()
    )
  }

  test("map:200:no-simplify") {
    runTest(
      "map_200",
      simplify = false,
      input = StmLiteral(VecLiteral((0 until 200).map(i => C(i)(U8)): _*)())(),
      expectedOutput = StmLiteral(
        VecLiteral((0 until 200).map(i => C(i + 5)(U8)): _*)()
      )()
    )
  }

  test("sum:1/840") {
    runTest(
      "sum_1_840",
      simplify = true,
      input = StmLiteral((0 until 840).map(t => C(t)(U32)): _*)(),
      expectedOutput = StmLiteral(C((0 until 840).sum)(U32))()
    )
  }

  test("sum:1/840:no-simplify") {
    runTest(
      "sum_1_840",
      simplify = false,
      input = StmLiteral((0 until 840).map(t => C(t)(U32)): _*)(),
      expectedOutput = StmLiteral(C((0 until 840).sum)(U32))()
    )
  }

  test("sum:1/210") {
    runTest(
      "sum_1_210",
      simplify = true,
      input = StmLiteral(
        (0 until 210).map(i =>
          VecLiteral((0 until 4).map(j => C(i + j)(U32)): _*)()
        ): _*
      )(),
      expectedOutput = StmLiteral(
        C((0 until 210).flatMap(i => (0 until 4).map(j => i + j)).sum)(U32)
      )()
    )
  }

  test("sum:1/210:no-simplify") {
    runTest(
      "sum_1_210",
      simplify = false,
      input = StmLiteral(
        (0 until 210).map(i =>
          VecLiteral((0 until 4).map(j => C(i + j)(U32)): _*)()
        ): _*
      )(),
      expectedOutput = StmLiteral(
        C((0 until 210).flatMap(i => (0 until 4).map(j => i + j)).sum)(U32)
      )()
    )
  }
}
