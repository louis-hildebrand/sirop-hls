package mhir.gen.vhdl
package transform

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._

object ClassifyVecAccumulators {

  def apply(s: GenStmBuild): GenStmBuild = {
    GenStmBuild(
      data = s.data,
      valid = s.valid,
      accumulators = s.accumulators.map({
        case VecWrite(x, cond, index, value) =>
          x -> VecWriteAccumulator(cond, index, value)
        case VecShiftLeft(x, len, init, next) =>
          x -> VecShiftLeftAccumulator(len, init, next)
        case eqn => eqn
      }),
      producers = s.producers,
      intermediates = s.intermediates
    )
  }
}
// TODO: Handle more cases?
private object VecWrite {

  /** A vector accumulator such that at most one element is updated per cycle.
    *
    * @param eqn
    *   the accumulator equation (name, initial value, next expression).
    * @return
    *   `(x, z, cond, i, write)`, where `x` is the accumulator name, `z` is the
    *   initial value, `cond` is the condition for updating the vector, `i` is
    *   the index to update, and `write` is the value to write.
    */
  def unapply(eqn: (Param, Accumulator)): Option[(Param, Expr, Expr, Expr)] = {
    eqn match {
      case (
            v0,
            ExprAccumulator(
              None,
              ExprIntermediate(
                VecBuild(
                  _,
                  Function(
                    i0,
                    Mux(And(terms @ _*), write, VecAccess(v1, i1: Param))
                  )
                )
              )
            )
          ) if v1 == v0 && i1 == i0 =>
        val indexToUpdate = terms.flatMap({
          case Equal(i2: Param, idx)
              if i2 == i0 && !idx.freeVars.contains(i0) =>
            Some(idx)
          case Equal(idx, i2: Param)
              if i2 == i0 && !idx.freeVars.contains(i0) =>
            Some(idx)
          case _ => None
        }) match {
          case Seq(idx) => Some(idx)
          case _        => None
        }
        indexToUpdate match {
          case Some(idx) =>
            val newCond = MaybeAnd(
              terms
                .map(_.subPreserveType(i0 -> idx))
                // Remove the silly index == index term
                .filterNot({
                  case Equal(x, y) if x == y => true
                  case _                     => false
                }): _*
            )().tchk()
            val newWrite = write.subPreserveType(i0 -> idx).tchk()
            Some((v0, newCond, idx, newWrite))
          case None => None
        }
      case _ => None
    }
  }
}

private object VecShiftLeft {

  def unapply(
      arg: (Param, Accumulator)
  ): Option[(Param, Long, Option[DataIntermediate], Expr)] = {
    arg match {
      case (
            x0,
            // TODO: Use special VecBuildAccumulator for this purpose instead?
            ExprAccumulator(
              init,
              ExprIntermediate(
                VecBuild(
                  IntCst(n),
                  Function(
                    i0,
                    Mux(
                      Equal(i1, IntCst(nMinusOne)),
                      e,
                      VecAccess(x1, Sum(IntCst(1), i2))
                    )
                  )
                )
              )
            )
          ) if x1 == x0 && i1 == i0 && i2 == i0 && nMinusOne == n - 1 =>
        Some((x0, n, init, e))
      case (
            x,
            ExprAccumulator(
              init,
              ExprIntermediate(VecBuild(IntCst(1), Function(_, e)))
            )
          ) =>
        Some((x, 1, init, e))
      case _ => None
    }
  }
}
