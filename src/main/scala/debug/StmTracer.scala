package debug

import opt.PartialEvalPass
import ir._

object StmTracer {
  def trace(stm: StmBuild): Seq[String] = {
    val n = PartialEvalPass.partialEval(StmLength(stm)).asInstanceOf[IntCst].i
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
        val currentAccValues = equations.map({ case (_, (z, _)) => z })
        val accStr = PrettyPrinter.show(
          Tuple(currentAccValues: _*),
          collapseStm = true,
          evalVec = true
        )(Map())
        val (nextEquations, nextOut, nextOutValid) =
          ir.eval(StmNext(StmBuild(n, output, equations.toMap))) match {
            case Tuple(
                  StmBuild(_, _, nextEquations),
                  out @ Tuple(_, valid @ (True | False))
                ) =>
              (
                equations.map({ case (x, _) => (x, nextEquations(x)) }),
                out,
                valid == True
              )
            case e =>
              throw new IllegalArgumentException(
                s"StmNext evaluated to $e. Expected it to evaluate to a 2-tuple."
              )
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
