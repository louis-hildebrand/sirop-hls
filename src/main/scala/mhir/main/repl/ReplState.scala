package mhir.main.repl

import mhir.ir._

case class ReplState(
    handshake: Boolean,
    showPrefix: Boolean,
    ctrlCCount: Int,
    private val variables: Map[Param, Expr]
) {

  private val handshakeParam = Param("__handshake", -1)(TyBool)
  private val showPrefixParam = Param("__show_prefix", -1)(TyBool)
  // Alias for __show_prefix
  // Keep it around for backwards-compatibility
  private val showPhysicalParam = Param("__show_physical", -1)(TyBool)

  def typingContext: Map[Param, Type] = {
    this.variables
      .map({ case (x, v) => x -> v.typ })
      .+(handshakeParam -> this.handshakeParam.typ)
      .+(showPrefixParam -> this.showPrefixParam.typ)
      .+(showPhysicalParam -> this.showPhysicalParam.typ)
  }

  def env: Map[Param, Expr] = {
    this.variables
      .+(handshakeParam -> (if (this.handshake) True else False))
      .+(showPrefixParam -> (if (this.showPrefix) True else False))
      .+(showPhysicalParam -> (if (this.showPrefix) True else False))
  }

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
