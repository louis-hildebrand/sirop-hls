package opt

import ir._

import scala.annotation.tailrec

sealed trait IfThenElseMotion
case object MoveUp extends IfThenElseMotion
case object HeuristicMotion extends IfThenElseMotion

object PartialEvalPass {
  def partialEval(
      e: Expr
  )(implicit
      facts: FactSet = FactSet(),
      m: IfThenElseMotion = HeuristicMotion
  ): Expr = {
    val pe = e match {
      case x: Param => x
      case Function(x, body) =>
        Function(x, partialEval(body)(facts.clearRange(x), m))()
      case FunCall(f: Expr, arg: Expr) =>
        partialEval(f) match {
          case Function(x, body) =>
            val a = partialEval(arg)
            partialEval(body.substitute(x -> a))
          case f => FunCall(f, partialEval(arg))()
        }

      case IntCst(_) => e
      case Sum(terms @ _*) =>
        val newChildren = terms.map(e => partialEval(e))
        m match {
          case MoveUp =>
            mergeIfThenElses(newChildren, x => y => x + y) match {
              case ite: IfThenElse => partialEval(ite)
              case e => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case HeuristicMotion =>
            ArithSimplifier.simplifyArithmetic(e.rebuild(newChildren))(facts)
        }
      case Prod(factors @ _*) =>
        val newChildren = factors.map(e => partialEval(e))
        m match {
          case MoveUp =>
            mergeIfThenElses(newChildren, x => y => x * y) match {
              case ite: IfThenElse => partialEval(ite)
              case e => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case HeuristicMotion =>
            ArithSimplifier.simplifyArithmetic(e.rebuild(newChildren))(facts)
        }
      case _: Div =>
        val newChildren = e.children.map(e => partialEval(e))
        m match {
          case MoveUp =>
            mergeIfThenElses(newChildren, x => y => x / y) match {
              case ite: IfThenElse => partialEval(ite)
              case e => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case HeuristicMotion =>
            ArithSimplifier.simplifyArithmetic(e.rebuild(newChildren))(facts)
        }
      case _: Mod =>
        val newChildren = e.children.map(e => partialEval(e))
        m match {
          case MoveUp =>
            mergeIfThenElses(newChildren, x => y => x % y) match {
              case ite: IfThenElse => partialEval(ite)
              case e => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case HeuristicMotion =>
            ArithSimplifier.simplifyArithmetic(e.rebuild(newChildren))(facts)
        }

      case True  => True
      case False => False
      case IfThenElse(c, t, f) =>
        val peIfThenElse =
          IfThenElse(partialEval(c), partialEval(t), partialEval(f))()
        ArithSimplifier.simplifyArithmetic(peIfThenElse)(facts) match {
          case IfThenElse(cond, trueE, falseE) =>
            partialEval(cond) match {
              case True  => partialEval(trueE)
              case False => partialEval(falseE)
              case cond =>
                val t = {
                  val newFacts = facts.isTrue(cond)
                  val t = splitAnd(cond).foldLeft(trueE)({ case (acc, c) =>
                    // TODO: Ideally, this canonicalization of moving everything
                    //       to the left would happen in general, not just here.
                    //       At the moment, this is also quite brittle because
                    //       it only works one way:
                    //         * The expression
                    //               if (x < y) then
                    //                 if (x - y < 0) then e1 else e2
                    //               else e3
                    //           should be successfully simplified
                    //         * The expression
                    //               if (x - y < 0) then
                    //                 if (x < y) then e1 else e2
                    //               else e3
                    //           may not :(
                    //       Unfortunately, ArithExpr works better with x < y
                    //       than x - y < 0 (at least as of 2025-03-12).
                    val condVariants = c match {
                      case LessThan(x, y) => Seq(c, LessThan(x - y, 0)())
                      case _              => Seq(c)
                    }
                    val subs = condVariants.map(c => c -> True).toMap
                    acc.substitute(subs)
                  })
                  partialEval(t)(newFacts, m)
                }
                val f = {
                  val newFacts = facts.isFalse(cond)
                  val f = splitOr(cond).foldLeft(falseE)({ case (acc, c) =>
                    // TODO: See TODO comment above (ideally do this
                    //       canonicalization everywhere, not just here)
                    val condVariants = c match {
                      case LessThan(x, y) => Seq(c, LessThan(x - y, 0)())
                      case _              => Seq(c)
                    }
                    val subs = condVariants.map(c => c -> False).toMap
                    acc.substitute(subs)
                  })
                  partialEval(f)(newFacts, m)
                }
                (t, f) match {
                  case _ if t == f   => t
                  case (True, False) => cond
                  case (False, True) => partialEval(Not(cond)())
                  case (StmNextK(s0, k0), StmNextK(s1, k1)) if s0 == s1 =>
                    partialEval(StmNextK(s0, IfThenElse(cond, k0, k1)())())
                  case _ =>
                    cond match {
                      // True branch is special case of false branch
                      case Equal(p: Param, r)
                          if partialEval(f.substitute(p -> r)) == t =>
                        f
                      // False branch is special case of true branch
                      case Not(Equal(p: Param, r))
                          if partialEval(t.substitute(p -> r)) == f =>
                        t
                      case _ =>
                        val x = IfThenElse(cond, t, f)()
                        if (
                          isBoolExpr(x).getOrElse(false) && !hasSideEffects(x)
                        ) {
                          partialEval((cond && t) || (Not(cond)() && f))
                        } else {
                          x
                        }
                    }
                }
            }
          case e =>
            // There may be more simplification opportunities that were wrapped inside BlackBoxes
            partialEval(e)
        }
      case Equal(e1, e2) =>
        // For Equal() and LessThan(), move IfThenElse up so that we can deal with things like
        // Min(t - 5, 5) < Min(t - 4, 5). This may cause the expression to explode in size, but since these are
        // booleans I imagine we'll often find opportunities for simplification (e.g., both branches being True, both
        // being False, IfThenElse(c, True, False) --> c).
        // If this ends up being problematic, we could always try putting a cap on the number of unique conditions in
        // an IfThenElse and avoid this transformation if the cap is exceeded.
        val newChildren =
          Seq(partialEval(e1)(facts, MoveUp), partialEval(e2)(facts, MoveUp))
        val merged = mergeIfThenElses(newChildren, x => y => x === y)
        merged match {
          case ite: IfThenElse => partialEval(ite)
          case e               => ArithSimplifier.simplifyArithmetic(e)(facts)
        }
      case LessThan(e1, e2) =>
        val newChildren = Seq(
          partialEval(e1)(facts, MoveUp),
          partialEval(e2)(facts, MoveUp)
        )
        mergeIfThenElses(newChildren, x => y => x < y) match {
          case ite: IfThenElse => partialEval(ite)
          case e               => ArithSimplifier.simplifyArithmetic(e)(facts)
        }
      case _: And =>
        val newChildren = e.children.map(e => partialEval(e))
        m match {
          case MoveUp =>
            mergeIfThenElses(newChildren, x => y => x && y) match {
              case ite: IfThenElse => partialEval(ite)
              case e => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case HeuristicMotion =>
            ArithSimplifier.simplifyArithmetic(e.rebuild(newChildren))(facts)
        }
      case _: Or =>
        val newChildren = e.children.map(e => partialEval(e))
        m match {
          case MoveUp =>
            mergeIfThenElses(newChildren, x => y => x || y) match {
              case ite: IfThenElse => partialEval(ite)
              case e => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case HeuristicMotion =>
            ArithSimplifier.simplifyArithmetic(e.rebuild(newChildren))(facts)
        }
      case Not(e) =>
        partialEval(e) match {
          case IfThenElse(c, t, f) =>
            // Move the IfThenElse up in every case because
            //  (1) the Not() may cancel with something further down and
            //  (2) since this is a unary operator, the expression is unlikely
            //      to grow *that* big.
            partialEval(IfThenElse(c, Not(t)(), Not(f)())())
          case e =>
            ArithSimplifier.simplifyArithmetic(Not(e)())(facts)
        }

      case t: Tuple =>
        // TODO: Move up?
        Tuple(t.elems.map(partialEval): _*)()
      case TupleAccess(t: Expr, IntCst(i)) =>
        partialEval(t) match {
          case tuple: Tuple =>
            partialEval(tuple.elems(i))
          case IfThenElse(c, t, f) =>
            // Move TupleAccess inside IfThenElse in the hope that it'll
            // encounter a Tuple(...).
            // This seems reasonable even if m == HeuristicMotion, since this
            // is essentially a unary operation; therefore, the expression is
            // not likely to grow *that* large.
            partialEval(
              IfThenElse(c, TupleAccess(t, i)(), TupleAccess(f, i)())()
            )
          case t => TupleAccess(t, i)()
        }

      case VecBuild(n, f) =>
        partialEval(n) match {
          case n =>
            val newFacts = facts.clearRange(f.param).between(f.param, 0, n)
            val newF =
              Function(f.param, partialEval(f.body)(newFacts))()
            (n, newF) match {
              case (
                    VecLength(x0: Param),
                    Function(i0, VecAccess(x1: Param, i1: Param))
                  ) if x0 == x1 && i0 == i1 =>
                x0
              case _ => VecBuild(n, newF)()
            }
        }
      case VecLength(v) =>
        partialEval(v) match {
          case VecBuild(n, _)      => partialEval(n)
          case IfThenElse(c, t, f) =>
            // Move the IfThenElse up in every case because
            //  (1) the VecLength() may cancel with something further down and
            //  (2) since this is a unary operator, the expression is unlikely
            //      to grow *that* big.
            partialEval(IfThenElse(c, VecLength(t)(), VecLength(f)())())
          case v => VecLength(v)()
        }
      case VecAccess(v, i: Expr) =>
        (partialEval(v), partialEval(i)) match {
          case (IfThenElse(c, t, f), i) =>
            // Move VecAccess inside IfThenElse in the hope that it'll
            // encounter a VecBuild(...)
            partialEval(IfThenElse(c, VecAccess(t, i)(), VecAccess(f, i)())())
          case (v: VecBuild, i) => partialEval(FunCall(v.f, i)())
          case (v, i)           => VecAccess(v, i)()
        }

      case s @ StmBuild(n, out, equations) =>
        val len = partialEval(n)(facts)
        val onlyElem = len match {
          case IntCst(1) =>
            // Maybe we can find the first element statically and just return it directly!
            tryEvalStmNext(s) match {
              case Some((_, out)) if !hasSideEffects(out) =>
                Some(partialEval(out)(facts))
              case _ => None
            }
          case _ =>
            None
        }
        onlyElem match {
          case Some(e) => StmBuild(1, SSome(e)(), Map[Param, (Expr, Expr)]())()
          case None    =>
            // Do the actual analysis to find the ranges outside the partial evaluator because doing it in the partial
            // evaluator is waaaay too slow. In many cases, it's not needed.
            val accRanges = facts.rangeByExpr.get(s) match {
              case Some(StmAccRange(accRanges)) => accRanges
              case _                            => Map()
            }
            val clearedFacts =
              s.accVars
                .foldLeft(facts)({ case (facts, x) => facts.clearRange(x) })
            val newFacts = accRanges
              .foldLeft(clearedFacts)({ case (facts, (x, r)) =>
                facts.range(x, r)
              })
            StmBuild(
              len,
              partialEval(out)(newFacts),
              equations.map({ case (x, (z, next)) =>
                // The recurrence variables shouldn't occur free in z, so use
                // the old facts for z
                x -> (partialEval(z)(facts), partialEval(next)(newFacts))
              })
            )()
        }
      case StmLength(s) =>
        partialEval(s) match {
          case s: StmBuild         => partialEval(s.n)
          case IfThenElse(c, t, f) =>
            // Move the IfThenElse up in every case because
            //  (1) the StmLength() may cancel with something further down and
            //  (2) since this is a unary operator, the expression is unlikely
            //      to grow *that* big.
            partialEval(IfThenElse(c, StmLength(t)(), StmLength(f)())())
          case s @ _ => StmLength(s)()
        }
      case StmNext(s) =>
        partialEval(s) match {
          case s: StmBuild =>
            tryEvalStmNext(s) match {
              case Some((nextStm, out)) => Tuple(nextStm, out)()
              case None                 => StmNext(s)()
            }
          case IfThenElse(c, t, f) =>
            // Move the IfThenElse up in every case because
            //  (1) the StmNext() may cancel with something further down and
            //  (2) since this is a unary operator, the expression is unlikely
            //      to grow *that* big.
            partialEval(IfThenElse(c, StmNext(t)(), StmNext(f)())())
          case s => StmNext(s)()
        }
      case StmNextK(s, k) =>
        val peStm = partialEval(s)
        partialEval(k) match {
          case IfThenElse(c, t, f) if m == MoveUp =>
            IfThenElse(
              c,
              partialEval(StmNextK(peStm, t)()),
              partialEval(StmNextK(peStm, f)())
            )()
          case IntCst(k) if k <= 0 => peStm
          // There are probably some cases where we want to convert StmNextK(s, k + 1) to StmNext(StmNextK(s, k)).__0
          // and other times where we want to go the other way.
          // Therefore, don't handle that here.
          case k => StmNextK(peStm, k)()
        }

      case _: VecLiteral | _: StmLiteral | _: SyntaxSugar => e.map(partialEval)

    }
    if (pe.isInstanceOf[Param] && e.hasType) {
      assert(
        pe.hasType,
        "the partial evaluator should not erase type annotations on variables"
      )
    }
    pe
  }

  def isEqual(e1: Expr, e2: Expr)(
      facts: FactSet = FactSet()
  ): Option[Boolean] = {
    partialEval(e1 === e2)(facts, MoveUp) match {
      case True  => Some(true)
      case False => Some(false)
      case _     => None
    }
  }

  def isSmaller(e1: Expr, e2: Expr)(
      facts: FactSet = FactSet()
  ): Option[Boolean] = {
    // TODO: Maybe this could be optimized by immediately returning `None` in
    //       cases where the partial evaluator won't return `True` or `False`
    partialEval(e1 < e2)(facts, MoveUp) match {
      case True  => Some(true)
      case False => Some(false)
      case _     => None
    }
  }

  def isSmallerOrEqual(e1: Expr, e2: Expr)(
      facts: FactSet = FactSet()
  ): Option[Boolean] = {
    // x <= y <==> !(x > y) <==> !(y < x)
    isSmaller(e2, e1)(facts).map(b => !b)
  }

  def isGreater(e1: Expr, e2: Expr)(
      facts: FactSet = FactSet()
  ): Option[Boolean] = {
    isSmaller(e2, e1)(facts)
  }

  def isGreaterOrEqual(e1: Expr, e2: Expr)(
      facts: FactSet = FactSet()
  ): Option[Boolean] = {
    isSmallerOrEqual(e2, e1)(facts)
  }

  private def mergeIfThenElses(
      exprs: Seq[Expr],
      op: Expr => Expr => Expr
  ): Expr = {
    exprs.tail.foldLeft(exprs.head)({ case (acc, e) =>
      (acc, e) match {
        case (IfThenElse(c1, t1, f1), IfThenElse(c2, t2, f2)) if (c1 == c2) =>
          IfThenElse(c1, op(t1)(t2), op(f1)(f2))()
        case (IfThenElse(c1, t1, f1), IfThenElse(c2, t2, f2)) =>
          IfThenElse(
            c1,
            IfThenElse(c2, op(t1)(t2), op(t1)(f2))(),
            IfThenElse(c2, op(f1)(t2), op(f1)(f2))()
          )()
        case (e1, IfThenElse(c, t, f)) =>
          IfThenElse(c, op(e1)(t), op(e1)(f))()
        case (IfThenElse(c, t, f), e2) =>
          IfThenElse(c, op(t)(e2), op(f)(e2))()
        case (e1, e2) =>
          op(e1)(e2)
      }
    })
  }

  /** Check whether <code>a</code> implies <code>b</code>.
    *
    * @return
    *   <code>Some(true)</code> if <code>a</code> implies <code>b</code>,
    *   <code>Some(false)</code> if <code>a</code> implies the negation of
    *   <code>b</code>, and <code>None</code> if we can't tell.
    */
  private def implies(a: Expr, b: Expr)(facts: FactSet): Option[Boolean] = {
    partialEval(b)(facts.isTrue(a)) match {
      case True  => Some(true)
      case False => Some(false)
      case _     => None
    }
  }

  @tailrec
  private def tryEvalStmNext(
      s: StmBuild,
      stepsWithoutValid: Int = 0
  ): Option[(StmBuild, Expr)] = {
    if (stepsWithoutValid >= 100) {
      None
    } else {
      s.n match {
        case IntCst(n) if n > 0 =>
          val currentValByVar: Map[Expr, Expr] = s.seedByVar.toMap
          partialEval(s.output.substitute(currentValByVar)) match {
            case Tuple(e, True) =>
              val nextEquations = s.equations.map({ case (x, (_, next)) =>
                val evaluatedNext =
                  partialEval(next.substitute(currentValByVar))
                x -> (evaluatedNext, next)
              })
              Some(StmBuild(n - 1, s.output, nextEquations)(), e)
            case Tuple(_, False) =>
              val nextEquations = s.equations.map({ case (x, (_, next)) =>
                val evaluatedNext =
                  partialEval(next.substitute(currentValByVar))
                x -> (evaluatedNext, next)
              })
              tryEvalStmNext(
                StmBuild(s.n, s.output, nextEquations)(),
                stepsWithoutValid = stepsWithoutValid + 1
              )
            case _ => None
          }
        case _ => None
      }
    }
  }

  @deprecated
  private def isBoolExpr(e: Expr): Option[Boolean] = {
    e match {
      // Definitely evaluates to a bool
      case True | False | _: And | _: Or | _: Not | _: Equal | _: LessThan =>
        Some(true)
      // Definitely not a bool
      case _: Tuple | _: StmBuild | _: VecBuild | _: IntExpr | _: Function |
          _: StmNext | _: StmNextK | _: VecLiteral | _: StmLiteral =>
        Some(false)
      // Not sure
      case _: Param | _: SyntaxSugar => None
      case TupleAccess(Tuple(elems @ _*), _) =>
        val isBool = elems.map(e => isBoolExpr(e))
        val atLeastOneTrue = isBool.exists(p => p.getOrElse(false))
        val atLeastOneFalse = isBool.exists(p => !p.getOrElse(true))
        (atLeastOneTrue, atLeastOneFalse) match {
          case (false, false) => None
          case (false, true)  => Some(false)
          case (true, false)  => Some(true)
          case (true, true)   => None
        }
      case TupleAccess(_, _)             => None
      case FunCall(Function(p, body), _) => isBoolExpr(body)
      case FunCall(_, _)                 => None
      case IfThenElse(_, t, f) =>
        (isBoolExpr(t), isBoolExpr(f)) match {
          case (None, None) => None
          case (None, Some(true)) | (Some(true), None) |
              (Some(true), Some(true)) =>
            Some(true)
          case (None, Some(false)) | (Some(false), None) |
              (Some(false), Some(false)) =>
            Some(false)
          case (Some(true), Some(false)) | (Some(false), Some(true)) => None
        }
      case _: VecAccess => None
    }
  }

  private def hasSideEffects(e: Expr): Boolean = {
    e match {
      case _: VecLength => false
      case _: StmLength => false
      case _: StmNext   => true
      case e            => e.children.exists(c => hasSideEffects(c))
    }
  }

  private def splitAnd(e: Expr): Seq[Expr] = {
    // TODO: Convert to POS form first?
    e match {
      case And(terms @ _*) => terms
      case e               => Seq(e)
    }
  }

  private def splitOr(e: Expr): Seq[Expr] = {
    // TODO: Convert to SOP form first?
    e match {
      case Or(terms @ _*) => terms
      case e              => Seq(e)
    }
  }
}
