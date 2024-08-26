import scala.annotation.tailrec

object ExprEvaluator {

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

  private def getLengths(e: Expr): Option[Seq[(Int, Int)]] = {
    partialEval(e) match {
      case Tuple(es: _*) =>
        if es.forall(e => e.isInstanceOf[Tuple]) then {
          val elems = es.map(e => e.asInstanceOf[Tuple].elems)
          if elems.forall(e =>
              e.length == 2
                && e(0).isInstanceOf[IntCst]
                && e(1).isInstanceOf[IntCst]
            )
          then {
            Some(
              elems.map(e =>
                (e(0).asInstanceOf[IntCst].i, e(1).asInstanceOf[IntCst].i)
              )
            )
          } else {
            None
          }
        } else {
          None
        }
      case _ => None
    }
  }

  private def getNextLengths(lens: Seq[(Int, Int)]): Seq[(Int, Int)] = {
    if lens.isEmpty then {
      lens
    } else {
      val backwardsLens = lens.reverse
      val bHead = backwardsLens.head
      val bTail = backwardsLens.tail
      val newBackwardsLens = if backwardsLens.head._1 == 1 then {
        // Overflow (inner stream is empty; move to next outer stream)
        val t = getNextLengths(bTail)
        if t.isEmpty || t.head._1 == 0 then {
          // Stream is completely empty
          (0, bHead._2) +: t
        } else {
          // Stream still has some elements left
          (bHead._2, bHead._2) +: t
        }
      } else {
        // No overflow
        (bHead._1 - 1, bHead._2) +: bTail
      }
      newBackwardsLens.reverse
    }
  }

  def partialEval(e: Expr): Expr = {
    e match {

      case t: Tuple => Tuple(t.elems.toSeq.map(partialEval(_)): _*)
      case TupleAccess(t: Expr, i: Expr) =>
        (partialEval(t), partialEval(i)) match {
          case (tuple: Tuple, index: IntCst) =>
            partialEval(tuple.elems(index.i))
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
          case cond @ _ =>
            if (trueE == falseE)
              trueE
            else
              IfThenElse(cond, partialEval(trueE), partialEval(falseE))
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

      case StmBuild(length, seed, f) =>
        StmBuild(
          partialEval(length),
          partialEval(seed),
          // ensures any free Param in f gets substituted
          partialEval(f).asInstanceOf[Function]
        )

      case StmLength(s) =>
        partialEval(s) match {
          case s: StmBuild => partialEval(s.lengths)
          case s @ _       => StmLength(s)
        }

      case StmNext(s: Expr) =>
        partialEval(s) match {
          case s: StmBuild =>
            getLengths(s.lengths) match {
              case Some(lens) =>
                assert(
                  !lens.exists((n, _) => n <= 0),
                  "Attempt to call StmNext() on an empty stream."
                )
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
                            Tuple(
                              getNextLengths(lens)
                                .map((x, y) => Tuple(x, y)): _*
                            ),
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
                              s.lengths,
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

  def canonicalize(stm: StmBuild): StmBuild = {
    val s = partialEval(stm).asInstanceOf[StmBuild]
    val s0 = tupleAccumulator(s)
    val s1 = flattenAccumulator(s0)
    val s2 = removeEmptyTuples(s1)
    moveStreamsToFront(s2)
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
      stm.lengths,
      Tuple(stm.seed),
      Function(acc, sub(tupleHead(stm.nextF.body)))
    )
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
      stm.lengths,
      flatten(stm.seed),
      Function(p, substitute(flattenHead(stm.nextF.body))(tupleAccessMap))
    )
  }

  /** Remove empty tuples from the accumulator (leaving the accumulator as an
    * empty tuple if the accumulator itself is empty).
    */
  private def removeEmptyTuples(stm: StmBuild): StmBuild = {
    // Assumes the accumulator is a tuple
    // A previous transformation should tuple the accumulator if necessary
    val seed = stm.seed.asInstanceOf[Tuple]
    val indicesToRemove = seed.elems.zipWithIndex
      .filter((e, _) => e == Tuple())
      .map((_, i) => i)
    // Need to adjust indices used to read accumulator.
    // For each element removed, you need to decrement the indices of all
    // following elements by one.
    val indexMap = (0 until seed.elems.length)
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
          case None    => TupleAccess(acc, i) -> Tuple()
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
      stm.lengths,
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
      stm.lengths,
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
      s.lengths,
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
      case _: TupleAccess | _: VecAccess | _: FunCall => ???
      case Tuple(a, e, valid) =>
        a match {
          case Tuple(
                TupleAccess(StmNext(TupleAccess(oldAcc, IntCst(0))), IntCst(0)),
                as: _*
              ) =>
            // CASE 1: StmNext() called.
            //         Update the inner accumulator.
            val innerNext = Param()
            // HACK: Expand all tuples manually.
            // Ideally this would be a canonicalization pass, but I think
            // figuring out the arity of a tuple-valued param requires input
            // from the type system.
            val innerNext0 = tupleExpand(innerNext.__0, innerStmAccArity)
            val newAcc0 = tupleExpand(newAcc.__0, outerStmAccArity)
            val out =
              substitute(e)(Map(StmNext(oldAcc.__0).__1 -> innerNext.__1))
            Let(
              innerNext,
              FunCall(innerNextF, newAcc.__1),
              IfThenElse(
                innerNext.__2,
                // CASE 1a: Received next element from inner stream.
                //          Update the outer accumulator.
                Tuple(
                  Tuple(Tuple(Tuple() +: as: _*), innerNext0),
                  out,
                  valid
                ),
                // CASE 1b: Inner stream did not produce element yet
                //          Leave the outer accumulator as-is.
                Tuple(Tuple(newAcc0, innerNext0), out, False)
              )
            )
          case Tuple(TupleAccess(oldAcc, IntCst(0)), as: _*) =>
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
      case _: IntExpr | _: BoolExpr | _: Param | _: VecBuild | _: StmBuild |
          _: StmNext | _: Function | _: Tuple =>
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
