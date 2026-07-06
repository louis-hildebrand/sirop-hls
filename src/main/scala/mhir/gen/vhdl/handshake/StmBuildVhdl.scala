package mhir.gen.vhdl.handshake

import com.typesafe.scalalogging.Logger
import mhir.gen.vhdl._
import mhir.ir._

import scala.collection.immutable.ListMap

/** VHDL converter for [[mhir.ir.StmBuild]].
  */
private[vhdl] object StmBuildVhdl {

  private implicit val logger: Logger = Logger(getClass.getName)

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
    implicit val context: VhdlContext = VhdlContext(
      ListMap(
        (s.accumulators.map({ case (x, _) => x.name -> VhdlType(x.typ) })
          ++ s.producers.map({ case (x, _) =>
            val TyStm(elemTyp, _) = x.typ
            s"${x.name}_data_internal" -> VhdlType(elemTyp)
          })).toSeq
          .sortBy({ case (name, _) => name }): _*
      )
    )

    val (readyExprByProducer, readyExprDecls) = {
      val (rcp, decls) =
        s.producers
          .map({ case (x, (_, ready)) =>
            val VhdlExpr(vhdl, decls) = VhdlExprGenerator.exprToVhdl(ready)
            (x -> vhdl, decls)
          })
          .unzip
      (rcp.toMap, decls.flatten)
    }

    val (producerPorts, producerSignals) =
      producerInterface(readyExprByProducer)

    val allDecls = (
      defaultDecls(s.data, s.valid, options)
        ++ registerDecls(s.accumulators, options)
        ++ producerSignals
        ++ readyExprDecls
    )
    val allPorts = (
      defaultInPorts(options)
        ++ defaultOutPorts(VhdlType(s.data.typ))
        ++ producerPorts
    )
    val component = CustomVhdlComponent(
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
  private def defaultInPorts(options: VhdlGeneratorOptions): Seq[InPort] = {
    Seq(
      InPort(options.clock, VhdlStdLogic),
      InPort(options.reset, VhdlStdLogic),
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
      data: Expr,
      valid: Expr,
      options: VhdlGeneratorOptions
  )(implicit
      ctx: VhdlContext
  ): Seq[Decl] = {
    val VhdlExpr(validVhdl, validDecls) = VhdlExprGenerator.exprToVhdl(valid)
    val VhdlExpr(dataVhdl, dataDecls) = VhdlExprGenerator.exprToVhdl(data)
    val defaultSignals = Seq(
      Signal(
        category = "Handshake (output)",
        name = "data_internal",
        typ = VhdlType(data.typ),
        init = None,
        assignStmt = Some(s"data_internal <= $dataVhdl;"),
        // No register added here for data; that should be added explicitly as
        // an accumulator at an earlier compilation stage
        cond = None
      ),
      Signal(
        category = "Handshake (output)",
        name = "valid_internal",
        typ = VhdlBool,
        init = Some("false"),
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
            + " (not valid_internal or sl2bool(ready))"
            //  * All required producers must have valid data.
            + s" and all_required_producers_valid;"
        ),
        cond = None
      )
    )
    defaultSignals ++ validDecls ++ dataDecls
  }

  /** Signals to represent the registers in this component.
    *
    * @param registerEquations
    *   The equations (variable, initial value, next value) representing the
    *   registers.
    */
  private def registerDecls(
      registerEquations: Iterable[(Param, (Expr, Expr))],
      options: VhdlGeneratorOptions
  )(implicit ctx: VhdlContext): Seq[Decl] = {
    registerEquations
      .flatMap({
        case SingleWriteVector(x, z, cond, idx, write) =>
          // If the accumulator is a vector such that at most one element is
          // updated per step, then emit VHDL code which makes that clear.
          // This makes it possible for Quartus to recognize the accumulator as
          // a BRAM (assuming the other conditions are met, like only reading
          // one element per cycle).
          // Quartus cannot recognize a BRAM if we write to every element at
          // every cycle, even if all but one element keeps the same value.
          logger.debug(
            s"accumulator ${x.name} is a single-write vector"
              + s" (cond: $cond, idx: $idx, write: $write)"
          )
          assert(x.typ.isData)
          assert(z.isInstanceOf[Undefined])
          val VhdlExpr(condVhdl, condDecls) = VhdlExprGenerator.exprToVhdl(cond)
          val VhdlExpr(idxVhdl, idxDecls) = VhdlExprGenerator.exprToVhdl(idx)
          val VhdlExpr(writeVhdl, writeDecls) =
            VhdlExprGenerator.exprToVhdl(write)
          val sig = Signal(
            category = "Registers",
            name = x.name,
            typ = VhdlType(x.typ),
            init = None,
            assignStmt = Some(
              s"""if can_update_acc and ($condVhdl) then
                 |    ${x.name}(to_integer($idxVhdl)) <= $writeVhdl;
                 |end if;
                 |""".stripMargin
            ),
            cond = Some("true")
          )
          sig +: (condDecls ++ idxDecls ++ writeDecls)
        case (x, (z, next)) =>
          assert(x.typ.isData)
          require(
            z.freeVars.isEmpty,
            s"Initial value for accumulator ${x.name} has free variables (${z.freeVars.toSeq.mkString(", ")})."
          )
          val initVhdl = VhdlExprGenerator.valueToVhdl(z)
          val VhdlExpr(nextVhdl, nextDecls) = VhdlExprGenerator.exprToVhdl(next)
          val shouldReset = !z.isInstanceOf[Undefined]
          val sig = Signal(
            category = "Registers",
            name = x.name,
            typ = VhdlType(x.typ),
            init = Some(initVhdl),
            assignStmt = Some({
              val update =
                s"""if can_update_acc then
                 |    ${x.name} <= $nextVhdl;
                 |end if;
                 |""".stripMargin.stripTrailing
              val reset = if (shouldReset) {
                s"""if sl2bool(${options.reset}) then
                 |    ${x.name} <= $initVhdl;
                 |els
                 |""".stripMargin.stripTrailing
              } else {
                ""
              }
              s"$reset$update"
            }),
            cond = Some("true")
          )
          sig +: nextDecls
      })
      .toSeq
  }

  private def producerInterface(
      readyExprByProducer: Map[Param, String]
  ): (Seq[Port], Seq[Signal]) = {
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
    val (ports, signals) = readyExprByProducer
      .map({ case (x, readyExpr) =>
        val dataType = VhdlType(x.typ.asInstanceOf[TyStm].t)
        val bitWidth = dataType.bitWidth
        val rawDataConversion =
          VhdlConversionGenerator
            .fromStdLogicVector(s"${x.name}_data", dataType)
        val otherProducers = readyExprByProducer
          .filter({ case (y, _) => y != x })
        val allOthersValid = if (otherProducers.isEmpty) {
          "true"
        } else {
          otherProducers
            .map({ case (x, c) => s"(not ($c) or (${x.name}_valid_internal))" })
            .mkString(" and ")
        }
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
            category = s"Handshake (input producer $x)",
            name = s"arpv_except_${x.name}",
            typ = VhdlBool,
            assignStmt = Some(s"arpv_except_${x.name} <= $allOthersValid;")
          ),
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
                + s" ($readyExpr) and (not valid_internal or sl2bool(ready)) and arpv_except_${x.name};"
            )
          ),
          Signal(
            category = s"Handshake (input producer $x)",
            name = s"${x.name}_data_internal",
            typ = dataType,
            assignStmt = Some(s"${x.name}_data_internal <= $rawDataConversion;")
          )
        )
        (ports, signals)
      })
      .unzip
    (ports.flatten.toSeq, allRequiredProducersValid +: signals.flatten.toSeq)
  }
}
