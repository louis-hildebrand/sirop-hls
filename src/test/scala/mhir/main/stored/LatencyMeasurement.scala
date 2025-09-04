package mhir.main.stored

import com.typesafe.scalalogging.Logger
import mhir.gen.vhdl.VhdlTestbenchGenerator
import os.Path

import scala.sys.exit

sealed trait LatencyResult
case class OkLatencyResult(latency: Int) extends LatencyResult
case class ErrLatencyResult(latency: Option[Int], simExitCode: Int)
    extends LatencyResult

object LatencyMeasurement {
  private val LatencyRegex = "LATENCY:\\s+([0-9]+) cycles".r
  private val TimeLimit = "1h"

  private val logger = Logger(getClass.getName)

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
    val latency = measure(dir)
    latency match {
      case OkLatencyResult(latency) =>
        println(s"LATENCY: $latency cycles")
      case ErrLatencyResult(Some(latency), simExitCode) =>
        println(s"LATENCY: $latency cycles")
        exit(simExitCode)
      case ErrLatencyResult(None, simExitCode) =>
        exit(simExitCode)
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

  // TODO: Test this?
  def measure(dir: Path): LatencyResult = {
    val benchName = dir.baseName
    val io = ProgramIO(benchName)
    VhdlTestbenchGenerator.makeFileBasedTestbench(io = io, dir = dir)
    val proc = os
      .proc("./src/test/sh/test_vhdl.sh", dir, "-v", s"--time-limit=$TimeLimit")
      .call(
        cwd = os.pwd,
        check = false
      )
    if (proc.exitCode != 0) {
      logger.error(s"Simulation exited with nonzero code: ${proc.exitCode}")
    }
    val latency = parseLatency(proc.out.trim)
    (proc.exitCode, latency) match {
      case (0, Some(latency)) => OkLatencyResult(latency)
      case (code, latency)    => ErrLatencyResult(latency, code)
    }
  }

  private def parseLatency(stdout: String): Option[Int] = {
    val matches = LatencyRegex.findAllMatchIn(stdout).toSeq
    if (matches.isEmpty) {
      logger.error("No latency found in testbench output.")
      None
    } else if (matches.length >= 2) {
      logger.error(
        s"Expected to find only one latency in testbench output, but found ${matches.length}."
      )
      None
    } else {
      val latency = matches.head.group(1)
      Some(Integer.parseInt(latency))
    }
  }
}
