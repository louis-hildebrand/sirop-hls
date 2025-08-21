package mhir.gen.verilog

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import os.Path

/** A sequence of outputs that a Verilog design under test is expected to
  * produce.
  */
sealed trait TestOutput {

  /** The type of the elements within the output stream.
    */
  def elemTyp: Type

  /** The length of this stream.
    */
  def len: Int

  /** Type check the elements within this test output, if any.
    */
  def tchk(): TestOutput
}

/** A sequence of expected outputs to hard-code into the testbench source code.
  *
  * @param elems
  *   the expected sequence of <i>valid</i> outputs.
  */
case class DirectTestOutput(elems: Seq[Expr]) extends TestOutput {
  override def elemTyp: Type = this.elems.head.tchk().typ

  override def len: Int = this.elems.length

  override def tchk(): DirectTestOutput = DirectTestOutput(elems.map(_.tchk()))
}

/** A sequence of expected outputs to read from files.
  *
  * @param data
  *   the path to the file containing the expected data.
  * @param mask
  *   the path to the file containing output masks. This makes it possible to
  *   ignore certain parts of the output (e.g., because they are undefined).
  *   Note that invalid elements (i.e., ones where there is <i>no</i> output
  *   because the `valid` signal is lowered) are always ignored.
  * @param elemTyp
  *   the type of the elements within the stream.
  * @param len
  *   the length of the stream.
  */
case class TestOutputFromFile(data: Path, mask: Path, elemTyp: Type, len: Int)
    extends TestOutput {
  override def tchk(): TestOutputFromFile = this
}
