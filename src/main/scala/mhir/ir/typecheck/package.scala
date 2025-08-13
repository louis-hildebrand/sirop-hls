package mhir.ir

/** The typechecker.
  *
  * To type check an expression, use the [[mhir.ir.typecheck.TypeCheck.tchk]]
  * extension method. The implicit class [[mhir.ir.typecheck.TypeCheck]] must be
  * in scope.
  *
  * @example
  *
  * {{{
  *   import mhir.ir.typecheck.TypeCheck
  *   IntCst(42)().tchk()
  * }}}
  */
package object typecheck {
  implicit class TypeCheck(expr: Expr) {

    /** Type checks this expression and annotates it with its type.
      *
      * @return
      *   a new expression that is equal to this one but has a type annotation
      *   that is not [[Missing]].
      * @throws TypeError
      *   if the expression is ill-typed.
      */
    def tchk(implicit context: Map[Param, Type] = Map()): Expr = {
      if (this.expr.typ != Missing) {
        return this.expr
      }
      this.expr match {
        case x: Param =>
          context.get(x) match {
            case Some(t) => x.rebuild(t)
            case None    => throw new TypeError(s"Free variable: $x.")
          }
        case Function(x, body) =>
          val t = x.typ match {
            case Missing =>
              throw new TypeError(s"Missing function input type annotation.")
            case t => t
          }
          val newBody = body.tchk(context + (x -> t))
          Function(x, newBody)(TyArrow(t, newBody.typ))
        case fc @ FunCall(f, arg) =>
          val newArg = arg.tchk
          val newF = f.annotateFunc(newArg.typ).tchk
          newF.typ match {
            case TyArrow(t1, t2) =>
              if (newArg.typ ~= t1) {
                fc.rebuild(t2, Seq(newF, newArg))
              } else {
                throw new TypeError(
                  s"Left-hand side of function call expects input of type $t1, but ${newArg.typ} was provided."
                )
              }
            case t =>
              throw new TypeError(
                s"Left-hand side of function call has type $t. It should be a function."
              )
          }

        case cst @ IntCst(n) =>
          assert(cst.typ == Missing)
          cst.rebuild(TyAnyInt.tightest(n, n))
        case s @ Sum(terms @ _*) =>
          val newTerms = terms.map(e => e.tchk)
          for ((t, i) <- newTerms.zipWithIndex) {
            if (!t.typ.isInstanceOf[TyAnyInt]) {
              throw new TypeError(
                s"Term $i of sum has type ${t.typ}."
                  + " Expected an integer."
              )
            }
          }
          val elemTypes = newTerms.map(e => e.typ.asInstanceOf[TyAnyInt])
          if (elemTypes.toSet.size > 1) {
            throw new TypeError(
              s"Operands of ${s.className} have different types: ${elemTypes.mkString(", ")}."
            )
          } else {
            s.rebuild(elemTypes.head, newTerms)
          }
        case p @ Prod(factors @ _*) =>
          val newFactors = factors.map(e => e.tchk)
          for ((t, i) <- newFactors.zipWithIndex) {
            if (!t.typ.isInstanceOf[TyAnyInt]) {
              throw new TypeError(
                s"Term $i of product has type ${t.typ}."
                  + " Expected an integer."
              )
            }
          }
          val elemTypes = newFactors.map(e => e.typ.asInstanceOf[TyAnyInt])
          if (elemTypes.toSet.size > 1) {
            throw new TypeError(
              s"Operands of ${p.className} have different types: ${elemTypes.mkString(", ")}."
            )
          } else {
            p.rebuild(elemTypes.head, newFactors)
          }
        case d @ Div(e1, e2) =>
          val newLhs = e1.tchk
          val t1 = newLhs.typ match {
            case t: TyAnyInt => t
            case t =>
              throw new TypeError(
                s"Left-hand side of ${d.className} has type $t."
                  + " Expected an integer"
              )
          }
          val newRhs = e2.tchk
          val t2 = newRhs.typ match {
            case t: TyAnyInt => t
            case t =>
              throw new TypeError(
                s"Right-hand side of ${d.className} has type $t."
                  + " Expected an integer."
              )
          }
          if (t1 ~= t2) {
            d.rebuild(t1, Seq(newLhs, newRhs))
          } else {
            throw new TypeError(
              s"Left-hand side of ${d.className} has type $t1, but right-hand side has type $t2."
            )
          }
        case m @ Mod(e1, e2) =>
          val newLhs = e1.tchk
          val t1 = newLhs.typ match {
            case t: TyAnyInt => t
            case t =>
              throw new TypeError(
                s"Left-hand side of ${m.className} has type $t."
                  + " Expected an integer"
              )
          }
          val newRhs = e2.tchk
          val t2 = newRhs.typ match {
            case t: TyAnyInt => t
            case t =>
              throw new TypeError(
                s"Right-hand side of ${m.className} has type $t."
                  + " Expected an integer."
              )
          }
          if (t1 ~= t2) {
            m.rebuild(t1, Seq(newLhs, newRhs))
          } else {
            throw new TypeError(
              s"Left-hand side of ${m.className} has type $t1, but right-hand side has type $t2."
            )
          }
        case pad @ PadTo(e, targetWidth) =>
          val newE = e.tchk
          newE.typ match {
            case t @ TyAnyInt(srcWidth) if targetWidth >= srcWidth =>
              pad.rebuild(t.withWidth(targetWidth), Seq(newE))
            case t: TyAnyInt =>
              throw new TypeError(
                s"Argument of ${PadTo.getClass.getSimpleName} has type $t but the target width is $targetWidth."
                  + " The target width cannot be smaller than the original width."
              )
            case t =>
              throw new TypeError(
                s"Argument of ${PadTo.getClass.getSimpleName} has type $t."
                  + " Expected an integer."
              )
          }
        case trunc @ TruncateTo(e, targetWidth) =>
          val newE = e.tchk
          newE.typ match {
            case t @ TyAnyInt(srcWidth) if targetWidth <= srcWidth =>
              trunc.rebuild(t.withWidth(targetWidth), Seq(newE))
            case t: TyAnyInt =>
              throw new TypeError(
                s"Argument of ${TruncateTo.getClass.getSimpleName} has type $t but the target width is $targetWidth."
                  + " The target width cannot be greater than the original width."
              )
            case t =>
              throw new TypeError(
                s"Argument of ${TruncateTo.getClass.getSimpleName} has type $t."
                  + " Expected an integer."
              )
          }
        case sgn @ ToSigned(e) =>
          val newE = e.tchk.expectUInt()
          val w = newE.typ.asInstanceOf[TyUInt].w
          // Widen by one bit so that the value is guaranteed to fit
          sgn.rebuild(TySInt(w + 1), Seq(newE))
        case uns @ ToUnsigned(e) =>
          val newE = e.tchk
          newE.typ match {
            case TySInt(w) =>
              // We don't need the sign bit anymore
              assert(w >= 1)
              uns.rebuild(TyUInt(w - 1), Seq(newE))
            case t =>
              throw new TypeError(
                s"Argument of ${ToUnsigned.getClass.getSimpleName} has type $t."
                  + " Expected a signed integer."
              )
          }
        case ll @ LLShift(e1, e2) =>
          val newE1 = e1.tchk.expectAnyInt()
          val newE2 = e2.tchk.expectUInt()
          ll.rebuild(newE1.typ, Seq(newE1, newE2))
        case lr @ LRShift(e1, e2) =>
          val newE1 = e1.tchk.expectAnyInt()
          val newE2 = e2.tchk.expectUInt()
          lr.rebuild(newE1.typ, Seq(newE1, newE2))

        case True  => True
        case False => False
        case mux @ Mux(c, t, f) =>
          val newC = c.tchk
          newC.typ match {
            case TyBool => ()
            case t =>
              throw new TypeError(s"Expected type $TyBool but found $t.")
          }
          val newT = t.tchk
          val newF = f.tchk
          if (newT.typ ~= newF.typ) {
            mux.rebuild(newT.typ, Seq(newC, newT, newF))
          } else {
            throw new TypeError(
              s"True branch of MUX has type ${newT.typ} but false branch has type ${newF.typ}."
            )
          }
        case a @ And(terms @ _*) =>
          val newTerms = terms.map(e => e.tchk)
          for ((t, i) <- newTerms.zipWithIndex) {
            if (t.typ != TyBool) {
              throw new TypeError(s"Term $i of AND has type ${t.typ}.")
            }
          }
          a.rebuild(TyBool, newTerms)
        case or @ Or(terms @ _*) =>
          val newTerms = terms.map(e => e.tchk)
          for ((t, i) <- newTerms.zipWithIndex) {
            if (t.typ != TyBool) {
              throw new TypeError(s"Term $i of OR has type ${t.typ}.")
            }
          }
          or.rebuild(TyBool, newTerms)
        case n @ Not(e) =>
          val newE = e.tchk
          newE.typ match {
            case TyBool => ()
            case t =>
              throw new TypeError(s"Expected type $TyBool but found $t.")
          }
          n.rebuild(TyBool, Seq(newE))
        case eq @ Equal(e1, e2) =>
          val newE1 = e1.tchk
          newE1.typ match {
            case t if t.isData => ()
            case t =>
              throw new TypeError(
                s"Left-hand side of ${eq.className} has non-data type $t."
              )
          }
          val newE2 = e2.tchk
          newE2.typ match {
            case t if t.isData => ()
            case t =>
              throw new TypeError(
                s"Right-hand side of ${eq.className} has non-data type $t."
              )
          }
          if (newE1.typ ~= newE2.typ) {
            eq.rebuild(TyBool, Seq(newE1, newE2))
          } else {
            throw new TypeError(
              s"Left-hand side of ${eq.className} has type ${newE1.typ} but right-hand side has type ${newE2.typ}."
            )
          }
        case lt @ LessThan(e1, e2) =>
          val newLhs = e1.tchk
          newLhs.typ match {
            case _: TyAnyInt => ()
            case t =>
              throw new TypeError(
                s"Left-hand side of ${lt.className} has type $t."
                  + " Expected an integer."
              )
          }
          val newRhs = e2.tchk
          newRhs.typ match {
            case _: TyAnyInt => ()
            case t =>
              throw new TypeError(
                s"Right-hand side of ${lt.className} has type $t."
                  + " Expected an integer."
              )
          }
          if (newLhs.typ ~= newRhs.typ) {
            lt.rebuild(TyBool, Seq(newLhs, newRhs))
          } else {
            throw new TypeError(
              s"Left-hand side of ${lt.className} has type ${newLhs.typ} but right-hand side has type ${newRhs.typ}."
            )
          }

        case t @ Tuple(elems @ _*) =>
          val newElems = elems.map(e => e.tchk)
          t.rebuild(TyTuple(newElems.map(e => e.typ): _*), newElems)
        case ta @ TupleAccess(t, idx: IntCst) =>
          val newIdx = idx.tchk
          assert(newIdx.typ.isInstanceOf[TyAnyInt])
          val newT = t.tchk
          newT.typ match {
            case TyTuple(ts @ _*) =>
              ta.rebuild(ts(idx.i.toInt), Seq(newT, newIdx))
            case t =>
              throw new TypeError(
                s"Left-hand side of tuple access has type $t."
              )
          }

        case vb @ VecBuild(n, f) =>
          val newN = n.tchk.expectUInt()
          val newF = f.tchk
          val vecT = newF.typ match {
            case TyArrow(_: TyUInt, t) => t
            case t => throw new TypeError(s"Function of VecBuild has type $t.")
          }
          vb.rebuild(TyVec(vecT, newN), Seq(newN, newF))
        case va @ VecAccess(v, i) =>
          val newV = v.tchk
          val vecT = newV.typ match {
            case TyVec(t, _) => t
            case t =>
              throw new TypeError(s"Left-hand side of VecAccess has type $t.")
          }
          val newI = i.tchk.expectUInt()
          va.rebuild(vecT, Seq(newV, newI))
        case vl @ VecLiteral(elems @ _*) =>
          elems match {
            case Seq() =>
              throw new IllegalArgumentException(
                "Cannot type check empty vector literal."
              )
            case _ =>
              val newElems = elems.map(e => e.tchk())
              for ((e, i) <- newElems.zipWithIndex.tail) {
                if (e.typ != newElems.head.typ) {
                  throw new TypeError(
                    s"Element 0 of vector has type ${newElems.head.typ} but element $i has type ${e.typ}."
                  )
                }
              }
              vl.rebuild(TyVec(newElems.head.typ, newElems.length), newElems)
          }

        case s: StmBuild =>
          val newContext = s.accVars.foldLeft(context)({ case (ctx, x) =>
            x.typ match {
              case Missing =>
                throw new TypeError(
                  s"Missing type annotation for accumulator $x."
                )
              case _: TyStm =>
                ctx + (x -> x.typ)
              case t if t.isData =>
                ctx + (x -> x.typ)
              case t =>
                throw new TypeError(
                  s"Invalid type $t for accumulator $x."
                    + s" Accumulators can only be streams or data."
                )
            }
          })
          val newN = s.n.tchk(context).expectUInt()
          val newEquations = s.equations.map({ case (x, (z, next)) =>
            val newZ = z.tchk(context)
            if (!(newZ.typ ~= x.typ)) {
              throw new TypeError(
                s"Seed for accumulator $x has type ${newZ.typ}."
                  + s" Expected type ${x.typ}."
              )
            }
            val newNext = next.tchk(newContext)
            val expectedNextTyp = x.typ match {
              case _: TyStm => TyBool
              case _        => x.typ
            }
            if (!(newNext.typ ~= expectedNextTyp)) {
              throw new TypeError(
                s"Next value for accumulator $x has type ${newNext.typ}."
                  + s" Expected type $expectedNextTyp."
              )
            }
            x -> (newZ, newNext)
          })
          val newData = s.data.tchk(newContext)
          val newValid = s.valid.tchk(newContext).expectType(TyBool)
          StmBuild(newN, newData, newValid, newEquations)(
            TyStm(newData.typ, newN)
          )
        case sn @ StmData(s) =>
          val newS = s.tchk
          newS.typ match {
            case TyStm(t, _) => sn.rebuild(t, Seq(newS))
            case t =>
              throw new TypeError(
                s"Argument of ${StmData.getClass.getSimpleName} has type $t."
              )
          }
        case let @ LetStm(x, in, out) =>
          val newIn = in.tchk
          val newX = x.typ match {
            case Missing =>
              x.rebuild(newIn.typ).asInstanceOf[Param]
            case t =>
              if (t ~= newIn.typ) {
                x
              } else {
                throw new TypeError(
                  s"Cannot bind variable of type $t to stream of type ${newIn.typ}."
                )
              }
          }
          val newOut = out.tchk(context + (newX -> newIn.typ))
          let.rebuild(newOut.typ, Seq(newX, newIn, newOut))
        case sn @ StmNextK(s, k) =>
          val newK = k.tchk
          newK.typ match {
            case _: TyAnyInt => ()
            case t =>
              throw new TypeError(
                s"Index of ${StmNextK.getClass.getSimpleName} has type $t."
                  + " Expected an integer."
              )
          }
          val newS = s.tchk
          newS.typ match {
            case TyStm(t, n) =>
              sn.rebuild(TyStm(t, n - k), Seq(newS, newK))
            case t =>
              throw new TypeError(
                s"Stream of ${StmNextK.getClass.getSimpleName} has type $t."
              )
          }
        case sl @ StmLiteral(elems @ _*) =>
          val checkedElems = elems.map(e => e.tchk())
          val types = checkedElems.map(e => e.typ).toSet
          if (types.isEmpty) {
            throw new IllegalArgumentException(
              "Cannot type check empty stream literal."
            )
          } else if (types.size == 1) {
            val t = types.head
            sl.rebuild(TyStm(t, checkedElems.length), checkedElems)
          } else {
            throw new IllegalArgumentException(
              "Inconsistent element types in stream literal."
            )
          }

        case s: SyntaxSugar => s.typecheck(context)
      }
    }

    /** Insist that this expression has the type of an unsigned integer.
      *
      * @return
      *   this expression, unchanged.
      * @throws TypeError
      *   if this expression does not have the expected type.
      */
    def expectUInt(): Expr = {
      this.expr.typ match {
        case _: TyUInt => this.expr
        case t =>
          throw new TypeError(s"Expected an unsigned integer but found $t.")
      }
    }

    /** Insist that this expression has the type of an integer, either signed or
      * unsigned.
      *
      * @return
      *   this expression, unchanged.
      * @throws TypeError
      *   if this expression does not have the expected type.
      */
    def expectAnyInt(): Expr = {
      this.expr.typ match {
        case _: TyAnyInt => this.expr
        case t =>
          throw new TypeError(s"Expected an integer but found $t.")
      }
    }

    /** Insists that this expression's type is [[TyStm]].
      *
      * @return
      *   this expression, unchanged.
      * @throws TypeError
      *   if this expression does not have the expected type.
      */
    def expectStream(): Expr = {
      this.expr.typ match {
        case _: TyStm => this.expr
        case t =>
          throw new TypeError(s"Expected a stream but found $t.")
      }
    }

    /** Insist that this expression's type is compatible with the given type (as
      * determined by [[Type.~=]]).
      *
      * @param t
      *   the expected type.
      * @return
      *   this expression, unchanged.
      * @throws TypeError
      *   if this expression does not have the expected type.
      */
    def expectType(t: Type): Expr = {
      if (this.expr.typ != t) {
        throw new TypeError(s"Expected type $t but found ${this.expr.typ}.")
      }
      this.expr
    }
  }
}
