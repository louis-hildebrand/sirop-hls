package mhir.main.shared

import ch.qos.logback.classic.Level
import mhir.optimize.OptimizerOptions
import os.Path

/** Options for the compiler.
  *
  * @param targets
  *   the compilation targets.
  * @param optFlags
  *   optimization settings.
  */
case class CompilerOptions(
    targets: Set[CompilerTarget],
    optFlags: OptimizerOptions,
    logLevel: Option[Level] = None
)
