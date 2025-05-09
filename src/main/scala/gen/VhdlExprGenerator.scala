package gen

import ir._
import opt.PartialEvalPass

/** @param vhdl
  *   VHDL code for this expression
  * @param decls
  *   Declarations required for this expression, including all its
  *   sub-expressions
  */
private[gen] case class VhdlExpr(vhdl: String, decls: Seq[Signal])

private object VhdlOp {
  def apply(op: String, terms: Seq[VhdlExpr]): VhdlExpr = {
    val vhdl = terms.map(e => s"(${e.vhdl})").mkString(s" $op ")
    val decls = terms.flatMap(e => e.decls)
    VhdlExpr(vhdl, decls)
  }
}

private object VhdlExprGenerator {

  /** Convert an expression to VHDL.
    *
    * @param e
    *   The expression to convert
    * @return
    *   A VHDL expression along with the signals required by that expression
    */
  def exprToVhdl(e: Expr): VhdlExpr = {
    e match {
      case x: Param  => VhdlExpr(x.name, Seq())
      case IntCst(n) => VhdlExpr(n.toString, Seq())
      // TODO: Specially handle cases like x + (-1 * y)?
      case Sum(terms @ _*)    => VhdlOp("+", terms.map(exprToVhdl))
      case Prod(factors @ _*) => VhdlOp("*", factors.map(exprToVhdl))
      case _: Div             => ???
      case _: Mod             => ???
      case True               => VhdlExpr("true", Seq())
      case False              => VhdlExpr("false", Seq())
      case ite @ IfThenElse(c, t, f) =>
        val cv = exprToVhdl(c)
        val tv = exprToVhdl(t)
        val fv = exprToVhdl(f)
        val sig = {
          val name = Param("ite")().name
          Signal(
            category = "Intermediate signals",
            name = name,
            typ = VhdlType(ite.typ),
            init = None,
            assignStmt = Some(
              s"$name <= (${tv.vhdl}) when (${cv.vhdl}) else (${fv.vhdl});"
            ),
            cond = None
          )
        }
        VhdlExpr(sig.name, sig +: (cv.decls ++ tv.decls ++ fv.decls))
      case Equal(e1, e2)    => VhdlOp("=", Seq(e1, e2).map(exprToVhdl))
      case LessThan(e1, e2) => VhdlOp("<", Seq(e1, e2).map(exprToVhdl))
      case Not(e) =>
        val ve = exprToVhdl(e)
        VhdlExpr(s"not (${ve.vhdl})", ve.decls)
      case And(terms @ _*) => VhdlOp("and", terms.map(exprToVhdl))
      case Or(terms @ _*)  => VhdlOp("or", terms.map(exprToVhdl))

      case TupleAccess(StmNext(s: Param), IntCst(1)) =>
        VhdlExpr(s"${s.name}_data_internal", Seq())
      case _: StmBuild | _: StmNext | TupleAccess(_: StmNext, _) =>
        throw new IllegalArgumentException(
          s"Cannot generate hardware for ${e.getClass.getSimpleName} in this position."
        )
      case _: StmNextK | _: StmLiteral =>
        throw new IllegalArgumentException(
          s"Cannot generate hardware for ${e.getClass.getSimpleName}."
        )

      case Tuple() => VhdlExpr("\"\"", Seq())
      case Tuple(elems @ _*) =>
        val vhdlElems = elems.map(exprToVhdl)
        val assignments = vhdlElems.zipWithIndex
          .map({ case (v, i) => s"i_$i => ${v.vhdl}" })
          .mkString(", ")
        VhdlExpr(s"($assignments)", vhdlElems.flatMap(e => e.decls))
      case TupleAccess(t, IntCst(i)) =>
        val tv = exprToVhdl(t)
        val tupSig = {
          val name = Param("tupaccess_t")().name
          Signal(
            category = "Intermediate signals",
            name = name,
            typ = VhdlType(t.typ),
            assignStmt = Some(s"$name <= ${tv.vhdl};")
          )
        }
        VhdlExpr(s"${tupSig.name}.i_$i", tupSig +: tv.decls)

      case _: Function => ???
      case _: FunCall  => ???

      case VecLiteral(elems @ _*) =>
        val vhdlElems = elems.map(exprToVhdl)
        val assignments = vhdlElems.zipWithIndex
          .map({ case (e, i) => s"$i => ${e.vhdl}" })
          .mkString(", ")
        VhdlExpr(s"($assignments)", vhdlElems.flatMap(e => e.decls))
      case VecBuild(IntCst(n), f) =>
        val elems = (0 until n).map(i =>
          // TODO: Partial evaluation here kind of approximates IfThenElse
          //       being short-circuiting---the other branch will be
          //       discarded if the condition evaluates to a bool constant.
          //       But if the condition cannot be evaluated statically,
          //       then it's not the same thing.
          PartialEvalPass.partialEval(FunCall(f, i)())
        )
        exprToVhdl(VecLiteral(elems: _*)().tchk())
      case VecBuild(n, _) =>
        throw new IllegalArgumentException(
          s"VecBuild with non-constant size ($n) is not supported."
        )
      case VecAccess(v, i) =>
        val vv = exprToVhdl(v)
        val iv = exprToVhdl(i)
        val vecSig = {
          val name = Param("vecaccess_v")().name
          Signal(
            category = "Intermediate signals",
            name = name,
            typ = VhdlType(v.typ),
            assignStmt = Some(s"$name <= ${vv.vhdl};")
          )
        }
        VhdlExpr(
          s"${vecSig.name}(${iv.vhdl})",
          vecSig +: (vv.decls ++ iv.decls)
        )

      case _: SyntaxSugar =>
        throw new IllegalArgumentException(
          s"Syntax sugar must be removed before hardware generation."
        )
    }
  }

  def valueToVhdl(v: Expr): String = {
    ir.eval(v).tchk() match {
      case False     => "false"
      case True      => "true"
      case IntCst(k) => k.toString
      case Tuple()   => "\"\""
      case Tuple(elems @ _*) =>
        val assignments = elems.zipWithIndex
          .map({ case (e, i) => s"i_$i => ${valueToVhdl(e)}" })
          .mkString(", ")
        s"($assignments)"
      case VecLiteral(elems @ _*) =>
        val assignments =
          elems.zipWithIndex
            .map({ case (e, i) => s"$i => ${valueToVhdl(e)}" })
            .mkString(", ")
        s"($assignments)"
      case _ =>
        throw new IllegalArgumentException(
          s"Cannot convert value $v to a VHDL expression. Is it really a value?"
        )
    }
  }
}
