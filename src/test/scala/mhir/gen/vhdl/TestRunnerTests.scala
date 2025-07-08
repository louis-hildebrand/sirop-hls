package mhir.gen.vhdl

import mhir.testing.VhdlTest
import org.scalatest.funsuite.AnyFunSuite

/** Smoke tests to ensure the test runner script itself is working as expected.
  */
@VhdlTest
class TestRunnerTests extends AnyFunSuite {
  test("test_vhdl.sh:or-gate") {
    assert(TestRunner.testExistingProject("examples/or-gate") == TestPassed)
  }

  test("test_vhdl.sh:stm-count-sum") {
    // The files are deliberately out of order (stm_1_scan.vhd depends on
    // stm_2_count.vhd but the former comes before the latter in an
    // alphabetical listing of the files).
    assert(
      TestRunner.testExistingProject("examples/stm-count-sum") == TestPassed
    )
  }

  test("test_vhdl.sh:design-compile-error") {
    assert(
      TestRunner.testExistingProject(
        "examples/design-compile-error"
      ) == DesignCompileFailed
    )
  }

  test("test_vhdl.sh:testbench-compile-error") {
    assert(
      TestRunner.testExistingProject(
        "examples/testbench-compile-error"
      ) == TestbenchCompileFailed
    )
  }

  test("test_vhdl.sh:wrong-output") {
    assert(
      TestRunner.testExistingProject(
        "examples/wrong-output"
      ) == SimulationFailed
    )
  }

  test("test_vhdl.sh:infinite-loop") {
    assert(
      TestRunner.testExistingProject(
        "examples/infinite-loop"
      ) == SimulationTimeout
    )
  }
}
