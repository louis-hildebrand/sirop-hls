package mhir.gen.vhdl

import mhir.ir._
import mhir.typecheck._
import mhir.canonicalize._

// TODO: Handle more cases?
private[vhdl] object SingleWriteVector {

  /** A vector accumulator such that at most one element is updated per cycle.
    *
    * @param eqn
    *   the accumulator equation (name, initial value, next expression).
    * @return
    *   `(x, z, cond, i, write)`, where `x` is the accumulator name, `z` is the
    *   initial value, `cond` is the condition for updating the vector, `i` is
    *   the index to update, and `write` is the value to write.
    */
  def unapply(
      eqn: (Param, (Expr, Expr))
  ): Option[(Param, Expr, Expr, Expr, Expr)] = {
    eqn match {
      case (
            v0,
            (
              z: Undefined,
              VecBuild(
                _,
                Function(
                  i0,
                  Mux(And(terms @ _*), write, VecAccess(v1, i1: Param))
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
            val newCond =
              MaybeAnd(terms: _*)().tchk().subPreserveType(i0 -> idx).tchk()
            val newWrite = write.subPreserveType(i0 -> idx).tchk()
            Some((v0, z, newCond, idx, newWrite))
          case None => None
        }
      case _ => None
    }
  }
}
