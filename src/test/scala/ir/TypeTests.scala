package ir

import org.scalatest.funsuite.AnyFunSuite

class TypeTests extends AnyFunSuite {
  private val n = Param("n")(TyInt)
  private val m = Param("m")(TyInt)
  private val k = Param("k")(TyInt)

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

  test("LowerType:NonStreams") {
    val t = TyTuple(
      Missing,
      TyInt,
      TyBool,
      TyArrow(TyInt, TyInt),
      TyVec(TyVec(TyInt, 5), 4)
    )
    assert(t.lower == t)
  }

  test("LowerType:Stm[Int]") {
    val t = TyStm(TyInt, n)
    assert(t.lower == t)
  }

  test("LowerType:Vec[Int]") {
    val t = TyVec(TyInt, n)
    assert(t.lower == t)
  }

  test("LowerType:Stm[Stm[Int]]") {
    val t = TyStm(TyStm(TyBool, m), n)
    assert(t.lower == TyStm(TyBool, n * m))
  }

  test("LowerType:Stm[Vec[Int]]") {
    val t = TyStm(TyVec(TyInt, m), n)
    assert(t.lower == t)
  }

  test("LowerType:Vec[Stm[Int]]") {
    val t = TyVec(TyStm(TyInt, m), n)
    assert(t.lower == TyStm(TyVec(TyInt, n), m))
  }

  test("LowerType:Vec[Vec[Int]]") {
    val t = TyVec(TyVec(TyInt, m), n)
    assert(t.lower == t)
  }

  test("LowerType:Stm[Stm[Stm[Int]]]") {
    val t = TyStm(TyStm(TyStm(TyBool, k), m), n)
    assert(t.lower == TyStm(TyBool, n * m * k))
  }

  test("LowerType:Stm[Stm[Vec[Int]]]") {
    val t = TyStm(TyStm(TyVec(TyInt, k), m), n)
    assert(t.lower == TyStm(TyVec(TyInt, k), n * m))
  }

  test("LowerType:Stm[Vec[Stm[Int]]]") {
    val t = TyStm(TyVec(TyStm(TyInt, k), m), n)
    assert(t.lower == TyStm(TyVec(TyInt, m), n * k))
  }

  test("LowerType:Stm[Vec[Vec[Int]]]") {
    val t = TyStm(TyVec(TyVec(TyInt, k), m), n)
    assert(t.lower == t)
  }

  test("LowerType:Vec[Stm[Stm[Int]]]") {
    val t = TyVec(TyStm(TyStm(TyInt, k), m), n)
    assert(t.lower == TyStm(TyVec(TyInt, n), m * k))
  }

  test("LowerType:Vec[Stm[Vec[Int]]]") {
    val t = TyVec(TyStm(TyVec(TyInt, k), m), n)
    assert(t.lower == TyStm(TyVec(TyVec(TyInt, k), n), m))
  }

  test("LowerType:Vec[Vec[Stm[Int]]]") {
    val t = TyVec(TyVec(TyStm(TyInt, k), m), n)
    assert(t.lower == TyStm(TyVec(TyVec(TyInt, m), n), k))
  }

  test("LowerType:Vec[Vec[Vec[Int]]]") {
    val t = TyVec(TyVec(TyVec(TyInt, k), m), n)
    assert(t.lower == t)
  }
}
