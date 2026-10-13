package mhir.matchers

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class VecAccessesTests extends AnyFunSuite {

  test("v") {
    val v = Param("v")(TyVec(U8, 3))
    v match {
      case VecAccesses(vec, indices) =>
        assert(vec == v)
        assert(indices == Seq())
      case _ => fail("no match")
    }
  }

  test("v[i]") {
    val v = Param("v")(TyVec(U8, 3))
    val i = Param("i")(U8)
    val e = VecAccess(v, i)().tchk()
    e match {
      case VecAccesses(vec, indices) =>
        assert(vec == v)
        assert(indices == Seq(i))
      case _ => fail("no match")
    }
  }

  test("v[i][j]") {
    val v = Param("v")(TyVec(TyVec(U8, 2), 3))
    val i = Param("i")(U8)
    val j = Param("j")(U8)
    val e = VecAccess(VecAccess(v, i)(), j)().tchk()
    e match {
      case VecAccesses(vec, indices) =>
        assert(vec == v)
        assert(indices == Seq(i, j))
      case _ => fail("no match")
    }
  }
}
