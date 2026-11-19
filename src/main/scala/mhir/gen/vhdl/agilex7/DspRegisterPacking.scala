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
      producers = s.producers,
      intermediates = s.intermediates.map({
        case intermediate @ (
              x,
              ForGenerateIntermediate(
                vars,
                bounds,
                AgilexMac2(ax, ay, bx, by, chainin, pipeline)
              )
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
            x -> ForGenerateIntermediate(vars, bounds, newMac)
          }
        case intermediate @ (
              x,
              ForGenerateIntermediate(
                vars,
                bounds,
                AgilexMadd(ax, ay, bx, by, pipeline)
              )
            ) =>
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
            x -> ForGenerateIntermediate(vars, bounds, newMadd)
          }
        case i =>
          i
      })
    )
  }

  private def countAvailableRegisters(s: GenStmBuild, input: Expr): Int = {
    input match {
      case _: IntCst => Int.MaxValue
      case VecAccesses(v: Param, vecAccessIndices) =>
        s.intermediates
          .get(v)
          .map({
            case ForGenerateIntermediate(
                  loopVars,
                  _,
                  VecShiftLeftAccumulator(n, None, _)
                ) =>
              vecAccessIndices.drop(loopVars.length).headOption match {
                case Some(IntCst(i)) => (n - i).toInt
                case _               => 0
              }
            case _ => 0
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
      case VecAccesses(pipe: Param, vecAccessIndices) =>
        s.intermediates
          .get(pipe)
          .collect({
            case ForGenerateIntermediate(
                  loopVars,
                  _,
                  VecShiftLeftAccumulator(len, None, pipeInputInLoop)
                ) =>
              assert(loopVars.length < vecAccessIndices.length)
              val IntCst(indexInShiftReg) = vecAccessIndices(loopVars.length)
              val otherIndices = vecAccessIndices.drop(loopVars.length + 1)
              val newIndex = indexInShiftReg + gobble
              assert(len >= 0)
              assert(newIndex >= 0)
              assert(newIndex <= len)
              val newInput = if (newIndex == len) {
                // pipeInput may refer to the loop variables, so pulling it out just like that
                // may not be valid.
                // We need to update the indices to use the ones from the VecAccess instead of
                // the loop variables.
                val subs = loopVars.zip(vecAccessIndices).toMap[Expr, Expr]
                val pipeInput = pipeInputInLoop.subPreserveType(subs)
                // Bypass the shift register altogether
                VecAccesses(pipeInput, otherIndices).tchk()
              } else {
                val shiftReg =
                  VecAccesses(pipe, vecAccessIndices.take(loopVars.length))
                VecAccesses(shiftReg, C(newIndex)() +: otherIndices).tchk()
              }
              assert(input.hasType, s"missing type for input")
              assert(
                newInput.typ == input.typ,
                s"input started with type ${input.typ} but now has type ${newInput.typ}"
              )
              newInput
          })
          .get
      case e =>
        throw new IllegalArgumentException(
          s"invalid argument to shortenShiftRegister: $e"
        )
    }
  }
}
