package opt

import ir._
import opt.{PartialEvalPass => PE}

object OptionSimplifier {

  /** Split an expression representing an <code>Option</code> into the
    * <code>data</code> and <code>valid</code> parts and simplify the
    * <code>data</code> part knowing that, if <code>valid = False</code>, then
    * the value of <code>data</code> doesn't matter.
    */
  def simplify(e: Expr): Tuple = {
    val data = PE.partialEval(e.__0)
    val valid = PE.partialEval(e.__1)
    val simplifiedData = if (valid == False) {
      PE.partialEval(Default(data.typ).tchk().lower())
    } else {
      PE.partialEval(data)(FactSet().assumeTrue(valid))
    }
    Tuple(simplifiedData, valid)()
  }
}
