package mhir.matchers

import mhir.canonicalize._
import mhir.ir._

object TyVecs {

  def apply(elemTyp: Type, lengths: Seq[Expr]): Type = {
    lengths.foldLeft(elemTyp)({ case (t, n) => TyVec(t, n) })
  }

  def unapply(typ: Type): Option[(Type, Seq[Expr])] = {
    typ match {
      case TyVec(TyVecs(elemTyp, lengths), n) =>
        Some((elemTyp, lengths :+ n))
      case typ =>
        Some((typ, Vector.empty))
    }
  }
}
