package mhir.gen
package vhdl
package test

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar.{AllOne, AllZero, ExprLowering}
import mhir.typecheck.TypeCheck
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
case class DirectTestOutput(
    data: Seq[Expr],
    ignore: Seq[Expr],
    elemTyp: Type,
    len: Int
) extends TestOutput {
  def elements: Iterator[(Expr, Expr)] = {
    this.data.zip(this.ignore).iterator
  }
}

object DirectTestOutput {

  def apply(data: Seq[Expr], ignore: Seq[Expr]): DirectTestOutput = {
    new DirectTestOutput(
      data,
      ignore,
      elemTyp = data.head.typ,
      len = data.length
    )
  }
}

/** A sequence of expected outputs to read from files.
  *
  * @param data
  *   the full path of the file containing the expected data.
  * @param mask
  *   the full path of the file containing output masks. This makes it possible
  *   to ignore certain parts of the output (e.g., because they are undefined).
  *   Note that invalid elements (i.e., ones where there is <i>no</i> output
  *   because the `valid` signal is lowered) are always ignored.
  * @param dir
  *   the path of the VHDL project directory.
  * @param elemTyp
  *   the type of the elements within the stream.
  * @param len
  *   the length of the stream.
  */
case class TestOutputFromFile(
    data: Path,
    mask: Path,
    dir: Path,
    elemTyp: Type,
    len: Int
) extends TestOutput
