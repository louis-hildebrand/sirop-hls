package mhir.main.stored

import mhir.ir._
import mhir.main.shared.BadArgsException
import mhir.sugar.{StmMap, StmReduce, StmZip}

/** A collection of pre-written programs.
  */
object Program {

  def apply(name: String): Expr = {
    name.toLowerCase match {
      case "map" => Map
      case "dot" => Dot
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

  /** A function that computes the dot product of two streams.
    */
  private val Dot: Expr = {
    val I0 = Param("I0")(TyStm(U16, 840))
    val I1 = Param("I1")(TyStm(U16, 840))
    val zipped = StmZip(I0, I1)()
    val multiplied = StmMap(zipped, (U16, U16) ::+ (x => x.__0 * x.__1))()
    val dot = StmReduce(multiplied, (U16, U16) ::+ (x => x.__0 + x.__1))()
    Function(I0, Function(I1, dot)())()
  }
}
