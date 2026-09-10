package mhir.ir

/** A full program defining a streaming accelerator.
  */
case class Program(
    constants: Seq[ConstDecl],
    accel: AccelDecl,
    test: Seq[TestDecl]
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

  def go: Option[Param] = {
    this.accel.annotations
      .get("go")
      .map({
        case x: Param => x
        case e =>
          throw new AssertionError(
            s"value for annotation 'go' is not a variable: $e"
          )
      })
  }

  def handshake: Boolean = {
    !this.accel.annotations.contains("no_handshake")
  }

  def headByParam: Map[Param, Expr] = {
    this.accel.annotationsByParam.collect({ case (("head", x), e) => x -> e })
  }
}

/** Companion object for [[Program]].
  */
object Program {

  /** Creates a [[Program]] with a default name and no other declarations.
    */
  def apply(e: Expr): Program = {
    Program(Seq(), AccelDecl("top", e, Map(), Map()), Seq())
  }

  def checkAnnotation(
      key: String,
      param: Option[Param],
      value: Option[Expr],
      err: String => Nothing
  ): Unit = {
    key match {
      case "clock" => expectIdentWithoutParam(key, param, value, err)
      case "no_handshake" =>
        value match {
          case None    => ()
          case Some(_) => err(s"unexpected value for annotation '$key'")
        }
      case "out_name" => expectIdentWithoutParam(key, param, value, err)
      case "reset"    => expectIdentWithoutParam(key, param, value, err)
      case "go"       => expectIdentWithoutParam(key, param, value, err)
      case "head"     => expectAnyWithParam(key, param, value, err)
      case _          => err(s"unknown annotation key: '$key'")
    }
  }

  private def expectIdentWithoutParam(
      key: String,
      param: Option[Param],
      value: Option[Expr],
      err: String => Nothing
  ): Unit = {
    param match {
      case None    => ()
      case Some(x) => err(s"unexpected parameter '$x' for annotation '$key'")
    }
    value match {
      case Some(_: Param) => ()
      case None =>
        err(s"missing value for annotation '$key'. Expected an identifier.")
      case _ =>
        err(s"invalid value for annotation '$key'. Expected an identifier.")
    }
  }

  private def expectAnyWithParam(
      key: String,
      maybeParam: Option[Param],
      value: Option[Expr],
      err: String => Nothing
  ): Unit = {
    val param = maybeParam match {
      case Some(x) => x
      case None    => err(s"missing parameter for annotation '$key'")
    }
    value match {
      case Some(_) => ()
      case None    => err(s"missing value for annotation '$key($param)'")
    }
  }
}
