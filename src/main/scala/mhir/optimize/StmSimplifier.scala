package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir.Lowering.ExprLowering
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.logging.time
import org.slf4j.event.Level

import scala.annotation.tailrec

/** Transformation that combines many different simplifications.
  *
  * Simplification should make the expression easier to analyze. In some cases
  * it may make the hardware a bit worse (e.g., by distributing a product over a
  * sum).
  */
object StmSimplifier {

  private implicit val logger: Logger = Logger(getClass.getName)

  /** Perform common-sense simplifications that should pretty much always be
    * beneficial, like partially evaluating and removing unused accumulator
    * elements.
    */
  def simplify(stm: StmBuild)(facts: FactSet = FactSet()): StmBuild = {
    logger.trace(s"simplifying stream: $stm")
    time("simplifying stream", Level.TRACE) {
      val s0 = simplifyInputs(tl(stm))(facts)
      val s1 = simplifyUntilFixpoint(s0, i = 0)(facts)
      s1
    }
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
  private def simplifyUntilFixpoint(s: StmBuild, i: Int)(
      facts: FactSet
  ): StmBuild = {
    val simplified = time(s"iteration $i stream simplification", Level.TRACE) {
      val s1 = tl(PartialEvalPass.partialEval(s)(facts))
      val s2 = tl(StmAccRemovalPass.removeUnusedVars(s1))
      val s3 = tl(StmAccRemovalPass.removeConstantVars(s2))
      val s4 = tl(StmAccRemovalPass.deduplicateVars(s3))
      val s5 = tl(StmAccRemovalPass.removePrefixCounter(s4))
      s5
    }
    val done =
      time(
        "checking whether stream simplification has reached fixpoint",
        Level.TRACE
      ) {
        simplified == s
      }
    if (done) {
      logger.trace(s"stream has reached fixpoint after ${i + 1} iterations")
      simplified
    } else {
      logger.trace(
        s"stream has not reached fixpoint yet after ${i + 1} iterations. Continuing simplification"
      )
      // New partial evaluation opportunities may have been revealed by
      // inlining constant-valued accumulator elements
      simplifyUntilFixpoint(simplified, i = i + 1)(facts)
    }
  }

  private def tl(s: Expr): StmBuild =
    s.tchk().lower().asInstanceOf[StmBuild]
}
