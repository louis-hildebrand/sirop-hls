package mhir.sugar

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar.StreamReplicator.StreamReplication
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class StreamReplicationTests extends AnyFunSuite {

  test("LetStm:BoundVariableReplicated") {
    val n = 7
    val m = 4
    val input = Param("input")(TyStm(U8, n))
    val original = {
      // let stm x = input in StmZip(StmMap(x, _ % 2 == 0), x)
      val x = Param("x")(TyStm(U8, n))
      val map = {
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          StmData(s)() % 2 === 0,
          True,
          Map[Param, (Expr, Expr)](
            s -> (x, True)
          )
        )()
      }
      val zipMap = {
        val s0 = Param("s0")(TyStm(TyBool, n))
        val s1 = Param("s1")(TyStm(U8, n))
        StmBuild(
          n,
          Tuple(StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0 -> (map, True),
            s1 -> (x, True)
          )
        )()
      }
      LetStm(1, x, input, zipMap)().tchk().lower()
    }
    val i = Param("i")(U8)
    val replicated =
      original.replicate(m = C(m)(U8), i = i, varsToReplicate = Set(input))

    // Check correct behaviour
    val examples = Seq(
      (
        StmLiteral(
          (0 until n).map(t =>
            VecLiteral((0 until m).map(i => C(m * t + i)(U8)): _*)()
          ): _*
        )().tchk(),
        StmLiteral(
          (0 until n).map(t =>
            VecLiteral(
              (0 until m).map(i => {
                val x = m * t + i
                Tuple(if (x % 2 == 0) True else False, C(x)(U8))()
              }): _*
            )()
          ): _*
        )().tchk()
      )
    )
    for ((in, expectedVal) <- examples) {
      val actualVal = mhir.eval.eval(replicated.subPreserveType(input -> in))
      assert(actualVal == expectedVal)
    }
  }

  test("LetStm:BoundVariableNotReplicated") {
    val n = 4
    val m = 5
    val input = Param("input")(TyStm(I32, n))
    val original = {
      // let stm ctr = StmCount(n) in StmZip(ctr, input)
      val ctr = Param("ctr")(TyStm(U8, n))
      val count = {
        val a = Param("a")(U8)
        StmBuild(
          n,
          a,
          True,
          Map[Param, (Expr, Expr)](
            a -> (C(0)(U8), Sum(C(1)(U8), a)())
          )
        )()
      }
      val zip = {
        val s0 = Param("s0")(TyStm(U8, n))
        val s1 = Param("s1")(TyStm(I32, n))
        StmBuild(
          n,
          Tuple(StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0 -> (ctr, True),
            s1 -> (input, True)
          )
        )()
      }
      LetStm(1, ctr, count, zip)().tchk().lower()
    }
    val i = Param("i")(U8)
    val replicated =
      original.replicate(m = C(m)(U16), i = i, varsToReplicate = Set(input))

    // Check correct behaviour
    val examples = Seq(
      (
        StmLiteral(
          (0 until n).map(t =>
            VecLiteral((0 until m).map(i => C(i - t)(I32)): _*)()
          ): _*
        )().tchk(),
        StmLiteral(
          (0 until n).map(t =>
            VecLiteral(
              (0 until m).map(i => Tuple(C(t)(U8), C(i - t)(I32))()): _*
            )()
          ): _*
        )().tchk()
      )
    )
    for ((in, expectedVal) <- examples) {
      val actualVal = mhir.eval.eval(replicated.subPreserveType(input -> in))
      assert(actualVal == expectedVal)
    }

    // It is unnecessary to replicate the shared stream in this case
    val expectedTyp = TyStm(U8, n)
    val actualTyp = replicated.asInstanceOf[LetStm].x.typ
    assert(actualTyp == expectedTyp)
  }

  test("FreeVariable:Stm[U8, 5]") {
    val n = 5
    val m = 4
    val s = Param("s", -1)(TyStm(TyVec(U8, 2), n))
    val i = Param("i")(U8)
    val replicated = s.replicate(m = C(m)(U8), i = i, varsToReplicate = Set())
    val sVal = StmLiteral(
      (0 until n).map(t => VecLiteral(C(t)(U8), C(t * t)(U8))()): _*
    )().tchk()
    val expected = StmLiteral(
      (0 until n).map(t =>
        VecLiteral(
          (0 until m).map(_ => VecLiteral(C(t)(U8), C(t * t)(U8))()): _*
        )()
      ): _*
    )().tchk()
    val actual = mhir.eval.eval(replicated.subPreserveType(s -> sVal))
    assert(actual == expected)
  }
}
