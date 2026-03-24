package mhir.main

import mhir.main.shared.{
  BadArgsException,
  CompilerOptions,
  HelpException,
  VersionException
}
import mhir.main.stored.Program
import os.Path

/** Parsed command-line arguments.
  *
  * @param src
  *   the program to compile.
  * @param options
  *   options to pass to the compiler.
  */
case class Args(src: Source, options: CompilerOptions)

/** Companion object for [[Args]].
  */
object Args {

  /** Parses the given command-line arguments.
    *
    * @param args
    *   the raw command-line arguments.
    */
  def apply(args: Seq[String]): Args = {
    if (args.contains("-h") || args.contains("--help")) {
      throw HelpException
    }
    if (args.contains("--version")) {
      throw VersionException
    }

    val (src, remainingArgs) = args match {
      case Seq("-s", "sirop", "-i", f, remainingArgs @ _*) =>
        val src = SiropSource(Path(f, base = os.pwd))
        (src, remainingArgs)
      case Seq("-s", "aetherling", "-i", f, remainingArgs @ _*) =>
        val src = AetherlingSource(Path(f, base = os.pwd))
        (src, remainingArgs)
      case Seq("-s", "stored", "-i", progName, remainingArgs @ _*) =>
        val src = StoredSource(Program(progName))
        (src, remainingArgs)
      case _ =>
        throw new BadArgsException(s"-s and -i must come first, in that order")
    }

    val options = CompilerOptions(remainingArgs)
    new Args(
      src = src,
      // The Aetherling compiler guarantees that each producer/consumer pair
      // has the same "time."
      options = options.copy(optFlags =
        options.optFlags.copy(assumeThroughputsMatch = true)
      )
    )
  }

  private[main] def printShortUsage(): Unit = {
    println(
      s"Usage: sirop -s (sirop|aetherling|stored) -i INPUT [OPTION]... [-h|--help]"
    )
  }

  private[main] def printFullUsage(): Unit = {
    this.printShortUsage()
    println()
    println(
      s"""Arguments:
         |  -s (sirop|aetherling|stored)  source language
         |  -i INPUT                      where to get the source code.
         |                                With -s stored, this is the program name.
         |                                Otherwise, this is the path to the source file.
         |  -h, --help                    print the help message and exit
         |  --version                     print the compiler version and exit
         |
         |""".stripMargin
        ++ CompilerOptions.longUsage
    )
  }
}
