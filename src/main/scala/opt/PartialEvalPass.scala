package opt

import ir._

import scala.annotation.tailrec

sealed trait MuxMotion
case object MoveUp extends MuxMotion
case object HeuristicMotion extends MuxMotion

object PartialEvalPass {
  def partialEval(
      e: Expr
  )(implicit
      facts: FactSet = FactSet(),
      m: MuxMotion = HeuristicMotion
  ): Expr = {
    val pe = facts.isTrue(e) match {
      case Some(true)  => True
      case Some(false) => False
      case None =>
        e match {
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
                mergeMuxes(newChildren, x => y => x + y) match {
                  case mux: Mux => partialEval(mux)
                  case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
                }
              case HeuristicMotion =>
                ArithSimplifier.simplifyArithmetic(e.rebuild(newChildren))(
                  facts
                )
            }
          case Prod(factors @ _*) =>
            val newChildren = factors.map(e => partialEval(e))
            m match {
              case MoveUp =>
                mergeMuxes(newChildren, x => y => x * y) match {
                  case mux: Mux => partialEval(mux)
                  case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
                }
              case HeuristicMotion =>
                ArithSimplifier.simplifyArithmetic(e.rebuild(newChildren))(
                  facts
                )
            }
          case _: Div =>
            val newChildren = e.children.map(e => partialEval(e))
            m match {
              case MoveUp =>
                mergeMuxes(newChildren, x => y => x / y) match {
                  case mux: Mux => partialEval(mux)
                  case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
                }
              case HeuristicMotion =>
                ArithSimplifier.simplifyArithmetic(e.rebuild(newChildren))(
                  facts
                )
            }
          case _: Mod =>
            val newChildren = e.children.map(e => partialEval(e))
            m match {
              case MoveUp =>
                mergeMuxes(newChildren, x => y => x % y) match {
                  case mux: Mux => partialEval(mux)
                  case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
                }
              case HeuristicMotion =>
                ArithSimplifier.simplifyArithmetic(e.rebuild(newChildren))(
                  facts
                )
            }

          case True  => True
          case False => False
          case Mux(c, t, f) =>
            val peMux =
              Mux(partialEval(c), partialEval(t), partialEval(f))()
            ArithSimplifier.simplifyArithmetic(peMux)(facts) match {
              case Mux(
                    Equal(i0, Sum(IntCst(-1), n0)),
                    c0,
                    Mux(LessThan(Sum(IntCst(1), i1), n1), c1, c2)
                  )
                  if i0 == i1 && n0 == n1
                    && isSmaller(i0, n0)(facts).getOrElse(false) =>
                partialEval(Mux(i0 === -1 + n0, c0, c1)())
              case Mux(cond, trueE, falseE) =>
                partialEval(cond) match {
                  case True  => partialEval(trueE)
                  case False => partialEval(falseE)
                  case cond =>
                    val t = {
                      val newFacts = facts.assumeTrue(cond)
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
                      val newFacts = facts.assumeFalse(cond)
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
                        partialEval(StmNextK(s0, Mux(cond, k0, k1)())())
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
                            val x = Mux(cond, t, f)()
                            if (
                              isBoolExpr(x)
                                .getOrElse(false) && !hasSideEffects(x)
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
            // For Equal() and LessThan(), move Mux up so that we can deal with things like
            // Min(t - 5, 5) < Min(t - 4, 5). This may cause the expression to explode in size, but since these are
            // booleans I imagine we'll often find opportunities for simplification (e.g., both branches being True, both
            // being False, Mux(c, True, False) --> c).
            // If this ends up being problematic, we could always try putting a cap on the number of unique conditions in
            // a Mux and avoid this transformation if the cap is exceeded.
            val newChildren =
              Seq(
                partialEval(e1)(facts, MoveUp),
                partialEval(e2)(facts, MoveUp)
              )
            val merged = mergeMuxes(newChildren, x => y => x === y)
            merged match {
              case mux: Mux => partialEval(mux)
              case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case LessThan(e1, e2) =>
            val newChildren = Seq(
              partialEval(e1)(facts, MoveUp),
              partialEval(e2)(facts, MoveUp)
            )
            mergeMuxes(newChildren, x => y => x < y) match {
              case mux: Mux => partialEval(mux)
              case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case _: And =>
            val newChildren = e.children.map(e => partialEval(e))
            m match {
              case MoveUp =>
                mergeMuxes(newChildren, x => y => x && y) match {
                  case mux: Mux => partialEval(mux)
                  case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
                }
              case HeuristicMotion =>
                ArithSimplifier.simplifyArithmetic(e.rebuild(newChildren))(
                  facts
                )
            }
          case _: Or =>
            val newChildren = e.children.map(e => partialEval(e))
            m match {
              case MoveUp =>
                mergeMuxes(newChildren, x => y => x || y) match {
                  case mux: Mux => partialEval(mux)
                  case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
                }
              case HeuristicMotion =>
                ArithSimplifier.simplifyArithmetic(e.rebuild(newChildren))(
                  facts
                )
            }
          case Not(e) =>
            partialEval(e) match {
              case Mux(c, t, f) =>
                // Move the Mux up in every case because
                //  (1) the Not() may cancel with something further down and
                //  (2) since this is a unary operator, the expression is unlikely
                //      to grow *that* big.
                partialEval(Mux(c, Not(t)(), Not(f)())())
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
              case Mux(c, t, f) =>
                // Move TupleAccess inside Mux in the hope that it'll
                // encounter a Tuple(...).
                // This seems reasonable even if m == HeuristicMotion, since this
                // is essentially a unary operation; therefore, the expression is
                // not likely to grow *that* large.
                partialEval(
                  Mux(c, TupleAccess(t, i)(), TupleAccess(f, i)())()
                )
              case t => TupleAccess(t, i)()
            }

          case VecBuild(n, f) =>
            val newN = partialEval(n)
            val newFacts = facts.clearRange(f.param).between(f.param, 0, newN)
            val newF = Function(f.param, partialEval(f.body)(newFacts))()
            (newN, newF) match {
              case (
                    VecLength(x0: Param),
                    Function(i0, VecAccess(x1: Param, i1: Param))
                  ) if x0 == x1 && i0 == i1 =>
                x0
              case _ => VecBuild(newN, newF)()
            }
          case VecLength(v) =>
            partialEval(v) match {
              case VecBuild(n, _) => partialEval(n)
              case Mux(c, t, f)   =>
                // Move the Mux up in every case because
                //  (1) the VecLength() may cancel with something further down and
                //  (2) since this is a unary operator, the expression is unlikely
                //      to grow *that* big.
                partialEval(Mux(c, VecLength(t)(), VecLength(f)())())
              case v => VecLength(v)()
            }
          case VecAccess(v, i: Expr) =>
            (partialEval(v), partialEval(i)) match {
              case (Mux(c, t, f), i) =>
                // Move VecAccess inside Mux in the hope that it'll
                // encounter a VecBuild(...)
                partialEval(
                  Mux(c, VecAccess(t, i)(), VecAccess(f, i)())()
                )
              case (v @ VecBuild(n, f), i) =>
                val t = v.tchk().typ.asInstanceOf[TyVec].t
                partialEval(
                  Mux(i >= 0 && i < n, FunCall(f, i)(), Default(t).lower())()
                )
              case (v, i) => VecAccess(v, i)()
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
              case Some(e) =>
                StmBuild(1, SSome(e)(), Map[Param, (Expr, Expr)]())()
              case None =>
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
              case s: StmBuild  => partialEval(s.n)
              case Mux(c, t, f) =>
                // Move the Mux up in every case because
                //  (1) the StmLength() may cancel with something further down and
                //  (2) since this is a unary operator, the expression is unlikely
                //      to grow *that* big.
                partialEval(Mux(c, StmLength(t)(), StmLength(f)())())
              case s @ _ => StmLength(s)()
            }
          case StmNext(s) =>
            partialEval(s) match {
              case s: StmBuild =>
                tryEvalStmNext(s) match {
                  case Some((nextStm, out)) => Tuple(nextStm, out)()
                  case None                 => StmNext(s)()
                }
              case Mux(c, t, f) =>
                // Move the Mux up in every case because
                //  (1) the StmNext() may cancel with something further down and
                //  (2) since this is a unary operator, the expression is unlikely
                //      to grow *that* big.
                partialEval(Mux(c, StmNext(t)(), StmNext(f)())())
              case s => StmNext(s)()
            }
          case StmNextK(s, k) =>
            val peStm = partialEval(s)
            partialEval(k) match {
              case Mux(c, t, f) if m == MoveUp =>
                Mux(
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

          case _: VecLiteral | _: StmLiteral | _: SyntaxSugar =>
            e.map(partialEval)

        }
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

  private def getPosAndNegTerms(e: Expr): (Seq[Expr], Seq[Expr]) = {
    e match {
      case Sum(terms @ _*) =>
        val children = terms.map(getPosAndNegTerms)
        (children.flatMap(e => e._1), children.flatMap(e => e._2))
      case Prod(IntCst(-1), e) => (Seq(), Seq(e))
      case e                   => (Seq(e), Seq())
    }
  }

  def isSmaller(e1: Expr, e2: Expr)(
      facts: FactSet = FactSet()
  ): Option[Boolean] = {
    val lt = {
      // ArithExpr seems to handle x < y better than x - y < 0, so try making all terms positive
      val (lhsPosTerms, lhsNegTerms) = getPosAndNegTerms(partialEval(e1)(facts))
      val (rhsPosTerms, rhsNegTerms) = getPosAndNegTerms(partialEval(e2)(facts))
      val newLhs = Sum(lhsPosTerms ++ rhsNegTerms: _*)()
      val newRhs = Sum(rhsPosTerms ++ lhsNegTerms: _*)()
      newLhs < newRhs
    }
    partialEval(lt)(facts, MoveUp) match {
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

  private def mergeMuxes(
      exprs: Seq[Expr],
      op: Expr => Expr => Expr
  ): Expr = {
    exprs.tail.foldLeft(exprs.head)({ case (acc, e) =>
      (acc, e) match {
        case (Mux(c1, t1, f1), Mux(c2, t2, f2)) if (c1 == c2) =>
          Mux(c1, op(t1)(t2), op(f1)(f2))()
        case (Mux(c1, t1, f1), Mux(c2, t2, f2)) =>
          Mux(
            c1,
            Mux(c2, op(t1)(t2), op(t1)(f2))(),
            Mux(c2, op(f1)(t2), op(f1)(f2))()
          )()
        case (e1, Mux(c, t, f)) =>
          Mux(c, op(e1)(t), op(e1)(f))()
        case (Mux(c, t, f), e2) =>
          Mux(c, op(t)(e2), op(f)(e2))()
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
    partialEval(b)(facts.assumeTrue(a)) match {
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
    if (stepsWithoutValid >= 10) {
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

  private def isBoolExpr(e: Expr): Option[Boolean] = {
    try {
      Some(e.tchk().typ == TyBool)
    } catch {
      case _: TypeError => None
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
