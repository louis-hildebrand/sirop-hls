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

/** Inputs and expected outputs in a format that basically works for both VHDL
  * and Verilog.
  *
  * @param inputs
  *   the sequence of inputs to provide.
  * @param expectedOutput
  *   the sequence of expected outputs.
  * @param hold
  *   the number of cycles for which to hold each input element.
  * @param skip
  *   the number of invalid output elements following each valid output element.
  */
case class AbstractTestIO(
    inputs: Seq[Seq[Expr]],
    expectedOutput: Seq[Expr],
    hold: Int = 1,
    skip: Int = 0
) extends TestIO {
  def toVhdl: vhdl.TestIO = {
    val nStreams = inputs.head.length
    val in = (0 until nStreams).map({ i =>
      vhdl.DirectTestInput(inputs.map(xs => xs(i)).map(Some(_)))
    })
    val out = vhdl.DirectTestOutput(this.expectedOutput)
    vhdl.TestIO(inputs = in, expectedOutput = out)
  }

  def toVerilog: verilog.TestIO = {
    val in = verilog.DirectTestInput(this.inputs, hold = hold)
    val out = verilog.DirectTestOutput(this.expectedOutput, skip = skip)
    verilog.TestIO(inputs = in, expectedOutput = out)
  }
}
