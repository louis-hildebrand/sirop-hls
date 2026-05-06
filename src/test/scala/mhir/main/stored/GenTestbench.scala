package mhir.main.stored

import com.typesafe.scalalogging.Logger
import mhir.gen.vhdl.test.{
  DirectTestInput,
  DirectTestOutput,
  VhdlTestRunner,
  VhdlTestbenchGenerator
}
import mhir.logging.time
import org.slf4j.event.Level
import os.Path

object GenTestbench {

  private implicit val logger: Logger = Logger(getClass.getName)

  def main(args: Array[String]): Unit = {
    if (args.contains("--help")) {
      printUsage()
      return
    }
    if (args.length != 1) {
      throw new IllegalArgumentException(
        s"Expected one argument but got ${args.length}."
      )
    }
    val dir = Path(args.head, base = os.pwd)
    generateTestbench(dir)
  }

  private def printUsage(): Unit = {
    val cls: String = {
      val fullName = Compiler.getClass.getCanonicalName
      if (fullName.endsWith("$")) {
        fullName.dropRight(1)
      } else {
        fullName
      }
    }
    println(s"Usage: Test / runMain $cls DIR")
  }

  def generateTestbench(dir: Path): Path = {
    val io = time("getting inputs and expected outputs", Level.DEBUG) {
      ProgramIO(dir.baseName)
    }
    time("generating testbench", Level.DEBUG) {
      // I manually convert these testbenches to work for SHIR, so I'd like the
      // testbench file to be self-contained
      VhdlTestbenchGenerator.makeDirectTestbenchFromPositionalInputs(
        inputs = io.inputs.map(_.asInstanceOf[DirectTestInput]),
        expectedOutput = io.expectedOutput.asInstanceOf[DirectTestOutput],
        dir = dir,
        testNotReady = false
      )
    }
    time("copying test_vhdl.sh", Level.DEBUG) {
      VhdlTestRunner.copyTestBashScript(dir)
    }
  }
}
