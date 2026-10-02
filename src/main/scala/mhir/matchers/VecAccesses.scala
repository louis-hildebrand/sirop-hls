package mhir.matchers

import mhir.ir._

object VecAccesses {

  def unapply(e: Expr): Option[(Expr, Seq[Expr])] = {
    e match {
      case VecAccess(VecAccesses(v, is), i) => Some((v, is :+ i))
      case v                                => Some((v, Seq.empty))
    }
  }
}
