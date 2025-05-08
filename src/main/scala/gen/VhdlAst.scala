package gen

import debug.indent

private[gen] case class VhdlFunction(
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

private[gen] trait Port
private[gen] case class InPort(
    name: String,
    typ: VhdlType
) extends Port

private[gen] case class OutPort(
    name: String,
    typ: VhdlType,
    assign: String
) extends Port

private[gen] case class Signal(
    name: String,
    typ: VhdlType,
    init: Option[String] = None,
    assignStmt: Option[String] = None,
    cond: Option[String] = None
)

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
