package opt

import ir._

/** The range <code>[lower, upper)</code>.
  *
  * @param lower
  *   Lower bound, inclusive
  * @param upper
  *   Upper bound, exclusive
  */
case class Range(lower: Option[Expr], upper: Option[Expr])
