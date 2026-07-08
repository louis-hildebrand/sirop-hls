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
    val allDecls = (registerSignals(s.accumulators, options)
      ++ intermediateDecls(s.intermediates, options))
    val allPorts = defaultOutPort(s.data) +:
      (defaultInPorts(options) ++ producerPorts(s.producers))
    CustomVhdlComponent(
      name = name,
      inPorts = allPorts.collect({ case p: InPort => p }),
      outPorts = allPorts.collect({ case p: OutPort => p }),
      signals = allDecls.collect({ case s: Signal => s }),
      functions = allDecls.collect({ case f: VhdlFunction => f }),
      children = Seq()
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
      accumulators: Iterable[(Param, (Expr, Expr))],
      options: VhdlGeneratorOptions
  ): Seq[Signal] = {
    accumulators
      .map({
        case SingleWriteVector(x, z, cond, idx, write) =>
          // TODO: Deduplicate this code (both in handshake/ and nohandshake/)?
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
          val condVhdl = VhdlExprGenerator.toVhdl(cond)
          val idxVhdl = VhdlExprGenerator.toVhdl(idx)
          val writeVhdl = VhdlExprGenerator.toVhdl(write)
          Signal(
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
        case (x, (z, next)) =>
          assert(x.typ.isData)
          require(
            z.freeVars.isEmpty,
            s"Initial value for accumulator ${x.name} has free variables (${z.freeVars.toSeq.mkString(", ")})."
          )
          val initVhdl = z match {
            case _: Undefined => None
            case z            => Some(VhdlExprGenerator.toVhdl(z))
          }
          val nextVhdl = VhdlExprGenerator.toVhdl(next)
          Signal(
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
      .flatMap({ case (x, i) => i.toVhdlInArchitecture(x, options) })
      .toSeq
  }
}
