package mhir.main.shared

import os.Path

sealed trait PrettyPrintDestination
object PPStdout extends PrettyPrintDestination
case class PPFile(path: Path) extends PrettyPrintDestination
