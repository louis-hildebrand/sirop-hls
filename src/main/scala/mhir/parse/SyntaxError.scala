package mhir.parse

/** Thrown to indicate that a program is syntactically invalid.
  */
class SyntaxError(msg: String) extends RuntimeException(msg) {
  override def getMessage: String = {
    s"SyntaxError: $msg"
  }
}
