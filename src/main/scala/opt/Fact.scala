package opt

import ir._

sealed trait IntOrInf {
  def +(that: IntOrInf): IntOrInf = {
    (this, that) match {
      case (NegInf, PosInf) | (PosInf, NegInf) =>
        throw new IllegalArgumentException(
          "Infinity minus infinity is undefined."
        )
      case (NegInf, _) | (_, NegInf) => NegInf
      case (PosInf, _) | (_, PosInf) => PosInf
      case (Finite(x), Finite(y))    => Finite(x + y)
    }
  }

  def *(that: IntOrInf): IntOrInf = {
    (this, that) match {
      case (NegInf, NegInf) | (PosInf, PosInf) => PosInf
      case (NegInf, PosInf) | (PosInf, NegInf) => NegInf
      case (Finite(0), _) | (Finite(0), _)     => Finite(0)
      case (NegInf, Finite(c)) =>
        if (c < 0) PosInf else NegInf
      case (Finite(c), NegInf) =>
        if (c < 0) PosInf else NegInf
      case (PosInf, Finite(c)) =>
        if (c < 0) NegInf else PosInf
      case (Finite(c), PosInf) =>
        if (c < 0) NegInf else PosInf
      case (Finite(x), Finite(y)) => Finite(x * y)
    }
  }

  def <(that: IntOrInf): Boolean = {
    (this, that) match {
      case (NegInf, NegInf)       => false
      case (NegInf, _)            => true
      case (Finite(_), NegInf)    => false
      case (Finite(x), Finite(y)) => x < y
      case (Finite(x), PosInf)    => true
      case (PosInf, _)            => false
    }
  }
}
object IntOrInf {
  def min(x: IntOrInf, y: IntOrInf): IntOrInf = {
    if (x < y) x else y
  }

  def max(x: IntOrInf, y: IntOrInf): IntOrInf = {
    if (x < y) y else x
  }
}
case class Finite(c: Int) extends IntOrInf
case object NegInf extends IntOrInf
case object PosInf extends IntOrInf

case class FactSet(rangeByParam: Map[Param, Range] = Map()) {

  /** Update the range information for <code>x</code>.
    *
    * @param x
    *   A variable
    * @param r
    *   A range
    * @return
    *   A fact set where the range of <code>x</code> is the intersection of the
    *   old range with the new one.
    */
  def range(x: Param, r: Range): FactSet = {
    val oldRange = rangeByParam.getOrElse(x, r.full())
    val newRange = oldRange.intersect(r)
    FactSet(rangeByParam + (x -> newRange))
  }
}

sealed trait Range {
  def union(that: Range): Range
  def intersect(that: Range): Range
  def full(): Range
  def empty(): Range
}
case class ScalarRange(lower: IntOrInf, upper: IntOrInf) extends Range {
  override def union(that: Range): ScalarRange = {
    that match {
      case that: ScalarRange =>
        ScalarRange(
          IntOrInf.min(this.lower, that.lower),
          IntOrInf.max(this.upper, that.upper)
        )
      case _ =>
        throw new IllegalArgumentException(
          s"Cannot take union of ${this.getClass.getSimpleName} with ${that.getClass.getSimpleName}."
        )
    }
  }

  override def intersect(that: Range): ScalarRange = {
    that match {
      case that: ScalarRange =>
        ScalarRange(
          IntOrInf.max(this.lower, that.lower),
          IntOrInf.min(this.upper, that.upper)
        )
      case _ =>
        throw new IllegalArgumentException(
          s"Cannot take intersection of ${this.getClass.getSimpleName} with ${that.getClass.getSimpleName}."
        )
    }
  }

  override def full(): ScalarRange = ScalarRange.full()

  override def empty(): ScalarRange = ScalarRange.empty()
}
object ScalarRange {
  def full(): ScalarRange = ScalarRange(NegInf, PosInf)
  def empty(): ScalarRange = ScalarRange(PosInf, NegInf)
}
