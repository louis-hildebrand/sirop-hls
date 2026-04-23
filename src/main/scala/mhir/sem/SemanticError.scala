package mhir.sem

case class SemanticError(msg: String) extends RuntimeException(msg) {
  override def getMessage: String = s"SemanticError: $msg"
}
