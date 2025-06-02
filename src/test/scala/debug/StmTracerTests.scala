package debug

import ir._
import operations.VecShiftLeft
import org.scalatest.funsuite.AnyFunSuite

class StmTracerTests extends AnyFunSuite {
  test("Trace") {
    ??? // TODO: See notes for test cases
  }

  test("TraceTop:SimpleStmBuild") {
    val n = 3
    val i = Param("i")(TyInt)
    val j = Param("j")(TyInt)
    val s = StmBuild(
      n,
      i + 2 * j,
      i % 2 === 0,
      Map[Param, (Expr, Expr)](i -> (0, i + 1), j -> (10, j + i))
    )().tchk().lower().asInstanceOf[StmBuild]
    val expected = Seq(
      s"""Step 0:
         |    Accumulator: ( ${i.name} = 0, ${j.name} = 10 )
         |    Output:      Some(20)
         |""".stripMargin.stripTrailing,
      s"""Step 1:
         |    Accumulator: ( ${i.name} = 1, ${j.name} = 10 )
         |    Output:      None
         |""".stripMargin.stripTrailing,
      s"""Step 2:
         |    Accumulator: ( ${i.name} = 2, ${j.name} = 11 )
         |    Output:      Some(24)
         |""".stripMargin.stripTrailing,
      s"""Step 3:
         |    Accumulator: ( ${i.name} = 3, ${j.name} = 13 )
         |    Output:      None
         |""".stripMargin.stripTrailing,
      s"""Step 4:
         |    Accumulator: ( ${i.name} = 4, ${j.name} = 16 )
         |    Output:      Some(36)
         |""".stripMargin.stripTrailing,
      s"""Step 5:
         |    Accumulator: ( ${i.name} = 5, ${j.name} = 20 )
         |    Output:      None
         |""".stripMargin.stripTrailing
    )
    val actual = StmTracer.traceTop(s)
    assert(actual == expected)
  }

  test("TraceTop:Stm2Vec") {
    val n = 4
    val i = Param("i")()
    val input =
      StmBuild(n, i, True, Map[Param, (Expr, Expr)](i -> (42, i + 1)))()
    val s = Param("s")()
    val v = Param("v")()
    val t = Param("t")()
    val stm2vec = StmBuild(
      1,
      VecShiftLeft(v, StmData(s)())(),
      t === n - 1,
      Map[Param, (Expr, Expr)](
        s -> (input, True),
        v -> (
          VecBuild(n, TyInt ::+ (_ => Default(TyInt)))(),
          VecShiftLeft(v, StmData(s)())()
        ),
        t -> (0, t + 1)
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val expected = Seq(
      s"""Step 0:
         |    Accumulator: ( ${s.name} = StmBuild(4; ...), ${t.name} = 0, ${v.name} = [0, 0, 0, 0] )
         |    Output:      None
         |""".stripMargin.stripTrailing,
      s"""Step 1:
         |    Accumulator: ( ${s.name} = StmBuild(3; ...), ${t.name} = 1, ${v.name} = [0, 0, 0, 42] )
         |    Output:      None
         |""".stripMargin.stripTrailing,
      s"""Step 2:
         |    Accumulator: ( ${s.name} = StmBuild(2; ...), ${t.name} = 2, ${v.name} = [0, 0, 42, 43] )
         |    Output:      None
         |""".stripMargin.stripTrailing,
      s"""Step 3:
         |    Accumulator: ( ${s.name} = StmBuild(1; ...), ${t.name} = 3, ${v.name} = [0, 42, 43, 44] )
         |    Output:      Some([42, 43, 44, 45])
         |""".stripMargin.stripTrailing,
      s"""Step 4:
         |    Accumulator: ( ${s.name} = StmBuild(0; ...), ${t.name} = 4, ${v.name} = [42, 43, 44, 45] )
         |    Output:      None
         |""".stripMargin.stripTrailing
    )
    val actual = StmTracer.traceTop(stm2vec)
    assert(actual == expected)
  }
}
