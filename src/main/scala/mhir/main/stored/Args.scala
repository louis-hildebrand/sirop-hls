package mhir.main.stored

import mhir.main.shared.CompilerOptions

/** Parsed command-line options.
  *
  * @param program
  *   the name of the program to compile.
  * @param options
  *   compiler options.
  */
case class Args(program: String, options: CompilerOptions)
