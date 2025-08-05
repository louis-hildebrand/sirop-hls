package mhir.sugar

import mhir.gen.vhdl.VhdlGenerator
import mhir.ir.Lowering.ExprLowering
import org.scalatest.funsuite.AnyFunSuite
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.sugar.Streamifier.Streamify

// TODO: Just use StmZip directly? Unfortunately it doesn't work right now since
//       I have yet to update the stream fusion pass, AsFusedStm2Stm, etc.
object SimpleStmZip {
  def apply(n: Int, s0: Expr, t0: Type, s1: Expr, t1: Type)(): Expr = {
    val s0Param = Param("s0")(TyStm(t0, -1))
    val s1Param = Param("s1")(TyStm(t1, -1))
    StmBuild(
      n,
      Tuple(StmData(s0Param)(), StmData(s1Param)())(),
      True,
      Map[Param, (Expr, Expr)](
        s0Param -> (s0, True),
        s1Param -> (s1, True)
      )
    )()
  }
}

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
    val actual = f.streamify()
    val examples = Seq(
      (StmLiteral(C(0)(U8))(), StmLiteral(C(42)(U8))()),
      (StmLiteral(C(99)(U8))(), StmLiteral(C(141)(U8))())
    )
    for ((in, out) <- examples) {
      assert(mhir.ir.eval(actual(in)) == out)
    }
    VhdlGenerator.validateExpr(actual)
  }

  test("i16 -> i16 -> i16") {
    val f = PlusFunction(I16).tchk().lower()
    val actual = f.streamify()
    val examples = Seq(
      (-1, 42, 41),
      (-100, 99, -1)
    )
    for ((in1, in2, out) <- examples) {
      val s = actual(StmLiteral(C(in1)(I16))())(StmLiteral(C(in2)(I16))())
      assert(mhir.ir.eval(s) == StmLiteral(C(out)(I16))())
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
    val actual = f.streamify()
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
      val s = actual(in)
      assert(mhir.ir.eval(s) == out)
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
    val actual = f.streamify()
    val examples = Seq(C(0)(U8), C(42)(U8), C(200)(U8))
    for (c <- examples) {
      val actualVal = mhir.ir.eval(actual(StmLiteral(c)()))
      val expectedVal = mhir.ir.eval(f(c))
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(actual)
  }

  test("u8 -> Stm[(u8, u8), n]:UsedInProducers") {
    val n = 7
    val f = (U8 ::+ (c =>
      SimpleStmZip(
        n,
        StmCst(n, c)(),
        U8,
        StmRange(n, c, C(1)(U8))(),
        U8
      )()
    )).tchk().lower()
    val actual = f.streamify()
    val examples = Seq(C(0)(U8), C(42)(U8), C(200)(U8))
    for (c <- examples) {
      val actualVal = mhir.ir.eval(actual(StmLiteral(c)()))
      val expectedVal = mhir.ir.eval(f(c))
      assert(actualVal == expectedVal)
    }
    VhdlGenerator.validateExpr(actual)
  }

  test("c => StmConcat(StmCst(3, c), StmCst(5, c))") {
    val f =
      (U8 ::+ (c => StmConcat(StmCst(3, c)(), StmCst(5, c)())())).tchk().lower()
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

  test("u8 -> Stm[i16, 10] -> Stm[(u8, i16), 10]") {
    val n = 10
    val f = (U8 ::+ (c =>
      TyStm(I16, n) ::+ (s => SimpleStmZip(n, StmCst(n, c)(), U8, s, I16)())
    ))
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
      TyStm(I16, n) ::+ (s =>
        SimpleStmZip(n, s, I16, StmRange(n, c, C(1)(U32))(), U32)()
      )
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
            SimpleStmZip(n, s, I8, StmCst(n, c)(), U16)(),
            LetStm(
              zippedTwice,
              SimpleStmZip(
                n,
                zippedOnce,
                (I8, U16),
                StmRange(n, c, C(1)(U16))(),
                U16
              )(),
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
}
