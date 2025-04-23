package operations

import ir._
import opt.PartialEvalPass

case class VecMap(v: Expr /* Vec<A; n> */, f: Expr /* A -> B */ )(
    val typ: Type = Missing
) /* Vec<B; n> */
    extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(v, f)

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
          case t => throw new TypeError(s"Function of VecMap has type $t.")
        }
      case t => throw new TypeError(s"Vector of VecMap has type $t.")
    }
  }

  override def lower(): Expr = {
    if (this.typ == Missing) {
      VecBuild(VecLength(v)(), (i: Expr) => FunCall(f, VecAccess(v, i)())())()
    } else {
      val (t1, t2) = {
        val t = f.typ.asInstanceOf[TyArrow]
        (t.t1, t.t2)
      }
      // TODO: yuck, let Param take a type annotation as input
      val i = Param("i").rebuild(TyInt).asInstanceOf[Param]
      val g = Function(i, TyInt, FunCall(f, VecAccess(v, i)(t1))(t2))(
        TyArrow(TyInt, t2)
      )
      VecBuild(VecLength(v)(TyInt), g)(this.typ)
    }
  }
}

case class VecFold(
    v: Expr /* Vec<T1; n> */,
    z: Expr /* T2 */,
    f: Expr /* T1 -> T2 -> T2 */
)(val typ: Type = Missing) /* T2 */
    extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(v, z, f)

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, z, f) => VecFold(v, z, f)(typ)
      case _            => throw new BadRebuildError(this, newChildren)
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
    this.rebuild(t2, Seq(newV, newZ, newF))
  }

  override def lower(): Expr = {
    // TODO: Implement this properly (preserving type!) once Iterate() works
    Iterate(
      VecLength(v)(TyInt),
      Tuple(z, 0)(),
      (acc: Expr) =>
        Tuple(
          FunCall(FunCall(f, VecAccess(v, acc.__1)())(), acc.__0)(),
          acc.__1 + 1
        )(),
      zSize = Some(2)
    ).__0.lowerAll()
  }
}

object VecScan {
  def apply(
      vec: Expr /* Vec<A; n> */,
      z: Expr /* B */,
      f: Expr => Expr => Expr /* A -> B -> B */,
      inclusive: Boolean
  ): Expr /* Vec<B; n> */ = {
    val n = VecLength(vec)()
    // TODO: Would it be better to use a second shift register for accessing
    //       the input instead of accessing the input using counter as index?
    Iterate(
      if (inclusive) n else n + -1,
      Tuple(0, VecBuild(n, (i: Expr) => z)())(),
      (acc: Expr) =>
        Tuple(
          acc.__0 + 1,
          VecShiftLeft(
            acc.__1,
            f(VecAccess(vec, acc.__0)())(VecAccess(acc.__1, n + -1)())
          )
        )(),
      zSize = Some(2)
    ).__1
  }
}

object Stm2Vec {
  def apply(
      s: Expr,
      // Ideally we would get this shape info from the type system
      n: Expr
  ): StmBuild =
    StmFold(
      s,
      VecBuild(n, (_: Expr) => Default(???))(),
      (v: Expr) => (e: Expr) => VecShiftLeft(v, e),
      stmShape = Seq(n)
    )
}

object Vec2Tuple {
  def apply(vec: VecBuild): Tuple = {
    val n = PartialEvalPass.partialEval(VecLength(vec)()).asInstanceOf[IntCst].i
    val elems = (0 until n).map(i => FunCall(vec.f, i)())
    Tuple(elems: _*)()
  }
}

case class VecPrepend(v: Expr /* Vec<A; n> */, e: Expr /* A */ )(
    val typ: Type = Missing
) /* Vec<A; n+1> */
    extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(v, e)

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

  override def lower(): Expr = {
    if (this.typ == Missing) {
      VecBuild(
        VecLength(v)() + 1,
        (i: Expr) => IfThenElse(i === 0, e, VecAccess(v, i + -1)())()
      )()
    } else {
      val t = this.v.typ.asInstanceOf[TyVec].t
      val n = this.v.typ.asInstanceOf[TyVec].n
      // TODO: yuck, let Param take a type annotation as input
      val i = Param("i").rebuild(TyInt).asInstanceOf[Param]
      val f =
        Function(i, TyInt, IfThenElse(i === 0, e, VecAccess(v, i - 1)(t))())(
          TyArrow(TyInt, t)
        )
      VecBuild(n + 1, f)(this.typ)
    }
  }
}

case class VecAppend(v: Expr /* Vec<A; n> */, e: Expr /* A */ )(
    val typ: Type = Missing
) /* Vec<A; n+1> */
    extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(v, e)

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

  override def lower(): Expr = {
    if (this.typ == Missing) {
      val n = VecLength(v)()
      VecBuild(
        n + 1,
        (i: Expr) => IfThenElse(i === n, e, VecAccess(v, i)())()
      )()
    } else {
      val t = this.v.typ.asInstanceOf[TyVec].t
      val n = this.v.typ.asInstanceOf[TyVec].n
      // TODO: yuck, let Param take a type annotation as input
      val i = Param("i").rebuild(TyInt).asInstanceOf[Param]
      val f =
        Function(i, TyInt, IfThenElse(i === n, e, VecAccess(v, i)(t))())(
          TyArrow(TyInt, t)
        )
      VecBuild(n + 1, f)(this.typ)
    }
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

    VecBuild(k, (i: Expr) => VecAccess(vec, i)())()
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
    VecBuild(k, (i: Expr) => VecAccess(vec, i + (n - k))())()
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
      (i: Expr) => IfThenElse(i === n + -1, e, VecAccess(vec, i + 1)())()
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
      (i: Expr) => IfThenElse(i === 0, e, VecAccess(vec, i + -1)())()
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
      (i: Expr) =>
        IfThenElse(i < n, VecAccess(v1, i)(), VecAccess(v2, i - n)())()
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
      (i: Expr) => Tuple(VecAccess(a, i)(), VecAccess(b, i)())()
    )()
}

// Not particularly useful, just the Vec counterpart to StmZipAlternating
object VecZipAlternating {
  def apply(
      a: Expr /* Vec<A; n> */,
      b: Expr /* Vec<A; n> */
  ): Expr /* Vec<(A, A); n> */ = {
    VecBuild(
      VecLength(a)(),
      (i: Expr) =>
        IfThenElse(
          (i % 2) === 0,
          Tuple(VecAccess(a, i)(), VecAccess(b, i)())(),
          Tuple(VecAccess(b, i)(), VecAccess(a, i)())()
        )()
    )()
  }
}

object VecRepeat {
  def apply(
      vec: Expr /* Vec<A; n> */,
      m: Expr
  ): Expr /* Vec<Vec<A; n>, m> */ = {
    VecBuild(m, (i: Expr) => vec)()
  }
}

object VecReverse {
  def apply(v: Expr /* Vec<A; n> */ ): Expr /* Vec<A; n> */ = {
    val n = VecLength(v)()
    VecBuild(n, (i: Expr) => VecAccess(v, n - i - 1)())()
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
      (i: Expr) => VecBuild(m, (j: Expr) => VecAccess(vec, i * m + j)())()
    )()
  }
}

object VecJoin {
  def apply(v: Expr /* Vec<Vec<A; m>; n> */ ): Expr /* Vec<A; n * m> */ = {
    val n = VecLength(v)()
    val m = IfThenElse(n === 0, 1, VecLength(VecAccess(v, 0)())())()
    IfThenElse(
      m === 0,
      VecBuild(0, (_: Expr) => Default(???))(),
      VecBuild(n * m, (i: Expr) => VecAccess(VecAccess(v, i / m)(), i % m)())()
    )()
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
      (i: Expr) => VecBuild(m, (j: Expr) => VecAccess(vec, i + j)())()
    )()
  }
}

object VecTranspose {
  def apply(v: Expr /* Vec<Vec<A; m>; n> */ ): Expr /* */ = {
    val n = VecLength(v)()
    val m = VecLength(VecAccess(v, 0)())()
    VecBuild(
      m,
      (i: Expr) => VecBuild(n, (j: Expr) => VecAccess(VecAccess(v, j)(), i)())()
    )()
  }
}
