package mhir.typecheck

import mhir.ir._

import scala.annotation.tailrec

trait TypeChecker {

  implicit class TypeCheck(expr: Expr) {

    def tchk()(implicit c: Canonicalizer): Expr = {
      this.tchk(Map(), Map())(c)
    }

    /** Type checks this expression and annotates it with its type.
      *
      * @param context
      *   the typing context. This maps bound variables to their types.
      * @param constValues
      *   the value of each constant. This allows the type checker to recognize
      *   that, for instance, `Vec[u16, 8]` is the same as `Vec[u16, N]` in case
      *   `N = 8`.
      * @return
      *   a new expression that is equal to this one but has a type annotation
      *   that is not [[Missing]].
      * @throws TypeError
      *   if the expression is ill-typed.
      * @throws NameError
      *   if there are free variables.
      */
    def tchk(
        context: Map[Param, Type],
        constValues: Map[Param, Expr]
    )(implicit c: Canonicalizer): Expr = {
      if (this.expr.typ != Missing) {
        return this.expr
      }
      this.expr match {
        case u: Undefined =>
          assert(u.hasType)
          u
        case x: Param =>
          context.get(x) match {
            case Some(t) => x.rebuild(t)
            case None    => throw NameError(s"name '$x' is not defined")
          }
        case Function(x, body) =>
          val t = x.typ match {
            case Missing =>
              throw new TypeError(s"Missing function input type annotation.")
            case t => t
          }
          val newBody = body.tchk(context + (x -> t), constValues)
          Function(x, newBody)(TyArrow(t, newBody.typ))
        case fc @ FunCall(f, arg) =>
          val newArg = arg.tchk(context, constValues)
          val newF = f.annotateFunc(newArg.typ).tchk(context, constValues)
          newF.typ match {
            case TyArrow(t1, t2) =>
              if (!newArg.typ.equalsGivenConstants(t1, constValues)) {
                throw new TypeError(
                  s"function expects type $t1, but argument has type ${newArg.typ}",
                  TypeChecker.relevantBindings(constValues, t1, newArg.typ)
                )
              }
              fc.rebuild(t2, Seq(newF, newArg))
            case t =>
              throw new TypeError(
                s"Left-hand side of function call has type $t. It should be a function."
              )
          }

        case cst @ IntCst(n) =>
          assert(cst.typ == Missing)
          cst.rebuild(TyAnyInt.tightest(n, n))
        case s @ Sum(terms @ _*) =>
          val newTerms = terms.map(e => e.tchk(context, constValues))
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
          val newFactors = factors.map(e => e.tchk(context, constValues))
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
          val newLhs = e1.tchk(context, constValues)
          val t1 = newLhs.typ match {
            case t: TyAnyInt => t
            case t =>
              throw new TypeError(
                s"Left-hand side of ${d.className} has type $t."
                  + " Expected an integer"
              )
          }
          val newRhs = e2.tchk(context, constValues)
          val t2 = newRhs.typ match {
            case t: TyAnyInt => t
            case t =>
              throw new TypeError(
                s"Right-hand side of ${d.className} has type $t."
                  + " Expected an integer."
              )
          }
          if (!t1.equalsGivenConstants(t2, constValues)) {
            throw new TypeError(
              s"left-hand side of ${d.className} has type $t1, but right-hand side has type $t2.",
              TypeChecker.relevantBindings(constValues, t1, t2)
            )
          }
          d.rebuild(t1, Seq(newLhs, newRhs))
        case m @ Mod(e1, e2) =>
          val newLhs = e1.tchk(context, constValues)
          val t1 = newLhs.typ match {
            case t: TyAnyInt => t
            case t =>
              throw new TypeError(
                s"Left-hand side of ${m.className} has type $t."
                  + " Expected an integer"
              )
          }
          val newRhs = e2.tchk(context, constValues)
          val t2 = newRhs.typ match {
            case t: TyAnyInt => t
            case t =>
              throw new TypeError(
                s"Right-hand side of ${m.className} has type $t."
                  + " Expected an integer."
              )
          }
          if (!t1.equalsGivenConstants(t2, constValues)) {
            throw new TypeError(
              s"left-hand side of ${m.className} has type $t1, but right-hand side has type $t2",
              TypeChecker.relevantBindings(constValues, t1, t2)
            )
          }
          m.rebuild(t1, Seq(newLhs, newRhs))
        case s @ WrappingSum(terms @ _*) =>
          val newTerms = terms.map(e => e.tchk(context, constValues))
          for ((t, i) <- newTerms.zipWithIndex) {
            if (!t.typ.isInstanceOf[TyAnyInt]) {
              throw new TypeError(
                s"Term $i of ${s.className} has type ${t.typ}."
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
        case d @ WrappingDiff(e1, e2) =>
          val newLhs = e1.tchk(context, constValues)
          val t1 = newLhs.typ match {
            case t: TyAnyInt => t
            case t =>
              throw new TypeError(
                s"Left-hand side of ${d.className} has type $t."
                  + " Expected an integer"
              )
          }
          val newRhs = e2.tchk(context, constValues)
          val t2 = newRhs.typ match {
            case t: TyAnyInt => t
            case t =>
              throw new TypeError(
                s"Right-hand side of ${d.className} has type $t."
                  + " Expected an integer."
              )
          }
          if (!t1.equalsGivenConstants(t2, constValues)) {
            throw new TypeError(
              s"left-hand side of ${d.className} has type $t1, but right-hand side has type $t2.",
              TypeChecker.relevantBindings(constValues, t1, t2)
            )
          }
          d.rebuild(t1, Seq(newLhs, newRhs))
        case p @ WrappingProd(factors @ _*) =>
          val newFactors = factors.map(e => e.tchk(context, constValues))
          for ((t, i) <- newFactors.zipWithIndex) {
            if (!t.typ.isInstanceOf[TyAnyInt]) {
              throw new TypeError(
                s"Term $i of ${p.className} has type ${t.typ}."
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
        case pad @ PadTo(e, targetWidth) =>
          val newE = e.tchk(context, constValues)
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
          val newE = e.tchk(context, constValues)
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
          val newE = e.tchk(context, constValues).expectUInt()
          val w = newE.typ.asInstanceOf[TyUInt].w
          // Widen by one bit so that the value is guaranteed to fit
          sgn.rebuild(TySInt(w + 1), Seq(newE))
        case uns @ ToUnsigned(e) =>
          val newE = e.tchk(context, constValues)
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
        case b @ Bits(e) =>
          val newE = e.tchk(context, constValues).expectData()
          b.rebuild(TyVec(TyBool, newE.typ.bitwidth), Seq(newE))
        case ia @ InterpretAs(e, targetTyp) =>
          val newE = e.tchk(context, constValues)
          val inWidth = newE.typ match {
            case TyVec(TyBool, n) => n
            case t =>
              throw new TypeError(
                s"Argument of ${ia.className} has type $t." +
                  s" Expected a vector of booleans."
              )
          }
          if (!targetTyp.isData) {
            throw new TypeError(
              s"Cannot interpret as non-data type $targetTyp."
            )
          }
          val outWidth = targetTyp.bitwidth
          if (!c.sameLen(inWidth, outWidth, constValues)) {
            throw new TypeError(
              s"Bitwidths in ${ia.className} do not match: "
                + s"input is $inWidth bits wide but the target type is $outWidth bits wide."
            )
          }
          ia.rebuild(targetTyp, Seq(newE))
        case ll @ LShift(e1, e2) =>
          val newE1 = e1.tchk(context, constValues).expectAnyInt()
          val newE2 = e2.tchk(context, constValues).expectUInt()
          ll.rebuild(newE1.typ, Seq(newE1, newE2))
        case ar @ ARShift(e1, e2) =>
          val newE1 = e1.tchk(context, constValues).expectAnyInt()
          val newE2 = e2.tchk(context, constValues).expectUInt()
          ar.rebuild(newE1.typ, Seq(newE1, newE2))
        case lr @ LRShift(e1, e2) =>
          val newE1 = e1.tchk(context, constValues).expectAnyInt()
          val newE2 = e2.tchk(context, constValues).expectUInt()
          lr.rebuild(newE1.typ, Seq(newE1, newE2))

        case c: FixCst => c
        case p @ IntFixProd(e1, e2) =>
          val newE1 = e1.tchk(context, constValues).expectUInt()
          val newE2 = e2.tchk(context, constValues).expectFixPoint()
          p.rebuild(newE1.typ, Seq(newE1, newE2))

        case True  => True
        case False => False
        case mux @ Mux(cond, t, f) =>
          val newC = cond.tchk(context, constValues)
          newC.typ match {
            case TyBool => ()
            case t =>
              throw new TypeError(s"Expected type $TyBool but found $t.")
          }
          val newT = t.tchk(context, constValues)
          val newF = f.tchk(context, constValues)
          if (!newT.typ.equalsGivenConstants(newF.typ, constValues)) {
            throw new TypeError(
              s"true branch of if-then-else has type ${newT.typ} but false branch has type ${newF.typ}",
              TypeChecker.relevantBindings(constValues, newT.typ, newF.typ)
            )
          }
          mux.rebuild(newT.typ, Seq(newC, newT, newF))
        case a @ And(terms @ _*) =>
          val newTerms = terms.map(e => e.tchk(context, constValues))
          for ((t, i) <- newTerms.zipWithIndex) {
            if (t.typ != TyBool) {
              throw new TypeError(s"Term $i of AND has type ${t.typ}.")
            }
          }
          a.rebuild(TyBool, newTerms)
        case or @ Or(terms @ _*) =>
          val newTerms = terms.map(e => e.tchk(context, constValues))
          for ((t, i) <- newTerms.zipWithIndex) {
            if (t.typ != TyBool) {
              throw new TypeError(s"Term $i of OR has type ${t.typ}.")
            }
          }
          or.rebuild(TyBool, newTerms)
        case n @ Not(e) =>
          val newE = e.tchk(context, constValues)
          newE.typ match {
            case TyBool => ()
            case t =>
              throw new TypeError(s"Expected type $TyBool but found $t.")
          }
          n.rebuild(TyBool, Seq(newE))
        case eq @ Equal(e1, e2) =>
          val newE1 = e1.tchk(context, constValues)
          newE1.typ match {
            case t if t.isData => ()
            case t =>
              throw new TypeError(
                s"Left-hand side of ${eq.className} has non-data type $t."
              )
          }
          val newE2 = e2.tchk(context, constValues)
          newE2.typ match {
            case t if t.isData => ()
            case t =>
              throw new TypeError(
                s"Right-hand side of ${eq.className} has non-data type $t."
              )
          }
          if (!newE1.typ.equalsGivenConstants(newE2.typ, constValues)) {
            throw new TypeError(
              s"left-hand side of ${eq.className} has type ${newE1.typ} but right-hand side has type ${newE2.typ}",
              TypeChecker.relevantBindings(constValues, newE1.typ, newE2.typ)
            )
          }
          eq.rebuild(TyBool, Seq(newE1, newE2))
        case lt @ LessThan(e1, e2) =>
          val newLhs = e1.tchk(context, constValues)
          newLhs.typ match {
            case _: TyAnyInt => ()
            case t =>
              throw new TypeError(
                s"Left-hand side of ${lt.className} has type $t."
                  + " Expected an integer."
              )
          }
          val newRhs = e2.tchk(context, constValues)
          newRhs.typ match {
            case _: TyAnyInt => ()
            case t =>
              throw new TypeError(
                s"Right-hand side of ${lt.className} has type $t."
                  + " Expected an integer."
              )
          }
          if (!newLhs.typ.equalsGivenConstants(newRhs.typ, constValues)) {
            throw new TypeError(
              s"left-hand side of ${lt.className} has type ${newLhs.typ} but right-hand side has type ${newRhs.typ}",
              TypeChecker.relevantBindings(constValues, newLhs.typ, newRhs.typ)
            )
          }
          lt.rebuild(TyBool, Seq(newLhs, newRhs))

        case t @ Tuple(elems @ _*) =>
          val newElems = elems.map(e => e.tchk(context, constValues))
          t.rebuild(TyTuple(newElems.map(e => e.typ): _*), newElems)
        case ta @ TupleAccess(t, idx: IntCst) =>
          val newIdx = idx.tchk(context, constValues)
          assert(newIdx.typ.isInstanceOf[TyAnyInt])
          val newT = t.tchk(context, constValues)
          newT.typ match {
            case TyTuple(ts @ _*) =>
              ta.rebuild(ts(idx.i.toInt), Seq(newT, newIdx))
            case t =>
              throw new TypeError(
                s"Left-hand side of tuple access has type $t."
              )
          }

        case vb @ VecBuild(n, f) =>
          val newN = n.tchk(context, constValues).expectUInt()
          val newF = f.tchk(context, constValues)
          val vecT = newF.typ match {
            case TyArrow(uint @ TyUInt(wI), t) =>
              newN match {
                case IntCst(n) =>
                  if (uint.maxInt < n - 1) {
                    throw new TypeError(
                      s"Index of $wI bits is not wide enough for ${vb.className} of length $n"
                    )
                  }
                case _ =>
                // TODO: Also check in this case?
                //       What's tricky is that, for example, the length can be
                //       256 (which is 9 bits) while the index is 8 bits wide.
              }
              t
            case t => throw new TypeError(s"Function of VecBuild has type $t.")
          }
          vb.rebuild(TyVec(vecT, newN), Seq(newN, newF))
        case va @ VecAccess(v, i) =>
          val newV = v.tchk(context, constValues)
          val vecT = newV.typ match {
            case TyVec(t, _) => t
            case t =>
              throw new TypeError(s"Left-hand side of VecAccess has type $t.")
          }
          val newI = i.tchk(context, constValues).expectUInt()
          va.rebuild(vecT, Seq(newV, newI))
        case vl @ VecLiteral(elems @ _*) =>
          elems match {
            case Seq() =>
              throw new IllegalArgumentException(
                "Cannot type check empty vector literal."
              )
            case _ =>
              val newElems = elems.map(e => e.tchk(context, constValues))
              for ((e, i) <- newElems.zipWithIndex.tail) {
                if (e.typ != newElems.head.typ) {
                  throw new TypeError(
                    s"Element 0 of vector has type ${newElems.head.typ} but element $i has type ${e.typ}."
                  )
                }
              }
              val len = newElems.length
              val n = C(len)(TyAnyInt.tightest(0, len))
              vl.rebuild(
                TyVec(newElems.head.typ, n)(NoOpCanonicalizer),
                newElems
              )
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
          val newN = s.n.tchk(context, constValues).expectUInt()
          val newEquations = s.equations.map({
            case (x, (s, ready)) if x.typ.isInstanceOf[TyStm] =>
              val newS = s.tchk(context, constValues)
              val TyStm(elemTyp, _) = x.typ
              newS.typ match {
                case TyStm(t, _) =>
                  if (!t.equalsGivenConstants(elemTyp, constValues)) {
                    throw new TypeError(
                      s"stream for producer $x has type $t." +
                        s" Expected a stream of type $elemTyp",
                      TypeChecker.relevantBindings(constValues, t, elemTyp)
                    )
                  }
                case t =>
                  throw new TypeError(
                    s"stream for producer $x has type $t. Expected a stream"
                  )
              }
              val newReady = ready.tchk(newContext, constValues)
              if (newReady.typ != TyBool) {
                throw new TypeError(
                  s"ready expression for producer $x has type ${newReady.typ}."
                    + s" Expected type $TyBool",
                  TypeChecker.relevantBindings(constValues, newReady.typ, x.typ)
                )
              }
              x -> (newS, newReady)
            case (x, (z, next)) if x.typ.isData =>
              val newZ = (z, x.typ) match {
                // TODO: Generalize this by using ReshapeData?
                //       But then there will be a circular dependency between
                //       the type checker and the lowering package :(
                case (IntCst(z), typ: TyAnyInt) if typ.contains(z) =>
                  IntCst(z)(x.typ)
                case _ =>
                  val newZ = z.tchk(context, constValues)
                  if (!newZ.typ.equalsGivenConstants(x.typ, constValues)) {
                    throw new TypeError(
                      s"seed for accumulator $x has type ${newZ.typ}."
                        + s" Expected type ${x.typ}.",
                      TypeChecker.relevantBindings(constValues, newZ.typ, x.typ)
                    )
                  }
                  newZ
              }
              val newNext = next.tchk(newContext, constValues)
              if (!newNext.typ.equalsGivenConstants(x.typ, constValues)) {
                throw new TypeError(
                  s"next value for accumulator $x has type ${newNext.typ}."
                    + s" Expected type ${x.typ}",
                  TypeChecker.relevantBindings(constValues, newNext.typ, x.typ)
                )
              }
              x -> (newZ, newNext)
            case (x, _) =>
              throw new TypeError(
                s"variable $x in sbuild has type ${x.typ}." +
                  s" Expected a stream or data."
              )
          })
          val newData = s.data.tchk(newContext, constValues)
          val newValid = s.valid
            .tchk(newContext, constValues)
            .expectType(TyBool, constValues)
          StmBuild(newN, newData, newValid, newEquations)(
            TyStm(newData.typ, newN),
            s.annotations
          )
        case sn @ StmData(s) =>
          val newS = s.tchk(context, constValues)
          newS.typ match {
            case TyStm(t, _) => sn.rebuild(t, Seq(newS))
            case t =>
              throw new TypeError(
                s"Argument of ${StmData.getClass.getSimpleName} has type $t."
              )
          }
        case let @ LetStm(bufSize, x, in, out) =>
          val newBufSize = bufSize.tchk(context, constValues).expectUInt()
          val newIn = in.tchk(context, constValues)
          val newX = x.typ match {
            case Missing =>
              x.rebuild(newIn.typ).asInstanceOf[Param]
            case TyStm(xElemTyp, _) =>
              newIn.typ match {
                case TyStm(inElemTyp, _) =>
                  if (!xElemTyp.equalsGivenConstants(inElemTyp, constValues)) {
                    throw new TypeError(
                      s"cannot bind variable of type ${x.typ} to stream of type ${newIn.typ}",
                      TypeChecker.relevantBindings(
                        constValues,
                        xElemTyp,
                        inElemTyp
                      )
                    )
                  }
                case _ =>
                  throw new TypeError(
                    s"cannot bind variable of type ${x.typ} to stream of type ${newIn.typ}",
                    TypeChecker.relevantBindings(
                      constValues,
                      xElemTyp,
                      newIn.typ
                    )
                  )
              }
              x
            case t =>
              throw new TypeError(
                s"variable in letstm has type $t." +
                  s" Expected a stream type."
              )
          }
          val newOut = out.tchk(context + (newX -> newIn.typ), constValues)
          let.rebuild(newOut.typ, Seq(newBufSize, newX, newIn, newOut))
        case sl @ StmLiteral(elems @ _*) =>
          val checkedElems = elems.map(e => e.tchk(context, constValues))
          val types = checkedElems.map(e => e.typ).toSet
          if (types.isEmpty) {
            throw new IllegalArgumentException(
              "Cannot type check empty stream literal."
            )
          } else if (types.size == 1) {
            val t = types.head
            val len = checkedElems.length
            val n = C(len)(TyAnyInt.tightest(0, len))
            sl.rebuild(TyStm(t, n)(NoOpCanonicalizer), checkedElems)
          } else {
            throw new IllegalArgumentException(
              "Inconsistent element types in stream literal."
            )
          }

        case s: SyntaxSugar => s.typecheck(context, constValues)
      }
    }

    def expectBool(): Expr = {
      this.expr.typ match {
        case TyBool => this.expr
        case t =>
          throw new TypeError(s"Expected a boolean but found $t.")
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

    /** Insist that this expression has the type of a fixed-point number.
      *
      * @return
      *   this expression, unchanged.
      * @throws TypeError
      *   if this expression does not have the expected type.
      */
    def expectFixPoint(): Expr = {
      this.expr.typ match {
        case _: TyFix => this.expr
        case t =>
          throw new TypeError(s"Expected a fixed-point number but found $t.")
      }
    }

    def expectData(): Expr = {
      if (!this.expr.typ.isData) {
        throw new TypeError(s"Expected a data type but found ${this.expr.typ}.")
      }
      this.expr
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

    def expectStreamOf(
        elemTyp: Type,
        constValues: Map[Param, Expr]
    )(implicit c: Canonicalizer): Expr = {
      this.expr.typ match {
        case TyStm(t, _) =>
          if (!t.equalsGivenConstants(elemTyp, constValues)) {
            throw new TypeError(
              s"expected a stream with elements of type $elemTyp," +
                s" but found a stream with elements of type $t",
              TypeChecker.relevantBindings(constValues, elemTyp, t)
            )
          }
        case t =>
          throw new TypeError(s"Expected a stream but found $t.")
      }
      this.expr
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
    def expectType(
        t: Type,
        constValues: Map[Param, Expr]
    )(implicit c: Canonicalizer): Expr = {
      if (!this.expr.typ.equalsGivenConstants(t, constValues)) {
        throw new TypeError(
          s"expected type $t but found ${this.expr.typ}",
          TypeChecker.relevantBindings(constValues, t, this.expr.typ)
        )
      }
      this.expr
    }
  }

  implicit class TypeCheckProgram(prog: Program) {

    /** Type checks the given program.
      *
      * This includes type checking all constants as well as the main
      * accelerator body.
      */
    def tchk()(implicit c: Canonicalizer): Program = {
      val prog1 = this.checkAndEvalConstants()
      TypeCheckProgram(prog1).checkOthers()
    }

    private def checkAndEvalConstants()(implicit c: Canonicalizer): Program = {
      // Type checking and evaluating constants need to be interleaved because
      // type checking an expression may depend on the value of previous
      // constants.
      // For example, the expression
      //    VecZip(v, [0:u8, 1:u8, 2:u8]v)
      // is only well-typed if v has length 3.
      // What if v has length N?
      // Then we need to know the value of N to determine whether the
      // expression is well-typed.
      val (constTypes, constVals, newConstants) = this.prog.constants
        .foldLeft(Map[Param, Type](), Map[Param, Expr](), Seq[ConstDecl]())({
          case ((types, values, result), decl) =>
            val newDecl = TypeChecker.checkAndEvalConst(decl, types, values)
            (
              types + (newDecl.x -> newDecl.x.typ),
              values + (newDecl.x -> newDecl.e),
              result :+ newDecl
            )
        })
      val (_, _, newTestSuite) = this.prog.test
        .foldLeft(constTypes, constVals, Seq[TestDecl]())({
          case ((types, values, result), decl: ConstDecl) =>
            val newDecl = TypeChecker.checkAndEvalConst(decl, types, values)
            (
              types + (newDecl.x -> newDecl.x.typ),
              values + (newDecl.x -> newDecl.e),
              result :+ newDecl
            )
          case ((types, values, result), a: Assertion) =>
            (types, values, result :+ a)
        })
      Program(newConstants, this.prog.accel, newTestSuite)
    }

    private def checkOthers()(implicit c: Canonicalizer): Program = {
      val mainConstTypes = this.prog.constants
        .map({ case ConstDecl(x, _) => x -> x.typ })
        .toMap
      val mainConstVals = this.prog.constants
        .map({ case ConstDecl(x, e) => x -> e })
        .toMap
      val newAccel =
        this.prog.accel
          .copy(body = this.prog.accel.body.tchk(mainConstTypes, mainConstVals))
      val (accelInputs, accelOutTyp) = {
        val (params, body) = TypeChecker.unwrapTopLevelFunction(newAccel.body)
        assert(body.typ != Missing)
        (params.toSet, body.typ)
      }
      val (_, _, newTestSuite) =
        this.prog.test
          .foldLeft(mainConstTypes, mainConstVals, Seq[TestDecl]())({
            case ((types, values, result), decl @ ConstDecl(x, e)) =>
              (types + (x -> x.typ), values + (x -> e), result :+ decl)
            case ((types, values, result), a: Assertion) =>
              (
                types,
                values,
                result :+ TypeChecker.check(
                  a,
                  types,
                  values,
                  accelInputs = accelInputs,
                  accelOutTyp = accelOutTyp
                )
              )
          })
      Program(this.prog.constants, newAccel, newTestSuite)
    }
  }
}

object TypeChecker {

  def relevantBindings(
      constValues: Map[Param, Expr],
      typ: Type*
  ): Map[Param, Expr] = {
    val freeVarsInTypes = typ.foldLeft(Set[Param]())({ case (acc, t) =>
      acc.union(t.freeVars)
    })
    constValues.filterKeys(x => freeVarsInTypes.contains(x))
  }

  def wrapTopLevelFunction(inputs: Seq[Param], body: Expr): Expr = {
    implicit val c: Canonicalizer = NoOpCanonicalizer
    inputs.foldRight(body)({ case (x, acc) => Function(x, acc)().tchk() })
  }

  def unwrapTopLevelFunction(f: Expr): (Seq[Param], Expr) = {
    @tailrec
    def unwrap(e: Expr, inputs: Seq[Param]): (Seq[Param], Expr) = {
      e match {
        case Function(x, e) if x.typ == TyTuple() =>
          // TODO: Why is this case needed again?
          unwrap(e, inputs)
        case Function(x, e) => unwrap(e, x +: inputs)
        case e              => (inputs, e)
      }
    }
    val (inputs, body) = unwrap(f, Seq())
    (inputs.reverse, body)
  }

  private def checkAndEvalConst(
      decl: ConstDecl,
      constTypes: Map[Param, Type],
      constVals: Map[Param, Expr]
  )(implicit c: Canonicalizer): ConstDecl = {
    val ConstDecl(x, e) = decl
    val checkedE = e.tchk(constTypes, constVals)
    assert(x.typ != Missing)
    assert(checkedE.typ == x.typ)
    val evaluatedE =
      mhir.eval.eval(checkedE.subPreserveType(constVals.toMap[Expr, Expr]))
    ConstDecl(x, evaluatedE)
  }

  private def check(
      a: Assertion,
      constTypes: Map[Param, Type],
      constVals: Map[Param, Expr],
      accelInputs: Set[Param],
      accelOutTyp: Type
  )(implicit c: Canonicalizer): Assertion = {
    checkInputNames(params = accelInputs, args = a.inputs.keySet)
    val newIn = a.inputs.map({ case (x, e) =>
      val newE = e.tchk(constTypes, constVals)
      val newX = x.rebuild(newE.typ).asInstanceOf[Param]
      newX -> newE
    })
    checkInputTypes(
      params = accelInputs,
      args = newIn.keySet,
      constVals
    )
    val newOut = a.expectedOutput.tchk(constTypes, constVals)
    if (!newOut.typ.equalsGivenConstants(accelOutTyp, constVals)) {
      val constNote = listRelevantConstants(constVals, newOut.typ, accelOutTyp)
      throw new TypeError(
        "invalid expected output in assertion:" +
          s" accelerator produces $accelOutTyp but assertion expects ${newOut.typ}$constNote"
      )
    }
    val newIgnore = a.ignore.map({ e =>
      val newE = e.tchk(constTypes, constVals)
      if (!newE.typ.equalsGivenConstants(accelOutTyp, constVals)) {
        val constNote = listRelevantConstants(constVals, newE.typ, accelOutTyp)
        throw new TypeError(
          "invalid 'ignoring' stream in assertion:" +
            s" accelerator produces $accelOutTyp but 'ignoring' stream has type ${newE.typ}$constNote"
        )
      }
      newE
    })
    Assertion(newIn, newOut, newIgnore)
  }

  private def checkInputNames(params: Set[Param], args: Set[Param]): Unit = {
    val missingInputs = params.diff(args)
    val missingInputsStr = missingInputs
      .map(x => s"'${x.name}'")
      .toSeq
      .sorted
      .mkString(", ")
    val unknownInputs = args.diff(params)
    val unknownInputsStr = unknownInputs
      .map(x => s"'${x.name}'")
      .toSeq
      .sorted
      .mkString(", ")
    if (missingInputs.nonEmpty && unknownInputs.nonEmpty) {
      throw new TypeError(
        s"invalid inputs in assertion:"
          + s" missing argument for $missingInputsStr and unknown parameter $unknownInputsStr"
      )
    } else if (missingInputs.nonEmpty) {
      throw new TypeError(
        s"invalid inputs in assertion:"
          + s" missing argument for $missingInputsStr"
      )
    } else if (unknownInputs.nonEmpty) {
      throw new TypeError(
        s"invalid inputs in assertion:"
          + s" unknown parameter $unknownInputsStr"
      )
    }
  }

  private def checkInputTypes(
      params: Set[Param],
      args: Set[Param],
      constants: Map[Param, Expr]
  )(implicit c: Canonicalizer): Unit = {
    assert(params == args)
    for ((p, a) <- params.zip(args)) {
      if (!a.typ.equalsGivenConstants(p.typ, constants)) {
        val constNote = listRelevantConstants(constants, p.typ, a.typ)
        throw new TypeError(
          "invalid inputs in assertion:"
            + s" parameter '${p.name}' has type ${p.typ} but assertion provides ${a.typ}$constNote"
        )
      }
    }
  }

  private def listRelevantConstants(
      allConstVals: Map[Param, Expr],
      types: Type*
  ): String = {
    val freeVars = TyTuple(types: _*).freeVars
    val relevantConstants =
      allConstVals.keySet.intersect(freeVars)
    if (relevantConstants.isEmpty) {
      ""
    } else {
      val constList = relevantConstants
        .map(x => s"${x.name} = ${allConstVals(x)}")
        .toSeq
        .sorted
        .mkString(", ")
      s" (note that $constList)"
    }
  }
}
