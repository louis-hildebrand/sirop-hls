package ir

import org.scalatest.funsuite.AnyFunSuite

class EvalTests extends AnyFunSuite {
  test("StmNextK") {
    val s = Param("s")
    val k = Param("k")
    val e = 42 + StmNext(StmNextK(s, 1 + k)).__1

    val s0 = StmBuild(3, -4, (i: Expr) => Tuple(i + 3, SSome(i)))
    for (kVal <- -1 until 2) {
      val expected = IntCst(42 + -4 + 3 * (kVal + 1))
      val actual = ir.eval(Let(s, s0, Let(k, kVal, e)))
      assert(actual == expected)
    }
  }

  test("ObviousInfiniteLoop") {
    val s = StmBuild(1, Tuple(), (_: Expr) => Tuple(Tuple(), NNone))
    assertThrows[InfiniteLoopError](ir.eval(s))
  }

  test("LessObviousInfiniteLoop") {
    val s = StmBuild(
      1,
      Tuple(0),
      (acc: Expr) =>
        Tuple(
          Tuple(acc.__0 + 2),
          IfThenElse((acc.__0 * acc.__0) % 2 === 1, SSome(acc.__0), NNone)
        )
    )
    assertThrows[InfiniteLoopError](ir.eval(s))
  }
}
