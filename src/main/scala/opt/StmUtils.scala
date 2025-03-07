package opt

import ir._

object ElemStillInUseException
    extends IllegalArgumentException(
      "At least one of the provided accumulator elements are still in use!"
    )

object StmUtils {

  def appendAccumulator(stm: StmBuild, z: Expr, next: Function): StmBuild = {
    val p = Param("acc")
    StmBuild(
      stm.length,
      Tuple(stm.seed, z),
      (acc: Expr) =>
        Let(
          p,
          FunCall(stm.nextF, acc.__0),
          Tuple(Tuple(p.__0, FunCall(next, acc.__1)), p.__1)
        )
    )
  }

  /** @param stm
    *   A stream whose accumulator is a non-nested tuple.
    * @param indicesToRemove
    *   The indices to remove from the accumulator.
    * @return
    *   The stream with the given accumulator elements removed.
    */
  def removeAccumulatorElemsByIndex(
      stm: StmBuild,
      indicesToRemove: Seq[Int]
  ): StmBuild = {
    val seed = stm.seed.asInstanceOf[Tuple]
    // Need to adjust indices used to read accumulator.
    // For each element removed, you need to decrement the indices of all
    // following elements by one.
    val indexMap = seed.elems.indices
      .map(i =>
        i ->
          (if (indicesToRemove.contains(i)) None
           else Some(i - indicesToRemove.count(j => j < i)))
      )
      .toMap
    val acc = stm.nextF.param
    val invalid = Param("invalid")
    val subs: Map[Expr, Expr] = indexMap
      .map({ case (i, j) =>
        j match {
          case None    => TupleAccess(acc, i) -> invalid
          case Some(j) => TupleAccess(acc, i) -> TupleAccess(acc, j)
        }
      })
    val f = (e: Expr) =>
      rearrangeTuple(
        indexMap.flatMap({ case (oldIdx, opt) =>
          opt match {
            case None         => None
            case Some(newIdx) => Some(oldIdx -> newIdx)
          }
        })
      )(e)
    val s = StmBuild(
      stm.length,
      f(seed),
      Function(
        acc,
        transformHead(f)(stm.nextF.body).substitute(subs)
      )
    )
    // TODO: Will this catch tuple accesses where the index is non-static?
    if (s.contains(invalid)) {
      throw ElemStillInUseException
    }
    s
  }

  def replaceAccumulatorElemWithUnit(stm: StmBuild, i: Int): StmBuild = {
    val acc = stm.nextF.param
    val newSeed = Tuple(
      stm.seed.asInstanceOf[Tuple].elems.updated(i, Tuple()): _*
    )
    val newNextF =
      Function(acc, stm.nextF.body.substitute(TupleAccess(acc, i) -> Tuple()))

    // Check that the old element is no longer being referenced
    val invalid = Param("invalid")
    val testSeed = Tuple(
      stm.seed.asInstanceOf[Tuple].elems.updated(i, invalid): _*
    )
    val e = PartialEvalPass.partialEval(FunCall(newNextF, testSeed))
    if (e.contains(invalid)) {
      throw ElemStillInUseException
    } else {
      StmBuild(stm.length, newSeed, newNextF)
    }
  }

  /** Create a new tuple by taking elements from the given tuple in a specific
    * order.
    *
    * @param indexMap
    *   Map from old index to new index
    * @param e
    *   Expression to permute (must be a tuple)
    */
  def rearrangeTuple(indexMap: Map[Int, Int])(e: Expr): Expr = {
    val t = e.asInstanceOf[Tuple]
    val newElems = indexMap
      .map({ case (oldIdx, newIdx) => newIdx -> t.elems(oldIdx) })
      .toSeq
      .sortBy({ case (i, _) => i })
      .map({ case (_, e) => e })
    Tuple(newElems: _*)
  }

  /** Given an expression that must evaluate to a tuple, construct a new
    * expression by applying `f` to the element at index 0 and keeping the
    * remaining elements unchanged.
    */
  def transformHead(f: Expr => Expr)(e: Expr): Expr = {
    e match {
      case _: IntExpr | _: BoolExpr | _: Function | _: VecBuild | _: StmBuild |
          Tuple() =>
        throw new IllegalArgumentException(
          "Failed to transform due to an apparent type error."
        )
      case _: TupleAccess | _: VecAccess | _: Param | _: FunCall | _: StmNext =>
        ???
      case IfThenElse(cond, trueE, falseE) =>
        IfThenElse(cond, transformHead(f)(trueE), transformHead(f)(falseE))
      case Tuple(elems @ _*) =>
        Tuple(f(elems.head) +: elems.tail: _*)
      case DontCare => DontCare
    }
  }
}
