package mhir.gen.vhdl.handshake

import mhir.gen.vhdl._
import mhir.ir._

/** VHDL converter for [[mhir.ir.StmBuild]].
  */
private[vhdl] object StmBuildVhdl {

  /** Converts a [[mhir.ir.StmBuild]] to a VHDL component.
    *
    * @param s
    *   the stream to convert.
    * @param inputs
    *   variables representing the stream producers feeding into this node.
    * @param name
    *   the name to use for the VHDL component.
    */
  private[vhdl] def apply(
      s: GenStmBuild,
      inputs: Set[Param],
      name: String,
      options: VhdlGeneratorOptions
  ): CustomVhdlComponent = {
    require(options.handshake)

    val intermediateDecls = s.intermediates
      .map({ case (x, i) => i.toVhdlDecl(Target(x), options) })

    val allDecls = (
      defaultSignals(s.valid, options)
        ++ registerSignals(s.accumulators, options)
        ++ producerSignals(s.producers)
        ++ intermediateDecls
    )
    val allPorts = (
      defaultInPorts(options)
        ++ defaultOutPorts(s.data)
        ++ producerPorts(s.producers)
    )
    val allChildren = s.intermediates
      .collect({ case (x, ip: IpBlockInst) =>
        ip.toVhdlEntityInst(x, options, enable = "bool2sl(can_update_acc)")
      })
      .toSeq
    CustomVhdlComponent(
      name = name,
      inPorts = allPorts.collect({ case p: InPort => p }),
      outPorts = allPorts.collect({ case p: OutPort => p }),
      signals = allDecls.collect({ case s: Signal => s }),
      functions = allDecls.collect({ case f: VhdlFunction => f }),
      children = allChildren
    )
  }

  /** Input ports that appear in all stream components.
    */
  private def defaultInPorts(options: VhdlGeneratorOptions): Seq[InPort] = {
    Seq(
      InPort(options.clock, VhdlStdLogic),
      InPort(options.reset, VhdlStdLogic),
      InPort("ready", VhdlStdLogic)
    )
  }

  /** Output ports that appear in all stream components.
    */
  private def defaultOutPorts(data: Expr): Seq[OutPort] = {
    val dataTyp = VhdlType(data.typ)
    val dataVhdl = VhdlExprGenerator.toVhdl(data)
    Seq(
      OutPort(
        name = "data",
        typ = VhdlStdLogicVec(dataTyp.bitWidth),
        assign = Some(
          VhdlConversionGenerator.toStdLogicVector(dataVhdl, dataTyp)
        )
      ),
      OutPort(
        name = "valid",
        typ = VhdlStdLogic,
        assign = Some("bool2sl(valid_internal)")
      )
    )
  }

  /** Signals that appear in all stream components.
    */
  private def defaultSignals(
      valid: Expr,
      options: VhdlGeneratorOptions
  ): Seq[Signal] = {
    val validVhdl = VhdlExprGenerator.toVhdl(valid)
    Seq(
      // No register added here for data; that should be added explicitly as
      // an accumulator at an earlier compilation stage
      Signal(
        category = "Handshake (output)",
        name = "valid_internal",
        typ = VhdlBool,
        assignStmt = Some(
          s"""if sl2bool(${options.reset}) then
             |    valid_internal <= false;
             |elsif transfer_ok or can_update_acc then
             |    valid_internal <= ($validVhdl) and all_required_producers_valid;
             |end if;
             |""".stripMargin.stripTrailing
        ),
        // A register is needed here because you can't really represent the
        // valid register as an accumulator; it needs to be set to false when
        // we send data to the consumer, even if our producers do not currently
        // have valid data
        cond = Some("true")
      ),
      Signal(
        category = "Handshake (output)",
        name = "transfer_ok",
        typ = VhdlBool,
        assignStmt = Some(
          "transfer_ok <= sl2bool(ready) and valid_internal;"
        ),
        cond = None
      ),
      Signal(
        category = "Handshake (output)",
        name = "can_update_acc",
        typ = VhdlBool,
        assignStmt = Some(
          "can_update_acc <="
          // This stream can keep working as long as the following conditions
          // are satisfied:
          //  * If it has some valid data to send, it cannot advance until
          //    that data is sent.
            + " (not valid_internal or sl2bool(ready))"
            //  * All required producers must have valid data.
            + s" and all_required_producers_valid;"
        ),
        cond = None
      )
    )
  }

  /** Signals to represent the registers in this component.
    *
    * @param registerEquations
    *   The equations (variable, initial value, next value) representing the
    *   registers.
    */
  private def registerSignals(
      registerEquations: Iterable[(Param, Accumulator)],
      options: VhdlGeneratorOptions
  ): Seq[Signal] = {
    registerEquations
      .map({ case (x, acc) =>
        acc.toVhdl(Target(x), enable = "can_update_acc", options = options)
      })
      .toSeq
  }

  private def producerPorts(producers: Map[Param, (Expr, Expr)]): Seq[Port] = {
    producers
      .flatMap({ case (x, (p, _)) =>
        assert(
          x == p,
          "the name of the producer variable should have been changed to match the stream itself"
            + s" (variable is $x, stream is $p)"
        )
        val dataType = VhdlType(x.typ.asInstanceOf[TyStm].t)
        val bitWidth = dataType.bitWidth
        Seq(
          InPort(name = s"${x.name}_data", typ = VhdlStdLogicVec(bitWidth)),
          InPort(name = s"${x.name}_valid", typ = VhdlStdLogic),
          OutPort(
            name = s"${x.name}_ready",
            typ = VhdlStdLogic,
            assign = Some(s"bool2sl(${x.name}_ready_internal)")
          )
        )
      })
      .toSeq
  }

  private def producerSignals(
      producers: Map[Param, (Expr, Expr)]
  ): Seq[Signal] = {
    val readyExprByProducer = producers
      .map({ case (x, (_, ready)) => x -> VhdlExprGenerator.toVhdl(ready) })
    // If waiting for multiple producers (e.g., in StmZip), don't raise the
    // ready signal until *all* required producers are ready
    // IMPORTANT: To avoid combinational loops, the producer's `valid` signal
    //            must NOT depend on the consumer's `ready` signal
    val allRequiredProducersValid = {
      val arpvExpr = if (readyExprByProducer.isEmpty) {
        "true"
      } else {
        readyExprByProducer
          .map({ case (x, c) => s"(not ($c) or (${x.name}_valid_internal))" })
          .mkString(" and ")
      }
      Signal(
        category = "Handshake (inputs)",
        name = "all_required_producers_valid",
        typ = VhdlBool,
        assignStmt = Some(s"all_required_producers_valid <= $arpvExpr;")
      )
    }
    val signals = producers
      .flatMap({ case (x, (_, ready)) =>
        val otherProducers = readyExprByProducer
          .filter({ case (y, _) => y != x })
        val allOthersValid = if (otherProducers.isEmpty) {
          "true"
        } else {
          otherProducers
            .map({ case (x, c) => s"(not ($c) or (${x.name}_valid_internal))" })
            .mkString(" and ")
        }
        val readyVhdl = VhdlExprGenerator.toVhdl(ready)
        Seq(
          Signal(
            category = s"Handshake (input producer $x)",
            name = s"arpv_except_${x.name}",
            typ = VhdlBool,
            assignStmt = Some(s"arpv_except_${x.name} <= $allOthersValid;")
          ),
          // The ${x.name}_data_internal signal will be added separately; an
          // earlier pass should have added intermediates for sdata(p)
          Signal(
            category = s"Handshake (input producer $x)",
            name = s"${x.name}_valid_internal",
            typ = VhdlBool,
            assignStmt = Some(
              s"${x.name}_valid_internal <= sl2bool(${x.name}_valid);"
            )
          ),
          Signal(
            category = s"Handshake (input producer $x)",
            name = s"${x.name}_ready_internal",
            typ = VhdlBool,
            assignStmt = Some(
              s"${x.name}_ready_internal <="
                + s" ($readyVhdl) and (not valid_internal or sl2bool(ready)) and arpv_except_${x.name};"
            )
          )
        )
      })
      .toSeq
    allRequiredProducersValid +: signals
  }
}
