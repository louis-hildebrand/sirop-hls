package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.ir.typecheck.{TypeCheck, TypeError}
import mhir.sugar._

import scala.annotation.tailrec

/** Partial evaluation, arithmetic simplification, and related functionality.
  */
object PartialEvalPass {

  private implicit val logger: Logger = Logger(getClass.getName)

  def pe(e: Expr): Expr = {
    // Mostly for debugging, since the debugger really doesn't seem to handle
    // implicit and default parameters well
    partialEval(e)
  }

  def partialEval(e: Expr)(implicit facts: FactSet = FactSet()): Expr = {
    @tailrec
    def fix(e: Expr, i: Int): Expr = {
      e match {
        case _: IntCst | _: FixCst | True | False =>
          e
        case e =>
          val e1 = doPartialEval(e)
          val e2 = moveMuxUpInBoolExpr(e1)
          val e3 = IntConversionMover.widen(e2)
          if (e3 == e) {
            logger.trace(
              s"finished partial evaluation after ${i + 1} iterations"
            )
            e3
          } else {
            logger.trace(s"finished iteration ${i + 1} for partial evaluation")
            fix(e3, i = i + 1)
          }
      }
    }
    logger.trace(s"starting fixpoint iteration for partial evaluation")
    fix(e.tchk(), i = 0)
  }

  private def moveMuxUpInBoolExpr(e: Expr): Expr = {
    require(e.hasType)
    if (e.typ == TyBool) {
      // For boolean expressions, be more aggressive and move MUXes up.
      // For expressions like Min(t - 5, 5) < Min(t - 4, 5), considering each
      // case separately helps.
      // This may cause the expression to explode in size, but since these
      // are booleans there should usually be opportunities for
      // simplification (e.g., both branches being True, both being False,
      // Mux(c, True, False) --> c).
      // If this ends up being problematic, we could always scan the
      // expression first and skip this if there are too many MUXes.
      MuxMover.moveUp(e).tchk()
    } else {
      e.map(moveMuxUpInBoolExpr).tchk()
    }
  }

  private def doPartialEval(expr: Expr)(implicit facts: FactSet): Expr = {
    val e = expr.tchk()
    val pe = facts.isTrue(e) match {
      case Some(true)  => True
      case Some(false) => False
      case None =>
        e match {
          case u: Undefined =>
            // Be very careful with undefined values.
            // For example, don't say that undefined + x --> undefined and
            // undefined * x --> undefined.
            // You may end up incorrectly simplifying as follows:
            //       (x => x + -1*x)(undefined)
            //   --> undefined + -1*undefined
            //   --> undefined + undefined
            //   --> undefined
            // Yet clearly the original expression will always evaluate to 0.
            u
          case x: Param =>
            facts.getRange(x) match {
              case Some(ScalarRange(Some(IntCst(lo)), Some(IntCst(hi))))
                  if lo + 1 == hi =>
                IntCst(lo)(x.typ)
              case Some(ScalarRange(Some(_: IntCst), Some(_: IntCst))) =>
                x
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
            val peArg = doPartialEval(arg)
            doPartialEval(f) match {
              case Function(x, body) =>
                assert(x.typ != Missing)
                assert(peArg.typ ~= x.typ)
                doPartialEval(body.subPreserveType(x -> peArg))
              case f => FunCall(f, peArg)()
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
          case _: WrappingSum =>
            val newChildren = e.children.map(e => doPartialEval(e))
            ArithSimplifier.simplifyArithmetic(
              e.rebuild(e.typ, newChildren)
            )(facts)
          case _: WrappingDiff =>
            val newChildren = e.children.map(e => doPartialEval(e))
            ArithSimplifier.simplifyArithmetic(
              e.rebuild(e.typ, newChildren)
            )(facts)
          case _: WrappingProd =>
            val newChildren = e.children.map(e => doPartialEval(e))
            ArithSimplifier.simplifyArithmetic(
              e.rebuild(e.typ, newChildren)
            )(facts)
          case PadTo(e, w) =>
            PadTo(doPartialEval(e), w)()
          case TruncateTo(arg, w) =>
            TruncateTo(doPartialEval(arg), w)()
          case ToSigned(e) =>
            ToSigned(doPartialEval(e))()
          case ToUnsigned(arg) =>
            ToUnsigned(doPartialEval(arg))()
          case ll @ LLShift(e1, e2) =>
            val newChildren = Seq(e1, e2).map(doPartialEval)
            ArithSimplifier
              .simplifyArithmetic(ll.rebuild(ll.typ, newChildren))(facts)
          case lr @ LRShift(e1, e2) =>
            val newChildren = Seq(e1, e2).map(doPartialEval)
            ArithSimplifier
              .simplifyArithmetic(lr.rebuild(lr.typ, newChildren))(facts)

          case c: FixCst => c
          case p @ IntFixProd(e1, e2) =>
            val newChildren = Seq(e1, e2).map(doPartialEval)
            ArithSimplifier
              .simplifyArithmetic(p.rebuild(p.typ, newChildren))(facts)

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
                  case (_, _, _: Undefined) => trueE
                  case (_, _: Undefined, _) => falseE
                  case _ if trueE.typ == TyBool =>
                    ArithSimplifier.simplifyArithmetic(
                      (cond && trueE) || (!cond && falseE)
                    )(facts)
                  case (Equal(e0, IntCst(hi)), IntCst(lo), Sum(IntCst(1), e1))
                      if e0 == e1
                        && e1.typ.asInstanceOf[TyAnyInt].maxInt == hi
                        && e1.typ.asInstanceOf[TyAnyInt].minInt == lo =>
                    WrappingSum(e1, C(1)(e1.typ))()
                  case (_, StmNextK(s0, k0), StmNextK(s1, k1)) if s0 == s1 =>
                    doPartialEval(StmNextK(s0, Mux(cond, k0, k1)())())
                  case _ if trueBranchIsMoreGeneral(cond, trueE, falseE) =>
                    trueE
                  case _ if falseBranchIsMoreGeneral(cond, trueE, falseE) =>
                    falseE
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
                tuple.elems(i.toInt)
              case Undefined(TyTuple(ts @ _*)) =>
                Undefined(ts(i.toInt))
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
              doPartialEval(n)(FactSet())
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
              case (VecLiteral(elems @ _*), IntCst(i)) =>
                elems(i.toInt)
              case (VecAccess(VecLiteral(elems @ _*), i), j: IntCst) =>
                val newElems = elems.map(e => doPartialEval(VecAccess(e, j)()))
                VecAccess(VecLiteral(newElems: _*)(), i)()
              case (VecBuild(_, f), i) =>
                // Out-of-bounds vector access is undefined behaviour, so just
                // don't worry about it
                val fInT = f.typ.asInstanceOf[TyArrow].t1
                doPartialEval(FunCall(f, Cast(i, fInT)())().tchk().lower())
              case (Undefined(TyVec(t, _)), _) =>
                Undefined(t)
              case (v, i) => VecAccess(v, i)()
            }

          case s @ StmBuild(n, data, valid, equations) =>
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
              doPartialEval(n)(facts),
              // The value of the data doesn't matter if it is invalid, so
              // we can assume it is valid when simplifying.
              doPartialEval(data)(newFacts.assumeTrue(newValid)),
              newValid,
              equations.map({ case (x, (z, next)) =>
                // The recurrence variables shouldn't occur free in z, so use
                // the old facts for z
                x -> (
                  doPartialEval(z)(facts),
                  doPartialEval(next)(newFacts)
                )
              })
            )()
          case StmData(s) => StmData(doPartialEval(s))()
          case LetStm(bufSize, x, in, out) =>
            LetStm(
              doPartialEval(bufSize),
              x,
              doPartialEval(in),
              doPartialEval(out)
            )()
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

  private def trueBranchIsMoreGeneral(
      c: Expr,
      t: Expr,
      f: Expr
  )(implicit facts: FactSet): Boolean = {
    if (!t.typ.isData) {
      false
    } else {
      assert(t.typ != Missing)
      val eq = Equal(t, f)().tchk()
      val newFacts = facts.assumeFalse(c)
      ArithSimplifier.simplifyArithmetic(eq)(newFacts) == True
    }
  }

  private def falseBranchIsMoreGeneral(
      c: Expr,
      t: Expr,
      f: Expr
  )(implicit facts: FactSet): Boolean = {
    if (!t.typ.isData) {
      false
    } else {
      assert(t.typ != Missing)
      val eq = Equal(t, f)().tchk()
      val newFacts = facts.assumeTrue(c)
      ArithSimplifier.simplifyArithmetic(eq)(newFacts) == True
    }
  }

  def isEqual(e1: Expr, e2: Expr)(
      facts: FactSet = FactSet()
  ): Option[Boolean] = {
    if (e1 == e2) {
      Some(true)
    } else {
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

  def partialEvalStmBuild(e: Expr): Expr = {
    // Make this a separate method because it can be extremely slow in some
    // cases and it's rarely useful
    e match {
      case StmBuild(IntCst(1), data, valid, eqns) =>
        val newEquations = eqns.map({
          case (x, (z, next)) if !x.typ.isInstanceOf[TyStm] => x -> (z, next)
          case (x, (stm, ready)) => x -> (partialEvalStmBuild(stm), ready)
        })
        val s = StmBuild(1, data, valid, newEquations)()
          .tchk()
          .asInstanceOf[StmBuild]
        // Maybe we can find the first element statically and just return it directly!
        val onlyElem = tryEvalStmNext(s)(FactSet()) match {
          case Some((out, _)) if !out.contains(classOf[StmData]) =>
            Some(out)
          case _ => None
        }
        onlyElem match {
          case Some(e) => StmBuild(1, e, True, Map[Param, (Expr, Expr)]())()
          case None    => s
        }
      case e =>
        e.map(partialEvalStmBuild)
    }
  }

  private def tryEvalStmNext(
      stm: StmBuild,
      stepsWithoutValid: Int = 0
  )(implicit facts: FactSet): Option[(Expr, StmBuild)] = {
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
                  doPartialEval(next.subPreserveType(currentValByVar)) match {
                    case False =>
                      val t = x.typ.asInstanceOf[TyStm].t
                      val head = mhir.ir.eval(Default(t))
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
                    val evaluatedNext =
                      doPartialEval(next.subPreserveType(subs))
                    x -> (evaluatedNext, next)
                })
                val evaluatedValid = doPartialEval(
                  s.valid.subPreserveType(subs)
                )
                evaluatedValid match {
                  case True =>
                    val v = doPartialEval(s.data.subPreserveType(subs))
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
