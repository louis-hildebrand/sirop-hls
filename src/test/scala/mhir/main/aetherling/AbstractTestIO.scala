package mhir.main.aetherling

import mhir.ir._
import mhir.gen.{vhdl, verilog}

/** The inputs and expected outputs to provide to a design under test.
  */
sealed trait TestIO {

  /** The inputs and expected outputs in the format required for testing a VHDL
    * design.
    */
  def toVhdl: vhdl.TestIO

  /** The inputs and expected outputs in the format required for testing a
    * Verilog design.
    */
  def toVerilog: verilog.TestIO
}

/** Inputs and expected outputs in a format that can be converted to either of
  * the formats for VHDL and Verilog testbenches.
  */
case class AbstractTestIO(in: AbstractTestInput, out: AbstractTestOutput)
    extends TestIO {

  def toVhdl: vhdl.TestIO = {
    vhdl.TestIO(inputs = in.toVhdl, expectedOutput = out.toVhdl)
  }

  def toVerilog: verilog.TestIO = {
    verilog.TestIO(inputs = in.toVerilog, expectedOutput = out.toVerilog)
  }
}

object AbstractTestIO {
  def apply(
      in: Seq[Seq[Expr]],
      out: Seq[Expr],
      hold: Int = 1,
      skip: Int = 0
  ): AbstractTestIO = {
    new AbstractTestIO(
      AbstractTestInput(in, hold = hold),
      AbstractTestOutput(out, skip = skip)
    )
  }
}
