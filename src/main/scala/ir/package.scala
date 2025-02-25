import scala.language.implicitConversions

package object ir extends OptionType with Eval {
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
          case f: Function =>
            // "Rename" to avoid variable capture
            val newParam = Param(f.param.prefix)
            Function(
              newParam,
              substitute(f.body)(substitutions + ((f.param, newParam)))
            )
          case e => e.rebuild(e.children.map(e => substitute(e)))
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
