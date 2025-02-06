package opt

import ir._

sealed trait Range

/** The range <code>[lower, upper)</code>.
  *
  * @param lower
  *   Lower bound, inclusive
  * @param upper
  *   Upper bound, exclusive
  */
case class ScalarRange(lower: Option[Expr], upper: Option[Expr]) extends Range

/** Ranges of the elements in the accumulator of a stream.
  */
case class StmAccRange(elemRanges: Seq[ScalarRange]) extends Range
