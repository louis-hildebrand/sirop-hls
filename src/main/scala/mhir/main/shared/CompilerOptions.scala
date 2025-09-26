package mhir.main.shared

import mhir.optimize.OptimizerOptions
import os.Path

/** Options for the compiler.
  *
  * @param showFinal
  *   whether to print the final expression passed to the code generator.
  * @param target
  *   the target language.
  * @param optFlags
  *   optimization settings.
  */
case class CompilerOptions(
    showFinal: Boolean,
    target: CompilerTarget,
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
    var targetDir: Option[Path] = None
    var overwrite = false
    var emitHdl = true
    var showFinal = false
    var mutArgs = args
    // Optimizer args
    var simplifyStmBuild = true
    var inlineLetStm = true
    var fuse = true
    var matchLatency = true
    var staticallyShrinkLetStmBuffers = true
    var maxLetStmBufSize: Option[Int] = None
    var balanceBinOpTrees = true
    var assumeThroughputsMatch = false

    while (mutArgs.nonEmpty) {
      mutArgs.head match {
        case "-o" | "--out-dir" =>
          mutArgs.drop(1).headOption match {
            case Some(dirName) =>
              targetDir = Some(Path(dirName, base = os.pwd))
              mutArgs = mutArgs.drop(1)
            case None =>
              throw new BadArgsException(s"missing value for ${mutArgs.head}")
          }
        case "--overwrite" =>
          overwrite = true
        case "--no-hdl" =>
          emitHdl = false
        case "--show-final" =>
          showFinal = true
        case "--opt:no-simplify-sbuild" =>
          simplifyStmBuild = false
        case "--opt:no-inline-letstm" =>
          inlineLetStm = false
        case "--opt:no-fuse" =>
          fuse = false
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
              mutArgs = mutArgs.drop(1)
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
      mutArgs = mutArgs.drop(1)
    }

    val target = (targetDir, overwrite, emitHdl) match {
      case (None, false, false) =>
        NullTarget
      case (None, _, true) =>
        throw new BadArgsException(
          "either an output directory must be provided or --no-hdl must be set"
        )
      case (None, true, _) =>
        throw new BadArgsException(
          "flag --overwrite is not applicable unless an output directory is provided"
        )
      case (Some(_), _, false) =>
        throw new BadArgsException(
          "--no-hdl cannot be used when an output directory is provided"
        )
      case (Some(outDir), overwrite, true) =>
        VhdlTarget(outDir = outDir, overwrite = overwrite)
    }
    CompilerOptions(
      showFinal = showFinal,
      target = target,
      optFlags = OptimizerOptions(
        simplifyStmBuild = simplifyStmBuild,
        inlineLetStm = inlineLetStm,
        fuse = fuse,
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
       |  -o,--out-dir DIR    path to the directory in which to emit the generated HDL
       |                      code
       |  --overwrite         what to do if the output directory already exists: if true
       |                      then delete the existing directory, if false then raise an
       |                      error
       |  --no-hdl            skip code generation
       |  --show-final        show the final program right before code generation
       |
       |Optimization Flags:
       |  --opt:no-simplify-sbuild        skip basic sbuild simplifications
       |  --opt:no-inline-letstm          skip inlining letstm
       |  --opt:no-fuse                   skip the greedy stream fusion pass
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
