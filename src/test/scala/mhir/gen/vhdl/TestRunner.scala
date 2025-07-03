package mhir.gen.vhdl

import mhir.ir._

import os.Path
import scala.sys.process._

sealed trait TestResult
case object TestPassed extends TestResult
case object DesignCompileFailed extends TestResult
case object TestbenchCompileFailed extends TestResult
case object SimulationFailed extends TestResult
case object SimulationTimeout extends TestResult
case object UnknownFailure extends TestResult

object TestRunner {
  private val VHDL_DIR = os.pwd / "vhdl"
  private val RUN_TEST_SH = VHDL_DIR / "run_test.sh"
  private[vhdl] val VHDL_TEST_DIR = VHDL_DIR / "auto_tests"

  def testExistingProject(dir: String): TestResult = {
    val fullDir = VHDL_DIR / dir
    testExistingProject(fullDir)
  }

  def testExistingProject(dir: Path): TestResult = {
    val cmd = s"$RUN_TEST_SH $dir"
    cmd.! match {
      case 0 => TestPassed
      case 4 => DesignCompileFailed
      case 5 => TestbenchCompileFailed
      case 6 => SimulationTimeout
      case 7 => SimulationFailed
      case _ => UnknownFailure
    }
  }

  def testExpr(
      e: Expr,
      inputs: Seq[TestInput] = Seq()
  ): TestResult = {
    os.remove.all(VHDL_TEST_DIR)
    os.makeDir.all(VHDL_TEST_DIR)
    val dataTyp = e match {
      case s: StmBuild => VhdlGenerator.emitVhdl(s, VHDL_TEST_DIR)
      case f: Function => VhdlGenerator.emitVhdl(f, VHDL_TEST_DIR)
      case _ =>
        throw new IllegalArgumentException(
          s"Only streams and functions are supported (got expression $e)."
        )
    }
    TestbenchGenerator.makeTestbench(inputs, e, dataTyp, VHDL_TEST_DIR)
    testExistingProject(VHDL_TEST_DIR)
  }
}
