package mhir.optimize

/** Options to enable and disable optimizations.
  *
  * @param simplifyStmBuild
  *   whether to run the [[mhir.optimize.StmBuildSimplifier]].
  * @param inlineLetStm
  *   whether to run the [[mhir.optimize.LetStmSimplifier]].
  * @param fuse
  *   whether to perform greedy stream fusion.
  * @param fission
  *   whether to perform stream fission.
  * @param matchLatency
  *   whether to perform latency matching.
  * @param balanceBinOpTrees
  *   whether to balance trees of binary operators.
  * @param assumeThroughputsMatch
  *   whether the optimizer can assume the throughputs along different branches
  *   of a [[mhir.ir.LetStm]] match.
  */
case class OptimizerOptions(
    simplifyStmBuild: Boolean,
    inlineLetStm: Boolean,
    fuse: Boolean,
    fission: Boolean,
    matchLatency: Boolean,
    removeUnusedData: Boolean,
    staticallyShrinkLetStmBuffers: Boolean,
    maxLetStmBufSize: Option[Int],
    balanceBinOpTrees: Boolean,
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
      inlineLetStm = true,
      fuse = true,
      fission = true,
      matchLatency = true,
      removeUnusedData = true,
      staticallyShrinkLetStmBuffers = true,
      maxLetStmBufSize = maxLetStmBufSize,
      balanceBinOpTrees = true,
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
      inlineLetStm = false,
      fuse = false,
      fission = false,
      matchLatency = false,
      removeUnusedData = false,
      staticallyShrinkLetStmBuffers = false,
      maxLetStmBufSize = None,
      balanceBinOpTrees = false,
      assumeThroughputsMatch = false
    )
  }
}
