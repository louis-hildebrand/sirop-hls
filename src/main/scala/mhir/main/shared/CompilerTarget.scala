package mhir.main.shared

import os.Path

/** What language to target and where to write the output files.
  */
sealed trait CompilerTarget

/** Don't send the program anywhere, just return it.
  *
  * This is useful for test code that invokes [[mhir.main.shared.Compiler]]
  * directly, but should not be used at the command line.
  */
object NullTarget extends CompilerTarget

/** Evaluate the program and print its value.
  */
case class EvalTarget(maxInvalidSteps: Option[Int]) extends CompilerTarget

/** Run the tests in the Sirop file and print the results.
  */
object TestTarget extends CompilerTarget

/** Pretty-print the program.
  *
  * @param dest
  *   where to send the pretty-printed program.
  */
case class PrettyPrintTarget(dest: PrettyPrintDestination, overwrite: Boolean)
    extends CompilerTarget

/** Generate VHDL.
  *
  * @param outDir
  *   the directory in which to create the VHDL project.
  * @param overwrite
  *   what to if the output directory already exists. If `true`, then delete the
  *   existing directory. If `false`, throw an exception.
  */
case class VhdlTarget(outDir: Path, overwrite: Boolean) extends CompilerTarget

/** Report the compile time.
  *
  * @param outFile
  *   the path to the file in which to write the report.
  * @param overwrite
  *   what to do if the file already exists. If `true`, then delete the existing
  *   file. If `false`, throw an exception.
  */
case class CompileTimeTarget(outFile: Path, overwrite: Boolean)
    extends CompilerTarget
