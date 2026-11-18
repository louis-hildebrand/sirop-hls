package mhir.gen.vhdl

import mhir.debug.indent
import mhir.gen.vhdl.ir._
import mhir.ir._
import os.Path

import scala.io.Source

object VhdlWriter {

  def emit(
      pipe: FlatPipeline,
      dir: Path,
      options: VhdlGeneratorOptions
  ): Unit = {
    val typesToDefine = findTypesUsedIn(pipe).flatMap(t => t.descendants + t)
    if (os.isDir(dir)) os.remove.all(dir)
    val designDir = dir / "design"
    os.makeDir.all(designDir)
    emitConversionsPackage(typesToDefine, designDir, options)
    emitTypedefsPackage(typesToDefine, designDir)
    emitComponents(pipe, designDir, options)
    emitProjectFiles(dir, designDir, options)
  }

  private def emitProjectFiles(
      dir: Path,
      designDir: Path,
      options: VhdlGeneratorOptions
  ): Unit = {
    val topName = options.topName
    os.write(
      dir / s"$topName.qpf",
      Source
        .fromResource("mhir/gen/top.qpf")
        .getLines()
        .map(
          _.replace(
            """PROJECT_REVISION = "top"""",
            s"""PROJECT_REVISION = "$topName""""
          )
        )
        .mkString("\n")
    )
    os.write(
      dir / s"$topName.qsf",
      Source
        .fromResource("mhir/gen/top.qsf")
        .getLines()
        .map({ line =>
          if (line.startsWith("set_global_assignment -name TOP_LEVEL_ENTITY")) {
            s"set_global_assignment -name TOP_LEVEL_ENTITY $topName"
          } else if (line.startsWith("set_global_assignment -name FAMILY")) {
            s"""set_global_assignment -name FAMILY "${options.deviceFamily}""""
          } else if (line.startsWith("set_global_assignment -name DEVICE")) {
            s"""set_global_assignment -name DEVICE "${options.device}""""
          } else {
            line
          }
        })
        .map(_ + "\n")
    )
    os.write(
      dir / s"$topName.sdc",
      Source
        .fromResource("mhir/gen/top.sdc")
        .getLines()
        .map({ line =>
          if (line.startsWith("create_clock -name clk")) {
            f"create_clock -name ${options.clock} -period ${options.clockPeriod}%.3f [get_ports ${options.clock}]"
          } else {
            line
          }
        })
        .map(_ + "\n")
    )
    for (p <- os.list(designDir)) {
      os.write.append(
        dir / s"$topName.qsf",
        s"set_global_assignment -name VHDL_FILE ${p.relativeTo(dir)}\n"
      )
    }
    if (options.virtualPins) {
      os.write.append(
        dir / s"$topName.qsf",
        "set_instance_assignment -name VIRTUAL_PIN ON -to *\n"
      )
    }
    os.write.append(dir / s"$topName.qsf", options.appendQsf.map(_ + "\n"))
  }

  private def emitConversionsPackage(
      types: Set[VhdlType],
      dir: Path,
      options: VhdlGeneratorOptions
  ): Unit = {
    // Sort in the same order as for the typedefs package
    val sortedTypes = types.toSeq.sortBy(x => x.vhdlName.length)
    val functions = VhdlConversionGenerator.defaultFunctions ++
      sortedTypes.flatMap(VhdlConversionGenerator.toSlvConverter) ++
      sortedTypes.flatMap(VhdlConversionGenerator.fromSlvConverter)
    val decls = functions.map(_.decl).mkString("\n")
    val bodies = functions.map(_.body).mkString("\n\n")
    val contents =
      s"""library IEEE;
         |use IEEE.std_logic_1164.all;
         |use IEEE.numeric_std.all;
         |
         |library ${options.library};
         |use ${options.library}.typedefs.all;
         |
         |package conversions is
         |${indent(decls)}
         |end package conversions;
         |
         |package body conversions is
         |${indent(bodies)}
         |end package body;
         |""".stripMargin

    val file = dir / "conversions.vhd"
    os.write(file, contents)
  }

  private def emitTypedefsPackage(
      typesToDefine: Set[VhdlType],
      dir: Path
  ): Unit = {
    val definitions = typesToDefine.toSeq
      // A type declaration cannot refer to types declared later.
      // Since every type's name includes all of its children's names, its
      // name is guaranteed to be strictly larger than any of its children's
      // names.
      // Therefore, sorting by name length puts the declarations in a
      // valid order.
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

    val file = dir / "typedefs.vhd"
    os.write(file, contents)
  }

  private def findTypesUsedIn(pipe: FlatPipeline): Set[VhdlType] = {
    pipe.inputs
      .map(_.typ)
      .++(pipe.sbuilds.flatMap(findTypesUsedIn))
      .flatMap({
        case TyData(typ)               => Some(typ)
        case TyStm(TyData(elemTyp), _) => Some(elemTyp)
        case _                         => None
      })
      .map(VhdlType(_))
  }

  private def findTypesUsedIn(s: StmBuildNode): Set[Type] = {
    findTypesUsedIn(s.s)
  }

  private def findTypesUsedIn(s: GenStmBuild): Set[Type] = {
    findTypesUsedIn(s.data)
      .++(findTypesUsedIn(s.valid))
      .++(s.intermediates.flatMap({ case (x, i) => findTypesUsedIn(x, i) }))
      .++(s.producers.flatMap({ case (x, (p, ready)) =>
        findTypesUsedIn(x) ++ findTypesUsedIn(p) ++ findTypesUsedIn(ready)
      }))
  }

  private def findTypesUsedIn(x: Param, i: Intermediate): Set[Type] = {
    findTypesUsedIn(x) ++ findTypesUsedIn(i)
  }

  private def findTypesUsedIn(i: Intermediate): Set[Type] = {
    i.exprChildren
      .flatMap(findTypesUsedIn)
      .++(i.intermediateChildren.flatMap(findTypesUsedIn))
      .toSet
  }

  private def findTypesUsedIn(e: Expr): Set[Type] = {
    e.children.flatMap(findTypesUsedIn).toSet + e.typ
  }

  private def emitComponents(
      pipe: FlatPipeline,
      dir: Path,
      options: VhdlGeneratorOptions
  ): Unit = {
    emitCustomIpBlocks(pipe, dir, options)
    emitComponent(pipe, dir, options)
    for (s <- pipe.sbuilds) {
      emitComponent(s, dir, options)
    }
    for (let <- pipe.lets) {
      emitComponent(let, dir, options)
    }
  }

  private def emitCustomIpBlocks(
      pipe: FlatPipeline,
      dir: Path,
      options: VhdlGeneratorOptions
  ): Unit = {
    val customBlocks = pipe.sbuilds
      .flatMap(s =>
        s.s.intermediates.values.flatMap(i => i.intermediateDescendants :+ i)
      )
      .collect({ case i: IpBlockInst => i.resourcesToCopy(dir) })
      .flatten
      .toMap
    val customBlocksAndLetStm = if (options.handshake && pipe.lets.nonEmpty) {
      customBlocks ++ Map(
        dir / "letstm_buf.vhd" -> "mhir/gen/vhdl/letstm_buf.vhd",
        dir / "dual_port_ram.vhd" -> "mhir/gen/vhdl/dual_port_ram.vhd",
        dir / "multi_consumer_ram.vhd" -> "mhir/gen/vhdl/multi_consumer_ram.vhd"
      )
    } else {
      customBlocks
    }
    for ((destination, source) <- customBlocksAndLetStm) {
      val code = Source
        .fromResource(source)
        .mkString
        .replace("%{LIBRARY}%", options.library)
      os.write(destination, code)
    }
  }

  private def emitComponent(
      entity: Entity,
      dir: Path,
      options: VhdlGeneratorOptions
  ): Unit = {
    if (entity.skip(options)) {
      return
    }
    val path = dir / s"${entity.entityName}.vhd"
    os.write.over(
      path,
      s"""library IEEE;
         |use IEEE.std_logic_1164.all;
         |use IEEE.numeric_std.all;
         |
         |library ${options.library};
         |use ${options.library}.conversions.all;
         |use ${options.library}.typedefs.all;
         |""".stripMargin.stripTrailing
    )
    os.write.append(path, "\n\n" + entity.toEntityDeclaration(options))
    os.write.append(path, "\n\n" + entity.toArchitectureBody(options))
  }
}
