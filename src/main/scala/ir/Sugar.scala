package ir

case object Let {
  def apply(p: Param, v: Expr, in: Expr): Expr = {
    FunCall(Function(p, Missing, in), v)
  }
}

// Option<T>
trait OptionType {
  val NNone: Expr = Tuple(Default, False)
}
case object SSome {
  def apply(e: Expr /* T */ ): Expr = Tuple(e, True)
}
case object OptionAccess {
  def apply(
      e: Expr /* Option<T> */,
      s: Function /* T -> V */,
      n: Function /* Unit -> V */
  ): Expr /* V */ =
    IfThenElse(
      e.__1,
      s.body.substitute(s.param -> e.__0),
      n.body.substitute(n.param -> Tuple())
    )
}
case object OptionUnwrapUnsafe {
  def apply(e: Expr /* Option<T> */ ): Expr /* T */ = {
    e.__0
  }
}
case object IsNone {
  def apply(e: Expr /* Option<T> */ ): Expr /* Bool */ = {
    Not(e.__1)
  }
}
case object IsSome {
  def apply(e: Expr /* Option<T> */ ): Expr /* Bool */ = {
    e.__1
  }
}
