package mhir.gen.vhdl
package transform

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck.TypeCheck

import scala.collection.immutable.ListMap

/** Transformation that inserts intermediate variables where required for VHDL
  * generation.
  *
  * For example, in [[VhdlExprGenerator]], "if-then-else" is translated to a
  * "concurrent conditional signal assignment"
  *
  * {{{
  *   tmp <= e1 when c else e2;
  * }}}
  *
  * But this is a statement, not an expression, so you can't write something
  * like
  *
  * {{{
  *   (e1 when c else e2) + 1
  * }}}
  *
  * Therefore, this pass will find all the "if-then-else" expressions (among
  * others) and bind them to intermediate variables.
  */
object IntermediateInsertion {

  def apply(s: GenStmBuild): GenStmBuild = {
    var intermediates = ListMap[Param, Intermediate]()
    val (data, dataIntermediates) = this.apply(s.data)
    intermediates ++= dataIntermediates
    val (valid, validIntermediates) = this.apply(s.valid)
    intermediates ++= validIntermediates
    val accumulators = s.accumulators.map({ case (x, (z, next)) =>
      // TODO: what if z requires intermediate values? In practice I don't
      //       think this will ever happen now, but at some point I may add
      //       generics or extern constants or something, and then the
      //       programmer might do something like
      //           if MY_GENERIC then ... else ...
      val (newNext, nextIntermediates) = this.apply(next)
      intermediates ++= nextIntermediates
      x -> (z, newNext)
    })
    val producers = s.producers.map({ case (x, (p, ready)) =>
      val (newReady, readyIntermediates) = this.apply(ready)
      intermediates ++= readyIntermediates
      x -> (p, newReady)
    })
    GenStmBuild(
      data = data,
      valid = valid,
      accumulators = accumulators,
      producers = producers,
      intermediates = intermediates
    )
  }

  def apply(e: Expr): (Expr, ListMap[Param, Intermediate]) = {
    new ExprIntermediateInsertion(ListMap()).resultAndIntermediates(e)
  }
}

private class ExprIntermediateInsertion(
    var intermediates: ListMap[Param, Intermediate]
) {

  def resultAndIntermediates(e: Expr): (Expr, ListMap[Param, Intermediate]) = {
    val result = this.run(e)
    (result, this.intermediates)
  }

  private def run(e: Expr): Expr = {
    // TODO: Avoid introducing needless intermediate variables at the top level
    //       (i.e., when this expression is going to be the right-hand side of
    //       an assignment statement already)?
    e match {
      // --- Variable binders --------------------------------------------------
      // Be careful with variable binders! The variable(s) they introduce are
      // not available in the outer scope, so I'll need to use a different
      // approach within them (e.g., making the body a series of let bindings?).
      case f @ Function(x, body) =>
        val (newBody, intermediates) =
          new ExprIntermediateInsertion(ListMap()).resultAndIntermediates(body)
        // Some intermediates can be kept within the function (e.g., simple
        // data) and some must be moved out (e.g., another function declaration)
        var innerIntermediates =
          ListMap[Param, Intermediate with AllowedInFunction]()
        var outerIntermediates = Map[Param, Intermediate]()
        for ((x, i) <- intermediates) {
          i match {
            // Keep inside
            case i: DataIntermediate     => innerIntermediates += x -> i
            case i: FunctionIntermediate => innerIntermediates += x -> i
            // Move outside
            case i @ StmDataIntermediate(p) =>
              outerIntermediates += x -> i
            // Not allowed
            case _: IpBlockInst =>
              throw new AssertionError(
                "IP blocks should not be instantiated inside functions"
              )
          }
        }
        this.intermediates ++= outerIntermediates
        val fName = Param("f")(f.typ)
        this.intermediates += fName -> FunctionIntermediate(
          Seq(x),
          innerIntermediates,
          newBody
        )
        fName
      case vb @ VecBuild(nExpr, f) =>
        // TODO: Handle this properly (emit some kind of loop, not a list of
        //       each and every element)
        val IntCst(n) = if (nExpr.freeVars.isEmpty) {
          mhir.eval.eval(nExpr)
        } else {
          throw new IllegalArgumentException(
            s"VHDL generation is not currently supported with non-constant vbuild length ($nExpr)"
          )
        }
        val fName = this.run(f)
        val TyArrow(idxTyp, _) = f.typ
        this.run(
          VecLiteral(
            (0 until n.toInt).map(i => FunCall(fName, C(i)(idxTyp))()): _*
          )(if (n == 0) vb.typ else Missing).tchk()
        )

      // --- Cases that require an intermediate variable -----------------------
      case Undefined(typ) =>
        val temp = Param("undef")(typ)
        this.intermediates += temp -> DataIntermediate(Undefined(typ))
        temp
      case Mux(c, t, f) =>
        // TODO: Specially handle "if-elseif-else," for readability? This may
        //       require changes in the VHDL expression generator as well.
        val c2 = this.run(c)
        val t2 = this.run(t)
        val f2 = this.run(f)
        val temp = Param("ite")(t.typ)
        this.intermediates += temp -> DataIntermediate(Mux(c2, t2, f2)().tchk())
        temp
      case bits @ Bits(e) =>
        val e2 = this.run(e)
        val temp = Param("bits")(bits.typ)
        this.intermediates += temp -> DataIntermediate(Bits(e2)().tchk())
        temp
      case ia @ InterpretAs(e, targetTyp) =>
        val e2 = this.run(e)
        val temp = Param("interpret_as")(ia.typ)
        this.intermediates += temp -> DataIntermediate(
          InterpretAs(e2, targetTyp)().tchk()
        )
        temp
      case tup @ Tuple(elems @ _*) =>
        val elems2 = elems.map(this.run)
        val temp = Param("t")(tup.typ)
        this.intermediates += temp -> DataIntermediate(
          Tuple(elems2: _*)().tchk()
        )
        temp
      case TupleAccess(t, i) =>
        // The left-hand side of a record access must be a "prefix," which can
        // be a "name" (which also includes things like tuple accesses) or a
        // function call.
        val t2 = this.run(t)
        t2 match {
          case _: Param | _: StmData =>
            // No need for a temporary variable; the left-hand side is already
            // just a name
            TupleAccess(t2, i)().tchk()
          case _ =>
            val temp = Param("tmp")(t2.typ)
            this.intermediates += temp -> DataIntermediate(t2)
            TupleAccess(temp, i)().tchk()
        }
      case vec @ VecLiteral(elems @ _*) =>
        val elems2 = elems.map(this.run)
        val vec2 =
          VecLiteral(elems2: _*)(if (elems.isEmpty) vec.typ else Missing).tchk()
        val temp = Param("v")(vec.typ)
        this.intermediates += temp -> DataIntermediate(vec2)
        temp
      case VecAccess(v, i) =>
        val v2 = this.run(v)
        val i2 = this.run(i)
        v2 match {
          case _: Param | _: StmData =>
            // No need for a temporary variable; the left-hand side is already
            // just a name
            VecAccess(v2, i2)().tchk()
          case _ =>
            val temp = Param("tmp")(v2.typ)
            this.intermediates += temp -> DataIntermediate(v2)
            VecAccess(temp, i2)().tchk()
        }
      case StmData(x: Param) =>
        val TyStm(elemTyp, _) = x.typ
        val temp = Param(s"${x.name}_data_internal")(elemTyp)
        this.intermediates += temp -> StmDataIntermediate(x)
        temp

      // --- Illegal cases -----------------------------------------------------
      case StmData(e) =>
        throw new IllegalArgumentException(
          s"invalid argument to sdata (expected a variable but found $e)"
        )
      case _: StmBuild | _: LetStm | _: StmLiteral =>
        throw new IllegalArgumentException(
          s"intermediate value insertion is not applicable to stream expressions;"
            + s" it should only be used for data types"
        )
      case _: SyntaxSugar =>
        throw new IllegalArgumentException(
          "expression must be lowered before intermediate value insertion"
        )

      // --- Normal case -------------------------------------------------------
      case e => e.map(this.run).tchk()
    }
  }
}
