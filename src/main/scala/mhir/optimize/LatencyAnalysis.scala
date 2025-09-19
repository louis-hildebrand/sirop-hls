package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._

object LatencyAnalysis {

  private val logger = Logger(getClass.getName)

  val LetStmLatency: Int = 2

  def allLatenciesMatch(let: LetStm): Boolean = {
    val combine = (x: Int) => (y: Int) => if (x == y) Some(x) else None
    latencyOfPaths(let.x, let.out, combine = combine).nonEmpty
  }

  def latencyOfLongestPath(src: Param, e: Expr): Option[Int] = {
    latencyOfPaths(src, e, combine = x => y => Some(math.max(x, y)))
  }

  /** Finds the latency that the given [[mhir.ir.StmBuild]] contributes to the
    * path from `src` via `acc`.
    *
    * In other words, this is the latency of the longest path from `src` through
    * `s` via `acc`, minus the latency of the longest path from `src` to `acc`.
    */
  def latency(
      src: Param,
      acc: Param,
      s: StmBuild
  ): Option[Int] = {
    val lat = s.valid match {
      case True => Some(1)
      case Equal(t: Param, IntCst(k))
          if s.accVars.contains(t) && (k + 1).isValidInt =>
        s.equations.get(t) match {
          case Some(
                (
                  IntCst(0),
                  Mux(
                    Equal(t1: Param, IntCst(k1)),
                    IntCst(0),
                    Sum(IntCst(1), t2: Param)
                  )
                )
              ) if t1 == t && t2 == t && k1 == k =>
            Some((k + 1).toInt)
          case _ =>
            None
        }
      case _ =>
        None
    }
    lat match {
      case None =>
        logger.warn(s"failed to find latency from $src through StmBuild")
        logger.trace(s"sbuild: $s")
      case _ => ()
    }
    lat
  }

  private def latencyOfPaths(
      src: Param,
      e: Expr,
      combine: Int => Int => Option[Int]
  ): Option[Int] = {
    assert(e.freeVars().contains(src))
    e match {
      case x: Param if x == src =>
        Some(0)
      case s: StmBuild =>
        val equationsWithSrc = s.equations
          .filter({ case (_, (z, _)) => z.freeVars().contains(src) })
        // If this is a join node, we want it to be "zip-like."
        // With "zip-like" nodes, data from each path must arrive at the join
        // at the same time.
        // Don't deal with "concat-like" nodes, for example, which have
        // different optimal data arrival timing.
        val isJoin = equationsWithSrc.size > 1
        val isZipLike = equationsWithSrc
          .forall({ case (_, (_, ready)) => ready == True })
        if (isJoin && !isZipLike) {
          None
        } else {
          equationsWithSrc
            .map({ case (x, _) => x })
            .foldLeft[Option[Option[Int]]](None)({
              // Outer option will be None if we haven't checked any paths yet
              // Inner option will be None if we were unable to find the latency
              // for at least one path
              case (None, x)       => Some(latencyOfPaths(src, x, s, combine))
              case (Some(None), _) => Some(None)
              case (Some(Some(lat1)), x) =>
                Some(
                  latencyOfPaths(src, x, s, combine).flatMap(combine(lat1)(_))
                )
            })
            .get // There must be at least one path containing `src`
        }
      case LetStm(_, x, in, out) =>
        if (in.freeVars().contains(src)) {
          // (1) Latency from `src` to `in` plus latency from `x` to `out`
          latencyOfPaths(src, in, combine)
            .flatMap({ part1 =>
              latencyOfPaths(x, out, combine).map(part2 =>
                part1 + part2 + LetStmLatency
              )
            })
            // (2) Latency from `src` directly to `out`
            .flatMap(latencyViaIn => {
              if (out.freeVars().contains(src)) {
                latencyOfPaths(src, out, combine)
                  .flatMap(combine(latencyViaIn)(_))
              } else {
                Some(latencyViaIn)
              }
            })
        } else {
          // Only latency from `src` directly to `out`
          latencyOfPaths(src, out, combine)
        }
      case e =>
        logger.warn(
          s"I don't know how to find the latency through an expression of type ${e.getClass.getName}"
        )
        None
    }
  }

  /** Find the latency of the longest path from `src` through `s` via input
    * stream `acc`.
    *
    * The latency is from `src` to the <i>output</i> of `s`.
    *
    * @param s
    *   the end of the path to check.
    * @param acc
    *   the input stream via which to measure the latency.
    * @param src
    *   the beginning of the path.
    * @param combine
    *   the function to use to combine results from different paths.
    * @return
    *   `Some(latency)` if the latency can be calculated statically; otherwise
    *   `None`.
    */
  private def latencyOfPaths(
      src: Param,
      acc: Param,
      s: StmBuild,
      combine: Int => Int => Option[Int]
  ): Option[Int] = {
    val inputStm = s.seedByVar(acc)
    latencyOfPaths(src, inputStm, combine)
      .flatMap(before => latency(src, acc, s).map(thru => before + thru))
  }
}
