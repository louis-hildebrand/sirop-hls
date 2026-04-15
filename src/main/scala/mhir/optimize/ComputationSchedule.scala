package mhir.optimize

import mhir.canonicalize._
import mhir.ir._

/** Which pipeline stage a certain computation should be performed in---producer
  * or consumer.
  */
sealed trait ComputationSchedule

/** All computation should be performed in the producer.
  *
  * @param e
  *   the expression.
  */
case class InProducer(e: Expr) extends ComputationSchedule

/** Some computation should be performed in the consumer and some should be
  * performed in the producer.
  *
  * @param cData
  *   the computation to perform in the consumer. The expression may contain
  *   free variables for the data coming from the producer.
  * @param pData
  *   the computation to perform in the producer. This provides a value for each
  *   hole in [[cData]].
  */
case class InConsumer(cData: Expr, pData: Map[Param, Expr])
    extends ComputationSchedule {
  override def equals(obj: Any): Boolean = {
    obj match {
      case that: InConsumer =>
        that.canEqual(this) && this.asFunCall() == that.asFunCall()
      case _ => false
    }
  }

  override def hashCode(): Int = {
    this.asFunCall().hashCode
  }

  def asFunCall(): FunCall = {
    val sortedPData = pData.toSeq.sortBy({ case (x, _) => (x.prefix, x.id) })
    val tmp =
      Param("tmp")(TyTuple(sortedPData.map({ case (x, _) => x.typ }): _*))
    val arg = Tuple(sortedPData.map({ case (_, e) => e }): _*)()
    val subs = sortedPData.zipWithIndex
      .map({ case ((x, _), i) => x -> TupleAccess(tmp, i)() })
      .toMap[Expr, Expr]
    val body = cData.subAndEraseType(subs)
    FunCall(Function(tmp, body)(), arg)()
  }
}
