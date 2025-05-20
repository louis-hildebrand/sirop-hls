package operations

import ir._

object PlusFunction {
  def apply(): Function = TyInt ::+ (x => TyInt ::+ (y => x + y))
}

// TODO: Reimplement these as syntax sugar as well?
object Min {
  def apply(x: Expr /* Int */, y: Expr /* Int */ ): Expr /* Int */ = {
    Mux(x < y, x, y)()
  }
}

object Max {
  def apply(x: Expr /* Int */, y: Expr /* Int */ ): Expr /* Int */ = {
    Mux(x > y, x, y)()
  }
}

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
