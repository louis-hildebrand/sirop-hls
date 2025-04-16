package ir

sealed trait Type
case object Missing extends Type
case object TyInt extends Type
case object TyBool extends Type
case class TyArrow(t1: Type, t2: Type) extends Type
case class TyTuple(ts: Type*) extends Type
case class TyVec(t: Type, n: Expr) extends Type
case class TyStm(t: Type, n: Expr) extends Type
