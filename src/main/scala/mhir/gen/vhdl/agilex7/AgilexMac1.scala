package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.ir._

case class AgilexMac1(x: Expr, y: Expr, chainin: Expr) extends IpBlockInst {

  assert(this.x.typ.isInstanceOf[TyAnyInt])
  assert(this.y.typ.isInstanceOf[TyAnyInt])
  assert(this.chainin.typ.isInstanceOf[TyAnyInt])

  // TODO: Implement absorbing registers into DSP
  def pipeline: Int = 0

  override def freeVars: Set[Param] = {
    x.freeVars ++ y.freeVars ++ chainin.freeVars
  }

  override def substitute(subs: Map[Expr, Expr]): Intermediate = {
    AgilexMac1(
      this.x.subPreserveType(subs),
      this.y.subPreserveType(subs),
      this.chainin.subPreserveType(subs)
    )
  }

  override def toVhdlEntityInst(
      target: Param,
      options: VhdlGeneratorOptions,
      enable: String
  ): VhdlEntityInstantiation = {
    ???
  }
}
