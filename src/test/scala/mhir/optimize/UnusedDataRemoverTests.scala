package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class UnusedDataRemoverTests extends AnyFunSuite {

  val pass: UnusedDataRemover = EnabledUnusedDataRemover

  test("(i16, bool) fork-join") {
    val n = 10
    val e = {
      val input = SimpleMap(
        SimpleCount(C(n)(U8)),
        e =>
          Tuple(
            PadTo(ToSigned(e)(), 16)(),
            Mod(e, C(2)(U8))() equ C(0)(U8)
          )()
      ).tchk()
      val x = Param("x")(TyStm((I16, TyBool), n))
      val zipped = {
        val s1 = Param("s1")(TyStm((I16, TyBool), -1))
        val s2 = Param("s2")(TyStm((I16, TyBool), -1))
        StmBuild(
          n,
          Tuple(StmData(s1)().__0, StmData(s2)().__1)(),
          True,
          Map[Param, (Expr, Expr)](
            s1 -> (SimpleNop(SimpleNop(x)), True),
            s2 -> (SimpleNop(SimpleNop(x)), True)
          )
        )().tchk()
      }
      LetStm(0, x, input, zipped)().tchk()
    }
    val actual = pass.removeUnusedData(e)
    val LetStm(IntCst(0), _, _, zip: StmBuild) = actual

    // Correctness
    val expectedVal = mhir.eval.eval(e)
    val actualVal = mhir.eval.eval(actual)
    assert(actualVal == expectedVal)

    // Effective simplification (data branch)
    assert(
      zip.producers.exists({ case (x, _) =>
        x.typ.asInstanceOf[TyStm].t == TyTuple(I16, TyTuple())
      }),
      "the second stage of one branch should contain only i16"
    )
    val dataStep2 = zip.producers
      .collectFirst({
        case (x, (s, _))
            if x.typ.asInstanceOf[TyStm].t == TyTuple(I16, TyTuple()) =>
          s
      })
      .get
      .asInstanceOf[StmBuild]
    assert(
      dataStep2.producers.exists({ case (x, _) =>
        x.typ.asInstanceOf[TyStm].t == TyTuple(I16, TyTuple())
      }),
      "the first stage of one branch should contain only i16"
    )

    // Effective simplification (valid branch)
    assert(
      zip.producers.exists({ case (x, _) =>
        x.typ.asInstanceOf[TyStm].t == TyTuple(TyTuple(), TyBool)
      }),
      "the second stage of one branch should contain only bool"
    )
    val validStep2 = zip.producers
      .collectFirst({
        case (x, (s, _))
            if x.typ.asInstanceOf[TyStm].t == TyTuple(TyTuple(), TyBool) =>
          s
      })
      .get
      .asInstanceOf[StmBuild]
    assert(
      validStep2.producers.exists({ case (x, _) =>
        x.typ.asInstanceOf[TyStm].t == TyTuple(TyTuple(), TyBool)
      }),
      "the first stage of one branch should contain only bool"
    )
  }
}
