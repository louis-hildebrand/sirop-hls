package ir

import org.scalatest.funsuite.AnyFunSuite

class TypeTests extends AnyFunSuite {
  test("AnnotatedFunction:TypedBody") {
    val f = TyInt ::+ (i => 2 * (i + 5))
    val expected = {
      val i = Param("i")()
      Function(i, Prod(2, Sum(i, 5)())())()
    }
    assert(f == expected)
    assert(f.typ == TyArrow(TyInt, TyInt))
  }

  test("AnnotatedFunction:UntypedBody") {
    val v = Param("v")()
    val f = TyInt ::+ (i => VecAccess(v, i)())
    val expected = {
      val i = Param("i")()
      Function(i, VecAccess(v, i)())()
    }
    assert(f == expected)
  }

  test("IsCompatibleWith:VecLength") {
    val n = Param("n")(TyInt)
    val v = Param("v")(TyVec(TyInt, n))
    val vLen = VecLength(v)(TyInt)
    assert(TyVec(TyInt, n) ~= TyVec(TyInt, vLen))
    assert(TyVec(TyInt, vLen) ~= TyVec(TyInt, n))

    val w = Param("w")(TyVec(TyInt, vLen))
    val wLen = VecLength(w)(TyInt)
    assert(TyVec(TyInt, n) ~= TyVec(TyInt, wLen))
    assert(TyVec(TyInt, wLen) ~= TyVec(TyInt, n))
    assert(TyVec(TyInt, vLen) ~= TyVec(TyInt, wLen))
    assert(TyVec(TyInt, wLen) ~= TyVec(TyInt, vLen))
  }

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
