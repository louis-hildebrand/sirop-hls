package mhir.main.sirop

import mhir.main.shared.CompilerOptions
import os.Path

/** Parsed command-line arguments.
  *
  * @param inFile
  *   the path to the input program.
  * @param options
  *   options to pass to the compiler.
  */
case class Args(inFile: Path, options: CompilerOptions)
