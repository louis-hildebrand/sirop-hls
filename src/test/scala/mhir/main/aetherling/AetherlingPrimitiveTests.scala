package mhir.main.aetherling

import com.typesafe.scalalogging.Logger
import mhir.gen.TestPassed
import mhir.gen.verilog.{
  VerilogProjectInitializer,
  VerilogTestRunner,
  VerilogTestbenchGenerator
}
import mhir.gen.vhdl.{VhdlTestRunner, VhdlTestbenchGenerator}
import mhir.logging.time
import mhir.main.shared.{CompilerOptions, VhdlTarget}
import mhir.optimize.OptimizerOptions
import mhir.testing.HardwareTest
import org.scalatest.funsuite.AnyFunSuite
import org.slf4j.event.Level

@HardwareTest
class AetherlingPrimitiveTests extends AnyFunSuite {
  private val AetherlingPrimitivesDir =
    os.pwd / "src" / "test" / "resources" / "aetherling_primitives" / "original"
  private val VerilogPrimitivesDir =
    os.pwd / "src" / "test" / "resources" / "aetherling_primitives" / "verilog"
  private val VhdlDir = os.pwd / "src" / "test" / "vhdl"
  private val VerilogDir = os.pwd / "src" / "test" / "verilog"
  private val TimeLimit: String = "10s"

  private val AllPrimitives: Seq[String] =
    os.list(AetherlingPrimitivesDir).map(_.baseName)
  private val PrimitivesToTest: Seq[String] =
    AllPrimitives

  private implicit val logger: Logger = Logger(getClass.getName)

  test("AllPrimitives") {
    assert(PrimitivesToTest == AllPrimitives)
  }

  for (testName <- PrimitivesToTest) {
    test(s"$testName:vhdl:simplified") {
      val io = AetherlingPrimitivesIO(testName).toVhdl
      val inFile = AetherlingPrimitivesDir / s"$testName.txt"
      val outDir = VhdlDir / "aetherling_unit" / s"${testName}_test"
      val args = Args(
        inFile = inFile,
        options = CompilerOptions(
          showFinal = false,
          target = VhdlTarget(outDir, overwrite = true),
          optFlags = OptimizerOptions.all(
            assumeThroughputsMatch = true,
            maxLetStmBufSize = None
          )
        )
      )
      Compiler.compile(args)
      time("generating VHDL testbench", Level.DEBUG) {
        VhdlTestbenchGenerator.makeFileBasedTestbench(io = io, dir = outDir)
      }
      val result = time("running VHDL simulation", Level.DEBUG) {
        VhdlTestRunner.testExistingProject(outDir, timeLimit = TimeLimit)
      }
      assert(result == TestPassed)
    }

    test(s"$testName:vhdl:unsimplified") {
      val io = AetherlingPrimitivesIO(testName).toVhdl
      val inFile = AetherlingPrimitivesDir / s"$testName.txt"
      val outDir =
        VhdlDir / "aetherling_unit" / s"${testName}_test_unsimplified"
      if (os.exists(outDir)) os.remove.all(outDir)
      val args = Args(
        inFile = inFile,
        options = CompilerOptions(
          showFinal = false,
          target = VhdlTarget(outDir, overwrite = true),
          optFlags = OptimizerOptions.Empty
        )
      )
      Compiler.compile(args)
      time("generating VHDL testbench", Level.DEBUG) {
        VhdlTestbenchGenerator.makeFileBasedTestbench(io = io, dir = outDir)
      }
      val result = time("running VHDL simulation", Level.DEBUG) {
        VhdlTestRunner.testExistingProject(outDir, timeLimit = TimeLimit)
      }
      assert(result == TestPassed)
    }

    test(s"$testName:verilog") {
      val io = AetherlingPrimitivesIO(testName).toVerilog
      val projectDir = VerilogDir / s"aetherling_unit" / s"${testName}_test"
      VerilogProjectInitializer.initProj(
        projectDir,
        VerilogPrimitivesDir / s"$testName.v",
        overwrite = true
      )
      time("generating Verilog testbench", Level.DEBUG) {
        VerilogTestbenchGenerator.makeTestbench(io, projectDir)
      }
      val result = time("running Verilog simulation", Level.DEBUG) {
        VerilogTestRunner.testExistingProject(projectDir, timeLimit = TimeLimit)
      }
      assert(result == TestPassed)
    }
  }
}
