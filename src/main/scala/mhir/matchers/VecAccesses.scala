package mhir.matchers

import mhir.ir._

import scala.annotation.tailrec

object VecAccesses {

  @tailrec
  def apply(v: Expr, indices: Seq[Expr]): Expr = {
    indices match {
      case Seq()           => v
      case Seq(i, is @ _*) => VecAccesses(VecAccess(v, i)(), is)
    }
  }

  def unapply(e: Expr): Option[(Expr, Seq[Expr])] = {
    e match {
      case VecAccess(VecAccesses(v, is), i) => Some((v, is :+ i))
      case v                                => Some((v, Seq.empty))
    }
  }
}
