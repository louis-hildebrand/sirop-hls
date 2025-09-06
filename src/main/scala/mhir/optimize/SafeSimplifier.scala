package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.{PartialEvalPass => PE}

trait SafeSimplifier {
  def enabled: Boolean
  def disabled: Boolean = !enabled

  def simplify(e: Expr)(implicit facts: FactSet = FactSet()): Expr
}

object SafeSimplifier {
  def apply(enabled: Boolean = true): SafeSimplifier = {
    if (enabled) EnabledSafeSimplifier else DisabledSafeSimplifier
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
object EnabledSafeSimplifier extends SafeSimplifier {

  private val logger = Logger(getClass.getName)

  private val stmBuildSimplifier: StmBuildSimplifier.type = StmBuildSimplifier
  private val letStmSimplifier: LetStmSimplifier = EnabledLetStmSimplifier

  override def enabled: Boolean = true

  def simplify(e: Expr)(implicit facts: FactSet = FactSet()): Expr = {
    logger.trace(s"performing conservative simplification: $e")
    logger.trace("partially evaluating...")
    val pe = PE.partialEval(e)
    logger.trace(s"after partial evaluation: $pe")
    simplifyStreams(pe)
  }

  private def simplifyStreams(e: Expr)(implicit facts: FactSet): Expr = {
    val result = e match {
      case s: StmBuild =>
        val newEquations = s.equations.map({
          case (x, (producer, next)) if x.typ.isInstanceOf[TyStm] =>
            x -> (simplifyStreams(producer), next)
          case eqn => eqn
        })
        val newS = StmBuild(s.n, s.data, s.valid, newEquations)()
        stmBuildSimplifier.simplify(newS)(facts)
      case LetStm(x, in, out) =>
        val newIn = simplifyStreams(in)
        val newOut = simplifyStreams(out)
        letStmSimplifier.simplify(LetStm(x, newIn, newOut)())
      case e =>
        e.map(simplifyStreams)
    }
    result.tchk()
  }
}

object DisabledSafeSimplifier extends SafeSimplifier {
  override def enabled: Boolean = false

  override def simplify(e: Expr)(implicit facts: FactSet): Expr = {
    PE.partialEval(e)(facts)
  }
}
