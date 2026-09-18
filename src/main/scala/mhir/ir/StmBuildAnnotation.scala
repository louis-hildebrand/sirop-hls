package mhir.ir

/** Extra information attached to a [[mhir.ir.StmBuild]].
  */
sealed trait StmBuildAnnotation {

  def map(f: Expr => Expr): StmBuildAnnotation
}

/** A name for an [[mhir.ir.StmBuild]] to help with debugging.
  */
case class NameAnnotation(name: String) extends StmBuildAnnotation {

  def map(f: Expr => Expr): NameAnnotation = this
}

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
  *   `StmTake` does <i>not</i> satisfy this condition, since it may produce its
  *   last output before reaching the end of the input stream.
  */
object NoInputsAfterLastOut extends StmBuildAnnotation {

  def map(f: Expr => Expr): NoInputsAfterLastOut.type = this
}

/** This [[mhir.ir.StmBuild]] will not produce any outputs after reading its
  * last input.
  *
  * In other words, once the last input is read, it is guaranteed that the last
  * output has been produced (possibly in the same step).
  */
object NoOutputsAfterLastIn extends StmBuildAnnotation {

  def map(f: Expr => Expr): NoOutputsAfterLastIn.type = this
}

/** This [[mhir.ir.StmBuild]] will never produce more than its current number of
  * outputs, even if you increase its length and give it a longer stream.
  *
  * @example
  *   If you implement `StmTake` as
  *   {{{
  *   sbuild(5)(sdata(p), true) {} { (p: Stm[u8, 10]) = { stm: s, ready: true } }
  *   }}}
  *   then it does not satisfy the condition.
  *
  * However, if you add a counter and change the valid expression to something
  * like `counter < 5`, then it will satisfy the condition.
  */
object SelfControlledOutputs extends StmBuildAnnotation {

  def map(f: Expr => Expr): SelfControlledOutputs.type = this
}

/** The given [[sink]] expression should be considered "used" for the purpose of
  * transformations like shift register shrinking
  * ([[mhir.optimize.ShiftRegisterShrinker]]).
  *
  * This can be used to limit how much the optimizer will shrink certain shift
  * registers if you want them to be available for DSP register packing (e.g.,
  * in [[mhir.gen.vhdl.agilex7.DspSelection]]).
  */
// TODO: warning if this has free variables?
// TODO: warning if there are more than one of these?
case class SinkAnnotation(sink: Expr) extends StmBuildAnnotation {

  def map(f: Expr => Expr): SinkAnnotation = SinkAnnotation(f(this.sink))
}
