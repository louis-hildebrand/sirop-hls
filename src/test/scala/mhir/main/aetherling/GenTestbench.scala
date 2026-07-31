package mhir.main.aetherling

import com.typesafe.scalalogging.Logger
import mhir.gen.verilog.{VerilogTestRunner, VerilogTestbenchGenerator}
import mhir.gen.vhdl.test._
import mhir.logging.time
import org.slf4j.event.Level
import os.Path

object GenTestbench {

  private implicit val logger: Logger = Logger(getClass.getName)

  def main(args: Array[String]): Unit = {
    if (args.contains("--help")) {
      printUsage()
    }
    if (args.length != 1) {
      throw new IllegalArgumentException(
        s"Expected one argument but got ${args.length}."
      )
    }
    val dir = Path(args.head, base = os.pwd)
    if (dir.segments.contains("verilog")) {
      generateVerilogTestbench(dir)
    } else if (dir.segments.contains("vhdl")) {
      generateVhdlTestbench(dir)
    } else {
      throw new IllegalArgumentException(
        s"Can't tell whether directory $dir is a Verilog project or a VHDL project."
      )
    }
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

  def generateVerilogTestbench(dir: Path): Unit = {
    val benchName = dir.baseName
    val io = AetherlingBenchmarkIO.verilogIO(benchName)
    VerilogTestbenchGenerator.makeFileBasedTestbench(io, dir)
    VerilogTestRunner.copyTestBashScript(dir)
  }

  def generateVhdlTestbench(dir: Path): Unit = {
    val benchName = dir.baseName
    val io = time("finding inputs and expected outputs", Level.DEBUG) {
      AetherlingBenchmarkIO.vhdlIO(benchName)
    }
    time("generating VHDL testbench") {
      VhdlTestbenchGenerator.makeFileBasedTestbench(io = io, dir = dir)
    }
    time("copying test_vhdl.sh into VHDL project") {
      VhdlTestRunner.copyTestScripts(dir)
    }
  }
}
