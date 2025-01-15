package opt

import ir._
import operations.{Max, CeilDiv}

object StmInductionVarRemovalPass {
  def removeInductionVars(stm: StmBuild): StmBuild = {
    val s = StmCanonPass.canonicalize(stm)
    val seed = s.seed.asInstanceOf[Tuple]
    val inductionVarByIdx: Map[Int, Function] =
      seed.elems.indices
        .flatMap(i => tryGetInductionVarByIdx(s, i).map(f => i -> f))
        .toMap
    if (inductionVarByIdx.isEmpty) {
      s
    } else {
      // TODO: it's a bit sketchy that partial evaluation is required here, isn't it?
      val s1 = PartialEvalPass
        .partialEval(
          StmUtils.appendAccumulator(s, 0, (i: Expr) => i + 1)
        )
        .asInstanceOf[StmBuild]

      val acc = s1.nextF.param
      val subs: Map[Expr, Expr] = inductionVarByIdx
        .map({ case (i, f) => TupleAccess(acc.__0, i) -> FunCall(f, acc.__1) })
      // Canonicalization is required for removing accumulator elements
      val s2 = StmCanonPass.canonicalize(
        StmBuild(
          s1.length,
          s1.seed,
          Function(acc, ir.substitute(s1.nextF.body)(subs))
        )
      )

      val indicesToRemove = inductionVarByIdx.keySet.toSeq
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
  private def tryGetInductionVarByIdx(s: StmBuild, i: Int): Option[Function] = {
    val seed = s.seed.asInstanceOf[Tuple]
    val z = seed.elems(i)
    val acc = s.nextF.param
    val next = PartialEvalPass.partialEval(TupleAccess(s.nextF.body.__0, i))
    (z, next) match {
      // TODO: Why did I specifically restrict delta to be an int constant at first? Are there potential problems for
      //       other kinds of expressions? Do I need to check that it's side effect-free (e.g., no `StmNext`)?
      // Constant
      case (z, TupleAccess(a0, IntCst(i0))) if a0 == acc && i0 == i =>
        Some((t: Expr) => z)
      // Up counter
      case (z, Add(TupleAccess(a0, IntCst(i0)), delta))
          if a0 == acc && i0 == i =>
        Some((t: Expr) => z + t * delta)
      // Down counter
      case (z, Sub(TupleAccess(a0, IntCst(i0)), delta))
          if a0 == acc && i0 == i =>
        Some((t: Expr) => z - t * delta)
      // Monotonic bool (True --> False, up counter)
      case (
            True,
            And(
              TupleAccess(a0, IntCst(i0)),
              LessThan(TupleAccess(a1, IntCst(j)), k)
            )
          ) if a0 == acc && a1 == acc && i0 == i =>
        tryGetMonotonicCounter(s, i, j) match {
          case Some((i0, delta)) if delta > 0 =>
            Some((t: Expr) => t < (CeilDiv(Max(0, k - i0), delta) + 1))
          case _ => None
        }
      // VecShiftLeft
      // TODO: watch out for infinite loops due to circular dependencies!
      case (
            VecBuild(n, f),
            VecBuild(
              VecLength(TupleAccess(a0, IntCst(i0))),
              Function(
                j0: Param,
                IfThenElse(
                  Equal(
                    j1,
                    Sub(VecLength(TupleAccess(a1, IntCst(i1))), IntCst(1))
                  ),
                  e,
                  VecAccess(TupleAccess(a2, IntCst(i2)), Add(j2, IntCst(1)))
                )
              )
            )
          )
          if a0 == acc && a1 == acc && a2 == acc && i0 == i && i1 == i && i2 == i && j1 == j0 && j2 == j0 =>
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
      // Unknown
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
      case Tuple(elems @ _*) =>
        val elemFunctions = elems.map(e => tryGetInductionVar(s, e))
        if (elemFunctions.exists(e => e.isEmpty)) {
          None
        } else {
          Some((t: Expr) =>
            Tuple(elemFunctions.map(f => FunCall(f.get, t)): _*)
          )
        }
      case TupleAccess(t, i) =>
        (tryGetInductionVar(s, t), tryGetInductionVar(s, i)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => TupleAccess(FunCall(f, t), FunCall(g, t)))
          case _ => None
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
      case Add(x, y) =>
        (tryGetInductionVar(s, x), tryGetInductionVar(s, y)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => FunCall(f, t) + FunCall(g, t))
          case _ => None
        }
      case Sub(x, y) =>
        (tryGetInductionVar(s, x), tryGetInductionVar(s, y)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => FunCall(f, t) - FunCall(g, t))
          case _ => None
        }
      case Mul(x, y) =>
        (tryGetInductionVar(s, x), tryGetInductionVar(s, y)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => FunCall(f, t) * FunCall(g, t))
          case _ => None
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
      case NotEqual(x, y) =>
        (tryGetInductionVar(s, x), tryGetInductionVar(s, y)) match {
          case (Some(f), Some(g)) =>
            Some((t: Expr) => FunCall(f, t) !== FunCall(g, t))
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
            Add(TupleAccess(p1, i1), IntCst(delta)),
            TupleAccess(p2, i2)
          )
          if p0 == acc && i0 == IntCst(
            i
          ) && p1 == acc && i1 == IntCst(
            j
          ) && p2 == acc && i2 == IntCst(j) =>
        Some((cntrInit, delta))
      case Add(TupleAccess(p0, i0), IntCst(delta))
          if p0 == acc && i0 == IntCst(j) =>
        Some((cntrInit, delta))
      case _ => None
    }
  }
}
