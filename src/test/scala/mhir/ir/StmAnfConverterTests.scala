package mhir.ir

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.TypeCheck
import mhir.sugar.{SimpleCount, SimpleMap, SimpleZip}
import org.scalatest.funsuite.AnyFunSuite

class StmAnfConverterTests extends AnyFunSuite {
  test("BasicTest") {
    val n = 16
    val original = {
      val x = Param("x")(TyStm(U8, n))
      LetStm(
        C(1)(U8),
        x,
        SimpleMap(
          SimpleMap(SimpleCount(C(n)(U8)), x => Sum(C(6)(U8), x)()),
          x => Prod(C(3)(U8), x)()
        ),
        SimpleZip(SimpleMap(x, x => x), x, SimpleMap(x, x => Sum(x, x)()))
      )().tchk().lower()
    }
    val anf = StmAnfConverter.convert(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(anf)
    assert(actualVal == expectedVal)

    // Correct form
    val expected = {
      val x0 = Param("x0")(TyStm(U8, n))
      val x1 = Param("x1")(TyStm(U8, n))
      val x2 = Param("x2")(TyStm(U8, n))
      val x3 = Param("x3")(TyStm(U8, n))
      val x4 = Param("x4")(TyStm(U8, n))
      val x5 = Param("x5")(TyStm(U8, n))
      val x6 = Param("x6")(TyStm((U8, U8, U8), n))
      LetInlineStm(
        x0,
        SimpleCount(C(n)(U8)),
        LetInlineStm(
          x1,
          SimpleMap(x0, x => Sum(C(6)(U8), x)()),
          LetInlineStm(
            x2,
            SimpleMap(x1, x => Prod(C(3)(U8), x)()),
            LetStm(
              1,
              x3,
              x2,
              LetInlineStm(
                x4,
                SimpleMap(x3, x => x),
                LetInlineStm(
                  x5,
                  SimpleMap(x3, x => Sum(x, x)()),
                  LetInlineStm(
                    x6,
                    SimpleZip(x4, x3, x5),
                    x6
                  )()
                )()
              )()
            )()
          )()
        )()
      )().tchk()
    }
    assert(anf == expected)
  }

  test("LetStmInSeries") {
    val n = 8
    val original = {
      val x = Param("x")(TyStm(U8, n))
      val y = Param("y")(TyStm(U8, n))
      val forkJoin = (input: Expr) =>
        SimpleMap(
          SimpleZip(input, SimpleMap(input, x => Sum(C(5)(U8), x)())),
          x => Sum(x.__0, x.__1)()
        )
      LetStm(
        1,
        x,
        LetStm(1, y, SimpleCount(C(n)(U8)), forkJoin(y))(),
        forkJoin(x)
      )().tchk().lower()
    }
    val anf = StmAnfConverter.convert(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(anf)
    assert(actualVal == expectedVal)

    // Correct form
    val expected = {
      val x0 = Param("x0")(TyStm(U8, n))
      val x1 = Param("x1")(TyStm(U8, n))
      val x2 = Param("x2")(TyStm(U8, n))
      val x3 = Param("x3")(TyStm((U8, U8), n))
      val x4 = Param("x4")(TyStm(U8, n))
      val x5 = Param("x5")(TyStm(U8, n))
      val x6 = Param("x6")(TyStm(U8, n))
      val x7 = Param("x7")(TyStm((U8, U8), n))
      val x8 = Param("x8")(TyStm(U8, n))
      LetInlineStm(
        x0,
        SimpleCount(C(n)(U8)),
        LetStm(
          1,
          x1,
          x0,
          LetInlineStm(
            x2,
            SimpleMap(x1, x => Sum(C(5)(U8), x)()),
            LetInlineStm(
              x3,
              SimpleZip(x1, x2),
              LetInlineStm(
                x4,
                SimpleMap(x3, x => Sum(x.__0, x.__1)()),
                LetStm(
                  1,
                  x5,
                  x4,
                  LetInlineStm(
                    x6,
                    SimpleMap(x5, x => Sum(C(5)(U8), x)()),
                    LetInlineStm(
                      x7,
                      SimpleZip(x5, x6),
                      LetInlineStm(
                        x8,
                        SimpleMap(x7, x => Sum(x.__0, x.__1)()),
                        x8
                      )()
                    )()
                  )()
                )()
              )()
            )()
          )()
        )()
      )().tchk()
    }
    assert(anf == expected)
  }
}
