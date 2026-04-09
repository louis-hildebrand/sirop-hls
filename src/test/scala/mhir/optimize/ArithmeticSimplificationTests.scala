package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.optimize.{PartialEvalPass => PE}
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class ArithmeticSimplificationTests extends AnyFunSuite {

  /** Lower and partially evaluate.
    */
  private val lpe = (e: Expr) => PartialEvalPass.partialEval(e.tchk().lower)

  test("IntCst:U32") {
    val e = C(0)(U32)
    val actual = lpe(e)
    assert(actual == e)
    assert(actual.typ == e.typ)
  }

  test("IntCst:I16") {
    val e = C(-2)(I16)
    val actual = lpe(e)
    assert(actual == e)
    assert(actual.typ == e.typ)
  }

  test("Sum") {
    val e1 = VecAccess(Param("v")(TyVec(I16, 5)), Param("i")(U8))()
    val e2 = Param("p")((TyBool, I16)).__1

    assert(lpe(C(1)(U8) + C(2)(U8)) == C(3)())
    assert(lpe(C(1)(U8) + e1) == Sum(1, e1)())
    assert(lpe(e1 + C(0)()) == e1)
    assert(lpe(C(0)() + e1) == e1)
    assert(lpe(e1 - e1) == C(0)())
    assert(lpe(e1 - 2 * e1) == Prod(-1, e1)())
    assert(lpe(3 * e1 + 4 * e1) == Prod(7, e1)())
    assert(lpe((e1 + C(9)()) + C(3)()) == Sum(C(12)(), e1)())
    assert(lpe((C(8)() + e1) + C(3)()) == Sum(C(11)(), e1)())
    assert(lpe(C(3)() + (e1 + C(4)())) == Sum(C(7)(), e1)())
    assert(lpe(C(2)() + (C(1)() + e1)) == Sum(C(3)(), e1)())
    assert(lpe((e1 - C(9)()) + C(3)()) == Sum(C(-6)(), e1)())
    assert(lpe(C(3)() + (e1 - C(4)())) == Sum(C(-1)(), e1)())
    assert(lpe((C(8)() - e1) + C(3)()) == Sum(C(11)(), Prod(-1, e1)())())
    assert(lpe(C(2)() + (C(1)() - e1)) == Sum(C(3)(), Prod(-1, e1)())())
    assert(lpe(e1 + (e2 - e1)) == e2)
    assert(lpe(e2 + (e1 - e2)) == e1)
    assert(lpe((e2 - e1) + e1) == e2)
    assert(lpe((e1 - e2) + e2) == e1)
    assert(lpe((((e1 + C(1)()) + e2) - C(1)()) - e2) == e1)
  }

  test("Sum:(x:u8) + 1 - (x:u8)") {
    val x = Param("x")(U8)
    assert(lpe(x + 1 - x) == C(1)())
  }

  test("Sum:(x:i2) + 255 - (x:i2)") {
    val i2 = TySInt(2)
    val x = Param("x")(i2)
    assert(lpe(x + 255 - x) == C(255)())
  }

  // The non-arithmetic terms within a sum are also simplified
  test("SimplifiableBlackBoxInsideAdd") {
    val i = Param("i")(U8)
    val f = Param("f")(U8 ->: TyVec(I8, 3))
    val e3 =
      VecAccess(FunCall(f, Tuple(C(0)(U8), C(10)(U8), C(20)(U8))().__1)(), i)()
    val e4 = Param("x")(I8)

    val actual = lpe((e3 + e4 + C(42)()) - e4)
    val expected = Sum(C(42)(), VecAccess(FunCall(f, C(10)(U8))(), i)())()
    assert(actual == expected)
  }

  test("Mul") {
    val e = VecAccess(Param("v")(TyVec(I16, 10)), Param("i")(U8))()

    assert(lpe(C(4)(U8) * C(9)(U8)) == C(36)())
    assert(lpe(e * C(0)()) == C(0)())
    assert(lpe(C(0)() * e) == C(0)())
    assert(lpe(e * C(1)()) == e)
    assert(lpe(C(1)() * e) == e)
  }

  // The non-arithmetic terms within a product are also simplified
  test("SimplifiableBlackBoxInsideMul") {
    val i = Param("i")(U8)
    val f = Param("f")(U8 ->: TyVec(I8, 3))
    val e =
      VecAccess(FunCall(f, Tuple(C(0)(U8), C(10)(U8), C(20)(U8))().__1)(), i)()

    val actual = lpe((C(42)() * e) * C(3)())
    val expected = Prod(C(126)(), VecAccess(FunCall(f, 10)(), i)())()
    assert(actual == expected)
  }

  test("Div") {
    val e = Param("x")(I8)

    // TODO: What about simplifying x * y / x if x is non-constant? Might need to check that x != 0 first
    assert(lpe(e / IntCst(1)()) == e)
    assert(lpe(IntCst(6)() * e / IntCst(6)()) == e)
    assert(lpe((IntCst(6)() * e) / IntCst(3)()) == Prod(2, e)())
    assert(lpe((e * IntCst(15)()) / IntCst(5)()) == Prod(3, e)())
    assert(lpe(IntCst(27)() * e / IntCst(6)()) == Div(Prod(9, e)(), 2)())
    assert(lpe(e * IntCst(27)() / IntCst(6)()) == Div(Prod(9, e)(), 2)())
  }

  test("Mod") {
    val e = Param("x")(I8)

    assert(lpe(IntCst(17)() % IntCst(12)()) == IntCst(5)())
    assert(lpe(IntCst(0)() % e) == IntCst(0)())
  }

  test("MuxToWrappingSum:Valid1") {
    val x = Param("x")(U8)
    val e = Mux(x equ C(255)(U8), C(0)(U8), Sum(x, C(1)(U8))())().tchk()
    assert(PE.partialEval(e) == WrappingSum(x, C(1)(U8))())
  }

  test("MuxToWrappingSum:Valid2") {
    val x = Param("x")(I8)
    val e = Mux(x equ C(127)(I8), C(-128)(I8), Sum(x, C(1)(I8))())().tchk()
    assert(PE.partialEval(e) == WrappingSum(x, C(1)(I8))())
  }

  test("MuxToWrappingSum:NotValid1") {
    val x = Param("x")(U8)
    val u5 = TyUInt(5)
    val e =
      Mux(x.rebuild(u5) equ C(31)(u5), C(0)(U8), Sum(x, C(1)(U8))())().tchk()
    assert(PE.partialEval(e) == e)
  }

  test("MuxToWrappingSum:NotValid2") {
    val x = Param("x")(I8)
    val e = Mux(x equ C(127)(I8), C(0)(I8), Sum(x, C(1)(I8))())().tchk()
    assert(PE.partialEval(e) == e)
  }

  test("WrappingSum:AllInts") {
    assert(PE.partialEval(WrappingSum(C(10)(U8), C(32)(U8))()) == C(42)(U8))
    assert(PE.partialEval(WrappingSum(C(255)(U8), C(3)(U8))()) == C(2)(U8))
    assert(PE.partialEval(WrappingSum(C(255)(U16), C(3)(U16))()) == C(258)(U16))
    assert(PE.partialEval(WrappingSum(C(120)(I8), C(12)(I8))()) == C(-124)(I8))
    assert(PE.partialEval(WrappingSum(C(-10)(I8), C(-128)(I8))()) == C(118)(I8))
  }

  test("WrappingSum:NestedWithConstants") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val original =
      WrappingSum(WrappingSum(x, C(250)(U8))(), WrappingSum(y, C(10)(U8))())()
        .tchk()
    val expected = WrappingSum(C((250 + 10) % 256)(U8), x, y)()
    val actual = PE.partialEval(original)
    assert(actual == expected)
  }

  test("WrappingSum:NestedWithoutConstants") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val z = Param("z")(U8)
    val w = Param("w")(U8)
    val original =
      WrappingSum(x, WrappingSum(y, WrappingSum(z, w)())())().tchk()
    val expected = WrappingSum(w, x, y, z)()
    val actual = PE.partialEval(original)
    assert(actual == expected)
  }

  test("WrappingDiff:AllInts") {
    assert(PE.partialEval(WrappingDiff(C(42)(U8), C(43)(U8))()) == C(255)(U8))
    assert(
      PE.partialEval(WrappingDiff(C(42)(U16), C(43)(U16))())
        == C(65535)(U16)
    )
    assert(PE.partialEval(WrappingDiff(C(42)(I8), C(-125)(I8))()) == C(-89)(I8))
    assert(PE.partialEval(WrappingDiff(C(-120)(I8), C(10)(I8))()) == C(126)(I8))
  }

  test("WrappingProd:AllInts") {
    assert(PE.partialEval(WrappingProd(C(100)(U8), C(7)(U8))()) == C(188)(U8))
    assert(
      PE.partialEval(WrappingProd(C(100)(U16), C(7)(U16))()) == C(700)(U16)
    )
    assert(
      PE.partialEval(WrappingProd(C(100)(U16), C(700)(U16))())
        == C(4464)(U16)
    )
    assert(PE.partialEval(WrappingProd(C(16)(I8), C(15)(I8))()) == C(-16)(I8))
    assert(PE.partialEval(WrappingProd(C(-16)(I8), C(14)(I8))()) == C(32)(I8))
  }

  test("WrappingProd:NestedWithConstants") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val original =
      WrappingProd(WrappingProd(x, C(130)(U8))(), WrappingProd(y, C(3)(U8))())()
        .tchk()
    val expected = WrappingProd(C((130 * 3) % 256)(U8), x, y)()
    val actual = PE.partialEval(original)
    assert(actual == expected)
  }

  test("WrappingProd:NestedWithoutConstants") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val z = Param("z")(U8)
    val w = Param("w")(U8)
    val original =
      WrappingProd(x, WrappingProd(y, WrappingProd(z, w)())())().tchk()
    val expected = WrappingProd(w, x, y, z)()
    val actual = PE.partialEval(original)
    assert(actual == expected)
  }

  test("StandardArithmetic:(x + x + x) / 3") {
    val x = Param("x")(U8)
    val original = Div(Sum(x, x, x)(), C(3)(U8))()
    val simplified = PE.partialEval(original)
    assert(simplified == x)
  }

  /* Do NOT simplify (x + x + x) / 3 to x: it may not work if there's
   * overflow!
   */
  test("WrappingArithmetic:(x + x + x) / 3") {
    val x = Param("x")(U8)
    val original = Div(WrappingSum(x, x, x)(), C(3)(U8))()
    val simplified = PE.partialEval(original)
    assert(simplified == original)
  }

  test("IntFixProd:BothConstant") {
    val oneOver16 = FixCst(8)(TyFix(U8, 7))
    assert(
      PE.partialEval(IntFixProd(C(15)(U8), oneOver16)())
        == C(0)(U8)
    )
    assert(
      PE.partialEval(IntFixProd(C(16)(U8), oneOver16)())
        == C(1)(U8)
    )
    assert(
      PE.partialEval(IntFixProd(C(17)(U8), oneOver16)())
        == C(1)(U8)
    )
    assert(
      PE.partialEval(IntFixProd(C(65)(U8), oneOver16)())
        == C(4)(U8)
    )

    val fiveOver128 = FixCst(5)(TyFix(U8, 7))
    assert(
      PE.partialEval(IntFixProd(C(128)(U8), fiveOver128)())
        == C(5)(U8)
    )
    assert(
      PE.partialEval(IntFixProd(C(64)(U8), fiveOver128)())
        == C(2)(U8)
    )
    assert(
      PE.partialEval(IntFixProd(C(50)(U8), fiveOver128)())
        == C(1)(U8)
    )

    assert(
      PE.partialEval(IntFixProd(C(255)(U8), FixCst(255)(TyFix(U8, 7)))())
        == C(252)()
    )
  }

  /** Just shift right instead of multiplying.
    */
  test("IntFixProd:x *_ 1/4") {
    val x = Param("x")(U32)
    val y = FixCst(32)(TyFix(U8, 7))
    val original = IntFixProd(x, y)().tchk()
    val simplified = PE.partialEval(original)
    val expected = x >>> C(2)(U8)
    assert(simplified == expected)
  }

  /** Just shift right instead of multiplying.
    */
  test("IntFixProd:x *_ 1/16") {
    val x = Param("x")(U8)
    val y = FixCst(8)(TyFix(U8, 7))
    val original = IntFixProd(x, y)().tchk()
    val simplified = PE.partialEval(original)
    val expected = x >>> C(4)(U8)
    assert(simplified == expected)
  }

  /** Just shift right instead of multiplying.
    */
  test("IntFixProd:x *_ 2^n") {
    val xVal = C(42)(U8)
    for (n <- 1 to 24) {
      val x = Param("x")(U8)
      val y = FixCst(1L << (n + 7))(TyFix(U32, 7))
      val original = IntFixProd(x, y)().tchk()
      val simplified = PE.partialEval(original)

      // Correctness
      val actualVal = mhir.eval.eval(simplified.subPreserveType(x -> xVal))
      val expectedVal = mhir.eval.eval(original.subPreserveType(x -> xVal))
      assert(actualVal == expectedVal)

      // Effective simplification
      val expected = x <<< C(n)(U8)
      assert(simplified == expected)
    }
  }

  /** I don't think you can turn this one into a simple shift.
    */
  test("IntFixProd:x *_ 5/128") {
    val x = Param("x")(U8)
    val y = FixCst(5)(TyFix(U8, 7))
    val original = IntFixProd(x, y)().tchk()
    val simplified = PE.partialEval(original)
    assert(simplified == original)
  }

  test("IntFixProd:x *_ 0") {
    val x = Param("x")(U8)
    val y = FixCst(0)(TyFix(U8, 7))
    val original = IntFixProd(x, y)().tchk()
    val simplified = PE.partialEval(original)
    assert(simplified == C(0)(U8))
    assert(simplified.typ == U8)
  }

  test("IntFixProd:0 *_ y") {
    val x = C(0)(U8)
    val y = Param("y")(TyFix(U8, 7))
    val original = IntFixProd(x, y)().tchk()
    val simplified = PE.partialEval(original)
    assert(simplified == C(0)(U8))
    assert(simplified.typ == U8)
  }

  test("IntFixProd:x *_ 1") {
    val x = Param("x")(U8)
    val y = FixCst(128)(TyFix(U8, 7))
    val original = IntFixProd(x, y)().tchk()
    val simplified = PE.partialEval(original)
    assert(simplified == x)
  }

  test("LLShift") {
    val e = Param("x")(U8)

    assert(lpe(C(0)(U8) <<< C(0)(U8)) == C(0)())
    assert(lpe(C(0)(U0) <<< C(1)(U8)) == C(0)())
    assert(lpe(C(42)(U8) <<< C(1)(U8)) == C(84)())
    assert(lpe(C(42)(U8) <<< C(3)(U8)) == C(80)())
    assert(lpe(C(42)(U16) <<< C(3)(U8)) == C(336)())
    assert(lpe(C(99)(I8) <<< C(1)(U8)) == C(-58)())
    assert(lpe(C(99)(I16) <<< C(1)(U8)) == C(198)())

    assert(lpe(e <<< C(0)(U8)) == e)
  }

  test("LRShift") {
    val e = Param("x")(U8)

    assert(lpe(C(0)(U8) >>> C(0)(U8)) == C(0)())
    assert(lpe(C(0)(U0) >>> C(1)(U8)) == C(0)())
    assert(lpe(C(168)(U8) >>> C(1)(U8)) == C(84)())
    assert(lpe(C(168)(U16) >>> C(3)(U8)) == C(21)())
    assert(lpe(C(-29)(I8) >>> C(1)(U8)) == C(113)())
    assert(lpe(C(-29)(I16) >>> C(1)(U8)) == C(32753)())

    assert(lpe(e >>> C(0)(U8)) == e)
  }

  test("SingletonRange") {
    val x = Param("x")(U8)
    val facts = FactSet().between(x, 0, 1)
    assert(PE.partialEval(x)(facts) == C(0)())
  }

  test("MuxWithBoundedVariable1") {
    val x = Param("x")((U8, I8))
    val y = Param("y")(TyVec(I32, 3))
    val z = Param("z")(TyVec(I32, 3))
    val a = Param("a")(I8)
    val e = Mux(x.__1 >= C(-2)(I8) + a, y, z)().tchk().lower

    val facts0 = FactSet()
    val expected0 = Mux(Sum(2, x.__1)() lt a, z, y)()
    assert(PE.partialEval(e)(facts0) == expected0)
    val facts1 = FactSet().geq(x.__1, lpe(a - 1))
    assert(PE.partialEval(e)(facts1) == y)
    val facts2 = FactSet().lt(x.__1, lpe(a - 2))
    assert(PE.partialEval(e)(facts2) == z)
  }

  test("MuxWithBoundedVariable2") {
    val x = Param("x")(U8)
    val y = Param("y")(TyVec(I32, 3))
    val z = Param("z")(TyVec(I32, 3))
    val e = Mux(ToSigned(x)() < C(5)(I9), y, z)().tchk().lower

    val facts0 = FactSet()
    val expected0 = Mux(x lt 5, y, z)()
    assert(PE.partialEval(e)(facts0) == expected0)
    val facts1 = FactSet().geq(x, 5)
    assert(PE.partialEval(e)(facts1) == z)
    val facts2 = FactSet().lt(x, 5)
    assert(PE.partialEval(e)(facts2) == y)
  }

  test("LessThanWithBoundedVariable") {
    val a = Param("a")((U8, I32))
    val n = Param("n")(U8)
    val e = (a.__0 >= n).tchk().lower

    val facts0 = FactSet()
    val expected0 = Not(LessThan(a.__0, n)())()
    assert(PartialEvalPass.partialEval(e)(facts0) == expected0)
    val facts1 = FactSet().geq(a.__0, n)
    assert(PartialEvalPass.partialEval(e)(facts1) == True)
    val facts2 = FactSet().lt(a.__0, n)
    assert(PartialEvalPass.partialEval(e)(facts2) == False)
  }

  test("SimplifyBranchesOfMux") {
    val i = IntCst(0)(U8)
    val e = Mux(
      (i % 2) === 0,
      Tuple(i, 2 + (2 * i))(),
      Tuple(2 + (2 * i), i)()
    )()
    assert(lpe(e) == Tuple(0, 2)())
  }

  test("PossibleNegativeToUnsignedInFalseBranch") {
    val u7 = TyUInt(7)
    val x = Param("x")(I8)
    val e =
      Mux(x + C(1)(I8) === C(0)(I8), C(0)(u7), ToUnsigned(x)())().tchk().lower
    val expected = ToUnsigned(
      Mux(Sum(x, C(1)(I8))() equ C(0)(I8), C(0)(I8), x)()
    )().tchk()
    val actual = PE.partialEval(e)
    assert(actual == expected)
  }

  test("PossibleInvalidTruncationInFalseBranch") {
    val u3 = TyUInt(3)
    val x = Param("x")(U8)
    val e = Mux(x === C(8)(U8), C(0)(u3), TruncateTo(x, 3)())().tchk().lower
    val expected = TruncateTo(Mux(x equ C(8)(U8), C(0)(U8), x)(), 3)().tchk()
    val actual = PE.partialEval(e)
    assert(actual == expected)
  }

  /** The partial evaluator may check whether one branch is a special case of
    * the other. But in some cases, this can lead to division by zero.
    */
  test("PossibleDivByZeroInFalseBranch") {
    val x = Param("x")(U8)

    val e0 = Mux(x === 0, C(1)(U8), 2 / x)()
    val expected0 = Mux(Equal(x, C(0)(U8))(), C(1)(U8), Div(C(2)(U8), x)())()
    assert(lpe(e0) == expected0)

    val e1 = Mux(x === 1, C(1)(I9), 3 / (1 - x))()
    val expected1 =
      Mux(
        Equal(x, C(1)(U8))(),
        C(1)(I9),
        Div(C(3)(I9), Sum(C(1)(I9), Prod(C(-1)(), ToSigned(x)())())())()
      )()
    assert(lpe(e1) == expected1)

    val e2 = Mux(x > 0, 10 / x, C(0)(U8))()
    val expected2 =
      Mux(LessThan(C(0)(U8), x)(), Div(C(10)(U8), x)(), C(0)(U8))()
    assert(lpe(e2) == expected2)
  }

  test("PossibleDivByZeroInTrueBranch") {
    val x = Param("x")(U8)

    val e0 = Mux(x !== 0, 2 / x, C(1)(U8))()
    val expected0 =
      Mux(Not(Equal(x, C(0)(U8))())(), Div(C(2)(), x)(), C(1)(U8))()
    assert(lpe(e0) == expected0)

    val e1 = Mux(x !== 1, 3 / (1 - x), C(1)(I9))()
    val expected1 =
      Mux(
        Not(Equal(x, C(1)(U8))())(),
        Div(C(3)(I9), Sum(C(1)(I9), Prod(C(-1)(I9), ToSigned(x)())())())(),
        C(1)(I9)
      )()
    assert(lpe(e1) == expected1)

    val e2 = Mux(x <= 0, C(0)(U8), 10 / x)()
    val expected2 =
      Mux(Not(LessThan(C(0)(U8), x)())(), C(0)(U8), Div(C(10)(U8), x)())()
    assert(lpe(e2) == expected2)
  }

  test("MinLessThanMin") {
    val t = Param("t")(U8)
    val e = Min(-5 + t, C(5)(I9))() < Min(-4 + t, C(5)(I9))()
    val actual = lpe(e)
    val expected = t leq C(9)()
    assert(actual == expected)
  }

  test("MinMinusMinGreaterOrEqualToZero") {
    val t = Param("t")(U8)
    val e = (Min(-4 + t, C(5)(I9))() - Min(-5 + t, C(5)(I9))()) >= 0
    val actual = lpe(e)
    assert(actual == True)
  }

  test("MinMinusMinLessOrEqualToOne") {
    val t = Param("t")(U8)
    val e = (Min(-4 + t, C(5)(I9))() - Min(-5 + t, C(5)(I9))()) <= 1
    val actual = lpe(e)
    assert(actual == True)
  }

  test("Mux(a < b, True, False) === False") {
    val a = Param("a")(U8)
    val b = Param("b")(U8)
    val e = Mux(a < b, True, False)() === False
    val actual = lpe(e)
    val expected = !(a lt b)
    assert(actual == expected)
  }

  test("1 < 2") {
    assert(lpe(C(1)() < C(2)()) == True)
    assert(lpe(C(1)() < C(1)()) == False)
  }

  test("n == n") {
    val n = Param("n")(U8)
    assert(PE.partialEval(n equ n) == True)
    assert(PE.isEqual(n, n)().contains(true))
  }

  test("ParamMinusOneLessThan") {
    val n = Param("n")(U8)
    val b = Param("b")(TyBool)
    val facts = FactSet().geq(n, 1)
    val e = (((-1 + n) >= 0) && b).tchk().lower
    val actual = PartialEvalPass.partialEval(e)(facts)
    assert(actual == b)
  }

  test("ParamMinusOneEqual") {
    val n = Param("n")(U8)
    val b = Param("b")(TyBool)
    val facts = FactSet().geq(n, 2)
    val e = (((-1 + n) === 0) || b).tchk().lower
    val actual = PartialEvalPass.partialEval(e)(facts)
    assert(actual == b)
  }

  test("BlackBox.identify") {
    val m = Param("m")()
    val id0 = BlackBox(m.rebuild(U8)).id
    System.gc()
    val id1 = BlackBox(m.rebuild(U8)).id
    assert(id0 == id1)
  }

  test("i == 1 && i == 1") {
    val i = Param("i")(U8)
    val original = ((i equ C(1)(U8)) && (i equ C(1)(U8))).tchk().lower
    val expected = i equ C(1)(U8)
    assert(PE.partialEval(original) == expected)
  }

  test("i == 0 && i == 2") {
    val i = Param("i")(U8)
    val original = (i equ C(0)(U8)) && (i equ C(2)(U8))
    val expected = False
    assert(PE.partialEval(original) == expected)
  }

  test("i == 5 && i != 5") {
    val i = Param("i")(U32)
    val original = (i equ C(5)(U32)) && (i nequ C(5)(U32))
    val expected = False
    assert(PE.partialEval(original) == expected)
  }

  test("i == 42 && i != 1") {
    val i = Param("i")(U8)
    val original = (i equ C(42)(U8)) && (i nequ C(1)(U8))
    val expected = i equ C(42)(U8)
    assert(PE.partialEval(original) == expected)
  }
}
