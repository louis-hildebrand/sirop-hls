package mhir.ir

/** A full program defining a streaming accelerator.
  */
case class Program(
    constants: Seq[ConstDecl],
    accel: AccelDecl
) {

  /** The expression for the main accelerator.
    */
  def body: Expr = this.accel.body

  /** The name of the accelerator.
    */
  def name: String = this.accel.name

  def outName: Option[String] = {
    this.accel.annotations.get("out_name").collect({ case x: Param => x.name })
  }

  def clock: Option[String] = {
    this.accel.annotations.get("clock").collect({ case x: Param => x.name })
  }

  def reset: Option[String] = {
    this.accel.annotations.get("reset").collect({ case x: Param => x.name })
  }

  def handshake: Boolean = {
    !this.accel.annotations.contains("no_handshake")
  }
}

/** Companion object for [[Program]].
  */
object Program {

  /** Creates a [[Program]] with a default name and no other declarations.
    */
  def apply(e: Expr): Program = Program(Seq(), AccelDecl("top", e, Map()))

  def checkAnnotation(
      key: String,
      value: Option[Expr],
      err: String => Nothing
  ): Unit = {
    key match {
      case "clock" => expectIdent(key, value, err)
      case "no_handshake" =>
        value match {
          case None    => ()
          case Some(_) => err(s"unexpected value for annotation '$key'")
        }
      case "out_name" => expectIdent(key, value, err)
      case "reset"    => expectIdent(key, value, err)
      case _          => err(s"unknown annotation key: '$key'")
    }
  }

  private def expectIdent(
      key: String,
      value: Option[Expr],
      err: String => Nothing
  ): Unit = {
    value match {
      case Some(_: Param) => ()
      case None =>
        err(s"missing value for annotation '$key'. Expected an identifier.")
      case _ =>
        err(s"invalid value for annotation '$key'. Expected an identifier.")
    }
  }
}
