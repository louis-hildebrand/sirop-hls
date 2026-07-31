package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.gen.vhdl.ir.{Intermediate, IpBlockInst}
import mhir.ir._

case class AgilexMac1(x: Expr, y: Expr, chainin: Expr) extends IpBlockInst {

  assert(this.x.typ.isInstanceOf[TyAnyInt])
  assert(this.y.typ.isInstanceOf[TyAnyInt])
  assert(this.chainin.typ.isInstanceOf[TyAnyInt])

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

  override def mapInputs(f: Expr => Expr): IpBlockInst = {
    AgilexMac1(f(this.x), f(this.y), f(this.chainin))
  }

  override def toVhdlEntityInst(
      target: Param,
      options: VhdlGeneratorOptions,
      enable: String
  ): VhdlEntityInstantiation = {
    // TODO: Actually instantiate an IP block?
    //       Two challenges:
    //         * How do I instantiate it in such a way that Quartus will be able to merge DSPs?
    //           I might need a special case, e.g., use the independent 18x18 mode when there's no chainin
    //         * The m18x18_plus36 mode has a really weird pipelining setup.
    //           There doesn't seem to be an input register for the 36-bit input!
    //           In other words, it seems to expect the 36-bit input to arrive one cycle later than the 18-bit factors.
    //       Maybe the best way to deal with these challenges would be to
    //       convert AgilexMac1 to other types of multiplications on a best-effort basis.
    //       For example, if there's no chainin, translate to the independent 18x18 mode
    //       (and possibly have another step which tries to merge those).
    //       Similarly, if I can find enough registers to satisfy the weird requirements for the 18x18_plus36 mode,
    //       I should translate to that mode.
    //       If neither of these special cases apply, stick to the behavioural fallback
    //       (and maybe emit a warning recommending that the programmer use an even number of multiplications).
    val resultTyp = target.typ match {
      case resTyp: TySInt => resTyp
      case resTyp: TyUInt => resTyp
      case typ =>
        throw new AssertionError(
          s"wrong type for target of ${this.getClass.getName}: $typ"
            + " (expected an integer)"
        )
    }
    val component =
      (this.x.typ, this.y.typ, this.chainin.typ, resultTyp) match {
        case (TySInt(axw), TySInt(ayw), TySInt(bw), TySInt(rw)) =>
          MacSignedFallbackComponent(
            axWidth = axw,
            ayWidth = ayw,
            bWidth = bw,
            resultWidth = rw
          )
        case (TyUInt(axw), TyUInt(ayw), TyUInt(bw), TyUInt(rw)) =>
          MacUnsignedFallbackComponent(
            axWidth = axw,
            ayWidth = ayw,
            bWidth = bw,
            resultWidth = rw
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
          "ax" -> VhdlExprGenerator.toVhdl(this.x),
          "ay" -> VhdlExprGenerator.toVhdl(this.y),
          "b" -> VhdlExprGenerator.toVhdl(this.chainin),
          "result" -> VhdlExprGenerator.toVhdl(target)
        )
      )
    )
  }
}
