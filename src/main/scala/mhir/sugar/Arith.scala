package mhir.sugar

import mhir.ir._
import mhir.typecheck.{TSum, TypeCheck, TypeError}

/** A function which computes the sum of two values.
  */
object PlusFunction {
  def apply(t: TyAnyInt): Function = t ::+ (x => t ::+ (y => x + y))
}

/** A function which computes the product of two values.
  */
object TimesFunction {
  def apply(t: TyAnyInt): Function = t ::+ (x => t ::+ (y => x * y))
}

/** The minimum of two values.
  */
case class Min(x: Expr, y: Expr)(typ: Type = Missing)
    extends SyntaxSugar(x, y)(typ) {
  def apply(x: Expr /* Int */, y: Expr /* Int */ ): Expr /* Int */ = {
    Mux(x < y, x, y)()
  }

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x, y) => Min(x, y)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val x = this.x.tchk(context).expectAnyInt()
    val y = this.y.tchk(context).expectAnyInt()
    ReshapeData.narrowestCommonAncestor(x.typ, y.typ) match {
      case Some(typ) =>
        this.rebuild(typ, Seq(x, y))
      case None =>
        throw new TypeError(
          s"Could not find common supertype for $className with inputs of type ${x.typ} and ${y.typ}."
        )
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    ReshapeData.narrowestCommonAncestor(x.typ, y.typ) match {
      case Some(typ) =>
        Mux(x < y, ReshapeData(x, typ)(), ReshapeData(y, typ)())()
          .tchk()
          .lower
      case None =>
        throw new TypeError(
          s"Could not find common supertype for $className with inputs of type ${x.typ} and ${y.typ}."
        )
    }
  }
}

/** The maximum of two values.
  */
case class Max(x: Expr, y: Expr)(typ: Type = Missing)
    extends SyntaxSugar(x, y)(typ) {
  def apply(x: Expr /* Int */, y: Expr /* Int */ ): Expr /* Int */ = {
    Mux(x > y, x, y)()
  }

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x, y) => Max(x, y)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val x = this.x.tchk(context).expectAnyInt()
    val y = this.y.tchk(context).expectAnyInt()
    ReshapeData.narrowestCommonAncestor(x.typ, y.typ) match {
      case Some(typ) =>
        this.rebuild(typ, Seq(x, y))
      case None =>
        throw new TypeError(
          s"Could not find common supertype for $className with inputs of type ${x.typ} and ${y.typ}."
        )
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    ReshapeData.narrowestCommonAncestor(x.typ, y.typ) match {
      case Some(typ) =>
        Mux(x > y, ReshapeData(x, typ)(), ReshapeData(y, typ)())()
          .tchk()
          .lower
      case None =>
        throw new TypeError(
          s"Could not find common supertype for $className with inputs of type ${x.typ} and ${y.typ}."
        )
    }
  }
}

/** The ceiling of the quotient of two values.
  */
case class CeilDiv(x: Expr, y: Expr)(typ: Type = Missing)
    extends SyntaxSugar(x, y)(typ) {
  def apply(x: Expr, y: Expr): Expr = {
    val q = Param("q")()
    Let(
      q,
      x / y,
      Mux((x % y !== 0) && ((x < 0) === (y < 0)), q + 1, q)()
    )()
  }

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x, y) => CeilDiv(x, y)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val x = this.x.tchk(context).expectAnyInt()
    val y = this.y.tchk(context).expectAnyInt()
    ReshapeData.narrowestCommonAncestor(x.typ, y.typ) match {
      case Some(typ) =>
        this.rebuild(typ, Seq(x, y))
      case None =>
        throw new TypeError(
          s"Could not find common supertype for $className with inputs of type ${x.typ} and ${y.typ}."
        )
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val q = Param("q")()
    Let(
      q,
      x / y,
      Mux((x % y !== 0) && ((x < 0) === (y < 0)), q + 1, q)()
    )().tchk().lower
  }
}

/** Convert from one data type to another, even if this may cause data loss.
  *
  * For example, [[mhir.sugar.ReshapeData]] will refuse to convert `u32` to
  * `u8`, but [[Cast]] can do it (using [[mhir.ir.TruncateTo]]).
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

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val e = this.e.tchk(context)
    if (!Cast.canCast(e.typ, this.target)) {
      throw new TypeError(s"Cannot cast from ${e.typ} to ${this.target}.")
    }
    this.rebuild(this.target, Seq(e))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    val e = this.e.lower
    val result = (e.typ, this.target) match {
      case (t1, t2) if t1 ~= t2 => e
      case (TyUInt(w1), TyUInt(w2)) =>
        if (w1 > w2) TruncateTo(e, w2)()
        else PadTo(e, w2)()
      case (_: TyUInt, t2: TySInt) =>
        Cast(ToSigned(e)(), t2)()
      case (_: TySInt, t2: TyUInt) =>
        Cast(ToUnsigned(e)(), t2)()
      case (TySInt(w1), TySInt(w2)) =>
        if (w1 > w2) TruncateTo(e, w2)()
        else PadTo(e, w2)()
      case (_: TyTuple, TyTuple(ts2 @ _*)) =>
        Tuple(ts2.zipWithIndex.map({ case (t, i) =>
          Cast(TupleAccess(e, i)(), t)()
        }): _*)()
      case (_: TyVec, TyVec(t2, n2)) =>
        VecBuild(n2, U32 ::+ (i => Cast(VecAccess(e, i)(), t2)()))()
      case _ =>
        throw new TypeError(s"Cannot cast from ${e.typ} to ${this.target}.")
    }
    result.tchk().lower
  }
}

object Cast {
  private def canCast(t1: Type, t2: Type)(implicit
      c: Canonicalizer
  ): Boolean = {
    (t1, t2) match {
      case _ if t1 ~= t2              => true
      case (_: TyAnyInt, _: TyAnyInt) => true
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) =>
        (ts1.length == ts2.length
        && ts1.zip(ts2).forall({ case (t1, t2) => canCast(t1, t2) }))
      case (TyVec(t1, n1), TyVec(t2, n2)) =>
        canCast(t1, t2) && c.sameLen(n1, n2)
      case _ => false
    }
  }
}

/** The sum of several values <i>without overflow</i>.
  *
  * The type of this expression will be chosen so as to guarantee that the sum
  * can be computed without overflow.
  *
  * @param terms
  *   the values to add up.
  */
case class SafeSum(terms: Expr*)(typ: Type = Missing)
    extends SyntaxSugar(terms: _*)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    SafeSum(newChildren: _*)(typ)
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val terms = this.terms.map(e => e.tchk(context).expectAnyInt())
    this.rebuild(
      TSum(terms.map(e => e.typ.asInstanceOf[TyAnyInt]): _*),
      terms
    )
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val terms = this.terms.map(e => e.lower)
    if (terms.isEmpty) {
      IntCst(0)(this.typ)
    } else {
      val typ = this.typ.asInstanceOf[TyAnyInt]
      Sum(terms.map(e => ReshapeData(e, typ)()): _*)().tchk().lower
    }
  }
}
