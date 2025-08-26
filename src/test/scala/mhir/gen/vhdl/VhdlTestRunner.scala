package mhir.gen
package vhdl

import mhir.gen.{
  DesignCompileFailed,
  SimulationFailed,
  SimulationTimeout,
  TestPassed,
  TestResult,
  TestbenchCompileFailed,
  UnknownFailure
}
import mhir.ir._
import os.{Path, RelPath}

import scala.sys.process._

/** Provides methods for testing a VHDL project.
  */
object VhdlTestRunner {
  private val VHDL_DIR = os.pwd / "src" / "test" / "vhdl"
  private val RUN_TEST_SH = os.pwd / "src" / "test" / "sh" / "test_vhdl.sh"
  private[vhdl] val VHDL_TEST_DIR = VHDL_DIR / "auto_tests"

  /** See [[testExistingProject(dir:Path*]].
    *
    * @param dir
    *   the name of the VHDL project, relative to the `vhdl` directory within
    *   this repository.
    * @return
    *   the test result.
    */
  def testExistingProject(dir: String): TestResult = {
    testExistingProject(VHDL_DIR / RelPath(dir))
  }

  /** Test an existing VHDL project that has both a design and a testbench.
    *
    * @param dir
    *   the path to the VHDL project.
    * @return
    *   the test result.
    */
  def testExistingProject(dir: Path, timeLimit: String = ""): TestResult = {
    val cmd = s"$RUN_TEST_SH $dir"
    val cmdWithTimeout =
      if (timeLimit.isBlank) cmd else s"$cmd --time-limit=$timeLimit"
    cmdWithTimeout.! match {
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
      inputs: Seq[DirectTestInput] = Seq()
  ): TestResult = {
    os.remove.all(VHDL_TEST_DIR)
    os.makeDir.all(VHDL_TEST_DIR)
    VhdlGenerator.emitVhdl(e, VHDL_TEST_DIR)
    VhdlTestbenchGenerator.makeTestbench(inputs, e, VHDL_TEST_DIR)
    testExistingProject(VHDL_TEST_DIR)
  }
}
