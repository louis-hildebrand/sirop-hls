package mhir.delay

import mhir.canonicalize._
import mhir.ir._
import mhir.optimize.PartialEvalPass
import mhir.sugar.{ExprLowering, SafeSum}
import mhir.typecheck._

/** Analysis that tries to determine the form of the elements in the physical
  * prefix of a given stream.
  *
  * For example, for a stream `[(0, false), (1, false)]s ++ [...]s`, this
  * analysis will find that the prefix pattern is `(undefined, false)`.
  */
object PrefixAnalysis {

  def findPrefixPatterns(
      e: Expr,
      patternByParam: Map[Param, PrefixPattern] = Map()
  ): PrefixPattern = {
    val TyStm(elemTyp, _) = e.typ
    e match {
      case x: Param =>
        val pattern = patternByParam.get(x).flatMap(_.pattern)
        ParamPrefixPattern(pattern, x)
      case StmLiteral(Seq(), logical) =>
        StmLiteralPrefixPattern(None, elemTyp, summarizeStmLiteral(logical))
      case StmLiteral(physical, logical) =>
        val pattern = physical.reduce(this.intersectPatterns)
        StmLiteralPrefixPattern(
          Some(pattern),
          elemTyp,
          summarizeStmLiteral(logical)
        )
      case s: StmBuild =>
        val producerPatterns = s.producers.map({ case (x, (stm, _, _)) =>
          x -> findPrefixPatterns(stm, patternByParam)
        })
        val pattern = findStmBuildPrefixPattern(
          s,
          producerPatterns.mapValues(p => p.pattern.getOrElse(Undefined(p.typ)))
        )
        StmBuildPrefixPattern(Some(pattern), elemTyp, producerPatterns)
      case LetStm(_, x, in, out) =>
        val inPattern = findPrefixPatterns(in, patternByParam)
        val outPattern = findPrefixPatterns(
          out,
          patternByParam + (x -> ParamPrefixPattern(inPattern.pattern, x))
        )
        LetStmPrefixPattern(inPattern, outPattern)
      case e =>
        throw new IllegalArgumentException(s"cannot find prefix pattern for $e")
    }
  }

  def evalBeforeTime(
      e: Expr,
      s: StmBuild,
      producerPatterns: Map[Param, Expr],
      timeLimit: Expr
  ): Expr = {
    val producerSubs = producerPatterns
      .map({
        case (x, u: Undefined) => x -> u
        case (x, e) =>
          val (_, _, xDelay) = s.producers(x)
          // Can we assume sdata(x) will match the prefix pattern for x?
          // It depends on the output and producer delay annotations.
          // For example, in StmDrop, some of the elements in the physical
          // prefix will come from the logical part of the input sequence, so
          // we can't use the prefix pattern there.
          val canUsePrefix = (timeLimit.typ, xDelay.typ) match {
            case (_: TyAnyInt, _: TyAnyInt) =>
              PartialEvalPass
                .isSmallerOrEqual(
                  timeLimit,
                  SafeSum(xDelay, C(1)())().tchk().lower
                )()
                .getOrElse(false)
            case _ => false
          }
          if (canUsePrefix) {
            x -> e
          } else {
            x -> Undefined(e.typ)
          }
      })
      .map({ case (x, e) => StmData(x)().tchk() -> e })
    val accumulatorSubs = s.accumulators
      .map({ case (x, (init, _, xDelay)) =>
        // Can we assume x will equal init when calculating nextData?
        // Only if the delay annotation for this accumulator is greater than
        // or equal to the output delay annotation.
        // Otherwise, the accumulator will be allowed to update itself, and
        // more work would be required to prove that its value remains stable.
        val canUseInit = (timeLimit.typ, xDelay.typ) match {
          case (_: TyAnyInt, _: TyAnyInt) =>
            PartialEvalPass
              .isSmallerOrEqual(timeLimit, xDelay)()
              .getOrElse(false)
          case _ => false
        }
        if (canUseInit) {
          x -> init
        } else {
          x -> Undefined(init.typ)
        }
      })
    val subs = accumulatorSubs ++ producerSubs
    mhir.eval.eval(e.subPreserveType(subs))
  }

  private def findStmBuildPrefixPattern(
      s: StmBuild,
      producerPatterns: Map[Param, Expr]
  ): Expr = {
    // TODO: speed this up by returning early if initData is undefined?
    val initData = mhir.eval.eval(s.initData)
    val nextData =
      this.evalBeforeTime(s.nextData, s, producerPatterns, timeLimit = s.delay)
    intersectPatterns(initData, nextData)
  }

  private def summarizeStmLiteral(logical: Seq[Expr]): String = {
    if (logical.length > 3) {
      logical.take(3).mkString("[", ", ", ", ...]s")
    } else {
      logical.mkString("[", ", ", "]s")
    }
  }

  private def intersectPatterns(a: Expr, b: Expr): Expr = {
    assert(
      (a.typ == b.typ) && (a.typ != Missing),
      s"patterns to intersect should have the same type (but found ${a.typ} and ${b.typ})"
    )
    (a, b) match {
      case (a, b) if a == b => a
      case (Tuple(aElems @ _*), Tuple(bElems @ _*)) =>
        val newElems = aElems
          .zip(bElems)
          .map({ case (a, b) => intersectPatterns(a, b) })
        if (newElems.forall(_.isInstanceOf[Undefined])) {
          Undefined(a.typ)
        } else {
          Tuple(newElems: _*)().tchk()
        }
      case _ => Undefined(a.typ)
    }
  }
}
