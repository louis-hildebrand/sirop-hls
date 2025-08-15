package mhir.main.aetherling

import com.typesafe.scalalogging.Logger
import mhir.gen.vhdl.VhdlGenerator
import mhir.ir._
import mhir.ir.Lowering.ExprLowering
import mhir.ir.Uncurrier.Uncurry
import mhir.ir.typecheck.TypeCheck
import mhir.parse.AetherlingParser
import mhir.optimize.{PartialEvalPass => PE, Optimizer => Opt}
import mhir.sugar.Streamifier.Streamify
import os.Path
import mhir.logging.time

/** A compiler for programs written in
  * [[https://dl.acm.org/doi/10.1145/3385412.3385983 Aetherling]]'s space-time
  * language.
  */
object Compiler {

  private implicit val logger: Logger = Logger(getClass.getName)

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
    val parsed = parse(args.inFile)
    val checked = typecheck(parsed)
    val lowered = lower(checked)

    val optimized = if (args.optimize) {
      logger.info("optimization is enabled")
      val pe = time("initial partial evaluation") {
        PE.partialEval(lowered)
      }
      val synthesizable = makeSynthesizable(pe)
      time("optimization") {
        Opt.optimize(synthesizable)
      }
    } else {
      logger.info("skipping optimization")
      makeSynthesizable(lowered)
    }

    val finalProgram = optimized
    if (args.showFinal) {
      println(ExprPrinter.display(finalProgram))
    }

    if (args.emitHdl) {
      emit(finalProgram, outDir = args.outDir, overwrite = args.overwrite)
    }

    logger.info("done compiling")
    finalProgram
  }

  private def parse(f: Path): Expr = {
    logger.info(s"parsing Aetherling code from $f")
    time("parsing") {
      val aetherlingCode = os.read(f)
      AetherlingParser.parse(aetherlingCode)
    }
  }

  private def typecheck(e: Expr): Expr = {
    time("type checking") {
      e.tchk()
    }
  }

  private def lower(e: Expr): Expr = {
    time("lowering") {
      translateStmLiteral(e.lower())
    }
  }

  private def makeSynthesizable(e: Expr): Expr = {
    time("making expression synthesizable") {
      val e1 = inlineFunCalls(e)
      val e2 = e1.streamify()
      val e3 = uncurryBody(e2)
      e3
    }
  }

  private def emit(
      finalProgram: Expr,
      outDir: Path,
      overwrite: Boolean
  ): Unit = {
    time("generating VHDL") {
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

  private def translateStmLiteral(e: Expr): Expr = {
    val result = e match {
      case s: StmLiteral => s.lower().asInstanceOf[StmLiteral].toStmBuild
      case e             => e.map(translateStmLiteral)
    }
    val checked = result.tchk()
    assert(checked.typ ~= e.typ)
    checked
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
}
