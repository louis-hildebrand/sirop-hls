package debug

import opt.PartialEvalPass
import ir._

object StmTracer {
  def trace(stm: StmBuild): Seq[String] = {
    val n = PartialEvalPass.partialEval(stm.n).asInstanceOf[IntCst].i
    trace(n, stm.equations.toSeq, stm.data, stm.valid, step = 0)
  }

  private def trace(
      n: Int,
      equations: Seq[(Param, (Expr, Expr))],
      data: Expr,
      valid: Expr,
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
          val evaluatedData = evalBigStep(data.substitute(currentValByVar))
          val evaluatedValid = evalBigStep(valid.substitute(currentValByVar))
          assert(
            evaluatedValid.isInstanceOf[BoolCst],
            s"stream valid expression must evaluate to a boolean (found $evaluatedValid)"
          )
          (
            // IMPORTANT: keep same order of accumulator elements
            equations.map({ case (x, _) => (x, nextEquations(x)) }),
            evaluatedData,
            evaluatedValid == True
          )
        }
        val outStr =
          if (nextOutValid)
            s"Some(${PrettyPrinter.show(nextOut, evalVec = true)(Map())})"
          else "None"
        val summary =
          s"""Step $step:
             |    Accumulator: $accStr
             |    Next output: $outStr
             |""".stripMargin.stripTrailing
        summary +: trace(
          if (nextOutValid) n - 1 else n,
          nextEquations,
          data,
          valid,
          step = step + 1
        )
      } catch {
        case e: Exception =>
          val summary =
            s"""Step $step:
             |    EXCEPTION: $e
             |""".stripMargin.stripTrailing
          Seq(summary)
      }
    }
  }
}
