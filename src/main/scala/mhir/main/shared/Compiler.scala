package mhir.main.shared

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.debug.{DotPrinter, Tracer}
import mhir.eval.{Evaluator, TestError, TestRunner}
import mhir.gen._
import mhir.gen.vhdl.test._
import mhir.gen.vhdl.{VhdlGenerator, VhdlGeneratorOptions}
import mhir.ir._
import mhir.logging.{time, time2}
import mhir.optimize.{LatencyAnalysis, Optimizer, OptimizerOptions}
import mhir.sem.SemanticAnalyzer
import mhir.sugar.Streamifier.Streamify
import mhir.sugar.Uncurrier.Uncurry
import mhir.sugar.{
  AllOne,
  AllZero,
  ExprLowering,
  ParamLowering,
  StmLiteralUtilsImplicit
}
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
    val (finalExpr, optimTime) =
      optimize(synthesizable, options.optFlags, handshake = lowered.handshake)
    val finalProgram =
      lowered.copy(accel = lowered.accel.copy(body = finalExpr))
    val latency = new LatencyAnalysis(handshake = lowered.handshake)
      .actualLatency(finalProgram.body)
      .latency
    latency match {
      case None =>
        if (finalProgram.handshake) {
          logger.debug(s"the latency of the design is unknown")
        } else {
          logger.warn(
            s"latency matching failed or was disabled, and the handshake protocol is disabled." +
              s" The VHDL design may not behave correctly."
          )
        }
      case Some(n) =>
        val cycleOrCycles = if (n == 1) "cycle" else "cycles"
        val msg = s"the design has a latency of $n $cycleOrCycles"
        if (finalProgram.handshake) {
          logger.debug(msg)
        } else {
          logger.info(msg)
        }
    }
    time("post-optimization semantic analysis", Level.DEBUG) {
      SemanticAnalyzer.check(
        lowered.copy(accel = lowered.accel.copy(body = finalExpr))
      )
    }
    val genTime =
      generateCode(options.vhdl, finalProgram, options.targets, latency)
    options.targets.toSeq
      .sortBy({
        case NullTarget                        => 0
        case _: PrettyPrintAfterLoweringTarget => 10
        case _: PrettyPrintTarget              => 20
        case _: EvalTarget                     => 30
        case _: TraceTarget                    => 40
        case _: CompileTimeTarget              => 50
        // The compiler will exit early if the tests fail.
        // Therefore, run things like pretty-printing beforehand, since they
        // may be useful for debugging the failing tests.
        case _: TestTarget => 60
        case _: VhdlTarget => 70
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
          val Assertion(inputs, _, _) = allAssertions(testIdx)
          val trace =
            Tracer.traceAll(
              finalProgram.body,
              handshake = finalProgram.handshake,
              inputs = inputs
            )
          DotPrinter.dumpDot(
            trace,
            outDir,
            overwrite = overwrite,
            topName = finalProgram.accel.name
          )
        case TestTarget(expectedPath, actualPath, overwrite) =>
          TestRunner.run(
            finalProgram,
            expectedPath = expectedPath,
            actualPath = actualPath,
            overwrite = overwrite
          )
        case VhdlTarget(outDir, _, runSim) =>
          if (runSim) {
            val result = VhdlTestRunner.testExistingProject(outDir)
            val moreInfoMsg =
              "For more details, try running './scripts/test_vhdl.sh . -v' in the generated VHDL directory."
            result match {
              case TestPassed =>
                logger.info("VHDL testbench passed!")
              case MissingVcom =>
                throw TestError(
                  "vcom does not seem to be working." +
                    " Is it installed and in your PATH?"
                )
              case DesignCompileFailed =>
                throw TestError(
                  s"compilation of the VHDL design failed. $moreInfoMsg"
                )
              case MissingVsim =>
                throw TestError(
                  "vsim does not seem to be working." +
                    " Is it installed and in your PATH?"
                )
              case TestbenchCompileFailed =>
                throw TestError(
                  s"compilation of the VHDL testbench failed. $moreInfoMsg"
                )
              case SimulationFailed =>
                throw TestError(s"VHDL simulation failed. $moreInfoMsg")
              case SimulationTimeout =>
                throw TestError(
                  "VHDL simulation timed out." +
                    " Is there an infinite loop?" +
                    s" $moreInfoMsg"
                )
              case NoTests =>
                throw TestError("no tests were found")
              case UnknownFailure =>
                throw TestError(
                  s"VHDL simulation failed for an unknown reason. $moreInfoMsg"
                )
            }
          }
        case PrettyPrintTarget(dest, overwrite) =>
          emitPrettyPrinted(finalExpr, dest = dest, overwrite = overwrite)
        case PrettyPrintAfterLoweringTarget(dest, overwrite) =>
          emitPrettyPrinted(lowered.body, dest = dest, overwrite = overwrite)
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
      val loweredTests =
        inlinedProg.test.map({
          case _: ConstDecl =>
            throw new RuntimeException(
              "constants should have been lowered by now"
            )
          case Assertion(inputs, expectedOutput, ignore) =>
            Assertion(
              inputs.map({ case (x, e) => x.lowerParam -> e.lower }),
              expectedOutput.lower,
              ignore.map(_.lower)
            )
        })
      inlinedProg.copy(
        accel = inlinedProg.accel.copy(body = loweredExpr),
        test = loweredTests
      )
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
        case ((subs, result), Assertion(in, out, ignore)) =>
          val newIn = in.map({ case (x, e) => x -> e.subPreserveType(subs) })
          val newOut = out.subPreserveType(subs)
          val newIgnore = ignore.map(_.subPreserveType(subs))
          (subs, result :+ Assertion(newIn, newOut, newIgnore))
      })
    Program(Seq(), newAccel, newTestSuite)
  }

  private def makeSynthesizable(e: Expr): (Expr, Duration) = {
    time2("making expression synthesizable", Level.DEBUG) {
      val e1 = inlineFunCalls(e)
      val e2 = e1.streamify
      val e3 = insertLetForTopLevelInputs(e2)
      val e4 = uncurryBody(e3)
      e4
    }
  }

  private def optimize(
      e: Expr,
      optFlags: OptimizerOptions,
      handshake: Boolean
  ): (Expr, Duration) = {
    time2("optimization", Level.DEBUG) {
      Optimizer(optFlags, handshake = handshake).optimize(e)
    }
  }

  private def generateCode(
      options: VhdlGeneratorOptions,
      prog: Program,
      targets: Set[CompilerTarget],
      latency: Option[Int]
  ): Duration = {
    val (_, codegenTime) = time2("codegen", Level.DEBUG) {
      targets.foreach({
        case VhdlTarget(outDir, overwrite, _) =>
          emitVhdl(
            options,
            prog,
            outDir,
            latency = latency,
            overwrite = overwrite
          )
        case _: EvalTarget                     => ()
        case _: TraceTarget                    => ()
        case _: TestTarget                     => ()
        case NullTarget                        => ()
        case _: PrettyPrintTarget              => ()
        case _: PrettyPrintAfterLoweringTarget => ()
        case _: CompileTimeTarget              => ()
      })
    }
    codegenTime
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
      finalProgram: Program,
      outDir: Path,
      overwrite: Boolean,
      latency: Option[Int]
  ): Unit = {
    val pipe = time("generating VHDL design", Level.DEBUG) {
      if (os.exists(outDir)) {
        if (overwrite) {
          os.remove.all(outDir)
        } else {
          throw new RuntimeException(
            s"Output directory $outDir already exists."
          )
        }
      }
      VhdlGenerator.emitVhdl(finalProgram.body, outDir, options)
    }
    val assertions = finalProgram.test.collect({ case a: Assertion => a })
    if (assertions.nonEmpty) {
      emitVhdlTestbench(
        assertions,
        options,
        outDir,
        latency,
        designUsesIpBlocks = pipe.usesIpBlocks
      )
    } else {
      logger.info(
        s"skipping VHDL testbench generation because no assertions were found in the source code"
      )
    }
  }

  private def emitVhdlTestbench(
      assertions: Seq[Assertion],
      options: VhdlGeneratorOptions,
      outDir: Path,
      latency: Option[Int],
      designUsesIpBlocks: Boolean
  ): Unit = {
    time("generating VHDL testbench", Level.DEBUG) {
      assert(os.isDir(outDir))
      val io = TestSuiteIO(assertions.map({ case Assertion(in, out, ignore) =>
        val inputs = in.map({ case (x, e) =>
          x -> (mhir.eval.eval(e) match {
            case StmLiteral(elems @ _*) =>
              DirectTestInput(elems.map(Some(_)))
            case e =>
              assert(
                e.typ.isData,
                "if the result of evaluation is not a stream, it should be one piece of data"
              )
              logger.warn(
                s"input for '$x' does not seem to be a stream." +
                  s" Accelerator inputs should normally be streams."
              )
              DirectTestInput(Seq(Some(e)))
          })
        })
        val expectedOutput = {
          val elems = mhir.eval.eval(out) match {
            case StmLiteral(elems @ _*) =>
              elems
            case e =>
              assert(
                e.typ.isData,
                "if the result of evaluation is not a stream, it should be one piece of data"
              )
              logger.warn(
                "expected output does not seem to be a stream." +
                  s" The accelerator output should normally be a stream."
              )
              Seq(e)
          }
          val elemTyp = out.typ match {
            case TyStm(t, _) => t
            case t           => t
          }
          val ignoreElems = ignore match {
            case Some(ignore) =>
              mhir.eval.eval(ignore) match {
                case StmLiteral(elems @ _*) =>
                  elems
                case e =>
                  assert(
                    e.typ.isData,
                    "if the result of evaluation is not a stream, it should be one piece of data"
                  )
                  logger.warn(
                    "ignore pattern does not seem to be a stream." +
                      s" The accelerator output should normally be a stream."
                  )
                  Seq(e)
              }
            case None =>
              elems.map(_ => AllZero(elemTyp))
          }
          latency match {
            case Some(latency) if !options.handshake =>
              logger.debug(
                s"adding $latency invalids at the beginning of the expected output to account for latency"
              )
              DirectTestOutput(
                (0 until latency).map(_ => Undefined(elemTyp)) ++ elems,
                (0 until latency).map(_ => AllOne(elemTyp)) ++ ignoreElems
              )
            case _ =>
              DirectTestOutput(elems, ignoreElems)
          }
        }
        KeywordTestIO(inputs, expectedOutput)
      }))
      VhdlTestbenchGenerator.makeDirectTestbench(
        io = io,
        dir = outDir,
        testNotReady = false,
        options = options
      )
      VhdlTestRunner.copyTestScripts(
        outDir,
        compileIpBlocks = designUsesIpBlocks
      )
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

  /** Insert [[mhir.sugar.Let]] for each top-level component input, in case the
    * input is used in many places.
    *
    * @param e
    *   the expression to transform. All inputs must be streams (i.e., the
    *   streamifier must be applied before calling this function).
    */
  private def insertLetForTopLevelInputs(e: Expr): Expr = {
    val (inputs, body) = TypeChecker.unwrapTopLevelFunction(e)
    val newBody = inputs.foldRight(body)({ case (x, body) =>
      val TyStm(_, n) = x.typ
      LetStm(n, x, x, body)().tchk().lower
    })
    TypeChecker.wrapTopLevelFunction(inputs, newBody)
  }

  /** Remove uncurried functions in the body of the program, but leave the
    * top-level component inputs curried.
    */
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
