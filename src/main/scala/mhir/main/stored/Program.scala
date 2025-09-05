package mhir.main.stored

import mhir.ir._
import mhir.main.shared.BadArgsException
import mhir.sugar.{StmMap, StmReduce, StmZip}
import mhir.sugar._

/** A collection of pre-written programs.
  */
object Program {

  def apply(name: String): Expr = {
    name.toLowerCase match {
      case "map" => Map
      case "dot" => Dot
      case "conv1d" => Conv1d
      case name =>
        throw new BadArgsException(s"unknown program: $name")
    }
  }

  /** Add 5 to every element of a stream.
    */
  private val Map: Expr = {
    val input = Param("I")(TyStm(U8, 200))
    Function(input, StmMap(input, U8 ::+ (x => x + 5))())()
  }

  /** Dot product of two streams.
    */
  private val Dot: Expr = {
    val I0 = Param("I0")(TyStm(U16, 840))
    val I1 = Param("I1")(TyStm(U16, 840))
    val zipped = StmZip(I0, I1)()
    val multiplied = StmMap(zipped, (U16, U16) ::+ (x => x.__0 * x.__1))()
    val dot = StmReduce(multiplied, (U16, U16) ::+ (x => x.__0 + x.__1))()
    Function(I0, Function(I1, dot)())()
  }

  /** 1-dimensional convolution.
    */
  private val Conv1d: Expr = {
    val kernel = VecLiteral(C(-1)(I8), C(0)(I8), C(1)(I8))()
    val input = Param("I")(TyStm(I8, 16))
    val slide = StmSlideV(input, 3)()
    val kernelStm = StmCst(14, kernel)()
    val s0 = StmZip(slide, kernelStm)()
    val s1 = StmMap(
      s0,
      (TyVec(I8, 3), TyVec(I8, 3)) ::+ (x => VecZip(x.__0, x.__1))
    )()
    val s2 = StmMap(
      s1,
      TyVec((I8, I8), 3) ::+ (v =>
        VecMap(v, (I8, I8) ::+ (x => x.__0 * x.__1))()
      )
    )()
    val s3 = StmMap(
      s2,
      TyVec(I8, 3) ::+ (v =>
        VecReduceComb(v, (I8, I8) ::+ (x => x.__0 + x.__1))()
      )
    )()
    val s4 = StmMap(s3, TyVec(I8, 1) ::+ (v => VecAccess(v, 0)()))()
    Function(input, s4)()
  }
}
