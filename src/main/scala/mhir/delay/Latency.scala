package mhir.delay

import mhir.ir._
import mhir.sem.SemanticError

/** The latency at a certain point in the program.
  */
sealed trait Latency

object Latency {

  def apply(d: InputDelay): Latency = {
    d match {
      case InputDelayConcrete(IntCst(n)) => LatencyConcrete(n.toInt)
      case InputDelayConcrete(delay) =>
        throw SemanticError(
          s"latency matching does not currently support non-constant delay $delay"
        )
      case InputDelayAuto => LatencyAuto
    }
  }
}

/** The latency is a concrete number of clock cycles after the global epoch.
  */
case class LatencyConcrete(n: Int) extends Latency

/** The latency could not be determined.
  *
  * This could be because the latency depends on runtime data, because the
  * compiler was not smart enough to figure it out from the source code, etc.
  */
object LatencyUnknown extends Latency

/** The latency depends entirely on one or more [[mhir.ir.InputDelay]] values
  * that the compiler must choose.
  */
object LatencyAuto extends Latency
