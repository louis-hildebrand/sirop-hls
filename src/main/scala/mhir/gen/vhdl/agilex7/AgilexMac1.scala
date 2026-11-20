package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.gen.vhdl.ir.{EntityInstantiation, Intermediate, IpBlockInst, Target}
import mhir.ir._
import os.Path

import scala.collection.immutable.ListMap

case class AgilexMac1(x: Expr, y: Expr, chainin: Expr) extends IpBlockInst {

  assert(this.x.typ.isInstanceOf[TyAnyInt])
  assert(this.y.typ.isInstanceOf[TyAnyInt])
  assert(this.chainin.typ.isInstanceOf[TyAnyInt])

  override def freeVars: Set[Param] = {
    x.freeVars ++ y.freeVars ++ chainin.freeVars
  }

  override def substitute(subs: Map[Expr, Expr]): AgilexMac1 = {
    AgilexMac1(
      this.x.subPreserveType(subs),
      this.y.subPreserveType(subs),
      this.chainin.subPreserveType(subs)
    )
  }

  override def map(f: Expr => Expr): AgilexMac1 = {
    AgilexMac1(f(this.x), f(this.y), f(this.chainin))
  }

  override def exprChildren: Seq[Expr] = Seq(x, y, chainin)

  override def intermediateChildren: Seq[Intermediate] = Seq.empty

  override def usesVendorIpBlocks: Boolean = true

  def signedOrUnsigned: String = {
    if (this.x.typ.asInstanceOf[TyAnyInt].signed) "signed" else "unsigned"
  }

  override def resourcesToCopy(vhdlDir: Path): Map[Path, String] = {
    Map(
      vhdlDir / s"mac_fallback_${this.signedOrUnsigned}.vhd" -> s"mhir/gen/vhdl/agilex7/mac_fallback_${this.signedOrUnsigned}.vhd"
    )
  }

  override def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    (this.x.typ, this.y.typ, this.chainin.typ, target.typ) match {
      case (tax: TyAnyInt, tay: TyAnyInt, tb: TyAnyInt, tr: TyAnyInt) =>
        assert(
          Seq(tax, tay, tb, tr).map(_.signed).toSet.size == 1,
          "DSP inputs and outputs should all have the same signedness"
        )
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
        EntityInstantiation(
          entityName =
            options.fullyQualify(s"mac_fallback_${this.signedOrUnsigned}"),
          genericArgs = ListMap(
            "AX_WIDTH" -> tax.w.toString,
            "AY_WIDTH" -> tay.w.toString,
            "B_WIDTH" -> tb.w.toString,
            "RESULT_WIDTH" -> tr.w.toString
          ),
          portArgs = ListMap(
            "clk" -> options.clock,
            "ena" -> s"bool2sl($enable)",
            "ax" -> VhdlExprGenerator.toVhdl(this.x),
            "ay" -> VhdlExprGenerator.toVhdl(this.y),
            "b" -> VhdlExprGenerator.toVhdl(this.chainin),
            "result" -> target.name
          ),
          label = Param("DSP")().name
        )
      case _ => throw new AssertionError("wrong types in DSP")
    }
  }
}
