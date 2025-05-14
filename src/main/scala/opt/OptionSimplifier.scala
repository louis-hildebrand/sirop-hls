package opt

import ir._

object OptionSimplifier {

  /** Split an expression representing an <code>Option</code> into the
    * <code>data</code> and <code>valid</code> parts and simplify the
    * <code>data</code> part knowing that, if <code>valid = False</code>, then
    * the value of <code>data</code> doesn't matter.
    */
  def simplify(e: Expr): Tuple = {
    val data = PartialEvalPass.partialEval(e.__0)
    val valid = PartialEvalPass.partialEval(e.__1)
    val simplifiedData =
      PartialEvalPass.partialEval(data)(FactSet().assumeTrue(valid))
    Tuple(simplifiedData, valid)()
  }
}
