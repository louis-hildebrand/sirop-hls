import scala.language.implicitConversions

package object ir extends Eval {
  implicit def int2IntCst(i: Int): IntCst = IntCst(i)

  implicit def bool2BoolExpr(b: Boolean): BoolExpr = if (b) True else False
}
