package mhir.matchers

import mhir.ir._

// TODO: generalize to non-static lengths
case class ShiftLeftSelf(length: Long, input: Expr)

// TODO: this is very similar to code in mhir.gen (ClassifyVecAccumulators); try to deduplicate this code
object ShiftLeftSelf {

  def unapply(
      acc: (Param, (Expr, Expr, Expr))
  ): Option[(Param, ShiftLeftSelf)] = {
    acc match {
      case (x0, (_, ShiftLeft(len, x1, input), _)) if x1 == x0 =>
        Some(x0 -> ShiftLeftSelf(len, input))
      // vbuild(1) { i => input }
      // TODO: move this to ShiftLeft rather than ShiftLeftSelf? Or just unwrap vectors whose length is 1?
      case (x0, (_, VecBuild(IntCst(1), Function(i, input)), _))
          if !input.freeVars.contains(i) =>
        Some(x0 -> ShiftLeftSelf(1, input))
      case _ => None
    }
  }
}

case class ShiftLeft(length: Long, vec: Expr, input: Expr)

object ShiftLeft {

  def unapply(e: Expr): Option[(Long, Expr, Expr)] = {
    e match {
      // vbuild(n) { i =>
      //   if (i == n - 1) {
      //     input
      //   } else {
      //     vec[1 + i]
      //   }
      // }
      case VecBuild(
            IntCst(n),
            Function(
              i0,
              Mux(
                Equal(i1, IntCst(nMinusOne)),
                input,
                VecAccess(vec, Sum(IntCst(1), i2))
              )
            )
          ) if i1 == i0 && i2 == i0 && nMinusOne == n - 1 =>
        Some((n, vec, input))
      // vbuild(n) { i =>
      //   (i != n - 1 && vec[1 + i]) || (i == n - 1 && input)
      // }
      case VecBuild(
            IntCst(n),
            Function(
              i0,
              Or(
                And(
                  Not(Equal(i1, IntCst(nMinusOne))),
                  VecAccess(vec, Sum(IntCst(1), i2))
                ),
                And(Equal(i3, IntCst(nMinusOneAgain)), input)
              )
            )
          )
          if i1 == i0
            && i2 == i0
            && i3 == i0
            && nMinusOne == n - 1
            && nMinusOneAgain == n - 1 =>
        Some((n, vec, input))
      case _ => None
    }
  }
}
