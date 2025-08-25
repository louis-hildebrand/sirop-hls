package mhir.main.aetherling

import mhir.ir._
import mhir.gen.{verilog, vhdl}
import mhir.ir.typecheck.TypeCheck

case class AbstractTestOutput(
    f: Int => Expr,
    elemTyp: Type,
    len: Int,
    skip: Int
) {
  def toVhdl: vhdl.TestOutput = {
    vhdl.DirectTestOutput(f, elemTyp = elemTyp, len = len)
  }

  def toVerilog: verilog.TestOutput = {
    verilog.DirectTestOutput(f, elemTyp = elemTyp, len = len, skip = skip)
  }

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
}

object AbstractTestOutput {
  def apply(elems: Seq[Expr], skip: Int = 0): AbstractTestOutput = {
    new AbstractTestOutput(
      (t: Int) => elems(t),
      elemTyp = elems.head.typ,
      len = elems.length,
      skip = skip
    )
  }
}
