package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time
import mhir.optimize.cost.{SimpleAreaCostModel, SimpleDelayCostModel}
import mhir.optimize.{PartialEvalPass => PE}
import org.slf4j.event.Level

import scala.annotation.tailrec

/** Top-level optimizer.
  */
class Optimizer(
    simplifier: StmSimplifier,
    letStmSimplifier: LetStmSimplifier,
    fusionPass: StmFusionPass,
    fissionPass: StmFissionPass,
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
    val s0 = time("initial partial evaluation", Level.DEBUG) {
      PE.partialEval(s)
    }

    val s1 = simplifier.simplify(s0)

    val s2 = fissionPass.fission(s1)

    if (fusionPass.disabled) {
      logger.debug("stream fusion is disabled")
    }
    val s3 = time(
      "greedy stream fusion",
      mute = fusionPass.disabled,
      level = Level.DEBUG
    ) {
      @tailrec
      def fix(s: Expr, i: Int): Expr = {
        val fused = fusionPass.fuse(s)
        // Simplify in case there are some instances of LetStm which now have
        // at most one consumer
        val simpl = letStmSimplifier.simplifyAll(fused)
        if (simpl == s) {
          logger.debug(
            s"reached fixpoint for greedy fusion after ${i + 1} iterations"
          )
          simpl
        } else {
          // Getting rid of the LetStm may have revealed new fusion
          // opportunities
          fix(simpl, i = i + 1)
        }
      }
      time("fixed-point iteration for fusion") {
        fix(s2, i = 0)
      }
    }

    val s4 = latencyMatcher.matchLatencies(s3)

    val s5 = letStmBufShrinker.shrinkBuffers(s4)

    // I think the program is more readable like this.
    // I don't think a compiler flag is needed, since it shouldn't change the
    // generated hardware in any meaningful way.
    val s6 = time("moving LetStm up", Level.DEBUG) {
      LetStmMover.moveUp(s5)
    }

    val s7 = binOpBalancer.balance(s6)

    val areaCost = SimpleAreaCostModel.cost(s7)
    logger.debug(s"final area cost: $areaCost")
    val delayCost = SimpleDelayCostModel.cost(s7)
    logger.debug(
      s"final delay cost: $delayCost (max ${SimpleDelayCostModel.FullCycleDelay})"
    )
    if (delayCost > SimpleDelayCostModel.FullCycleDelay) {
      val percent =
        100 * (delayCost / SimpleDelayCostModel.FullCycleDelay.toDouble)
      logger.warn(
        f"delay cost of $delayCost is $percent%.0f%% of maximum."
          + " Design may not meet timing requirements."
      )
    }

    s7
  }
}

object Optimizer {
  def apply(options: OptimizerOptions): Optimizer = {
    val stmBuildSimplifier =
      StmBuildSimplifier(enabled = options.simplifyStmBuild)
    val letStmSimplifier = LetStmSimplifier(enabled = options.simplifyLetStm)
    val simplifier = StmSimplifier(stmBuildSimplifier, letStmSimplifier)
    val loggingSimplifier = StmSimplifierWithLogging(simplifier)
    val binOpBalancer =
      BinOpTreeBalancingPass(enabled = options.balanceBinOpTrees)
    val binOpBalancerWithLogging =
      BinOpTreeBalancingPassWithLogging(binOpBalancer)
    val fusionPass =
      StmFusionPass(simplifier = simplifier, enabled = options.fuse)
    val fissionPass = StmFissionPassWithLogging(
      StmFissionPass(
        scheduler = StmOutputScheduler(binOpBalancer),
        enabled = options.fission
      )
    )
    val latencyMatcher = StmLatencyMatcher(enabled = options.matchLatency)
    val letStmBufShrinker = {
      val staticPass = if (options.staticallyShrinkLetStmBuffers) {
        Some(
          new StaticLetStmBufferShrinker(
            assumeThroughputsMatch = options.assumeThroughputsMatch
          )
        )
      } else {
        None
      }
      val manualPass =
        options.maxLetStmBufSize.map(mbs => new ManualLetStmBufferShrinker(mbs))
      new CombinedLetStmBufferShrinker(Seq(staticPass, manualPass).flatten)
    }
    new Optimizer(
      loggingSimplifier,
      letStmSimplifier,
      fusionPass,
      fissionPass,
      latencyMatcher,
      letStmBufShrinker = letStmBufShrinker,
      binOpBalancerWithLogging
    )
  }
}
