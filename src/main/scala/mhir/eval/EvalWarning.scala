package mhir.eval

import mhir.ir._

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
  *   the number that was too large to fit within the target type.
  * @param typ
  *   the type of the expression that overflowed.
  */
case class OverflowWarning(n: Long, typ: TyAnyInt) extends EvalWarning {
  override def display: String = s"value $n does not fit in type $typ"
}

/** A consumer stream tried to read the data from a producer while the `ready`
  * expression was false.
  */
case class StmDataWithoutReady(x: Param) extends EvalWarning {
  override def display: String =
    s"attempt to read StmData($x) while ready = false"
}

case class UndefinedPrimitive(typ: Type) extends EvalWarning {
  override def display: String =
    s"attempt to evaluate the ${Undefined(typ)} primitive"
}
