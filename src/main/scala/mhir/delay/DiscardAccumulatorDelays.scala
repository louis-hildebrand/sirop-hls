package mhir.delay

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._

/** Transformation that discards all the accumulator delays in
  * [[mhir.ir.StmBuild]].
  *
  * Unlike [[ReplaceAccumulatorDelaysWithGo]], this transformation does not do
  * anything to replace the accumulator delays (e.g., adding a new producer).
  * [[DiscardAccumulatorDelays]] just naively erases all the delays.
  */
object DiscardAccumulatorDelays {

  def apply(e: Expr): Expr = {
    e match {
      case s: StmBuild =>
        s.mapProducers({ case (x, (stm, ready, delay)) =>
          x -> (this.apply(stm), ready, delay)
        }).mapAccumulators({ case (x, (init, next, _)) =>
          x -> (init, next, Tuple()())
        }).tchk()
      case e => e.map(this.apply).tchk()
    }
  }
}
