package mhir.ir

case class FileError(msg: String) extends RuntimeException(msg) {

  override def getMessage: String = {
    s"FileError: $msg"
  }
}
