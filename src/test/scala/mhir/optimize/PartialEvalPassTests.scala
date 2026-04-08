package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.experimental.StmAccRangeAnalysis
import mhir.optimize.{PartialEvalPass => PE}
import mhir.sugar._
import mhir.testing.ParamStore
import org.scalatest.funsuite.AnyFunSuite

class PartialEvalPassTests extends AnyFunSuite {
  private val x = new ParamStore("x")
  private val y = new ParamStore("y")

  private val lpe = (e: Expr) => PartialEvalPass.partialEval(e.tchk().lower())

  test("PadTo:Const") {
    for (n <- -10 to 10) {
      val e = PadTo(IntCst(n)(I8), 16)()
      val actual = lpe(e)
      val expected = mhir.eval.eval(e)
      assert(actual == expected)
      assert(actual.typ == I16)
    }
  }

  test("PadTo:SameWidth") {
    val x = Param("x")()
    for (t <- COMMON_INT_TYPES) {
      val actual = lpe(PadTo(x.rebuild(t), t.w)())
      val expected = x.rebuild(t)
      assert(actual == expected)
      assert(actual.typ == expected.typ)
    }
  }

  test("TruncateTo:Const") {
    for (n <- -4 to 3) {
      val e = TruncateTo(IntCst(n)(I8), 3)()
      val actual = lpe(e)
      val expected = mhir.eval.eval(e)
      assert(actual == expected)
      assert(actual.typ == TySInt(3))
    }
  }

  test("TruncateTo:SameWidth") {
    val x = Param("x")()
    for (t <- COMMON_INT_TYPES) {
      val actual = lpe(TruncateTo(x.rebuild(t), t.w)())
      val expected = x.rebuild(t)
      assert(actual == expected)
      assert(actual.typ == expected.typ)
    }
  }

  test("Truncate(Pad(x))") {
    val x = Param("x")(U8)

    assert(lpe(TruncateTo(PadTo(x, 8)(), 8)()) == x)
    assert(lpe(TruncateTo(PadTo(x, 9)(), 8)()) == x)
    assert(lpe(TruncateTo(PadTo(x, 10)(), 8)()) == x)

    assert(lpe(TruncateTo(PadTo(x, 9)(), 7)()) == TruncateTo(x, 7)())
    assert(lpe(TruncateTo(PadTo(x, 10)(), 7)()) == TruncateTo(x, 7)())
    assert(lpe(TruncateTo(PadTo(x, 11)(), 7)()) == TruncateTo(x, 7)())

    assert(lpe(TruncateTo(PadTo(x, 10)(), 9)()) == PadTo(x, 9)())
    assert(lpe(TruncateTo(PadTo(x, 11)(), 9)()) == PadTo(x, 9)())
    assert(lpe(TruncateTo(PadTo(x, 12)(), 9)()) == PadTo(x, 9)())
  }

  test("Pad(Truncate(x))") {
    val x = Param("x")(U8)

    // TODO: can we somehow do this while still recording the assumption that x
    //       is small enough to be truncated like this?

    assert(lpe(PadTo(TruncateTo(x, 8)(), 8)()) == x)
    assert(lpe(PadTo(TruncateTo(x, 7)(), 8)()) == x)
    assert(lpe(PadTo(TruncateTo(x, 6)(), 8)()) == x)

    assert(lpe(PadTo(TruncateTo(x, 7)(), 9)()) == PadTo(x, 9)())
    assert(lpe(PadTo(TruncateTo(x, 6)(), 9)()) == PadTo(x, 9)())
    assert(lpe(PadTo(TruncateTo(x, 5)(), 9)()) == PadTo(x, 9)())

    assert(lpe(PadTo(TruncateTo(x, 6)(), 7)()) == TruncateTo(x, 7)())
    assert(lpe(PadTo(TruncateTo(x, 5)(), 7)()) == TruncateTo(x, 7)())
    assert(lpe(PadTo(TruncateTo(x, 4)(), 7)()) == TruncateTo(x, 7)())
  }

  test("Pad(Pad(x))") {
    val x = Param("x")(U8)
    val actual = lpe(PadTo(PadTo(x, 10)(), 12)())
    assert(actual == PadTo(x, 12)())
  }

  test("Truncate(Truncate(x))") {
    val x = Param("x")(U8)
    val actual = lpe(TruncateTo(TruncateTo(x, 7)(), 5)())
    assert(actual == TruncateTo(x, 5)())
  }

  test("ToUnsigned:Const") {
    for (n <- 0 to 10) {
      val e = ToUnsigned(IntCst(n)(I8))()
      val actual = lpe(e)
      val expected = mhir.eval.eval(e)
      assert(actual == expected)
      assert(actual.typ == TyUInt(7))
    }
  }

  test("ToSigned:Const") {
    for (n <- 0 to 10) {
      val e = ToSigned(IntCst(n)(U8))()
      val actual = lpe(e)
      val expected = mhir.eval.eval(e)
      assert(actual == expected)
      assert(actual.typ == TySInt(9))
    }
  }

  test("ToSigned(ToUnsigned(x))") {
    val x = Param("x")(I8)
    val e = ToSigned(ToUnsigned(x)())().tchk().lower()
    assert(PE.partialEval(e) == x)
  }

  test("ToUnsigned(ToSigned(x))") {
    val x = Param("x")(U8)
    val e = ToUnsigned(ToSigned(x)())().tchk().lower()
    assert(PE.partialEval(e) == x)
  }

  test("ToSigned(x) < ToSigned(y)") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    assert(PE.partialEval(ToSigned(x)() lt ToSigned(y)()) == (x lt y))
  }

  test("ToSigned(x) < -1:i9") {
    val x = Param("x")(U8)
    assert(PE.partialEval(ToSigned(x)() lt C(-1)(I9)) == False)
  }

  test("ToSigned(x) < 42:i9") {
    val x = Param("x")(U8)
    assert(PE.partialEval(ToSigned(x)() lt C(42)(I9)) == (x lt 42))
  }

  test("13:i9 < ToSigned(x)") {
    val x = Param("x")(U8)
    assert(PE.partialEval(C(13)(I9) lt ToSigned(x)()) == (13 lt x))
  }

  test("ToSigned(x) > 1:i9") {
    val x = Param("x")(U8)
    assert(PE.partialEval(ToSigned(x)() gt C(1)(I9)) == (x gt 1))
  }

  test("ToSigned(x) <= 2:i9") {
    val x = Param("x")(U8)
    assert(PE.partialEval(ToSigned(x)() leq C(2)(I9)) == (x leq 2))
  }

  test("ToSigned(x) >= 3:i9") {
    val x = Param("x")(U8)
    assert(PE.partialEval(ToSigned(x)() geq C(3)(I9)) == (x geq 3))
  }

  test("ToSigned(x) == -1:i9") {
    val x = Param("x")(U8)
    assert(PE.partialEval(ToSigned(x)() equ C(-1)(I9)) == False)
  }

  test("ToSigned(x) == 4:i9") {
    val x = Param("x")(U8)
    assert(PE.partialEval(ToSigned(x)() equ C(4)(I9)) == (x equ 4))
  }

  test("PadTo(x:u8, 32) < PadTo(y:u16, 32)") {
    val x = Param("x")(U8)
    val y = Param("y")(U16)
    val e = PadTo(x, 32)() lt PadTo(y, 32)()
    assert(PE.partialEval(e) == (PadTo(x, 16)() lt y))
  }

  test("PadTo(x:i16, 20) < PadTo(y:i8, 20)") {
    val x = Param("x")(I16)
    val y = Param("y")(I8)
    val e = PadTo(x, 20)() lt PadTo(y, 20)()
    assert(PE.partialEval(e) == (x lt PadTo(y, 16)()))
  }

  test("PadTo(x, 16) < 42:u16") {
    val x = Param("x")(U8)
    assert(PE.partialEval(PadTo(x, 16)() lt C(42)(U16)) == (x lt 42))
  }

  test("PadTo(x:u8, 32) < 512:u32") {
    val x = Param("x")(U8)
    assert(PE.partialEval(PadTo(x, 32)() lt C(512)(U32)) == True)
  }

  test("PadTo(x:i8, 29) == PadTo(y:i16, 29)") {
    val x = Param("x")(I8)
    val y = Param("y")(I16)
    val e = PadTo(x, 29)() equ PadTo(y, 29)()
    assert(PE.partialEval(e) == (PadTo(x, 16)() equ y))
  }

  test("PadTo(x:u16, 17) == PadTo(y:u8, 17)") {
    val x = Param("x")(U16)
    val y = Param("y")(U8)
    val e = PadTo(x, 17)() equ PadTo(y, 17)()
    assert(PE.partialEval(e) == (x equ PadTo(y, 16)()))
  }

  test("PadTo(x, 16) == 99:u16") {
    val x = Param("x")(U8)
    assert(PE.partialEval(PadTo(x, 16)() equ C(99)(U16)) == (x equ 99))
  }

  test("42:i32 == PadTo(x, 32)") {
    val x = Param("x")(I8)
    assert(PE.partialEval(C(42)(I32) equ PadTo(x, 32)()) == (C(42)() equ x))
  }

  test("TruncateTo(x:u32, 8) < TruncateTo(y:u16, 8)") {
    val x = Param("x")(U32)
    val y = Param("y")(U16)
    val e = TruncateTo(x, 8)() lt TruncateTo(y, 8)()
    assert(PE.partialEval(e) == (x lt PadTo(y, 32)()))
  }

  test("TruncateTo(x:u16, 8) < TruncateTo(y:u32, 8)") {
    val x = Param("x")(U16)
    val y = Param("y")(U32)
    val e = TruncateTo(x, 8)() lt TruncateTo(y, 8)()
    assert(PE.partialEval(e) == (PadTo(x, 32)() lt y))
  }

  test("TruncateTo(x, 8) < 42:u8") {
    val x = Param("x")(U16)
    assert(PE.partialEval(TruncateTo(x, 8)() lt C(42)(U8)) == (x lt 42))
  }

  test("-10:i16 < TruncateTo(x, 16)") {
    val x = Param("x")(I32)
    assert(
      PE.partialEval(C(-10)(I16) lt TruncateTo(x, 16)()) == (0 lt Sum(10, x)())
    )
  }

  test("10:i16 < TruncateTo(x, 16)") {
    val x = Param("x")(I32)
    assert(PE.partialEval(C(10)(I16) lt TruncateTo(x, 16)()) == (10 lt x))
  }

  test("TruncateTo(x:i32, 9) == TruncateTo(y:i16, 9)") {
    val x = Param("x")(I32)
    val y = Param("y")(I16)
    val e = TruncateTo(x, 9)() equ TruncateTo(y, 9)()
    assert(PE.partialEval(e) == (x equ PadTo(y, 32)()))
  }

  test("TruncateTo(x:u16, 10) == TruncateTo(y:u32, 10)") {
    val x = Param("x")(U16)
    val y = Param("y")(U32)
    val e = TruncateTo(x, 10)() equ TruncateTo(y, 10)()
    assert(PE.partialEval(e) == (PadTo(x, 32)() equ y))
  }

  test("TruncateTo(x, 8) == 99:u8") {
    val x = Param("x")(U16)
    assert(PE.partialEval(TruncateTo(x, 8)() equ C(99)(U8)) == (x equ 99))
  }

  test("42:i16 == TruncateTo(x, 16)") {
    val x = Param("x")(I32)
    assert(
      PE.partialEval(C(42)(I16) equ TruncateTo(x, 16)()) == (C(42)() equ x)
    )
  }

  test("ToUnsigned(x) < 42:u8") {
    val x = Param("x")(I9)
    assert(PE.partialEval(ToUnsigned(x)() lt C(42)(U8)) == (x lt 42))
  }

  test("12:u8 < ToUnsigned(x)") {
    val x = Param("x")(I9)
    assert(PE.partialEval(C(12)(U8) lt ToUnsigned(x)()) == (12 lt x))
  }

  test("ToUnsigned(x) == ToUnsigned(y)") {
    val x = Param("x")(I9)
    val y = Param("y")(I9)
    val e = ToUnsigned(x)() equ ToUnsigned(y)()
    assert(PE.partialEval(e) == (x equ y))
  }

  test("ToUnsigned(x) == 13:u8") {
    val x = Param("x")(I9)
    assert(PE.partialEval(ToUnsigned(x)() equ C(13)(U8)) == (x equ 13))
  }

  test("13:u8 == ToUnsigned(x)") {
    val x = Param("x")(I9)
    assert(PE.partialEval(C(13)(U8) equ ToUnsigned(x)()) == (C(13)() equ x))
  }

  test("TruncateTo(ToUnsigned(x), 8) < TruncateTo(ToUnsigned(y), 8)") {
    val x = Param("x")(I16)
    val y = Param("y")(I16)
    val e = TruncateTo(ToUnsigned(x)(), 8)() lt TruncateTo(ToUnsigned(y)(), 8)()
    assert(PE.partialEval(e) == (x lt y))
  }

  test("TruncateTo(ToUnsigned(x), 8) == TruncateTo(ToUnsigned(y), 8)") {
    val x = Param("x")(I16)
    val y = Param("y")(I16)
    val e =
      TruncateTo(ToUnsigned(x)(), 8)() equ TruncateTo(ToUnsigned(y)(), 8)()
    assert(PE.partialEval(e) == (x equ y))
  }

  test("ReusedParam:FreeAndBoundTupleVar") {
    val x = Param("x")(TyTuple(U8, U8))
    val e =
      Tuple(
        x.__0 >= 1,
        FunCall(
          Function(x, x.__0 >= 1)(),
          Tuple(x.__1, x.__0)()
        )(),
        x.__0 >= 1
      )().tchk().lower()
    val facts = FactSet().geq(x.__0, 1)
    val actual = PartialEvalPass.partialEval(e)(facts)
    val expected = Tuple(True, Not(LessThan(x.__1, C(1)())())(), True)()
    assert(actual == expected)
  }

  test("ReusedParam:NestedScalarFunctions") {
    val e = (U8 ::+ (y =>
      Mux(y > 42, U8 ::+ (y => y > 10), U8 ::+ (_ => y > 45))()
    )).tchk().lower()
    val actual = PE.partialEval(e)
    val expected = U8 ::+ (y =>
      Mux(
        LessThan(42, y)(),
        U8 ::+ (z => LessThan(10, z)()),
        U8 ::+ (_ => False)
      )()
    )
    assert(actual == expected)
  }

  test("ReusedParam:StmAccumulator") {
    val a = Param("a")(U8)
    val s = StmBuild(
      10,
      Tuple(a >= 0, a < 4)(),
      True,
      Map(a -> (C(0)(U8), a + 1))
    )()
    val e = Tuple(a < 4, s)().tchk().lower()
    val facts =
      FactSet()
        // Inside the stream, a >= 0
        .range(s, StmAccRange(Map(a -> ScalarRange(Some(0), None))))
        // Outside the stream, a < 4
        .lt(a, 4)
    val actual = PartialEvalPass.partialEval(e)(facts)
    val expected = Tuple(
      True,
      StmBuild(
        10,
        Tuple(True, a lt 4)(),
        True,
        Map(a -> (C(0)(U8), Sum(a, 1)()))
      )()
    )()
    assert(actual == expected)
  }

  test("ReusedParam:VecIndex") {
    val i = Param("i")(U8)
    val e =
      Tuple(
        i > 1,
        VecBuild(7, Function(i, Tuple(i >= 0, i < 7, i > 2)())())(),
        i > 2
      )().tchk().lower()
    val facts = FactSet().geq(i, 3)
    val actual = PartialEvalPass.partialEval(e)(facts)
    val expected =
      Tuple(
        True,
        VecBuild(7, U8 ::+ (i => Tuple(True, True, LessThan(2, i)())()))(),
        True
      )()
    assert(actual == expected)
  }

  test("MuxTrueBranchSpecialCaseOfFalseBranch") {
    val n = C(10)(I8)
    val i = Param("i")(I8)
    val acc = Param("acc")((I8, U8))
    val z = Param("z")(I8)
    val delta = Param("delta")(I8)
    val e = Mux(
      i === (n - 1),
      z + acc.__0 * delta,
      z + ((acc.__0 + (i + 1)) - n) * delta
    )().tchk().lower()
    val actual = PE.partialEval(e)
    val expected = Sum(
      z,
      Prod(delta, acc.__0)(),
      Prod(delta, i)(),
      Prod(-9, delta)()
    )()
    assert(actual == expected)
  }

  test("MuxFalseBranchSpecialCaseOfTrueBranch") {
    val x = Param("x")(I8)
    val e = Mux(x < 42, Max(x, C(10)(I8))(), x)().tchk().lower()
    val expected = Mux(LessThan(10, x)(), x, C(10)(I8))()
    assert(PE.partialEval(e) == expected)
  }

  test("ScalarInequality:x<x+1") {
    val x = Param("x")(U8)
    assert(lpe((x - 1) < x) == True)
    assert(lpe(x < x) == False)
  }

  test("ScalarEquality:x+c==k") {
    val x = Param("x")(I8)
    assert(lpe(x - 1 === 3) == Equal(x, 4)())
    assert(lpe(5 + x === 2) == Equal(Sum(3, x)(), 0)())
  }

  test("ScalarEquality:x+c<k") {
    val x = Param("x")(U8)
    assert(lpe(x - 2 < 9) == LessThan(x, 11)())
    assert(lpe(-3 + x < 4) == LessThan(x, 7)())
  }

  test("ScalarInequality:(x >= c) && (x < c + 1)") {
    val c = 42
    val x = Param("x")(U8)
    val e = (x >= c) && (x < (c + 1))
    assert(lpe(e) == (x equ c))
  }

  test("x < 9 || x == 9") {
    val x = Param("x")(U8)
    val e = (x lt C(9)(U8)) || (x equ C(9)(U8))
    assert(PE.partialEval(e) == (x leq 9))
  }

  test("x < 255 || x == 255") {
    val x = Param("x")(U8)
    val e = (x lt C(255)(U8)) || (x equ C(255)(U8))
    assert(PE.partialEval(e) == True)
  }

  /** The partial evaluator may try to combine the constants on opposite sides
    * of a comparison. But in some cases, this will lead to overflow!
    */
  test("-1:i1 == 0:i1") {
    val i1 = TySInt(1)
    val e = C(-1)(i1) equ C(0)(i1)
    assert(PE.partialEval(e) == False)
  }

  /** The partial evaluator may try to combine the constants on opposite sides
    * of a comparison. But in some cases, this will lead to overflow!
    */
  test("x + 4:i4 < y - 4:i4") {
    val i4 = TySInt(4)
    val e = Sum(x(i4), C(4)(i4))() lt Sum(y(i4), C(-4)(i4))()
    assert(PE.partialEval(e) == e)
  }

  test("StmAccumulatorGreaterOrEqualToInitialVal") {
    val n = Param("n")(U8)
    val z = Param("z")(I8)
    val a = Param("a")(I8)
    val s = StmBuild(
      n,
      a,
      a >= z,
      Map[Param, (Expr, Expr)](
        a -> (z, Mux(a >= z, a + 3, a - 1)())
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val facts = FactSet().range(s, StmAccRangeAnalysis.findAccRanges(s))
    val expected = StmBuild(
      n,
      a,
      True,
      Map[Param, (Expr, Expr)](
        a -> (z, Sum(a, C(3)())())
      )
    )()
    assert(PartialEvalPass.partialEval(s)(facts) == expected)
  }

  test("StmOneElement") {
    val z = Param("z")(U8)
    val a0 = Param("a")(U8)
    val a1 = Param("a")(U8)
    val s = StmBuild(
      1,
      a0,
      True,
      Map[Param, (Expr, Expr)](
        a0 -> (z, a0 + a1 + a1),
        a1 -> (C(0)(U8), a1 + a0)
      )
    )().tchk().lower()
    val expected = StmBuild(1, z, True)()
    assert(PartialEvalPass.partialEvalStmBuild(s) == expected)
  }

  test("StmOneElementWithInputs") {
    val n = 5
    val z = Param("z")(U8)
    val i = Param("i")(U8)
    val a = Param("a")(U8)
    val s = Param("s")(TyStm(U8, -1))
    val sum = StmBuild(
      1,
      a + StmData(s)(),
      i === n - 1,
      Map[Param, (Expr, Expr)](
        i -> (C(0)(U8), i + 1),
        a -> (z, a + StmData(s)()),
        s -> (StmCount(C(n)(U8))(), True)
      )
    )().tchk().lower()
    val expected = StmBuild(1, Sum(z, (0 until n).sum)(), True)()
    val actual = PartialEvalPass.partialEvalStmBuild(sum).tchk().lower()
    assert(actual == expected)
  }

  test("StmOneElementNotReducible") {
    // I do NOT want this to be simplified to something like
    //   StmCst(1, StmData(s)())
    // because then we're calling StmData(s)() outside a stream
    val s = Param("s")(TyStm(U8, 5))
    val a = Param("a")(TyStm(U8, -1))
    val stm = StmBuild(
      1,
      StmData(a)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (s, True)
      )
    )()
    assert(PartialEvalPass.partialEvalStmBuild(stm) == stm)
  }

  test("VecBuildIndexRange") {
    val n = Param("n")(U8)
    val v =
      VecBuild(
        n,
        U8 ::+ (i => Tuple(i > -1, i < n + 1, i >= n, i < 0, i > 0)())
      )().tchk().lower()
    val expected =
      VecBuild(
        n,
        U8 ::+ (i => Tuple(True, True, False, False, i gt 0)())
      )()
    assert(PartialEvalPass.partialEval(v) == expected)
  }

  test("MuxCondition:x < 10 && x >= 0") {
    val x = Param("x")(I8)
    val e =
      Mux(
        x < 10 && x >= 0,
        Tuple(x < 11, x >= 10, x >= -1, x < 9)(),
        Tuple(x < 9, x >= 10, x >= 11, True)()
      )().tchk().lower()
    val expected =
      Mux(
        (x lt 10) && (x geq 0),
        Tuple(True, False, True, x lt 9)(),
        Tuple(x lt 9, x geq 10, x geq 11, True)()
      )()
    val actual = PartialEvalPass.partialEval(e)
    assert(actual == expected)
  }

  test("MuxCondition:ToSigned(x) < 5") {
    val x = Param("x")(U8)
    val e =
      Mux(
        ToSigned(x)() < C(5)(I9),
        ToSigned(x)() < C(10)(I9),
        ToSigned(x)() < C(3)(I9)
      )().tchk().lower()
    val expected = x lt C(5)(I9)
    val actual = lpe(e)
    assert(actual == expected)
  }

  test("SmallerOrEqualWithVarRanges") {
    val n = Param("n")(U8)
    val i = Param("i")(U8)
    val facts = FactSet().geq(n, 2).geq(i, 0).lt(i, n)
    val e = (1 - n + i).tchk().lower()
    assert(PE.isSmallerOrEqual(e, 0)(facts).contains(true))
    assert(PE.isSmallerOrEqual(e, 0)().isEmpty)
  }

  test("GreaterOrEqualWithVarRanges") {
    val c = Param("c")(I8)
    val facts = FactSet().geq(c, 0)
    val delta = 2 - c + 2 * c
    assert(PartialEvalPass.isGreaterOrEqual(delta, 0)(facts).contains(true))
    assert(PartialEvalPass.isGreaterOrEqual(delta, 0)().isEmpty)
  }

  test("ArbitraryConditionIsTrue") {
    val c0 = Param("c0")(TyBool)
    val c1 = Param("c1")(TyBool)
    val facts = FactSet().assumeTrue(c0 && (c0 || c1))
    val e = (U8 ::+ (i => Mux(c0, i + 1, i)())).tchk().lower()
    val actual = PartialEvalPass.partialEval(e)(facts)
    val expected = U8 ::+ (i => Sum(i, 1)())
    assert(actual == expected)
  }

  test("ClearVariableRange") {
    val i = Param("i")(U8)
    val facts = FactSet().assumeTrue(i === 0)
    val e = Function(i, i === 0)().tchk().lower()
    val actual = PartialEvalPass.partialEval(e)(facts)
    val expected = U8 ::+ (i => i equ 0)
    assert(actual == expected)
  }

  test("VecAccess(VecBuild)") {
    val e =
      VecAccess(VecBuild(5, U8 ::+ (i => 7 + i))(), C(3)(U8))().tchk().lower()
    val actual = PE.partialEval(e)
    val expected = C(10)()
    assert(actual == expected)
  }

  test("VecAccess(VecLiteral)") {
    val v = VecLiteral(C(41)(U8), C(42)(U8), C(43)(U8))()
    assert(PE.partialEval(VecAccess(v, 0)()) == C(41)(U8))
    assert(PE.partialEval(VecAccess(v, 1)()) == C(42)(U8))
    assert(PE.partialEval(VecAccess(v, 2)()) == C(43)(U8))
  }

  test("VecLiteral(...)[i][IntCst]") {
    val v = VecLiteral(
      VecLiteral(C(0)(U8), C(2)(U8), C(4)(U8))(),
      VecLiteral(C(10)(U8), C(12)(U8), C(14)(U8))(),
      VecLiteral(C(20)(U8), C(22)(U8), C(24)(U8))()
    )().tchk()
    val i = Param("i")(U8)

    val va0 = VecAccess(VecAccess(v, i)(), 0)().tchk()
    val expected0 =
      VecAccess(VecLiteral(C(0)(U8), C(10)(U8), C(20)(U8))(), i)().tchk()
    assert(PE.partialEval(va0) == expected0)

    val va1 = VecAccess(VecAccess(v, i)(), 1)().tchk()
    val expected1 =
      VecAccess(VecLiteral(C(2)(U8), C(12)(U8), C(22)(U8))(), i)().tchk()
    assert(PE.partialEval(va1) == expected1)

    val va2 = VecAccess(VecAccess(v, i)(), 2)().tchk()
    val expected2 =
      VecAccess(VecLiteral(C(4)(U8), C(14)(U8), C(24)(U8))(), i)().tchk()
    assert(PE.partialEval(va2) == expected2)
  }

  ignore("NestedMux") {
    val i = Param("i")(U8)
    val n = Param("n")(U8)
    val c0 = Param("c0")((U8, I16))
    val c1 = Param("c1")((U8, I16))
    val c2 = Param("c2")((U8, I16))
    val e = Mux(i === -1 + n, c0, Mux(1 + i < n, c1, c2)())().tchk().lower()

    val actual0 = PE.partialEval(e)(FactSet())
    val expected0 =
      Mux(
        Sum(1, ToSigned(i)())() equ ToSigned(n)(),
        c0,
        Mux(Sum(1, i)() lt n, c1, c2)()
      )()
    assert(actual0 == expected0)

    val facts = FactSet().between(i, 0, n)
    val expected1 = Mux(Sum(1, ToSigned(i)())() equ ToSigned(n)(), c0, c1)()
    val actual1 = PE.partialEval(e)(facts)
    assert(actual1 == expected1)
  }

  test("NestedLet") {
    val n = Param("n")(U8)
    val m = Param("m")(U8)
    val e =
      VecBuild(n, U32 ::+ (i => VecBuild(m, U32 ::+ (j => Tuple(i, j)()))()))()
    val lets = Let(n, C(3)(U8), Let(m, C(2)(U8), e)())()
    val evaluated = lpe(lets)
    val expected =
      VecBuild(
        C(3)(U8),
        U32 ::+ (i => VecBuild(C(2)(U8), U32 ::+ (j => Tuple(i, j)()))())
      )()
    assert(evaluated == expected)
    assert(evaluated.typ == TyVec(TyVec((U32, U32), C(2)(U8)), C(3)(U8)))
  }

  test("if (n == 0) then VecBuild(n, ...) else VecBuild(n, ...)") {
    val n = Param("n")(U8)
    val e =
      Mux(
        n equ C(0)(U8),
        VecBuild(n, U32 ::+ (_ => C(-1)(I8)))(),
        VecBuild(n, U32 ::+ (_ => C(42)(I8)))()
      )().tchk().lower()
    assert(PE.partialEval(e) == e)
  }

  /** Used to debug an infinite loop in the partial evaluator.
    */
  test("true && !SmartLessThan(t, 0)") {
    val t = Param("t")(I32)
    val e = And(True, Not(SmartLessThan(t, 0)())())()
    val actual = PE.partialEval(e)
    val expected = Not(SmartLessThan(t, 0)())()
    assert(actual == expected)
  }

  test("if (c) then VecBuild(...) else v") {
    val c = Param("c")(TyBool)
    val n = Param("n")(U8)
    val v = Param("v")(TyVec(U8, n))
    val e =
      Mux(c, VecBuild(n, U8 ::+ (i => VecAccess(v, i)()))(), v)().tchk().lower()
    assert(PE.partialEval(e) == e)
  }
}
