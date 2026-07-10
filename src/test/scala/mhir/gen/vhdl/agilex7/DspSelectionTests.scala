package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.gen.vhdl.GenStmBuild
import mhir.ir._
import mhir.optimize.cost.SimpleDelayCostModel
import mhir.optimize.{
  EnabledBinOpTreeBalancingPass,
  StmOutputScheduler,
  PartialEvalPass => PE
}
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable.ListMap

class DspSelectionTests extends AnyFunSuite {

  private val pass = DspSelection(
    StmOutputScheduler(
      EnabledBinOpTreeBalancingPass,
      SimpleDelayCostModel(madd = true)
    )
  )

  // Check that the order of the terms I'm creating here match the order you'd
  // actually get after optimization
  test("SumOrder") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val z = Param("z")(U8)
    val e = Sum(x, Prod(y, z)())().tchk()
    assert(PE.partialEval(e) == e)
  }

  private val i44 = TySInt(44)
  private val u44 = TyUInt(44)
  private val primes = Seq(3, 5, 7, 11, 13, 17)
  for (depth <- Seq(1, 2, 3, 4, 5, 6)) {
    for ((inTyp, outTyp) <- Seq((I16, i44), (U16, u44))) {

      test(s"Systolic:$inTyp->$outTyp,$depth-tap,0 input pipeline stages") {
        val original = {
          val p1 = Param("p1")(TyStm(TyVec(inTyp, depth), 8))
          val p2 = Param("p2")(TyStm(TyVec(inTyp, depth), 8))
          val stages = (0 until depth).map(i => Param(s"stage${i}_out")(outTyp))
          GenStmBuild(
            data = stages(depth - 1).tchk(),
            valid = True,
            accumulators = stages.zipWithIndex
              .map({ case (x, i) =>
                val next = {
                  val prod = Prod(
                    PadTo(VecAccess(StmData(p1)(), 0)(), 44)(),
                    PadTo(VecAccess(StmData(p2)(), 0)(), 44)()
                  )()
                  val sum = if (i == 0) {
                    prod
                  } else {
                    Sum(stages(i - 1), prod)()
                  }
                  val out = if (i == depth - 1) {
                    TruncateTo(ARShift(sum, 15)(), 18)()
                  } else {
                    sum
                  }
                  out.tchk()
                }
                x -> (Undefined(outTyp), next)
              })
              .toMap,
            producers = Map(
              p1 -> (p1, True),
              p2 -> (p2, True)
            ),
            intermediates = ListMap()
          )
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
          Map(
            // Start of the chain
            s"agilex7_mac1(signed=$signed, pipeline=0, chainin=false, chainout=${depth >= 3})"
              -> numSingles,
            // Start of the chain
            s"agilex7_mac2(signed=$signed, pipeline=0, chainin=false, chainout=true)"
              -> numDoublesAtStart,
            // Middle of the chain
            s"agilex7_mac2(signed=$signed, pipeline=0, chainin=true, chainout=true)"
              -> numDoublesAtMiddle,
            // End of the chain
            s"agilex7_mac2(signed=$signed, pipeline=0, chainin=true, chainout=false)"
              -> numDoublesAtEnd,
            // Simultaneously at the start and end of the chain
            s"agilex7_mac2(signed=$signed, pipeline=0, chainin=false, chainout=false)"
              -> numDoublesAtStartAndEnd
          ).filter({ case (_, n) => n > 0 })
        }
        assert(actualStats == expectedStats)
      }

      test(
        s"Systolic:$inTyp->$outTyp,$depth-tap,0 input pipeline stages,const"
      ) {
        val original = {
          val p1 = Param("p1")(TyStm(TyVec(inTyp, depth), 8))
          val stages = (0 until depth).map(i => Param(s"stage${i}_out")(outTyp))
          GenStmBuild(
            data = stages(depth - 1).tchk(),
            valid = True,
            accumulators = stages.zipWithIndex
              .map({ case (x, i) =>
                val next = {
                  val prod = Prod(
                    C(primes(i))(outTyp),
                    PadTo(VecAccess(StmData(p1)(), 0)(), 44)()
                  )()
                  val sum = if (i == 0) {
                    prod
                  } else {
                    Sum(stages(i - 1), prod)()
                  }
                  val out = if (i == depth - 1) {
                    TruncateTo(ARShift(sum, 15)(), 18)()
                  } else {
                    sum
                  }
                  out.tchk()
                }
                x -> (Undefined(outTyp), next)
              })
              .toMap,
            producers = Map(
              p1 -> (p1, True)
            ),
            intermediates = ListMap()
          )
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
          Map(
            // Start of the chain
            s"agilex7_mac1(signed=$signed, pipeline=0, chainin=false, chainout=${depth >= 3})"
              -> numSingles,
            // Start of the chain
            s"agilex7_mac2(signed=$signed, pipeline=0, chainin=false, chainout=true)"
              -> numDoublesAtStart,
            // Middle of the chain
            s"agilex7_mac2(signed=$signed, pipeline=0, chainin=true, chainout=true)"
              -> numDoublesAtMiddle,
            // End of the chain
            s"agilex7_mac2(signed=$signed, pipeline=0, chainin=true, chainout=false)"
              -> numDoublesAtEnd,
            // Simultaneously at the start and end of the chain
            s"agilex7_mac2(signed=$signed, pipeline=0, chainin=false, chainout=false)"
              -> numDoublesAtStartAndEnd
          ).filter({ case (_, n) => n > 0 })
        }
        assert(actualStats == expectedStats)
      }

    }
  }
}
