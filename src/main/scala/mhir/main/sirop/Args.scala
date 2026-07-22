package mhir.main.sirop

import mhir.main.shared.CompilerOptions
import os.Path

/** Parsed command-line arguments.
  *
  * @param inFile
  *   the path to the input program.
  * @param constOverrides
  *   new values provided via the command line to override the values of const
  *   declarations in the source code.
  * @param options
  *   options to pass to the compiler.
  */
case class Args(
    inFile: Path,
    constOverrides: Map[String, String],
    options: CompilerOptions
)
