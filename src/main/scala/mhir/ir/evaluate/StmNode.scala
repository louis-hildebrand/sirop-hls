package mhir.ir
package evaluate

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.TypeError

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

/** Companion object for [[StmNode]].
  */
object StmNode {

  /** If a stream takes this many steps without producing a valid element,
    * assume it is stuck in an infinite loop.
    */
  private[ir] val MaxInvalidSteps = 10000

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
