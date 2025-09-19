package mhir.optimize
import com.typesafe.scalalogging.Logger
import mhir.ir.Expr

class CombinedLetStmBufferShrinker(steps: Seq[LetStmBufferShrinker])
    extends LetStmBufferShrinker {

  private val logger: Logger = Logger(getClass.getName)
  private var hasLogged: Boolean = false

  override def enabled: Boolean = steps.exists(_.enabled)

  override def shrinkBuffers(e: Expr): Expr = {
    if (this.disabled && !this.hasLogged) {
      this.hasLogged = true
      logger.debug("no letstm buffer shrinking passes are enabled")
    }
    this.steps.foldLeft(e)({ case (acc, pass) => pass.shrinkBuffers(acc) })
  }
}
