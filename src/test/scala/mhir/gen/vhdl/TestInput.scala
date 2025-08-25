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
  * @param f
  *   a function which computes the element at the given index in the sequence.
  */
case class DirectTestInput(f: Int => Option[Expr], elemTyp: Type, len: Int)
    extends TestInput {
  def elements: Iterator[Option[Expr]] = {
    Stream.from(0).take(len).map(f).iterator
  }
}

object DirectTestInput {
  def apply(elems: Seq[Option[Expr]]): DirectTestInput = {
    DirectTestInput(
      (i: Int) => elems(i),
      elemTyp = elems.flatten.head.tchk().typ,
      len = elems.length
    )
  }
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
