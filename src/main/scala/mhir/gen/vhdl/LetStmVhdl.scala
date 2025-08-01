package mhir.gen.vhdl

import mhir.ir._

/** VHDL converter for [[LetStm]].
  */
private[vhdl] object LetStmVhdl {

  /** Converts a [[mhir.ir.LetStm]] to a VHDL component.
    *
    * @param let
    *   the expression to convert.
    * @param inputs
    *   variables representing the stream producers feeding into this node.
    * @param name
    *   the name to use for the VHDL component.
    */
  private[vhdl] def apply(
      let: LetStm,
      inputs: Set[Param],
      name: String
  ): CustomVhdlComponent = {
    val LetStm(x, in, out) = let

    for (y <- inputs) {
      val n = Tuple(in, out)().countOccurrences(y)
      if (n > 1) {
        throw new IllegalArgumentException(
          s"Input $y is used more than once."
            + " Consider using LetStm if you want the stream to have multiple consumers."
        )
      }
    }

    val producerElemTyp = in.typ.asInstanceOf[TyStm].t
    val outElemTyp = out.typ.asInstanceOf[TyStm].t

    val (xs, newOut) = makeVariantsOfFreeVar(x, out)
    val bufferByVar = xs.map(x => x -> Param("buf")().name).toMap

    val allPorts = {
      val externalProducerPorts = inputs.flatMap(x => {
        val stmTyp = x.typ.asInstanceOf[TyStm]
        val inputUsed = in.freeVars().contains(x) || out.freeVars().contains(x)
        val ready = if (inputUsed) None else Some("'0'")
        Seq(
          InPort(
            name = s"${x.name}_data",
            typ = VhdlType(stmTyp.t).toStdLogicVec
          ),
          InPort(name = s"${x.name}_valid", typ = VhdlStdLogic),
          OutPort(name = s"${x.name}_ready", typ = VhdlStdLogic, assign = ready)
        )
      })
      (defaultInPorts
        ++ defaultOutPorts(outElemTyp)
        ++ externalProducerPorts)
    }

    val allSignals = (
      defaultSignals(producerElemTyp, bufferNames = bufferByVar.values.toSeq)
        ++ bufferByVar.flatMap({ case (_, buf) =>
          bufferSignals(buf, producerElemTyp)
        })
    )

    val producerComponent = in match {
      case x: Param if inputs.contains(x) =>
        instantiateNoOpProducer(x, producerElemTyp)
      case s =>
        instantiateCustomProducer(s)
    }
    val consumerComponent = out match {
      case y: Param if y == x =>
        assert(bufferByVar.size == 1)
        instantiateNoOpBufferConsumer(bufferByVar.head._2, producerElemTyp)
      case y: Param if inputs.contains(y) =>
        instantiateNoOpInputConsumer(y)
      case _ =>
        instantiateCustomConsumer(newOut, bufferByVar)
    }
    val bufferComponents = bufferByVar
      .map({ case (_, buf) => instantiateBuffer(buf, producerElemTyp) })
      .toSeq

    val comment = ExprPrinter
      .display(let)
      .split("\n")
      .map(x => s"-- $x")
      .mkString("\n")
    CustomVhdlComponent(
      comment = comment,
      name = name,
      inPorts = allPorts.flatMap({
        case p: InPort => Some(p)
        case _         => None
      }),
      outPorts = allPorts.flatMap({
        case p: OutPort => Some(p)
        case _          => None
      }),
      signals = allSignals,
      functions = Seq(),
      children = producerComponent +: consumerComponent +: bufferComponents
    )
  }

  /** Makes a fresh copy of a given variable for each place it occurs free in
    * the given expression.
    *
    * @example
    *   `StmZip(s, s)` may be replaced with `StmZip(s1, s2)`, where `s`, `s1`,
    *   and `s2` are all different from each other.
    *
    * @param x
    *   the variable to freshen.
    * @param expr
    *   the expression in which to search for the variable.
    * @return
    *   all the fresh copies of the variable, along with the updated expression.
    */
  private def makeVariantsOfFreeVar(
      x: Param,
      expr: Expr
  ): (Set[Param], Expr) = {
    require(expr.hasType)
    val (xs, newE) = expr match {
      case y: Param if y == x =>
        val newX = x.freshCopy
        (Set(newX), newX)
      case Function(y, _) if y == x =>
        (Set[Param](), expr)
      case LetStm(y, _, _) if y == x =>
        (Set[Param](), expr)
      case s: StmBuild if s.accVars.contains(x) =>
        (Set[Param](), expr)
      case _ =>
        val (freshVars, newChildren) =
          expr.children.map(e => makeVariantsOfFreeVar(x, e)).unzip
        (freshVars.flatten.toSet, expr.rebuild(expr.typ, newChildren))
    }
    assert(newE.typ == expr.typ)
    (xs, newE)
  }

  /** Input ports that appear every time.
    */
  private def defaultInPorts: Seq[InPort] = {
    Seq(
      InPort("clk", VhdlStdLogic),
      InPort("ready", VhdlStdLogic)
    )
  }

  /** Output ports that appear every time.
    */
  private def defaultOutPorts(outElemTyp: Type): Seq[OutPort] = {
    Seq(
      OutPort(
        name = "data",
        typ = VhdlStdLogicVec(VhdlType(outElemTyp).bitWidth),
        assign = None
      ),
      OutPort(name = "valid", typ = VhdlStdLogic, assign = None)
    )
  }

  /** Signals that appear every time.
    *
    * @param producerElemTyp
    *   the type of the elements within the producer stream.
    * @param bufferNames
    *   the names of all the buffers.
    */
  private def defaultSignals(
      producerElemTyp: Type,
      bufferNames: Seq[String]
  ): Seq[Signal] = {
    val allConsumersReady = if (bufferNames.isEmpty) {
      "'1'"
    } else {
      bufferNames.map(buf => s"producer_to_${buf}_ready").mkString(" and ")
    }
    Seq(
      Signal(
        category = "Producer to buffers",
        name = "producer_data",
        typ = VhdlType(producerElemTyp).toStdLogicVec
      ),
      Signal(
        category = "Producer to buffers",
        name = "producer_valid",
        typ = VhdlStdLogic
      ),
      Signal(
        category = "Producer to buffers",
        name = "all_consumers_ready",
        typ = VhdlStdLogic,
        assignStmt = Some(s"all_consumers_ready <= $allConsumersReady;")
      ),
      Signal(
        category = "Producer to buffers",
        name = "producer_to_buf_valid",
        typ = VhdlStdLogic,
        assignStmt = Some(
          "producer_to_buf_valid <= all_consumers_ready and producer_valid;"
        )
      )
    )
  }

  /** Signals that are connected to the input or output of a given buffer and
    * are specific to that buffer.
    *
    * @param buf
    *   the buffer.
    * @param producerElemTyp
    *   the type of the elements within the producer stream.
    */
  private def bufferSignals(buf: String, producerElemTyp: Type): Seq[Signal] = {
    Seq(
      Signal(
        category = "Producer to buffers",
        name = s"producer_to_${buf}_ready",
        typ = VhdlStdLogic
      ),
      Signal(
        category = s"$buf to consumer",
        name = s"${buf}_to_consumer_data",
        typ = VhdlType(producerElemTyp).toStdLogicVec
      ),
      Signal(
        category = s"$buf to consumer",
        name = s"${buf}_to_consumer_ready",
        typ = VhdlStdLogic
      ),
      Signal(
        category = s"$buf to consumer",
        name = s"${buf}_to_consumer_valid",
        typ = VhdlStdLogic
      )
    )
  }

  /** Instantiates a component for the producer that simply forwards the given
    * input stream.
    *
    * @param x
    *   the input stream to pass along.
    * @param producerElemTyp
    *   the type of the elements within the producer stream.
    */
  private def instantiateNoOpProducer(
      x: Param,
      producerElemTyp: Type
  ): VhdlEntityInstantiation = {
    val bitWidth = VhdlType(producerElemTyp).bitWidth
    val portMap = PortMap(
      Map(
        "data_out" -> "producer_data",
        "valid_out" -> "producer_valid",
        "consumer_ready" -> "all_consumers_ready",
        "data_in" -> s"${x.name}_data",
        "valid_in" -> s"${x.name}_valid",
        "producer_ready" -> s"${x.name}_ready"
      )
    )
    VhdlEntityInstantiation("PRODUCER", StmNoOpComponent(bitWidth), portMap)
  }

  /** Instantiates a component for the producer which is not a no-op.
    *
    * @param s
    *   the expression for the producer.
    */
  private def instantiateCustomProducer(s: Expr): VhdlEntityInstantiation = {
    val inputs = s.freeVars()
    val component = AnyStmVhdl(s, inputs)
    val portMap = PortMap(
      Map(
        "clk" -> "clk",
        "data" -> "producer_data",
        "valid" -> "producer_valid",
        "ready" -> "all_consumers_ready"
      ) ++ inputs.flatMap(x =>
        Seq("data", "valid", "ready")
          .map(sig => s"${x.name}_$sig")
          .map(x => x -> x)
      )
    )
    VhdlEntityInstantiation("PRODUCER", component, portMap)
  }

  /** Instantiates a component for the consumer which simply forwards the value
    * from a buffer.
    *
    * @param buf
    *   the buffer.
    * @param producerElemTyp
    *   the type of the elements within the producer stream.
    */
  private def instantiateNoOpBufferConsumer(
      buf: String,
      producerElemTyp: Type
  ): VhdlEntityInstantiation = {
    val bitWidth = VhdlType(producerElemTyp).bitWidth
    val component = StmNoOpComponent(bitWidth)
    val portMap = PortMap(
      Map(
        "data_out" -> "data",
        "valid_out" -> "valid",
        "consumer_ready" -> "ready",
        "data_in" -> s"${buf}_to_consumer_data",
        "valid_in" -> s"${buf}_to_consumer_valid",
        "producer_ready" -> s"${buf}_to_consumer_ready"
      )
    )
    VhdlEntityInstantiation("CONSUMER", component, portMap)
  }

  /** Instantiates a component for the consumer which simply forwards an input
    * stream.
    *
    * @param x
    *   the input stream to pass along.
    */
  private def instantiateNoOpInputConsumer(
      x: Param
  ): VhdlEntityInstantiation = {
    val bitWidth = VhdlType(x.typ.asInstanceOf[TyStm].t).bitWidth
    val portMap = PortMap(
      Map(
        "data_out" -> "data",
        "valid_out" -> "valid",
        "consumer_ready" -> "ready",
        "data_in" -> s"${x.name}_data",
        "valid_in" -> s"${x.name}_valid",
        "producer_ready" -> s"${x.name}_ready"
      )
    )
    VhdlEntityInstantiation("CONSUMER", StmNoOpComponent(bitWidth), portMap)
  }

  /** Instantiates a component for the consumer which is not a no-op.
    *
    * @param s
    *   the expression for the consumer.
    * @param bufferByVar
    *   a map from variants of the variable bound by the [[mhir.ir.LetStm]] to
    *   their buffer.
    */
  private def instantiateCustomConsumer(
      s: Expr,
      bufferByVar: Map[Param, String]
  ): VhdlEntityInstantiation = {
    val innerInputs = s.freeVars()
    val component = AnyStmVhdl(s, innerInputs)
    val portMap = PortMap(
      Map(
        "clk" -> "clk",
        "data" -> "data",
        "ready" -> "ready",
        "valid" -> "valid"
      ) ++ innerInputs.flatMap(x => {
        bufferByVar.get(x) match {
          case Some(buf) =>
            // This input is the variable bound by this LetStm.
            // Get it from its buffer.
            Seq("data", "ready", "valid").map(sig =>
              s"${x.name}_$sig" -> s"${buf}_to_consumer_$sig"
            )
          case None =>
            // This input comes from outside the LetStm.
            Seq("data", "valid", "ready")
              .map(sig => s"${x.name}_$sig")
              .map(x => x -> x)
        }
      })
    )
    VhdlEntityInstantiation("CONSUMER", component, portMap)
  }

  /** Instantiates a component for a buffer.
    *
    * @param buf
    *   the name for the buffer.
    * @param producerElemTyp
    *   the type of the elements within the producer stream.
    */
  private def instantiateBuffer(
      buf: String,
      producerElemTyp: Type
  ): VhdlEntityInstantiation = {
    val bitWidth = VhdlType(producerElemTyp).bitWidth
    val component = StmBufferComponent(bitWidth)
    val portMap = PortMap(
      Map(
        "clk" -> "clk",
        "data_out" -> s"${buf}_to_consumer_data",
        "valid_out" -> s"${buf}_to_consumer_valid",
        "consumer_ready" -> s"${buf}_to_consumer_ready",
        "data_in" -> "producer_data",
        "valid_in" -> "producer_to_buf_valid",
        "producer_ready" -> s"producer_to_${buf}_ready"
      )
    )
    VhdlEntityInstantiation(buf.toUpperCase, component, portMap)
  }
}
