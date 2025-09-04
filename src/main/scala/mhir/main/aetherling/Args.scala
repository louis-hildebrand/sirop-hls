package mhir.main.aetherling

import mhir.main.shared.{CompilerOptions, NullTarget, VhdlTarget}
import os.Path

/** Parsed command-line arguments.
  *
  * @param inFile
  *   the path to the input Aetherling program.
  * @param options
  *   options to pass to the compiler.
  * @param help
  *   whether to show the help message and exit.
  */
case class Args(inFile: Path, options: CompilerOptions, help: Boolean)

/** Companion object for [[Args]].
  */
object Args {

  /** Parses the given command-line arguments.
    *
    * @param args
    *   the raw command-line arguments.
    */
  def apply(args: Array[String]): Args = {
    val (srcFilename, outDirName) = args match {
      case Array(src, out, _*) if src.startsWith("-") || out.startsWith("-") =>
        throw new BadArgsException(
          "path to Aetherling benchmark and output directory must come first"
        )
      case Array(src, out, _*) => (src, out)
      case _ =>
        throw new BadArgsException(
          "path to Aetherling benchmark and output directory are required"
        )
    }
    val src = Path(srcFilename, base = os.pwd)
    val out = Path(outDirName, base = os.pwd)
    var help = false
    var optimize = true
    var emitHdl = true
    var showFinal = false
    var overwrite = false
    for (a <- args.drop(2)) {
      a match {
        case "-h" | "--help" =>
          help = true
        case "--no-optimize" =>
          optimize = false
        case "--no-hdl" =>
          emitHdl = false
        case "--show-final" =>
          showFinal = true
        case "--overwrite" =>
          overwrite = true
        case _ =>
          throw new BadArgsException(s"unrecognized argument: $a")
      }
    }
    Args(
      inFile = src,
      outDir = out,
      help = help,
      optimize = optimize,
      emitHdl = emitHdl,
      showFinal = showFinal,
      overwrite = overwrite
    )
  }

  def apply(
      inFile: Path,
      outDir: Path,
      help: Boolean = false,
      optimize: Boolean = true,
      emitHdl: Boolean = true,
      showFinal: Boolean = false,
      overwrite: Boolean = false
  ): Args = {
    val options = CompilerOptions(
      optimize = optimize,
      showFinal = showFinal,
      target = if (emitHdl) {
        VhdlTarget(outDir = outDir, overwrite = overwrite)
      } else {
        NullTarget
      }
    )
    new Args(inFile = inFile, options = options, help = help)
  }

  private[main] def printShortUsage(): Unit = {
    val cls: String = {
      val fullName = Compiler.getClass.getCanonicalName
      if (fullName.endsWith("$")) {
        fullName.dropRight(1)
      } else {
        fullName
      }
    }
    println(
      s"Usage: runMain $cls SRC OUT [-h|--help] [--no-optimize] [--no-hdl] [--show-final] [--overwrite]"
    )
  }

  private[main] def printFullUsage(): Unit = {
    this.printShortUsage()
    println()
    println(
      s"""Arguments:
         |  SRC                path to the Aetherling program to compile
         |  OUT                path to the directory in which to emit the VHDL code
         |  -h, --help         print the help message and exit
         |  --no-optimize      do not optimize the program
         |  --no-hdl           do not emit any HDL code
         |  --show-final       show the final program right before VHDL generation
         |  --overwrite        what to do if directory OUT already exists: if true then
         |                     delete the existing directory, if false then raise an error
         |""".stripMargin.stripTrailing
    )
  }
}
