package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.delay.LatencyConcrete
import mhir.gen.vhdl.ir.FlattenPipeline
import mhir.gen.vhdl.transform.ApplyTransformations
import mhir.ir._
import mhir.optimize.{Optimizer, OptimizerOptions}
import mhir.sugar.StreamReplicator.StreamReplication
import mhir.sugar.{
  ExprLowering,
  SimpleMap,
  StmCst,
  StmMapDotCascaded,
  StmMapDotTree
}
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class DspSelectionTests extends AnyFunSuite {

  private val primes = Seq(3, 5, 7, 11, 13, 17)

  // ===== M18X18_SYSTOLIC =====================================================

  // TODO: Test that DSP selection does not happen at 45+ bits?
  // TODO: Test that DSP selection won't increase the number of multiplications?

  private val depths = Seq(1, 2, 5, 6)
  private val types = Seq((I16, I43), (U16, U43))
  private val pipelines = Seq(0, 1, 4)
  private val loops = Seq(Seq(), Seq(3), Seq(2, 4))

  for (depth <- depths) {
    for ((inTyp, outTyp) <- types) {
      for (pipeline <- pipelines) {
        for (loop <- loops) {
          for (const <- Seq(false, true)) {

            val name = s"Systolic:" +
              s"$inTyp->$outTyp," +
              s"$depth-tap," +
              s"$pipeline pipeline stages," +
              s"vec(${loop.mkString(",")})," +
              (if (const) "const" else "non-const")
            test(name) {
              val options = VhdlGeneratorOptions()
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
                val allParams = p1 match {
                  case p1: Param => Seq(p1, p2)
                  case _         => Seq(p2)
                }
                val sbuild = SimpleMap(
                  StmMapDotCascaded(p1, p2, pipeline, outTyp)().tchk(),
                  x =>
                    Tuple(
                      Tuple(TruncateTo(ARShift(x, 2)(), 32)(), True)(),
                      False
                    )()
                ).tchk().lower
                val sbuildReplicated =
                  loop.foldRight(sbuild)({ case (len, sbuild) =>
                    sbuild
                      .replicate(
                        C(len)().tchk(),
                        Param("i")(U32),
                        allParams.toSet
                      )
                      .tchk()
                  })
                val optimizer = Optimizer(
                  OptimizerOptions
                    .all(
                      assumeThroughputsMatch = false,
                      maxLetStmBufSize = None
                    )
                    .copy(madd = true),
                  handshake = true,
                  headByParam = Map(),
                  latencyByParam = allParams.map(_ -> LatencyConcrete(0)).toMap
                )
                val unoptimizedFunc = allParams.foldRight(sbuildReplicated)({
                  case (x, e) => Function(x, e)().tchk()
                })
                val (optimizedFunc, delayUpdates) =
                  optimizer.optimize(unoptimizedFunc)
                assert(
                  delayUpdates.isEmpty,
                  "there should be no delay updates, since every param was already assigned a concrete delay"
                )
                FlattenPipeline(optimizedFunc, options)
              }
              val actual = {
                val pipe = ApplyTransformations(original, options)
                assert(pipe.lets.isEmpty)
                assert(pipe.sbuilds.size == 1)
                pipe.sbuilds.head.s
              }

              val actualStats = IpStats(actual)
              val expectedStats = {
                val signed = inTyp.isInstanceOf[TySInt]
                val numSingles = depth % 2
                val numDoubles = depth / 2
                val numDoublesAtStart = if (depth >= 2) 1 else 0
                // TODO: Is it important to disable the chainout port when it's not used?
                // val numDoublesAtEnd = if (depth % 2 == 0 && depth >= 4) 1 else 0
                // val numDoublesAtStartAndEnd = if (depth == 2) 1 else 0
                val numDoublesAtEnd = 0
                val numDoublesAtStartAndEnd = 0
                val numDoublesAtMiddle =
                  numDoubles - numDoublesAtStart - numDoublesAtEnd - numDoublesAtStartAndEnd
                val numSinglesAtEnd = if (depth % 2 == 1 && depth >= 3) 1 else 0
                val numSinglesAtStartAndEnd = if (depth == 1) 1 else 0
                assert(numSinglesAtEnd + numSinglesAtStartAndEnd == numSingles)
                val cappedPipeline = math.min(3, pipeline)
                val prefix = loop.foldRight("")({ case (n, acc) =>
                  s"for(${n - 1}) " + acc
                })
                Map(
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
                    -> numDoublesAtStartAndEnd,
                  // End of the chain
                  s"agilex7_mac1(signed=$signed, chainin=true)" -> numSinglesAtEnd,
                  // Simultaneously at the start and end of the chain
                  s"agilex7_mac1(signed=$signed, chainin=false)" -> numSinglesAtStartAndEnd
                )
                  .filter({ case (_, n) => n > 0 })
                  .map({ case (k, v) => (prefix + k) -> v })
              }
              assert(actualStats == expectedStats)
            }

          }
        }
      }
    }
  }

  // ===== M18X18_SUMOF2 =======================================================

  for (depth <- depths) {
    for ((inTyp, outTyp) <- Seq((I16, I38), (U16, U38))) {
      for (pipeline <- pipelines) {
        for (loop <- loops) {
          for (const <- Seq(false, true)) {

            val name = s"Tree:" +
              s"$inTyp->$outTyp," +
              s"$depth-tap," +
              s"vec(${loop.mkString(",")})," +
              s"$pipeline pipeline stages," +
              (if (const) "const" else "non-const")
            test(name) {
              val options = VhdlGeneratorOptions()
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
                val allParams = p1 match {
                  case p1: Param => Seq(p1, p2)
                  case _         => Seq(p2)
                }
                val sbuild = SimpleMap(
                  StmMapDotTree(p1, p2, pipeline)().tchk(),
                  x =>
                    Tuple(
                      Tuple(TruncateTo(ARShift(x, 2)(), 32)(), True)(),
                      False
                    )()
                ).tchk().lower
                val sbuildReplicated =
                  loop.foldRight(sbuild)({ case (len, sbuild) =>
                    sbuild
                      .replicate(
                        C(len)().tchk(),
                        Param("i")(U32),
                        allParams.toSet
                      )
                      .tchk()
                  })
                val optimizer = Optimizer(
                  OptimizerOptions
                    .all(
                      assumeThroughputsMatch = false,
                      maxLetStmBufSize = None
                    )
                    .copy(madd = true),
                  handshake = true,
                  headByParam = Map(),
                  latencyByParam = allParams.map(_ -> LatencyConcrete(0)).toMap
                )
                val unoptimizedFunc = allParams.foldRight(sbuildReplicated)({
                  case (x, e) => Function(x, e)().tchk()
                })
                val (optimizedFunc, delayUpdates) =
                  optimizer.optimize(unoptimizedFunc)
                assert(
                  delayUpdates.isEmpty,
                  "there should be no delay updates, since every param was already assigned a concrete delay"
                )
                FlattenPipeline(optimizedFunc, options)
              }
              val actual = {
                val pipe = ApplyTransformations(original, options)
                assert(pipe.lets.isEmpty)
                pipe.sbuilds.head.s
              }

              val actualStats = IpStats(actual)
              val prefix = loop.foldRight("")({ case (n, acc) =>
                s"for(${n - 1}) " + acc
              })
              val expectedStats = {
                val signed = inTyp.isInstanceOf[TySInt]
                val cappedPipeline = math.min(3, pipeline)
                Map(
                  s"agilex7_madd(signed=$signed, pipeline=$cappedPipeline)" -> (depth / 2),
                  // Leftovers
                  s"agilex7_mac1(signed=$signed, chainin=false)" -> (depth % 2)
                )
                  .filter({ case (_, n) => n > 0 })
                  .map({ case (k, v) => (prefix + k) -> v })
              }
              assert(actualStats == expectedStats)
            }
          }
        }
      }
    }
  }
}
