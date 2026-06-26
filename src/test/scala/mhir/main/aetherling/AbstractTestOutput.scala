package mhir.main.aetherling

import mhir.canonicalize._
import mhir.gen.{verilog, vhdl}
import mhir.ir._
import mhir.sugar.{AllOne, AllZero, ExprLowering}
import mhir.typecheck._

/** A sequence of expected outputs that can be converted to the format required
  * by the VHDL or Verilog testbench generators.
  *
  * @param f
  *   a function which returns the element at index `i` in the expected sequence
  *   of <i>valid</i> outputs.
  * @param elemTyp
  *   the type of the elements in the output stream.
  * @param len
  *   the number of valid elements in the output stream.
  * @param skip
  *   the number of elements to skip after each valid output.
  */
case class AbstractTestOutput(
    f: Int => Expr,
    elemTyp: Type,
    len: Int,
    skip: Int
) {

  /** Converts to the format required by the VHDL testbench generator.
    */
  def toVhdl: vhdl.test.TestOutput = {
    vhdl.test.DirectTestOutput(
      Stream.from(0).take(len).map(f),
      Stream.from(0).take(len).map(i => getMask(f(i))),
      elemTyp = elemTyp,
      len = len
    )
  }

  private def getMask(elem: Expr): Expr = {
    require(elem.hasType)
    elem match {
      case VecLiteral(elems @ _*) =>
        VecLiteral(elems.map(getMask): _*)().tchk()
      case Tuple(elems @ _*) =>
        Tuple(elems.map(getMask): _*)().tchk()
      case Undefined(typ) =>
        AllOne(typ).tchk().lower
      case v =>
        assert(!v.contains(classOf[Undefined]))
        AllZero(v.typ).tchk().lower
    }
  }

  /** Converts to the format required by the Verilog testbench generator.
    */
  def toVerilog: verilog.TestOutput = {
    verilog.DirectTestOutput(f, elemTyp = elemTyp, len = len, skip = skip)
  }

  /** Similar to [[AbstractTestInput.vec]].
    *
    * @param par
    *   the number of elements to expect per cycle.
    */
  def vec(par: Int): AbstractTestOutput = {
    require(len % par == 0)
    AbstractTestOutput(
      f = (t: Int) => {
        val start = t * par
        val end = (t + 1) * par
        VecLiteral((start until end).map(t => this.f(t)): _*)().tchk()
      },
      elemTyp = TyVec(this.elemTyp, par),
      len = this.len / par,
      skip = this.skip
    )
  }

  /** Constructs a new [[AbstractTestOutput]] that is the same as this one but
    * with an updated [[skip]] value.
    */
  def withSkip(newSkip: Int): AbstractTestOutput = {
    new AbstractTestOutput(
      f = this.f,
      elemTyp = this.elemTyp,
      len = this.len,
      skip = newSkip
    )
  }
}

/** Companion object for [[AbstractTestOutput]].
  */
object AbstractTestOutput {

  /** Constructs an [[AbstractTestOutput]] from a pre-computed sequence.
    *
    * @param elems
    *   the expected sequence of valid outputs.
    * @param skip
    *   the number of elements to skip after each valid output.
    */
  def apply(elems: Seq[Expr], skip: Int = 0): AbstractTestOutput = {
    new AbstractTestOutput(
      (t: Int) => elems(t),
      elemTyp = elems.head.typ,
      len = elems.length,
      skip = skip
    )
  }
}
