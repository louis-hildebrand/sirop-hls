object MapV {
  def apply(input: VecBuild, f: Expr => Expr) : VecBuild =
    VecBuild(VecLength(input), (i:Expr) => f(VecAccess(input, i)))
}

object Vec2Stm {
  def apply(v: Expr) : StmBuild =
    StmBuild(VecLength(v), 0, (i:Expr) => Tuple(i + 1, VecAccess(v, i)))
}

object Stm2Vec {
  def apply(s: Expr) : Expr =
    StmFold(s,
      VecBuild(StmLength(s), (i:Expr) => IntCst(0)),
       (e: Expr) => (
         (v: Expr) =>
           VecBuild(VecLength(v), (i:Expr) => IfThenElse(i eq VecLength(v)+ -1, e, VecAccess(v, i+1)))
         )
    )
}

object Stm2VecAlternative {
  def apply(stm: Expr /* Stm<A> */): Expr /* Vec<A> */ = {
    val n = StmLength(stm)
    val v = VecBuild(n, (i: Expr) => IntCst(0))
    val next = Param()
    Iterate(
      n,
      Tuple(0, stm, v),
      (acc: Expr) =>
        Let(next, StmNext(acc.__1),
          Tuple(
            acc.__0 + 1,
            next.__0,
            VecBuild(n, (i: Expr) => IfThenElse(Equal(i, acc.__0), next.__1, VecAccess(acc.__2, i)))))
    ).__2
  }
}

object VecZip {
  def apply(a: Expr /* Vec<A> */, b: Expr /* Vec<B> */): VecBuild /* Vec<(A, B)> */ =
    VecBuild(StmLength(a), (i: Expr) => Tuple(VecAccess(a, i), VecAccess(b, i)))
}
