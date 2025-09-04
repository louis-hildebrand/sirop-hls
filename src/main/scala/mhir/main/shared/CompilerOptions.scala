package mhir.main.shared

/** Options for the compiler.
  *
  * @param optimize
  *   whether optimization is enabled.
  * @param showFinal
  *   whether to print the final expression passed to the code generator.
  * @param target
  *   the target language.
  */
case class CompilerOptions(
    optimize: Boolean,
    showFinal: Boolean,
    target: CompilerTarget
)
