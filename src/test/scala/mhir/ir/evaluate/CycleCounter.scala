package mhir.ir.evaluate

import mhir.ir._

object CycleCounter {
  def count(s: Expr): Int = {
    // Subtract one for "step zero"
    // Subtract one for the last step, which shows the empty pipeline
    mhir.debug.Tracer.traceAll(s).steps.length - 2
  }
}
