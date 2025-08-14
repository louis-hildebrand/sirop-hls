package mhir.sugar

import mhir.ir.Lowering.ExprLowering
import org.scalatest.funsuite.AnyFunSuite
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

/** Tests for lowering [[mhir.ir.StmLiteral]] and [[mhir.ir.VecLiteral]].
  */
class LiteralTests extends AnyFunSuite {

  /* Easy *********************************************************************/

  test("StmLiteral:Stm[U8, 5]") {
    val original = StmLiteral((0 until 5).map(C(_)(U8)): _*)().tchk()
    val lowered = original.lower()
    assert(lowered == original)
    assert(lowered.typ == TyStm(U8, 5))
  }

  test("StmLiteral:Stm[Vec[(U8, I16), 4], 5]") {
    val original = StmLiteral(
      (0 until 5).map(t =>
        VecLiteral((0 until 4).map(i => Tuple(C(t)(U8), C(i)(I16))()): _*)()
      ): _*
    )().tchk()
    val lowered = original.lower()
    assert(lowered == original)
    assert(lowered.typ == TyStm(TyVec((U8, I16), 4), 5))
  }

  test("VecLiteral:Vec[U8, 7]") {
    val original = VecLiteral((0 until 7).map(C(_)(U8)): _*)().tchk()
    val lowered = original.lower()
    assert(lowered.typ == TyVec(U8, 7))
    assert(lowered == original)
  }

  /* Move the stream to the outside *******************************************/

  test("VecLiteral:Vec[Stm[(U8, U8), 4], 3]") {
    val original = VecLiteral(
      (0 until 3).map(i =>
        StmLiteral((0 until 4).map(t => Tuple(C(i)(U8), C(t)(U8))()): _*)()
      ): _*
    )().tchk()
    val expected = StmLiteral(
      (0 until 4).map(t =>
        VecLiteral((0 until 3).map(i => Tuple(C(i)(U8), C(t)(U8))()): _*)()
      ): _*
    )().tchk()

    val lowered = original.lower()
    assert(lowered.typ == TyStm(TyVec((U8, U8), 3), 4))
    assert(lowered == expected)
  }

  test("VecLiteral:Vec[Stm[U8, 3], 0]") {
    val original = VecLiteral()(TyVec(TyStm(U8, 3), 0))
    val expected =
      StmLiteral((0 until 3).map(_ => VecLiteral()(TyVec(U8, 0))): _*)().tchk()
    val lowered = original.lower()
    assert(lowered == expected)
    assert(lowered.typ == TyStm(TyVec(U8, 0), 3))
  }

  test("VecLiteral:Vec[Stm[U8, 0], 3]") {
    val original =
      VecLiteral((0 until 3).map(_ => StmLiteral()(TyStm(U8, 0))): _*)().tchk()
    val expected = StmLiteral()(TyStm(TyVec(U8, 3), 0))
    val lowered = original.lower()
    assert(lowered == expected)
    assert(lowered.typ == TyStm(TyVec(U8, 3), 0))
  }

  /* Flatten nested streams ***************************************************/

  test("StmLiteral:Stm[Stm[(U8, U8), 3], 2]") {
    val original = StmLiteral(
      (0 until 2).map(i =>
        StmLiteral(
          (0 until 3).map(j => Tuple(C(i)(U8), C(j)(U8))()): _*
        )()
      ): _*
    )().tchk()
    val expected = StmLiteral(
      (0 until 6).map({ t =>
        val i = t / 3
        val j = t % 3
        Tuple(C(i)(U8), C(j)(U8))()
      }): _*
    )().tchk()

    val lowered = original.lower()
    assert(lowered.typ == TyStm((U8, U8), 6))
    assert(lowered == expected)
  }

  test("StmLiteral:Stm[Stm[U8, 3], 0]") {
    val original = StmLiteral()(TyStm(TyStm(U8, 3), 0))
    val expected = StmLiteral()(TyStm(U8, 0))
    val lowered = original.lower()
    assert(lowered == expected)
    assert(lowered.typ == TyStm(U8, 0))
  }

  test("StmLiteral:Stm[Stm[U8, 0], 3]") {
    val original =
      StmLiteral((0 until 3).map(_ => StmLiteral()(TyStm(U8, 0))): _*)().tchk()
    val expected = StmLiteral()(TyStm(U8, 0))
    val lowered = original.lower()
    assert(lowered == expected)
    assert(lowered.typ == TyStm(U8, 0))
  }

  /* Multi-step examples ******************************************************/

  test("StmLiteral:Stm[Vec[Stm[(U8, U8, U8), 4], 3], 2]") {
    val original = StmLiteral(
      (0 until 2).map(i =>
        VecLiteral(
          (0 until 3).map(j =>
            StmLiteral(
              (0 until 4).map(k => Tuple(C(i)(U8), C(j)(U8), C(k)(U8))()): _*
            )()
          ): _*
        )()
      ): _*
    )().tchk()
    val expected = StmLiteral(
      (0 until 8).map(t =>
        VecLiteral(
          (0 until 3).map({ j =>
            val i = t / 4
            val k = t % 4
            Tuple(C(i)(U8), C(j)(U8), C(k)(U8))()
          }): _*
        )()
      ): _*
    )().tchk()

    val lowered = original.lower().asInstanceOf[StmLiteral]
    assert(lowered.typ == TyStm(TyVec((U8, U8, U8), 3), 8))
    assert(lowered == expected)

    val stmBuild = lowered.toStmBuild
    assert(stmBuild.typ == TyStm(TyVec((U8, U8, U8), 3), 8))
    assert(mhir.ir.eval(stmBuild) == expected)
  }

  test("VecLiteral:Vec[Vec[Stm[(U8, U8, U8), 2], 3], 4]") {
    val original = VecLiteral(
      (0 until 4).map(i =>
        VecLiteral(
          (0 until 3).map(j =>
            StmLiteral(
              (0 until 2).map(k => Tuple(C(i)(U8), C(j)(U8), C(k)(U8))()): _*
            )()
          ): _*
        )()
      ): _*
    )().tchk()
    val expected = StmLiteral(
      (0 until 2).map(k =>
        VecLiteral(
          (0 until 4).map(i =>
            VecLiteral(
              (0 until 3).map(j => Tuple(C(i)(U8), C(j)(U8), C(k)(U8))()): _*
            )()
          ): _*
        )()
      ): _*
    )()
    val lowered = original.lower()
    assert(lowered.typ == TyStm(TyVec(TyVec((U8, U8, U8), 3), 4), 2))
    assert(lowered == expected)
  }
}
