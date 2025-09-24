package mhir.gen.vhdl

import mhir.debug.indent
import mhir.ir.{Expr, ExprPrinter}
import os.Path

private[vhdl] sealed trait Decl {
  def vhdlDecl: String
}

private[vhdl] sealed trait VhdlFunctionMode
private[vhdl] object PureFunction extends VhdlFunctionMode
private[vhdl] object ImpureFunction extends VhdlFunctionMode

private[vhdl] case class VhdlFunction(
    name: String,
    args: Seq[(String, VhdlType)],
    returnType: VhdlType,
    decls: Seq[Decl],
    ret: String,
    mode: VhdlFunctionMode = PureFunction
) extends Decl {
  require(
    decls.forall(d => !d.isInstanceOf[Signal]),
    "Signals are not allowed in VHDL functions."
  )

  private def signature: String = {
    val pureOrImpure = mode match {
      case PureFunction   => "pure"
      case ImpureFunction => "impure"
    }
    val argList = args
      .map({ case (x, t) =>
        val tStr = t match {
          case VhdlStdLogicVec(n, _) =>
            if (n < 0) t.vhdlTypeMark else t.vhdlName
          case VhdlArray(n, _) => if (n < 0) t.vhdlTypeMark else t.vhdlName
          case VhdlSigned(n)   => if (n < 0) t.vhdlTypeMark else t.vhdlName
          case VhdlUnsigned(n) => if (n < 0) t.vhdlTypeMark else t.vhdlName
          case _               => t.vhdlName
        }
        s"$x : in $tStr"
      })
      .mkString("; ")
    s"$pureOrImpure function $name ($argList) return ${returnType.vhdlTypeMark}"
  }

  def vhdlSignature: String = s"$signature;"

  def vhdlImpl: String = {
    val declsStr = if (decls.isEmpty) {
      "-- No variables needed"
    } else {
      decls.map(x => x.vhdlDecl).mkString("\n")
    }
    val varAssignments = decls
      .flatMap({
        case x: VhdlVariable             => Some(x.assignStmt)
        case _: VhdlFunction | _: Signal => None
      })
      .mkString("\n")
    val body = s"$varAssignments\nreturn $ret;"
    s"""$signature is
       |${indent(declsStr)}
       |begin
       |${indent(body)}
       |end;
       |""".stripMargin.stripTrailing
  }

  override def vhdlDecl: String = vhdlImpl
}

private[vhdl] trait Port
private[vhdl] case class InPort(
    name: String,
    typ: VhdlType
) extends Port

private[vhdl] case class OutPort(
    name: String,
    typ: VhdlType,
    assign: Option[String]
) extends Port

private[vhdl] sealed trait VarOrSigDecl extends Decl {
  val name: String
  val typ: VhdlType
}

private[vhdl] case class VhdlVariable(
    name: String,
    typ: VhdlType,
    assignStmt: String
) extends VarOrSigDecl {
  override def vhdlDecl: String = s"variable $name : ${typ.vhdlName};"
}

/** A signal within a VHDL design
  *
  * @param category
  *   a user-readable category for this signal. This simply allows the signals
  *   to be grouped in a readable way in the VHDL code; it does not change the
  *   behaviour of the signal.
  * @param name
  *   the name of the signal.
  * @param typ
  *   the type of the signal.
  * @param init
  *   the initial value of the signal.
  * @param assignStmt
  *   the statement which updates the signal (see also: [[cond]]).
  * @param cond
  *   the condition under which the signal is updated. If [[None]], then
  *   [[assignStmt]] will be interpreted as a combinational statement. If
  *   [[Some]], then [[assignStmt]] will be interpreted as a sequential
  *   statement which is run if [[cond]] is true.
  */
private[vhdl] case class Signal(
    category: String,
    name: String,
    typ: VhdlType,
    init: Option[String] = None,
    assignStmt: Option[String] = None,
    cond: Option[String] = None
) extends VarOrSigDecl {
  override def vhdlDecl: String = {
    val str1 = s"signal $name : ${typ.vhdlName}"
    val str2 = init match {
      case Some(z) => s"$str1 := $z"
      case None    => str1
    }
    s"$str2;"
  }
}

/** The arguments to use for a VHDL `port map`.
  *
  * @param map
  *   a map from port names to signal names.
  */
private[vhdl] case class PortMap(map: Map[String, String])

/** One component (entity + architecture) in VHDL.
  */
private[vhdl] sealed trait VhdlComponent

/** The predefined `stm_nop` component.
  *
  * @param bitWidth
  *   the bit width to use when instantiating this component.
  */
private[vhdl] case class StmNoOpComponent(bitWidth: Int) extends VhdlComponent {
  val VhdName: String = "stm_nop.vhd"
}

/** The predefined `letstm_buf` component.
  *
  * @param bitWidth
  *   the value for the `BIT_WIDTH` generic argument.
  * @param bufSize
  *   the value for the `BUF_SIZE` generic argument.
  * @param numConsumers
  *   the value for the `N_CONSUMERS` generic argument.
  */
private[vhdl] case class LetStmBufComponent(
    bitWidth: Int,
    bufSize: Int,
    numConsumers: Int
) extends VhdlComponent {
  val VhdNames: Set[String] =
    Set("letstm_buf.vhd", "dual_port_ram.vhd", "multi_consumer_ram.vhd")
}

/** A custom component, like for a [[mhir.ir.StmBuild]].
  *
  * @param name
  *   name of the component
  * @param inPorts
  *   input ports
  * @param outPorts
  *   output ports
  * @param signals
  *   internal signals
  * @param functions
  *   the functions that must be declared within this component.
  * @param children
  *   other components that must be instantiated by this component.
  */
private[vhdl] case class CustomVhdlComponent(
    expr: Option[Expr],
    name: String,
    inPorts: Seq[InPort],
    outPorts: Seq[OutPort],
    signals: Seq[Signal],
    functions: Seq[VhdlFunction],
    children: Seq[VhdlEntityInstantiation]
) extends VhdlComponent {
  checkPortMaps()

  private def checkPortMaps(): Unit = {
    for (VhdlEntityInstantiation(name, child, map) <- children) {
      val expectedPorts = child match {
        case child: CustomVhdlComponent =>
          (child.inPorts.map(_.name) ++ child.outPorts.map(_.name)).toSet
        case _: StmNoOpComponent =>
          Set(
            "c_data",
            "c_valid",
            "c_ready",
            "p_data",
            "p_valid",
            "p_ready"
          )
        case _: LetStmBufComponent =>
          Set(
            "clk",
            "p_data",
            "p_valid",
            "p_ready",
            "c_data",
            "c_valid",
            "c_ready"
          )
      }
      val actualPorts = map.map.keySet
      assert(
        expectedPorts == actualPorts,
        s"wrong ports for component $name"
          + s" (expected $expectedPorts but got $actualPorts)"
      )
    }
  }

  def writeVhdl(f: Path): Unit = {
    writeHeader(f)
    writeEntity(f)
    writeArchitecture(f)
  }

  private def writeHeader(f: Path): Unit = {
    this.expr match {
      case Some(expr) =>
        val comment = ExprPrinter
          .display(expr, maxWidth = 120)
          .split("\n")
          .map(x => s"-- $x")
          .mkString("\n")
        os.write.append(f, comment)
      case None => ()
    }
    os.write.append(
      f,
      """
        |library IEEE;
        |use IEEE.std_logic_1164.all;
        |use IEEE.numeric_std.all;
        |use work.conversions.all;
        |use work.typedefs.all;
        |""".stripMargin
    )
  }

  private def writeEntity(f: Path): Unit = {
    val portDecls =
      (inPorts.map(p => s"${p.name} : in ${p.typ.vhdlName}")
        ++ outPorts.map(p => s"${p.name} : out ${p.typ.vhdlName}"))
        .sortBy(x => x)
    os.write.append(
      f,
      s"""entity $name is
         |port (
         |    ${portDecls.mkString(";\n    ")}
         |);
         |end;
         |""".stripMargin
    )
  }

  private def writeArchitecture(f: Path): Unit = {
    os.write.append(f, s"architecture arch of $name is\n")

    val signalCategories = signals
      .groupBy(s => s.category)
      .map({ case (name, signals) => name -> signals.sortBy(s => s.name) })
      .toSeq
      .sortBy({ case (cat, _) => cat })

    for ((categoryName, signals) <- signalCategories) {
      val block = signals.map(s => s.vhdlDecl).sortBy(x => x).mkString("\n")
      val str = indent(s"-- $categoryName\n$block") + "\n\n"
      os.write.append(f, str)
    }

    val funDecls = functions.map(f => f.vhdlDecl).mkString("\n\n")
    os.write.append(f, indent(funDecls) + "\n")

    os.write.append(f, "begin\n")

    val portMaps = children
      .map({ case VhdlEntityInstantiation(name, c, pm) =>
        val assignments =
          pm.map
            .map({ case (k, v) => s"$k => $v" })
            .toSeq
            .sorted
            .mkString(",\n" + " ".repeat(16))
        c match {
          case c: CustomVhdlComponent =>
            s"""    $name : entity work.${c.name}
               |            port map(
               |                $assignments);
               |""".stripMargin.stripTrailing
          case c: StmNoOpComponent =>
            s"""    $name : entity work.stm_nop
               |            generic map(BIT_WIDTH => ${c.bitWidth})
               |            port map(
               |                $assignments);
               |""".stripMargin.stripTrailing
          case c: LetStmBufComponent =>
            s"""    $name : entity work.letstm_buf
               |            generic map(BIT_WIDTH => ${c.bitWidth}, BUF_SIZE => ${c.bufSize}, N_CONSUMERS => ${c.numConsumers})
               |            port map(
               |                $assignments);
               |""".stripMargin
        }
      })
      .sortBy(x => x)
      .mkString("\n")
    os.write.append(f, portMaps + "\n\n")

    for ((categoryName, signals) <- signalCategories) {
      val stmts = signals
        .filter(s => s.cond.isEmpty && s.assignStmt.isDefined)
        .map(s => s.assignStmt.get)
      // Print each statement one-by-one to avoid Java heap overflow
      // This tends to happen with very large expressions (e.g., designs that
      // have not been optimized at all - not even partial evaluation)
      if (stmts.nonEmpty) {
        os.write.append(f, s"    -- $categoryName\n")
        for (s <- stmts) {
          os.write.append(f, indent(s) + "\n")
        }
      }
    }

    val outPortAssignments = {
      val block = outPorts
        .filter(p => p.assign.isDefined)
        .map(p => s"${p.name} <= ${p.assign.get};")
        .mkString("\n")
      if (block.isBlank) {
        Seq()
      } else {
        Seq(s"-- Output ports\n$block")
      }
    }
    os.write.append(f, indent(outPortAssignments.mkString("\n\n")) + "\n\n")

    val clkStmts = signalCategories
      .flatMap({ case (categoryName, signals) =>
        val stmts = signals
          .filter(s => s.cond.nonEmpty && s.assignStmt.isDefined)
          .sortBy(s => s.name)
          .map(s => {
            val cond = s.cond.get
            cond match {
              case "true" => s.assignStmt.get
              case _ => s"if ($cond) then\n${indent(s.assignStmt.get)}\nend if;"
            }
          })
        if (stmts.isEmpty) {
          None
        } else {
          Some(s"-- $categoryName\n${stmts.mkString("\n")}")
        }
      })
      .mkString("\n\n")
    val process = if (clkStmts.isBlank) {
      ""
    } else {
      s"""process
         |begin
         |    wait until rising_edge(clk);
         |
         |${indent(clkStmts)}
         |end process ;
         |""".stripMargin.stripTrailing
    }
    os.write.append(f, s"${indent(process)}")

    os.write.append(f, "end;\n")
  }
}

/** An instantiation of a VHDL entity.
  *
  * @param name
  *   the name to use for this instance.
  * @param c
  *   the component to instantiate.
  * @param args
  *   the arguments to pass to the component.
  */
private[vhdl] case class VhdlEntityInstantiation(
    name: String,
    c: VhdlComponent,
    args: PortMap
)
