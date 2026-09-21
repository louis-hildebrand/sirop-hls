package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.logging.time
import mhir.typecheck._
import org.slf4j.event.Level

/** This transformation removes parts of the sbuild output data that are not
  * used by the consumer.
  *
  * @example
  *   in the following code, element 1 of the output of `x` is unused
  *   {{{
  *     sbuild(n)(sdata(s).0 + 5:u8, true) {} {
  *       (x: Stm[(u8, u8), n]) = { stm: ..., ready: true }
  *     }
  *   }}}
  *   Therefore, the `Stm[(u8, u8), n]` can be replaced by a `Stm[(u8, ()), n]`.
  */
trait UnusedDataRemover {

  def removeUnusedData(e: Expr): Expr

  def makeFunction(useStatus: UseStatus, inTyp: Type): Function

  def makeInverseFunction(useStatus: UseStatus, originalTyp: Type): Function
}

object UnusedDataRemover {

  def apply(enabled: Boolean): UnusedDataRemover = {
    if (enabled) EnabledUnusedDataRemover else DisabledUnusedDataRemover
  }
}

object DisabledUnusedDataRemover extends UnusedDataRemover {

  private val logger: Logger = Logger(getClass.getName)
  private var hasLogged: Boolean = false

  override def removeUnusedData(e: Expr): Expr = {
    if (!hasLogged) {
      hasLogged = true
      logger.debug("unused data removal is disabled")
    }
    e
  }

  override def makeFunction(
      useStatus: UseStatus,
      inTyp: Type
  ): mhir.ir.Function = {
    val x = Param("x")(inTyp)
    Function(x, x)().tchk().asInstanceOf[Function]
  }

  override def makeInverseFunction(
      useStatus: UseStatus,
      originalTyp: Type
  ): Function = {
    val x = Param("x")(originalTyp)
    Function(x, x)().tchk().asInstanceOf[Function]
  }
}

object EnabledUnusedDataRemover extends UnusedDataRemover {

  private implicit val logger: Logger = Logger(getClass.getName)

  def removeUnusedData(e: Expr): Expr = {
    time("removing unused parts of sbuild data", Level.DEBUG) {
      doRemoveUnusedData(e)
    }
  }

  override def makeFunction(
      useStatus: UseStatus,
      originalTyp: Type
  ): mhir.ir.Function = {
    val x = Param("x")(originalTyp)
    Function(x, makeFunctionBody(useStatus, x))().tchk().asInstanceOf[Function]
  }

  override def makeInverseFunction(
      useStatus: UseStatus,
      originalTyp: Type
  ): mhir.ir.Function = {
    val prunedTyp = makeFunctionBody(useStatus, Param("temp")(originalTyp)).typ
    assert(prunedTyp != Missing)
    val x = Param("x")(prunedTyp)
    val body = makeInverseFunctionBody(useStatus, x, originalTyp)
    assert(body.typ == originalTyp)
    Function(x, body)().tchk().asInstanceOf[Function]
  }

  private def makeFunctionBody(useStatus: UseStatus, x: Expr): Expr = {
    useStatus match {
      case AllUsed   => x
      case AllUnused => Tuple()().tchk()
      case SomeUnused(elems @ _*) =>
        val newElems = elems.zipWithIndex
          .flatMap({
            case (AllUnused, _) => None
            case (u, i) => Some(makeFunctionBody(u, TupleAccess(x, i)()))
          })
        if (newElems.length == 1) {
          newElems.head.tchk()
        } else {
          Tuple(newElems: _*)().tchk()
        }
    }
  }

  private def makeInverseFunctionBody(
      useStatus: UseStatus,
      x: Expr,
      originalTyp: Type
  ): Expr = {
    useStatus match {
      case AllUsed   => x
      case AllUnused => Undefined(originalTyp)
      case SomeUnused(elems @ _*) =>
        val TyTuple(typElems @ _*) = originalTyp
        assert(typElems.length == elems.length)
        val onlyOneUsed = elems.count(_ != AllUnused) == 1
        if (onlyOneUsed) {
          Tuple(
            elems
              .zip(typElems)
              .map({
                case (AllUnused, t) => Undefined(t)
                case (u, t)         => makeInverseFunctionBody(u, x, t)
              }): _*
          )().tchk()
        } else {
          val (exprElems, _) = elems
            .zip(typElems)
            .foldLeft(Seq[Expr](), 0)({
              case ((acc, nextIndex), (AllUnused, t)) =>
                (acc :+ Undefined(t), nextIndex)
              case ((acc, nextIndex), (u, t)) =>
                val newElem =
                  makeInverseFunctionBody(u, TupleAccess(x, nextIndex)(), t)
                (acc :+ newElem, nextIndex + 1)
            })
          Tuple(exprElems: _*)().tchk()
        }
    }
  }

  private def doRemoveUnusedData(e: Expr): Expr = {
    require(
      e.hasType,
      "expression must be type-checked before removing unused data"
    )
    val result = e match {
      case s1: StmBuild =>
        val s2 = s1.producers
          .foldLeft(s1)({
            case (consumer, (x, (producer: StmBuild, ready, delay))) =>
              val uses = UnusedDataAnalysis(x).findUnused(consumer)
              if (uses == AllUsed) {
                consumer
              } else {
                val TyStm(oldDataTyp, _) = x.typ
                val newDataTyp = transformTyp(oldDataTyp, uses)
                val newX = Param(x.prefix)(TyStm(newDataTyp, -1))
                val newProducerInitData = transformExpr(producer.initData, uses)
                assert(newProducerInitData.typ == newDataTyp)
                val newProducerNextData = transformExpr(producer.nextData, uses)
                assert(newProducerNextData.typ == newDataTyp)
                val newProducer = StmBuild(
                  producer.n,
                  producer.delay,
                  newProducerInitData,
                  newProducerNextData,
                  producer.valid,
                  producer.accumulators,
                  producer.producers
                )().tchk()
                StmBuild(
                  consumer.n,
                  consumer.delay,
                  consumer.initData,
                  consumer.nextData.subAndEraseType(x -> newX).tchk(),
                  consumer.valid.subAndEraseType(x -> newX).tchk(),
                  consumer.accumulators.map({ case (y, (init, next, delay)) =>
                    y -> (init, next.subAndEraseType(x -> newX).tchk(), delay)
                  }),
                  (consumer.producers - x)
                    .map({ case (y, (stm, ready, delay)) =>
                      y -> (stm, ready.subAndEraseType(x -> newX).tchk(), delay)
                    })
                    .+(newX -> (newProducer, ready, delay))
                )().tchk().asInstanceOf[StmBuild]
              }
            case (acc, _) => acc
          })
        // Recurse after handling this sbuild, not beforehand, so that
        // information about unused data propagates from sink back to source
        s2
          .mapProducers({
            case (x, (s, ready, delay)) if x.typ.isInstanceOf[TyStm] =>
              x -> (doRemoveUnusedData(s), ready, delay)
            case eqn => eqn
          })
          .tchk()
          .asInstanceOf[StmBuild]
      case e =>
        e.map(doRemoveUnusedData)
    }
    val typedResult = result.tchk()
    assert(
      typedResult.typ == e.typ,
      "removing unused data should preserve the type at the output of the stream"
        + s" (expected ${e.typ}, found ${typedResult.typ})"
    )
    typedResult
  }

  private def transformTyp(typ: Type, use: UseStatus): Type = {
    use match {
      case AllUsed   => typ
      case AllUnused => TyTuple()
      case SomeUnused(elems @ _*) =>
        typ match {
          case TyTuple(ts @ _*) if ts.length == elems.length =>
            TyTuple(
              ts.zip(elems).map({ case (t, u) => transformTyp(t, u) }): _*
            )
          case _ =>
            throw new IllegalArgumentException(
              s"use status $use does not correspond to type $typ"
            )
        }
    }
  }

  private def transformExpr(e: Expr, use: UseStatus): Expr = {
    use match {
      case AllUsed   => e
      case AllUnused => Tuple()().tchk()
      case SomeUnused(useElems @ _*) =>
        e match {
          case Tuple(exprElems @ _*) =>
            assert(exprElems.length == useElems.length)
            val newExprElems = exprElems
              .zip(useElems)
              .map({ case (e, u) => transformExpr(e, u) })
            Tuple(newExprElems: _*)().tchk()
          case e =>
            val exprElems = useElems.zipWithIndex
              .map({ case (u, i) =>
                transformExpr(TupleAccess(e, i)().tchk(), u)
              })
            Tuple(exprElems: _*)().tchk()
        }
    }
  }
}
