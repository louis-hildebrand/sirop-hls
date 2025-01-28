package opt

import ir._

case class FactSet(rangeByParam: Map[Param, Range] = Map()) {
  def lowerBound(x: Param, c: Int): FactSet = {
    rangeByParam.get(x) match {
      case None => FactSet(rangeByParam + (x -> ScalarRange(lower = Some(c))))
      case Some(ScalarRange(None, u)) =>
        FactSet(rangeByParam + (x -> ScalarRange(lower = Some(c), upper = u)))
      case Some(ScalarRange(Some(l), u)) if c > l =>
        FactSet(rangeByParam + (x -> ScalarRange(lower = Some(c), upper = u)))
      case _ => this
    }
  }

  def upperBound(x: Param, c: Int): FactSet = {
    rangeByParam.get(x) match {
      case None => FactSet(rangeByParam + (x -> ScalarRange(upper = Some(c))))
      case Some(ScalarRange(l, None)) =>
        FactSet(rangeByParam + (x -> ScalarRange(lower = l, upper = Some(c))))
      case Some(ScalarRange(l, Some(u))) if c < u =>
        FactSet(rangeByParam + (x -> ScalarRange(lower = l, upper = Some(c))))
      case _ => this
    }
  }
}

sealed trait Range
case class ScalarRange(lower: Option[Int] = None, upper: Option[Int] = None)
    extends Range {
  def union(that: ScalarRange): ScalarRange = {
    val lower = (this.lower, that.lower) match {
      case (Some(a), Some(b)) => Some(math.min(a, b))
      case _                  => None
    }
    val upper = (this.upper, that.upper) match {
      case (Some(a), Some(b)) => Some(math.max(a, b))
      case _                  => None
    }
    ScalarRange(lower, upper)
  }
}
