package mhir.sugar

import com.typesafe.scalalogging.Logger
import mhir.ir.Lowering.{ExprLowering, TypeLowering}
import mhir.ir.StreamReplicator.StreamReplication
import mhir.ir._
import mhir.ir.typecheck.{TypeCheck, TypeError}
import mhir.sugar.Streamifier.Streamify

import scala.annotation.tailrec

private object VL {
  val logger: Logger = Logger("VectorSyntaxSugar")
}

case class VecLength(v: Expr)(typ: Type = Missing) extends SyntaxSugar(v)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v) => VecLength(v)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV = v.tchk
    newV.typ match {
      case TyVec(_, n) =>
        assert(n.typ != Missing)
        this.rebuild(n.typ, Seq(newV))
      case t =>
        throw new TypeError(
          s"Vector in VecLength has type $t. Expected a vector."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    v.typ.asInstanceOf[TyVec].n.tchk().lower()
  }
}

case class VecMap(v: Expr /* Vec<A; n> */, f: Expr /* A -> B */ )(
    typ: Type = Missing
) /* Vec<B; n> */
    extends SyntaxSugar(v, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, f) => VecMap(v, f)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    val (t1, n) = newV.typ match {
      case TyVec(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"Vector of $className has type $t. Expected a vector."
        )
    }
    val newF = this.f.annotateFunc(t1).tchk
    newF.typ match {
      case TyArrow(t, t2) if t ~= t1 =>
        this.rebuild(TyVec(t2, n), Seq(newV, newF))
      case t =>
        throw new TypeError(
          s"Function of VecMap has type $t. Expected a function with input type $t1."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    VL.logger.trace(s"lowering $className: $this")
    requireType()
    val v = this.v.lower()
    val f = this.f.lower()
    val result = f.typ.asInstanceOf[TyArrow] match {
      case TyArrow(t1, t2) if t1.isData && t2.isData =>
        VL.logger.trace(s"lowering $className as standard vector...")
        VecBuild(
          v.typ.asInstanceOf[TyVec].n,
          U32 ::+ (i => FunCall(f, VecAccess(v, i)())())
        )().tchk().lower()
      case TyArrow(t1, _: TyStm) =>
        VL.logger.trace(s"lowering $className containing streams...")
        val Function(x, stm) = f.streamify()
        val n = this.v.typ.asInstanceOf[TyVec].n
        val i = Param("i")(U32)
        val replicatedStm = stm.replicate(n, i = i, varsToReplicate = Set(x))
        val result = t1 match {
          case _: TyStm =>
            replicatedStm.subPreserveType(x -> v)
          case _ =>
            replicatedStm.subPreserveType(x -> StmCst(1, v)().tchk().lower())
        }
        result.tchk()
      case t =>
        throw new IllegalArgumentException(
          s"Cannot lower VecMap with function of type $t."
        )
    }
    VL.logger.trace(s"done lowering $className")
    result
  }
}

/** Map over two vectors of the same length.
  *
  * @param v1
  *   (`Vec[A, n]`) the first vector.
  * @param v2
  *   (`Vec[B, n]`) the second vector.
  * @param f
  *   (`A -> B -> C`) a function to apply to each corresponding pair of elements
  *   in the vectors (`v1[0]` with `v2[0]`, `v1[1]` with `v2[1]`, etc.).
  * @note
  *   the result will have type `Vec[C, n]`
  */
case class VecMap2(v1: Expr, v2: Expr, f: Expr)(typ: Type = Missing)
    extends SyntaxSugar(v1, v2, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v1, v2, f) => VecMap2(v1, v2, f)(typ)
      case _              => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val v1 = this.v1.tchk
    val (t1, n1) = v1.typ match {
      case TyVec(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"First vector in $className has type $t."
            + " Expected a vector."
        )
    }
    val v2 = this.v2.tchk
    val (t2, n2) = v2.typ match {
      case TyVec(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"Second vector in $className has type $t."
            + " Expected a vector."
        )
    }
    if (!Type.sameLen(n1, n2)) {
      throw new TypeError(
        s"Vector lengths in $className do not match: $n1 and $n2."
      )
    }
    val f = this.f.annotateFunc(t1, t2).tchk
    val t3 = f.typ match {
      case TyArrow(ft1, TyArrow(ft2, ft3)) if (ft1 ~= t1) && (ft2 ~= t2) =>
        ft3
      case t =>
        throw new TypeError(
          s"Function in $className has type $t."
            + s" Expected a function with input types $t1 and $t2."
        )
    }
    this.rebuild(TyVec(t3, n1), Seq(v1, v2, f))
  }

  override def lowerSyntaxSugar(): Expr = {
    VL.logger.trace(s"lowering $className: $this")
    requireType()
    val n = this.v1.typ.asInstanceOf[TyVec].n
    val v1 = this.v1.lower()
    val v2 = this.v2.lower()
    val f = this.f.lower()
    val lowered = f.typ match {
      case TyArrow(TyData(_), TyArrow(TyData(_), TyData(_))) =>
        VL.logger.trace(s"lowering $className as standard vector...")
        VecBuild(
          n,
          U32 ::+ (i => f(VecAccess(v1, i)())(VecAccess(v2, i)()))
        )().tchk().lower()
      case TyArrow(t1, TyArrow(t2, _: TyStm)) =>
        VL.logger.trace(s"lowering $className containing streams...")
        val Function(x1, Function(x2, stm)) = f.streamify()
        val i = Param("i")(U32)
        val replicatedStm =
          stm.replicate(n, i = i, varsToReplicate = Set(x1, x2))
        val withV1 = t1 match {
          case _: TyStm =>
            replicatedStm.subPreserveType(x1 -> v1)
          case _ =>
            replicatedStm.subPreserveType(x2 -> StmCst(1, v2)().tchk().lower())
        }
        val withV2 = t2 match {
          case _: TyStm =>
            withV1.subPreserveType(x2 -> v2)
          case _ =>
            withV1.subPreserveType(x2 -> StmCst(1, v2)()).tchk().lower()
        }
        withV2.tchk()
      case t =>
        throw new IllegalArgumentException(
          s"Cannot lower $className with function of type $t."
        )
    }
    VL.logger.trace(s"done lowering $className")
    lowered
  }
}

/** Sequential fold over a vector.
  *
  * This produces a stream of length one containing the result, since it may
  * take multiple steps.
  *
  * @param v
  *   the vector to fold over.
  * @param z
  *   the initial value.
  * @param f
  *   the function to use for folding.
  */
case class VecFoldSeq(
    v: Expr /* Vec<T1; n> */,
    z: Expr /* T2 */,
    f: Function /* T2 -> T1 -> T2 */
)(typ: Type = Missing) /* Stm<T2; 1> */
    extends SyntaxSugar(v, z, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, z, f: Function) => VecFoldSeq(v, z, f)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    val t1 = newV.typ match {
      case TyVec(t, _) => t
      case t           => throw new TypeError(s"Vector in VecFold has type $t.")
    }
    val newZ = z.tchk(context)
    val t2 = newZ.typ
    val newF = f.tchk(context)
    newF.typ match {
      case TyArrow(t3, TyArrow(t4, t5))
          if (t3 ~= t1) && (t4 ~= t2) && (t5 ~= t2) =>
        ()
      case t =>
        throw new TypeError(
          s"Function in VecFold has type $t. Expected ${TyArrow(t1, TyArrow(t2, t2))}."
        )
    }
    this.rebuild(TyStm(t2, 1), Seq(newV, newZ, newF))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    StmFold(Vec2Stm(v)(), z, f)().tchk().lower()
  }
}

/** Combinational fold over a vector.
  *
  * This produces the result as-is in one step.
  *
  * @param v
  *   the vector to fold over.
  * @param z
  *   the initial value.
  * @param f
  *   the function to use for folding.
  */
case class VecFoldComb(
    v: Expr /* Vec<T1; n> */,
    z: Expr /* T2 */,
    f: Expr /* T2 -> T1 -> T2 */
)(typ: Type = Missing) /* T2 */
    extends SyntaxSugar(v, z, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, z, f) => VecFoldComb(v, z, f)(typ)
      case _            => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val v = this.v.tchk
    val t1 = v.typ match {
      case TyVec(t, _) => t
      case t =>
        throw new TypeError(
          s"Vector in $className has type $t. Expected a vector."
        )
    }
    val z = this.z.tchk
    val t2 = z.typ
    val f = this.f.tchk.expectType(t2 ->: t1 ->: t2)
    this.rebuild(t2, Seq(v, z, f))
  }

  override def lowerSyntaxSugar(): Expr = {
    val v = this.v.lower()
    val z = this.z.lower()
    val f = this.f.lower()
    val n = v.typ.asInstanceOf[TyVec].n
    n match {
      case IntCst(n) =>
        (0 until n.toInt)
          .foldLeft(z)({ case (acc, i) => f(acc)(VecAccess(v, C(i)())()) })
          .tchk()
          .lower()
      case e =>
        throw new IllegalArgumentException(
          s"Cannot use $className on a vector with non-constant size $e."
        )
    }
  }
}

/** Combinational reduce over a vector
  *
  * This is a bit like [[VecFoldComb]], but the first element of the vector is
  * used as the initial value.
  *
  * This is meant to mirror the `reduce_s` primitive from
  * [[https://dl.acm.org/doi/10.1145/3385412.3385983 Aetherling]]. Therefore,
  * strange expressions like `reduce_s (map_s (add I) I) I` must unfortunately
  * be supported.
  */
case class VecReduceComb(
    v: Expr /* Vec<T; n> */,
    f: Expr /* (T, T) -> T */
)(typ: Type = Missing) /* Vec<T; 1> */
    extends SyntaxSugar(v, f)(typ) /* T */ {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, f) => VecReduceComb(v, f)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val v = this.v.tchk
    // The type of the accumulator, but possibly wrapped in a bunch of vectors
    // and streams of length 1
    val wrappedTyp = v.typ match {
      case TyVec(t, _) => t
      case t =>
        throw new TypeError(
          s"Vector in $className has type $t. Expected a vector."
        )
    }
    val tupledTyp = tupleElemType(wrappedTyp, this.f)
    val f =
      this.f.annotateFunc(tupledTyp).tchk.expectType(tupledTyp ->: wrappedTyp)
    this.rebuild(TyVec(wrappedTyp, 1), Seq(v, f))
  }

  override def lowerSyntaxSugar(): Expr = {
    VL.logger.trace(s"lowering $className: $this")
    requireType()
    val v = this.v.lower()
    val wrappedTyp = this.typ.asInstanceOf[TyVec].t
    val f = unwrapFunc(wrappedTyp, this.f).lower()
    val n = this.v.typ.asInstanceOf[TyVec].n match {
      case IntCst(n) if n > 0 => n
      case IntCst(n) if n <= 0 =>
        throw new IllegalArgumentException(
          s"Cannot reduce over empty vector (length $n)."
        )
      case e =>
        throw new IllegalArgumentException(
          s"Cannot reduce over vector with non-constant size $e."
        )
    }
    val result = (v: Expr) => {
      val elem =
        (i: Int) => unwrapElem(wrappedTyp, this.f, VecAccess(v, i)())
      (1 until n.toInt)
        .foldLeft(elem(0))({ case (acc, i) => f(Tuple(acc, elem(i))()) })
    }
    wrapResult(result, v).tchk().lower()
  }

  private def tupleElemType(wrappedTyp: Type, f: Expr): Type = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        TyVec(tupleElemType(t, g), 1)
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        TyStm(tupleElemType(t, g), 1)
      case _ =>
        (wrappedTyp, wrappedTyp)
    }
  }

  @tailrec
  private def unwrapFunc(wrappedTyp: Type, f: Expr): Expr = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        unwrapFunc(t, g)
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        unwrapFunc(t, g)
      case _ =>
        f
    }
  }

  private def unwrapElem(wrappedTyp: Type, f: Expr, x: Expr): Expr = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        VecAccess(unwrapElem(t, g, x), 0)()
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        // Streams should be moved to the outside during lowering, so no need
        // to do anything here
        unwrapElem(t, g, x)
      case _ =>
        x
    }
  }

  private def wrapResult(result: Expr => Expr, v: Expr): Expr = {
    def wrap(t: Type, x: Expr): Expr = {
      assert(x.hasType)
      if (t == x.typ) {
        x
      } else {
        t match {
          case TyVec(t, IntCst(1)) =>
            VecBuild(1, U8 ::+ (_ => wrap(t, x)))()
          case TyStm(t, IntCst(1)) =>
            wrap(t, x)
          case t =>
            throw new IllegalArgumentException(
              s"Cannot wrap result of $className to have type $t."
            )
        }
      }
    }
    this.typ.lower match {
      case TyStm(t, m) =>
        require(
          Type.sameLen(m, 1),
          s"Cannot wrap result of $className into a stream of length $m."
        )
        val vv = Param("v")(v.typ.asInstanceOf[TyStm].t)
        val res = result(vv).tchk()
        StmMap(v, Function(vv, wrap(t, res))())()
      case t =>
        val res = result(v).tchk()
        wrap(t, res)
    }
  }
}

/** Sequential, inclusive scan over a vector.
  */
object VecScanInclusive {
  def apply(
      v: Expr /* Vec<A; n> */,
      z: Expr /* B */,
      f: Function /* B -> A -> B */
  ): Expr /* Stm<Vec<B; n>; 1> */ = {
    Stm2Vec(StmScanInclusive(Vec2Stm(v)(), z, f)())()
  }
}

/** Sequential, exclusive scan over a vector.
  */
object VecScanExclusive {
  def apply(
      v: Expr /* Vec<A; n> */,
      z: Expr /* B */,
      f: Function /* B -> A -> B */
  ): Expr /* Stm<Vec<B; n>; 1> */ = {
    Stm2Vec(StmScanExclusive(Vec2Stm(v)(), z, f)())()
  }
}

case class Stm2Vec(s: Expr /* Stm<A; n> */ )(
    typ: Type = Missing
) /* Stm<Vec<A; n>; 1> */
    extends SyntaxSugar(s)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => Stm2Vec(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newS = s.tchk(context)
    newS.typ match {
      case TyStm(t, n) => this.rebuild(TyStm(TyVec(t, n), 1), Seq(newS))
      case t           => throw new TypeError(s"Stream in Stm2Vec has type $t.")
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val s = this.s.lower()
    val (t, n) = this.typ match {
      case TyStm(TyVec(t, n), IntCst(1)) => (t, n)
      case t =>
        throw new IllegalArgumentException(s"Stm2Vec has wrong type $t.")
    }
    StmFold(
      s,
      Undefined(TyVec(t, n)),
      TyVec(t, n) ::+ (v => t ::+ (e => VecShiftLeft(v, e)()))
    )().tchk().lower()
  }
}

object Vec2Tuple {
  def apply(vec: VecBuild): Tuple = {
    val n = mhir.ir.eval(VecLength(vec)()).asInstanceOf[IntCst].i
    val elems = (0 until n.toInt).map(i => VecAccess(vec, i)())
    Tuple(elems: _*)()
  }
}

case class VecPrepend(v: Expr /* Vec<A; n> */, e: Expr /* A */ )(
    typ: Type = Missing
) /* Vec<A; n+1> */
    extends SyntaxSugar(v, e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, e) => VecPrepend(v, e)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    val (t, n) = newV.typ match {
      case TyVec(t, n) => (t, n)
      case t => throw new TypeError(s"Vector of VecPrepend has type $t.")
    }
    val newE = e.tchk(context)
    if (newE.typ ~= t) {
      this.rebuild(TyVec(t, SafeSum(n, 1)().tchk()), Seq(newV, newE))
    } else {
      throw new TypeError(
        s"Element of VecPrepend has type ${newE.typ}. Expected $t."
      )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    VecConcat(VecBuild(1, U32 ::+ (_ => e))(), v)().tchk().lower()
  }
}

case class VecAppend(v: Expr /* Vec<A; n> */, e: Expr /* A */ )(
    typ: Type = Missing
) /* Vec<A; n+1> */
    extends SyntaxSugar(v, e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, e) => VecAppend(v, e)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    val (t, n) = newV.typ match {
      case TyVec(t, n) => (t, n)
      case t => throw new TypeError(s"Vector of VecAppend has type $t.")
    }
    val newE = e.tchk(context)
    if (newE.typ ~= t) {
      this.rebuild(TyVec(t, SafeSum(n, 1)().tchk()), Seq(newV, newE))
    } else {
      throw new TypeError(
        s"Element of VecAppend has type ${newE.typ}. Expected $t."
      )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    VecConcat(v, VecBuild(1, U32 ::+ (_ => e))())().tchk().lower()
  }
}

case class VecPrefix(
    vec: Expr /* Vec<A; n> */,
    k: Expr /* Int */
)(typ: Type = Missing)
    extends SyntaxSugar(vec, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, k) => VecPrefix(v, k)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newK = k.tchk.expectUInt()
    val newV = vec.tchk(context)
    newV.typ match {
      case TyVec(t, _) =>
        this.rebuild(TyVec(t, newK), Seq(newV, newK))
      case t =>
        throw new TypeError(
          s"Argument of ${VecPrefix.getClass.getSimpleName} has type $t. Expected a vector."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    VecBuild(k, U32 ::+ (i => VecAccess(vec, i)()))().tchk().lower()
  }
}

case class VecSuffix(
    vec: Expr /* Vec<A; n> */,
    k: Expr /* Int */
)(typ: Type = Missing) /* Vec<A; k> */
    extends SyntaxSugar(vec, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, k) => VecSuffix(v, k)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newK = k.tchk.expectUInt()
    val newV = vec.tchk(context)
    newV.typ match {
      case TyVec(t, _) =>
        this.rebuild(TyVec(t, k), Seq(newV, newK))
      case t =>
        throw new TypeError(
          s"Argument of ${VecSuffix.getClass.getSimpleName} has type $t. Expected a vector."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    val n = vec.typ.asInstanceOf[TyVec].n
    val i0 = ToUnsigned(n - k)()
    VecBuild(k, U32 ::+ (i => VecAccess(vec, i0 + i)()))()
      .tchk()
      .lower()
  }
}

/** Discard the first element of the given vector and insert the given value at
  * the end.
  *
  * @param vec
  *   the vector to shift.
  * @param e
  *   the element to insert.
  */
case class VecShiftLeft(
    vec: Expr /* Vec<A; n> */,
    e: Expr /* A */
)(typ: Type = Missing) /* Vec<A; n> */
    extends SyntaxSugar(vec, e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, e) => VecShiftLeft(v, e)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV = vec.tchk(context)
    val (t, n) = newV.typ match {
      case TyVec(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"First argument in ${VecShiftLeft.getClass.getSimpleName} has type $t. Expected a vector."
        )
    }
    val newE = e.tchk(context).expectType(t)
    this.rebuild(TyVec(t, n), Seq(newV, newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.vec.typ.asInstanceOf[TyVec].n
    val e = this.e.lower()
    val v = this.vec.lower()
    v.typ match {
      case TyStm(tv: TyVec, _) =>
        val tt = TyTuple(tv, tv.t)
        StmMap(StmZip(v, e)(), tt ::+ (vv => VecShiftLeft(vv.__0, vv.__1)()))()
          .tchk()
          .lower()
      case _ =>
        VecBuild(
          n,
          U32 ::+ (i => Mux((i + 1) === n, e, VecAccess(v, i + 1)())())
        )().tchk().lower()
    }
  }
}

/** Discard the last element of the given vector and insert the given value at
  * the beginning.
  *
  * @param vec
  *   the vector to shift.
  * @param e
  *   the element to insert.
  */
case class VecShiftRight(
    vec: Expr /* Vec<A; n> */,
    e: Expr /* A */
)(typ: Type = Missing) /* Vec<A; n> */
    extends SyntaxSugar(vec, e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, e) => VecShiftRight(v, e)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV = vec.tchk(context)
    val (t, n) = newV.typ match {
      case TyVec(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"First argument in ${VecShiftRight.getClass.getSimpleName} has type $t. Expected a vector."
        )
    }
    val newE = e.tchk(context).expectType(t)
    this.rebuild(TyVec(t, n), Seq(newV, newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = vec.typ.asInstanceOf[TyVec].n
    VecPrepend(VecPrefix(vec, ToUnsigned(n - 1)())(), e)().tchk().lower()
  }
}

/** Discard the last element of the given vector and insert an undefined value
  * at the beginning.
  *
  * @param vec
  *   the vector to shift.
  */
case class VecShiftRightGarbage(vec: Expr, shiftAmount: IntCst)(
    typ: Type = Missing
) extends SyntaxSugar(vec, shiftAmount)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, m: IntCst) => VecShiftRightGarbage(v, m)(typ)
      case _                 => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV = vec.tchk(context)
    val (t, n) = newV.typ match {
      case TyVec(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"First argument in ${VecShiftRight.getClass.getSimpleName} has type $t. Expected a vector."
        )
    }
    val newShiftAmount = this.shiftAmount.tchk.expectUInt().asInstanceOf[IntCst]
    if (newShiftAmount.i <= 0) {
      throw new TypeError(
        s"Shift amount in $className must be strictly positive (got $newShiftAmount)."
      )
    }
    this.rebuild(TyVec(t, n), Seq(newV, newShiftAmount))
  }

  override def lowerSyntaxSugar(): Expr = {
    VL.logger.trace(s"lowering $className: $this")
    requireType()
    val TyVec(t, n) = this.vec.typ
    VecConcat(
      Undefined(TyVec(t, this.shiftAmount)),
      VecPrefix(
        this.vec,
        ToUnsigned(SafeSum(n, C(-this.shiftAmount.i)())())()
      )()
    )().tchk().lower()
  }
}

case class VecConcat(
    v1: Expr /* Vec<A; n> */,
    v2: Expr /* Vec<A; m> */
)(typ: Type = Missing) /* Vec<A; n+m> */
    extends SyntaxSugar(v1, v2)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v1, v2) => VecConcat(v1, v2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV1 = v1.tchk(context)
    val (t1, n1) = newV1.typ match {
      case TyVec(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"First argument in VecConcat has type $t. Expected a vector."
        )
    }
    val newV2 = v2.tchk(context)
    val (t2, n2) = newV2.typ match {
      case TyVec(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"Second argument in VecConcat has type $t. Expected a vector."
        )
    }
    if (t1 ~= t2) {
      this.rebuild(TyVec(t1, SafeSum(n1, n2)().tchk()), Seq(newV1, newV2))
    } else {
      throw new TypeError(
        s"First vector in VecConcat contains elements of type $t1, but second vector contains elements of type $t2."
      )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val v1 = this.v1.lower()
    val n1 = this.v1.typ.asInstanceOf[TyVec].n
    val v2 = this.v2.lower()
    val n2 = this.v2.typ.asInstanceOf[TyVec].n
    v1.typ match {
      case TyStm(tv1: TyVec, _) =>
        val tv2 = v2.typ.asInstanceOf[TyStm].t.asInstanceOf[TyVec]
        StmMap(
          StmZip(v1, v2)(),
          TyTuple(tv1, tv2) ::+ (vv => VecConcat(vv.__0, vv.__1)())
        )().tchk().lower()
      case _ =>
        VecBuild(
          SafeSum(n1, n2)(),
          U32 ::+ (i =>
            Mux(
              i < n1,
              VecAccess(v1, i)(),
              VecAccess(v2, ToUnsigned(i - n1)())()
            )()
          )
        )().tchk().lower()
    }
  }
}

case class VecZip(a: Expr, b: Expr)(typ: Type = Missing)
    extends SyntaxSugar(a, b)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(a, b) => VecZip(a, b)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val a = this.a.tchk
    val (aElem, aLen) = a.typ match {
      case TyVec(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"First argument to $className has type $t. Expected a vector."
        )
    }
    val b = this.b.tchk
    val bElem = b.typ match {
      case TyVec(t, _) => t
      case t =>
        throw new TypeError(
          s"First argument to $className has type $t. Expected a vector."
        )
    }
    this.rebuild(TyVec((aElem, bElem), aLen), Seq(a, b))
  }

  override def lowerSyntaxSugar(): Expr = {
    VecMap2(a, b, Missing ::+ (x => Missing ::+ (y => Tuple(x, y)())))()
      .tchk()
      .lower()
  }
}

object VecRepeat {
  def apply(
      vec: Expr /* Vec<A; n> */,
      m: Expr
  ): Expr /* Vec<Vec<A; n>, m> */ = {
    VecBuild(m, U32 ::+ (_ => vec))()
  }
}

object VecReverse {
  def apply(v: Expr /* Vec<A; n> */ ): Expr /* Vec<A; n> */ = {
    val n = VecLength(v)()
    VecBuild(n, U32 ::+ (i => VecAccess(v, ToUnsigned(n - i - 1)())()))()
  }
}

/** Convert a flat vector to a nested vector.
  *
  * @note
  *   `n`, the size of the flat vector, must be divisible by `m`.
  *
  * @param vec
  *   (`Vec[T, n]`) the vector to split.
  * @param m
  *   (`Int`) the size of the new inner dimension.
  * @return
  *   (`Vec[Vec[T, m], n/m]`)
  */
case class VecSplit(vec: Expr, m: Expr)(typ: Type = Missing)
    extends SyntaxSugar(vec, m)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, m) => VecSplit(v, m)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val vec = this.vec.tchk
    val (t, n) = vec.typ match {
      case TyVec(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"Vector in $className has type $t."
            + " Expected a vector."
        )
    }
    val m = this.m.tchk.expectUInt()
    this.rebuild(TyVec(TyVec(t, m), n / m), Seq(vec, m))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = VecLength(this.vec)()
    VecBuild(
      n / this.m,
      U32 ::+ (i =>
        VecBuild(m, U32 ::+ (j => VecAccess(this.vec, i * this.m + j)()))()
      )
    )().tchk().lower()
  }
}

case class VecJoin(v: Expr /* Vec<Vec<A; m>; n> */ )(
    typ: Type = Missing
) /* Vec<A; n*m> */
    extends SyntaxSugar(v)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v) => VecJoin(v)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    newV.typ match {
      case TyVec(TyVec(t, m), n) =>
        this.rebuild(TyVec(t, SafeProd(n, m)().tchk()), Seq(newV))
      case t =>
        throw new TypeError(
          s"Vector in VecJoin has type $t. Expected a nested vector."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val (n, m) = this.v.typ match {
      case TyVec(TyVec(_, m), n) => (n, m)
      case t => throw new TypeError(s"Vector in VecJoin has type $t.")
    }
    VecBuild(
      SafeProd(n, m)(),
      U32 ::+ (i => VecAccess(VecAccess(v, i / m)(), i % m)())
    )().tchk().lower()
  }
}

object VecSlide {
  def apply(
      vec: Expr /* Vec<A; n> */,
      m: Int
  ): Expr /* Vec<Vec<A, m>, n-m+1> */ = {
    val n = VecLength(vec)()
    VecBuild(
      ToUnsigned(n + -m + 1)(),
      U32 ::+ (i => VecBuild(m, U32 ::+ (j => VecAccess(vec, i + j)()))())
    )()
  }
}

object VecTranspose {
  def apply(v: Expr /* Vec<Vec<A; m>; n> */ ): Expr /* Vec<Vec<A; n>; m> */ = {
    val n = VecLength(v)()
    val m = VecLength(VecAccess(v, 0)())()
    VecBuild(
      m,
      U32 ::+ (i =>
        VecBuild(n, U32 ::+ (j => VecAccess(VecAccess(v, j)(), i)()))()
      )
    )()
  }
}
