package opt

import ir.*

object StmUtils {

  /** @param stm
    *   A stream whose accumulator is a non-nested tuple.
    * @param indicesToRemove
    *   The indices to remove from the accumulator.
    * @param replace
    *   A function which, given an index from the list of indices to remove,
    *   provides an expression with which to replace the element at that index
    *   if it occurs in the body of the stream.
    * @return
    *   The stream with the given accumulator elements removed.
    */
  def removeAccumulatorElemsByIndex(
      stm: StmBuild,
      indicesToRemove: Seq[Int],
      replace: Int => Expr
  ): StmBuild = {
    val seed = stm.seed.asInstanceOf[Tuple]
    // Need to adjust indices used to read accumulator.
    // For each element removed, you need to decrement the indices of all
    // following elements by one.
    val indexMap = seed.elems.indices
      .map(i =>
        i ->
          (if indicesToRemove.contains(i) then None
           else Some(i - indicesToRemove.count(j => j < i)))
      )
      .toMap
    val acc = stm.nextF.param
    val subs: Map[Expr, Expr] = indexMap
      .map((i, j) =>
        j match {
          case None    => TupleAccess(acc, i) -> replace(i)
          case Some(j) => TupleAccess(acc, i) -> TupleAccess(acc, j)
        }
      )
    val f = rearrangeTuple(
      indexMap.flatMap((oldIdx, opt) =>
        opt match {
          case None         => None
          case Some(newIdx) => Some(oldIdx -> newIdx)
        }
      )
    )
    StmBuild(
      stm.length,
      f(seed),
      Function(
        acc,
        PartialEvalPass.substitute(transformHead(f)(stm.nextF.body))(subs)
      )
    )
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
      .map((oldIdx, newIdx) => newIdx -> t.elems(oldIdx))
      .toSeq
      .sortBy((i, _) => i)
      .map((_, e) => e)
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
      case Tuple(elems: _*) =>
        Tuple(f(elems.head) +: elems.tail: _*)
      case DontCare => DontCare
    }
  }
}
