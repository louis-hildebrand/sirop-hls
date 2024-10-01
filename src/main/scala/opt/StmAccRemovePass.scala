package opt

import ir.*

/** Recurrence equation of the form
  *
  *   - Either:
  *     - i(t + 1) = if b(t) then i(t) + delta else i(t)
  *     - i(t + 1) = i(t) + delta
  *   - b(0) = true
  *   - b(t + 1) = b(t) && (i(t) < k)
  *
  * `j` is the index of the counter variable within the accumulator tuple.
  */
case class MonotonicBoolRecurrenceEquation(
    k: Int,
    i0: Int,
    delta: Int,
    j: Int
)

object StmAccRemovePass {
  def removeUnnecessaryAccumulators(stm: StmBuild): StmBuild = {
    val s1 = StmCanonPass.canonicalize(stm)
    removeMonotonicBools(s1)
  }

  private def removeMonotonicBools(stm: StmBuild): StmBuild = {
    // TODO: generalize this pass a bit? e.g., support bounds that are not
    //       integer constants, multiple counters, etc.
    val seed = stm.seed.asInstanceOf[Tuple]
    val acc = stm.nextF.param
    val replacements: Map[Int, Expr] =
      seed.elems.indices
        .flatMap(i =>
          getMonotonicBoolEquation(stm, i) match {
            case Some(eq) =>
              // We can replace the boolean with TupleAccess(acc, j) < T
              val max = Math.max
              val ceildiv = (a: Int, b: Int) => Math.ceil(a.toDouble / b).toInt
              val t =
                (ceildiv(max(0, eq.k - eq.i0), eq.delta) + 1) * eq.delta + eq.i0
              Some(i -> LessThan(TupleAccess(acc, IntCst(eq.j)), t))
            case None => None
          }
        )
        .toMap
    val subs: Map[Expr, Expr] =
      replacements.map((i, e) => TupleAccess(acc, i) -> e)
    val s = StmBuild(
      stm.length,
      stm.seed,
      Function(acc, PartialEvalPass.substitute(stm.nextF.body)(subs))
    )
    val indicesToRemove = replacements.keySet.toSeq
    StmUtils.removeAccumulatorElemsByIndex(s, indicesToRemove)
  }

  private def getMonotonicBoolEquation(
      stm: StmBuild,
      i: Int
  ): Option[MonotonicBoolRecurrenceEquation] = {
    val seed = stm.seed.asInstanceOf[Tuple]
    val init = seed.elems(i)
    val acc = stm.nextF.param
    init match {
      case True =>
        val boolUpdateExpr =
          PartialEvalPass.partialEval(TupleAccess(stm.nextF.body.__0, i))
        boolUpdateExpr match {
          case And(
                TupleAccess(p0, i0),
                LessThan(TupleAccess(p1, IntCst(j)), IntCst(k))
              ) if p0 == acc && p1 == acc && i0 == IntCst(i) =>
            seed.elems(j) match {
              case IntCst(cntrInit) =>
                val cntrUpdateExpr =
                  PartialEvalPass.partialEval(
                    TupleAccess(stm.nextF.body.__0, j)
                  )
                cntrUpdateExpr match {
                  case IfThenElse(
                        TupleAccess(p0, i0),
                        Add(TupleAccess(p1, i1), IntCst(delta)),
                        TupleAccess(p2, i2)
                      )
                      if p0 == acc && i0 == IntCst(
                        i
                      ) && p1 == acc && i1 == IntCst(
                        j
                      ) && p2 == acc && i2 == IntCst(j) =>
                    val eq = MonotonicBoolRecurrenceEquation(
                      k = k,
                      i0 = cntrInit,
                      delta = delta,
                      j = j
                    )
                    Some(eq)
                  case Add(TupleAccess(p0, i0), IntCst(delta))
                      if p0 == acc && i0 == IntCst(j) =>
                    val eq = MonotonicBoolRecurrenceEquation(
                      k = k,
                      i0 = cntrInit,
                      delta = delta,
                      j = j
                    )
                    Some(eq)
                  case _ => None
                }
              case _ => None
            }
          case _ => None
        }
      case False =>
        // TODO
        None
      case _ => None
    }
  }
}
