package mhir.gen.vhdl

import mhir.ir._

/** VHDL converter for [[mhir.ir.StmBuild]].
  */
private[vhdl] object StmBuildVhdl {
  private val InputStreamRules: String = (
    "Input streams must only appear"
      + " (1) directly as the initial value for an accumulator or"
      + " (2) inside the initial value for an accumulator as long as that initial value is a StmBuild or a LetStm."
      + " Furthermore, an input stream cannot be used more than once."
  )

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
    val whereUsedByInput = findWhereUsedByInput(stm, inputs)

    // TODO: Define a new trait representing producer streams?

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

    val (registerEquations, internalProducerEquations) = {
      val (r, ip) = s.equations.toSeq
        .map({ case eqn @ (x, (z, _)) =>
          (x.typ, z) match {
            // CASE 1: registers
            case (t, _) if t.isData => (Some(eqn), None)
            // CASE 3: external producer streams
            case (_: TyStm, z: Param) =>
              assert(inputs.contains(z))
              assert(z == x)
              (None, None)
            // CASE 2: internal producer streams
            case (_: TyStm, _) => (None, Some(eqn))
            // CASE 4: others
            case (t, z) =>
              throw new IllegalArgumentException(
                s"Accumulator variable $x cannot be mapped to a register, an internal producer stream, or an external producer stream."
                  + s" It has type $t and initial value $z."
              )
          }
        })
        .unzip
      (r.flatten, ip.flatten)
    }

    val (readyCondByProducer, readyCondDecls) = {
      val (rcp, decls) =
        (
          // CASE 1 (internal producers).
          // Check how the stream accumulator is updated.
          internalProducerEquations
            .map({ case (x, (_, next)) =>
              val VhdlExpr(vhdl, decls) = VhdlExprGenerator.exprToVhdl(next)
              (x -> vhdl, decls)
            })
            ++ whereUsedByInput.flatMap({
              // CASE 2 (external producers used here).
              // Check how the stream accumulator is updated.
              case (x, Here) =>
                val next = s.nextByVar(x)
                val VhdlExpr(vhdl, decls) = VhdlExprGenerator.exprToVhdl(next)
                Some(x -> vhdl, decls)
              // CASE 3 (external producers used nowhere).
              // Never read from it.
              case (x, Nowhere) => Some(x -> "false", Seq())
              // CASE 4 (external producers used in a sub-component).
              // The sub-component will take care of it.
              case _ => None
            })
        ).unzip
      (rcp.toMap, decls.flatten)
    }

    // If waiting for multiple producers (e.g., in StmZip), don't raise the
    // ready signal until *all* required producers are ready
    // IMPORTANT: To avoid combinational loops, the producer's `valid` signal
    //            must NOT depend on the consumer's `ready` signal
    val allRequiredProducersValidSignal = {
      val arpvExpr = if (readyCondByProducer.isEmpty) {
        "true"
      } else {
        readyCondByProducer
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

    val (internalProducers, internalProducerSignals) =
      internalProducerInterface(
        internalProducerEquations.map({ case (x, (z, _)) => x -> z }).toMap,
        readyCondByProducer
      )
    val (externalProducerPorts, externalProducerSignals) =
      externalProducerInterface(whereUsedByInput, readyCondByProducer)

    val comment = ExprPrinter
      .display(s)
      .split("\n")
      .map(x => s"-- $x")
      .mkString("\n")
    val allDecls = (
      defaultDecls(s.n, s.data, s.valid, allRequiredProducersValidSignal)
        ++ registerDecls(registerEquations)
        ++ internalProducerSignals
        ++ externalProducerSignals
        ++ readyCondDecls
    )
    val allPorts = (
      defaultInPorts
        ++ defaultOutPorts(VhdlType(s.data.typ))
        ++ externalProducerPorts
    )
    val component = CustomVhdlComponent(
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
      signals = allDecls.flatMap({
        case s: Signal => Some(s)
        case _         => None
      }),
      functions = allDecls.flatMap({
        case f: VhdlFunction => Some(f)
        case _               => None
      }),
      children = internalProducers.map({ case (c, pm) =>
        VhdlEntityInstantiation(c.name, c, pm)
      })
    )

    component
  }

  private def findWhereUsedByInput(
      stm: StmBuild,
      inputs: Set[Param]
  ): Map[Param, WhereUsed] = {
    // Freshen variables so that no accumulator variables are called the same
    // thing as an input variable.
    // This simplifies the checks for improper use of an input.
    val s = stm.renameVars
    inputs
      .map(x => {
        if (s.data.freeVars().union(s.valid.freeVars()).contains(x)) {
          throw new IllegalArgumentException(
            s"Input $x occurs free in the stream output. $InputStreamRules"
          )
        }
        if (s.n.freeVars().contains(x)) {
          throw new IllegalArgumentException(
            s"Input $x occurs free in the stream length. $InputStreamRules"
          )
        }
        var u: WhereUsed = Nowhere
        for ((acc, (seed, next)) <- s.equations) {
          if (next.freeVars().contains(x)) {
            throw new IllegalArgumentException(
              s"Input $x occurs free in the `next` expression for accumulator $acc. $InputStreamRules"
            )
          }
          (seed, u) match {
            case (x2: Param, Nowhere) if x2 == x =>
              u = Here
            case (s, Nowhere) if s.freeVars().contains(x) =>
              u = InChild
            case _ if seed.freeVars().contains(x) =>
              assert(u != Nowhere)
              throw new IllegalArgumentException(
                s"Input $x is used more than once. $InputStreamRules"
              )
            case _ => ()
          }
        }
        x -> u
      })
      .toMap
  }

  /** Input ports that appear in all stream components.
    */
  private def defaultInPorts: Seq[InPort] = {
    Seq(
      InPort("clk", VhdlStdLogic),
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
    val VhdlExpr(nVhdl, nDecls) = VhdlExprGenerator.exprToVhdl(n)
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
          s"valid_internal <= ($validVhdl) and ${allRequiredProducersValidSig.name};"
        ),
        cond = Some("transfer_ok or can_update_acc")
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
          assignStmt = Some(s"${x.name} <= $nextVhdl;"),
          cond = Some("can_update_acc")
        )
        sig +: nextDecls
      })
      .toSeq
  }

  private def internalProducerInterface(
      producers: Map[Param, Expr],
      readyByProducer: Map[Param, String]
  ): (Seq[(CustomVhdlComponent, PortMap)], Seq[Signal]) = {
    val children = producers
      .map({ case (x, z) =>
        val inputsUsedInThisChild = z.freeVars()
        val component = AnyStmVhdl(z, inputsUsedInThisChild)
        val portMap = PortMap(
          Map(
            "clk" -> "clk",
            "valid" -> s"${x.name}_valid",
            "ready" -> s"${x.name}_ready",
            "data" -> s"${x.name}_data"
          )
            ++ inputsUsedInThisChild
              .flatMap(y =>
                Seq(s"${y.name}_data", s"${y.name}_valid", s"${y.name}_ready")
              )
              .map(y => y -> y)
              .toMap
        )
        (component, portMap)
      })
      .toSeq
    val childSignals =
      producers
        .flatMap({ case (x, _) =>
          val dataType = x.typ.asInstanceOf[TyStm].t
          val bitWidth = getBitWidth(dataType)
          val rawDataConversion =
            VhdlConversionGenerator.fromStdLogicVector(
              s"${x.name}_data",
              VhdlType(dataType)
            )
          val ready = readyByProducer(x)
          Seq(
            Signal(
              category = s"Handshake (input $x - internal)",
              name = s"${x.name}_valid",
              typ = VhdlStdLogic
            ),
            Signal(
              category = s"Handshake (input $x - internal)",
              name = s"${x.name}_valid_internal",
              typ = VhdlBool,
              assignStmt = Some(
                s"${x.name}_valid_internal <= sl2bool(${x.name}_valid);"
              )
            ),
            Signal(
              category = s"Handshake (input $x - internal)",
              name = s"${x.name}_ready",
              typ = VhdlStdLogic,
              assignStmt = Some(
                s"${x.name}_ready <= bool2sl(${x.name}_ready_internal);"
              )
            ),
            Signal(
              category = s"Handshake (input $x - internal)",
              name = s"${x.name}_ready_internal",
              typ = VhdlBool,
              assignStmt = Some(
                s"${x.name}_ready_internal <= ($ready) and can_update_acc;"
              )
            ),
            Signal(
              category = s"Handshake (input $x - internal)",
              name = s"${x.name}_data",
              typ = VhdlStdLogicVec(bitWidth)
            ),
            Signal(
              category = s"Handshake (input $x - internal)",
              name = s"${x.name}_data_internal",
              typ = VhdlType(dataType),
              assignStmt =
                Some(s"${x.name}_data_internal <= $rawDataConversion;")
            )
          )
        })
        .toSeq
    (children, childSignals)
  }

  private def externalProducerInterface(
      whereUsedByInput: Map[Param, WhereUsed],
      readyCondByProducer: Map[Param, String]
  ): (Seq[Port], Seq[Signal]) = {
    val (ports, signals) = whereUsedByInput
      .map({
        case (x, InChild) =>
          val bitWidth = VhdlType(x.typ.asInstanceOf[TyStm].t).bitWidth
          val ports = Seq(
            InPort(name = s"${x.name}_data", typ = VhdlStdLogicVec(bitWidth)),
            InPort(name = s"${x.name}_valid", typ = VhdlStdLogic),
            OutPort(
              name = s"${x.name}_ready",
              typ = VhdlStdLogic,
              assign = None
            )
          )
          (ports, Seq())
        case (x, Nowhere) =>
          val bitWidth = VhdlType(x.typ.asInstanceOf[TyStm].t).bitWidth
          val readyCond = readyCondByProducer(x)
          val ports = Seq(
            InPort(name = s"${x.name}_data", typ = VhdlStdLogicVec(bitWidth)),
            InPort(name = s"${x.name}_valid", typ = VhdlStdLogic),
            OutPort(
              name = s"${x.name}_ready",
              typ = VhdlStdLogic,
              assign = Some(s"bool2sl($readyCond)")
            )
          )
          val signals = Seq(
            // Needed for the all_required_producers_valid signal
            Signal(
              category = s"Handshake (input $x - external, unused)",
              name = s"${x.name}_valid_internal",
              typ = VhdlBool,
              assignStmt =
                Some(s"${x.name}_valid_internal <= sl2bool(${x.name}_valid);")
            )
          )
          (ports, signals)
        case (x, Here) =>
          val dataType = VhdlType(x.typ.asInstanceOf[TyStm].t)
          val bitWidth = dataType.bitWidth
          val readyCond = readyCondByProducer(x)
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
                s"${x.name}_ready_internal <= ($readyCond) and can_update_acc;"
              )
            ),
            Signal(
              category = s"Handshake (input $x - external, used here)",
              name = s"${x.name}_data_internal",
              typ = dataType,
              assignStmt =
                Some(s"${x.name}_data_internal <= $rawDataConversion;")
            )
          )
          (ports, signals)
      })
      .unzip
    (ports.flatten.toSeq, signals.flatten.toSeq)
  }

  private def getBitWidth(t: Type): Int = VhdlType(t).bitWidth
}
