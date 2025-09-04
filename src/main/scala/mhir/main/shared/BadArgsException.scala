package mhir.main.shared

/** Thrown to indicate that the given command-line arguments are invalid.
  *
  * @param msg
  *   an explanation of the problem.
  */
class BadArgsException(msg: String) extends RuntimeException(msg)
