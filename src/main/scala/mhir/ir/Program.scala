package mhir.ir

/** A full program defining a streaming accelerator.
  *
  * @param name
  *   the name of the top-level entity.
  * @param e
  *   the expression describing the accelerator.
  */
case class Program(name: String, constants: Seq[ConstDecl], e: Expr)

/** Companion object for [[Program]].
  */
object Program {

  /** Creates a [[Program]] with the default name.
    */
  def apply(e: Expr): Program = Program("top", Seq(), e)
}
