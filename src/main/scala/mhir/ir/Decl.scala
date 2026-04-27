package mhir.ir

/** A constant declaration.
  *
  * @param x
  *   parameter for the constant (i.e, its name and type).
  * @param e
  *   the value of the constant.
  */
case class ConstDecl(x: Param, e: Expr)

/** The main accelerator declaration.
  *
  * @param name
  *   the name of the accelerator.
  * @param body
  *   the expression describing the accelerator.
  * @param annotations
  *   annotations (e.g., giving the names of the ports in the top-level entity).
  */
case class AccelDecl(name: String, body: Expr, annotations: Map[String, Expr])
