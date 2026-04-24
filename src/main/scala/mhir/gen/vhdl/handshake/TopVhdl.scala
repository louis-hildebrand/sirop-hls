package mhir.gen.vhdl
package handshake

import mhir.ir._

object TopVhdl {

  /** Generate the top-level VHDL entity.
    *
    * @param f
    *   the function defining the accelerator's behaviour.
    */
  def apply(f: Expr, options: VhdlGeneratorOptions): CustomVhdlComponent = {
    require(options.handshake)
    val pipe = FlattenPipeline(f, options)
    val childComponents = {
      val sbuilds = pipe.sbuilds.zipWithIndex.map({
        case ((x, s: StmBuild), i) =>
          val inputsOfS = s.freeVars
          val component = StmBuildVhdl(
            s,
            inputsOfS,
            name = s"sbuild_${i + 1}",
            options = options
          )
          val portMap = PortMap(
            Map(
              options.clock -> options.clock,
              options.reset -> options.reset,
              // Handshake with consumer
              s"data" -> s"${x.name}_data",
              s"valid" -> s"${x.name}_valid",
              s"ready" -> s"${x.name}_ready"
            ) ++ inputsOfS.flatMap({ x =>
              // Handshake with producers
              Map(
                s"${x.name}_data" -> s"${x.name}_data",
                s"${x.name}_valid" -> s"${x.name}_valid",
                s"${x.name}_ready" -> s"${x.name}_ready"
              )
            })
          )
          VhdlEntityInstantiation(component.name, component, portMap)
      })
      val lets = pipe.lets.zipWithIndex.map({ case ((x, bufSize, xs), i) =>
        val component = LetStmVhdl(
          x,
          bufSize,
          xs.toSeq.sortBy(_.name),
          name = s"letstm_${i + 1}",
          options = options
        )
        val portMap = PortMap(
          Map(
            options.clock -> options.clock,
            options.reset -> options.reset,
            // Handshake with consumer
            s"${x.name}_data" -> s"${x.name}_data",
            s"${x.name}_valid" -> s"${x.name}_valid",
            s"${x.name}_ready" -> s"${x.name}_ready"
          ) ++ xs.flatMap({ x =>
            // Handshake with producers
            Map(
              s"${x.name}_data" -> s"${x.name}_data",
              s"${x.name}_valid" -> s"${x.name}_valid",
              s"${x.name}_ready" -> s"${x.name}_ready"
            )
          })
        )
        VhdlEntityInstantiation(component.name, component, portMap)
      })
      sbuilds ++ lets
    }
    val signals = {
      val sbuildOutputs = pipe.sbuilds.flatMap({ case (x, _) =>
        Seq(
          Signal(
            category = "",
            name = s"${x.name}_data",
            typ = VhdlType(x.typ.asInstanceOf[TyStm].t).toStdLogicVec
          ),
          Signal(
            category = "",
            name = s"${x.name}_valid",
            typ = VhdlStdLogic
          ),
          Signal(
            category = "",
            name = s"${x.name}_ready",
            typ = VhdlStdLogic,
            assignStmt = if (x == pipe.sink) {
              Some(s"${x.name}_ready <= ${topReady(options.outName)};")
            } else {
              None
            }
          )
        )
      })
      val letOutputs = pipe.lets.flatMap({ case (_, _, xs) =>
        xs.flatMap({ x =>
          Seq(
            Signal(
              category = "",
              name = s"${x.name}_data",
              typ = VhdlType(x.typ.asInstanceOf[TyStm].t).toStdLogicVec
            ),
            Signal(
              category = "",
              name = s"${x.name}_valid",
              typ = VhdlStdLogic
            ),
            Signal(
              category = "",
              name = s"${x.name}_ready",
              typ = VhdlStdLogic,
              assignStmt = if (x == pipe.sink) {
                Some(s"${x.name}_ready <= ready;")
              } else {
                None
              }
            )
          )
        })
      })
      sbuildOutputs ++ letOutputs
    }
    val ports = {
      val TyStm(outElemTyp, _) = pipe.sink.typ
      Seq(
        InPort(options.clock, VhdlStdLogic),
        InPort(options.reset, VhdlStdLogic),
        // Handshake with consumer
        OutPort(
          topData(options.outName),
          VhdlType(outElemTyp).toStdLogicVec,
          assign = Some(s"${pipe.sink.name}_data")
        ),
        OutPort(
          topValid(options.outName),
          VhdlStdLogic,
          assign = Some(s"${pipe.sink.name}_valid")
        ),
        InPort(
          topReady(options.outName),
          VhdlStdLogic
        )
      ) ++ pipe.inputs.flatMap({ x =>
        val TyStm(inElemTyp, _) = x.typ
        Seq(
          // Handshake with producers
          InPort(s"${x.name}_data", VhdlType(inElemTyp).toStdLogicVec),
          InPort(s"${x.name}_valid", VhdlStdLogic),
          OutPort(
            s"${x.name}_ready",
            VhdlStdLogic,
            assign = if (pipe.unusedInputs.contains(x)) {
              Some(s"${x.name}_valid")
            } else {
              None
            }
          )
        )
      })
    }
    CustomVhdlComponent(
      expr = Some(f),
      name = options.topName,
      inPorts =
        ports.filter(_.isInstanceOf[InPort]).map(_.asInstanceOf[InPort]),
      outPorts =
        ports.filter(_.isInstanceOf[OutPort]).map(_.asInstanceOf[OutPort]),
      signals = signals,
      functions = Seq(),
      children = childComponents
    )
  }

  def topData(outName: Option[String]): String = {
    outName.map(_ + "_").getOrElse("") + "data"
  }

  def topValid(outName: Option[String]): String = {
    outName.map(_ + "_").getOrElse("") + "valid"
  }

  def topReady(outName: Option[String]): String = {
    outName.map(_ + "_").getOrElse("") + "ready"
  }
}
