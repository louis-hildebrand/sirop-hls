object VecMap {
  def apply(input: VecBuild, f: Expr => Expr): VecBuild =
    VecBuild(VecLength(input), (i: Expr) => f(VecAccess(input, i)))
}

object VecFold {
  def apply(
      vec: Expr /* Vec<A> */,
      z: Expr /*B*/,
      f: Function /*A -> B -> B*/
  ): Expr /* B */ = {
    Iterate(
      VecLength(vec),
      Tuple(z, 0),
      (acc: Expr) =>
        Tuple(
          FunCall(FunCall(f, VecAccess(vec, acc.__1)), acc.__0),
          acc.__1 + 1
        ),
      // TODO: this assumes `z` in `VecFold` is not a tuple (which happens to be the case in all tests so far)
      zSize = Some(2)
    ).__0
  }
}

object VecScan {
  def apply(
      vec: Expr /* Vec<A; n> */,
      z: Expr /* B */,
      f: Expr => Expr => Expr /* A -> B -> B */,
      inclusive: Boolean
  ): Expr /* Vec<B; n> */ = {
    val n = VecLength(vec)
    // TODO: Would it be better to use a second shift register for accessing
    // the input instead of accessing the input using counter as index?
    Iterate(
      if inclusive then n else n + -1,
      Tuple(0, VecBuild(n, (i: Expr) => z)),
      (acc: Expr) =>
        Tuple(
          acc.__0 + 1,
          VecShiftLeft(
            acc.__1,
            f(VecAccess(vec, acc.__0))(VecAccess(acc.__1, n + -1))
          )
        ),
      zSize = Some(2)
    ).__1
  }
}

object Stm2Vec {
  def apply(
      s: Expr,
      // Ideally we would get this shape info from the type system
      n: Int
  ): Expr =
    StmFold(
      s,
      VecBuild(n, (i: Expr) => IntCst(0)),
      (v: Expr) => (e: Expr) => VecShiftLeft(v, e),
      stmShape = Seq(n)
    )
}

object Vec2Tuple {
  def apply(vec: VecBuild): Tuple = {
    val n = ExprEvaluator.partialEval(VecLength(vec)).asInstanceOf[IntCst].i
    val elems = (0 until n).map(i => FunCall(vec.f, i))
    Tuple(elems: _*)
  }
}

object VecPrepend {
  def apply(
      vec: Expr /* Vec<A; n> */,
      e: Expr /* A */
  ): Expr /* Vec<A; n + 1> */ = {
    val n = VecLength(vec)
    VecBuild(
      n + 1,
      (i: Expr) => IfThenElse(i === 0, e, VecAccess(vec, i + -1))
    )
  }
}

object VecAppend {
  def apply(
      vec: Expr /* Vec<A; n> */,
      e: Expr /* A */
  ): Expr /* Vec<A; n + 1> */ = {
    val n = VecLength(vec)
    VecBuild(
      n + 1,
      (i: Expr) => IfThenElse(i === n, e, VecAccess(vec, i))
    )
  }
}

object VecPrefix {
  def apply(
      vec: Expr /* Vec<A; n> */,
      k: Expr /* Int */
  ): Expr /* Vec<A; k> */ = {
    val nVal = ExprEvaluator.partialEval(VecLength(vec)).asInstanceOf[IntCst].i
    val kVal = ExprEvaluator.partialEval(k).asInstanceOf[IntCst].i
    require(kVal >= 0)
    require(kVal <= nVal)

    VecBuild(k, (i: Expr) => VecAccess(vec, i))
  }
}

object VecSuffix {
  def apply(
      vec: Expr /* Vec<A; n> */,
      k: Expr /* Int */
  ): Expr /* Vec<A; k> */ = {
    val nVal = ExprEvaluator.partialEval(VecLength(vec)).asInstanceOf[IntCst].i
    val kVal = ExprEvaluator.partialEval(k).asInstanceOf[IntCst].i
    require(kVal >= 0)
    require(kVal <= nVal)

    val n = VecLength(vec)
    VecBuild(k, (i: Expr) => VecAccess(vec, i + (n - k)))
  }
}

object VecShiftLeft {
  def apply(
      vec: Expr /* Vec<A; n> */,
      e: Expr /* A */
  ): Expr /* Vec<A; n> */ = {
    val n = VecLength(vec)
    VecBuild(
      n,
      (i: Expr) => IfThenElse(i === n + -1, e, VecAccess(vec, i + 1))
    )
  }
}

object VecShiftRight {
  def apply(
      vec: Expr /* Vec<A; n> */,
      e: Expr /* A */
  ): Expr /* Vec<A; n> */ = {
    val n = VecLength(vec)
    VecBuild(
      n,
      (i: Expr) => IfThenElse(i === 0, e, VecAccess(vec, i + -1))
    )
  }
}

object VecConcat {
  def apply(
      v1: Expr /* Vec<A; n> */,
      v2: Expr /* Vec<A; m> */
  ): Expr /* Vec<A; n+m> */ = {
    val n = VecLength(v1)
    val m = VecLength(v2)
    VecBuild(
      n + m,
      (i: Expr) => IfThenElse(i < n, VecAccess(v1, i), VecAccess(v2, i - n))
    )
  }
}

object VecZip {
  def apply(
      a: Expr /* Vec<A; n> */,
      b: Expr /* Vec<B; n> */
  ): VecBuild /* Vec<(A, B); n> */ =
    VecBuild(VecLength(a), (i: Expr) => Tuple(VecAccess(a, i), VecAccess(b, i)))
}

// Not particularly useful, just the Vec counterpart to StmZipAlternating
object VecZipAlternating {
  def apply(
      a: Expr /* Vec<A; n> */,
      b: Expr /* Vec<A; n> */
  ): Expr /* Vec<(A, A); n> */ = {
    VecBuild(
      VecLength(a),
      (i: Expr) =>
        IfThenElse(
          (i % 2) === 0,
          Tuple(VecAccess(a, i), VecAccess(b, i)),
          Tuple(VecAccess(b, i), VecAccess(a, i))
        )
    )
  }
}

object VecRepeat {
  def apply(vec: Expr /* Vec<A; n> */, m: Int): Expr /* Vec<Vec<A; n>, m> */ = {
    VecBuild(m, (i: Expr) => vec)
  }
}

object VecSplit {
  def apply(
      vec: Expr /* Vec<A; n> */,
      m: Int
  ): VecBuild /* Vec<Vec<A; m>; n/m> */ = {
    val n = VecLength(vec)
    // n must be divisible by m
    VecBuild(
      n / m,
      (i: Expr) => VecBuild(m, (j: Expr) => VecAccess(vec, i * m + j))
    )
  }
}

object VecJoin {
  def apply(v: Expr /* Vec<Vec<A; m>; n> */ ): VecBuild /* Vec<A; n * m> */ = {
    val n = VecLength(v)
    val m = IfThenElse(Equal(n, 0), 1, VecLength(VecAccess(v, 0)))
    VecBuild(n * m, (i: Expr) => VecAccess(VecAccess(v, i / m), i % m))
  }
}

object VecSlide {
  def apply(
      vec: Expr /* Vec<A; n> */,
      m: Int
  ): Expr /* Vec<Vec<A, m>, n-m+1> */ = {
    val n = VecLength(vec)
    VecBuild(
      n + -m + 1,
      (i: Expr) => VecBuild(m, (j: Expr) => VecAccess(vec, i + j))
    )
  }
}

object VecTranspose {
  def apply(v: Expr /* Vec<Vec<A; m>; n> */ ): Expr /* */ = {
    val n = VecLength(v)
    val m = VecLength(VecAccess(v, 0))
    VecBuild(
      m,
      (i: Expr) => VecBuild(n, (j: Expr) => VecAccess(VecAccess(v, j), i))
    )
  }
}
