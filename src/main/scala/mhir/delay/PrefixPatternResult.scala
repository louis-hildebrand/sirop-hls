package mhir.delay

import mhir.ir._

sealed trait PrefixPattern {

  /** The pattern that all elements of the physical prefix follow.
    *
    * If the pattern is `None`, it means the pattern is missing (e.g., because
    * none was specified for a top-level parameter, because a stream literal
    * with no physical prefix appears in the source code, etc.)
    *
    * @example
    *   if the pattern is `(undefined, false)`, it means (1) all elements of the
    *   physical prefix are tuples, (2) we don't know what the first tuple
    *   element will be, and (3) the second tuple element will always be
    *   `false`.
    */
  def pattern: Option[Expr]

  /** The element type of this stream.
    */
  def typ: Type

  assert(typ.isData, s"prefix pattern type should always be data, not $typ")
}

/** Prefix pattern for a stream literal.
  *
  * @param pattern
  *   the prefix pattern.
  * @param display
  *   a visual representation of the stream literal (e.g., the first few
  *   elements) so that a helpful warning message can be emitted if no pattern
  *   is specified.
  */
case class StmLiteralPrefixPattern(
    pattern: Option[Expr],
    typ: Type,
    display: String
) extends PrefixPattern

/** Prefix pattern for a parameter.
  *
  * @param pattern
  *   the prefix pattern.
  * @param param
  *   the parameter (included so a helpful warning message can be emitted if no
  *   pattern is specified).
  */
case class ParamPrefixPattern(pattern: Option[Expr], param: Param)
    extends PrefixPattern {

  override def typ: Type = {
    val TyStm(elemTyp, _) = param.typ
    elemTyp
  }
}

/** Prefix pattern for a [[mhir.ir.StmBuild]] expression.
  *
  * @param pattern
  *   the prefix pattern for the output of the [[mhir.ir.StmBuild]].
  * @param producerPatterns
  *   the prefix pattern for each producer.
  */
case class StmBuildPrefixPattern(
    pattern: Some[Expr],
    typ: Type,
    producerPatterns: Map[Param, PrefixPattern]
) extends PrefixPattern

/** Prefix pattern for a [[mhir.ir.LetStm]] expression.
  *
  * @param pattern
  *   the prefix pattern for the output of the [[mhir.ir.LetStm]].
  * @param inPattern
  *   the prefix pattern for the input of the [[mhir.ir.LetStm]].
  */
case class LetStmPrefixPattern(in: PrefixPattern, out: PrefixPattern)
    extends PrefixPattern {

  override def pattern: Option[Expr] = out.pattern

  override def typ: Type = out.typ
}
