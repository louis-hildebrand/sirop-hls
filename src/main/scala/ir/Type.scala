package ir

import opt.PartialEvalPass

import scala.annotation.tailrec

sealed trait Type {
  def ::+(f: Expr => Expr): Function = {
    val x = Param("x")(this)
    val body = f(x)
    val t =
      if (body.typ != Missing) TyArrow(this, body.typ)
      else Missing
    Function(x, f(x))(t)
  }

  /** This type, but where all streams are made one-dimensional.
    */
  def flat: Type = {
    this match {
      case Missing | TyInt | TyBool => this
      case TyArrow(t1, t2)          => TyArrow(t1.flat, t2.flat)
      case TyTuple(ts @ _*)         => TyTuple(ts.map(t => t.flat): _*)
      case TyVec(t, n)              => TyVec(t.flat, n)
      case TyStm(t, n) =>
        t.flat match {
          case TyStm(t, m) => TyStm(t, n * m)
          case t           => TyStm(t, n)
        }
    }
  }

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
        t1.isCompatibleWith(t2) && sameLen(n1, n2)
      case (TyStm(t1, _), TyStm(t2, _)) =>
        // Two streams are compatible even if they have different lengths!
        t1.isCompatibleWith(t2)
      case _ => false
    }
  }

  /** Check whether two types are "compatible," i.e., will have the same shape
    * in hardware.
    */
  def ~=(that: Type): Boolean = this.isCompatibleWith(that)

  private def sameLen(e1: Expr, e2: Expr): Boolean = {
    val e1Normalized = normalizeLen(e1)
    val e2Normalized = normalizeLen(e2)
    e1Normalized == e2Normalized
  }

  @tailrec
  private def normalizeLen(e: Expr): Expr = {
    e match {
      case VecLength(v) =>
        v.typ match {
          case TyVec(_, n) => normalizeLen(n)
          // TODO: It is very sketchy to have the type checker depend on an
          //       optimization pass
          case _ => PartialEvalPass.partialEval(e)
        }
      case e => PartialEvalPass.partialEval(e)
    }
  }
}
case object Missing extends Type
case object TyInt extends Type
case object TyBool extends Type
case class TyArrow(t1: Type, t2: Type) extends Type
case class TyTuple(ts: Type*) extends Type
case class TyVec(t: Type, n: Expr) extends Type

/** @param t
  *   The type of the <i>unwrapped</i> elements produced by the stream. In other
  *   words, this is the type seen by consumers of the stream (usually
  *   <i>not</i> an <code>Option</code> type).
  * @param n
  *   The length of the stream.
  */
case class TyStm(t: Type, n: Expr) extends Type {

  /** If this is a stream of type <code>Stm&lt;T; n&gt;</code>, then
    * <code>tOpt</code> is <code>Option&lt;T&gt;</code>. That is,
    * <code>tOpt</code> is the type of the output expression inside the
    * <code>StmBuild</code> itself.
    */
  val tOpt: Type = TyOption(t)
}

case object TyOption {
  def apply(t: Type): Type = TyTuple(t, TyBool)
}
case object Int2 {
  def apply(): Type = TyTuple(TyInt, TyInt)
}
