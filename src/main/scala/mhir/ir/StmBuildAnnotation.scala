package mhir.ir

/** Extra information attached to a [[mhir.ir.StmBuild]].
  */
sealed trait StmBuildAnnotation

case class NameAnnotation(name: String) extends StmBuildAnnotation

/** This [[mhir.ir.StmBuild]] will not read any inputs after producing its last
  * output.
  *
  * In other words, once the last output has been produced, it is guaranteed
  * that all input streams have been exhausted as well.
  *
  * @example
  *   `StmReduce` satisfies this condition, since it will not produce its output
  *   before reaching the end of the input stream.
  * @example
  *   `StmPrefix` does <i>not</i> satisfy this condition, since it may produce
  *   its last output before reaching the end of the input stream.
  */
object NoInputsAfterLastOut extends StmBuildAnnotation
