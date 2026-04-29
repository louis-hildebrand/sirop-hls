package mhir.main.shared

import com.typesafe.scalalogging.Logger
import jdk.jshell.EvalException
import mhir.canonicalize._
import mhir.debug.{DotPrinter, Tracer}
import mhir.eval.{Evaluator, TestError, TestRunner}
import mhir.gen.vhdl.{VhdlGenerator, VhdlGeneratorOptions}
import mhir.ir._
import mhir.logging.{time, time2}
import mhir.optimize.{Optimizer, OptimizerOptions}
import mhir.sem.SemanticAnalyzer
import mhir.sugar.Streamifier.Streamify
import mhir.sugar.Uncurrier.Uncurry
import mhir.sugar.{ExprLowering, StmLiteralUtilsImplicit}
import mhir.typecheck.{TypeCheck, TypeCheckProgram, TypeChecker}
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
    val vhdlOptions = {
      val vhdl0 = originalOptions.vhdl.copy(
        topName = topName,
        outName = prog.outName
      )
      val vhdl1 = prog.clock match {
        case Some(clock) => vhdl0.copy(clock = clock)
        case None        => vhdl0
      }
      val vhdl2 = prog.reset match {
        case Some(reset) => vhdl1.copy(reset = reset)
        case None        => vhdl1
      }
      val vhdl3 = vhdl2.copy(handshake = prog.handshake)
      vhdl3
    }
    val options = originalOptions.copy(vhdl = vhdlOptions)
    val (checked, tchkTime) = typecheck(prog)
    time("semantic analysis (only names)", Level.DEBUG) {
      SemanticAnalyzer.checkNames(checked)
    }
    val (lowered, lowerTime) = lower(checked)
    val (synthesizable, synthTime) = makeSynthesizable(lowered.body)
    time("semantic analysis", Level.DEBUG) {
      SemanticAnalyzer.check(
        lowered.copy(accel = lowered.accel.copy(body = synthesizable))
      )
    }
    val (finalExpr, optimTime) = optimize(synthesizable, options.optFlags)
    val finalProgram =
      lowered.copy(accel = lowered.accel.copy(body = finalExpr))
    time("post-optimization semantic analysis", Level.DEBUG) {
      SemanticAnalyzer.check(
        lowered.copy(accel = lowered.accel.copy(body = finalExpr))
      )
    }
    val genTime = generateCode(options.vhdl, finalExpr, options.targets)
    options.targets.toSeq
      .sortBy({
        case NullTarget           => 0
        case _: PrettyPrintTarget => 1
        case _: EvalTarget        => 2
        case _: TraceTarget       => 3
        case _: CompileTimeTarget => 4
        // The compiler will exit early if the tests fail.
        // Therefore, run things like pretty-printing beforehand, since they
        // may be useful for debugging the failing tests.
        case TestTarget    => 5
        case _: VhdlTarget => 6
      })
      .foreach({
        case NullTarget => ()
        case EvalTarget(maxInvalidSteps) =>
          val evaluator = Evaluator(
            handshake = finalProgram.handshake,
            maxInvalidSteps = maxInvalidSteps
          )
          val result = time("evaluation", Level.DEBUG) {
            evaluator.eval(finalExpr)
          }
          println(ExprPrinter.display(result))
        case TraceTarget(outDir, testIdx, overwrite) =>
          val allAssertions = finalProgram.test
            .collect({ case a: Assertion => a })
          if (testIdx < 0 || testIdx >= allAssertions.length) {
            val numTests = allAssertions.length
            val isOrAre = if (numTests == 1) "is" else "are"
            val testOrTests = if (numTests == 1) "test" else "tests"
            throw TestError(
              s"cannot generate trace from test case $testIdx because no such test exists." +
                s" There $isOrAre ${allAssertions.length} $testOrTests in total."
            )
          }
          val Assertion(inputs, _) = allAssertions(testIdx)
          val (_, body) = TypeChecker.unwrapTopLevelFunction(finalProgram.body)
          val trace =
            Tracer.traceAll(
              body,
              handshake = finalProgram.handshake,
              inputs = inputs
            )
          DotPrinter.dumpDot(
            trace,
            outDir,
            overwrite = overwrite,
            topName = finalProgram.accel.name
          )
        case TestTarget =>
          TestRunner.run(finalProgram)
        case _: VhdlTarget => () // already done
        case PrettyPrintTarget(dest, overwrite) =>
          emitPrettyPrinted(finalExpr, dest = dest, overwrite = overwrite)
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
    finalExpr
  }

  private def typecheck(prog: Program): (Program, Duration) = {
    time2("type checking", Level.DEBUG) {
      prog.tchk()
    }
  }

  private def lower(prog: Program): (Program, Duration) = {
    time2("lowering", Level.DEBUG) {
      val inlinedProg = inlineConstants(prog)
      val loweredExpr = translateStmLiteral(inlinedProg.accel.body.lower)
      inlinedProg.copy(accel = inlinedProg.accel.copy(body = loweredExpr))
    }
  }

  private def inlineConstants(prog: Program): Program = {
    val mainConstVals = prog.constants
      .map({ case ConstDecl(x, e) => x -> e })
      .toMap[Expr, Expr]
    val newAccel =
      prog.accel.copy(body = prog.accel.body.subPreserveType(mainConstVals))
    val (_, newTestSuite) =
      prog.test.foldLeft(mainConstVals, Seq[Assertion]())({
        case ((subs, result), ConstDecl(x, e)) =>
          (subs + (x -> e), result)
        case ((subs, result), Assertion(in, out)) =>
          val newIn = in.map({ case (x, e) => x -> e.subPreserveType(subs) })
          val newOut = out.subPreserveType(subs)
          (subs, result :+ Assertion(newIn, newOut))
      })
    Program(Seq(), newAccel, newTestSuite)
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
        case _: TraceTarget       => ()
        case TestTarget           => ()
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
