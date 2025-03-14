package opt

import ir._
import operations._
import org.scalatest.funsuite.AnyFunSuite

class StmFusePassTests extends AnyFunSuite {
  private val canon = (e: Expr) =>
    StmCanonPass.canonicalize(e.asInstanceOf[StmBuild])
  private val fuseCompletely = (e: StmBuild) => StmFusePass.fuseCompletely(e)

  test("CountFromFive") {
    val s = StmMap(
      StmCount(3),
      (x: Expr) => x + 5,
      n = 3,
      fInShape = None,
      fOutShape = None
    )
    val actual = canon(fuseCompletely(s.asInstanceOf[StmBuild]))

    // Correct behaviour
    val expectedElems = StmLiteral.ints(5, 6, 7)
    assert(ir.eval(s) == expectedElems)
    assert(ir.eval(actual) == expectedElems)
    // Successful fusion
    val ideal =
      StmBuild(
        3,
        Tuple(0),
        (acc: Expr) => Tuple(Tuple(acc.__0 + 1), SSome(acc.__0 + 5))
      )
    assert(actual == ideal)
  }

  test("MapMap") {
    val p = Param()
    val f = (x: Expr) => (x + 2) * (x + 3) * (x + 4)
    val g = (x: Expr) => x - 10
    val s =
      StmMap(
        StmMap(p, f, n = 5, fInShape = None, fOutShape = None),
        g,
        n = 5,
        fInShape = None,
        fOutShape = None
      )
    val actual = canon(fuseCompletely(s.asInstanceOf[StmBuild]))

    // Correct behaviour
    // (Using one example p, f, and g)
    val call = (e: Expr) => Let(p, StmCount(5), e)
    val expectedElems = StmLiteral.ints(14, 50, 110, 200, 326)
    assert(ir.eval(call(s)) == expectedElems)
    assert(ir.eval(call(actual)) == expectedElems)
    // Successful fusion
    val ideal = StmBuild(
      5,
      Tuple(p),
      (acc: Expr) =>
        Tuple(
          Tuple(StmNext(acc.__0).__0),
          SSome({
            val x = StmNext(acc.__0).__1
            (x + 2) * (x + 3) * (x + 4) + -10
          })
        )
    )
    assert(actual == ideal)
  }

  test("MapFold") {
    val p = Param()
    val n = Param()
    val f = (x: Expr) => (x + 2) * (x + 3) * (x + 4)
    val s =
      StmFold(
        StmMap(p, f, n = n, fInShape = None, fOutShape = None),
        0,
        (acc: Expr) => (x: Expr) => acc + x,
        stmShape = Seq(n)
      )
    val actual = canon(fuseCompletely(s))

    // Correct behaviour
    // (Using one example p, f, and g)
    val call = (e: Expr) => Let(n, 5, Let(p, StmCount(n), e))
    val expected =
      StmLiteral(
        ir.eval(
          FunCall(f, 0)
            + FunCall(f, 1)
            + FunCall(f, 2)
            + FunCall(f, 3)
            + FunCall(f, 4)
        )
      )
    assert(ir.eval(call(s)) == expected)
    assert(ir.eval(call(actual)) == expected)
    // Successful fusion
    val ideal = canon(
      fuseCompletely(
        StmFold(
          p,
          0,
          (acc: Expr) => (x: Expr) => acc + (x + 2) * (x + 3) * (x + 4),
          stmShape = Seq(n)
        )
      )
    )
    assert(actual == ideal)
  }

  test("StmShiftRight") {
    val p = Param()
    val s = StmPrepend(
      StmPrefix(p, StmLength(p) - 1, shape = Seq(5)),
      42,
      eShape = Seq()
    )
    val actual = canon(fuseCompletely(s.asInstanceOf[StmBuild]))

    // Correct behaviour
    // (Using one example p)
    val call = (e: Expr) => Let(p, StmCount(5), e)
    val expectedElems = StmLiteral.ints(42, 0, 1, 2, 3)
    assert(ir.eval(call(s)) == expectedElems)
    assert(ir.eval(call(actual)) == expectedElems)
    // Successful fusion
    val ideal = StmBuild(
      StmLength(p),
      Tuple(p, 1, 0),
      (acc: Expr) =>
        IfThenElse(
          acc.__1 === 0,
          IfThenElse(
            acc.__2 < (StmLength(p) + -1),
            Tuple(
              Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 + 1),
              SSome(StmNext(acc.__0).__1)
            ),
            Tuple(
              Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 + 1),
              NNone
            )
          ),
          Tuple(Tuple(acc.__0, acc.__1 + -1, acc.__2), SSome(42))
        )
    )
    assert(actual == ideal)
  }

  test("StmShiftLeft") {
    val p = Param()
    val n = 5
    val s =
      StmAppend(StmSuffix(p, n - 1, shape = Seq(5)), 42, eShape = Seq())
    val actual = canon(fuseCompletely(s.asInstanceOf[StmBuild]))

    // Correct behaviour
    val call = (e: Expr) => Let(p, StmCount(n), e)
    val expectedElems = StmLiteral.ints(1, 2, 3, 4, 42)
    assert(ir.eval(call(s)) == expectedElems)
    assert(ir.eval(call(actual)) == expectedElems)
    // Successful fusion
    val ideal =
      StmBuild(
        5,
        Tuple(p, 4, 0),
        (acc: Expr) =>
          IfThenElse(
            acc.__1 === 0,
            Tuple(Tuple(acc.__0, acc.__1, acc.__2), SSome(42)),
            IfThenElse(
              acc.__2 >= 1,
              Tuple(
                Tuple(StmNext(acc.__0).__0, acc.__1 + -1, acc.__2 + 1),
                SSome(StmNext(acc.__0).__1)
              ),
              Tuple(
                Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 + 1),
                NNone
              )
            )
          )
      )
    assert(actual == ideal)
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
