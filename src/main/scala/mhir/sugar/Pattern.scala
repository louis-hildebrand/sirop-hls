package mhir.sugar

import mhir.ir._
import mhir.typecheck.TypeError

/** A pattern for matching parameters (e.g., in a function that accepts a
  * tuple).
  */
sealed abstract class Pattern(children: Expr*)(typ: Type)
    extends SyntaxSugar(children: _*)(typ) {

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    if (this.typ == Missing) {
      throw new TypeError("missing type annotations for pattern")
    }
    this
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    throw new RuntimeException("patterns should not be lowered")
  }

  def toParam: Param = {
    this match {
      case _: TuplePattern =>
        val names = this.paramPrefixes
        val newName = if (names.isEmpty) {
          "_"
        } else {
          names.mkString("_")
        }
        Param(newName)(this.typ)
      case ParamPattern(x) => x
    }
  }

  def params: Seq[Param] = {
    this match {
      case TuplePattern(elems @ _*) => elems.flatMap(_.params)
      case ParamPattern(x)          => Seq(x)
    }
  }

  def paramPrefixes: Seq[String] = {
    this.params.map(_.prefix)
  }

  def mergeType(typ: Type): Pattern = {
    (this, typ) match {
      case (p @ ParamPattern(x), typ) =>
        if (x.hasType) p else ParamPattern(x.rebuild(typ).asInstanceOf[Param])
      case (TuplePattern(thisElems @ _*), TyTuple(typElems @ _*))
          if thisElems.length == typElems.length =>
        TuplePattern(
          thisElems.zip(typElems).map({ case (p, t) => p.mergeType(t) }): _*
        )
      case _ =>
        this
    }
  }

  def rename(subs: Map[Param, Param]): Pattern = {
    this match {
      case ParamPattern(x) =>
        ParamPattern(subs.getOrElse(x, x))
      case TuplePattern(elems @ _*) =>
        TuplePattern(elems.map(_.rename(subs)): _*)
    }
  }

  override def displayOneLine(): String = {
    this match {
      case ParamPattern(x) =>
        x.typ match {
          case Missing => x.name
          case _       => s"${x.name}: ${x.typ}"
        }
      case TuplePattern(oneElem) =>
        s"(${oneElem.displayOneLine()},)"
      case TuplePattern(elems @ _*) =>
        elems.map(_.toString).mkString("(", ", ", ")")
    }
  }

  override def displayMultiLine(maxWidth: Int): String = {
    // Patterns shouldn't normally be that big, so just keep it on one line
    this.displayOneLine()
  }
}

/** A pattern that matches a tuple.
  *
  * @param elems
  *   the pattern for each tuple element.
  */
case class TuplePattern(elems: Pattern*)
    extends Pattern(elems: _*)(TuplePattern.chooseType(elems)) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    if (newChildren.exists(e => !e.isInstanceOf[Pattern])) {
      throw new BadRebuildError(this, newChildren)
    } else {
      val result = TuplePattern(newChildren.map(_.asInstanceOf[Pattern]): _*)
      assert(
        typ == Missing || typ == result.typ,
        s"wrong type for $className: expected ${result.typ}, got $typ"
      )
      result
    }
  }
}

/** Companion object for [[TuplePattern]].
  */
object TuplePattern {

  private def chooseType(children: Seq[Expr]): Type = {
    if (children.exists(!_.hasType)) {
      Missing
    } else {
      TyTuple(children.map(_.typ): _*)
    }
  }
}

/** A pattern that is just a single parameter.
  */
case class ParamPattern(x: Param) extends Pattern(x)(x.typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x: Param) =>
        val result = ParamPattern(x)
        assert(
          typ == Missing || typ == result.typ,
          s"wrong type for $className: expected ${result.typ}, got $typ"
        )
        result
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }
}
