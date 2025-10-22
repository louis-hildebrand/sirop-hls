package mhir.main.shared

import com.typesafe.scalalogging.Logger
import mhir.gen.vhdl.VhdlGenerator
import mhir.ir.Lowering.ExprLowering
import mhir.ir.Uncurrier.Uncurry
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.logging.{time, time2}
import mhir.optimize.{Optimizer, OptimizerOptions}
import mhir.sugar.Streamifier.Streamify
import org.slf4j.event.Level
import os.Path

import java.time.Duration

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
  def compile(
      e: Expr,
      options: CompilerOptions,
      parseTime: Duration
  ): Expr = {
    val result = time("compilation", Level.DEBUG) {
      doCompile(e, options, parseTime = parseTime)
    }
    result
  }

  private def doCompile(
      parsed: Expr,
      options: CompilerOptions,
      parseTime: Duration
  ): Expr = {
    val (checked, tchkTime) = typecheck(parsed)
    val (lowered, lowerTime) = lower(checked)
    val (synthesizable, synthTime) = makeSynthesizable(lowered)
    val (finalProgram, optimTime) = optimize(synthesizable, options.optFlags)
    val genTime = generateCode(finalProgram, options.targets)
    options.targets.toSeq
      .foreach({
        case NullTarget    => ()
        case _: VhdlTarget => () // already done
        case PrettyPrintTarget(dest, overwrite) =>
          emitPrettyPrinted(finalProgram, dest = dest, overwrite = overwrite)
        case CompileTimeTarget(f, overwrite) =>
          emitCompileTimeReport(
            f,
            overwrite,
            parse = parseTime,
            typecheck = tchkTime,
            lower = lowerTime,
            makeSynth = synthTime,
            optimize = optimTime,
            codegen = genTime
          )
      })
    finalProgram
  }

  private def typecheck(e: Expr): (Expr, Duration) = {
    time2("type checking", Level.DEBUG) {
      e.tchk()
    }
  }

  private def lower(e: Expr): (Expr, Duration) = {
    time2("lowering", Level.DEBUG) {
      translateStmLiteral(e.lower())
    }
  }

  private def makeSynthesizable(e: Expr): (Expr, Duration) = {
    time2("making expression synthesizable", Level.DEBUG) {
      val e1 = inlineFunCalls(e)
      val e2 = e1.streamify()
      val e3 = uncurryBody(e2)
      e3
    }
  }

  private def optimize(
      e: Expr,
      optFlags: OptimizerOptions
  ): (Expr, Duration) = {
    time2("optimization", Level.DEBUG) {
      Optimizer(optFlags).optimize(e)
    }
  }

  private def generateCode(
      prog: Expr,
      targets: Set[CompilerTarget]
  ): Duration = {
    val (_, time) = time2("codegen", Level.DEBUG) {
      targets.foreach({
        case VhdlTarget(outDir, overwrite) =>
          emitVhdl(prog, outDir, overwrite)
        case NullTarget           => ()
        case _: PrettyPrintTarget => ()
        case _: CompileTimeTarget => ()
      })
    }
    time
  }

  private def emitPrettyPrinted(
      finalProgram: Expr,
      dest: PrettyPrintDestination,
      overwrite: Boolean
  ): Unit = {
    time("pretty printing", Level.DEBUG) {
      val pp = ExprPrinter.display(finalProgram) + "\n"
      dest match {
        case PPStdout =>
          print(pp)
        case PPFile(f) =>
          if (overwrite) {
            os.write.over(f, pp)
          } else {
            os.write(f, pp)
          }
      }
    }
  }

  private def emitVhdl(
      finalProgram: Expr,
      outDir: Path,
      overwrite: Boolean
  ): Unit = {
    time("generating VHDL", Level.DEBUG) {
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

  private def emitCompileTimeReport(
      f: Path,
      overwrite: Boolean,
      parse: Duration,
      typecheck: Duration,
      lower: Duration,
      makeSynth: Duration,
      optimize: Duration,
      codegen: Duration
  ): Unit = {
    time("reporting compile time", Level.DEBUG) {
      val csvStr =
        s"""step,millis
           |parse,${parse.toMillis}
           |typecheck,${typecheck.toMillis}
           |lower,${lower.toMillis}
           |make_synth,${makeSynth.toMillis}
           |optimize,${optimize.toMillis}
           |codegen,${codegen.toMillis}
           |""".stripMargin
      if (overwrite) {
        os.write.over(f, csvStr)
      } else {
        os.write(f, csvStr)
      }
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
