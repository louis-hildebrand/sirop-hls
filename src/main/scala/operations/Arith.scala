package operations

import ir._

/** A function which computes the sum of two values.
  */
object PlusFunction {
  def apply(t: TyAnyInt): Function = t ::+ (x => t ::+ (y => x + y))

  @deprecated
  def apply(): Function = TyInt ::+ (x => TyInt ::+ (y => x + y))
}

/** A function which computes the product of two values.
  */
object TimesFunction {
  def apply(t: TyAnyInt): Function = t ::+ (x => t ::+ (y => x * y))
}

// TODO: Reimplement these as syntax sugar as well?

/** The minimum of two values.
  */
object Min {
  def apply(x: Expr /* Int */, y: Expr /* Int */ ): Expr /* Int */ = {
    Mux(x < y, x, y)()
  }
}

/** The maximum of two values.
  */
object Max {
  def apply(x: Expr /* Int */, y: Expr /* Int */ ): Expr /* Int */ = {
    Mux(x > y, x, y)()
  }
}

/** The ceiling of the quotient of two values.
  */
object CeilDiv {
  def apply(x: Expr, y: Expr): Expr = {
    val q = Param("q")()
    Let(
      q,
      x / y,
      Mux((x % y !== 0) && ((x < 0) === (y < 0)), q + 1, q)()
    )()
  }
}
