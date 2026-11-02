package mhir.ir

/** The delay annotation for an input stream.
  */
sealed trait InputDelay

/** The delay annotation is a concrete value.
  */
case class InputDelayConcrete(n: Expr) extends InputDelay
