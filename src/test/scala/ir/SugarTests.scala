package ir

import org.scalatest.funsuite.AnyFunSuite

class SugarTests extends AnyFunSuite {
  test("Substitute:[y / x](let x = x * y in x + y)") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val e = Let(x, x * y, x + y)()
    val actual = e.subPreserveType(x -> y)
    val expected = Let(x, y * y, x + y)()
    assert(actual == expected)
  }

  test("Substitute:[y / x](let y = x * y in x + y)") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val e = Let(y, x * y, x + y)()
    val actual = e.subPreserveType(x -> y)
    val y2 = Param("y2")(U8)
    val expected = Let(y2, y * y, y + y2)()
    assert(actual == expected)
  }

  test("Let:Equals") {
    val e1 = {
      val x = Param("x")(U8)
      Let(x, 42, (x + 1) * (x + 2))()
    }
    val e2 = {
      val y = Param("y")()
      Let(y, 42, (y + 1) * (y + 2))()
    }
    assert(e1 == e2)
    assert(e2 == e1)
    assert(e1.hashCode() == e2.hashCode())
  }

  test("Let:NotEquals:DifferentBody") {
    val x = Param("x")(U8)
    val e1 = Let(x, 42, (x + 1) * (x + 2))()
    val y = Param("y")(U8)
    val e2 = Let(y, 42, (y + 1) * (x + 2))()
    assert(e1 != e2)
    assert(e2 != e1)
  }

  test("Let:NotEquals:DifferentArg") {
    val x = Param("x")(U8)
    val e1 = Let(x, 42, (x + 1) * (x + 2))()
    val e2 = Let(x, 43, (x + 1) * (x + 2))()
    assert(e1 != e2)
    assert(e2 != e1)
  }

  test("ReshapeData:i0 to u0") {
    val i0 = TySInt(0)
    assert(ReshapeData.canReshape(i0, U0))
    assert(ReshapeData.canReshape(i0, U8))
    assert(ReshapeData.canReshape(U0, i0))
    assert(Type.supertype(i0, U0).contains(U0))
    assert(Type.supertype(U0, i0).contains(U0))
    assert(Type.supertype(i0, i0).contains(U0))
    assert(Type.supertype(Seq(i0, U0)).contains(U0))
    assert(Type.supertype(Seq(U0, i0)).contains(U0))
    assert(Type.supertype(Seq(i0, U0, U0)).contains(U0))
  }

  test("ReshapeData:Valid") {
    val x = Param("x")()
    val e =
      (t1: Type, t2: Type) => ReshapeData(x.rebuild(t1), t2)().tchk().lower()

    assert(e(U8, U16) == PadTo(x, 16)())
    assert(e(I8, I32) == PadTo(x, 32)())
    assert(e(U8, I16) == PadTo(ToSigned(x)(), 16)())
    assert(
      e(TyTuple(U8, U16), TyTuple(U16, U32))
        == Tuple(PadTo(x.__0, 16)(), PadTo(x.__1, 32)())()
    )
    assert(
      e(TyVec(I8, 5), TyVec(I16, 5))
        == VecBuild(5, U32 ::+ (i => PadTo(VecAccess(x, i)(), 16)()))()
    )
  }

  test("ReshapeData:Invalid") {
    val e = (t1: Type, t2: Type) => ReshapeData(Param("x")(t1), t2)()

    assertThrows[TypeError](e(U16, U8).tchk())
    assertThrows[TypeError](e(U16, TyUInt(15)).tchk())
    assertThrows[TypeError](e(U16, I16).tchk())
    assertThrows[TypeError](e(I8, U32).tchk())
    assertThrows[TypeError](e(TyBool, U32).tchk())
  }

  test("SafeSum") {
    val u3 = TyUInt(3)
    val u4 = TyUInt(4)
    val e = SafeSum(IntCst(4)(u3), IntCst(4)(u3))().tchk()

    assert(e.typ == u4)
    assert(ir.eval(e) == IntCst(8)())

    assert(ir.eval(SafeSum()()) == C(0)())
  }

  test("SafeProd") {
    val u2 = TyUInt(2)
    val u3 = TyUInt(3)
    val u5 = TyUInt(5)
    val e = SafeProd(IntCst(3)(u2), IntCst(4)(u3))().tchk()

    assert(e.typ == u5)
    assert(ir.eval(e) == IntCst(12)())

    assert(ir.eval(SafeProd()()) == C(1)())

    assert(ir.eval(SafeProd(42, 0)()) == C(0)())
  }
}
