package mhir.gen.vhdl
package nohandshake

import mhir.ir._

object TopVhdl {

  /** Generate the top-level VHDL entity.
    *
    * @param f
    *   the function defining the accelerator's behaviour.
    */
  def apply(f: Expr, options: VhdlGeneratorOptions): CustomVhdlComponent = {
    require(!options.handshake)
    val pipe = FlattenPipeline(f)
    val childComponents = pipe.sbuilds.zipWithIndex.map({
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
            "data" -> x.name
          ) ++ inputsOfS.map(x => x.name -> x.name)
        )
        VhdlEntityInstantiation(component.name, component, portMap)
    })
    val signals = {
      val sbuildOutputs = pipe.sbuilds.flatMap({ case (x, _) =>
        Seq(
          Signal(
            category = "sbuild outputs",
            name = x.name,
            typ = VhdlType(x.typ.asInstanceOf[TyStm].t).toStdLogicVec
          )
        )
      })
      val letOutputs = pipe.lets.flatMap({ case (in, _, xs) =>
        xs.flatMap({ x =>
          Seq(
            Signal(
              category = "letstm outputs",
              name = x.name,
              typ = VhdlType(x.typ.asInstanceOf[TyStm].t).toStdLogicVec,
              assignStmt = Some(s"$x <= $in;")
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
        OutPort(
          topData(options.outName),
          VhdlType(outElemTyp).toStdLogicVec,
          assign = Some(pipe.sink.name)
        )
      ) ++ pipe.inputs.map({ x =>
        val TyStm(inElemTyp, _) = x.typ
        InPort(x.name, VhdlType(inElemTyp).toStdLogicVec)
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
    outName.getOrElse("data")
  }
}
