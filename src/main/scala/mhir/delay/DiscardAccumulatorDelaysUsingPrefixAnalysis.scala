package mhir.delay

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._

/** Transformation that discards accumulator delays if it can prove the
  * accumulator's initial value will remain the same without any extra logic.
  *
  * This transformation uses the [[PrefixAnalysis]] to find the prefix pattern
  * for each stream. Suppose that, using this information, an accumulator's
  * `next` value will be the same as its `init` value. Then that accumulator's
  * initial conditions will be satisfied even without adding a reference to a
  * `go` stream (as in [[ReplaceAccumulatorDelaysWithGo]]), so the delay can be
  * discarded.
  *
  * For example, consider [[mhir.sugar.StmSlideStartingWith]] with the initial
  * buffer being all `false`. Suppose the prefix pattern for the input is also
  * `false`. Then, if we just let the design run, the buffer will stay full of
  * `false` (assuming the prefix pattern is correct).
  */
object DiscardAccumulatorDelaysUsingPrefixAnalysis {

  def apply(f: Expr, headByParam: Map[Param, Expr]): Expr = {
    val (inputs, body) = TypeChecker.unwrapTopLevelFunction(f)
    val patternByParam = headByParam.map({ case (x, e) =>
      x -> ParamPrefixPattern(Some(e), x)
    })
    val prefixPatterns = PrefixAnalysis.findPrefixPatterns(body, patternByParam)
    val newBody = this.apply(body, prefixPatterns)
    TypeChecker.wrapTopLevelFunction(inputs, newBody)
  }

  private def apply(e: Expr, prefixPattern: PrefixPattern): Expr = {
    prefixPattern match {
      case _: ParamPrefixPattern      => e
      case _: StmLiteralPrefixPattern => e
      case LetStmPrefixPattern(inPattern, outPattern) =>
        val LetStm(bufSize, x, in, out) = e
        LetStm(
          bufSize,
          x,
          this.apply(in, inPattern),
          this.apply(out, outPattern)
        )().tchk()
      case StmBuildPrefixPattern(_, _, producerPatterns) =>
        val s = e.asInstanceOf[StmBuild]
        assert(producerPatterns.keySet == s.producers.keySet)
        val newProducers = s.producers.map({ case (x, (stm, ready, delay)) =>
          val prefixPattern = producerPatterns(x)
          x -> (this.apply(stm, prefixPattern), ready, delay)
        })
        val delaysToErase = findDelaysToErase(
          s,
          producerPatterns.mapValues(p => p.pattern.getOrElse(Undefined(p.typ)))
        )
        val newAccumulators = s.accumulators.map({
          case (x, (init, next, _)) if delaysToErase.contains(x) =>
            x -> (init, next, Tuple()().tchk())
          case eqn => eqn
        })
        s.copy(accumulators = newAccumulators, producers = newProducers)(
          typ = Missing,
          annotations = s.annotations
        ).tchk()
    }
  }

  private def findDelaysToErase(
      s: StmBuild,
      producerPatterns: Map[Param, Expr]
  ): Set[Param] = {
    s.accumulators
      .flatMap({
        case (x, (_: Undefined, _, _)) =>
          Some(x)
        case (x, (init, next, delay)) =>
          val initVal = mhir.eval.eval(init)
          val nextVal =
            PrefixAnalysis.evalBeforeTime(next, s, producerPatterns, delay)
          if (nextVal == initVal) {
            Some(x)
          } else {
            None
          }
      })
      .toSet
  }
}
