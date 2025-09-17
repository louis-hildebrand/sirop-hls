package mhir.optimize

/** Options to enable and disable optimizations.
  *
  * @param simplifyStmBuild
  *   whether to run the [[mhir.optimize.StmBuildSimplifier]].
  * @param simplifyLetStm
  *   whether to run the [[mhir.optimize.LetStmSimplifier]].
  * @param fuse
  *   whether to perform greedy fusion.
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
    simplifyLetStm: Boolean,
    fuse: Boolean,
    matchLatency: Boolean,
    balanceBinOpTrees: Boolean,
    assumeThroughputsMatch: Boolean
)

/** Companion object for [[OptimizerOptions]].
  */
object OptimizerOptions {

  /** An instance of [[OptimizerOptions]] where all optimizations are enabled.
    */
  def all(assumeThroughputsMatch: Boolean): OptimizerOptions = {
    new OptimizerOptions(
      simplifyStmBuild = true,
      simplifyLetStm = true,
      fuse = true,
      matchLatency = true,
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
      simplifyLetStm = false,
      fuse = false,
      matchLatency = false,
      balanceBinOpTrees = false,
      assumeThroughputsMatch = false
    )
  }
}
