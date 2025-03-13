package opt

import ir._

sealed trait Range {

  /** Combine this range with a new one. If this range and the new one both have
    * a lower bound, then the new lower bound will be taken. Likewise for the
    * upper bound.
    */
  def merge(that: Range): Range
}

/** The range <code>[lower, upper)</code>.
  *
  * @param lower
  *   Lower bound, inclusive
  * @param upper
  *   Upper bound, exclusive
  */
case class ScalarRange(lower: Option[Expr], upper: Option[Expr]) extends Range {
  override def merge(that: Range): ScalarRange = {
    that match {
      case ScalarRange(newLower, newUpper) =>
        val mergedLower = if (newLower.isDefined) newLower else lower
        val mergedUpper = if (newUpper.isDefined) newUpper else upper
        ScalarRange(mergedLower, mergedUpper)
      case _ => throw new IllegalArgumentException(s"Range type mismatch")
    }
  }
}

/** Ranges of the elements in the accumulator of a stream.
  */
case class StmAccRange(elemRanges: Map[Param, ScalarRange]) extends Range {
  override def merge(that: Range): StmAccRange = {
    that match {
      case StmAccRange(newRanges) if (newRanges.size == elemRanges.size) =>
        if (elemRanges.keySet != newRanges.keySet) {
          throw new IllegalArgumentException("Range parameter set mismatch")
        }
        val zipped = (elemRanges.keySet
          .union(newRanges.keySet))
          .map(x => x -> (elemRanges(x), newRanges(x)))
          .toMap
        StmAccRange(
          zipped
            .map({ case (x, (r0, r1)) => x -> r0.merge(r1) })
        )
      case _ => throw new IllegalArgumentException("Range type mismatch")
    }
  }
}
