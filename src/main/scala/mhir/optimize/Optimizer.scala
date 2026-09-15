package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time
import mhir.optimize.cost.SimpleDelayCostModel
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
    delay: SimpleDelayCostModel,
    shiftRegisterShrinker: ShiftRegisterShrinker,
    latencyMatcher: LatencyMatcher,
    letStmBufShrinker: LetStmBufferShrinker,
    binOpBalancer: BinOpTreeBalancingPass,
    unusedDataRemover: UnusedDataRemover,
    headByParam: Map[Param, Expr]
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

    logger.debug(s"stream fusion: ${fusionPass.strategy}")
    val s3 = time("stream fusion", level = Level.DEBUG) {
      @tailrec
      def fix(s: Expr, i: Int): Expr = {
        logger.debug(s"stream fusion + letstm simplification: iteration $i")
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
      fix(s2, i = 0)
    }

    val s4 = {
      val s3_1 = shiftRegisterShrinker.applyRecursively(s3)
      val s3_2 = simplifier.simplify(s3_1)
      val s3_3 = letStmSimplifier.simplifyAll(s3_2)
      // TODO: shift register shrinking tends to result in a bunch of duplicate let bindings, as in
      //    letstm[n] s1 = ... in
      //    letstm[n] s2 = s1 in
      //    letstm[n] s3 = s2 in
      //    ...
      //  Will this cause problems for resource usage?
      s3_3
    }

    val s5 = latencyMatcher.matchLatencies(s4, headByParam = headByParam)

    val s6 = unusedDataRemover.removeUnusedData(s5)

    val s7 = letStmBufShrinker.shrinkBuffers(s6)

    // I think the program is more readable like this.
    // I don't think a compiler flag is needed, since it shouldn't change the
    // generated hardware in any meaningful way.
    val s8 = time("moving LetStm up", Level.DEBUG) {
      LetStmMover.moveUp(s7)
    }

    val s9 = binOpBalancer.balance(s8)

    val delayCost = delay.rawCost(s9)
    val delayCostPercent =
      100 * (delayCost / delay.FullCycleDelay.toDouble)
    logger.debug(
      f"final combinational delay cost: $delayCostPercent%.0f%% of maximum"
    )
    if (delayCost > delay.FullCycleDelay) {
      logger.warn(
        f"combinational delay cost is $delayCostPercent%.0f%% of maximum."
          + " Design may not meet timing requirements."
      )
    }

    s9
  }
}

object Optimizer {
  def apply(
      options: OptimizerOptions,
      handshake: Boolean,
      headByParam: Map[Param, Expr]
  ): Optimizer = {
    val stmBuildSimplifier =
      StmBuildSimplifier(enabled = options.simplifyStmBuild)
    val letStmSimplifier = LetStmSimplifier(enabled = options.inlineLetStm)
    val simplifier = StmSimplifier(stmBuildSimplifier, letStmSimplifier)
    val loggingSimplifier = StmSimplifierWithLogging(simplifier)
    val binOpBalancer =
      BinOpTreeBalancingPass(enabled = options.balanceBinOpTrees)
    val binOpBalancerWithLogging =
      BinOpTreeBalancingPassWithLogging(binOpBalancer)
    val delayCostModel = SimpleDelayCostModel(madd = options.madd)
    val fusionPass = StmFusionPass(
      simplifier = stmBuildSimplifier,
      delayCostModel = delayCostModel,
      handshake = handshake,
      enabled = options.fuse
    )
    val fissionPass = StmFissionPassWithLogging(
      StmFissionPass(
        scheduler = StmOutputScheduler(binOpBalancer, delayCostModel),
        enabled = options.fission
      )
    )
    val shiftRegisterShrinker = ShiftRegisterShrinker(
      delayCostModel = delayCostModel,
      // TODO: introduce command-line arg to control this
      enabled = true,
      handshake = handshake
    )
    val latencyAnalysis = new LatencyAnalysis(handshake = handshake)
    val latencyMatcher = LatencyMatcher(
      latencyAnalysis,
      handshake = handshake,
      enabled = options.matchLatency
    )
    val letStmBufShrinker = {
      val staticPass = if (options.staticallyShrinkLetStmBuffers) {
        Some(
          new StaticLetStmBufferShrinker(
            latencyAnalysis = latencyAnalysis,
            handshake = handshake,
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
    val unusedDataRemover =
      UnusedDataRemover(enabled = options.removeUnusedData)
    new Optimizer(
      loggingSimplifier,
      letStmSimplifier,
      fusionPass,
      fissionPass,
      delayCostModel,
      shiftRegisterShrinker,
      latencyMatcher,
      letStmBufShrinker,
      binOpBalancerWithLogging,
      unusedDataRemover,
      headByParam = headByParam
    )
  }
}
