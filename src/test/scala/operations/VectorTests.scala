package operations

import ir._
import opt.PartialEvalPass
import org.scalatest.funsuite.AnyFunSuite

class VectorTests extends AnyFunSuite {
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

  test("BuildV_and_Access") {
    val cstVec = VecBuild(2, TyInt ::+ (_ => 7))()
    assert(PartialEvalPass.partialEval(VecAccess(cstVec, 0)()) == IntCst(7))
    assert(PartialEvalPass.partialEval(VecAccess(cstVec, 1)()) == IntCst(7))

    val oneTwoThreeVec = VecBuild(3, TyInt ::+ (i => i + 1))()
    assert(ir.eval(VecAccess(oneTwoThreeVec, 0)()) == IntCst(1))
    assert(ir.eval(VecAccess(oneTwoThreeVec, 1)()) == IntCst(2))
    assert(ir.eval(VecAccess(oneTwoThreeVec, 2)()) == IntCst(3))
  }

  test("Map_and_Access") {
    val v0 = VecBuild(3, TyInt ::+ (i => i + 1))()
    val v1 = VecMap(v0, TyInt ::+ (x => x * x))().tchk()
    assert(ir.eval(VecAccess(v1, 0)()) == IntCst(1))
    assert(ir.eval(VecAccess(v1, 1)()) == IntCst(4))
    assert(ir.eval(VecAccess(v1, 2)()) == IntCst(9))
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

  test("Fold") {
    val oneTwoThreeVec = VecBuild(3, TyInt ::+ (i => i + 1))()
    val sum =
      VecFold(oneTwoThreeVec, 7, TyInt ::+ (e1 => TyInt ::+ (e2 => e1 + e2)))()
        .tchk()
    assert(ir.eval(sum) == StmLiteral(IntCst(13))())
  }

  test("SumRows") {
    // TODO: How to handle this case?
    assume(false)
    val v =
      VecBuild(3, TyInt ::+ (i => VecBuild(2, TyInt ::+ (j => i + j))()))()
    val v2 =
      VecMap(
        v,
        TyVec(TyInt, 3) ::+ (v => VecFold(v, 0, PlusFunction())())
      )()
    assert(ir.eval(v2) == VecLiteral(1, 3, 5)())
  }

  test("VecScanInclusive") {
    // [2, 3,  4,  5,  6]
    val v = VecBuild(5, TyInt ::+ (i => i + 2))()
    // [2, 7, 18, 41, 88]
    val sum =
      VecScanInclusive(v, 0, TyInt ::+ (acc => TyInt ::+ (x => x + 2 * acc)))
        .tchk()
    assert(ir.eval(sum) == StmLiteral(VecLiteral(2, 7, 18, 41, 88)())())
  }

  test("VecScanExclusive") {
    // [2, 3, 4,  5,  6]
    val v = VecBuild(5, TyInt ::+ (i => i + 2))()
    // [0, 2, 7, 18, 41]
    val sum =
      VecScanExclusive(v, 0, TyInt ::+ (acc => TyInt ::+ (x => x + 2 * acc)))
        .tchk()
    assert(ir.eval(sum) == StmLiteral(VecLiteral(0, 2, 7, 18, 41)())())
  }

  test("Stm2Vec") {
    val v = Stm2Vec(StmCount(3)())().tchk()
    val expected = StmLiteral(VecLiteral.ints(0, 1, 2))()
    assert(ir.eval(v) == expected)
  }

  test("VecPrepend:Vec[Int]") {
    val v = VecBuild(3, TyInt ::+ (i => i + 5))()
    val e = IntCst(42)
    val actual = VecPrepend(v, e)()
    val expected = VecLiteral(42, 5, 6, 7)()
    assert(ir.eval(actual) == expected)
    assert(ir.eval(actual.tchk()) == expected)
  }

  test("VecPrepend:Vec[Stm[Int]]") {
    val m = 4
    val n = 3
    val s = StmCst(m, 99)()
    val vs = VecBuild(n, TyInt ::+ (i => StmRange(m, i, i)()))()
    val e = VecPrepend(vs, s)()
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral(
          (0 until (n + 1)).map(i =>
            IntCst(if (i == 0) 99 else (i - 1) + t * (i - 1))
          ): _*
        )()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecAppend:Vec[Int]") {
    val v = VecBuild(3, TyInt ::+ (i => i + 5))()
    val e = IntCst(42)
    val actual = VecAppend(v, e)()
    val expected = VecLiteral(5, 6, 7, 42)()
    assert(ir.eval(actual) == expected)
    assert(ir.eval(actual.tchk()) == expected)
  }

  test("VecAppend:Vec[Stm[Int]]") {
    val m = 4
    val n = 3
    val s = StmCst(m, 99)()
    val vs = VecBuild(n, TyInt ::+ (i => StmRange(m, i, i)()))()
    val e = VecAppend(vs, s)()
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral(
          (0 until (n + 1)).map(i => IntCst(if (i < n) i + t * i else 99)): _*
        )()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecPrefix:Vec[Int]") {
    val v = VecBuild(3, TyInt ::+ (i => i))()
    assert(ir.eval(VecPrefix(v, 0)()) == VecLiteral()())
    assert(ir.eval(VecPrefix(v, 1)()) == VecLiteral(0)())
    assert(ir.eval(VecPrefix(v, 2)()) == VecLiteral(0, 1)())
    assert(ir.eval(VecPrefix(v, 3)()) == VecLiteral(0, 1, 2)())
  }

  test("VecPrefix:Vec[Stm[Int]]") {
    val n = 5
    val m = 3
    val k = Param("k")(TyInt)
    val vs = VecBuild(n, TyInt ::+ (i => StmRange(m, i, i)()))()
    val e = VecPrefix(vs, k)().tchk().lower()
    for (kVal <- 1 to n) {
      val expected = StmLiteral(
        (0 until m).map(t =>
          VecLiteral((0 until kVal).map(i => IntCst((1 + t) * i)): _*)()
        ): _*
      )()
      val actual = ir.eval(Let(k, kVal, e)())
      assert(actual == expected, s"(for k = $kVal)")
    }
  }

  test("VecSuffix:Vec[Int]") {
    val v = VecBuild(3, TyInt ::+ (i => i))()
    assert(ir.eval(VecSuffix(v, 0)().tchk()) == VecLiteral()())
    assert(ir.eval(VecSuffix(v, 1)().tchk()) == VecLiteral(2)())
    assert(ir.eval(VecSuffix(v, 2)().tchk()) == VecLiteral(1, 2)())
    assert(ir.eval(VecSuffix(v, 3)().tchk()) == VecLiteral(0, 1, 2)())
  }

  test("VecSuffix:Vec[Stm[Int]]") {
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

  test("VecShiftLeft:Vec[Int]") {
    val v = VecBuild(3, TyInt ::+ (i => i * (i + 2)))()
    assert(ir.eval(VecShiftLeft(v, 42)().tchk()) == VecLiteral(3, 8, 42)())
  }

  test("VecShiftLeft:Vec[Stm[Int]]") {
    val m = 4
    val n = 3
    val s = StmCst(m, 99)()
    val vs = VecBuild(n, TyInt ::+ (i => StmRange(m, i, i)()))()
    val e = VecShiftLeft(vs, s)().tchk().lower()
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral(
          (0 until n).map(i =>
            IntCst(if (i < n - 1) (i + 1) + t * (i + 1) else 99)
          ): _*
        )()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }

  test("VecShiftRight:Vec[Int]") {
    val v = VecBuild(3, TyInt ::+ (i => i * (i + 2)))()
    assert(ir.eval(VecShiftRight(v, 42)().tchk()) == VecLiteral(42, 0, 3)())
  }

  test("VecShiftRight:Vec[Stm[Int]]") {
    val m = 4
    val n = 3
    val s = StmCst(m, 99)()
    val vs = VecBuild(n, TyInt ::+ (i => StmRange(m, i, i)()))()
    val e = VecShiftRight(vs, s)().tchk().lower()
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral(
          (0 until n).map(i =>
            IntCst(if (i == 0) 99 else (i - 1) + t * (i - 1))
          ): _*
        )()
      ): _*
    )()
    val actual = ir.eval(e)
    assert(actual == expected)
  }
  test("VecConcat:Vec[Int]") {
    val v1 = VecBuild(2, TyInt ::+ (i => i))()
    val v2 = VecBuild(4, TyInt ::+ (i => i))()
    assert(
      ir.eval(VecConcat(v1, v2)().tchk()) == VecLiteral(0, 1, 0, 1, 2, 3)()
    )
    assert(
      ir.eval(VecConcat(v2, v1)().tchk()) == VecLiteral(0, 1, 2, 3, 0, 1)()
    )
  }

  test("VecConcat:Vec[Stm[Int]]") {
    val m = 3
    val n1 = 4
    val n2 = 2
    val vs1 = VecBuild(n1, TyInt ::+ (i => StmRange(m, 42, i)()))()
    val vs2 = VecBuild(n2, TyInt ::+ (_ => StmCst(m, 99)()))()

    val e1 = VecConcat(vs1, vs2)().tchk().lower()
    val expected1 = StmLiteral(
      (0 until m).map(t =>
        VecLiteral(
          (0 until (n1 + n2)).map(i =>
            IntCst(if (i < n1) 42 + i * t else 99)
          ): _*
        )()
      ): _*
    )()
    val actual1 = ir.eval(e1)
    assert(actual1 == expected1)

    val e2 = VecConcat(vs2, vs1)().tchk().lower()
    val expected2 = StmLiteral(
      (0 until m).map(t =>
        VecLiteral(
          (0 until (n1 + n2)).map(i =>
            IntCst(if (i < n2) 99 else 42 + (i - n2) * t)
          ): _*
        )()
      ): _*
    )()
    val actual2 = ir.eval(e2)
    assert(actual2 == expected2)
  }

  test("Vec2Tuple") {
    val v = VecBuild(5, TyInt ::+ (i => i * (i + 1)))()
    val expected = Tuple(0, 2, 6, 12, 20)()
    val actual = PartialEvalPass.partialEval(Vec2Tuple(v))
    assert(actual == expected)
  }

  test("VecZip") {
    val v0 = VecBuild(3, TyInt ::+ (i => i))()
    val v1 = VecBuild(3, TyInt ::+ (i => (i + 1) * 2))()
    val zipped = VecZip(v0, v1).tchk()
    val expected = VecLiteral(
      Tuple(0, 2)(),
      Tuple(1, 4)(),
      Tuple(2, 6)()
    )()
    assert(ir.eval(zipped) == expected)
  }

  test("VecRepeat:Vec[Int]") {
    val v = VecBuild(4, TyInt ::+ (i => (i + 1) * (i + 1)))()
    val v2 = VecRepeat(v, 2)
    val expected = VecLiteral(
      VecLiteral(IntCst(1), IntCst(4), IntCst(9), IntCst(16))(),
      VecLiteral(IntCst(1), IntCst(4), IntCst(9), IntCst(16))()
    )()
    assert(ir.eval(v2) == expected)
  }

  test("VecRepeat:Vec[Stm[Int]]") {
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

  test("VecReverse:Vec[Int]") {
    val v = VecBuild(4, TyInt ::+ (i => (i + 1) * (i + 1)))()
    assert(ir.eval(VecReverse(v).tchk()) == VecLiteral(16, 9, 4, 1)())
  }

  test("VecReverse:Vec[Stm[Int]]") {
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

  test("VecSplit:Vec[Int]") {
    val v = VecBuild(6, TyInt ::+ (i => i * i))()
    val split = VecSplit(v, 3).tchk()
    val expected = VecLiteral(
      VecLiteral(IntCst(0), IntCst(1), IntCst(4))(),
      VecLiteral(IntCst(9), IntCst(16), IntCst(25))()
    )()
    assert(ir.eval(split) == expected)
  }

  test("VecSplit:Vec[Stm[Int]]") {
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

  test("VecJoin:Vec[Int]") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3]]
    val v =
      VecBuild(3, TyInt ::+ (i => VecBuild(2, TyInt ::+ (j => i + j))()))()
    // [0, 1, 1, 2, 2, 3]
    val joined = VecJoin(v)().tchk()
    assert(ir.eval(joined) == VecLiteral(0, 1, 1, 2, 2, 3)())
  }

  test("VecJoin:Vec[Stm[Int]]") {
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

  test("VecSlide") {
    // [0, 3, 6, 9, 12]
    val v = VecBuild(5, TyInt ::+ (i => i * 3))()
    // [[0, 3, 6],
    //  [3, 6, 9],
    //  [6, 9, 12]]
    val actual = VecSlide(v, 3).tchk()
    val expected = VecLiteral(
      VecLiteral(IntCst(0), IntCst(3), IntCst(6))(),
      VecLiteral(IntCst(3), IntCst(6), IntCst(9))(),
      VecLiteral(IntCst(6), IntCst(9), IntCst(12))()
    )()
    assert(ir.eval(actual) == expected)
  }

  test("VecTranspose") {
    val v = VecTranspose(
      VecBuild(
        3,
        TyInt ::+ (i => VecBuild(4, TyInt ::+ (j => Tuple(i, j)()))())
      )()
    ).tchk()
    val expected = VecLiteral(
      VecLiteral(Tuple(0, 0)(), Tuple(1, 0)(), Tuple(2, 0)())(),
      VecLiteral(Tuple(0, 1)(), Tuple(1, 1)(), Tuple(2, 1)())(),
      VecLiteral(Tuple(0, 2)(), Tuple(1, 2)(), Tuple(2, 2)())(),
      VecLiteral(Tuple(0, 3)(), Tuple(1, 3)(), Tuple(2, 3)())()
    )()
    assert(ir.eval(v) == expected)
  }

  test("VecTransposeTranspose") {
    val x = Param("x")()
    val v =
      VecBuild(
        4,
        TyInt ::+ (i => VecBuild(3, TyInt ::+ (j => Tuple(i, j)()))())
      )()
    val expected = VecLiteral(
      VecLiteral(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)())(),
      VecLiteral(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())(),
      VecLiteral(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)())(),
      VecLiteral(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)())()
    )()
    val actual = Let(x, v, VecTranspose(VecTranspose(x)))().tchk()

    assert(ir.eval(actual) == expected)
  }
}
