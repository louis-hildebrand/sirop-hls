package opt

import ir._
import operations._
import org.scalatest.funsuite.AnyFunSuite

class StmFusePassTests extends AnyFunSuite {
  private val fuseCompletely = (e: StmBuild) => StmFusePass.fuseCompletely(e)

  test("MapPlusFive") {
    val s = Param("s")
    val original = StmBuild(
      3,
      SSome(StmNext(s).__1 + 5),
      Map[Param, (Expr, Expr)](
        s -> (StmCount(3), StmNext(s).__0)
      )
    )
    val fused = fuseCompletely(original)

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

  test("StmShiftRight") {
    val input = Param("input")
    val original = StmPrepend(
      StmPrefix(input, StmLength(input) - 1, shape = Seq(5)),
      42,
      eShape = Seq()
    )
    val fused = fuseCompletely(original.asInstanceOf[StmBuild])

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

  test("StmShiftLeft") {
    val input = Param("input")
    val n = 5
    val original =
      StmAppend(StmSuffix(input, n - 1, shape = Seq(5)), 42, eShape = Seq())
    val fused = fuseCompletely(original.asInstanceOf[StmBuild])

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

  test("ZipCounters") {
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
    val actual1 = PartialEvalPass.partialEval(StmFusePass.fuseWith(s, x1))
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
    val actual2 = PartialEvalPass.partialEval(
      StmFusePass.fuseWith(StmFusePass.fuseWith(s, x1), x2)
    )
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
}
