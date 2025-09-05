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
    val LetStm(x, in, out) = let

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
      children = Seq(producerComponent, consumerComponent)
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
    (Seq(
      Signal(
        category = "Buffer",
        name = "buf",
        typ = VhdlType(producerElemTyp).toStdLogicVec,
        assignStmt = Some("buf <= producer_data;"),
        cond = Some("sl2bool(can_update and producer_valid)")
      ),
      Signal(
        category = "Buffer",
        name = "can_update",
        typ = VhdlStdLogic,
        assignStmt = {
          val canUpdate = if (xs.isEmpty) {
            "'1'"
          } else {
            xs.map(x => s"${x}_will_have_read").mkString(" and ")
          }
          Some(s"can_update <= $canUpdate;")
        }
      ),
      Signal(
        category = "Producer to buffer",
        name = "producer_data",
        typ = VhdlType(producerElemTyp).toStdLogicVec
      ),
      Signal(
        category = "Producer to buffer",
        name = "producer_valid",
        typ = VhdlStdLogic
      ),
      Signal(
        category = "Producer to buffer",
        name = "producer_ready",
        typ = VhdlStdLogic,
        assignStmt = Some(s"producer_ready <= can_update;")
      )
    )
      ++ xs.flatMap(x =>
        Seq(
          Signal(
            category = "Buffer",
            name = s"${x}_has_read",
            typ = VhdlStdLogic,
            init = Some("'1'"),
            assignStmt = {
              Some(s"""if sl2bool(can_update and producer_valid) then
                      |    ${x}_has_read <= '0';
                      |else
                      |    ${x}_has_read <= ${x}_will_have_read;
                      |end if;
                      |""".stripMargin.stripTrailing)
            },
            cond = Some("true")
          ),
          Signal(
            category = "Buffer",
            name = s"${x}_will_have_read",
            typ = VhdlStdLogic,
            assignStmt = Some(
              s"${x}_will_have_read <= ${x}_has_read or buf_to_${x}_ready;"
            )
          ),
          Signal(
            category = "Buffer to consumers",
            name = s"buf_to_${x}_data",
            typ = VhdlType(producerElemTyp).toStdLogicVec,
            assignStmt = Some(s"buf_to_${x}_data <= buf;")
          ),
          Signal(
            category = "Buffer to consumers",
            name = s"buf_to_${x}_valid",
            typ = VhdlStdLogic,
            assignStmt = Some(s"buf_to_${x}_valid <= not ${x}_has_read;")
          ),
          Signal(
            category = "Buffer to consumers",
            name = s"buf_to_${x}_ready",
            typ = VhdlStdLogic
          )
        )
      ))
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
        "consumer_ready" -> "producer_ready",
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
        "ready" -> "producer_ready"
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
        "data_out" -> "data",
        "valid_out" -> "valid",
        "consumer_ready" -> "ready",
        "data_in" -> s"buf_to_${x}_data",
        "valid_in" -> s"buf_to_${x}_valid",
        "producer_ready" -> s"buf_to_${x}_ready"
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
}
