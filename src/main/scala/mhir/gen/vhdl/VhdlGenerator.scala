package mhir.gen.vhdl

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import os.Path

import scala.annotation.tailrec

/** The main class for generating VHDL from an [[mhir.ir.Expr]].
  *
  * To generate VHDL, use [[emitVhdl]].
  */
object VhdlGenerator {

  /** Creates a VHDL design for the given expression and saves it in the given
    * directory.
    *
    * @param f
    *   the expression for which to generate VHDL.
    * @param dir
    *   the directory in which to save the design.
    */
  def emitVhdl(f: Expr, dir: Path): Unit = {
    validateExpr(f)
    val (inputs, stm) = unwrapTopLevelFunction(f, rename = true)
    val topComponent = AnyStmVhdl(stm, inputs.toSet, "top")
    VhdlWriter.emit(topComponent, dir)
  }

  // TODO: Move this somewhere else, since it's used in many places?
  def unwrapTopLevelFunction(
      f: Expr,
      rename: Boolean
  ): (Seq[Param], Expr) = {
    @tailrec
    def unwrap(e: Expr, inputs: Seq[Param]): (Seq[Param], Expr) = {
      e match {
        case Function(x, e) if x.typ == TyTuple() =>
          unwrap(e, inputs)
        case Function(x, e) =>
          if (rename) {
            val y = Param(s"I${inputs.length}", -1)(x.typ)
            assert(!e.freeVars().contains(y))
            unwrap(e.subPreserveType(x -> y), y +: inputs)
          } else {
            unwrap(e, x +: inputs)
          }
        case e =>
          (inputs, e)
      }
    }

    val (inputSeq, stm) = unwrap(f, Seq())
    if (inputSeq.map(x => x.name).toSet.size != inputSeq.length) {
      val paramList = inputSeq.reverse.map(x => x.name).mkString(", ")
      throw new IllegalArgumentException(
        s"Duplicate parameters in top-level parameter list $paramList."
      )
    }
    (inputSeq.reverse, stm)
  }

  def validateExpr(e: Expr): Unit = {
    require(
      e.typ != Missing,
      "Expression must be type checked before hardware generation."
    )
    require(
      !e.contains(classOf[SyntaxSugar]),
      "Expression must be lowered before hardware generation."
    )
    require(
      e.freeVars().isEmpty,
      s"Cannot generate hardware for expression with free variables (${e.freeVars()})."
    )
    val (inputs, stm) = e match {
      case f: Function => unwrapTopLevelFunction(f, rename = false)
      case e           => (Seq(), e)
    }
    for (x <- inputs) {
      require(
        x.typ.isInstanceOf[TyStm],
        s"Top-level parameter has type ${x.typ}."
          + " All top-level parameters must be streams."
      )
      val numOccurrences = stm.countFreeOccurrences(x)
      require(
        numOccurrences <= 1,
        s"Top-level parameter $x is used $numOccurrences times."
          + " No top-level parameter should be used more than once."
          + " To describe a stream with multiple consumers, consider using LetStm."
      )
    }
  }

  def valueToStdLogicVector(v: Expr): String = {
    mhir.ir.eval(v).tchk() match {
      case False => "\"0\""
      case True  => "\"1\""
      case c: IntCst =>
        c.typ.asInstanceOf[TyAnyInt] match {
          case TyUInt(w) => s"std_logic_vector(to_unsigned(${c.i}, $w))"
          case TySInt(w) => s"std_logic_vector(to_signed(${c.i}, $w))"
        }
      case c: FixCst =>
        valueToStdLogicVector(C(c.numer)(c.typ.t))
      case Tuple(elems @ _*) =>
        if (elems.isEmpty) {
          "\"\""
        } else {
          elems.map(valueToStdLogicVector).map(x => s"($x)").mkString(" & ")
        }
      case vec: VecLiteral =>
        valueToStdLogicVector(Tuple(vec.elems: _*)())
      case _ =>
        throw new IllegalArgumentException(
          s"Cannot convert value $v to a std_logic_vector. Is it really a value?"
        )
    }
  }
}
