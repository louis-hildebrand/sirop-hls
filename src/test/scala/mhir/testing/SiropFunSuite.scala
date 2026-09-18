package mhir.testing

import mhir.canonicalize._
import mhir.ir._
import mhir.optimize.PartialEvalPass
import mhir.parse.sirop.Parser
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class SiropFunSuite extends AnyFunSuite {

  def makeSbuild(
      src: String,
      context: Map[Param, Type],
      annotations: Set[StmBuildAnnotation] = Set()
  ): StmBuild = {
    val expr = Parser.parse(src).body.asInstanceOf[StmBuild]
    val withAnnotations = annotations
      .foldLeft(expr)({ case (e, a) => e.annotate(a) })
    PartialEvalPass
      .partialEval(withAnnotations.tchk(context, Map()).lower)
      .asInstanceOf[StmBuild]
  }

  def assertSameVal(
      actual: Expr,
      expected: Expr,
      handshake: Boolean,
      inputs: Map[Param, Expr]
  ): Unit = {
    val expectedVal =
      mhir.eval.eval(expected, handshake = handshake, inputs = inputs)
    val actualVal =
      mhir.eval.eval(actual, handshake = handshake, inputs = inputs)
    assert(valueMatches(actualVal, expectedVal))
  }

  private def valueMatches(actual: Expr, expected: Expr): Boolean = {
    (actual, expected) match {
      case (actual, expected) if actual == expected => true
      // It's fine to replace undefined with a concrete value
      case (_, _: Undefined) => true
      // It's NOT fine to replace a concrete value with undefined
      // (Maybe this check isn't strictly necessary, but it's nice to be explicit about it.)
      case (_: Undefined, _) => false
      case (
            StmLiteral(actualPhysical, actualLogical),
            StmLiteral(expectedPhysical, expectedLogical)
          ) =>
        seqMatches(actualPhysical, expectedPhysical) &&
        seqMatches(actualLogical, expectedLogical)
      case (VecLiteral(actualElems @ _*), VecLiteral(expectedElems @ _*)) =>
        seqMatches(actualElems, expectedElems)
      case (Tuple(actualElems @ _*), Tuple(expectedElems @ _*)) =>
        seqMatches(actualElems, expectedElems)
      case _ => false
    }
  }

  private def seqMatches(
      actualElems: Seq[Expr],
      expectedElems: Seq[Expr]
  ): Boolean = {
    actualElems.length == expectedElems.length &&
    actualElems.zip(expectedElems).forall({ case (x, y) => valueMatches(x, y) })
  }

  def assertSameAccumulators(
      actual: StmBuild,
      expected: StmBuild
  ): Unit = {
    // Include the types separately because two params with the same name but
    // different types will be considered syntactically equal.
    val expectedAccumulators = expected.accumulators.keySet.map(x => (x, x.typ))
    val actualAccumulators = actual.accumulators.keySet.map(x => (x, x.typ))
    assert(actualAccumulators == expectedAccumulators)
  }

  def counterWithPrefix(
      n: Int,
      start: Long,
      elemTyp: TyAnyInt = U8,
      prefixLen: Int = 2,
      prefixStart: Long = 150
  ): StmLiteral = {
    StmLiteral(
      (0 until prefixLen).map(prefixStart + _).map(C(_)(elemTyp)),
      (0 until n).map(start + _).map(C(_)(elemTyp))
    )(Missing).tchk().asInstanceOf[StmLiteral]
  }
}
