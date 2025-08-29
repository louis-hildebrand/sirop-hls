package mhir.sugar

import mhir.gen.vhdl.VhdlGenerator
import mhir.ir.Lowering.ExprLowering
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.sugar.Streamifier.Streamify
import org.scalatest.funsuite.AnyFunSuite

class StreamifierTests extends AnyFunSuite {
  test("u8") {
    val e = C(42)(U8)
    val actual = e.streamify().tchk().lower()
    val expected = StmLiteral(e)()
    assert(mhir.ir.eval(actual) == expected)
    VhdlGenerator.validateExpr(actual)
  }

  test("Vec[bool, 20]") {
    val e = VecBuild(20, U8 ::+ (i => i))().tchk().lower()
    val actual = e.streamify().tchk().lower()
    val expected = mhir.ir.eval(StmLiteral(e)())
    assert(mhir.ir.eval(actual) == expected)
    VhdlGenerator.validateExpr(actual)
  }

  test("u8 -> u8") {
    val f = (U8 ::+ (x => x + C(42)(U8))).tchk().lower()
    val actual = f.streamify().asInstanceOf[Function]
    val examples = Seq(
      (StmLiteral(C(0)(U8))(), StmLiteral(C(42)(U8))()),
      (StmLiteral(C(99)(U8))(), StmLiteral(C(141)(U8))())
    )
    for ((in, out) <- examples) {
      val actualVal = mhir.ir.eval(LetStm(actual.param, in, actual.body)())
      assert(actualVal == out)
    }
    VhdlGenerator.validateExpr(actual)
  }

  test("i16 -> i16 -> i16") {
    val f = PlusFunction(I16).tchk().lower()
    val actual = f.streamify().asInstanceOf[Function]
    val examples = Seq(
      (-1, 42, 41),
      (-100, 99, -1)
    )
    for ((in1, in2, out) <- examples) {
      val (x1, x2, body) = (
        actual.param,
        actual.body.asInstanceOf[Function].param,
        actual.body.asInstanceOf[Function].body
      )
      val in1Stm = StmLiteral(C(in1)(I16))()
      val in2Stm = StmLiteral(C(in2)(I16))()
      val actualVal =
        mhir.ir.eval(LetStm(x1, in1Stm, LetStm(x2, in2Stm, body)())())
      val expectedVal = StmLiteral(C(out)(I16))()
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(actual)
  }

  test("s => StmMap(x, +5)") {
    val n = 10
    val f = {
      val s = Param("s")(TyStm(U8, n))
      val map = {
        val x = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          C(5)(U8) + StmData(x)(),
          True,
          Map[Param, (Expr, Expr)](
            x -> (s, True)
          )
        )()
      }
      Function(s, map)().tchk().lower()
    }
    val actual = f.streamify().asInstanceOf[Function]
    val examples = Seq(
      (
        StmLiteral((0 until n).map(t => C(t)(U8)): _*)(),
        StmLiteral((0 until n).map(t => C(t + 5)(U8)): _*)()
      ),
      (
        StmLiteral((0 until n).map(t => C(t * t)(U8)): _*)(),
        StmLiteral((0 until n).map(t => C(t * t + 5)(U8)): _*)()
      )
    )
    for ((in, out) <- examples) {
      val actualVal = mhir.ir.eval(LetStm(actual.param, in, actual.body)())
      assert(actualVal == out)
    }
    VhdlGenerator.validateExpr(actual)
  }

  test("(x : Stm[u8, 10]) => (y : Stm[u8, 10]) => x") {
    val f = (TyStm(U8, 10) ::+ (x => TyStm(U8, 10) ::+ (_ => x))).tchk().lower()
    val actual = f.streamify()
    val expected =
      (TyStm(U8, 10) ::+ (x =>
        TyStm(U8, 10) ::+ (_ =>
          StmBuild(
            10,
            StmData(x)(),
            True,
            Map[Param, (Expr, Expr)](x -> (x, True))
          )()
        )
      )).tchk().lower()
    assert(actual == expected)
    VhdlGenerator.validateExpr(actual)
  }

  test("(x : Stm[u8, 10]) => (y : Stm[u8, 10]) => y") {
    val f = (TyStm(U8, 10) ::+ (_ => TyStm(U8, 10) ::+ (y => y))).tchk().lower()
    val actual = f.streamify()
    val expected =
      (TyStm(U8, 10) ::+ (_ =>
        TyStm(U8, 10) ::+ (y =>
          StmBuild(
            10,
            StmData(y)(),
            True,
            Map[Param, (Expr, Expr)](y -> (y, True))
          )()
        )
      )).tchk().lower()
    assert(actual == expected)
    VhdlGenerator.validateExpr(actual)
  }

  test("u8 -> Stm[(u8, u8), 10]:UsedDirectly") {
    val i = Param("i")(U8)
    val f = (U8 ::+ (c =>
      StmBuild(
        10,
        Tuple(C(13)(U8) + c, i)(),
        True,
        Map[Param, (Expr, Expr)](
          i -> (c, i + C(1)(U8))
        )
      )()
    )).tchk().lower()
    val actual = f.streamify().asInstanceOf[Function]
    val examples = Seq(C(0)(U8), C(42)(U8), C(200)(U8))
    for (c <- examples) {
      val cStm = StmLiteral(c)()
      val actualVal = mhir.ir.eval(LetStm(actual.param, cStm, actual.body)())
      val expectedVal = mhir.ir.eval(f(c))
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(actual)
  }

  test("u8 -> Stm[(u8, u8), n]:UsedInProducers") {
    val n = 7
    val f = (U8 ::+ (c => StmZip(StmCst(n, c)(), StmRange(n, c, C(1)(U8))())()))
      .tchk()
      .lower()
    val actual = f.streamify()
    val examples = Seq(C(0)(U8), C(42)(U8), C(200)(U8))
    for (c <- examples) {
      val actualVal = mhir.ir.eval(actual(StmLiteral(c)()))
      val expectedVal = mhir.ir.eval(f(c))
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(actual)
  }

  test("c => StmConcat(StmCst(3, c), StmCst(5, 42))") {
    val f = {
      val c = Param("c")(U8)
      val n1 = 3
      val cst1 = StmBuild(n1, c, True)()
      val n2 = 5
      val cst2 = StmBuild(n2, C(42)(U8), True)()
      val concat = {
        val t = Param("t")(U8)
        val s0 = Param("s0")(TyStm(U8, -1))
        val s1 = Param("s1")(TyStm(U8, -1))
        StmBuild(
          n1 + n2,
          Mux(t lt C(n1)(U8), StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr)](
            t -> (C(0)(U8), Sum(C(1)(U8), t)()),
            s0 -> (cst1, t lt C(n1)(U8)),
            s1 -> (cst2, t geq C(n1)(U8))
          )
        )()
      }
      Function(c, concat)().tchk().lower()
    }
    val actual = f.streamify().asInstanceOf[Function]

    // Check correct behaviour
    val examples = Seq(0, 42, 200).map(C(_)(U8))
    for (c <- examples) {
      val cStm = StmLiteral(c)()
      val actualVal = mhir.ir.eval(LetStm(actual.param, cStm, actual.body)())
      val expectedVal = mhir.ir.eval(f(c))
      assert(actualVal == expectedVal)
    }

    // Check that the streamifier doesn't needlessly complicate StmBuilds that
    // don't actually use the input directly.
    // In this case, the StmConcat and StmCst(5, 42) do not need to store a
    // copy of the input.
    val expected = {
      val c = Param("c")(TyStm(U8, 1))
      val n1 = 3
      val cst1 = {
        val isFirstStep = Param("is_first_step")(TyBool)
        val cBuf = Param("c_buf")(U8)
        val cStm = Param("c_stm")(TyStm(U8, 1))
        StmBuild(
          n1,
          // This MUX is not really necessary, but it should be straightforward
          // for the optimizer to remove it and it would make the streamifier
          // code, which is already quite long, a little bit more complex
          Mux(isFirstStep, StmData(cStm)(), cBuf)(),
          True,
          Map[Param, (Expr, Expr)](
            isFirstStep -> (True, False),
            cBuf -> (Default(U8), Mux(isFirstStep, StmData(cStm)(), cBuf)()),
            cStm -> (c, isFirstStep)
          )
        )()
      }
      val n2 = 5
      val cst2 = StmBuild(n2, C(42)(U8), True)()
      val concat = {
        val t = Param("t")(U8)
        val s0 = Param("s0")(TyStm(U8, -1))
        val s1 = Param("s1")(TyStm(U8, -1))
        StmBuild(
          n1 + n2,
          Mux(t lt C(n1)(U8), StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr)](
            t -> (C(0)(U8), Sum(C(1)(U8), t)()),
            s0 -> (cst1, t lt C(n1)(U8)),
            s1 -> (cst2, t geq C(n1)(U8))
          )
        )()
      }
      Function(c, LetStm(c, c, concat)())().tchk().lower()
    }
    assert(actual == expected)

    VhdlGenerator.validateExpr(actual)
  }

  test("ReadyDependsDirectlyOnInput") {
    val even = Param("even")(TyBool)
    val n = 5
    val s = Param("s")(TyStm(U8, n))
    val b = Param("b")(TyBool)
    val originalStm = StmBuild(
      2 * n,
      Mux(even === b, StmData(s)(), C(42)(U8))(),
      True,
      Map[Param, (Expr, Expr)](
        s -> (StmCount(C(n)(U8))(), even === b),
        b -> (True, !b)
      )
    )()
    val originalFunc = Function(even, originalStm)().tchk().lower()
    val streamifiedFunc = originalFunc.streamify()

    val expectedF = mhir.ir.eval(originalFunc(False))
    val actualF =
      mhir.ir.eval(streamifiedFunc(StmCst(1, False)().tchk().lower()))
    assert(actualF == expectedF)

    val expectedT = mhir.ir.eval(originalFunc(True))
    val actualT =
      mhir.ir.eval(streamifiedFunc(StmCst(1, True)().tchk().lower()))
    assert(actualT == expectedT)
  }

  test("ReadyDependsIndirectlyOnInput") {
    val even = Param("even")(TyBool)
    val evenAcc = Param("even_reg")(TyBool)
    val n = 5
    val s = Param("s")(TyStm(U8, n))
    val b = Param("b")(TyBool)
    val originalStm = StmBuild(
      2 * n,
      Mux(evenAcc === b, StmData(s)(), C(42)(U8))(),
      True,
      Map[Param, (Expr, Expr)](
        s -> (StmCount(C(n)(U8))(), evenAcc === b),
        b -> (True, !b),
        evenAcc -> (even, evenAcc)
      )
    )()
    val originalFunc = Function(even, originalStm)().tchk().lower()
    val streamifiedFunc = originalFunc.streamify()

    val expectedF = mhir.ir.eval(originalFunc(False))
    val actualF =
      mhir.ir.eval(streamifiedFunc(StmCst(1, False)().tchk().lower()))
    assert(actualF == expectedF)

    val expectedT = mhir.ir.eval(originalFunc(True))
    val actualT =
      mhir.ir.eval(streamifiedFunc(StmCst(1, True)().tchk().lower()))
    assert(actualT == expectedT)
  }

  test("u8 -> Stm[i16, 10] -> Stm[(u8, i16), 10]") {
    val n = 10
    val f = (U8 ::+ (c => TyStm(I16, n) ::+ (s => StmZip(StmCst(n, c)(), s)())))
      .tchk()
      .lower()
    val actual = f.streamify()
    val examples = Seq(
      (C(42)(U8), StmLiteral((0 until n).map(t => C(t - 5)(I16)): _*)()),
      (C(99)(U8), StmLiteral((0 until n).map(t => C(t * t)(I16)): _*)())
    )
    for ((c, s) <- examples) {
      val actualVal = mhir.ir.eval(actual(StmLiteral(c)())(s))
      val expectedVal = mhir.ir.eval(f(c)(s))
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(actual)
  }

  test("Stm[i16, 10] -> u32 -> Stm[(i16, u32), 10]") {
    val n = 10
    val f = (U32 ::+ (c =>
      TyStm(I16, n) ::+ (s => StmZip(s, StmRange(n, c, C(1)(U32))())())
    )).tchk().lower()
    val actual = f.streamify()
    val examples = Seq(
      (C(42)(U32), StmLiteral((0 until n).map(t => C(t - 5)(I16)): _*)()),
      (C(999)(U32), StmLiteral((0 until n).map(t => C(t * t)(I16)): _*)())
    )
    for ((c, s) <- examples) {
      val actualVal = mhir.ir.eval(actual(StmLiteral(c)())(s))
      val expectedVal = mhir.ir.eval(f(c)(s))
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(actual)
  }

  test("LetStm") {
    val n = 7
    val f = {
      val zippedOnce = Param("zip1")()
      val zippedTwice = Param("zip2")()
      (U16 ::+ (c =>
        TyStm(I8, n) ::+ (s =>
          LetStm(
            zippedOnce,
            StmZip(s, StmCst(n, c)())(),
            LetStm(
              zippedTwice,
              StmZip(zippedOnce, StmRange(n, c, C(1)(U16))())(),
              zippedTwice
            )()
          )()
        )
      )).tchk().lower()
    }
    val actual = f.streamify()
    val examples = Seq(
      (C(42)(U16), StmLiteral((0 until n).map(t => C(t - 5)(I8)): _*)()),
      (C(999)(U16), StmLiteral((0 until n).map(t => C(t * t)(I8)): _*)())
    )
    for ((c, s) <- examples) {
      val actualVal = mhir.ir.eval(actual(StmLiteral(c)())(s))
      val expectedVal = mhir.ir.eval(f(c)(s))
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(actual)
  }

  // The streamifier should leave free variables as-is
  test("FreeVar:u8") {
    val n = 11
    val c = Param("c")(U8)
    val original = StmBuild(n, c, True)().tchk().lower()
    val actual = original.streamify()
    assert(actual == original)
  }

  // The streamifier should leave free variables as-is
  test("FreeVar:Stm[u8, n]") {
    val n = 11
    val s = Param("s")(TyStm(U8, n))
    val acc = Param("s")(TyStm(U8, -1))
    val original = StmBuild(
      n,
      Sum(C(5)(U8), StmData(acc)())(),
      True,
      Map[Param, (Expr, Expr)](
        acc -> (s, True)
      )
    )().tchk().lower()
    val actual = original.streamify()
    assert(actual == original)
  }

  // The length of the top-level stream must not depend on any input.
  // If it did, what expression would we use to represent the stream's type?
  test("TopStreamLengthDependingOnInput") {
    val f = (U8 ::+ (n => StmCount(n)())).tchk().lower()
    val exc = intercept[IllegalArgumentException](f.streamify())
    assert(exc.getMessage.startsWith("Types cannot depend on any inputs."))
  }

  // The lengths of non-top-level streams should not depend on any input.
  // TODO: Maybe it would be possible to allow it by fusion. But there's no
  //       clear use case for it, as far as I know, and it's extra complexity.
  test("ProducerStreamLengthDependingOnInput") {
    val f = (U8 ::+ (n => StmFold(StmCount(n)(), C(0)(U8), PlusFunction(U8))()))
      .tchk()
      .lower()
    val exc = intercept[IllegalArgumentException](f.streamify())
    assert(exc.getMessage.startsWith("Types cannot depend on any inputs."))
  }

  test("VecLengthDependingOnInput") {
    val f = (U8 ::+ (n => StmCst(1, VecBuild(n, U8 ::+ (i => i + 1))())()))
      .tchk()
      .lower()
    val exc = intercept[IllegalArgumentException](f.streamify())
    assert(exc.getMessage.startsWith("Types cannot depend on any inputs."))
  }
}
