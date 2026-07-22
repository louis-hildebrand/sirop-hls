package mhir.typecheck

case class NameError(msg: String) extends RuntimeException(msg) {

  override def getMessage: String = s"NameError: $msg"
}
