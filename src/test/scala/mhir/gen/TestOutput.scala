package mhir.gen

import mhir.ir._

/** A sequence of outputs that a design is expected to produce.
  *
  * @param elems
  *   the expected sequence of <i>valid</i> outputs.
  */
case class TestOutput(elems: Seq[Expr])
