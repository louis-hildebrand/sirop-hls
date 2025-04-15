package gen

import java.nio.file.Paths
import sys.process._

sealed trait TestResult
case object TestPassed extends TestResult
case object DesignCompileFailed extends TestResult
case object TestbenchCompileFailed extends TestResult
case object SimulationFailed extends TestResult
case object SimulationTimeout extends TestResult
case object UnknownFailure extends TestResult

object TestRunner {
  private val VHDL_DIR =
    Paths.get(System.getProperty("user.dir")).resolve("vhdl")
  private val RUN_TEST_SH = VHDL_DIR.resolve("run_test.sh")

  def runTest(dir: String): TestResult = {
    val fullDir = VHDL_DIR.resolve(dir)
    val cmd = s"$RUN_TEST_SH $fullDir"
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
