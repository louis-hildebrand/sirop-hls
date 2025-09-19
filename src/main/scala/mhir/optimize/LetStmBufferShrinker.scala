package mhir.optimize

import mhir.ir._

trait LetStmBufferShrinker {
  def shrinkBuffers(e: Expr): Expr

  /** Whether this optimization pass is active.
    */
  def enabled: Boolean

  /** See [[enabled]].
    */
  final def disabled: Boolean = !this.enabled
}
