package mhir.ir
package evaluate

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.{TypeCheck, TypeError}

import scala.annotation.tailrec
import scala.language.{existentials, implicitConversions}

/** The evaluator.
  */
trait Eval {

  private val MaxInvalidSteps = 10000

  /** Evaluates an expression.
    *
    * @param e
    *   the expression to evaluate.
    * @param suppressWarnings
    *   if true, then no exceptions will be thrown for warnings.
    *
    * @throws mhir.ir.typecheck.TypeError
    *   if the expression is ill-typed.
    * @throws EvalException
    *   if the evaluator encounters an undefined value <i>and it seems to affect
    *   the final value</i>, or if a stream seems to be deadlocked.
    */
  def eval(e: Expr, suppressWarnings: Boolean = false): Expr = {
    val Value(v, warnings) = evalBigStep(e.tchk().lower())
    if (warnings.isEmpty || suppressWarnings) {
      v
    } else {
      throw UndefinedValException(warnings)
    }
  }

  @tailrec
  private def evalPipeline(
      pipe: StmPipeline,
      elems: Seq[Expr],
      invalidSteps: Int = 0,
      maxInvalid: Int = MaxInvalidSteps
  ): Expr = {
    if (pipe.isEmpty) {
      StmLiteral(elems.reverse: _*)(pipe.typ)
    } else if (pipe.isStuck) {
      throw new DeadlockError(pipe.deadlockReasons.toSeq)
    } else if (invalidSteps >= maxInvalid) {
      throw new DeadlockError(Seq(TooManySteps))
    } else {
      pipe.sink.out match {
        case Some(v) =>
          evalPipeline(
            pipe.step(),
            v +: elems,
            invalidSteps = 0,
            maxInvalid = maxInvalid
          )
        case None =>
          evalPipeline(
            pipe.step(),
            elems,
            invalidSteps = invalidSteps + 1,
            maxInvalid = maxInvalid
          )
      }
    }
  }

  private[ir] def stmLiteralToStmBuild(s: StmLiteral): StmBuild = {
    val t = s.typ.asInstanceOf[TyStm].t
    val n = s.typ.asInstanceOf[TyStm].n
    // The index type must be at least wide enough to fit the value 1, since
    // the index accumulator is updated by i + 1
    val idxTyp = TyAnyInt.tightest(0, math.max(1, s.elems.length))
    val i = Param("i")(idxTyp)
    val v = Param("v")(TyVec(t, n))
    StmBuild(
      s.elems.length,
      VecAccess(v, i)(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (IntCst(0)(idxTyp), i + 1),
        v -> (VecLiteral(s.elems: _*)(TyVec(t, n)), v)
      )
    )().tchk().lower().asInstanceOf[StmBuild]
  }

  private[ir] def evalBigStep(e: Expr): Value = {
    val result: Value = e match {
      case x: Param =>
        throw new IllegalArgumentException(
          s"Free variable ${x.name}. Terms must be closed."
        )
      case f: Function => Value(f, Set())
      case FunCall(f, arg) =>
        val Value(fVal, fWarn) = evalBigStep(f)
        fVal match {
          case Function(x, body) =>
            // Leave stream inputs unevaluated
            val Value(a, aWarn) = arg.typ match {
              case _: TyStm => Value(arg, Set())
              case _        => evalBigStep(arg)
            }
            evalBigStep(body.subPreserveType(x -> a))
              .addWarnings(aWarn ++ fWarn)
          case v =>
            throw new IllegalArgumentException(
              s"Left-hand side of function application evaluated to $v. It must evaluate to a function."
            )
        }

      case n: IntCst => Value(n, Set())
      case Sum(terms @ _*) =>
        val termValues = terms.map(e => evalBigStep(e))
        if (termValues.forall(v => v.e.isInstanceOf[IntCst])) {
          val xs = termValues.map(v => v.e.asInstanceOf[IntCst].i)
          val warnings = termValues.flatMap(v => v.warnings).toSet
          val result = xs.sum
          val typ = e.typ.asInstanceOf[TyAnyInt]
          if (typ.contains(result)) {
            Value(IntCst(result)(e.typ), warnings)
          } else {
            val overflowWarning = OverflowWarning(result, typ)
            Value(Default.getDefault(typ), warnings + overflowWarning)
          }
        } else {
          throw new IllegalArgumentException(
            s"Terms of Sum evaluated to $termValues. They must each evaluate to an integer."
          )
        }
      case Prod(factors @ _*) =>
        val factorValues = factors.map(e => evalBigStep(e))
        if (factorValues.forall(v => v.e.isInstanceOf[IntCst])) {
          val xs = factorValues.map(v => v.e.asInstanceOf[IntCst].i)
          val warnings = factorValues.flatMap(v => v.warnings).toSet
          val result = xs.product
          val typ = e.typ.asInstanceOf[TyAnyInt]
          if (typ.contains(result)) {
            Value(IntCst(result)(e.typ), warnings)
          } else {
            val overflowWarning = OverflowWarning(result, typ)
            Value(Default.getDefault(typ), warnings + overflowWarning)
          }
        } else {
          throw new IllegalArgumentException(
            s"Terms of Prod evaluated to $factorValues. They must each evaluate to an integer."
          )
        }
      case Div(e1, e2) =>
        val Value(numer, numerWarn) = evalBigStep(e1)
        val Value(denom, denomWarn) = evalBigStep(e2)
        (numer, denom) match {
          case (IntCst(_), IntCst(0)) =>
            val v = Default.getDefault(e.typ)
            Value(v, (numerWarn ++ denomWarn) + DivByZeroWarning)
          case (IntCst(n1), IntCst(n2)) =>
            Value(IntCst(n1 / n2)(e.typ), numerWarn ++ denomWarn)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Div evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case Mod(e1, e2) =>
        val Value(numer, numerWarn) = evalBigStep(e1)
        val Value(denom, denomWarn) = evalBigStep(e2)
        (numer, denom) match {
          case (IntCst(_), IntCst(0)) =>
            val v = Default.getDefault(e.typ)
            Value(v, (numerWarn ++ denomWarn) + DivByZeroWarning)
          case (IntCst(n1), IntCst(n2)) =>
            Value(IntCst(n1 % n2)(e.typ), numerWarn ++ denomWarn)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Mod evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case PadTo(e, targetWidth) =>
        val v = evalBigStep(e)
        assert(
          v.e.isInstanceOf[IntCst],
          s"argument of ${PadTo.getClass.getSimpleName} must be an integer (found ${v.e})"
        )
        val typ = v.e.typ.asInstanceOf[TyAnyInt]
        assert(
          targetWidth >= typ.w,
          s"pad target width must be greater than or equal to original width (target $targetWidth, original ${typ.w})"
        )
        typ match {
          case _: TySInt => Value(v.e.rebuild(TySInt(targetWidth)), v.warnings)
          case _: TyUInt => Value(v.e.rebuild(TyUInt(targetWidth)), v.warnings)
        }
      case TruncateTo(e, targetWidth) =>
        val Value(IntCst(i), warnings) = evalBigStep(e)
        val typ = e.typ.asInstanceOf[TyAnyInt]
        assert(
          targetWidth <= typ.w,
          s"truncate target width must be less than or equal to original width (target $targetWidth, original ${typ.w})"
        )
        val targetTyp = typ.withWidth(targetWidth)
        if (targetTyp.contains(i)) {
          Value(IntCst(i)(targetTyp), warnings)
        } else {
          val overflowWarning = OverflowWarning(i, targetTyp)
          Value(
            IntCst(truncate(i, targetTyp))(targetTyp),
            warnings + overflowWarning
          )
        }
      case ToSigned(e) =>
        val v = evalBigStep(e)
        v.e.typ.asInstanceOf[TyUInt] match {
          case TyUInt(w) =>
            Value(v.e.rebuild(TySInt(w + 1)), v.warnings)
        }
      case ToUnsigned(e) =>
        val v = evalBigStep(e)
        val i = v.e.asInstanceOf[IntCst].i
        v.e.typ.asInstanceOf[TySInt] match {
          case TySInt(w) =>
            // Just drop the sign bit
            assert(w >= 1)
            val typ = TyUInt(w - 1)
            if (typ.contains(i)) {
              Value(IntCst(i)(typ), v.warnings)
            } else {
              val overflowWarning = OverflowWarning(i, typ)
              Value(IntCst(truncate(i, typ))(typ), v.warnings + overflowWarning)
            }
        }
      case LLShift(e1, e2) =>
        val Value(n1, warn1) = evalBigStep(e1)
        val Value(n2, warn2) = evalBigStep(e2)
        (n1, n2) match {
          case (IntCst(k1), IntCst(k2)) =>
            val result = truncate(k1 << k2, n1.typ.asInstanceOf[TyAnyInt])
            Value(C(result)(n1.typ), warn1 ++ warn2)
          case (v1, v2) =>
            throw new TypeError(
              s"Operands of LLShift evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }

      case True  => Value(True, Set())
      case False => Value(False, Set())
      case Not(e) =>
        evalBigStep(e) match {
          case Value(False, w) => Value(True, w)
          case Value(True, w)  => Value(False, w)
          case v =>
            throw new IllegalArgumentException(
              s"Operand of Not evaluated to $v. It must evaluate to a boolean."
            )
        }
      case And(terms @ _*) =>
        // TODO: Make the warnings more accurate
        //        * If any values are `False`, only keep those?
        //        * If any of the `False` values have no warnings, then discard
        //          all warnings?
        val termValues = terms.map(e => evalBigStep(e))
        if (termValues.forall(v => v.e.isInstanceOf[BoolCst])) {
          val v = if (termValues.exists(v => v.e == False)) {
            False
          } else {
            True
          }
          val warnings = termValues.flatMap(v => v.warnings).toSet
          Value(v, warnings)
        } else {
          throw new IllegalArgumentException(
            s"Terms of And evaluated to $termValues. They must all evaluate to booleans."
          )
        }
      case Or(terms @ _*) =>
        // TODO: Make the warnings more accurate
        //        * If any values are `True`, only keep their warnings?
        //        * If any of the `True` values have no warnings, then discard
        //          all warnings?
        val termValues = terms.map(e => evalBigStep(e))
        if (termValues.forall(v => v.e.isInstanceOf[BoolCst])) {
          val v = if (termValues.exists(v => v.e == True)) {
            True
          } else {
            False
          }
          val warnings = termValues.flatMap(v => v.warnings).toSet
          Value(v, warnings)
        } else {
          throw new IllegalArgumentException(
            s"Terms of Or evaluated to $termValues. They must all evaluate to booleans."
          )
        }
      case Equal(e1, e2) =>
        val Value(e1Val, e1Warn) = evalBigStep(e1)
        val Value(e2Val, e2Warn) = evalBigStep(e2)
        val result = if (areEqual(e1Val, e2Val)) True else False
        Value(result, e1Warn ++ e2Warn)
      case LessThan(e1, e2) =>
        val Value(e1Val, e1Warn) = evalBigStep(e1)
        val Value(e2Val, e2Warn) = evalBigStep(e2)
        (e1Val, e2Val) match {
          case (IntCst(n1), IntCst(n2)) =>
            val v = if (n1 < n2) True else False
            Value(v, e1Warn ++ e2Warn)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of LessThan evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case Mux(c, t, f) =>
        // Note that both branches are always evaluated!
        val Value(cVal, cWarn) = evalBigStep(c)
        val Value(tVal, tWarn) = evalBigStep(t)
        val Value(fVal, fWarn) = evalBigStep(f)
        cVal match {
          case True  => Value(tVal, cWarn ++ tWarn)
          case False => Value(fVal, cWarn ++ fWarn)
          case v =>
            throw new IllegalArgumentException(
              s"Condition of Mux evaluated to $v. It must evaluate to a boolean."
            )
        }

      case Tuple(elems @ _*) =>
        val elemValues = elems.map(e => evalBigStep(e))
        val v = Tuple(elemValues.map(v => v.e): _*)()
        val warnings = elemValues.flatMap(v => v.warnings).toSet
        Value(v, warnings)
      case TupleAccess(t, IntCst(i)) =>
        // TODO: make the warnings more accurate by somehow only taking the
        //       ones that apply to the chosen element?
        val Value(tVal, tWarn) = evalBigStep(t)
        tVal match {
          case Tuple(elems @ _*) =>
            val v = if (elems.indices.contains(i)) {
              elems(i.toInt)
            } else {
              throw new TypeError(
                s"Index $i is out of bounds for a tuple with ${elems.length} elements."
              )
            }
            Value(v, tWarn)
          case v =>
            throw new IllegalArgumentException(
              s"Tuple of tuple access evaluated to $v. It must evaluate to a tuple literal."
            )
        }

      case vb @ VecBuild(n, f) =>
        assert(vb.typ.isInstanceOf[TyVec])
        val Value(nVal, nWarn) = evalBigStep(n)
        nVal match {
          case IntCst(n) if n >= 0 =>
            val elemValues = (0 until n.toInt).map(i => {
              val inTyp = f.param.typ
              assert(inTyp.isInstanceOf[TyUInt])
              evalBigStep(FunCall(f, IntCst(i)(inTyp))().tchk())
            })
            val v = VecLiteral(elemValues.map(v => v.e): _*)(vb.typ)
            val warnings = nWarn ++ elemValues.flatMap(v => v.warnings).toSet
            Value(v, warnings)
          case n =>
            throw new IllegalArgumentException(
              s"Vector length $n. Vectors must have non-negative integer length."
            )
        }
      case VecAccess(v, i) =>
        val Value(vVal, vWarn) = evalBigStep(v)
        vVal match {
          case VecLiteral(elems @ _*) =>
            val Value(iVal, iWarn) = evalBigStep(i)
            iVal match {
              case IntCst(i) if elems.indices.contains(i) =>
                Value(elems(i.toInt), vWarn ++ iWarn)
              case IntCst(i) =>
                val t = v.tchk().typ.asInstanceOf[TyVec].t
                val oobWarn = VecIndexOutOfBoundsWarning(elems.length, i)
                evalBigStep(Default(t).lower())
                  .addWarnings((vWarn ++ iWarn) + oobWarn)
              case v =>
                throw new IllegalArgumentException(
                  s"Index of vector access evaluated to $v. It must evaluate to a vector."
                )
            }
          case v =>
            throw new IllegalArgumentException(
              s"Vector of vector access evaluated to $v. It must evaluate to a vector."
            )
        }
      case vl: VecLiteral =>
        val elemValues = vl.elems.map(e => evalBigStep(e))
        val v = VecLiteral(elemValues.map(v => v.e): _*)(e.typ)
        val warnings = elemValues.flatMap(v => v.warnings).toSet
        Value(v, warnings)

      case s @ (_: StmLiteral | _: StmBuild | _: LetStm) =>
        Value(evalPipeline(StmPipeline(s), Seq()), Set())
      case _: StmData =>
        throw new IllegalArgumentException(
          s"Invalid use of ${StmData.getClass.getSimpleName} (e.g., outside a stream or with incorrect arguments)."
        )
      case StmNextK(s, k) =>
        val Value(sVal, sWarn) = evalBigStep(s)
        sVal match {
          case s: StmLiteral =>
            val Value(kVal, kWarn) = evalBigStep(k)
            kVal match {
              case IntCst(k) if k <= 0 =>
                Value(s, sWarn ++ kWarn)
              case IntCst(k) =>
                val t = s.typ.asInstanceOf[TyStm].t
                val newElems = s.elems.drop(k.toInt)
                val v = StmLiteral(newElems: _*)(TyStm(t, newElems.length))
                Value(v, sWarn ++ kWarn)
              case e =>
                throw new TypeError(
                  s"Number in ${StmNextK.getClass.getSimpleName} evaluated to $e."
                    + "It must evaluate to an integer."
                )
            }
          case e =>
            throw new TypeError(
              s"Stream in ${StmNextK.getClass.getSimpleName} evaluated to $e."
                + s"It must evaluate to a ${StmLiteral.getClass.getSimpleName}."
            )
        }

      case s: SyntaxSugar =>
        throw new IllegalArgumentException(
          s"There should be no more syntax sugar after lowering. Found $s."
        )
    }
    val Value(v, warnings) = result
    val typedV = v.tchk()
    assert(
      typedV.typ ~~= e.typ,
      s"evaluation should preserve the type (expected ${e.typ}, found ${typedV.typ})"
    )
    Value(typedV, warnings)
  }

  private def truncate(n: Long, typ: TyAnyInt): Long = {
    val mask = (0 until typ.w).foldLeft(0L)({ case (n, _) =>
      (n << 1) | 1
    })
    val masked = n & mask
    typ match {
      case _: TySInt =>
        // Need to sign extend because the value is stored in a Scala
        // Long, which is a 64-bit signed int
        assert(
          masked.isInstanceOf[Long],
          "expect value to be stored in a 64-bit int"
        )
        val msbIsOne = (masked & (1L << (typ.w - 1))) != 0
        if (msbIsOne) {
          (typ.w until 64).foldLeft(masked)({ case (n, i) =>
            n | (1L << i)
          })
        } else {
          masked
        }
      case _: TyUInt =>
        masked
    }
  }

  private def areEqual(v1: Expr, v2: Expr): Boolean = {
    (v1, v2) match {
      case (b1: BoolCst, b2: BoolCst) => b1 == b2
      case (IntCst(n1), IntCst(n2))   => n1 == n2
      case (Tuple(xs1 @ _*), Tuple(xs2 @ _*)) =>
        assert(xs1.length == xs2.length, "tuple lengths must match")
        xs1.zip(xs2).forall({ case (x, y) => areEqual(x, y) })
      case (VecLiteral(xs1 @ _*), VecLiteral(xs2 @ _*)) =>
        assert(xs1.length == xs2.length, "vector length must match")
        xs1.zip(xs2).forall({ case (x, y) => areEqual(x, y) })
      case _ => false
    }
  }
}
