package mhir.gen
package vhdl

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import os.Path

/** A sequence of outputs that a VHDL design under test is expected to produce.
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
  */
case class DirectTestOutput(f: Int => Expr, elemTyp: Type, len: Int)
    extends TestOutput {
  def elements: Iterator[Expr] = {
    Stream.from(0).take(len).map(f).iterator
  }
}

object DirectTestOutput {
  def apply(elems: Seq[Expr]): DirectTestOutput = {
    DirectTestOutput(
      i => elems(i),
      elemTyp = elems.head.tchk().typ,
      len = elems.length
    )
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
  */
case class TestOutputFromFile(data: Path, mask: Path, elemTyp: Type, len: Int)
    extends TestOutput
