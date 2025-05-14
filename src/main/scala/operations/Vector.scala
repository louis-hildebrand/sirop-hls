package operations

import ir._
import opt.PartialEvalPass

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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    newV.typ match {
      case TyVec(t1, n) =>
        val newF = f.tchk(context)
        newF.typ match {
          case TyArrow(t, t2) if t.isCompatibleWith(t1) =>
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
    VecBuild(
      VecLength(v)(),
      TyInt ::+ (i => FunCall(f, VecAccess(v, i)())())
    )().tchk().lower()
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

  override def typecheck(context: Map[Param, Type]): Expr = {
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

  override def typecheck(context: Map[Param, Type]): Expr = {
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
      VecBuild(n, TyInt ::+ (_ => Default(t)))(),
      TyVec(t, n) ::+ (v => t ::+ (e => VecShiftLeft(v, e)))
    )().tchk().lower()
  }
}

object Vec2Tuple {
  def apply(vec: VecBuild): Tuple = {
    val n = PartialEvalPass.partialEval(VecLength(vec)()).asInstanceOf[IntCst].i
    val elems = (0 until n).map(i => FunCall(vec.f, i)())
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    val (t, n) = newV.typ match {
      case TyVec(t, n) => (t, n)
      case t => throw new TypeError(s"Vector of VecPrepend has type $t.")
    }
    val newE = e.tchk(context)
    if (newE.typ.isCompatibleWith(t)) {
      this.rebuild(TyVec(t, n + 1), Seq(newV, newE))
    } else {
      throw new TypeError(
        s"Element of VecPrepend has type ${newE.typ}. Expected $t."
      )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    VecBuild(
      VecLength(v)() + 1,
      TyInt ::+ (i => Mux(i === 0, e, VecAccess(v, i + -1)())())
    )().tchk().lower()
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    val (t, n) = newV.typ match {
      case TyVec(t, n) => (t, n)
      case t => throw new TypeError(s"Vector of VecAppend has type $t.")
    }
    val newE = e.tchk(context)
    if (newE.typ.isCompatibleWith(t)) {
      this.rebuild(TyVec(t, n + 1), Seq(newV, newE))
    } else {
      throw new TypeError(
        s"Element of VecAppend has type ${newE.typ}. Expected $t."
      )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    val n = VecLength(v)()
    VecBuild(
      n + 1,
      TyInt ::+ (i => Mux(i === n, e, VecAccess(v, i)())())
    )().tchk().lower()
  }
}

object VecPrefix {
  def apply(
      vec: Expr /* Vec<A; n> */,
      k: Expr /* Int */
  ): Expr /* Vec<A; k> */ = {
    val nVal =
      PartialEvalPass.partialEval(VecLength(vec)()).asInstanceOf[IntCst].i
    val kVal = PartialEvalPass.partialEval(k).asInstanceOf[IntCst].i
    require(kVal >= 0)
    require(kVal <= nVal)

    VecBuild(k, TyInt ::+ (i => VecAccess(vec, i)()))()
  }
}

object VecSuffix {
  def apply(
      vec: Expr /* Vec<A; n> */,
      k: Expr /* Int */
  ): Expr /* Vec<A; k> */ = {
    val nVal =
      PartialEvalPass.partialEval(VecLength(vec)()).asInstanceOf[IntCst].i
    val kVal = PartialEvalPass.partialEval(k).asInstanceOf[IntCst].i
    require(kVal >= 0)
    require(kVal <= nVal)

    val n = VecLength(vec)()
    VecBuild(k, TyInt ::+ (i => VecAccess(vec, i + (n - k))()))()
  }
}

object VecShiftLeft {
  def apply(
      vec: Expr /* Vec<A; n> */,
      e: Expr /* A */
  ): Expr /* Vec<A; n> */ = {
    val n = VecLength(vec)()
    VecBuild(
      n,
      TyInt ::+ (i => Mux(i === n + -1, e, VecAccess(vec, i + 1)())())
    )()
  }
}

object VecShiftRight {
  def apply(
      vec: Expr /* Vec<A; n> */,
      e: Expr /* A */
  ): Expr /* Vec<A; n> */ = {
    val n = VecLength(vec)()
    VecBuild(
      n,
      TyInt ::+ (i => Mux(i === 0, e, VecAccess(vec, i + -1)())())
    )()
  }
}

object VecConcat {
  def apply(
      v1: Expr /* Vec<A; n> */,
      v2: Expr /* Vec<A; m> */
  ): Expr /* Vec<A; n+m> */ = {
    val n = VecLength(v1)()
    val m = VecLength(v2)()
    VecBuild(
      n + m,
      TyInt ::+ (i =>
        Mux(i < n, VecAccess(v1, i)(), VecAccess(v2, i - n)())()
      )
    )()
  }
}

object VecZip {
  def apply(
      a: Expr /* Vec<A; n> */,
      b: Expr /* Vec<B; n> */
  ): VecBuild /* Vec<(A, B); n> */ =
    VecBuild(
      VecLength(a)(),
      TyInt ::+ (i => Tuple(VecAccess(a, i)(), VecAccess(b, i)())())
    )()
}

object VecRepeat {
  def apply(
      vec: Expr /* Vec<A; n> */,
      m: Expr
  ): Expr /* Vec<Vec<A; n>, m> */ = {
    VecBuild(m, TyInt ::+ (_ => vec))()
  }
}

object VecReverse {
  def apply(v: Expr /* Vec<A; n> */ ): Expr /* Vec<A; n> */ = {
    val n = VecLength(v)()
    VecBuild(n, TyInt ::+ (i => VecAccess(v, n - i - 1)()))()
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
      TyInt ::+ (i =>
        VecBuild(m, TyInt ::+ (j => VecAccess(vec, i * m + j)()))()
      )
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    newV.typ match {
      case TyVec(TyVec(t, m), n) => this.rebuild(TyVec(t, n * m), Seq(newV))
      case t =>
        throw new TypeError(
          s"Vector in VecJoin has type $t. Expected a nested vector."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val v = this.v.lower()
    val (n, m) = this.v.typ match {
      case TyVec(TyVec(_, m), n) => (n, m)
      case t => throw new TypeError(s"Vector in VecJoin has type $t.")
    }
    VecBuild(
      n * m,
      TyInt ::+ (i => VecAccess(VecAccess(v, i / m)(), i % m)())
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
      n + -m + 1,
      TyInt ::+ (i => VecBuild(m, TyInt ::+ (j => VecAccess(vec, i + j)()))())
    )()
  }
}

object VecTranspose {
  def apply(v: Expr /* Vec<Vec<A; m>; n> */ ): Expr /* Vec<Vec<A; n>; m> */ = {
    val n = VecLength(v)()
    val m = VecLength(VecAccess(v, 0)())()
    VecBuild(
      m,
      TyInt ::+ (i =>
        VecBuild(n, TyInt ::+ (j => VecAccess(VecAccess(v, j)(), i)()))()
      )
    )()
  }
}
