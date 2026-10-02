package mhir.matchers

import mhir.ir._

object NestedVecCst {

  def unapply(e: Expr): Option[Expr] = {
    e match {
      case VecBuild(_, Function(_, NestedVecCst(k)))         => Some(k)
      case VecBuild(_, Function(_, k)) if k.freeVars.isEmpty => Some(k)
      case _                                                 => None
    }
  }
}
