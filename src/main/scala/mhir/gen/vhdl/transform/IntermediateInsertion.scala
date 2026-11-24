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
    val sdata = s.producers.map({ case (_, (p, _)) =>
      val sdata = StmData(p)().tchk().asInstanceOf[StmData]
      val intermediateName = Param(s"${p.name}_data")(sdata.typ)
      sdata -> intermediateName
    })
    val initialIntermediates = ListMap(
      sdata
        .map({ case (StmData(p), name) =>
          name -> StmDataIntermediate(p.asInstanceOf[Param])
        })
        .toSeq: _*
    )
    new IntermediateInsertion(initialIntermediates, sdata).apply(s)
  }

  def apply(
      e: Expr,
      sdata: Map[StmData, Param]
  ): (Expr, ListMap[Param, Intermediate]) = {
    val initialIntermediates = ListMap(
      sdata
        .map({ case (StmData(p), name) =>
          name -> StmDataIntermediate(p.asInstanceOf[Param])
        })
        .toSeq: _*
    )
    val pass = new IntermediateInsertion(initialIntermediates, sdata)
    val newExpr = pass.getExprAndMutateThis(e)
    (newExpr, pass.intermediates)
  }
}

private class IntermediateInsertion(
    var intermediates: ListMap[Param, Intermediate],
    var sdata: Map[StmData, Param]
) {

  def apply(s: GenStmBuild): GenStmBuild = {
    val data = this.getExprAndMutateThis(s.data)
    val valid = this.getExprAndMutateThis(s.valid)
    val producers = s.producers.map({ case (x, (p, ready)) =>
      x -> (p, this.getExprAndMutateThis(ready))
    })
    s.intermediates.foreach({ case (x, i) => runAndMutateThis(x, i) })
    GenStmBuild(
      data = data,
      valid = valid,
      producers = producers,
      intermediates = this.intermediates
    )
  }

  private def runAndMutateThis(x: Param, i: Intermediate): Unit = {
    val newI = this.getIntermediateAndMutateThis(i)
    this.intermediates += (x -> newI)
  }

  private def getIntermediateAndMutateThis(i: Intermediate): Intermediate = {
    i match {
      case i: SequentialIntermediate => this.getSeqIntermediateAndMutateThis(i)
      case ForGenerateIntermediate(loopVars, loopBounds, body) =>
        val subPass = {
          // Some intermediates may depend on the loop variables, so I can't necessarily pull
          // all the intermediates out of the loop.
          // I need to create a separate IntermediateInsertion instance to keep the two sets of
          // intermediates separate.
          new IntermediateInsertion(ListMap(), this.sdata)
        }
        val body2 = subPass.getIntermediateAndMutateThis(body)
        val (locals, outsiders) = subPass.intermediates
          .partition({ case (_, _: Intermediate) =>
            // If an Intermediate doesn't depend on the loop variables, move it out of the loop.
            // This makes it clear that there should only be one instance of that signal, not
            // one per loop instantiation.
            true
          // TODO: This implementation is broken. How can I salvage it? I'd need to check the transitive dependencies.
          // i.freeVars.intersect(loopVars.toSet).nonEmpty
          })
        this.intermediates ++= outsiders
        val body3 = BlockIntermediate(locals, body2)
        ForGenerateIntermediate(loopVars, loopBounds, body3)
      case BlockIntermediate(locals, body) =>
        val subPass = new IntermediateInsertion(ListMap(), this.sdata)
        locals.foreach({ case (x, i) => subPass.runAndMutateThis(x, i) })
        val newBody = subPass.getIntermediateAndMutateThis(body)
        BlockIntermediate(subPass.intermediates, newBody)
      case ip: IpBlockInst =>
        // This special case is needed to ensure the inputs to the IP blocks are valid "port actuals"
        ip.map(this.getPortActualAndMutateThis)
      case ExprAccumulator(init, next) =>
        // Special case to avoid unnecessary intermediate variables
        val newInit = init.map(this.getSeqIntermediateAndMutateThis)
        val newNext = this.getSeqIntermediateAndMutateThis(next)
        ExprAccumulator(newInit, newNext)
      case acc: Accumulator => acc.map(this.getExprAndMutateThis)
    }
  }

  private def getSeqIntermediateAndMutateThis(
      i: SequentialIntermediate
  ): SequentialIntermediate = {
    i match {
      case _: FunctionIntermediate =>
        throw new AssertionError(
          "there should not be any FunctionIntermediates yet at this compilation stage"
        )
      case i: SimpleIntermediate =>
        i.map(this.getExprAndMutateThis) match {
          // If the result ended up being just some variable x, where x is the name of an
          // intermediate, then remove x and return that intermediate directly.
          case result @ ExprIntermediate(x: Param) =>
            this.intermediates.lastOption match {
              // Be careful to not remove the intermediate if it's used elsewhere.
              // Intermediates are added to the list in order, so if this is the
              // last one it's definitely not used anywhere else.
              case Some((y: Param, i: SequentialIntermediate)) if y == x =>
                this.intermediates = this.intermediates.init
                i
              case _ => result
            }
          case result => result
        }
    }
  }

  /** Insert intermediates at the appropriate places in the given expression and
    * record them in the [[intermediates]] field.
    *
    * @return
    *   an expression that can be straightforwardly translated to a VHDL
    *   expression.
    * @note
    *   in some cases this will introduce unnecessary intermediates (i.e., the
    *   returned expression is a variable called X, which is used to define an
    *   intermediate but not used anywhere else). However, that can be handled
    *   by the caller (see
    *   [[IntermediateInsertion.getSeqIntermediateAndMutateThis]]).
    */
  private def getExprAndMutateThis(e: Expr): Expr = {
    e match {
      // --- Variable binders --------------------------------------------------
      // Be careful with variable binders! The variable(s) they introduce are
      // not available in the outer scope, so I'll need to use a different
      // approach within them (e.g., making the body a series of let bindings?).
      case f @ Function(x, body) =>
        val subPass = new IntermediateInsertion(ListMap(), this.sdata)
        val newBody = subPass.getExprAndMutateThis(body)
        val intermediates = subPass.intermediates
        // Some intermediates can be kept within the function (e.g., simple
        // data) and some must be moved out (e.g., another function declaration)
        var innerIntermediates = ListMap[Param, SequentialIntermediate]()
        var outerIntermediates = Map[Param, Intermediate]()
        for ((x, i) <- intermediates) {
          i match {
            // Move outside
            case i: StmDataIntermediate => outerIntermediates += x -> i
            // Keep inside
            case i: SimpleIntermediate   => innerIntermediates += x -> i
            case i: FunctionIntermediate => innerIntermediates += x -> i
            // Not allowed
            case _: IpBlockInst =>
              throw new AssertionError(
                "IP blocks cannot appear inside functions"
              )
            case _: BlockIntermediate =>
              throw new AssertionError("blocks cannot appear inside functions")
            case _: ForGenerateIntermediate =>
              throw new AssertionError(
                "'for generate' loops cannot appear inside functions"
              )
            case _: Accumulator =>
              throw new AssertionError(
                "accumulators cannot appear inside functions"
              )
          }
        }
        this.intermediates ++= outerIntermediates
        val fName = Param("f")(f.typ)
        val newFunc = FunctionIntermediate(Seq(x), innerIntermediates, newBody)
        this.intermediates += (fName -> newFunc)
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
        val fName = this.getExprAndMutateThis(f)
        val TyArrow(idxTyp, _) = f.typ
        this.getExprAndMutateThis(
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
        // TODO: Specially handle "if-elseif-else," for readability?
        //       Maybe I can just let the branches in MuxIntermediate be
        //       Intermediates themselves.
        val c2 = this.getExprAndMutateThis(c)
        val t2 = this.getExprAndMutateThis(t)
        val f2 = this.getExprAndMutateThis(f)
        val temp = Param("ite")(t.typ)
        this.intermediates += temp -> MuxIntermediate(c2, t2, f2)
        temp
      case bits @ Bits(e) =>
        val e2 = this.getExprAndMutateThis(e)
        val temp = Param("bits")(bits.typ)
        this.intermediates += temp -> ExprIntermediate(Bits(e2)().tchk())
        temp
      case ia @ InterpretAs(e, targetTyp) =>
        val e2 = this.getExprAndMutateThis(e)
        val temp = Param("interpret_as")(ia.typ)
        this.intermediates += temp -> ExprIntermediate(
          InterpretAs(e2, targetTyp)().tchk()
        )
        temp
      case tup @ Tuple(elems @ _*) =>
        val elems2 = elems.map(this.getExprAndMutateThis)
        val temp = Param("t")(tup.typ)
        this.intermediates += temp -> ExprIntermediate(
          Tuple(elems2: _*)().tchk()
        )
        temp
      case TupleAccess(t, i) =>
        val t2 = this.getPrefixAndMutateThis(t)
        TupleAccess(t2, i)().tchk()
      case vec @ VecLiteral(elems @ _*) =>
        val elems2 = elems.map(this.getExprAndMutateThis)
        val vec2 =
          VecLiteral(elems2: _*)(if (elems.isEmpty) vec.typ else Missing).tchk()
        val temp = Param("v")(vec.typ)
        this.intermediates += temp -> ExprIntermediate(vec2)
        temp
      case VecAccess(v, i) =>
        val v2 = this.getPrefixAndMutateThis(v)
        val i2 = this.getExprAndMutateThis(i)
        VecAccess(v2, i2)().tchk()
      case sd: StmData =>
        this.sdata(sd)

      // --- Illegal cases -----------------------------------------------------
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
      case e => e.map(this.getExprAndMutateThis).tchk()
    }
  }

  /** Insert intermediates where necessary to turn the given Sirop expression
    * into a valid "actual_part" in a port map (see VHDL-2002 standard, section
    * 1.1.1.2).
    */
  private def getPortActualAndMutateThis(e: Expr): Expr = {
    this.getExprAndMutateThis(e) match {
      case VhdlPortActual(newE) => newE
      case newE =>
        val tmp = Param("port_map_arg")(newE.typ)
        this.intermediates += (tmp -> ExprIntermediate(newE))
        tmp
    }
  }

  /** Insert intermediates where needed to turn the given Sirop expression into
    * a valid "prefix" (see IEEE Std 1076-2002 section 6.1).
    */
  private def getPrefixAndMutateThis(e: Expr): Expr = {
    this.getExprAndMutateThis(e) match {
      case VhdlPrefix(e) => e
      case e =>
        val prefix = Param("prefix")(e.typ)
        this.intermediates += prefix -> ExprIntermediate(e)
        prefix
    }
  }
}

private object VhdlPortActual {

  /** Decides whether `e` is a valid `port_actual`.
    *
    * See IEEE Std 1076-2002 section 1.1.1.2.
    */
  def unapply(e: Expr): Option[Expr] = {
    Some(e).filter(this.isValidPortActual)
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

private object VhdlPrefix {

  /** Matches "prefixes" as defined in IEEE Std 1076-2002, section 6.1.
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
