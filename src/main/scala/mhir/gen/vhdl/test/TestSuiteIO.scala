package mhir.gen.vhdl
package test

import mhir.ir._

/** Inputs and outputs for a series of test cases.
  */
case class TestSuiteIO(tests: Seq[KeywordTestIO]) {
  def outElemTyp: Type = tests.head.expectedOutput.elemTyp
  def params: Seq[Param] = tests.head.inputs.keys.toSeq
}
