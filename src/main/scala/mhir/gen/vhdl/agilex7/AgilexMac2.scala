package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._

case class AgilexMac2(
    ax: Expr,
    ay: Expr,
    bx: Expr,
    by: Expr,
    chainin: Expr,
    pipeline: Int
) extends IpBlockInst {

  override def freeVars: Set[Param] = {
    ax.freeVars ++ ay.freeVars ++ bx.freeVars ++ by.freeVars ++ chainin.freeVars
  }

  override def substitute(subs: Map[Expr, Expr]): Intermediate = {
    AgilexMac2(
      this.ax.subPreserveType(subs),
      this.ay.subPreserveType(subs),
      this.bx.subPreserveType(subs),
      this.by.subPreserveType(subs),
      this.chainin.subPreserveType(subs),
      this.pipeline
    )
  }

  override def toVhdl(
      target: Param,
      options: VhdlGeneratorOptions,
      enable: String
  ): VhdlEntityInstantiation = {
    val (resultTyp, enableChainOut) = target.typ match {
      case resTyp: TySInt                      => (resTyp, false)
      case resTyp: TyUInt                      => (resTyp, false)
      case TyTuple(resTyp: TySInt, TySInt(44)) => (resTyp, true)
      case TyTuple(resTyp: TyUInt, TyUInt(44)) => (resTyp, true)
      case TyTuple(resTyp: TyAnyInt, typ) =>
        val signedOrUnsigned = resTyp match {
          case _: TySInt => "signed"
          case _: TyUInt => "unsigned"
        }
        throw new IllegalArgumentException(
          s"wrong type for chainout: $typ"
            + s" (expected a 44-bit $signedOrUnsigned int type)"
        )
      case typ =>
        throw new AssertionError(
          s"wrong type for target of ${this.getClass.getName}: $typ"
            + " (expected a tuple with result and chainout types)"
        )
    }
    val component =
      (this.ax.typ, this.ay.typ, this.bx.typ, this.by.typ, resultTyp) match {
        case (TySInt(axw), TySInt(ayw), TySInt(bxw), TySInt(byw), TySInt(rw)) =>
          SystolicSignedDspComponent(
            axWidth = axw,
            ayWidth = ayw,
            bxWidth = bxw,
            byWidth = byw,
            resultWidth = rw,
            pipeline = this.pipeline,
            enableChainIn = this.chainin match {
              case IntCst(0) => false
              case _         => true
            }
          )
        case (TyUInt(axw), TyUInt(ayw), TyUInt(bxw), TyUInt(byw), TyUInt(cw)) =>
          ???
        case _ =>
          throw new AssertionError(
            s"the inputs and outputs for ${this.getClass.getName} should all have the same signedness"
          )
      }
    val (resultVhdl, chainOutVhdl) = if (enableChainOut) {
      val r = VhdlExprGenerator.toVhdl(target.__0.tchk())
      val c = VhdlExprGenerator.toVhdl(target.__1.tchk())
      (r, c)
    } else {
      (VhdlExprGenerator.toVhdl(target), "open")
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
          "chainin" -> VhdlExprGenerator.toVhdl(this.chainin),
          "chainout" -> chainOutVhdl,
          "result" -> resultVhdl
        )
      )
    )
  }
}
