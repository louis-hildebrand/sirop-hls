package ir

import org.scalatest.funsuite.AnyFunSuite

class TypeTests extends AnyFunSuite {
  test("Flatten:NonStreams") {
    val t = TyTuple(
      Missing,
      TyInt,
      TyBool,
      TyArrow(TyInt, TyInt),
      TyVec(TyVec(TyInt, 5), 4)
    )
    assert(t.flat == t)
  }

  test("Flatten:1DStream") {
    val n = Param("n")()
    val t = TyStm(TyInt, n)
    assert(t.flat == t)
  }

  test("Flatten:2DStream") {
    val n = Param("n")()
    val m = Param("m")()
    val t = TyStm(TyStm(TyBool, m), n)
    assert(t.flat == TyStm(TyBool, n * m))
  }

  test("Flatten:3DStream") {
    val n = Param("n")()
    val m = Param("m")()
    val k = Param("k")()
    val t = TyStm(TyStm(TyStm(TyBool, k), m), n)
    assert(t.flat == TyStm(TyBool, n * m * k))
  }
}
