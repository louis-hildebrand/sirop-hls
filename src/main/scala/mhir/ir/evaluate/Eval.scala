package mhir.ir
package evaluate

import scala.language.{existentials, implicitConversions}

/** The evaluator.
  */
trait Eval {

  /** See [[mhir.ir.evaluate.Evaluator.eval]].
    */
  def eval(
      e: Expr,
      stmData: Map[Param, Option[Expr]] = Map(),
      maxInvalidSteps: Option[Int] = None,
      suppressWarnings: Boolean = false
  ): Expr = {
    val evaluator = Evaluator(
      maxInvalidSteps = maxInvalidSteps,
      suppressWarnings = suppressWarnings
    )
    evaluator.eval(e, stmData = stmData)
  }
}
