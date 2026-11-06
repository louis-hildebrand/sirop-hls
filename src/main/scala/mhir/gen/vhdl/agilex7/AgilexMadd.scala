package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.gen.vhdl.ir.{Intermediate, IpBlockInst}
import mhir.ir._
import mhir.typecheck._

case class AgilexMadd(
    ax: Expr,
    ay: Expr,
    bx: Expr,
    by: Expr,
    pipeline: Int
) extends IpBlockInst {

  assert(this.ax.typ.isInstanceOf[TyAnyInt])
  assert(this.ay.typ.isInstanceOf[TyAnyInt])
  assert(this.bx.typ.isInstanceOf[TyAnyInt])
  assert(this.by.typ.isInstanceOf[TyAnyInt])
  assert(this.pipeline >= 0 && this.pipeline <= 3)

  override def freeVars: Set[Param] = {
    ax.freeVars ++ ay.freeVars ++ bx.freeVars ++ by.freeVars
  }

  override def substitute(subs: Map[Expr, Expr]): Intermediate = {
    AgilexMadd(
      this.ax.subPreserveType(subs),
      this.ay.subPreserveType(subs),
      this.bx.subPreserveType(subs),
      this.by.subPreserveType(subs),
      this.pipeline
    )
  }

  def mapInputs(f: Expr => Expr): IpBlockInst = {
    AgilexMadd(
      f(this.ax),
      f(this.ay),
      f(this.bx),
      f(this.by),
      this.pipeline
    )
  }

  override def toVhdlEntityInst(
      target: Param,
      options: VhdlGeneratorOptions,
      enable: String
  ): VhdlEntityInstantiation = {
    val component =
      (this.ax.typ, this.ay.typ, this.bx.typ, this.by.typ, target.typ) match {
        case (TySInt(axw), TySInt(ayw), TySInt(bxw), TySInt(byw), TySInt(rw)) =>
          Sumof2SignedDspComponent(
            axWidth = axw,
            ayWidth = ayw,
            bxWidth = bxw,
            byWidth = byw,
            resultWidth = rw,
            pipeline = this.pipeline
          )
        case (TyUInt(axw), TyUInt(ayw), TyUInt(bxw), TyUInt(byw), TyUInt(rw)) =>
          Sumof2UnsignedDspComponent(
            axWidth = axw,
            ayWidth = ayw,
            bxWidth = bxw,
            byWidth = byw,
            resultWidth = rw,
            pipeline = this.pipeline
          )
        case _ =>
          throw new AssertionError(
            s"the inputs and outputs for ${this.getClass.getName} should all have the same signedness"
          )
      }
    VhdlEntityInstantiation(
      name = Param(s"${target.prefix.toUpperCase}_DSP")().name,
      c = component,
      args = PortMap(
        Map(
          "clk" -> options.clock,
          "ena" -> enable,
          "ax" -> VhdlExprGenerator.toVhdl(this.ax),
          "ay" -> VhdlExprGenerator.toVhdl(this.ay),
          "bx" -> VhdlExprGenerator.toVhdl(this.bx),
          "by" -> VhdlExprGenerator.toVhdl(this.by),
          "result" -> VhdlExprGenerator.toVhdl(target)
        )
      )
    )
  }
}
