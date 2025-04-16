package opt

import ir._
import operations.{CeilDiv, Max}

import scala.annotation.tailrec

/** Induction variables are accumulator elements which can be expressed as a
  * function of <code>t</code>, where <code>t</code> is a counter which starts
  * at zero and counts up by one. This pass tries to remove those accumulator
  * elements and instead compute them using <code>t</code>.
  */
class StmInductionVarRemovalPass(facts: FactSet) {

  /** Remove as many induction variables as possible from the given stream.
    */
  def removeInductionVars(s: StmBuild): StmBuild = {
    val funcByIdx = findClosedForms(s)
    replaceInductionVars(s, funcByIdx)
  }

  /** Try to find the value of the given stream's accumulator at time
    * <code>t</code>.
    */
  def tryFindAccumulatorAtTime(
      s: StmBuild,
      c: Expr
  ): Option[Map[Param, Expr]] = {
    val closedFormByVar = findClosedForms(s)

    // Need the new seed to be synthesizable, so can't have any StmNextK's.
    // But we can get rid of StmNextK if k <= 0, so try to do that.
    def removeStmNextK(e: Expr): Expr = {
      e match {
        case StmNextK(s, k) =>
          val isKNonPositive =
            PartialEvalPass.isSmallerOrEqual(k, 0)(this.facts).getOrElse(false)
          if (isKNonPositive) s else e
        case e => e.rebuild(e.children.map(c => removeStmNextK(c)))
      }
    }

    val allElemsHaveClosedForm =
      s.accVars.forall(x => closedFormByVar.isDefinedAt(x))
    if (allElemsHaveClosedForm) {
      val seedsAtT = s.accVars
        .map(x => {
          val e1 = FunCall(closedFormByVar(x), c)
          val e2 = removeStmNextK(e1)
          x -> PartialEvalPass.partialEval(e2)(this.facts)
        })
        .toMap
      // TODO: Is this a sufficient condition?
      val noStmNextK = seedsAtT.forall({ case (_, z) =>
        !z.contains(classOf[StmNextK])
      })
      if (noStmNextK) Some(seedsAtT) else None
    } else {
      None
    }
  }

  /** Try to find an expression for the <code>Option&lt;T&gt;</code> output of
    * this stream as a function of <code>t</code>. The stream must already be in
    * canonical form. In particular, its accumulator must be a flat tuple.
    */
  def tryFindClosedFormForOutput(s: StmBuild): Option[Function] = {
    val closedFormByVar = findClosedForms(s)
    val t = Param("t")
    val subs: Map[Expr, Expr] =
      closedFormByVar.map({ case (x, f) => x -> FunCall(f, t) })
    val out = s.output.substitute(subs)
    val allVarsRemoved = out.freeVars().intersect(s.accVars).isEmpty
    if (allVarsRemoved) Some(Function(t, TyInt, out)) else None
  }

  private def findClosedForms(s: StmBuild): Map[Param, Function] = {
    val t = Param("t")
    // The dependency graph may have cycles. To deal with that, combine
    // elements of each strongly connected component into one tuple.
    val g = s.accVarDependencies.condensation()
    var closedFormByVar = Map[Param, Function]()
    for (v <- g.topologicalOrder()) {
      val dependencies = g.outNeighbours(v).flatten
      val allDependenciesHaveClosedForm =
        dependencies.forall(i => closedFormByVar.contains(i))
      if (!allDependenciesHaveClosedForm) {
        // How can I find a closed form for an accumulator element if I can't
        // find closed forms for all its dependencies?
      } else {
        val subs: Map[Expr, Expr] =
          closedFormByVar.map({ case (x, f) => x -> FunCall(f, t) })
        val equations =
          s.equations
            .filter({ case (x, _) => v.contains(x) })
            .map({ case (x, (z, next)) =>
              // Replace other accumulator vars by their closed form
              x -> (z, next.substitute(subs))
            })
        if (v.size == 1) {
          // For simplicity of the recurrence-solving code, don't wrap this one
          // recurrence in a tuple
          val (x, (z, nextExpr)) = equations.head
          assert(x == v.head)
          assert(
            (nextExpr.freeVars().intersect(s.accVars) - x).isEmpty,
            "all dependencies should have been removed"
          )
          val next = Function(
            t,
            TyInt,
            Function(
              x,
              Missing,
              PartialEvalPass.partialEval(nextExpr)(this.facts.geq(t, 0))
            )
          )
          tryFindClosedForm(0, z, next) match {
            case Some(f) =>
              val peF =
                PartialEvalPass
                  .partialEval(f)(this.facts)
                  .asInstanceOf[Function]
              closedFormByVar += (x -> peF)
            case None => ()
          }
        } else {
          // Combine the recurrence equations into one equation and then split
          // the solution afterwards
          val (z, next, paramByIndex) = mergeRecurrenceEquations(
            equations = s.equations.filter({ case (x, _) => v.contains(x) }),
            t = t
          )
          tryFindClosedForm(0, z, next) match {
            case Some(f) =>
              for ((i, x) <- paramByIndex) {
                val g = Function(
                  t,
                  TyInt,
                  PartialEvalPass
                    .partialEval(TupleAccess(FunCall(f, t), i))(this.facts)
                )
                closedFormByVar += (x -> g)
              }
            case None => ()
          }
        }
      }
    }
    closedFormByVar
  }

  /** Merge the given recurrence equations into one equation which deals with a
    * tuple.
    */
  private def mergeRecurrenceEquations(
      equations: Map[Param, (Expr, Expr)],
      t: Param
  ): (Expr, Function, Map[Int, Param]) = {
    // Sort so that patterns on multiple accumulator elements get them in a
    // more predictable order
    val paramByIndex =
      equations.toSeq
        .sortBy({ case (_, (z, _)) => z })(ExprOrdering)
        .zipWithIndex
        .map({ case ((x, _), i) => i -> x })
        .toMap
    val z = Tuple((0 until equations.size).map(i => {
      val x = paramByIndex(i)
      equations(x)._1
    }): _*)
    val acc = Param("acc")
    val subs: Map[Expr, Expr] =
      paramByIndex.map({ case (i, x) => x -> TupleAccess(acc, i) })
    val next = Function(
      t,
      TyInt,
      Function(
        acc,
        Missing,
        Tuple((0 until equations.size).map(i => {
          val x = paramByIndex(i)
          equations(x)._2.substitute(subs)
        }): _*)
      )
    )
    (z, next, paramByIndex)
  }

  /** Replace each accumulator variable with its closed form, if it has one.
    */
  private def replaceInductionVars(
      stm: StmBuild,
      closedFormByVar: Map[Param, Function]
  ): StmBuild = {
    if (closedFormByVar.isEmpty) {
      stm
    } else {
      val t = Param("t")

      // Process the closed forms without StmNextK first because they should
      // always work regardless of order.
      // The ones with StmNextK may depend on others.
      // TODO: I should probably also sort the ones with StmNextK amongst
      //       themselves.
      //       Maybe do it in topological order or reverse topological order
      //       or something?
      val closedFormsInOrder = closedFormByVar.toSeq
        .sortBy({ case (_, f) => f.contains(classOf[StmNextK]) })

      // Try to replace the existing elements with their closed form
      var s = stm
      var newAccVars = Map[Param, (Expr, Function)]()
      for ((x, f) <- closedFormsInOrder) {
        val newStm = PartialEvalPass
          .partialEval(s.replaceVar(x, FunCall(f, t)))(this.facts)
          .asInstanceOf[StmBuild]
        assert(!newStm.freeVars().contains(f.param))
        if (!newStm.contains(classOf[StmNextK])) {
          s = newStm
        } else if (f.body.isInstanceOf[StmNextK]) {
          // No point in trying to replace a stream-valued expression with its
          // closed form
        } else {
          replaceStmNextK(newStm, t) match {
            case Some((withoutStmNextK: StmBuild, newRecEqns)) =>
              assert(!withoutStmNextK.contains(classOf[StmNextK]))
              // TODO: Check that the stream is still synthesizable
              //       (e.g., every StmNext(acc).__1 has a corresponding
              //       StmNext(acc).__0)?
              // TODO: Ensure that the input stream is still fully consumed?
              s = withoutStmNextK
              newAccVars ++= newRecEqns
            case _ => ()
          }
        }
      }

      // Add new elements as required (e.g., the counter for t, input streams
      // for elements whose closed form included StmNextK)
      for ((x, (z, Function(t, _, f))) <- newAccVars) {
        s = s.addAccumulator(x, z, FunCall(f, x))
      }
      s = s.addAccumulator(t, 0, t + 1)
      s
    }
  }

  /** Get rid of every occurrence of StmNextK in the given expression.
    *
    * @return
    *   The new expression with some free variables, along with recursive
    *   formulas for those free variables
    */
  private def replaceStmNextK(
      e: Expr,
      t: Param
  ): Option[(Expr, Map[Param, (Expr, Function)])] = {
    e match {
      case s: StmNextK =>
        tryFindRecursiveForm(s, t) match {
          case Some((z, f)) =>
            val r = Param("r")
            Some((r, Map(r -> (z, f))))
          case None => None
        }
      case e =>
        val childResults = e.children.map(c => replaceStmNextK(c, t))
        if (childResults.exists(x => x.isEmpty)) {
          None
        } else {
          val unwrappedChildResults = childResults.map(x => x.get)
          val newChildren = unwrappedChildResults.map(x => x._1)
          val newAccElems = unwrappedChildResults
            .map(x => x._2)
            .foldLeft(Map[Param, (Expr, Function)]())(_ ++ _)
          Some((e.rebuild(newChildren), newAccElems))
        }
    }
  }

  /** Attempt to convert a recurrence equation of the form
    * {{{
    *   x(t0) = z
    *   x(t + 1) = f(t, x(t)) for all t >= t0
    * }}}
    * to a function of <code>t</code>.
    */
  def tryFindClosedForm(
      t0: Expr,
      z: Expr,
      next: Function
  ): Option[Function] = {
    val t = next.param
    (z, next) match {
      case (z, Function(t, _, Function(x0, _, x1))) if x0 == x1 =>
        // Identity
        Some(Function(t, TyInt, z))
      case (z, Function(t, _, Function(x, _, e))) if !e.contains(x) =>
        //     x_{t+1} = f(t) for t >= 0
        // ==> x_t = f(t - 1) for t >= 1
        Some(
          Function(t, TyInt, IfThenElse(t === 0, z, e.substitute(t -> (t - 1))))
        )
      case Counter(delta) => Some(Function(t, TyInt, z + (t - t0) * delta))
      case LeftShiftRegister(n, f, g) =>
        Some(
          Function(
            t,
            TyInt,
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
      case (
            s,
            Function(t, _, Function(x0, _, TupleAccess(StmNext(x1), IntCst(0))))
          ) if x0 == x1 =>
        Some(Function(t, TyInt, StmNextK(s, t - t0)))
      case Piecewise(k, f, g) =>
        tryFindClosedForm(t0, z, f) match {
          case Some(f) =>
            // The time at which we switch from one side of the piecewise function to the other
            val t1 = Max(k, t0)
            val valAtT1 = FunCall(f, t1)
            tryFindClosedForm(t1, valAtT1, g) match {
              case Some(g) =>
                Some(
                  Function(
                    t,
                    TyInt,
                    IfThenElse(t < t1, FunCall(f, t), FunCall(g, t))
                  )
                )
              case None => None
            }
          case None => None
        }
      // TODO: Add similar cases (e.g., a counter that starts false and becomes true)?
      case (
            True,
            Function(t, _, Function(acc, _, and @ And(terms @ _*)))
          ) if terms.contains(acc) =>
        (t, acc, and.remove(acc)) match {
          case TimeLessThan(k) if !k.contains(acc) =>
            // Add one to account for the fact that the boolean will only
            // change value at the next cycle.
            // Use Max to account for the case where the condition is
            // immediately false: the value at t = 0 will nevertheless be True.
            Some(Function(t, TyInt, t - t0 < 1 + Max(0, k)))
          case _ => None
        }
      case (
            Tuple(True, i0),
            Function(
              t,
              _,
              Function(
                acc,
                _,
                Tuple(
                  and @ And(terms @ _*),
                  boundedCtrUpdate @ IfThenElse(
                    TupleAccess(a0, IntCst(0)),
                    ctrUpdateIfTrue,
                    TupleAccess(a1, IntCst(1))
                  )
                )
              )
            )
          ) if a0 == acc && a1 == acc && terms.contains(acc.__0) =>
        val bCond = and.remove(acc.__0)
        // (1) Find closed form for counter if it was unbounded
        val a = Param("a")
        val ctrNext = Function(
          t,
          TyInt,
          Function(a, TyInt, ctrUpdateIfTrue.substitute(acc.__1 -> a))
        )
        if (!ctrNext.contains(acc)) {
          tryFindClosedForm(t0, i0, ctrNext) match {
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
                      TyInt,
                      Function(
                        a,
                        TyInt,
                        boundedCtrUpdate.substitute(
                          Map[Expr, Expr](acc.__0 -> (t < K), acc.__1 -> a)
                        )
                      )
                    )
                    tryFindClosedForm(t0, i0, boundedCtrNext) match {
                      case Some(h) =>
                        Some(Function(t, TyInt, Tuple(t < K, FunCall(h, t))))
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

  /** Attempt to convert a <code>StmNextK</code> into a recurrence equation
    * involving only <code>StmNext</code>. The recurrence equation will be a
    * function of the form <code>f(t, x(t))</code>.
    */
  def tryFindRecursiveForm(
      nxt: StmNextK,
      t: Param
  ): Option[(Expr, Function)] = {
    val facts = this.facts.geq(t, 0)

    def findRec(e: Expr, t0: Expr): Option[(Expr, Function)] = {
      e match {
        case IfThenElse(c, lhs, rhs) =>
          (t, Param(), c) match {
            case TimeLessThan(k) =>
              findRec(lhs, t0) match {
                case Some((z0, f0)) =>
                  findRec(rhs, k) match {
                    case Some((z1, f1)) =>
                      val expectedZ1 = lhs.substitute(t -> k)
                      val isContinuous = PartialEvalPass
                        .isEqual(z1, expectedZ1)(facts)
                        .getOrElse(false)
                      if (isContinuous) {
                        val f =
                          Function(
                            t,
                            TyInt,
                            (x: Expr) =>
                              IfThenElse(
                                c,
                                FunCall(FunCall(f0, t), x),
                                FunCall(FunCall(f1, t), x)
                              )
                          )
                        Some((z0, f))
                      } else {
                        None
                      }
                    case _ => None
                  }
                case _ => None
              }
            case _ => None
          }
        case next @ StmNextK(_, k) =>
          // Need to show that 0 <= k(t + 1) - k(t) <= 1
          // (i.e., can only read one element at a time; can't skip, can't go backwards, etc.)
          val nextK = k.substitute(t -> (t + 1))
          val deltaK = PartialEvalPass.partialEval(nextK - k)(this.facts)
          val isDeltaZeroOrOne = (
            PartialEvalPass.isGreaterOrEqual(deltaK, 0)(facts).getOrElse(false)
              && PartialEvalPass
                .isSmallerOrEqual(deltaK, 1)(facts)
                .getOrElse(false)
          )
          if (isDeltaZeroOrOne) {
            val z = next.substitute(t -> t0)
            val nextF = Function(
              t,
              TyInt,
              (acc: Expr) =>
                IfThenElse(deltaK === 0 || k < 0, acc, StmNext(acc).__0)
            )
            Some((z, nextF))
          } else {
            None
          }
        case _ => None
      }
    }

    val e = PartialEvalPass.partialEval(nxt)(this.facts, MoveUp)
    findRec(e, t0 = 0) match {
      case Some((z_, f_)) =>
        val z = PartialEvalPass.partialEval(z_)(this.facts)
        // Start reading the stream from the beginning: can't start from the middle
        // TODO: Is this condition really necessary? Maybe I can get away with
        //       just checking that the new z is valid (e.g., it doesn't
        //       contain StmNextK, it's not DontCare)
        val initOk = z match {
          case s if s == nxt.s => true
          case StmNextK(s, k) =>
            s == nxt.s && PartialEvalPass
              .isSmallerOrEqual(k, 0)(facts)
              .getOrElse(false)
          case _ => false
        }
        if (initOk) {
          val f = PartialEvalPass.partialEval(f_)(this.facts)
          Some((nxt.s, f.asInstanceOf[Function]))
        } else {
          None
        }
      case _ => None
    }
  }
}

object StmInductionVarRemovalPass {
  def apply(facts: FactSet = FactSet()): StmInductionVarRemovalPass = {
    new StmInductionVarRemovalPass(facts)
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
      case Function(t, _, Function(acc, _, Sum(terms))) =>
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
              _,
              Function(
                acc,
                _,
                VecBuild(
                  // TODO: What if the VecLength has been partially evaluated to a constant?
                  VecLength(a0),
                  Function(
                    i0: Param,
                    _,
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
        Some((n, f, Function(t, TyInt, e)))
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
      case Function(t, _, Function(acc, _, e)) =>
        (t, acc, e) match {
          case IfTimeLessThan(k, a, b) =>
            Some(
              (
                k,
                Function(t, TyInt, Function(acc, Missing, a)),
                Function(t, TyInt, Function(acc, Missing, b))
              )
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
              case _ => None
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
      case _ => None
    }
  }
}
