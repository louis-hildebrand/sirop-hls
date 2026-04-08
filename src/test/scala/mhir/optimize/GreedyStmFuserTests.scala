package mhir.optimize

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.sugar._
import org.scalatest.funsuite.AnyFunSuite

class GreedyStmFuserTests extends AnyFunSuite {

  private val fusionPass = new GreedyStmFusionPass(StmBuildSimplifier())

  /* Map(+3) is clearly better than Map(+1) |> Map(+2).
   */
  test("ShouldFuse:Map(+1) |> Map(+2)") {
    val n = 16
    val input = Param("input")(TyStm(U8, n))
    val mapPlus = (input: Expr, k: Expr) => SimpleMap(input, x => Sum(k, x)())
    val original = mapPlus(mapPlus(input, C(1)(U8)), C(2)(U8))
    val actual = fusionPass.fuse(original)

    // Correctness
    val exampleIn = StmLiteral((0 until n).map(C(_)(U8)): _*)().tchk()
    val originalVal = mhir.ir.eval(original.subPreserveType(input -> exampleIn))
    val actualVal = mhir.ir.eval(actual.subPreserveType(input -> exampleIn))
    assert(actualVal == originalVal)

    // Successful fusion
    val expected = mapPlus(input, C(3)(U8))
    assert(actual == expected)
  }

  /* The `VecAccess` in the consumer cancels out the `VecBuild` in the
   * producer. There's no apparent drawback to fusing here.
   */
  test("ShouldFuse:Map(VecBuild(1, ...)) |> Map(VecAccess(0))") {
    val n = 8
    val original = {
      val vecStm = {
        val a = Param("a")(I16)
        StmBuild(
          n,
          VecBuild(1, U8 ::+ (_ => a))(),
          True,
          Map[Param, (Expr, Expr)](
            a -> (C(-4)(I16), Sum(C(2)(I16), a)())
          )
        )().tchk()
      }
      val vecAccessStm = {
        val s = Param("s")(TyStm(TyVec(I16, 1), -1))
        StmBuild(
          n,
          VecAccess(StmData(s)(), 0)(),
          True,
          Map[Param, (Expr, Expr)](s -> (vecStm, True))
        )().tchk()
      }
      vecAccessStm.lower()
    }
    val actual = fusionPass.fuse(original)

    // Correctness
    val originalVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(actual)
    assert(actualVal == originalVal)

    // Successful fusion
    val expected = {
      val a = Param("a")(I16)
      StmBuild(
        n,
        a,
        True,
        Map[Param, (Expr, Expr)](
          a -> (C(-4)(I16), Sum(C(2)(I16), a)())
        )
      )().tchk()
    }
    assert(actual == expected)
  }

  /* Test that fusion works even with multiple consumers and when the consumers
   * themselves have consumers.
   */
  test("ShouldFuse:Zip(Counter, input, Counter)") {
    val n = 7
    val input = Param("input")(TyStm(I8, n))
    val original = {
      val count = {
        val a = Param("a")(U8)
        StmBuild(
          n,
          a,
          True,
          Map[Param, (Expr, Expr)](a -> (C(0)(U8), Sum(C(1)(U8), a)()))
        )().tchk()
      }
      val countPlusOne = {
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          Sum(C(1)(U8), StmData(s)())(),
          True,
          Map[Param, (Expr, Expr)](
            s -> (count, True)
          )
        )().tchk()
      }
      val zip = {
        val s0 = Param("s0")(TyStm(U8, -1))
        val s1 = Param("s1")(TyStm(I8, -1))
        val s2 = Param("s2")(TyStm(U8, -1))
        StmBuild(
          n,
          Tuple(StmData(s0)(), StmData(s1)(), StmData(s2)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0 -> (count, True),
            s1 -> (input, True),
            s2 -> (countPlusOne, True)
          )
        )().tchk()
      }
      zip.lower()
    }
    val actual = fusionPass.fuse(original)

    // Correctness
    val exampleIn = StmLiteral(
      (0 until n).map(x => -6 * (x + 1)).map(C(_)(I8)): _*
    )().tchk()
    val originalVal = mhir.ir.eval(original.subPreserveType(input -> exampleIn))
    val actualVal = mhir.ir.eval(actual.subPreserveType(input -> exampleIn))
    assert(actualVal == originalVal)

    // Successful fusion
    val expected = {
      val a = Param("a")(U8)
      val s = Param("s")(TyStm(I8, -1))
      StmBuild(
        n,
        Tuple(a, StmData(s)(), Sum(C(1)(U8), a)())(),
        True,
        Map[Param, (Expr, Expr)](
          a -> (C(0)(U8), Sum(C(1)(U8), a)()),
          s -> (input, True)
        )
      )().tchk()
    }
    assert(actual == expected)
  }

  /* Don't put two multiplications in the same cycle because then the design
   * would probably not meet the timing requirements.
   */
  test("ShouldNotFuse:TooMuchDelay") {
    val n = 8
    val input = Param("input")(TyStm(TyVec(U16, 4), n))
    val original = {
      val s2 = {
        val s = Param("s")(TyStm(TyVec(U16, 4), -1))
        StmBuild(
          n,
          VecBuild(
            2,
            U16 ::+ (i =>
              VecAccess(StmData(s)(), C(2)(U16) * i)()
                * VecAccess(StmData(s)(), C(2)(U16) * i + C(1)(U16))()
            )
          )(),
          True,
          Map[Param, (Expr, Expr)](
            s -> (input, True)
          )
        )().tchk()
      }
      val s1 = {
        val s = Param("s")(TyStm(TyVec(U16, 2), -1))
        StmBuild(
          n,
          VecAccess(StmData(s)(), 0)() * VecAccess(StmData(s)(), 1)(),
          True,
          Map[Param, (Expr, Expr)](
            s -> (s2, True)
          )
        )().tchk()
      }
      s1.lower()
    }
    val actual = fusionPass.fuse(original)

    // Correctness
    val exampleIn = StmLiteral(
      (0 until n).map(t =>
        VecLiteral((0 until 4).map(i => C(t + i)(U16)): _*)()
      ): _*
    )().tchk()
    val originalVal = mhir.ir.eval(original.subPreserveType(input -> exampleIn))
    val actualVal = mhir.ir.eval(actual.subPreserveType(input -> exampleIn))
    assert(actualVal == originalVal)

    // No fusion
    assert(actual == original)
  }
}
