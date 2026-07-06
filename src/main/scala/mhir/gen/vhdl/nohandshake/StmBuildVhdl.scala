package mhir.gen.vhdl.nohandshake

import com.typesafe.scalalogging.Logger
import mhir.gen.vhdl._
import mhir.ir._

import scala.collection.immutable.ListMap

/** VHDL converter for [[mhir.ir.StmBuild]] when the handshake protocol is
  * disabled.
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
    require(!options.handshake)
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

    val producers = s.producers.map({ case (_, (p, _)) => p }).toSet
    val (producerPorts, producerSignals) = producerInterface(producers)

    val allDecls = (
      defaultDecls(s.data)
        ++ registerDecls(s.accumulators, options)
        ++ producerSignals
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
      InPort("go", VhdlStdLogic)
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
      )
    )
  }

  /** Signals that appear in all stream components.
    */
  private def defaultDecls(data: Expr)(implicit ctx: VhdlContext): Seq[Decl] = {
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
      )
    )
    defaultSignals ++ dataDecls
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
              s"""if $condVhdl then
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
          val initVhdl = z match {
            case _: Undefined => None
            case z            => Some(VhdlExprGenerator.valueToVhdl(z))
          }
          val VhdlExpr(nextVhdl, nextDecls) = VhdlExprGenerator.exprToVhdl(next)
          val sig = Signal(
            category = "Registers",
            name = x.name,
            typ = VhdlType(x.typ),
            init = initVhdl,
            assignStmt = Some({
              val reset = initVhdl match {
                case None => ""
                case Some(z) =>
                  s"""if sl2bool(${options.reset}) then
                     |    ${x.name} <= $z;
                     |els
                     |""".stripMargin.stripTrailing
              }
              val update =
                s"""if sl2bool(go) then
                   |    ${x.name} <= $nextVhdl;
                   |end if;
                   |""".stripMargin.stripTrailing
              s"$reset$update"
            }),
            cond = Some("true")
          )
          sig +: nextDecls
      })
      .toSeq
  }

  private def producerInterface(
      producers: Set[Param]
  ): (Seq[Port], Seq[Signal]) = {
    val (ports, signals) = producers
      .map({ x =>
        val dataType = VhdlType(x.typ.asInstanceOf[TyStm].t)
        val bitWidth = dataType.bitWidth
        val rawDataConversion =
          VhdlConversionGenerator.fromStdLogicVector(x.name, dataType)
        val ports = InPort(name = x.name, typ = VhdlStdLogicVec(bitWidth))
        val signal = Signal(
          category = s"Handshake (input producer $x)",
          name = s"${x.name}_data_internal",
          typ = dataType,
          assignStmt = Some(s"${x.name}_data_internal <= $rawDataConversion;")
        )
        (ports, signal)
      })
      .unzip
    (ports.toSeq, signals.toSeq)
  }
}
