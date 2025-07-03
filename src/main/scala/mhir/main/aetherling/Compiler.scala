package mhir.main.aetherling

import mhir.gen.vhdl.VhdlGenerator
import mhir.ir._
import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.TypeCheck
import mhir.parse.AetherlingParser
import mhir.optimize.{Simplifier => S}
import mhir.sugar.Streamifier.Streamify

/** A compiler for programs written in
  * [[https://dl.acm.org/doi/10.1145/3385412.3385983 Aetherling]]'s space-time
  * language.
  */
object Compiler {

  /** The program entry point.
    *
    * @param args
    *   the command-line arguments.
    */
  def main(args: Array[String]): Unit = {
    val a =
      try {
        Args(args)
      } catch {
        case exc: BadArgsException =>
          println(s"Invalid command-line arguments: ${exc.getMessage}")
          println()
          Args.printShortUsage()
          return
      }
    if (a.help) {
      Args.printFullUsage()
    } else {
      compile(a)
    }
  }

  /** Runs the compiler.
    *
    * @param args
    *   the parsed command-line arguments.
    * @return
    *   the final program from which VHDL was generated.
    */
  def compile(args: Args): Expr = {
    val aetherlingCode = os.read(args.inFile)
    val parsed = AetherlingParser.parse(aetherlingCode)
    if (args.showParsed) {
      println(ExprPrinter.display(parsed))
    }

    val checked = parsed.tchk()
    if (args.showChecked) {
      println(ExprPrinter.display(checked))
    }

    val lowered = checked.lower()
    if (args.showLowered) {
      println(ExprPrinter.display(lowered))
    }

    val simplified = if (args.simplify) {
      S.simplify(lowered)
    } else {
      lowered
    }
    if (args.showSimplified) {
      println(ExprPrinter.display(simplified))
    }

    val finalProgram = makeSynthesizable(simplified)
    if (args.showFinal) {
      println(ExprPrinter.display(finalProgram))
    }

    finalProgram match {
      case f: Function =>
        VhdlGenerator.emitVhdl(f, args.outDir)
      case s: StmBuild =>
        VhdlGenerator.emitVhdl(s, args.outDir)
      case e =>
        throw new RuntimeException(
          s"Cannot compile program which is neither a stream nor a function: $e."
        )
    }

    finalProgram
  }

  private def makeSynthesizable(e: Expr): Expr = {
    e.streamify()
  }
}
