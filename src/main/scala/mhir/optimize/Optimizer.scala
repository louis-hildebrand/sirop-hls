package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.optimize.{BinOpTreeMaker => BOTM}

/** Top-level optimizer.
  */
object Optimizer {
  private val logger = Logger(getClass.getName)

  def optimize(s: Expr): Expr = {
    logger.trace(s"optimizing expression: $s")
    val s0 = SafeSimplifier.simplify(s)
    logger.trace(s"after simplification: $s0")
    val s1 = LetStmMover.moveUp(s0)
    logger.trace(s"after moving up lets: $s1")
    val s2 = StmLatencyMatcher.matchLatencies(s1)
    logger.trace(s"after matching latencies: $s2")
    val s3 = BOTM.makeBinOpTrees(s2)
    logger.trace(s"after balancing binop trees: $s3")
    logger.trace("done optimizing")
    s3
  }
}
