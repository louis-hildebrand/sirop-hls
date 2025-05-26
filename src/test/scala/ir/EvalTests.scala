package ir

import org.scalatest.funsuite.AnyFunSuite

class EvalTests extends AnyFunSuite {
  test("IntCst") {
    assert(ir.eval(IntCst(3)) == IntCst(3))
  }

  test("StmNextK") {
    val s = Param("s")()
    val k = Param("k")(TyInt)
    val e = StmNextK(s, 1 + k)()

    val i = Param("i")()
    val s0 =
      StmBuild(3, i, True, Map[Param, (Expr, Expr)](i -> (-4, i + 3)))()
    val elems = Seq(-4, -1, 2)
    for (kVal <- -3 until 2) {
      val expected = StmLiteral.ints(elems.drop(kVal + 1): _*)
      val actual = ir.eval(Let(s, s0, Let(k, kVal, e)())().tchk())
      assert(actual == expected)
    }
  }

  test("StmBuild") {
    val i = Param("i")()
    val s =
      StmBuild(
        5,
        i + 42,
        True,
        Map[Param, (Expr, Expr)](i -> (9, 2 * i + 1))
      )()
    val expected = StmLiteral(9 + 42, 19 + 42, 39 + 42, 79 + 42, 159 + 42)()
    val actual = ir.eval(s)
    assert(actual == expected)
  }

  test("ObviousInfiniteLoop") {
    val s = StmBuild(1, 0, False, Map[Param, (Expr, Expr)]())()
    assertThrows[InfiniteLoopError](ir.eval(s))
  }

  test("LessObviousInfiniteLoop") {
    val a = Param("a")()
    val s = StmBuild(
      1,
      a,
      a % 2 === 1,
      Map[Param, (Expr, Expr)](
        a -> (0, a + 2)
      )
    )().tchk()
    assertThrows[InfiniteLoopError](ir.eval(s))
  }
}
