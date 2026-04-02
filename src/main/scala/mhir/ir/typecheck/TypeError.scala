package mhir.ir
package typecheck

/** Thrown to say that an expression is ill-typed.
  *
  * @param msg
  *   an explanation of the error.
  */
class TypeError(msg: String) extends RuntimeException(msg) {
  override def getMessage: String = s"TypeError: $msg"
}
