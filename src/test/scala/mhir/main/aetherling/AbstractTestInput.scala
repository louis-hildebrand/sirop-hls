package mhir.main.aetherling

import mhir.gen.{verilog, vhdl}
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

/** A sequence of test inputs in a format that can be converted into the formats
  * required by the VHDL or Verilog testbench generators.
  *
  * @param f
  *   a function that generates one element from the sequence. `f(t)(i)`
  *   computes the element at time step `t` for the stream at index `i`.
  * @param elemTypes
  *   the types of the elements within the input streams.
  * @param len
  *   the total length of the input sequence. This must be the same across all
  *   input streams.
  * @param hold
  *   the number of clock cycles for which to hold each input.
  */
case class AbstractTestInput(
    f: Int => Int => Expr,
    elemTypes: Seq[Type],
    len: Int,
    hold: Int
) {

  /** Converts to the format required by the VHDL testbench generator.
    */
  def toVhdl: Seq[vhdl.TestInput] = {
    (0 until nStreams).map({ i =>
      vhdl.DirectTestInput(
        (t: Int) => Some(f(t)(i)),
        elemTyp = elemTypes(i),
        len = len
      )
    })
  }

  /** Converts to the format required by the testbench generator.
    */
  def toVerilog: verilog.TestInput = {
    verilog.DirectTestInput(
      (t: Int) => (0 until nStreams).map(i => f(t)(i)),
      elemTypes = elemTypes,
      len = len,
      hold = hold
    )
  }

  /** Parallelizes each input stream so that `par` elements are passed in per
    * cycle.
    *
    * @example
    *   if you start with a stream of 200 values of type `u8` and call this
    *   method with `par = 4`, the input will now be 50 values of type
    *   `TyVec(U8, 4)`.
    *
    * @param par
    *   the number of elements to pass in per clock cycle.
    */
  def vec(par: Int): AbstractTestInput = {
    require(len % par == 0)
    AbstractTestInput(
      f = (t: Int) =>
        (i: Int) => {
          val start = t * par
          val end = (t + 1) * par
          VecLiteral((start until end).map(t => this.f(t)(i)): _*)().tchk()
        },
      elemTypes = this.elemTypes.map(TyVec(_, par)),
      len = this.len / par,
      hold = this.hold
    )
  }

  /** Constructs a new [[AbstractTestInput]] that is the same as this one but
    * with an updated [[hold]] value.
    */
  def withHold(newHold: Int): AbstractTestInput = {
    new AbstractTestInput(
      f = this.f,
      elemTypes = this.elemTypes,
      len = this.len,
      hold = newHold
    )
  }

  private def nStreams: Int = elemTypes.length
}

/** Companion object for [[AbstractTestInput]].
  */
object AbstractTestInput {

  /** Constructs an [[AbstractTestInput]] from a pre-computed sequence of
    * inputs.
    */
  def apply(elems: Seq[Seq[Expr]], hold: Int = 1): AbstractTestInput = {
    new AbstractTestInput(
      f = (t: Int) => (i: Int) => elems(t)(i),
      elemTypes = elems.headOption match {
        case Some(xs) => xs.map(_.typ)
        case None     => Seq()
      },
      len = elems.length,
      hold = hold
    )
  }
}
