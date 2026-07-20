package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.ir._
import mhir.optimize.cost.SimpleDelayCostModel
import mhir.optimize.{
  EnabledBinOpTreeBalancingPass,
  Optimizer,
  OptimizerOptions,
  StmOutputScheduler
}
import mhir.sugar.{ExprLowering, MulAddCascaded, StmCst}
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class DspSelectionTests extends AnyFunSuite {

  private val optimizer: Optimizer = Optimizer(
    OptimizerOptions
      .all(
        assumeThroughputsMatch = false,
        maxLetStmBufSize = None
      )
      .copy(madd = true),
    handshake = true
  )
  private def simplify(e: Expr): Expr = {
    optimizer.optimize(e)
  }

  private val pass = DspSelection(
    StmOutputScheduler(
      EnabledBinOpTreeBalancingPass,
      SimpleDelayCostModel(madd = true)
    )
  )

  private val primes = Seq(3, 5, 7, 11, 13, 17)
  for (depth <- Seq(1, 2, 3, 4, 5, 6)) {
    for ((inTyp, outTyp) <- Seq((I16, I44), (U16, U44))) {
      for (pipeline <- Seq(0, 1, 2, 3, 4)) {
        for (const <- Seq(false, true)) {

          val name =
            s"Systolic:$inTyp->$outTyp,$depth-tap,$pipeline pipeline stages," +
              (if (const) "const" else "non-const")
          test(name) {
            val original = {
              val p1 = if (const) {
                StmCst(
                  8,
                  VecLiteral(primes.take(depth).map(C(_)(inTyp)): _*)()
                )().tchk().lower
              } else {
                Param("p1")(TyStm(TyVec(inTyp, depth), 8))
              }
              val p2 = Param("p2")(TyStm(TyVec(inTyp, depth), 8))
              val sbuild = MulAddCascaded(p1, p2, pipeline)().tchk().lower
              val f = p1 match {
                case p1: Param =>
                  simplify(Function(p1, Function(p2, sbuild)())().tchk())
                case _ =>
                  simplify(Function(p2, sbuild)().tchk())
              }
              val streamPipeline = FlattenPipeline(f, VhdlGeneratorOptions())
              assert(streamPipeline.sbuilds.size == 1)
              assert(streamPipeline.lets.isEmpty)
              streamPipeline.sbuilds.head.s
            }
            val actual = pass.apply(original)

            val actualStats = IpStats(actual)
            val expectedStats = {
              val signed = inTyp.isInstanceOf[TySInt]
              val numSingles = depth % 2
              val numDoubles = depth / 2
              val numDoublesAtStart = if (depth % 2 == 0 && depth >= 4) 1 else 0
              val numDoublesAtEnd = if (depth >= 3) 1 else 0
              val numDoublesAtStartAndEnd = if (depth == 2) 1 else 0
              val numDoublesAtMiddle =
                numDoubles - numDoublesAtStart - numDoublesAtEnd - numDoublesAtStartAndEnd
              val cappedPipeline = math.min(3, pipeline)
              Map(
                // Start of the chain
                // TODO: Merge in pipeline registers for agilex_mac1 too?
                s"agilex7_mac1(signed=$signed, pipeline=0, chainin=false, chainout=${depth >= 3})"
                  -> numSingles,
                // Start of the chain
                s"agilex7_mac2(signed=$signed, pipeline=$cappedPipeline, chainin=false, chainout=true)"
                  -> numDoublesAtStart,
                // Middle of the chain
                s"agilex7_mac2(signed=$signed, pipeline=$cappedPipeline, chainin=true, chainout=true)"
                  -> numDoublesAtMiddle,
                // End of the chain
                s"agilex7_mac2(signed=$signed, pipeline=$cappedPipeline, chainin=true, chainout=false)"
                  -> numDoublesAtEnd,
                // Simultaneously at the start and end of the chain
                s"agilex7_mac2(signed=$signed, pipeline=$cappedPipeline, chainin=false, chainout=false)"
                  -> numDoublesAtStartAndEnd
              ).filter({ case (_, n) => n > 0 })
            }
            assert(actualStats == expectedStats)
          }

        }
      }
    }
  }
}
