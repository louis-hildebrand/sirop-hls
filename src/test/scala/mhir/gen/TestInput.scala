package mhir.gen

import mhir.ir._

/** A sequence of inputs to provide to a design.
  *
  * @param elems
  *   the elements of the sequence. `None` means that no input should be
  *   provided in the given clock cycle (i.e., `valid = 0`). `Some(x)` means
  *   that data `x` should be sent as input (i.e., `valid = 1`).
  */
case class TestInput(elems: Seq[Option[Expr]])
