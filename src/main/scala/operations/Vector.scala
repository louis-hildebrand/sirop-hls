package operations

import ir._

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
    newV.typ match {
      case TyVec(t1, n) =>
        val newF = f.tchk(context)
        newF.typ match {
          case TyArrow(t, t2) if t ~= t1 =>
            this.rebuild(TyVec(t2, n), Seq(newV, newF))
          case t =>
            throw new TypeError(
              s"Function of VecMap has type $t. Expected a function with input type $t1."
            )
        }
      case t => throw new TypeError(s"Vector of VecMap has type $t.")
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val v = this.v.lower()
    val f = this.f.lower()
    f.typ.asInstanceOf[TyArrow] match {
      case TyArrow(t1, t2)
          if Default.hasDefault(t1) && Default.hasDefault(t2) =>
        VecBuild(
          VecLength(v)(),
          U32 ::+ (i => FunCall(f, VecAccess(v, i)())())
        )().tchk().lower()
      case TyArrow(t1, _: TyStm) =>
        val (x, stm) = f match {
          case f: Function => AsStm2Stm(f)
          case f =>
            throw new IllegalArgumentException(
              s"Cannot lower VecMap with function $f."
            )
        }
        val n = this.v.typ.asInstanceOf[TyVec].n
        val i = Param("i")(U32)
        val replicatedStm = stm.replicate(n, i = i, varsToReplicate = Set(x))
        t1 match {
          case _: TyStm =>
            replicatedStm.subPreserveType(x -> v)
          case _ =>
            replicatedStm.subPreserveType(x -> StmCst(1, v)().tchk().lower())
        }
      case t =>
        throw new IllegalArgumentException(
          s"Cannot lower VecMap with function of type $t."
        )
    }
  }
}

case class VecFold(
    v: Expr /* Vec<T1; n> */,
    z: Expr /* T2 */,
    f: Function /* T1 -> T2 -> T2 */
)(typ: Type = Missing) /* Stm<T2; 1> */
    extends SyntaxSugar(v, z, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, z, f: Function) => VecFold(v, z, f)(typ)
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
          if t3.isCompatibleWith(t1) && t4.isCompatibleWith(t2) && t5
            .isCompatibleWith(t2) =>
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

object VecScanInclusive {
  def apply(
      v: Expr /* Vec<A; n> */,
      z: Expr /* B */,
      f: Function /* B -> A -> B */
  ): Expr /* Stm<Vec<B; n>; 1> */ = {
    Stm2Vec(StmScanInclusive(Vec2Stm(v)(), z, f)())()
  }
}

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
      VecBuild(n, U32 ::+ (_ => Default(t)))(),
      TyVec(t, n) ::+ (v => t ::+ (e => VecShiftLeft(v, e)()))
    )().tchk().lower()
  }
}

object Vec2Tuple {
  def apply(vec: VecBuild): Tuple = {
    val n = ir.eval(VecLength(vec)()).asInstanceOf[IntCst].i
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
          U32 ::+ (i => Mux(i === n + -1, e, VecAccess(v, i + 1)())())
        )().tchk().lower()
    }
  }
}

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

object VecZip {
  def apply(
      a: Expr /* Vec<A; n> */,
      b: Expr /* Vec<B; n> */
  ): VecBuild /* Vec<(A, B); n> */ =
    VecBuild(
      VecLength(a)(),
      U32 ::+ (i => Tuple(VecAccess(a, i)(), VecAccess(b, i)())())
    )()
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

object VecSplit {
  def apply(
      vec: Expr /* Vec<A; n> */,
      m: Expr
  ): VecBuild /* Vec<Vec<A; m>; n/m> */ = {
    val n = VecLength(vec)()
    // n must be divisible by m
    VecBuild(
      n / m,
      U32 ::+ (i => VecBuild(m, U32 ::+ (j => VecAccess(vec, i * m + j)()))())
    )()
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
