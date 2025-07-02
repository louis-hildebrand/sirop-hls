package mhir.debug

import mhir.ir._
import mhir.optimize.PartialEvalPass

/** Functions for displaying an expression.
  *
  * TODO: Merge this into [[mhir.ir.ExprPrinter]].
  */
object PrettyPrinter {

  /** Produce Scala code that I can copy and paste (e.g., to take some large
    * expression and use it in a test case).
    */
  def showScala(e: Expr): String = {
    e match {
      case tup @ Tuple(elems @ _*) =>
        val children = elems.map(e => showScala(e))
        s"Tuple(${children.mkString(",")})(${showScala(tup.typ)})"
      case ta @ TupleAccess(t, i) =>
        i match {
          case IntCst(i) if 0 <= i && i <= 5 =>
            s"${showScalaWithParens(t)}.__$i"
          case _ =>
            s"TupleAccess(${showScala(t)},${showScala(i)})(${ta.typ})"
        }
      case x: Param => s"${x.name}"
      case Function(param, body) =>
        s"{ val ${param.name} = Param(${param.prefix})(${param.typ}) ; Function(${param.name}, ${showScala(body)})() }"
      case fc @ FunCall(f, arg) =>
        s"(${showScala(f)})(${showScala(arg)})(${showScala(fc.typ)})"
      case c: IntCst =>
        s"IntCst(${c.i}L)(${showScala(c.typ)})"
      case s @ Sum(terms @ _*) =>
        s"Sum(${terms.map(e => showScala(e)).mkString(",")})(${showScala(s.typ)})"
      case p @ Prod(factors @ _*) =>
        s"Prod(${factors.map(e => showScala(e)).mkString(",")})(${showScala(p.typ)})"
      case d @ Div(x, y) =>
        s"Div(${showScala(x)},${showScala(y)})(${showScala(d.typ)})"
      case m @ Mod(x, y) =>
        s"Mod(${showScala(x)},${showScala(y)})(${showScala(m.typ)})"
      case pt @ PadTo(x, w) =>
        s"PadTo(${showScala(x)},$w)(${showScala(pt.typ)})"
      case tt @ TruncateTo(x, w) =>
        s"TruncateTo(${showScala(x)},$w)(${showScala(tt.typ)})"
      case ts @ ToSigned(x) =>
        s"ToSigned(${showScala(x)})(${showScala(ts.typ)})"
      case tu @ ToUnsigned(x) =>
        s"ToUnsigned(${showScala(x)})(${showScala(tu.typ)})"
      case True  => "True"
      case False => "False"
      case eq @ Equal(x, y) =>
        s"Equal(${showScala(x)},${showScala(y)})(${showScala(eq.typ)})"
      case lt @ LessThan(x, y) =>
        s"LessThan(${showScala(x)},${showScala(y)})(${showScala(lt.typ)})"
      case n @ Not(e) => s"Not(${showScala(e)})(${n.typ})"
      case a @ And(terms @ _*) =>
        s"And(${terms.map(e => showScala(e)).mkString(",")})(${showScala(a.typ)})"
      case or @ Or(terms @ _*) =>
        s"Or(${terms.map(e => showScala(e)).mkString(",")})(${showScala(or.typ)})"
      case m @ Mux(c, t, f) =>
        s"Mux(${showScala(c)},${showScala(t)},${showScala(f)})(${showScala(m.typ)})"
      case s @ StmBuild(n, data, valid, eqns) =>
        val equationsStr =
          s"Map(${eqns.map({ case (x, (z, next)) =>
              s"${showScala(x)}->(${showScala(z)},${showScala(next)})"
            })})"
        s"StmBuild(${showScala(n)},${showScala(data)},${showScala(valid)},$equationsStr)(${showScala(s.typ)})"
      case s @ StmLiteral(elems @ _*) =>
        val children = elems.map(e => showScala(e))
        s"StmLiteral(${children.mkString(",")})(${showScala(s.typ)})"
      case sd @ StmData(s) =>
        s"StmData(${showScala(s)})(${showScala(sd.typ)})"
      case sn @ StmNextK(s, k) =>
        s"StmNextK(${showScala(s)},${showScala(k)})(${showScala(sn.typ)})"
      case vb @ VecBuild(n, f) =>
        s"VecBuild(${showScala(n)},${showScala(f)})(${showScala(vb.typ)})"
      case v @ VecLiteral(elems @ _*) =>
        val children = elems.map(e => showScala(e))
        s"VecLiteral(${children.mkString(",")})(${showScala(v.typ)})"
      case va @ VecAccess(v, i) =>
        s"VecAccess(${showScala(v)},${showScala(i)})(${showScala(va.typ)})"
      case Default(t) =>
        s"Default(${showScala(t)})"
      case e: SyntaxSugar =>
        val name = e.getClass.getSimpleName
        val children = e.children.map(showScala).mkString(", ")
        s"$name($children)(${showScala(e.typ)})"
    }
  }

  private def showScala(t: Type): String = {
    t match {
      case Missing                                   => "Missing"
      case t: TyUInt if COMMON_INT_TYPES.contains(t) => s"U${t.w}"
      case TyUInt(w)                                 => s"TyUInt($w)"
      case t: TySInt if COMMON_INT_TYPES.contains(t) => s"I${t.w}"
      case TySInt(w)                                 => s"TySInt($w)"
      case TyBool                                    => "TyBool"
      case TyArrow(t1, t2) => s"TyArrow(${showScala(t1)},${showScala(t2)})"
      case TyTuple(ts @ _*) =>
        s"TyTuple(${ts.map(t => showScala(t)).mkString(",")})"
      case TyVec(t, n) => s"TyVec(${showScala(t)},${showScala(n)})"
      case TyStm(t, n) => s"TyStm(${showScala(t)},${showScala(n)})"
    }
  }

  private def showScalaWithParens(e: Expr): String = {
    val str = showScala(e)
    if (shouldParenthesize(e)) s"($str)" else str
  }

  private def shouldParenthesize(e: Expr): Boolean = {
    e match {
      case _: IntCst | _: Param | _: TupleAccess | _: ToSigned | _: ToUnsigned |
          _: PadTo | _: TruncateTo =>
        false
      case _ => true
    }
  }
}
