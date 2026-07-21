package mhir.gen.vhdl
package transform

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.sugar.{Cast, ExprLowering, SmartLessThan}
import mhir.typecheck.TypeCheck

/** Add bounds checking where necessary for vector accesses.
  *
  * ModelSim/QuestaSim will complain about out-of-bounds array accesses. This
  * seems to be the case even if the result is never used (as in, it feeds into
  * a multiplexer that discards it).
  */
object BoundsCheckInsertion {

  private val logger: Logger = Logger(getClass.getName)

  def apply(s: GenStmBuild): GenStmBuild = {
    GenStmBuild(
      data = this.apply(s.data),
      valid = this.apply(s.valid),
      accumulators = s.accumulators.map({ case (x, acc: Accumulator) =>
        x -> acc.map(this.apply)
      }),
      producers = s.producers.map({ case (x, (p, ready)) =>
        x -> (p, this.apply(ready))
      }),
      intermediates = s.intermediates.map({ case (x, i) =>
        x -> this.apply(i)
      })
    )
  }

  private def apply(i: Intermediate): Intermediate = {
    i match {
      case i: DataIntermediate => i.map(this.apply)
      case _: IpBlockInst =>
        throw new AssertionError(
          "there shouldn't be any IP blocks yet at this compilation stage"
        )
      case FunctionIntermediate(params, intermediates, body) =>
        FunctionIntermediate(
          params,
          intermediates
            .map({ case (x, i) =>
              x -> this.apply(i).asInstanceOf[IntermediateInFunction]
            }),
          this.apply(body)
        )
    }
  }

  def apply(e: Expr): Expr = {
    val result = e match {
      case va @ VecAccess(v, i) =>
        v.typ.asInstanceOf[TyVec] match {
          case TyVec(_, IntCst(n)) if n <= 0 =>
            logger.warn(
              s"access in vector of length 0 is definitely out of bounds"
            )
            Undefined(va.typ)
          case TyVec(_, IntCst(n)) =>
            i match {
              case IntCst(i) =>
                if (i >= 0 && i < n) {
                  va
                } else {
                  logger.warn(
                    s"access to index $i in vector of length $n is definitely out of bounds"
                  )
                  Undefined(va.typ)
                }
              case i =>
                if (isPowerOfTwo(n)) {
                  val requiredTyp = TyAnyInt.tightest(0, n - 1)
                  VecAccess(v, Cast(i, requiredTyp)())().tchk().lower
                } else {
                  val boundedIdx = Mux(
                    SmartLessThan(i, C(n)())(),
                    i,
                    Undefined(i.typ)
                  )().tchk().lower
                  VecAccess(v, boundedIdx)().tchk()
                }
            }
          case TyVec(_, n) =>
            val boundedIdx = Mux(
              SmartLessThan(i, n)(),
              i,
              Undefined(i.typ)
            )().tchk().lower
            VecAccess(v, boundedIdx)().tchk()
        }
      case e => e.map(this.apply).tchk()
    }
    assert(result.hasType)
    result
  }

  /** Decides whether the given number is a power of two.
    */
  private def isPowerOfTwo(n: Long): Boolean = {
    // https://stackoverflow.com/a/19383296
    //
    // Positive example:
    //   n     : 00010000
    //   n - 1 : 00001111
    //   &     : 00000000
    //
    // Negative example:
    //   n     : 00010001
    //   n - 1 : 00010000
    //   &     : 00010000
    (n > 0) && ((n & (n - 1)) == 0)
  }
}
