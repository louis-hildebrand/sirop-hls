import scala.language.implicitConversions

package object ir extends OptionType with Eval {
  implicit def int2IntCst(i: Int): IntCst = IntCst(i)

  implicit def bool2BoolExpr(b: Boolean): BoolExpr = if (b) True else False

  implicit def scalaUnaryLambdaToFunction(sl: Expr => Expr): Function = {
    val p = Param("x")
    Function(p, Missing, sl(p))
  }

  implicit def scalaUnaryLambdaToAnnotatedFunction(
      sl: Expr => (Type, Expr)
  ): Function = {
    val x = Param("x")
    val (t, e) = sl(x)
    Function(x, t, e)
  }

  implicit def scalaBinaryLambdaToFunction(
      sl: Expr => Expr => Expr
  ): Function = {
    val p1 = Param("x")
    val p2 = Param("y")
    Function(p1, Missing, Function(p2, Missing, sl(p1)(p2)))
  }
}
