package opt

import ir._

import org.scalatest.funsuite.AnyFunSuite

class StmUtilsTests extends AnyFunSuite {
  test("SuccessfullyRemoveAccumulatorElems") {
    val b = Param("b")
    val s = StmBuild(
      2,
      Tuple(0, 0, 0, 0, False),
      (acc: Expr) =>
        Tuple(
          IfThenElse(
            b,
            Tuple(
              acc.__0 + 1,
              acc.__1 + 1,
              acc.__0,
              acc.__3 - 2,
              Not(acc.__4)
            ),
            acc
          ),
          SSome(acc.__1)
        )
    )
    val actual =
      StmUtils.removeAccumulatorElemsByIndex(s, indicesToRemove = Seq(0, 2))
    val expected = StmBuild(
      2,
      Tuple(0, 0, False),
      (acc: Expr) =>
        Tuple(
          IfThenElse(
            b,
            Tuple(acc.__0 + 1, acc.__1 - 2, Not(acc.__2)),
            Tuple(acc.__0, acc.__1, acc.__2)
          ),
          SSome(acc.__0)
        )
    )
    assert(actual == expected)
  }

  test("TryToRemoveAccumulatorElemStillInUse1") {
    val s = StmBuild(
      5,
      Tuple(0, 0),
      (acc: Expr) =>
        Tuple(Tuple(acc.__0 + 1, acc.__1 + 1), SSome(Tuple(acc.__0, acc.__1)))
    )
    assertThrows[ElemStillInUseException.type](
      StmUtils.removeAccumulatorElemsByIndex(s, indicesToRemove = Seq(0))
    )
  }

  test("TryToRemoveAccumulatorElemStillInUse2") {
    val s = StmBuild(
      5,
      Tuple(0, 0),
      (acc: Expr) =>
        Tuple(Tuple(acc.__0 + acc.__1, acc.__1 + 1), SSome(acc.__0))
    )
    assertThrows[ElemStillInUseException.type](
      StmUtils.removeAccumulatorElemsByIndex(s, indicesToRemove = Seq(1))
    )
  }
}
