package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.logging.time
import mhir.typecheck.TypeCheck
import org.slf4j.event.Level

import scala.annotation.tailrec

/** Stream fission splits a single [[mhir.ir.StmBuild]] into multiple stages.
  *
  * Fission tends to improve the maximum clock frequency of the design at the
  * cost of increasing resource usage.
  */
trait StmFissionPass {
  def enabled: Boolean
  final def disabled: Boolean = !this.enabled

  def fission(e: Expr): Expr
}

object StmFissionPass {
  def apply(enabled: Boolean, scheduler: StmOutputScheduler): StmFissionPass = {
    if (enabled) {
      EnabledStmFissionPass(scheduler)
    } else {
      DisabledStmFissionPass
    }
  }
}

object DisabledStmFissionPass extends StmFissionPass {
  override def enabled: Boolean = false

  override def fission(e: Expr): Expr = e
}

case class EnabledStmFissionPass(scheduler: StmOutputScheduler)
    extends StmFissionPass {

  private def logger: Logger = Logger(getClass.getName)

  override def enabled: Boolean = true

  override def fission(e: Expr): Expr = {
    e match {
      case s: StmBuild =>
        fissionStmBuild(
          s
            .mapProducers({ case (x, (s, ready, delay)) =>
              x -> (fission(s), ready, delay)
            })
            .tchk()
            .asInstanceOf[StmBuild]
        )
      case LetStm(bufSize, x, in, out) =>
        LetStm(bufSize, x, fission(in), fission(out))().tchk()
      case Function(x, body) =>
        Function(x, fission(body))().tchk()
      case e => e
    }
  }

  @tailrec
  private def fissionStmBuild(stm: StmBuild): Expr = {
    this.scheduler.schedule(stm.nextData) match {
      case InProducer(data) =>
        stm
          .copy(nextData = data)(
            typ = stm.typ,
            annotations = stm.annotations
          )
          .tchk()
      case ic: InConsumer =>
        stm.initData match {
          case _: Undefined =>
            val FunCall(Function(x, cData), pData) = ic.asFunCall().tchk()
            val producer: Expr = StmBuild(
              stm.n,
              stm.delay,
              Undefined(pData.typ),
              pData,
              stm.valid,
              stm.accumulators,
              stm.producers
            )(typ = Missing, annotations = stm.annotations).tchk()
            val TyStm(typ, n) = producer.typ
            val s = Param("s")(TyStm(typ, -1))
            val consumer = {
              val nextData = cData.subPreserveType(x -> StmData(s)().tchk())
              StmBuild(
                n,
                C(1)(),
                Undefined(nextData.typ),
                nextData,
                True,
                Map(),
                Map[Param, (Expr, Expr, Expr)](
                  s -> (producer, True, C(0)())
                )
              )().annotateWithName("Fission").tchk().asInstanceOf[StmBuild]
            }
            fissionStmBuild(consumer)
          case e =>
            val where =
              stm.nameAnnotation.map(name => s" (in $name)").getOrElse("")
            logger.warn(
              s"automatic fission is not supported when the head of the stream is $e$where." +
                s" Consider replacing the head with undefined or manually splitting this expression into multiple stages."
            )
            stm
        }
    }
  }
}

case class StmFissionPassWithLogging(underlying: StmFissionPass)
    extends StmFissionPass {

  private implicit val logger: Logger = Logger(getClass.getName)
  private var hasLogged: Boolean = false

  override def enabled: Boolean = this.underlying.enabled

  override def fission(e: Expr): Expr = {
    if (this.disabled && !this.hasLogged) {
      this.logger.debug(s"stream fission is disabled")
      this.hasLogged = true
    }
    time("stream fission", Level.DEBUG, mute = this.disabled) {
      this.underlying.fission(e)
    }
  }
}
