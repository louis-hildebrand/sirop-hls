package mhir.main.stored

import mhir.ir.Expr
import mhir.main.shared.{BadArgsException, CompilerOptions, HelpException}

/** Parsed command-line options.
  *
  * @param program
  *   the expression to compile.
  * @param options
  *   compiler options.
  */
case class Args(program: Expr, options: CompilerOptions)
