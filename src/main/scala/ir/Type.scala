package ir

sealed trait Type {

  /** Check whether two types are "compatible," i.e., will have the same shape
    * in hardware.
    */
  def isCompatibleWith(that: Type): Boolean = {
    (this, that) match {
      case (TyBool, TyBool) => true
      case (TyInt, TyInt)   => true
      case (TyArrow(t1, t2), TyArrow(t3, t4)) =>
        t1.isCompatibleWith(t3) && t2.isCompatibleWith(t4)
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) =>
        (ts1.length == ts2.length
        && ts1
          .zip(ts2)
          .forall({ case (t1, t2) => t1.isCompatibleWith(t2) }))
      case (TyVec(t1, n1), TyVec(t2, n2)) =>
        // TODO: Improve check for equality of lengths?
        t1.isCompatibleWith(t2) && n1 == n2
      case (TyStm(t1, _), TyStm(t2, _)) =>
        // Two streams are compatible even if they have different lengths!
        t1.isCompatibleWith(t2)
      case _ => false
    }
  }
}
case object Missing extends Type
case object TyInt extends Type
case object TyBool extends Type
case class TyArrow(t1: Type, t2: Type) extends Type
case class TyTuple(ts: Type*) extends Type
case class TyVec(t: Type, n: Expr) extends Type
case class TyStm(t: Type, n: Expr) extends Type
