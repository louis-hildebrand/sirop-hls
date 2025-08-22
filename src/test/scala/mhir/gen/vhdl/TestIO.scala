package mhir.gen.vhdl

/** Inputs and expected outputs for testing a VHDL design.
  *
  * @param inputs
  *   the inputs to provide to the design.
  * @param expectedOutput
  *   the expected output.
  */
case class TestIO(inputs: Seq[TestInput], expectedOutput: TestOutput)
