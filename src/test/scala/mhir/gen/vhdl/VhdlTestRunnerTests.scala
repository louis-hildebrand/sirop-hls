package mhir.gen.vhdl

import mhir.gen.vhdl.test.VhdlTestRunner
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
class VhdlTestRunnerTests extends AnyFunSuite {
  test("or-gate") {
    val result = VhdlTestRunner.testExistingProject("examples/or-gate")
    assert(result == TestPassed)
  }

  test("stm-count-sum") {
    // The files are deliberately out of order (stm_1_scan.vhd depends on
    // stm_2_count.vhd but the former comes before the latter in an
    // alphabetical listing of the files).
    val result = VhdlTestRunner.testExistingProject("examples/stm-count-sum")
    assert(result == TestPassed)
  }

  test("design-compile-error") {
    val result =
      VhdlTestRunner.testExistingProject("examples/design-compile-error")
    assert(result == DesignCompileFailed)
  }

  test("testbench-compile-error") {
    val result =
      VhdlTestRunner.testExistingProject("examples/testbench-compile-error")
    assert(result == TestbenchCompileFailed)
  }

  test("wrong-output") {
    val result = VhdlTestRunner.testExistingProject("examples/wrong-output")
    assert(result == SimulationFailed)
  }

  test("infinite-loop") {
    val result = VhdlTestRunner.testExistingProject("examples/infinite-loop")
    assert(result == SimulationTimeout)
  }
}
