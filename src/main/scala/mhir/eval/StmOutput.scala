package mhir.eval

import mhir.ir._

sealed trait StmOutput

object NoOutput extends StmOutput

case class PhysicalOutput(e: Expr) extends StmOutput

case class LogicalOutput(e: Expr) extends StmOutput
