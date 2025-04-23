package debug

import opt.PartialEvalPass
import ir._

import scala.annotation.tailrec

object PrettyPrinter {
  def show(e: Expr, collapseStm: Boolean = false, evalVec: Boolean = false)(
      implicit nameByParam: Map[Param, String]
  ): String = {
    e match {
      case True      => "true"
      case False     => "false"
      case IntCst(n) => n.toString
      // In theory we should also pass `collapseStm` to `showWithParens`, but
      // hopefully there are no streams being built inside these expressions
      case Sum(terms)     => terms.map(e => showWithParens(e)).mkString(" + ")
      case Prod(factors)  => factors.map(e => showWithParens(e)).mkString(" * ")
      case Div(_, x, y)   => s"${showWithParens(x)} / ${showWithParens(y)}"
      case Mod(_, x, y)   => s"${showWithParens(x)} % ${showWithParens(y)}"
      case Equal(_, x, y) => s"${showWithParens(x)} === ${showWithParens(y)}"
      case Not(_, Equal(_, x, y)) =>
        s"${showWithParens(x)} !== ${showWithParens(y)}"
      case LessThan(_, x, y) => s"${showWithParens(x)} < ${showWithParens(y)}"
      case Not(_, LessThan(_, x, y)) =>
        s"${showWithParens(x)} >= ${showWithParens(y)}"
      case And(terms @ _*) => terms.map(e => showWithParens(e)).mkString(" && ")
      case Or(terms @ _*)  => terms.map(e => showWithParens(e)).mkString(" || ")
      case Not(_, x)       => s"!${showWithParens(x)}"
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
      case Function(_, p, t, b) =>
        val pStr = show(p, collapseStm = collapseStm, evalVec = evalVec)
        val tStr = show(t)
        val bStr = show(b, collapseStm = collapseStm, evalVec = evalVec)
        if (isMultiline(bStr)) {
          s"""(${pStr} : $tStr) =>
             |${indent(bStr)}
             |""".stripMargin.stripTrailing
        } else {
          s"(${pStr}) => ${bStr}"
        }
      case FunCall(_, f, a) =>
        s"(${show(f, collapseStm = collapseStm, evalVec = evalVec)})(${show(a, collapseStm = collapseStm, evalVec = evalVec)})"
      case Tuple(_, elems @ _*) =>
        // Include the trailing t to distinguish between a parenthesized
        // expression and a tuple of one element.
        "t(" + elems
          .map(e => show(e, collapseStm = collapseStm, evalVec = evalVec))
          .mkString(", ") + ")"
      case TupleAccess(_, t, i) =>
        s"${show(t, collapseStm = collapseStm, evalVec = evalVec)}.__${show(i, collapseStm = collapseStm, evalVec = evalVec)}"
      case StmBuild(_, n, out, equations) =>
        if (collapseStm) {
          val nStr = show(n, collapseStm = collapseStm, evalVec = evalVec)
          s"StmBuild(${nStr}; ...)"
        } else {
          val nStr = show(n, collapseStm = collapseStm, evalVec = evalVec)
          val outStr = show(out, collapseStm = collapseStm, evalVec = evalVec)
          val recStrings = equations.map({ case (x, (z, next)) =>
            val zStr = show(z, collapseStm = collapseStm, evalVec = evalVec)
            val nextStr =
              show(next, collapseStm = collapseStm, evalVec = evalVec)
            s"${show(x)}: (\n${indent(zStr)},\n${indent(nextStr)}\n)"
          })
          val inside = s"$nStr;\n$outStr;\n${recStrings.mkString(";\n")}"
          s"StmBuild(\n${indent(inside)}\n)"
        }
      case StmLength(_, s) =>
        val stmStr = show(s, collapseStm = true)
        s"StmLength($stmStr)"
      case VecLiteral(_, elems @ _*) =>
        val sh = (e: Expr) =>
          show(e, collapseStm = collapseStm, evalVec = evalVec)
        val children = e.children.map(sh).mkString(", ")
        s"[$children]"
      case v @ VecBuild(_, n, f) =>
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
      case VecAccess(_, v, i) =>
        s"${show(v)}[${show(i, collapseStm = collapseStm, evalVec = evalVec)}]"
      case VecLength(_, v) =>
        s"VecLength(${show(v, collapseStm = collapseStm, evalVec = evalVec)})"
      case e =>
        val name = e.getClass.getSimpleName
        val sh = (e: Expr) =>
          show(e, collapseStm = collapseStm, evalVec = evalVec)
        val children = e.children.map(sh).mkString(", ")
        s"$name($children)"
    }
  }

  def show(t: Type): String = {
    t match {
      case Missing         => "?"
      case TyInt           => "Int"
      case TyBool          => "Bool"
      case TyArrow(t1, t2) => s"($t1) -> ($t2)"
      case TyTuple(ts @ _*) =>
        val tsStr = ts.map(t => show(t)).mkString(", ")
        s"($tsStr)"
      case TyVec(t, n) => s"Vec[${show(t)}, ${show(n)(Map())}]"
      case TyStm(t, n) => s"Stm[${show(t)}, ${show(n)(Map())}]"
    }
  }

  /** Produce Scala code that I can copy and paste (e.g., to take some large
    * expression and use it in a test case).
    */
  def showScala(e: Expr): String = {
    e match {
      case Tuple(_, elems @ _*) =>
        val children = elems.map(e => showScala(e))
        s"Tuple(${children.mkString(",")})"
      case TupleAccess(_, t, i) =>
        i match {
          case IntCst(i) if 0 <= i && i <= 5 =>
            s"${showScalaWithParens(t)}.__$i"
          case _ =>
            s"TupleAccess(${showScala(t)},${showScala(i)})"
        }
      case p: Param => p.name
      case Function(_, param, inTyp, body) =>
        s"(${param.name}: Expr) => (${showScala(inTyp)}, ${showScala(body)})"
      case FunCall(_, f, arg) =>
        s"(${showScala(f)})(${showScala(arg)})"
      case IntCst(i) => i.toString
      case Sum(terms) =>
        s"Sum(${terms.map(e => showScala(e)).mkString(",")})"
      case Prod(factors) =>
        s"Prod(${factors.map(e => showScala(e)).mkString(",")})"
      case Div(_, x, y) =>
        s"Div(${showScala(x)},${showScala(y)})"
      case Mod(_, x, y) =>
        s"Mod(${showScala(x)},${showScala(y)})"
      case True  => "True"
      case False => "False"
      case Equal(_, x, y) =>
        s"Equal(${showScala(x)},${showScala(y)})"
      case LessThan(_, x, y) =>
        s"LessThan(${showScala(x)},${showScala(y)})"
      case Not(_, e) => s"Not(${showScala(e)})"
      case And(terms @ _*) =>
        s"And(${terms.map(e => showScala(e)).mkString(",")})"
      case Or(terms @ _*) =>
        s"Or(${terms.map(e => showScala(e)).mkString(",")})"
      case IfThenElse(c, t, f) =>
        s"IfThenElse(${showScala(c)},${showScala(t)},${showScala(f)})"
      case StmBuild(_, n, out, eqns) =>
        val equationsStr =
          s"Map(${eqns.map({ case (x, (z, next)) =>
              s"${showScala(x)}->(${showScala(z)},${showScala(next)})"
            })})"
        s"StmBuild(${showScala(n)},${showScala(out)},$equationsStr)"
      case StmLiteral(_, elems @ _*) =>
        val children = elems.map(e => showScala(e))
        s"StmLiteral(${children.mkString(",")})"
      case StmNext(_, s) =>
        s"StmNext(${showScala(s)})"
      case StmNextK(_, s, k) =>
        s"StmNextK(${showScala(s)},${showScala(k)})"
      case StmLength(_, s) =>
        s"StmLength(${showScala(s)})"
      case VecBuild(_, n, f) =>
        s"VecBuild(${showScala(n)},${showScala(f)})"
      case VecLiteral(_, elems @ _*) =>
        val children = elems.map(e => showScala(e))
        s"VecLiteral(${children.mkString(",")})"
      case VecAccess(_, v, i) =>
        s"VecAccess(${showScala(v)},${showScala(i)})"
      case VecLength(_, v) =>
        s"VecLength(${showScala(v)})"
    }
  }

  private def showScala(t: Type): String = {
    t.toString
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
