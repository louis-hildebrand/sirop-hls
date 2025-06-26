package mhir.ir
package evaluate

/** The result of evaluation along with warnings for the undefined behaviour
  * that affects the result, if any.
  *
  * @param e
  *   the result of evaluation.
  * @param warnings
  *   undefined behaviours that affect the result.
  */
case class Value(e: Expr, warnings: Set[EvalWarning]) {
  def addWarnings(ws: Set[EvalWarning]): Value = {
    Value(this.e, this.warnings ++ ws)
  }
}
