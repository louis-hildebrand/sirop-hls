package ir

import org.scalatest.funsuite.AnyFunSuite

class TypecheckerTests extends AnyFunSuite {
  private def assertAllNodesHaveType(e: Expr): Unit = {
    assert(e.typ != Missing)
    e.children.foreach(e => assertAllNodesHaveType(e))
  }

  test("IdentityFunction") {
    val original: Expr = TyInt ::+ (x => x)
    val checked = original.tchk()
    assert(checked.typ == TyArrow(TyInt, TyInt))
    assertAllNodesHaveType(checked)
  }

  test("FunCall") {
    val original = FunCall(TyInt ::+ (x => x < 42), 42)()
    val checked = original.tchk()
    assert(checked.typ == TyBool)
    assertAllNodesHaveType(checked)
  }

  test("IntAndBoolFunction") {
    val original: Expr =
      TyTuple(TyInt, TyBool) ::+ (x =>
        Tuple(
          Or(x.__1, Not(x.__1)(), And(x.__1, x.__0 === 2, x.__0 < 6)())(),
          x.__0 + 2 * x.__0 + x.__0 / 4 + x.__0 % 8
        )()
      )
    val checked = original.tchk()
    assert(
      checked.typ == TyArrow(TyTuple(TyInt, TyBool), TyTuple(TyBool, TyInt))
    )
    assertAllNodesHaveType(checked)
  }

  test("Vector") {
    val n = Param("n")()
    val v = VecBuild(n, TyInt ::+ (i => Tuple(i + 1, i % 2 === 0)()))()
    val original = VecAccess(v, VecLength(v)() - 1)()
    val checked = original.tchk(Map(n -> TyInt))
    assert(checked.typ == TyTuple(TyInt, TyBool))
    assertAllNodesHaveType(checked)
  }

  test("SimpleStream") {
    val n = Param("n")()
    val b = Param("b")(TyBool)
    val a = Param("a")(TyInt)
    val original =
      StmBuild(
        n,
        a,
        b,
        Map[Param, (Expr, Expr)](
          a -> (0, Mux(b, a + 2, a + 1)()),
          b -> (False, Not(b)() || (a % 4 === 0))
        )
      )()
    val checked = original.tchk(Map(n -> TyInt))
    assert(checked.typ == TyStm(TyInt, n.rebuild(TyInt)))
    assertAllNodesHaveType(checked)
  }

  test("StreamWithStreamInput") {
    val n = Param("n")()
    val input = Param("input")()
    val s = Param("s")(TyStm(TyInt, n))
    val a = Param("a")(TyInt)
    val original = StmBuild(
      n,
      a,
      True,
      Map[Param, (Expr, Expr)](
        s -> (input, a % 2 === 0),
        a -> (0, Mux(a % 2 === 0, a + StmData(s)(), a + 1)())
      )
    )()
    val checked =
      original.tchk(Map(n -> TyInt, input -> TyStm(TyInt, n)))
    assert(checked.typ == TyStm(TyInt, n.rebuild(TyInt)))
    assertAllNodesHaveType(checked)
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
    val e = FunCall(IntCst(42), IntCst(43))()
    assertThrows[TypeError](e.tchk())
  }

  test("FunCall:WrongArgType") {
    val f = Param("f")()
    val e = FunCall(f, True)()
    assertThrows[TypeError](e.tchk(Map(f -> TyArrow(TyInt, TyBool))))
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
    val e = Mux(IntCst(42), False, True)()
    assertThrows[TypeError](e.tchk())
  }

  test("Mux:IncompatibleBranches") {
    val e = Mux(True, Tuple(42, 43)(), Tuple(True, 99)())()
    assertThrows[TypeError](e.tchk())
  }

  test("And:WrongTerms") {
    val b = Param("b")()
    assertThrows[TypeError](
      (b && IntCst(1)).tchk(Map(b -> TyBool))
    )
    assertThrows[TypeError](
      (IntCst(1) && b).tchk(Map(b -> TyBool))
    )
  }

  test("Or:WrongTerms") {
    val b = Param("b")()
    assertThrows[TypeError](
      (b || IntCst(1)).tchk(Map(b -> TyBool))
    )
    assertThrows[TypeError](
      (IntCst(1) || b).tchk(Map(b -> TyBool))
    )
  }

  test("Not:WrongInput") {
    val x = Param("x")()
    assertThrows[TypeError](Not(x)().tchk(Map(x -> TyInt)))
  }

  test("Equal:DifferentTypes") {
    assertThrows[TypeError]((IntCst(42) === True).tchk())
    assertThrows[TypeError]((True === IntCst(42)).tchk())
  }

  test("Equal:Streams") {
    val s = Param("s")()
    assertThrows[TypeError](
      (s === s).tchk(Map(s -> TyStm(TyInt, 1)))
    )
  }

  test("Equal:Functions") {
    val f = Param("f")()
    assertThrows[TypeError](
      (f === f).tchk(Map(f -> TyArrow(TyInt, TyInt)))
    )
  }

  test("LessThan:Bool") {
    assertThrows[TypeError]((True < False).tchk())
  }

  test("TupleAccess:NonTuple") {
    assertThrows[TypeError](IntCst(42).__1.tchk())
  }

  test("VecBuild:NonIntLength") {
    val e = VecBuild(True, TyInt ::+ (i => i))()
    assertThrows[TypeError](e.tchk())
  }

  test("VecBuild:WrongFunctionType") {
    val y = Param("y")()
    val f = TyBool ::+ (x => x && y)
    val e = VecBuild(42, f)()
    assertThrows[TypeError](e.tchk(Map(y -> TyBool)))
  }

  test("VecAccess:NonVecTarget") {
    val e = VecAccess(42, 43)()
    assertThrows[TypeError](e.tchk())
  }

  test("VecAccess:NonIntIndex") {
    val v = Param("v")()
    val e = VecAccess(v, True)()
    assertThrows[TypeError](e.tchk(Map(v -> TyVec(TyInt, 2))))
  }

  test("VecLength:NonVecTarget") {
    val e = VecLength(42)()
    assertThrows[TypeError](e.tchk())
  }

  test("StmBuild:NonIntLength") {
    val e = StmBuild(True, 5, True)()
    assertThrows[TypeError](e.tchk())
  }

  test("StmBuild:NonBoolValid") {
    val e = StmBuild(42, Tuple(43, 44)(), 45)()
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
        a -> (VecBuild(10, TyInt ::+ (i => i))(),
        VecBuild(11, TyInt ::+ (i => i))())
      )
    )()
    assertThrows[TypeError](e.tchk())
  }

  test("StmData:NonStmTarget") {
    val x = Param("x")(TyInt)
    assertThrows[TypeError](StmData(x)().tchk())
  }

  test("StmLength:NonStmTarget") {
    val e = StmLength(42)()
    assertThrows[TypeError](e.tchk())
  }
}
