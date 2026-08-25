package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

class StmSourceFusionPassTests extends AnyFunSuite {

  private val pass = StmSourceFusionPass(StmBuildSimplifier())

  test("ZipWithThree") {
    val n = 8
    val input1 = Param("input1")(TyStm(I16, n))
    val input2 = Param("input2")(TyStm(I16, n))
    val original = {
      val x = Param("x")(TyStm(U8, n))
      LetStm(
        C(0)(),
        x,
        LetStm(C(0)(), x, StmCst(n, C(3)(U8))(), x)(),
        SimpleZip(SimpleZip(input1, x), SimpleZip(input2, x))
      )().tchk().lower
    }
    val fused = pass.fuse(original)

    // Correctness
    val inputs = Map(
      input1 -> StmRange(n, C(3)(I16), C(-1)(I16))().tchk().lower,
      input2 -> StmRange(n, C(-4)(I16), C(1)(I16))().tchk().lower
    )
    val expectedVal = mhir.eval.eval(original, inputs = inputs)
    val actualVal = mhir.eval.eval(fused, inputs = inputs)
    assert(actualVal == expectedVal)

    // Successful fusion: there shouldn't be any more sbuild instances with
    // no producers
    assert(
      !fused.contains(e =>
        e match {
          case s: StmBuild => s.producers.isEmpty
          case _           => false
        }
      )
    )

    // Not too much fusion: the top-level zip should be left un-fused
    val fusedSbuild = fused.asInstanceOf[StmBuild]
    assert(fusedSbuild.producers.size == 2)
    assert(fusedSbuild.producers.forall({ case (_, (stm, _, _)) =>
      stm.isInstanceOf[StmBuild]
    }))
  }
}
