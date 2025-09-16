package mhir.gen.vhdl

import mhir.ir._
import mhir.ir.typecheck.TypeCheck

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
    val LetStm(bufSize, x, in, out) = let
    val IntCst(bufSizeVal) = mhir.ir.eval(bufSize)

    for (y <- inputs) {
      val n = Tuple(in, out)().countFreeOccurrences(y)
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

    val producerComponent = in match {
      case x: Param if inputs.contains(x) =>
        instantiateNoOpProducer(x, producerElemTyp)
      case s =>
        instantiateCustomProducer(s)
    }
    val consumerComponent = out match {
      case y: Param if y == x =>
        assert(xs.size == 1)
        instantiateNoOpBufferConsumer(xs.head.name, producerElemTyp)
      case y: Param if inputs.contains(y) =>
        val stmId = {
          val TyStm(t, n) = y.typ
          val s = Param("s")(TyStm(t, -1))
          StmBuild(
            n,
            StmData(s)(),
            True,
            Map[Param, (Expr, Expr)](s -> (y, True))
          )().tchk()
        }
        instantiateCustomConsumer(stmId, xs.map(_.name))
      case _ =>
        instantiateCustomConsumer(newOut, xs.map(_.name))
    }
    val bufferComponent = instantiateBuffer(
      bitWidth = VhdlType(producerElemTyp).bitWidth,
      bufSize = bufSizeVal.toInt,
      numConsumers = xs.size
    )

    CustomVhdlComponent(
      expr = let,
      name = name,
      inPorts = allPorts.flatMap({
        case p: InPort => Some(p)
        case _         => None
      }),
      outPorts = allPorts.flatMap({
        case p: OutPort => Some(p)
        case _          => None
      }),
      signals = signals(producerElemTyp, xs.map(_.name)),
      functions = Seq(),
      children = Seq(producerComponent, bufferComponent, consumerComponent)
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
      case LetStm(_, y, _, _) if y == x =>
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

  /** Signals in this component.
    *
    * @param producerElemTyp
    *   the type of the elements within the producer stream.
    * @param xs
    *   the variants of the variable bound by the [[mhir.ir.LetStm]].
    */
  private def signals(
      producerElemTyp: Type,
      xs: Set[String]
  ): Seq[Signal] = {
    val xsInOrder = if (xs.isEmpty) {
      Seq("fake")
    } else {
      xs.toSeq.sorted
    }
    val bitWidth = VhdlType(producerElemTyp).bitWidth
    (Seq(
      Signal(
        category = "Producer to buffer",
        name = "producer_to_buf_data",
        typ = VhdlType(producerElemTyp).toStdLogicVec
      ),
      Signal(
        category = "Producer to buffer",
        name = "producer_to_buf_valid",
        typ = VhdlStdLogic
      ),
      Signal(
        category = "Producer to buffer",
        name = "producer_to_buf_ready",
        typ = VhdlStdLogic
      ),
      Signal(
        category = "Buffer to consumers",
        name = "buf_to_consumers_data",
        typ = VhdlStdLogicVec(bitWidth * xsInOrder.size, IndexUp)
      ),
      Signal(
        category = "Buffer to consumers",
        name = "buf_to_consumers_valid",
        typ = VhdlStdLogicVec(xsInOrder.size, IndexUp)
      ),
      Signal(
        category = "Buffer to consumers",
        name = "buf_to_consumers_ready",
        typ = VhdlStdLogicVec(xsInOrder.size, IndexUp),
        assignStmt = Some({
          val rhs = xsInOrder.zipWithIndex
            .map({ case (x, i) => s"$i => buf_to_${x}_ready" })
            .mkString("(", ", ", ")")
          s"buf_to_consumers_ready <= $rhs;"
        })
      )
    )
      ++ xsInOrder.zipWithIndex.flatMap({ case (x, i) =>
        Seq(
          Signal(
            category = "Buffer to consumers",
            name = s"buf_to_${x}_data",
            typ = VhdlType(producerElemTyp).toStdLogicVec,
            assignStmt = {
              val lsb = i * bitWidth
              val msb = lsb + bitWidth - 1
              Some(s"buf_to_${x}_data <= buf_to_consumers_data($lsb to $msb);")
            }
          ),
          Signal(
            category = "Buffer to consumers",
            name = s"buf_to_${x}_valid",
            typ = VhdlStdLogic,
            assignStmt =
              Some(s"buf_to_${x}_valid <= buf_to_consumers_valid($i);")
          ),
          Signal(
            category = "Buffer to consumers",
            name = s"buf_to_${x}_ready",
            typ = VhdlStdLogic
          )
        )
      }))
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
        "c_data" -> "producer_to_buf_data",
        "c_valid" -> "producer_to_buf_valid",
        "c_ready" -> "producer_to_buf_ready",
        "p_data" -> s"${x.name}_data",
        "p_valid" -> s"${x.name}_valid",
        "p_ready" -> s"${x.name}_ready"
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
        "data" -> "producer_to_buf_data",
        "valid" -> "producer_to_buf_valid",
        "ready" -> "producer_to_buf_ready"
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
    * @param x
    *   the (updated) name of the variable bound by this [[mhir.ir.LetStm]].
    * @param producerElemTyp
    *   the type of the elements within the producer stream.
    */
  private def instantiateNoOpBufferConsumer(
      x: String,
      producerElemTyp: Type
  ): VhdlEntityInstantiation = {
    val bitWidth = VhdlType(producerElemTyp).bitWidth
    val component = StmNoOpComponent(bitWidth)
    val portMap = PortMap(
      Map(
        "c_data" -> "data",
        "c_valid" -> "valid",
        "c_ready" -> "ready",
        "p_data" -> s"buf_to_${x}_data",
        "p_valid" -> s"buf_to_${x}_valid",
        "p_ready" -> s"buf_to_${x}_ready"
      )
    )
    VhdlEntityInstantiation("CONSUMER", component, portMap)
  }

  /** Instantiates a component for the consumer which is not a no-op.
    *
    * @param s
    *   the expression for the consumer.
    * @param xs
    *   the variants of the variable bound by this [[mhir.ir.LetStm]].
    */
  private def instantiateCustomConsumer(
      s: Expr,
      xs: Set[String]
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
        if (xs.contains(x.name)) {
          // This input is the variable bound by this LetStm.
          // Get it from the buffer.
          Seq(
            s"${x.name}_data" -> s"buf_to_${x}_data",
            s"${x.name}_valid" -> s"buf_to_${x}_valid",
            s"${x.name}_ready" -> s"buf_to_${x}_ready"
          )
        } else {
          // This input comes from outside the LetStm.
          Seq(
            s"${x.name}_data" -> s"${x.name}_data",
            s"${x.name}_valid" -> s"${x.name}_valid",
            s"${x.name}_ready" -> s"${x.name}_ready"
          )
        }
      })
    )
    VhdlEntityInstantiation("CONSUMER", component, portMap)
  }

  private def instantiateBuffer(
      bitWidth: Int,
      bufSize: Int,
      numConsumers: Int
  ): VhdlEntityInstantiation = {
    val component = LetStmBufComponent(
      bitWidth = bitWidth,
      bufSize = bufSize,
      numConsumers = math.max(1, numConsumers)
    )
    val portMap = PortMap(
      Map(
        "clk" -> "clk",
        "p_data" -> "producer_to_buf_data",
        "p_valid" -> "producer_to_buf_valid",
        "p_ready" -> "producer_to_buf_ready",
        "c_data" -> "buf_to_consumers_data",
        "c_valid" -> "buf_to_consumers_valid",
        "c_ready" -> "buf_to_consumers_ready"
      )
    )
    VhdlEntityInstantiation("BUF", component, portMap)
  }
}
