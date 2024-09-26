import opt.PartialEvalPass
import ir.*

import scala.jdk.CollectionConverters.*

object PrettyPrinter {
  def show(e: Expr, collapseStm: Boolean = false, evalVec: Boolean = false)(
      implicit numByParam: Map[Param, Int]
  ): String = {
    e match {
      case True      => "True"
      case False     => "False"
      case DontCare  => "DontCare"
      case IntCst(n) => n.toString
      // In theory we should also pass `collapseStm` to `showWithParens`, but
      // hopefully there are no streams being built inside these expressions
      case Add(x, y)      => s"${showWithParens(x)} + ${showWithParens(y)}"
      case Sub(x, y)      => s"${showWithParens(x)} - ${showWithParens(y)}"
      case Mul(x, y)      => s"${showWithParens(x)} * ${showWithParens(y)}"
      case Div(x, y)      => s"${showWithParens(x)} / ${showWithParens(y)}"
      case Mod(x, y)      => s"${showWithParens(x)} % ${showWithParens(y)}"
      case Equal(x, y)    => s"${showWithParens(x)} === ${showWithParens(y)}"
      case NotEqual(x, y) => s"${showWithParens(x)} !== ${showWithParens(y)}"
      case LessThan(x, y) => s"${showWithParens(x)} < ${showWithParens(y)}"
      case And(x, y)      => s"${showWithParens(x)} && ${showWithParens(y)}"
      case Or(x, y)       => s"${showWithParens(x)} || ${showWithParens(y)}"
      case Not(x)         => s"!${showWithParens(x)}"
      case IfThenElse(c, t, f) =>
        s"""if (${show(c, collapseStm = collapseStm, evalVec = evalVec)}) then {
           |${indent(show(t, collapseStm = collapseStm, evalVec = evalVec))}
           |} else {
           |${indent(show(f, collapseStm = collapseStm, evalVec = evalVec))}
           |}
           |""".stripMargin.stripTrailing
      case p: Param =>
        numByParam.get(p) match {
          case Some(n) => s"p${n}"
          case None    => p.toString
        }
      case Function(p, b) =>
        val paramNum =
          if numByParam.isEmpty then 0 else numByParam.values.max + 1
        val pStr =
          show(p, collapseStm = collapseStm, evalVec = evalVec)(
            numByParam + (p -> paramNum)
          )
        val bStr =
          show(b, collapseStm = collapseStm, evalVec = evalVec)(
            numByParam + (p -> paramNum)
          )
        if isMultiline(bStr) then {
          s"""(${pStr} /* ${p.toString} */) =>
             |${indent(bStr)}
             |""".stripMargin.stripTrailing
        } else {
          s"(${pStr} /* ${p.toString} */) => ${bStr}"
        }
      case FunCall(f, a) =>
        s"(${show(f, collapseStm = collapseStm, evalVec = evalVec)})(${show(a, collapseStm = collapseStm, evalVec = evalVec)})"
      case Tuple(elems: _*) =>
        // Include the trailing t to distinguish between a parenthesized
        // expression and a tuple of one element.
        "t(" + elems
          .map(e => show(e, collapseStm = collapseStm, evalVec = evalVec))
          .mkString(", ") + ")"
      case TupleAccess(t, i) =>
        s"${show(t, collapseStm = collapseStm, evalVec = evalVec)}.__${show(i, collapseStm = collapseStm, evalVec = evalVec)}"
      case StmBuild(n, z, f) =>
        if collapseStm then {
          s"StmBuild(${show(n, collapseStm = collapseStm, evalVec = evalVec)}, ...)"
        } else {
          val nStr = show(n, collapseStm = collapseStm, evalVec = evalVec)
          val zStr = show(z, collapseStm = collapseStm, evalVec = evalVec)
          val fStr = show(f, collapseStm = collapseStm, evalVec = evalVec)
          if isMultiline(nStr) || isMultiline(zStr) || isMultiline(fStr) then {
            s"""StmBuild(
               |${indent(nStr)},
               |${indent(zStr)},
               |${indent(fStr)}
               |)
               |""".stripMargin.stripTrailing
          } else {
            s"StmBuild(${nStr}, ${zStr}, ${fStr})"
          }
        }
      case StmNext(s) =>
        s"StmNext(${show(s, collapseStm = collapseStm, evalVec = evalVec)})"
      case StmLength(s) =>
        s"StmLength(${show(s, collapseStm = collapseStm, evalVec = evalVec)})"
      case v @ VecBuild(n, f) =>
        val elems = if evalVec then tryEvalVec(v) else None
        elems match {
          case Some(elems) =>
            // Show the elements
            val elemStrs = elems.map(e =>
              show(e, collapseStm = collapseStm, evalVec = evalVec)
            )
            if elemStrs.exists(s => isMultiline(s)) then {
              "[\n" + elemStrs.map(s => indent(s) + ",\n") + "]"
            } else {
              "[" + elemStrs.mkString(", ") + "]"
            }
          case None =>
            // Show the StmBuild itself
            val nStr = show(n, collapseStm = collapseStm, evalVec = evalVec)
            val fStr = show(f, collapseStm = collapseStm, evalVec = evalVec)
            if isMultiline(nStr) || isMultiline(fStr) then {
              s"""VecBuild(
                 |${indent(nStr)},
                 |${indent(fStr)}
                 |)
                 |""".stripMargin
            } else {
              s"VecBuild(${nStr}, ${fStr})"
            }
        }
      case VecAccess(v, i) =>
        s"${show(v)}[${show(i, collapseStm = collapseStm, evalVec = evalVec)}]"
      case VecLength(v) =>
        s"VecLength(${show(v, collapseStm = collapseStm, evalVec = evalVec)})"
    }
  }

  private def indent(s: String): String = {
    s.lines.map(l => s"    ${l}").iterator().asScala.mkString("\n")
  }

  private def isMultiline(s: String): Boolean = s.linesIterator.length > 1

  private def showWithParens(
      e: Expr
  )(implicit numByParam: Map[Param, Int]): String = {
    if shouldParenthesize(e) then {
      s"(${show(e)})"
    } else {
      show(e)
    }
  }

  private def shouldParenthesize(e: Expr): Boolean = {
    e match {
      case _: IntCst | _: Param | _: TupleAccess => false
      case _                                     => true
    }
  }

  private def tryEvalVec(v: VecBuild): Option[Seq[Expr]] = {
    PartialEvalPass.partialEval(v.len) match {
      case IntCst(n) =>
        val elems =
          (0 until n).map(i => PartialEvalPass.partialEval(VecAccess(v, i)))
        if elems.exists(e => e.isInstanceOf[VecAccess]) then {
          None
        } else {
          Some(elems)
        }
      case _ => None
    }
  }
}
