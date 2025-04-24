package operations

import ir._

// TODO: Reimplement these as syntax sugar as well?
object Min {
  def apply(x: Expr /* Int */, y: Expr /* Int */ ): Expr /* Int */ = {
    IfThenElse(x < y, x, y)()
  }
}

object Max {
  def apply(x: Expr /* Int */, y: Expr /* Int */ ): Expr /* Int */ = {
    IfThenElse(x > y, x, y)()
  }
}

object CeilDiv {
  def apply(x: Expr, y: Expr): Expr = {
    val q = Param("q")()
    Let(
      q,
      x / y,
      IfThenElse((x % y !== 0) && ((x < 0) === (y < 0)), q + 1, q)()
    )()
  }
}
