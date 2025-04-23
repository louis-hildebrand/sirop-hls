package ir

import org.scalatest.funsuite.AnyFunSuite

class EvalTests extends AnyFunSuite {
  test("IntCst") {
    assert(ir.eval(IntCst(3)) == IntCst(3))
  }

  test("StmNextK") {
    val s = Param("s")()
    val k = Param("k")()
    val e = 42 + StmNext(StmNextK(s, 1 + k)())().__1

    val i = Param("i")()
    val s0 =
      StmBuild(3, SSome(i)(), Map[Param, (Expr, Expr)](i -> (-4, i + 3)))()
    for (kVal <- -1 until 2) {
      val expected = IntCst(42 + -4 + 3 * (kVal + 1))
      val actual = ir.eval(Let(s, s0, Let(k, kVal, e)())())
      assert(actual == expected)
    }
  }

  test("ObviousInfiniteLoop") {
    val s = StmBuild(1, NNone(TyInt), Map[Param, (Expr, Expr)]())()
    assertThrows[InfiniteLoopError](ir.eval(s))
  }

  test("LessObviousInfiniteLoop") {
    val a = Param("a")()
    val s = StmBuild(
      1,
      IfThenElse((a * a) % 2 === 1, SSome(a)(), NNone(TyInt))(),
      Map[Param, (Expr, Expr)](
        a -> (0, a + 2)
      )
    )()
    assertThrows[InfiniteLoopError](ir.eval(s))
  }
}
