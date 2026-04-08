package mhir.gen.vhdl

import mhir.canonicalize._
import mhir.ir.typecheck.{TypeCheck, TypeError}
import mhir.ir.{ExprPrinter => EP, _}
import mhir.sugar._

/** A `let` expression for streams which does <i>not</i> allow sharing.
  *
  * Like [[mhir.ir.LetStm]], `in` and `out` must both be streams and `out` can
  * mention `in`. However, unlike with [[mhir.ir.LetStm]], `in` can be used at
  * most once in `out`.
  *
  * This expression is useful for representing a stream pipeline in ANF.
  */
case class LetInlineStm(x: Param, in: Expr, out: Expr)(typ: Type = Missing)
    extends SyntaxSugar(x, in, out)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x: Param, in, out) => LetInlineStm(x, in, out)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val in = this.in.tchk
    val x = this.x.typ match {
      case Missing =>
        this.x.rebuild(in.typ).asInstanceOf[Param]
      case t =>
        if (t ~= in.typ) {
          this.x
        } else {
          throw new TypeError(
            s"Cannot bind variable of type $t to stream of type ${in.typ}."
          )
        }
    }
    val out = this.out.tchk(context + (x -> in.typ))
    this.rebuild(out.typ, Seq(x, in, out))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    assert(this.out.countFreeOccurrences(this.x) <= 1)
    this.out.subPreserveType(this.x -> this.in).lower()
  }

  override def sugarSubAndKeepType(
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val wouldCapture = subs.exists({ case (_, rhs) =>
      rhs.freeVars.contains(this.x)
    })
    val newX = if (wouldCapture) this.x.freshCopy else this.x
    val newSubs =
      subs
        // Substitutions with `x` free on the LHS will never match
        // again, since `x` is now bound.
        .filter({ case (lhs, _) => !lhs.freeVars.contains(this.x) })
        // Rename the bound variable if necessary
        .++(if (this.x == newX) Seq() else Seq(x -> newX))
    LetInlineStm(
      // There may be substitutions to do within the type annotation
      Param(newX.prefix, newX.id)(newX.typ.substitute(subs)(c)),
      // `x` is not bound here, so use the old subs
      this.in.subPreserveType(subs)(c),
      // `x` is bound here, so use the new subs
      this.out.subPreserveType(newSubs)(c)
    )(this.typ)
  }

  override def sugarSubAndEraseType(
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val wouldCapture = subs.exists({ case (_, rhs) =>
      rhs.freeVars.contains(this.x)
    })
    val newX = if (wouldCapture) this.x.freshCopy else this.x
    val newSubs =
      subs
        // Substitutions with `x` free on the LHS will never match
        // again, since `x` is now bound.
        .filter({ case (lhs, _) => !lhs.freeVars.contains(this.x) })
        // Rename the bound variable if necessary
        .++(if (this.x == newX) Seq() else Seq(x -> newX))
    LetInlineStm(
      // There may be substitutions to do within the type annotation
      Param(newX.prefix, newX.id)(newX.typ.substitute(subs)(c)),
      // `x` is not bound here, so use the old subs
      this.in.subAndEraseType(subs)(c),
      // `x` is bound here, so use the new subs
      this.out.subAndEraseType(newSubs)(c)
    )()
  }

  override def precedence: Int = Precedence.Max

  override def displayOneLine(): String = {
    val xStr = this.x.typ match {
      case Missing => this.x.name
      case t       => s"${this.x.name}: $t"
    }
    val inStr =
      EP.displayOneLine(this.in, parentPrecedence = this.precedence)
    val outStr =
      EP.displayOneLine(this.out, parentPrecedence = this.precedence)
    s"let inline stm $xStr = $inStr in $outStr"
  }

  override def displayMultiLine(maxWidth: Int): String = {
    val xStr = this.x.typ match {
      case Missing => this.x.name
      case t       => s"${this.x.name}: $t"
    }
    val inStr = {
      val str = EP.display(
        this.in,
        maxWidth = maxWidth - EP.Indent.length,
        parentPrecedence = this.precedence
      )
      this.in match {
        case _: Let | _: LetStm | _: LetInlineStm if str.contains("\n") =>
          s"(\n${EP.indent(str)}\n)"
        case _ =>
          str
      }
    }
    val outStr = this.out match {
      case _: LetInlineStm | _: LetStm =>
        EP.displayMultiLine(this.out, maxWidth = maxWidth)
      case _ =>
        EP.display(this.out, maxWidth = maxWidth)
    }
    s"let inline stm $xStr = $inStr in\n$outStr"
  }

  private def asFunCall(): FunCall = {
    FunCall(Function(this.x, this.out)(), this.in)()
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: LetInlineStm => this.asFunCall() == that.asFunCall()
      case _                  => false
    }
  }

  override def hashCode(): Int = {
    this.asFunCall().hashCode()
  }
}
