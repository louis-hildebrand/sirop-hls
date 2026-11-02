package mhir.delay

/** The latency at a certain point in the program.
  */
sealed trait Latency

/** The latency is a concrete number of clock cycles after the global epoch.
  */
case class LatencyConcrete(n: Int) extends Latency

/** The latency could not be determined.
  *
  * This could be because the latency depends on runtime data, because the
  * compiler was not smart enough to figure it out from the source code, etc.
  */
object LatencyUnknown extends Latency
