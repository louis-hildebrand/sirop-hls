package mhir.main.shared

import ch.qos.logback.classic.Level
import mhir.gen.vhdl.VhdlGeneratorOptions
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
    vhdl: VhdlGeneratorOptions = VhdlGeneratorOptions(),
    logLevel: Option[Level] = None
)
