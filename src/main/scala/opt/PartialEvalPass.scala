package opt

import ir._
import operations.Cast

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
              case PadTo(e, w2) =>
                assert(
                  w >= w2,
                  "intermediate width in nested pads must not be greater than the final width"
                    + s" (intermediate width is $w2, final width is $w)"
                )
                PadTo(e, w)()
              case TruncateTo(e, _) =>
                // It is undefined behaviour if `e` does not fit in the target
                // type, so the partial evaluator is allowed to treat this as a no-op
                e
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
              case TruncateTo(e, w2) =>
                assert(
                  w <= w2,
                  "intermediate width in nested truncates must not be less than the final width"
                    + s" (intermediate width is $w2, final width is $w)"
                )
                TruncateTo(e, w)()
              case e =>
                e.typ match {
                  case TyAnyInt(w0) if w == w0 => e
                  case _                       => TruncateTo(e, w)()
                }
            }
          case ToSigned(e) =>
            partialEval(e) match {
              case ToUnsigned(e) =>
                // It is undefined behaviour if `e` is negative, so the partial
                // evaluator is allowed to treat this as a no-op
                e
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
              case ToSigned(e) => e
              case Mux(c, t, f) =>
                partialEval(Mux(c, ToUnsigned(t)(), ToUnsigned(f)())())
              case v: IntCst =>
                ir.eval(ToUnsigned(v)())
              case e => ToUnsigned(e)()
            }

          case True  => True
          case False => False
          case Mux(c, t, f) =>
            partialEval(c)(facts, MoveUp) match {
              case True  => partialEval(t)
              case False => partialEval(f)
              case cond =>
                val trueE = partialEval(t)(facts.assumeTrue(cond), m)
                val falseE = partialEval(f)(facts.assumeFalse(cond), m)
                (cond, trueE, falseE) match {
                  case _ if trueE == falseE => trueE
                  case _ if trueE.typ == TyBool =>
                    partialEval((cond && trueE) || (!cond && falseE))
                  case (_, StmNextK(s0, k0), StmNextK(s1, k1)) if s0 == s1 =>
                    partialEval(StmNextK(s0, Mux(cond, k0, k1)())())
                  case (
                        Equal(Sum(IntCst(1), i0), n0),
                        c0,
                        Mux(LessThan(Sum(IntCst(1), i1), n1), c1, _)
                      )
                      if isEqual(i0, i1)(facts).getOrElse(false)
                        && isEqual(n0, n1)(facts).getOrElse(false)
                        && isSmaller(i0, n0)(facts).getOrElse(false) =>
                    partialEval(Mux(i0 + 1 === n0, c0, c1)().tchk().lower())
                  // True branch is special case of false branch
                  case _
                      if Default.hasDefault(trueE.typ)
                        && isEqual(trueE, falseE)(facts.assumeTrue(cond))
                          .getOrElse(false) =>
                    falseE
                  // False branch is special case of true branch
                  case _
                      if Default.hasDefault(falseE.typ)
                        && isEqual(trueE, falseE)(facts.assumeFalse(cond))
                          .getOrElse(false) =>
                    trueE
                  case _ =>
                    Mux(cond, trueE, falseE)()
                }
            }
          case Equal(lhs, rhs) =>
            // For Equal() and LessThan(), move Mux up so that we can deal with things like
            // Min(t - 5, 5) < Min(t - 4, 5). This may cause the expression to explode in size, but since these are
            // booleans I imagine we'll often find opportunities for simplification (e.g., both branches being True, both
            // being False, Mux(c, True, False) --> c).
            // If this ends up being problematic, we could always try putting a cap on the number of unique conditions in
            // a Mux and avoid this transformation if the cap is exceeded.
            val (newLhs, newRhs) = if (lhs.typ.isInstanceOf[TyAnyInt]) {
              combineConstants(
                partialEval(lhs)(facts, MoveUp),
                partialEval(rhs)(facts, MoveUp)
              )
            } else {
              (partialEval(lhs)(facts, MoveUp), partialEval(rhs)(facts, MoveUp))
            }
            val merged =
              mergeMuxes(Seq(newLhs, newRhs), x => y => Equal(x, y)())
            merged match {
              case mux: Mux => partialEval(mux)
              case e        => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case LessThan(lhs, rhs) =>
            val (newLhs, newRhs) =
              combineConstants(
                partialEval(lhs)(facts, MoveUp),
                partialEval(rhs)(facts, MoveUp)
              )
            val merged =
              mergeMuxes(Seq(newLhs, newRhs), x => y => LessThan(x, y)())
            merged match {
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
          case VecAccess(v, i: Expr) =>
            (partialEval(v), partialEval(i)) match {
              case (Mux(c, t, f), i) =>
                // Move VecAccess inside Mux in the hope that it'll
                // encounter a VecBuild(...)
                partialEval(
                  Mux(c, VecAccess(t, i)(), VecAccess(f, i)())()
                )
              case (VecBuild(_, f), i) =>
                // Out-of-bounds vector access is undefined behaviour, so just
                // don't worry about it
                val fInT = f.typ.asInstanceOf[TyArrow].t1
                partialEval(FunCall(f, Cast(i, fInT)())().tchk().lower())
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
    val typedExpr = pe.tchk()
    assert(
      typedExpr.typ ~~= e.typ,
      s"partial evaluation should preserve type annotations (expected ${e.typ}, found ${typedExpr.typ})"
    )
    typedExpr
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

  private def combineConstants(lhs: Expr, rhs: Expr): (Expr, Expr) = {
    require(
      lhs.typ.isInstanceOf[TyAnyInt],
      "Left-hand side should be an integer."
    )
    require(
      rhs.typ.isInstanceOf[TyAnyInt],
      "Right-hand side must be an integer."
    )
    val (lhsCst, lhsNonCstTerms) = lhs match {
      case Sum(terms @ _*) =>
        val (cst, nonCst) = terms.partition(e => e.isInstanceOf[IntCst])
        (cst.map(e => e.asInstanceOf[IntCst].i).sum, nonCst)
      case IntCst(k) => (k, Seq())
      case e         => (0L, Seq(e))
    }
    val (rhsCst, rhsNonCstTerms) = rhs match {
      case Sum(terms @ _*) =>
        val (cst, nonCst) = terms.partition(e => e.isInstanceOf[IntCst])
        (cst.map(e => e.asInstanceOf[IntCst].i).sum, nonCst)
      case IntCst(k) => (k, Seq())
      case e         => (0L, Seq(e))
    }
    lhsCst - rhsCst match {
      case 0 =>
        (Sum(lhsNonCstTerms: _*)(lhs.typ), Sum(rhsNonCstTerms: _*)(rhs.typ))
      case k if k > 0 =>
        (
          Sum(C(k)(lhs.typ) +: lhsNonCstTerms: _*)(lhs.typ),
          Sum(rhsNonCstTerms: _*)(rhs.typ)
        )
      case k =>
        (
          Sum(lhsNonCstTerms: _*)(lhs.typ),
          Sum(C(-k)(rhs.typ) +: rhsNonCstTerms: _*)(rhs.typ)
        )
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
}
