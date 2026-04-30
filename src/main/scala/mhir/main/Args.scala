package mhir.main

import ch.qos.logback.classic.Level
import mhir.gen.vhdl.VhdlGeneratorOptions
import mhir.main.shared._
import mhir.main.stored.Program
import mhir.optimize.OptimizerOptions
import os.Path

/** Parsed command-line arguments.
  *
  * @param src
  *   the program to compile.
  * @param options
  *   options to pass to the compiler.
  */
case class Args(src: Option[Source], options: CompilerOptions)

/** Companion object for [[Args]].
  */
object Args {

  /** Parses the given command-line arguments.
    *
    * @param args
    *   the raw command-line arguments.
    */
  def apply(args: Seq[String]): Args = {
    if (args.contains("-h") || args.contains("--help")) {
      throw HelpException
    }
    if (args.contains("--version")) {
      throw VersionException
    }

    // Input args
    var sourceLang = "sirop"
    var input: Option[String] = None
    // Output args
    var vhdlDir: Option[Path] = None
    var prettyPrintDest: Option[PrettyPrintDestination] = None
    var timeReportFile: Option[Path] = None
    var eval: Boolean = false
    var traceOutDir: Option[Path] = None
    var traceTestIdx: Option[Int] = None
    var runTests: Boolean = false
    var maxInvalidSteps: Option[Int] = None
    var overwrite = false
    var mutArgs = args
    var logLevel = Level.INFO
    // VHDL args
    var vhdlFamily: Option[String] = None
    var vhdlDevice: Option[String] = None
    // Optimizer args
    var simplifyStmBuild = true
    var inlineLetStm = true
    var fuse = true
    var fission = true
    var matchLatency = true
    var staticallyShrinkLetStmBuffers = true
    var maxLetStmBufSize: Option[Int] = None
    var balanceBinOpTrees = true
    var assumeThroughputsMatch = false

    while (mutArgs.nonEmpty) {
      var numToDrop = 1
      mutArgs.head match {
        // Input args
        case "-s" =>
          mutArgs.drop(1).headOption match {
            case Some(lang) =>
              sourceLang = lang
              numToDrop = 2
            case None =>
              throw new BadArgsException(s"missing value for ${mutArgs.head}")
          }
        case "-i" =>
          mutArgs.drop(1).headOption match {
            case Some(in) =>
              input = Some(in)
              numToDrop = 2
            case None =>
              throw new BadArgsException(s"missing value for ${mutArgs.head}")
          }
        // Output args
        case "--out:vhdl" =>
          mutArgs.drop(1).headOption match {
            case Some(dirName) =>
              vhdlDir = Some(Path(dirName, base = os.pwd))
              numToDrop = 2
            case None =>
              throw new BadArgsException(s"missing value for ${mutArgs.head}")
          }
        case "--out:pp" =>
          mutArgs.drop(1).headOption match {
            case Some("-") =>
              prettyPrintDest = Some(PPStdout)
            case Some(fName) =>
              prettyPrintDest = Some(PPFile(Path(fName, base = os.pwd)))
            case None =>
              throw new BadArgsException(s"missing value for ${mutArgs.head}")
          }
          numToDrop = 2
        case "--out:ctime" =>
          mutArgs.drop(1).headOption match {
            case Some(fName) =>
              timeReportFile = Some(Path(fName, base = os.pwd))
              numToDrop = 2
            case None =>
              throw new BadArgsException(s"missing value for ${mutArgs.head}")
          }
        case "--out:eval" =>
          eval = true
        case "--out:eval:max-invalid-steps" =>
          mutArgs.drop(1).headOption match {
            case Some(num) =>
              val stepsInt =
                try {
                  num.toInt
                } catch {
                  case _: NumberFormatException =>
                    throw new BadArgsException(
                      s"value for ${mutArgs.head} must be an integer (found $num)"
                    )
                }
              maxInvalidSteps = Some(stepsInt)
              numToDrop = 2
            case None =>
              throw new BadArgsException(s"missing value for ${mutArgs.head}")
          }
        case "--out:trace" =>
          mutArgs.drop(1).headOption match {
            case Some(dirName) =>
              traceOutDir = Some(Path(dirName, base = os.pwd))
              numToDrop = 2
            case None =>
              throw new BadArgsException(s"missing value for ${mutArgs.head}")
          }
        case "--out:trace:test" =>
          mutArgs.drop(1).headOption match {
            case Some(num) =>
              val testIdx =
                try {
                  num.toInt
                } catch {
                  case _: NumberFormatException =>
                    throw new BadArgsException(
                      s"value for ${mutArgs.head} must be an integer (found $num)"
                    )
                }
              traceTestIdx = Some(testIdx)
              numToDrop = 2
            case None =>
              throw new BadArgsException(s"missing value for ${mutArgs.head}")
          }
        case "--out:test" =>
          runTests = true
        case "--overwrite" =>
          overwrite = true
        case "-q" | "--quiet" =>
          logLevel = Level.WARN
        case "-v" | "--verbose" =>
          logLevel = Level.DEBUG
        // VHDL args
        case "--out:vhdl:family" =>
          mutArgs.drop(1).headOption match {
            case Some(family) =>
              vhdlFamily = Some(family)
              numToDrop = 2
            case None =>
              throw new BadArgsException(s"missing value for ${mutArgs.head}")
          }
        case "--out:vhdl:device" =>
          mutArgs.drop(1).headOption match {
            case Some(device) =>
              vhdlDevice = Some(device)
              numToDrop = 2
            case None =>
              throw new BadArgsException(s"missing value for ${mutArgs.head}")
          }
        // Optimizer args
        case "--opt:no-simplify-sbuild" =>
          simplifyStmBuild = false
        case "--opt:no-inline-letstm" =>
          inlineLetStm = false
        case "--opt:no-fuse" =>
          fuse = false
        case "--opt:no-fission" =>
          fission = false
        case "--opt:no-latmatch" =>
          matchLatency = false
        case "--opt:no-static-buf-shrink" =>
          staticallyShrinkLetStmBuffers = false
        case "--opt:max-let-buf-size" =>
          mutArgs.drop(1).headOption match {
            case Some(sizeStr) =>
              val sizeInt =
                try {
                  sizeStr.toInt
                } catch {
                  case _: NumberFormatException =>
                    throw new BadArgsException(
                      s"value for ${mutArgs.head} must be an integer (found $sizeStr)"
                    )
                }
              if (sizeInt < 0) {
                throw new BadArgsException(
                  s"value for ${mutArgs.head} must be non-negative (got $sizeInt)"
                )
              }
              maxLetStmBufSize = Some(sizeInt)
              numToDrop = 2
            case None =>
              throw new BadArgsException(s"missing value for ${mutArgs.head}")
          }
        case "--opt:no-balance-binop-trees" =>
          balanceBinOpTrees = false
        case "--opt:assume-throughputs-match" =>
          assumeThroughputsMatch = true
        case a =>
          throw new BadArgsException(s"unknown argument: $a")
      }
      mutArgs = mutArgs.drop(numToDrop)
    }

    val targets: Set[CompilerTarget] = {
      val evalTarget = if (eval) {
        Some(EvalTarget(maxInvalidSteps))
      } else {
        if (maxInvalidSteps.isDefined) {
          throw new BadArgsException(
            s"--out:eval:max-invalid-steps is only valid when --out:eval is also given"
          )
        }
        None
      }
      val traceTarget = traceOutDir.map(
        TraceTarget(
          _,
          testIdx = traceTestIdx.getOrElse(0),
          overwrite = overwrite
        )
      )
      val testTarget = if (runTests) Some(TestTarget) else None
      val vhdlTarget = vhdlDir.map(VhdlTarget(_, overwrite = overwrite))
      val ppTarget =
        prettyPrintDest.map(PrettyPrintTarget(_, overwrite = overwrite))
      val timeReportTarget =
        timeReportFile.map(CompileTimeTarget(_, overwrite = overwrite))
      (evalTarget.toSet
        ++ traceTarget.toSet
        ++ testTarget.toSet
        ++ vhdlTarget.toSet
        ++ ppTarget.toSet
        ++ timeReportTarget.toSet)
    }
    val src: Option[Source] = (sourceLang, input) match {
      case ("sirop", None) =>
        None
      case ("sirop", Some(f)) =>
        Some(SiropSource(Path(f, base = os.pwd)))
      case ("aetherling", Some(f)) =>
        Some(AetherlingSource(Path(f, base = os.pwd)))
      case ("stored", Some(progName)) =>
        Some(StoredSource(Program(progName)))
      case (lang @ ("aetherling" | "stored"), None) =>
        throw new BadArgsException(
          s"REPL is not available for language '$lang'"
        )
      case (lang, _) =>
        throw new BadArgsException(s"unknown language: '$lang'")
    }
    if (targets.isEmpty && src.nonEmpty) {
      throw new BadArgsException("no compilation targets specified")
    }
    val options = CompilerOptions(
      targets = targets,
      vhdl = {
        val opt0 = VhdlGeneratorOptions()
        val opt1 = vhdlFamily match {
          case Some(family) => opt0.copy(deviceFamily = family)
          case None         => opt0
        }
        val opt2 = vhdlDevice match {
          case Some(device) => opt1.copy(device = device)
          case None         => opt1
        }
        opt2
      },
      optFlags = OptimizerOptions(
        simplifyStmBuild = simplifyStmBuild,
        inlineLetStm = inlineLetStm,
        fuse = fuse,
        fission = fission,
        matchLatency = matchLatency,
        staticallyShrinkLetStmBuffers = staticallyShrinkLetStmBuffers,
        maxLetStmBufSize = maxLetStmBufSize,
        balanceBinOpTrees = balanceBinOpTrees,
        assumeThroughputsMatch = (
          // The Aetherling compiler guarantees that each producer/consumer pair
          // has the same "time."
          assumeThroughputsMatch || src.exists(_.isInstanceOf[AetherlingSource])
        )
      ),
      logLevel = Some(logLevel)
    )
    new Args(src = src, options = options)
  }

  private[main] def printShortUsage(): Unit = {
    println(
      s"Usage: sirop -s (sirop|aetherling|stored) [-i INPUT] [OPTION]... [-h|--help] [--version]"
    )
  }

  private[main] def printFullUsage(): Unit = {
    this.printShortUsage()
    println()
    println(
      s"""Source Arguments:
         |  -s (sirop|aetherling|stored)  source language (default: sirop)
         |  -i INPUT                      where to get the source code.
         |                                With -s stored, this is the program name.
         |                                Otherwise, this is the path to the source file.
         |                                If this argument is omitted, the REPL will be
         |                                launched.
         |  -h, --help                    print the help message and exit
         |  --version                     print the compiler version and exit
         |
         |Output Arguments:
         |  --out:eval                       evaluate the program and print its value
         |  --out:eval:max-invalid-steps N   maximum number of invalid sbuild outputs when
         |                                   evaluating. A negative value disables the
         |                                   limit.
         |
         |  --out:trace DIR                  generate a trace in the given directory. The
         |                                   trace shows the state of the accelerator at
         |                                   each time step.
         |  --out:trace:test TEST            the zero-based index of the test case from
         |                                   which to get the inputs to the accelerator
         |                                   when generating the trace
         |
         |  --out:test                       run the tests and print the results
         |
         |  --out:vhdl DIR                   emit VHDL code in the given directory
         |  --out:vhdl:family                the value for the FAMILY assignment in the
         |                                   .qsf file
         |  --out:vhdl:device                the value for the DEVICE assignment in the
         |                                   .qsf file
         |
         |  --out:pp (FILE|-)                pretty-print the final program to the given
         |                                   file, or to stdout if argument "-" is given
         |
         |  --out:ctime FILE                 write a report of the compile time to the given
         |                                   directory
         |
         |  --overwrite                      what to do if the output file or directory
         |                                   already exists: if true then delete it, if
         |                                   false then raise an error
         |  -q,--quiet                       reduce the number of log messages
         |  -v,--verbose                     increase the number of log messages
         |
         |Optimization Flags:
         |  --opt:no-simplify-sbuild        skip basic sbuild simplifications
         |  --opt:no-inline-letstm          skip inlining letstm
         |  --opt:no-fuse                   skip the greedy stream fusion pass
         |  --opt:no-fission                skip the stream fission pass
         |  --opt:no-latmatch               skip the latency matching pass
         |  --opt:no-static-buf-shrink      skip static letstm buffer shrinking
         |  --opt:max-let-buf-size SIZE     maximum buffer size for letstm
         |  --opt:no-balance-binop-trees    skip the binop tree balancing pass
         |  --opt:assume-throughputs-match  whether the optimizer can assume the
         |                                  throughputs along different branches of a
         |                                  letstm match. This is always the case for
         |                                  Aetherling programs, for example.
         |""".stripMargin.stripTrailing()
    )
  }
}
