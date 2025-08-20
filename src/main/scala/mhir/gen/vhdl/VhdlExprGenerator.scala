package mhir.gen.vhdl

import mhir.ir._
import mhir.ir.typecheck.TypeCheck

/** @param vhdl
  *   VHDL code for this expression
  * @param decls
  *   Declarations required for this expression, including all its
  *   sub-expressions
  */
private[vhdl] case class VhdlExpr(vhdl: String, decls: Seq[Decl])

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
    require(
      e.hasType,
      "Expression must be type-checked before it can be converted to a VHDL expression."
    )
    e match {
      case x: Param => VhdlExpr(x.name, Seq())
      case c: IntCst =>
        c.typ.asInstanceOf[TyAnyInt] match {
          case TyUInt(w) => VhdlExpr(s"to_unsigned(${c.i}, $w)", Seq())
          case TySInt(w) => VhdlExpr(s"to_signed(${c.i}, $w)", Seq())
        }
      // TODO: Specially handle cases like x + (-1 * y)?
      case Sum(terms @ _*) => VhdlOp("+", terms.map(exprToVhdl))
      case Prod(factors @ _*) =>
        val w = e.typ.asInstanceOf[TyAnyInt].w
        makeProduct(factors.map(exprToVhdl), bitWidth = w)
      case Div(e1, e2)             => VhdlOp("/", Seq(e1, e2).map(exprToVhdl))
      case Mod(e1, e2)             => VhdlOp("rem", Seq(e1, e2).map(exprToVhdl))
      case WrappingSum(terms @ _*) => VhdlOp("+", terms.map(exprToVhdl))
      case WrappingDiff(e1, e2)    => VhdlOp("-", Seq(e1, e2).map(exprToVhdl))
      case WrappingProd(factors @ _*) =>
        val w = e.typ.asInstanceOf[TyAnyInt].w
        makeProduct(factors.map(exprToVhdl), bitWidth = w)
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
        assert(w >= 1)
        val ev = exprToVhdl(e)
        VhdlExpr(s"truncate(unsigned(${ev.vhdl}), ${w - 1})", ev.decls)
      case LLShift(e1, e2) =>
        val VhdlExpr(e1Vhdl, e1Decls) = exprToVhdl(e1)
        val VhdlExpr(e2Vhdl, e2Decls) = exprToVhdl(e2)
        VhdlExpr(s"($e1Vhdl) sll to_integer($e2Vhdl)", e1Decls ++ e2Decls)
      case LRShift(e1, e2) =>
        val VhdlExpr(e1Vhdl, e1Decls) = exprToVhdl(e1)
        val VhdlExpr(e2Vhdl, e2Decls) = exprToVhdl(e2)
        VhdlExpr(s"($e1Vhdl) srl to_integer($e2Vhdl)", e1Decls ++ e2Decls)

      case c: FixCst =>
        exprToVhdl(C(c.numer)(c.typ.t))
      case IntFixProd(e1, e2) =>
        val VhdlExpr(e1Vhdl, e1Decls) = exprToVhdl(e1)
        val VhdlExpr(e2Vhdl, e2Decls) = exprToVhdl(e2)
        val w1 = e1.typ.asInstanceOf[TyAnyInt].w
        val w2 = e2.typ.asInstanceOf[TyFix] match {
          case TyFix(TyUInt(w2), shift) => math.max(w2, shift)
        }
        val lsb = e2.typ.asInstanceOf[TyFix].shift
        val msb = lsb + w1 - 1
        val tempVar = {
          val name = Param("int_fix_prod")().name
          val typ = TyUInt(w1 + w2)
          mode match {
            case NormalMode =>
              Signal(
                category = "Intermediate signals",
                name = name,
                typ = VhdlType(typ),
                assignStmt = Some(s"$name <= ($e1Vhdl) * pad($e2Vhdl, $w2);")
              )
            case InFunctionMode =>
              VhdlVariable(
                name = name,
                typ = VhdlType(typ),
                assignStmt = s"$name := ($e1Vhdl) * ($e2Vhdl);"
              )
          }
        }
        VhdlExpr(
          s"${tempVar.name}($msb downto $lsb)",
          tempVar +: (e1Decls ++ e2Decls)
        )

      case True  => VhdlExpr("true", Seq())
      case False => VhdlExpr("false", Seq())
      case Mux(c, t, f) =>
        val cv = exprToVhdl(c)
        val tv = exprToVhdl(t)
        val fv = exprToVhdl(f)
        val tempVar = {
          val name = Param("ite")().name
          mode match {
            case NormalMode =>
              Signal(
                category = "Intermediate signals",
                name = name,
                typ = VhdlType(t.typ),
                assignStmt = Some(
                  s"$name <= (${tv.vhdl}) when (${cv.vhdl}) else (${fv.vhdl});"
                )
              )
            case InFunctionMode =>
              val stmt =
                s"""if (${cv.vhdl}) then
                   |    $name := ${tv.vhdl};
                   |else
                   |    $name := ${fv.vhdl};
                   |end if;
                   |""".stripMargin.stripTrailing
              VhdlVariable(
                name = name,
                typ = VhdlType(t.typ),
                assignStmt = stmt
              )
          }
        }
        VhdlExpr(tempVar.name, tempVar +: (cv.decls ++ tv.decls ++ fv.decls))
      case Equal(e1, e2)    => VhdlOp("=", Seq(e1, e2).map(exprToVhdl))
      case LessThan(e1, e2) => VhdlOp("<", Seq(e1, e2).map(exprToVhdl))
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
      case _: StmBuild | _: LetStm =>
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
        val tempVar = {
          val name = Param("tupaccess_t")().name
          mode match {
            case NormalMode =>
              Signal(
                category = "Intermediate signals",
                name = name,
                typ = VhdlType(t.typ),
                assignStmt = Some(s"$name <= ${tv.vhdl};")
              )
            case InFunctionMode =>
              VhdlVariable(
                name = name,
                typ = VhdlType(t.typ),
                assignStmt = s"$name := ${tv.vhdl};"
              )
          }
        }
        VhdlExpr(s"${tempVar.name}.i_$i", tempVar +: tv.decls)

      case Function(x, body) =>
        val bodyVhdl = exprToVhdl(body)(InFunctionMode)
        val func = {
          val name = Param("f")().name
          val (inType, outType) = e.typ.asInstanceOf[TyArrow] match {
            case TyArrow(t1, t2) if t1.isData && t2.isData =>
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
        val av = exprToVhdl(arg)
        VhdlExpr(s"${fv.vhdl}(${av.vhdl})", fv.decls ++ av.decls)

      case v @ VecLiteral(elems @ _*) =>
        val vhdlElems = elems.map(exprToVhdl)
        val tempVar = {
          val name = Param("vec")().name
          val vec = if (elems.isEmpty) {
            VhdlConversionGenerator.fromStdLogicVector(
              "(others => 'X')",
              VhdlType(v.typ)
            )
          } else {
            vhdlElems.zipWithIndex
              .map({ case (e, i) => s"$i => ${e.vhdl}" })
              .mkString("(", ", ", ")")
          }
          mode match {
            case NormalMode =>
              Signal(
                category = "Intermediate signals",
                name = name,
                typ = VhdlType(v.typ),
                assignStmt = Some(s"$name <= $vec;")
              )
            case InFunctionMode =>
              VhdlVariable(
                name = name,
                typ = VhdlType(v.typ),
                assignStmt = s"$name := $vec;"
              )
          }
        }
        VhdlExpr(tempVar.name, tempVar +: vhdlElems.flatMap(e => e.decls))
      case vb @ VecBuild(len, f) =>
        if (len.freeVars().isEmpty) {
          // TODO: Use for-generate here instead?
          val n = mhir.ir.eval(len).asInstanceOf[IntCst].i
          val idxTyp = f.param.typ.asInstanceOf[TyAnyInt]
          val elems =
            (0 until n.toInt).map(i =>
              f.body.subPreserveType(f.param -> C(i)(idxTyp))
            )
          exprToVhdl(VecLiteral(elems: _*)(vb.typ))
        } else {
          throw new IllegalArgumentException(
            s"VecBuild with non-constant size ($len) is not supported."
          )
        }
      case VecAccess(v, i) =>
        // TODO: Have a special case for when the index is static?
        val vv = exprToVhdl(v)
        val iv: VhdlExpr = {
          val actualWidth = i.typ.asInstanceOf[TyUInt].w
          val expectedWidth =
            VhdlType(v.typ).asInstanceOf[VhdlArray].idxBitWidth
          if (actualWidth > expectedWidth) {
            exprToVhdl(TruncateTo(i, expectedWidth)().tchk())
          } else if (actualWidth < expectedWidth) {
            exprToVhdl(PadTo(i, expectedWidth)().tchk())
          } else {
            exprToVhdl(i)
          }
        }
        VhdlExpr(s"vec_access(${vv.vhdl}, ${iv.vhdl})", vv.decls ++ iv.decls)

      case _: SyntaxSugar =>
        throw new IllegalArgumentException(
          s"Syntax sugar must be removed before hardware generation."
        )
    }
  }

  private def makeProduct(factors: Seq[VhdlExpr], bitWidth: Int): VhdlExpr = {
    require(factors.nonEmpty)
    if (factors.length == 1) {
      factors.head
    } else {
      val (leftFactors, rightFactors) = factors.splitAt(factors.length / 2)
      val lhs = makeProduct(leftFactors, bitWidth)
      val rhs = makeProduct(rightFactors, bitWidth)
      VhdlExpr(
        s"truncate((${lhs.vhdl}) * (${rhs.vhdl}), $bitWidth)",
        lhs.decls ++ rhs.decls
      )
    }
  }

  def valueToVhdl(v: Expr): String = {
    mhir.ir.eval(v).tchk() match {
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
