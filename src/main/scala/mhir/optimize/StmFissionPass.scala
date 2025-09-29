package mhir.optimize

import mhir.ir._
import mhir.ir.typecheck.TypeCheck

import scala.annotation.tailrec

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
  override def enabled: Boolean = true

  override def fission(e: Expr): Expr = {
    e match {
      case s: StmBuild =>
        val newEquations = s.equations.map({
          case (x, (s, ready)) if x.typ.isInstanceOf[TyStm] =>
            x -> (fission(s), ready)
          case eqn => eqn
        })
        fissionStmBuild(
          StmBuild(s.n, s.data, s.valid, newEquations)()
            .tchk()
            .asInstanceOf[StmBuild]
        )
      case LetStm(bufSize, x, in, out) =>
        LetStm(bufSize, x, fission(in), fission(out))().tchk()
      case Function(x, body) if body.typ.isInstanceOf[TyStm] =>
        Function(x, fission(body))().tchk()
      case e => e
    }
  }

  @tailrec
  private def fissionStmBuild(stm: StmBuild): Expr = {
    this.scheduler.schedule(stm.data) match {
      case InProducer(data) =>
        StmBuild(stm.n, data, stm.valid, stm.equations)().tchk()
      case ic: InConsumer =>
        val FunCall(Function(x, cData), pData) = ic.asFunCall().tchk()
        val producer = StmBuild(stm.n, pData, stm.valid, stm.equations)().tchk()
        val TyStm(typ, n) = producer.typ
        val s = Param("s")(TyStm(typ, -1))
        val consumer = StmBuild(
          n,
          cData.subPreserveType(x -> StmData(s)().tchk()),
          True,
          Map[Param, (Expr, Expr)](
            s -> (producer, True)
          )
        )().tchk().asInstanceOf[StmBuild]
        fissionStmBuild(consumer)
    }
  }
}
