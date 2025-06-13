package gen

import debug.PrettyPrinter
import ir._

import java.nio.file.Path
import scala.annotation.tailrec

/** Where an input stream is used in a given component.
  */
private sealed trait WhereUsed

/** The input stream is read by this component.
  */
private object Here extends WhereUsed

/** The input stream is passed along to exactly one sub-component.
  */
private object InChild extends WhereUsed

/** The input stream is not used anywhere in this component.
  */
private object Nowhere extends WhereUsed

object VhdlGenerator {

  /** Create a VHDL design for the given stream and save it in the given
    * directory.
    *
    * @param s
    *   The stream for which to generate VHDL
    * @param dir
    *   The directory in which to save the design
    */
  def emitVhdl(s: StmBuild, dir: Path): VhdlType = {
    validateExpr(s)
    val (topComponent, typ) = stmBuildToVhdl(s, Set(), "top")
    VhdlWriter.emit(topComponent, dir)
    typ
  }

  /** Create a VHDL design for the given function and save it in the given
    * directory.
    *
    * @param f
    *   The function for which to generate VHDL
    * @param dir
    *   The directory in which to save the design
    */
  def emitVhdl(f: Function, dir: Path): VhdlType = {
    validateExpr(f)
    val (inputs, stm) = unwrapTopLevelFunction(f)
    val (topComponent, typ) = stmBuildToVhdl(stm, inputs.toSet, "top")
    VhdlWriter.emit(topComponent, dir)
    typ
  }

  def unwrapTopLevelFunction(f: Function): (Seq[Param], StmBuild) = {
    @tailrec
    def unwrap(e: Expr, inputs: Seq[Param]): (Seq[Param], StmBuild) = {
      e match {
        case s: StmBuild    => (inputs, s)
        case Function(x, e) => unwrap(e, x +: inputs)
        case e =>
          throw new IllegalArgumentException(
            "Cannot generate top-level component for expression."
              + " A program must be a StmBuild or a function that returns a program."
              + s" The expression is $e."
          )
      }
    }

    val (inputSeq, stm) = unwrap(f, Seq())
    if (inputSeq.map(x => x.name).toSet.size != inputSeq.length) {
      val paramList = inputSeq.reverse.map(x => x.name).mkString(", ")
      throw new IllegalArgumentException(
        s"Duplicate parameters in top-level parameter list $paramList."
      )
    }
    (inputSeq.reverse, stm)
  }

  private def validateExpr(e: Expr): Unit = {
    require(
      e.typ != Missing,
      "Expression must be type checked before hardware generation."
    )
    require(
      !e.contains(classOf[SyntaxSugar]),
      "Expression must be lowered before hardware generation."
    )
    require(
      e.freeVars().isEmpty,
      s"Cannot generate hardware for expression with free variables (${e.freeVars()})."
    )
  }

  private def stmBuildToVhdl(
      stm: StmBuild,
      inputs: Set[Param],
      name: String
  ): (VhdlComponent, VhdlType) = {
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
            case (t, _) if Default.hasDefault(t) => (Some(eqn), None)
            // CASE 2: internal producer streams
            case (_: TyStm, _: StmBuild) => (None, Some(eqn))
            // CASE 3: external producer streams
            case (_: TyStm, z: Param) =>
              assert(inputs.contains(z))
              assert(z == x)
              (None, None)
            // CASE 4: others
            case (t, _) =>
              throw new IllegalArgumentException(
                s"Accumulator variable $x with type $t cannot be mapped to a register, an internal producer stream, or an external producer stream."
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
      internalProducerInterface(internalProducerEquations, readyCondByProducer)
    val (externalProducerPorts, externalProducerSignals) =
      externalProducerInterface(whereUsedByInput, readyCondByProducer)

    val comment = PrettyPrinter
      .show(s)(Map())
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
    val component = VhdlComponent(
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
      children = internalProducers
    )

    (component, VhdlType(s.data.typ))
  }

  private def findWhereUsedByInput(
      stm: StmBuild,
      inputs: Set[Param]
  ): Map[Param, WhereUsed] = {
    val rules = (
      "Input streams must only appear"
        + " (1) directly as the initial value for an accumulator or"
        + " (2) inside the initial value for an accumulator as long as that initial value is a StmBuild."
        + " Furthermore, an input stream cannot be used more than once."
    )
    // Freshen variables so that no accumulator variables are called the same
    // thing as an input variable.
    // This simplifies the checks for improper use of an input.
    val s = stm.renameVars
    inputs
      .map(x => {
        if (s.data.freeVars().union(s.valid.freeVars()).contains(x)) {
          throw new IllegalArgumentException(
            s"Input $x occurs free in the stream output. $rules"
          )
        }
        if (s.n.freeVars().contains(x)) {
          throw new IllegalArgumentException(
            s"Input $x occurs free in the stream length. $rules"
          )
        }
        var u: WhereUsed = Nowhere
        for ((acc, (seed, next)) <- s.equations) {
          if (next.freeVars().contains(x)) {
            throw new IllegalArgumentException(
              s"Input $x occurs free in the `next` expression for accumulator $acc. $rules"
            )
          }
          (seed, u) match {
            case (x2: Param, Nowhere) if x2 == x =>
              u = Here
            case (s: StmBuild, Nowhere) if s.freeVars().contains(x) =>
              u = InChild
            case (_, Nowhere) if seed.freeVars().contains(x) =>
              throw new IllegalArgumentException(
                s"Input $x is used improperly in initial value for accumulator $acc."
              )
            case _ if seed.freeVars().contains(x) =>
              assert(u != Nowhere)
              throw new IllegalArgumentException(
                s"Input $x is used more than once. $rules"
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
        assign =
          Some(s"(others => '0') when not transfer_ok else $dataInternalSlv")
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
        name = "num_outputs",
        typ = VhdlInt,
        init = Some("0"),
        assignStmt = Some("num_outputs <= num_outputs + 1;"),
        cond = Some("transfer_ok")
      ),
      Signal(
        category = "Handshake (output)",
        name = "valid_internal",
        typ = VhdlBool,
        init = None,
        assignStmt = Some(
          s"valid_internal <= (num_outputs < $nVhdl) and ($validVhdl) and ${allRequiredProducersValidSig.name};"
        ),
        cond = None
      ),
      Signal(
        category = "Handshake (output)",
        name = "data_internal",
        typ = VhdlType(data.typ),
        init = None,
        assignStmt = Some(s"data_internal <= $dataVhdl;"),
        cond = None
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
          // If a data element was transferred, then we must update all the
          // accumulators.
            + " transfer_ok or"
            // If not, this stream can still keep working, except that:
            //  * If it has some valid data to send, it cannot advance until
            //    that data is sent.
            + " (not valid_internal"
            //  * All required producers must have valid data.
            + s" and ${allRequiredProducersValidSig.name});"
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
        assert(Default.hasDefault(x.typ))
        require(
          z.freeVars().isEmpty,
          s"Initial value for accumulator ${x.name} has free variables (${z.freeVars().toSeq.mkString(", ")})."
        )
        val reshapedInit = ReshapeData(z, x.typ)().tchk().lower()
        val initVhdl = VhdlExprGenerator.valueToVhdl(reshapedInit)
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
      producerEquations: Iterable[(Param, (Expr, Expr))],
      readyByProducer: Map[Param, String]
  ): (Seq[(VhdlComponent, PortMap)], Seq[Signal]) = {
    val children = producerEquations
      .map({ case (x, (z, _)) =>
        val inputsUsedInThisChild = z.freeVars()
        val zStm = z.asInstanceOf[StmBuild]
        val componentName = Param("stm")().name
        val (component, _) =
          stmBuildToVhdl(zStm, inputsUsedInThisChild, componentName)
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
      producerEquations
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

  def valueToStdLogicVector(v: Expr, len: String): String = {
    ir.eval(v).tchk() match {
      case False     => "\"0\""
      case True      => "\"1\""
      case IntCst(k) => s"std_logic_vector(to_signed($k, $len))"
      case Tuple(elems @ _*) =>
        if (elems.isEmpty) {
          "\"\""
        } else {
          elems
            .map(e => valueToStdLogicVector(e, getBitWidth(e.typ).toString))
            .map(x => s"($x)")
            .mkString(" & ")
        }
      case vec: VecLiteral =>
        valueToStdLogicVector(Tuple(vec.elems: _*)(), len)
      case _ =>
        throw new IllegalArgumentException(
          s"Cannot convert value $v to a std_logic_vector. Is it really a value?"
        )
    }
  }

  private def getBitWidth(t: Type): Int = VhdlType(t).bitWidth
}
