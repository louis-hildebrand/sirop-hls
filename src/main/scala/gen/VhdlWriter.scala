package gen

import debug.indent
import java.nio.file.{Files, Path}

object VhdlWriter {
  def emit(top: VhdlComponent, dir: Path): Unit = {
    val typesToDefine =
      findTypesUsedIn(top).flatMap(t => t.descendants + t)
    val designDir = dir.resolve("design")
    Files.createDirectory(designDir)
    emitConversionsPackage(typesToDefine, designDir)
    emitTypedefs(typesToDefine, designDir)
    emitComponents(top, designDir)
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
}
