package gen

import debug.{PrettyPrinter, indent}
import ir._
import opt.PartialEvalPass

import java.nio.file.{Files, Path}

// TODO: Move these types to a separate file

private sealed trait VhdlType {
  def vhdlName: String
  def vhdlTypeMark: String = vhdlName
  def vhdlDefinition: Option[String]
  def children: Set[VhdlType]
  def bitWidth: Int
}

private case object VhdlInt extends VhdlType {
  override def vhdlName: String = "integer"

  override def vhdlDefinition: Option[String] = None

  override def children: Set[VhdlType] = Set()

  override def bitWidth: Int = 32
}

private case object VhdlBool extends VhdlType {
  override def vhdlName: String = "boolean"

  override def vhdlDefinition: Option[String] = None

  override def children: Set[VhdlType] = Set()

  override def bitWidth: Int = 1
}

private case object VhdlStdLogic extends VhdlType {
  override def vhdlName: String = "std_logic"

  override def vhdlDefinition: Option[String] = None

  override def children: Set[VhdlType] = Set()

  override def bitWidth: Int = 1
}

private case class VhdlStdLogicVec(n: Int) extends VhdlType {
  override def vhdlName: String = s"std_logic_vector(${n - 1} downto 0)"
  override def vhdlTypeMark: String = "std_logic_vector"

  override def vhdlDefinition: Option[String] = None

  override def children: Set[VhdlType] = Set()

  override def bitWidth: Int = n
}

private case class VhdlRecord(fieldTypes: Seq[VhdlType]) extends VhdlType {
  override def vhdlName: String = {
    val fieldTypeNames = fieldTypes
      .map(t => {
        val name = t.vhdlName
        if (name.startsWith("\\")) {
          assert(name.length >= 2)
          assert(name.endsWith("\\"))
          name.drop(1).dropRight(1)
        } else {
          name
        }
      })
    s"\\(${fieldTypeNames.mkString(", ")})\\"
  }

  override def vhdlDefinition: Option[String] = {
    if (fieldTypes.isEmpty) {
      // VHDL records cannot be empty, so use an empty array instead
      Some(s"type $vhdlName is array (0 downto 1) of character;")
    } else {
      val fieldDecls = fieldTypes.zipWithIndex
        .map({ case (t, i) => s"i_$i : ${t.vhdlName};" })
      val definition =
        s"""type $vhdlName is
           |record
           |    ${fieldDecls.mkString("\n    ")}
           |end record;
           |""".stripMargin.stripTrailing
      Some(definition)
    }
  }

  override def children: Set[VhdlType] = {
    fieldTypes.toSet.flatMap((t: VhdlType) => t.children + t)
  }

  override def bitWidth: Int = fieldTypes.map(t => t.bitWidth).sum
}

private case class VhdlArray(n: Int, t: VhdlType) extends VhdlType {
  override def vhdlName: String = {
    val elemTypeName = t.vhdlName
    val strippedElemTypeName = if (elemTypeName.startsWith("\\")) {
      assert(elemTypeName.length >= 2)
      assert(elemTypeName.endsWith("\\"))
      elemTypeName.drop(1).dropRight(1)
    } else {
      elemTypeName
    }
    s"\\Vec[$strippedElemTypeName; $n]\\"
  }

  override def vhdlDefinition: Option[String] = {
    val definition =
      s"""type $vhdlName is
         |array (0 to ${n - 1}) of ${t.vhdlName};
         |""".stripMargin.stripTrailing
    Some(definition)
  }

  override def children: Set[VhdlType] = {
    t.children + t
  }

  override def bitWidth: Int = n * t.bitWidth
}

private case class VhdlFunction(
    name: String,
    args: Seq[(String, VhdlType)],
    returnType: VhdlType,
    variables: Seq[(String, VhdlType)],
    body: String
) {
  private def signature: String = {
    val argList =
      args.map({ case (x, t) => s"$x : in ${t.vhdlName}" }).mkString(", ")
    s"function $name ($argList) return ${returnType.vhdlTypeMark}"
  }

  def vhdlDecl: String = s"$signature;"

  def vhdlImpl: String = {
    val varDecls = if (variables.isEmpty) {
      "-- No variables"
    } else {
      variables
        .map({ case (x, t) => s"variable $x : ${t.vhdlName};" })
        .mkString("\n")
    }
    s"""$signature is
       |${indent(varDecls)}
       |begin
       |${indent(body)}
       |end;
       |""".stripMargin.stripTrailing
  }
}

private trait Port
private case class InPort(
    name: String,
    typ: VhdlType
) extends Port

private case class OutPort(
    name: String,
    typ: VhdlType,
    assign: String
) extends Port

private case class Signal(
    name: String,
    typ: VhdlType,
    init: Option[String] = None,
    assignStmt: Option[String] = None,
    cond: Option[String] = None
)

private case class PortMap(map: Map[String, String])

/** One component (entity + architecture) in VHDL.
  *
  * @param comment
  *   The expression in the IR from which this component was generated
  * @param name
  *   Name of the component
  * @param inPorts
  *   Input ports
  * @param outPorts
  *   Output ports
  * @param signals
  *   Internal signals
  */
private case class VhdlComponent(
    comment: String,
    name: String,
    inPorts: Seq[InPort],
    outPorts: Seq[OutPort],
    signals: Seq[Signal],
    children: Seq[(VhdlComponent, PortMap)]
) {
  checkPortMaps()

  private def checkPortMaps(): Unit = {
    for ((child, map) <- children) {
      val expectedPorts =
        (child.inPorts.map(p => p.name) ++ child.outPorts.map(p =>
          p.name
        )).toSet
      val actualPorts = map.map.keySet
      assert(
        expectedPorts == actualPorts,
        s"wrong ports for component ${child.name} (expected $expectedPorts but got $actualPorts)"
      )
    }
  }

  def vhdl: String = {
    val portDecls = (
      inPorts.map(p => s"${p.name} : in ${p.typ.vhdlName}")
        ++ outPorts.map(p => s"${p.name} : out ${p.typ.vhdlName}")
    ).sortBy(x => x)
    val sigDecls = signals
      .map(s => {
        val str1 = s"signal ${s.name} : ${s.typ.vhdlName}"
        val str2 = s.init match {
          case Some(z) => s"$str1 := $z"
          case None    => str1
        }
        s"$str2;"
      })
      .sortBy(x => x)
    val portMaps = children
      .map({ case (c, pm) =>
        val assignments =
          pm.map.map({ case (k, v) => s"$k => $v" }).mkString(", ")
        s"${c.name.toUpperCase} : entity work.${c.name} port map($assignments);"
      })
      .sortBy(x => x)
    val combStmts = (
      signals
        .filter(s => s.cond.isEmpty && s.assignStmt.isDefined)
        .map(s => s.assignStmt.get)
        ++ outPorts.map(p => s"${p.name} <= ${p.assign};")
    ).sortBy(x => x)
    val clkStmts = signals
      .filter(s => s.cond.nonEmpty && s.assignStmt.isDefined)
      .sortBy(s => s.name)
      .map(s => s"if (${s.cond.get}) then ${s.assignStmt.get} end if;")
    comment + "\n" +
      s"""library IEEE;
         |use IEEE.std_logic_1164.all;
         |use IEEE.numeric_std.all;
         |use work.conversions.all;
         |use work.typedefs.all;
         |
         |entity $name is
         |port (
         |    ${portDecls.mkString(";\n    ")}
         |);
         |end;
         |
         |architecture arch of $name is
         |    ${sigDecls.mkString("\n    ")}
         |begin
         |    ${portMaps.mkString("\n    ")}
         |
         |    ${combStmts.mkString("\n    ")}
         |
         |    process
         |    begin
         |        wait until rising_edge(clk) and can_update_acc;
         |        ${clkStmts.mkString("\n        ")}
         |    end process ;
         |end;
         |""".stripMargin
  }
}

object VhdlGenerator {
  def makeVhdl(s: StmBuild, dir: Path): Int = {
    require(
      s.typ != Missing,
      "Expression must be type checked before hardware generation."
    )
    require(
      !s.contains(classOf[SyntaxSugar]),
      "Expression must be lowered before hardware generation."
    )

    val (topComponent, bitWidth) = stmBuildToComponent(s, "top")
    val typesToDefine =
      findTypesUsedIn(topComponent).flatMap(t => t.children + t)

    val designDir = dir.resolve("design")
    Files.createDirectory(designDir)
    emitConversionsPackage(typesToDefine, designDir)
    emitTypedefs(typesToDefine, designDir)
    emitComponents(topComponent, designDir)

    bitWidth
  }

  private def emitConversionsPackage(types: Set[VhdlType], dir: Path): Unit = {
    val defaultFunctions = Seq(
      VhdlFunction(
        name = "bool2sl",
        args = Seq(("b", VhdlBool)),
        returnType = VhdlStdLogic,
        variables = Seq(("x", VhdlStdLogic)),
        body = """x := '1' when (b) else '0';
                 |return x;
                 |""".stripMargin.stripTrailing
      ),
      VhdlFunction(
        name = "sl2bool",
        args = Seq(("x", VhdlStdLogic)),
        returnType = VhdlBool,
        variables = Seq(),
        body = "return x = '1';"
      )
    )
    val toSlvFunctions = types.flatMap(t => toSlvConverter(t))
    val fromSlvFunctions = types.flatMap(t => fromSlvConverter(t))
    val functions =
      (defaultFunctions ++ toSlvFunctions ++ fromSlvFunctions)
        .sortBy(f => f.vhdlDecl)

    val decls = functions.map(f => f.vhdlDecl).mkString("\n\n")
    val impls = functions.map(f => f.vhdlImpl).mkString("\n\n")
    val contents =
      s"""library IEEE;
         |use IEEE.std_logic_1164.all;
         |use IEEE.numeric_std.all;
         |use work.typedefs.all;
         |
         |package conversions is
         |${indent(decls)}
         |end package;
         |
         |package body conversions is
         |${indent(impls)}
         |end package body;
         |""".stripMargin

    val file = dir.resolve("conversions.vhd")
    Files.writeString(file, contents)
  }

  private def emitTypedefs(typesToDefine: Set[VhdlType], dir: Path): Unit = {
    val definitions = typesToDefine.toSeq
      // A type declaration cannot refer to types declared later.
      // Since every type's name includes all of its children's names, its
      // name is guaranteed to be strictly larger than any of its children's
      // names.
      // Therefore, sorting by name length puts the declarations in the
      // desired order.
      .sortBy(x => x.vhdlName.length)
      .flatMap(t => t.vhdlDefinition)
    val contents =
      s"""library IEEE;
         |use IEEE.std_logic_1164.all;
         |use IEEE.numeric_std.all;
         |
         |package typedefs is
         |${indent(definitions.mkString("\n\n"))}
         |end package;
         |""".stripMargin

    val file = dir.resolve("typedefs.vhd")
    Files.writeString(file, contents)
  }

  private def findTypesUsedIn(c: VhdlComponent): Set[VhdlType] = {
    (c.inPorts.map(p => p.typ)
      ++ c.outPorts.map(p => p.typ)
      ++ c.signals.map(s => s.typ)
      ++ c.children.flatMap({ case (c, _) => findTypesUsedIn(c) })).toSet
  }

  private def emitComponents(c: VhdlComponent, dir: Path): Unit = {
    val file = dir.resolve(s"${c.name}.vhd")
    Files.writeString(file, c.vhdl)
    for ((child, _) <- c.children) {
      emitComponents(child, dir)
    }
  }

  private def stmBuildToComponent(
      s: StmBuild,
      name: String
  ): (VhdlComponent, Int) = {
    // TODO: It would be nice to avoid partial evaluation
    val valid = PartialEvalPass
      .partialEval(IsSome(s.output)().tchk().lower())
      .tchk()
      .lower()
    val data = PartialEvalPass
      .partialEval(OptionUnwrapUnsafe(s.output)().tchk().lower())
      .tchk()
      .lower()
    val bitWidth = getBitWidth(data.typ)
    val (nVhdl, nSignals) = makeVhdlExpr(s.n)
    val (validVhdl, validSignals) = makeVhdlExpr(valid)
    val (dataVhdl, dataSignals) = makeVhdlExpr(data)

    val (registers, internalProducers, externalProducers) = {
      val (r, ip, ep) = s.equations
        .map({ case eqn @ (x, (z, _)) =>
          (x.typ, z) match {
            // CASE 1: registers
            case (t, _) if Default.hasDefault(t) => (Some(eqn), None, None)
            // CASE 2: internal producer streams
            case (_: TyStm, _: StmBuild) => (None, Some(eqn), None)
            // CASE 3: external producer streams
            case (_: TyStm, _: Param) => (None, None, Some(eqn))
            // CASE 4: others
            case (t, _) =>
              throw new IllegalArgumentException(
                s"Accumulator variable $x with type $t cannot be mapped to a register, an internal producer stream, or an external producer stream."
              )
          }
        })
        .unzip3
      (r.flatten.toSeq, ip.flatten.toSeq, ep.flatten.toSeq)
    }

    // Simple registers
    val accSignals = registers
      .flatMap({ case (x, (z, next)) =>
        assert(Default.hasDefault(x.typ))
        require(
          z.freeVars().isEmpty,
          "Accumulator initial values must not have free variables."
        )
        val initVhdl = valueToVhdl(z)
        val (nextVhdl, nextSignals) = makeVhdlExpr(next)
        val sig = Signal(
          name = x.name,
          typ = makeVhdlType(x.typ),
          init = Some(initVhdl),
          assignStmt = Some(s"${x.name} <= $nextVhdl;"),
          cond = Some("can_update_acc")
        )
        sig +: nextSignals
      })

    // All producer streams
    val allProducers = internalProducers ++ externalProducers
    val (readyCondByProducer, readyCondSignals) = {
      val (rcp, sig) = allProducers
        .map({ case (x, (_, next)) =>
          // TODO: Partially evaluate?
          val c = StmBuild.stmNextCallCondition(next, x)
          val (vhdl, sig) = makeVhdlExpr(c)
          (x -> vhdl, sig)
        })
        .unzip
      (rcp.toMap, sig.flatten)
    }

    // If waiting for multiple producers (e.g., in StmZip), don't raise the
    // ready signal until *all* required producers are ready
    val arpvExpr = if (readyCondByProducer.isEmpty) {
      "true"
    } else {
      readyCondByProducer
        .map({ case (x, c) => s"(not ($c) or (${x.name}_data_valid))" })
        .mkString(" and ")
    }
    // IMPORTANT: To avoid combinational loops, the producer's `valid` signal
    //            must NOT depend on the consumer's `ready` signal
    val allRequiredProducersValidSignal = Signal(
      name = "all_required_producers_valid",
      typ = VhdlBool,
      assignStmt = Some(s"all_required_producers_valid <= $arpvExpr;")
    )

    // Internal producer streams
    val children = internalProducers
      .map({ case (x, (z, _)) =>
        val componentName = Param("stm")().name
        val (component, _) =
          stmBuildToComponent(z.asInstanceOf[StmBuild], componentName)
        val portMap = PortMap(
          Map(
            "clk" -> "clk",
            "data_valid" -> s"${x.name}_data_valid_raw",
            "data_ready" -> s"${x.name}_data_ready_raw",
            "data" -> s"${x.name}_data_raw"
          )
        )
        (component, portMap)
      })
    val childSignals =
      internalProducers.flatMap({ case (x, _) =>
        val dataType = x.typ.asInstanceOf[TyStm].t
        val bitWidth = getBitWidth(dataType)
        val rawDataConversion =
          fromStdLogicVector(s"${x.name}_data_raw", dataType)
        val readyCond = readyCondByProducer(x)
        Seq(
          Signal(name = s"${x.name}_data_valid_raw", typ = VhdlStdLogic),
          Signal(
            name = s"${x.name}_data_valid",
            typ = VhdlBool,
            assignStmt = Some(
              s"${x.name}_data_valid <= sl2bool(${x.name}_data_valid_raw);"
            )
          ),
          Signal(
            name = s"${x.name}_data_ready_raw",
            typ = VhdlStdLogic,
            assignStmt = Some(
              s"${x.name}_data_ready_raw <= bool2sl(${x.name}_data_ready);"
            )
          ),
          Signal(
            name = s"${x.name}_data_ready",
            typ = VhdlBool,
            assignStmt = Some(
              s"${x.name}_data_ready <= ($readyCond) and can_update_acc;"
            )
          ),
          Signal(
            name = s"${x.name}_data_raw",
            typ = VhdlStdLogicVec(bitWidth)
          ),
          Signal(
            name = s"${x.name}_data",
            typ = makeVhdlType(dataType),
            assignStmt = Some(s"${x.name}_data <= $rawDataConversion;")
          )
        )
      })

    // TODO: External producer streams
    assert(
      externalProducers.isEmpty,
      "external producer streams are not yet supported"
    )

    // Ports and signals that appear in all stream components
    val defaultInPorts = Seq(
      InPort("clk", VhdlStdLogic),
      InPort("data_ready", VhdlStdLogic)
    )
    val dataInternalSlv = toStdLogicVector("data_internal", data.typ)
    val defaultOutPorts = Seq(
      OutPort(
        name = "data",
        typ = VhdlStdLogicVec(bitWidth),
        assign = s"(others => '0') when not transfer_ok else $dataInternalSlv"
      ),
      OutPort(
        name = "data_valid",
        typ = VhdlStdLogic,
        assign = "bool2sl(data_valid_internal)"
      )
    )
    val defaultSignals = Seq(
      Signal(
        "num_outputs",
        typ = VhdlInt,
        init = Some("0"),
        assignStmt = Some("num_outputs <= num_outputs + 1;"),
        cond = Some("transfer_ok")
      ),
      Signal(
        "data_valid_internal",
        typ = VhdlBool,
        init = None,
        assignStmt = Some(
          s"data_valid_internal <= (num_outputs < $nVhdl) and ($validVhdl) and all_required_producers_valid;"
        ),
        cond = None
      ),
      Signal(
        "data_internal",
        typ = makeVhdlType(data.typ),
        init = None,
        assignStmt = Some(s"data_internal <= $dataVhdl;"),
        cond = None
      ),
      Signal(
        "transfer_ok",
        typ = VhdlBool,
        init = None,
        assignStmt = Some(
          "transfer_ok <= sl2bool(data_ready) and data_valid_internal;"
        ),
        cond = None
      ),
      Signal(
        "can_update_acc",
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
            + " (not data_valid_internal"
            //  * All required producers must have valid data.
            + " and all_required_producers_valid);"
        ),
        cond = None
      )
    )

    val comment = PrettyPrinter
      .show(s)(Map())
      .split("\n")
      .map(x => s"-- $x")
      .mkString("\n")
    val allSignals = (
      defaultSignals
        ++ nSignals
        ++ dataSignals
        ++ validSignals
        ++ accSignals
        ++ childSignals
        ++ readyCondSignals
        ++ Seq(allRequiredProducersValidSignal)
    )
    val component = VhdlComponent(
      comment = comment,
      name = name,
      inPorts = defaultInPorts,
      outPorts = defaultOutPorts,
      signals = allSignals,
      children = children
    )

    (component, bitWidth)
  }

  private def makeVhdlExpr(e: Expr): (String, Seq[Signal]) = {
    e match {
      case x: Param  => (x.name, Seq())
      case IntCst(n) => (n.toString, Seq())
      case Sum(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => makeVhdlExpr(e)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" + "), signals.flatten)
      case Prod(factors @ _*) =>
        val (vhdlFactors, signals) =
          factors.map(e => makeVhdlExpr(e)).unzip
        (vhdlFactors.map(x => s"($x)").mkString(" * "), signals.flatten)
      case _: Div => ???
      case _: Mod => ???
      case True   => ("true", Seq())
      case False  => ("false", Seq())
      case ite @ IfThenElse(c, t, f) =>
        val (cVhdl, cSignals) = makeVhdlExpr(c)
        val (tVhdl, tSignals) = makeVhdlExpr(t)
        val (fVhdl, fSignals) = makeVhdlExpr(f)
        val sigName = Param("ite")().name
        val sig = Signal(
          sigName,
          typ = makeVhdlType(ite.typ),
          init = None,
          assignStmt =
            Some(s"$sigName <= ($tVhdl) when ($cVhdl) else ($fVhdl);"),
          cond = None
        )
        (sigName, sig +: (cSignals ++ tSignals ++ fSignals))
      case Equal(e1, e2) =>
        val (e1Vhdl, e1Signals) = makeVhdlExpr(e1)
        val (e2Vhdl, e2Signals) = makeVhdlExpr(e2)
        (s"($e1Vhdl) = ($e2Vhdl)", e1Signals ++ e2Signals)
      case LessThan(e1, e2) =>
        val (e1Vhdl, e1Signals) = makeVhdlExpr(e1)
        val (e2Vhdl, e2Signals) = makeVhdlExpr(e2)
        (s"($e1Vhdl) < ($e2Vhdl)", e1Signals ++ e2Signals)
      case Not(e) =>
        val (vhdlE, signals) = makeVhdlExpr(e)
        (s"not ($vhdlE)", signals)
      case And(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => makeVhdlExpr(e)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" and "), signals.flatten)
      case Or(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => makeVhdlExpr(e)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" or "), signals.flatten)

      case TupleAccess(StmNext(s: Param), IntCst(1)) =>
        (s"${s.name}_data", Seq())
      case _: StmLiteral => ???
      case _: StmBuild | _: StmNext | TupleAccess(_: StmNext, _) =>
        throw new IllegalArgumentException(
          s"Cannot generate hardware for ${e.getClass.getSimpleName} in this position."
        )
      case _: StmNextK =>
        throw new IllegalArgumentException(
          s"Cannot generate hardware for ${e.getClass.getSimpleName}."
        )

      case Tuple() => ("\"\"", Seq())
      case Tuple(elems @ _*) =>
        val (vhdlElems, signals) = elems.map(makeVhdlExpr).unzip
        val assignments = vhdlElems.zipWithIndex
          .map({ case (v, i) => s"i_$i => $v" })
          .mkString(", ")
        (s"($assignments)", signals.flatten)
      case TupleAccess(t, IntCst(i)) =>
        val (vhdlTuple, signals) = makeVhdlExpr(t)
        val tupSigName = Param("tupaccess_t")().name
        val tupSig = Signal(
          name = tupSigName,
          typ = makeVhdlType(t.typ),
          assignStmt = Some(s"$tupSigName <= $vhdlTuple;")
        )
        (s"${tupSig.name}.i_$i", tupSig +: signals)

      case _: Function => ???
      case _: FunCall  => ???

      case VecLiteral(elems @ _*) => ???
      case VecBuild(IntCst(n), f) =>
        val vecSigName = Param("vbuild")().name
        val (vec, vecBodySignals) = {
          val (elems, signals) = (0 until n)
            .map(i => {
              // TODO: Partial evaluation here kind of approximates IfThenElse
              //       being short-circuiting---the other branch will be
              //       discarded if the condition evaluates to a bool constant.
              //       But if the condition cannot be evaluated statically,
              //       then it's not the same thing.
              val body = PartialEvalPass.partialEval(FunCall(f, i)())
              makeVhdlExpr(body)
            })
            .unzip
          val assignments = elems.zipWithIndex
            .map({ case (e, i) => s"$i => $e" })
            .mkString(", ")
          (s"($assignments)", signals.flatten)
        }
        val vecSig = {
          Signal(
            name = vecSigName,
            typ = makeVhdlType(e.typ),
            assignStmt = Some(s"$vecSigName <= $vec;")
          )
        }
        (vecSig.name, vecSig +: vecBodySignals)
      case VecBuild(n, _) =>
        throw new IllegalArgumentException(
          s"VecBuild with non-constant size ($n) is not supported."
        )
      case VecAccess(v, i) =>
        val (vhdlVec, vecSignals) = makeVhdlExpr(v)
        val (vhdlIdx, idxSignals) = makeVhdlExpr(i)
        val vecSig = {
          val name = Param("vecaccess_v")().name
          Signal(
            name = name,
            typ = makeVhdlType(v.typ),
            assignStmt = Some(s"$name <= $vhdlVec;")
          )
        }
        (s"${vecSig.name}($vhdlIdx)", vecSig +: (vecSignals ++ idxSignals))

      case _: SyntaxSugar =>
        throw new IllegalArgumentException(
          s"Syntax sugar must be removed before hardware generation."
        )
    }
  }

  private def valueToVhdl(v: Expr): String = {
    ir.eval(v).tchk() match {
      case False     => "false"
      case True      => "true"
      case IntCst(k) => k.toString
      case Tuple()   => "\"\""
      case Tuple(elems @ _*) =>
        val assignments = elems.zipWithIndex
          .map({ case (e, i) => s"i_$i => ${valueToVhdl(e)}" })
          .mkString(", ")
        s"($assignments)"
      case VecLiteral(elems @ _*) =>
        val assignments =
          elems.zipWithIndex
            .map({ case (e, i) => s"$i => ${valueToVhdl(e)}" })
            .mkString(", ")
        s"($assignments)"
      case _ =>
        throw new IllegalArgumentException(
          s"Cannot convert value $v to a VHDL expression. Is it really a value?"
        )
    }
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

  // TODO: Organize these conversion methods a bit better? Move them to a separate class?

  /** @param e
    *   The VHDL expression to convert to a std_logic_vector
    * @param t
    *   The type of the original expression
    */
  private def toStdLogicVector(e: String, t: Type): String = {
    toStdLogicVector(e, makeVhdlType(t))
  }

  private def toStdLogicVector(e: String, t: VhdlType): String = {
    t match {
      case VhdlStdLogic       => s"(0 => $e)"
      case _: VhdlStdLogicVec => e
      case _ =>
        val f = toSlvConverterName(t)
        s"$f($e)"
    }
  }

  private def toSlvConverterName(t: VhdlType): String = {
    "to_std_logic_vector"
  }

  private def toSlvConverter(t: VhdlType): Option[VhdlFunction] = {
    t match {
      case VhdlBool           => Some(boolToSlvConverter)
      case VhdlInt            => Some(intToSlvConverter)
      case VhdlStdLogic       => None
      case _: VhdlStdLogicVec => None
      case t: VhdlRecord      => Some(recordToSlvConverter(t))
      case t: VhdlArray       => Some(arrayToSlvConverter(t))
    }
  }

  private def boolToSlvConverter: VhdlFunction = {
    VhdlFunction(
      name = toSlvConverterName(VhdlBool),
      args = Seq(("b", VhdlBool)),
      returnType = VhdlStdLogicVec(1),
      variables = Seq(("x", VhdlStdLogicVec(1))),
      body = """x := "1" when (b) else "0";
               |return x;
               |""".stripMargin.stripTrailing
    )
  }

  private def intToSlvConverter: VhdlFunction = {
    VhdlFunction(
      name = toSlvConverterName(VhdlInt),
      args = Seq(("n", VhdlInt)),
      returnType = VhdlStdLogicVec(32),
      variables = Seq(),
      body = "return std_logic_vector(to_signed(n, 32));"
    )
  }

  private def recordToSlvConverter(tup: VhdlRecord): VhdlFunction = {
    VhdlFunction(
      name = toSlvConverterName(tup),
      args = Seq(("x", tup)),
      returnType = VhdlStdLogicVec(tup.bitWidth),
      variables = Seq(),
      body = if (tup.fieldTypes.isEmpty) {
        "return \"\";"
      } else {
        val out = tup.fieldTypes.zipWithIndex
          .map({ case (t, i) => toStdLogicVector(s"x.i_$i", t) })
          .mkString(" & ")
        s"return $out;"
      }
    )
  }

  private def arrayToSlvConverter(arr: VhdlArray): VhdlFunction = {
    VhdlFunction(
      name = toSlvConverterName(arr),
      args = Seq(("x", arr)),
      returnType = VhdlStdLogicVec(arr.bitWidth),
      variables = Seq(),
      body = {
        val out = (0 until arr.n)
          .map(i => toStdLogicVector(s"x($i)", arr.t))
          .mkString(" & ")
        s"return $out;"
      }
    )
  }

  private def fromStdLogicVector(e: String, t: Type): String = {
    fromStdLogicVector(e, makeVhdlType(t))
  }

  private def fromStdLogicVector(e: String, t: VhdlType): String = {
    t match {
      case _: VhdlStdLogicVec => e
      case _ =>
        val f = fromSlvConverterName(t)
        s"$f($e)"
    }
  }

  private def fromSlvConverterName(t: VhdlType): String = {
    // It seems like the VHDL compiler can tell which overload to use based on
    // the return type (since the result of this conversion is always assigned
    // to a signal or return value, not converted back to a std_logic_vector).
    // If at some point that is no longer true, just change this name to
    // include the target type.
    "from_std_logic_vector"
  }

  private def fromSlvConverter(t: VhdlType): Option[VhdlFunction] = {
    t match {
      case VhdlStdLogic       => Some(stdLogicFromSlvConverter)
      case VhdlBool           => Some(boolFromSlvConverter)
      case VhdlInt            => Some(intFromSlvConverter)
      case _: VhdlStdLogicVec => None
      case t: VhdlRecord      => Some(recordFromSlvConverter(t))
      case t: VhdlArray       => Some(arrayFromSlvConverter(t))
    }
  }

  private def stdLogicFromSlvConverter: VhdlFunction = {
    VhdlFunction(
      name = fromSlvConverterName(VhdlStdLogic),
      args = Seq(("v", VhdlStdLogicVec(1))),
      returnType = VhdlStdLogic,
      variables = Seq(),
      body = "return v(0);"
    )
  }

  private def boolFromSlvConverter: VhdlFunction = {
    VhdlFunction(
      name = fromSlvConverterName(VhdlBool),
      args = Seq(("v", VhdlStdLogicVec(1))),
      returnType = VhdlBool,
      variables = Seq(),
      body = "return v = \"1\";"
    )
  }

  private def intFromSlvConverter: VhdlFunction = {
    VhdlFunction(
      name = fromSlvConverterName(VhdlInt),
      args = Seq(("v", VhdlStdLogicVec(32))),
      returnType = VhdlInt,
      variables = Seq(),
      body = "return to_integer(signed(v));"
    )
  }

  private def recordFromSlvConverter(tup: VhdlRecord): VhdlFunction = {
    VhdlFunction(
      name = fromSlvConverterName(tup),
      args = Seq(("v", VhdlStdLogicVec(tup.bitWidth))),
      returnType = tup,
      variables = Seq(),
      body = if (tup.fieldTypes.isEmpty) {
        "return \"\";"
      } else {
        val elems = tup.fieldTypes.zipWithIndex
          .map({ case (t, i) =>
            val bitsBefore = tup.fieldTypes.take(i).map(t => t.bitWidth).sum
            val bitsInside = t.bitWidth
            val msb = tup.bitWidth - 1 - bitsBefore
            val lsb = msb - bitsInside + 1
            fromStdLogicVector(s"v($msb downto $lsb)", t)
          })
        val assignments = elems.zipWithIndex
          .map({ case (v, i) => s"i_$i => $v" })
          .mkString(", ")
        s"return ($assignments);"
      }
    )
  }

  private def arrayFromSlvConverter(vec: VhdlArray): VhdlFunction = {
    VhdlFunction(
      name = fromSlvConverterName(vec),
      args = Seq(("v", VhdlStdLogicVec(vec.bitWidth))),
      returnType = vec,
      variables = Seq(),
      body = {
        val elems = (0 until vec.n).map(i => {
          val msb = vec.bitWidth - 1 - i * vec.t.bitWidth
          val lsb = msb - vec.t.bitWidth + 1
          fromStdLogicVector(s"v($msb downto $lsb)", vec.t)
        })
        val assignments = elems.zipWithIndex
          .map({ case (v, i) => s"$i => $v" })
          .mkString(", ")
        s"return ($assignments);"
      }
    )
  }

  private def makeVhdlType(t: Type): VhdlType = {
    t match {
      case TyInt               => VhdlInt
      case TyBool              => VhdlBool
      case TyTuple(ts @ _*)    => VhdlRecord(ts.map(makeVhdlType))
      case TyVec(t, IntCst(n)) => VhdlArray(n, makeVhdlType(t))
      case t =>
        throw new IllegalArgumentException(
          s"Cannot convert type $t to a VHDL type."
        )
    }
  }

  private def getBitWidth(t: Type): Int = makeVhdlType(t).bitWidth
}
