package mhir.parse

/** Thrown to indicate that a program is syntactically invalid.
  */
class SyntaxError(msg: String) extends RuntimeException(msg)
