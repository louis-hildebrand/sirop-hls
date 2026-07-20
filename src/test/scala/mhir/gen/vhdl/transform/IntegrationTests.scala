package mhir.gen.vhdl
package transform

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar.{ExprLowering, StmRepeat}
import mhir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

class IntegrationTests extends AnyFunSuite {

  test("StmRepeatUsesVecWriteAccumulator") {
    val options = VhdlGeneratorOptions()
    val original = {
      val p = Param("p")(TyStm(U8, 8))
      val f = Function(p, StmRepeat(p, C(3)())())().tchk().lower
      FlattenPipeline(f, options)
    }
    val transformed = ApplyTransformations(original, options)
    assert(transformed.sbuilds.exists({ case StmBuildNode(_, s, _) =>
      s.accumulators.exists({
        case (_, _: VecWriteAccumulator) => true
        case _                           => false
      })
    }))
  }
}
