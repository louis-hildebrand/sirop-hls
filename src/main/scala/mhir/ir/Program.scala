package mhir.ir

/** A full program defining a streaming accelerator.
  *
  * @param name
  *   the name of the top-level entity.
  * @param e
  *   the expression describing the accelerator.
  */
case class Program(
    name: String,
    annotations: Map[String, Expr],
    constants: Seq[ConstDecl],
    e: Expr
) {

  def outName: Option[String] = {
    this.annotations.get("out_name").collect({ case x: Param => x.name })
  }
}

/** Companion object for [[Program]].
  */
object Program {

  /** Creates a [[Program]] with the default name.
    */
  def apply(e: Expr): Program = Program("top", Map(), Seq(), e)

  def checkAnnotation(
      key: String,
      value: Option[Expr],
      err: String => Nothing
  ): Unit = {
    key match {
      case "out_name" =>
        value match {
          case Some(_: Param) => ()
          case None =>
            err(s"missing value for annotation '$key'. Expected an identifier.")
          case _ =>
            err(s"invalid value for annotation '$key'. Expected an identifier.")
        }
      case _ => err(s"unknown annotation key: '$key'")
    }
  }
}
