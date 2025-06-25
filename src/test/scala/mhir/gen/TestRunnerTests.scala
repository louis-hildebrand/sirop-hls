package mhir.gen

import mhir.testing.VhdlTest
import org.scalatest.funsuite.AnyFunSuite

/** Smoke tests to ensure the test runner script itself is working as expected.
  */
@VhdlTest
class TestRunnerTests extends AnyFunSuite {
  test("run_test.sh:or-gate") {
    assert(TestRunner.testExistingProject("or-gate") == TestPassed)
  }

  test("run_test.sh:stm-count-sum") {
    // The files are deliberately out of order (stm_1_scan.vhd depends on
    // stm_2_count.vhd but the former comes before the latter in an
    // alphabetical listing of the files).
    assert(TestRunner.testExistingProject("stm-count-sum") == TestPassed)
  }

  test("run_test.sh:design-compile-error") {
    assert(
      TestRunner.testExistingProject(
        "design-compile-error"
      ) == DesignCompileFailed
    )
  }

  test("run_test.sh:testbench-compile-error") {
    assert(
      TestRunner.testExistingProject(
        "testbench-compile-error"
      ) == TestbenchCompileFailed
    )
  }

  test("run_test.sh:wrong-output") {
    assert(TestRunner.testExistingProject("wrong-output") == SimulationFailed)
  }

  test("run_test.sh:infinite-loop") {
    assert(TestRunner.testExistingProject("infinite-loop") == SimulationTimeout)
  }
}
