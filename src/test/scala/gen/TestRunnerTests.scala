package gen

import org.scalatest.funsuite.AnyFunSuite

/** Smoke tests to ensure the test runner script itself is working as expected.
  */
class TestRunnerTests extends AnyFunSuite {
  test("run_test.sh:or-gate") {
    assert(TestRunner.testExistingProject("or-gate") == TestPassed)
  }

  test("run_test.sh:stm-count") {
    assert(TestRunner.testExistingProject("stm-count") == TestPassed)
  }

  test("run_test.sh:design-compile-error") {
    assert(TestRunner.testExistingProject("design-compile-error") == DesignCompileFailed)
  }

  test("run_test.sh:testbench-compile-error") {
    assert(
      TestRunner.testExistingProject("testbench-compile-error") == TestbenchCompileFailed
    )
  }

  test("run_test.sh:wrong-output") {
    assert(TestRunner.testExistingProject("wrong-output") == SimulationFailed)
  }

  test("run_test.sh:infinite-loop") {
    assert(TestRunner.testExistingProject("infinite-loop") == SimulationTimeout)
  }
}
