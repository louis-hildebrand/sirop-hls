package mhir.gen.verilog

import os.Path

/** Methods for initializing a complete Verilog project.
  */
object VerilogProjectInitializer {
  private val ResourcesDir =
    os.pwd / "src" / "main" / "resources" / "mhir" / "gen"

  /** Copy all the required files to make a complete, synthesizable Verilog
    * project.
    *
    * @param dir
    *   the directory in which to create the project.
    * @param top
    *   the `Top.v` file with the top-level Verilog module.
    * @param overwrite
    *   what to do if the directory already exists and is non-empty. If `true`,
    *   delete all existing files. If `false`, throw an exception and leave the
    *   existing files as-is.
    */
  def initProj(dir: Path, top: Path, overwrite: Boolean = false): Unit = {
    if (os.isFile(dir)) {
      throw new IllegalArgumentException(
        s"$dir already exists and is a file, not a directory."
      )
    }
    if (os.isDir(dir) && os.list(dir).nonEmpty) {
      if (overwrite) {
        os.remove.all(dir)
      } else {
        throw new IllegalArgumentException(
          s"Directory $dir already exists and is not empty."
        )
      }
    }
    if (!os.exists(dir)) {
      os.makeDir.all(dir)
    }
    if (!os.isFile(top)) {
      throw new IllegalArgumentException(
        s"$top does not exist or is not a file."
      )
    }

    os.copy(from = top, to = dir / "Top.v")

    os.copy(from = ResourcesDir / "verilog" / "Mul.v", to = dir / "Mul.v")

    val qpfText = os.read(ResourcesDir / "top.qpf").replaceAll("top", "Top")
    os.write(dir / "Top.qpf", qpfText)

    val qsfText = (
      os.read(ResourcesDir / "top.qsf")
        .replaceAll("top", "Top")
        + "set_global_assignment -name VERILOG_FILE Top.v\n"
        + "set_global_assignment -name VERILOG_FILE Mul.v\n"
    )
    os.write(dir / "Top.qsf", qsfText)

    val sdcText = os.read(ResourcesDir / "top.sdc").replaceAll("clk", "clock")
    os.write(dir / "Top.sdc", sdcText)
  }

  def main(args: Array[String]): Unit = {
    args match {
      case Array(dir, top) =>
        initProj(Path(dir, base = os.pwd), Path(top, base = os.pwd))
      case Array(dir, top, "--overwrite") =>
        initProj(
          Path(dir, base = os.pwd),
          Path(top, base = os.pwd),
          overwrite = true
        )
      case _ =>
        throw new IllegalArgumentException(
          s"Invalid arguments: ${args.mkString("[", ", ", "]")}."
            + " Expected two arguments: project directory and path to Top.v."
        )
    }
  }
}
