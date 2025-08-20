package mhir.ir

import mhir.ir.Lowering.TypeLowering
import org.scalatest.funsuite.AnyFunSuite

class TypeTests extends AnyFunSuite {
  private val n = Param("n")(U8)
  private val m = Param("m")(U8)
  private val k = Param("k")(U8)

  test("AnnotatedFunction:TypedBody") {
    val f = U8 ::+ (_ => IntCst(-1)(I32))
    val expected = Function(Param("_")(U8), IntCst(-1)(I32))()
    assert(f == expected)
    assert(f.typ == TyArrow(U8, I32))
  }

  test("AnnotatedFunction:UntypedBody") {
    val v = Param("v")()
    val f = U8 ::+ (i => VecAccess(v, i)())
    val expected = {
      val i = Param("i")()
      Function(i, VecAccess(v, i)())()
    }
    assert(f == expected)
  }

  test("U8 -> I8") {
    assert(U8 ->: I8 == TyArrow(U8, I8))
  }

  test("U8 -> U16 -> U32") {
    assert(U8 ->: U16 ->: U32 == TyArrow(U8, TyArrow(U16, U32)))
  }

  test("(I8 -> I16) -> I32") {
    assert((I8 ->: I16) ->: I32 == TyArrow(TyArrow(I8, I16), I32))
  }

  test("ToString:Arrow") {
    val t = (U8 ->: U16) ->: U16 ->: TyTuple(TyBool, I32)
    val expected = "(u8 -> u16) -> u16 -> (bool, i32)"
    assert(t.toString == expected)
  }

  test("IsCompatibleWith:Int") {
    val intTypes = Seq(U8, U16, U32, I8, I16, I32)
    for (t <- intTypes) {
      assert(t ~= t)
    }
    for (i <- intTypes.indices) {
      for (j <- intTypes.indices) {
        if (i != j) {
          assert(!(intTypes(i) ~= intTypes(j)))
        }
      }
    }
  }

  test("IsCompatibleWith:Fix") {
    val types = Seq(TyFix(U8, 7), TyFix(U8, 9), TyFix(U16, 7))
    for (t <- types) {
      assert(t ~= t)
    }
    for (i <- types.indices) {
      for (j <- types.indices) {
        if (i != j) {
          assert(!(types(i) ~= types(j)))
        }
      }
    }
  }

  test("IsCompatibleWith:Vec") {
    assert(TyVec(I16, n) ~= TyVec(I16, n))
    assert(TyVec(U8, m) ~= TyVec(U8, m))
    assert(TyVec(I16, n) ~= TyVec(I16, n + C(2)(U8) + C(-2)(I8)))
  }

  test("MinInt:SInt") {
    assert(TySInt(1).minInt == -1)
    assert(TySInt(2).minInt == -2)
    assert(TySInt(3).minInt == -4)
    assert(TySInt(4).minInt == -8)
    assert(TySInt(5).minInt == -16)
  }

  test("MinInt:UInt") {
    for (w <- 0 to 10) {
      assert(TyUInt(w).minInt == 0)
    }
  }

  test("MaxInt:SInt") {
    assert(TySInt(1).maxInt == 0)
    assert(TySInt(2).maxInt == 1)
    assert(TySInt(3).maxInt == 3)
    assert(TySInt(4).maxInt == 7)
    assert(TySInt(5).maxInt == 15)
  }

  test("MaxInt:UInt") {
    assert(TyUInt(0).maxInt == 0)
    assert(TyUInt(1).maxInt == 1)
    assert(TyUInt(2).maxInt == 3)
    assert(TyUInt(3).maxInt == 7)
    assert(TyUInt(4).maxInt == 15)
    assert(TyUInt(5).maxInt == 31)
  }

  test("LowerType:NonStreams") {
    val t = TyTuple(
      Missing,
      U8,
      I16,
      TyBool,
      TyArrow(I8, I32),
      TyVec(TyVec(U32, 5), 4)
    )
    assert(t.lower == t)
  }

  test("LowerType:Stm[Int]") {
    val t = TyStm(U8, n)
    assert(t.lower == t)
  }

  test("LowerType:Vec[Int]") {
    val t = TyVec(U8, n)
    assert(t.lower == t)
  }

  test("LowerType:Stm[Stm[Bool]]") {
    val t = TyStm(TyStm(TyBool, m), n)
    assert(t.lower == TyStm(TyBool, Prod(PadTo(n, 16)(), PadTo(m, 16)())()))
  }

  test("LowerType:Stm[Stm[Int, 3], 4]") {
    val t = TyStm(TyStm(U8, 3), 4)
    val n = t.lower.asInstanceOf[TyStm].n
    assert(mhir.ir.eval(n) == C(12)())
  }

  test("LowerType:Stm[Vec[Int]]") {
    val t = TyStm(TyVec(U8, m), n)
    assert(t.lower == t)
  }

  test("LowerType:Vec[Stm[Int]]") {
    val t = TyVec(TyStm(U16, m), n)
    assert(t.lower == TyStm(TyVec(U16, n), m))
  }

  test("LowerType:Vec[Vec[Int]]") {
    val t = TyVec(TyVec(U32, m), n)
    assert(t.lower == t)
  }

  test("LowerType:Stm[Stm[Stm[Int]]]") {
    val t = TyStm(TyStm(TyStm(TyBool, k), m), n)
    val expected = TyStm(
      TyBool,
      Prod(
        PadTo(Prod(PadTo(k, 16)(), PadTo(m, 16)())(), 24)(),
        PadTo(n, 24)()
      )()
    )
    assert(t.lower == expected)
  }

  test("LowerType:Stm[Stm[Vec[Int]]]") {
    val t = TyStm(TyStm(TyVec(I8, k), m), n)
    val expected = TyStm(TyVec(I8, k), Prod(PadTo(n, 16)(), PadTo(m, 16)())())
    assert(t.lower == expected)
  }

  test("LowerType:Stm[Vec[Stm[Int]]]") {
    val t = TyStm(TyVec(TyStm(I16, k), m), n)
    val expected = TyStm(TyVec(I16, m), Prod(PadTo(n, 16)(), PadTo(k, 16)())())
    assert(t.lower == expected)
  }

  test("LowerType:Stm[Vec[Vec[Int]]]") {
    val t = TyStm(TyVec(TyVec(I32, k), m), n)
    assert(t.lower == t)
  }

  test("LowerType:Vec[Stm[Stm[Int]]]") {
    val t = TyVec(TyStm(TyStm(U8, k), m), n)
    val expected = TyStm(TyVec(U8, n), Prod(PadTo(m, 16)(), PadTo(k, 16)())())
    assert(t.lower == expected)
  }

  test("LowerType:Vec[Stm[Vec[Int]]]") {
    val t = TyVec(TyStm(TyVec(U16, k), m), n)
    assert(t.lower == TyStm(TyVec(TyVec(U16, k), n), m))
  }

  test("LowerType:Vec[Vec[Stm[Int]]]") {
    val t = TyVec(TyVec(TyStm(U32, k), m), n)
    assert(t.lower == TyStm(TyVec(TyVec(U32, m), n), k))
  }

  test("LowerType:Vec[Vec[Vec[Int]]]") {
    val t = TyVec(TyVec(TyVec(U8, k), m), n)
    assert(t.lower == t)
  }
}
