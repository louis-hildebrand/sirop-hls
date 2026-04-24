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
      name: String,
      options: VhdlGeneratorOptions
  ): CustomVhdlComponent = {
    require(!options.handshake)
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

    implicit val context: VhdlContext = VhdlContext(
      ListMap(
        s.equations
          .map({
            case (x, _) if x.typ.isData =>
              x.name -> VhdlType(x.typ)
            case (x, _) if x.typ.isInstanceOf[TyStm] =>
              s"${x.name}_data_internal" -> VhdlType(
                x.typ.asInstanceOf[TyStm].t
              )
            case _ => ???
          })
          .toSeq
          .sortBy({ case (name, _) => name }): _*
      )
    )

    val producers = s.equations
      .flatMap({
        case (x, (p: Param, _)) if x.typ.isInstanceOf[TyStm] =>
          assert(p.isInstanceOf[Param])
          Some(p)
        case (x, _) if x.typ.isInstanceOf[TyStm] =>
          ???
        case _ => None
      })
      .toSet
    val registerEquations = s.equations.flatMap({
      case (x, (z, next)) if x.typ.isData => Some(x -> (z, next))
      case _                              => None
    })

    val (producerPorts, producerSignals) = producerInterface(producers)

    val allDecls = (
      defaultDecls(s.n, s.data)
        ++ registerDecls(registerEquations, options)
        ++ producerSignals
    )
    val allPorts = (
      defaultInPorts(options)
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
  private def defaultInPorts(options: VhdlGeneratorOptions): Seq[InPort] = {
    Seq(
      InPort(options.clock, VhdlStdLogic),
      InPort(options.reset, VhdlStdLogic)
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
  private def defaultDecls(
      n: Expr,
      data: Expr
  )(implicit ctx: VhdlContext): Seq[Decl] = {
    val VhdlExpr(_, nDecls) = VhdlExprGenerator.exprToVhdl(n)
    val VhdlExpr(dataVhdl, dataDecls) = VhdlExprGenerator.exprToVhdl(data)
    val defaultSignals = Seq(
      Signal(
        category = "Handshake (output)",
        name = "data_internal",
        typ = VhdlType(data.typ),
        init = None,
        assignStmt = Some(s"data_internal <= $dataVhdl;"),
        cond = Some("true")
      )
    )
    defaultSignals ++ nDecls ++ dataDecls
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
