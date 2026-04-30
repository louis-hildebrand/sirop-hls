package mhir.gen.vhdl
package test

import mhir.ir.Param

/** Inputs and expected outputs for testing a VHDL design, where the inputs are
  * associated with parameters (rather than passed by position).
  *
  * @param inputs
  *   the inputs to provide to the design.
  * @param expectedOutput
  *   the expected output.
  */
case class KeywordTestIO(
    inputs: Map[Param, TestInput],
    expectedOutput: TestOutput
)
