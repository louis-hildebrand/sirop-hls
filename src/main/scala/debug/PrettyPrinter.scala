package debug

import opt.PartialEvalPass
import ir._

import scala.annotation.tailrec

object PrettyPrinter {
  def show(e: Expr, collapseStm: Boolean = false, evalVec: Boolean = false)(
      implicit nameByParam: Map[Param, String]
  ): String = {
    e match {
      case True      => "True"
      case False     => "False"
      case DontCare  => "DontCare"
      case IntCst(n) => n.toString
      // In theory we should also pass `collapseStm` to `showWithParens`, but
      // hopefully there are no streams being built inside these expressions
      case Add(x, Neg(y)) => s"${showWithParens(x)} - ${showWithParens(y)}"
      case Add(x, y)      => s"${showWithParens(x)} + ${showWithParens(y)}"
      case Neg(x)         => s"-${showWithParens(x)}"
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
        nameByParam.get(p) match {
          case Some(name) => name
          case None       => p.toString
        }
      case Function(p, b) =>
        val newParamName = chooseNewParamName(nameByParam.values.toSet)
        val pStr =
          show(p, collapseStm = collapseStm, evalVec = evalVec)(
            nameByParam + (p -> newParamName)
          )
        val bStr =
          show(b, collapseStm = collapseStm, evalVec = evalVec)(
            nameByParam + (p -> newParamName)
          )
        if (isMultiline(bStr)) {
          s"""(${pStr} /* ${p.toString} */) =>
             |${indent(bStr)}
             |""".stripMargin.stripTrailing
        } else {
          s"(${pStr} /* ${p.toString} */) => ${bStr}"
        }
      case FunCall(f, a) =>
        s"(${show(f, collapseStm = collapseStm, evalVec = evalVec)})(${show(a, collapseStm = collapseStm, evalVec = evalVec)})"
      case Tuple(elems @ _*) =>
        // Include the trailing t to distinguish between a parenthesized
        // expression and a tuple of one element.
        "t(" + elems
          .map(e => show(e, collapseStm = collapseStm, evalVec = evalVec))
          .mkString(", ") + ")"
      case TupleAccess(t, i) =>
        s"${show(t, collapseStm = collapseStm, evalVec = evalVec)}.__${show(i, collapseStm = collapseStm, evalVec = evalVec)}"
      case StmBuild(n, z, f) =>
        if (collapseStm) {
          s"StmBuild(${show(n, collapseStm = collapseStm, evalVec = evalVec)}, ...)"
        } else {
          val nStr = show(n, collapseStm = collapseStm, evalVec = evalVec)
          val zStr = show(z, collapseStm = collapseStm, evalVec = evalVec)
          val fStr = show(f, collapseStm = collapseStm, evalVec = evalVec)
          if (isMultiline(nStr) || isMultiline(zStr) || isMultiline(fStr)) {
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
        val elems = if (evalVec) tryEvalVec(v) else None
        elems match {
          case Some(elems) =>
            // Show the elements
            val elemStrs = elems.map(e =>
              show(e, collapseStm = collapseStm, evalVec = evalVec)
            )
            if (elemStrs.exists(s => isMultiline(s))) {
              "[\n" + elemStrs.map(s => indent(s) + ",\n") + "]"
            } else {
              "[" + elemStrs.mkString(", ") + "]"
            }
          case None =>
            // Show the StmBuild itself
            val nStr = show(n, collapseStm = collapseStm, evalVec = evalVec)
            val fStr = show(f, collapseStm = collapseStm, evalVec = evalVec)
            if (isMultiline(nStr) || isMultiline(fStr)) {
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

  @tailrec
  private def chooseNewParamName(takenNames: Set[String], i: Int = 0): String =
    if (!takenNames.contains(s"p${i}")) {
      s"p${i}"
    } else {
      chooseNewParamName(takenNames, i + 1)
    }

  private def isMultiline(s: String): Boolean = s.linesIterator.length > 1

  private def showWithParens(
      e: Expr
  )(implicit nameByParam: Map[Param, String]): String = {
    if (shouldParenthesize(e)) {
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
        if (elems.exists(e => e.isInstanceOf[VecAccess])) {
          None
        } else {
          Some(elems)
        }
      case _ => None
    }
  }
}
