package mhir.gen.vhdl

import mhir.canonicalize._
import mhir.gen.CodegenError
import mhir.ir._
import mhir.sem._
import mhir.typecheck._

private[vhdl] case class FlatPipeline(
    sbuilds: Seq[(Param, StmBuild)],
    lets: Seq[(Param, Int, Set[Param])],
    inputs: Set[Param],
    unusedInputs: Set[Param],
    sink: Param
)

object FlattenPipeline {

  private[vhdl] def apply(
      f: Expr,
      options: VhdlGeneratorOptions
  ): FlatPipeline = {
    validateExpr(f, options)
    val (inputs, stm) = TypeChecker.unwrapTopLevelFunction(f)
    val unusedInputs = inputs.toSet.diff(stm.freeVars)
    val anfStm = StmAnfConverter.convert(stm)
    val pipe = listChildren(anfStm, inputs.toSet, unusedInputs)
    ensureAtLeastOneBuffer(pipe)
  }

  private def validateExpr(e: Expr, options: VhdlGeneratorOptions): Unit = {
    require(
      e.typ != Missing,
      "Expression must be type checked before hardware generation."
    )
    require(
      !e.hasSyntaxSugar,
      "Expression must be lowered before hardware generation."
    )
    require(
      e.freeVars.isEmpty,
      s"Cannot generate hardware for expression with free variables (${e.freeVars})."
    )
    val (inputs, stm) = e match {
      case f: Function => TypeChecker.unwrapTopLevelFunction(f)
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
      if (options.reservedKeywords.contains(x.name)) {
        throw CodegenError(
          s"'${x.name}' cannot be used as an input stream name, since it is a reserved keyword in VHDL"
        )
      }
    }
    options.outName match {
      case None => ()
      case Some(outName) =>
        if (options.reservedKeywords.contains(outName)) {
          throw CodegenError(
            s"'$outName' cannot be used as an output stream name, since it is a reserved keyword in VHDL"
          )
        }
    }
  }

  /** Makes a fresh copy of a given variable for each place it occurs free in
    * the given expression.
    *
    * @example
    *   `StmZip(s, s)` may be replaced with `StmZip(s1, s2)`, where `s`, `s1`,
    *   and `s2` are all different from each other.
    *
    * @param x
    *   the variable to freshen.
    * @param expr
    *   the expression in which to search for the variable.
    * @return
    *   all the fresh copies of the variable, along with the updated expression.
    */
  private def makeVariantsOfFreeVar(
      x: Param,
      expr: Expr
  ): (Set[Param], Expr) = {
    require(expr.hasType)
    val (xs, newE) = expr match {
      case y: Param if y == x =>
        val newX = x.freshCopy
        (Set(newX), newX)
      case Function(y, _) if y == x =>
        (Set[Param](), expr)
      case LetStm(_, y, _, _) if y == x =>
        (Set[Param](), expr)
      case s: StmBuild if s.accVars.contains(x) =>
        (Set[Param](), expr)
      case _ =>
        val (freshVars, newChildren) =
          expr.children.map(e => makeVariantsOfFreeVar(x, e)).unzip
        (freshVars.flatten.toSet, expr.rebuild(expr.typ, newChildren))
    }
    assert(newE.typ == expr.typ)
    (xs, newE)
  }

  private def listChildren(
      stm: Expr,
      inputs: Set[Param],
      unusedInputs: Set[Param]
  ): FlatPipeline = {
    stm match {
      case LetInlineStm(x, in: StmBuild, out) =>
        val rest = listChildren(out, inputs, unusedInputs)
        rest.copy(sbuilds = (x -> in) +: rest.sbuilds)
      case LetInlineStm(_, in, _) =>
        // TODO: Proper error message
        ???
      case LetStm(bufSizeExpr, x, in: Param, out) =>
        val IntCst(bufSize) = mhir.eval.eval(bufSizeExpr)
        val (xs, newOut) = makeVariantsOfFreeVar(x, out)
        val rest = listChildren(newOut, inputs, unusedInputs)
        rest.copy(lets = (in, bufSize.toInt, xs) +: rest.lets)
      case LetStm(_, _, in, _) =>
        // TODO: Proper error message
        ???
      case x: Param =>
        FlatPipeline(
          sbuilds = Seq(),
          lets = Seq(),
          inputs = inputs,
          unusedInputs = unusedInputs,
          sink = x
        )
      case e =>
        throw new IllegalArgumentException(
          s"Expected pipeline output to be a variable, but found ${e.getClass.getSimpleName}: $e"
        )
    }
  }

  private def ensureAtLeastOneBuffer(pipe: FlatPipeline): FlatPipeline = {
    if (pipe.inputs.contains(pipe.sink)) {
      val newSink = Param("s")(pipe.sink.typ)
      val nop = {
        val TyStm(typ, n) = pipe.sink.typ
        val s = Param("s")(TyStm(typ, -1))
        StmBuild(
          n,
          StmData(s)(),
          True,
          Map[Param, (Expr, Expr)](s -> (pipe.sink, True))
        )().tchk().asInstanceOf[StmBuild]
      }
      pipe.copy(sbuilds = pipe.sbuilds :+ (newSink, nop), sink = newSink)
    } else {
      pipe
    }
  }
}
