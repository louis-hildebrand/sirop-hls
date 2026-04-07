package mhir.main.repl

case class ReplState(ctrlCCount: Int = 0) {

  def resetCtrlCCount(): ReplState = this.copy(ctrlCCount = 0)

  def incrementCtrlCCount(): ReplState = this.copy(ctrlCCount = ctrlCCount + 1)
}
