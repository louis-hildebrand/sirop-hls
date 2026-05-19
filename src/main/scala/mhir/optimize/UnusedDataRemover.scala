package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._

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
}

object UnusedDataRemover {

  def apply(enabled: Boolean): UnusedDataRemover = {
    if (enabled) EnabledUnusedDataRemover else DisabledUnusedDataRemover
  }
}

object DisabledUnusedDataRemover extends UnusedDataRemover {

  override def removeUnusedData(e: Expr): Expr = e
}

object EnabledUnusedDataRemover extends UnusedDataRemover {

  def removeUnusedData(e: Expr): Expr = {
    require(
      e.hasType,
      "expression must be type-checked before removing unused data"
    )
    val result = e match {
      case s1: StmBuild =>
        val s2 = s1.producers
          .foldLeft(s1)({
            case (consumer, (x, (producer: StmBuild, ready))) =>
              val uses = UnusedDataAnalysis(x).findUnused(consumer)
              if (uses == AllUsed) {
                consumer
              } else {
                val TyStm(oldDataTyp, _) = x.typ
                val newDataTyp = transformTyp(oldDataTyp, uses)
                val newX = Param(x.prefix)(TyStm(newDataTyp, -1))
                val newProducerData = transformExpr(producer.data, uses)
                assert(newProducerData.typ == newDataTyp)
                val newProducer = StmBuild(
                  producer.n,
                  newProducerData,
                  producer.valid,
                  producer.equations
                )().tchk()
                val newEquations =
                  (consumer.equations - x).map({ case (x, (z, next)) =>
                    x -> (z, next.subAndEraseType(x -> newX).tchk())
                  }) + (newX -> (newProducer, ready))
                StmBuild(
                  consumer.n,
                  consumer.data.subAndEraseType(x -> newX).tchk(),
                  consumer.valid.subAndEraseType(x -> newX).tchk(),
                  newEquations
                )().tchk().asInstanceOf[StmBuild]
              }
            case (acc, _) => acc
          })
        // Recurse after handling this sbuild, not beforehand, so that
        // information about unused data propagates from sink back to source
        StmBuild(
          s2.n,
          s2.data,
          s2.valid,
          s2.equations.map({
            case (x, (s, ready)) if x.typ.isInstanceOf[TyStm] =>
              x -> (removeUnusedData(s), ready)
            case eqn => eqn
          })
        )().tchk().asInstanceOf[StmBuild]
      case e =>
        e.map(removeUnusedData)
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
            assert(exprElems == useElems)
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
