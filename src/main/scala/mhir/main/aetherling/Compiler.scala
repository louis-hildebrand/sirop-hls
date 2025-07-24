package mhir.main.aetherling

import mhir.gen.vhdl.VhdlGenerator
import mhir.ir._
import mhir.ir.Lowering.ExprLowering
import mhir.ir.Uncurrier.Uncurry
import mhir.ir.typecheck.TypeCheck
import mhir.parse.AetherlingParser
import mhir.optimize.{Optimizer => Opt}
import mhir.sugar.Streamifier.Streamify
import os.Path

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

    val optimized = if (args.optimize) {
      Opt.optimize(lowered)
    } else {
      lowered
    }
    if (args.showOptimized) {
      println(ExprPrinter.display(optimized))
    }

    val finalProgram = makeSynthesizable(optimized)
    if (args.showFinal) {
      println(ExprPrinter.display(finalProgram))
    }

    if (args.emitHdl) {
      emit(finalProgram, outDir = args.outDir, overwrite = args.overwrite)
    }

    finalProgram
  }

  private def makeSynthesizable(e: Expr): Expr = {
    uncurryBody(inlineFunCalls(e).streamify())
  }

  private def inlineFunCalls(e: Expr): Expr = {
    val result = e match {
      case FunCall(f, arg) =>
        inlineFunCalls(f) match {
          case Function(x, body) =>
            body.subPreserveType(x -> arg)
          case f =>
            FunCall(f, arg)()
        }
      case Function(x, body) =>
        Function(x, inlineFunCalls(body))()
      case e => e
    }
    result.tchk()
  }

  private def uncurryBody(e: Expr): Expr = {
    val result = e match {
      case Function(x, body) =>
        Function(x, uncurryBody(body))()
      case e =>
        e.uncurry()
    }
    result.tchk()
  }

  private def emit(
      finalProgram: Expr,
      outDir: Path,
      overwrite: Boolean
  ): Unit = {
    if (os.exists(outDir)) {
      if (overwrite) {
        os.remove.all(outDir)
      } else {
        throw new RuntimeException(
          s"Output directory $outDir already exists."
        )
      }
    }
    finalProgram match {
      case f: Function =>
        VhdlGenerator.emitVhdl(f, outDir)
      case s: StmBuild =>
        VhdlGenerator.emitVhdl(s, outDir)
      case e =>
        throw new RuntimeException(
          s"Cannot compile program which is neither a stream nor a function: $e."
        )
    }
  }
}
