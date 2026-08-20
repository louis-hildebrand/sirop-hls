package mhir.eval
package handshake

import mhir.ir._

/** The parts of a stream-producing component which do not change at runtime.
  *
  * @param data
  *   an expression for this node's output.
  * @param valid
  *   an expression indicating whether the data is valid.
  * @param nextByAccumulator
  *   for each data accumulator, an expression for the next value.
  * @param readyByInput
  *   for each input stream, an expression saying whether this node will consume
  *   that input.
  * @param typ
  *   The stream type.
  */
private[handshake] case class StmNodeHardware(
    data: Expr,
    valid: Expr,
    inputs: Map[Param, StmNodeId],
    nextByAccumulator: Map[Param, Expr],
    readyByInput: Map[Param, Expr],
    typ: TyStm
)
