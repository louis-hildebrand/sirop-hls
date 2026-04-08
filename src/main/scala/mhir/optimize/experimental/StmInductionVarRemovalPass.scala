package mhir.optimize

import mhir.ir._
import mhir.ir.typecheck.{TypeCheck, TypeError}
import mhir.optimize.{PartialEvalPass => PE}
import mhir.sugar._

import scala.annotation.tailrec

// You may need to don a hazmat suit before working on this code.

/** Construct a new stream by skipping a certain number of elements within
  * another stream.
  *
  * This construct is <i>not</i> synthesizable in general—stream must be read in
  * order starting from the beginning, but this allows jumping to a random index
  * within a stream. However, it is useful for certain optimization passes
  * (e.g., [[mhir.optimize.StmInductionVarRemovalPass]]).
  *
  * @param s
  *   the original stream.
  * @param k
  *   the number of elements to skip.
  */
case class StmNextK(s: Expr /* Stm<A; n> */, k: Expr /* Int */ )(
    typ: Type = Missing
) extends SyntaxSugar(s, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, i) => StmNextK(s, i)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newK = this.k.tchk
    newK.typ match {
      case _: TyAnyInt => ()
      case t =>
        throw new TypeError(
          s"Index of ${StmNextK.getClass.getSimpleName} has type $t."
            + " Expected an integer."
        )
    }
    val newS = s.tchk
    newS.typ match {
      case TyStm(t, n) =>
        this.rebuild(TyStm(t, n - k), Seq(newS, newK))
      case t =>
        throw new TypeError(
          s"Stream of ${StmNextK.getClass.getSimpleName} has type $t."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    throw new RuntimeException(s"$className should not be lowered")
  }
}

/** Transformations that attempt to remove induction variables.
  *
  * Induction variables are accumulator elements which can be expressed as a
  * function of <code>t</code>, where <code>t</code> is a counter which starts
  * at zero and counts up by one.
  */
class StmInductionVarRemovalPass(facts: FactSet) {

  /** Remove as many induction variables as possible from the given stream.
    */
  def removeInductionVars(s: StmBuild): StmBuild = {
    val funcByIdx = findClosedForms(s)
    replaceInductionVars(s, funcByIdx)
  }

  /** Try to find the value of the given stream's accumulator at time
    * <code>c</code>.
    */
  def tryFindAccumulatorAtTime(
      s: StmBuild,
      c: Expr
  ): Option[Map[Param, Expr]] = {
    require(
      ReshapeData.canReshape(c.typ, I33),
      s"Unsupported type for time (expected something that can be reshaped to $I33, got ${c.typ})."
    )

    val closedFormByVar = findClosedForms(s)

    // Need the new seed to be synthesizable, so can't have any StmNextK's.
    // But we can get rid of StmNextK if k <= 0, so try to do that.
    def removeStmNextK(e: Expr): Expr = {
      e match {
        case StmNextK(s, k) =>
          val isKNonPositive =
            PE.isSmallerOrEqual(k, 0)(this.facts).getOrElse(false)
          if (isKNonPositive) s else e
        case e => e.rebuildAndEraseType(e.children.map(c => removeStmNextK(c)))
      }
    }

    val allElemsHaveClosedForm =
      s.accVars.forall(x => closedFormByVar.isDefinedAt(x))
    if (allElemsHaveClosedForm) {
      val seedsAtT = s.accVars
        .map(x => {
          val e1 =
            FunCall(closedFormByVar(x), ReshapeData(c, I33)())().tchk().lower()
          val e2 = removeStmNextK(e1)
          x -> PE.partialEval(e2)(this.facts)
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

  /** Try to find an expression for the <code>valid</code> expression of this
    * stream as a function of <code>t</code>.
    */
  def tryFindClosedFormForValid(s: StmBuild): Option[Function] = {
    val closedFormByVar = findClosedForms(s)
    /* Even though t >= 0, use a signed type to minimize conversions like
     * ToSigned and ToUnsigned (e.g., when computing t - t0).
     * Use at least 33 bits so that you can work with accumulators of type u32
     * without needing to pad `t`.
     */
    val t = Param("t")(I33)
    val subs: Map[Expr, Expr] =
      closedFormByVar.map({ case (x, f) => x -> FunCall(f, t)().tchk() })
    val valid = s.valid.subPreserveType(subs)
    val allVarsRemoved = valid.freeVars.intersect(s.accVars).isEmpty
    if (allVarsRemoved) Some(Function(t, valid)()) else None
  }

  private def findClosedForms(s: StmBuild): Map[Param, Function] = {
    if (s.typ == Missing) {
      throw new IllegalArgumentException(
        s"The stream should be type-checked before trying to remove induction variables."
      )
    }
    if (s.hasSyntaxSugar) {
      throw new IllegalArgumentException(
        s"The stream should be lowered before trying to remove induction variables."
      )
    }
    /* Even though t >= 0, use a signed type to minimize conversions like
     * ToSigned and ToUnsigned (e.g., when computing t - t0).
     * Use at least 33 bits so that you can work with accumulators of type u32
     * without needing to pad `t`.
     */
    val t = Param("t")(I33)
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
          closedFormByVar.map({ case (x, f) =>
            val funCall = FunCall(f, t)().tchk()
            val y = Param("y")(funCall.typ)
            val replacement =
              Cast(y, x.typ)().tchk().lower().subPreserveType(y -> funCall)
            x -> replacement
          })
        val equations =
          s.equations
            .filter({ case (x, _) => v.contains(x) })
            .map({ case (x, (z, next)) =>
              // Replace other accumulator vars by their closed form
              x -> (z, next.subPreserveType(subs))
            })
        if (v.size == 1) {
          // For simplicity of the recurrence-solving code, don't wrap this one
          // recurrence in a tuple
          val (x, (z, nextExpr)) = equations.head
          assert(x == v.head)
          assert(
            (nextExpr.freeVars.intersect(s.accVars) - x).isEmpty,
            "all dependencies should have been removed"
          )
          val next = Function(
            t,
            Function(
              x,
              PE.partialEval(nextExpr)(this.facts.geq(t, 0))
            )()
          )()
          RecurrenceSolver.tryFindClosedForm(0, z, next) match {
            case Some(f) =>
              val peF =
                PE.partialEval(f.tchk())(this.facts.geq(t, 0))
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
          val typedNext = next.tchk().asInstanceOf[Function]
          RecurrenceSolver.tryFindClosedForm(0, z.tchk(), typedNext) match {
            case Some(f) =>
              for ((i, x) <- paramByIndex) {
                val g = Function(
                  t,
                  PE.partialEval(
                    TupleAccess(FunCall(f, t)(), i)().tchk().lower()
                  )(this.facts.geq(t, 0))
                )()
                closedFormByVar += (x -> g)
              }
            case None => ()
          }
        }
      }
    }
    for ((x, f) <- closedFormByVar) {
      assert(
        !f.contains(e =>
          e.isInstanceOf[SyntaxSugar] && !e.isInstanceOf[StmNextK]
        ),
        s"there should be no syntax sugar in the closed forms (found some in the function for $x)"
      )
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
    }): _*)()
    val tupleType = TyTuple(
      (0 until equations.size).map(i => paramByIndex(i).typ): _*
    )
    val acc = Param("acc")(tupleType)
    val subs: Map[Expr, Expr] =
      paramByIndex.map({ case (i, x) => x -> TupleAccess(acc, i)().tchk() })
    val next = Function(
      t,
      Function(
        acc,
        Tuple((0 until equations.size).map(i => {
          val x = paramByIndex(i)
          equations(x)._2.subPreserveType(subs)
        }): _*)()
      )()
    )()
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
      /* Even though t >= 0, use a signed type to minimize conversions like
       * ToSigned and ToUnsigned (e.g., when computing t - t0).
       * Use at least 33 bits so that you can work with accumulators of type u32
       * without needing to pad `t`.
       */
      val t = Param("t")(I33)

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
        val newStm = {
          val replacement = {
            val funCall = FunCall(f, t)().tchk()
            val y = Param("y")(funCall.typ)
            Cast(y, x.typ)().tchk().lower().subPreserveType(y -> funCall)
          }
          val replaced = s.replaceVar(x, replacement)
          val pe = PE.partialEval(replaced)(this.facts.geq(t, 0))
          val movedUp = moveStmNextKUp(pe)
          movedUp.asInstanceOf[StmBuild]
        }
        assert(!newStm.freeVars.contains(f.param))
        if (!newStm.contains(classOf[StmNextK])) {
          s = newStm
        } else if (f.body.isInstanceOf[StmNextK]) {
          // No point in trying to replace a stream-valued expression with its
          // closed form
        } else {
          replaceStmNextK(newStm, t)(this.facts.geq(t, 0)) match {
            case Some((withoutStmNextK: StmBuild, newRecEqns)) =>
              assert(!withoutStmNextK.contains(classOf[StmNextK]))
              // TODO: Ensure that the input stream is still fully consumed?
              s = withoutStmNextK
              newAccVars ++= newRecEqns
            case _ => ()
          }
        }
      }

      // Add new elements as required (e.g., the counter for t, input streams
      // for elements whose closed form included StmNextK)
      for ((x, (z, Function(t, f))) <- newAccVars) {
        s = s.addAccumulator(x, z, FunCall(f, x)())
      }
      s = s.addAccumulator(t, C(0)(t.typ), Sum(t, C(1)(t.typ))())
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
  )(facts: FactSet): Option[(Expr, Map[Param, (Expr, Function)])] = {
    e match {
      case s: StmNextK =>
        val sChk = s.tchk().asInstanceOf[StmNextK]
        RecurrenceSolver.tryFindRecursiveForm(sChk, t)(facts) match {
          case Some((z, f)) =>
            val typ = sChk.typ.asInstanceOf[TyStm].t
            val r = Param("r")(TyStm(typ, -1))
            Some((r, Map(r -> (z, f))))
          case None => None
        }
      case e =>
        val childResults = e match {
          case VecBuild(n, f @ Function(i, body)) =>
            Seq(
              replaceStmNextK(n, t)(facts),
              replaceStmNextK(body, t)(facts.geq(i, 0).lt(i, n)).map({
                case (newBody, xs) =>
                  (f.rebuildAndEraseType(Seq(i, newBody)), xs)
              })
            )
          case Mux(c, tru, fals) =>
            Seq(
              replaceStmNextK(c, t)(facts),
              replaceStmNextK(tru, t)(facts.assumeTrue(c)),
              replaceStmNextK(fals, t)(facts.assumeFalse(c))
            )
          case e => e.children.map(c => replaceStmNextK(c, t)(facts))
        }
        if (childResults.exists(x => x.isEmpty)) {
          None
        } else {
          val unwrappedChildResults = childResults.map(x => x.get)
          val newChildren = unwrappedChildResults.map(x => x._1)
          val newAccElems = unwrappedChildResults
            .map(x => x._2)
            .foldLeft(Map[Param, (Expr, Function)]())(_ ++ _)
          Some((e.rebuildAndEraseType(newChildren), newAccElems))
        }
    }
  }

  /** Move [[StmNextK]] nodes towards the root of the AST.
    */
  private def moveStmNextKUp(e: Expr): Expr = {
    val result = e match {
      case Mux(c, t, f) =>
        val newC = moveStmNextKUp(c)
        val newT = moveStmNextKUp(t)
        val newF = moveStmNextKUp(f)
        (newT, newF) match {
          case (StmNextK(s0, k0), StmNextK(s1, k1)) if s0 == s1 =>
            StmNextK(s0, Mux(newC, k0, k1)())()
          case (StmData(StmNextK(s0, k0)), StmData(StmNextK(s1, k1)))
              if s0 == s1 =>
            StmData(StmNextK(s0, Mux(newC, k0, k1)())())()
          case _ =>
            Mux(newC, newT, newF)()
        }
      case e => e.map(moveStmNextKUp)
    }
    result.tchk()
  }
}

/** Factory for [[StmInductionVarRemovalPass]].
  */
object StmInductionVarRemovalPass {
  def apply(facts: FactSet = FactSet()): StmInductionVarRemovalPass = {
    new StmInductionVarRemovalPass(facts)
  }
}

private object RecurrenceSolver {

  def tryFindClosedForm(t0: Expr, z: Expr, next: Function): Option[Function] = {
    require(
      !z.contains(e =>
        e.isInstanceOf[SyntaxSugar] && !e.isInstanceOf[StmNextK]
      ),
      "Syntax sugar must be removed before a closed form can be found."
    )
    require(
      !next.contains(e =>
        e.isInstanceOf[SyntaxSugar] && !e.isInstanceOf[StmNextK]
      ),
      "Syntax sugar must be removed before a closed form can be found."
    )
    val peZ = PE.partialEval(z.tchk())
    val peNext = PE.partialEval(next.tchk()).asInstanceOf[Function]
    val closedForm = peZ.typ match {
      case _: TyStm      => tryFindStmClosedForm(peZ, peNext)
      case t if t.isData => tryFindDataClosedForm(t0, peZ, peNext)
      case _             => None
    }
    closedForm.foreach(f => {
      val typedF = f.tchk().asInstanceOf[Function]
      assert(
        !typedF.contains(e =>
          e.isInstanceOf[SyntaxSugar] && !e.isInstanceOf[StmNextK]
        ),
        s"closed form should not contain any syntax sugar (expression is $typedF)"
      )
      assert(
        typedF.typ ~= I33 ->: peZ.typ,
        s"Closed form must have the same type as the original (expected ${I33 ->: peZ.typ} but found ${typedF.typ})."
      )
    })
    closedForm
  }

  /** Attempt to convert a recurrence equation of the form
    * {{{
    *   x(t0) = z
    *   x(t + 1) = f(t, x(t)) for all t >= t0
    * }}}
    * to a function of <code>t</code>.
    */
  private def tryFindDataClosedForm(
      t0: Expr,
      z: Expr,
      next: Function
  ): Option[Function] = {
    val t = next.param

    /** The type of the accumulator */
    val typ = {
      require(
        z.typ != Missing,
        "Equation must be type checked before a closed form can be found."
      )
      require(
        !z.typ.isInstanceOf[TyStm],
        s"Input must not be a stream. Use tryFindStmClosedForm instead."
      )
      val nextTyp = next.typ.asInstanceOf[TyArrow].t2.asInstanceOf[TyArrow].t2
      require(
        z.typ ~= nextTyp,
        s"Initial value has type ${z.typ} but next function return type is $nextTyp."
      )
      z.typ
    }
    val result = (z, next) match {
      case (z, Function(t, Function(x0, x1))) if x0 == x1 =>
        // Identity
        Some(Function(t, z)())
      case (z, Function(t, Function(x, e))) if !e.contains(x) =>
        //     x_{t+1} = f(t) for t >= 0
        // ==> x_t = f(t - 1) for t >= 1
        Some(
          Function(
            t,
            Mux(
              t === 0,
              z,
              e.subPreserveType(t -> (t - 1).tchk().lower())
            )()
          )().tchk().lower()
        )
      case Counter(delta) =>
        Some(Function(t, Cast(z + (t - t0) * delta, typ)())().tchk().lower())
      case LeftShiftRegister(n, f, g) =>
        val fInT = f.tchk().typ.asInstanceOf[TyArrow].t1
        val gInT = g.tchk().typ.asInstanceOf[TyArrow].t1
        Some(
          Function(
            t,
            VecBuild(
              n,
              U32 ::+ (i =>
                Mux(
                  // Imagine the vector is a fixed, infinite tape and we're
                  // moving to the right as time goes along.
                  //   [f(0), f(1), ..., f(n - 1), g(0), g(1), ...]
                  ((t - t0 + i) < n).tchk().lower(),
                  FunCall(f, Cast(t - t0 + i, fInT)().tchk().lower())(),
                  FunCall(g, Cast(t - t0 + i - n, gInT)().tchk().lower())()
                )()
              )
            )()
          )().tchk().asInstanceOf[Function]
        )
      case Piecewise(k, f, g) =>
        tryFindClosedForm(t0, z, f) match {
          case Some(f) =>
            // The time at which we switch from one side of the piecewise function to the other
            val t1 = Max(k, t0)().tchk().lower()
            val valAtT1 = FunCall(f, t1)().tchk()
            tryFindClosedForm(t1, valAtT1, g) match {
              case Some(g) =>
                Some(
                  Function(
                    t,
                    Mux(
                      (t < t1).tchk().lower(),
                      FunCall(f, t)(),
                      FunCall(g, t)()
                    )()
                  )().tchk()
                )
              case None => None
            }
          case None => None
        }
      // TODO: Add similar cases (e.g., a counter that starts false and becomes true)?
      case (
            True,
            Function(t, Function(acc, and @ And(terms @ _*)))
          ) if terms.contains(acc) =>
        (t, acc, and.remove(acc)) match {
          case TimeLessThan(k) if !k.contains(acc) =>
            // Add one to account for the fact that the boolean will only
            // change value at the next cycle.
            // Use Max to account for the case where the condition is
            // immediately false: the value at t = 0 will nevertheless be True.
            Some(Function(t, t - t0 < C(1)() + Max(0, k)())().tchk().lower())
          case _ => None
        }
      case (
            Tuple(True, i0),
            Function(
              t,
              Function(
                acc,
                Tuple(
                  and @ And(terms @ _*),
                  boundedCtrUpdate @ Mux(
                    TupleAccess(a0, IntCst(0)),
                    ctrUpdateIfTrue,
                    TupleAccess(a1, IntCst(1))
                  )
                )
              )
            )
          ) if a0 == acc && a1 == acc && terms.contains(acc.__0) =>
        val bCond = and.remove(acc.__0)
        // (1) Find closed form for counter as if it was unbounded
        val a = Param("a")(acc.__1.tchk().typ)
        // TODO: what if there's overflow here?
        val ctrNext = Function(
          t,
          Function(a, ctrUpdateIfTrue.subPreserveType(acc.__1 -> a))()
        )()
        if (!ctrNext.contains(acc)) {
          tryFindClosedForm(t0, i0, ctrNext) match {
            case Some(f) =>
              // (2) Find closed form for the boolean if the counter was unbounded
              val bCondIfUnbounded = PE.partialEval(
                bCond.subPreserveType(
                  Map[Expr, Expr](
                    acc.__0 -> a,
                    acc.__1 -> FunCall(f, t)().tchk()
                  )
                )
              )
              if (!bCondIfUnbounded.contains(acc)) {
                (t, a, bCondIfUnbounded) match {
                  case TimeLessThan(k) =>
                    // The boolean is equivalent to t < K (need to account for the fact that the accumulator only
                    // changes at the next cycle and the condition may be false immediately)
                    val K = C(1)() + Max(t0, k)()
                    // (3) Now find closed form for the bounded counter
                    val boundedCtrNext = Function(
                      t,
                      Function(
                        a,
                        boundedCtrUpdate.subPreserveType(
                          Map[Expr, Expr](
                            acc.__0 -> (t < K).tchk().lower(),
                            acc.__1 -> a
                          )
                        )
                      )()
                    )().tchk().lower().asInstanceOf[Function]
                    // TODO: Call tryFindClosedForm instead (this will
                    //       partially evaluate the next function, which
                    //       apparently breaks the pattern matching)
                    tryFindDataClosedForm(t0, i0, boundedCtrNext) match {
                      case Some(h) =>
                        Some(
                          Function(t, Tuple(t < K, FunCall(h, t)())())()
                            .tchk()
                            .lower()
                        )
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
    result.map(f => {
      val peF = PE.partialEval(f.tchk()).asInstanceOf[Function]
      assert(
        peF.typ == I33 ->: typ,
        s"Closed form must have the same type as the original (expected ${I33 ->: typ} but found ${peF.typ})."
      )
      assert(
        !peF.contains(e =>
          e.isInstanceOf[SyntaxSugar] && !e.isInstanceOf[StmNextK]
        ),
        s"closed form should not contain any syntax sugar (expression is $peF)"
      )
      peF
    })
  }

  private def tryFindStmClosedForm(
      z: Expr,
      next: Function
  ): Option[Function] = {
    require(
      z.typ.isInstanceOf[TyStm],
      s"Input must be a stream. Use tryFindDataClosedForm instead."
    )
    val nextIdx = next match {
      case Function(t, Function(x, body)) if !body.contains(x) =>
        val i = Param("i")(I33)
        Function(t, Function(i, Mux(body, i + 1, i)())())()
      case _ =>
        throw new IllegalArgumentException(
          s"Invalid update function: $next."
            + " Expected a function of two inputs where the second input is unused."
        )
    }
    val peNextIdx =
      PE.partialEval(nextIdx.tchk().lower()).asInstanceOf[Function]
    tryFindClosedForm(t0 = C(0)(I33), z = C(0)(I33), next = peNextIdx) match {
      case None                 => None
      case Some(Function(t, i)) => Some(Function(t, StmNextK(z, i)())())
    }
  }

  /** Attempt to convert a <code>StmNextK</code> into a recurrence equation
    * without any <code>StmNextK</code>. The recurrence equation will be a
    * function of the form <code>f(t, x(t))</code>.
    */
  def tryFindRecursiveForm(
      nxt: StmNextK,
      t: Param
  )(fs: FactSet): Option[(Expr, Function)] = {
    nxt.typ match {
      case Missing =>
        throw new IllegalArgumentException(
          "Expression must be type checked before a recursive form can be found."
        )
      case _ => ()
    }
    val facts = fs.geq(t, 0)

    def findRec(e: Expr, t0: Expr): Option[(Expr, Function)] = {
      assert(t0.typ == I33, s"t0 should be an $I33")
      e match {
        case mux @ Mux(c, lhs, rhs) =>
          (t, Param("p")(), c) match {
            case TimeLessThan(k) =>
              findRec(lhs, t0) match {
                case Some((z0, f0)) =>
                  findRec(rhs, k) match {
                    case Some((z1, f1)) =>
                      val expectedZ1 = lhs.subPreserveType(t -> k.tchk())
                      val isContinuous =
                        PE.isEqual(
                          z1.tchk().lower(),
                          expectedZ1.tchk().lower()
                        )(facts)
                          .getOrElse(false)
                      if (isContinuous) {
                        val f =
                          Function(
                            t,
                            mux.typ ::+ (x =>
                              Mux(
                                c,
                                FunCall(FunCall(f0, t)(), x)(),
                                FunCall(FunCall(f1, t)(), x)()
                              )()
                            )
                          )()
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
          val nextK = k.subPreserveType(t -> (t + 1).tchk().lower())
          val deltaK = PE.partialEval((nextK - k).tchk().lower())(facts)
          val isDeltaZeroOrOne = (
            PE.isGreaterOrEqual(deltaK, 0)(facts).getOrElse(false)
              && PE
                .isSmallerOrEqual(deltaK, 1)(facts)
                .getOrElse(false)
          )
          if (isDeltaZeroOrOne) {
            val z = next.subPreserveType(t -> t0.tchk())
            val acc = Param("acc")(next.typ)
            val nextF =
              Function(t, Function(acc, (deltaK !== 0) && (k >= 0))())()
                .tchk()
                .lower()
                .asInstanceOf[Function]
            Some((z, nextF))
          } else {
            None
          }
        case _ => None
      }
    }

    val e = PE.partialEval(MuxMover.moveUp(nxt))(facts).tchk()
    findRec(e, t0 = C(0)(I33)) match {
      case Some((z_, f_)) =>
        val z = PE.partialEval(z_)(facts)
        // Start reading the stream from the beginning: can't start from the middle
        // TODO: Is this condition really necessary? Maybe I can get away with
        //       just checking that the new z is valid (e.g., it doesn't
        //       contain StmNextK, it's not DontCare)
        val initOk = z match {
          case s if s == nxt.s => true
          case StmNextK(s, k) =>
            (s == nxt.s
            && PE.isSmallerOrEqual(k, 0)(facts).getOrElse(false))
          case _ => false
        }
        if (initOk) {
          val f = PE.partialEval(f_)(facts)
          Some((nxt.s, f.asInstanceOf[Function]))
        } else {
          None
        }
      case _ => None
    }
  }
}

private object Counter {

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
      case Function(t, Function(acc, Sum(terms @ _*))) =>
        val termsWithAcc = terms.filter(e => e.contains(acc))
        termsWithAcc match {
          case Seq(a) if a == acc =>
            val otherTerms = terms.diff(termsWithAcc)
            val delta = Sum(otherTerms: _*)()
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

private object LeftShiftRegister {

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
    val (peZ, peNext) =
      (PE.partialEval(z), PE.partialEval(next))
    // Partially evaluate because the initial value may be a function call that
    // ends up returning a vector, for example
    (peZ, peNext) match {
      case (
            VecBuild(n0, f),
            Function(
              t,
              Function(
                acc,
                VecBuild(
                  n1,
                  Function(
                    i0: Param,
                    body
                  )
                )
              )
            )
          ) if PE.isEqual(n1, n0)().getOrElse(false) =>
        def checkBody(body: Expr): Option[Expr] = {
          body match {
            case PadTo(body, w) => checkBody(body).map(PadTo(_, w)().tchk())
            case TruncateTo(body, w) =>
              checkBody(body).map(TruncateTo(_, w)().tchk())
            case ToUnsigned(body) => checkBody(body).map(ToUnsigned(_)().tchk())
            case ToSigned(body)   => checkBody(body).map(ToSigned(_)().tchk())
            case Mux(lastIdxCond, e /* may have t as free variable */, va)
                if !e.contains(acc) =>
              val condOk = lastIdxCond match {
                case Equal(Sum(IntCst(1), i2: Param), n2)
                    if i2 == i0 && PE.isEqual(n2, n0)().getOrElse(false) =>
                  true
                case Equal(i2: Param, n2)
                    if i2 == i0
                      && PE
                        .isEqual((n2 + 1).tchk().lower(), n0)()
                        .getOrElse(false) =>
                  true
                case _ =>
                  false
              }
              val vecAccessOk = checkVecAccess(va)
              if (condOk && vecAccessOk) {
                Some(e)
              } else {
                None
              }
            case _ =>
              None
          }
        }
        @tailrec
        def checkVecAccess(va: Expr): Boolean = {
          va match {
            case PadTo(va, _)      => checkVecAccess(va)
            case TruncateTo(va, _) => checkVecAccess(va)
            case ToUnsigned(va)    => checkVecAccess(va)
            case ToSigned(va)      => checkVecAccess(va)
            case VecAccess(a2, Sum(IntCst(1), i1)) if a2 == acc && i1 == i0 =>
              true
          }
        }
        checkBody(body) match {
          case Some(e) => Some((n0, f, Function(t, e)()))
          case None    => None
        }
      case (
            Undefined(TyVec(typ, n0)),
            Function(
              t,
              Function(
                acc,
                VecBuild(
                  n1,
                  Function(
                    i0: Param,
                    Mux(
                      lastIdxCond,
                      e /* may have t as free variable */,
                      VecAccess(a2, Sum(IntCst(1), i1))
                    )
                  )
                )
              )
            )
          )
          if a2 == acc && i1 == i0 && !e.contains(acc)
            && PE.isEqual(n1, n0)().getOrElse(false) =>
        lastIdxCond match {
          case Equal(Sum(IntCst(1), i2: Param), n2)
              if i2 == i0 && PE.isEqual(n2, n0)().getOrElse(false) =>
            // TODO: Take advantage somehow of the fact that the initial value
            //       is undefined?
            Some((n0, U32 ::+ (_ => Default(typ).lower()), Function(t, e)()))
          case Equal(i2: Param, n2)
              if i2 == i0
                && PE.isEqual((n2 + 1).tchk().lower(), n0)().getOrElse(false) =>
            Some((n0, U32 ::+ (_ => Default(typ).lower()), Function(t, e)()))
          case _ => None
        }
      case _ => None
    }
  }
}

private object Piecewise {

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
              (
                k,
                Function(t, Function(acc, a)())(),
                Function(t, Function(acc, b)())()
              )
            )
          case _ => None
        }
      case _ => None
    }
  }
}

private object TimeLessThan {
  def unapply(args: (Param, Param, Expr)): Option[Expr] = {
    val (t, acc, lt) = args
    (t, acc, Mux(lt, True, False)()) match {
      case IfTimeLessThan(k, True, False) => Some(k)
      case _                              => None
    }
  }
}

private object IfTimeLessThan {

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

private object IfLessThan {

  /** An expression of the form <code>if (x &lt k) then a else b</code>, where
    * <code>k</code> does not depend on <code>x</code>
    *
    * @return
    *   <code>(k, a, b)</code>
    */
  def unapply(args: (Expr, Expr)): Option[(Expr, Expr, Expr)] = {
    val (e, x) = args
    e match {
      case Mux(LessThan(y, z), a, b) =>
        // Look at y - z to deal with things like 10 + x < 20, 5 < x, etc.
        // Just matching LessThan(x, k) doesn't handle those cases.
        (PE.partialEval((y - z).tchk().lower()), x) match {
          case LinearFunctionOf(c0, c1) =>
            // TODO: Use a more general approach to find the sign of a particular expression?
            c1 match {
              case IntCst(c1) if c1 > 0 =>
                //     c0 + c1*x < 0
                // iff c1*x < -c0
                // iff x < ceil(-c0 / c1)  (since we're dealing with integers)
                Some((CeilDiv(C(-1)() * c0, C(c1)())(), a, b))
              case IntCst(c1) if c1 < 0 =>
                //     c0 + c1*x < 0
                // iff c1*x < -c0
                // iff x > ceil(-c0 / c1)  (sign flips because c1 < 0)
                // iff x >= 1 + ceil(-c0 / c1)
                // TODO: what if there's overflow?
                Some((C(1)() + CeilDiv(C(-1)() * c0, C(c1)())(), b, a))
              case _ => None
            }
          case _ => None
        }
      case _ => None
    }
  }
}

private object LinearFunctionOf {

  /** An expression of the form <code>c0 + c1*x</code>, where neither
    * <code>c0</code> nor <code>c1</code> depend on <code>x</code>.
    *
    * @return
    *   <code>(c0, c1)</code>
    */
  def unapply(args: (Expr, Expr)): Option[(Expr, Expr)] = {
    val (e, x) = args
    e match {
      case Sum(terms @ _*) =>
        val termsWithX = terms.filter(e => e.contains(x))
        termsWithX match {
          case Seq(termWithX) =>
            (termWithX, x) match {
              case ConstMultipleOf(c1) =>
                val termsWithoutX = terms.diff(termsWithX)
                val c0 = Sum(termsWithoutX: _*)()
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

private object ConstMultipleOf {

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
      case Prod(factors @ _*) =>
        val factorsWithX = factors.filter(e => e.contains(x))
        factorsWithX match {
          case Seq(y) if y == x =>
            val factorsWithoutX = factors.diff(factorsWithX)
            val coeff = factorsWithoutX match {
              case Seq(e) => e
              case xs     => Prod(xs: _*)()
            }
            Some(coeff)
          case _ => None
        }
      case _ => None
    }
  }
}
