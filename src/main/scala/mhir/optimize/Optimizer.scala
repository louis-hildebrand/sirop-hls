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
    val s1 = StmLatencyMatcher.matchLatencies(s0)
    logger.trace(s"after matching latencies: $s1")
    // I think the program is more readable like this
    val s2 = LetStmMover.moveUp(s1)
    val s3 = BOTM.makeBinOpTrees(s2)
    logger.trace(s"after balancing binop trees: $s3")
    logger.trace("done optimizing")
    s3
  }
}
