package mhir.gen.vhdl

import mhir.ir._
import mhir.typecheck.TypeError

private object VhdlExprGenerator {

  /** Convert an expression to VHDL.
    *
    * @param e
    *   The expression to convert
    * @return
    *   An equivalent VHDL expression.
    */
  def toVhdl(e: Expr): String = {
    require(
      e.hasType,
      "Expression must be type-checked before it can be converted to a VHDL expression."
    )
    e match {
      case Undefined(typ) => makeUndefined(typ)
      case x: Param       => x.name
      case FunCall(f: Param, Tuple(args @ _*)) =>
        s"${f.name}(${args.map(toVhdl).mkString(", ")})"
      case fc: FunCall =>
        throw new IllegalArgumentException(
          s"invalid function call $fc."
            + " The left-hand side must be a variable"
            + " and the function arguments must be provided as a tuple"
            + " (main arg along with context)."
        )

      case c: IntCst =>
        c.typ.asInstanceOf[TyAnyInt] match {
          case TyUInt(w) => s"to_unsigned(${c.i}, $w)"
          case TySInt(w) => s"to_signed(${c.i}, $w)"
        }
      // TODO: Specially handle cases like x + (-1 * y)?
      case Sum(terms @ _*) =>
        terms.map(toVhdl).map(x => s"($x)").mkString(" + ")
      case WrappingSum(terms @ _*) =>
        terms.map(toVhdl).map(x => s"($x)").mkString(" + ")
      case WrappingDiff(e1, e2) =>
        s"(${toVhdl(e1)}) - (${toVhdl(e2)})"
      case Prod(factors @ _*) =>
        val TyAnyInt(w) = e.typ
        makeProduct(factors.map(toVhdl), bitWidth = w)
      case WrappingProd(factors @ _*) =>
        val TyAnyInt(w) = e.typ
        makeProduct(factors.map(toVhdl), bitWidth = w)
      case Div(e1, e2) =>
        s"(${toVhdl(e1)}) / (${toVhdl(e2)})"
      case Mod(e1, e2) =>
        s"(${toVhdl(e1)}) rem (${toVhdl(e2)})"
      case PadTo(e, w) =>
        s"pad(${toVhdl(e)}, $w)"
      case TruncateTo(e, w) =>
        s"truncate(${toVhdl(e)}, $w)"
      case ToSigned(e) =>
        val w = e.typ.asInstanceOf[TyUInt].w
        s"signed(pad(${toVhdl(e)}, ${w + 1}))"
      case ToUnsigned(e) =>
        val w = e.typ.asInstanceOf[TySInt].w
        assert(w >= 1)
        s"truncate(unsigned(${toVhdl(e)}), ${w - 1})"
      case Bits(e) =>
        val vhdlTyp = VhdlType(e.typ)
        VhdlConversionGenerator.fromStdLogicVector(
          VhdlConversionGenerator.toStdLogicVector(toVhdl(e), vhdlTyp),
          vhdlTyp.toBoolVec
        )
      case InterpretAs(e, targetTyp) =>
        val vhdlTyp = VhdlType(e.typ)
        VhdlConversionGenerator.fromStdLogicVector(
          VhdlConversionGenerator.toStdLogicVector(toVhdl(e), vhdlTyp),
          VhdlType(targetTyp)
        )
      case LShift(e1, e2) => s"(${toVhdl(e1)}) sll ${toVhdlInteger(e2)}"
      case ARShift(e1, e2) =>
        val e1Vhdl = toVhdl(e1)
        val e2Vhdl = toVhdlInteger(e2)
        e1.typ.asInstanceOf[TyAnyInt] match {
          case _: TySInt =>
            s"signed(to_stdlogicvector(to_bitvector(std_logic_vector($e1Vhdl)) sra $e2Vhdl))"
          case _: TyUInt =>
            // Arithmetic shift right is the same as logical shift right for
            // unsigned values.
            // We can't use sra here (at least not the bitvector version)
            // because the MSB might be 1.
            s"($e1Vhdl) srl $e2Vhdl"
        }
      case LRShift(e1, e2) => s"(${toVhdl(e1)}) srl ${toVhdlInteger(e2)}"

      case c: FixCst =>
        toVhdl(C(c.numer)(c.typ.t))
      case IntFixProd(e1, e2) =>
        val w1 = e1.typ.asInstanceOf[TyAnyInt].w
        val (w2, shift) = e2.typ.asInstanceOf[TyFix] match {
          case TyFix(TyUInt(w2), shift) => (math.max(w2, shift), shift)
        }
        s"truncate(((${toVhdl(e1)}) * pad(${toVhdl(e2)}, $w2)) srl $shift, $w1)"

      case True             => "true"
      case False            => "false"
      case Equal(e1, e2)    => s"(${toVhdl(e1)}) = (${toVhdl(e2)})"
      case LessThan(e1, e2) => s"(${toVhdl(e1)}) < (${toVhdl(e2)})"
      case Not(e)           => s"not (${toVhdl(e)})"
      case And(terms @ _*) =>
        terms.map(toVhdl).map(x => s"($x)").mkString(" and ")
      case Or(terms @ _*) =>
        terms.map(toVhdl).map(x => s"($x)").mkString(" or ")

      case Tuple() => "\"\""
      case Tuple(elems @ _*) =>
        elems.zipWithIndex
          .map({ case (e, i) => s"i_$i => ${toVhdl(e)}" })
          .mkString("(", ", ", ")")
      case TupleAccess(t, IntCst(i)) => s"${toVhdl(t)}.i_$i"

      case v @ VecLiteral() =>
        VhdlConversionGenerator.fromStdLogicVector(
          "(others => 'X')",
          VhdlType(v.typ)
        )
      case VecLiteral(elems @ _*) =>
        elems.zipWithIndex
          .map({ case (e, i) => s"$i => ${toVhdl(e)}" })
          .mkString("(", ", ", ")")
      case VecAccess(v, i) => s"${toVhdl(v)}(${toVhdlInteger(i)})"

      case e @ (_: Mux | _: VecBuild | _: StmData | _: Function) =>
        throw new AssertionError(
          s"cannot generate expression for ${e.className};"
            + " this should have been handled at an earlier compilation stage"
        )
      case _: StmBuild | _: LetStm =>
        throw new IllegalArgumentException(
          s"cannot generate hardware for ${e.getClass.getSimpleName} in this position"
        )
      case _: StmLiteral =>
        throw new IllegalArgumentException(
          s"cannot generate hardware for ${e.getClass.getSimpleName}"
        )
      case _: SyntaxSugar =>
        throw new IllegalArgumentException(
          s"syntax sugar must be removed before hardware generation"
        )
    }
  }

  def toVhdlInteger(e: Expr): String = {
    e match {
      case IntCst(i) => i.toString
      case e         => s"to_integer(${toVhdl(e)})"
    }
  }

  private def makeUndefined(typ: Type): String = {
    typ match {
      case _: TyAnyInt => "(others => 'X')"
      case _: TyFix    => "(others => 'X')"
      case TyBool      => "false"
      case TyTuple()   => "\"\""
      case TyTuple(ts @ _*) =>
        ts.zipWithIndex
          .map({ case (t, i) => s"i_$i => ${makeUndefined(t)}" })
          .mkString("(", ", ", ")")
      case TyVec(t, _) => s"(others => ${makeUndefined(t)})"
      case Missing | _: TyStm | _: TyArrow =>
        throw new TypeError(
          s"Cannot generate undefined value for type $typ."
        )
    }
  }

  private def makeProduct(factors: Seq[String], bitWidth: Int): String = {
    require(factors.nonEmpty)
    if (factors.length == 1) {
      factors.head
    } else {
      val (leftFactors, rightFactors) = factors.splitAt(factors.length / 2)
      val lhs = makeProduct(leftFactors, bitWidth)
      val rhs = makeProduct(rightFactors, bitWidth)
      s"truncate(($lhs) * ($rhs), $bitWidth)"
    }
  }
}
