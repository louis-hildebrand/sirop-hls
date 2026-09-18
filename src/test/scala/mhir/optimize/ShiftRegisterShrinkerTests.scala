package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.optimize.cost.SimpleDelayCostModel
import mhir.sugar._
import mhir.testing.SiropFunSuite
import mhir.typecheck._

import scala.annotation.tailrec

class ShiftRegisterShrinkerTests extends SiropFunSuite {

  private val delayCostModel = SimpleDelayCostModel(madd = true)
  private val pass: ShiftRegisterShrinker = {
    ShiftRegisterShrinker(delayCostModel, enabled = true, handshake = false)
  }
  private val passWithHandshake: ShiftRegisterShrinker = {
    ShiftRegisterShrinker(delayCostModel, enabled = true, handshake = true)
  }

  /** Apply transformations required before evaluation in `no_handshake` mode.
    */
  private def postProcess(
      e: Expr,
      handshake: Boolean = false,
      headByParam: Map[Param, Expr] = Map()
  ): Expr = {
    // TODO: I think this basic sequence of transformations appears in three places: the main compiler, the REPL, and here. See if I can deduplicate it.
    // Certain transformations are needed when the handshake protocol is
    // disabled, namely
    //  * partial evaluation (to simplify the delay annotations),
    //  * latency matching (otherwise the results will be wrong),
    //  * and letstm buffer shrinking (otherwise the evaluator will complain
    //    about nonzero buffer sizes).
    val func = headByParam.foldLeft(e)({ case (e, (x, _)) =>
      Function(x, e)().tchk()
    })
    val simplified = PartialEvalPass.partialEval(func)
    val latencyAnalysis = new LatencyAnalysis(handshake = handshake)
    // Unused data removal doesn't really matter in this case, since we're
    // dealing with test code rather than the main program
    val unusedDataRemover = UnusedDataRemover(enabled = false)
    val latencyMatcher = EnabledLatencyMatcher(
      latencyAnalysis,
      unusedDataRemover,
      handshake = handshake
    )
    val afterLatencyMatching =
      latencyMatcher.matchLatencies(simplified, headByParam)
    val letBufShrinker = new StaticLetStmBufferShrinker(
      latencyAnalysis,
      handshake = handshake,
      assumeThroughputsMatch = false
    )
    val afterLetBufShrinking =
      letBufShrinker.shrinkBuffers(afterLatencyMatching)
    val (_, result) = TypeChecker.unwrapTopLevelFunction(afterLetBufShrinking)
    result
  }

  @tailrec
  private def unwrapLetStm(e: Expr): Expr = {
    e match {
      case LetStm(_, _, _, out) => unwrapLetStm(out)
      case e                    => e
    }
  }

  test("Simple") {
    val input = Param("input", -1)(TyStm(U8, 12))
    val original = makeSbuild(
      """sbuild(8 @ 4)(undefined, (buf[0], buf[1]), true) {
        |  (buf: Vec[u8, 4]) = {
        |    init: undefined:Vec[u8, 4],
        |    // NOTICE: the input to the shift register is basically free in
        |    //         terms of combinational delay.
        |    //         Why not shrink the shift register one element further
        |    //         by letting sdata(p) bypass the shift register?
        |    next: buf.VecShiftLeft(sdata(p))
        |  }
        |} {
        |  (p: Stm[u8, -1] @ 0) = {
        |    stm: input,
        |    ready: true
        |  }
        |}
        |""".stripMargin.stripTrailing,
      context = Map(input -> input.typ)
    )
    val simplified = this.postProcess(
      pass.applyOnce(original),
      headByParam = Map(input -> Undefined(U8))
    )

    // Same behaviour
    assertSameVal(
      // StmDelay(..., 2) actually delays by 3 cycles due to out register
      StmDelay(simplified, C(2)())().tchk().lower,
      original,
      handshake = false,
      inputs = Map(input -> counterWithPrefix(12, 42))
    )

    // Successful simplification: the shift register should now be smaller
    val simplifiedSbuild = unwrapLetStm(simplified).asInstanceOf[StmBuild]
    assert(simplifiedSbuild.accumulators.size == 1)
    val (buf, _) = simplifiedSbuild.accumulators.head
    assert(buf.typ == TyVec(U8, 1))
  }

  test("SimpleWithSink") {
    val input = Param("input", -1)(TyStm(U8, 12))
    val original = makeSbuild(
      """sbuild(8 @ 4)(undefined, (buf[0], buf[1]), true) {
        |  (buf: Vec[u8, 4]) = {
        |    init: undefined:Vec[u8, 4],
        |    // NOTICE: The input to the shift register is basically free in
        |    //         terms of combinational delay.
        |    //         However, since the sink annotation indicates that
        |    //         buf[1] is used, we should not shrink the vector below
        |    //         length 2.
        |    next: buf.VecShiftLeft(sdata(p))
        |  }
        |} {
        |  (p: Stm[u8, -1] @ 0) = {
        |    stm: input,
        |    ready: true
        |  }
        |}
        |""".stripMargin.stripTrailing,
      context = Map(input -> input.typ),
      annotations =
        Set(SinkAnnotation(VecAccess(Param("buf", -1)(Missing), C(1)())()))
    )
    val simplified = this.postProcess(
      pass.applyOnce(original),
      headByParam = Map(input -> Undefined(U8))
    )

    // Same behaviour
    assertSameVal(
      // StmDelay(..., 1) actually delays by 2 cycles due to out register
      StmDelay(simplified, C(1)())().tchk().lower,
      original,
      handshake = false,
      inputs = Map(input -> counterWithPrefix(12, 42))
    )

    // Successful simplification: the shift register should now be smaller
    val simplifiedSbuild = unwrapLetStm(simplified).asInstanceOf[StmBuild]
    assert(simplifiedSbuild.accumulators.size == 1)
    val (buf, _) = simplifiedSbuild.accumulators.head
    assert(buf.typ == TyVec(U8, 2))
  }

  test("RemoveCompletely") {
    val input = Param("input", -1)(TyStm(U8, 12))
    val original = makeSbuild(
      """sbuild(8 @ 5)(undefined, buf[0], true) {
        |  (buf: Vec[u8, 4]) = {
        |    init: undefined:Vec[u8, 4],
        |    // NOTICE: the input to the shift register is basically free in
        |    //         terms of combinational delay.
        |    //         Why not shrink the shift register one element further
        |    //         by letting sdata(p) bypass the shift register?
        |    //         And in this case, that actually completely eliminates
        |    //         the shift register!
        |    next: buf.VecShiftLeft(sdata(p))
        |  }
        |} {
        |  (p: Stm[u8, -1] @ 0) = {
        |    stm: input,
        |    ready: true
        |  }
        |}
        |""".stripMargin.stripTrailing,
      context = Map(input -> input.typ)
    )
    val simplified = this.postProcess(
      pass.applyOnce(original),
      headByParam = Map(input -> Undefined(U8))
    )

    // Same behaviour
    assertSameVal(
      // StmDelay(..., 3) actually delays by 4 cycles due to out register
      StmDelay(simplified, C(3)())().tchk().lower,
      original,
      handshake = false,
      inputs = Map(input -> counterWithPrefix(12, 42))
    )

    // Successful simplification: the shift register should now be smaller
    val simplifiedSbuild = unwrapLetStm(simplified).asInstanceOf[StmBuild]
    assert(simplifiedSbuild.accumulators.size == 1)
    val (buf, _) = simplifiedSbuild.accumulators.head
    assert(buf.typ == TyVec(U8, 0))
  }

  test("HighDelayInput") {
    val input = Param("input", -1)(TyStm(U8, 12))
    val original = makeSbuild(
      """sbuild(8 @ 4)(undefined, (buf[0], buf[1]), true) {
        |  (buf: Vec[u8, 4]) = {
        |    init: undefined:Vec[u8, 4],
        |    // NOTICE: the shift register input is pretty expensive in terms
        |    //         of combinational delay, so we don't want to bypass
        |    //         the shift register in this case.
        |    next: buf.VecShiftLeft(sdata(p) * sdata(p))
        |  }
        |} {
        |  (p: Stm[u8, -1] @ 0) = {
        |    stm: input,
        |    ready: true
        |  }
        |}
        |""".stripMargin.stripTrailing,
      context = Map(input -> input.typ)
    )
    val simplified = this.postProcess(
      pass.applyOnce(original),
      headByParam = Map(input -> Undefined(U8))
    )

    // Same behaviour
    assertSameVal(
      // StmDelay(..., 1) actually delays by 2 cycles due to out register
      StmDelay(simplified, C(1)())().tchk().lower,
      original,
      handshake = false,
      inputs = Map(input -> counterWithPrefix(12, 42))
    )

    // Successful simplification: the shift register should now be smaller
    val simplifiedSbuild = unwrapLetStm(simplified).asInstanceOf[StmBuild]
    assert(simplifiedSbuild.accumulators.size == 1)
    val (buf, _) = simplifiedSbuild.accumulators.head
    assert(buf.typ == TyVec(U8, 2))
  }

  test("ProducerUsedTwice") {
    val input = Param("input", -1)(TyStm(U8, 12))
    val original = makeSbuild(
      // Notice how sdata(p) is used inside and outside the shift register, so
      // we need to be careful about changing the schedule of the shift
      // register relative to the rest of the sbuild
      """sbuild(8 @ 4)(undefined, (sdata(p), buf[0], buf[1]), true) {
        |  (buf: Vec[u8, 4]) = {
        |    init: undefined:Vec[u8, 4],
        |    next: buf.VecShiftLeft(sdata(p))
        |  }
        |} {
        |  (p: Stm[u8, -1] @ 0) = {
        |    stm: input,
        |    ready: true
        |  }
        |}
        |""".stripMargin.stripTrailing,
      context = Map(input -> input.typ)
    )
    val simplified = pass.applyOnce(original)

    // Same behaviour
    assertSameVal(
      this.postProcess(
        simplified,
        headByParam = Map(input -> Undefined(Missing))
      ),
      original,
      handshake = false,
      inputs = Map(input -> counterWithPrefix(12, 42))
    )

    // Successful simplification: the shift register should now be smaller
    val simplifiedSbuild = unwrapLetStm(simplified).asInstanceOf[StmBuild]
    assert(simplifiedSbuild.accumulators.size == 1)
    val (buf, _) = simplifiedSbuild.accumulators.head
    assert(buf.typ == TyVec(U8, 1))
  }

  test("ConstantInput") {
    val original = makeSbuild(
      """sbuild(8 @ 4)(undefined, (buf[0], buf[1]), true) {
        |  (buf: Vec[(u8, bool), 4]) = {
        |    init: undefined,
        |    next: buf.VecShiftLeft( (42:u8, true) )
        |  }
        |} {}
        |""".stripMargin.stripTrailing,
      context = Map()
    )
    val simplified = this.postProcess(
      pass.applyOnce(original),
      headByParam = Map()
    )

    // Same behaviour
    assertSameVal(
      simplified,
      original,
      handshake = false,
      inputs = Map()
    )

    // Successful simplification: the shift register should now be smaller
    val simplifiedSbuild = unwrapLetStm(simplified).asInstanceOf[StmBuild]
    assert(simplifiedSbuild.accumulators.size == 1)
    val (buf, _) = simplifiedSbuild.accumulators.head
    assert(buf.typ == TyVec(TyTuple(U8, TyBool), 0))
  }

  test("ConcreteInit") {
    val input = Param("input", -1)(TyStm(U8, 12))
    val original = makeSbuild(
      """sbuild(8 @ 1)(undefined, (buf[0], buf[1]), true) {
        |  (buf: Vec[u8, 4] @ 1) = {
        |    init: VecRange(4, 1:u8, 1:u8),
        |    next: buf.VecShiftLeft(sdata(p))
        |  }
        |} {
        |  (p: Stm[u8, -1] @ 0) = {
        |    stm: input,
        |    ready: true
        |  }
        |}
        |""".stripMargin.stripTrailing,
      context = Map(input -> input.typ)
    )
    val simplified = this.postProcess(
      pass.applyOnce(original),
      headByParam = Map(input -> Undefined(U8))
    )

    // Same behaviour
    assertSameVal(
      simplified,
      original,
      handshake = false,
      inputs = Map(input -> counterWithPrefix(12, 42))
    )

    // If simplification did occur, maybe the test is broken somehow
    val simplifiedSbuild = unwrapLetStm(simplified).asInstanceOf[StmBuild]
    assert(simplifiedSbuild.accumulators.size == 1)
    val (buf, _) = simplifiedSbuild.accumulators.head
    assert(buf.typ == TyVec(U8, 4))
  }

  test("AllUsed") {
    val input = Param("input", -1)(TyStm(U8, 12))
    val original = makeSbuild(
      """sbuild(8 @ 4)(undefined, buf, true) {
        |  (buf: Vec[u8, 4]) = {
        |    init: undefined:Vec[u8, 4],
        |    next: buf.VecShiftLeft(sdata(p))
        |  }
        |} {
        |  (p: Stm[u8, -1] @ 0) = {
        |    stm: input,
        |    ready: true
        |  }
        |}
        |""".stripMargin.stripTrailing,
      context = Map(input -> input.typ)
    )
    val simplified = this.postProcess(
      pass.applyOnce(original),
      headByParam = Map(input -> Undefined(U8))
    )

    // Same behaviour
    assertSameVal(
      simplified,
      original,
      handshake = false,
      inputs = Map(input -> counterWithPrefix(12, 42))
    )

    // If simplification did occur, maybe the test is broken somehow
    val simplifiedSbuild = unwrapLetStm(simplified).asInstanceOf[StmBuild]
    assert(simplifiedSbuild.accumulators.size == 1)
    val (buf, _) = simplifiedSbuild.accumulators.head
    assert(buf.typ == TyVec(U8, 4))
  }

  test("SelfLoop") {
    val original = makeSbuild(
      """sbuild(8 @ 4)(undefined, buf[0], true) {
        |  (buf: Vec[u8, 4] @ 1) = {
        |    init: VecRange(4, 10:u8, 1:u8),
        |    next: buf.VecShiftLeft(buf[1])
        |  }
        |} {}
        |""".stripMargin.stripTrailing,
      context = Map()
    )
    val simplified = this.postProcess(pass.applyOnce(original))

    // Same behaviour
    assertSameVal(simplified, original, handshake = false, inputs = Map())

    // If simplification did occur, maybe the test is broken somehow
    val simplifiedSbuild = unwrapLetStm(simplified).asInstanceOf[StmBuild]
    assert(simplifiedSbuild.accumulators.size == 1)
    val (buf, _) = simplifiedSbuild.accumulators.head
    assert(buf.typ == TyVec(U8, 4))
  }

  test("NotEnoughOutDelay") {
    val input = Param("input", -1)(TyStm(U8, 12))
    val original = makeSbuild(
      """sbuild(16 @ 1)(
        |  undefined,
        |  if started then (buf[0], buf[1]) else (255:u8, 250:u8),
        |  true
        |) {
        |  (buf: Vec[u8, 4]) = {
        |    init: undefined:Vec[u8, 4],
        |    next: buf.VecShiftLeft(sdata(p))
        |  },
        |  (started: bool @ 0) = {
        |    init: false,
        |    next: true
        |  }
        |} {
        |  (p: Stm[u8, -1] @ 0) = {
        |    stm: input,
        |    ready: true
        |  }
        |}
        |""".stripMargin.stripTrailing,
      context = Map(input -> input.typ)
    )
    val simplified = this.postProcess(
      pass.applyOnce(original),
      headByParam = Map(input -> Undefined(U8))
    )

    // Same behaviour
    assertSameVal(
      simplified,
      original,
      handshake = false,
      inputs = Map(input -> counterWithPrefix(12, 42, prefixLen = 0))
    )

    // If simplification did occur, maybe the test is broken somehow
    val simplifiedSbuild = unwrapLetStm(simplified).asInstanceOf[StmBuild]
    assert(simplifiedSbuild.accumulators.size == 2)
    val (buf, _) = simplifiedSbuild.accumulators
      .find({ case (x, _) => x.typ != TyBool })
      .get
    assert(buf.typ == TyVec(U8, 4))
  }

  test("HandshakeEnabled") {
    val input = Param("input", -1)(TyStm(U8, 12))
    val original = makeSbuild(
      """sbuild(8 @ 4)(undefined, (buf[0], buf[1]), true) {
        |  (buf: Vec[u8, 4]) = {
        |    init: undefined:Vec[u8, 4],
        |    next: buf.VecShiftLeft(sdata(p))
        |  }
        |} {
        |  (p: Stm[u8, -1] @ 0) = {
        |    stm: input,
        |    ready: true
        |  }
        |}
        |""".stripMargin.stripTrailing,
      context = Map(input -> input.typ)
    )
    val simplified = passWithHandshake.applyOnce(original)

    // Same behaviour
    assertSameVal(
      simplified,
      original,
      handshake = true,
      inputs = Map(input -> counterWithPrefix(12, 42, prefixLen = 0))
    )

    // If simplification did occur, maybe the test is broken somehow
    val simplifiedSbuild = unwrapLetStm(simplified).asInstanceOf[StmBuild]
    assert(simplifiedSbuild.accumulators.size == 1)
    val (buf, _) = simplifiedSbuild.accumulators.head
    assert(buf.typ == TyVec(U8, 4))
  }
}
