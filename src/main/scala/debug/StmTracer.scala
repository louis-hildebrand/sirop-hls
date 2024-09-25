object StmTracer {
  def trace(stm: StmBuild): Seq[String] = {
    val n = ExprEvaluator.partialEval(StmLength(stm)).asInstanceOf[IntCst].i
    trace(n, stm.seed, stm.nextF, step = 0)
  }

  private def trace(
      n: Int,
      acc: Expr,
      nextF: Function,
      step: Int
  ): Seq[String] = {
    if n == 0 then {
      Seq()
    } else {
      try {
        val next = ExprEvaluator.partialEval(FunCall(nextF, acc))
        val nextAcc = ExprEvaluator.partialEval(next.__0)
        val nextOut = ExprEvaluator.partialEval(next.__1)
        val valid = boolExpr2Boolean(
          ExprEvaluator.partialEval(next.__2).asInstanceOf[BoolExpr]
        )
        val accStr =
          PrettyPrinter.show(acc, collapseStm = true, evalVec = true)(Map())
        val outStr = PrettyPrinter.show(nextOut, evalVec = true)(Map())
        val summary =
          s"""Step ${step}:
             |    Accumulator: ${accStr}
             |    Next output: ${outStr}
             |     Next valid: ${PrettyPrinter.show(valid)(Map())}
             |""".stripMargin.stripTrailing
        summary +: trace(
          if valid then n - 1 else n,
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
