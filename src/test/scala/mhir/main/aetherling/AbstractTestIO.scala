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
  */
case class AbstractTestIO(inputs: Seq[Seq[Expr]], expectedOutput: Seq[Expr])
    extends TestIO {
  def toVhdl: vhdl.TestIO = {
    val nStreams = inputs.head.length
    val in = (0 until nStreams).map({ i =>
      vhdl.DirectTestInput(inputs.map(xs => xs(i)).map(Some(_)))
    })
    val out = vhdl.DirectTestOutput(this.expectedOutput)
    vhdl.TestIO(inputs = in, expectedOutput = out)
  }

  def toVerilog: verilog.TestIO = {
    val in = verilog.DirectTestInput(this.inputs)
    val out = verilog.DirectTestOutput(this.expectedOutput)
    verilog.TestIO(inputs = in, expectedOutput = out)
  }
}

/** Separate inputs and expected outputs for the VHDL and Verilog cases.
  *
  * Use this class when the VHDL and Verilog require very different stimuli
  * (e.g., for designs with sub-1 throughput).
  */
case class ConcreteTestIO(vhdlIO: vhdl.TestIO, verilogIO: verilog.TestIO)
    extends TestIO {
  override def toVhdl: vhdl.TestIO = this.vhdlIO

  override def toVerilog: verilog.TestIO = this.verilogIO
}
