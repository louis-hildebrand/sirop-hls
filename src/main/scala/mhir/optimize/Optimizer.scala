package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time
import mhir.optimize.{PartialEvalPass => PE}

import scala.annotation.tailrec

/** Top-level optimizer.
  */
class Optimizer(
    simplifier: StmSimplifier,
    letStmSimplifier: LetStmSimplifier,
    fusionPass: StmFusionPass,
    latencyMatcher: StmLatencyMatcher,
    letStmBufShrinker: LetStmBufferShrinker,
    binOpBalancer: BinOpTreeBalancingPass
) {
  private implicit val logger: Logger = Logger(getClass.getName)

  def optimize(s: Expr): Expr = {
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

    if (simplifier.disabled) {
      logger.info("basic stream simplification is disabled")
    }
    val s1 = time("basic stream simplification", mute = simplifier.disabled) {
      simplifier.simplify(s0)
    }

    if (fusionPass.disabled) {
      logger.info("stream fusion is disabled")
    }
    val s2 = time("greedy stream fusion", mute = fusionPass.disabled) {
      @tailrec
      def fix(s: Expr, i: Int): Expr = {
        val fused = fusionPass.fuse(s)
        // Simplify in case there are some instances of LetStm which now have
        // at most one consumer
        val simpl = letStmSimplifier.simplifyAll(fused)
        if (simpl == s) {
          logger.trace(
            s"reached fixpoint for greedy fusion after ${i + 1} iterations"
          )
          simpl
        } else {
          // Getting rid of the LetStm may have revealed new fusion
          // opportunities
          fix(simpl, i = i + 1)
        }
      }
      fix(s1, i = 0)
    }

    if (latencyMatcher.disabled) {
      logger.info("latency matching is disabled")
    }
    val s3 = time("latency matching", mute = latencyMatcher.disabled) {
      latencyMatcher.matchLatencies(s2)
    }

    if (letStmBufShrinker.disabled) {
      logger.info("letstm buffer shrinking is disabled")
    }
    val s4 =
      time("letstm buffer shrinking", mute = letStmBufShrinker.disabled) {
        letStmBufShrinker.shrinkBuffers(s3)
      }

    // I think the program is more readable like this.
    // I don't think a compiler flag is needed, since it shouldn't change the
    // generated hardware in any meaningful way.
    val s5 = time("moving LetStm up") {
      LetStmMover.moveUp(s4)
    }

    if (binOpBalancer.disabled) {
      logger.info("balancing binop trees is disabled")
    }
    val s6 = time("balancing binop trees", mute = binOpBalancer.disabled) {
      binOpBalancer.balance(s5)
    }

    s6
  }
}

object Optimizer {
  def apply(options: OptimizerOptions): Optimizer = {
    val stmBuildSimplifier =
      StmBuildSimplifier(enabled = options.simplifyStmBuild)
    val letStmSimplifier = LetStmSimplifier(enabled = options.simplifyLetStm)
    val simplifier = StmSimplifier(stmBuildSimplifier, letStmSimplifier)
    val fusionPass =
      StmFusionPass(simplifier = simplifier, enabled = options.fuse)
    val latencyMatcher = StmLatencyMatcher(enabled = options.matchLatency)
    val letStmBufShrinker =
      new StaticLetStmBufferShrinker(assumeThroughputsMatch =
        options.assumeThroughputsMatch
      )
    val binOpBalancer =
      BinOpTreeBalancingPass(enabled = options.balanceBinOpTrees)
    new Optimizer(
      simplifier,
      letStmSimplifier,
      fusionPass,
      latencyMatcher,
      letStmBufShrinker = letStmBufShrinker,
      binOpBalancer
    )
  }
}
