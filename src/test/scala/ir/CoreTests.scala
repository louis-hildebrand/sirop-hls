package ir

import operations._
import opt.{PartialEvalPass, StmAccRemovalPass}
import org.scalatest.funsuite.AnyFunSuite

class CoreTests extends AnyFunSuite {
  test("Sum:Flatten") {
    val x = Param()
    val y = Param()
    val z = Param()
    val w = Param()

    assert(x + y + z + w == Sum(z, x, y, w))
    assert(x + y + z + w + x != Sum(z, x, y, w))
  }

  test("Prod:Flatten") {
    val x = Param()
    val y = Param()
    val z = Param()
    val w = Param()

    assert(x * y * z * w == Prod(z, x, y, w))
    assert(x * y * z * w * x != Prod(z, x, y, w))
  }

  test("IfThenElse:MakeCondPositive") {
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

  test("Function:Equals") {
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

  test("Function:NotEquals") {
    val x = Param("x")
    val f = Function(x, (x + 1) * (x + 2))
    val y = Param("y")
    val g = Function(y, (y + 1) * (x + 2))
    assert(f != g)
    assert(g != f)
  }

  test("StmBuild:Equals:NoAccumulatorVars") {
    val s1 = StmBuild(3, SSome(True), Map[Param, (Expr, Expr)]())
    val s2 = StmBuild(3, SSome(True), Map[Param, (Expr, Expr)]())
    assert(s1 == s2)
    assert(s2 == s1)
    assert(s1.hashCode == s2.hashCode)
  }

  test("StmBuild:Equals:OneAccumulatorVar") {
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

  test("StmBuild:Equals:TwoAccumulatorVars") {
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

  test("StmBuild:Equals:ThreeAccumulatorVars") {
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

  test("StmBuild:NotEquals:DifferentLengths") {
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
    val s1 = StmBuild(i, SSome(i), Map[Param, (Expr, Expr)](i -> (z, i + 1)))
    val s2 = StmBuild(j, SSome(j), Map[Param, (Expr, Expr)](j -> (z, j + 1)))
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuild:NotEquals:DifferentOutputs1") {
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
    val s1 = StmBuild(n, SSome(i), Map[Param, (Expr, Expr)](i -> (z, i + 1)))
    val s2 = StmBuild(n, SSome(i), Map[Param, (Expr, Expr)](j -> (z, j + 1)))
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuild:NotEquals:DifferentOutputs2") {
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

  test("StmBuild:NotEquals:DifferentSeeds") {
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val s1 = StmBuild(n, SSome(i), Map[Param, (Expr, Expr)](i -> (i, i + 1)))
    val s2 = StmBuild(n, SSome(j), Map[Param, (Expr, Expr)](j -> (j, j + 1)))
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuild:NotEquals:DifferentStructures") {
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

  test("StmBuild:RenameVars") {
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
    val renamed = original.renameVars
    assert(original == renamed)
    assert(original.accVars.intersect(renamed.accVars).isEmpty)
  }

  test("StmBuild:Fuse:MapPlusFive") {
    val s = Param("s")
    val original = StmBuild(
      3,
      SSome(StmNext(s).__1 + 5),
      Map[Param, (Expr, Expr)](
        s -> (StmCount(3), StmNext(s).__0)
      )
    )
    val fused = original.fuseCompletely()

    // Correct behaviour
    val expectedElems = StmLiteral.ints(5, 6, 7)
    assert(ir.eval(original) == expectedElems)
    assert(ir.eval(fused) == expectedElems)
    // Successful fusion
    val i = Param("i")
    val ideal =
      StmBuild(
        3,
        SSome(i + 5),
        Map[Param, (Expr, Expr)](
          i -> (0, i + 1)
        )
      )
    assert(PartialEvalPass.partialEval(fused) == ideal)
  }

  test("StmBuild:Fuse:StmShiftRight") {
    val input = Param("input")
    val original = StmPrepend(
      StmPrefix(input, StmLength(input) - 1, shape = Seq(5)),
      42,
      eShape = Seq()
    )
    val fused = original.asInstanceOf[StmBuild].fuseCompletely()

    // Correct behaviour
    // (Using one example input)
    val call = (e: Expr) => Let(input, StmCount(5), e)
    val expectedElems = StmLiteral.ints(42, 0, 1, 2, 3)
    assert(ir.eval(call(original)) == expectedElems)
    assert(ir.eval(call(fused)) == expectedElems)
    // Successful fusion
    val s = Param("s")
    val i = Param("i")
    val j = Param("j")
    val ideal = StmBuild(
      StmLength(input),
      IfThenElse(
        i === 1,
        IfThenElse(j < -1 + StmLength(input), SSome(StmNext(s).__1), NNone),
        SSome(42)
      ),
      Map[Param, (Expr, Expr)](
        s -> (input, IfThenElse(i === 1, StmNext(s).__0, s)),
        i -> (0, IfThenElse(i === 1, i, i + 1)),
        j -> (0, IfThenElse(i === 1, j + 1, j))
      )
    )
    val fusedAndSimplified =
      PartialEvalPass.partialEval(StmAccRemovalPass.removeConstantVars(fused))
    assert(fusedAndSimplified == ideal)
  }

  test("StmBuild:Fuse:StmShiftLeft") {
    val input = Param("input")
    val n = 5
    val original =
      StmAppend(StmSuffix(input, n - 1, shape = Seq(5)), 42, eShape = Seq())
    val fused = original.asInstanceOf[StmBuild].fuseCompletely()

    // Correct behaviour
    val call = (e: Expr) => Let(input, StmCount(n), e)
    val expectedElems = StmLiteral.ints(1, 2, 3, 4, 42)
    assert(ir.eval(call(original)) == expectedElems)
    assert(ir.eval(call(fused)) == expectedElems)
    // Successful fusion
    val s = Param("s")
    val i = Param("i")
    val j = Param("j")
    val ideal =
      StmBuild(
        n,
        IfThenElse(
          i === 4,
          SSome(42),
          IfThenElse(j < 1, NNone, SSome(StmNext(s).__1))
        ),
        Map[Param, (Expr, Expr)](
          s -> (input, IfThenElse(i === 4, s, StmNext(s).__0)),
          i -> (0, IfThenElse(i === 4, i, IfThenElse(j < 1, i, i + 1))),
          j -> (0, IfThenElse(i === 4, j, j + 1))
        )
      )
    val fusedAndSimplified =
      PartialEvalPass.partialEval(StmAccRemovalPass.removeConstantVars(fused))
    assert(fusedAndSimplified == ideal)
  }

  test("StmBuild:Fuse:ZipCounters") {
    val i = Param("i")
    // [0, 1, 2, 3]
    val c1 = StmBuild(4, SSome(i), Map[Param, (Expr, Expr)](i -> (0, i + 1)))
    // [9, 11, 13, 15]
    val c2 = StmBuild(4, SSome(i), Map[Param, (Expr, Expr)](i -> (9, i + 2)))
    // [(0, 9), (1, 11), (2, 13), (3, 15)]
    val s = StmZip(c1, c2)

    val x1 = s.seedByVar.find({ case (_, z) => z == c1 }).get._1
    val x2 = s.seedByVar.find({ case (_, z) => z == c2 }).get._1

    val i1 = Param("i")
    val i2 = Param("i")

    // 1) After one fusion
    val actual1 = PartialEvalPass.partialEval(s.fuseWith(x1))
    // 1a) Correct behaviour
    assert(ir.eval(actual1) == ir.eval(s))
    // 1b) Successful fusion
    val ideal1 = StmBuild(
      4,
      SSome(Tuple(i1, StmNext(x2).__1)),
      Map[Param, (Expr, Expr)](
        x2 -> s.equations(x2),
        i1 -> (0, i1 + 1)
      )
    )
    assert(actual1 == ideal1)

    // 2) After two fusions
    val actual2 = PartialEvalPass.partialEval(s.fuseWith(x1).fuseWith(x2))
    // 2a) Correct behaviour
    val expectedElems =
      StmLiteral(Tuple(0, 9), Tuple(1, 11), Tuple(2, 13), Tuple(3, 15))
    assert(ir.eval(s) == expectedElems)
    assert(ir.eval(actual2) == expectedElems)
    // 2b) Successful fusion
    val ideal2 = StmBuild(
      4,
      SSome(Tuple(i1, i2)),
      Map[Param, (Expr, Expr)](
        i1 -> (0, i1 + 1),
        i2 -> (9, i2 + 2)
      )
    )
    assert(actual2 == ideal2)
  }

  test("StmBuild:AddOutputCounter") {
    val n = Param("n")
    val out = Param("out")
    val outCtr = Param("out_ctr")
    val s = StmBuild(
      n,
      out,
      Map[Param, (Expr, Expr)](outCtr -> (10, outCtr * 2))
    )

    val actual = s.addOutputCounter(outCtr)

    // The existing bound variable should be renamed
    val i = Param("i")
    val expectedOutCtrSeed = IntCst(0)
    val expectedOutCtrNext = OptionAccess(
      out,
      (_: Expr) => outCtr + 1,
      (_: Expr) => outCtr
    )
    val expected = StmBuild(
      n,
      out,
      Map[Param, (Expr, Expr)](
        i -> (10, i * 2),
        outCtr -> (expectedOutCtrSeed, expectedOutCtrNext)
      )
    )
    assert(actual == expected)
    assert(actual.seedByVar(outCtr) == expectedOutCtrSeed)
    assert(actual.nextByVar(outCtr) == expectedOutCtrNext)
  }
}
