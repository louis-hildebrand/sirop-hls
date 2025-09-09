package mhir.optimize

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.{NameSimplifier => NS}
import mhir.sugar.StmZip
import org.scalatest.funsuite.AnyFunSuite

class NameSimplifierTests extends AnyFunSuite {

  /** Free variables should be left as-is.
    */
  test("FreeVariable") {
    val original = Param("x")(U8)
    val simplified = NS.simplify(original)
    assert(simplified == original)
  }

  /** Parameters with unique prefixes can obviously be renamed.
    */
  test("Function:TwoDifferentParams") {
    val original = {
      val x = Param("x")(U16)
      val y = Param("y")(U16)
      Function(x, Function(y, Sum(x, y)())())()
    }
    val simplified = NS.simplify(original)
    assert(simplified == original)
    assert(simplified.toString() == "(x : u16) => (y : u16) => x + y")
  }

  /** If one variable shadows another, you can rename even if they have the same
    * prefix.
    */
  test("Function:Shadowing") {
    val original = {
      val x = Param("x", -1)(I8)
      Function(x, Function(x, Sum(C(1)(I8), x)())())()
    }
    val simplified = NS.simplify(original)
    assert(simplified == original)
    assert(simplified.toString() == "(x : i8) => (x : i8) => 1:i8 + x")
  }

  /** You cannot rename if it would lead to variable capture.
    */
  test("Function:VariableCapture") {
    mhir.ir.reset()
    val original = {
      val x1 = Param("x")(I8)
      val x2 = Param("x")(I8)
      Function(x1, Function(x2, Sum(x1, x2)())())()
    }
    val simplified = NS.simplify(original)
    assert(simplified == original)
    // You can still rename the first param
    assert(simplified.toString() == "(x : i8) => (x_2 : i8) => x + x_2")
  }

  /** Parameters with unique prefixes can obviously be renamed.
    */
  test("LetStm:TwoDifferentNames") {
    mhir.ir.reset()
    val original = {
      val x = Param("x")(TyStm(U8, 5))
      val y = Param("y")(TyStm(I32, 5))
      LetStm(x, x, LetStm(y, y, StmZip(x, y)())())().tchk()
    }
    val simplified = NS.simplify(original)
    assert(simplified == original)
    val expectedStr =
      "let stm x: Stm[u8, 5:u3] = x_1 in let stm y: Stm[i32, 5:u3] = y_2 in StmZip(x, y)"
    assert(simplified.toString() == expectedStr)
  }

  /** If one variable shadows another, you can rename even if they have the same
    * prefix.
    */
  test("LetStm:Shadowing") {
    mhir.ir.reset()
    val original = {
      val x = Param("x")(TyStm(U8, 5))
      LetStm(x, x, LetStm(x, LetStm(x, x, x)(), StmZip(x, x)())())().tchk()
    }
    val simplified = NS.simplify(original)
    assert(simplified == original)
    val expectedStr =
      """let stm x: Stm[u8, 5:u3] = x_1 in
        |let stm x: Stm[u8, 5:u3] = let stm x: Stm[u8, 5:u3] = x in x in
        |StmZip(x, x)
        |""".stripMargin.stripTrailing
    val actualStr = ExprPrinter.displayMultiLine(simplified, maxWidth = 100)
    assert(actualStr == expectedStr)
  }

  /** You cannot rename if it would lead to variable capture.
    */
  test("LetStm:VariableCapture") {
    mhir.ir.reset()
    val original = {
      val x1 = Param("x")(TyStm(U8, 5))
      val x2 = Param("x")(TyStm(U8, 5))
      val x3 = Param("x")(TyStm(U8, 5))
      LetStm(x1, x1, LetStm(x2, LetStm(x3, x1, x3)(), StmZip(x1, x2)())())()
    }
    val simplified = NS.simplify(original)
    assert(simplified == original)
    // You can still rename the first param
    val expectedStr =
      """let stm x: Stm[u8, 5:u3] = x_1 in
        |let stm x_2: Stm[u8, 5:u3] = let stm x: Stm[u8, 5:u3] = x in x in
        |StmZip(x, x_2)
        |""".stripMargin.stripTrailing
    val actualStr = ExprPrinter.displayMultiLine(simplified, maxWidth = 100)
    assert(actualStr == expectedStr)
  }

  /** Accumulators with unique prefixes can obviously be renamed.
    */
  test("StmBuild:DifferentAccumulatorNames") {
    val original = {
      val id = (t: Type) => (t ::+ (x => x))
      val a = Param("a")(U8)
      val b = Param("b")(TyBool)
      StmBuild(
        id(TyUInt(3))(4),
        id(U8)(a),
        id(TyBool)(b),
        Map[Param, (Expr, Expr)](
          a -> (id(U8)(C(0)(U8)), Sum(C(1)(U8), a)()),
          b -> (True, id(TyBool)(!b))
        )
      )().tchk()
    }
    val simplified = NS.simplify(original)
    assert(simplified == original)
    val expectedStr =
      """sbuild(
        |  ((x : u3) => x)(4:u3);
        |  ((x : u8) => x)(a);
        |  ((x : bool) => x)(b);
        |  (a : u8) = (
        |    ((x : u8) => x)(0:u8),
        |    1:u8 + a
        |  );
        |  (b : bool) = (
        |    true,
        |    ((x : bool) => x)(!b)
        |  );
        |)
        |""".stripMargin.stripTrailing
    val actualStr = ExprPrinter.displayMultiLine(simplified, maxWidth = 100)
    assert(actualStr == expectedStr)
  }

  /** Accumulators with the same prefix within the same StmBuild cannot have
    * their numeric ID completely removed. However, other accumulators with
    * unique prefixes should still be renamed.
    */
  test("StmBuild:TwoAccumulatorsSamePrefix") {
    mhir.ir.reset()
    val original = {
      val a = Param("a")(U8)
      val b2 = Param("b")(TyBool)
      val b1 = Param("b")(TyBool)
      StmBuild(
        6,
        Tuple(a, b1, b2)(),
        b1,
        Map[Param, (Expr, Expr)](
          a -> (C(0)(U8), Sum(C(1)(U8), a)()),
          b1 -> (True, !b1),
          b2 -> (False, !b2)
        )
      )().tchk()
    }
    val simplified = NS.simplify(original)
    assert(simplified == original)
    val expectedStr =
      """sbuild(
        |  6:u3;
        |  (a, b_1, b_2);
        |  b_1;
        |  (a : u8) = (
        |    0:u8,
        |    1:u8 + a
        |  );
        |  (b_1 : bool) = (
        |    true,
        |    !b_1
        |  );
        |  (b_2 : bool) = (
        |    false,
        |    !b_2
        |  );
        |)
        |""".stripMargin.stripTrailing
    val actualStr = ExprPrinter.display(simplified)
    assert(actualStr == expectedStr)
  }

  /** Accumulators cannot be renamed if it would lead to variable capture.
    */
  test("StmBuild:VariableCaptureInData") {
    val original = {
      val xFree = Param("x", -1)(U8)
      val xAcc = Param("x")(U8)
      StmBuild(
        5,
        Tuple(xAcc, xFree)(),
        True,
        Map[Param, (Expr, Expr)](
          xAcc -> (C(0)(U8), Sum(C(1)(U8), xAcc)())
        )
      )()
    }
    val simplified = NS.simplify(original)
    assert(simplified == original)
  }

  /** Accumulators cannot be renamed if it would lead to variable capture.
    */
  test("StmBuild:VariableCaptureInValid") {
    val original = {
      val xFree = Param("x", -1)(U8)
      val xAcc = Param("x")(U8)
      StmBuild(
        5,
        xAcc,
        xFree equ C(1)(U8),
        Map[Param, (Expr, Expr)](
          xAcc -> (C(0)(U8), Sum(C(1)(U8), xAcc)())
        )
      )()
    }
    val simplified = NS.simplify(original)
    assert(simplified == original)
  }

  /** Accumulators cannot be renamed if it would lead to variable capture.
    */
  test("StmBuild:VariableCaptureInNext") {
    val original = {
      val xFree = Param("x", -1)(U8)
      val xAcc = Param("x")(U8)
      StmBuild(
        5,
        xAcc,
        True,
        Map[Param, (Expr, Expr)](
          xAcc -> (C(0)(U8), Sum(xFree, xAcc)())
        )
      )()
    }
    val simplified = NS.simplify(original)
    assert(simplified == original)
  }

  test("VecBuild:1D") {
    val original = VecBuild(5, U8 ::+ (i => i))().tchk()
    val simplified = NS.simplify(original)
    assert(simplified == original)
    val expectedStr = "vbuild(5:u3, (i : u8) => i)"
    assert(simplified.toString() == expectedStr)
  }

  test("VecBuild:2D") {
    val original =
      VecBuild(5, U8 ::+ (i => VecBuild(4, U16 ::+ (j => Tuple(i, j)()))()))()
        .tchk()
    val simplified = NS.simplify(original)
    assert(simplified == original)
    val expectedStr =
      "vbuild(5:u3, (i : u8) => vbuild(4:u3, (j : u16) => (i, j)))"
    assert(simplified.toString() == expectedStr)
  }
}
