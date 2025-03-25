package opt

import ir._

import scala.annotation.tailrec

object StmSimplifier {

  /** Perform common-sense simplifications that should pretty much always be
    * beneficial, like partially evaluating and removing unused accumulator
    * elements.
    */
  @tailrec
  def simplify(s: StmBuild)(facts: FactSet = FactSet()): StmBuild = {
    val simplified = {
      val s1 = PartialEvalPass.partialEval(s)(facts).asInstanceOf[StmBuild]
      val s2 = StmAccRemovalPass.removeUnusedElems(s1)
      val s3 = StmAccRemovalPass.removeConstantVars(s2)
      s3
    }
    if (simplified == s) {
      simplified
    } else {
      // New partial evaluation opportunities may have been revealed by
      // inlining constant-valued accumulator elements
      simplify(simplified)(facts)
    }
  }
}
