package mhir.gen.vhdl

import mhir.gen.vhdl.agilex7.{AgilexMac1, AgilexMac2, AgilexMadd}
import mhir.gen.vhdl.ir._
import mhir.ir._

object IpStats {

  def apply(s: GenStmBuild): Map[String, Int] = {
    s.intermediates.foldLeft(Map[String, Int]())({
      case (
            stats,
            (
              target,
              ForGenerateIntermediate(vars, bounds, _, ip: IpBlockInst)
            )
          ) =>
        val description = this.describe(target, vars, bounds, ip)
        val n = stats.getOrElse(description, 0)
        stats + (description -> (n + 1))
      case (stats, _) => stats
    })
  }

  private def describe(
      target: Param,
      vars: Seq[Param],
      bounds: Seq[Int],
      ip: IpBlockInst
  ): String = {
    (vars, bounds) match {
      case (Seq(), Seq()) =>
        ip match {
          case mac: AgilexMac1 =>
            val signed = mac.x.typ.isInstanceOf[TySInt]
            val chainInEnabled = mac.chainin != C(0)()
            s"agilex7_mac1(signed=$signed, chainin=$chainInEnabled)"
          case mac: AgilexMac2 =>
            val signed = mac.ax.typ.isInstanceOf[TySInt]
            val pipeline = mac.pipeline
            val chainInEnabled = mac.chainin != C(0)()
            val chainOutEnabled = target.typ.isInstanceOf[TyTuple]
            s"agilex7_mac2(signed=$signed, pipeline=$pipeline, chainin=$chainInEnabled, chainout=$chainOutEnabled)"
          case madd: AgilexMadd =>
            val signed = madd.ax.typ.isInstanceOf[TySInt]
            val pipeline = madd.pipeline
            s"agilex7_madd(signed=$signed, pipeline=$pipeline)"
        }
      case (Seq(x, otherVars @ _*), Seq(bound, otherBounds @ _*)) =>
        val newTarget = target.typ match {
          case TyVec(elemTyp, _) => Param(target.prefix, target.id)(elemTyp)
          case _                 => ???
        }
        val body = describe(newTarget, otherVars, otherBounds, ip)
        s"for($bound) $body"
      case _ =>
        ???
    }
  }
}
