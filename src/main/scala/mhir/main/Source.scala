package mhir.main

import os.Path
import mhir.ir._

sealed trait Source

case class AetherlingSource(inFile: Path) extends Source

case class StoredSource(program: Expr) extends Source
