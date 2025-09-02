package mhir.optimize.cost

import mhir.ir._

object BitWidth {
  def apply(typ: Type): Long = {
    typ match {
      case TyBool              => 1
      case TyAnyInt(w)         => w
      case TyFix(t, _)         => BitWidth(t)
      case TyTuple(ts @ _*)    => ts.map(BitWidth(_)).sum
      case TyVec(t, IntCst(n)) => n * BitWidth(t)
      case Missing | _: TyArrow | _: TyStm =>
        throw new IllegalArgumentException(
          s"Cannot find bit width for non-data type $typ"
        )
    }
  }
}
