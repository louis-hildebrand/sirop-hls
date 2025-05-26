package operations

import ir._
import org.scalatest.funsuite.AnyFunSuite

class VectorOfStreamTests extends AnyFunSuite {
  test("VecBuild:StmRange") {
    val n = 3
    val m = 4
    val e = VecBuild(n, TyInt ::+ (i => StmRange(m, i, i)()))().tchk().lower()
    val expected = StmLiteral(
      (0 until m).map(j =>
        VecLiteral((0 until n).map(i => IntCst(i + j * i)): _*)()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("NestedVecBuild:StmRange") {
    val n = 3
    val m = 4
    val k = 5
    val e = VecBuild(
      n,
      TyInt ::+ (i => VecBuild(m, TyInt ::+ (j => StmRange(k, i, j)()))())
    )().tchk().lower()
    val expected = StmLiteral(
      (0 until k).map(t =>
        VecLiteral(
          (0 until n).map(i =>
            VecLiteral((0 until m).map(j => IntCst(i + j * t)): _*)()
          ): _*
        )()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecBuild:StmZip") {
    val n = 4
    val m = 3
    val e =
      VecBuild(
        n,
        TyInt ::+ (i => StmZip(StmCount(m)(), StmRange(m, i, i)())())
      )().tchk().lower()
    val expected = StmLiteral(
      (0 until m).map(j =>
        VecLiteral((0 until n).map(i => Tuple(j, i + j * i)()): _*)()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecBuild:StmZipFused") {
    val n = 4
    val m = 3
    val a1 = Param("a1")()
    val a2 = Param("a2")()
    val e = VecBuild(
      n,
      TyInt ::+ (i =>
        StmBuild(
          m,
          SSome(Tuple(a1, a2)())(),
          Map[Param, (Expr, Expr)](a1 -> (0, a1 + 1), a2 -> (i, a2 + i))
        )()
      )
    )().tchk().lower()
    val expected = StmLiteral(
      (0 until m).map(j =>
        VecLiteral((0 until n).map(i => Tuple(j, i + j * i)()): _*)()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecBuild:StmSum") {
    val n = 5
    val m = 3
    val e = VecBuild(
      n,
      TyInt ::+ (i => StmFold(StmCst(m, i * i)(), 0, PlusFunction())())
    )().tchk().lower()
    val expected = StmLiteral(
      VecLiteral(
        (0 until n).map(i => IntCst((0 until m).map(_ => i * i).sum)): _*
      )()
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecMap:StmSum") {
    // TODO: Write a test like VecBuild:StmSum, but in which the input must also be replicated
    ???
  }
}
