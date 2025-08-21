package mhir.gen.verilog

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
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

  /** Type check the data, if applicable.
    */
  def tchk(): TestInput
}

/** A sequence of inputs to hard-code in the testbench.
  *
  * @param steps
  *   values for each input at each time step. For example,
  *   {{{Seq(Seq(0, 1), Seq(2, 3), Seq(4, 5))}}} means assign { I0, I1 } = { 0,
  *   1 } at time step 0, assign { I0, I1 } = { 2, 3 } at time step 1, and
  *   assign { I0, I1 } = { 4, 5 } at time step 2.
  */
case class DirectTestInput(steps: Seq[Seq[Expr]]) extends TestInput {
  override def elemTypes: Seq[Type] = steps.head.map(_.tchk().typ)

  override def len: Int = steps.length

  override def tchk(): DirectTestInput = DirectTestInput(
    steps.map(elems => elems.map(_.tchk()))
  )
}

/** A sequence of inputs to read from a file.
  *
  * @param f
  *   the path to the file containing the input data.
  */
case class TestInputFromFile(f: Path, elemTypes: Seq[Type], len: Int)
    extends TestInput {
  override def tchk(): TestInputFromFile = this
}
