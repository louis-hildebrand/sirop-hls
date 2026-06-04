package mhir.parse

/** Thrown to indicate that a program is syntactically invalid.
  *
  * @param msg
  *   a message to show to the programmer.
  * @param loc
  *   the location of the error in the source code.
  */
case class SyntaxError(msg: String)(val loc: Option[SourcePoint])
    extends RuntimeException(msg) {
  override def getMessage: String = {
    loc match {
      case Some(loc) => s"SyntaxError(${loc.line}:${loc.col}): $msg"
      case None      => s"SyntaxError: $msg"
    }
  }
}

/** Companion object for [[SyntaxError]].
  */
object SyntaxError {

  /** Creates a [[SyntaxError]] with no location information.
    */
  def apply(msg: String, loc: None.type): SyntaxError = {
    new SyntaxError(msg)(loc)
  }

  /** Creates a [[SyntaxError]] with the given location.
    */
  def apply(msg: String, loc: SourcePoint): SyntaxError = {
    new SyntaxError(msg)(Some(loc))
  }
}
