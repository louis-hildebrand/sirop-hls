package mhir.matchers

import mhir.canonicalize.standardCanonicalizer
import mhir.ir._
import mhir.typecheck.TypeCheck

// TODO: generalize to non-static lengths
case class ShiftLeftSelf(length: Long, input: Expr)

object ShiftLeftSelf {

  def unapply(
      acc: (Param, (Expr, Expr, Expr))
  ): Option[(Param, ShiftLeftSelf)] = {
    acc match {
      case (x0, (_, ShiftLeft(len, x1, input), _)) if x1.forall(_ == x0) =>
        Some(x0 -> ShiftLeftSelf(len, input))
      case _ => None
    }
  }
}

object MapShiftLeft {

  def unapply(e: Expr): Option[(Seq[Param], Long, Option[Expr], Expr)] = {
    e match {
      case ShiftLeft(n, vec, input) =>
        Some((List.empty, n, vec, input))
      case VecBuild(
            _,
            Function(i0, MapShiftLeft(params, len, vec, input))
          ) =>
        vec match {
          case None => Some((i0 +: params, len, None, input))
          case Some(VecAccess(vec, i1))
              if i1 == i0 && !vec.freeVars.contains(i0) =>
            Some((i0 +: params, len, Some(vec), input))
          case _ => None
        }
      case _ => None
    }
  }
}

object ShiftLeft {

  def unapply(e: Expr): Option[(Long, Option[Expr], Expr)] = {
    e match {
      // vbuild(n) { i =>
      //   if (i == n - 1) then {
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
          )
          if i1 == i0 && i2 == i0 && nMinusOne == n - 1 &&
            !vec.freeVars.contains(i0) =>
        Some((n, Some(vec), input))
      // vbuild(n) { i =>
      //   truncate(
      //     if (i == n - 1) then {
      //       input
      //     } else {
      //       pad(vec[1 + i])
      //   )
      // }
      case VecBuild(
            IntCst(n),
            Function(
              i0,
              TruncateTo(
                Mux(
                  Equal(i1, IntCst(nMinusOne)),
                  input,
                  PadTo(VecAccess(vec, Sum(IntCst(1), i2)), _)
                ),
                truncWidth
              )
            )
          )
          if i1 == i0 && i2 == i0 && nMinusOne == n - 1 &&
            !vec.freeVars.contains(i0) =>
        Some((n, Some(vec), TruncateTo(input, truncWidth)().tchk()))
      // vbuild(n) { i =>
      //   unsign(
      //     if (i == n - 1) then {
      //       input
      //     } else {
      //       sign(vec[1 + i])
      //   )
      // }
      case VecBuild(
            IntCst(n),
            Function(
              i0,
              ToUnsigned(
                Mux(
                  Equal(i1, IntCst(nMinusOne)),
                  input,
                  ToSigned(VecAccess(vec, Sum(IntCst(1), i2)))
                )
              )
            )
          )
          if i1 == i0 && i2 == i0 && nMinusOne == n - 1 &&
            !vec.freeVars.contains(i0) =>
        Some((n, Some(vec), ToUnsigned(input)().tchk()))
      // vbuild(n) { i =>
      //   unsign(truncate(
      //     if (i == n - 1) then {
      //       input
      //     } else {
      //       sign(pad(vec[1 + i]))
      //   ))
      // }
      case VecBuild(
            IntCst(n),
            Function(
              i0,
              ToUnsigned(
                TruncateTo(
                  Mux(
                    Equal(i1, IntCst(nMinusOne)),
                    input,
                    PadTo(ToSigned(VecAccess(vec, Sum(IntCst(1), i2))), _)
                  ),
                  truncWidth
                )
              )
            )
          )
          if i1 == i0 && i2 == i0 && nMinusOne == n - 1 &&
            !vec.freeVars.contains(i0) =>
        Some(
          (n, Some(vec), ToUnsigned(TruncateTo(input, truncWidth)())().tchk())
        )
      // vbuild(n) { _ => input }
      case VecBuild(IntCst(1), Function(i, input)) =>
        val inputWithoutI = input.subPreserveType(i -> C(0)(i.typ))
        Some((1, None, inputWithoutI))
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
            && nMinusOneAgain == n - 1
            && !vec.freeVars.contains(i0) =>
        Some((n, Some(vec), input))
      case _ => None
    }
  }
}
