package mhir.typecheck

import mhir.canonicalize._
import mhir.ir._
import mhir.parse.sirop.Parser
import mhir.sugar._
import mhir.testing.ParamStore
import org.scalatest.funsuite.AnyFunSuite

class TypecheckerTests extends AnyFunSuite {
  private val X = new ParamStore("X")
  private val Y = new ParamStore("Y")
  private val Z = new ParamStore("Y")

  private def assertAllNodesHaveType(e: Expr): Unit = {
    assert(e.typ != Missing)
    e.children.foreach(e => assertAllNodesHaveType(e))
  }

  test("IdentityFunction") {
    val original: Expr = U8 ::+ (x => x)
    val checked = original.tchk()
    assert(checked.typ == U8 ->: U8)
    assertAllNodesHaveType(checked)
  }

  test("FunCall") {
    val original = FunCall(U8 ::+ (x => x < IntCst(42)(U8)), IntCst(42)(U8))()
    val checked = original.tchk()
    assert(checked.typ == TyBool)
    assertAllNodesHaveType(checked)
  }

  test("IntCst") {
    assert(IntCst(-4)().tchk().typ == TySInt(3))
    assert(IntCst(-3)().tchk().typ == TySInt(3))
    assert(IntCst(-2)().tchk().typ == TySInt(2))
    assert(IntCst(-1)().tchk().typ == TySInt(1))
    assert(IntCst(0)().tchk().typ == TyUInt(0))
    assert(IntCst(1)().tchk().typ == TyUInt(1))
    assert(IntCst(2)().tchk().typ == TyUInt(2))
    assert(IntCst(3)().tchk().typ == TyUInt(2))
    assert(IntCst(4)().tchk().typ == TyUInt(3))
  }

  test("Equal:Valid") {
    val types = Seq(
      U8,
      U16,
      U32,
      I8,
      I16,
      I32,
      TyBool,
      TyTuple(U8, TyBool),
      TyVec(U8, IntCst(42)(U8))
    )
    for (t <- types) {
      val x = Param("x")(t)
      val y = Param("y")(t)
      assert(Equal(x, y)().tchk().typ == TyBool)
    }
  }

  test("Equal:Invalid") {
    // Non-data type
    assertThrows[TypeError](
      Equal(Param("x")(TyStm(U8, 1)), Param("y")(TyStm(U8, 1)))().tchk()
    )
    assertThrows[TypeError](
      Equal(Param("x")(U8 ->: U8), Param("y")(U8 ->: U8))().tchk()
    )
    // Completely different types
    assertThrows[TypeError](Equal(Param("x")(TyBool), Param("y")(I8))().tchk())
    // Different int types
    assertThrows[TypeError](Equal(Param("x")(U8), Param("y")(I8))().tchk())
  }

  test("LessThan:Valid") {
    for (t <- Seq(U8, U16, U32, I8, I16, I32)) {
      val x = Param("x")(t)
      val y = Param("y")(t)
      assert(LessThan(x, y)().tchk().typ == TyBool)
    }
  }

  test("LessThan:Invalid") {
    assertThrows[TypeError](
      LessThan(Param("x")(TyBool), Param("y")(U8))().tchk()
    )
    assertThrows[TypeError](
      LessThan(Param("x")(U8), Param("y")(TyBool))().tchk()
    )
    assertThrows[TypeError](LessThan(Param("x")(U8), Param("x")(I8))().tchk())
  }

  private val xU8 = Param("x")(U8)
  private val yU8 = Param("y")(U8)
  private val zU8 = Param("z")(U8)
  private val yU16 = Param("y")(U16)
  private val yI8 = Param("y")(I8)
  private val xI32 = Param("x")(I32)
  private val yI32 = Param("y")(I32)

  test("u8 + u8 + u8") {
    assert(Sum(xU8, yU8, zU8)().tchk().typ == U8)
  }

  test("i32 + i32") {
    assert(Sum(xI32, yI32)().tchk().typ == I32)
  }

  test("EmptySum") {
    assert(MaybeSum()().tchk().typ == TyUInt(0))
  }

  test("u8 + i8") {
    assertThrows[TypeError](Sum(xU8, yI8)().tchk())
  }

  test("u8 + u16") {
    assertThrows[TypeError](Sum(xU8, yU16)().tchk())
  }

  test("WrappingSum(u8, u8, u8)") {
    assert(WrappingSum(X(U8), Y(U8), Z(U8))().tchk().typ == U8)
  }

  test("WrappingSum(i16, i16") {
    assert(WrappingSum(X(I16), Y(I16))().tchk().typ == I16)
  }

  test("WrappingSum(u8, i8)") {
    assertThrows[TypeError](WrappingSum(X(U8), Y(I8))().tchk())
  }

  test("WrappingSum(i16, i32)") {
    assertThrows[TypeError](WrappingSum(X(I16), Y(I32))().tchk())
  }

  test("WrappingDiff(u8, u8)") {
    assert(WrappingDiff(X(U8), Y(U8))().tchk().typ == U8)
  }

  test("WrappingDiff(i16, i16") {
    assert(WrappingDiff(X(I16), Y(I16))().tchk().typ == I16)
  }

  test("WrappingDiff(u8, i8)") {
    assertThrows[TypeError](WrappingDiff(X(U8), Y(I8))().tchk())
  }

  test("WrappingDiff(i16, i32)") {
    assertThrows[TypeError](WrappingDiff(X(I16), Y(I32))().tchk())
  }

  test("u8 * u8 * u8") {
    assert(Prod(xU8, yU8, zU8)().tchk().typ == U8)
  }

  test("i32 * i32") {
    assert(Prod(xI32, yI32)().tchk().typ == I32)
  }

  test("EmptyProd") {
    assert(MaybeProd()().tchk().typ == TyUInt(1))
  }

  test("u8 * i8") {
    assertThrows[TypeError](Prod(xU8, yI8)().tchk())
  }

  test("u8 * u16") {
    assertThrows[TypeError](Prod(xU8, yU16)().tchk())
  }

  test("WrappingProd(u8, u8, u8)") {
    assert(WrappingProd(X(U8), Y(U8), Z(U8))().tchk().typ == U8)
  }

  test("WrappingProd(i16, i16") {
    assert(WrappingProd(X(I16), Y(I16))().tchk().typ == I16)
  }

  test("WrappingProd(u8, i8)") {
    assertThrows[TypeError](WrappingProd(X(U8), Y(I8))().tchk())
  }

  test("WrappingProd(i16, i32)") {
    assertThrows[TypeError](WrappingProd(X(I16), Y(I32))().tchk())
  }

  test("u8 / u8") {
    assert(Div(xU8, yU8)().tchk().typ == U8)
  }

  test("i32 / i32") {
    assert(Div(xI32, yI32)().tchk().typ == I32)
  }

  test("u8 / i8") {
    assertThrows[TypeError](Div(xU8, yI8)().tchk())
  }

  test("u8 / u16") {
    assertThrows[TypeError](Div(xU8, yU16)().tchk())
  }

  test("u8 % u8") {
    assert(Mod(xU8, yU8)().tchk().typ == U8)
  }

  test("i32 % i32") {
    assert(Mod(xI32, yI32)().tchk().typ == I32)
  }

  test("u8 % i8") {
    assertThrows[TypeError](Mod(xU8, yI8)().tchk())
  }

  test("u8 % u16") {
    assertThrows[TypeError](Mod(xU8, yU16)().tchk())
  }

  test("TruncateTo:u16 to u8") {
    assert(TruncateTo(yU16, 8)().tchk().typ == U8)
  }

  test("TruncateTo:i8 to u0") {
    assert(TruncateTo(yI8, 0)().tchk().typ == U0)
  }

  test("FixCst") {
    assert(FixCst(8)(TyFix(U8, 7)).tchk().typ == TyFix(U8, 7))
    assert(FixCst(1)(TyFix(U8, 7)).tchk().typ == TyFix(U8, 7))
    assert(FixCst(1)(TyFix(U16, 10)).tchk().typ == TyFix(U16, 10))
  }

  test("IntFixProd") {
    assert(IntFixProd(X(U8), Y(TyFix(U8, 7)))().tchk().typ == U8)
    assert(IntFixProd(X(U16), Y(TyFix(U8, 7)))().tchk().typ == U16)
    assert(IntFixProd(X(U32), Y(TyFix(U8, 7)))().tchk().typ == U32)
  }

  test("bool *_ fix") {
    assertThrows[TypeError](IntFixProd(X(TyBool), Y(TyFix(U8, 7)))().tchk())
  }

  test("u8 *_ u8") {
    assertThrows[TypeError](IntFixProd(X(U8), Y(U8))().tchk())
  }

  test("fix *_ u8") {
    assertThrows[TypeError](IntFixProd(X(TyFix(U8, 7)), Y(U8))().tchk())
  }

  test("uint << uint") {
    assert((X(U8) << Y(U8)).tchk().typ == U8)
    assert((X(U8) << Y(U16)).tchk().typ == U8)
    assert((X(U8) << Y(U32)).tchk().typ == U8)
    assert((X(U16) << Y(U8)).tchk().typ == U16)
    assert((X(U16) << Y(U16)).tchk().typ == U16)
    assert((X(U16) << Y(U32)).tchk().typ == U16)
    assert((X(U32) << Y(U8)).tchk().typ == U32)
    assert((X(U32) << Y(U16)).tchk().typ == U32)
    assert((X(U32) << Y(U32)).tchk().typ == U32)
  }

  test("sint << uint") {
    assert((X(I8) << Y(U8)).tchk().typ == I8)
    assert((X(I8) << Y(U16)).tchk().typ == I8)
    assert((X(I8) << Y(U32)).tchk().typ == I8)
    assert((X(I16) << Y(U8)).tchk().typ == I16)
    assert((X(I16) << Y(U16)).tchk().typ == I16)
    assert((X(I16) << Y(U32)).tchk().typ == I16)
    assert((X(I32) << Y(U8)).tchk().typ == I32)
    assert((X(I32) << Y(U16)).tchk().typ == I32)
    assert((X(I32) << Y(U32)).tchk().typ == I32)
  }

  test("bool << u8") {
    assertThrows[TypeError]((X(TyBool) << Y(U8)).tchk())
  }

  test("u8 << bool") {
    assertThrows[TypeError]((X(U8) << Y(TyBool)).tchk())
  }

  test("u8 << i8") {
    assertThrows[TypeError]((X(U8) << Y(I8)).tchk())
  }

  test("uint >> uint") {
    assert((X(U8) >> Y(U8)).tchk().typ == U8)
    assert((X(U8) >> Y(U16)).tchk().typ == U8)
    assert((X(U8) >> Y(U32)).tchk().typ == U8)
    assert((X(U16) >> Y(U8)).tchk().typ == U16)
    assert((X(U16) >> Y(U16)).tchk().typ == U16)
    assert((X(U16) >> Y(U32)).tchk().typ == U16)
    assert((X(U32) >> Y(U8)).tchk().typ == U32)
    assert((X(U32) >> Y(U16)).tchk().typ == U32)
    assert((X(U32) >> Y(U32)).tchk().typ == U32)
  }

  test("sint >> uint") {
    assert((X(I8) >> Y(U8)).tchk().typ == I8)
    assert((X(I8) >> Y(U16)).tchk().typ == I8)
    assert((X(I8) >> Y(U32)).tchk().typ == I8)
    assert((X(I16) >> Y(U8)).tchk().typ == I16)
    assert((X(I16) >> Y(U16)).tchk().typ == I16)
    assert((X(I16) >> Y(U32)).tchk().typ == I16)
    assert((X(I32) >> Y(U8)).tchk().typ == I32)
    assert((X(I32) >> Y(U16)).tchk().typ == I32)
    assert((X(I32) >> Y(U32)).tchk().typ == I32)
  }

  test("bool >> u8") {
    assertThrows[TypeError]((X(TyBool) >> Y(U8)).tchk())
  }

  test("u8 >> bool") {
    assertThrows[TypeError]((X(U8) >> Y(TyBool)).tchk())
  }

  test("u8 >> i8") {
    assertThrows[TypeError]((X(U8) >> Y(I8)).tchk())
  }

  test("uint >>> uint") {
    assert((X(U8) >>> Y(U8)).tchk().typ == U8)
    assert((X(U8) >>> Y(U16)).tchk().typ == U8)
    assert((X(U8) >>> Y(U32)).tchk().typ == U8)
    assert((X(U16) >>> Y(U8)).tchk().typ == U16)
    assert((X(U16) >>> Y(U16)).tchk().typ == U16)
    assert((X(U16) >>> Y(U32)).tchk().typ == U16)
    assert((X(U32) >>> Y(U8)).tchk().typ == U32)
    assert((X(U32) >>> Y(U16)).tchk().typ == U32)
    assert((X(U32) >>> Y(U32)).tchk().typ == U32)
  }

  test("sint >>> uint") {
    assert((X(I8) >>> Y(U8)).tchk().typ == I8)
    assert((X(I8) >>> Y(U16)).tchk().typ == I8)
    assert((X(I8) >>> Y(U32)).tchk().typ == I8)
    assert((X(I16) >>> Y(U8)).tchk().typ == I16)
    assert((X(I16) >>> Y(U16)).tchk().typ == I16)
    assert((X(I16) >>> Y(U32)).tchk().typ == I16)
    assert((X(I32) >>> Y(U8)).tchk().typ == I32)
    assert((X(I32) >>> Y(U16)).tchk().typ == I32)
    assert((X(I32) >>> Y(U32)).tchk().typ == I32)
  }

  test("bool >>> u8") {
    assertThrows[TypeError]((X(TyBool) >>> Y(U8)).tchk())
  }

  test("u8 >>> bool") {
    assertThrows[TypeError]((X(U8) >>> Y(TyBool)).tchk())
  }

  test("u8 >>> i8") {
    assertThrows[TypeError]((X(U8) >>> Y(I8)).tchk())
  }

  test("IntAndBoolFunction") {
    val original: Expr =
      TyTuple(TyUInt(8), TyBool) ::+ (x =>
        Tuple(
          x.__1 || !x.__1 || (x.__1 && x.__0 === 2 && x.__0 < 6),
          x.__0 + 2 * x.__0 + x.__0 / 4 + x.__0 % 8
        )()
      )
    val checked = original.tchk()
    val expectedType = TyArrow(TyTuple(U8, TyBool), TyTuple(TyBool, U8))
    assert(checked.typ == expectedType)
    assertAllNodesHaveType(checked)
  }

  test("Vector") {
    val n = Param("n")()
    val v = VecBuild(n, U8 ::+ (i => Tuple(i + 1, i % 2 === 0)()))()
    val original = VecAccess(v, ToUnsigned(n - 1)())()
    val checked = original.tchk(Map(n -> U8), Map())
    assert(checked.typ == TyTuple(U8, TyBool))
    assertAllNodesHaveType(checked)
  }

  test("VecBuild:IndexSizeTooSmall:U8") {
    assert(VecBuild(255, U8 ::+ (i => i))().tchk().typ == TyVec(U8, 255))
    assert(VecBuild(256, U8 ::+ (i => i))().tchk().typ == TyVec(U8, 256))
    assertThrows[TypeError](VecBuild(257, U8 ::+ (i => i))().tchk())
    assertThrows[TypeError](VecBuild(258, U8 ::+ (i => i))().tchk())
  }

  test("VecBuild:IndexSizeTooSmall:U16") {
    assert(VecBuild(65535, U16 ::+ (i => i))().tchk().typ == TyVec(U16, 65535))
    assert(VecBuild(65536, U16 ::+ (i => i))().tchk().typ == TyVec(U16, 65536))
    assertThrows[TypeError](VecBuild(65537, U16 ::+ (i => i))().tchk())
    assertThrows[TypeError](VecBuild(65538, U16 ::+ (i => i))().tchk())
  }

  test("Bits:U5") {
    val x = Param("x")(TyUInt(5))
    val checked = Bits(x)().tchk()
    assert(checked.typ == TyVec(TyBool, 5))
  }

  test("Bits:(Vec[u8, 4], Vec[i8, 4], bool)") {
    val x = Param("x")((TyVec(U8, 4), TyVec(I8, 4), TyBool))
    val checked = Bits(x)().tchk()
    assert(checked.typ == TyVec(TyBool, 8 * 4 + 8 * 4 + 1))
  }

  test("Bits:u8 -> u8") {
    val f = Param("f")(U8 ->: U8)
    assertThrows[TypeError](Bits(f)().tchk())
  }

  test("Bits:Stm[u8, 8]") {
    val s = Param("s")(TyStm(U8, 8))
    assertThrows[TypeError](Bits(s)().tchk())
  }

  test("SimpleStream") {
    val n = Param("n")()
    val b = Param("b")(TyBool)
    val a = Param("a")(U8)
    val original =
      StmBuild(
        n,
        a,
        b,
        Map[Param, (Expr, Expr)](
          a -> (C(0)(), Mux(b, a + 2, a + 1)()),
          b -> (False, Not(b)() || (a % 4 === 0))
        )
      )()
    val checked = original.tchk(Map(n -> U16), Map())
    assert(checked.typ == TyStm(U8, n.rebuild(U16)))
    assertAllNodesHaveType(checked)
  }

  test("StreamWithStreamInput") {
    val n = Param("n")(U8)
    val input = Param("input")()
    val s = Param("s")(TyStm(I16, n))
    val a = Param("a")(I16)
    val original = StmBuild(
      n,
      a,
      True,
      Map[Param, (Expr, Expr)](
        s -> (input, a % 2 === 0),
        a -> (
          ReshapeData(0, I16)(),
          Mux(a % 2 === 0, a + StmData(s)(), a + 1)()
        )
      )
    )()
    val checked = original.tchk(Map(input -> TyStm(I16, n)), Map())
    assert(checked.typ == TyStm(I16, n.rebuild(U8)))
    assertAllNodesHaveType(checked)
  }

  test("let stm x = s in StmZip(x, x)") {
    val n = 10
    val s = Param("s")(TyStm(U8, n))
    val let = {
      val x = Param("x")()
      val s1 = Param("s1")(TyStm(U8, -1))
      val s2 = Param("s2")(TyStm(U8, -1))
      val zipped = StmBuild(
        n,
        Tuple(StmData(s1)(), StmData(s2)())(),
        True,
        Map[Param, (Expr, Expr)](
          s1 -> (x, True),
          s2 -> (x, True)
        )
      )()
      LetStm(1, x, s, zipped)()
    }
    assert(let.tchk().typ == TyStm((U8, U8), 10))
  }

  test("LetWrongType") {
    val x = Param("x", -1)(U8)
    val e = Let(x, True, x +% C(5)(U8))()
    assertThrows[TypeError](e.tchk())
  }

  test("FreeVar") {
    val x = Param("x")()
    assertThrows[TypeError](x.tchk())
  }

  test("Function:NonUniqueType") {
    val f: Function = Missing ::+ (x => x)
    assertThrows[TypeError](f.tchk())
  }

  test("FunCall:NotFunction") {
    val e = FunCall(IntCst(42)(), IntCst(43)())()
    assertThrows[TypeError](e.tchk())
  }

  test("FunCall:WrongArgType") {
    val f = Param("f")()
    val e = FunCall(f, True)()
    assertThrows[TypeError](e.tchk(Map(f -> TyArrow(U8, TyBool)), Map()))
  }

  test("Sum:WrongTerms") {
    assertThrows[TypeError]((True + 1).tchk())
    assertThrows[TypeError]((1 + True).tchk())
  }

  test("Prod:WrongTerms") {
    assertThrows[TypeError]((True * 2).tchk())
    assertThrows[TypeError]((2 * True).tchk())
  }

  test("Div:WrongTerms") {
    assertThrows[TypeError]((False / 4).tchk())
    assertThrows[TypeError]((4 / False).tchk())
  }

  test("Mod:WrongTerms") {
    assertThrows[TypeError]((False % 4).tchk())
    assertThrows[TypeError]((4 % False).tchk())
  }

  test("Mux:NonBoolCondition") {
    val e = Mux(IntCst(42)(), False, True)()
    assertThrows[TypeError](e.tchk())
  }

  test("Mux:IncompatibleBranches") {
    val e = Mux(True, Tuple(42, 43)(), Tuple(True, 99)())()
    assertThrows[TypeError](e.tchk())
  }

  test("And:WrongTerms") {
    val b = Param("b")()
    assertThrows[TypeError](
      (b && IntCst(1)()).tchk(Map(b -> TyBool), Map())
    )
    assertThrows[TypeError](
      (IntCst(1)() && b).tchk(Map(b -> TyBool), Map())
    )
  }

  test("Or:WrongTerms") {
    val b = Param("b")()
    assertThrows[TypeError](
      (b || IntCst(1)()).tchk(Map(b -> TyBool), Map())
    )
    assertThrows[TypeError](
      (IntCst(1)() || b).tchk(Map(b -> TyBool), Map())
    )
  }

  test("Not:WrongInput") {
    val x = Param("x")()
    assertThrows[TypeError](Not(x)().tchk(Map(x -> U8), Map()))
  }

  test("Equal:DifferentTypes") {
    assertThrows[TypeError]((IntCst(42)() equ True).tchk())
    assertThrows[TypeError]((True equ IntCst(42)()).tchk())
  }

  test("Equal:Streams") {
    val s = Param("s")()
    assertThrows[TypeError](
      (s === s).tchk(Map(s -> TyStm(U8, 1)), Map())
    )
  }

  test("Equal:Functions") {
    val f = Param("f")()
    assertThrows[TypeError](
      (f === f).tchk(Map(f -> TyArrow(U16, I16)), Map())
    )
  }

  test("LessThan:Bool") {
    assertThrows[TypeError]((True lt False).tchk())
  }

  test("TupleAccess:NonTuple") {
    assertThrows[TypeError](IntCst(42)().__1.tchk())
  }

  test("VecBuild:NonIntLength") {
    val e = VecBuild(True, U8 ::+ (i => i))()
    assertThrows[TypeError](e.tchk())
  }

  test("VecBuild:WrongFunctionType") {
    val y = Param("y")()
    val f = TyBool ::+ (x => x && y)
    val e = VecBuild(42, f)()
    assertThrows[TypeError](e.tchk(Map(y -> TyBool), Map()))
  }

  test("VecAccess:NonVecTarget") {
    val e = VecAccess(42, 43)()
    assertThrows[TypeError](e.tchk())
  }

  test("VecAccess:NonIntIndex") {
    val v = Param("v")()
    val e = VecAccess(v, True)()
    assertThrows[TypeError](e.tchk(Map(v -> TyVec(U8, 2)), Map()))
  }

  test("StmBuild:NonIntLength") {
    val e = StmBuild(True, 5, True)()
    assertThrows[TypeError](e.tchk())
  }

  test("StmBuild:NonBoolValid") {
    val e = StmBuild(42, Tuple(43, 44)(), 45)()
    assertThrows[TypeError](e.tchk())
  }

  test("StmBuild:InitWrongType") {
    val a = Param("a")(U8)
    val e =
      StmBuild(4, a, True, Map[Param, (Expr, Expr)](a -> (True, C(0)(U8))))()
    assertThrows[TypeError](e.tchk())
  }

  test("StmBuild:NextWrongType") {
    val a = Param("a")()
    val e = StmBuild(2, 5, True, Map[Param, (Expr, Expr)](a -> (0, True)))()
    assertThrows[TypeError](e.tchk())
  }

  test("StmBuild:NextWrongShape") {
    val a = Param("a")()
    val e = StmBuild(
      3,
      4,
      True,
      Map[Param, (Expr, Expr)](
        a -> (VecBuild(10, U8 ::+ (i => i))(),
        VecBuild(11, U8 ::+ (i => i))())
      )
    )()
    assertThrows[TypeError](e.tchk())
  }

  test("StmData:NonStmTarget") {
    val x = Param("x")(U8)
    assertThrows[TypeError](StmData(x)().tchk())
  }

  test("TestSuite:OK") {
    val src =
      """const N: u32 = 10
        |
        |accelerator top = (s: Stm[u8, N]) => s.StmMap((x) => x + 5:u8)
        |
        |assert { s = [0:u8, 1:u8, 2:u8, 3:u8, 4:u8, 5:u8, 6:u8, 7:u8, 8:u8, 9:u8]s }
        |yields [5:u8, 6:u8, 7:u8, 8:u8, 9:u8, 10:u8, 11:u8, 12:u8, 13:u8, 14:u8]s
        |
        |const M: u32 = 5
        |const V: Vec[u8, M] = vbuild(M) { (i: u8) => i }
        |const Z: u8 = V.VecReduce( (x) => x.0 + x.1 )[0]
        |assert { s = StmRange(N, Z, 1:u8) } yields StmRange(N, Z + 5:u8, 1:u8)
        |
        |const V2: Vec[u8, N] = vbuild(N) { (i: u8) => i }
        |const Z2: u8 = V2.VecReduce( (x) => x.0 + x.1 )[0]
        |const DELTA2: u8 = 2
        |assert {
        |  s = StmRange(N, Z2, DELTA2)
        |}
        |yields StmRange(N, Z2 + 5:u8, DELTA2)
        |""".stripMargin
    val prog = Parser.parse(src)
    val checked = prog.tchk()
    assert(checked.accel.body.hasType)
    for (td <- checked.test) {
      td match {
        case ConstDecl(_, e) =>
          assert(e.hasType)
        case Assertion(inputs, expectedOutput) =>
          for ((_, e) <- inputs) {
            assert(e.hasType)
          }
          assert(expectedOutput.hasType)
      }
    }
  }

  test("TestSuite:Error:MainAcceleratorUsingTestConst") {
    val src =
      """accelerator top = (s: Stm[u8, 3]) => s.StmMap( (x) => x + K )
        |
        |const K: u8 = 5
        |assert { s = StmRange(3, 0:u8, 1:u8) } yields [K, K + 1:u8, K + 2:u8]s
        |""".stripMargin
    val prog = Parser.parse(src)
    val ex = intercept[TypeError](prog.tchk())
    assert(ex.getMessage.toLowerCase.contains("free variable: k"))
  }

  test("TestSuite:Error:MissingAcceleratorArg") {
    val src =
      """accelerator top = (a: Stm[bool, 4]) => (b: Stm[i8, 4]) => StmZip(a, b)
        |
        |assert { a = [true, false, false, true]s }
        |yields [(true, 0:i8), (false, 0:i8), (false, 0:i8), (true, 0:i8)]s
        |""".stripMargin
    val prog = Parser.parse(src)
    val ex = intercept[TypeError](prog.tchk())
    assert(
      ex.getMessage
        .toLowerCase()
        .contains("invalid inputs in assertion: missing argument for 'b'")
    )
  }

  test("TestSuite:Error:AdditionalAcceleratorArg") {
    val src =
      """accelerator top = (a: Stm[bool, 4]) => a.StmMap( (x) => (x, 0:i8) )
        |
        |assert { a = [true, false, false, true]s, b = [0:i8, 0:i8, 0:i8]s }
        |yields [(true, 0:i8), (false, 0:i8), (false, 0:i8), (true, 0:i8)]s
        |""".stripMargin
    val prog = Parser.parse(src)
    val ex = intercept[TypeError](prog.tchk())
    assert(
      ex.getMessage
        .toLowerCase()
        .contains("invalid inputs in assertion: unknown parameter 'b'")
    )
  }

  test("TestSuite:Error:MisnamedAcceleratorArg") {
    val src =
      """accelerator top = (a: Stm[bool, 4]) => a.StmMap( (x) => (x, 0:i8) )
        |
        |assert { s = [true, false, false, true]s }
        |yields [(true, 0:i8), (false, 0:i8), (false, 0:i8), (true, 0:i8)]s
        |""".stripMargin
    val prog = Parser.parse(src)
    val ex = intercept[TypeError](prog.tchk())
    assert(
      ex.getMessage
        .toLowerCase()
        .contains(
          "invalid inputs in assertion: missing argument for 'a' and unknown parameter 's'"
        )
    )
  }

  test("TestSuite:Error:WrongOutputType") {
    // Although the bitwidth is the same here, using the wrong type could still
    // cause problems.
    // In the evaluator, this would obviously be an issue because 0 != [0]v.
    // Even for VHDL simulation, the testbench might not compile if it tries to
    // convert the output to a type that's not defined in the main VHDL code.
    val src =
      """accelerator top = StmRange(4, 0:u8, 1:u8)
        |
        |assert {} yields [[0:u8]v, [1:u8]v, [2:u8]v, [3:u8]v]s
        |""".stripMargin
    val prog = Parser.parse(src)
    val ex = intercept[TypeError](prog.tchk())
    assert(
      ex.getMessage
        .contains(
          "invalid expected output in assertion:" +
            " accelerator produces Stm[u8, 4:u3] but assertion expects Stm[Vec[u8, 1:u1], 4:u3]"
        )
    )
  }

  test("TestSuite:Error:WrongOutputStreamLength1") {
    // Although the bitwidth is the same here, using the wrong type could still
    // cause problems.
    // In the evaluator, this would obviously be an issue because 0 != [0]v.
    // Even for VHDL simulation, the testbench might not compile if it tries to
    // convert the output to a type that's not defined in the main VHDL code.
    val src =
      """accelerator top = StmRange(4, 0:u8, 1:u8)
        |
        |assert {} yields [0:u8, 1:u8, 2:u8, 3:u8, 4:u8]s
        |""".stripMargin
    val prog = Parser.parse(src)
    val ex = intercept[TypeError](prog.tchk())
    assert(
      ex.getMessage
        .contains(
          "invalid expected output in assertion:" +
            " accelerator produces Stm[u8, 4:u3] but assertion expects Stm[u8, 5:u3]"
        )
    )
  }

  test("TestSuite:Error:WrongOutputStreamLength2") {
    // Although the bitwidth is the same here, using the wrong type could still
    // cause problems.
    // In the evaluator, this would obviously be an issue because 0 != [0]v.
    // Even for VHDL simulation, the testbench might not compile if it tries to
    // convert the output to a type that's not defined in the main VHDL code.
    val src =
      """const N: u32 = 4
        |const Z: u8 = 0
        |const DELTA: u8 = 1
        |
        |accelerator top = StmRange(N, Z, DELTA)
        |
        |assert {} yields [0:u8, 1:u8, 2:u8, 3:u8, 4:u8]s
        |""".stripMargin
    val prog = Parser.parse(src)
    val ex = intercept[TypeError](prog.tchk())
    assert(
      ex.getMessage
        .contains(
          "invalid expected output in assertion:" +
            " accelerator produces Stm[u8, N] but assertion expects Stm[u8, 5:u3] (note that N = 4:u32)"
        )
    )
  }

  test("TestSuite:Error:WrongInputType") {
    val src =
      """accelerator top = (s: Stm[Vec[u8, 1], 3]) => s.StmMap( (x) => x[0] )
        |
        |assert { s = [0:u8, 1:u8, 2:u8]s }
        |yields [0:u8, 1:u8, 2:u8]s
        |""".stripMargin
    val prog = Parser.parse(src)
    val ex = intercept[TypeError](prog.tchk())
    assert(
      ex.getMessage.contains(
        "invalid inputs in assertion: parameter 's' has type Stm[Vec[u8, 1:u1], 3:u2] but assertion provides Stm[u8, 3:u2]"
      )
    )
  }

  test("TestSuite:Error:WrongInputStreamLength1") {
    val src =
      """accelerator top = (s: Stm[u8, 3]) => s.StmMap( (x) => x + 5:u8 )
        |
        |assert { s = [0:u8, 1:u8, 2:u8, 3:u8]s }
        |yields [5:u8, 6:u8, 7:u8]s
        |""".stripMargin
    val prog = Parser.parse(src)
    val ex = intercept[TypeError](prog.tchk())
    assert(
      ex.getMessage.contains(
        "invalid inputs in assertion: parameter 's' has type Stm[u8, 3:u2] but assertion provides Stm[u8, 4:u3]"
      )
    )
  }

  test("TestSuite:Error:WrongInputStreamLength2") {
    val src = {
      """const N: u32 = 3
        |const K: u8 = 5
        |
        |accelerator top = (s: Stm[u8, N]) => s.StmMap( (x) => x + K )
        |
        |assert { s = [0:u8, 1:u8, 2:u8, 3:u8]s }
        |yields [5:u8, 6:u8, 7:u8]s
        |""".stripMargin
    }
    val prog = Parser.parse(src)
    val ex = intercept[TypeError](prog.tchk())
    assert(
      ex.getMessage.contains(
        "invalid inputs in assertion: parameter 's' has type Stm[u8, N] but assertion provides Stm[u8, 4:u3] (note that N = 3:u32)"
      )
    )
  }
}
