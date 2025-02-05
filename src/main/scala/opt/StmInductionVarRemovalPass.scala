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
    val s = StmCanonPass.canonicalize(stm)
    val inductionVarByIdx: Map[Int, Function] =
      s.seed
        .asInstanceOf[Tuple]
        .elems
        .indices
        .flatMap(i => tryGetInductionVarByIdx(s, i).map(f => i -> f))
        .toMap
    removeInductionVars(s, inductionVarByIdx)
  }

  /** If all induction variables can be removed, then do so. But if any
    * induction variables cannot be removed, return <code>None</code>.
    */
  def tryRemoveAllInductionVars(stm: StmBuild): Option[StmBuild] = {
    val s = StmCanonPass.canonicalize(stm)
    val maybeInductionVars =
      s.seed
        .asInstanceOf[Tuple]
        .elems
        .indices
        .map(i => tryGetInductionVarByIdx(s, i).map(f => i -> f))
    if (maybeInductionVars.forall(x => x.isDefined)) {
      val inductionVarByIdx = maybeInductionVars.map(x => x.get).toMap
      Some(removeInductionVars(s, inductionVarByIdx))
    } else {
      None
    }
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
          Function(acc, ir.substitute(s1.nextF.body)(subs))
        )
      )

      val indicesToRemove = funByIdx.keySet.toSeq
      StmUtils.removeAccumulatorElemsByIndex(s2, indicesToRemove)
    }
  }

  /** Attempt to express the accumulator element at index `i` as a function of a
    * simple up-counter.
    *
    * @param s
    *   Stream
    * @param i
    *   Index
    * @return
    *   A function that returns the value of the `i`-th accumulator element as a
    *   function of a simple up-counter.
    */
  private def tryGetInductionVarByIdx(
      s: StmBuild,
      i: Int
  ): Option[Function] = {
    val seed = s.seed.asInstanceOf[Tuple]
    val z = seed.elems(i)
    val next = PartialEvalPass.partialEval(TupleAccess(s.nextF.body.__0, i))
    (z, next, s, i) match {
      case Counter(z, delta) => Some((t: Expr) => z + t * delta)
      case MonotonicBool(z, t0) =>
        if (z) Some((t: Expr) => t < t0) else Some((t: Expr) => t >= t0)
      // TODO: watch out for infinite loops due to circular dependencies!
      case LeftShiftRegister(n, f, e) =>
        tryGetInductionVar(s, e) match {
          case Some(g) =>
            Some((t: Expr) =>
              VecBuild(
                n,
                (i: Expr) =>
                  IfThenElse(
                    (t + i) < n,
                    FunCall(f, t + i),
                    FunCall(g, t + i - n)
                  )
              )
            )
          case None => None
        }
      case _ => None
    }
  }

  /** Attempt to express the given expression as a function of a simple
    * up-counter.
    *
    * @param s
    *   Stream
    * @param e
    *   Expression
    * @return
    *   A function that returns the value of the `i`-th accumulator element as a
    *   function of a simple up-counter.
    */
  private def tryGetInductionVar(s: StmBuild, e: Expr): Option[Function] = {
    val acc = s.nextF.param
    e match {
      case TupleAccess(a, IntCst(i)) if a == acc =>
        tryGetInductionVarByIdx(s, i)
      case TupleAccess(t, i) =>
        (tryGetInductionVar(s, t), tryGetInductionVar(s, i)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => TupleAccess(FunCall(f, t), FunCall(g, t)))
          case _ => None
        }
      case Tuple(elems @ _*) =>
        val elemFunctions = elems.map(e => tryGetInductionVar(s, e))
        if (elemFunctions.exists(e => e.isEmpty)) {
          None
        } else {
          Some((t: Expr) =>
            Tuple(elemFunctions.map(f => FunCall(f.get, t)): _*)
          )
        }
      case p: Param if p == acc =>
        // Maybe I could try getting *all* accumulator elements as induction variables
        None
      case p: Param => Some((_: Expr) => p)
      case Function(p, body) =>
        tryGetInductionVar(s, body) match {
          case Some(f) => Some((t: Expr) => Function(p, FunCall(f, t)))
          case _       => None
        }
      case FunCall(f, x) =>
        (tryGetInductionVar(s, f), tryGetInductionVar(s, x)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => FunCall(FunCall(f, t), FunCall(g, t)))
          case _ => None
        }
      case IntCst(n) => Some((_: Expr) => IntCst(n))
      case Sum(terms) =>
        val indVars = terms.map(e => tryGetInductionVar(s, e))
        if (indVars.forall(e => e.isDefined)) {
          val unwrappedVars = indVars.map(e => e.get)
          Some((t: Expr) => Sum(unwrappedVars.map(f => FunCall(f, t)): _*))
        } else {
          None
        }
      case Prod(factors) =>
        val indVars = factors.map(e => tryGetInductionVar(s, e))
        if (indVars.forall(e => e.isDefined)) {
          val unwrappedVars = indVars.map(e => e.get)
          Some((t: Expr) => Prod(unwrappedVars.map(f => FunCall(f, t)): _*))
        } else {
          None
        }
      case Div(x, y) =>
        (tryGetInductionVar(s, x), tryGetInductionVar(s, y)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => FunCall(f, t) / FunCall(g, t))
          case _ => None
        }
      case Mod(x, y) =>
        (tryGetInductionVar(s, x), tryGetInductionVar(s, y)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => FunCall(f, t) % FunCall(g, t))
          case _ => None
        }
      case True  => Some((_: Expr) => True)
      case False => Some((_: Expr) => False)
      case IfThenElse(c, t, f) =>
        (
          tryGetInductionVar(s, c),
          tryGetInductionVar(s, t),
          tryGetInductionVar(s, f)
        ) match {
          case (Some(f), Some(g), Some(h)) =>
            Some((t: Expr) =>
              IfThenElse(FunCall(f, t), FunCall(g, t), FunCall(h, t))
            )
          case _ => None
        }
      case Equal(x, y) =>
        (tryGetInductionVar(s, x), tryGetInductionVar(s, y)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => FunCall(f, t) === FunCall(g, t))
          case _ => None
        }
      case LessThan(x, y) =>
        (tryGetInductionVar(s, x), tryGetInductionVar(s, y)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => FunCall(f, t) < FunCall(g, t))
          case _ => None
        }
      case Not(x) =>
        tryGetInductionVar(s, x) match {
          case Some(f) => Some((t: Expr) => Not(FunCall(f, t)))
          case _       => None
        }
      case And(x, y) =>
        (tryGetInductionVar(s, x), tryGetInductionVar(s, y)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => FunCall(f, t) && FunCall(g, t))
          case _ => None
        }
      case Or(x, y) =>
        (tryGetInductionVar(s, x), tryGetInductionVar(s, y)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => FunCall(f, t) || FunCall(g, t))
          case _ => None
        }
      case DontCare     => Some((_: Expr) => DontCare)
      case _: StmBuild  => None
      case _: StmNext   => None
      case _: StmLength => None
      case VecBuild(n, f) =>
        (tryGetInductionVar(s, n), tryGetInductionVar(s, f)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => VecBuild(FunCall(f, t), FunCall(g, t)))
          case _ => None
        }
      case VecAccess(v, i) =>
        (tryGetInductionVar(s, v), tryGetInductionVar(s, i)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => VecAccess(FunCall(f, t), FunCall(g, t)))
          case _ => None
        }
      case VecLength(v) =>
        tryGetInductionVar(s, v) match {
          case Some(f) => Some((t: Expr) => VecLength(FunCall(f, t)))
          case _       => None
        }
    }
  }
}

object Counter {

  /** A counter starting at <code>z</code> and changing by <code>delta</code> at
    * each step, where <code>delta</code> is constant within the stream (i.e.,
    * does not depend on the accumulator).
    *
    * @return
    *   <code>Some((z, delta))</code> if this is a counter, otherwise
    *   <code>None</code>.
    */
  def unapply(args: (Expr, Expr, StmBuild, Int)): Option[(Expr, Expr)] = {
    val (z, next, stm, i) = args
    val acc = stm.nextF.param
    (z, next) match {
      // TODO: Possibly need to check that delta has no side effects
      // Constant
      case (z, TupleAccess(a0, IntCst(i0))) if a0 == acc && i0 == i =>
        Some((z, IntCst(0)))
      // Counter
      case (z, Sum(terms)) =>
        // Try to find the accumulator
        val j = terms.zipWithIndex.foldLeft[Option[Int]](None)({
          case (Some(j), _) => Some(j)
          case (None, (TupleAccess(a0, IntCst(i0)), j))
              if a0 == acc && i0 == i =>
            Some(j)
          case _ => None
        })
        j match {
          case None => None
          case Some(j) =>
            val otherTerms = terms.zipWithIndex
              .filter({ case (_, k) => k != j })
              .map({ case (e, _) => e })
            val delta = Sum(otherTerms: _*)
            val isConstantInStream = !ir.contains(delta, acc)
            if (isConstantInStream) {
              Some((z, delta))
            } else {
              None
            }
        }
      case _ => None
    }
  }
}

object MonotonicBool {

  /** A boolean that is <code>True</code> iff <code>t &lt; t0</code>, or one
    * which is <code>False</code> iff <code>t &lt; t0</code>. Let <code>z</code>
    * be the initial value of the boolean.
    *
    * @return
    *   <code>Some((z, t0))</code> if this is a monotonic boolean, otherwise
    *   <code>None</code>.
    */
  def unapply(args: (Expr, Expr, StmBuild, Int)): Option[(Boolean, Expr)] = {
    val (z, next, stm, i) = args
    val acc = stm.nextF.param
    (z, next) match {
      // TODO: There are many more cases to handle
      // True --> False, up counter
      case (
            True,
            And(
              TupleAccess(a0, IntCst(i0)),
              LessThan(TupleAccess(a1, IntCst(j)), k)
            )
          ) if a0 == acc && a1 == acc && i0 == i =>
        tryGetMonotonicCounter(stm, i, j) match {
          case Some((i0, delta)) if delta > 0 =>
            val t0 = CeilDiv(Max(0, k - i0), delta) + 1
            Some((true, t0))
          case _ => None
        }
      case _ => None
    }
  }

  /** @param s
    *   Stream.
    * @param i
    *   Index of a boolean that depends on this accumulator.
    * @param j
    *   Index of the counter.
    * @return
    *   `Some(z, delta)` if this is a counter, otherwise `None`
    */
  private def tryGetMonotonicCounter(
      s: StmBuild,
      i: Int,
      j: Int
  ): Option[(Expr, Int)] = {
    val cntrInit = s.seed.asInstanceOf[Tuple].elems(j)
    val cntrUpdateExpr =
      PartialEvalPass.partialEval(
        TupleAccess(s.nextF.body.__0, j)
      )
    val acc = s.nextF.param
    cntrUpdateExpr match {
      case IfThenElse(
            TupleAccess(p0, i0),
            Sum(Seq(IntCst(delta), TupleAccess(p1, i1))),
            TupleAccess(p2, i2)
          )
          if p0 == acc && i0 == IntCst(
            i
          ) && p1 == acc && i1 == IntCst(
            j
          ) && p2 == acc && i2 == IntCst(j) =>
        Some((cntrInit, delta))
      case Sum(Seq(IntCst(delta), TupleAccess(p0, i0)))
          if p0 == acc && i0 == IntCst(j) =>
        Some((cntrInit, delta))
      case _ => None
    }
  }
}

object LeftShiftRegister {

  /** A leftwards-shifting shift register (i.e., a <code>VecShiftLeft</code>) of
    * size <code>n</code>, whose initial values are given by the function
    * <code>f</code>, and whose new values are given by the expression
    * <code>e</code>.
    *
    * @return
    *   <code>Some((n, f, e))</code> if the given expression is a left shift
    *   register, otherwise <code>None</code>.
    */
  def unapply(
      args: (Expr, Expr, StmBuild, Int)
  ): Option[(Expr, Expr, Expr)] = {
    // TODO: Do I need to watch out for side effects and make sure `e` doesn't depend on `acc` and so on here too?
    val (z, next, stm, i) = args
    val acc = stm.nextF.param
    (z, next) match {
      case (
            VecBuild(n, f),
            VecBuild(
              VecLength(TupleAccess(a0, IntCst(i0))),
              Function(
                j0: Param,
                IfThenElse(
                  Equal(
                    j1,
                    // TODO: What if the VecLength has been partially evaluated to a constant?
                    Sum(Seq(IntCst(-1), VecLength(TupleAccess(a1, IntCst(i1)))))
                  ),
                  e,
                  VecAccess(
                    TupleAccess(a2, IntCst(i2)),
                    Sum(Seq(IntCst(1), j2))
                  )
                )
              )
            )
          )
          if a0 == acc && a1 == acc && a2 == acc && i0 == i && i1 == i && i2 == i && j1 == j0 && j2 == j0 =>
        Some((n, f, e))
      case _ => None
    }
  }
}
