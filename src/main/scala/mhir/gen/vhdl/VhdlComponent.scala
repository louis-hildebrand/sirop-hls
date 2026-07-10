package mhir.gen.vhdl

import mhir.debug.indent
import os.Path

import scala.collection.immutable.ListMap

/** One component (entity + architecture) in VHDL.
  */
private[vhdl] sealed trait VhdlComponent

private[vhdl] trait PredefinedComponent extends VhdlComponent {
  def entityName: String
  def generics: ListMap[String, String]
  def portNames: Set[String]
  def filesToCopy(vhdlDir: Path): Map[Path, String]
  def typesUsed: Set[VhdlType]
}

/** The predefined `stm_nop` component.
  *
  * @param bitWidth
  *   the bit width to use when instantiating this component.
  */
private[vhdl] case class StmNoOpComponent(bitWidth: Int)
    extends PredefinedComponent {

  override def entityName: String = "work.stm_nop"

  override def generics: ListMap[String, String] = {
    ListMap("BIT_WIDTH" -> this.bitWidth.toString)
  }

  override def portNames: Set[String] = {
    Set(
      "c_data",
      "c_valid",
      "c_ready",
      "p_data",
      "p_valid",
      "p_ready"
    )
  }

  override def filesToCopy(vhdlDir: Path): Map[Path, String] = {
    Map(vhdlDir / "stm_nop.vhd" -> s"mhir/gen/vhdl/stm_nop.vhd")
  }

  override def typesUsed: Set[VhdlType] = {
    Set(VhdlStdLogic, VhdlStdLogicVec(this.bitWidth))
  }
}

/** The predefined `start_delay` component.
  *
  * @param maxLatency
  *   the value for the `MAX_LATENCY` generic.
  */
private[vhdl] case class StartDelayComponent(maxLatency: Int)
    extends PredefinedComponent {

  override def entityName: String = "work.start_delay"

  override def generics: ListMap[String, String] = {
    ListMap("MAX_LATENCY" -> this.maxLatency.toString)
  }

  override def portNames: Set[String] = Set("clk", "reset", "go")

  override def filesToCopy(vhdlDir: Path): Map[Path, String] = {
    Map(vhdlDir / "start_delay.vhd" -> "mhir/gen/vhdl/start_delay.vhd")
  }

  override def typesUsed: Set[VhdlType] = {
    Set(
      VhdlStdLogic,
      VhdlStdLogicVec(n = 1 + this.maxLatency, direction = IndexUp)
    )
  }
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
) extends PredefinedComponent {

  override def entityName: String = "work.letstm_buf"

  override def generics: ListMap[String, String] = {
    ListMap(
      "BIT_WIDTH" -> this.bitWidth.toString,
      "BUF_SIZE" -> this.bufSize.toString,
      "N_CONSUMERS" -> this.numConsumers.toString
    )
  }

  override def portNames: Set[String] = {
    Set(
      "clk",
      "reset",
      "p_data",
      "p_valid",
      "p_ready",
      "c_data",
      "c_valid",
      "c_ready"
    )
  }

  override def filesToCopy(vhdlDir: Path): Map[Path, String] = {
    Map(
      vhdlDir / "letstm_buf.vhd" -> "mhir/gen/vhdl/letstm_buf.vhd",
      vhdlDir / "dual_port_ram.vhd" -> "mhir/gen/vhdl/dual_port_ram.vhd",
      vhdlDir / "multi_consumer_ram.vhd" -> "mhir/gen/vhdl/multi_consumer_ram.vhd"
    )
  }

  override def typesUsed: Set[VhdlType] = {
    Set(VhdlStdLogic, VhdlStdLogicVec(this.bitWidth))
  }
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
        case child: PredefinedComponent =>
          child.portNames
      }
      val actualPorts = map.map.keySet
      assert(
        expectedPorts == actualPorts,
        s"wrong ports for component $name"
          + s" (expected $expectedPorts but got $actualPorts)"
      )
    }
  }

  def writeVhdl(f: Path, options: VhdlGeneratorOptions): Unit = {
    writeHeader(f)
    writeEntity(f)
    writeArchitecture(f, options)
  }

  private def writeHeader(f: Path): Unit = {
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
        .sortBy(x => x.toLowerCase)
    os.write.append(
      f,
      s"""entity $name is
         |port (
         |    ${portDecls.mkString(";\n    ")}
         |);
         |end entity $name;
         |""".stripMargin
    )
  }

  private def writeArchitecture(
      f: Path,
      options: VhdlGeneratorOptions
  ): Unit = {
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
            .mkString(",\n" + " ".repeat(12))
        c match {
          case c: CustomVhdlComponent =>
            s"""    $name : entity work.${c.name}
               |        port map(
               |            $assignments);
               |""".stripMargin.stripTrailing
          case c: PredefinedComponent =>
            val genericAssignments = c.generics
              .map({ case (k, v) => s"$k => $v" })
              .toSeq
              .sorted
              .mkString(",\n" + " ".repeat(12))
            s"""    $name : entity ${c.entityName}
               |        generic map(
               |            $genericAssignments)
               |        port map(
               |            $assignments);
               |""".stripMargin.stripTrailing
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
      s"""    process
         |    begin
         |        wait until rising_edge(${options.clock});
         |
         |${indent(clkStmts, 2)}
         |    end process;
         |""".stripMargin.stripTrailing
    }
    os.write.append(f, s"$process\nend;\n")
  }
}
