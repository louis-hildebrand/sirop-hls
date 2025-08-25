package mhir.main.aetherling

import mhir.gen.{verilog, vhdl}
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

case class AbstractTestInput(
    f: Int => Int => Expr,
    elemTypes: Seq[Type],
    len: Int,
    hold: Int
) {
  def nStreams: Int = elemTypes.length

  def toVhdl: Seq[vhdl.TestInput] = {
    (0 until nStreams).map({ i =>
      vhdl.DirectTestInput(
        (t: Int) => Some(f(t)(i)),
        elemTyp = elemTypes(i),
        len = len
      )
    })
  }

  def toVerilog: verilog.TestInput = {
    verilog.DirectTestInput(
      (t: Int) => (0 until nStreams).map(i => f(t)(i)),
      elemTypes = elemTypes,
      len = len,
      hold = hold
    )
  }

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
}

object AbstractTestInput {
  def apply(elems: Seq[Seq[Expr]], hold: Int = 1): AbstractTestInput = {
    new AbstractTestInput(
      f = (t: Int) => (i: Int) => elems(t)(i),
      elemTypes = elems.head.map(_.typ),
      len = elems.length,
      hold = hold
    )
  }
}
