package mhir.main.stored

import mhir.ir._
import mhir.sugar.StmMap

/** A collection of pre-written programs.
  */
object Program {

  def apply(name: String): Expr = {
    name.toLowerCase match {
      case "map" => Map
      case name =>
        throw new BadArgsException(s"unknown program: $name")
    }
  }

  /** A function that adds 5 to every element of a stream.
    */
  private val Map: Expr = {
    val input = Param("I")(TyStm(U8, 200))
    Function(input, StmMap(input, U8 ::+ (x => x + 5))())()
  }
}
