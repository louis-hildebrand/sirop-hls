package ir

import org.scalatest.funsuite.AnyFunSuite

class CoreTests extends AnyFunSuite {
  test("FlattenSum") {
    val x = Param()
    val y = Param()
    val z = Param()
    val w = Param()

    assert(x + y + z + w == Sum(z, x, y, w))
    assert(x + y + z + w + x != Sum(z, x, y, w))
  }

  test("FlattenProd") {
    val x = Param()
    val y = Param()
    val z = Param()
    val w = Param()

    assert(x * y * z * w == Prod(z, x, y, w))
    assert(x * y * z * w * x != Prod(z, x, y, w))
  }

  test("MakeIfThenElsePositive") {
    val c = Param("c")
    val t = Param("t")
    val f = Param("f")

    val e0 = IfThenElse(c, t, f).asInstanceOf[IfThenElse]
    assert(e0.c == c)
    assert(e0.t == t)
    assert(e0.f == f)

    val e1 = IfThenElse(Not(c), f, t)
    assert(e0 == e1)
  }

  test("FunctionsEqual") {
    val f = {
      val x = Param("x")
      Function(x, (x + 1) * (x + 2))
    }
    val g = {
      val y = Param("y")
      Function(y, (y + 1) * (y + 2))
    }
    assert(f == g)
    assert(g == f)
    assert(f.hashCode() == g.hashCode())
  }

  test("FunctionsNotEqual") {
    val x = Param("x")
    val f = Function(x, (x + 1) * (x + 2))
    val y = Param("y")
    val g = Function(y, (y + 1) * (x + 2))
    assert(f != g)
    assert(g != f)
  }

  test("StmBuildEqual:NoAccumulatorVars") {
    val s1 = StmBuild(3, SSome(True), Map[Param, (Expr, Expr)]())
    val s2 = StmBuild(3, SSome(True), Map[Param, (Expr, Expr)]())
    assert(s1 == s2)
    assert(s2 == s1)
    assert(s1.hashCode == s2.hashCode)
  }

  test("StmBuildEqual:OneAccumulatorVar") {
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
    val s1 = StmBuild(n, SSome(i), Map[Param, (Expr, Expr)](i -> (z, i + 1)))
    val s2 = StmBuild(n, SSome(j), Map[Param, (Expr, Expr)](j -> (z, j + 1)))
    assert(s1 == s2)
    assert(s2 == s1)
    assert(s1.hashCode == s2.hashCode)
  }

  test("StmBuildEqual:TwoAccumulatorVars") {
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
    val s1 =
      StmBuild(
        n,
        SSome(j - i),
        Map[Param, (Expr, Expr)](i -> (z, i + 1), j -> (0, j * 2))
      )
    val s2 =
      StmBuild(
        n,
        SSome(i - j),
        Map[Param, (Expr, Expr)](j -> (z, j + 1), i -> (0, i * 2))
      )
    assert(s1 == s2)
    assert(s2 == s1)
    assert(s1.hashCode == s2.hashCode)
  }

  test("StmBuildEqual:ThreeAccumulatorVars") {
    val n = Param("n")
    val a = Param("a")
    val c = Param("c")
    val d = Param("d")
    val s1 = StmBuild(
      n,
      SSome(a * c * d),
      Map[Param, (Expr, Expr)](
        a -> (0, a + 1),
        c -> (1, c + 2),
        d -> (2, d + 3)
      )
    )
    val s2 = StmBuild(
      n,
      SSome(a * c * d),
      Map[Param, (Expr, Expr)](
        a -> (0, a + 1),
        d -> (2, d + 3),
        c -> (1, c + 2)
      )
    )
    assert(s1 == s2)
    assert(s2 == s1)
    assert(s1.hashCode == s2.hashCode)
  }

  test("StmBuildNotEqual:DifferentLengths") {
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
    val s1 = StmBuild(i, SSome(i), Map[Param, (Expr, Expr)](i -> (z, i + 1)))
    val s2 = StmBuild(j, SSome(j), Map[Param, (Expr, Expr)](j -> (z, j + 1)))
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuildNotEqual:DifferentOutputs1") {
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
    val s1 = StmBuild(n, SSome(i), Map[Param, (Expr, Expr)](i -> (z, i + 1)))
    val s2 = StmBuild(n, SSome(i), Map[Param, (Expr, Expr)](j -> (z, j + 1)))
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuildNotEqual:DifferentOutputs2") {
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
    val s1 =
      StmBuild(
        n,
        SSome(i - j),
        Map[Param, (Expr, Expr)](i -> (z, i + 1), j -> (0, j * 2))
      )
    val s2 =
      StmBuild(
        n,
        SSome(i - j),
        Map[Param, (Expr, Expr)](j -> (z, j + 1), i -> (0, i * 2))
      )
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuildNotEqual:DifferentSeeds") {
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val s1 = StmBuild(n, SSome(i), Map[Param, (Expr, Expr)](i -> (i, i + 1)))
    val s2 = StmBuild(n, SSome(j), Map[Param, (Expr, Expr)](j -> (j, j + 1)))
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuildNotEqual:DifferentStructures") {
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
    val s1 = StmBuild(
      n,
      SSome(i),
      Map[Param, (Expr, Expr)](
        i -> (z, i + 1),
        j -> (z, j - 1)
      )
    )
    val s2 = StmBuild(
      n,
      SSome(j),
      Map[Param, (Expr, Expr)](
        i -> (z, j + 1),
        j -> (z, i - 1)
      )
    )
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("RenameStmBuildVars") {
    val n = Param("n")
    val z = Param("z")
    val a = Param("a")
    val r0 = Param("r0")
    val r1 = Param("r1")
    val original = StmBuild(
      n,
      z + a + r0 * r1,
      Map[Param, (Expr, Expr)](
        a -> (z, a + 1),
        r0 -> (1, a * 2),
        r1 -> (z, a - 1)
      )
    )
    val renamed = original.renameAccVars
    assert(original == renamed)
    assert(original.accVars.intersect(renamed.accVars).isEmpty)
  }
}
