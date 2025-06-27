package mhir.ir

/** Thrown to indicate that integer overflow has occurred.
  *
  * @param n
  *   the number that was too large to fit within the target type.
  * @param typ
  *   the type of the expression that overflowed.
  */
case class OverflowException(n: Long, typ: Type)
    extends IllegalArgumentException(s"Value $n does not fit within type $typ.")
