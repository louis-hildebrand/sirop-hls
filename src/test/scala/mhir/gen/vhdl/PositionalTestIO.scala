package mhir.gen.vhdl

/** Inputs and expected outputs for testing a VHDL design, with the inputs
  * passed in positional format.
  *
  * @param inputs
  *   the inputs to provide to the design.
  * @param expectedOutput
  *   the expected output.
  */
case class PositionalTestIO(inputs: Seq[TestInput], expectedOutput: TestOutput)
