object MapV {
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

object Vec2Stm {
  def apply(v: Expr): StmBuild =
    StmBuild(VecLength(v), 0, (i: Expr) => Tuple(i + 1, VecAccess(v, i)))
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
