package opt

import ir._

import scala.annotation.tailrec

object StmSimplifier {

  /** Perform common-sense simplifications that should pretty much always be
    * beneficial, like partially evaluating and removing unused accumulator
    * elements.
    */
  def simplify(s: StmBuild)(facts: FactSet = FactSet()): StmBuild = {
    simplifyUntilFixpoint(simplifyInputs(s)(facts))(facts)
  }

  private def simplifyInputs(s: StmBuild)(facts: FactSet): StmBuild = {
    val newEquations = s.equations.map({
      case (x, (producer: StmBuild, next)) =>
        x -> (simplify(producer)(facts), next)
      case eqn => eqn
    })
    StmBuild(s.n, s.data, s.valid, newEquations)()
  }

  @tailrec
  private def simplifyUntilFixpoint(s: StmBuild)(facts: FactSet): StmBuild = {
    val simplified = {
      val s1 = tl(PartialEvalPass.partialEval(s)(facts))
      val s2 = tl(StmAccRemovalPass.removeUnusedVars(s1))
      val s3 = tl(StmAccRemovalPass.removeConstantVars(s2))
      val s4 = tl(StmAccRemovalPass.deduplicateVars(s3))
      s4
    }
    if (simplified == s) {
      simplified
    } else {
      // New partial evaluation opportunities may have been revealed by
      // inlining constant-valued accumulator elements
      simplifyUntilFixpoint(simplified)(facts)
    }
  }

  private def tl(s: Expr): StmBuild =
    s.tchk().lower().asInstanceOf[StmBuild]
}
