
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

