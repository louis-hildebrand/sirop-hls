package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.testing.ParamStore
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class StmOutputSchedulerTests extends AnyFunSuite {

  private val x = ParamStore("x")
  private val y = ParamStore("y")
  private val z = ParamStore("z")

  private val pass = StmOutputScheduler(EnabledBinOpTreeBalancingPass)

  /** Simple example where computation is all done in the producer.
    */
  test("x + y") {
    val e = (x(U8) + y(U8)).tchk().lower()
    val actual = pass.schedule(e)
    val expected = InProducer(e)
    assert(actual == expected)
  }

  /** Simple example where computation is split between producer and consumer.
    */
  test("x * y + z") {
    val e = Sum(Prod(x(U8), y(U8))(), z(U8))().tchk().lower()
    val actual = pass.schedule(e)
    val expected = {
      val tmp0 = Param("tmp0")(U8)
      val tmp1 = Param("tmp1")(U8)
      InConsumer(
        Sum(tmp0, tmp1)(),
        Map(tmp0 -> Prod(x(U8), y(U8))(), tmp1 -> z(U8))
      )
    }
    assert(actual == expected)
  }

  /** Constants should be inlined instead of being sent from producer to
    * consumer.
    */
  test("(3 * x + 1, false, true, 1/4)") {
    val e = Tuple(
      Sum(Prod(C(3)(U8), x(U8))(), C(1)(U8))(),
      False,
      True,
      FixCst(32)(TyFix(U8, 7))
    )().tchk().lower()
    val actual = pass.schedule(e)
    val expected = {
      val tmp = Param("tmp")(U8)
      InConsumer(
        Tuple(Sum(tmp, C(1)(U8))(), False, True, FixCst(32)(TyFix(U8, 7)))()
          .tchk(),
        Map(tmp -> Prod(C(3)(U8), x(U8))())
      )
    }
    assert(actual == expected)
  }

  /** Expressions sent from producer to consumer should be deduplicated.
    */
  test("(3 * x + y, 5 * x + y)") {
    val e = Tuple(
      Sum(Prod(C(3)(U16), x(U16))(), y(U16))(),
      Sum(Prod(C(5)(U16), x(U16))(), y(U16))()
    )().tchk().lower()
    val actual = pass.schedule(e)
    val expected = {
      val tmp0 = Param("tmp0")(U16)
      val tmp1 = Param("tmp1")(U16)
      val tmp2 = Param("tmp2")(U16)
      InConsumer(
        Tuple(Sum(tmp0, tmp1)(), Sum(tmp2, tmp1)())().tchk(),
        Map(
          tmp0 -> Prod(C(3)(U16), x(U16))().tchk(),
          tmp1 -> y(U16),
          tmp2 -> Prod(C(5)(U16), x(U16))().tchk()
        )
      )
    }
    assert(actual == expected)
  }

  test("VecBuild") {
    val n = 8
    val v = Param("v")(TyVec(U8, n))
    val e = VecBuild(
      n,
      U8 ::+ (i => Sum(Prod(VecAccess(v, i)(), x(U8))(), y(U8))())
    )().tchk().lower()
    val actual = pass.schedule(e)
    val expected = {
      val tmp0 = Param("tmp0")(U8)
      val tmp1 = Param("tmp1")(TyVec(U8, n))
      InConsumer(
        VecBuild(n, U8 ::+ (i => Sum(VecAccess(tmp1, i)(), tmp0)()))().tchk(),
        Map(
          tmp0 -> y(U8),
          tmp1 -> VecBuild(n, U8 ::+ (i => Prod(VecAccess(v, i)(), x(U8))()))()
            .tchk()
        )
      )
    }
    assert(actual == expected)
  }

  test("NestedVecBuild") {
    val n = 8
    val m = 16
    val v = Param("v")(TyVec(TyVec(U8, m), n))
    val e = VecBuild(
      n,
      U8 ::+ (i =>
        VecBuild(
          m,
          U8 ::+ (j =>
            Sum(Prod(VecAccess(VecAccess(v, i)(), j)(), x(U8))(), y(U8))()
          )
        )()
      )
    )().tchk().lower()
    val actual = pass.schedule(e)
    val expected = {
      val tmp0 = Param("tmp0")(U8)
      val tmp1 = Param("tmp1")(TyVec(TyVec(U8, m), n))
      InConsumer(
        VecBuild(
          n,
          U8 ::+ (i =>
            VecBuild(
              m,
              U8 ::+ (j => Sum(VecAccess(VecAccess(tmp1, i)(), j)(), tmp0)())
            )()
          )
        )().tchk(),
        Map(
          tmp0 -> y(U8),
          tmp1 -> VecBuild(
            n,
            U8 ::+ (i =>
              VecBuild(
                m,
                U8 ::+ (j => Prod(VecAccess(VecAccess(v, i)(), j)(), x(U8))())
              )()
            )
          )().tchk()
        )
      )
    }
    assert(actual == expected)
  }

  test("FunCall:AllInProducer") {
    val e = FunCall(
      Function(y(U8), Sum(y(U8), z(U8))())(),
      Sum(x(U8), C(1)(U8))()
    )().tchk().lower()
    val actual = pass.schedule(e)
    val expected = InProducer(e)
    assert(actual == expected)
  }

  test("FunCall:ArgInProducerBodyInConsumer") {
    // (let x = 3 * y in x +% z)
    //   + (let x = 5 * z in x -% 1)
    val e = Sum(
      FunCall(
        Function(x(U8), WrappingSum(x(U8), z(U8))())(),
        Prod(C(3)(U8), y(U8))()
      )(),
      FunCall(
        Function(x(U8), WrappingDiff(x(U8), C(1)(U8))())(),
        Prod(C(5)(U8), z(U8))()
      )()
    )().tchk().lower()
    val actual = pass.schedule(e)
    val expected = {
      val tmp0 = Param("tmp0")(U8)
      val tmp1 = Param("tmp1")(U8)
      val tmp2 = Param("tmp2")(U8)
      InConsumer(
        Sum(WrappingSum(tmp1, tmp0)(), WrappingDiff(tmp2, C(1)(U8))())().tchk(),
        Map(
          tmp0 -> z(U8),
          tmp1 -> Prod(C(3)(U8), y(U8))().tchk(),
          tmp2 -> Prod(C(5)(U8), z(U8))().tchk()
        )
      )
    }
    assert(actual == expected)
  }

  test("FunCall:ArgInConsumer") {
    val e = FunCall(
      Function(x(U8), Sum(x(U8), z(U8))())(),
      Sum(Prod(x(U8), y(U8))(), C(1)(U8))()
    )().tchk().lower()
    val actual = pass.schedule(e)
    val expected = {
      val tmp = Param("tmp")(U8)
      InConsumer(
        FunCall(Function(x(U8), Sum(x(U8), z(U8))())(), Sum(tmp, C(1)(U8))())()
          .tchk(),
        Map(tmp -> Prod(x(U8), y(U8))())
      )
    }
    assert(actual == expected)
  }

  /** It's no good to move all the computation to the consumer and nothing in
    * the producer. If a given expression doesn't fit anywhere, it's better to
    * leave it in the producer.
    */
  test("ManyInputProd") {
    val e = Prod(x(U8), y(U8), z(U8))().tchk()
    val actual = StmOutputScheduler(DisabledBinOpTreeBalancingPass).schedule(e)
    val expected = InProducer(e)
    assert(actual == expected)
  }
}
