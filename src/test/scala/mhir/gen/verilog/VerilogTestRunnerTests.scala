package mhir.gen.verilog

import mhir.gen.{
  DesignCompileFailed,
  SimulationFailed,
  SimulationTimeout,
  TestPassed,
  TestbenchCompileFailed
}
import mhir.testing.HardwareTest
import org.scalatest.funsuite.AnyFunSuite

/** Smoke tests to ensure the test runner script itself is working as expected.
  */
@HardwareTest
class VerilogTestRunnerTests extends AnyFunSuite {

  private val EXAMPLES =
    os.pwd / "src" / "test" / "resources" / "mhir" / "gen" / "verilog"

  test("map") {
    val result = VerilogTestRunner.testExistingProject(EXAMPLES / "map")
    assert(result == TestPassed)
  }

  test("design-compile-error") {
    val result =
      VerilogTestRunner.testExistingProject(EXAMPLES / "design-compile-error")
    assert(result == DesignCompileFailed)
  }

  test("testbench-compile-error") {
    val result =
      VerilogTestRunner.testExistingProject(
        EXAMPLES / "testbench-compile-error"
      )
    assert(result == TestbenchCompileFailed)
  }

  test("wrong-output") {
    val result =
      VerilogTestRunner.testExistingProject(EXAMPLES / "wrong-output")
    assert(result == SimulationFailed)
  }

  test("infinite-loop") {
    val result =
      VerilogTestRunner.testExistingProject(EXAMPLES / "infinite-loop")
    assert(result == SimulationTimeout)
  }
}
