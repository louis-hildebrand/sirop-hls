package mhir.eval

import mhir.ir._

object IntCstOrUndefined {

  def apply(k: Long)(typ: TyAnyInt): Expr = {
    if (typ.contains(k)) {
      IntCst(k)(typ)
    } else {
      Undefined(typ)
    }
  }
}
