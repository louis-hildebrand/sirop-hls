package mhir.matchers

import mhir.canonicalize._
import mhir.ir._
import mhir.optimize.PartialEvalPass
import mhir.sugar._
import mhir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

class ShiftLeftTests extends AnyFunSuite {

  private def simplify(e: Expr): Expr = {
    PartialEvalPass.partialEval(e.tchk().lower)
  }

  test("ShiftLeft:u32") {
    val x = Param("x")(U32)
    val v = Param("v")(TyVec(U32, 3))
    val e = simplify(VecShiftLeft(v, SmartSum(C(42)(U8), x)())())
    assert(ShiftLeft.unapply(e).contains((3, v, Sum(C(42)(U32), x)())))
  }

  test("ShiftLeft:u16:Truncate") {
    val x = Param("x")(U32)
    val v = Param("v")(TyVec(U16, 4))
    val e =
      simplify(VecShiftLeft(v, SmartSum(C(42)(U8), TruncateTo(x, 16)())())())
    val actual = ShiftLeft.unapply(e)
    assert(actual.contains((4, v, TruncateTo(Sum(C(42)(U32), x)(), 16)())))
  }

  test("ShiftLeft:u8:ToUnsigned") {
    val x = Param("x")(I9)
    val v = Param("v")(TyVec(U8, 5))
    val e =
      simplify(VecShiftLeft(v, SmartSum(C(42)(U8), ToUnsigned(x)())())())
    val actual = ShiftLeft.unapply(e)
    assert(actual.contains((5, v, ToUnsigned(Sum(C(42)(I9), x)())())))
  }

  test("ShiftLeft:u8:TruncateAndToUnsigned") {
    val x = Param("x")(I16)
    val v = Param("v")(TyVec(U8, 6))
    val e = simplify(
      VecShiftLeft(
        v,
        SmartSum(C(42)(U8), TruncateTo(ToUnsigned(x)(), 8)())()
      )()
    )
    val actual = ShiftLeft.unapply(e)
    assert(
      actual.contains(
        (6, v, ToUnsigned(TruncateTo(Sum(C(42)(I9), x)(), 9)())())
      )
    )
  }

  test("ShiftLeft:bool") {
    val x = Param("x")(TyBool)
    val v = Param("v")(TyVec(TyBool, 2))
    val e = simplify(VecShiftLeft(v, x)())
    assert(ShiftLeft.unapply(e).contains((2, v, x)))
  }

  test("MapShiftLeft:Depth0") {
    val x = Param("x")(U8)
    val v = Param("v")(TyVec(U8, 3))
    val e = simplify(VecShiftLeft(v, x)())
    val actual = MapShiftLeft.unapply(e)
    actual match {
      case Some((Seq(), 3, v1, x1)) =>
        assert(v1 == v)
        assert(x1 == x)
      case out =>
        fail(s"wrong output: $out")
    }
  }

  test("MapShiftLeft:Depth1") {
    val x = Param("x")(U8)
    val v = Param("v")(TyVec(TyVec(U8, 4), 3))
    val e = simplify(
      VecMap(
        v, {
          val row = Param("row")(TyVec(U8, 4))
          Function(row, VecShiftLeft(row, x)())()
        }
      )()
    )
    val actual = MapShiftLeft.unapply(e)
    actual match {
      case Some((Seq(_), 4, v1, x1)) =>
        assert(v1 == v)
        assert(x1 == x)
      case out =>
        fail(s"wrong output: $out")
    }
  }

  test("MapShiftLeft:Depth2") {
    val x = Param("x")(U32)
    val v = Param("v")(TyVec(TyVec(TyVec(U32, 7), 2), 3))
    val i = Param("i")(U32)
    val j = Param("j")(U32)
    val k = Param("k")(U32)
    val e = simplify(
      VecBuild(
        3,
        Function(
          i,
          VecBuild(
            2,
            Function(
              j,
              VecShiftLeft(
                VecAccess(VecAccess(v, i)(), j)(),
                Sum(x, i, j, k)()
              )()
            )()
          )()
        )()
      )()
    )
    val actual = MapShiftLeft.unapply(e)
    actual match {
      case Some((Seq(i0, j0), 7, v1, Sum(i1, j1, k1, x1))) =>
        assert(i0 == i1)
        assert(j0 == j1)
        assert(k1 == k)
        assert(v1 == v)
        assert(x1 == x)
      case out =>
        fail(s"wrong output: $out")
    }
  }
}
