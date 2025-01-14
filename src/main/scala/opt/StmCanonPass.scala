package opt

import scala.annotation.tailrec
import ir.*

object StmCanonPass {
  def canonicalIdentityStream(n: Expr, p: Param): StmBuild = {
    StmCanonPass.canonicalize(
      StmBuild(
        n,
        p,
        (acc: Expr) => Tuple(StmNext(acc).__0, StmNext(acc).__1, True)
      )
    )
  }

  /** Converts a stream to canonical form. This means:
    *
    *   - The stream accumulator is a non-nested tuple.
    *   - Accumulator elements that are constant are inlined. In particular,
    *     none of the accumulator elements are empty tuples (although the
    *     accumulator as a whole may be empty).
    *   - In the stream body, all `Tuple(...)` expressions are as close as
    *     possible to the leaves and `IfThenElse(...)` expressions are as close
    *     as possible to the root.
    *
    * @param stm
    *   The stream to canonicalize.
    * @return
    *   The stream in canonical form.
    */
  def canonicalize(stm: StmBuild): StmBuild = {
    val s = PartialEvalPass.partialEval(stm).asInstanceOf[StmBuild]
    val s0 = tupleAccumulator(s)
    // TODO: would it be better to move IfThenElse *inside* tuples?
    val s1 = moveIfThenElseOutsideTupleInStmBody(s0)
    val s2 = flattenAccumulator(s1)
    // TODO: move accumulator removal to a separate object StmAccRemovePass?
    //       Unfortunately this causes some stream tests (namely,
    //       StmFold:3D:Sum and StmFold:3D:Product) to seemingly get stuck in
    //       an infinite loop when moving IfThenElse outside Tuple.
    val s3 = removeConstantAccumulatorElems(s2)
    val s4 = moveStreamsToFront(s3)
    // Canonicalization may have revealed new partial evaluation opportunities
    PartialEvalPass.partialEval(s4).asInstanceOf[StmBuild]
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Canonicalization steps
  // -------------------------------------------------------------------------------------------------------------------

  /** Wrap the accumulator in a tuple and update `nextF` accordingly.
    *
    * This is useful to ensure the accumulator is always a tuple (and not, for
    * example, a scalar).
    */
  private def tupleAccumulator(stm: StmBuild): StmBuild = {
    val acc = stm.nextF.param
    val sub = (body: Expr) =>
      PartialEvalPass.substitute(body)(Map(acc -> TupleAccess(acc, 0)))
    val tupleHead = StmUtils.transformHead((e: Expr) => Tuple(e))
    StmBuild(
      stm.length,
      Tuple(stm.seed),
      Function(acc, sub(tupleHead(stm.nextF.body)))
    )
  }

  /** Move `IfThenElse` expressions outside of `Tuple` expressions in the body
    * of the given stream.
    *
    * @param stm
    *   The stream to which to apply the transformation.
    * @return
    *   The new stream.
    */
  private def moveIfThenElseOutsideTupleInStmBody(stm: StmBuild): StmBuild = {
    StmBuild(
      stm.length,
      stm.seed,
      Function(stm.nextF.param, moveIfThenElseOutsideTuple(stm.nextF.body))
    )
  }

  /** Flatten the accumulator. For example, if the seed was ((0, 1), 2), the new
    * seed will be (0, 1, 2) and `nextF` will be updated accordingly.
    */
  private def flattenAccumulator(stm: StmBuild): StmBuild = {
    val p = Param()
    val (tupleAccessMap, _) =
      makeTupleAccessMap(stm.seed, stm.nextF.param, p, Seq(), 0)
    val flattenHead = StmUtils.transformHead(e => flatten(e))
    StmBuild(
      stm.length,
      flatten(stm.seed),
      Function(
        p,
        PartialEvalPass.substitute(flattenHead(stm.nextF.body))(tupleAccessMap)
      )
    )
  }

  private def removeConstantAccumulatorElems(stm: StmBuild): StmBuild = {
    val indicesToRemove = findConstantAccumulatorElems(
      stm,
      // Only remove accumulator elements for which we can definitely perform
      // constant propagation. Replacing all uses with the initial value
      // probably wouldn't work if the element is a stream, for example.
      indices = stm.seed
        .asInstanceOf[Tuple]
        .elems
        .zipWithIndex
        .filter((e, i) =>
          e match {
            case _: IntCst | True | False => true
            case Tuple()                  => true
            case _                        => false
          }
        )
        .map({ case (e, i) => i })
        .toSet
    )
    val acc = stm.nextF.param
    val seed = stm.seed.asInstanceOf[Tuple]
    val subs: Map[Expr, Expr] =
      indicesToRemove.map(i => TupleAccess(acc, i) -> seed.elems(i)).toMap
    StmUtils.removeAccumulatorElemsByIndex(
      PartialEvalPass.substitute(stm)(subs).asInstanceOf[StmBuild],
      indicesToRemove.toSeq
    )
  }

  /** Permute the elements in the accumulator so that the input streams, if any,
    * all occupy the places with the lowest indices.
    */
  private def moveStreamsToFront(stm: StmBuild): StmBuild = {
    val seed = stm.seed.asInstanceOf[Tuple]
    val acc = stm.nextF.param
    val indexMap = seed.elems.zipWithIndex
      .sortBy((e, i) => (!e.isInstanceOf[StmBuild], !e.isInstanceOf[Param]))
      .zipWithIndex
      .map({ case ((_, oldIdx), newIdx) => oldIdx -> newIdx })
      .toMap
    val sub = (body: Expr) =>
      PartialEvalPass.substitute(body)(
        indexMap.map({ case (oldIdx, newIdx) =>
          TupleAccess(acc, oldIdx) -> TupleAccess(acc, newIdx)
        })
      )
    StmBuild(
      stm.length,
      StmUtils.rearrangeTuple(indexMap)(stm.seed),
      Function(
        acc,
        sub(
          StmUtils.transformHead(StmUtils.rearrangeTuple(indexMap))(
            stm.nextF.body
          )
        )
      )
    )
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------------------------------------------------

  /** Move `IfThenElse` expressions outside of `Tuple` expressions.
    *
    * For example, `Tuple(IfThenElse(cond, 1, 2), 3, 4)` will be replaced by
    * `IfThenElse(cond, Tuple(1, 3, 4), Tuple(2, 3, 4))`.
    */
  private def moveIfThenElseOutsideTuple(e: Expr): Expr = {
    e match {
      case Tuple(elems: _*) =>
        val newElems = elems.map(e => moveIfThenElseOutsideTuple(e))
        rewriteIfThenElseInTuple(Tuple(newElems: _*))
      case IfThenElse(cond, trueE, falseE) =>
        IfThenElse(
          cond,
          moveIfThenElseOutsideTuple(trueE),
          moveIfThenElseOutsideTuple(falseE)
        )
      case DontCare => DontCare
      case _: BoolExpr | _: IntExpr | _: Function | _: StmBuild | _: VecBuild =>
        // Definitely will not evaluate to a tuple
        e
      case _: TupleAccess | _: VecAccess | _: StmNext | _: FunCall | _: Param =>
        // TODO: not sure what to do here.
        e
    }
  }

  private def rewriteIfThenElseInTuple(t: Tuple): Expr = {
    val i = t.elems.indexWhere(e => e.isInstanceOf[IfThenElse])
    if (i < 0) {
      t
    } else {
      val ite = t.elems(i).asInstanceOf[IfThenElse]
      IfThenElse(
        ite.cond,
        rewriteIfThenElseInTuple(Tuple(t.elems.updated(i, ite.trueE): _*)),
        rewriteIfThenElseInTuple(Tuple(t.elems.updated(i, ite.falseE): _*))
      )
    }
  }

  /** @param stm
    *   A stream whose accumulator is a non-nested tuple.
    * @return
    *   The indices within the accumulator tuple of the constant-valued
    *   elements.
    */
  @tailrec
  private def findConstantAccumulatorElems(
      stm: StmBuild,
      indices: Set[Int]
  ): Set[Int] = {
    if (indices.isEmpty) {
      Set()
    } else {
      val seed = stm.seed.asInstanceOf[Tuple]
      val z = Tuple(
        seed.elems.zipWithIndex
          .map({ case (e, i) => if (indices.contains(i)) e else Param() }): _*
      )
      val acc =
        PartialEvalPass.partialEval(TupleAccess(FunCall(stm.nextF, z), 0))
      val constantIndices = indices.filter(i =>
        PartialEvalPass.partialEval(TupleAccess(acc, i)) == seed.elems(i)
      )
      if (constantIndices == indices) {
        indices
      } else {
        findConstantAccumulatorElems(stm, constantIndices)
      }
    }
  }

  /** Traverse the tree of tuples and construct a map from old tuple accesses
    * (e.g., acc.__0.__1) to tuple accesses in a flattened version of the tree
    * (e.g., acc.__1). This assumes the flattening happens via a pre-order
    * traversal.
    *
    * @param e
    *   Expression to traverse
    * @param oldAcc
    *   Parameter that was being used to refer to the original tuple.
    * @param newAcc
    *   Parameter that will be used to refer to the new tuple.
    * @param path
    *   Sequence of tuple accesses needed to get to the current expression
    * @param nextIdx
    *   Next index in the flattened expression
    */
  private def makeTupleAccessMap(
      e: Expr,
      oldAcc: Param,
      newAcc: Param,
      path: Seq[Int],
      nextIdx: Int
  ): (Map[Expr, Expr], Int) = {
    e match {
      case Tuple(elems: _*) if !elems.isEmpty =>
        // Inner node
        /* The programmer could refer directly to one of these inner nodes,
         * right? Maybe assume the expression is already rewritten in such a
         * way that all tuple expressions are expanded (e.g.,
         * Tuple(acc.__0, acc.__1) instead of just acc). Doing this requires
         * type-checking to know the arity of tuple-typed parameters.
         */
        var ni = nextIdx
        var m = Map[Expr, Expr]()
        for ((elem, idx) <- elems.zipWithIndex) {
          val (m_, ni_) =
            makeTupleAccessMap(elem, oldAcc, newAcc, path :+ idx, ni)
          ni = ni_
          m = m ++ m_
        }
        (m, ni)
      case _ =>
        // Leaf node
        val originalAccess =
          path.foldLeft(oldAcc: Expr)((e, i) => TupleAccess(e, i))
        val newAccess = TupleAccess(newAcc, nextIdx)
        (Map(originalAccess -> newAccess), nextIdx + 1)
    }
  }

  /** Flatten the given tree of tuples via a pre-order traversal.
    */
  private def flatten(e: Expr): Expr = {
    e match {
      case Tuple(elems: _*) if !elems.isEmpty =>
        val flatElems = elems.map(e => flatten(e))
        val combinedElems = flatElems.flatMap(e =>
          e match {
            case Tuple(elems: _*) if !elems.isEmpty => elems
            case _                                  => Seq(e)
          }
        )
        Tuple(combinedElems: _*)
      case _ => e
    }
  }

  /** Expand an expression that evaluates to a tuple. For example, `acc.__0` may
    * be expanded to `Tuple(acc.__0.__0, acc.__0.__1)`.
    *
    * @param e
    *   Expression to expand
    * @param n
    *   Number of elements in the expanded tuple
    */
  private def tupleExpand(e: Expr, n: Int): Expr = {
    Tuple((0 until n).map(i => TupleAccess(e, i)): _*)
  }
}
