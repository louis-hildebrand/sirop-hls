package mhir.gen.verilog

import mhir.gen.{
  DesignCompileFailed,
  SimulationFailed,
  SimulationTimeout,
  TestPassed,
  TestResult,
  TestbenchCompileFailed,
  UnknownFailure
}
import os.{Path, RelPath}

import scala.sys.process._

object VerilogTestRunner {
  private val VERILOG_DIR = os.pwd / "src" / "test" / "verilog"
  private val RUN_TEST_SH = os.pwd / "src" / "test" / "sh" / "test_verilog.sh"

  def testExistingProject(dir: String): TestResult = {
    testExistingProject(VERILOG_DIR / RelPath(dir))
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
}
