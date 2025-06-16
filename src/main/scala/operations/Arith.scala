package operations

import ir._

/** A function which computes the sum of two values.
  */
object PlusFunction {
  def apply(t: TyAnyInt): Function = t ::+ (x => t ::+ (y => x + y))

  @deprecated
  def apply(): Function = TyInt ::+ (x => TyInt ::+ (y => x + y))
}

/** A function which computes the product of two values.
  */
object TimesFunction {
  def apply(t: TyAnyInt): Function = t ::+ (x => t ::+ (y => x * y))
}

// TODO: Reimplement these as syntax sugar as well?

/** The minimum of two values.
  */
object Min {
  def apply(x: Expr /* Int */, y: Expr /* Int */ ): Expr /* Int */ = {
    Mux(x < y, x, y)()
  }
}

/** The maximum of two values.
  */
object Max {
  def apply(x: Expr /* Int */, y: Expr /* Int */ ): Expr /* Int */ = {
    Mux(x > y, x, y)()
  }
}

/** The ceiling of the quotient of two values.
  */
object CeilDiv {
  def apply(x: Expr, y: Expr): Expr = {
    val q = Param("q")()
    Let(
      q,
      x / y,
      Mux((x % y !== 0) && ((x < 0) === (y < 0)), q + 1, q)()
    )()
  }
}

/** Convert from one data type to another, even if this may cause data loss.
  *
  * For example, [[ReshapeData]] will refuse to convert `u32` to `u8`, but
  * [[Cast]] can do it (using [[TruncateTo]]).
  *
  * @param e
  *   the expression to convert.
  * @param target
  *   the target type.
  */
case class Cast(e: Expr, target: Type)(typ: Type = Missing)
    extends SyntaxSugar(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => Cast(e, this.target)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val e = this.e.tchk
    if (!Cast.canCast(e.typ, this.target)) {
      throw new TypeError(s"Cannot cast from ${e.typ} to ${this.target}.")
    }
    this.rebuild(this.target, Seq(e))
  }

  override def lowerSyntaxSugar(): Expr = {
    val e = this.e.lower()
    val result = (e.typ, this.target) match {
      case (TyBool, TyBool) => e
      case (TyUInt(w1), TyUInt(w2)) =>
        if (w1 < w2) PadTo(e, w2)()
        else if (w1 > w2) TruncateTo(e, w2)()
        else e
      case (_: TyUInt, t2: TySInt) =>
        Cast(ToSigned(e)(), t2)()
      case (_: TySInt, t2: TyUInt) =>
        Cast(ToUnsigned(e)(), t2)()
      case (TySInt(w1), TySInt(w2)) =>
        if (w1 < w2) PadTo(e, w2)()
        else if (w1 > w2) TruncateTo(e, w2)()
        else e
      case (_: TyTuple, TyTuple(ts2 @ _*)) =>
        Tuple(ts2.zipWithIndex.map({ case (t, i) =>
          Cast(TupleAccess(e, i)(), t)()
        }): _*)()
      case (_: TyVec, TyVec(t2, n2)) =>
        VecBuild(n2, U32 ::+ (i => Cast(VecAccess(e, i)(), t2)()))()
      case _ =>
        throw new TypeError(s"Cannot cast from ${e.typ} to ${this.target}.")
    }
    result.tchk().lower()
  }
}

private object Cast {
  private def canCast(t1: Type, t2: Type): Boolean = {
    (t1, t2) match {
      case (TyBool, TyBool)           => true
      case (_: TyAnyInt, _: TyAnyInt) => true
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) =>
        (ts1.length == ts2.length
        && ts1.zip(ts2).forall({ case (t1, t2) => canCast(t1, t2) }))
      case (TyVec(t1, n1), TyVec(t2, n2)) =>
        canCast(t1, t2) && Type.sameLen(n1, n2)
      case _ => false
    }
  }
}
