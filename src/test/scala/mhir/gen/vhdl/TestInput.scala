package mhir.gen
package vhdl

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import os.Path

/** A sequence of inputs to provide to a VHDL design under test.
  */
sealed trait TestInput {

  /** The type of the elements in this input stream.
    */
  def elemTyp: Type

  /** The length of the input sequence, including invalid elements.
    */
  def len: Int
}

/** A sequence of inputs to provide directly in the testbench source code.
  *
  * @param elems
  *   the elements of the sequence. `None` means that no input should be
  *   provided in the given clock cycle (i.e., `valid = 0`). `Some(x)` means
  *   that data `x` should be sent as input (i.e., `valid = 1`).
  */
case class DirectTestInput(elems: Seq[Option[Expr]]) extends TestInput {
  def elemTyp: Type = elems.flatten.head.tchk().typ

  def len: Int = elems.length
}

/** A sequence of inputs to read from files.
  *
  * @param data
  *   the path to the file containing the values for the `data` signal.
  * @param valid
  *   the path to the file containing the values for the `valid` signal.
  */
case class TestInputFromFiles(data: Path, valid: Path, elemTyp: Type, len: Int)
    extends TestInput
