package mhir.gen.vhdl

import mhir.ir._

/** VHDL converter for [[mhir.ir.StmBuild]].
  */
private[vhdl] object StmBuildVhdl {

  /** Converts a [[mhir.ir.StmBuild]] to a VHDL component.
    *
    * @param stm
    *   the stream to convert.
    * @param inputs
    *   variables representing the stream producers feeding into this node.
    * @param name
    *   the name to use for the VHDL component.
    */
  private[vhdl] def apply(
      stm: StmBuild,
      inputs: Set[Param],
      name: String
  ): CustomVhdlComponent = {
    // Rename accumulator variables such that the accumulator name and the
    // input variable name coincide.
    // Otherwise, the naming of ports and signals can get quite confusing.
    val s = {
      // Freshen all variables first to avoid clashes
      val s = stm.renameVars
      // There should be no name clashes unless a given input is used multiple
      // times, which is not allowed.
      val replacements = s.seedByVar.flatMap({
        case (x, z: Param) => Some(x -> z)
        case _             => None
      })
      s.renameVars(replacements)
    }

    val producerEquations = s.equations.flatMap({
      case (x, (p, ready)) if x.typ.isInstanceOf[TyStm] =>
        assert(p.isInstanceOf[Param])
        Some(x -> (p, ready))
      case _ => None
    })
    val registerEquations = s.equations.flatMap({
      case (x, (z, next)) if x.typ.isData => Some(x -> (z, next))
      case _                              => None
    })

    val (readyExprByProducer, readyExprDecls) = {
      val (rcp, decls) =
        producerEquations
          .map({ case (x, (_, ready)) =>
            val VhdlExpr(vhdl, decls) = VhdlExprGenerator.exprToVhdl(ready)
            (x -> vhdl, decls)
          })
          .unzip
      (rcp.toMap, decls.flatten)
    }
    // If waiting for multiple producers (e.g., in StmZip), don't raise the
    // ready signal until *all* required producers are ready
    // IMPORTANT: To avoid combinational loops, the producer's `valid` signal
    //            must NOT depend on the consumer's `ready` signal
    val allRequiredProducersValidSignal = {
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

    val (producerPorts, producerSignals) =
      producerInterface(readyExprByProducer)

    val allDecls = (
      defaultDecls(s.n, s.data, s.valid, allRequiredProducersValidSignal)
        ++ registerDecls(registerEquations)
        ++ producerSignals
        ++ readyExprDecls
    )
    val allPorts = (
      defaultInPorts
        ++ defaultOutPorts(VhdlType(s.data.typ))
        ++ producerPorts
    )
    val component = CustomVhdlComponent(
      expr = Some(s),
      name = name,
      inPorts = allPorts.flatMap({
        case p: InPort => Some(p)
        case _         => None
      }),
      outPorts = allPorts.flatMap({
        case p: OutPort => Some(p)
        case _          => None
      }),
      signals = allDecls.flatMap({
        case s: Signal => Some(s)
        case _         => None
      }),
      functions = allDecls.flatMap({
        case f: VhdlFunction => Some(f)
        case _               => None
      }),
      children = Seq()
    )

    component
  }

  /** Input ports that appear in all stream components.
    */
  private def defaultInPorts: Seq[InPort] = {
    Seq(
      InPort("clk", VhdlStdLogic),
      InPort("reset", VhdlStdLogic),
      InPort("ready", VhdlStdLogic)
    )
  }

  /** Output ports that appear in all stream components.
    */
  private def defaultOutPorts(dataType: VhdlType): Seq[OutPort] = {
    val dataInternalSlv =
      VhdlConversionGenerator.toStdLogicVector("data_internal", dataType)
    Seq(
      OutPort(
        name = "data",
        typ = VhdlStdLogicVec(dataType.bitWidth),
        assign = Some(dataInternalSlv)
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
  private def defaultDecls(
      n: Expr,
      data: Expr,
      valid: Expr,
      allRequiredProducersValidSig: Signal
  ): Seq[Decl] = {
    val VhdlExpr(_, nDecls) = VhdlExprGenerator.exprToVhdl(n)
    val VhdlExpr(validVhdl, validDecls) = VhdlExprGenerator.exprToVhdl(valid)
    val VhdlExpr(dataVhdl, dataDecls) = VhdlExprGenerator.exprToVhdl(data)
    val defaultSignals = Seq(
      Signal(
        category = "Handshake (output)",
        name = "data_internal",
        typ = VhdlType(data.typ),
        init = None,
        assignStmt = Some(s"data_internal <= $dataVhdl;"),
        cond = Some("transfer_ok or can_update_acc")
      ),
      Signal(
        category = "Handshake (output)",
        name = "valid_internal",
        typ = VhdlBool,
        init = Some("false"),
        assignStmt = Some(
          s"""if sl2bool(reset) then
             |    valid_internal <= false;
             |elsif transfer_ok or can_update_acc then
             |    valid_internal <= ($validVhdl) and ${allRequiredProducersValidSig.name};
             |end if;
             |""".stripMargin.stripTrailing
        ),
        cond = Some("true")
      ),
      Signal(
        category = "Handshake (output)",
        name = "transfer_ok",
        typ = VhdlBool,
        init = None,
        assignStmt = Some(
          "transfer_ok <= sl2bool(ready) and valid_internal;"
        ),
        cond = None
      ),
      Signal(
        category = "Handshake (output)",
        name = "can_update_acc",
        typ = VhdlBool,
        init = None,
        assignStmt = Some(
          "can_update_acc <="
          // This stream can keep working as long as the following conditions
          // are satisfied:
          //  * If it has some valid data to send, it cannot advance until
          //    that data is sent.
            + " (not valid_internal or transfer_ok)"
            //  * All required producers must have valid data.
            + s" and ${allRequiredProducersValidSig.name};"
        ),
        cond = None
      )
    )
    allRequiredProducersValidSig +: (defaultSignals ++ nDecls ++ validDecls ++ dataDecls)
  }

  /** Signals to represent the registers in this component.
    *
    * @param registerEquations
    *   The equations (variable, initial value, next value) representing the
    *   registers.
    */
  private def registerDecls(
      registerEquations: Iterable[(Param, (Expr, Expr))]
  ): Seq[Decl] = {
    registerEquations
      .flatMap({ case (x, (z, next)) =>
        assert(x.typ.isData)
        require(
          z.freeVars().isEmpty,
          s"Initial value for accumulator ${x.name} has free variables (${z.freeVars().toSeq.mkString(", ")})."
        )
        val initVhdl = VhdlExprGenerator.valueToVhdl(z)
        val VhdlExpr(nextVhdl, nextDecls) = VhdlExprGenerator.exprToVhdl(next)
        val sig = Signal(
          category = "Registers",
          name = x.name,
          typ = VhdlType(x.typ),
          init = Some(initVhdl),
          assignStmt = Some(
            s"""if sl2bool(reset) then
               |    ${x.name} <= $initVhdl;
               |elsif can_update_acc then
               |    ${x.name} <= $nextVhdl;
               |end if;
               |""".stripMargin.stripTrailing
          ),
          cond = Some("true")
        )
        sig +: nextDecls
      })
      .toSeq
  }

  private def producerInterface(
      readyExprByProducer: Map[Param, String]
  ): (Seq[Port], Seq[Signal]) = {
    val (ports, signals) = readyExprByProducer
      .map({ case (x, readyExpr) =>
        val dataType = VhdlType(x.typ.asInstanceOf[TyStm].t)
        val bitWidth = dataType.bitWidth
        val rawDataConversion =
          VhdlConversionGenerator
            .fromStdLogicVector(s"${x.name}_data", dataType)
        val ports = Seq(
          InPort(name = s"${x.name}_data", typ = VhdlStdLogicVec(bitWidth)),
          InPort(name = s"${x.name}_valid", typ = VhdlStdLogic),
          OutPort(
            name = s"${x.name}_ready",
            typ = VhdlStdLogic,
            assign = Some(s"bool2sl(${x.name}_ready_internal)")
          )
        )
        val signals = Seq(
          Signal(
            category = s"Handshake (input $x - external, used here)",
            name = s"${x.name}_valid_internal",
            typ = VhdlBool,
            assignStmt = Some(
              s"${x.name}_valid_internal <= sl2bool(${x.name}_valid);"
            )
          ),
          Signal(
            category = s"Handshake (input $x - external, used here)",
            name = s"${x.name}_ready_internal",
            typ = VhdlBool,
            assignStmt = Some(
              s"${x.name}_ready_internal <= ($readyExpr) and can_update_acc;"
            )
          ),
          Signal(
            category = s"Handshake (input $x - external, used here)",
            name = s"${x.name}_data_internal",
            typ = dataType,
            assignStmt = Some(s"${x.name}_data_internal <= $rawDataConversion;")
          )
        )
        (ports, signals)
      })
      .unzip
    (ports.flatten.toSeq, signals.flatten.toSeq)
  }
}
