package mhir.ir

case class OverflowException(n: Long, typ: Type)
    extends IllegalArgumentException(s"Value $n does not fit within type $typ.")
