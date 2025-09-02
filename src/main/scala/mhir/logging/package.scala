package mhir

import com.typesafe.scalalogging.Logger
import org.slf4j.event.Level

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
  def time[T](name: String, level: Level = Level.TRACE)(
      op: => T
  )(implicit logger: Logger): T = {
    val start = System.nanoTime()
    logger.underlying.atLevel(level).log(s"starting $name...")
    val result = op
    val duration = (System.nanoTime() - start) / 1000000L
    logger.underlying.atLevel(level).log(s"finished $name in $duration ms")
    result
  }
}
