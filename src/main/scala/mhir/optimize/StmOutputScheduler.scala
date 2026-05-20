package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.optimize.cost.SimpleDelayCostModel
import mhir.canonicalize._
import mhir.typecheck.TypeCheck

/** Transformation to split up the output of a stream into at most two steps.
  */
case class StmOutputScheduler(
    binOpBalancer: BinOpTreeBalancingPass,
    delayCostModel: SimpleDelayCostModel
) {

  private val logger: Logger = Logger(getClass.getName)

  /** Splits an expression into at most two stages---one for the producer and
    * possibly one for the consumer.
    */
  def schedule(e: Expr): ComputationSchedule = {
    require(
      e.hasType,
      "expression must be type checked before stream output scheduling"
    )
    require(
      !e.hasSyntaxSugar,
      "expression must be lowered before stream output scheduling"
    )
    val e1 = this.binOpBalancer.balance(e)
    val e2 = doSchedule(Set(), Map())(e1)
    val e3 = inlineConstants(e2)
    val e4 = deduplicate(e3)
    e4
  }

  private def doSchedule(
      staticVars: Set[Param],
      varCosts: Map[Param, Long]
  )(e: Expr): ComputationSchedule = {
    e match {
      // Special cases
      case VecBuild(n, Function(i, e)) =>
        doSchedule(staticVars + i, varCosts)(e) match {
          case InProducer(e) =>
            InProducer(VecBuild(n, Function(i, e)())().tchk())
          case InConsumer(cData, pData) =>
            // If any expressions in the producer depend on the index `i`,
            // those expressions must be turned into vectors and uses of those
            // expressions must be turned into vector accesses
            val renamings = pData
              .filter({ case (_, e) => e.freeVars.contains(i) })
              .keySet
              .map({ x =>
                val y = Param("tmp")(TyVec(x.typ, n))
                x -> y
              })
              .toMap
            val cDataSubs = renamings
              .map({ case (x, y) => x -> VecAccess(y, i)().tchk() })
              .toMap[Expr, Expr]
            val newCData =
              VecBuild(n, Function(i, cData.subPreserveType(cDataSubs))())()
                .tchk()
            val newPData = pData.map({ case (x, e) =>
              renamings.get(x) match {
                case Some(y) => y -> VecBuild(n, Function(i, e)())().tchk()
                case None    => x -> e
              }
            })
            InConsumer(newCData, newPData)
        }
      case FunCall(Function(x, body), arg) =>
        doSchedule(staticVars, varCosts)(arg) match {
          case InProducer(arg) =>
            val argCost = delayCostModel.rawCost(arg, staticVars, varCosts)
            val bodyDecomp =
              doSchedule(staticVars, varCosts + (x -> argCost))(body)
            bodyDecomp match {
              case InProducer(body) =>
                InProducer(FunCall(Function(x, body)(), arg)().tchk())
              case InConsumer(cData, pData) =>
                val y = Param("tmp")(x.typ)
                val newCData = cData.subPreserveType(x -> y)
                val newPData = pData
                  // Need to duplicate arg on the producer side :/
                  .map({ case (z, e) => z -> e.subPreserveType(x -> arg) })
                  .+(y -> arg)
                InConsumer(newCData, newPData)
            }
          case InConsumer(ca, pa) =>
            InConsumer(FunCall(Function(x, body)(), ca)().tchk(), pa)
        }
      case _: FunCall => ???

      // Normal case
      case e @ (_: Param | _: StmData | _: BoolCst | _: IntCst | _: FixCst |
          _: Undefined | _: Tuple | _: TupleAccess | _: VecLiteral |
          _: VecAccess | _: Mux | _: Sum | _: Prod | _: Div | _: Mod |
          _: WrappingSum | _: WrappingDiff | _: WrappingProd | _: PadTo |
          _: TruncateTo | _: ToSigned | _: ToUnsigned | _: LLShift |
          _: LRShift | _: IntFixProd | _: Equal | _: LessThan | _: Not |
          _: And | _: Or) =>
        val cost = delayCostModel.rawCost(
          e,
          staticVars = staticVars,
          varCosts = varCosts
        )
        val stayInProducer = if (cost <= delayCostModel.FullCycleDelay) {
          true
        } else if (allChildrenAtomic(e, staticVars, varCosts)) {
          logger.warn(
            s"expression has too much delay, even with atomic children: $e"
          )
          true
        } else {
          false
        }
        if (stayInProducer) {
          InProducer(e)
        } else {
          val scheduledChildren =
            e.children.map(doSchedule(staticVars, varCosts))
          val (cData, pData) = scheduledChildren
            .map({
              case InProducer(x: Param) if varCosts.contains(x) =>
                (x, Map[Param, Expr]())
              case InProducer(e) =>
                val x = Param("tmp")(e.typ)
                (x, Map(x -> e))
              case InConsumer(cData, pData) =>
                (cData, pData)
            })
            .unzip
          InConsumer(e.rebuildAndEraseType(cData).tchk(), pData.flatten.toMap)
        }

      // Not allowed
      case e @ (_: Function | _: StmLiteral | _: StmBuild | _: LetStm) =>
        throw new IllegalArgumentException(
          s"Cannot schedule non-data expression $e"
        )
      case e: SyntaxSugar =>
        throw new IllegalArgumentException(s"Cannot schedule syntax sugar $e")
    }
  }

  private def allChildrenAtomic(
      e: Expr,
      staticVars: Set[Param],
      varCosts: Map[Param, Long]
  ): Boolean = {
    e.children.forall({ e =>
      delayCostModel.rawCost(e, staticVars, varCosts) == 0
    })
  }

  private def inlineConstants(
      stage: ComputationSchedule
  ): ComputationSchedule = {
    stage match {
      case s: InProducer => s
      case InConsumer(cData, pData) =>
        val subs = pData.filter({
          case (_, _: BoolCst) => true
          case (_, _: IntCst)  => true
          case (_, _: FixCst)  => true
          case _               => false
        })
        val newCData = cData.subPreserveType(subs.toMap[Expr, Expr])
        val newPData = pData.filter({ case (x, _) => !subs.contains(x) })
        InConsumer(newCData, newPData)
    }
  }

  private def deduplicate(stage: ComputationSchedule): ComputationSchedule = {
    stage match {
      case s: InProducer => s
      case InConsumer(cData, pData) =>
        val subs = pData.toSeq
          .groupBy({ case (_, e) => e })
          .map({ case (e, xs) => e -> xs.map(_._1) })
          .flatMap({ case (_, xs) =>
            val representative = xs.minBy(_.name)
            xs.filter(_ != representative).map(_ -> representative)
          })
        val newPData = pData.filter({ case (x, _) => !subs.contains(x) })
        val newCData = cData.subPreserveType(subs.toMap[Expr, Expr])
        InConsumer(newCData, newPData)
    }
  }
}
