package debug

import opt.PartialEvalPass
import ir._

object StmTracer {
  def trace(stm: StmBuild): Seq[String] = {
    val n = PartialEvalPass.partialEval(StmLength(stm)).asInstanceOf[IntCst].i
    trace(n, stm.seed, stm.nextF, step = 0)
  }

  private def trace(
      n: Int,
      acc: Expr,
      nextF: Function,
      step: Int
  ): Seq[String] = {
    if (n == 0) {
      Seq()
    } else {
      try {
        val next = PartialEvalPass.partialEval(FunCall(nextF, acc))
        val nextAcc = PartialEvalPass.partialEval(next.__0)
        val nextOut = PartialEvalPass.partialEval(next.__1)
        val accStr =
          PrettyPrinter.show(acc, collapseStm = true, evalVec = true)(Map())
        val outStr = PrettyPrinter.show(nextOut, evalVec = true)(Map())
        val summary =
          s"""Step ${step}:
             |    Accumulator: ${accStr}
             |    Next output: ${outStr}
             |""".stripMargin.stripTrailing
        val valid = nextOut match {
          case Tuple(_, True)  => true
          case Tuple(_, False) => false
          case e =>
            throw new IllegalArgumentException(
              s"Invalid stream output ${e}. Expected an Option (i.e., a tuple whose second element is a boolean)."
            )
        }
        summary +: trace(
          if (valid) n - 1 else n,
          nextAcc,
          nextF,
          step = step + 1
        )
      } catch {
        case e: AssertionError =>
          val summary =
            s"""Step ${step}:
             |    EXCEPTION: ${e}
             |""".stripMargin.stripTrailing
          Seq(summary)
      }
    }
  }
}
