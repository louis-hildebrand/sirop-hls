package mhir.gen.verilog

import mhir.ir._
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
}

/** A sequence of expected outputs to hard-code into the testbench source code.
  *
  * @param f
  *   a function that computes the expected value of the `i`th <i>valid</i>
  *   output.
  * @param skip
  *   the number of invalid output elements following each valid output element.
  */
case class DirectTestOutput(f: Int => Expr, elemTyp: Type, len: Int, skip: Int)
    extends TestOutput {
  def elements: Iterator[Expr] = {
    Stream.from(0).take(len).map(f).iterator
  }
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
  * @param skip
  *   the number of invalid output elements following each valid output element.
  */
case class TestOutputFromFile(
    data: Path,
    mask: Path,
    elemTyp: Type,
    len: Int,
    skip: Int
) extends TestOutput
