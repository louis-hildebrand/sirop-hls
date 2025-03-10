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
      case Sum(terms)    => terms.map(e => showWithParens(e)).mkString(" + ")
      case Prod(factors) => factors.map(e => showWithParens(e)).mkString(" * ")
      case Div(x, y)     => s"${showWithParens(x)} / ${showWithParens(y)}"
      case Mod(x, y)     => s"${showWithParens(x)} % ${showWithParens(y)}"
      case Equal(x, y)   => s"${showWithParens(x)} === ${showWithParens(y)}"
      case Not(Equal(x, y)) => s"${showWithParens(x)} !== ${showWithParens(y)}"
      case LessThan(x, y)   => s"${showWithParens(x)} < ${showWithParens(y)}"
      case Not(LessThan(x, y)) =>
        s"${showWithParens(x)} >= ${showWithParens(y)}"
      case And(x, y) => s"${showWithParens(x)} && ${showWithParens(y)}"
      case Or(x, y)  => s"${showWithParens(x)} || ${showWithParens(y)}"
      case Not(x)    => s"!${showWithParens(x)}"
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
        val pStr = show(p, collapseStm = collapseStm, evalVec = evalVec)
        val bStr = show(b, collapseStm = collapseStm, evalVec = evalVec)
        if (isMultiline(bStr)) {
          s"""(${pStr}) =>
             |${indent(bStr)}
             |""".stripMargin.stripTrailing
        } else {
          s"(${pStr}) => ${bStr}"
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
      case e =>
        val name = e.getClass.getSimpleName
        val sh = (e: Expr) =>
          show(e, collapseStm = collapseStm, evalVec = evalVec)
        val children = e.children.map(sh).mkString(", ")
        s"$name($children)"
    }
  }

  /** Produce Scala code that I can copy and paste (e.g., to take some large
    * expression and use it in a test case).
    */
  def showScala(e: Expr): String = {
    e match {
      case Tuple(elems @ _*) =>
        val children = elems.map(e => showScala(e))
        s"Tuple(${children.mkString(",")})"
      case TupleAccess(t, i) =>
        i match {
          case IntCst(i) if 0 <= i && i <= 5 =>
            s"${showScalaWithParens(t)}.__$i"
          case _ =>
            s"TupleAccess(${showScala(t)},${showScala(i)})"
        }
      case p: Param => p.name
      case Function(param, body) =>
        s"(${param.name}: Expr) => ${showScala(body)}"
      case FunCall(f, arg) =>
        s"(${showScala(f)})(${showScala(arg)})"
      case IntCst(i) => i.toString
      case Sum(terms) =>
        s"Sum(${terms.map(e => showScala(e)).mkString(",")})"
      case Prod(factors) =>
        s"Prod(${factors.map(e => showScala(e)).mkString(",")})"
      case Div(x, y) =>
        s"Div(${showScala(x)},${showScala(y)})"
      case Mod(x, y) =>
        s"Mod(${showScala(x)},${showScala(y)})"
      case True  => "True"
      case False => "False"
      case Equal(x, y) =>
        s"Equal(${showScala(x)},${showScala(y)})"
      case LessThan(x, y) =>
        s"LessThan(${showScala(x)},${showScala(y)})"
      case Not(e) => s"Not(${showScala(e)})"
      case And(x, y) =>
        s"And(${showScala(x)},${showScala(y)})"
      case Or(x, y) =>
        s"Or(${showScala(x)},${showScala(y)})"
      case IfThenElse(c, t, f) =>
        s"IfThenElse(${showScala(c)},${showScala(t)},${showScala(f)})"
      case DontCare => "DontCare"
      case StmBuild(n, z, f) =>
        s"StmBuild(${showScala(n)},${showScala(z)},${showScala(f)})"
      case StmLiteral(elems @ _*) =>
        val children = elems.map(e => showScala(e))
        s"StmLiteral(${children.mkString(",")})"
      case StmNext(s) =>
        s"StmNext(${showScala(s)})"
      case StmNextK(s, k) =>
        s"StmNextK(${showScala(s)},${showScala(k)})"
      case StmLength(s) =>
        s"StmLength(${showScala(s)})"
      case VecBuild(n, f) =>
        s"VecBuild(${showScala(n)},${showScala(f)})"
      case VecLiteral(elems @ _*) =>
        val children = elems.map(e => showScala(e))
        s"VecLiteral(${children.mkString(",")})"
      case VecAccess(v, i) =>
        s"VecAccess(${showScala(v)},${showScala(i)})"
      case VecLength(v) =>
        s"VecLength(${showScala(v)})"
    }
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

  private def showScalaWithParens(e: Expr): String = {
    val str = showScala(e)
    if (shouldParenthesize(e)) s"($str)" else str
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
