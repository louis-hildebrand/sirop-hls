package opt

import ir._
import operations.{Max, CeilDiv}

/** Induction variables are accumulator elements which can be expressed as a
  * function of <code>t</code>, where <code>t</code> is a counter which starts
  * at zero and counts up by one. This pass tries to remove those accumulator
  * elements and instead compute them using <code>t</code>.
  */
object StmInductionVarRemovalPass {

  /** Remove as many induction variables as possible from the given stream.
    */
  def removeInductionVars(stm: StmBuild): StmBuild = {
    // Canonicalize to flatten the accumulator and whatnot
    val s = StmCanonPass.canonicalize(stm)
    val funcByIdx = findClosedForms(s)
    removeInductionVars(s, funcByIdx)
  }

  /** If all induction variables can be removed, then do so. But if any
    * induction variables cannot be removed, return <code>None</code>.
    */
  def tryRemoveAllInductionVars(stm: StmBuild): Option[StmBuild] = {
    // Canonicalize to flatten the accumulator and whatnot
    val s = StmCanonPass.canonicalize(stm)
    val funcByIdx = findClosedForms(s)
    val allElemsRemovable = s.seed
      .asInstanceOf[Tuple]
      .elems
      .indices
      .forall(i => funcByIdx.contains(i))
    if (allElemsRemovable) {
      Some(removeInductionVars(s, funcByIdx))
    } else {
      None
    }
  }

  private def findClosedForms(s: StmBuild): Map[Int, Function] = {
    val seed = s.seed.asInstanceOf[Tuple]
    val acc = s.nextF.param
    val t = Param("t")
    val initAndNextByIdx = seed.elems.indices.map(i => {
      val z = seed.elems(i)
      val next = PartialEvalPass.partialEval(TupleAccess(s.nextF.body.__0, i))
      (z, next)
    })
    // The dependency graph may be acyclic. To deal with that, combine elements
    // of each strongly connected component into one tuple.
    val g = dependencyGraph(initAndNextByIdx, acc).condensation()
    var funcByIdx = Map[Int, Function]()
    for (v <- g.topologicalOrder()) {
      val dependencies = g.outNeighbours(v).flatten
      val allDependenciesHaveClosedForm =
        dependencies.forall(i => funcByIdx.contains(i))
      // How can I find a closed form for an accumulator element if I can't
      // find closed forms for all its dependencies?
      if (allDependenciesHaveClosedForm) {
        val (z, next, indices) = makeRecurrenceEquation(
          equations = initAndNextByIdx.zipWithIndex
            .filter({ case (_, i) => v.contains(i) })
            .map({ case ((z, f), i) => i -> (z, f) })
            .toMap,
          dependencies = dependencies.map(i => i -> funcByIdx(i)).toMap,
          acc = acc,
          t = t
        )
        if (next.contains(acc)) {
          // The accumulator is still there, so maybe there are some
          // non-static dependencies (e.g., TupleAccess(acc, i) where i
          // cannot be partially evaluated to an int).
          // Doesn't seem like there's much we can do in that case.
        } else {
          tryGetInductionVar(0, z, next) match {
            case Some(f) =>
              if (z.isInstanceOf[Tuple]) {
                // Track each accumulator element separately, even though they
                // were tupled together earlier
                for ((iOld, iNew) <- indices.zipWithIndex) {
                  val g = Function(
                    t,
                    PartialEvalPass.partialEval(
                      TupleAccess(FunCall(f, t), iNew)
                    )
                  )
                  funcByIdx += (iOld -> g)
                }
              } else {
                funcByIdx += (v.head -> PartialEvalPass
                  .partialEval(f)
                  .asInstanceOf[Function])
              }
            case None => ()
          }
        }
      }
    }
    funcByIdx
  }

  /** Construct the directed graph representing dependencies between accumulator
    * elements. The graph may contain cycles.
    */
  private def dependencyGraph(
      equationByIdx: Seq[(Expr, Expr)],
      acc: Param
  ): DiGraph[Int] = {
    val nodes = equationByIdx.indices
    val edges = nodes.flatMap(i => {
      val (_, next) = equationByIdx(i)
      getDependencies(next, acc)
        // If we can't statically determine dependencies, assume it depends on
        // all the accumulator elements
        .getOrElse(nodes)
        .map(j => (i, j))
    })
    DiGraph(nodes.toSet, edges.toSet)
  }

  /** Find which elements of the accumulator <code>e</code> depends on. Return
    * <code>None</code> if the expression does depend on <code>acc</code> but we
    * can't tell statically which indices are being accessed.
    */
  private def getDependencies(e: Expr, acc: Param): Option[Set[Int]] = {
    e match {
      case TupleAccess(a: Param, IntCst(j)) =>
        if (a == acc) Some(Set(j)) else Some(Set())
      case a: Param =>
        if (a == acc)
          // Maybe we got here via a `TupleAccess(acc, j)` where `j` is some
          // expression that couldn't be partially evaluated to an int.
          None
        else
          Some(Set())
      case e =>
        e.children.foldLeft[Option[Set[Int]]](Some(Set[Int]()))({
          case (None, _) => None
          case (Some(deps), e) =>
            getDependencies(e, acc) match {
              case Some(moreDeps) => Some(deps ++ moreDeps)
              case None           => None
            }
        })
    }
  }

  /** Make a recurrence equation only for the given indices, replacing each
    * reference to another accumulator element with the closed-form function for
    * that element. Return the initial element and the update function. If we're
    * dealing with multiple indices (i.e., there's a circular dependency), then
    * the recurrence variable will be a tuple. For convenience, if we're dealing
    * with just on element, the recurrence variable will NOT be a tuple.
    *
    * @param equations
    *   The original recurrence equation (which uses the full <code>acc</code>)
    *   by index. This must contain ONLY the indices which will be part of the
    *   simplified recurrence equation.
    * @param dependencies
    *   The value of each accumulator element that this one depends on, as a
    *   function of <code>t</code>.
    * @param acc
    *   The input to the original stream's update function.
    */
  private def makeRecurrenceEquation(
      equations: Map[Int, (Expr, Expr)],
      dependencies: Map[Int, Function],
      acc: Param,
      t: Param
  ): (Expr, Function, Seq[Int]) = {
    // Sort so that patterns on multiple accumulator elements get them in a
    // more predictable order
    val indices =
      equations.toSeq
        .sortBy({ case (i, (z, _)) => z })(ExprOrdering)
        .map({ case (i, _) => i })
    // Initial value
    val z = if (indices.length == 1) {
      equations(indices.head)._1
    } else {
      Tuple(indices.map(i => equations(i)._1): _*)
    }
    // Update function
    val nexts = indices.map(i => equations(i)._2)
    val x = Param("x")
    val subs = {
      // Replace dependencies with their value as a function of t
      val depSubs =
        dependencies.foldLeft(Map[Expr, Expr]())({ case (subs, (i, f)) =>
          subs + (TupleAccess(acc, i) -> PartialEvalPass.partialEval(
            FunCall(f, t)
          ))
        })
      // Use a new parameter to represent the recurrence variable
      val recVarSubs =
        if (indices.length == 1) {
          val i = indices.head
          Map(TupleAccess(acc, i) -> x)
        } else {
          indices
            .map(i => TupleAccess(acc, i) -> TupleAccess(x, indices.indexOf(i)))
            .toMap
        }
      depSubs ++ recVarSubs
    }
    val next =
      Function(
        t,
        Function(
          x,
          (if (indices.length == 1) nexts.head else Tuple(nexts: _*))
            .substitute(subs)
        )
      )
    (z, next, indices)
  }

  private def removeInductionVars(
      stm: StmBuild,
      funByIdx: Map[Int, Function]
  ): StmBuild = {
    if (funByIdx.isEmpty) {
      stm
    } else {
      val s1 = PartialEvalPass
        .partialEval(
          StmUtils.appendAccumulator(stm, 0, (i: Expr) => i + 1)
        )
        .asInstanceOf[StmBuild]

      val acc = s1.nextF.param
      val subs: Map[Expr, Expr] = funByIdx
        .map({ case (i, f) => TupleAccess(acc.__0, i) -> FunCall(f, acc.__1) })
      // Canonicalization is required for removing accumulator elements
      val s2 = StmCanonPass.canonicalize(
        StmBuild(
          s1.length,
          s1.seed,
          Function(acc, s1.nextF.body.substitute(subs))
        )
      )

      val indicesToRemove = funByIdx.keySet.toSeq
      StmUtils.removeAccumulatorElemsByIndex(s2, indicesToRemove)
    }
  }

  /** Attempt to convert a recurrence equation of the form
    * {{{
    *   x(t0) = z
    *   x(t + 1) = f(t, x(t)) for all t >= t0
    * }}}
    * to a function of <code>t</code>.
    */
  private def tryGetInductionVar(
      t0: Expr,
      z: Expr,
      next: Function
  ): Option[Function] = {
    val t = next.param
    (z, next) match {
      case (z, Function(t, Function(x0, x1))) if x0 == x1 =>
        Some(Function(t, z))
      case Counter(delta) => Some(Function(t, z + (t - t0) * delta))
      case LeftShiftRegister(n, f, g) =>
        Some(
          Function(
            t,
            VecBuild(
              n,
              (i: Expr) =>
                IfThenElse(
                  // Imagine the vector is a fixed, infinite tape and we're
                  // moving to the right as time goes along.
                  //   [f(0), f(1), ..., f(n - 1), g(0), g(1), ...]
                  (t - t0 + i) < n,
                  FunCall(f, t - t0 + i),
                  FunCall(g, t - t0 + i - n)
                )
            )
          )
        )
      case Piecewise(k, f, g) =>
        tryGetInductionVar(t0, z, f) match {
          case Some(f) =>
            // The time at which we switch from one side of the piecewise function to the other
            val t1 = Max(k, t0)
            val valAtT1 = FunCall(f, t1)
            tryGetInductionVar(t1, valAtT1, g) match {
              case Some(g) =>
                Some(
                  Function(
                    t,
                    IfThenElse(t < t1, FunCall(f, t), FunCall(g, t))
                  )
                )
              case None => None
            }
          case None => None
        }
      // TODO: Add similar cases (e.g., a counter that starts false and becomes true)?
      case (True, Function(t, Function(acc, And(a, c)))) if a == acc =>
        (t, acc, c) match {
          case TimeLessThan(k) if !k.contains(acc) =>
            // Add one to account for the fact that the boolean will only
            // change value at the next cycle.
            // Use Max to account for the case where the condition is
            // immediately false: the value at t = 0 will nevertheless be True.
            Some(Function(t, t - t0 < 1 + Max(0, k)))
          case _ => None
        }
      case (
            Tuple(True, i0),
            Function(
              t,
              Function(
                acc,
                Tuple(
                  And(TupleAccess(a0, IntCst(0)), bCond),
                  boundedCtrUpdate @ IfThenElse(
                    TupleAccess(a1, IntCst(0)),
                    ctrUpdateIfTrue,
                    TupleAccess(a2, IntCst(1))
                  )
                )
              )
            )
          ) if a0 == acc && a1 == acc && a2 == acc =>
        // (1) Find closed form for counter if it was unbounded
        val a = Param("a")
        val ctrNext = Function(
          t,
          Function(a, ctrUpdateIfTrue.substitute(acc.__1 -> a))
        )
        if (!ctrNext.contains(acc)) {
          tryGetInductionVar(t0, i0, ctrNext) match {
            case Some(f) =>
              // (2) Find closed form for the boolean if the counter was unbounded
              val bCondIfUnbounded = bCond.substitute(
                Map[Expr, Expr](acc.__0 -> a, acc.__1 -> FunCall(f, t))
              )
              if (!bCondIfUnbounded.contains(acc)) {
                (t, a, bCondIfUnbounded) match {
                  case TimeLessThan(k) =>
                    // The boolean is equivalent to t < K (need to account for the fact that the accumulator only
                    // changes at the next cycle and the condition may be false immediately)
                    val K = 1 + Max(t0, k)
                    // (3) Now find closed form for the bounded counter
                    val boundedCtrNext = Function(
                      t,
                      Function(
                        a,
                        boundedCtrUpdate.substitute(
                          Map[Expr, Expr](acc.__0 -> (t < K), acc.__1 -> a)
                        )
                      )
                    )
                    tryGetInductionVar(t0, i0, boundedCtrNext) match {
                      case Some(h) =>
                        Some(Function(t, Tuple(t < K, FunCall(h, t))))
                      case None => None
                    }
                  case _ => None
                }
              } else {
                None
              }
            case None => None
          }
        } else {
          None
        }
      case _ => None
    }
  }
}

object Counter {

  /** A counter which changes by <code>delta</code> at each step, where
    * <code>delta</code> is constant within the stream (i.e., does not depend on
    * the accumulator).
    *
    * @return
    *   <code>Some(delta)</code> if this is a counter, otherwise
    *   <code>None</code>.
    */
  def unapply(args: (Expr, Function)): Option[Expr] = {
    val (_, next) = args
    next match {
      case Function(t, Function(acc, Sum(terms))) =>
        val termsWithAcc = terms.filter(e => e.contains(acc))
        termsWithAcc match {
          case Seq(a) if a == acc =>
            val otherTerms = terms.diff(termsWithAcc)
            val delta = Sum(otherTerms: _*)
            val isConstInStream = !delta.contains(t)
            if (isConstInStream) {
              Some(delta)
            } else {
              None
            }
          case _ => None
        }
      case _ => None
    }
  }
}

object LeftShiftRegister {

  /** A leftwards-shifting shift register (i.e., a <code>VecShiftLeft</code>) of
    * size <code>n</code>, where the initial value at index <code>i</code> is
    * <code>f(i)</code>, and the element inserted at time <code>t</code> is
    * <code>g(t)</code>.
    *
    * @return
    *   <code>Some((n, f, g))</code> if the given expression is a left shift
    *   register, otherwise <code>None</code>.
    */
  def unapply(args: (Expr, Function)): Option[(Expr, Expr, Expr)] = {
    val (z, next) = args
    // Partially evaluate because the initial value may be a function call that
    // ends up returning a vector, for example
    (PartialEvalPass.partialEval(z), next) match {
      case (
            VecBuild(n, f),
            Function(
              t,
              Function(
                acc,
                VecBuild(
                  // TODO: What if the VecLength has been partially evaluated to a constant?
                  VecLength(a0),
                  Function(
                    i0: Param,
                    IfThenElse(
                      Equal(i1, Sum(Seq(IntCst(-1), VecLength(a1)))),
                      e,
                      VecAccess(a2, Sum(Seq(IntCst(1), i2)))
                    )
                  )
                )
              )
            )
          )
          if a0 == acc && a1 == acc && a2 == acc && i1 == i0 && i2 == i0
            && !e.contains(acc) =>
        // e may have t as a free variable
        Some((n, f, Function(t, e)))
      case _ => None
    }
  }
}

object Piecewise {

  /** A function of the form <code>h(t, x) = if (t &lt; k) then f(t, x) else
    * g(t, x)</code>
    *
    * @return
    *   `(k, f, g)`
    */
  def unapply(args: (Expr, Function)): Option[(Expr, Function, Function)] = {
    val (_, next) = args
    next match {
      case Function(t, Function(acc, e)) =>
        (t, acc, e) match {
          case IfTimeLessThan(k, a, b) =>
            Some(
              (k, Function(t, Function(acc, a)), Function(t, Function(acc, b)))
            )
          case _ => None
        }
      case _ => None
    }
  }
}

object TimeLessThan {
  def unapply(args: (Param, Param, Expr)): Option[Expr] = {
    val (t, acc, lt) = args
    (t, acc, IfThenElse(lt, True, False)) match {
      case IfTimeLessThan(k, True, False) => Some(k)
      case _                              => None
    }
  }
}

object IfTimeLessThan {

  /** An expression of the form <code>if (t &lt; k) then a else b</code>, where
    * <code>k</code> neither depends on <code>t</code> nor <code>acc</code>.
    *
    * @return
    *   <code>(k, a, b)</code>
    */
  def unapply(args: (Param, Param, Expr)): Option[(Expr, Expr, Expr)] = {
    val (t, acc, e) = args
    (e, t) match {
      case IfLessThan(k, a, b) =>
        if (!k.contains(acc)) Some((k, a, b)) else None
      case _ => None
    }
  }
}

object IfLessThan {

  /** An expression of the form <code>if (x &lt k) then a else b</code>, where
    * <code>k</code> does not depend on <code>x</code>
    *
    * @return
    *   <code>(k, a, b)</code>
    */
  def unapply(args: (Expr, Expr)): Option[(Expr, Expr, Expr)] = {
    val (e, x) = args
    e match {
      case IfThenElse(LessThan(y, z), a, b) =>
        // Look at y - z to deal with things like 10 + x < 20, 5 < x, etc.
        // Just matching LessThan(x, k) doesn't handle those cases.
        (PartialEvalPass.partialEval(y - z), x) match {
          case LinearFunctionOf(c0, c1) =>
            // TODO: Use a more general approach to find the sign of a particular expression?
            c1 match {
              case IntCst(c1) if c1 > 0 =>
                //     c0 + c1*x < 0
                // iff c1*x < -c0
                // iff x < ceil(-c0 / c1)  (since we're dealing with integers)
                Some((CeilDiv(-1 * c0, c1), a, b))
              case IntCst(c1) if c1 < 0 =>
                //     c0 + c1*x < 0
                // iff c1*x < -c0
                // iff x > ceil(-c0 / c1)  (sign flips because c1 < 0)
                // iff x >= 1 + ceil(-c0 / c1)
                Some((1 + CeilDiv(-1 * c0, c1), b, a))
              case _ => None
            }
          case _ => None
        }
      case _ => None
    }
  }
}

object LinearFunctionOf {

  /** An expression of the form <code>c0 + c1*x</code>, where neither
    * <code>c0</code> nor <code>c1</code> depend on <code>x</code>.
    *
    * @return
    *   <code>(c0, c1)</code>
    */
  def unapply(args: (Expr, Expr)): Option[(Expr, Expr)] = {
    val (e, x) = args
    e match {
      case Sum(terms) =>
        val termsWithX = terms.filter(e => e.contains(x))
        termsWithX match {
          case Seq(termWithX) =>
            (termWithX, x) match {
              case ConstMultipleOf(c1) =>
                val termsWithoutX = terms.diff(termsWithX)
                val c0 = Sum(termsWithoutX: _*)
                Some((c0, c1))
            }
          case _ => None
        }
      case e =>
        (e, x) match {
          case ConstMultipleOf(c1) => Some((0, c1))
          case _                   => None
        }
    }
  }
}

object ConstMultipleOf {

  /** An expression of the form <code>k * x</code>, where <code>k</code> does
    * not depend on <code>x</code>
    *
    * @return
    *   The coefficient, <code>k</code>
    */
  def unapply(args: (Expr, Expr)): Option[Expr] = {
    val (e, x) = args
    e match {
      case y if y == x => Some(1)
      case Prod(factors) =>
        val factorsWithX = factors.filter(e => e.contains(x))
        factorsWithX match {
          case Seq(y) if y == x =>
            val factorsWithoutX = factors.diff(factorsWithX)
            val coeff = factorsWithoutX match {
              case Seq(e) => e
              case xs     => Prod(xs: _*)
            }
            Some(coeff)
          case _ => None
        }
    }
  }
}
