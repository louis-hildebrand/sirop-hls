package mhir

import com.typesafe.scalalogging.Logger
import org.slf4j.event.Level

import java.time.Duration

/** Utilities to help with logging.
  */
package object logging {

  /** Log when the given operation starts and ends, along with the time taken.
    *
    * @param name
    *   a short description of the operation (e.g., "type checking").
    * @param level
    *   the log level to use.
    * @param op
    *   the operation to run.
    * @param logger
    *   the logger to use.
    * @return
    *   the value produced by the operation.
    */
  def time[T](name: String, level: Level = Level.TRACE, mute: Boolean = false)(
      op: => T
  )(implicit logger: Logger): T = {
    val (result, _) = time2(name, level, mute)(op)
    result
  }

  /** Runs the given operation, logs its start and end times, and returns 2
    * values: the result and the duration.
    *
    * @param name
    *   a short description of the operation (e.g., "type checking").
    * @param level
    *   the log level to use.
    * @param op
    *   the operation to run.
    * @param logger
    *   the logger to use.
    * @return
    *   the value produced by the operation along with the elapsed time.
    */
  def time2[T](name: String, level: Level = Level.TRACE, mute: Boolean = false)(
      op: => T
  )(implicit logger: Logger): (T, Duration) = {
    val start = System.nanoTime()
    if (!mute) logger.underlying.atLevel(level).log(s"starting $name...")
    val result = op
    val end = System.nanoTime()
    val duration = Duration.ofNanos(end - start)
    if (!mute) {
      val ms = duration.toMillis
      logger.underlying.atLevel(level).log(s"finished $name in $ms ms")
    }
    (result, duration)
  }
}
