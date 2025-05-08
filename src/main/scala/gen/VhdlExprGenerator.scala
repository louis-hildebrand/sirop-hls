package gen

import ir._
import opt.PartialEvalPass

private object VhdlExprGenerator {

  /** Convert an expression to VHDL.
    *
    * @param e
    *   The expression to convert
    * @return
    *   A VHDL expression along with the signals required by that expression
    */
  def exprToVhdl(e: Expr): (String, Seq[Signal]) = {
    e match {
      case x: Param  => (x.name, Seq())
      case IntCst(n) => (n.toString, Seq())
      case Sum(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => exprToVhdl(e)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" + "), signals.flatten)
      case Prod(factors @ _*) =>
        val (vhdlFactors, signals) =
          factors.map(e => exprToVhdl(e)).unzip
        (vhdlFactors.map(x => s"($x)").mkString(" * "), signals.flatten)
      case _: Div => ???
      case _: Mod => ???
      case True   => ("true", Seq())
      case False  => ("false", Seq())
      case ite @ IfThenElse(c, t, f) =>
        val (cVhdl, cSignals) = exprToVhdl(c)
        val (tVhdl, tSignals) = exprToVhdl(t)
        val (fVhdl, fSignals) = exprToVhdl(f)
        val sigName = Param("ite")().name
        val sig = Signal(
          sigName,
          typ = VhdlType(ite.typ),
          init = None,
          assignStmt =
            Some(s"$sigName <= ($tVhdl) when ($cVhdl) else ($fVhdl);"),
          cond = None
        )
        (sigName, sig +: (cSignals ++ tSignals ++ fSignals))
      case Equal(e1, e2) =>
        val (e1Vhdl, e1Signals) = exprToVhdl(e1)
        val (e2Vhdl, e2Signals) = exprToVhdl(e2)
        (s"($e1Vhdl) = ($e2Vhdl)", e1Signals ++ e2Signals)
      case LessThan(e1, e2) =>
        val (e1Vhdl, e1Signals) = exprToVhdl(e1)
        val (e2Vhdl, e2Signals) = exprToVhdl(e2)
        (s"($e1Vhdl) < ($e2Vhdl)", e1Signals ++ e2Signals)
      case Not(e) =>
        val (vhdlE, signals) = exprToVhdl(e)
        (s"not ($vhdlE)", signals)
      case And(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => exprToVhdl(e)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" and "), signals.flatten)
      case Or(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => exprToVhdl(e)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" or "), signals.flatten)

      case TupleAccess(StmNext(s: Param), IntCst(1)) =>
        (s"${s.name}_data_internal", Seq())
      case _: StmLiteral => ???
      case _: StmBuild | _: StmNext | TupleAccess(_: StmNext, _) =>
        throw new IllegalArgumentException(
          s"Cannot generate hardware for ${e.getClass.getSimpleName} in this position."
        )
      case _: StmNextK =>
        throw new IllegalArgumentException(
          s"Cannot generate hardware for ${e.getClass.getSimpleName}."
        )

      case Tuple() => ("\"\"", Seq())
      case Tuple(elems @ _*) =>
        val (vhdlElems, signals) = elems.map(exprToVhdl).unzip
        val assignments = vhdlElems.zipWithIndex
          .map({ case (v, i) => s"i_$i => $v" })
          .mkString(", ")
        (s"($assignments)", signals.flatten)
      case TupleAccess(t, IntCst(i)) =>
        val (vhdlTuple, signals) = exprToVhdl(t)
        val tupSigName = Param("tupaccess_t")().name
        val tupSig = Signal(
          name = tupSigName,
          typ = VhdlType(t.typ),
          assignStmt = Some(s"$tupSigName <= $vhdlTuple;")
        )
        (s"${tupSig.name}.i_$i", tupSig +: signals)

      case _: Function => ???
      case _: FunCall  => ???

      case VecLiteral(elems @ _*) => ???
      case VecBuild(IntCst(n), f) =>
        val vecSigName = Param("vbuild")().name
        val (vec, vecBodySignals) = {
          val (elems, signals) = (0 until n)
            .map(i => {
              // TODO: Partial evaluation here kind of approximates IfThenElse
              //       being short-circuiting---the other branch will be
              //       discarded if the condition evaluates to a bool constant.
              //       But if the condition cannot be evaluated statically,
              //       then it's not the same thing.
              val body = PartialEvalPass.partialEval(FunCall(f, i)())
              exprToVhdl(body)
            })
            .unzip
          val assignments = elems.zipWithIndex
            .map({ case (e, i) => s"$i => $e" })
            .mkString(", ")
          (s"($assignments)", signals.flatten)
        }
        val vecSig = {
          Signal(
            name = vecSigName,
            typ = VhdlType(e.typ),
            assignStmt = Some(s"$vecSigName <= $vec;")
          )
        }
        (vecSig.name, vecSig +: vecBodySignals)
      case VecBuild(n, _) =>
        throw new IllegalArgumentException(
          s"VecBuild with non-constant size ($n) is not supported."
        )
      case VecAccess(v, i) =>
        val (vhdlVec, vecSignals) = exprToVhdl(v)
        val (vhdlIdx, idxSignals) = exprToVhdl(i)
        val vecSig = {
          val name = Param("vecaccess_v")().name
          Signal(
            name = name,
            typ = VhdlType(v.typ),
            assignStmt = Some(s"$name <= $vhdlVec;")
          )
        }
        (s"${vecSig.name}($vhdlIdx)", vecSig +: (vecSignals ++ idxSignals))

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
