package mhir.eval
package nohandshake

import mhir.ir._

/** The parts of a stream-producing component which do not change at runtime.
  *
  * @param data
  *   an expression for this node's output.
  * @param nextByAccumulator
  *   for each data accumulator, an expression for the next value.
  * @param typ
  *   The stream type.
  */
private[nohandshake] case class StmNodeHardware(
    id: StmNodeId,
    data: Expr,
    inputs: Map[Param, StmNodeId],
    nextByAccumulator: Map[Param, Expr],
    absoluteDelayToFirst: Int,
    absoluteDelayToLast: Int,
    absoluteDelayByAccumulator: Map[Param, Int],
    typ: TyStm,
    loc: StmNodeLocation
)
