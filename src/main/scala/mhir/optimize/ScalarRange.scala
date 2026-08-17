package mhir.optimize

import mhir.ir._

/** The range <code>[lower, upper)</code>.
  *
  * @param lower
  *   Lower bound, inclusive
  * @param upper
  *   Upper bound, exclusive
  */
case class ScalarRange(lower: Option[Expr], upper: Option[Expr]) {
  this.lower.foreach(e =>
    require(
      !e.hasSyntaxSugar,
      "Lower bound must not contain syntax sugar."
    )
  )
  this.upper.foreach(e =>
    require(
      !e.hasSyntaxSugar,
      "Upper bound must not contain syntax sugar."
    )
  )

  /** Combine this range with a new one. If this range and the new one both have
    * a lower bound, then the new lower bound will be taken. Likewise for the
    * upper bound.
    */
  def merge(that: ScalarRange): ScalarRange = {
    that match {
      case ScalarRange(newLower, newUpper) =>
        val mergedLower = if (newLower.isDefined) newLower else this.lower
        val mergedUpper = if (newUpper.isDefined) newUpper else this.upper
        ScalarRange(mergedLower, mergedUpper)
    }
  }
}
