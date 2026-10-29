package mhir.gen.vhdl

import mhir.gen.vhdl.test.VhdlTestRunner
import mhir.gen.{
  DesignCompileFailed,
  NoTests,
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
class VhdlTestRunnerTests extends AnyFunSuite {

  private val EXAMPLES =
    os.pwd / "src" / "test" / "resources" / "mhir" / "gen" / "vhdl"
  private val TIME_LIMIT = "10s"

  test("no-tests-1") {
    val result = VhdlTestRunner.testExistingProject(
      EXAMPLES / "no-tests-1",
      timeLimit = TIME_LIMIT
    )
    assert(result == NoTests)
  }

  test("no-tests-2") {
    val result = VhdlTestRunner.testExistingProject(
      EXAMPLES / "no-tests-2",
      timeLimit = TIME_LIMIT
    )
    assert(result == NoTests)
  }

  test("or-gate") {
    val result = VhdlTestRunner.testExistingProject(
      EXAMPLES / "or-gate",
      timeLimit = TIME_LIMIT
    )
    assert(result == TestPassed)
  }

  test("stm-count-sum") {
    // The files are deliberately out of order (stm_1_scan.vhd depends on
    // stm_2_count.vhd but the former comes before the latter in an
    // alphabetical listing of the files).
    val result = VhdlTestRunner.testExistingProject(
      EXAMPLES / "stm-count-sum",
      timeLimit = TIME_LIMIT
    )
    assert(result == TestPassed)
  }

  test("design-compile-error") {
    val result = VhdlTestRunner.testExistingProject(
      EXAMPLES / "design-compile-error",
      timeLimit = TIME_LIMIT
    )
    assert(result == DesignCompileFailed)
  }

  test("testbench-compile-error") {
    val result = VhdlTestRunner.testExistingProject(
      EXAMPLES / "testbench-compile-error",
      timeLimit = TIME_LIMIT
    )
    assert(result == TestbenchCompileFailed)
  }

  test("wrong-output") {
    val result = VhdlTestRunner.testExistingProject(
      EXAMPLES / "wrong-output",
      timeLimit = TIME_LIMIT
    )
    assert(result == SimulationFailed)
  }

  test("infinite-loop") {
    val result = VhdlTestRunner.testExistingProject(
      EXAMPLES / "infinite-loop",
      timeLimit = "10s"
    )
    assert(result == SimulationTimeout)
  }
}
