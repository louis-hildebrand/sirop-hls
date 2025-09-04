package mhir.main.shared

import com.typesafe.scalalogging.Logger
import mhir.gen.vhdl.VhdlGenerator
import mhir.ir.Lowering.ExprLowering
import mhir.ir.Uncurrier.Uncurry
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.logging.time
import mhir.optimize.{Optimizer => Opt, PartialEvalPass => PE}
import mhir.sugar.Streamifier.Streamify
import org.slf4j.event.Level
import os.Path

object Compiler {

  private implicit val logger: Logger = Logger(getClass.getName)

  /** Runs the compiler.
    *
    * @param e
    *   the expression to compile.
    * @param options
    *   compiler options.
    * @return
    *   the final program from which VHDL was generated.
    */
  def compile(e: Expr, options: CompilerOptions): Expr = {
    val result = time("compilation", Level.INFO) {
      doCompile(e, options)
    }
    if (options.showFinal) {
      println(ExprPrinter.display(result))
    }
    result
  }

  private def doCompile(parsed: Expr, options: CompilerOptions): Expr = {
    val checked = typecheck(parsed)
    val lowered = lower(checked)

    val optimized = if (options.optimize) {
      logger.info("optimization is enabled")
      val pe = time("initial partial evaluation", Level.INFO) {
        PE.partialEval(lowered)
      }
      val synthesizable = makeSynthesizable(pe)
      time("optimization", Level.INFO) {
        Opt.optimize(synthesizable)
      }
    } else {
      logger.info("skipping optimization")
      makeSynthesizable(lowered)
    }

    val finalProgram = optimized

    options.target match {
      case NullTarget => ()
      case VhdlTarget(outDir, overwrite) =>
        emit(finalProgram, outDir = outDir, overwrite = overwrite)
    }

    finalProgram
  }

  private def typecheck(e: Expr): Expr = {
    time("type checking", Level.INFO) {
      e.tchk()
    }
  }

  private def lower(e: Expr): Expr = {
    time("lowering", Level.INFO) {
      translateStmLiteral(e.lower())
    }
  }

  private def makeSynthesizable(e: Expr): Expr = {
    time("making expression synthesizable", Level.INFO) {
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
    time("generating VHDL", Level.INFO) {
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
