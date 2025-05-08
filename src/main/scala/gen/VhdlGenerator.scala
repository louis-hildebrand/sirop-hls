package gen

import debug.{PrettyPrinter, indent}
import ir._
import opt.PartialEvalPass

import java.nio.file.{Files, Path}

object VhdlGenerator {

  /** Create a VHDL design for the given expression and save it in the given
    * directory.
    *
    * @param s
    *   The expression for which to generate VHDL
    * @param dir
    *   The directory in which to save the design
    * @return
    */
  def emitVhdl(s: StmBuild, dir: Path): Int = {
    require(
      s.typ != Missing,
      "Expression must be type checked before hardware generation."
    )
    require(
      !s.contains(classOf[SyntaxSugar]),
      "Expression must be lowered before hardware generation."
    )

    val (topComponent, bitWidth) = stmBuildToVhdl(s, "top")
    val typesToDefine =
      findTypesUsedIn(topComponent).flatMap(t => t.descendants + t)

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
    val toSlvFunctions =
      types.flatMap(t => VhdlConversionGenerator.toSlvConverter(t))
    val fromSlvFunctions =
      types.flatMap(t => VhdlConversionGenerator.fromSlvConverter(t))
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

  private def stmBuildToVhdl(
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
    val (nVhdl, nSignals) = exprToVhdl(s.n)
    val (validVhdl, validSignals) = exprToVhdl(valid)
    val (dataVhdl, dataSignals) = exprToVhdl(data)

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
        val (nextVhdl, nextSignals) = exprToVhdl(next)
        val sig = Signal(
          name = x.name,
          typ = VhdlType(x.typ),
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
          val (vhdl, sig) = exprToVhdl(c)
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
          stmBuildToVhdl(z.asInstanceOf[StmBuild], componentName)
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
          VhdlConversionGenerator.fromStdLogicVector(
            s"${x.name}_data_raw",
            VhdlType(dataType)
          )
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
            typ = VhdlType(dataType),
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
    val dataInternalSlv =
      VhdlConversionGenerator.toStdLogicVector(
        "data_internal",
        VhdlType(data.typ)
      )
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
        typ = VhdlType(data.typ),
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

  /** Convert an expression to VHDL.
    *
    * @param e
    *   The expression to convert
    * @return
    *   A VHDL expression along with the signals required by that expression
    */
  private def exprToVhdl(e: Expr): (String, Seq[Signal]) = {
    e match {
      case x: Param  => (x.name, Seq())
      case IntCst(n) => (n.toString, Seq())
      case Sum(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => exprToVhdl(e)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" + "), signals.flatten)
      case Prod(factors @ _*) =>
        val (vhdlFactors, signals) =
          factors.map(e => exprToVhdl(e)).unzip
        (vhdlFactors.map(x => s"($x)").mkString(" * "), signals.flatten)
      case _: Div => ???
      case _: Mod => ???
      case True   => ("true", Seq())
      case False  => ("false", Seq())
      case ite @ IfThenElse(c, t, f) =>
        val (cVhdl, cSignals) = exprToVhdl(c)
        val (tVhdl, tSignals) = exprToVhdl(t)
        val (fVhdl, fSignals) = exprToVhdl(f)
        val sigName = Param("ite")().name
        val sig = Signal(
          sigName,
          typ = VhdlType(ite.typ),
          init = None,
          assignStmt =
            Some(s"$sigName <= ($tVhdl) when ($cVhdl) else ($fVhdl);"),
          cond = None
        )
        (sigName, sig +: (cSignals ++ tSignals ++ fSignals))
      case Equal(e1, e2) =>
        val (e1Vhdl, e1Signals) = exprToVhdl(e1)
        val (e2Vhdl, e2Signals) = exprToVhdl(e2)
        (s"($e1Vhdl) = ($e2Vhdl)", e1Signals ++ e2Signals)
      case LessThan(e1, e2) =>
        val (e1Vhdl, e1Signals) = exprToVhdl(e1)
        val (e2Vhdl, e2Signals) = exprToVhdl(e2)
        (s"($e1Vhdl) < ($e2Vhdl)", e1Signals ++ e2Signals)
      case Not(e) =>
        val (vhdlE, signals) = exprToVhdl(e)
        (s"not ($vhdlE)", signals)
      case And(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => exprToVhdl(e)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" and "), signals.flatten)
      case Or(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => exprToVhdl(e)).unzip
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
        val (vhdlElems, signals) = elems.map(exprToVhdl).unzip
        val assignments = vhdlElems.zipWithIndex
          .map({ case (v, i) => s"i_$i => $v" })
          .mkString(", ")
        (s"($assignments)", signals.flatten)
      case TupleAccess(t, IntCst(i)) =>
        val (vhdlTuple, signals) = exprToVhdl(t)
        val tupSigName = Param("tupaccess_t")().name
        val tupSig = Signal(
          name = tupSigName,
          typ = VhdlType(t.typ),
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
              exprToVhdl(body)
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
            typ = VhdlType(e.typ),
            assignStmt = Some(s"$vecSigName <= $vec;")
          )
        }
        (vecSig.name, vecSig +: vecBodySignals)
      case VecBuild(n, _) =>
        throw new IllegalArgumentException(
          s"VecBuild with non-constant size ($n) is not supported."
        )
      case VecAccess(v, i) =>
        val (vhdlVec, vecSignals) = exprToVhdl(v)
        val (vhdlIdx, idxSignals) = exprToVhdl(i)
        val vecSig = {
          val name = Param("vecaccess_v")().name
          Signal(
            name = name,
            typ = VhdlType(v.typ),
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

  private def getBitWidth(t: Type): Int = VhdlType(t).bitWidth
}
