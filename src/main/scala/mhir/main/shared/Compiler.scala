package mhir.main.shared

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.debug.{DotPrinter, Tracer}
import mhir.delay.{DiscardAccumulatorDelays, ReplaceAccumulatorDelaysWithGo}
import mhir.eval.{Evaluator, TestError, TestRunner}
import mhir.gen._
import mhir.gen.vhdl.test._
import mhir.gen.vhdl.{VhdlGenerator, VhdlGeneratorOptions}
import mhir.ir._
import mhir.logging.{time, time2}
import mhir.optimize._
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
    // HACK: set global options so the lowering pass can read them easily (see
    // comment on the mhir.ir.globalOptions field)
    mhir.ir.globalOptions = GlobalOptions(handshake = prog.handshake)
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
    val (typeChecked, tchkTime) = typecheck(prog)
    val checked = time("semantic analysis (only names)", Level.DEBUG) {
      SemanticAnalyzer.checkNames(typeChecked)
    }
    val (lowered, lowerTime) = lower(checked)
    val (synthesizable, synthTime) = makeSynthesizable(lowered)
    time("semantic analysis", Level.DEBUG) {
      SemanticAnalyzer.check(synthesizable)
      SemanticAnalyzer.checkForWarnings(synthesizable)
    }
    val (finalProgram, optimTime) =
      optimize(
        synthesizable,
        options.optFlags,
        handshake = lowered.handshake
      )
    val latency = {
      val (inputs, body) = TypeChecker.unwrapTopLevelFunction(finalProgram.body)
      val analysis = new LatencyAnalysis(handshake = finalProgram.handshake)
      analysis.actualLatency(body, inputs.map(_ -> Some(0)).toMap).latency
    }
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
      SemanticAnalyzer.check(finalProgram)
    }
    val genTime = generateCode(options.vhdl, finalProgram, options.targets)
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
            evaluator.eval(finalProgram.body)
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
          DotPrinter(
            topName = finalProgram.accel.name,
            showReadyValidArrows = finalProgram.handshake
          )
            .dumpDot(trace, outDir, overwrite = overwrite)
        case TestTarget(expectedPath, actualPath, showPhysical, overwrite) =>
          TestRunner.run(
            finalProgram,
            expectedPath = expectedPath,
            actualPath = actualPath,
            showPhysical = showPhysical,
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
          emitPrettyPrinted(
            finalProgram.body,
            dest = dest,
            overwrite = overwrite
          )
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
    finalProgram.body
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

  private def makeSynthesizable(prog: Program): (Program, Duration) = {
    time2("making expression synthesizable", Level.DEBUG) {
      val e1 = inlineFunCalls(prog.body)
      val e2 = e1.streamify
      val e3 = if (prog.handshake) {
        // TODO: should I emit a warning that accumulator delays will be ignored?
        DiscardAccumulatorDelays.apply(e2)
      } else {
        new ReplaceAccumulatorDelaysWithGo(prog.go).apply(e2)
      }
      val e4 = {
        // This needs to happen after accumulator delay removal, since that
        // tends to increase the fanout of the "go" input
        insertLetForTopLevelInputs(e3)
      }
      val e5 = uncurryBody(e4)
      prog.copy(accel = prog.accel.copy(body = e5))
    }
  }

  private def optimize(
      prog: Program,
      optFlags: OptimizerOptions,
      handshake: Boolean
  ): (Program, Duration) = {
    time2("optimization", Level.DEBUG) {
      val optimizer = Optimizer(
        optFlags,
        handshake = handshake,
        headByParam = prog.headByParam
      )
      val newBody = optimizer.optimize(prog.body)
      val transform = if (prog.handshake) { (e: Expr) =>
        PartialEvalPass.partialEval(e)()
      } else {
        val latencyAnalysis = new LatencyAnalysis(handshake = prog.handshake)
        val latencyMatcher =
          new EnabledLatencyMatcher(latencyAnalysis, handshake = prog.handshake)
        val letBufShrinker = new StaticLetStmBufferShrinker(
          latencyAnalysis,
          handshake = prog.handshake,
          assumeThroughputsMatch = optFlags.assumeThroughputsMatch
        )
        (e: Expr) => {
          val e0 = PartialEvalPass.partialEval(e)()
          // Need to run latency matching and shrink all the letstm buffers
          // to avoid errors and warnings during evaluation
          val e1 = latencyMatcher.matchLatencies(e0, headByParam = Map())
          val e2 = letBufShrinker.shrinkBuffers(e1)
          e2
        }
      }
      val newTests = prog.test.map({
        case cd: ConstDecl => cd
        case Assertion(inputs, expectedOutput, ignore) =>
          val newInputs = inputs.map({ case (x, e) =>
            x -> transform(e)
          })
          Assertion(newInputs, expectedOutput, ignore)
      })
      prog.copy(accel = prog.accel.copy(body = newBody), test = newTests)
    }
  }

  private def generateCode(
      options: VhdlGeneratorOptions,
      prog: Program,
      targets: Set[CompilerTarget]
  ): Duration = {
    val (_, codegenTime) = time2("codegen", Level.DEBUG) {
      targets.foreach({
        case VhdlTarget(outDir, overwrite, _) =>
          emitVhdl(options, prog, outDir, overwrite = overwrite)
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
      overwrite: Boolean
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
    val hasAssertions = finalProgram.test.exists(_.isInstanceOf[Assertion])
    if (hasAssertions) {
      emitVhdlTestbench(
        finalProgram,
        options,
        outDir,
        designUsesIpBlocks = pipe.usesIpBlocks
      )
    } else {
      logger.info(
        s"skipping VHDL testbench generation because no assertions were found in the source code"
      )
    }
  }

  private def emitVhdlTestbench(
      prog: Program,
      options: VhdlGeneratorOptions,
      outDir: Path,
      designUsesIpBlocks: Boolean
  ): Unit = {
    time("generating VHDL testbench", Level.DEBUG) {
      assert(os.isDir(outDir))
      val assertions = prog.test.collect({ case a: Assertion => a })
      val io = TestSuiteIO(assertions.map({ case Assertion(in, out, ignore) =>
        val inputValues = in.map({ case (x, e) =>
          // TODO: enforce rule that inputs must be streams while type checking program
          x -> mhir.eval
            .eval(e, handshake = options.handshake)
            .asInstanceOf[StmLiteral]
        })
        val inputLatencies = inputValues.map({ case (x, s) =>
          x -> Some(s.physical.length)
        })
        val inputs = inputValues.map({ case (x, s) =>
          // TODO: Enforce rule that inputs must be streams while type checking program
          x -> DirectTestInput((s.physical ++ s.logical).map(Some(_)))
        })
        val TyStm(elemTyp, _) = out.typ
        val expectedOutput = {
          val StmLiteral(_, elems) =
            mhir.eval.eval(out, handshake = options.handshake)
          val ignoreElems = ignore match {
            case Some(ignore) =>
              val StmLiteral(_, ignoreLogical) =
                mhir.eval.eval(ignore, handshake = options.handshake)
              ignoreLogical
            case None =>
              elems.map(_ => AllZero(elemTyp))
          }
          val latency = {
            val analysis = new LatencyAnalysis(handshake = options.handshake)
            val (_, body) = TypeChecker.unwrapTopLevelFunction(prog.body)
            analysis.actualLatency(body, inputLatencies).latency
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
