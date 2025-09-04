package mhir.optimize

/** Options to enable and disable optimizations.
  *
  * @param simplify
  *   whether to perform basic partial evaluation and simplification.
  * @param fuse
  *   whether to perform greedy fusion.
  * @param matchLatency
  *   whether to perform latency matching.
  * @param balanceBinOpTrees
  *   whether to balance trees of binary operators.
  */
case class OptimizerOptions(
    simplify: Boolean,
    fuse: Boolean,
    matchLatency: Boolean,
    balanceBinOpTrees: Boolean
)

/** Companion object for [[OptimizerOptions]].
  */
object OptimizerOptions {

  /** An instance of [[OptimizerOptions]] where all optimizations are enabled.
    */
  def All: OptimizerOptions = {
    new OptimizerOptions(
      simplify = true,
      fuse = true,
      matchLatency = true,
      balanceBinOpTrees = true
    )
  }

  /** An instance of [[OptimizerOptions]] where all optimizations are disabled.
    *
    * @return
    */
  def Empty: OptimizerOptions = {
    OptimizerOptions(
      simplify = false,
      fuse = false,
      matchLatency = false,
      balanceBinOpTrees = false
    )
  }
}
