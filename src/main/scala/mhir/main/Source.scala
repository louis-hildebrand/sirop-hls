package mhir.main

import os.Path

sealed trait Source

case class SiropSource(inFile: Path) extends Source

case class AetherlingSource(inFile: Path) extends Source

case class StoredSource(program: String) extends Source
