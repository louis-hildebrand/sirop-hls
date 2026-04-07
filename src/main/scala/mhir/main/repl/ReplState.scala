package mhir.main.repl

import mhir.ir._

case class ReplState(ctrlCCount: Int, variables: Map[Param, Expr]) {

  def resetCtrlCCount(): ReplState = {
    this.copy(ctrlCCount = 0)
  }

  def incrementCtrlCCount(): ReplState = {
    this.copy(ctrlCCount = this.ctrlCCount + 1)
  }

  def addVar(x: Param, v: Expr): ReplState = {
    this.copy(variables = this.variables + (x -> v))
  }
}
