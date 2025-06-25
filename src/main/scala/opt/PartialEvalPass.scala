package opt

import ir._
import operations.Cast

object PartialEvalPass {
  def pe(e: Expr): Expr = {
    // Mostly for debugging, since the debugger really doesn't seem to handle
    // implicit and default parameters well
    partialEval(e)
  }

  def partialEval(e: Expr)(implicit facts: FactSet = FactSet()): Expr = {
    val e1 = doPartialEval(e)
    // Partial evaluation may reveal more opportunities to move int
    // conversions (e.g., by function inlining)...
    val e2 = IntConversionMover.widen(e1)
    // ... but also, moving int conversions will make it easier to simplify
    // arithmetic operations.
    val e3 = doPartialEval(e2)
    e3
  }

  private def doPartialEval(expr: Expr)(implicit facts: FactSet): Expr = {
    val e = {
      val typed = expr.tchk()
      if (typed.typ == TyBool) {
        // For boolean expressions, be more aggressive and move MUXes up.
        // For expressions like Min(t - 5, 5) < Min(t - 4, 5), considering each
        // case separately helps.
        // This may cause the expression to explode in size, but since these
        // are booleans there should usually be opportunities for
        // simplification (e.g., both branches being True, both being False,
        // Mux(c, True, False) --> c).
        // If this ends up being problematic, we could always scan the
        // expression first and skip this if there are too many MUXes.
        MuxMover.moveUp(typed).tchk()
      } else {
        typed
      }
    }
    val pe = facts.isTrue(e) match {
      case Some(true)  => True
      case Some(false) => False
      case None =>
        e match {
          case x: Param =>
            facts.getRange(x) match {
              case Some(ScalarRange(Some(lo), Some(hi)))
                  if isEqual(SafeSum(lo, 1)().tchk().lower(), hi)(facts)
                    .getOrElse(false) =>
                doPartialEval(ReshapeData(lo, x.typ)().tchk().lower())
              case _ =>
                x
            }
          case Function(x, body) =>
            Function(x, doPartialEval(body)(facts.clearRange(x)))()
          case FunCall(f: Expr, arg: Expr) =>
            doPartialEval(f) match {
              case Function(x, body) =>
                val a = doPartialEval(arg)
                assert(x.typ != Missing)
                assert(a.typ ~= x.typ)
                doPartialEval(body.subPreserveType(x -> a))
              case f => FunCall(f, doPartialEval(arg))()
            }

          case IntCst(_) => e
          case Sum(terms @ _*) =>
            val newChildren = terms.map(e => doPartialEval(e))
            ArithSimplifier.simplifyArithmetic(
              e.rebuild(e.typ, newChildren)
            )(facts)
          case Prod(factors @ _*) =>
            val newChildren = factors.map(e => doPartialEval(e))
            ArithSimplifier.simplifyArithmetic(
              e.rebuild(e.typ, newChildren)
            )(facts)
          case _: Div =>
            val newChildren = e.children.map(e => doPartialEval(e))
            ArithSimplifier.simplifyArithmetic(
              e.rebuild(e.typ, newChildren)
            )(facts)
          case _: Mod =>
            val newChildren = e.children.map(e => doPartialEval(e))
            ArithSimplifier.simplifyArithmetic(
              e.rebuild(e.typ, newChildren)
            )(facts)
          case PadTo(e, w) =>
            doPartialEval(e) match {
              case v: IntCst => ir.eval(PadTo(v, w)())
              case PadTo(e, w2) =>
                assert(
                  w >= w2,
                  "intermediate width in nested pads must not be greater than the final width"
                    + s" (intermediate width is $w2, final width is $w)"
                )
                PadTo(e, w)()
              case TruncateTo(e, _) =>
                // It is undefined behaviour if `e` does not fit in the target
                // type, so the partial evaluator is allowed to cancel out
                // like this
                val w0 = e.typ.asInstanceOf[TyAnyInt].w
                if (w < w0) TruncateTo(e, w)()
                else if (w == w0) e
                else PadTo(e, w)()
              case e =>
                e.typ match {
                  case TyAnyInt(w0) if w0 == w => e
                  case _                       => PadTo(e, w)()
                }
            }
          case TruncateTo(arg, w) =>
            doPartialEval(arg) match {
              case v: IntCst =>
                try {
                  // Wrap in try/catch because the argument may not fit in the
                  // target range (and this can occur while speculatively
                  // partially evaluating one branch of a MUX to see whether it
                  // is equivalent to the other)
                  ir.eval(TruncateTo(v, w)())
                } catch {
                  case _: EvalException => e
                }
              case Mux(c, t, f) =>
                doPartialEval(Mux(c, TruncateTo(t, w)(), TruncateTo(f, w)())())
              case PadTo(e, _) =>
                val w0 = e.typ.asInstanceOf[TyAnyInt].w
                if (w < w0) TruncateTo(e, w)()
                else if (w == w0) e
                else PadTo(e, w)()
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
            doPartialEval(e) match {
              case v: IntCst     => ir.eval(ToSigned(v)())
              case ToUnsigned(e) =>
                // It is undefined behaviour if `e` is negative, so the partial
                // evaluator is allowed to treat this as a no-op
                e
              case e => ToSigned(e)()
            }
          case ToUnsigned(arg) =>
            doPartialEval(arg) match {
              case ToSigned(e) => e
              case Mux(c, t, f) =>
                doPartialEval(Mux(c, ToUnsigned(t)(), ToUnsigned(f)())())
              case v: IntCst =>
                try {
                  // Wrap in try/catch because the argument may not fit in the
                  // target range (and this can occur while speculatively
                  // partially evaluating one branch of a MUX to see whether it
                  // is equivalent to the other)
                  ir.eval(ToUnsigned(v)())
                } catch {
                  case _: EvalException => e
                }
              case e => ToUnsigned(e)()
            }

          case True  => True
          case False => False
          case Mux(c, t, f) =>
            doPartialEval(c)(facts) match {
              case True  => doPartialEval(t)
              case False => doPartialEval(f)
              case cond =>
                val trueE = doPartialEval(t)(facts.assumeTrue(cond))
                val falseE = doPartialEval(f)(facts.assumeFalse(cond))
                (cond, trueE, falseE) match {
                  case _ if trueE == falseE => trueE
                  case _ if trueE.typ == TyBool =>
                    doPartialEval((cond && trueE) || (!cond && falseE))
                  case (_, StmNextK(s0, k0), StmNextK(s1, k1)) if s0 == s1 =>
                    doPartialEval(StmNextK(s0, Mux(cond, k0, k1)())())
                  case (
                        Equal(Sum(IntCst(1), i0), n0),
                        c0,
                        Mux(LessThan(Sum(IntCst(1), i1), n1), c1, _)
                      )
                      if isEqual(i0, i1)(facts).getOrElse(false)
                        && isEqual(n0, n1)(facts).getOrElse(false)
                        && isSmaller(i0, n0)(facts).getOrElse(false) =>
                    doPartialEval(Mux(i0 + 1 === n0, c0, c1)().tchk().lower())
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
            val (newLhs, newRhs) = if (lhs.typ.isInstanceOf[TyAnyInt]) {
              combineConstants(
                doPartialEval(lhs)(facts),
                doPartialEval(rhs)(facts)
              )
            } else {
              (doPartialEval(lhs)(facts), doPartialEval(rhs)(facts))
            }
            ArithSimplifier.simplifyArithmetic(Equal(newLhs, newRhs)())(facts)
          case LessThan(lhs, rhs) =>
            val (newLhs, newRhs) =
              combineConstants(
                doPartialEval(lhs)(facts),
                doPartialEval(rhs)(facts)
              )
            val lt = LessThan(newLhs, newRhs)()
            ArithSimplifier.simplifyArithmetic(lt)(facts)
          case _: And =>
            ArithSimplifier.simplifyArithmetic(e.map(doPartialEval))(facts)
          case _: Or =>
            ArithSimplifier.simplifyArithmetic(e.map(doPartialEval))(facts)
          case Not(e) =>
            ArithSimplifier.simplifyArithmetic(Not(doPartialEval(e))())(facts)

          case t: Tuple =>
            Tuple(t.elems.map(doPartialEval): _*)()
          case TupleAccess(t: Expr, IntCst(i)) =>
            doPartialEval(t) match {
              case tuple: Tuple =>
                doPartialEval(tuple.elems(i.toInt))
              case Mux(c, t, f) =>
                // Move TupleAccess inside Mux in the hope that it'll
                // encounter a Tuple(...).
                // This seems reasonable even if m == HeuristicMotion, since this
                // is essentially a unary operation; therefore, the expression is
                // not likely to grow *that* large.
                doPartialEval(
                  Mux(c, TupleAccess(t, C(i)())(), TupleAccess(f, C(i)())())()
                )
              case t => TupleAccess(t, C(i)())()
            }

          case VecBuild(n, f) =>
            val newN = {
              // Be careful about cases like the following:
              //   if (n == 0) then VecBuild(n, ...) else VecBuild(n, ...)
              // The type checker must still be able to verify afterwards that
              // the two branches are compatible.
              val simplifiedN = doPartialEval(n)
              if (isEqual(simplifiedN, n)(FactSet()).getOrElse(false)) {
                simplifiedN
              } else {
                n
              }
            }
            val newFacts = facts.clearRange(f.param).between(f.param, 0, newN)
            val newF = Function(f.param, doPartialEval(f.body)(newFacts))()
            VecBuild(newN, newF)()
          case VecAccess(v, i: Expr) =>
            (doPartialEval(v), doPartialEval(i)) match {
              case (Mux(c, t, f), i) =>
                // Move VecAccess inside Mux in the hope that it'll
                // encounter a VecBuild(...)
                doPartialEval(
                  Mux(c, VecAccess(t, i)(), VecAccess(f, i)())()
                )
              case (VecBuild(_, f), i) =>
                // Out-of-bounds vector access is undefined behaviour, so just
                // don't worry about it
                val fInT = f.typ.asInstanceOf[TyArrow].t1
                doPartialEval(FunCall(f, Cast(i, fInT)())().tchk().lower())
              case (v, i) => VecAccess(v, i)()
            }

          case s @ StmBuild(n, data, valid, equations) =>
            val len = doPartialEval(n)(facts)
            val onlyElem = len match {
              case IntCst(1) =>
                // Maybe we can find the first element statically and just return it directly!
                tryEvalStmNext(s) match {
                  case Some((out, _)) if !out.contains(classOf[StmData]) =>
                    Some(doPartialEval(out)(facts))
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
                val newValid = doPartialEval(valid)(newFacts)
                StmBuild(
                  len,
                  // The value of the data doesn't matter if it is invalid, so
                  // we can assume it is valid when simplifying.
                  doPartialEval(data)(newFacts.assumeTrue(newValid)),
                  newValid,
                  equations.map({ case (x, (z, next)) =>
                    // The recurrence variables shouldn't occur free in z, so use
                    // the old facts for z
                    x -> (doPartialEval(z)(facts), doPartialEval(next)(
                      newFacts
                    ))
                  })
                )()
            }
          case StmData(s) => StmData(doPartialEval(s))()
          case StmNextK(s, k) =>
            val peStm = doPartialEval(s)
            doPartialEval(k) match {
              case IntCst(k) if k <= 0 => peStm
              case k                   => StmNextK(peStm, k)()
            }

          case _: VecLiteral | _: StmLiteral | _: SyntaxSugar =>
            e.map(doPartialEval)

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
    val eq =
      try {
        (e1 === e2).tchk().lower()
      } catch {
        case _: TypeError =>
          return None
      }
    partialEval(eq)(facts) match {
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
    partialEval(lt)(facts) match {
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
        try {
          val cst = C(k)(lhs.typ)
          (
            Sum(cst +: lhsNonCstTerms: _*)(lhs.typ),
            Sum(rhsNonCstTerms: _*)(rhs.typ)
          )
        } catch {
          case _: OverflowException =>
            (lhs, rhs)
        }
      case k =>
        try {
          val cst = C(-k)(rhs.typ)
          (
            Sum(lhsNonCstTerms: _*)(lhs.typ),
            Sum(cst +: rhsNonCstTerms: _*)(rhs.typ)
          )
        } catch {
          case _: OverflowException =>
            (lhs, rhs)
        }
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
                    Some(
                      (
                        v,
                        StmBuild(C(n - 1)(), s.data, s.valid, nextEquations)()
                      )
                    )
                  case False =>
                    tryEvalStmNext(
                      StmBuild(C(n)(), s.data, s.valid, nextEquations)(),
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
