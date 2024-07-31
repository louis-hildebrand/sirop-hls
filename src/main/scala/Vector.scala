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
      (acc: Expr) => {
        Tuple(
          FunCall(FunCall(f, VecAccess(vec, acc.__1)), acc.__0),
          acc.__1 + 1
        )
      }
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
    Iterate(
      if inclusive then n else n + -1,
      Tuple(0, VecBuild(n, (i: Expr) => z)),
      (acc: Expr) =>
        Tuple(
          acc.__0 + 1,
          VecBuild(
            n,
            // Like a shift register, but one element is computed using f
            (i: Expr) =>
              IfThenElse(
                i eq n + -1,
                f(VecAccess(vec, acc.__0))(VecAccess(acc.__1, n + -1)),
                VecAccess(acc.__1, i + 1)
              )
          )
        )
    ).__1
  }
}

object Stm2Vec {
  def apply(s: Expr): Expr =
    StmFold(
      s,
      VecBuild(StmLength(s), (i: Expr) => IntCst(0)),
      (e: Expr) =>
        (
            (v: Expr) =>
              VecBuild(
                VecLength(v),
                (i: Expr) =>
                  IfThenElse(i eq VecLength(v) + -1, e, VecAccess(v, i + 1))
              )
        )
    )
}

object Stm2VecAlternative {
  def apply(stm: Expr /* Stm<A> */ ): Expr /* Vec<A> */ = {
    val n = StmLength(stm)
    val v = VecBuild(n, (i: Expr) => IntCst(0))
    val next = Param()
    Iterate(
      n,
      Tuple(0, stm, v),
      (acc: Expr) =>
        Let(
          next,
          StmNext(acc.__1),
          Tuple(
            acc.__0 + 1,
            next.__0,
            VecBuild(
              n,
              (i: Expr) =>
                IfThenElse(Equal(i, acc.__0), next.__1, VecAccess(acc.__2, i))
            )
          )
        )
    ).__2
  }
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
      (i: Expr) => IfThenElse(i eq 0, e, VecAccess(vec, i + -1))
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
      (i: Expr) => IfThenElse(i eq n, e, VecAccess(vec, i))
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
      (i: Expr) => IfThenElse(i lt n, VecAccess(v1, i), VecAccess(v2, i - n))
    )
  }
}

object VecZip {
  def apply(
      a: Expr /* Vec<A> */,
      b: Expr /* Vec<B> */
  ): VecBuild /* Vec<(A, B)> */ =
    VecBuild(VecLength(a), (i: Expr) => Tuple(VecAccess(a, i), VecAccess(b, i)))
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
