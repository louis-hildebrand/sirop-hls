package mhir.eval

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.sugar.ExprLowering
import mhir.typecheck.{TypeCheck, TypeError}

import scala.annotation.tailrec
import scala.language.{existentials, implicitConversions}

/** The evaluator.
  *
  * @param maxInvalidSteps
  *   the maximum number of steps without a valid output from a [[StmBuild]]
  *   node. If this is less than or equal to zero, then there is no limit.
  * @param suppressWarnings
  *   if `false`, do not throw any errors for warnings (e.g., when the output
  *   seems to be undefined).
  */
class Evaluator(val maxInvalidSteps: Int, val suppressWarnings: Boolean) {

  private implicit val logger: Logger = Logger(getClass.getName)

  /** Evaluates an expression.
    *
    * @param e
    *   the expression to evaluate.
    * @throws mhir.typecheck.TypeError
    *   if the expression is ill-typed.
    * @throws EvalException
    *   if the evaluator encounters an undefined value <i>and it seems to affect
    *   the final value</i>, or if a stream seems to be deadlocked.
    */
  def eval(e: Expr, stmData: Map[Param, Option[Expr]] = Map()): Expr = {
    val Value(v, warnings) = evalBigStep(stmData)(e.tchk().lower)
    if (warnings.isEmpty) {
      v
    } else if (this.suppressWarnings) {
      val warnStr = warnings.map(_.display).mkString(", ")
      logger.warn(s"the result of evaluation seems to be undefined: $warnStr")
      v
    } else {
      throw UndefinedValException(warnings)
    }
  }

  @tailrec
  private def evalPipeline(
      pipe: StmPipeline,
      elems: Seq[Expr],
      invalidSteps: Int
  ): Expr = {
    if (pipe.isEmpty) {
      StmLiteral(elems.reverse: _*)(pipe.typ)
    } else if (pipe.isStuck) {
      throw new DeadlockError(pipe.deadlockReasons.toSeq)
    } else if (
      this.maxInvalidSteps > 0 && invalidSteps >= this.maxInvalidSteps
    ) {
      throw new DeadlockError(Seq(TooManySteps))
    } else {
      val nextPipe = pipe.step()
      if (nextPipe.sameState(pipe)) {
        throw new DeadlockError(Seq(PipelineFixpoint))
      }
      pipe.sink.out(StmNodeId("")) match {
        case Some(v) =>
          evalPipeline(nextPipe, v +: elems, invalidSteps = 0)
        case None =>
          evalPipeline(nextPipe, elems, invalidSteps = invalidSteps + 1)
      }
    }
  }

  private[eval] def evalBigStep(
      stmData: Map[Param, Option[Expr]]
  )(e: Expr): Value = {
    val result: Value = e match {
      case Undefined(typ) =>
        val Value(v, warnings) = evalBigStep(stmData)(DefaultVal(typ))
        Value(v, warnings + UndefinedPrimitive(typ))
      case x: Param =>
        throw new IllegalArgumentException(
          s"Free variable ${x.name}. Terms must be closed."
        )
      case f: Function => Value(f, Set())
      case FunCall(f, arg) =>
        val Value(fVal, fWarn) = evalBigStep(stmData)(f)
        fVal match {
          case Function(x, body) =>
            // Leave stream inputs unevaluated
            val Value(a, aWarn) = arg.typ match {
              case _: TyStm => Value(arg, Set())
              case _        => evalBigStep(stmData)(arg)
            }
            evalBigStep(stmData)(body.subPreserveType(x -> a))
              .addWarnings(aWarn ++ fWarn)
          case v =>
            throw new IllegalArgumentException(
              s"Left-hand side of function application evaluated to $v. It must evaluate to a function."
            )
        }

      case n: IntCst => Value(n, Set())
      case Sum(terms @ _*) =>
        val termValues = terms.map(evalBigStep(stmData))
        if (termValues.forall(v => v.e.isInstanceOf[IntCst])) {
          val xs = termValues.map(v => v.e.asInstanceOf[IntCst].i)
          val warnings = termValues.flatMap(v => v.warnings).toSet
          val result = xs.sum
          val typ = e.typ.asInstanceOf[TyAnyInt]
          if (typ.contains(result)) {
            Value(IntCst(result)(e.typ), warnings)
          } else {
            val overflowWarning = OverflowWarning(result, typ)
            Value(DefaultVal(typ), warnings + overflowWarning)
          }
        } else {
          throw new IllegalArgumentException(
            s"Terms of Sum evaluated to $termValues. They must each evaluate to an integer."
          )
        }
      case Prod(factors @ _*) =>
        val factorValues = factors.map(evalBigStep(stmData))
        if (factorValues.forall(v => v.e.isInstanceOf[IntCst])) {
          val xs = factorValues.map(v => v.e.asInstanceOf[IntCst].i)
          val warnings = factorValues.flatMap(v => v.warnings).toSet
          val result = xs.product
          val typ = e.typ.asInstanceOf[TyAnyInt]
          if (typ.contains(result)) {
            Value(IntCst(result)(e.typ), warnings)
          } else {
            val overflowWarning = OverflowWarning(result, typ)
            Value(DefaultVal(typ), warnings + overflowWarning)
          }
        } else {
          throw new IllegalArgumentException(
            s"Terms of Prod evaluated to $factorValues. They must each evaluate to an integer."
          )
        }
      case Div(e1, e2) =>
        val Value(numer, numerWarn) = evalBigStep(stmData)(e1)
        val Value(denom, denomWarn) = evalBigStep(stmData)(e2)
        (numer, denom) match {
          case (IntCst(_), IntCst(0)) =>
            val v = DefaultVal(e.typ)
            Value(v, (numerWarn ++ denomWarn) + DivByZeroWarning)
          case (IntCst(n1), IntCst(n2)) =>
            Value(IntCst(n1 / n2)(e.typ), numerWarn ++ denomWarn)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Div evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case Mod(e1, e2) =>
        val Value(numer, numerWarn) = evalBigStep(stmData)(e1)
        val Value(denom, denomWarn) = evalBigStep(stmData)(e2)
        (numer, denom) match {
          case (IntCst(_), IntCst(0)) =>
            val v = DefaultVal(e.typ)
            Value(v, (numerWarn ++ denomWarn) + DivByZeroWarning)
          case (IntCst(n1), IntCst(n2)) =>
            Value(IntCst(n1 % n2)(e.typ), numerWarn ++ denomWarn)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Mod evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case s @ WrappingSum(terms @ _*) =>
        val termValues = terms.map(evalBigStep(stmData))
        if (termValues.forall(v => v.e.isInstanceOf[IntCst])) {
          val xs = termValues.map(v => v.e.asInstanceOf[IntCst].i)
          val warnings = termValues.flatMap(v => v.warnings).toSet
          val result = xs.sum
          val typ = e.typ.asInstanceOf[TyAnyInt]
          Value(IntCst(truncate(result, typ))(e.typ), warnings)
        } else {
          throw new IllegalArgumentException(
            s"Operands of ${s.className} evaluated to $termValues."
              + " They must all evaluate to integers."
          )
        }
      case d @ WrappingDiff(e1, e2) =>
        val Value(v1, e1Warn) = evalBigStep(stmData)(e1)
        val Value(v2, e2Warn) = evalBigStep(stmData)(e2)
        (v1, v2) match {
          case (IntCst(n1), IntCst(n2)) =>
            val result = n1 - n2
            val typ = e1.typ.asInstanceOf[TyAnyInt]
            Value(IntCst(truncate(result, typ))(e.typ), e1Warn ++ e2Warn)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of ${d.className} evaluated to $v1 and $v2."
                + " They must each evaluate to an integer."
            )
        }
      case p @ WrappingProd(factors @ _*) =>
        val factorValues = factors.map(evalBigStep(stmData))
        if (factorValues.forall(v => v.e.isInstanceOf[IntCst])) {
          val xs = factorValues.map(v => v.e.asInstanceOf[IntCst].i)
          val warnings = factorValues.flatMap(v => v.warnings).toSet
          val result = xs.product
          val typ = e.typ.asInstanceOf[TyAnyInt]
          Value(IntCst(truncate(result, typ))(e.typ), warnings)
        } else {
          throw new IllegalArgumentException(
            s"Operands of ${p.className} evaluated to $factorValues."
              + " They must all evaluate to integers."
          )
        }
      case PadTo(e, targetWidth) =>
        val v = evalBigStep(stmData)(e)
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
        val Value(IntCst(i), warnings) = evalBigStep(stmData)(e)
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
        val v = evalBigStep(stmData)(e)
        v.e.typ.asInstanceOf[TyUInt] match {
          case TyUInt(w) =>
            Value(v.e.rebuild(TySInt(w + 1)), v.warnings)
        }
      case ToUnsigned(e) =>
        val v = evalBigStep(stmData)(e)
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
        val Value(n1, warn1) = evalBigStep(stmData)(e1)
        val Value(n2, warn2) = evalBigStep(stmData)(e2)
        (n1, n2) match {
          case (IntCst(k1), IntCst(k2)) =>
            val result = truncate(k1 << k2, n1.typ.asInstanceOf[TyAnyInt])
            Value(C(result)(n1.typ), warn1 ++ warn2)
          case (v1, v2) =>
            throw new TypeError(
              s"Operands of LLShift evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case LRShift(e1, e2) =>
        val Value(n1, warn1) = evalBigStep(stmData)(e1)
        val Value(n2, warn2) = evalBigStep(stmData)(e2)
        (n1, n2) match {
          case (IntCst(k1), IntCst(k2)) =>
            val w = n1.typ.asInstanceOf[TyAnyInt].w
            val result = maskOutHigherBits(k1, w) >>> k2
            val extendedResult = n1.typ.asInstanceOf[TyAnyInt] match {
              case TySInt(w) => signExtendToLong(result, w)
              case TyUInt(_) => result
            }
            Value(C(extendedResult)(n1.typ), warn1 ++ warn2)
          case (v1, v2) =>
            throw new TypeError(
              s"Operands of LRShift evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }

      case c: FixCst => Value(c, Set())
      case p @ IntFixProd(e1, e2) =>
        val Value(v1, warn1) = evalBigStep(stmData)(e1)
        val Value(v2, warn2) = evalBigStep(stmData)(e2)
        (v1, v2) match {
          case (v1 @ IntCst(k), v2 @ FixCst(numer)) =>
            val result = (k * numer) >>> v2.typ.shift
            val typ = v1.typ.asInstanceOf[TyUInt]
            Value(IntCst(truncate(result, typ))(typ), warn1 ++ warn2)
          case (v1, v2) =>
            throw new TypeError(
              s"Operands of ${p.className} evaluated to $v1 and $v2."
                + " The first must evaluate to an integer and the second must evaluate to a fixed-point number."
            )
        }

      case True  => Value(True, Set())
      case False => Value(False, Set())
      case Not(e) =>
        evalBigStep(stmData)(e) match {
          case Value(False, w) => Value(True, w)
          case Value(True, w)  => Value(False, w)
          case v =>
            throw new IllegalArgumentException(
              s"Operand of Not evaluated to $v. It must evaluate to a boolean."
            )
        }
      case And(terms @ _*) =>
        val termValues = terms.map(evalBigStep(stmData))
        if (termValues.forall(v => v.e.isInstanceOf[BoolCst])) {
          val v = if (termValues.exists(v => v.e == False)) {
            False
          } else {
            True
          }
          val existsFalseTermWithoutWarnings = termValues.exists({
            case Value(False, warnings) => warnings.isEmpty
            case _                      => false
          })
          val warnings = if (existsFalseTermWithoutWarnings) {
            Set[EvalWarning]()
          } else {
            termValues.flatMap(v => v.warnings).toSet
          }
          Value(v, warnings)
        } else {
          throw new IllegalArgumentException(
            s"Terms of And evaluated to $termValues. They must all evaluate to booleans."
          )
        }
      case Or(terms @ _*) =>
        val termValues = terms.map(evalBigStep(stmData))
        if (termValues.forall(v => v.e.isInstanceOf[BoolCst])) {
          val v = if (termValues.exists(v => v.e == True)) {
            True
          } else {
            False
          }
          val existsTrueTermWithoutWarnings = termValues.exists({
            case Value(True, warnings) => warnings.isEmpty
            case _                     => false
          })
          val warnings = if (existsTrueTermWithoutWarnings) {
            Set[EvalWarning]()
          } else {
            termValues.flatMap(v => v.warnings).toSet
          }
          Value(v, warnings)
        } else {
          throw new IllegalArgumentException(
            s"Terms of Or evaluated to $termValues. They must all evaluate to booleans."
          )
        }
      case Equal(e1, e2) =>
        val Value(e1Val, e1Warn) = evalBigStep(stmData)(e1)
        val Value(e2Val, e2Warn) = evalBigStep(stmData)(e2)
        val result = if (areEqual(e1Val, e2Val)) True else False
        Value(result, e1Warn ++ e2Warn)
      case LessThan(e1, e2) =>
        val Value(e1Val, e1Warn) = evalBigStep(stmData)(e1)
        val Value(e2Val, e2Warn) = evalBigStep(stmData)(e2)
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
        val Value(cVal, cWarn) = evalBigStep(stmData)(c)
        cVal match {
          case True =>
            val Value(tVal, tWarn) = evalBigStep(stmData)(t)
            Value(tVal, cWarn ++ tWarn)
          case False =>
            val Value(fVal, fWarn) = evalBigStep(stmData)(f)
            Value(fVal, cWarn ++ fWarn)
          case v =>
            throw new IllegalArgumentException(
              s"Condition of Mux evaluated to $v. It must evaluate to a boolean."
            )
        }

      case Tuple(elems @ _*) =>
        val elemValues = elems.map(evalBigStep(stmData))
        val v = Tuple(elemValues.map(v => v.e): _*)()
        val warnings = elemValues.flatMap(v => v.warnings).toSet
        Value(v, warnings)
      case TupleAccess(t, IntCst(i)) =>
        // TODO: make the warnings more accurate by somehow only taking the
        //       ones that apply to the chosen element?
        val Value(tVal, tWarn) = evalBigStep(stmData)(t)
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
        val Value(nVal, nWarn) = evalBigStep(stmData)(n)
        nVal match {
          case IntCst(n) if n >= 0 =>
            val elemValues = (0 until n.toInt).map(i => {
              val inTyp = f.param.typ
              assert(inTyp.isInstanceOf[TyUInt])
              evalBigStep(stmData)(FunCall(f, IntCst(i)(inTyp))().tchk())
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
        val Value(vVal, vWarn) = evalBigStep(stmData)(v)
        vVal match {
          case VecLiteral(elems @ _*) =>
            val Value(iVal, iWarn) = evalBigStep(stmData)(i)
            iVal match {
              case IntCst(i) if elems.indices.contains(i) =>
                Value(elems(i.toInt), vWarn ++ iWarn)
              case IntCst(i) =>
                val t = v.tchk().typ.asInstanceOf[TyVec].t
                val oobWarn = VecIndexOutOfBoundsWarning(elems.length, i)
                evalBigStep(stmData)(DefaultVal(t))
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
        val elemValues = vl.elems.map(e => evalBigStep(stmData)(e))
        val v = VecLiteral(elemValues.map(v => v.e): _*)(e.typ)
        val warnings = elemValues.flatMap(v => v.warnings).toSet
        Value(v, warnings)

      case s @ (_: StmLiteral | _: StmBuild | _: LetStm) =>
        Value(evalPipeline(StmPipeline(s), Seq(), invalidSteps = 0), Set())
      case sd @ StmData(s: Param) =>
        stmData.get(s) match {
          case None =>
            throw new IllegalArgumentException(
              s"Invalid use of ${StmData.getClass.getSimpleName} (e.g., outside a stream or with incorrect arguments)."
            )
          case Some(None) =>
            evalBigStep(Map())(DefaultVal(sd.typ))
              .addWarnings(Set(StmDataWithoutReady(s)))
          case Some(Some(v)) =>
            Value(v, Set())
        }
      case StmData(_) =>
        throw new IllegalArgumentException(
          s"Invalid use of ${StmData.getClass.getSimpleName} (non-param input)."
        )

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
    val masked = maskOutHigherBits(n, typ.w)
    typ match {
      case _: TySInt =>
        // Need to sign extend because the value is stored in a Scala
        // Long, which is a 64-bit signed int
        signExtendToLong(masked, typ.w)
      case _: TyUInt =>
        // Higher bits are already zero, as they should be
        masked
    }
  }

  private def maskOutHigherBits(n: Long, w: Int): Long = {
    val mask = (0 until w).foldLeft(0L)({ case (n, _) =>
      (n << 1) | 1
    })
    n & mask
  }

  private def signExtendToLong(n: Long, w: Int): Long = {
    val msbIsOne = (n & (1L << (w - 1))) != 0
    if (msbIsOne) {
      (w until 64).foldLeft(n)({ case (n, i) =>
        n | (1L << i)
      })
    } else {
      n
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

/** Companion object for [[Evaluator]].
  */
object Evaluator {

  private val DefaultMaxInvalidSteps: Int = 10000

  def apply(
      maxInvalidSteps: Option[Int] = None,
      suppressWarnings: Boolean = false
  ): Evaluator = {
    new Evaluator(
      maxInvalidSteps = maxInvalidSteps.getOrElse(DefaultMaxInvalidSteps),
      suppressWarnings = suppressWarnings
    )
  }
}
