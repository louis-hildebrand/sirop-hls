package mhir.gen

import mhir.ir._
import mhir.ir.typecheck.TypeCheck

/** A sequence of outputs that a design is expected to produce.
  */
sealed trait TestOutput {

  /** The type of the elements within the output stream.
    */
  def elemTyp: Type
}

/** A sequence of expected outputs to hard-code into the testbench source code.
  *
  * @param elems
  *   the expected sequence of <i>valid</i> outputs.
  */
case class DirectTestOutput(elems: Seq[Expr]) extends TestOutput {
  override def elemTyp: Type = {
    this.elems.head.tchk().typ
  }
}
