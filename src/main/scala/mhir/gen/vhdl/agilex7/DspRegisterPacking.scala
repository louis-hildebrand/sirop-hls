package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.gen.vhdl.ir._
import mhir.gen.vhdl.transform.RemoveUnused
import mhir.ir._
import mhir.matchers.VecAccesses
import mhir.typecheck._

object DspRegisterPacking {

  def apply(s: GenStmBuild): GenStmBuild = {
    val s1 = this.mergeRegistersIntoDsps(s)
    // After DSP register packing, some registers may no longer be needed
    val s2 = RemoveUnused.apply(s1)
    s2
  }

  /** Perform DSP register packing, i.e., take registers from outside the DSPs
    * and bring them into the DSPs.
    */
  private def mergeRegistersIntoDsps(s: GenStmBuild): GenStmBuild = {
    GenStmBuild(
      data = s.data,
      valid = s.valid,
      accumulators = s.accumulators,
      producers = s.producers,
      intermediates = s.intermediates.map({
        case intermediate @ (
              x,
              AgilexMac2(ax, ay, bx, by, chainin, pipeline)
            ) =>
          assert(pipeline >= 0)
          assert(pipeline <= 3)
          // How long is the shift register before each input?
          val gobble = math.min(
            3 - pipeline,
            Seq(ax, ay, bx, by).map(countAvailableRegisters(s, _)).min
          )
          // Perform the transformation
          if (gobble <= 0) {
            intermediate
          } else {
            val newMac = AgilexMac2(
              ax = shortenShiftRegister(s, ax, gobble),
              ay = shortenShiftRegister(s, ay, gobble),
              bx = shortenShiftRegister(s, bx, gobble),
              by = shortenShiftRegister(s, by, gobble),
              chainin,
              pipeline + gobble
            )
            x -> newMac
          }
        case intermediate @ (x, AgilexMadd(ax, ay, bx, by, pipeline)) =>
          assert(pipeline >= 0)
          assert(pipeline <= 0)
          // How long is the shift register before each input?
          val gobble = math.min(
            3 - pipeline,
            Seq(ax, ay, bx, by).map(countAvailableRegisters(s, _)).min
          )
          // Perform the transformation
          if (gobble <= 0) {
            intermediate
          } else {
            val newMadd = AgilexMadd(
              ax = shortenShiftRegister(s, ax, gobble),
              ay = shortenShiftRegister(s, ay, gobble),
              bx = shortenShiftRegister(s, bx, gobble),
              by = shortenShiftRegister(s, by, gobble),
              pipeline + gobble
            )
            x -> newMadd
          }
        case i =>
          i
      })
    )
  }

  private def countAvailableRegisters(s: GenStmBuild, input: Expr): Int = {
    input match {
      case _: IntCst => Int.MaxValue
      case VecAccesses(v: Param, Seq(IntCst(i), _*)) =>
        s.accumulators
          .get(v)
          .collect({ case VecShiftLeftAccumulator(n, None, _) =>
            (n - i).toInt
          })
          .getOrElse(0)
      case _ => 0
    }
  }

  private def shortenShiftRegister(
      s: GenStmBuild,
      input: Expr,
      gobble: Int
  ): Expr = {
    input match {
      case i: IntCst => i
      case VecAccesses(pipe: Param, Seq(IntCst(i), otherIndices @ _*)) =>
        val (len, pipeInput) = s.accumulators
          .get(pipe)
          .collect({ case VecShiftLeftAccumulator(len, None, pipeInput) =>
            (len, pipeInput)
          })
          .get
        val newIndex = i + gobble
        assert(len >= 0)
        assert(newIndex >= 0)
        assert(newIndex <= len)
        val newInput = if (newIndex == len) {
          // Bypass the shift register altogether
          VecAccesses(pipeInput, otherIndices).tchk()
        } else {
          VecAccesses(pipe, C(newIndex)() +: otherIndices).tchk()
        }
        assert(input.hasType, s"missing type for input")
        assert(
          newInput.typ == input.typ,
          s"input started with type ${input.typ} but now has type ${newInput.typ}"
        )
        newInput
      case e =>
        throw new IllegalArgumentException(
          s"invalid argument to shortenShiftRegister: $e"
        )
    }
  }
}
