package mhir.gen.vhdl
package transform

import mhir.canonicalize._
import mhir.gen.vhdl.ir._
import mhir.ir._
import mhir.typecheck.TypeCheck

import scala.annotation.tailrec
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
    new IntermediateInsertion(ListMap()).apply(s)
  }

  def apply(e: Expr): (Expr, ListMap[Param, Intermediate]) = {
    val pass = new IntermediateInsertion(ListMap())
    val newExpr = pass.getExprAndMutateIntermediates(e)
    (newExpr, pass.intermediates)
  }
}

private class IntermediateInsertion(
    var intermediates: ListMap[Param, Intermediate]
) {

  def apply(s: GenStmBuild): GenStmBuild = {
    val data = this.getExprAndMutateIntermediates(s.data)
    val valid = this.getExprAndMutateIntermediates(s.valid)
    val accumulators = s.accumulators.map({
      case (x, ExprAccumulator(init, next)) =>
        val newInit = init.map(this.getDataIntermediateAndMutateIntermediates)
        val newNext = this.getDataIntermediateAndMutateIntermediates(next)
        x -> ExprAccumulator(newInit, newNext)
      case (x, acc) =>
        x -> acc.map(this.getExprAndMutateIntermediates)
    })
    val producers = s.producers.map({ case (x, ready) =>
      x -> this.getExprAndMutateIntermediates(ready)
    })
    s.intermediates.foreach({
      case (x, i: DataIntermediate) =>
        val newI = i.map(this.getExprAndMutateIntermediates)
        this.intermediates += (x -> newI)
      case (x, i: IpBlockInst) =>
        val newI = i.mapInputs(this.getPortActualAndMutateIntermediates)
        this.intermediates += (x -> newI)
      case (x, f: FunctionIntermediate) =>
        throw new AssertionError(
          s"there should not be any ${f.getClass.getName} yet at this compilation stage"
            + s" (found function $x)"
        )
    })
    GenStmBuild(
      data = data,
      valid = valid,
      accumulators = accumulators,
      producers = producers,
      intermediates = this.intermediates
    )
  }

  /** Insert intermediates where necessary in the given expression; return the
    * updated expression and append the necessary intermediates to the
    * [[intermediates]] field.
    *
    * @return
    *   an expression that can be straightforwardly translated to a VHDL
    *   expression.
    */
  def getExprAndMutateIntermediates(e: Expr): Expr = {
    val (newE, newIntermediates) =
      new ExprIntermediateInsertion(ListMap()).resultAndIntermediates(e)
    this.intermediates ++= newIntermediates
    newE
  }

  /** Similar to [[getExprAndMutateIntermediates]], except that we want the
    * result to be a [[DataIntermediate]] rather than an [[Expr]]. This makes it
    * possible to cut out unnecessary intermediates; i.e., instead of returning
    * an [[ExprIntermediate]] containing a [[Param]] pointing to an
    * [[Intermediate]]; just return that [[Intermediate]] directly.
    */
  private def getDataIntermediateAndMutateIntermediates(
      i: DataIntermediate
  ): DataIntermediate = {
    i match {
      case ExprIntermediate(e) =>
        val (newE, newIntermediates) =
          new ExprIntermediateInsertion(ListMap()).resultAndIntermediates(e)
        newE match {
          case x: Param =>
            // Be careful to not remove the intermediate if it's used by
            // another intermediate.
            // Intermediates are added to the list in order, so if this is the
            // last one it's definitely not used anywhere else.
            newIntermediates.lastOption match {
              case Some((y, i: DataIntermediate)) if y == x =>
                // Cut out the middleman
                this.intermediates ++= newIntermediates.init
                i
              case None =>
                this.intermediates ++= newIntermediates
                ExprIntermediate(newE)
            }
          case _ =>
            this.intermediates ++= newIntermediates
            ExprIntermediate(newE)
        }
      case i => i.map(this.getExprAndMutateIntermediates)
    }
  }

  /** Insert intermediates where necessary to turn the given Sirop expression
    * into
    *
    * @return
    *   an expression that can be used as an "actual part" in a port map.
    */
  private def getPortActualAndMutateIntermediates(e: Expr): Expr = {
    val newE = this.getExprAndMutateIntermediates(e)
    if (isValidPortActual(newE)) {
      newE
    } else {
      val tmp = Param("port_map_arg")(newE.typ)
      this.intermediates += (tmp -> ExprIntermediate(newE))
      tmp
    }
  }

  @tailrec
  private def isValidPortActual(e: Expr): Boolean = {
    e match {
      case _: BoolCst | _: IntCst | _: Param => true
      case TupleAccess(t, _)                 => isValidPortActual(t)
      case VecAccess(v, _: IntCst)           => isValidPortActual(v)
      case _                                 => false
    }
  }
}

private class ExprIntermediateInsertion(
    var intermediates: ListMap[Param, Intermediate]
) {

  def resultAndIntermediates(e: Expr): (Expr, ListMap[Param, Intermediate]) = {
    val result = this.run(e)
    (result, this.intermediates)
  }

  /** Insert intermediates at the appropriate places in the given expression and
    * record them in the [[intermediates]] field.
    *
    * @note
    *   in some cases this will introduce unnecessary intermediates (i.e., the
    *   returned expression is a variable called X, which is used to define an
    *   intermediate but not used anywhere else). However, that can be handled
    *   by the caller (see
    *   [[IntermediateInsertion.getDataIntermediateAndMutateIntermediates]]).
    */
  private def run(e: Expr): Expr = {
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
        var innerIntermediates = ListMap[Param, IntermediateInFunction]()
        var outerIntermediates = Map[Param, Intermediate]()
        for ((x, i) <- intermediates) {
          i match {
            // Move outside
            case i: StmDataIntermediate => outerIntermediates += x -> i
            // Keep inside
            case i: DataIntermediate     => innerIntermediates += x -> i
            case i: FunctionIntermediate => innerIntermediates += x -> i
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
        this.intermediates += temp -> ExprIntermediate(Undefined(typ))
        temp
      case Mux(c, t, f) =>
        // TODO: Specially handle "if-elseif-else," for readability? This may
        //       require changes in the VHDL expression generator as well.
        val c2 = this.run(c)
        val t2 = this.run(t)
        val f2 = this.run(f)
        val temp = Param("ite")(t.typ)
        this.intermediates += temp -> MuxIntermediate(c2, t2, f2)
        temp
      case bits @ Bits(e) =>
        val e2 = this.run(e)
        val temp = Param("bits")(bits.typ)
        this.intermediates += temp -> ExprIntermediate(Bits(e2)().tchk())
        temp
      case ia @ InterpretAs(e, targetTyp) =>
        val e2 = this.run(e)
        val temp = Param("interpret_as")(ia.typ)
        this.intermediates += temp -> ExprIntermediate(
          InterpretAs(e2, targetTyp)().tchk()
        )
        temp
      case tup @ Tuple(elems @ _*) =>
        val elems2 = elems.map(this.run)
        val temp = Param("t")(tup.typ)
        this.intermediates += temp -> ExprIntermediate(
          Tuple(elems2: _*)().tchk()
        )
        temp
      case TupleAccess(t, i) =>
        val t2 = this.getPrefixAndMutateIntermediates(t)
        TupleAccess(t2, i)().tchk()
      case vec @ VecLiteral(elems @ _*) =>
        val elems2 = elems.map(this.run)
        val vec2 =
          VecLiteral(elems2: _*)(if (elems.isEmpty) vec.typ else Missing).tchk()
        val temp = Param("v")(vec.typ)
        this.intermediates += temp -> ExprIntermediate(vec2)
        temp
      case VecAccess(v, i) =>
        val v2 = this.getPrefixAndMutateIntermediates(v)
        val i2 = this.run(i)
        VecAccess(v2, i2)().tchk()
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

  private def getPrefixAndMutateIntermediates(e: Expr): Expr = {
    this.run(e) match {
      case VhdlPrefix(e) => e
      case e =>
        val prefix = Param("prefix")(e.typ)
        this.intermediates += prefix -> ExprIntermediate(e)
        prefix
    }
  }
}

private object VhdlPrefix {

  /** Matches "prefixes" as defined in IEEE Std 1076-2002.
    *
    * {{{
    * prefix ::= name
    *          | function_call
    *
    * name ::= simple_name
    *        | operator_symbol
    *        | selected_name
    *        | indexed_name
    *        | slice_name
    *        | attribute_name
    *
    * simple_name ::= identifier
    *
    * selected_name ::= prefix . suffix
    *
    * suffix ::= simple_name
    *          | character_literal
    *          | operator_symbol
    *          | "all"
    *
    * indexed_name ::= prefix ( expression { , expression } )
    *
    * function_call ::= function_name [ ( actual_parameter_part ) ]
    * }}}
    *
    * @note
    *   this method is conservative - if it returns `Some(e)` then `e` is
    *   definitely a prefix, but if this method returns `None` it does not
    *   necessarily mean the argument was <i>not</i> a prefix.
    */
  def unapply(e: Expr): Option[Expr] = {
    Some(e).filter(this.isPrefix)
  }

  @tailrec
  private def isPrefix(e: Expr): Boolean = {
    e match {
      case _: Param             => true // simple_name
      case TupleAccess(t, _)    => isPrefix(t) // selected_name
      case VecAccess(v, _)      => isPrefix(v) // indexed_name
      case FunCall(_: Param, _) => true // function_call
      case _                    => false
    }
  }
}
