package mhir

import mhir.ir._

/** The evaluator.
  */
package object eval {

  /** See [[mhir.ir.evaluate.Evaluator.eval]].
    */
  def eval(
      e: Expr,
      handshake: Boolean = true,
      inputs: Map[Param, Expr] = Map(),
      stmData: Map[Param, Option[Expr]] = Map(),
      maxInvalidSteps: Option[Int] = None
  ): Expr = {
    val evaluator =
      Evaluator(handshake = handshake, maxInvalidSteps = maxInvalidSteps)
    evaluator.eval(e, inputs = inputs, stmData = stmData)
  }
}
