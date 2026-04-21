package mhir.main.shared

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.eval.Evaluator
import mhir.gen.vhdl.{VhdlGenerator, VhdlGeneratorOptions}
import mhir.ir._
import mhir.logging.{time, time2}
import mhir.optimize.{Optimizer, OptimizerOptions}
import mhir.sugar.Streamifier.Streamify
import mhir.sugar.Uncurrier.Uncurry
import mhir.sugar.{ExprLowering, StmLiteralUtilsImplicit}
import mhir.typecheck.{TypeCheck, TypeCheckProgram}
import org.slf4j.event.Level
import os.Path

import java.time.Duration

object Compiler {

  private implicit val logger: Logger = Logger(getClass.getName)

  /** Runs the compiler.
    *
    * @param prog
    *   the program to compile.
    * @param options
    *   compiler options.
    * @return
    *   the final program from which VHDL was generated.
    */
  def compile(
      prog: Program,
      options: CompilerOptions,
      argparseTime: Duration,
      parseTime: Duration
  ): Expr = {
    time("compilation", Level.DEBUG) {
      doCompile(
        prog,
        options,
        argparseTime = argparseTime,
        parseTime = parseTime
      )
    }
  }

  private def doCompile(
      prog: Program,
      originalOptions: CompilerOptions,
      argparseTime: Duration,
      parseTime: Duration
  ): Expr = {
    val topName = prog.name
    val options =
      originalOptions.copy(vhdl = originalOptions.vhdl.copy(topName = topName))
    val (checked, tchkTime) = typecheck(prog)
    val (lowered, lowerTime) = lower(checked)
    val (synthesizable, synthTime) = makeSynthesizable(lowered)
    val (finalProgram, optimTime) = optimize(synthesizable, options.optFlags)
    val genTime = generateCode(options.vhdl, finalProgram, options.targets)
    options.targets.toSeq
      .foreach({
        case NullTarget => ()
        case EvalTarget(maxInvalidSteps) =>
          val evaluator = Evaluator(maxInvalidSteps = maxInvalidSteps)
          val result = time("evaluation", Level.DEBUG) {
            evaluator.eval(finalProgram)
          }
          println(ExprPrinter.display(result))
        case _: VhdlTarget => () // already done
        case PrettyPrintTarget(dest, overwrite) =>
          emitPrettyPrinted(finalProgram, dest = dest, overwrite = overwrite)
        case CompileTimeTarget(f, overwrite) =>
          emitCompileTimeReport(
            f,
            overwrite,
            argparse = argparseTime,
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

  private def typecheck(prog: Program): (Program, Duration) = {
    time2("type checking", Level.DEBUG) {
      prog.tchk()
    }
  }

  private def lower(prog: Program): (Expr, Duration) = {
    time2("lowering", Level.DEBUG) {
      val e = inlineConstants(prog)
      translateStmLiteral(e.lower)
    }
  }

  private def inlineConstants(prog: Program): Expr = {
    val subs = prog.constants.foldLeft(Map[Expr, Expr]())({
      case (subs, ConstDecl(x, e)) =>
        val v = mhir.eval.eval(e.subPreserveType(subs))
        val newX = x.lower.subPreserveType(subs)
        subs + (newX -> v)
    })
    prog.e.subPreserveType(subs)
  }

  private def makeSynthesizable(e: Expr): (Expr, Duration) = {
    time2("making expression synthesizable", Level.DEBUG) {
      val e1 = inlineFunCalls(e)
      val e2 = e1.streamify
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
      options: VhdlGeneratorOptions,
      prog: Expr,
      targets: Set[CompilerTarget]
  ): Duration = {
    val (_, time) = time2("codegen", Level.DEBUG) {
      targets.foreach({
        case VhdlTarget(outDir, overwrite) =>
          emitVhdl(options, prog, outDir, overwrite)
        case _: EvalTarget        => ()
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
      options: VhdlGeneratorOptions,
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
      VhdlGenerator.emitVhdl(finalProgram, outDir, options)
    }
  }

  private def emitCompileTimeReport(
      f: Path,
      overwrite: Boolean,
      argparse: Duration,
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
           |argparse,${argparse.toMillis}
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
      case s: StmLiteral => s.lower.asInstanceOf[StmLiteral].toStmBuild
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
          (inlineFunCalls(f), inlineFunCalls(arg)) match {
            case (f @ Function(x, body), arg)
                if x.typ.isData && body.typ.isData =>
              FunCall(f, arg)()
            case (Function(x, body), arg) =>
              body.subPreserveType(x -> arg)
            case (f, arg) =>
              FunCall(f, arg)()
          }
        case e =>
          e.map(inlineFunCalls)
      }
      result.tchk()
    }
  }

  private def uncurryBody(e: Expr): Expr = {
    val result = e match {
      case Function(x, body) =>
        Function(x, uncurryBody(body))()
      case e =>
        e.uncurry
    }
    result.tchk()
  }
}
