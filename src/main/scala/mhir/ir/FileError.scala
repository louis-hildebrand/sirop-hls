package mhir.ir

case class FileError(msg: String) extends RuntimeException(msg)
