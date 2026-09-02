package mhir.sugar

import mhir.canonicalize._
import mhir.delay.DiscardAccumulatorDelays
import mhir.gen.vhdl.VhdlGenerator
import mhir.ir._
import mhir.sugar.Streamifier.Streamify
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class StreamifierTests extends AnyFunSuite {

  test("u8") {
    val e = C(42)(U8)
    val actual = e.streamify.tchk().lower
    val expected = StmLiteral(e)()
    assert(mhir.eval.eval(actual) == expected)
    // TODO: use semantic analyzer instead of VhdlGenerator.validateExpr?
    //       Would first need to ensure the semantic analyzer includes all the necessary checks.
    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  test("Vec[bool, 20]") {
    val e = VecBuild(20, U8 ::+ (i => i))().tchk().lower
    val actual = e.streamify.tchk().lower
    val expected = mhir.eval.eval(StmLiteral(e)())
    assert(mhir.eval.eval(actual) == expected)
    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  test("u8 -> u8") {
    val f = (U8 ::+ (x => x + C(42)(U8))).tchk().lower
    val actual = f.streamify.asInstanceOf[Function]
    val examples = Seq(
      (StmLiteral(C(0)(U8))(), StmLiteral(C(42)(U8))()),
      (StmLiteral(C(99)(U8))(), StmLiteral(C(141)(U8))())
    )
    for ((in, out) <- examples) {
      val actualVal = mhir.eval.eval(LetStm(1, actual.param, in, actual.body)())
      assert(actualVal == out)
    }
    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  test("i16 -> i16 -> i16") {
    val f = PlusFunction(I16).tchk().lower
    val actual = f.streamify.asInstanceOf[Function]
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
        mhir.eval.eval(LetStm(1, x1, in1Stm, LetStm(1, x2, in2Stm, body)())())
      val expectedVal = StmLiteral(C(out)(I16))()
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  test("s => StmMap(x, +5)") {
    val n = 10
    val f = {
      val s = Param("s")(TyStm(U8, n))
      val map = {
        val x = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          Tuple()(),
          Undefined(Missing),
          C(5)(U8) + StmData(x)(),
          True,
          Map(),
          Map[Param, (Expr, Expr, Expr)](
            x -> (s, True, Tuple()())
          )
        )()
      }
      Function(s, map)().tchk().lower
    }
    val actual = f.streamify.asInstanceOf[Function]
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
      val actualVal = mhir.eval.eval(LetStm(1, actual.param, in, actual.body)())
      assert(actualVal == out)
    }
    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  test("(x : Stm[u8, 10]) => (y : Stm[u8, 10]) => x") {
    val f = (TyStm(U8, 10) ::+ (x => TyStm(U8, 10) ::+ (_ => x))).tchk().lower
    val actual = f.streamify
    val expected =
      (TyStm(U8, 10) ::+ (x =>
        TyStm(U8, 10) ::+ (_ =>
          StmBuild(
            10,
            1,
            Undefined(Missing),
            StmData(x)(),
            True,
            Map(),
            Map[Param, (Expr, Expr, Expr)](x -> (x, True, C(0)()))
          )()
        )
      )).tchk().lower
    assert(actual == expected)
    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  test("(x : Stm[u8, 10]) => (y : Stm[u8, 10]) => y") {
    val f = (TyStm(U8, 10) ::+ (_ => TyStm(U8, 10) ::+ (y => y))).tchk().lower
    val actual = f.streamify
    val expected =
      (TyStm(U8, 10) ::+ (_ =>
        TyStm(U8, 10) ::+ (y =>
          StmBuild(
            10,
            1,
            Undefined(Missing),
            StmData(y)(),
            True,
            Map(),
            Map[Param, (Expr, Expr, Expr)](y -> (y, True, C(0)()))
          )()
        )
      )).tchk().lower
    assert(actual == expected)
    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  test("u8 -> Stm[(u8, u8), 10]:UsedDirectly") {
    val i = Param("i")(U8)
    val f = (U8 ::+ (c =>
      StmBuild(
        10,
        Tuple()(),
        Undefined(Missing),
        Tuple(C(13)(U8) + c, i)(),
        True,
        Map[Param, (Expr, Expr, Expr)](
          i -> (c, i + C(1)(U8), Tuple()())
        ),
        Map()
      )()
    )).tchk().lower.asInstanceOf[Function]
    val actual = f.streamify.asInstanceOf[Function]
    val examples = Seq(C(0)(U8), C(42)(U8), C(200)(U8))
    for (c <- examples) {
      val cStm = StmLiteral(c)()
      val actualVal =
        mhir.eval.eval(LetStm(1, actual.param, cStm, actual.body)())
      val expectedVal = mhir.eval.eval(f.body.subPreserveType(f.param -> c))
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  test("u8 -> Stm[(u8, u8), n]:UsedInProducers") {
    val n = 7
    val f = (U8 ::+ (c => StmZip(StmCst(n, c)(), StmRange(n, c, C(1)(U8))())()))
      .tchk()
      .lower
      .asInstanceOf[Function]
    val actual = f.streamify.asInstanceOf[Function]
    val examples = Seq(C(0)(U8), C(42)(U8), C(200)(U8))
    for (c <- examples) {
      val actualVal = mhir.eval.eval(
        actual.body,
        inputs = Map(actual.param -> StmLiteral(c)())
      )
      val expectedVal = mhir.eval.eval(f.body.subPreserveType(f.param -> c))
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  test("c => StmConcat(StmCst(3, c), StmCst(5, 42))") {
    val f = {
      val c = Param("c")(U8)
      val n1 = 3
      val cst1 =
        StmBuild(n1, Tuple()(), Undefined(Missing), c, True, Map(), Map())()
      val n2 = 5
      val cst2 = StmBuild(
        n2,
        Tuple()(),
        Undefined(Missing),
        C(42)(U8),
        True,
        Map(),
        Map()
      )()
      val concat = {
        val t = Param("t")(U8)
        val s0 = Param("s0")(TyStm(U8, -1))
        val s1 = Param("s1")(TyStm(U8, -1))
        StmBuild(
          n1 + n2,
          Tuple()(),
          Undefined(Missing),
          Mux(t lt C(n1)(U8), StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr, Expr)](
            t -> (C(0)(U8), Sum(C(1)(U8), t)(), Tuple()())
          ),
          Map[Param, (Expr, Expr, Expr)](
            s0 -> (cst1, t lt C(n1)(U8), Tuple()()),
            s1 -> (cst2, t geq C(n1)(U8), Tuple()())
          )
        )()
      }
      Function(c, concat)().tchk().lower.asInstanceOf[Function]
    }
    val actual = f.streamify.asInstanceOf[Function]

    // Check correct behaviour
    val examples = Seq(0, 42, 200).map(C(_)(U8))
    for (c <- examples) {
      val cStm = StmLiteral(c)()
      val actualVal =
        mhir.eval.eval(LetStm(1, actual.param, cStm, actual.body)())
      val expectedVal = mhir.eval.eval(f.body.subPreserveType(f.param -> c))
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
          Tuple()(),
          Undefined(Missing),
          // This MUX is not really necessary, but it should be straightforward
          // for the optimizer to remove it and it would make the streamifier
          // code, which is already quite long, a little bit more complex
          Mux(isFirstStep, StmData(cStm)(), cBuf)(),
          True,
          Map[Param, (Expr, Expr, Expr)](
            isFirstStep -> (True, False, C(1)()),
            cBuf -> (
              Undefined(U8),
              Mux(
                isFirstStep,
                StmData(cStm)(),
                cBuf
              )(),
              Tuple()()
            )
          ),
          Map[Param, (Expr, Expr, Expr)](
            cStm -> (c, isFirstStep, C(0)())
          )
        )()
      }
      val n2 = 5
      val cst2 = StmBuild(
        n2,
        Tuple()(),
        Undefined(Missing),
        C(42)(U8),
        True,
        Map(),
        Map()
      )()
      val concat = {
        val t = Param("t")(U8)
        val s0 = Param("s0")(TyStm(U8, -1))
        val s1 = Param("s1")(TyStm(U8, -1))
        StmBuild(
          n1 + n2,
          Tuple()(),
          Undefined(Missing),
          Mux(t lt C(n1)(U8), StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr, Expr)](
            t -> (C(0)(U8), Sum(C(1)(U8), t)(), Tuple()())
          ),
          Map[Param, (Expr, Expr, Expr)](
            s0 -> (cst1, t lt C(n1)(U8), Tuple()()),
            s1 -> (cst2, t geq C(n1)(U8), Tuple()())
          )
        )()
      }
      Function(c, LetStm(1, c, c, concat)())().tchk().lower
    }
    assert(actual == expected)

    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  test("ReadyDependsDirectlyOnInput") {
    val even = Param("even")(TyBool)
    val n = 5
    val s = Param("s")(TyStm(U8, n))
    val b = Param("b")(TyBool)
    val originalStm = StmBuild(
      2 * n,
      Tuple()(),
      Undefined(Missing),
      Mux(even === b, StmData(s)(), C(42)(U8))(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        b -> (True, !b, Tuple()())
      ),
      Map[Param, (Expr, Expr, Expr)](
        s -> (StmCount(C(n)(U8))(), even === b, Tuple()())
      )
    )()
    val originalFunc = Function(even, originalStm)().tchk().lower
    val streamifiedFunc = originalFunc.streamify.asInstanceOf[Function]

    val expectedF = mhir.eval.eval(originalStm.subPreserveType(even -> False))
    val actualF = mhir.eval.eval(
      streamifiedFunc.body,
      inputs = Map(streamifiedFunc.param -> StmLiteral(False)().tchk())
    )
    assert(actualF == expectedF)

    val expectedT = mhir.eval.eval(originalStm.subPreserveType(even -> True))
    val actualT = mhir.eval.eval(
      streamifiedFunc.body,
      inputs = Map(streamifiedFunc.param -> StmLiteral(True)().tchk())
    )
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
      Tuple()(),
      Undefined(Missing),
      Mux(evenAcc === b, StmData(s)(), C(42)(U8))(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        b -> (True, !b, Tuple()()),
        evenAcc -> (even, evenAcc, Tuple()())
      ),
      Map[Param, (Expr, Expr, Expr)](
        s -> (StmCount(C(n)(U8))(), evenAcc === b, Tuple()())
      )
    )()
    val originalFunc = Function(even, originalStm)().tchk().lower
    val streamifiedFunc = originalFunc.streamify.asInstanceOf[Function]

    val expectedF = mhir.eval.eval(originalStm.subPreserveType(even -> False))
    val actualF = mhir.eval.eval(
      streamifiedFunc.body,
      inputs = Map(streamifiedFunc.param -> StmLiteral(False)().tchk())
    )
    assert(actualF == expectedF)

    val expectedT = mhir.eval.eval(originalStm.subPreserveType(even -> True))
    val actualT = mhir.eval.eval(
      streamifiedFunc.body,
      inputs = Map(streamifiedFunc.param -> StmLiteral(True)().tchk())
    )
    assert(actualT == expectedT)
  }

  test("u8 -> Stm[i16, 10] -> Stm[(u8, i16), 10]") {
    val n = 10
    val f @ Function(originalC, Function(originalS, originalBody)) =
      (U8 ::+ (c => TyStm(I16, n) ::+ (s => StmZip(StmCst(n, c)(), s)())))
        .tchk()
        .lower
    val actual @ Function(c, Function(s, actualBody)) = f.streamify
    val examples = Seq(
      (C(42)(U8), StmLiteral((0 until n).map(t => C(t - 5)(I16)): _*)()),
      (C(99)(U8), StmLiteral((0 until n).map(t => C(t * t)(I16)): _*)())
    )
    for ((cVal, sVal) <- examples) {
      val actualVal = mhir.eval.eval(
        actualBody,
        inputs = Map(c -> StmLiteral(cVal)().tchk(), s -> sVal)
      )
      val expectedVal = mhir.eval.eval(
        originalBody.subPreserveType(originalC -> cVal),
        inputs = Map(originalS -> sVal)
      )
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  test("Stm[i16, 10] -> u32 -> Stm[(i16, u32), 10]") {
    val n = 10
    val f @ Function(c0, Function(s0, originalBody)) =
      (U32 ::+ (c =>
        TyStm(I16, n) ::+ (s => StmZip(s, StmRange(n, c, C(1)(U32))())())
      )).tchk().lower
    val actual @ Function(c1, Function(s1, actualBody)) = f.streamify
    val examples = Seq(
      (C(42)(U32), StmLiteral((0 until n).map(t => C(t - 5)(I16)): _*)()),
      (C(999)(U32), StmLiteral((0 until n).map(t => C(t * t)(I16)): _*)())
    )
    for ((cVal, sVal) <- examples) {
      val actualVal = mhir.eval.eval(
        actualBody,
        inputs = Map(c1 -> StmLiteral(cVal)().tchk(), s1 -> sVal)
      )
      val expectedVal = mhir.eval.eval(
        originalBody.subPreserveType(c0 -> cVal),
        inputs = Map(s0 -> sVal)
      )
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  test("LetStm") {
    val n = 7
    val f @ Function(c0, Function(s0, originalBody)) = {
      val zippedOnce = Param("zip1")()
      val zippedTwice = Param("zip2")()
      (U16 ::+ (c =>
        TyStm(I8, n) ::+ (s =>
          LetStm(
            1,
            zippedOnce,
            StmZip(s, StmCst(n, c)())(),
            LetStm(
              1,
              zippedTwice,
              StmZip(zippedOnce, StmRange(n, c, C(1)(U16))())(),
              zippedTwice
            )()
          )()
        )
      )).tchk().lower
    }
    val actual @ Function(c1, Function(s1, actualBody)) = f.streamify
    val examples = Seq(
      (C(42)(U16), StmLiteral((0 until n).map(t => C(t - 5)(I8)): _*)()),
      (C(999)(U16), StmLiteral((0 until n).map(t => C(t * t)(I8)): _*)())
    )
    for ((cVal, sVal) <- examples) {
      val actualVal = mhir.eval.eval(
        actualBody,
        inputs = Map(c1 -> StmLiteral(cVal)().tchk(), s1 -> sVal)
      )
      val expectedVal = mhir.eval.eval(
        originalBody.subPreserveType(c0 -> cVal),
        inputs = Map(s0 -> sVal)
      )
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(DiscardAccumulatorDelays(actual))
  }

  // The streamifier should leave free variables as-is
  test("FreeVar:u8") {
    val n = 11
    val c = Param("c")(U8)
    val original = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      c,
      True,
      Map(),
      Map()
    )().tchk().lower
    val actual = original.streamify
    assert(actual == original)
  }

  // The streamifier should leave free variables as-is
  test("FreeVar:Stm[u8, n]") {
    val n = 11
    val s = Param("s")(TyStm(U8, n))
    val acc = Param("s")(TyStm(U8, -1))
    val original = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Sum(C(5)(U8), StmData(acc)())(),
      True,
      Map(),
      Map[Param, (Expr, Expr, Expr)](
        acc -> (s, True, Tuple()())
      )
    )().tchk().lower
    val actual = original.streamify
    assert(actual == original)
  }

  // The length of the top-level stream must not depend on any input.
  // If it did, what expression would we use to represent the stream's type?
  test("TopStreamLengthDependingOnInput") {
    val f = (U8 ::+ (n => StmCount(n)())).tchk().lower
    val exc = intercept[IllegalArgumentException](f.streamify)
    assert(exc.getMessage.startsWith("Types cannot depend on any inputs."))
  }

  // The lengths of non-top-level streams should not depend on any input.
  // NOTE: Maybe it would be possible to allow it by fusion. But there's no
  //       clear use case for it, as far as I know, and it's extra complexity.
  test("ProducerStreamLengthDependingOnInput") {
    val f =
      (U8 ::+ (n =>
        StmFold(
          StmCount(n)(),
          C(0)(U8),
          (U8, U8) ::+ (x => Sum(x.__0, x.__1)())
        )()
      )).tchk().lower
    val exc = intercept[IllegalArgumentException](f.streamify)
    assert(exc.getMessage.startsWith("Types cannot depend on any inputs."))
  }

  test("VecLengthDependingOnInput") {
    val f = (U8 ::+ (n => StmCst(1, VecBuild(n, U8 ::+ (i => i + 1))())()))
      .tchk()
      .lower
    val exc = intercept[IllegalArgumentException](f.streamify)
    assert(exc.getMessage.startsWith("Types cannot depend on any inputs."))
  }
}
