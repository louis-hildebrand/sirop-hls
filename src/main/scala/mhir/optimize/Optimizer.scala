package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time
import mhir.optimize.{BinOpTreeMaker => BOTM, PartialEvalPass => PE}

/** Top-level optimizer.
  */
object Optimizer {
  private implicit val logger: Logger = Logger(getClass.getName)

  def optimize(s: Expr): Expr = {
    logger.trace(s"optimizing expression: $s")
    val s0 = time("basic simplifications") {
      SafeSimplifier.simplify(s)
    }
    val s1 = time("greedy fusion") {
      val fused = GreedyStmFuser.fuse(s0)
      // Partially evaluate in case there are some instances of LetStm which
      // now have at most one consumer
      val pe = PE.partialEval(fused)
      pe
    }
    val s2 = time("latency matching") {
      StmLatencyMatcher.matchLatencies(s1)
    }
    // I think the program is more readable like this
    val s3 = time("moving LetStm up") {
      LetStmMover.moveUp(s2)
    }
    val s4 = time("balancing binop trees") {
      BOTM.makeBinOpTrees(s3)
    }
    s4
  }
}
