package mhir.gen.verilog

import mhir.ir._
import os.Path

/** A sequence of inputs to provide to a Verilog design under test.
  */
sealed trait TestInput {

  /** The length of the input streams.
    */
  def len: Int

  /** The number of streams.
    *
    * This determines the naming of the input registers in the testbench.
    */
  def numStreams: Int = elemTypes.length

  /** The types of the elements within the input streams.
    *
    * This determines the widths of the input registers in the testbench.
    */
  def elemTypes: Seq[Type]
}

/** A sequence of inputs to hard-code in the testbench.
  *
  * @param f
  *   a function that computes the values for each input at each time step. For
  *   example, if
  *   {{{Seq(0, 1, 2).map(f) == Seq(Seq(0, 1), Seq(2, 3), Seq(4, 5))}}} it means
  *   assign { I0, I1 } = { 0, 1 } at time step 0, assign { I0, I1 } = { 2, 3 }
  *   at time step 1, and assign { I0, I1 } = { 4, 5 } at time step 2.
  * @param hold
  *   the number of cycles for which to hold each input element.
  */
case class DirectTestInput(
    f: Int => Seq[Expr],
    elemTypes: Seq[Type],
    len: Int,
    hold: Int
) extends TestInput {
  def steps: Iterator[Seq[Expr]] = {
    Stream.from(0).take(len).map(f).iterator
  }
}

/** A sequence of inputs to read from a file.
  *
  * @param f
  *   the full path to the file containing the input data.
  * @param dir
  *   the path of the Verilog project directory.
  * @param hold
  *   the number of cycles for which to hold each input element.
  */
case class TestInputFromFile(
    f: Path,
    dir: Path,
    elemTypes: Seq[Type],
    len: Int,
    hold: Int
) extends TestInput
