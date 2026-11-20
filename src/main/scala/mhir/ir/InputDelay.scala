package mhir.ir

/** The delay annotation for an input stream.
  */
sealed trait InputDelay {

  // TODO: Extract this to a trait called CanMap or something?
  //       See https://stackoverflow.com/questions/57200316/use-self-type-as-return-type-in-scala-trait
  def map(f: Expr => Expr): InputDelay

  override def toString: String = {
    this match {
      case InputDelayConcrete(IntCst(n)) => n.toString // omit type annotation
      case InputDelayConcrete(n)         => n.toString
      case InputDelayAuto                => "?"
    }
  }
}

/** The delay annotation is a concrete value.
  */
case class InputDelayConcrete(n: Expr) extends InputDelay {

  override def map(f: Expr => Expr): InputDelay = {
    InputDelayConcrete(f(this.n))
  }
}

/** The delay annotation should be chosen automatically by the compiler.
  */
object InputDelayAuto extends InputDelay {

  override def map(f: Expr => Expr): InputDelay = {
    InputDelayAuto
  }
}
