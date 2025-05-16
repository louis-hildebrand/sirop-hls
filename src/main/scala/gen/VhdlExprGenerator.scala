package gen

import ir._
import opt.PartialEvalPass

/** @param vhdl
  *   VHDL code for this expression
  * @param decls
  *   Declarations required for this expression, including all its
  *   sub-expressions
  */
private[gen] case class VhdlExpr(vhdl: String, decls: Seq[Decl])

private object VhdlOp {
  def apply(op: String, terms: Seq[VhdlExpr]): VhdlExpr = {
    val vhdl = terms.map(e => s"(${e.vhdl})").mkString(s" $op ")
    val decls = terms.flatMap(e => e.decls)
    VhdlExpr(vhdl, decls)
  }
}

private sealed trait ExprGenMode
private object NormalMode extends ExprGenMode
private object InFunctionMode extends ExprGenMode

private object VhdlExprGenerator {

  /** Convert an expression to VHDL.
    *
    * @param e
    *   The expression to convert
    * @return
    *   A VHDL expression along with the signals required by that expression
    */
  def exprToVhdl(e: Expr)(implicit mode: ExprGenMode = NormalMode): VhdlExpr = {
    e match {
      case x: Param  => VhdlExpr(x.name, Seq())
      case IntCst(n) => VhdlExpr(n.toString, Seq())
      // TODO: Specially handle cases like x + (-1 * y)?
      case Sum(terms @ _*)    => VhdlOp("+", terms.map(exprToVhdl))
      case Prod(factors @ _*) => VhdlOp("*", factors.map(exprToVhdl))
      case Div(e1, e2)        => VhdlOp("/", Seq(e1, e2).map(exprToVhdl))
      case Mod(e1, e2)        => VhdlOp("rem", Seq(e1, e2).map(exprToVhdl))
      case True               => VhdlExpr("true", Seq())
      case False              => VhdlExpr("false", Seq())
      case mux @ Mux(c, t, f) =>
        val cv = exprToVhdl(c)
        val tv = exprToVhdl(t)
        val fv = exprToVhdl(f)
        val tempVar = intermediateVar(
          "ite",
          s"(${tv.vhdl}) when (${cv.vhdl}) else (${fv.vhdl})",
          VhdlType(mux.typ),
          mode
        )
        VhdlExpr(tempVar.name, tempVar +: (cv.decls ++ tv.decls ++ fv.decls))
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
        val tempVar =
          intermediateVar("tupaccess_t", tv.vhdl, VhdlType(t.typ), mode)
        VhdlExpr(s"${tempVar.name}.i_$i", tempVar +: tv.decls)

      case Function(x, body) =>
        // TODO: Un-curry
        val bodyVhdl = exprToVhdl(body)(InFunctionMode)
        val func = {
          val name = Param("f")().name
          val (inType, outType) = e.typ.asInstanceOf[TyArrow] match {
            case TyArrow(t1, t2) => (VhdlType(t1), VhdlType(t2))
          }
          VhdlFunction(
            name = name,
            args = Seq((x.name, inType)),
            returnType = outType,
            // The expression generator *prepends* temporary variables or
            // signals to the list (i.e., a variable will come before its
            // dependencies).
            // That's why the list needs to be reversed.
            decls = bodyVhdl.decls.reverse,
            ret = bodyVhdl.vhdl,
            mode = ImpureFunction
          )
        }
        VhdlExpr(func.name, Seq(func))
      case FunCall(f, arg) =>
        val fv = exprToVhdl(f)
        val av = exprToVhdl(arg)
        VhdlExpr(s"${fv.vhdl}(${av.vhdl})", fv.decls ++ av.decls)

      case VecLiteral(elems @ _*) =>
        val vhdlElems = elems.map(exprToVhdl)
        val assignments = vhdlElems.zipWithIndex
          .map({ case (e, i) => s"$i => ${e.vhdl}" })
          .mkString(", ")
        VhdlExpr(s"($assignments)", vhdlElems.flatMap(e => e.decls))
      case VecBuild(len, f) if len.freeVars().isEmpty =>
        // TODO: Use for-generate here instead?
        val n = ir.eval(len).asInstanceOf[IntCst].i
        val elems =
          (0 until n).map(i => PartialEvalPass.partialEval(FunCall(f, i)()))
        exprToVhdl(VecLiteral(elems: _*)().tchk())
      case VecBuild(n, _) =>
        throw new IllegalArgumentException(
          s"VecBuild with non-constant size ($n) is not supported."
        )
      case VecAccess(v, i) =>
        // TODO: Have a special case for when the index is static?
        val vv = exprToVhdl(v)
        val iv = exprToVhdl(i)
        VhdlExpr(s"vec_access(${vv.vhdl}, ${iv.vhdl})", vv.decls ++ iv.decls)

      case _: SyntaxSugar =>
        throw new IllegalArgumentException(
          s"Syntax sugar must be removed before hardware generation."
        )
    }
  }

  private def intermediateVar(
      namePrefix: String,
      assign: String,
      typ: VhdlType,
      mode: ExprGenMode
  ): VarOrSigDecl = {
    val name = Param(namePrefix)().name
    mode match {
      case NormalMode =>
        Signal(
          category = "Intermediate signals",
          name = name,
          typ = typ,
          assignStmt = Some(s"$name <= $assign;")
        )
      case InFunctionMode =>
        VhdlVariable(name = name, typ = typ, assignStmt = s"$name := $assign;")
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
