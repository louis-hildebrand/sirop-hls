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
class Evaluator(
    val handshake: Boolean,
    val maxInvalidSteps: Int,
    val suppressWarnings: Boolean
) {

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
  def eval(
      e: Expr,
      inputs: Map[Param, Expr] = Map(),
      stmData: Map[Param, Option[Expr]] = Map()
  ): Expr = {
    val Value(v, warnings) = evalBigStep(inputs, stmData)(e.tchk().lower)
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
      inputs: Map[Param, Expr],
      stmData: Map[Param, Option[Expr]]
  )(e: Expr): Value = {
    val result: Value = e match {
      case Undefined(typ) =>
        val Value(v, warnings) = evalBigStep(inputs, stmData)(DefaultVal(typ))
        Value(v, warnings + UndefinedPrimitive(typ))
      case x: Param =>
        throw new IllegalArgumentException(
          s"Free variable ${x.name}. Terms must be closed."
        )
      case f: Function => Value(f, Set())
      case FunCall(f, arg) =>
        val Value(fVal, fWarn) = evalBigStep(inputs, stmData)(f)
        fVal match {
          case Function(x, body) =>
            // Leave stream inputs unevaluated
            val Value(a, aWarn) = arg.typ match {
              case _: TyStm => Value(arg, Set())
              case _        => evalBigStep(inputs, stmData)(arg)
            }
            evalBigStep(inputs, stmData)(body.subPreserveType(x -> a))
              .addWarnings(aWarn ++ fWarn)
          case v =>
            throw new IllegalArgumentException(
              s"Left-hand side of function application evaluated to $v. It must evaluate to a function."
            )
        }

      case n: IntCst => Value(n, Set())
      case Sum(terms @ _*) =>
        val termValues = terms.map(evalBigStep(inputs, stmData))
        if (termValues.forall(v => v.e.isInstanceOf[IntCst])) {
          val xs = termValues.map(v => v.e.asInstanceOf[IntCst].i)
          val warnings = termValues.flatMap(v => v.warnings).toSet
          val result = xs.sum
          val typ = e.typ.asInstanceOf[TyAnyInt]
          if (typ.contains(result)) {
            Value(IntCst(result)(e.typ), warnings)
          } else {
            val overflowWarning =
              OverflowWarning(
                result,
                typ,
                Sum(termValues.map(_.e): _*)().toString
              )
            Value(DefaultVal(typ), warnings + overflowWarning)
          }
        } else {
          throw new IllegalArgumentException(
            s"Terms of Sum evaluated to $termValues. They must each evaluate to an integer."
          )
        }
      case Prod(factors @ _*) =>
        val factorValues = factors.map(evalBigStep(inputs, stmData))
        if (factorValues.forall(v => v.e.isInstanceOf[IntCst])) {
          val xs = factorValues.map(v => v.e.asInstanceOf[IntCst].i)
          val warnings = factorValues.flatMap(v => v.warnings).toSet
          val result = xs.product
          val typ = e.typ.asInstanceOf[TyAnyInt]
          if (typ.contains(result)) {
            Value(IntCst(result)(e.typ), warnings)
          } else {
            val overflowWarning = OverflowWarning(
              result,
              typ,
              Prod(factorValues.map(_.e): _*)().toString
            )
            Value(DefaultVal(typ), warnings + overflowWarning)
          }
        } else {
          throw new IllegalArgumentException(
            s"Terms of Prod evaluated to $factorValues. They must each evaluate to an integer."
          )
        }
      case Div(e1, e2) =>
        val Value(numer, numerWarn) = evalBigStep(inputs, stmData)(e1)
        val Value(denom, denomWarn) = evalBigStep(inputs, stmData)(e2)
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
        val Value(numer, numerWarn) = evalBigStep(inputs, stmData)(e1)
        val Value(denom, denomWarn) = evalBigStep(inputs, stmData)(e2)
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
        val termValues = terms.map(evalBigStep(inputs, stmData))
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
        val Value(v1, e1Warn) = evalBigStep(inputs, stmData)(e1)
        val Value(v2, e2Warn) = evalBigStep(inputs, stmData)(e2)
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
        val factorValues = factors.map(evalBigStep(inputs, stmData))
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
        val v = evalBigStep(inputs, stmData)(e)
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
        val Value(k @ IntCst(i), warnings) = evalBigStep(inputs, stmData)(e)
        val typ = e.typ.asInstanceOf[TyAnyInt]
        assert(
          targetWidth <= typ.w,
          s"truncate target width must be less than or equal to original width (target $targetWidth, original ${typ.w})"
        )
        val targetTyp = typ.withWidth(targetWidth)
        if (targetTyp.contains(i)) {
          Value(IntCst(i)(targetTyp), warnings)
        } else {
          val overflowWarning =
            OverflowWarning(i, targetTyp, TruncateTo(k, targetWidth)().toString)
          Value(
            IntCst(truncate(i, targetTyp))(targetTyp),
            warnings + overflowWarning
          )
        }
      case ToSigned(e) =>
        val v = evalBigStep(inputs, stmData)(e)
        v.e.typ.asInstanceOf[TyUInt] match {
          case TyUInt(w) =>
            Value(v.e.rebuild(TySInt(w + 1)), v.warnings)
        }
      case ToUnsigned(e) =>
        val v = evalBigStep(inputs, stmData)(e)
        val i = v.e.asInstanceOf[IntCst].i
        v.e.typ.asInstanceOf[TySInt] match {
          case TySInt(w) =>
            // Just drop the sign bit
            assert(w >= 1)
            val typ = TyUInt(w - 1)
            if (typ.contains(i)) {
              Value(IntCst(i)(typ), v.warnings)
            } else {
              val overflowWarning =
                OverflowWarning(i, typ, ToUnsigned(v.e)().toString)
              Value(IntCst(truncate(i, typ))(typ), v.warnings + overflowWarning)
            }
        }
      case Bits(e) =>
        def toBits(e: Expr): Seq[BoolCst] = {
          e match {
            case b: BoolCst => Seq(b)
            case c: IntCst =>
              val w = c.typ.asInstanceOf[TyAnyInt].w
              // TODO: This seems like it would probably be quite slow.
              //       Make a faster version?
              // TODO: This duplicates functionality in mhir.gen.
              //       Share the code somehow?
              if (c.i < 0) {
                val bin = c.i.toBinaryString
                  .map(_ == '1')
                  .map(x => if (x) True else False)
                assert(bin.head == True)
                assert(bin.length == 64)
                val truncated = bin.takeRight(w)
                assert(truncated.head == True)
                truncated
              } else {
                val bin = c.i.toBinaryString
                  .map(_ == '1')
                  .map(x => if (x) True else False)
                assert(bin.length <= w)
                val padded = (0 until (w - bin.length)).map(_ => False) ++ bin
                if (c.typ.isInstanceOf[TySInt]) {
                  assert(padded.head == False)
                }
                padded
              }
            case k: FixCst              => toBits(C(k.numer)(k.typ.t))
            case Tuple(elems @ _*)      => elems.flatMap(toBits)
            case VecLiteral(elems @ _*) => elems.flatMap(toBits)
            case e =>
              throw new TypeError(s"Cannot find binary representation for $e")
          }
        }
        val Value(v, warnings) = evalBigStep(inputs, stmData)(e)
        val bits = toBits(v)
        Value(VecLiteral(bits: _*)(TyVec(TyBool, bits.length)), warnings)
      case InterpretAs(e, targetTyp) =>
        @tailrec
        def intFromBits(bits: Seq[BoolCst], k: Long): Long = {
          bits match {
            case Seq()               => k
            case Seq(False, bs @ _*) => intFromBits(bs, k << 1)
            case Seq(True, bs @ _*)  => intFromBits(bs, (k << 1) | 1)
          }
        }
        def fromBits(bits: Seq[BoolCst], targetTyp: Type): Expr = {
          targetTyp match {
            case TyBool =>
              bits.head
            case uint: TyUInt => C(intFromBits(bits, 0))(uint)
            case int: TySInt =>
              bits.head match {
                case False =>
                  C(intFromBits(bits, 0))(int)
                case True =>
                  // Start with -1 (which is all 1s in twos complement) to
                  // handle sign extension
                  C(intFromBits(bits, -1))(int)
              }
            case typ @ TyTuple(elems @ _*) =>
              val elemRangeStarts = elems.scanLeft(0)({ case (acc, t) =>
                val IntCst(w) = t.bitwidth
                acc + w.toInt
              })
              val elemRangeEnds = elemRangeStarts.drop(1)
              Tuple(
                elems
                  .zip(elemRangeStarts.zip(elemRangeEnds))
                  .map({ case (typ, (from, until)) =>
                    fromBits(bits.slice(from, until), typ)
                  }): _*
              )(typ)
            case vecTyp @ TyVec(elemTyp, IntCst(n)) =>
              val IntCst(wLong) = elemTyp.bitwidth
              val w = wLong.toInt
              VecLiteral(
                (0 until n.toInt)
                  .map({ i =>
                    fromBits(bits.slice(i * w, (i + 1) * w), elemTyp)
                  }): _*
              )(vecTyp)
          }
        }
        val Value(v: VecLiteral, warnings) = evalBigStep(inputs, stmData)(e)
        Value(
          fromBits(v.elems.map(_.asInstanceOf[BoolCst]), targetTyp),
          warnings
        )
      case LShift(e1, e2) =>
        val Value(n1, warn1) = evalBigStep(inputs, stmData)(e1)
        val Value(n2, warn2) = evalBigStep(inputs, stmData)(e2)
        (n1, n2) match {
          case (IntCst(k1), IntCst(k2)) =>
            val result = truncate(k1 << k2, n1.typ.asInstanceOf[TyAnyInt])
            Value(C(result)(n1.typ), warn1 ++ warn2)
          case (v1, v2) =>
            throw new TypeError(
              s"Operands of LShift evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case ARShift(e1, e2) =>
        val Value(n1, warn1) = evalBigStep(inputs, stmData)(e1)
        val Value(n2, warn2) = evalBigStep(inputs, stmData)(e2)
        (n1, n2) match {
          case (IntCst(k1), IntCst(k2)) =>
            val result = k1 >> k2
            val extendedResult = n1.typ.asInstanceOf[TyAnyInt] match {
              case TySInt(w) => signExtendToLong(result, w)
              case TyUInt(_) => result
            }
            Value(C(extendedResult)(n1.typ), warn1 ++ warn2)
          case (v1, v2) =>
            throw new TypeError(
              s"Operands of ARShift evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case LRShift(e1, e2) =>
        val Value(n1, warn1) = evalBigStep(inputs, stmData)(e1)
        val Value(n2, warn2) = evalBigStep(inputs, stmData)(e2)
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
        val Value(v1, warn1) = evalBigStep(inputs, stmData)(e1)
        val Value(v2, warn2) = evalBigStep(inputs, stmData)(e2)
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
        evalBigStep(inputs, stmData)(e) match {
          case Value(False, w) => Value(True, w)
          case Value(True, w)  => Value(False, w)
          case v =>
            throw new IllegalArgumentException(
              s"Operand of Not evaluated to $v. It must evaluate to a boolean."
            )
        }
      case And(terms @ _*) =>
        val termValues = terms.map(evalBigStep(inputs, stmData))
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
        val termValues = terms.map(evalBigStep(inputs, stmData))
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
        val Value(e1Val, e1Warn) = evalBigStep(inputs, stmData)(e1)
        val Value(e2Val, e2Warn) = evalBigStep(inputs, stmData)(e2)
        val result = if (areEqual(e1Val, e2Val)) True else False
        Value(result, e1Warn ++ e2Warn)
      case LessThan(e1, e2) =>
        val Value(e1Val, e1Warn) = evalBigStep(inputs, stmData)(e1)
        val Value(e2Val, e2Warn) = evalBigStep(inputs, stmData)(e2)
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
        val Value(cVal, cWarn) = evalBigStep(inputs, stmData)(c)
        cVal match {
          case True =>
            val Value(tVal, tWarn) = evalBigStep(inputs, stmData)(t)
            Value(tVal, cWarn ++ tWarn)
          case False =>
            val Value(fVal, fWarn) = evalBigStep(inputs, stmData)(f)
            Value(fVal, cWarn ++ fWarn)
          case v =>
            throw new IllegalArgumentException(
              s"Condition of Mux evaluated to $v. It must evaluate to a boolean."
            )
        }

      case Tuple(elems @ _*) =>
        val elemValues = elems.map(evalBigStep(inputs, stmData))
        val v = Tuple(elemValues.map(v => v.e): _*)()
        val warnings = elemValues.flatMap(v => v.warnings).toSet
        Value(v, warnings)
      case TupleAccess(t, IntCst(i)) =>
        // TODO: make the warnings more accurate by somehow only taking the
        //       ones that apply to the chosen element?
        val Value(tVal, tWarn) = evalBigStep(inputs, stmData)(t)
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
        val Value(nVal, nWarn) = evalBigStep(inputs, stmData)(n)
        nVal match {
          case IntCst(n) if n >= 0 =>
            val elemValues = (0 until n.toInt).map(i => {
              val inTyp = f.param.typ
              assert(inTyp.isInstanceOf[TyUInt])
              evalBigStep(inputs, stmData)(
                FunCall(f, IntCst(i)(inTyp))().tchk()
              )
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
        val Value(vVal, vWarn) = evalBigStep(inputs, stmData)(v)
        vVal match {
          case VecLiteral(elems @ _*) =>
            val Value(iVal, iWarn) = evalBigStep(inputs, stmData)(i)
            iVal match {
              case IntCst(i) if elems.indices.contains(i) =>
                Value(elems(i.toInt), vWarn ++ iWarn)
              case IntCst(i) =>
                val t = v.tchk().typ.asInstanceOf[TyVec].t
                val oobWarn = VecIndexOutOfBoundsWarning(elems.length, i)
                evalBigStep(inputs, stmData)(DefaultVal(t))
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
        val elemValues = vl.elems.map(e => evalBigStep(inputs, stmData)(e))
        val v = VecLiteral(elemValues.map(v => v.e): _*)(e.typ)
        val warnings = elemValues.flatMap(v => v.warnings).toSet
        Value(v, warnings)

      case s @ (_: StmLiteral | _: StmBuild | _: LetStm) =>
        Value(
          evalPipeline(
            StmPipeline(s, inputs = inputs, handshake = this.handshake),
            Seq(),
            invalidSteps = 0
          ),
          Set()
        )
      case sd @ StmData(s: Param) =>
        stmData.get(s) match {
          case None =>
            throw new IllegalArgumentException(
              s"Invalid use of ${StmData.getClass.getSimpleName} (e.g., outside a stream or with incorrect arguments)."
            )
          case Some(None) =>
            evalBigStep(Map(), Map())(DefaultVal(sd.typ))
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
      handshake: Boolean,
      maxInvalidSteps: Option[Int] = None,
      suppressWarnings: Boolean = false
  ): Evaluator = {
    new Evaluator(
      handshake = handshake,
      maxInvalidSteps = maxInvalidSteps.getOrElse(DefaultMaxInvalidSteps),
      suppressWarnings = suppressWarnings
    )
  }
}
