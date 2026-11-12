package mhir.gen.vhdl
package transform

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.gen.vhdl.ir._
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
        x -> acc.map(this.apply(_))
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
      case i: DataIntermediate => i.map(this.apply(_, Map()))
      case ForGenerateIntermediate(_, _, _, _, _: IpBlockInst) =>
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
          this.apply(body, Map())
        )
    }
  }

  def apply(e: Expr, paramBoundsExclusive: Map[Param, Long] = Map()): Expr = {
    val result = e match {
      case va: VecAccess =>
        // Try to avoid adding bounds checking, since it makes later passes (e.g., DSP selection)
        // more difficult and it clutters up the generated VHDL
        skipIfVecLengthIsZero(va)
          .orElse(skipIfIndexIsIntCst(va))
          .orElse(skipIfIndexIsStaticallyBounded(va, paramBoundsExclusive))
          .orElse(skipIfVecLengthIsPowerOfTwo(va))
          .getOrElse(addBoundsChecking(va))
      // TODO: Also add a case for Mux where the condition looks like LessThan(_: Param, _)?
      case VecBuild(n @ IntCst(nLong), Function(i, body)) =>
        val newParamBounds = paramBoundsExclusive + (i -> nLong)
        VecBuild(n, Function(i, this.apply(body, newParamBounds))())().tchk()
      case e => e.map(this.apply(_, paramBoundsExclusive)).tchk()
    }
    assert(result.hasType)
    result
  }

  /** If the vector length is zero, any vector access will be out of bounds.
    */
  private def skipIfVecLengthIsZero(va: VecAccess): Option[Expr] = {
    va.vec.typ match {
      case TyVec(_, IntCst(n)) if n <= 0 =>
        logger.warn(
          s"access in vector of length 0 is definitely out of bounds"
        )
        Some(Undefined(va.typ))
      case _ => None
    }
  }

  /** If the index and vector length are both constants, we can statically
    * decide whether the vec access is out of bounds.
    */
  private def skipIfIndexIsIntCst(va: VecAccess): Option[Expr] = {
    (va.vec.typ, va) match {
      case (TyVec(_, IntCst(n)), VecAccess(_, IntCst(i))) =>
        if (i >= 0 && i < n) {
          Some(va)
        } else {
          logger.warn(
            s"access to index $i in vector of length $n is definitely out of bounds"
          )
          Some(Undefined(va.typ))
        }
      case _ => None
    }
  }

  /** If the index is a static variable and its upper bound is known to be
    * within the vector, we can skip bounds checking.
    */
  private def skipIfIndexIsStaticallyBounded(
      va: VecAccess,
      paramBoundsExclusive: Map[Param, Long]
  ): Option[Expr] = {
    // TODO: What if the index is something like `i + 1`, where `i` is static?
    va match {
      case VecAccess(vec, i: Param) =>
        (vec.typ, paramBoundsExclusive.get(i)) match {
          case (TyVec(_, IntCst(n)), Some(m)) =>
            val maxValueOfI = m - 1 // since the upper bound is exclusive
            val maxIndexInVec = n - 1 // since vectors are 0-indexed
            if (maxValueOfI <= maxIndexInVec) Some(va) else None
          case _ => None
        }
      case _ => None
    }
  }

  /** If the vector length is a power of two, we can simply truncate the index
    * so it'll never be out of bounds.
    */
  private def skipIfVecLengthIsPowerOfTwo(va: VecAccess): Option[Expr] = {
    va.vec.typ match {
      case TyVec(_, IntCst(n)) if isPowerOfTwo(n) =>
        val requiredTyp = TyAnyInt.tightest(0, n - 1)
        val VecAccess(v, i) = va
        Some(VecAccess(v, Cast(i, requiredTyp)())().tchk().lower)
      case _ => None
    }
  }

  private def addBoundsChecking(va: VecAccess): Expr = {
    val TyVec(_, n) = va.vec.typ
    val VecAccess(v, i) = va
    val boundedIdx =
      Mux(SmartLessThan(i, n)(), i, Undefined(i.typ))().tchk().lower
    VecAccess(v, boundedIdx)().tchk()
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
