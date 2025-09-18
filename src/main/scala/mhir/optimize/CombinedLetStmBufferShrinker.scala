package mhir.optimize
import mhir.ir.Expr

class CombinedLetStmBufferShrinker(steps: Seq[LetStmBufferShrinker])
    extends LetStmBufferShrinker {
  override def enabled: Boolean = steps.exists(_.enabled)

  override def shrinkBuffers(e: Expr): Expr = {
    this.steps.foldLeft(e)({ case (acc, pass) => pass.shrinkBuffers(acc) })
  }
}
