package opt

import ir._

import scala.annotation.tailrec

sealed trait MuxMotion
case object MoveUp extends MuxMotion
case object HeuristicMotion extends MuxMotion

object PartialEvalPass {
  def pe(e: Expr): Expr = {
    // Mostly for debugging, since the debugger really doesn't seem to handle
    // implicit and default parameters well
    partialEval(e)
  }

  def partialEval(
      expr: Expr
  )(implicit
      facts: FactSet = FactSet(),
      m: MuxMotion = HeuristicMotion
  ): Expr = {
    val e = expr.tchk()
    var expectedTyp = e.typ
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
                assert(x.typ != Missing)
                assert(a.typ ~= x.typ)
                expectedTyp = e.typ.substitute(x -> a)
                partialEval(body.subPreserveType(x -> a))
              case f => FunCall(f, partialEval(arg))()
            }

          case IntCst(_) => e
          case Sum(terms @ _*) =>
            val newChildren = terms.map(e => partialEval(e))
            m match {
              case MoveUp =>
                mergeMuxes(newChildren, x => y => Sum(x, y)()) match {
                  case mux: Mux => partialEval(mux)
                  // TODO: Ensure `e` has been type checked here
                  case e => ArithSimplifier.simplifyArithmetic(e)(facts)
                }
              case HeuristicMotion =>
                ArithSimplifier.simplifyArithmetic(
                  e.rebuild(e.typ, newChildren)
                )(facts)
            }
          case Prod(factors @ _*) =>
            val newChildren = factors.map(e => partialEval(e))
            m match {
              case MoveUp =>
                mergeMuxes(newChildren, x => y => Prod(x, y)()) match {
                  case mux: Mux => partialEval(mux)
                  case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
                }
              case HeuristicMotion =>
                ArithSimplifier.simplifyArithmetic(
                  e.rebuild(e.typ, newChildren)
                )(facts)
            }
          case _: Div =>
            val newChildren = e.children.map(e => partialEval(e))
            m match {
              case MoveUp =>
                mergeMuxes(newChildren, x => y => Div(x, y)()) match {
                  case mux: Mux => partialEval(mux)
                  case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
                }
              case HeuristicMotion =>
                ArithSimplifier.simplifyArithmetic(
                  e.rebuild(e.typ, newChildren)
                )(facts)
            }
          case _: Mod =>
            val newChildren = e.children.map(e => partialEval(e))
            m match {
              case MoveUp =>
                mergeMuxes(newChildren, x => y => Mod(x, y)()) match {
                  case mux: Mux => partialEval(mux)
                  case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
                }
              case HeuristicMotion =>
                ArithSimplifier.simplifyArithmetic(
                  e.rebuild(e.typ, newChildren)
                )(facts)
            }
          case PadTo(e, w) =>
            partialEval(e) match {
              case v: IntCst => ir.eval(PadTo(v, w)())
              case Sum(terms @ _*) =>
                partialEval(Sum(terms.map(e => PadTo(e, w)()): _*)())
              case Prod(factors @ _*) =>
                partialEval(Prod(factors.map(e => PadTo(e, w)()): _*)())
              // TODO: Move the PadTo down in other cases?
              case Mux(c, t, f) =>
                partialEval(Mux(c, PadTo(t, w)(), PadTo(f, w)())())
              case e =>
                e.typ match {
                  case TyAnyInt(w0) if w0 == w => e
                  case _                       => PadTo(e, w)()
                }
            }
          case TruncateTo(e, w) =>
            partialEval(e) match {
              case v: IntCst =>
                ir.eval(TruncateTo(v, w)())
              case Mux(c, t, f) =>
                partialEval(Mux(c, TruncateTo(t, w)(), TruncateTo(f, w)())())
              case PadTo(e, _) =>
                e.typ match {
                  case TyAnyInt(w0) =>
                    if (w < w0) {
                      TruncateTo(e, w)()
                    } else if (w == w0) {
                      e
                    } else {
                      PadTo(e, w)()
                    }
                  case _ =>
                    TruncateTo(e, w)()
                }
              case e =>
                e.typ match {
                  case TyAnyInt(w0) if w == w0 => e
                  case _                       => TruncateTo(e, w)()
                }
            }
          case ToSigned(e) =>
            partialEval(e) match {
              case v: IntCst => ir.eval(ToSigned(v)())
              case Sum(terms @ _*) =>
                partialEval(Sum(terms.map(e => ToSigned(e)()): _*)())
              case Prod(factors @ _*) =>
                partialEval(Prod(factors.map(e => ToSigned(e)()): _*)())
              // TODO: Move the ToSigned towards the leaves in other cases too?
              case Mux(c, t, f) =>
                partialEval(Mux(c, ToSigned(t)(), ToSigned(f)())())
              case e => ToSigned(e)()
            }
          case ToUnsigned(e) =>
            partialEval(e) match {
              case Mux(c, t, f) =>
                partialEval(Mux(c, ToUnsigned(t)(), ToUnsigned(f)())())
              case v: IntCst =>
                // TODO: This is a hack to ensure the argument of ToUnsigned
                //       is signed. Better to preserve the type during partial
                //       evaluation
                val vv = v.typ match {
                  case _: TySInt => v
                  case _         => v.rebuild(TyAnyInt.tightest(-1, v.i))
                }
                ir.eval(ToUnsigned(vv)())
              case e => ToUnsigned(e)()
            }

          case True         => True
          case False        => False
          case Mux(c, t, f) =>
            // TODO: This needs to be cleaned up (e.g., to reduce the amount of duplicate partial evaluation)
            val peMux = Mux(
              partialEval(c)(facts, MoveUp),
              partialEval(t),
              partialEval(f)
            )()
            peMux match {
              case Mux(
                    Equal(i0, Sum(IntCst(-1), n0)),
                    c0,
                    Mux(LessThan(i1, Sum(IntCst(-1), n1)), c1, _)
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
                        // TODO: Bring back this canonicalization while being
                        //       careful about signed vs unsigned arithmetic?
//                        val condVariants = c match {
//                          case LessThan(x, y) => Seq(c, LessThan(x - y, 0)())
//                          case _              => Seq(c)
//                        }
                        val condVariants = Seq(c)
                        val subs = condVariants.map(c => c -> True).toMap
                        acc.subPreserveType(subs)
                      })
                      partialEval(t)(newFacts, m)
                    }
                    val f = {
                      val newFacts = facts.assumeFalse(cond)
                      val f = splitOr(cond).foldLeft(falseE)({ case (acc, c) =>
                        // TODO: See TODO comment above (ideally do this
                        //       canonicalization everywhere, not just here)
//                        val condVariants = c match {
//                          case LessThan(x, y) => Seq(c, LessThan(x - y, 0)())
//                          case _              => Seq(c)
//                        }
                        val condVariants = Seq(c)
                        val subs = condVariants.map(c => c -> False).toMap
                        acc.subPreserveType(subs)
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
                              if partialEval(f.subPreserveType(p -> r)) == t =>
                            f
                          // False branch is special case of true branch
                          case Not(Equal(p: Param, r))
                              if partialEval(t.subPreserveType(p -> r)) == f =>
                            t
                          case _ =>
                            val x = Mux(cond, t, f)()
                            if (
                              isBoolExpr(x).getOrElse(false)
                              && !x.contains(classOf[StmData])
                            ) {
                              partialEval((cond && t) || (Not(cond)() && f))
                            } else {
                              x
                            }
                        }
                    }
                }
            }
          case _: Equal =>
            // For Equal() and LessThan(), move Mux up so that we can deal with things like
            // Min(t - 5, 5) < Min(t - 4, 5). This may cause the expression to explode in size, but since these are
            // booleans I imagine we'll often find opportunities for simplification (e.g., both branches being True, both
            // being False, Mux(c, True, False) --> c).
            // If this ends up being problematic, we could always try putting a cap on the number of unique conditions in
            // a Mux and avoid this transformation if the cap is exceeded.
            val newChildren = e.children.map(e => partialEval(e))
            val merged = mergeMuxes(newChildren, x => y => Equal(x, y)())
            merged match {
              case mux: Mux => partialEval(mux)
              case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case _: LessThan =>
            val newChildren = e.children.map(e => partialEval(e))
            mergeMuxes(newChildren, x => y => LessThan(x, y)()) match {
              case mux: Mux => partialEval(mux)
              case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case _: And =>
            val newChildren = e.children.map(e => partialEval(e))
            m match {
              case MoveUp =>
                mergeMuxes(newChildren, x => y => And(x, y)()) match {
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
                mergeMuxes(newChildren, x => y => Or(x, y)()) match {
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
                partialEval(tuple.elems(i.toInt))
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
                  Mux(i >= 0 && i < n, FunCall(f, i)(), Default(t))()
                    .tchk()
                    .lower()
                )
              case (v, i) => VecAccess(v, i)()
            }

          case s @ StmBuild(n, data, valid, equations) =>
            val len = partialEval(n)(facts)
            val onlyElem = len match {
              case IntCst(1) =>
                // Maybe we can find the first element statically and just return it directly!
                tryEvalStmNext(s) match {
                  case Some((out, _)) if !out.contains(classOf[StmData]) =>
                    Some(partialEval(out)(facts))
                  case _ => None
                }
              case _ =>
                None
            }
            onlyElem match {
              case Some(e) =>
                StmBuild(1, e, True, Map[Param, (Expr, Expr)]())()
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
                val newValid = partialEval(valid)(newFacts)
                StmBuild(
                  len,
                  // The value of the data doesn't matter if it is invalid, so
                  // we can assume it is valid when simplifying.
                  partialEval(data)(newFacts.assumeTrue(newValid)),
                  newValid,
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
          case StmData(s) => StmData(partialEval(s))()
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
              case k                   => StmNextK(peStm, k)()
            }

          case _: VecLiteral | _: StmLiteral | _: SyntaxSugar =>
            e.map(partialEval)

        }
    }
    if (expectedTyp != Missing) {
      val typedExpr = pe.tchk()
      assert(
        typedExpr.typ ~= expectedTyp,
        s"partial evaluation should preserve type annotations (expected $expectedTyp, found ${typedExpr.typ})"
      )
      typedExpr
    } else {
      pe
    }
  }

  def isEqual(e1: Expr, e2: Expr)(
      facts: FactSet = FactSet()
  ): Option[Boolean] = {
    partialEval((e1 === e2).tchk().lower())(facts, MoveUp) match {
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
      val newLhs = SmartSum(lhsPosTerms ++ rhsNegTerms: _*)()
      val newRhs = SmartSum(rhsPosTerms ++ lhsNegTerms: _*)()
      (newLhs < newRhs).tchk().lower()
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
    val merged = exprs.tail.foldLeft(exprs.head)({ case (acc, e) =>
      (acc, e) match {
        case (Mux(c1, t1, f1), Mux(c2, t2, f2)) if c1 == c2 =>
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
    merged.tchk()
  }

  private def moveConstantsToRhs(lhs: Expr, rhs: Expr): (Expr, Expr) = {
    lhs match {
      case IntCst(k) if k != 0        => (0, rhs - k)
      case Sum(IntCst(k), terms @ _*) => (Sum(terms: _*)(), rhs - k)
      case _                          => (lhs, rhs)
    }
  }

  private def tryEvalStmNext(
      stm: StmBuild,
      stepsWithoutValid: Int = 0
  ): Option[(Expr, StmBuild)] = {
    if (stepsWithoutValid >= 10) {
      None
    } else {
      val s =
        try {
          Some(stm.tchk().asInstanceOf[StmBuild])
        } catch {
          case _: TypeError => None
        }
      s match {
        case Some(s) =>
          s.n match {
            case IntCst(n) if n > 0 =>
              val currentValByVar: Map[Expr, Expr] = s.seedByVar.toMap
              val inputStreamOptions = s.equations.flatMap({
                case (x, (z, next)) if x.typ.isInstanceOf[TyStm] =>
                  partialEval(next.subPreserveType(currentValByVar)) match {
                    case False =>
                      val t = x.typ.asInstanceOf[TyStm].t
                      val head = ir.eval(Default(t))
                      Some(Some(x -> (head, z)))
                    case True =>
                      val maybeHeadAndTail = z match {
                        case s: StmBuild => tryEvalStmNext(s)
                        case _           => None
                      }
                      Some(maybeHeadAndTail.map(nxt => x -> nxt))
                    case _ => None
                  }
                case (x, _) =>
                  assert(x.typ != Missing)
                  None
              })
              if (inputStreamOptions.exists(x => x.isEmpty)) {
                None
              } else {
                val inputStreams = inputStreamOptions.map(x => x.get).toMap
                val subs = inputStreams.foldLeft(currentValByVar)({
                  case (acc, (x, (head, _))) => acc + (StmData(x)() -> head)
                })
                val nextEquations = s.equations.map({
                  case (x, (_, next)) if x.typ.isInstanceOf[TyStm] =>
                    val (_, tail) = inputStreams(x)
                    x -> (tail, next)
                  case (x, (_, next)) =>
                    val evaluatedNext = partialEval(next.subPreserveType(subs))
                    x -> (evaluatedNext, next)
                })
                val evaluatedValid = partialEval(s.valid.subPreserveType(subs))
                evaluatedValid match {
                  case True =>
                    val v = partialEval(s.data.subPreserveType(subs))
                    Some((v, StmBuild(n - 1, s.data, s.valid, nextEquations)()))
                  case False =>
                    tryEvalStmNext(
                      StmBuild(n, s.data, s.valid, nextEquations)(),
                      stepsWithoutValid + 1
                    )
                  case _ => None
                }
              }
            case _ => None
          }
        case None => None
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
