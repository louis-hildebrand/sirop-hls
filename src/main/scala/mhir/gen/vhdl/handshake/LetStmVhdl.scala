package mhir.gen.vhdl.handshake

import mhir.gen.vhdl._
import mhir.ir._

/** VHDL converter for [[mhir.ir.LetStm]].
  */
object LetStmVhdl {
  def apply(
      input: Param,
      bufSize: Int,
      outputs: Seq[Param],
      name: String,
      options: VhdlGeneratorOptions
  ): CustomVhdlComponent = {
    val TyStm(elemTyp, _) = input.typ
    val bitWidth = VhdlType(elemTyp).bitWidth
    val bufferComponent = {
      val component = LetStmBufComponent(
        bitWidth = VhdlType(elemTyp).bitWidth,
        bufSize = bufSize,
        numConsumers = math.max(1, outputs.size)
      )
      val portMap = PortMap(
        Map(
          "clk" -> options.clock,
          "reset" -> options.reset,
          "p_data" -> s"${input.name}_data",
          "p_valid" -> s"${input.name}_valid",
          "p_ready" -> s"${input.name}_ready",
          "c_data" -> "buf_to_consumers_data",
          "c_valid" -> "buf_to_consumers_valid",
          "c_ready" -> "buf_to_consumers_ready"
        )
      )
      VhdlEntityInstantiation("BUF", component, portMap)
    }
    val signals = {
      Seq(
        Signal(
          category = "Buffer to consumers",
          name = "buf_to_consumers_data",
          typ = VhdlStdLogicVec(bitWidth * math.max(1, outputs.size), IndexUp)
        ),
        Signal(
          category = "Buffer to consumers",
          name = "buf_to_consumers_valid",
          typ = VhdlStdLogicVec(math.max(1, outputs.size), IndexUp)
        ),
        Signal(
          category = "Buffer to consumers",
          name = "buf_to_consumers_ready",
          typ = VhdlStdLogicVec(math.max(1, outputs.size), IndexUp),
          assignStmt = Some({
            val rhs = if (outputs.nonEmpty) {
              outputs.zipWithIndex
                .map({ case (x, i) => s"$i => ${x}_ready" })
                .mkString("(", ", ", ")")
            } else {
              "\"1\""
            }
            s"buf_to_consumers_ready <= $rhs;"
          })
        )
      )
    }
    val ports: Seq[Port] = {
      Seq(
        InPort(options.clock, VhdlStdLogic),
        InPort(options.reset, VhdlStdLogic),
        // Handshake with producer
        InPort(s"${input.name}_data", VhdlType(elemTyp).toStdLogicVec),
        InPort(s"${input.name}_valid", VhdlStdLogic),
        OutPort(s"${input.name}_ready", VhdlStdLogic, None)
      ) ++ outputs.zipWithIndex.flatMap({ case (x, i) =>
        Seq(
          // Handshake with consumers
          OutPort(
            s"${x.name}_data",
            VhdlType(elemTyp).toStdLogicVec, {
              val lsb = i * bitWidth
              val msb = lsb + bitWidth - 1
              Some(s"buf_to_consumers_data($lsb to $msb)")
            }
          ),
          OutPort(
            s"${x.name}_valid",
            VhdlStdLogic,
            Some(s"buf_to_consumers_valid($i)")
          ),
          InPort(s"${x.name}_ready", VhdlStdLogic)
        )
      })
    }
    CustomVhdlComponent(
      name = name,
      inPorts =
        ports.filter(_.isInstanceOf[InPort]).map(_.asInstanceOf[InPort]),
      outPorts =
        ports.filter(_.isInstanceOf[OutPort]).map(_.asInstanceOf[OutPort]),
      signals = signals,
      functions = Seq(),
      children = Seq(bufferComponent)
    )
  }
}
