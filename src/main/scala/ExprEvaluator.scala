import scala.annotation.tailrec

object ExprEvaluator {

  def contains(e1: Expr, e2: Expr): Boolean = {
    e1 match {
      case _ if e1 == e2                       => true
      case True | False | _: IntCst | _: Param => false
      case Add(x, y)      => contains(x, e2) || contains(y, e2)
      case Sub(x, y)      => contains(x, e2) || contains(y, e2)
      case Mul(x, y)      => contains(x, e2) || contains(y, e2)
      case Div(x, y)      => contains(x, e2) || contains(y, e2)
      case Mod(x, y)      => contains(x, e2) || contains(y, e2)
      case Equal(x, y)    => contains(x, e2) || contains(y, e2)
      case NotEqual(x, y) => contains(x, e2) || contains(y, e2)
      case LessThan(x, y) => contains(x, e2) || contains(y, e2)
      case And(x, y)      => contains(x, e2) || contains(y, e2)
      case Or(x, y)       => contains(x, e2) || contains(y, e2)
      case Not(x)         => contains(x, e2)
      case IfThenElse(c, t, f) =>
        contains(c, e2) || contains(t, e2) || contains(f, e2)
      case Function(p, b)    => contains(p, e2) || contains(b, e2)
      case FunCall(f, a)     => contains(f, e2) || contains(a, e2)
      case Tuple(elems: _*)  => elems.exists(e => contains(e, e2))
      case TupleAccess(t, i) => contains(t, e2) || contains(i, e2)
      case VecBuild(n, f)    => contains(n, e2) || contains(f, e2)
      case VecAccess(v, i)   => contains(v, e2) || contains(i, e2)
      case VecLength(v)      => contains(v, e2)
      case StmBuild(n, z, f) =>
        contains(n, e2) || contains(z, e2) || contains(f, e2)
      case StmNext(s)   => contains(s, e2)
      case StmLength(s) => contains(s, e2)
    }
  }

  def substitute(
      e: Expr
  )(implicit substitutions: Map[Expr, Expr]): Expr = {
    substitutions.get(e) match {
      case Some(v) => v
      case None =>
        e match {
          case t: Tuple => Tuple(t.elems.toSeq.map(substitute(_)): _*)
          case TupleAccess(t: Expr, i: Expr) =>
            TupleAccess(substitute(t), substitute(i))

          case p: Param => p
          case f: Function => {
            val newParam = Param()
            Function(
              newParam,
              substitute(f.body)(substitutions + ((f.param, newParam)))
            )
            // when substituting the body, this might be come a new function if anything is susbstituted, therefore, we create a new Param
          }
          case FunCall(f: Expr, arg: Expr) =>
            FunCall(substitute(f), substitute(arg))

          case Add(e1: Expr, e2: Expr) => Add(substitute(e1), substitute(e2))
          case Sub(e1: Expr, e2: Expr) => Sub(substitute(e1), substitute(e2))
          case Mul(e1: Expr, e2: Expr) => Mul(substitute(e1), substitute(e2))
          case Div(e1: Expr, e2: Expr) => Div(substitute(e1), substitute(e2))
          case Mod(e1: Expr, e2: Expr) => Mod(substitute(e1), substitute(e2))
          case IntCst(_)               => e

          case True  => True
          case False => False
          case IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) =>
            IfThenElse(substitute(cond), substitute(trueE), substitute(falseE))
          case NotEqual(e1: Expr, e2: Expr) =>
            NotEqual(substitute(e1), substitute(e2))
          case Equal(e1: Expr, e2: Expr) =>
            Equal(substitute(e1), substitute(e2))
          case LessThan(e1: Expr, e2: Expr) =>
            LessThan(substitute(e1), substitute(e2))
          case And(e1: Expr, e2: Expr) => And(substitute(e1), substitute(e2))
          case Or(e1: Expr, e2: Expr)  => Or(substitute(e1), substitute(e2))
          case Not(e: Expr)            => Not(substitute(e))

          case StmBuild(lengths, seed, f) =>
            StmBuild(
              substitute(lengths),
              substitute(seed),
              substitute(f).asInstanceOf[Function]
            )
          case StmLength(s)     => StmLength(substitute(s))
          case StmNext(e: Expr) => StmNext(substitute(e))

          case VecBuild(len: Expr, f: Function) =>
            VecBuild(substitute(len), substitute(f).asInstanceOf[Function])
          case VecAccess(vec: Expr, i: Expr) =>
            VecAccess(substitute(vec), substitute(i))
          case VecLength(vec: Expr) => VecLength(substitute(vec))
        }
    }
  }

  def partialEval(e: Expr): Expr = {
    e match {

      case t: Tuple => Tuple(t.elems.toSeq.map(partialEval(_)): _*)
      case TupleAccess(t: Expr, i: Expr) =>
        (partialEval(t), partialEval(i)) match {
          case (tuple: Tuple, index: IntCst) =>
            partialEval(tuple.elems(index.i))
          case (IfThenElse(c, t, f), i) =>
            // Move TupleAccess inside IfThenElse in the hope that it'll
            // encounter a Tuple(...)
            partialEval(IfThenElse(c, TupleAccess(t, i), TupleAccess(f, i)))
          case (tuple @ _, index @ _) => TupleAccess(tuple, index)
        }

      case p: Param          => p
      case Function(p, body) => Function(p, partialEval(body))
      case FunCall(f: Expr, arg: Expr) =>
        partialEval(f) match {
          case fun: Function =>
            partialEval(
              substitute(fun.body)(Map(fun.param -> partialEval(arg)))
            )
          case fun @ _ => FunCall(fun, partialEval(arg))
        }

      case Add(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)       => e1.i + e2.i
          case (e, IntCst(0))                 => e
          case (Add(e, IntCst(a)), IntCst(b)) => partialEval(e + (a + b))
          case (Add(IntCst(a), e), IntCst(b)) => partialEval(e + (a + b))
          case (IntCst(b), Add(e, IntCst(a))) => partialEval(e + (a + b))
          case (IntCst(b), Add(IntCst(a), e)) => partialEval(e + (a + b))
          case (Sub(e, IntCst(a)), IntCst(b)) => partialEval(e + (b - a))
          case (Sub(IntCst(a), e), IntCst(b)) => partialEval(IntCst(a + b) - e)
          case (IntCst(b), Sub(e, IntCst(a))) => partialEval(e + (b - a))
          case (IntCst(b), Sub(IntCst(a), e)) => partialEval(IntCst(a + b) - e)
          case (e1 @ _, e2 @ _)               => Add(e1, e2)
        }
      case Sub(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)       => e1.i - e2.i
          case (x, y) if x == y               => 0
          case (e, IntCst(0))                 => e
          case (Add(e, IntCst(a)), IntCst(b)) => partialEval(e + (a - b))
          case (Add(IntCst(a), e), IntCst(b)) => partialEval(e + (a - b))
          case (IntCst(b), Add(e, IntCst(a))) => partialEval(IntCst(b - a) - e)
          case (IntCst(b), Add(IntCst(a), e)) => partialEval(IntCst(b - a) - e)
          case (Sub(e, IntCst(a)), IntCst(b)) => partialEval(e - (a + b))
          case (Sub(IntCst(a), e), IntCst(b)) => partialEval(IntCst(a - b) - e)
          case (IntCst(b), Sub(e, IntCst(a))) => partialEval(IntCst(a + b) - e)
          case (IntCst(b), Sub(IntCst(a), e)) => partialEval(e + (b - a))
          case (Sub(x, y), z) if x == z       => partialEval(IntCst(0) - y)
          case (x, Sub(y, z)) if x == y       => z
          case (e1 @ _, e2 @ _)               => Sub(e1, e2)
        }
      case Mul(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i * e2.i
          case (e1 @ _, e2 @ _)         => Mul(e1, e2)
        }
      case Div(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i / e2.i
          case (e1 @ _, e2 @ _)         => Div(e1, e2)
        }
      case Mod(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i % e2.i
          case (e1 @ _, e2 @ _)         => Mod(e1, e2)
        }
      case IntCst(_) => e

      case True  => True
      case False => False
      case IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) =>
        partialEval(cond) match {
          case True  => partialEval(trueE)
          case False => partialEval(falseE)
          case cond  =>
            // If (x0 && ... && xn) = True, then xi = True for each i
            val t = partialEval(
              substitute(trueE)(splitAnd(cond).map(e => e -> True).toMap)
            )
            // If (x0 || ... || xn) = False, then xi = False for each i
            val f = partialEval(
              substitute(falseE)(splitOr(cond).map(e => e -> False).toMap)
            )
            if t == f then t else IfThenElse(cond, t, f)
        }
      case NotEqual(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i != e2.i
          case (e1 @ _, e2 @ _)         => NotEqual(e1, e2)
        }
      case Equal(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i == e2.i
          case (e1 @ _, e2 @ _)         => Equal(e1, e2)
        }
      case LessThan(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i < e2.i
          case (e1 @ _, e2 @ _)         => LessThan(e1, e2)
        }
      case And(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (False, _) => False
          case (_, False) => False
          case (True, e)  => e
          case (e, True)  => e
          case (e1, e2)   => And(e1, e2)
        }
      case Or(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (True, _) | (_, True) => True
          case (False, e)            => e
          case (e, False)            => e
          case (e1, e2)              => Or(e1, e2)
        }
      case Not(e: Expr) =>
        partialEval(e) match {
          case True   => False
          case False  => True
          case Not(e) => e
          case e      => Not(e)
        }

      case StmBuild(length, seed, f) =>
        StmBuild(
          partialEval(length),
          partialEval(seed),
          // ensures any free Param in f gets substituted
          partialEval(f).asInstanceOf[Function]
        )

      case StmLength(s) =>
        partialEval(s) match {
          case s: StmBuild => partialEval(s.length)
          case s @ _       => StmLength(s)
        }

      case StmNext(s: Expr) =>
        partialEval(s) match {
          case s: StmBuild =>
            s.length match {
              case IntCst(len) =>
                assert(len > 0, "Attempt to call StmNext() on an empty stream.")
                partialEval(FunCall(s.nextF, s.seed)) match {
                  case next: Tuple =>
                    val n = next.elems.length
                    require(
                      n == 3,
                      s"The function in StmBuild returned a ${n}-tuple instead of a 3-tuple."
                    )
                    partialEval(next.__2) match {
                      case True =>
                        // return the new stream and the next element
                        Tuple(
                          StmBuild(
                            len - 1,
                            partialEval(next.__0),
                            // this function may have free parameters
                            partialEval(s.nextF).asInstanceOf[Function]
                          ),
                          partialEval(next.__1)
                        )
                      case False =>
                        // skip this element, look for the next one
                        partialEval(
                          StmNext(
                            StmBuild(
                              s.length,
                              partialEval(next.__0),
                              // this function may have free parameters
                              partialEval(s.nextF).asInstanceOf[Function]
                            )
                          )
                        )
                      case _ =>
                        StmNext(s)
                    }
                  case _ => StmNext(s)
                }
              case _ => StmNext(s)
            }
          case s => StmNext(s)
        }

      case VecBuild(len: Expr, f: Function) =>
        VecBuild(
          partialEval(len),
          partialEval(f).asInstanceOf[
            Function
          ] /* ensures any free Param in f gets substituted */
        )
      case VecAccess(vec: Expr, i: Expr) =>
        partialEval(vec) match {
          case vec: VecBuild => partialEval(FunCall(vec.f, partialEval(i)))
          case vec @ _       => VecAccess(vec, partialEval(i))
        }
      case VecLength(vec: Expr) =>
        partialEval(vec) match {
          case vec: VecBuild => partialEval(vec.len)
          case vec @ _       => VecLength(vec)
        }
    }
  }

  private def splitAnd(e: Expr): Seq[Expr] = {
    // TODO: Convert to POS form first?
    e match {
      case And(x, y) => splitAnd(x) ++ splitAnd(y)
      case e         => Seq(e)
    }
  }

  private def splitOr(e: Expr): Seq[Expr] = {
    // TODO: Convert to SOP form first?
    e match {
      case Or(x, y) => splitOr(x) ++ splitOr(y)
      case e        => Seq(e)
    }
  }

  def canonicalize(stm: StmBuild): StmBuild = {
    val s = partialEval(stm).asInstanceOf[StmBuild]
    val s0 = tupleAccumulator(s)
    // TODO: would it be better to move IfThenElse *inside* tuples?
    val s1 = moveIfThenElseOutsideTupleInStmBody(s0)
    val s2 = flattenAccumulator(s1)
    val s3 = removeConstantAccumulatorElems(s2)
    moveStreamsToFront(s3)
  }

  /** Wrap the accumulator in a tuple and update `nextF` accordingly.
    *
    * This is useful to ensure the accumulator is always a tuple (and not, for
    * example, a scalar).
    */
  private def tupleAccumulator(stm: StmBuild): StmBuild = {
    val acc = stm.nextF.param
    val sub = (body: Expr) => substitute(body)(Map(acc -> TupleAccess(acc, 0)))
    val tupleHead = transformHead((e: Expr) => Tuple(e))
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
    if i < 0 then {
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

  /** Flatten the accumulator. For example, if the seed was ((0, 1), 2), the new
    * seed will be (0, 1, 2) and `nextF` will be updated accordingly.
    */
  private def flattenAccumulator(stm: StmBuild): StmBuild = {
    val p = Param()
    val (tupleAccessMap, _) =
      makeTupleAccessMap(stm.seed, stm.nextF.param, p, Seq(), 0)
    val flattenHead = transformHead(e => flatten(e))
    StmBuild(
      stm.length,
      flatten(stm.seed),
      Function(p, substitute(flattenHead(stm.nextF.body))(tupleAccessMap))
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
        .map((e, i) => i)
        .toSet
    )
    removeAccumulatorElemsByIndex(
      stm,
      indicesToRemove.toSeq,
      replace = i => stm.seed.asInstanceOf[Tuple].elems(i)
    )
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
    if indices.isEmpty then {
      Set()
    } else {
      val seed = stm.seed.asInstanceOf[Tuple]
      val z = Tuple(
        seed.elems.zipWithIndex
          .map((e, i) => if indices.contains(i) then e else Param()): _*
      )
      val acc = ExprEvaluator.partialEval(TupleAccess(FunCall(stm.nextF, z), 0))
      val constantIndices = indices.filter(i =>
        ExprEvaluator.partialEval(TupleAccess(acc, i)) == seed.elems(i)
      )
      if constantIndices == indices then {
        indices
      } else {
        findConstantAccumulatorElems(stm, constantIndices)
      }
    }
  }

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
  private def removeAccumulatorElemsByIndex(
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
      Function(acc, substitute(transformHead(f)(stm.nextF.body))(subs))
    )
  }

  /** Permute the elements in the accumulator so that the input streams, if any,
    * all occupy the places with the lowest indices.
    */
  private def moveStreamsToFront(stm: StmBuild): StmBuild = {
    val seed = stm.seed.asInstanceOf[Tuple]
    val acc = stm.nextF.param
    val indexMap = seed.elems.zipWithIndex
      .sortBy((e, i) => !e.isInstanceOf[StmBuild])
      .zipWithIndex
      .map({ case ((_, oldIdx), newIdx) => oldIdx -> newIdx })
      .toMap
    val sub = (body: Expr) =>
      substitute(body)(
        indexMap.map((oldIdx, newIdx) =>
          TupleAccess(acc, oldIdx) -> TupleAccess(acc, newIdx)
        )
      )
    StmBuild(
      stm.length,
      rearrangeTuple(indexMap)(stm.seed),
      Function(
        acc,
        sub(transformHead(rearrangeTuple(indexMap))(stm.nextF.body))
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
  private def rearrangeTuple(indexMap: Map[Int, Int])(e: Expr): Expr = {
    val t = e.asInstanceOf[Tuple]
    val newElems = indexMap
      .map((oldIdx, newIdx) => newIdx -> t.elems(oldIdx))
      .toSeq
      .sortBy((i, _) => i)
      .map((_, e) => e)
    Tuple(newElems: _*)
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

  /** Given an expression that must evaluate to a tuple, construct a new
    * expression by applying `f` to the element at index 0 and keeping the
    * remaining elements unchanged.
    */
  private def transformHead(f: Expr => Expr)(e: Expr): Expr = {
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

  /** Fuse a `StmBuild` with its stream inputs until it has no more stream
    * inputs.
    */
  @tailrec
  def fuseCompletely(stm: Expr /* Stm<A; n> */ ): Expr /* Stm<A; n> */ = {
    val s = canonicalize(partialEval(stm).asInstanceOf[StmBuild])
    s.seed.asInstanceOf[Tuple].elems.head match {
      case _: StmBuild => fuseCompletely(fuse(s))
      case _           => s
    }
  }

  /** Fuse a `StmBuild` with its first stream input.
    */
  def fuse(stm: Expr /* Stm<A; n> */ ): Expr /* Stm<A; n> */ = {
    val s = canonicalize(partialEval(stm).asInstanceOf[StmBuild])
    val outerSeed = s.seed.asInstanceOf[Tuple]
    val inputStm = outerSeed.elems.head match {
      case s: StmBuild => canonicalize(s)
      case _ =>
        throw new IllegalArgumentException(
          "No input streams found to fuse with."
        )
    }
    StmBuild(
      s.length,
      // Replace the inner stream with an empty tuple in the new seed.
      // This minimizes the need for updating the indices in tuple access
      // expressions.
      Tuple(Tuple(Tuple() +: outerSeed.elems.tail: _*), inputStm.seed), {
        val acc = Param()
        Function(
          acc,
          fuseFunctionBodies(
            newAcc = acc,
            oldAcc = s.nextF.param,
            outerBody = s.nextF.body,
            innerNextF = inputStm.nextF,
            innerStmAccArity = inputStm.seed.asInstanceOf[Tuple].elems.length,
            outerStmAccArity = s.seed.asInstanceOf[Tuple].elems.length
          )
        )
      }
    )
  }

  private def fuseFunctionBodies(
      newAcc: Param,
      oldAcc: Param,
      outerBody: Expr,
      innerNextF: Function,
      innerStmAccArity: Int,
      outerStmAccArity: Int
  ): Expr = {
    val e = outerBody match {
      case IfThenElse(cond, trueE, falseE) =>
        IfThenElse(
          // TODO: What if the condition somehow uses `acc.__0`?
          cond,
          fuseFunctionBodies(
            newAcc,
            oldAcc,
            trueE,
            innerNextF,
            innerStmAccArity,
            outerStmAccArity
          ),
          fuseFunctionBodies(
            newAcc,
            oldAcc,
            falseE,
            innerNextF,
            innerStmAccArity,
            outerStmAccArity
          )
        )
      case _: TupleAccess | _: VecAccess | _: StmNext | _: FunCall | _: Param =>
        ???
      case Tuple(a, e, valid) =>
        a match {
          case Tuple(
                TupleAccess(StmNext(TupleAccess(p, IntCst(0))), IntCst(0)),
                as: _*
              ) if p == oldAcc =>
            // CASE 1: StmNext() called.
            //         Update the inner accumulator.
            val innerNext = Param()
            // HACK: Expand all tuples manually.
            // Ideally this would be a canonicalization pass, but I think
            // figuring out the arity of a tuple-valued param requires input
            // from the type system.
            val innerNext0 = tupleExpand(innerNext.__0, innerStmAccArity)
            val newAcc0 = tupleExpand(newAcc.__0, outerStmAccArity)
            val subInnerNextData =
              Map[Expr, Expr](StmNext(oldAcc.__0).__1 -> innerNext.__1)
            val out = substitute(e)(subInnerNextData)
            Let(
              innerNext,
              FunCall(innerNextF, newAcc.__1),
              IfThenElse(
                innerNext.__2,
                // CASE 1a: Received next element from inner stream.
                //          Update the outer accumulator.
                Tuple(
                  Tuple(
                    Tuple(
                      Tuple() +: as.map(a =>
                        substitute(a)(subInnerNextData)
                      ): _*
                    ),
                    innerNext0
                  ),
                  out,
                  substitute(valid)(subInnerNextData)
                ),
                // CASE 1b: Inner stream did not produce element yet
                //          Leave the outer accumulator as-is.
                Tuple(Tuple(newAcc0, innerNext0), out, False)
              )
            )
          case Tuple(TupleAccess(p, IntCst(0)), as: _*) if p == oldAcc =>
            // HACK: Expand tuple manually, as above.
            val newAcc1 = tupleExpand(newAcc.__1, innerStmAccArity)
            // CASE 2: StmNext() not called, so leave the inner accumulator
            //         as-is.
            Tuple(Tuple(Tuple(Tuple() +: as: _*), newAcc1), e, valid)
          case Tuple(x, _: _*) =>
            throw new IllegalArgumentException(
              s"I can't tell whether StmNext() is being called in ${x} (where oldAcc = ${oldAcc})."
            )
          case _ => ???
        }
      case _: IntExpr | _: BoolExpr | _: VecBuild | _: StmBuild | _: Function |
          _: Tuple =>
        throw new IllegalArgumentException(
          "Could not fuse function bodies due to an apparent type error."
        )
    }
    substitute(e)(Map(oldAcc -> TupleAccess(newAcc, 0)))
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
