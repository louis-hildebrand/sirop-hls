package ir

import org.scalatest.funsuite.AnyFunSuite

class TypecheckerTests extends AnyFunSuite {
  private def assertAllNodesHaveType(e: Expr): Unit = {
    assert(e.typ != Missing)
    e.children.foreach(e => assertAllNodesHaveType(e))
  }

  test("IdentityFunction") {
    val original: Expr = (x: Expr) => (TyInt, x)
    val checked = original.tchk(Map())
    assert(checked.typ == TyArrow(TyInt, TyInt))
    assertAllNodesHaveType(checked)
  }

  test("FunCall") {
    val original = FunCall((x: Expr) => (TyInt, x < 42), 42)()
    val checked = original.tchk(Map())
    assert(checked.typ == TyBool)
    assertAllNodesHaveType(checked)
  }

  test("IntAndBoolFunction") {
    val original: Expr =
      (x: Expr) =>
        (
          TyTuple(TyInt, TyBool),
          Tuple(
            Or(x.__1, Not(x.__1)(), And(x.__1, x.__0 === 2, x.__0 < 6)())(),
            x.__0 + 2 * x.__0 + x.__0 / 4 + x.__0 % 8
          )()
        )
    val checked = original.tchk(Map())
    assert(
      checked.typ == TyArrow(TyTuple(TyInt, TyBool), TyTuple(TyBool, TyInt))
    )
    assertAllNodesHaveType(checked)
  }

  test("Vector") {
    val n = Param("n")()
    val v = VecBuild(n, (i: Expr) => Tuple(i + 1, i % 2 === 0)())()
    val original = VecAccess(v, VecLength(v)() - 1)()
    val checked = original.tchk(Map(n -> TyInt))
    assert(checked.typ == TyTuple(TyInt, TyBool))
    assertAllNodesHaveType(checked)
  }

  test("SimpleStream") {
    val n = Param("n")()
    val b = Param("b")()
    val a = Param("a")()
    val original =
      StmBuild(
        n,
        IfThenElse(b, SSome(a)(), NNone(TyInt))(),
        Map[Param, (Expr, Expr)](
          a -> (0, IfThenElse(b, a + 2, a + 1)()),
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
    val s = Param("s")()
    val a = Param("a")()
    val original = StmBuild(
      n,
      SSome(a)(),
      Map[Param, (Expr, Expr)](
        s -> (input, IfThenElse(a % 2 === 0, StmNext(s)().__0, s)()),
        a -> (0, IfThenElse(a % 2 === 0, a + StmNext(s)().__1, a + 1)())
      )
    )()
    val checked =
      original.tchk(Map(n -> TyInt, input -> TyStm(TyInt, n)))
    assert(checked.typ == TyStm(TyInt, n.rebuild(TyInt)))
    assertAllNodesHaveType(checked)
  }

  test("FreeVar") {
    val x = Param("x")()
    assertThrows[TypeError](x.tchk(Map()))
  }

  test("Function:NonUniqueType") {
    val f: Function = (x: Expr) => x
    assertThrows[TypeError](f.tchk(Map()))
  }

  test("FunCall:NotFunction") {
    val e = FunCall(IntCst(42), IntCst(43))()
    assertThrows[TypeError](e.tchk(Map()))
  }

  test("FunCall:WrongArgType") {
    val f = Param("f")()
    val e = FunCall(f, True)()
    assertThrows[TypeError](e.tchk(Map(f -> TyArrow(TyInt, TyBool))))
  }

  test("Sum:WrongTerms") {
    assertThrows[TypeError]((True + 1).tchk(Map()))
    assertThrows[TypeError]((1 + True).tchk(Map()))
  }

  test("Prod:WrongTerms") {
    assertThrows[TypeError]((True * 2).tchk(Map()))
    assertThrows[TypeError]((2 * True).tchk(Map()))
  }

  test("Div:WrongTerms") {
    assertThrows[TypeError]((False / 4).tchk(Map()))
    assertThrows[TypeError]((4 / False).tchk(Map()))
  }

  test("Mod:WrongTerms") {
    assertThrows[TypeError]((False % 4).tchk(Map()))
    assertThrows[TypeError]((4 % False).tchk(Map()))
  }

  test("IfThenElse:NonBoolCondition") {
    val e = IfThenElse(IntCst(42), False, True)()
    assertThrows[TypeError](e.tchk(Map()))
  }

  test("IfThenElse:IncompatibleBranches") {
    val e = IfThenElse(True, Tuple(42, 43)(), Tuple(True, 99)())()
    assertThrows[TypeError](e.tchk(Map()))
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
    assertThrows[TypeError]((IntCst(42) === True).tchk(Map()))
    assertThrows[TypeError]((True === IntCst(42)).tchk(Map()))
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
    assertThrows[TypeError]((True < False).tchk(Map()))
  }

  test("TupleAccess:NonTuple") {
    assertThrows[TypeError](IntCst(42).__1.tchk(Map()))
  }

  test("VecBuild:NonIntLength") {
    val e = VecBuild(True, (i: Expr) => i)()
    assertThrows[TypeError](e.tchk(Map()))
  }

  test("VecBuild:WrongFunctionType") {
    val y = Param("y")()
    val f = (x: Expr) => (TyBool, x && y)
    val e = VecBuild(42, f)()
    assertThrows[TypeError](e.tchk(Map(y -> TyBool)))
  }

  test("VecAccess:NonVecTarget") {
    val e = VecAccess(42, 43)()
    assertThrows[TypeError](e.tchk(Map()))
  }

  test("VecAccess:NonIntIndex") {
    val v = Param("v")()
    val e = VecAccess(v, True)()
    assertThrows[TypeError](e.tchk(Map(v -> TyVec(TyInt, 2))))
  }

  test("VecLength:NonVecTarget") {
    val e = VecLength(42)()
    assertThrows[TypeError](e.tchk(Map()))
  }

  test("StmBuild:NonIntLength") {
    val e = StmBuild(True, SSome(5)())()
    assertThrows[TypeError](e.tchk(Map()))
  }

  test("StmBuild:NonOptionOutput") {
    val e = StmBuild(42, Tuple(43, 44)())()
    assertThrows[TypeError](e.tchk(Map()))
  }

  test("StmBuild:NextWrongType") {
    val a = Param("a")()
    val e = StmBuild(2, SSome(5)(), Map[Param, (Expr, Expr)](a -> (0, True)))()
    assertThrows[TypeError](e.tchk(Map()))
  }

  test("StmBuild:NextWrongShape") {
    val a = Param("a")()
    val e = StmBuild(
      3,
      SSome(4)(),
      Map[Param, (Expr, Expr)](
        a -> (VecBuild(10, (i: Expr) => i)(), VecBuild(11, (i: Expr) => i)())
      )
    )()
    assertThrows[TypeError](e.tchk(Map()))
  }

  test("StmNext:NonStmTarget") {
    val e = StmNext(42)()
    assertThrows[TypeError](e.tchk(Map()))
  }

  test("StmLength:NonStmTarget") {
    val e = StmLength(42)()
    assertThrows[TypeError](e.tchk(Map()))
  }
}
