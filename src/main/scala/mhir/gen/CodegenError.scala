package mhir.gen

case class CodegenError(msg: String) extends RuntimeException(msg) {

  override def getMessage: String = s"CodegenError: $msg"
}
