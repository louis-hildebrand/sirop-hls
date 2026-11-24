package mhir.gen.vhdl
package transform

import mhir.canonicalize._
import mhir.gen.vhdl.agilex7.AgilexMac2
import mhir.gen.vhdl.ir._
import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable.ListMap

class IntermediateInsertionTests extends AnyFunSuite {

  /* The VHDL expression generator translates "if-then-else" expressions to
   * "concurrent conditional signal assignment" statements. These are not
   * expressions, so an intermediate variable is needed.
   */
  test("IfElse") {
    val c = Param("c")(TyBool)
    val e = Sum(C(1)(U8), Mux(c, C(42)(U8), C(0)(U8))())()
    val (actualExpr, actualIntermediates) = IntermediateInsertion(e, Map.empty)

    assert(actualIntermediates.size == 1)
    val (tmp, actualIntermediate) = actualIntermediates.head
    assert(actualIntermediate == MuxIntermediate(c, C(42)(U8), C(0)(U8)))
    assert(actualIntermediate.asInstanceOf[MuxIntermediate].t.typ == U8)
    assert(actualIntermediate.asInstanceOf[MuxIntermediate].f.typ == U8)
    val expectedExpr = Sum(C(1)(U8), tmp)()
    assert(actualExpr == expectedExpr)
    assert(actualExpr.typ == U8)
  }

  /* I don't want to have unnecessary intermediates that are only used as the
   * `next` value for one accumulator. (1) This makes later steps like DSP
   * selection more difficult to do reliably, since you've got to deal with
   * extra levels of indirection. (2) This makes the generated VHDL uglier.
   */
  test("IfElseInAccumulator") {
    val p = Param("p")(TyStm(U8, 8))
    val acc = Param("acc")(U8)
    val original = GenStmBuild(
      data = acc,
      valid = True,
      intermediates = ListMap(
        acc -> ExprAccumulator(
          Some(ExprIntermediate(C(0)(U8))),
          ExprIntermediate(
            Mux(
              Equal(Mod(acc, C(2)(U8))(), C(0)(U8))(),
              StmData(p)(),
              Sum(C(1)(U8), acc)()
            )().tchk()
          )
        )
      ),
      producers = Map(
        p -> (p, True)
      )
    )
    val actual = IntermediateInsertion(original)

    val sdata = actual.intermediates
      .collectFirst({ case (x, _: StmDataIntermediate) => x })
      .get
    val expected = GenStmBuild(
      data = acc,
      valid = True,
      intermediates = ListMap(
        acc -> ExprAccumulator(
          Some(ExprIntermediate(C(0)(U8))),
          MuxIntermediate(
            Equal(Mod(acc, C(2)(U8))(), C(0)(U8))().tchk(),
            sdata,
            Sum(C(1)(U8), acc)().tchk()
          )
        ),
        sdata -> StmDataIntermediate(p)
      ),
      producers = Map(
        p -> (p, True)
      )
    )
    assert(actual == expected)
  }

  test("IfElseInExistingIntermediate") {
    val x = Param("x")(U8)
    val b = Param("b")(TyBool)
    val original = GenStmBuild(
      data = Sum(x, x)().tchk(),
      valid = True,
      intermediates = ListMap(
        b -> ExprAccumulator(
          Some(ExprIntermediate(True)),
          ExprIntermediate(Not(b)().tchk())
        ),
        x -> ExprIntermediate(
          Sum(C(5)(U8), Mux(b, C(42)(U8), C(0)(U8))())().tchk()
        )
      ),
      producers = Map()
    )
    val actual = IntermediateInsertion(original)

    val intermediatesExceptAccumulators = actual.intermediates
      .filterNot({ case (_, i) => i.isInstanceOf[Accumulator] })
    assert(intermediatesExceptAccumulators.size == 2)
    val ite = intermediatesExceptAccumulators
      .filterNot({ case (_, i) => i.isInstanceOf[Accumulator] })
      .collectFirst({ case (y, _) if y != x => y })
      .get
    val expected = GenStmBuild(
      data = Sum(x, x)().tchk(),
      valid = True,
      intermediates = ListMap(
        b -> ExprAccumulator(
          Some(ExprIntermediate(True)),
          ExprIntermediate(Not(b)().tchk())
        ),
        ite -> MuxIntermediate(b, C(42)(U8), C(0)(U8)),
        x -> ExprIntermediate(Sum(C(5)(U8), ite)().tchk())
      ),
      producers = Map()
    )
    assert(actual == expected)
  }

  test("MuxAndStmDataInsideFunction") {
    val s = Param("s")(TyStm(I16, 16))
    val x = Param("x")(I16)
    val fImpl = Function(
      x,
      Sum(
        Mux(
          x gt C(0)(I16),
          x,
          Mux(x gt C(-10)(I16), C(-10)(I16), C(0)(I16))()
        )(),
        Mux(x lt C(42)(I16), x, C(42)(I16))(),
        StmData(s)()
      )()
    )().tchk()
    val sdataIntermediateName = Param("s_data")(I16)
    val (actualExpr, actualIntermediates) =
      IntermediateInsertion(fImpl, Map(StmData(s)() -> sdataIntermediateName))

    assert(actualExpr.isInstanceOf[Param])
    val actualExprParam = actualExpr.asInstanceOf[Param]

    assert(actualIntermediates.size == 2)
    val FunctionIntermediate(Seq(actualX), actualFunIntermediates, actualBody) =
      actualIntermediates(actualExprParam)
    assert(actualX == x)
    val (sdata, StmDataIntermediate(actualS)) =
      (actualIntermediates - actualExprParam).head
    assert(actualS == s)
    assert(sdata == sdataIntermediateName)

    val (ite1, ite2, ite3) = actualBody match {
      case Sum(ite2: Param, ite3: Param, actualSdata) =>
        assert(actualSdata == sdata)
        actualFunIntermediates(ite2) match {
          case MuxIntermediate(_, _, ite1: Param) =>
            (ite1, ite2, ite3)
          case e =>
            fail(s"wrong RHS for second if-then-else: $e")
        }
      case body =>
        fail(s"wrong body: $body")
    }

    val expectedIntermediates = ListMap(
      ite1 -> MuxIntermediate(x gt C(-10)(I16), C(-10)(I16), C(0)(I16)),
      ite2 -> MuxIntermediate(x gt C(0)(I16), x, ite1),
      ite3 -> MuxIntermediate(x lt C(42)(I16), x, C(42)(I16))
    )
    assert(actualFunIntermediates == expectedIntermediates)
  }

  /* Undefined initial values for the accumulators should stay as-is so that
   * later passes can easily recognize it (e.g., to know that there's no need
   * to emit reset logic).
   */
  test("UndefinedInitialValue") {
    val acc = Param("acc")(U8)
    val p = Param("p")(TyStm(U8, 16))
    val original = GenStmBuild(
      data = acc,
      valid = True,
      intermediates = ListMap(
        acc -> ExprAccumulator(None, ExprIntermediate(StmData(p)().tchk()))
      ),
      producers = Map(
        p -> (p, True)
      )
    )
    val actual = IntermediateInsertion(original)
    assert(
      actual.intermediates.collect({ case (_, a: Accumulator) => a }).size == 1
    )
    assert(
      actual.intermediates
        .collectFirst({ case (_, a: ExprAccumulator) => a })
        .init
        .isEmpty
    )
  }

  test("DspBlocks") {
    val mul1 = Param("mul1")(I44)
    val mul2 = Param("mul2")(I44)
    val b = Param("b")(TyBool)
    val p = Param("p")(TyStm(TyTuple(TyTuple(U8, U8), TyVec(U8, 4)), 16))
    val original = GenStmBuild(
      data = ARShift(mul2, 2)().tchk(),
      valid = True,
      intermediates = ListMap(
        b -> ExprAccumulator(
          Some(ExprIntermediate(True)),
          ExprIntermediate(Not(b)().tchk())
        ),
        mul1 -> ExprIntermediate(C(0)(U8)),
        mul2 -> ForGenerateIntermediate(
          AgilexMac2(
            VecAccess(StmData(p)().__1, 0)().tchk(),
            Sum(
              C(1)(U8),
              Mux(b, VecAccess(StmData(p)().__1, 1)(), C(1)(U8))()
            )()
              .tchk(),
            VecAccess(StmData(p)().__1, 2)().tchk(),
            Sum(
              C(1)(U8),
              Mux(b, VecAccess(StmData(p)().__1, 3)(), C(1)(U8))()
            )()
              .tchk(),
            mul1,
            pipeline = 2
          )
        )
      ),
      producers = Map(
        p -> (p, True)
      )
    )
    val actual = IntermediateInsertion(original)

    val ipBlocks = actual.intermediates
      .flatMap({ case (_, i) => i.intermediateDescendants :+ i })
      .collect({ case i: IpBlockInst => i })
      .toSeq
    assert(ipBlocks.length == 1)
    val mac2Count = ipBlocks.count(_.isInstanceOf[AgilexMac2])
    assert(mac2Count == 1)
    val mac2 = ipBlocks.collectFirst({ case i: AgilexMac2 => i }).get
    mac2 match {
      case AgilexMac2(
            // Keep tuple access as-is
            VecAccess(TupleAccess(_: Param, IntCst(1)), IntCst(0)),
            // Pull out sum
            _: Param,
            VecAccess(TupleAccess(_: Param, IntCst(1)), IntCst(2)),
            _: Param,
            mul1Actual: Param,
            2
          ) if mul1Actual == mul1 =>
        ()
      case i => fail(s"invalid mac2: $i")
    }
  }
}
