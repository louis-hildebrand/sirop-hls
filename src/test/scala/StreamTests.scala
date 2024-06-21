import org.scalatest.funsuite.AnyFunSuite

import scala.runtime.stdLibPatches.Predef.assert


class StreamTests extends AnyFunSuite {

  def assertStreamEqual(stream: Expr, expectedSeq: Seq[Expr]) = {
    var actualSeq = Seq[Expr]()
    var n: Expr = Tuple(stream, 0 /*unused*/)
    expectedSeq.foreach(exp =>
      n = ExprInterpreter.partialInterpret(StmNext(n.__0))
      actualSeq = actualSeq :+ ExprInterpreter.partialInterpret(n.__1)
    )
    assert(actualSeq == expectedSeq)
    assert(ExprInterpreter.partialInterpret(StmLength(n.__0)) == IntCst(0))
  }

  test("IntCst") {
    val intAst = IntCst(3)
    assert(ExprInterpreter.partialInterpret(intAst) == IntCst(3))
  }

  test("CstStream") {
    val cstStream = CstStream(4, 3)
    assertStreamEqual(cstStream, Seq(3,3,3,3))
  }

  test("CounterStream") {
    val cntAst = CounterStream(5)
    assertStreamEqual(cntAst, Seq(0,1,2,3,4))
  }

  test("Map") {
    val mapAst = MapS(CounterStream(5), e => Add(e, IntCst(7)))
    assertStreamEqual(mapAst, Seq(7,8,9,10,11))
  }

  test("Pad") {
    val stmPadFirst = PadFirst(CounterStream(3), IntCst(33))
    assertStreamEqual(stmPadFirst, Seq(33,0,1,2))

    val stmPadLast = PadLast(CounterStream(3), IntCst(44))
    assertStreamEqual(stmPadLast, Seq(0,1,2,44))

    val stmFirstLast = PadLast(PadFirst(CounterStream(3),IntCst(33)),IntCst(44))
    assertStreamEqual(stmFirstLast, Seq(33,0,1,2,44))

    val stmLastFirst = PadFirst(PadLast(CounterStream(3), IntCst(44)), IntCst(33))
    assertStreamEqual(stmLastFirst, Seq(33,0,1,2,44))
  }

  test("Concat") {
    val stmConcatTwice = Concat(CstStream(1, 77), CstStream(1, 77))
    assertStreamEqual(stmConcatTwice, Seq(77,77))

    val stmConcat = Concat(CounterStream(3),CstStream(4,77))
    assertStreamEqual(stmConcat, Seq(0,1,2,77,77,77,77))

    val stmConcat1 = Concat(CounterStream(3),CstStream(4,77))
    val stmConcat2 = Concat(stmConcat1, CounterStream(2))
    assertStreamEqual(stmConcat2, Seq(0,1,2,77,77,77,77,0,1))
  }


  test("2DTupleStream") {
    val cnt2DAst = Counter2DStream(2, 3)
    val next1 = StmNext(cnt2DAst)
    assertStreamEqual(next1.__1, Seq(Tuple(0, 0),Tuple(0, 1),Tuple(0, 2)))

    val next2 = StmNext(next1.__0)
    assertStreamEqual(next2.__1, Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2)))

    assert(ExprInterpreter.partialInterpret(StmLength(next2.__0)) == IntCst(0))
  }

  test("MapMapStream") {
    val stream = StmBuild(3, 0, (i: Expr) => Tuple(i + 1, i))
    val mapMapStream = MapS(MapS(stream, e => e+1), e=>e*2)

    assertStreamEqual(mapMapStream, Seq(2,4,6))
  }

  test("Map2DStream") {
    val stream2D = StmBuild(2, 0, (i: Expr) => Tuple(i + 1,
      StmBuild(3, 0, (j: Expr) =>
        Tuple(j + 1,
          i*3+j
      ))
    ))

    val mappedStream2D = MapS(stream2D, e1 =>
      MapS(e1, e2 =>
         e2+1
      )
    )

    val next1 = StmNext(mappedStream2D)
    assertStreamEqual(next1.__1, Seq(1,2,3))

    val next2 = StmNext(next1.__0)
    assertStreamEqual(next2.__1, Seq(4,5,6))

    assert(ExprInterpreter.partialInterpret(StmLength(next2.__0)) == IntCst(0))
  }


  test("foldStm") {
    val sum = StmFold(CounterStream(5), 2, (i: Expr) => (acc:Expr) => i+acc)
    assert(ExprInterpreter.partialInterpret(sum) == IntCst(12))
  }

  test("fold2Dstm") {
    val stream2D = StmBuild(2, 0, (i: Expr) => Tuple(i + 1,
      StmBuild(3, 0, (j: Expr) =>
        Tuple(j + 1,
          i * 3 + j
        ))
    ))

    val mapFold = MapS(stream2D, innerStream =>
      StmFold(innerStream, 0, (i: Expr) => (acc:Expr) => i+acc)
    )

    assertStreamEqual(mapFold, Seq(3,12))
  }


  test("Vec2Stm") {
    val oneTwoThreeVec = VecBuild(3, (i: Expr) => i + 1)
    val stm = Vec2Stm(oneTwoThreeVec)

    assertStreamEqual(stm, Seq(1,2,3))
  }



}
