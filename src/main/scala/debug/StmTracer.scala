package debug

import ir._

import scala.annotation.tailrec

object StmTracer {

  /** Trace execution step-by-step, showing the accumulator values and outputs
    * for all streams.
    *
    * @param s
    *   Stream to trace
    * @return
    *   Summary of the stream state and output at each step
    */
  def traceAll(s: StmBuild): Seq[String] = {
    def trace(
        s: StmNode,
        summaries: Seq[String],
        t: Int
    ): Seq[String] = {
      s.state match {
        case Empty =>
          // TODO: Show one last summary?
          summaries.reverse
        case Stalled             => ???
        case Invalid             => ???
        case Valid(v)            => ???
        case Deadlocked(reasons) => ???
      }
    }

    trace(StmNode(s), Seq(), t = 0)
  }

  /** Trace execution step-by-step, showing the accumulator values and outputs
    * only for the top-level stream.
    *
    * @param s
    *   Stream to trace
    * @return
    *   Summary of the stream state and output at each step
    */
  def traceTop(s: StmBuild): Seq[String] = {
    def makeSummary(
        t: Int,
        acc: Map[Param, Expr],
        inputs: Map[Param, StmNode],
        v: Option[Expr]
    ): String = {
      val accStr = {
        val dataAccStrs = acc.map({ case (x, v) =>
          val vStr = PrettyPrinter.show(v, evalVec = true)(Map())
          s"${x.name} = $vStr"
        })
        val inputStrs = inputs.map({ case (x, s) =>
          s"${x.name} = StmBuild(${s.n}; ...)"
        })
        (dataAccStrs ++ inputStrs).toSeq.sorted.mkString("( ", ", ", " )")
      }
      val outStr = v match {
        case Some(v) => s"Some(${PrettyPrinter.show(v, evalVec = true)(Map())})"
        case None    => "None"
      }
      s"""Step $t:
         |    Accumulator: $accStr
         |    Output:      $outStr
         |""".stripMargin.stripTrailing
    }

    def makeDeadlockSummary(t: Int, reasons: Seq[DeadlockReason]): String = {
      s"""Step $t:
         |    DEADLOCK: ${reasons.mkString(", ")}
         |""".stripMargin.stripTrailing
    }

    @tailrec
    def trace(
        s: StmNode,
        summaries: Seq[String],
        t: Int
    ): Seq[String] = {
      s.state match {
        case Empty =>
          val sum = makeSummary(t, s.currentValByDataAcc, s.inputs, None)
          (sum +: summaries).reverse
        case Deadlocked(reasons) =>
          val sum = makeDeadlockSummary(t, reasons)
          (sum +: summaries).reverse
        case Stalled =>
          // This is still considered the same logical "step" even if multiple
          // physical clock cycles may pass
          trace(s.step(), summaries, t = t)
        case Invalid =>
          val sum = makeSummary(t, s.currentValByDataAcc, s.inputs, None)
          trace(s.step(), sum +: summaries, t = t + 1)
        case Valid(v) =>
          val sum = makeSummary(t, s.currentValByDataAcc, s.inputs, Some(v))
          trace(s.step(), sum +: summaries, t = t + 1)
      }
    }

    trace(StmNode(s), Seq(), t = 0)

//    val n = ir.eval(s.n).asInstanceOf[IntCst].i
//    if (n <= 0) {
//      Seq()
//    } else {
//      try {
//        val accStr = s.equations.toSeq
//          .sortBy({ case (x, _) => x.name })
//          .map({ case (x, (z, _)) =>
//            val zStr =
//              PrettyPrinter.show(z, collapseStm = true, evalVec = true)(Map())
//            s"${x.name} = $zStr"
//          })
//          .mkString("{ ", ", ", " }")
//        val (head, tail) = stepStream(s)
//        val outStr = head match {
//          case Some(v) =>
//            s"Some(${PrettyPrinter.show(v, evalVec = true)(Map())})"
//          case None => "None"
//        }
//        val summary =
//          s"""Step $step:
//             |    Accumulator: $accStr
//             |    Output:      $outStr
//             |""".stripMargin.stripTrailing
//        summary +: traceTop(tail.asInstanceOf[StmBuild], step = step + 1)
//      } catch {
//        case e: Exception =>
//          val summary =
//            s"""Step $step:
//             |    EXCEPTION: $e
//             |""".stripMargin.stripTrailing
//          Seq(summary)
//      }
//    }
  }
}
