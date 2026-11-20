package mhir.delay

import com.typesafe.scalalogging.Logger
import mhir.ir._

/** Analysis for finding the latency of each node in the pipeline.
  *
  * The latency of a node is the number of clock cycles until its output
  * represents a valid logical element. For example, if `s` is available at time
  * 0, an expression like `s.StmMap(f).StmMap(g)` (where `f` and `g` are scalar
  * functions) has a latency of 2 cycles. In VHDL, the first two outputs from
  * this expression will be undefined (when the handshake protocol is enabled)
  * or part of the "physical output" (when the handshake protocol is disabled).
  *
  * @param handshake
  *   whether the handshake protocol is enabled.
  */
class LatencyAnalysis(handshake: Boolean) {

  private val logger: Logger = Logger(getClass.getName)

  /** Find the latency of each node, but only if the latency along each path is
    * consistent.
    *
    * If a [[mhir.ir.StmBuild]] node has multiple producer streams, this method
    * will insist that their latencies all be consistent with the corresponding
    * delay annotations.
    */
  def actualLatency(
      e: Expr,
      inputLatencies: Map[Param, Latency]
  ): LatencyNode = {
    val aggregator = (xs: Iterable[Int]) =>
      if (xs.toSet.size > 1) None else xs.headOption
    latencyForward(e, inputLatencies, aggregator)
  }

  /** Find the latency of each node assuming latency matching will be
    * successful.
    *
    * If a [[mhir.ir.StmBuild]] node has multiple producer streams, this method
    * will take the maximum of all their latencies (plus the delay added by the
    * [[mhir.ir.StmBuild]] itself).
    */
  def idealLatency(
      e: Expr,
      inputLatencies: Map[Param, Latency]
  ): LatencyNode = {
    latencyForward(e, inputLatencies, xs => Some(xs.max))
  }

  def solveForAutoDelays(
      e: Expr,
      latencyWithAuto: LatencyNode,
      latencyByParam: Map[Param, Latency]
  ): Map[Param, Int] = {
    val outLatency = latencyWithAuto.latency match {
      case LatencyConcrete(n) => Some(n)
      case LatencyAuto        =>
        // Apparently the latency of the pipeline entirely depends on inputs
        // with "auto" delay.
        // Set the total latency to an arbitrary value and solve for the input
        // delays.
        // With the output of the pipeline at t = 0, the input stream
        // latencies will probably end up being negative, but that's fine.
        Some(0)
      case LatencyUnknown =>
        logger.debug(
          "cannot solve for auto delays because final latency is unknown"
        )
        None
    }
    val delayUpdates = outLatency
      .map({ outLatency =>
        val backPass = new BackwardLatencyAnalysis(handshake = this.handshake)
        backPass.runAndRecordDelays(outLatency, e, latencyWithAuto)
        backPass.delayUpdates
      })
      .getOrElse(Map())
    assert(
      delayUpdates.keySet.subsetOf(latencyByParam.keySet), {
        val extraParams = delayUpdates.keySet
          .diff(latencyByParam.keySet)
          .toSeq
          .sortBy(_.name)
          .mkString(", ")
        s"delayUpdates has unknown params: $extraParams"
      }
    )
    latencyByParam
      .flatMap({
        case (x, LatencyAuto) =>
          // If an input does not appear in delayUpdates, it means its delay
          // doesn't matter.
          // Therefore, arbitrarily choose 0 for the delay.
          Some(x -> delayUpdates.getOrElse(x, 0))
        case _ => None
      })
  }

  private def latencyForward(
      e: Expr,
      latencyByVar: Map[Param, Latency],
      sbuildAggregator: Iterable[Int] => Option[Int]
  ): LatencyNode = {
    e match {
      case x: Param =>
        latencyByVar.get(x) match {
          case Some(lat) => LatencySource(lat)
          case None =>
            logger.warn(s"delay is unknown for input $x")
            LatencySource(LatencyUnknown)
        }
      case StmLiteral(physical, _) =>
        LatencySource(LatencyConcrete(physical.length))
      case LetStm(_, x, in, out) =>
        val inLatency = latencyForward(in, latencyByVar, sbuildAggregator)
        val selfLatency = LatencyAnalysis.letStmLatency(this.handshake)
        val inPlusSelfLatency = inLatency.latency match {
          case LatencyConcrete(n) => LatencyConcrete(n + selfLatency)
          case LatencyUnknown     => LatencyUnknown
          case LatencyAuto        => LatencyAuto
        }
        val outLatency = latencyForward(
          out,
          latencyByVar + (x -> inPlusSelfLatency),
          sbuildAggregator
        )
        LatencyLetStm(outLatency.latency, inLatency, outLatency)
      case s: StmBuild =>
        // Recurse
        val latencyChildren = s.producers.map({ case (x, (p, _, _)) =>
          x -> latencyForward(p, latencyByVar, sbuildAggregator)
        })
        // Collect the delay annotations
        val delayAnnotations =
          LatencyAnalysis.getProducerDelays(s, this.handshake)
        val outDelay = LatencyAnalysis.getOutDelay(s, this.handshake)
        // Assume "internal source nodes" have local epoch 0
        if (s.producers.isEmpty) {
          return LatencyStmBuild(
            outDelay match {
              case Some(n) => LatencyConcrete(n)
              case None    => LatencyUnknown
            },
            Some(0),
            Map()
          )
        }
        // If all producers have "auto" latency, then the output latency is also auto
        val allAuto = latencyChildren.nonEmpty &&
          latencyChildren.forall({ case (_, lat) =>
            lat.latency == LatencyAuto
          })
        if (allAuto) {
          return LatencyStmBuild(LatencyAuto, None, latencyChildren)
        }
        // For each producer, we can work out what the local epoch should be
        // knowing that the absolute latency of that producer should equal the
        // local epoch plus the corresponding delay annotation...
        val localEpochByProducer = latencyChildren
          // Ignore paths with "auto" latency: we'll solve for their latency
          // such that it matches the paths with concrete latency.
          .filter({ case (_, lat) => lat.latency != LatencyAuto })
          .map({ case (x, _) =>
            val epoch =
              (latencyChildren(x).latency, delayAnnotations.get(x)) match {
                case (LatencyConcrete(latency), Some(delay)) =>
                  Some(latency - delay)
                case _ => None
              }
            x -> epoch
          })
        // ... then we aggregate those answers using the given function (take
        // the maximum, insist they all be the same, etc.)
        val localEpoch = if (localEpochByProducer.values.forall(_.nonEmpty)) {
          sbuildAggregator(localEpochByProducer.values.map(_.get))
        } else {
          // At least one of the producers are missing a latency or a delay
          // annotation, so I have no idea what the epoch of this sbuild is
          None
        }
        val outLatency = localEpoch
          .zip(outDelay)
          .map({ case (localEpoch, outDelay) => localEpoch + outDelay })
          .headOption match {
          case Some(n) => LatencyConcrete(n)
          case None    => LatencyUnknown
        }
        LatencyStmBuild(outLatency, localEpoch, latencyChildren)
      case e =>
        throw new IllegalArgumentException(s"cannot find latency for $e")
    }
  }
}

object LatencyAnalysis {

  def letStmLatency(handshake: Boolean): Int = if (handshake) 2 else 0

  def getProducerDelays(
      s: StmBuild,
      handshake: Boolean
  ): Map[Param, Int] = {
    s.producers.flatMap({
      case (x, (_, _, IntCst(delay)))       => Some(x -> delay.toInt)
      case (x, (_, _, delay)) if !handshake =>
        // TODO: support non-static delay annotations?
        throw new IllegalArgumentException(
          s"non-static delay annotation $delay (on producer $x) is not supported yet"
        )
      case (x, p) => guessProducerDelay(p).map(x -> _)
    })
  }

  def getOutDelay(s: StmBuild, handshake: Boolean): Option[Int] = {
    s.delay match {
      case IntCst(delay)       => Some(delay.toInt)
      case delay if !handshake =>
        // TODO: support non-static delay annotations?
        throw new IllegalArgumentException(
          s"non-static delay annotation $delay (for sbuild output) is not supported yet"
        )
      case _ => guessOutDelay(s)
    }
  }

  private def guessProducerDelay(producer: (Expr, Expr, Expr)): Option[Int] = {
    producer match {
      case (_, True, Tuple()) => Some(0)
      // TODO: add case for StmConcat?
      case _ => None
    }
  }

  /** A guess at the output delay for one [[mhir.ir.StmBuild]] node, i.e., the
    * time at which the output becomes valid, relative to the <i>local<i> epoch.
    *
    * In other words, this is the latency the node would have if all its inputs
    * were immediately valid, at the global epoch.
    */
  private def guessOutDelay(s: StmBuild): Option[Int] = {
    s.valid match {
      case True => Some(1)
      case Equal(t: Param, IntCst(k))
          if s.namesDefinedHere.contains(t) && (k + 1).isValidInt =>
        s.accumulators.get(t) match {
          case Some(
                (
                  IntCst(0),
                  Mux(
                    Equal(t1: Param, IntCst(k1)),
                    IntCst(0),
                    Sum(IntCst(1), t2: Param)
                  ),
                  _
                )
              ) if t1 == t && t2 == t && k1 == k =>
            Some((k + 1).toInt)
          case _ =>
            None
        }
      case _ => None
    }
  }
}

private class BackwardLatencyAnalysis(handshake: Boolean) {

  var delayUpdates: Map[Param, Int] = Map()

  def runAndRecordDelays(
      targetLatency: Int,
      e: Expr,
      latencyWithAuto: LatencyNode
  ): Unit = {
    latencyWithAuto match {
      case LatencySource(latency) =>
        latency match {
          case LatencyAuto =>
            val x = e.asInstanceOf[Param]
            val newTargetLatency = this.delayUpdates
              .getOrElse(x, Int.MaxValue)
              .min(targetLatency)
            this.delayUpdates += (x -> newTargetLatency)
          case _ => ()
        }
      case LatencyLetStm(_, inLat, outLat) =>
        val let = e.asInstanceOf[LetStm]
        this.runAndRecordDelays(targetLatency, let.out, outLat)
        val xLatencyBeforeFork = this.delayUpdates
          // (1) Maybe we just found the latency of `let.x`
          .get(let.x)
          .map({ xLatencyAfterFork =>
            assert(
              !inLat.latency.isInstanceOf[LatencyConcrete],
              s"why was ${let.x} added to delayUpdates if its latency was already known?"
            )
            // We just figured out what the latency of `let.x` should be *after*
            // the fork, but what should it be *before* the fork?
            // In no_handshake mode this will be the same value, but with the
            // handshake protocol letstm itself adds some latency.
            xLatencyAfterFork - LatencyAnalysis.letStmLatency(this.handshake)
          })
          .getOrElse(
            inLat.latency match {
              // (2) Maybe the latency of `let.x` was already known; if so, use that value
              case LatencyConcrete(n) =>
                n + LatencyAnalysis.letStmLatency(this.handshake)
              // (3) Maybe `let.x` is unused
              case LatencyAuto =>
                // Just continue with some random value; apparently it doesn't matter
                0
              // (4) Maybe the latency could not be found (e.g., in a program
              // with dynamic scheduling)
              case LatencyUnknown =>
                assert(
                  !this.handshake,
                  s"failed to calculate latency of ${let.x}, even though the handshake protocol is disabled"
                )
                return
            }
          )
        this.delayUpdates -= let.x
        this.runAndRecordDelays(xLatencyBeforeFork, let.in, inLat)
      case LatencyStmBuild(_, maybeLocalEpoch, producers) =>
        val s = e.asInstanceOf[StmBuild]
        assert(s.producers.size == producers.size)
        val localEpoch = maybeLocalEpoch
          .orElse({
            val outDelay = LatencyAnalysis.getOutDelay(s, handshake)
            // Calculate the local epoch, knowing that localEpoch + outDelay should match the target latency
            outDelay.map(targetLatency - _)
          })
          .getOrElse({
            // We're stuck: we can't calculate the expected producer latencies
            // without the local epoch
            assert(
              !this.handshake,
              "failed to calculate local epoch in sbuild, even though the handshake protocol is disabled"
            )
            return
          })
        // Calculate the expected latency for each producer, knowing that
        // the absolute latency of that producer should equal the local
        // epoch plus the corresponding delay annotation...
        val delayAnnotations =
          LatencyAnalysis.getProducerDelays(s, this.handshake)
        val expectedLatencyByProducer =
          producers.flatMap({ case (x, _) =>
            delayAnnotations.get(x) match {
              case Some(delay) => Some(x -> (localEpoch + delay))
              case None        => None
            }
          })
        // Recurse
        producers.foreach({ case (x, lat) =>
          expectedLatencyByProducer.get(x) match {
            case None => ()
            case Some(xLatency) =>
              val (p, _, _) = s.producers(x)
              this.runAndRecordDelays(xLatency, p, lat)
          }
        })
    }
  }
}
