package mhir.main.shared

import os.Path

/** What language to target and where to write the output files.
  */
sealed trait CompilerTarget

/** Don't write the output anywhere; skip code generation.
  */
object NullTarget extends CompilerTarget

/** Generate VHDL.
  *
  * @param outDir
  *   the directory in which to create the VHDL project.
  * @param overwrite
  *   what to if the output directory already exists. If `true`, then delete the
  *   existing directory. If `false`, throw an exception.
  */
case class VhdlTarget(outDir: Path, overwrite: Boolean) extends CompilerTarget
