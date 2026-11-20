package mhir.gen.vhdl
package transform

import mhir.canonicalize._
import mhir.gen.vhdl.ir._
import mhir.ir._
import mhir.matchers.MapShiftLeft
import mhir.typecheck._

object ClassifyVecAccumulators {

  def apply(s: GenStmBuild): GenStmBuild = {
    GenStmBuild(
      data = s.data,
      valid = s.valid,
      producers = s.producers,
      intermediates = s.intermediates.map({
        case (x, a: Accumulator) =>
          (x, a) match {
            case VecWrite(x, cond, index, value) =>
              x -> VecWriteAccumulator(cond, index, value)
            case VecMapShiftLeft(x, params, lengths, None, next) =>
              // TODO: Handle the case where init != None as well?
              assert(lengths.size == params.size + 1)
              x -> ForGenerateIntermediate(
                params,
                lengths.init.map(n => (n - 1).toInt),
                VecShiftLeftAccumulator(lengths.last, None, next)
              )
            case a => a
          }
        case i => i
      })
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

private object VecMapShiftLeft {

  def unapply(
      arg: (Param, Accumulator)
  ): Option[
    (Param, Seq[Param], Seq[Long], Option[SequentialIntermediate], Expr)
  ] = {
    arg match {
      case (
            x0,
            ExprAccumulator(
              init,
              ExprIntermediate(MapShiftLeft(params, lengths, x1, input))
            )
          ) if x1.forall(_ == x0) =>
        // The parameter for the innermost vector (the one being shifted) doesn't
        // matter and is therefore not returned by MapShiftLeft
        assert(lengths.size == params.size + 1)
        Some((x0, params, lengths, init, input))
      case _ => None
    }
  }
}
