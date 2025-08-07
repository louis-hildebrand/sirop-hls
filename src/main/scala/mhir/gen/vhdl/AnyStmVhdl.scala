package mhir.gen.vhdl

import mhir.ir._

/** VHDL converter for any stream producer.
  */
private[vhdl] object AnyStmVhdl {

  /** Converts a stream-producing expression to a VHDL component.
    *
    * @param e
    *   the streaming expression to convert.
    * @param inputs
    *   variables representing the stream producers feeding into this node.
    * @param name
    *   the name to use for the VHDL component. If the provided name is blank,
    *   one will be chosen automatically.
    */
  private[vhdl] def apply(
      e: Expr,
      inputs: Set[Param],
      name: String = ""
  ): CustomVhdlComponent = {
    val realName = if (name.isBlank) nameStreamUnit(e) else name
    e match {
      case stm: StmBuild =>
        StmBuildVhdl(stm, inputs, realName)
      case let: LetStm =>
        LetStmVhdl(let, inputs, realName)
      case e =>
        throw new IllegalArgumentException(
          "Cannot generate component for expression."
            + " A program must be a StmBuild, a LetStm, or a function that returns a program."
            + s" The expression is $e."
        )
    }
  }

  private def nameStreamUnit(e: Expr): String = {
    val prefix = e match {
      case _: StmBuild => "sbuild"
      case _: LetStm   => "let_stm"
      case _           => "stm"
    }
    Param(prefix)().name
  }
}
