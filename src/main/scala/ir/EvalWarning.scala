package ir

/** A problem that the evaluator encountered which does not immediately result
  * in an exception.
  */
sealed trait EvalWarning {
  def display: String
}

/** Division or modulo by zero.
  */
object DivByZeroWarning extends EvalWarning {
  override def display: String = "division or modulo by zero"
}

/** An out-of-bounds [[VecAccess]].
  *
  * @param n
  *   the length of the vector.
  * @param i
  *   the index that was accessed.
  */
case class VecIndexOutOfBoundsWarning(n: Int, i: Long) extends EvalWarning {
  override def display: String =
    s"attempt to access index $i in vector of length $n"
}

/** Integer overflow occurred.
  *
  * @param n
  *   the non-overflowed value.
  * @param typ
  *   the type of the expression that overflowed.
  */
case class OverflowWarning(n: Long, typ: TyAnyInt) extends EvalWarning {
  override def display: String = s"value $n does not fit in type $typ"
}
