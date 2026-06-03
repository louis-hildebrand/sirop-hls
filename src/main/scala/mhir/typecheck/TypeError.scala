package mhir.typecheck

import mhir.ir._

/** Thrown to say that an expression is ill-typed.
  *
  * @param msg
  *   an explanation of the error.
  */
class TypeError(msg: String, relevantConstValues: Map[Param, Expr] = Map())
    extends RuntimeException(msg) {

  override def getMessage: String = {
    val constValuesNote = if (relevantConstValues.isEmpty) {
      ""
    } else {
      val str =
        relevantConstValues.map({ case (x, e) => s"$x = $e" }).mkString(", ")
      s" (note: relevant constants include $str)"
    }
    s"TypeError: $msg$constValuesNote"
  }
}
