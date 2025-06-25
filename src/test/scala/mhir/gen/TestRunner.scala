package mhir.gen

import mhir.ir._

import java.nio.file.{Files, Path, Paths}
import scala.reflect.io.Directory
import scala.sys.process._

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
  private[gen] val VHDL_TEST_DIR = VHDL_DIR.resolve("auto_tests")

  def testExistingProject(dir: String): TestResult = {
    val fullDir = VHDL_DIR.resolve(dir)
    testExistingProject(fullDir)
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

  def testExpr(
      e: Expr,
      inputs: Seq[TestInput] = Seq()
  ): TestResult = {
    new Directory(VHDL_TEST_DIR.toFile).deleteRecursively()
    Files.createDirectory(VHDL_TEST_DIR)
    val dataTyp = e match {
      case s: StmBuild => VhdlGenerator.emitVhdl(s, VHDL_TEST_DIR)
      case f: Function => VhdlGenerator.emitVhdl(f, VHDL_TEST_DIR)
      case _ =>
        throw new IllegalArgumentException(
          s"Only streams and functions are supported (got expression $e)."
        )
    }
    TestbenchGenerator.makeTestbench(inputs, e, dataTyp, VHDL_TEST_DIR)
    testExistingProject(VHDL_TEST_DIR)
  }
}
