package ir

import scala.annotation.tailrec
import scala.language.implicitConversions

class DeadlockError(val reasons: Seq[DeadlockReason])
    extends RuntimeException(s"Deadlock (${reasons.mkString(", ")}).")

sealed trait DeadlockReason

/** The stream is definitely deadlocked because it tried to read from an empty
  * stream.
  */
object EmptyStreamRead extends DeadlockReason

/** The stream <i>appears</i> to be deadlocked because it took too many steps
  * without producing any valid outputs.
  */
object TooManySteps extends DeadlockReason

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
    n: Int,
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
    val canStep = transferOk || (!this.state
      .isInstanceOf[Valid] && allRequiredProducersValid(requiredProducers))
    val newInputs = this.inputs.map({ case (x, node) =>
      val ready = canStep && requiredProducers.contains(x)
      x -> node.transferData(ready)
    })
    val newDataAccumulators = if (canStep) {
      val subs =
        currentValByDataAcc ++ getDataByInput(this.inputs, requiredProducers)
      this.hw.nextByDataAcc.map({ case (x, nextExpr) =>
        x -> evalBigStep(nextExpr.subPreserveType(subs))
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
      val evaluatedValid = evalBigStep(this.hw.valid.subPreserveType(subs))
      evaluatedValid match {
        case True =>
          val evaluatedData = evalBigStep(this.hw.data.subPreserveType(subs))
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
        evalBigStep(readyExpr.subPreserveType(currentValByDataAcc)) match {
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
        x -> evalBigStep(z)
      }),
      inputs = inputs.toMap,
      state = Stalled,
      invalidSteps = 0
    )
  }
}

trait Eval {

  def eval(e: Expr): Expr = evalBigStep(e.tchk().lower())

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
    val v = Param("v")()
    val i = Param("i")()
    val t = s.typ.asInstanceOf[TyStm].t
    val n = s.typ.asInstanceOf[TyStm].n
    StmBuild(
      s.elems.length,
      VecAccess(v, i)(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (0, i + 1),
        v -> (VecLiteral(s.elems: _*)(TyVec(t, n)), v)
      )
    )().tchk().lower().asInstanceOf[StmBuild]
  }

  private[ir] def evalBigStep(e: Expr): Expr = {
    val v: Expr = e match {
      case x: Param =>
        throw new IllegalArgumentException(
          s"Free variable ${x.name}. Terms must be closed."
        )
      case f: Function => f
      case FunCall(f, arg) =>
        evalBigStep(f) match {
          case Function(x, body) =>
            // Leave stream inputs unevaluated
            val a = arg.typ match {
              case _: TyStm => arg
              case _        => evalBigStep(arg)
            }
            evalBigStep(body.subPreserveType(x -> a))
          case v =>
            throw new IllegalArgumentException(
              s"Left-hand side of function application evaluated to $v. It must evaluate to a function."
            )
        }

      case IntCst(n) => IntCst(n)
      case Sum(terms @ _*) =>
        val termValues = terms.map(e => evalBigStep(e))
        if (termValues.forall(e => e.isInstanceOf[IntCst])) {
          val xs = termValues.map(e => e.asInstanceOf[IntCst].i)
          IntCst(xs.sum)
        } else {
          throw new IllegalArgumentException(
            s"Terms of Sum evaluated to $termValues. They must each evaluate to an integer."
          )
        }
      case Prod(factors @ _*) =>
        val factorValues = factors.map(e => evalBigStep(e))
        if (factorValues.forall(e => e.isInstanceOf[IntCst])) {
          val xs = factorValues.map(e => e.asInstanceOf[IntCst].i)
          IntCst(xs.product)
        } else {
          throw new IllegalArgumentException(
            s"Terms of Prod evaluated to $factorValues. They must each evaluate to an integer."
          )
        }
      case Div(e1, e2) =>
        val numer = evalBigStep(e1)
        val denom = evalBigStep(e2)
        (numer, denom) match {
          case (IntCst(_), IntCst(0)) =>
            throw new IllegalArgumentException("Division by zero.")
          case (IntCst(n1), IntCst(n2)) => IntCst(n1 / n2)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Div evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case Mod(e1, e2) =>
        val numer = evalBigStep(e1)
        val denom = evalBigStep(e2)
        (numer, denom) match {
          case (IntCst(_), IntCst(0)) =>
            throw new IllegalArgumentException("Modulo by zero.")
          case (IntCst(n1), IntCst(n2)) => IntCst(n1 % n2)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Mod evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }

      case True  => True
      case False => False
      case Not(e) =>
        evalBigStep(e) match {
          case False => True
          case True  => False
          case v =>
            throw new IllegalArgumentException(
              s"Operand of Not evaluated to $v. It must evaluate to a boolean."
            )
        }
      case And(terms @ _*) =>
        val termValues = terms.map(e => evalBigStep(e))
        if (termValues.forall(e => e.isInstanceOf[BoolCst])) {
          if (termValues.contains(False)) False else True
        } else {
          throw new IllegalArgumentException(
            s"Terms of And evaluated to $termValues. They must all evaluate to booleans."
          )
        }
      case Or(terms @ _*) =>
        val termValues = terms.map(e => evalBigStep(e))
        if (termValues.forall(e => e.isInstanceOf[BoolCst])) {
          if (termValues.contains(True)) True else False
        } else {
          throw new IllegalArgumentException(
            s"Terms of Or evaluated to $termValues. They must all evaluate to booleans."
          )
        }
      case Equal(e1, e2) =>
        (evalBigStep(e1), evalBigStep(e2)) match {
          // Bool
          case (v1: BoolCst, v2) => v1 == v2
          case (v1, v2: BoolCst) => v1 == v2
          // Int
          case (v1: IntCst, v2) => v1 == v2
          case (v1, v2: IntCst) => v1 == v2
          // Tuple
          case (v1: Tuple, v2) => evalTupleEqual(v1, v2)
          case (v1, v2: Tuple) => evalTupleEqual(v2, v1)
          // Vector
          case (v1: VecLiteral, v2) => evalVecEqual(v1, v2)
          case (v1, v2: VecLiteral) => evalVecEqual(v2, v1)
          // It doesn't really make sense to compare functions or streams in
          // hardware
          case (e1, e2) =>
            throw new IllegalArgumentException(s"Cannot compare $e1 with $e2.")
        }
      case LessThan(e1, e2) =>
        (evalBigStep(e1), evalBigStep(e2)) match {
          case (IntCst(n1), IntCst(n2)) =>
            if (n1 < n2) True else False
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of LessThan evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case Mux(c, t, f) =>
        // Note that both branches are always evaluated!
        val tVal = evalBigStep(t)
        val fVal = evalBigStep(f)
        evalBigStep(c) match {
          case True  => tVal
          case False => fVal
          case v =>
            throw new IllegalArgumentException(
              s"Condition of Mux evaluated to $v. It must evaluate to a boolean."
            )
        }

      case Tuple(elems @ _*) => Tuple(elems.map(e => evalBigStep(e)): _*)()
      case TupleAccess(t, i) =>
        evalBigStep(t) match {
          case Tuple(elems @ _*) =>
            evalBigStep(i) match {
              case IntCst(i) if elems.indices.contains(i) => elems(i)
              case IntCst(i) =>
                throw new TypeError(
                  s"Index $i is out of bounds for a tuple with ${elems.length} elements."
                )
              case v =>
                throw new IllegalArgumentException(
                  s"Index of tuple access evaluated to $v. It must evaluate to an integer."
                )
            }
          case v =>
            throw new IllegalArgumentException(
              s"Tuple of tuple access evaluated to $v. It must evaluate to a tuple literal."
            )
        }

      case v @ VecBuild(n, f) =>
        assert(v.typ.isInstanceOf[TyVec])
        evalBigStep(n) match {
          case IntCst(n) if n >= 0 =>
            VecLiteral(
              (0 until n).map(i =>
                evalBigStep(FunCall(f, IntCst(i))().tchk())
              ): _*
            )(v.typ)
          case n =>
            throw new IllegalArgumentException(
              s"Vector length $n. Vectors must have non-negative integer length."
            )
        }
      case VecAccess(v, i) =>
        evalBigStep(v) match {
          case VecLiteral(elems @ _*) =>
            evalBigStep(i) match {
              case IntCst(i) if elems.indices.contains(i) => elems(i)
              case _: IntCst =>
                val t = v.tchk().typ.asInstanceOf[TyVec].t
                evalBigStep(Default(t).lower())
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
      case v: VecLiteral => v

      case s: StmBuild => evalPipeline(StmNode(s), Seq(), 0)
      case s: StmLiteral =>
        evalPipeline(StmNode(stmLiteralToStmBuild(s)), Seq(), 0)
      case _: StmData =>
        throw new IllegalArgumentException(
          s"Invalid use of ${StmData.getClass.getSimpleName} (e.g., outside a stream or with incorrect arguments)."
        )
      case StmNextK(s, k) =>
        evalBigStep(s) match {
          case s: StmLiteral =>
            evalBigStep(k) match {
              case IntCst(k) if k <= 0 => s
              case IntCst(k) =>
                val t = s.typ.asInstanceOf[TyStm].t
                val newElems = s.elems.drop(k)
                StmLiteral(newElems: _*)(TyStm(t, newElems.length))
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
    val typedV = v.tchk()
    assert(
      typedV.typ ~~= e.typ,
      s"evaluation should preserve type (expected ${e.typ}, got ${typedV.typ})"
    )
    typedV
  }

  private def evalTupleEqual(v1: Tuple, v2: Expr): Expr = {
    val v2Elems = v2 match {
      case Tuple(elems @ _*) => elems
      case _ =>
        v1.elems.indices.map(i => evalBigStep(TupleAccess(v2, i)().tchk()))
    }
    if (v1.elems.length != v2Elems.length) {
      False
    } else {
      evalBigStep(
        v1.elems
          .zip(v2Elems)
          .foldLeft(True: Expr)({ case (a, (e1, e2)) => a && (e1 === e2) })
      )
    }
  }

  private def evalVecEqual(v1: VecLiteral, v2: Expr): Expr = {
    val v2Elems = v2 match {
      case VecLiteral(elems @ _*) => elems
      case _ =>
        v1.elems.indices.map(i => evalBigStep(VecAccess(v2, i)().tchk()))
    }
    if (v1.elems.length != v2Elems.length) {
      False
    } else {
      evalBigStep(
        v1.elems
          .zip(v2Elems)
          .foldLeft(True: Expr)({ case (a, (e1, e2)) => a && (e1 === e2) })
      )
    }
  }
}
