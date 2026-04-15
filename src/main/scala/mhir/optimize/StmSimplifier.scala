package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.logging.time
import mhir.optimize.{PartialEvalPass => PE}
import mhir.typecheck.TypeCheck
import org.slf4j.event.Level

trait StmSimplifier {
  def enabled: Boolean
  def disabled: Boolean = !enabled

  def simplify(e: Expr)(implicit facts: FactSet = FactSet()): Expr
}

object StmSimplifier {
  def apply(
      stmBuildSimplifier: StmBuildSimplifier = StmBuildSimplifier(),
      letStmSimplifier: LetStmSimplifier = LetStmSimplifier()
  ): StmSimplifier = {
    if (stmBuildSimplifier.enabled || letStmSimplifier.enabled) {
      EnabledStmSimplifier(stmBuildSimplifier, letStmSimplifier)
    } else {
      DisabledStmSimplifier
    }
  }
}

object DisabledStmSimplifier extends StmSimplifier {

  override def enabled: Boolean = false

  override def simplify(e: Expr)(implicit facts: FactSet): Expr = {
    PE.partialEval(e)(facts)
  }
}

/** Applies various transformations with the goal of making an expression easier
  * to analyze.
  *
  * This pass doesn't do any exploration, it just applies common sense
  * transformations (e.g., partial evaluation) that should pretty much always be
  * helpful. Note that in some cases this may increase the cost of the hardware
  * (e.g., by increasing the bit width of arithmetic expressions), but it should
  * make expressions easier to analyze.
  */
case class EnabledStmSimplifier(
    stmBuildSimplifier: StmBuildSimplifier,
    letStmSimplifier: LetStmSimplifier
) extends StmSimplifier {

  override def enabled: Boolean = true

  def simplify(e: Expr)(implicit facts: FactSet = FactSet()): Expr = {
    val pe = PE.partialEval(e)
    letStmSimplifier.simplifyAll(simplifyStmBuild(pe))
  }

  private def simplifyStmBuild(e: Expr)(implicit facts: FactSet): Expr = {
    val result = e match {
      case s: StmBuild =>
        val newEquations = s.equations.map({
          case (x, (producer, next)) if x.typ.isInstanceOf[TyStm] =>
            x -> (simplifyStmBuild(producer), next)
          case eqn => eqn
        })
        val newS = StmBuild(s.n, s.data, s.valid, newEquations)()
        stmBuildSimplifier.simplify(newS)(facts)
      case e =>
        e.map(simplifyStmBuild)
    }
    result.tchk()
  }
}

case class StmSimplifierWithLogging(underlying: StmSimplifier)
    extends StmSimplifier {

  private implicit val logger: Logger = Logger(getClass.getName)
  private var hasLogged: Boolean = false

  override def enabled: Boolean = true

  def simplify(e: Expr)(implicit facts: FactSet = FactSet()): Expr = {
    if (underlying.disabled) {
      if (!hasLogged) {
        hasLogged = true
        logger.debug("basic stream simplification is disabled")
      }
      underlying.simplify(e)(facts)
    } else {
      logger.trace(s"performing basic stream simplification: $e")
      time("basic stream simplification", Level.DEBUG) {
        underlying.simplify(e)(facts)
      }
    }
  }
}
