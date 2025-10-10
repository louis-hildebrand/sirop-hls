package mhir.main.aetherling

import mhir.main.shared.CompilerOptions
import os.Path

/** Parsed command-line arguments.
  *
  * @param inFile
  *   the path to the input Aetherling program.
  * @param options
  *   options to pass to the compiler.
  */
case class Args(inFile: Path, options: CompilerOptions)
