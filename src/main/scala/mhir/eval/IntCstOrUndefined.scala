package mhir.eval

import mhir.ir._

object IntCstOrUndefined {

  def apply(k: Long)(typ: TyAnyInt): Expr = {
    if (typ.contains(k)) {
      // TODO: skip the check inside the IntCst constructor to save time?
      //       Maybe move that check to the factory method instead?
      IntCst(k)(typ)
    } else {
      Undefined(typ)
    }
  }
}
