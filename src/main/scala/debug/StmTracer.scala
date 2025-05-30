package debug

import ir._

object StmTracer {

  /** Trace the execution of a stream step-by-step, showing the accumulator
    * values and outputs.
    *
    * @param stm
    *   Stream to trace
    * @return
    *   Summary of the stream state and output at each step
    */
  def trace(stm: StmBuild): Seq[String] = {
    trace(stm, step = 0)
  }

  private def trace(s: StmBuild, step: Int): Seq[String] = {
    val n = ir.eval(s.n).asInstanceOf[IntCst].i
    if (n <= 0) {
      Seq()
    } else {
      try {
        val accStr = s.equations.toSeq
          .sortBy({ case (x, _) => x.name })
          .map({ case (x, (z, _)) =>
            val zStr =
              PrettyPrinter.show(z, collapseStm = true, evalVec = true)(Map())
            s"${x.name} = $zStr"
          })
          .mkString("{ ", ", ", " }")
        val (head, tail) = stepStream(s)
        val outStr = head match {
          case Some(v) =>
            s"Some(${PrettyPrinter.show(v, evalVec = true)(Map())})"
          case None => "None"
        }
        val summary =
          s"""Step $step:
             |    Accumulator: $accStr
             |    Output:      $outStr
             |""".stripMargin.stripTrailing
        summary +: trace(tail.asInstanceOf[StmBuild], step = step + 1)
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
