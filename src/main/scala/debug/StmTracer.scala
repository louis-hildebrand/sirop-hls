package debug

import opt.PartialEvalPass
import ir._

object StmTracer {
  def trace(stm: StmBuild): Seq[String] = {
    val n = PartialEvalPass.partialEval(stm.n).asInstanceOf[IntCst].i
    trace(n, stm.equations.toSeq, stm.output, step = 0)
  }

  private def trace(
      n: Int,
      equations: Seq[(Param, (Expr, Expr))],
      output: Expr,
      step: Int
  ): Seq[String] = {
    if (n <= 0) {
      Seq()
    } else {
      try {
        val accStr = equations.map({ case (x, (z, _)) =>
          val zStr =
            PrettyPrinter.show(z, collapseStm = true, evalVec = true)(Map())
          s"${x.name} = $zStr"
        })
        val (nextEquations, nextOut, nextOutValid) = {
          val currentValByVar: Map[Expr, Expr] =
            equations.map({ case (x, (z, _)) => x -> z }).toMap
          val nextEquations = equations
            .map({ case (x, (_, next)) =>
              val evaluatedNext = evalBigStep(next.substitute(currentValByVar))
              x -> (evaluatedNext, next)
            })
            .toMap
          val evaluatedOutput = evalBigStep(output.substitute(currentValByVar))
          evaluatedOutput match {
            case Tuple(_, v, valid: BoolCst) =>
              (
                // IMPORTANT: keep same order of accumulator elements
                equations.map({ case (x, _) => (x, nextEquations(x)) }),
                v,
                valid == True
              )
            case e =>
              throw new IllegalArgumentException(
                s"Output of StmNext evaluated to $e."
                  + " Expected it to evaluate to an option (i.e., a 2-tuple whose second element is a boolean)."
              )
          }
        }
        val outStr = PrettyPrinter.show(nextOut, evalVec = true)(Map())
        val summary =
          s"""Step $step:
             |    Accumulator: $accStr
             |    Next output: ($outStr, valid: $nextOutValid)
             |""".stripMargin.stripTrailing
        summary +: trace(
          if (nextOutValid) n - 1 else n,
          nextEquations,
          output,
          step = step + 1
        )
      } catch {
        case e: AssertionError =>
          val summary =
            s"""Step $step:
             |    EXCEPTION: $e
             |""".stripMargin.stripTrailing
          Seq(summary)
      }
    }
  }
}
