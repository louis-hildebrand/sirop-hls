package operations

import ir.*

object Min {
  def apply(x: Expr /* Int */, y: Expr /* Int */ ): Expr /* Int */ = {
    IfThenElse(x < y, x, y)
  }
}

object Max {
  def apply(x: Expr /* Int */, y: Expr /* Int */ ): Expr /* Int */ = {
    IfThenElse(x > y, x, y)
  }
}

object CeilDiv {
  def apply(x: Expr, y: Expr): Expr = {
    IfThenElse(x % y === 0, x / y, x / y + 1)
  }
}
