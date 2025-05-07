package gen

import debug.PrettyPrinter
import ir._
import opt.PartialEvalPass

import java.nio.file.{Files, Path}

private trait Port
private case class InPort(
    name: String,
    typ: String
) extends Port

private case class OutPort(
    name: String,
    typ: String,
    assign: String
) extends Port

private case class Signal(
    name: String,
    typ: String,
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
      inPorts.map(p => s"${p.name} : in ${p.typ}")
        ++ outPorts.map(p => s"${p.name} : out ${p.typ}")
    ).sortBy(x => x)
    val sigDecls = signals
      .map(s => {
        val str1 = s"signal ${s.name} : ${s.typ}"
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
         |use work.helpers.all;
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

    val designDir = dir.resolve("design")
    Files.createDirectory(designDir)
    emitHelperPackage(designDir)
    emitVhdlFiles(topComponent, designDir)

    bitWidth
  }

  private def emitHelperPackage(dir: Path): Unit = {
    val contents =
      s"""library IEEE;
         |use IEEE.std_logic_1164.all;
         |use IEEE.numeric_std.all;
         |
         |package helpers is
         |    function bool2sl (b : in boolean) return std_logic;
         |    function bool2slv (b : boolean) return std_logic_vector;
         |    function sl2bool (x : in std_logic) return boolean;
         |    function slv2bool (v : in std_logic_vector(0 downto 0)) return boolean;
         |    function int2slv (n : integer) return std_logic_vector;
         |    function slv2int (v : in std_logic_vector(31 downto 0)) return integer;
         |end package;
         |
         |package body helpers is
         |    function bool2sl (b : in boolean) return std_logic is
         |        variable x : std_logic;
         |    begin
         |        x := '1' when (b) else '0';
         |        return x;
         |    end;
         |
         |    function bool2slv (b : in boolean) return std_logic_vector is
         |        variable x : std_logic_vector(0 downto 0);
         |    begin
         |        x := "1" when (b) else "0";
         |        return x;
         |    end;
         |
         |    function sl2bool (x : in std_logic) return boolean is
         |    begin
         |        return x = '1';
         |    end;
         |
         |    function slv2bool (v : in std_logic_vector(0 downto 0)) return boolean is
         |    begin
         |        return v = "1";
         |    end;
         |
         |    function int2slv (n : integer) return std_logic_vector is
         |    begin
         |        return std_logic_vector(to_signed(n, 32));
         |    end;
         |
         |    function slv2int (v : in std_logic_vector(31 downto 0)) return integer is
         |    begin
         |        return to_integer(signed(v));
         |    end;
         |end package body;
         |""".stripMargin

    val file = dir.resolve("helpers.vhd")
    Files.writeString(file, contents)
  }

  private def emitVhdlFiles(c: VhdlComponent, dir: Path): Unit = {
    val file = dir.resolve(s"${c.name}.vhd")
    Files.writeString(file, c.vhdl)
    for ((child, _) <- c.children) {
      emitVhdlFiles(child, dir)
    }
  }

  private def stmBuildToComponent(
      s: StmBuild,
      name: String
  ): (VhdlComponent, Int) = {
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
      typ = "boolean",
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
          Signal(name = s"${x.name}_data_valid_raw", typ = "std_logic"),
          Signal(
            name = s"${x.name}_data_valid",
            typ = "boolean",
            assignStmt = Some(
              s"${x.name}_data_valid <= sl2bool(${x.name}_data_valid_raw);"
            )
          ),
          Signal(
            name = s"${x.name}_data_ready_raw",
            typ = "std_logic",
            assignStmt = Some(
              s"${x.name}_data_ready_raw <= bool2sl(${x.name}_data_ready);"
            )
          ),
          Signal(
            name = s"${x.name}_data_ready",
            typ = "boolean",
            assignStmt = Some(
              s"${x.name}_data_ready <= ($readyCond) and can_update_acc;"
            )
          ),
          Signal(
            name = s"${x.name}_data_raw",
            typ = s"std_logic_vector(${bitWidth - 1} downto 0)"
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
      InPort("clk", "std_logic"),
      InPort("data_ready", "std_logic")
    )
    val dataInternalStdLogicVec = toStdLogicVector("data_internal", data.typ)
    val defaultOutPorts = Seq(
      OutPort(
        name = "data",
        typ = s"std_logic_vector(${bitWidth - 1} downto 0)",
        assign =
          s"(others => '0') when not transfer_ok else $dataInternalStdLogicVec"
      ),
      OutPort(
        name = "data_valid",
        typ = "std_logic",
        assign = "bool2sl(data_valid_internal)"
      )
    )
    val defaultSignals = Seq(
      Signal(
        "num_outputs",
        typ = "integer",
        init = Some("0"),
        assignStmt = Some("num_outputs <= num_outputs + 1;"),
        cond = Some("transfer_ok")
      ),
      Signal(
        "data_valid_internal",
        typ = "boolean",
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
        typ = "boolean",
        init = None,
        assignStmt = Some(
          "transfer_ok <= sl2bool(data_ready) and data_valid_internal;"
        ),
        cond = None
      ),
      Signal(
        "can_update_acc",
        typ = "boolean",
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

      case Tuple(elems @ _*) =>
        val (vhdlElems, signals) = elems.map(makeVhdlExpr).unzip
        assert(vhdlElems.length == elems.length)
        val vhdlElemsStdLogicVec = vhdlElems
          .zip(elems)
          .map({ case (vhdl, e) => toStdLogicVector(vhdl, e.typ) })
        val concat = vhdlElemsStdLogicVec
          .foldLeft("\"\"")({ case (acc, e) => s"$acc & ($e)" })
        (concat, signals.flatten)
      case ta @ TupleAccess(t, IntCst(i)) =>
        val tupTyp = t.typ.asInstanceOf[TyTuple]
        val bitWidth = getBitWidth(tupTyp)
        val bitsBefore = tupTyp.ts.take(i).map(t => getBitWidth(t)).sum
        val bitsInside = getBitWidth(tupTyp.ts(i))
        val msb = bitWidth - 1 - bitsBefore
        val lsb = msb - bitsInside + 1
        val (vhdlTuple, signals) = makeVhdlExpr(t)
        val tupleSigName = Param("tupaccess_t")().name
        val tupleSig = Signal(
          name = tupleSigName,
          typ = makeVhdlType(t.typ),
          assignStmt = Some(s"$tupleSigName <= $vhdlTuple;")
        )
        val vhdlTupAccess =
          fromStdLogicVector(s"${tupleSig.name}($msb downto $lsb)", ta.typ)
        val sigName = Param("tupaccess")().name
        val sig = Signal(
          name = sigName,
          typ = makeVhdlType(ta.typ),
          init = Some(valueToVhdl(Default(ta.typ).lower())),
          assignStmt = Some(s"$sigName <= $vhdlTupAccess;"),
          cond = None
        )
        (sig.name, tupleSig +: sig +: signals)

      case _: Function => ???
      case _: FunCall  => ???

      case VecLiteral(elems @ _*) =>
        val (elemsVhdl, signals) = elems.map(makeVhdlExpr).unzip
        assert(elemsVhdl.length == elems.length)
        val elemsStdLogicVec = elemsVhdl
          .zip(elems)
          .map({ case (vhdl, e) => toStdLogicVector(vhdl, e.typ) })
        (
          elemsStdLogicVec.map(x => s"($x)").mkString(" & "),
          signals.flatten
        )
      case VecBuild(IntCst(n), f) =>
        val typ = f.typ.asInstanceOf[TyArrow].t2
        // TODO: It would be nice to avoid partial evaluation here
        val elems = (0 until n).map(i =>
          PartialEvalPass.partialEval(FunCall(f, i)()).tchk().lower()
        )
        val e = VecLiteral(elems: _*)(TyVec(typ, n))
        makeVhdlExpr(e)
      case VecBuild(n, _) =>
        throw new IllegalArgumentException(
          s"VecBuild with non-constant size ($n) is not supported."
        )
      case va @ VecAccess(v, i) =>
        val (elemBitWidth, n) = v.typ match {
          case TyVec(t, IntCst(n)) if n > 0 => (getBitWidth(t), n)
          case t =>
            throw new IllegalArgumentException(
              s"Invalid type for vector in VecAccess: $t. Only non-empty, constant-size vectors are allowed."
            )
        }
        val totBitWidth = n * elemBitWidth
        val (vhdlVec, vecSignals) = makeVhdlExpr(v)
        val (vhdlIdx, idxSignals) = makeVhdlExpr(i)
        val vecSigName = Param("vecaccess_v")().name
        val vecSig = Signal(
          name = vecSigName,
          typ = makeVhdlType(v.typ),
          assignStmt = Some(s"$vecSigName <= $vhdlVec;")
        )
        val sigName = Param("vecaccess")().name
        val cases = (0 until n).map(i => {
          val cond = if (i == n - 1) "others" else i.toString
          val msb = totBitWidth - 1 - i * elemBitWidth
          val lsb = msb - elemBitWidth + 1
          val value = s"${vecSig.name}($msb downto $lsb)"
          s"($value) when $cond"
        })
        val assign =
          s"with ($vhdlIdx) select $sigName <= ${cases.mkString(", ")};"
        val sig = Signal(
          name = sigName,
          typ = makeVhdlType(va.typ),
          init = None,
          assignStmt = Some(assign),
          cond = None
        )
        (sigName, vecSig +: sig +: (vecSignals ++ idxSignals))

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
      case _: Tuple | _: VecLiteral =>
        valueToStdLogicVector(v, getBitWidth(v.typ).toString)
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

  /** @param e
    *   The VHDL expression to convert to a std_logic_vector
    * @param t
    *   The type of the original expression
    */
  private def toStdLogicVector(
      e: String,
      t: Type
  ): String = {
    // TODO: Is the length really necessary?
    // TODO: Get rid of the second return value
    t match {
      case TyInt                 => s"int2slv($e)"
      case TyBool                => s"bool2slv($e)"
      case _: TyTuple | _: TyVec =>
        // Tuples and vectors are already represented using std_logic_vector
        // TODO: But might it be necessary to resize?
        e
      case t =>
        throw new IllegalArgumentException(
          s"Cannot convert data of type $t to a std_logic_vector."
        )
    }
  }

  private def fromStdLogicVector(e: String, t: Type): String = {
    t match {
      case TyBool                => s"slv2bool($e)"
      case TyInt                 => s"slv2int($e)"
      case _: TyTuple | _: TyVec =>
        // Tuples and vectors are already represented using std_logic_vector
        // TODO: But might it be necessary to resize?
        e
      case t =>
        throw new IllegalArgumentException(
          s"Cannot convert data of type std_logic_vector to $t."
        )
    }
  }

  private def makeVhdlType(t: Type): String = {
    t match {
      case TyInt  => "integer"
      case TyBool => "boolean"
      case _: TyTuple | _: TyVec =>
        val bitWidth = getBitWidth(t)
        s"std_logic_vector(${bitWidth - 1} downto 0)"
      case t =>
        throw new IllegalArgumentException(
          s"Cannot convert type $t to a VHDL type."
        )
    }
  }

  private def getBitWidth(t: Type): Int = {
    t match {
      case TyBool              => 1
      case TyInt               => 32
      case TyTuple(ts @ _*)    => ts.map(t => getBitWidth(t)).sum
      case TyVec(t, IntCst(n)) => getBitWidth(t) * n
      case t =>
        throw new IllegalArgumentException(
          s"Cannot find bit width for type $t."
        )
    }
  }
}
