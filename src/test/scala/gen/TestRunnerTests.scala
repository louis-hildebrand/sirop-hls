package gen

import org.scalatest.funsuite.AnyFunSuite

/** Smoke tests to ensure the test runner script itself is working as expected.
  */
class TestRunnerTests extends AnyFunSuite {
  test("run_test.sh:or-gate") {
    assert(TestRunner.runTest("or-gate") == TestPassed)
  }

  test("run_test.sh:stm-count") {
    assert(TestRunner.runTest("stm-count") == TestPassed)
  }

  test("run_test.sh:design-compile-error") {
    assert(TestRunner.runTest("design-compile-error") == DesignCompileFailed)
  }

  test("run_test.sh:testbench-compile-error") {
    assert(
      TestRunner.runTest("testbench-compile-error") == TestbenchCompileFailed
    )
  }

  test("run_test.sh:wrong-output") {
    assert(TestRunner.runTest("wrong-output") == SimulationFailed)
  }

  test("run_test.sh:infinite-loop") {
    assert(TestRunner.runTest("infinite-loop") == SimulationTimeout)
  }
}
