package ir

case object Let {
  def apply(p: Param, v: Expr, in: Expr): Expr = {
    FunCall(Function(p, in), v)
  }
}

// Option<T>
trait OptionType {
  val NNone: Expr = Tuple(DontCare, False)
}
case object SSome {
  def apply(e: Expr /* T */ ): Expr = Tuple(e, True)
}
case object OptionAccess {
  def apply(
      e: Expr /* Option<T> */,
      s: Expr /* T -> V */,
      n: Expr /* V */
  ): Expr /* V */ = IfThenElse(e.__1, FunCall(s, e.__0), FunCall(n, Tuple()))
}
case object IsNone {
  def apply(e: Expr /* Option<T> */ ): Expr /* Bool */ = {
    Not(e.__1)
  }
}
