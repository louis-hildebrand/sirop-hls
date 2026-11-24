package mhir.gen.vhdl

import mhir.gen.vhdl.agilex7.{AgilexMac2, AgilexMadd}
import mhir.gen.vhdl.ir._
import mhir.ir._

object IpStats {

  def apply(s: GenStmBuild): Map[String, Int] = {
    s.intermediates.foldLeft(Map[String, Int]())({ case (stats, (target, i)) =>
      describe(target.typ, i) match {
        case Some(description) =>
          val n = stats.getOrElse(description, 0)
          stats + (description -> (n + 1))
        case None =>
          stats
      }
    })
  }

  private def describe(targetTyp: Type, i: Intermediate): Option[String] = {
    i match {
      case mac: AgilexMac2 =>
        val signed = mac.ax.typ.isInstanceOf[TySInt]
        val pipeline = mac.pipeline
        val chainInEnabled = mac.chainin != C(0)()
        val chainOutEnabled = targetTyp.isInstanceOf[TyTuple]
        Some(
          s"agilex7_mac2(signed=$signed, pipeline=$pipeline, chainin=$chainInEnabled, chainout=$chainOutEnabled)"
        )
      case madd: AgilexMadd =>
        val signed = madd.ax.typ.isInstanceOf[TySInt]
        val pipeline = madd.pipeline
        Some(s"agilex7_madd(signed=$signed, pipeline=$pipeline)")
      case ForGenerateIntermediate(_, bounds, body) =>
        val elemTyp = bounds.foldLeft(targetTyp)({ case (t, _) =>
          val TyVec(elemTyp, _) = t
          elemTyp
        })
        describe(elemTyp, body).map(bodyDescription =>
          bounds.foldRight(bodyDescription)({ case (bound, acc) =>
            s"for($bound) $acc"
          })
        )
      case BlockIntermediate(locals, body) =>
        assert(
          !locals
            .flatMap({ case (_, i) => i.intermediateDescendants :+ i })
            .exists(_.isInstanceOf[IpBlockInst]),
          "TODO: handle some local variables in BlockIntermediate being IP blocks"
        )
        describe(targetTyp, body)
      case _ => None
    }
  }
}
