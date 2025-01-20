import scala.language.implicitConversions

package object ir {
  def contains(e1: Expr, e2: Expr): Boolean = {
    e1 match {
      case _ if e1 == e2 => true
      case e             => e.children.exists(c => contains(c, e2))
    }
  }

  def substitute(
      e: Expr
  )(implicit substitutions: Map[Expr, Expr]): Expr = {
    substitutions.get(e) match {
      case Some(v) => v
      case None =>
        e match {
          case t: Tuple => Tuple(t.elems.map(substitute(_)): _*)
          case TupleAccess(t: Expr, i: Expr) =>
            TupleAccess(substitute(t), substitute(i))

          case p: Param => p
          case f: Function =>
            val newParam = Param(f.param.prefix)
            Function(
              newParam,
              substitute(f.body)(substitutions + ((f.param, newParam)))
            )
          // When substituting the body, this might be come a new function if anything is substituted, therefore, we create a new Param
          case FunCall(f: Expr, arg: Expr) =>
            FunCall(substitute(f), substitute(arg))

          case Sum(terms)              => Sum(terms.map(e => substitute(e)))
          case Prod(factors)           => Prod(factors.map(e => substitute(e)))
          case Div(e1: Expr, e2: Expr) => Div(substitute(e1), substitute(e2))
          case Mod(e1: Expr, e2: Expr) => Mod(substitute(e1), substitute(e2))
          case IntCst(_)               => e

          case True  => True
          case False => False
          case IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) =>
            IfThenElse(substitute(cond), substitute(trueE), substitute(falseE))
          case NotEqual(e1: Expr, e2: Expr) =>
            NotEqual(substitute(e1), substitute(e2))
          case Equal(e1: Expr, e2: Expr) =>
            Equal(substitute(e1), substitute(e2))
          case LessThan(e1: Expr, e2: Expr) =>
            LessThan(substitute(e1), substitute(e2))
          case And(e1: Expr, e2: Expr) => And(substitute(e1), substitute(e2))
          case Or(e1: Expr, e2: Expr)  => Or(substitute(e1), substitute(e2))
          case Not(e: Expr)            => Not(substitute(e))

          case DontCare => DontCare

          case StmBuild(lengths, seed, f) =>
            StmBuild(
              substitute(lengths),
              substitute(seed),
              substitute(f).asInstanceOf[Function]
            )
          case StmLength(s)     => StmLength(substitute(s))
          case StmNext(e: Expr) => StmNext(substitute(e))

          case VecBuild(len: Expr, f) =>
            VecBuild(substitute(len), substitute(f))
          case VecAccess(vec: Expr, i: Expr) =>
            VecAccess(substitute(vec), substitute(i))
          case VecLength(vec: Expr) => VecLength(substitute(vec))
        }
    }
  }

  implicit def int2IntCst(i: Int): IntCst = IntCst(i)

  implicit def bool2BoolExpr(b: Boolean): BoolExpr = if (b) True else False

  implicit def scalaUnaryLambdaToFunction(sl: Expr => Expr): Function = {
    val p = Param("x")
    Function(p, sl(p))
  }

  implicit def scalaBinaryLambdaToFunction(
      sl: Expr => Expr => Expr
  ): Function = {
    val p1 = Param("x")
    val p2 = Param("y")
    Function(p1, Function(p2, sl(p1)(p2)))
  }
}
