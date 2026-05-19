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

  test("(used, (used, (used, used)), ((unused, unused), used))") {
    val typ = TyTuple(
      TyTuple(U8, TyBool),
      TyTuple(I16, (I16, I16)),
      TyTuple(TyTuple(I16, I16), I16)
    )
    val original = {
      val n = 10
      val p = Param("p")(TyStm(typ, -1))
      val input = SimpleMap(
        SimpleCount(C(n)(U8)),
        x => {
          val xI16 = PadTo(ToSigned(x)(), 16)()
          Tuple(
            Tuple(x, Mod(x, C(2)(U8))() equ C(0)(U8))(),
            Tuple(
              Sum(xI16, C(2)(I16))(),
              Tuple(Sum(xI16, C(3)(I16))(), Sum(xI16, C(4)(I16))())()
            )(),
            Tuple(
              Tuple(Sum(xI16, C(5)(I16))(), Sum(xI16, C(6)(I16))())(),
              Sum(xI16, C(7)(I16))()
            )()
          )()
        }
      ).tchk()
      val sum = Param("sum")(I16)
      val prod = Param("prod")(I16)
      val min = Param("min")(I16)
      StmBuild(
        n,
        Tuple(
          StmData(p)().__0,
          sum,
          prod,
          min
        )(),
        StmData(p)().__2.__1 geq C(0)(I16),
        Map[Param, (Expr, Expr)](
          p -> (input, True),
          sum -> (C(0)(I16), Sum(sum, StmData(p)().__1.__0)()),
          prod -> (C(0)(I16), Prod(prod, StmData(p)().__1.__1.__0)()),
          min -> (C(0)(I16), Sum(sum, StmData(p)().__1.__1.__1)())
        )
      )().tchk().asInstanceOf[StmBuild]
    }
    val actual = pass.removeUnusedData(original)

    // Correctness
    val expectedVal = mhir.eval.eval(original)
    val actualVal = mhir.eval.eval(actual)
    assert(actualVal == expectedVal)

    // Effective simplification
    val actualConsumer = actual.asInstanceOf[StmBuild]
    val (actualProducer, _) = actualConsumer.producers.head
    val TyStm(producerDataTyp, _) = actualProducer.typ
    val expectedTyp = TyTuple(
      TyTuple(U8, TyBool),
      TyTuple(I16, (I16, I16)),
      TyTuple(TyTuple(), I16)
    )
    assert(producerDataTyp == expectedTyp)
  }
}
