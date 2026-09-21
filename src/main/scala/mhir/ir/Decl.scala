package mhir.ir

/** A declaration that could appear in a test suite.
  */
sealed trait TestDecl {

  def alphaEquals(that: TestDecl): Boolean
}

/** A constant declaration.
  *
  * @param x
  *   parameter for the constant (i.e, its name and type).
  * @param e
  *   the value of the constant.
  */
case class ConstDecl(x: Param, e: Expr) extends TestDecl {

  override def alphaEquals(that: TestDecl): Boolean = {
    that match {
      case ConstDecl(x, e) => x == this.x && (e alphaEquals this.e)
      case _               => false
    }
  }
}

/** The main accelerator declaration.
  *
  * @param name
  *   the name of the accelerator.
  * @param body
  *   the expression describing the accelerator.
  * @param annotations
  *   annotations (e.g., giving the names of the ports in the top-level entity).
  * @param annotationsByParam
  *   annotations associated with a specific parameter.
  */
case class AccelDecl(
    name: String,
    body: Expr,
    annotations: Map[String, Expr],
    annotationsByParam: Map[(String, Param), Expr]
) {

  def alphaEquals(that: AccelDecl): Boolean = {
    that match {
      case AccelDecl(name, body, annotations, annotationsByParam) =>
        name == this.name &&
        (body alphaEquals this.body) &&
        (annotations.keySet == this.annotations.keySet) &&
        annotations
          .forall({ case (x, e) => e alphaEquals this.annotations(x) }) &&
        (annotationsByParam.keySet == this.annotationsByParam.keySet) &&
        annotationsByParam
          .forall({ case (x, e) => e alphaEquals this.annotationsByParam(x) })
      case _ => false
    }
  }
}

/** One assertion in a user-defined test suite.
  *
  * @param inputs
  *   the inputs to provide to the accelerator.
  * @param expectedOutput
  *   the expected output from the accelerator.
  * @param ignore
  *   the parts of the output to ignore.
  * @param prefixCondition
  *   condition that all elements of the physical prefix must satisfy.
  */
case class Assertion(
    inputs: Map[Param, Expr],
    expectedOutput: Expr,
    ignore: Option[Expr],
    prefixCondition: Option[Expr]
) extends TestDecl {

  override def alphaEquals(that: TestDecl): Boolean = {
    that match {
      case Assertion(inputs, expectedOutput, ignore, prefixCondition) =>
        (inputs.keySet == this.inputs.keySet) &&
        inputs.forall({ case (x, e1) => e1 alphaEquals this.inputs(x) }) &&
        (expectedOutput alphaEquals this.expectedOutput) &&
        ((ignore.isEmpty && this.ignore.isEmpty) ||
          (ignore.get alphaEquals this.ignore.get)) &&
        ((prefixCondition.isEmpty && this.prefixCondition.isEmpty) ||
          (prefixCondition.get alphaEquals this.prefixCondition.get))
      case _ => false
    }
  }
}
