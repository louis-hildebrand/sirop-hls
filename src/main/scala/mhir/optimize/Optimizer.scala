package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time
import mhir.optimize.{BinOpTreeMaker => BOTM, PartialEvalPass => PE}

/** Top-level optimizer.
  */
object Optimizer {
  private implicit val logger: Logger = Logger(getClass.getName)

  def optimize(s: Expr, options: OptimizerOptions): Expr = {
    logger.trace(s"optimizing expression: $s")

    val s0 = if (options.simplify) {
      time("basic simplifications") {
        SafeSimplifier.simplify(s)
      }
    } else {
      logger.info(s"skipping basic simplification")
      s
    }

    val s1 = if (options.fuse) {
      time("greedy fusion") {
        val fused = GreedyStmFuser.fuse(s0)
        // Partially evaluate in case there are some instances of LetStm which
        // now have at most one consumer
        val pe = PE.partialEval(fused)
        pe
      }
    } else {
      logger.info("skipping greedy fusion")
      s0
    }

    val s2 = if (options.matchLatency) {
      time("latency matching") {
        StmLatencyMatcher.matchLatencies(s1)
      }
    } else {
      logger.info("skipping latency matching")
      s1
    }

    // I think the program is more readable like this.
    // I don't think a compiler flag is needed, since it shouldn't change the
    // generated hardware in any meaningful way.
    val s3 = time("moving LetStm up") {
      LetStmMover.moveUp(s2)
    }

    val s4 = if (options.balanceBinOpTrees) {
      time("balancing binop trees") {
        BOTM.makeBinOpTrees(s3)
      }
    } else {
      logger.info("skipping balancing binop trees")
      s3
    }

    s4
  }
}
