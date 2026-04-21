package mhir.gen.vhdl

import mhir.debug.indent
import mhir.gen.CodegenError
import os.Path

import scala.io.Source

object VhdlWriter {

  def emit(top: CustomVhdlComponent, dir: Path): Unit = {
    val typesToDefine =
      findTypesUsedIn(top).flatMap(t => t.descendants + t)
    if (os.isDir(dir)) os.remove.all(dir)
    val designDir = dir / "design"
    os.makeDir.all(designDir)
    emitConversionsPackage(typesToDefine, designDir)
    emitTypedefs(typesToDefine, designDir)
    emitComponents(top, designDir, readReservedWords())
    emitProjectFiles(dir, designDir, topName = top.name)
  }

  private def readReservedWords(): Set[String] = {
    Source.fromResource("mhir/gen/vhdl/reserved_words.txt").getLines().toSet
  }

  private def emitProjectFiles(
      dir: Path,
      designDir: Path,
      topName: String
  ): Unit = {
    os.write(
      dir / s"$topName.qpf",
      Source
        .fromResource("mhir/gen/top.qpf")
        .mkString
        .replace(
          """PROJECT_REVISION = "top"""",
          s"""PROJECT_REVISION = "$topName""""
        )
    )
    os.write(
      dir / s"$topName.qsf",
      Source
        .fromResource("mhir/gen/top.qsf")
        .mkString
        .replace(
          "set_global_assignment -name TOP_LEVEL_ENTITY top",
          s"set_global_assignment -name TOP_LEVEL_ENTITY $topName"
        )
    )
    os.write(
      dir / s"$topName.sdc",
      Source.fromResource("mhir/gen/top.sdc").mkString
    )
    for (p <- os.list(designDir)) {
      os.write.append(
        dir / s"$topName.qsf",
        s"set_global_assignment -name VHDL_FILE ${p.relativeTo(dir)}\n"
      )
    }
  }

  private def emitConversionsPackage(types: Set[VhdlType], dir: Path): Unit = {
    val defaultFunctions = Seq(
      VhdlFunction(
        name = "bool2sl",
        args = Seq(("b", VhdlBool)),
        returnType = VhdlStdLogic,
        decls = Seq(
          VhdlVariable(
            "x",
            VhdlStdLogic,
            s"""if (b) then
               |    x := '1';
               |else
               |    x := '0';
               |end if;
               |""".stripMargin.stripTrailing
          )
        ),
        ret = "x"
      ),
      VhdlFunction(
        name = "sl2bool",
        args = Seq(("x", VhdlStdLogic)),
        returnType = VhdlBool,
        decls = Seq(),
        ret = "x = '1'"
      )
    )
    val toSlvFunctions =
      types.flatMap(t => VhdlConversionGenerator.toSlvConverter(t))
    val fromSlvFunctions =
      types.flatMap(t => VhdlConversionGenerator.fromSlvConverter(t))
    val functions =
      (defaultFunctions ++ toSlvFunctions ++ fromSlvFunctions)
        .sortBy(f => f.vhdlSignature)

    val signatures = functions.map(f => f.vhdlSignature).mkString("\n\n")
    val impls = functions.map(f => f.vhdlImpl).mkString("\n\n")
    val contents =
      s"""library IEEE;
         |use IEEE.std_logic_1164.all;
         |use IEEE.numeric_std.all;
         |use work.typedefs.all;
         |
         |package conversions is
         |${indent(signatures)}
         |
         |    pure function pad (x : in signed; w: natural) return signed;
         |    pure function pad (x : in unsigned; w: natural) return unsigned;
         |    pure function truncate (x : in signed; w: natural) return signed;
         |    pure function truncate (x : in unsigned; w: natural) return unsigned;
         |end package;
         |
         |package body conversions is
         |${indent(impls)}
         |
         |    pure function pad (x : in signed; w: natural) return signed is
         |    begin
         |        return resize(x, w);
         |    end;
         |
         |    pure function pad (x : in unsigned; w: natural) return unsigned is
         |    begin
         |        return resize(x, w);
         |    end;
         |
         |    pure function truncate (x : in signed; w: natural) return signed is
         |    begin
         |        return x(w - 1 downto 0);
         |    end;
         |
         |    pure function truncate (x : in unsigned; w: natural) return unsigned is
         |    begin
         |        return x(w - 1 downto 0);
         |    end;
         |end package body;
         |""".stripMargin

    val file = dir / "conversions.vhd"
    os.write(file, contents)
  }

  private def emitTypedefs(typesToDefine: Set[VhdlType], dir: Path): Unit = {
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

  private def findTypesUsedIn(c: VhdlComponent): Set[VhdlType] = {
    c match {
      case c: StmNoOpComponent =>
        Set(VhdlStdLogic, VhdlStdLogicVec(c.bitWidth))
      case c: LetStmBufComponent =>
        Set(VhdlStdLogic, VhdlStdLogicVec(c.bitWidth))
      case c: CustomVhdlComponent =>
        (c.inPorts.map(p => p.typ)
          ++ c.outPorts.map(p => p.typ)
          ++ c.signals.map(s => s.typ)
          ++ c.functions.flatMap(f => findTypesUsedIn(f))
          ++ c.children.flatMap({ case VhdlEntityInstantiation(_, c, _) =>
            findTypesUsedIn(c)
          })).toSet
    }
  }

  private def findTypesUsedIn(f: VhdlFunction): Set[VhdlType] = {
    (f.args.map({ case (_, t) => t })
      ++ f.decls.flatMap({
        case f: VhdlFunction => findTypesUsedIn(f)
        case d: VarOrSigDecl => Seq(d.typ)
      })
      ++ Seq(f.returnType)).toSet
  }

  private def emitComponents(
      c: VhdlComponent,
      dir: Path,
      reservedWords: Set[String]
  ): Unit = {
    c match {
      case c: StmNoOpComponent =>
        os.write.over(
          dir / c.VhdName,
          Source.fromResource(s"mhir/gen/vhdl/${c.VhdName}").mkString
        )
      case c: LetStmBufComponent =>
        for (name <- c.VhdNames) {
          os.write.over(
            dir / name,
            Source.fromResource(s"mhir/gen/vhdl/$name").mkString
          )
        }
      case c: CustomVhdlComponent =>
        if (reservedWords.contains(c.name.toLowerCase)) {
          throw CodegenError(
            s"cannot generate entity '${c.name}', since its name is a reserved keyword in VHDL"
          )
        }
        c.writeVhdl(dir / s"${c.name}.vhd")
        for (VhdlEntityInstantiation(_, child, _) <- c.children) {
          emitComponents(child, dir, reservedWords)
        }
    }
  }
}
