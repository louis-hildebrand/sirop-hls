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

    // No flag to skip this step because
    //  (1) The partial evaluator is used everywhere (including by the type
    //      checker, to check lengths!). Skipping this step would not completely
    //      disable partial evaluation, and turning off partial evaluation at
    //      the source (maybe with some kind of mutable kill switch) may break
    //      things.
    //  (2) If you don't at least partially evaluate, the code is so bad for
    //      bigger examples (e.g., 1920x1080 conv2d) that simulation fails.
    val s0 = PE.partialEval(s)

    val s1 = if (options.simplify) {
      time("basic simplifications") {
        SafeSimplifier.simplify(s0)
      }
    } else {
      logger.info(s"skipping basic simplification")
      s0
    }

    val s2 = if (options.fuse) {
      time("greedy fusion") {
        val fused = GreedyStmFuser.fuse(s1)
        // Partially evaluate in case there are some instances of LetStm which
        // now have at most one consumer
        val pe = PE.partialEval(fused)
        pe
      }
    } else {
      logger.info("skipping greedy fusion")
      s1
    }

    val s3 = if (options.matchLatency) {
      time("latency matching") {
        StmLatencyMatcher.matchLatencies(s2)
      }
    } else {
      logger.info("skipping latency matching")
      s2
    }

    // I think the program is more readable like this.
    // I don't think a compiler flag is needed, since it shouldn't change the
    // generated hardware in any meaningful way.
    val s4 = time("moving LetStm up") {
      LetStmMover.moveUp(s3)
    }

    val s5 = if (options.balanceBinOpTrees) {
      time("balancing binop trees") {
        BOTM.makeBinOpTrees(s4)
      }
    } else {
      logger.info("skipping balancing binop trees")
      s4
    }

    s5
  }
}
