package mhir.optimize

/** Options to enable and disable optimizations.
  *
  * @param simplifyStmBuild
  *   whether to run the [[mhir.optimize.StmBuildSimplifier]].
  * @param shrinkShiftRegisters
  *   whether to run the [[mhir.optimize.ShiftRegisterShrinker]].
  * @param inlineLetStm
  *   whether to run the [[mhir.optimize.LetStmSimplifier]].
  * @param fuse
  *   whether to perform greedy stream fusion.
  * @param fission
  *   whether to perform stream fission.
  * @param matchLatency
  *   whether to perform latency matching.
  * @param matchLatencyInTests
  *   whether to automatically adjust the test input streams' latencies to match
  *   the delay annotations.
  * @param removeUnusedData
  *   whether to perform the unused data removal pass.
  * @param balanceBinOpTrees
  *   whether to balance trees of binary operators.
  * @param madd
  *   whether the DSPs on the target device support multiplication and addition
  *   in one cycle.
  * @param targetFmaxMHz
  *   the target Fmax, in MHz.
  * @param assumeThroughputsMatch
  *   whether the optimizer can assume the throughputs along different branches
  *   of a [[mhir.ir.LetStm]] match.
  */
case class OptimizerOptions(
    simplifyStmBuild: Boolean,
    shrinkShiftRegisters: Boolean,
    inlineLetStm: Boolean,
    fuse: Boolean,
    fission: Boolean,
    matchLatency: Boolean,
    matchLatencyInTests: Boolean,
    removeUnusedData: Boolean,
    staticallyShrinkLetStmBuffers: Boolean,
    maxLetStmBufSize: Option[Int],
    balanceBinOpTrees: Boolean,
    madd: Boolean,
    targetFmaxMHz: Long,
    assumeThroughputsMatch: Boolean
)

/** Companion object for [[OptimizerOptions]].
  */
object OptimizerOptions {

  /** An instance of [[OptimizerOptions]] where all optimizations are enabled.
    */
  def all(
      assumeThroughputsMatch: Boolean,
      maxLetStmBufSize: Option[Int]
  ): OptimizerOptions = {
    new OptimizerOptions(
      simplifyStmBuild = true,
      shrinkShiftRegisters = true,
      inlineLetStm = true,
      fuse = true,
      fission = true,
      matchLatency = true,
      matchLatencyInTests = true,
      removeUnusedData = true,
      staticallyShrinkLetStmBuffers = true,
      maxLetStmBufSize = maxLetStmBufSize,
      balanceBinOpTrees = true,
      madd = false,
      targetFmaxMHz = 175, // TODO: replace this arbitrary value?
      assumeThroughputsMatch = assumeThroughputsMatch
    )
  }

  /** An instance of [[OptimizerOptions]] where all optimizations are disabled.
    *
    * @return
    */
  def Empty: OptimizerOptions = {
    OptimizerOptions(
      simplifyStmBuild = false,
      shrinkShiftRegisters = false,
      inlineLetStm = false,
      fuse = false,
      fission = false,
      matchLatency = false,
      matchLatencyInTests = false,
      removeUnusedData = false,
      staticallyShrinkLetStmBuffers = false,
      maxLetStmBufSize = None,
      balanceBinOpTrees = false,
      madd = false,
      targetFmaxMHz = 175, // TODO: replace this arbitrary value?
      assumeThroughputsMatch = false
    )
  }
}
