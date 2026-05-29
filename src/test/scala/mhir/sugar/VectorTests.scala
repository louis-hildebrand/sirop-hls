package mhir.sugar

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class VectorTests extends AnyFunSuite {
  test("VecBuild:StmRange") {
    val n = 3
    val m = 4
    val e = VecBuild(n, U32 ::+ (i => StmRange(m, i, i)()))().tchk().lower
    val expected = StmLiteral(
      (0 until m).map(j =>
        VecLiteral((0 until n).map(i => IntCst(i + j * i)()): _*)()
      ): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecBuild:Stream with free variables") {
    val n = 3
    val m = 4
    val s = Param("s")(TyStm(U32, m))
    val e = VecBuild(n, U32 ::+ (_ => s))().tchk().lower
    val sVal = StmRange(m, C(999)(U32), C(10)(U32))().tchk()
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral((0 until n).map(_ => IntCst(999 + 10 * t)()): _*)()
      ): _*
    )()
    val actual = mhir.eval.eval(e.subPreserveType(s -> sVal))
    assert(actual == expected)
  }

  test("NestedVecBuild:StmRange") {
    val n = 3
    val m = 4
    val k = 5
    val e = VecBuild(
      n,
      U32 ::+ (i => VecBuild(m, U32 ::+ (j => StmRange(k, i, j)()))())
    )().tchk().lower
    val expected = StmLiteral(
      (0 until k).map(t =>
        VecLiteral(
          (0 until n).map(i =>
            VecLiteral((0 until m).map(j => IntCst(i + j * t)()): _*)()
          ): _*
        )()
      ): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecBuild:StmZip") {
    val n = 4
    val m = 3
    val e =
      VecBuild(
        n,
        U32 ::+ (i => StmZip(StmCount(m)(), StmRange(m, i, i)())())
      )().tchk().lower
    val expected = StmLiteral(
      (0 until m).map(j =>
        VecLiteral((0 until n).map(i => Tuple(j, i + j * i)()): _*)()
      ): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecBuild:StmZipFused") {
    val n = 4
    val m = 3
    val a1 = Param("a1")(U32)
    val a2 = Param("a2")(U32)
    val e = VecBuild(
      n,
      U32 ::+ (i =>
        StmBuild(
          m,
          Tuple(a1, a2)(),
          True,
          Map[Param, (Expr, Expr)](
            a1 -> (C(0)(U32), a1 + 1),
            a2 -> (i, a2 + i)
          )
        )()
      )
    )().tchk().lower
    val expected = StmLiteral(
      (0 until m).map(j =>
        VecLiteral((0 until n).map(i => Tuple(j, i + j * i)()): _*)()
      ): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecBuild:LetStm") {
    val n = 5
    val m = 9
    val i = Param("i")(U8)
    val s = Param("s")(TyStm(U8, m))
    val let =
      LetStm(
        1,
        s,
        StmRange(m, C(0)(U8), i)(),
        StmZip(StmCount(C(m)(U8))(), s)()
      )()
    val e = VecBuild(n, Function(i, let)())().tchk().lower
    val expected =
      StmLiteral(
        (0 until m).map(t =>
          VecLiteral(
            (0 until n).map(i => Tuple(C(t)(U8), C(i * t)(U8))()): _*
          )()
        ): _*
      )().tchk()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecBuild:VecBuild(2, i => if (i == 0) then stm0 else stm1)") {
    val n = 8
    val s0 = StmCount(C(n)(U8))().tchk()
    val s1 = StmRange(n, C(42)(U8), C(2)(U8))().tchk()
    val original =
      VecBuild(2, U8 ::+ (i => Mux(i === 0, s0, s1)()))().tchk().lower
    val expected = StmLiteral(
      (0 until n).map(t => VecLiteral(C(t)(U8), C(42 + 2 * t)(U8))()): _*
    )().tchk()
    val actual = mhir.eval.eval(original)
    assert(actual == expected)
  }

  test("VecAccess") {
    val n = 5
    val m = 4
    val idx = Param("idx")(U32)
    val e = VecAccess(
      VecBuild(n, U32 ::+ (i => StmRange(m, i, i)()))(),
      idx
    )().tchk().lower
    for (idxVal <- 0 until n) {
      val expected =
        StmLiteral((0 until m).map(t => IntCst(idxVal + t * idxVal)()): _*)()
      val actual = mhir.eval.eval(Let(idx, C(idxVal)(U32), e)())
      assert(actual == expected)
    }
  }

  test("BuildV_and_Access") {
    val cstVec = VecBuild(2, U32 ::+ (_ => 7))()
    assert(mhir.eval.eval(VecAccess(cstVec, 0)()) == IntCst(7)())
    assert(mhir.eval.eval(VecAccess(cstVec, 1)()) == IntCst(7)())

    val oneTwoThreeVec = VecBuild(3, U32 ::+ (i => i + 1))()
    assert(mhir.eval.eval(VecAccess(oneTwoThreeVec, 0)()) == IntCst(1)())
    assert(mhir.eval.eval(VecAccess(oneTwoThreeVec, 1)()) == IntCst(2)())
    assert(mhir.eval.eval(VecAccess(oneTwoThreeVec, 2)()) == IntCst(3)())
  }

  test("VecCst") {
    val c = Tuple(C(42)(U8), False)().tchk()
    val v = VecCst(3, c)().tchk().lower
    val expected = VecLiteral(c, c, c)().tchk()
    assert(mhir.eval.eval(v) == expected)
  }

  test("VecRange") {
    val v = VecRange(3, C(2)(U8), C(4)(U8))().tchk().lower
    val expected = VecLiteral(C(2)(U8), C(6)(U8), C(10)(U8))().tchk()
    assert(mhir.eval.eval(v) == expected)
  }

  test("Map_and_Access") {
    val v0 = VecBuild(C(3)(U8), U8 ::+ (i => i + 1))()
    val v1 = VecMap(v0, U8 ::+ (x => x * x))().tchk()
    assert(mhir.eval.eval(VecAccess(v1, 0)()) == IntCst(1)())
    assert(mhir.eval.eval(VecAccess(v1, 1)()) == IntCst(4)())
    assert(mhir.eval.eval(VecAccess(v1, 2)()) == IntCst(9)())
  }

  test("VecMap:StmMap(VecMap(StmMap))") {
    val i33 = TySInt(33)
    val n = 2
    val m = 3
    val k = 4
    val svs =
      StmCst(
        n,
        VecBuild(
          m,
          U32 ::+ (i => StmRange(k, i - 1, ReshapeData(i * 2, i33)())())
        )()
      )()
    val e = StmMap(
      svs,
      TyVec(TyStm(i33, C(k)(U8)), C(m)(U8)) ::+ (vs =>
        VecMap(
          vs,
          TyStm(i33, C(k)(U8)) ::+ (s => StmMap(s, i33 ::+ (x => x + 42))())
        )()
      )
    )().tchk().lower
    val expected =
      StmLiteral(
        (0 until n).flatMap(_ =>
          (0 until k).map(t1 =>
            VecLiteral(
              (0 until m).map(i => IntCst((i - 1 + i * 2 * t1) + 42)()): _*
            )()
          )
        ): _*
      )()
    val actual = mhir.eval.eval(e)
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
        U32 ::+ (i => VecBuild(k, U32 ::+ (j => StmRange(p, i, j)()))())
      )()
    )()
    val e = StmMap(
      svvs,
      TyVec(TyVec(TyStm(U32, p), k), m) ::+ (vvs =>
        VecMap(
          vvs,
          TyVec(TyStm(U32, p), k) ::+ (vs =>
            VecMap(
              vs,
              TyStm(U32, p) ::+ (s => StmMap(s, U32 ::+ (x => x * x + 9))())
            )()
          )
        )()
      )
    )().tchk().lower
    val expected =
      StmLiteral(
        (0 until n).flatMap(_ =>
          (0 until p).map(t1 =>
            VecLiteral(
              (0 until m).map(i =>
                VecLiteral((0 until k).map(j => {
                  val x = i + t1 * j
                  IntCst(x * x + 9)()
                }): _*)()
              ): _*
            )()
          )
        ): _*
      )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecMap:VecMap(VecCount(n), i => StmRange(m, i, 1))") {
    val n = 2
    val m = 3
    val e = VecMap(
      VecBuild(n, U32 ::+ (i => i))(),
      U32 ::+ (i => StmRange(m, i, C(1)(U32))())
    )().tchk().lower
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral((0 until n).map(i => IntCst(i + t)()): _*)()
      ): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecFoldComb:SumAndProd") {
    val v = VecBuild(4, U32 ::+ (i => i + 1))()
    val sum =
      VecFoldComb(
        v,
        Tuple(C(0)(U32), C(1)(U32))(),
        (U32, U32) ::+ (acc => U32 ::+ (a => Tuple(acc.__0 + a, acc.__0 * a)()))
      )().tchk().lower
    assert(mhir.eval.eval(sum) == Tuple(C(10)(), C(24)())())
  }

  test("VecFoldComb:HornersMethod") {
    // [2, 3, 4, 5]
    // i.e., 2x^3 + 3x^2 + 4x + 5
    // i.e., 5 + x*(4 + x*(3 + x*2))
    val v = VecBuild(4, U32 ::+ (i => i + 2))()
    val x = C(10)(U32)
    val result =
      VecFoldComb(v, C(0)(U32), U32 ::+ (acc => U32 ::+ (a => a + x * acc)))()
        .tchk()
        .lower
    assert(mhir.eval.eval(result) == C(2345)())
  }

  test("VecReduceComb:Vec[Int,3]:Sum") {
    val v = VecBuild(3, U32 ::+ (i => i + 1))()
    val sum =
      VecReduceComb(v, Missing ::+ (x => x.__0 + x.__1))().tchk().lower
    assert(mhir.eval.eval(sum) == VecLiteral(C(6)())())
  }

  test("VecReduceComb:Vec[Int,4]:HornersMethod") {
    // [2, 3, 4, 5]
    // i.e., 2x^3 + 3x^2 + 4x + 5
    // i.e., 5 + x*(4 + x*(3 + x*2))
    val v = VecBuild(4, U32 ::+ (i => i + 2))()
    val x = C(10)(U32)
    val result =
      VecReduceComb(v, (U32, U32) ::+ (a => a.__1 + x * a.__0))()
        .tchk()
        .lower
    assert(mhir.eval.eval(result) == VecLiteral(C(2345)())())
  }

  test("VecReduceComb:Vec[Vec[Int,1],4]:Sum") {
    val v = Param("v")(TyVec(TyVec(U8, 1), 4))
    val sum = VecReduceComb(
      v,
      Missing ::+ (v => VecMap(v, Missing ::+ (x => x.__0 + x.__1))())
    )().tchk().lower

    val vVal = VecLiteral(
      VecLiteral(C(1)(U8))(),
      VecLiteral(C(2)(U8))(),
      VecLiteral(C(3)(U8))(),
      VecLiteral(C(4)(U8))()
    )().tchk()
    val expected = VecLiteral(VecLiteral(10)())()
    val actual = mhir.eval.eval(sum.subPreserveType(v -> vVal))
    assert(actual == expected)
  }

  test("VecReduceComb:Vec[Stm[Int,1],5]:Sum") {
    val v = Param("v")(TyVec(TyStm(U8, 1), 5))
    val sum = VecReduceComb(
      v,
      Missing ::+ (v => StmMap(v, Missing ::+ (x => x.__0 + x.__1))())
    )().tchk().lower

    val vVal =
      StmLiteral(
        VecLiteral(C(1)(U8), C(2)(U8), C(3)(U8), C(4)(U8), C(5)(U8))()
      )().tchk()
    val expected = StmLiteral(VecLiteral(15)())()
    val actual = mhir.eval.eval(sum.subPreserveType(v -> vVal))
    assert(actual == expected)
  }

  test("VecReduceComb:Vec[Vec[Stm[Stm[Vec[Int,1],1],1],1],4]:Sum") {
    val v = Param("v")(TyVec(TyVec(TyStm(TyStm(TyVec(U8, 1), 1), 1), 1), 5))
    val sum = VecReduceComb(
      v,
      Missing ::+ (v =>
        VecMap(
          v,
          Missing ::+ (s =>
            StmMap(
              s,
              Missing ::+ (s =>
                StmMap(
                  s,
                  Missing ::+ (v =>
                    VecMap(v, Missing ::+ (x => x.__0 + x.__1))()
                  )
                )()
              )
            )()
          )
        )()
      )
    )().tchk().lower

    val vVal =
      StmLiteral(
        VecLiteral(
          VecLiteral(VecLiteral(C(1)(U8))())(),
          VecLiteral(VecLiteral(C(2)(U8))())(),
          VecLiteral(VecLiteral(C(3)(U8))())(),
          VecLiteral(VecLiteral(C(4)(U8))())(),
          VecLiteral(VecLiteral(C(5)(U8))())()
        )()
      )().tchk()
    val expected = StmLiteral(VecLiteral(VecLiteral(VecLiteral(15)())())())()
    val actual = mhir.eval.eval(sum.subPreserveType(v -> vVal))
    assert(actual == expected)
  }

  test("VecAll:Empty") {
    val e = VecAll(VecLiteral()(TyVec(TyBool, 0)))().tchk().lower
    assert(mhir.eval.eval(e) == True)
  }

  test("VecAll:AllTrue") {
    val v = VecLiteral((0 until 8).map(_ => True): _*)()
    val e = VecAll(v)().tchk().lower
    val actual = mhir.eval.eval(e)
    assert(actual == True)
  }

  test("VecAll:OneFalse") {
    val v = VecLiteral((0 until 9).map({
      case 2 => False
      case _ => True
    }): _*)()
    val e = VecAll(v)().tchk().lower
    val actual = mhir.eval.eval(e)
    assert(actual == False)
  }

  test("VecAny:Empty") {
    val e = VecAny(VecLiteral()(TyVec(TyBool, 0)))().tchk().lower
    assert(mhir.eval.eval(e) == False)
  }

  test("VecAny:AllFalse") {
    val v = VecLiteral((0 until 8).map(_ => False): _*)()
    val e = VecAny(v)().tchk().lower
    val actual = mhir.eval.eval(e)
    assert(actual == False)
  }

  test("VecAny:OneTrue") {
    val v = VecLiteral((0 until 9).map({
      case 0 => True
      case _ => False
    }): _*)()
    val e = VecAny(v)().tchk().lower
    val actual = mhir.eval.eval(e)
    assert(actual == True)
  }

  test("SumRows") {
    val v =
      VecBuild(3, U32 ::+ (i => VecBuild(2, U32 ::+ (j => i + j))()))()
    val v2 =
      VecMap(
        v,
        TyVec(U32, 2) ::+ (v => VecFoldComb(v, C(0)(U32), PlusFunction(U32))())
      )()
    assert(mhir.eval.eval(v2) == VecLiteral(1, 3, 5)())
  }

  test("Stm2Vec") {
    val v = Stm2Vec(StmCount(3)())().tchk()
    val expected = StmLiteral(VecLiteral.ints(0, 1, 2))()
    assert(mhir.eval.eval(v) == expected)
  }

  test("VecPrepend:Vec[Int]") {
    val v = VecBuild(3, U8 ::+ (i => i + 5))()
    val actual = VecPrepend(v, C(42)(U8))().tchk().lower
    val expected = VecLiteral(42, 5, 6, 7)()
    assert(mhir.eval.eval(actual) == expected)
  }

  test("VecPrepend:Vec[Stm[Int]]") {
    val m = 4
    val n = 3
    val s = StmCst(m, C(99)(U32))()
    val vs = VecBuild(n, U32 ::+ (i => StmRange(m, i, i)()))()
    val e = VecPrepend(vs, s)()
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral(
          (0 until (n + 1)).map(i =>
            IntCst(if (i == 0) 99 else (i - 1) + t * (i - 1))()
          ): _*
        )()
      ): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecAppend:Vec[Int]") {
    val v = VecBuild(3, U32 ::+ (i => i + 5))()
    val actual = VecAppend(v, C(42)(U32))()
    val expected = VecLiteral(5, 6, 7, 42)()
    assert(mhir.eval.eval(actual) == expected)
    assert(mhir.eval.eval(actual.tchk()) == expected)
  }

  test("VecAppend:Vec[Stm[Int]]") {
    val m = 4
    val n = 3
    val s = StmCst(m, C(99)(U32))()
    val vs = VecBuild(n, U32 ::+ (i => StmRange(m, i, i)()))()
    val e = VecAppend(vs, s)()
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral(
          (0 until (n + 1)).map(i => IntCst(if (i < n) i + t * i else 99)()): _*
        )()
      ): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecPrefix:Vec[Int]") {
    val v = VecBuild(3, U32 ::+ (i => i))()
    assert(mhir.eval.eval(VecPrefix(v, 0)()) == VecLiteral()())
    assert(mhir.eval.eval(VecPrefix(v, 1)()) == VecLiteral(0)())
    assert(mhir.eval.eval(VecPrefix(v, 2)()) == VecLiteral(0, 1)())
    assert(mhir.eval.eval(VecPrefix(v, 3)()) == VecLiteral(0, 1, 2)())
  }

  test("VecPrefix:Vec[Stm[Int]]") {
    val n = 5
    val m = 3
    val k = Param("k")(U32)
    val vs = VecBuild(n, U32 ::+ (i => StmRange(m, i, i)()))()
    val e = VecPrefix(vs, k)().tchk().lower
    for (kVal <- 1 to n) {
      val expected = StmLiteral(
        (0 until m).map(t =>
          VecLiteral((0 until kVal).map(i => IntCst((1 + t) * i)()): _*)()
        ): _*
      )()
      val actual = mhir.eval.eval(e.subPreserveType(k -> C(kVal)(U32)))
      assert(actual == expected, s"(for k = $kVal)")
    }
  }

  test("VecSuffix:Vec[Int]") {
    val v = VecBuild(3, U32 ::+ (i => i))()
    assert(mhir.eval.eval(VecSuffix(v, 0)().tchk()) == VecLiteral()())
    assert(mhir.eval.eval(VecSuffix(v, 1)().tchk()) == VecLiteral(2)())
    assert(mhir.eval.eval(VecSuffix(v, 2)().tchk()) == VecLiteral(1, 2)())
    assert(mhir.eval.eval(VecSuffix(v, 3)().tchk()) == VecLiteral(0, 1, 2)())
  }

  test("VecSuffix:Vec[Stm[Int]]") {
    val n = 5
    val m = 3
    val k = Param("k")(U32)
    val vs = VecBuild(n, U32 ::+ (i => StmRange(m, i, i)()))()
    val e = VecSuffix(vs, k)().tchk().lower
    for (kVal <- 1 to n) {
      val expected = StmLiteral(
        (0 until m).map(t =>
          VecLiteral((n - kVal until n).map(i => IntCst((1 + t) * i)()): _*)()
        ): _*
      )()
      val actual = mhir.eval.eval(e.subPreserveType(k -> C(kVal)(U32)))
      assert(actual == expected, s"(for k = $kVal)")
    }
  }

  test("VecShiftLeft:Vec[Int, 3]") {
    val v = VecBuild(3, U32 ::+ (i => i * (i + 2)))()
    assert(
      mhir.eval
        .eval(VecShiftLeft(v, C(42)(U32))().tchk()) == VecLiteral(3, 8, 42)()
    )
  }

  test("VecShiftLeft:Vec[Int, 7:u3]") {
    // Test that there's no overflow, even if the vector length is the maximum
    // int of the given type

    val u3 = TyUInt(3)
    val v = VecBuild(C(7)(u3), u3 ::+ (i => i))()
    val actual = VecShiftLeft(v, C(3)(u3))().tchk().lower
    val expected = VecLiteral(
      C(1)(u3),
      C(2)(u3),
      C(3)(u3),
      C(4)(u3),
      C(5)(u3),
      C(6)(u3),
      C(3)(u3)
    )()
    assert(mhir.eval.eval(actual) == expected)
  }

  test("VecShiftLeft:Vec[Stm[Int]]") {
    val m = 4
    val n = 3
    val s = StmCst(m, C(99)(U32))()
    val vs = VecBuild(n, U32 ::+ (i => StmRange(m, i, i)()))()
    val e = VecShiftLeft(vs, s)().tchk().lower
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral(
          (0 until n).map(i =>
            IntCst(if (i < n - 1) (i + 1) + t * (i + 1) else 99)()
          ): _*
        )()
      ): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecShiftRight:Vec[Int]") {
    val v = VecBuild(3, U32 ::+ (i => i * (i + 2)))()
    assert(
      mhir.eval
        .eval(VecShiftRight(v, C(42)(U32))().tchk()) == VecLiteral(42, 0, 3)()
    )
  }

  test("VecShiftRight:Vec[Stm[Int]]") {
    val m = 4
    val n = 3
    val s = StmCst(m, C(99)(U32))()
    val vs = VecBuild(n, U32 ::+ (i => StmRange(m, i, i)()))()
    val e = VecShiftRight(vs, s)().tchk().lower
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral(
          (0 until n).map(i =>
            IntCst(if (i == 0) 99 else (i - 1) + t * (i - 1))()
          ): _*
        )()
      ): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecShiftRightGarbage:Vec[Int]") {
    def extract(e: Expr): Seq[Expr] = {
      e.asInstanceOf[VecLiteral].elems
    }
    def eval(e: Expr): Expr = mhir.eval.eval(e, suppressWarnings = true)

    val input = VecBuild(6, U8 ::+ (i => i))()
    val expected = (0 until 6).map(C(_)(U8))

    val v1 = VecShiftRightGarbage(input, 1)().tchk().lower
    val actual1 = extract(eval(v1)).drop(1)
    assert(actual1 == expected.dropRight(1))

    val v2 = VecShiftRightGarbage(input, 2)().tchk().lower
    val actual2 = extract(eval(v2)).drop(2)
    assert(actual2 == expected.dropRight(2))

    val v3 = VecShiftRightGarbage(input, 3)().tchk().lower
    val actual3 = extract(eval(v3)).drop(3)
    assert(actual3 == expected.dropRight(3))
  }

  test("VecConcat:Vec[Int]") {
    val v1 = VecBuild(2, U32 ::+ (i => i))()
    val v2 = VecBuild(4, U32 ::+ (i => i))()
    assert(
      mhir.eval.eval(VecConcat(v1, v2)().tchk()) == VecLiteral(0, 1, 0, 1, 2,
        3)()
    )
    assert(
      mhir.eval.eval(VecConcat(v2, v1)().tchk()) == VecLiteral(0, 1, 2, 3, 0,
        1)()
    )
  }

  test("VecConcat:Vec[Stm[Int]]") {
    val m = 3
    val n1 = 4
    val n2 = 2
    val vs1 = VecBuild(n1, U32 ::+ (i => StmRange(m, C(42)(U32), i)()))()
    val vs2 = VecBuild(n2, U32 ::+ (_ => StmCst(m, C(99)(U32))()))()

    val e1 = VecConcat(vs1, vs2)().tchk().lower
    val expected1 = StmLiteral(
      (0 until m).map(t =>
        VecLiteral(
          (0 until (n1 + n2)).map(i =>
            IntCst(if (i < n1) 42 + i * t else 99)()
          ): _*
        )()
      ): _*
    )()
    val actual1 = mhir.eval.eval(e1)
    assert(actual1 == expected1)

    val e2 = VecConcat(vs2, vs1)().tchk().lower
    val expected2 = StmLiteral(
      (0 until m).map(t =>
        VecLiteral(
          (0 until (n1 + n2)).map(i =>
            IntCst(if (i < n2) 99 else 42 + (i - n2) * t)()
          ): _*
        )()
      ): _*
    )()
    val actual2 = mhir.eval.eval(e2)
    assert(actual2 == expected2)
  }

  test("Vec2Tuple") {
    val v = VecBuild(5, U32 ::+ (i => i * (i + 1)))()
    val expected = Tuple(0, 2, 6, 12, 20)()
    val actual = mhir.eval.eval(Vec2Tuple(v)())
    assert(actual == expected)
  }

  test("VecMap2:Prod") {
    val n = 6
    val v1 = VecBuild(n, U8 ::+ (i => i))()
    val v2 = VecBuild(n, U8 ::+ (i => i + 10))()
    val combined = VecMap2(v1, v2, TimesFunction(U8))()
    val expected = VecLiteral((0 until n).map(i => C(i * (i + 10))(U8)): _*)()
    val actual = mhir.eval.eval(combined)
    assert(actual == expected)
  }

  test("VecMap2:VecConcat") {
    val n = 3
    val m1 = 4
    val m2 = 5
    val v1 = VecBuild(
      n,
      U8 ::+ (i => VecBuild(m1, U8 ::+ (j => Tuple(C(1)(U8), i, j)()))())
    )()
    val v2 = VecBuild(
      n,
      U8 ::+ (i => VecBuild(m2, U8 ::+ (j => Tuple(C(2)(U8), i, j)()))())
    )()
    val combined = VecMap2(
      v1,
      v2,
      Missing ::+ (v1 => Missing ::+ (v2 => VecConcat(v1, v2)()))
    )()
    val expected = VecLiteral(
      (0 until n).map(i => {
        val v1Elems =
          (0 until m1).map(j => Tuple(C(1)(U8), C(i)(U8), C(j)(U8))())
        val v2Elems =
          (0 until m2).map(j => Tuple(C(2)(U8), C(i)(U8), C(j)(U8))())
        VecLiteral(v1Elems ++ v2Elems: _*)()
      }): _*
    )()
    val actual = mhir.eval.eval(combined)
    assert(actual == expected)
  }

  test("VecMap2:Vec[Stm[T, 3], 4]") {
    val v0 = Param("v0")(TyVec(TyStm(U8, 3), 4))
    val v1 = Param("v1")(TyVec(TyStm(I16, 3), 4))
    val map = VecMap2(
      v0,
      v1,
      TyStm(U8, 3) ::+ (s0 => TyStm(I16, 3) ::+ (s1 => StmZip(s0, s1)()))
    )().tchk().lower

    val v0Val = VecLiteral(
      StmLiteral(C(0)(U8), C(1)(U8), C(2)(U8))(),
      StmLiteral(C(3)(U8), C(4)(U8), C(5)(U8))(),
      StmLiteral(C(6)(U8), C(7)(U8), C(8)(U8))(),
      StmLiteral(C(9)(U8), C(10)(U8), C(11)(U8))()
    )().tchk().lower
    val v1Val = VecLiteral(
      StmLiteral(C(0)(I16), C(-10)(I16), C(-20)(I16))(),
      StmLiteral(C(-30)(I16), C(-40)(I16), C(-50)(I16))(),
      StmLiteral(C(-60)(I16), C(-70)(I16), C(-80)(I16))(),
      StmLiteral(C(-90)(I16), C(-100)(I16), C(-110)(I16))()
    )().tchk().lower
    val expected = StmLiteral(
      VecLiteral(
        Tuple(C(0)(U8), C(0)(I16))(),
        Tuple(C(3)(U8), C(-30)(I16))(),
        Tuple(C(6)(U8), C(-60)(I16))(),
        Tuple(C(9)(U8), C(-90)(I16))()
      )(),
      VecLiteral(
        Tuple(C(1)(U8), C(-10)(I16))(),
        Tuple(C(4)(U8), C(-40)(I16))(),
        Tuple(C(7)(U8), C(-70)(I16))(),
        Tuple(C(10)(U8), C(-100)(I16))()
      )(),
      VecLiteral(
        Tuple(C(2)(U8), C(-20)(I16))(),
        Tuple(C(5)(U8), C(-50)(I16))(),
        Tuple(C(8)(U8), C(-80)(I16))(),
        Tuple(C(11)(U8), C(-110)(I16))()
      )()
    )().tchk()
    val actual = mhir.eval.eval(
      map.subPreserveType(Map[Expr, Expr](v0 -> v0Val, v1 -> v1Val))
    )
    assert(actual == expected)
  }

  test("VecMap2(VecAppend)") {
    val v0 = VecLiteral(
      VecLiteral(
        StmLiteral(C(42)(U8), C(44)(U8), C(46)(U8))(),
        StmLiteral(C(99)(U8), C(98)(U8), C(97)(U8))()
      )()
    )()
    val v1 = VecLiteral(StmLiteral((0 until 3).map(C(_)(U8)): _*)())()
    val original = VecMap2(
      v0,
      v1,
      TyVec(TyStm(U8, 3), 2) ::+ (i0 =>
        TyStm(U8, 3) ::+ (i1 => VecAppend(i0, i1)())
      )
    )().tchk().lower
    val expected = StmLiteral(
      VecLiteral(VecLiteral(C(42)(U8), C(99)(U8), C(0)(U8))())(),
      VecLiteral(VecLiteral(C(44)(U8), C(98)(U8), C(1)(U8))())(),
      VecLiteral(VecLiteral(C(46)(U8), C(97)(U8), C(2)(U8))())()
    )().tchk()
    val actual = mhir.eval.eval(original)
    assert(actual == expected)
  }

  test("VecZip") {
    val v0 = VecBuild(3, U32 ::+ (i => i))()
    val v1 = VecBuild(3, U32 ::+ (i => (i + 1) * 2))()
    val zipped = VecZip(v0, v1)().tchk()
    val expected = VecLiteral(
      Tuple(0, 2)(),
      Tuple(1, 4)(),
      Tuple(2, 6)()
    )()
    assert(mhir.eval.eval(zipped) == expected)
  }

  test("VecRepeat:Vec[Int]") {
    val v = VecBuild(4, U32 ::+ (i => (i + 1) * (i + 1)))()
    val v2 = VecRepeat(v, 2)
    val expected = VecLiteral(
      VecLiteral(IntCst(1)(), IntCst(4)(), IntCst(9)(), IntCst(16)())(),
      VecLiteral(IntCst(1)(), IntCst(4)(), IntCst(9)(), IntCst(16)())()
    )()
    assert(mhir.eval.eval(v2) == expected)
  }

  test("VecRepeat:Vec[Stm[Int]]") {
    val n = 4
    val m = 3
    val k = Param("k")(U32)
    val vs = VecBuild(n, U32 ::+ (i => StmRange(m, i + 1, i + 1)()))()
    val e = VecRepeat(vs, k).tchk().lower
    for (kVal <- Seq(1, 2, 5)) {
      val expected = StmLiteral(
        (0 until m).map(t =>
          VecLiteral(
            (0 until kVal).map(_ =>
              VecLiteral(
                (0 until n).map(i => IntCst((t + 1) * (i + 1))()): _*
              )()
            ): _*
          )()
        ): _*
      )()
      val actual = mhir.eval.eval(e.subPreserveType(k -> C(kVal)(U32)))
      assert(actual == expected)
    }
  }

  test("VecReverse:Vec[Int]") {
    val v = VecBuild(4, U32 ::+ (i => (i + 1) * (i + 1)))()
    assert(mhir.eval.eval(VecReverse(v).tchk()) == VecLiteral(16, 9, 4, 1)())
  }

  test("VecReverse:Vec[Stm[Int]]") {
    val n = 3
    val m = 3
    val vs = VecBuild(n, U32 ::+ (i => StmRange(m, i, i + 1)()))()
    val e = VecReverse(vs).tchk().lower
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral((0 until m).map(j => {
          val i = n - 1 - j
          IntCst(i + t * (i + 1))()
        }): _*)()
      ): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecSplit:Vec[Int]") {
    val v = VecBuild(6, U32 ::+ (i => i * i))()
    val split = VecSplit(v, 3)().tchk()
    val expected = VecLiteral(
      VecLiteral(IntCst(0)(), IntCst(1)(), IntCst(4)())(),
      VecLiteral(IntCst(9)(), IntCst(16)(), IntCst(25)())()
    )()
    assert(mhir.eval.eval(split) == expected)
  }

  test("VecSplit:Vec[Stm[Int]]") {
    val nm = 12
    val m = Param("m")(U32)
    val k = 3
    val vs = VecBuild(nm, U32 ::+ (i => StmRange(k, C(0)(U32), i)()))()
    val e = VecSplit(vs, m)().tchk().lower
    for (mVal <- Seq(1, 2, 3, 4, 6, 12)) {
      val nVal = nm / mVal
      val expected = StmLiteral(
        (0 until k).map(t =>
          VecLiteral(
            (0 until nVal).map(i =>
              VecLiteral(
                (0 until mVal).map(j => IntCst(t * (i * mVal + j))()): _*
              )()
            ): _*
          )()
        ): _*
      )()
      val actual = mhir.eval.eval(e.subPreserveType(m -> C(mVal)(U32)))
      assert(actual == expected)
    }
  }

  test("VecJoin:Vec[Int]") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3]]
    val v =
      VecBuild(3, U32 ::+ (i => VecBuild(2, U32 ::+ (j => i + j))()))()
    // [0, 1, 1, 2, 2, 3]
    val joined = VecJoin(v)().tchk()
    assert(mhir.eval.eval(joined) == VecLiteral(0, 1, 1, 2, 2, 3)())
  }

  test("VecJoin:Vec[Stm[Int]]") {
    val n = 2
    val m = 3
    val k = 4
    val vs = VecBuild(
      n,
      U32 ::+ (i => VecBuild(m, U32 ::+ (j => StmRange(k, i, j)()))())
    )()
    val e = VecJoin(vs)()
    val expected = StmLiteral(
      (0 until k).map(t =>
        VecLiteral((0 until n * m).map(ij => {
          val i = ij / m
          val j = ij % m
          IntCst(i + t * j)()
        }): _*)()
      ): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecSlide") {
    // [0, 3, 6, 9, 12]
    val v = VecBuild(5, U32 ::+ (i => i * 3))()
    // [[0, 3, 6],
    //  [3, 6, 9],
    //  [6, 9, 12]]
    val actual = VecSlide(v, 3).tchk()
    val expected = VecLiteral(
      VecLiteral(IntCst(0)(), IntCst(3)(), IntCst(6)())(),
      VecLiteral(IntCst(3)(), IntCst(6)(), IntCst(9)())(),
      VecLiteral(IntCst(6)(), IntCst(9)(), IntCst(12)())()
    )()
    assert(mhir.eval.eval(actual) == expected)
  }

  test("VecTranspose") {
    val v = VecTranspose(
      VecBuild(
        3,
        U32 ::+ (i => VecBuild(4, U32 ::+ (j => Tuple(i, j)()))())
      )()
    )().tchk()
    val expected = VecLiteral(
      VecLiteral(Tuple(0, 0)(), Tuple(1, 0)(), Tuple(2, 0)())(),
      VecLiteral(Tuple(0, 1)(), Tuple(1, 1)(), Tuple(2, 1)())(),
      VecLiteral(Tuple(0, 2)(), Tuple(1, 2)(), Tuple(2, 2)())(),
      VecLiteral(Tuple(0, 3)(), Tuple(1, 3)(), Tuple(2, 3)())()
    )()
    assert(mhir.eval.eval(v) == expected)
  }

  test("VecTransposeTranspose") {
    val x = Param("x")()
    val v =
      VecBuild(
        4,
        U32 ::+ (i => VecBuild(3, U32 ::+ (j => Tuple(i, j)()))())
      )()
    val expected = VecLiteral(
      VecLiteral(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)())(),
      VecLiteral(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())(),
      VecLiteral(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)())(),
      VecLiteral(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)())()
    )()
    val actual = Let(x, v, VecTranspose(VecTranspose(x)())())().tchk()

    assert(mhir.eval.eval(actual) == expected)
  }
}
