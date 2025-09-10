package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.{PartialEvalPass => PE}

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

  private val logger = Logger(getClass.getName)

  override def enabled: Boolean = true

  def simplify(e: Expr)(implicit facts: FactSet = FactSet()): Expr = {
    logger.trace(s"performing basic stream simplification: $e")
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

object DisabledStmSimplifier extends StmSimplifier {
  override def enabled: Boolean = false

  override def simplify(e: Expr)(implicit facts: FactSet): Expr = {
    PE.partialEval(e)(facts)
  }
}
