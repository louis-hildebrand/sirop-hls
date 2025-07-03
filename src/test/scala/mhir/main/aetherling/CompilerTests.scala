package mhir.main.aetherling

import mhir.gen.vhdl.{TestInput, TestPassed, TestRunner, TestbenchGenerator}
import mhir.ir._
import mhir.testing.VhdlTest
import org.scalatest.funsuite.AnyFunSuite

/** Tests for [[mhir.main.aetherling.Compiler]].
  */
@VhdlTest
class CompilerTests extends AnyFunSuite {
  private val BenchmarksDir =
    os.pwd / "src" / "test" / "resources" / "aetherling_benchmarks" / "original"
  private val VhdlDir = os.pwd / "vhdl"

  test("map:1") {
    val outDir = VhdlDir / "aetherling_map_1_test"
    if (os.exists(outDir)) os.remove.all(outDir)
    val args = Args(inFile = BenchmarksDir / "map_1.txt", outDir = outDir)
    val f = Compiler.compile(args)
    val input = TestInput(
      (0 until 200).flatMap(t => Seq(None, Some(C((t * t) % 250)(U8))))
    )
    val expectedOutput = StmLiteral(
      (0 until 200).map(t => C(5 + (t * t) % 250)(U8)): _*
    )()
    TestbenchGenerator.makeTestbench(
      inputsByVar = Map(f.asInstanceOf[Function].param -> input),
      out = expectedOutput,
      dir = outDir
    )
    assert(TestRunner.testExistingProject(outDir) == TestPassed)
  }

  test("map:20") {
    val inFile = BenchmarksDir / "map_20.txt"
    val outDir = VhdlDir / "aetherling_map_20_test"
    if (os.exists(outDir)) os.remove.all(outDir)
    val args = Args(inFile = inFile, outDir = outDir)
    val f = Compiler.compile(args)
    val input = TestInput(
      (0 until 10).flatMap(t =>
        Seq(None, Some(VecLiteral((0 until 20).map(i => C(i + t)(U8)): _*)()))
      )
    )
    val expectedOutput = StmLiteral(
      (0 until 10).map(t =>
        VecLiteral((0 until 20).map(i => C(i + t + 5)(U8)): _*)()
      ): _*
    )()
    TestbenchGenerator.makeTestbench(
      inputsByVar = Map(f.asInstanceOf[Function].param -> input),
      out = expectedOutput,
      dir = outDir
    )
    assert(TestRunner.testExistingProject(outDir) == TestPassed)
  }

  test("map:200") {
    val outDir = VhdlDir / "aetherling_map_200_test"
    if (os.exists(outDir)) os.remove.all(outDir)
    val args = Args(inFile = BenchmarksDir / "map_200.txt", outDir = outDir)
    val f = Compiler.compile(args)
    val input = TestInput(
      Seq(
        Some(StmLiteral(VecLiteral((0 until 200).map(i => C(i)(U8)): _*)())())
      )
    )
    val expectedOutput = StmLiteral(
      VecLiteral((0 until 200).map(i => C(i + 5)(U8)): _*)()
    )()
    TestbenchGenerator.makeTestbench(
      inputsByVar = Map(f.asInstanceOf[Function].param -> input),
      out = expectedOutput,
      dir = outDir
    )
    assert(TestRunner.testExistingProject(outDir) == TestPassed)
  }
}
