package mhir.main.aetherling

import mhir.gen.verilog.VerilogTestbenchGenerator
import mhir.gen.vhdl.VhdlTestbenchGenerator
import os.Path

object AetherlingBenchmarkLatencyMeasurement {
  private val LatencyRegex = "LATENCY:\\s+([0-9]+) cycles".r

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
    val latency = if (dir.segments.contains("verilog")) {
      measureVerilog(dir)
    } else if (dir.segments.contains("vhdl")) {
      measureVhdl(dir)
    } else {
      throw new IllegalArgumentException(
        s"Can't tell whether directory $dir is a Verilog project or a VHDL project."
      )
    }
    println(latency)
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

  // TODO: Test this?
  def measureVerilog(dir: Path): Int = {
    val benchName = dir.baseName
    val (inputs, outputs) = AetherlingBenchmarkTests.ioByBenchmark(benchName)
    VerilogTestbenchGenerator.makeTestbench(inputs, outputs, dir)
    val proc =
      os.proc("./src/test/sh/test_verilog.sh", dir, "-v").call(cwd = os.pwd)
    parseLatency(proc.out.trim)
  }

  // TODO: Test this?
  def measureVhdl(dir: Path): Int = {
    val benchName = dir.baseName
    val (inputs, outputs) = AetherlingBenchmarkTests.ioByBenchmark(benchName)
    VhdlTestbenchGenerator.makeTestbench(
      inputs = inputs,
      out = outputs,
      dir = dir,
      testNotReady = false
    )
    val proc =
      os.proc("./src/test/sh/test_vhdl.sh", dir, "-v").call(cwd = os.pwd)
    parseLatency(proc.out.trim)
  }

  private def parseLatency(stdout: String): Int = {
    val matches = LatencyRegex.findAllMatchIn(stdout).toSeq
    if (matches.isEmpty) {
      throw new RuntimeException("No latency found in testbench output.")
    } else if (matches.length >= 2) {
      throw new RuntimeException(
        s"Expected to find only one latency in testbench output, but found ${matches.length}."
      )
    } else {
      val latency = matches.head.group(1)
      Integer.parseInt(latency)
    }
  }
}
