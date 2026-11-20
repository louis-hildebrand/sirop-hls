package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.testing.SiropFunSuite
import mhir.typecheck.TypeCheck

class TupleAccumulatorSplitterTests extends SiropFunSuite {

  private val pass = TupleAccumulatorSplitter

  test("Vec[(u8, bool)]") {
    val p1 = Param("p1", -1)(TyStm(TyVec(U8, 3), 10))
    val p2 = Param("p2", -1)(TyStm(TyVec(TyBool, 3), 10))
    val original = makeSbuild(
      """sbuild(10 @ 1)(v, true) {
         |    (v: Vec[(u8, bool), 3]) = {
         |        init: zeros:[Vec[(u8, bool), 3]](),
         |        next: VecZip(sdata(p1), sdata(p2))
         |    }
         |} {
         |    (p1: Stm[Vec[u8, 3], 10] @ 0) = {
         |        stm: p1,
         |        ready: true
         |    },
         |    (p2: Stm[Vec[bool, 3], 10] @ 0) = {
         |        stm: p2,
         |        ready: true
         |    }
         |}
        |""".stripMargin,
      context = Map(p1 -> p1.typ, p2 -> p2.typ),
      annotations = Set()
    )
    val inputs = Map(
      p1 -> StmLiteral(
        (0 until 10).map(t =>
          VecLiteral((0 until 3).map(i => C(i + t)(U8)): _*)()
        ): _*
      )().tchk(),
      p2 -> StmLiteral(
        (0 until 10).map({ t =>
          val patterns = Seq(
            Seq(false, false, false),
            Seq(false, false, true),
            Seq(false, true, false),
            Seq(false, true, true),
            Seq(true, false, false),
            Seq(true, false, true),
            Seq(true, true, false),
            Seq(true, true, true)
          )
          VecLiteral(patterns(t % 8).map(if (_) True else False): _*)()
        }): _*
      )().tchk()
    )

    val actual = pass.applyOnce(original)

    // Correctness
    val originalVal =
      mhir.eval.eval(original, handshake = false, inputs = inputs)
    val actualVal = mhir.eval.eval(actual, handshake = false, inputs = inputs)
    assert(actualVal == originalVal)

    // Successful transformation
    val accumulatorTypes =
      actual.accumulators.map({ case (x, _) => x.typ.toString }).toSeq.sorted
    val expectedTypes = Seq("Vec[bool, 3]", "Vec[u8, 3]")
    assert(accumulatorTypes == expectedTypes)
  }

  test("Vec[((u8, bool), u16)]") {
    val original = makeSbuild(
      """sbuild(10 @ 1)(v, true) {
        |    (v: Vec[((u8, bool), u16), 5]) = {
        |        init: vbuild(5) { (i: u8) =>
        |            ((i, i < 3), pad16(i + 10))
        |        },
        |        next: v.VecShiftLeft(v[0])
        |    }
        |} {}
        |""".stripMargin,
      context = Map(),
      annotations = Set()
    )

    val actual = pass.applyOnce(original)

    // Correctness
    val originalVal = mhir.eval.eval(original, handshake = false)
    val actualVal = mhir.eval.eval(actual, handshake = false)
    assert(actualVal == originalVal)

    // Successful transformation
    val accumulatorTypes =
      actual.accumulators.toSeq.map({ case (x, _) => x.typ.toString }).sorted
    val expectedTypes = Seq("Vec[bool, 5]", "Vec[u16, 5]", "Vec[u8, 5]")
    assert(accumulatorTypes == expectedTypes)
  }

  test("Vec[Vec[(i8, i8)]]") {
    val original = makeSbuild(
      """sbuild(10 @ 1)(v, true) {
        |    (v: Vec[Vec[(i8, i8), 4], 3]) = {
        |        init: vbuild(3) { (i: u7) =>
        |            vbuild(4) { (j: u7) =>
        |                (sign(i), sign(j))
        |            }
        |        },
        |        next: v.VecShiftLeft(v[0])
        |    }
        |} {}
        |""".stripMargin,
      context = Map(),
      annotations = Set()
    )

    val actual = pass.applyOnce(original)

    // Correctness
    val originalVal = mhir.eval.eval(original, handshake = false)
    val actualVal = mhir.eval.eval(actual, handshake = false)
    assert(actualVal == originalVal)

    // Successful transformation
    val accumulatorTypes =
      actual.accumulators.map({ case (x, _) => x.typ.toString }).toSeq.sorted
    val expectedTypes = Seq("Vec[Vec[i8, 4], 3]", "Vec[Vec[i8, 4], 3]")
    assert(accumulatorTypes == expectedTypes)
  }

  test("(Vec[(i8, i8)], Vec[(u8, u8)])") {
    val original = makeSbuild(
      """sbuild(10 @ 1)(v, true) {
        |    (v: (Vec[(i8, i8), 5], Vec[(u8, u8), 3])) = {
        |        init: (
        |            vbuild(5) { (i: u7) =>
        |                (sign(i), sign(i - 3))
        |            },
        |            vbuild(3) { (i: u8) =>
        |                (i, i + 5)
        |            }
        |        ),
        |        next: (v.0.VecShiftLeft(v.0[0]), v.1.VecShiftLeft(v.1[0]))
        |    }
        |} {}
        |""".stripMargin,
      context = Map(),
      annotations = Set()
    )

    val actual = pass.applyOnce(original)

    // Correctness
    val originalVal = mhir.eval.eval(original, handshake = false)
    val actualVal = mhir.eval.eval(actual, handshake = false)
    assert(actualVal == originalVal)

    // Successful transformation
    val accumulatorTypes =
      actual.accumulators.map({ case (x, _) => x.typ.toString }).toSeq.sorted
    val expectedTypes =
      Seq("Vec[i8, 5]", "Vec[i8, 5]", "Vec[u8, 3]", "Vec[u8, 3]")
    assert(accumulatorTypes == expectedTypes)
  }
}
