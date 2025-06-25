package gen

import debug.indent

private[gen] sealed trait Decl {
  def vhdlDecl: String
}

private[gen] sealed trait VhdlFunctionMode
private[gen] object PureFunction extends VhdlFunctionMode
private[gen] object ImpureFunction extends VhdlFunctionMode

private[gen] case class VhdlFunction(
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
          case VhdlStdLogicVec(n) => if (n < 0) t.vhdlTypeMark else t.vhdlName
          case VhdlArray(n, _)    => if (n < 0) t.vhdlTypeMark else t.vhdlName
          case VhdlSigned(n)      => if (n < 0) t.vhdlTypeMark else t.vhdlName
          case VhdlUnsigned(n)    => if (n < 0) t.vhdlTypeMark else t.vhdlName
          case _                  => t.vhdlName
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

private[gen] trait Port
private[gen] case class InPort(
    name: String,
    typ: VhdlType
) extends Port

private[gen] case class OutPort(
    name: String,
    typ: VhdlType,
    assign: Option[String]
) extends Port

private[gen] sealed trait VarOrSigDecl extends Decl {
  val name: String
  val typ: VhdlType
}

private[gen] case class VhdlVariable(
    name: String,
    typ: VhdlType,
    assignStmt: String
) extends VarOrSigDecl {
  override def vhdlDecl: String = s"variable $name : ${typ.vhdlName};"
}

private[gen] case class Signal(
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

private[gen] case class PortMap(map: Map[String, String])

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
private[gen] case class VhdlComponent(
    comment: String,
    name: String,
    inPorts: Seq[InPort],
    outPorts: Seq[OutPort],
    signals: Seq[Signal],
    functions: Seq[VhdlFunction],
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
    val signalCategories = signals
      .groupBy(s => s.category)
      .map({ case (name, signals) => name -> signals.sortBy(s => s.name) })
      .toSeq
      .sortBy({ case (cat, _) => cat })
    val portDecls = (
      inPorts.map(p => s"${p.name} : in ${p.typ.vhdlName}")
        ++ outPorts.map(p => s"${p.name} : out ${p.typ.vhdlName}")
    ).sortBy(x => x)
    val sigDecls = signalCategories
      .map({ case (categoryName, signals) =>
        val block = signals.map(s => s.vhdlDecl).sortBy(x => x).mkString("\n")
        s"-- $categoryName\n$block"
      })
      .mkString("\n\n")
    val funDecls = functions.map(f => f.vhdlDecl).mkString("\n\n")
    val portMaps = children
      .map({ case (c, pm) =>
        val assignments =
          pm.map.map({ case (k, v) => s"$k => $v" }).mkString(", ")
        s"${c.name.toUpperCase} : entity work.${c.name} port map($assignments);"
      })
      .sortBy(x => x)
    val combStmts = {
      val signalAssignments =
        signalCategories.flatMap({ case (categoryName, signals) =>
          val stmts = signals
            .filter(s => s.cond.isEmpty && s.assignStmt.isDefined)
            .map(s => s.assignStmt.get)
          if (stmts.isEmpty) {
            None
          } else {
            Some(s"-- $categoryName\n${stmts.mkString("\n")}")
          }
        })
      val outPortAssignments = {
        val block = outPorts
          .filter(p => p.assign.isDefined)
          .map(p => s"${p.name} <= ${p.assign.get};")
          .mkString("\n")
        Seq(s"-- Output ports\n$block")
      }
      (signalAssignments ++ outPortAssignments).mkString("\n\n")
    }
    val clkStmts = signalCategories
      .flatMap({ case (categoryName, signals) =>
        val stmts = signals
          .filter(s => s.cond.nonEmpty && s.assignStmt.isDefined)
          .sortBy(s => s.name)
          .map(s =>
            s"if (${s.cond.get}) then\n${indent(s.assignStmt.get)}\nend if;"
          )
        if (stmts.isEmpty) {
          None
        } else {
          Some(s"-- $categoryName\n${stmts.mkString("\n")}")
        }
      })
      .mkString("\n\n")
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
         |${indent(sigDecls)}
         |
         |${indent(funDecls)}
         |begin
         |    ${portMaps.mkString("\n    ")}
         |
         |${indent(combStmts)}
         |
         |    process
         |    begin
         |        wait until rising_edge(clk);
         |
         |${indent(clkStmts, 2)}
         |    end process ;
         |end;
         |""".stripMargin
  }
}
