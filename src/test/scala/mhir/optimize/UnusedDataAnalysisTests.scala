package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class UnusedDataAnalysisTests extends AnyFunSuite {

  test("(used, (used, (used, used)), ((unused, unused), used))") {
    val typ = TyTuple(
      TyTuple(U8, TyBool),
      TyTuple(I16, (I16, I16)),
      TyTuple(TyTuple(I16, I16), I16)
    )
    val p = Param("p")(TyStm(typ, -1))
    val e = {
      val s = Param("s")(TyStm(typ, 10))
      val sum = Param("sum")(I16)
      val prod = Param("prod")(I16)
      val min = Param("min")(I16)
      StmBuild(
        10,
        Tuple(
          StmData(p)().__0,
          sum,
          prod,
          min
        )(),
        StmData(p)().__2.__1 geq C(0)(I16),
        Map[Param, (Expr, Expr)](
          p -> (s, True),
          sum -> (C(0)(I16), Sum(sum, StmData(p)().__1.__0)()),
          prod -> (C(0)(I16), Prod(prod, StmData(p)().__1.__1.__0)()),
          min -> (C(0)(I16), Sum(sum, StmData(p)().__1.__1.__1)())
        )
      )().tchk().asInstanceOf[StmBuild]
    }
    val actual = UnusedDataAnalysis(p).findUnused(e)
    val expected = SomeUnused(AllUsed, AllUsed, SomeUnused(AllUnused, AllUsed))
    assert(actual == expected)
  }
}
