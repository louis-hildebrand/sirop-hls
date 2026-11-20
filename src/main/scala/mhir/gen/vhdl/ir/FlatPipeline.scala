package mhir.gen.vhdl.ir

import mhir.debug.indent
import mhir.gen.CodegenError
import mhir.gen.vhdl._
import mhir.ir._

import scala.collection.immutable.ListMap

trait Entity {

  def entityName: String

  def toEntityDeclaration(options: VhdlGeneratorOptions): String

  def toArchitectureBody(options: VhdlGeneratorOptions): String

  def skip(options: VhdlGeneratorOptions): Boolean = false
}

trait SingleOutputEntity extends Entity {

  def inputs: Set[Param]

  def output: Param

  def outData(options: VhdlGeneratorOptions): String
  def outValid(options: VhdlGeneratorOptions): String
  def outReady(options: VhdlGeneratorOptions): String

  override def toEntityDeclaration(options: VhdlGeneratorOptions): String = {
    val portList = this.getPorts(options).mkString(";\n")
    val portClause =
      s"""port (
         |${indent(portList)}
         |);
         |""".stripMargin.stripTrailing
    // See IEEE Std 1076-2002 section 1.1
    s"""entity ${this.entityName} is
       |$portClause
       |end entity ${this.entityName};
       |""".stripMargin.stripTrailing
  }

  private def getPorts(options: VhdlGeneratorOptions): Seq[String] = {
    val inputStreamPorts = this.inputs.flatMap({ x =>
      val TyStm(inElemTyp, _) = x.typ
      val subtypeIndication = VhdlType(inElemTyp).toStdLogicVec.vhdlName
      if (options.handshake) {
        Seq(
          s"${x.name}_data  : in  $subtypeIndication",
          s"${x.name}_valid : in  std_logic",
          s"${x.name}_ready : out std_logic"
        )
      } else {
        Seq(s"${x.name} : in $subtypeIndication")
      }
    })
    val outputStreamPorts = {
      val TyStm(outElemTyp, _) = this.output.typ
      val subtypeIndication = VhdlType(outElemTyp).toStdLogicVec.vhdlName
      if (options.handshake) {
        Seq(
          s"${this.outData(options)}  : out $subtypeIndication",
          s"${this.outValid(options)} : out std_logic",
          s"${this.outReady(options)} : in  std_logic"
        )
      } else {
        Seq(s"${this.outData(options)} : out $subtypeIndication")
      }
    }
    Seq(s"${options.clock} : in std_logic", s"${options.reset} : in std_logic")
      .++(inputStreamPorts)
      .++(outputStreamPorts)
  }
}

private[vhdl] sealed trait PipelineNode

/** @param output
  *   the name for the output stream.
  */
private[vhdl] case class StmBuildNode(
    entityName: String,
    output: Param,
    s: GenStmBuild
) extends PipelineNode
    with SingleOutputEntity {

  override def inputs: Set[Param] = {
    this.s.producers.map({ case (_, (p, _)) => p }).toSet
  }

  override def outData(options: VhdlGeneratorOptions): String = "data"
  override def outValid(options: VhdlGeneratorOptions): String = "valid"
  override def outReady(options: VhdlGeneratorOptions): String = "ready"

  def toArchitectureBody(options: VhdlGeneratorOptions): String = {
    val identifier = "arch"
    val architectureDeclarativePart = this.s.intermediates
      .map({
        case (x, f: FunctionIntermediate) =>
          f.toDeclarativeItem(x.name, options)
        case (x, _) => SignalTarget(x).toDecl
      })
      .++(
        if (options.handshake) {
          Seq(
            "signal all_required_producers_valid: boolean;",
            "signal can_update_acc: boolean;",
            "signal transfer_ok: boolean;",
            "signal valid_internal: boolean;"
          )
            .++(
              this.s.producers.flatMap({ case (x, (p, _)) =>
                assert(
                  x == p,
                  "the name of the producer variable should have been changed to match the stream itself"
                    + s" (variable is $x, stream is $p)"
                  // TODO: I think this comment is out of date
                  // This is important because the expression generator turns sdata(x)
                  // into something like x_data, whereas the actual port name is
                  // something like p_data.
                  // As for why the producer list is a Map[Param, (Expr, Expr)] at all
                  // rather than a Map[Param, Expr], see the message for commit
                  // 0763db07.
                )
                Seq(
                  s"signal ${x.name}_ready_internal : boolean;",
                  // The ${x.name}_data_internal signal will be added separately; an
                  // earlier pass should have added intermediates for sdata(p)
                  s"signal ${x.name}_valid_internal : boolean;",
                  s"signal arpv_except_${x.name} : boolean;"
                )
              })
            )
        } else {
          Seq.empty
        }
      )
      .mkString("\n")
    val enable = if (options.handshake) "can_update_acc" else "true"
    val architectureStatementPart = this.s.intermediates
      // Emit statements for intermediates
      .map({ case (x, i: Intermediate) =>
        i.toConcurrentStatement(SignalTarget(x), options, enable = enable)
      })
      .toSeq
      // Emit statement to write output data
      .:+({
        val data = VhdlExprGenerator.toVhdl(this.s.data)
        val dataTyp = VhdlType(this.s.data.typ)
        val dataStdLogicVec =
          VhdlConversionGenerator.toStdLogicVector(data, dataTyp)
        s"${this.outData(options)} <= $dataStdLogicVec;"
      })
      // Emit statements that only appear when the handshake protocol is enabled
      .++(
        if (options.handshake) {
          val readyExprByProducer = this.s.producers
            .map({ case (x, (_, ready)) =>
              x -> VhdlExprGenerator.toVhdl(ready)
            })
          val allRequiredProducersValid = if (readyExprByProducer.isEmpty) {
            "true"
          } else {
            readyExprByProducer
              .map({ case (x, c) =>
                s"(${x.name}_valid_internal or not ($c))"
              })
              .mkString(" and ")
          }
          val validVhdl = VhdlExprGenerator.toVhdl(this.s.valid)
          val validStdLogic = "bool2sl(valid_internal)"
          // TODO: Make some of these into intermediates, rather than handling it here?
          Seq(
            s"all_required_producers_valid <= $allRequiredProducersValid;",
            s"""process
               |begin
               |    wait until rising_edge(${options.clock});
               |    if sl2bool(${options.reset}) then
               |        valid_internal <= false;
               |    elsif transfer_ok or can_update_acc then
               |        valid_internal <= ($validVhdl) and all_required_producers_valid;
               |    end if;
               |end process;
               |""".stripMargin.stripTrailing,
            s"${this.outValid(options)} <= $validStdLogic;",
            s"transfer_ok <= sl2bool(${this.outReady(options)}) and valid_internal;",
            "can_update_acc <=" +
              // This stream can keep working as long as the following conditions
              // are satisfied:
              //  * If it has some valid data to send, it cannot advance until
              //    that data is sent.
              " (not valid_internal or sl2bool(ready))" +
              //  * All required producers must have valid data.
              s" and all_required_producers_valid;"
          )
            .++(
              this.s.producers.flatMap({ case (x, (_, ready)) =>
                val otherProducers = readyExprByProducer
                  .filter({ case (y, _) => y != x })
                val allOthersValid = if (otherProducers.isEmpty) {
                  "true"
                } else {
                  otherProducers
                    .map({ case (x, c) =>
                      s"(not ($c) or (${x.name}_valid_internal))"
                    })
                    .mkString(" and ")
                }
                val readyVhdl = VhdlExprGenerator.toVhdl(ready)
                Seq(
                  s"${x.name}_ready <= bool2sl(${x.name}_ready_internal);",
                  s"${x.name}_ready_internal <=" +
                    s" ($readyVhdl) and (not valid_internal or sl2bool(ready)) and arpv_except_${x.name};",
                  // The ${x.name}_data_internal signal will be added separately; an
                  // earlier pass should have added intermediates for sdata(p)
                  s"${x.name}_valid_internal <= sl2bool(${x.name}_valid);",
                  s"arpv_except_${x.name} <= $allOthersValid;"
                )
              })
            )
        } else {
          Seq.empty
        }
      )
      .mkString("\n\n")
    // See IEEE Std 1076-2002 section 1.2
    s"""architecture $identifier of ${this.entityName} is
       |${indent(architectureDeclarativePart)}
       |begin
       |${indent(architectureStatementPart)}
       |end architecture $identifier;
       |""".stripMargin.stripTrailing
  }
}

/** @param in
  *   the stream feeding into this node.
  * @param bufSize
  *   the size of the [[mhir.ir.LetStm]] buffer.
  * @param out
  *   the names of the output streams.
  */
private[vhdl] case class LetStmNode(
    entityName: String,
    in: Param,
    bufSize: Int,
    out: Set[Param]
) extends PipelineNode
    with Entity {

  override def skip(options: VhdlGeneratorOptions): Boolean = !options.handshake

  override def toEntityDeclaration(options: VhdlGeneratorOptions): String = {
    val TyStm(elemTyp, _) = this.in.typ
    val subtypeIndication = VhdlType(elemTyp).toStdLogicVec.vhdlName
    val portList = Seq(
      s"${options.clock} : in std_logic",
      s"${options.reset} : in std_logic",
      // Input streams
      s"${this.in.name}_data  : in  $subtypeIndication",
      s"${this.in.name}_valid : in  std_logic",
      s"${this.in.name}_ready : out std_logic"
    )
      .++(
        // Output streams
        this.out.flatMap(x =>
          Seq(
            s"${x.name}_data  : out $subtypeIndication",
            s"${x.name}_valid : out std_logic",
            s"${x.name}_ready : in  std_logic"
          )
        )
      )
      .mkString(";\n")
    val portClause =
      s"""port (
         |${indent(portList)}
         |);
         |""".stripMargin.stripTrailing
    // See IEEE Std 1076-2002 section 1.1
    s"""entity ${this.entityName} is
       |$portClause
       |end entity ${this.entityName};
       |""".stripMargin.stripTrailing
  }

  override def toArchitectureBody(options: VhdlGeneratorOptions): String = {
    val identifier = "arch"
    val outputSeq = this.out.toSeq
    val TyStm(elemTyp, _) = this.in.typ
    val bitwidth = VhdlType(elemTyp).bitWidth
    val architectureDeclarativePart = {
      val dataSubtypeIndication = VhdlStdLogicVec(
        bitwidth * math.max(1, outputSeq.size),
        IndexUp
      ).vhdlName
      val validSubtypeIndication =
        VhdlStdLogicVec(math.max(1, outputSeq.size), IndexUp).vhdlName
      val readySubtypeIndication = validSubtypeIndication
      Seq(
        s"signal buf_to_consumers_data  : $dataSubtypeIndication;",
        s"signal buf_to_consumers_valid : $validSubtypeIndication;",
        s"signal buf_to_consumers_ready : $readySubtypeIndication;"
      ).mkString("\n")
    }
    val architectureStatementPart = Seq(
      {
        val genericArgs = ListMap(
          "BIT_WIDTH" -> bitwidth.toString,
          "BUF_SIZE" -> this.bufSize.toString,
          "N_CONSUMERS" -> math.max(1, outputSeq.size).toString
        )
        val portArgs = ListMap(
          "clk" -> options.clock,
          "reset" -> options.reset,
          "p_data" -> s"${this.in.name}_data",
          "p_valid" -> s"${this.in.name}_valid",
          "p_ready" -> s"${this.in.name}_ready",
          "c_data" -> "buf_to_consumers_data",
          "c_valid" -> "buf_to_consumers_valid",
          "c_ready" -> "buf_to_consumers_ready"
        )
        EntityInstantiation(
          entityName = options.fullyQualify("letstm_buf"),
          genericArgs = genericArgs,
          portArgs = portArgs,
          label = "BUF"
        )
      },
      // Assign buf_to_consumers_ready
      {
        val rhs = if (outputSeq.isEmpty) {
          "\"1\""
        } else {
          outputSeq.zipWithIndex
            .map({ case (x, i) => s"$i => ${x}_ready" })
            .mkString("(", ", ", ")")
        }
        s"buf_to_consumers_ready <= $rhs;"
      }
    )
      // Assign output data
      .++(
        outputSeq.zipWithIndex.map({ case (x, i) =>
          val lsb = i * bitwidth
          val msb = lsb + bitwidth - 1
          s"${x.name}_data <= buf_to_consumers_data($lsb to $msb);"
        })
      )
      // Assign output valid
      .++(
        outputSeq.zipWithIndex.map({ case (x, i) =>
          s"${x.name}_valid <= buf_to_consumers_valid($i);"
        })
      )
      .mkString("\n")
    // See IEEE Std 1076-2002 section 1.2
    s"""architecture $identifier of ${this.entityName} is
       |${indent(architectureDeclarativePart)}
       |begin
       |${indent(architectureStatementPart)}
       |end architecture $identifier;
       |""".stripMargin.stripTrailing
  }
}

private[vhdl] case class FlatPipeline(
    entityName: String,
    sbuilds: Seq[StmBuildNode],
    lets: Seq[LetStmNode],
    inputs: Set[Param],
    unusedInputs: Set[Param],
    sink: Param
) extends SingleOutputEntity {

  def mapSbuilds(f: GenStmBuild => GenStmBuild): FlatPipeline = {
    this.copy(sbuilds = this.sbuilds.map(node => node.copy(s = f(node.s))))
  }

  def usesVendorIpBlocks: Boolean = {
    this.sbuilds.exists(s =>
      s.s.intermediates.values
        .flatMap(i => i.intermediateDescendants :+ i)
        .collect({ case i: IpBlockInst => i.usesVendorIpBlocks })
        .exists(x => x)
    )
  }

  override def output: Param = this.sink

  override def outData(options: VhdlGeneratorOptions): String = {
    FlatPipeline.topData(options)
  }

  override def outValid(options: VhdlGeneratorOptions): String = {
    FlatPipeline.topValid(options)
  }

  override def outReady(options: VhdlGeneratorOptions): String = {
    FlatPipeline.topReady(options)
  }

  def toArchitectureBody(options: VhdlGeneratorOptions): String = {
    // TODO: Move these checks somewhere else (maybe VhdlGenerator.validateExpr)?
    for (LetStmNode(_, _, bufSize, _) <- this.lets) {
      if (!options.handshake && bufSize != 0) {
        throw CodegenError(
          s"cannot generate letstm with a nonzero buffer size ($bufSize) when the handshake protocol is disabled"
        )
      }
    }
    if (this.inputs.map(_.name).contains(FlatPipeline.topData(options))) {
      val name = FlatPipeline.topData(options)
      // TODO: Move this to the same place as the other related errors (e.g., the ones about reserved keywords)
      throw CodegenError(
        s"'$name' cannot be used as an input stream name, since it is also used for the output stream"
      )
    }
    val identifier = "arch"
    val architectureDeclarativePart = this.sbuilds
      .map(_.output)
      .sortBy(_.name)
      .++(this.lets.flatMap(_.out).sortBy(_.name))
      .flatMap({ x =>
        val TyStm(elemTyp, _) = x.typ
        val subtypeIndication = VhdlType(elemTyp).toStdLogicVec.vhdlName
        if (options.handshake) {
          Seq(
            // See IEEE Std 1076-2002 section 4.3.1.2
            s"signal ${x.name}_data  : $subtypeIndication;",
            s"signal ${x.name}_valid : std_logic;",
            s"signal ${x.name}_ready : std_logic;"
          )
        } else {
          // See IEEE Std 1076-2002 section 4.3.1.2
          Seq(s"signal ${x.name}: $subtypeIndication;")
        }
      })
      .mkString("\n")
    val architectureStatementPart =
      this.sbuilds
        // Emit sbuild
        .map({ case sbuild @ StmBuildNode(_, out, s) =>
          val inputsOfS = s.producers.map({ case (_, (p, _)) => p }).toSet
          val portArgs = if (options.handshake) {
            // Clock, reset
            ListMap(
              options.clock -> options.clock,
              options.reset -> options.reset
            )
              // Input streams
              .++(inputsOfS.flatMap({ x =>
                ListMap(
                  s"${x.name}_data" -> s"${x.name}_data",
                  s"${x.name}_valid" -> s"${x.name}_valid",
                  s"${x.name}_ready" -> s"${x.name}_ready"
                )
              }))
              // Output stream
              .++(
                ListMap(
                  s"data" -> s"${out.name}_data",
                  s"valid" -> s"${out.name}_valid",
                  s"ready" -> s"${out.name}_ready"
                )
              )
          } else {
            // Clock, reset
            ListMap(
              options.clock -> options.clock,
              options.reset -> options.reset
            )
              // Input streams
              .++(inputsOfS.map(x => x.name -> x.name))
              // Output stream
              .+("data" -> out.name)
          }
          EntityInstantiation(
            entityName = options.fullyQualify(sbuild.entityName),
            genericArgs = ListMap.empty,
            portArgs = portArgs,
            label = sbuild.entityName.toUpperCase
          )
        })
        // Emit letstm
        .++(
          this.lets.flatMap({ case let @ LetStmNode(_, in, _, out) =>
            if (options.handshake) {
              val portArgs = ListMap(
                // Clock, reset
                options.clock -> options.clock,
                options.reset -> options.reset,
                // Input stream
                s"${in.name}_data" -> s"${in.name}_data",
                s"${in.name}_valid" -> s"${in.name}_valid",
                s"${in.name}_ready" -> s"${in.name}_ready"
              ) ++ out.flatMap({ x =>
                // Output streams
                ListMap(
                  s"${x.name}_data" -> s"${x.name}_data",
                  s"${x.name}_valid" -> s"${x.name}_valid",
                  s"${x.name}_ready" -> s"${x.name}_ready"
                )
              })
              Seq(
                EntityInstantiation(
                  options.fullyQualify(let.entityName),
                  genericArgs = ListMap.empty,
                  portArgs = portArgs,
                  label = let.entityName.toUpperCase
                )
              )
            } else {
              out.map(x => s"$x <= $in;")
            }
          })
        )
        // Send stream from sink to entity output ports
        .++(
          if (options.handshake) {
            Seq(
              s"${this.outData(options)} <= ${this.sink.name}_data;",
              s"${this.outValid(options)} <= ${this.sink.name}_valid;",
              s"${this.sink.name}_ready <= ${this.outReady(options)};"
            )
          } else {
            Seq(s"${this.outData(options)} <= ${this.sink.name};")
          }
        )
        .mkString("\n")
    // See IEEE Std 1076-2002 section 1.2
    s"""architecture $identifier of ${this.entityName} is
       |${indent(architectureDeclarativePart)}
       |begin
       |${indent(architectureStatementPart)}
       |end architecture $identifier;
       |""".stripMargin.stripTrailing
  }
}

object FlatPipeline {

  def topData(options: VhdlGeneratorOptions): String = {
    if (options.handshake) {
      options.outName.map(_ + "_").getOrElse("") + "data"
    } else {
      options.outName.getOrElse("data")
    }
  }

  def topValid(options: VhdlGeneratorOptions): String = {
    assert(
      options.handshake,
      "there shouldn't be a valid signal if the handshake protocol is disabled"
    )
    options.outName.map(_ + "_").getOrElse("") + "valid"
  }

  def topReady(options: VhdlGeneratorOptions): String = {
    assert(
      options.handshake,
      "there shouldn't be a ready signal if the handshake protocol is disabled"
    )
    options.outName.map(_ + "_").getOrElse("") + "ready"
  }
}

object EntityInstantiation {

  def apply(
      entityName: String,
      genericArgs: ListMap[String, String],
      portArgs: ListMap[String, String],
      label: String = ""
  ): String = {
    // See IEEE Std 1076-2002 section 5.2.1.2
    val genericMapAspect = if (genericArgs.isEmpty) {
      ""
    } else {
      genericArgs
        .map({ case (formal, actual) => s"$formal => $actual" })
        .map("        " + _)
        .mkString("\n    generic map(\n", ",\n", ")")
    }
    val portMapAspect = if (portArgs.isEmpty) {
      ""
    } else {
      portArgs
        .map({ case (formal, actual) => s"$formal => $actual" })
        .map("        " + _)
        .mkString("\n    port map(\n", ",\n", ")")
    }
    // See IEEE Std 1076-2002 section 9.6
    val instantiationLabel = if (label.isBlank) Param("L")().name else label
    s"$instantiationLabel: entity $entityName$genericMapAspect$portMapAspect;"
  }
}
