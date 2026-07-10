package mhir.gen.vhdl

import mhir.gen.vhdl.agilex7.{AgilexMac1, AgilexMac2}
import mhir.ir._

object IpStats {

  def apply(s: GenStmBuild): Map[String, Int] = {
    s.intermediates.foldLeft(Map[String, Int]())({
      case (stats, (target, ip: IpBlockInst)) =>
        val description = this.describe(target, ip)
        val n = stats.getOrElse(description, 0)
        stats + (description -> (n + 1))
      case (stats, _) => stats
    })
  }

  private def describe(target: Param, ip: IpBlockInst): String = {
    ip match {
      case mac: AgilexMac1 =>
        val signed = mac.x.typ.isInstanceOf[TySInt]
        val pipeline = mac.pipeline
        val chainInEnabled = mac.chainin != C(0)()
        val chainOutEnabled = target.typ.isInstanceOf[TyTuple]
        s"agilex7_mac1(signed=$signed, pipeline=$pipeline, chainin=$chainInEnabled, chainout=$chainOutEnabled)"
      case mac: AgilexMac2 =>
        val signed = mac.ax.typ.isInstanceOf[TySInt]
        val pipeline = mac.pipeline
        val chainInEnabled = mac.chainin != C(0)()
        val chainOutEnabled = target.typ.isInstanceOf[TyTuple]
        s"agilex7_mac2(signed=$signed, pipeline=$pipeline, chainin=$chainInEnabled, chainout=$chainOutEnabled)"
    }
  }
}
