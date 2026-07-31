package mhir.gen
package vhdl
package test

import com.typesafe.scalalogging.Logger
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
import mhir.logging.time
import org.slf4j.event.Level
import os.Path

import scala.io.Source
import scala.sys.process._

/** Provides methods for testing a VHDL project.
  */
object VhdlTestRunner {
  private val VHDL_DIR = os.pwd / "src" / "test" / "vhdl"
  private[vhdl] val VHDL_TEST_DIR = VHDL_DIR / "auto_tests"

  private implicit val logger: Logger = Logger(getClass.getName)

  /** Test an existing VHDL project that has both a design and a testbench.
    *
    * @param dir
    *   the path to the VHDL project.
    * @return
    *   the test result.
    */
  def testExistingProject(dir: Path, timeLimit: String = ""): TestResult = {
    val shellScriptPath = copyTestScripts(dir)
    val cmd = s"$shellScriptPath $dir"
    val cmdWithTimeout =
      if (timeLimit.isBlank) cmd else s"$cmd --time-limit=$timeLimit"
    val result = cmdWithTimeout.!
    result match {
      case 0  => TestPassed
      case 4  => DesignCompileFailed
      case 5  => TestbenchCompileFailed
      case 6  => SimulationTimeout
      case 7  => SimulationFailed
      case 8  => NoTests
      case 9  => MissingVsim
      case 10 => MissingVcom
      case _  => UnknownFailure
    }
  }

  def testExpr(
      e: Expr,
      inputs: Seq[Seq[DirectTestInput]] = Seq(Seq()),
      options: VhdlGeneratorOptions = VhdlGeneratorOptions()
  ): TestResult = {
    require(inputs.nonEmpty)
    os.remove.all(VHDL_TEST_DIR)
    os.makeDir.all(VHDL_TEST_DIR)
    time("generating VHDL design", Level.DEBUG) {
      VhdlGenerator.emitVhdl(e, VHDL_TEST_DIR, options)
    }
    time("generating VHDL testbench", Level.DEBUG) {
      VhdlTestbenchGenerator.makeTestbench(
        inputs,
        e,
        VHDL_TEST_DIR,
        options = options
      )
    }
    time("running simulation", Level.DEBUG) {
      testExistingProject(VHDL_TEST_DIR)
    }
  }

  def testWithoutHandshake(
      e: Expr,
      io: TestSuiteIO,
      options: VhdlGeneratorOptions = VhdlGeneratorOptions()
  ): TestResult = {
    val updatedOptions = options.copy(handshake = false)
    os.remove.all(VHDL_TEST_DIR)
    os.makeDir.all(VHDL_TEST_DIR)
    time("generating VHDL design", Level.DEBUG) {
      VhdlGenerator.emitVhdl(e, VHDL_TEST_DIR, options = updatedOptions)
    }
    time("generating VHDL testbench", Level.DEBUG) {
      VhdlTestbenchGenerator.makeDirectTestbench(
        io,
        VHDL_TEST_DIR,
        testNotReady = false,
        options = updatedOptions
      )
    }
    time("running simulation", Level.DEBUG) {
      testExistingProject(VHDL_TEST_DIR)
    }
  }

  /** Copy the scripts for running VHDL simulation over to the given directory.
    *
    * @param dir
    *   the VHDL project directory.
    * @return
    *   the path to the Bash script (inside [[dir]]).
    */
  def copyTestScripts(dir: Path, compileIpBlocks: Boolean = false): Path = {
    val scriptsDir = dir / "scripts"
    os.makeDir.all(scriptsDir)
    val testShPath = scriptsDir / "test_vhdl.sh"
    os.write.over(
      testShPath,
      Source.fromResource("mhir/gen/vhdl/test_vhdl.sh").mkString,
      perms = "rwxrwxr-x"
    )
    if (compileIpBlocks) {
      os.write.over(
        scriptsDir / "compile_ip_blocks.sh",
        Source.fromResource("mhir/gen/vhdl/compile_ip_blocks.sh").mkString,
        perms = "rwxrwxr-x"
      )
    }
    os.write.over(
      dir / "Makefile",
      Source.fromResource("mhir/gen/vhdl/Makefile").mkString
    )
    testShPath
  }
}
