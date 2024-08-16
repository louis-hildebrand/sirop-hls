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

          case StmBuild(length, seed, f) =>
            StmBuild(
              substitute(length),
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

  def partialEval(
      e: Expr
  )(implicit substitutions: Map[Expr, Expr] = Map()): Expr = {
    e match {

      case t: Tuple => Tuple(t.elems.toSeq.map(partialEval(_)): _*)
      case TupleAccess(t: Expr, i: Expr) =>
        (partialEval(t), partialEval(i)) match {
          case (tuple: Tuple, index: IntCst) =>
            partialEval(tuple.elems(index.i))
          case (tuple @ _, index @ _) => TupleAccess(tuple, index)
        }

      case p: Param =>
        substitutions.get(p) match {
          case Some(v) => v
          case None    => p
        }
      case f: Function =>
        val newF = substitute(f).asInstanceOf[Function]
        Function(newF.param, partialEval(newF.body))
      case FunCall(f: Expr, arg: Expr) =>
        partialEval(f) match {
          case fun: Function =>
            partialEval(fun.body)(
              substitutions + ((fun.param, partialEval(arg)))
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
          case s: StmBuild => partialEval(s.length)
          case s @ _       => StmLength(s)
        }

      case StmNext(s: Expr) =>
        partialEval(s) match {
          case s: StmBuild =>
            partialEval(s.length) match {
              case len: IntCst => {
                assert(
                  len.i > 0,
                  "Attempt to call StmNext() on a stream of length 0."
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
                            len.i - 1,
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
                              len,
                              partialEval(next.__0),
                              // this function may have free parameters
                              partialEval(s.nextF).asInstanceOf[Function]
                            )
                          )
                        )
                      case _ =>
                        StmNext(s)
                    }
                  case next @ _ => StmNext(s)
                }
              }
              case len @ _ => StmNext(s)
            }
          case s @ _ => StmNext(s)
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
      stm.length,
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
      stm.length,
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
    val f = removeIndices(indicesToRemove)
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
      permute(indexMap)(stm.seed),
      Function(acc, sub(transformHead(permute(indexMap))(stm.nextF.body)))
    )
  }

  /** Permute the elements of a tuple.
    *
    * @param indexMap
    *   Map from old index to new index
    * @param e
    *   Expression to permute (must be a tuple)
    */
  private def permute(indexMap: Map[Int, Int])(e: Expr): Expr = {
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

  private def removeIndices(indices: Seq[Int])(e: Expr): Tuple = {
    val newElems = e
      .asInstanceOf[Tuple]
      .elems
      .zipWithIndex
      .filter((_, i) => !indices.contains(i))
      .map((e, _) => e)
    Tuple(newElems: _*)
  }

  /** Fuse a `StmBuild` with its first stream input.
    */
  def fuse(stm: Expr /* Stm<A; n> */ ): Expr /* Stm<A; n> */ = {
    // TODO: Canonicalize first?
    // TODO: Simply require accumulator to be a Tuple from the start?
    val s = ExprEvaluator.partialEval(stm).asInstanceOf[StmBuild]
    val inputStmPath = locateFirstInputStream(s) match {
      case Some(s) => s
      case None => throw new IllegalArgumentException("No input streams found.")
    }
    val inputStm = extract(s.seed, inputStmPath).asInstanceOf[StmBuild]

    val newSeed =
      Tuple(replaceWith(s.seed, inputStmPath, Tuple()), inputStm.seed)
    val newNextF = fuseFunctions(s.nextF, inputStm.nextF, inputStmPath)

    // TODO: Rewrite the "outer" stream without the input stream (replace
    //       references in nextF with a new "hole" Param?)
    // Combined seed: basically combine the seeds of the two original streams
    // Combined nextF: basically the "outer" stream's nextF, but call the
    //                 "inner" stream's nextF in the right places?
    StmBuild(StmLength(stm), newSeed, newNextF)
  }

  /** Return the path to the first input stream for the given `StmBuild`. The
    * path is a sequence of tuple indices. For example, if the accumulator of
    * the given stream is of the form (Int, (Stm, Stm), Bool), then this method
    * should return Seq(1, 0) because to reach the first stream you must extract
    * element 1 from the outermost tuple and then extract element 0 from the
    * inner tuple. If the accumulator is nothing but a StmBuild, then this
    * method should return Seq().
    */
  private def locateFirstInputStream(s: StmBuild): Option[Seq[Int]] =
    locateFirstStream(s.seed)

  private def locateFirstStream(e: Expr): Option[Seq[Int]] = {
    e match {
      case _: StmBuild      => Some(Seq())
      case Tuple(elems: _*) => locateFirstStream(elems, 0)
      case _                => None
    }
  }

  @tailrec
  private def locateFirstStream(elems: Seq[Expr], i: Int): Option[Seq[Int]] = {
    elems.headOption match {
      case None => None
      case Some(e) =>
        locateFirstStream(e) match {
          case Some(indices) => Some(i +: indices)
          case None          => locateFirstStream(elems.tail, i + 1)
        }
    }
  }

  private def extract(e: Expr, path: Seq[Int]): Expr = {
    (e, path) match {
      case (_, Seq())                 => e
      case (t: Tuple, Seq(i, is: _*)) => extract(t.elems(i), is)
      // Tuple(p.__0, p.__1) is equivalent to just p (if p is a 2-tuple)
      case (p: Param, Seq(i, is: _*)) => extract(TupleAccess(p, i), is)
      case _ => throw new IllegalArgumentException("Failed to extract.")
    }
  }

  private def matches(e: Expr, p: Param, path: Seq[Int]): Boolean = {
    e == path.foldLeft(p: Expr)((e, i) => TupleAccess(e, i))
  }

  private def replaceWith(e: Expr, path: Seq[Int], replacement: Expr): Tuple = {
    path match {
      case Seq() => Tuple()
      case Seq(i, is: _*) =>
        e match {
          case t: Tuple =>
            Tuple(
              t.elems.updated(i, replaceWith(t.elems(i), is, replacement)): _*
            )
          // TODO: Handle this case properly
          // Tuple(p.__0, p.__1) is equivalent to just p (if p is a 2-tuple)
          // However, it seems like we would need to know the type of p to know
          // how many elements to put, and we don't have that information in
          // the interpreter
          case p: Param => ???
          case _ => throw new IllegalArgumentException("Failed to replace.")
        }
    }
  }

  private def fuseFunctions(
      outerNextF: Function,
      innerNextF: Function,
      // Position of the inner stream within the outer accumulator
      path: Seq[Int]
  ): Function = {
    val acc = Param()
    Function(
      acc,
      fuseFunctionBodies(
        acc,
        outerNextF.param,
        outerNextF.body,
        innerNextF,
        path
      )
    )
  }

  private def fuseFunctionBodies(
      newAcc: Param,
      oldAcc: Param,
      body: Expr,
      innerNextF: Function,
      // Position of the inner stream within the outer accumulator
      path: Seq[Int]
  ): Expr = {
    val e = body match {
      case IfThenElse(cond, trueE, falseE) =>
        IfThenElse(
          cond,
          fuseFunctionBodies(newAcc, oldAcc, trueE, innerNextF, path),
          fuseFunctionBodies(newAcc, oldAcc, falseE, innerNextF, path)
        )
      case _: TupleAccess | _: VecAccess | _: FunCall => ???
      case Tuple(a, e, valid) =>
        extract(a, path) match {
          case TupleAccess(StmNext(s), IntCst(0)) if matches(s, oldAcc, path) =>
            // StmNext() called, so update the inner accumulator
            val innerNext = Param()
            Let(
              innerNext,
              FunCall(innerNextF, TupleAccess(newAcc, 1)),
              IfThenElse(
                TupleAccess(innerNext, 2),
                // Received next element from inner stream; proceed as planned
                Tuple(
                  Tuple(
                    replaceWith(a, path, Tuple()),
                    TupleAccess(innerNext, 0)
                  ), {
                    val t =
                      path.foldLeft(oldAcc: Expr)((e, i) => TupleAccess(e, i))
                    val original = TupleAccess(StmNext(t), 1)
                    substitute(e)(Map(original -> TupleAccess(innerNext, 1)))
                  },
                  valid
                ),
                // Inner stream did not yet produce element; don't update the
                // outer accumulator
                Tuple(
                  Tuple(TupleAccess(newAcc, 0), TupleAccess(innerNext, 0)), {
                    val t =
                      path.foldLeft(oldAcc: Expr)((e, i) => TupleAccess(e, i))
                    val original = TupleAccess(StmNext(t), 1)
                    substitute(e)(Map(original -> TupleAccess(innerNext, 1)))
                  },
                  False
                )
              )
            )
          case s if matches(s, oldAcc, path) =>
            // StmNext() not called, so leave the inner accumulator as-is
            Tuple(
              Tuple(
                replaceWith(a, path, Tuple()),
                TupleAccess(newAcc, 1)
              ),
              e,
              valid
            )
          case x =>
            throw new IllegalArgumentException(
              s"I can't tell whether or not StmNext() is being called in ${x}, with oldAcc = ${oldAcc} and path = ${path}"
            )
        }
      case _: IntExpr | _: BoolExpr | _: Param | _: VecBuild | _: StmBuild |
          _: StmNext | _: Function | _: Tuple =>
        throw new IllegalArgumentException(
          "Could not fuse function bodies due to an apparent type error."
        )
    }
    substitute(e)(Map(oldAcc -> TupleAccess(newAcc, 0)))
  }
}
