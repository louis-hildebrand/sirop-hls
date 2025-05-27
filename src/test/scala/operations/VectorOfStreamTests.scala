package operations

import ir._
import org.scalatest.funsuite.AnyFunSuite

// TODO: Just combine these tests with the main VectorTests class?
class VectorOfStreamTests extends AnyFunSuite {
  test("VecBuild:StmRange") {
    val n = 3
    val m = 4
    val e = VecBuild(n, TyInt ::+ (i => StmRange(m, i, i)()))().tchk().lower()
    val expected = StmLiteral(
      (0 until m).map(j =>
        VecLiteral((0 until n).map(i => IntCst(i + j * i)): _*)()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("NestedVecBuild:StmRange") {
    val n = 3
    val m = 4
    val k = 5
    val e = VecBuild(
      n,
      TyInt ::+ (i => VecBuild(m, TyInt ::+ (j => StmRange(k, i, j)()))())
    )().tchk().lower()
    val expected = StmLiteral(
      (0 until k).map(t =>
        VecLiteral(
          (0 until n).map(i =>
            VecLiteral((0 until m).map(j => IntCst(i + j * t)): _*)()
          ): _*
        )()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecBuild:StmZip") {
    val n = 4
    val m = 3
    val e =
      VecBuild(
        n,
        TyInt ::+ (i => StmZip(StmCount(m)(), StmRange(m, i, i)())())
      )().tchk().lower()
    val expected = StmLiteral(
      (0 until m).map(j =>
        VecLiteral((0 until n).map(i => Tuple(j, i + j * i)()): _*)()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecBuild:StmZipFused") {
    val n = 4
    val m = 3
    val a1 = Param("a1")()
    val a2 = Param("a2")()
    val e = VecBuild(
      n,
      TyInt ::+ (i =>
        StmBuild(
          m,
          Tuple(a1, a2)(),
          True,
          Map[Param, (Expr, Expr)](a1 -> (0, a1 + 1), a2 -> (i, a2 + i))
        )()
      )
    )().tchk().lower()
    val expected = StmLiteral(
      (0 until m).map(j =>
        VecLiteral((0 until n).map(i => Tuple(j, i + j * i)()): _*)()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecBuild:StmSum") {
    val n = 5
    val m = 3
    val e = VecBuild(
      n,
      TyInt ::+ (i => StmFold(StmCst(m, i * i)(), 0, PlusFunction())())
    )().tchk().lower()
    val expected = StmLiteral(
      VecLiteral(
        (0 until n).map(i => IntCst((0 until m).map(_ => i * i).sum)): _*
      )()
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecAccess") {
    val n = 5
    val m = 4
    val idx = Param("idx")(TyInt)
    val e = VecAccess(
      VecBuild(n, TyInt ::+ (i => StmRange(m, i, i)()))(),
      idx
    )().tchk().lower()
    for (idxVal <- 0 until n) {
      val expected =
        StmLiteral((0 until m).map(t => IntCst(idxVal + t * idxVal)): _*)()
      val actual = ir.eval(Let(idx, idxVal, e)())
      assert(actual == expected)
    }
  }

  test("VecMap:StmMap(VecMap(StmMap))") {
    val n = 2
    val m = 3
    val k = 4
    val svs =
      StmCst(n, VecBuild(m, TyInt ::+ (i => StmRange(k, i - 1, i * 2)()))())()
    val e = StmMap(
      svs,
      TyVec(TyStm(TyInt, k), m) ::+ (vs =>
        VecMap(
          vs,
          TyStm(TyInt, k) ::+ (s => StmMap(s, TyInt ::+ (x => x + 42))())
        )()
      )
    )().tchk().lower()
    val expected =
      StmLiteral(
        (0 until n).flatMap(_ =>
          (0 until k).map(t1 =>
            VecLiteral(
              (0 until m).map(i => IntCst((i - 1 + i * 2 * t1) + 42)): _*
            )()
          )
        ): _*
      )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecMap:StmMap(VecMap(VecMap(StmMap)))") {
    val n = 2
    val m = 3
    val k = 4
    val p = 5
    val svvs = StmCst(
      n,
      VecBuild(
        m,
        TyInt ::+ (i => VecBuild(k, TyInt ::+ (j => StmRange(p, i, j)()))())
      )()
    )()
    val e = StmMap(
      svvs,
      TyVec(TyVec(TyStm(TyInt, p), k), m) ::+ (vvs =>
        VecMap(
          vvs,
          TyVec(TyStm(TyInt, p), k) ::+ (vs =>
            VecMap(
              vs,
              TyStm(TyInt, p) ::+ (s => StmMap(s, TyInt ::+ (x => x * x + 9))())
            )()
          )
        )()
      )
    )().tchk().lower()
    val expected =
      StmLiteral(
        (0 until n).flatMap(_ =>
          (0 until p).map(t1 =>
            VecLiteral(
              (0 until m).map(i =>
                VecLiteral((0 until k).map(j => {
                  val x = i + t1 * j
                  IntCst(x * x + 9)
                }): _*)()
              ): _*
            )()
          )
        ): _*
      )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecMap:VecMap(StmScan)") {
    val n = 5
    val m = 3
    val vs = VecBuild(n, TyInt ::+ (i => StmRange(m, i, 1)()))()
    val e = VecMap(
      vs,
      TyStm(TyInt, m) ::+ (s => StmScanInclusive(s, 0, PlusFunction())())
    )().tchk().lower()
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral((0 until n).map(i => IntCst((i to i + t).sum)): _*)()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecMap:VecMap(VecCount(n), i => StmRange(m, i, 1))") {
    val n = 2
    val m = 3
    val e = VecMap(
      VecBuild(n, TyInt ::+ (i => i))(),
      TyInt ::+ (i => StmRange(m, i, 1)())
    )().tchk().lower()
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral((0 until n).map(i => IntCst(i + t)): _*)()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("Vec2Stm") {
    val n = 4
    val m = 3
    val vs = VecBuild(n, TyInt ::+ (i => StmRange(m, 42, i)()))()
    val e = Vec2Stm(vs)().tchk().lower()
    val expected =
      StmLiteral(
        (0 until m).flatMap(i => (0 until n).map(t => IntCst(42 + i * t))): _*
      )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecPrefix") {
    val n = 5
    val m = 3
    val k = Param("k")(TyInt)
    val vs = VecBuild(n, TyInt ::+ (i => StmRange(m, i, i)()))()
    val e = VecPrefix(vs, k)().tchk().lower()
    for (kVal <- (1 to n)) {
      val expected = StmLiteral(
        (0 until m).map(t =>
          VecLiteral((0 until kVal).map(i => IntCst((1 + t) * i)): _*)()
        ): _*
      )()
      val actual = ir.eval(Let(k, kVal, e)())
      assert(actual == expected, s"(for k = $kVal)")
    }
  }

  test("VecSuffix") {
    val n = 5
    val m = 3
    val k = Param("k")(TyInt)
    val vs = VecBuild(n, TyInt ::+ (i => StmRange(m, i, i)()))()
    val e = VecSuffix(vs, k)().tchk().lower()
    for (kVal <- 1 to n) {
      val expected = StmLiteral(
        (0 until m).map(t =>
          VecLiteral((n - kVal until n).map(i => IntCst((1 + t) * i)): _*)()
        ): _*
      )()
      val actual = ir.eval(Let(k, kVal, e)())
      assert(actual == expected, s"(for k = $kVal)")
    }
  }

  test("VecRepeat") {
    val n = 4
    val m = 3
    val k = Param("k")(TyInt)
    val vs = VecBuild(n, TyInt ::+ (i => StmRange(m, i + 1, i + 1)()))()
    val e = VecRepeat(vs, k).tchk().lower()
    for (kVal <- Seq(1, 2, 5)) {
      val expected = StmLiteral(
        (0 until m).map(t =>
          VecLiteral(
            (0 until kVal).map(_ =>
              VecLiteral((0 until n).map(i => IntCst((t + 1) * (i + 1))): _*)()
            ): _*
          )()
        ): _*
      )()
      val actual = ir.eval(Let(k, kVal, e)())
      assert(actual == expected)
    }
  }

  test("VecReverse") {
    val n = 3
    val m = 3
    val vs = VecBuild(n, TyInt ::+ (i => StmRange(m, i - 1, i + 1)()))()
    val e = VecReverse(vs).tchk().lower()
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral((0 until m).map(j => {
          val i = n - 1 - j
          IntCst((i - 1) + t * (i + 1))
        }): _*)()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecSplit") {
    val nm = 12
    val m = Param("m")(TyInt)
    val k = 3
    val vs = VecBuild(nm, TyInt ::+ (i => StmRange(k, 0, i)()))()
    val e = VecSplit(vs, m).tchk().lower()
    for (mVal <- Seq(1, 2, 3, 4, 6, 12)) {
      val nVal = nm / mVal
      val expected = StmLiteral(
        (0 until k).map(t =>
          VecLiteral(
            (0 until nVal).map(i =>
              VecLiteral(
                (0 until mVal).map(j => IntCst(t * (i * mVal + j))): _*
              )()
            ): _*
          )()
        ): _*
      )()
      val actual = ir.eval(Let(m, mVal, e)())
      assert(actual == expected)
    }
  }

  test("VecJoin") {
    val n = 2
    val m = 3
    val k = 4
    val vs = VecBuild(
      n,
      TyInt ::+ (i => VecBuild(m, TyInt ::+ (j => StmRange(k, i, j)()))())
    )()
    val e = VecJoin(vs)()
    val expected = StmLiteral(
      (0 until k).map(t =>
        VecLiteral((0 until n * m).map(ij => {
          val i = ij / m
          val j = ij % m
          IntCst(i + t * j)
        }): _*)()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }
}
