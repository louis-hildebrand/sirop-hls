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
      case x: Param => VhdlExpr(x.name, Seq())
      case c: IntCst =>
        c.typ.asInstanceOf[TyAnyInt] match {
          case TyUInt(w) => VhdlExpr(s"to_unsigned(${c.i}, $w)", Seq())
          case TySInt(w) => VhdlExpr(s"to_signed(${c.i}, $w)", Seq())
        }
      case sum: Sum   => makeSum(sum)(mode)
      case prod: Prod => makeProd(prod)(mode)
      case div: Div   => makeDiv(div)(mode)
      case mod: Mod   => makeMod(mod)(mode)
      case PadTo(e, w) =>
        val ev = exprToVhdl(e)
        VhdlExpr(s"pad(${ev.vhdl}, $w)", ev.decls)
      case TruncateTo(e, w) =>
        val ev = exprToVhdl(e)
        VhdlExpr(s"truncate(${ev.vhdl}, $w)", ev.decls)
      case ToSigned(e) =>
        val w = e.typ.asInstanceOf[TyUInt].w
        val ev = exprToVhdl(e)
        VhdlExpr(s"signed(pad(${ev.vhdl}, ${w + 1}))", ev.decls)
      case ToUnsigned(e) =>
        val w = e.typ.asInstanceOf[TySInt].w
        val newWidth = math.max(0, w - 1)
        val ev = exprToVhdl(e)
        VhdlExpr(s"truncate(unsigned(${ev.vhdl}), $newWidth)", ev.decls)

      case True  => VhdlExpr("true", Seq())
      case False => VhdlExpr("false", Seq())
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
      case eq: Equal    => makeEqual(eq)(mode)
      case lt: LessThan => makeLessThan(lt)(mode)
      case Not(e) =>
        val ve = exprToVhdl(e)
        VhdlExpr(s"not (${ve.vhdl})", ve.decls)
      case And(terms @ _*) => VhdlOp("and", terms.map(exprToVhdl))
      case Or(terms @ _*)  => VhdlOp("or", terms.map(exprToVhdl))

      case StmData(s: Param) =>
        VhdlExpr(s"${s.name}_data_internal", Seq())
      case StmData(e) =>
        throw new IllegalArgumentException(
          s"Invalid argument to ${StmData.getClass.getSimpleName}: $e"
        )
      case _: StmBuild =>
        throw new IllegalArgumentException(
          s"Cannot generate hardware for ${e.getClass.getSimpleName} in this position."
        )
      case _: StmNextK | _: StmLiteral =>
        throw new IllegalArgumentException(
          s"Cannot generate hardware for ${e.getClass.getSimpleName}."
        )

      case Tuple(elems @ _*) =>
        elems match {
          case Seq() =>
            VhdlExpr("\"\"", Seq())
          case elems =>
            val vhdlElems = elems.map(exprToVhdl)
            val assignments = vhdlElems.zipWithIndex
              .map({ case (v, i) => s"i_$i => ${v.vhdl}" })
              .mkString(", ")
            VhdlExpr(s"($assignments)", vhdlElems.flatMap(e => e.decls))
        }
      case TupleAccess(t, IntCst(i)) =>
        val tv = exprToVhdl(t)
        val tempVar =
          intermediateVar("tupaccess_t", tv.vhdl, VhdlType(t.typ), mode)
        VhdlExpr(s"${tempVar.name}.i_$i", tempVar +: tv.decls)

      case Function(x, body) =>
        val bodyVhdl = exprToVhdl(body)(InFunctionMode)
        val func = {
          val name = Param("f")().name
          val (inType, outType) = e.typ.asInstanceOf[TyArrow] match {
            case TyArrow(t1, t2)
                if Default.hasDefault(t1) && Default.hasDefault(t2) =>
              (VhdlType(t1), VhdlType(t2))
            case TyArrow(t1, t2) =>
              throw new NotImplementedError(
                s"Cannot generate VHDL function with input type $t1 and output type $t2."
              )
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
        val inTyp = f.typ.asInstanceOf[TyArrow].t1
        val reshapedArg =
          ReshapeData(arg, f.typ.asInstanceOf[TyArrow].t1)().tchk().lower()
        val av = exprToVhdl(reshapedArg)
        VhdlExpr(s"${fv.vhdl}(${av.vhdl})", fv.decls ++ av.decls)

      case VecLiteral(elems @ _*) =>
        val vhdlElems = elems.map(exprToVhdl)
        val assignments = vhdlElems.zipWithIndex
          .map({ case (e, i) => s"$i => ${e.vhdl}" })
          .mkString(", ")
        VhdlExpr(s"($assignments)", vhdlElems.flatMap(e => e.decls))
      case VecBuild(len, f) =>
        if (len.freeVars().isEmpty) {
          // TODO: Use for-generate here instead?
          val n = ir.eval(len).asInstanceOf[IntCst].i
          val idxTyp = f.param.typ.asInstanceOf[TyAnyInt]
          val elems =
            (0 until n.toInt).map(i =>
              PartialEvalPass.partialEval(FunCall(f, C(i)(idxTyp))())
            )
          exprToVhdl(VecLiteral(elems: _*)().tchk())
        } else {
          throw new IllegalArgumentException(
            s"VecBuild with non-constant size ($len) is not supported."
          )
        }
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

  private def makeSum(sum: Sum)(implicit mode: ExprGenMode): VhdlExpr = {
    // TODO: Specially handle cases like x + (-1 * y)?
    require(sum.terms.nonEmpty)
    val typ = sum.typ
    assert(
      typ.isInstanceOf[TyAnyInt],
      s"the type of a sum should be an integer type (found type $typ)"
    )
    val reshapedTerms =
      sum.terms.map(e => ReshapeData(e, typ)().tchk().lower())
    VhdlOp("+", reshapedTerms.map(exprToVhdl))
  }

  private def makeProd(prod: Prod)(implicit mode: ExprGenMode): VhdlExpr = {
    def makeTree(factors: Seq[VhdlExpr], bitWidth: Int): VhdlExpr = {
      require(factors.nonEmpty)
      if (factors.length == 1) {
        factors.head
      } else {
        val (leftFactors, rightFactors) = factors.splitAt(factors.length / 2)
        val lhs = makeTree(leftFactors, bitWidth)
        val rhs = makeTree(rightFactors, bitWidth)
        VhdlExpr(
          s"truncate((${lhs.vhdl}) * (${rhs.vhdl}), $bitWidth)",
          lhs.decls ++ rhs.decls
        )
      }
    }

    assert(
      prod.typ.isInstanceOf[TyAnyInt],
      s"the type of a product should be an integer type (found type ${prod.typ})"
    )
    val typ = prod.typ.asInstanceOf[TyAnyInt]
    val reshapedInputs =
      prod.factors.map(e => ReshapeData(e, typ)().tchk().lower())
    makeTree(reshapedInputs.map(exprToVhdl), typ.w)
  }

  private def makeDiv(div: Div)(implicit mode: ExprGenMode): VhdlExpr = {
    val typ = div.typ
    assert(
      typ.isInstanceOf[TyAnyInt],
      s"the type of a quotient should be an integer type (found type $typ)"
    )
    val reshapedInputs =
      div.children.map(e => ReshapeData(e, typ)().tchk().lower())
    VhdlOp("/", reshapedInputs.map(exprToVhdl))
  }

  private def makeMod(mod: Mod)(implicit mode: ExprGenMode): VhdlExpr = {
    val typ = mod.typ
    assert(
      typ.isInstanceOf[TyAnyInt],
      s"the type of Mod should be an integer type (found type $typ)"
    )
    val reshapedInputs =
      mod.children.map(e => ReshapeData(e, typ)().tchk().lower())
    VhdlOp("rem", reshapedInputs.map(exprToVhdl))
  }

  private def makeEqual(eq: Equal)(implicit mode: ExprGenMode): VhdlExpr = {
    val typ = Type.supertype(eq.e1.typ, eq.e2.typ) match {
      case Some(typ) => typ
      case None =>
        throw new TypeError(
          s"Could not find common supertype for operand types ${eq.e1.typ} and ${eq.e2.typ} in ${Equal.getClass.getSimpleName}."
        )
    }
    val reshapedInputs =
      eq.children.map(e => ReshapeData(e, typ)().tchk().lower())
    VhdlOp("=", reshapedInputs.map(exprToVhdl))
  }

  private def makeLessThan(
      lt: LessThan
  )(implicit mode: ExprGenMode): VhdlExpr = {
    val typ = Type.supertype(lt.e1.typ, lt.e2.typ) match {
      case Some(typ) => typ
      case None =>
        throw new TypeError(
          s"Could not find common supertype for operand types ${lt.e1.typ} and ${lt.e2.typ} in ${LessThan.getClass.getSimpleName}."
        )
    }
    val reshapedInputs =
      lt.children.map(e => ReshapeData(e, typ)().tchk().lower())
    VhdlOp("<", reshapedInputs.map(exprToVhdl))
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
      case False => "false"
      case True  => "true"
      case c: IntCst =>
        c.typ.asInstanceOf[TyAnyInt] match {
          case TyUInt(w) => s"to_unsigned(${c.i}, $w)"
          case TySInt(w) => s"to_signed(${c.i}, $w)"
        }
      case Tuple() => "\"\""
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
