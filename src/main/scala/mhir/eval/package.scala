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
      maxInvalidSteps: Option[Int] = None,
      suppressWarnings: Boolean = false
  ): Expr = {
    val evaluator = Evaluator(
      handshake = handshake,
      maxInvalidSteps = maxInvalidSteps,
      suppressWarnings = suppressWarnings
    )
    evaluator.eval(e, inputs = inputs, stmData = stmData)
  }
}
