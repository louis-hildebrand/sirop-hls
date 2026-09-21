package mhir.matchers

import mhir.ir._

// TODO: generalize to non-static lengths
case class ShiftLeft(length: Long, input: Expr)

// TODO: this is very similar to code in mhir.gen (ClassifyVecAccumulators); try to deduplicate this code
object ShiftLeft {

  def unapply(acc: (Param, (Expr, Expr, Expr))): Option[(Param, ShiftLeft)] = {
    acc match {
      case (
            x0,
            (
              _,
              VecBuild(
                IntCst(n),
                Function(
                  i0,
                  Mux(
                    Equal(i1, IntCst(nMinusOne)),
                    input,
                    VecAccess(x1, Sum(IntCst(1), i2))
                  )
                )
              ),
              _
            )
          ) if i1 == i0 && i2 == i0 && x1 == x0 && nMinusOne == n - 1 =>
        Some(x0 -> ShiftLeft(n, input))
      case (x, (_, VecBuild(IntCst(1), Function(i, input)), _))
          if !input.freeVars.contains(i) =>
        Some(x -> ShiftLeft(1, input))
      case (
            x0,
            (
              _,
              VecBuild(
                IntCst(n),
                Function(
                  i0,
                  Or(
                    And(
                      Not(Equal(i1, IntCst(nMinusOne))),
                      VecAccess(x1, Sum(IntCst(1), i2))
                    ),
                    And(Equal(i3, IntCst(nMinusOneAgain)), input)
                  )
                )
              ),
              _
            )
          )
          if i1 == i0
            && i2 == i0
            && i3 == i0
            && x1 == x0
            && nMinusOne == n - 1
            && nMinusOneAgain == n - 1 =>
        Some(x0 -> ShiftLeft(n, input))
      case _ => None
    }
  }
}
