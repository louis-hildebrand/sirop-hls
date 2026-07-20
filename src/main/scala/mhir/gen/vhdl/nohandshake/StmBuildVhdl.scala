package mhir.gen.vhdl.nohandshake

import mhir.gen.vhdl._
import mhir.ir._

import scala.collection.immutable.ListMap

/** VHDL converter for [[mhir.ir.StmBuild]] when the handshake protocol is
  * disabled.
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
    require(!options.handshake)
    val allDecls = (registerSignals(s.accumulators, options)
      ++ intermediateDecls(s.intermediates, options))
    val allPorts = defaultOutPort(s.data) +:
      (defaultInPorts(options) ++ producerPorts(s.producers))
    val allChildren = s.intermediates
      .collect({ case (x, ip: IpBlockInst) =>
        ip.toVhdlEntityInst(x, options, enable = "'1'")
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
      InPort("go", VhdlStdLogic)
    )
  }

  /** Output ports that appear in all stream components.
    */
  private def defaultOutPort(data: Expr): OutPort = {
    val dataTyp = VhdlType(data.typ)
    val dataVhdl = VhdlExprGenerator.toVhdl(data)
    OutPort(
      name = "data",
      typ = VhdlStdLogicVec(dataTyp.bitWidth),
      assign = Some(VhdlConversionGenerator.toStdLogicVector(dataVhdl, dataTyp))
    )
  }

  /** Signals to represent the registers in this component.
    *
    * @param accumulators
    *   The equations (variable, initial value, next value) representing the
    *   registers.
    */
  private def registerSignals(
      accumulators: Iterable[(Param, Accumulator)],
      options: VhdlGeneratorOptions
  ): Seq[Signal] = {
    accumulators
      .map({ case (x, acc) =>
        acc.toVhdl(x, enable = s"sl2bool(go)", options = options)
      })
      .toSeq
  }

  private def producerPorts(
      producers: Map[Param, (Expr, Expr)]
  ): Seq[Port] = {
    producers
      .map({ case (x, (p, _)) =>
        assert(
          x == p,
          "the name of the producer variable should have been changed to match the stream itself"
            + s" (variable is $x, stream is $p)"
        )
        val dataType = VhdlType(x.typ.asInstanceOf[TyStm].t)
        val bitWidth = dataType.bitWidth
        InPort(name = x.name, typ = VhdlStdLogicVec(bitWidth))
      })
      .toSeq
  }

  private def intermediateDecls(
      intermediates: ListMap[Param, Intermediate],
      options: VhdlGeneratorOptions
  ): Seq[Decl] = {
    intermediates
      .map({ case (x, i) => i.toVhdlDecl(x, options) })
      .toSeq
  }
}
