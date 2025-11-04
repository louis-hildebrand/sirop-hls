package mhir.main.shared

import mhir.optimize.OptimizerOptions
import os.Path

/** Options for the compiler.
  *
  * @param targets
  *   the compilation targets.
  * @param optFlags
  *   optimization settings.
  */
case class CompilerOptions(
    targets: Set[CompilerTarget],
    optFlags: OptimizerOptions
)

/** Companion object for [[CompilerOptions]].
  */
object CompilerOptions {

  /** Parses compiler options from command-line arguments.
    *
    * @param args
    *   the command-line arguments.
    */
  def apply(args: Array[String]): CompilerOptions = {
    // General args
    var vhdlDir: Option[Path] = None
    var prettyPrintDest: Option[PrettyPrintDestination] = None
    var timeReportFile: Option[Path] = None
    var overwrite = false
    var mutArgs = args
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
        case "--overwrite" =>
          overwrite = true
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
      val vhdlTarget = vhdlDir.map(VhdlTarget(_, overwrite = overwrite))
      val ppTarget =
        prettyPrintDest.map(PrettyPrintTarget(_, overwrite = overwrite))
      val timeReportTarget =
        timeReportFile.map(CompileTimeTarget(_, overwrite = overwrite))
      vhdlTarget.toSet ++ ppTarget.toSet ++ timeReportTarget.toSeq
    }
    if (targets.isEmpty) {
      throw new BadArgsException("no compilation targets specified")
    }
    CompilerOptions(
      targets = targets,
      optFlags = OptimizerOptions(
        simplifyStmBuild = simplifyStmBuild,
        inlineLetStm = inlineLetStm,
        fuse = fuse,
        fission = fission,
        matchLatency = matchLatency,
        staticallyShrinkLetStmBuffers = staticallyShrinkLetStmBuffers,
        maxLetStmBufSize = maxLetStmBufSize,
        balanceBinOpTrees = balanceBinOpTrees,
        assumeThroughputsMatch = assumeThroughputsMatch
      )
    )
  }

  def longUsage: String = {
    s"""General Arguments:
       |  --out:vhdl DIR      emit VHDL code in the given directory
       |  --out:pp (FILE|-)   pretty-print the final program to the given file, or to
       |                      stdout if argument "-" is given
       |  --out:ctime FILE    write a report of the compile time to the given
       |                      directory
       |  --overwrite         what to do if the output file or directory already
       |                      exists: if true then delete it, if false then raise an
       |                      error
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
  }
}
