package mhir.main.aetherling

import com.typesafe.scalalogging.Logger
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

  private val logger = Logger(getClass.getName)

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
    logger.debug(s"parsing Aetherling code from ${args.inFile}...")
    val aetherlingCode = os.read(args.inFile)
    val parsed = AetherlingParser.parse(aetherlingCode)
    if (args.showParsed) {
      println(ExprPrinter.display(parsed))
    }
    logger.trace(s"parsed expression: $parsed")

    logger.debug("type checking expression...")
    val checked = parsed.tchk()
    if (args.showChecked) {
      println(ExprPrinter.display(checked))
    }
    logger.trace(s"type-checked expression: $checked")

    logger.debug("lowering expression...")
    val lowered = translateStmLiteral(checked.lower())
    if (args.showLowered) {
      println(ExprPrinter.display(lowered))
    }
    logger.trace(s"lowered expression: $lowered")

    val optimized = if (args.optimize) {
      logger.debug("optimizing expression...")
      val result = Opt.optimize(lowered)
      logger.trace(s"optimized expression: $result")
      result
    } else {
      logger.debug("skipping optimization")
      lowered
    }
    if (args.showOptimized) {
      println(ExprPrinter.display(optimized))
    }

    logger.debug("ensuring expression is synthesizable...")
    val finalProgram = makeSynthesizable(optimized)
    if (args.showFinal) {
      println(ExprPrinter.display(finalProgram))
    }
    logger.trace(s"final program: $finalProgram")

    if (args.emitHdl) {
      emit(finalProgram, outDir = args.outDir, overwrite = args.overwrite)
    }

    logger.debug("done compiling")
    finalProgram
  }

  private def translateStmLiteral(e: Expr): Expr = {
    val result = e match {
      case s: StmLiteral => s.lower().asInstanceOf[StmLiteral].toStmBuild
      case e             => e.map(translateStmLiteral)
    }
    val checked = result.tchk()
    assert(checked.typ ~= e.typ)
    checked
  }

  private def makeSynthesizable(e: Expr): Expr = {
    val e1 = inlineFunCalls(e)
    val e2 = e1.streamify()
    val e3 = uncurryBody(e2)
    e3
  }

  private def inlineFunCalls(e: Expr): Expr = {
    require(e.hasType)
    if (e.typ.isData) {
      e
    } else {
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
    VhdlGenerator.emitVhdl(finalProgram, outDir)
  }
}
