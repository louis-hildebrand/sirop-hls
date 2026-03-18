package mhir.ir

/** Extra information attached to a [[mhir.ir.StmBuild]].
  */
sealed trait StmBuildAnnotation

/** A name for an [[mhir.ir.StmBuild]] to help with debugging.
  */
case class NameAnnotation(name: String) extends StmBuildAnnotation

/** This [[mhir.ir.StmBuild]] will not read any inputs after producing its last
  * output.
  *
  * In other words, once the last output has been produced, it is guaranteed
  * that all input streams have been exhausted as well (possibly in the same
  * step).
  *
  * @example
  *   `StmReduce` satisfies this condition, since it will not produce its output
  *   before reaching the end of the input stream.
  * @example
  *   `StmPrefix` does <i>not</i> satisfy this condition, since it may produce
  *   its last output before reaching the end of the input stream.
  */
object NoInputsAfterLastOut extends StmBuildAnnotation

/** This [[mhir.ir.StmBuild]] will not produce any outputs after reading its
  * last input.
  *
  * In other words, once the last input is read, it is guaranteed that the last
  * output has been produced (possibly in the same step).
  */
object NoOutputsAfterLastIn extends StmBuildAnnotation

/** This [[mhir.ir.StmBuild]] will never produce more than its current number of
  * outputs, even if you increase its length and give it a longer stream.
  *
  * @example
  *   If you implement `StmPrefix` as
  *   {{{
  *   sbuild(5)(sdata(p), true) {} { (p: Stm[u8, 10]) = { stm: s, ready: true } }
  *   }}}
  *   then it does not satisfy the condition.
  *
  * However, if you add a counter and change the valid expression to something
  * like `counter < 5`, then it will satisfy the condition.
  */
object SelfControlledOutputs extends StmBuildAnnotation
