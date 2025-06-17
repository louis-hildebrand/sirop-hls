package ir

import scala.annotation.tailrec
import scala.language.{existentials, implicitConversions}

/** The state of a node in the pipeline.
  */
sealed trait NodeState

/** The node has no more data to send.
  */
object Empty extends NodeState

/** The node is waiting for some of its input nodes.
  */
object Stalled extends NodeState

/** The node has taken one step but the output was invalid (i.e.,
  * <code>None</code>).
  */
object Invalid extends NodeState

/** The node has a valid output.
  *
  * @param v
  *   The output.
  */
case class Valid(v: Expr) extends NodeState

/** The node is deadlocked and will never produce any more outputs.
  */
case class Deadlocked(reasons: Seq[DeadlockReason]) extends NodeState

/** The parts of a stream-producing component which do not change at runtime.
  *
  * @param data
  *   The expression for this node's output.
  * @param valid
  *   An expression indicating whether the data is valid.
  * @param nextByDataAcc
  *   An expression for the next value for each data accumulator.
  * @param readyByInput
  *   For each input, an expression saying whether this node will consume that
  *   input.
  * @param typ
  *   The stream type.
  */
case class StmNodeHardware(
    data: Expr,
    valid: Expr,
    nextByDataAcc: Map[Param, Expr],
    readyByInput: Map[Param, Expr],
    typ: TyStm
)

/** One stream-producing component in a pipeline. This is similar to
  * <code>StmBuild</code>, but with extra data to allow evaluation.
  *
  * @param hw
  *   The static information of this component.
  * @param n
  *   The length of this stream.
  * @param currentValByDataAcc
  *   For each data accumulator, the current value.
  * @param inputs
  *   The producer nodes that feed into this node.
  * @param state
  *   The current state of this node.
  */
case class StmNode(
    hw: StmNodeHardware,
    n: Long,
    currentValByDataAcc: Map[Param, Expr],
    inputs: Map[Param, StmNode],
    state: NodeState,
    invalidSteps: Int
) {

  /** Compute the new state of the pipeline after one cycle.
    */
  def step(): StmNode = {
    this.transferData(true).computeNextOutputs()
  }

  /** At each ready/valid interface, update the nodes according to whether or
    * not data was transferred.
    *
    * @param ready
    *   Whether the consumer is ready to receive output.
    */
  private def transferData(ready: Boolean): StmNode = {
    val requiredProducers =
      getRequiredProducers(this.inputs, this.currentValByDataAcc.toMap)
    val transferOk = this.state.isInstanceOf[Valid] && ready
    val canStep = this.state != Empty && (transferOk || (!this.state
      .isInstanceOf[Valid] && allRequiredProducersValid(requiredProducers)))
    val newInputs = this.inputs.map({ case (x, node) =>
      val ready = canStep && requiredProducers.contains(x)
      x -> node.transferData(ready)
    })
    val newDataAccumulators = if (canStep) {
      val subs =
        currentValByDataAcc ++ getDataByInput(this.inputs, requiredProducers)
      this.hw.nextByDataAcc.map({ case (x, nextExpr) =>
        x -> eval(nextExpr.subPreserveType(subs))
      })
    } else {
      this.currentValByDataAcc
    }
    StmNode(
      hw = this.hw,
      n = if (transferOk) this.n - 1 else this.n,
      currentValByDataAcc = newDataAccumulators,
      inputs = newInputs,
      state = Stalled,
      invalidSteps = this.invalidSteps
    )
  }

  /** Traverse the pipeline source-to-sink, computing node outputs along the
    * way.
    */
  private def computeNextOutputs(): StmNode = {
    assert(
      this.state == Stalled,
      "transferData() must be called before computeNextOutputs(), and transferData() should set all nodes' states to Stalled"
    )

    val newInputs =
      this.inputs.map({ case (x, node) => x -> node.computeNextOutputs() })

    val requiredProducers =
      getRequiredProducers(newInputs, this.currentValByDataAcc.toMap)

    val deadlockReasons = checkDeadlocks(requiredProducers)
    val newState = if (this.n <= 0) {
      Empty
    } else if (deadlockReasons.nonEmpty) {
      Deadlocked(deadlockReasons.toSeq)
    } else if (!allRequiredProducersValid(requiredProducers)) {
      Stalled
    } else {
      val subs =
        currentValByDataAcc ++ getDataByInput(newInputs, requiredProducers)
      val evaluatedValid = eval(this.hw.valid.subPreserveType(subs))
      evaluatedValid match {
        case True =>
          val evaluatedData = eval(this.hw.data.subPreserveType(subs))
          Valid(evaluatedData)
        case False =>
          Invalid
        case v =>
          throw new TypeError(
            s"Valid signal evaluated to $v. It must evaluate to a boolean."
          )
      }
    }
    val newInvalidSteps = newState match {
      case _: Valid => 0
      case Invalid  => this.invalidSteps + 1
      case _        => this.invalidSteps
    }
    StmNode(
      hw = this.hw,
      n = this.n,
      currentValByDataAcc = this.currentValByDataAcc,
      inputs = newInputs,
      state = newState,
      invalidSteps = newInvalidSteps
    )
  }

  private def getRequiredProducers(
      producers: Map[Param, StmNode],
      currentValByDataAcc: Map[Expr, Expr]
  ): Map[Param, StmNode] = {
    producers
      .filter({ case (x, _) =>
        val readyExpr = this.hw.readyByInput(x)
        if (readyExpr.contains(classOf[StmData])) {
          throw new IllegalArgumentException(
            s"${StmData.getClass.getSimpleName} cannot be used in a ready expression."
          )
        }
        eval(readyExpr.subPreserveType(currentValByDataAcc)) match {
          case True  => true
          case False => false
          case v =>
            throw new TypeError(
              s"Ready signal evaluated to $v. It must evaluate to a boolean."
            )
        }
      })
  }

  private def getDataByInput(
      inputs: Map[Param, StmNode],
      requiredProducers: Map[Param, StmNode]
  ): Map[Expr, Expr] = {
    inputs.map({ case (x, node) =>
      if (requiredProducers.contains(x)) {
        // If `canStep` is true, then all required producers must have
        // valid data.
        val v = node.state.asInstanceOf[Valid].v
        StmData(x)() -> v
      } else {
        val typ = x.typ.asInstanceOf[TyStm].t
        StmData(x)() -> Default(typ).lower()
      }
    })
  }

  private def allRequiredProducersValid(
      requiredProducers: Map[Param, StmNode]
  ): Boolean = {
    requiredProducers.forall({ case (_, n) => n.state.isInstanceOf[Valid] })
  }

  private def checkDeadlocks(
      requiredProducers: Map[Param, StmNode]
  ): Set[DeadlockReason] = {
    val reasons: Set[DeadlockReason] = requiredProducers
      .flatMap({ case (_, s) =>
        s.state match {
          case Empty                        => Set(EmptyStreamRead)
          case Deadlocked(reasons)          => reasons
          case _: Valid | Invalid | Stalled => Set()
        }
      })
      .toSet
    if (this.invalidSteps >= StmNode.MaxInvalidSteps) reasons + TooManySteps
    else reasons
  }
}

object StmNode {

  /** If a stream takes this many steps without producing a valid element,
    * assume it is stuck in an infinite loop.
    */
  private[ir] val MaxInvalidSteps = 1000

  def apply(s: StmBuild): StmNode = {
    StmNode.init(s).computeNextOutputs()
  }

  private def init(s: StmBuild): StmNode = {
    require(
      s.typ != Missing,
      s"Stream must be type checked before it can be converted to a ${StmNode.getClass.getSimpleName}."
    )
    val n = eval(s.n) match {
      case IntCst(n) => n
      case e =>
        throw new TypeError(
          s"Stream length evaluated to $e. It must evaluate to an integer."
        )
    }
    val (inputStmBuilds, accumulators) =
      s.equations.partition({ case (x, _) => x.typ.isInstanceOf[TyStm] })
    val (inputs, readyByInput) = inputStmBuilds
      .map({
        case (x, (z: StmBuild, ready)) => (x -> StmNode.init(z), x -> ready)
        case (x, (z: StmLiteral, ready)) =>
          val stm = stmLiteralToStmBuild(z)
          (x -> StmNode.init(stm), x -> ready)
        case (x, (z, _)) =>
          throw new IllegalArgumentException(
            s"Initial value for stream-valued accumulator ${x.name} is $z. Expected a StmBuild."
          )
      })
      .unzip
    StmNode(
      hw = StmNodeHardware(
        data = s.data,
        valid = s.valid,
        nextByDataAcc = accumulators.map({ case (x, (_, next)) => x -> next }),
        readyByInput = readyByInput.toMap,
        typ = s.typ.asInstanceOf[TyStm]
      ),
      n = n,
      currentValByDataAcc = accumulators.map({ case (x, (z, _)) =>
        x -> eval(z)
      }),
      inputs = inputs.toMap,
      state = Stalled,
      invalidSteps = 0
    )
  }
}

trait Eval {

  def eval(e: Expr): Expr = {
    val Value(v, warnings) = evalBigStep(e.tchk().lower())
    if (warnings.isEmpty) {
      v
    } else {
      throw UndefinedValException(warnings)
    }
  }

  @tailrec
  private def evalPipeline(
      s: StmNode,
      elems: Seq[Expr],
      numInvalid: Int
  ): Expr = {
    s.state match {
      case Empty =>
        StmLiteral(elems.reverse: _*)(s.hw.typ)
      case Valid(v) => evalPipeline(s.step(), v +: elems, numInvalid = 0)
      case Stalled | Invalid =>
        evalPipeline(s.step(), elems, numInvalid = numInvalid + 1)
      case Deadlocked(rs) => throw new DeadlockError(rs)
    }
  }

  private[ir] def stmLiteralToStmBuild(s: StmLiteral): StmBuild = {
    val t = s.typ.asInstanceOf[TyStm].t
    val n = s.typ.asInstanceOf[TyStm].n
    val idxTyp = TyAnyInt.tightest(0, s.elems.length)
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
        val v = evalBigStep(e)
        val i = v.e.asInstanceOf[IntCst].i
        val typ = v.e.typ.asInstanceOf[TyAnyInt]
        assert(
          targetWidth <= typ.w,
          s"truncate target width must be less than or equal to original width (target $targetWidth, original ${typ.w})"
        )
        val targetTyp = typ.withWidth(targetWidth)
        Value(IntCst(truncate(i, targetTyp))(targetTyp), v.warnings)
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
            Value(IntCst(truncate(i, typ))(typ), v.warnings)
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

      case s: StmBuild =>
        Value(evalPipeline(StmNode(s), Seq(), 0), Set())
      case s: StmLiteral =>
        Value(evalPipeline(StmNode(stmLiteralToStmBuild(s)), Seq(), 0), Set())
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
