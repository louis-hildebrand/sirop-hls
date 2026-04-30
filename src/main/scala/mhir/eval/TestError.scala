package mhir.eval

/** An error related to running the user-defined test suite.
  */
case class TestError(msg: String) extends RuntimeException(msg) {

  override def getMessage: String = s"TestError: $msg"
}
