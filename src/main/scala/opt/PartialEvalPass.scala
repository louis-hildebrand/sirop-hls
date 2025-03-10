package opt

import ir._

import scala.annotation.tailrec

case class FactSet(rangeByExpr: Map[Expr, Range] = Map()) {

  /** Update the range information for <code>x</code>.
    *
    * @param e
    *   An expression. This could be a variable, a tuple access
    *   (<code>acc._1</code>), etc. However, it must <i>not</i> be an arithmetic
    *   expression (a sum, a product, etc.).
    * @param r
    *   A range
    * @return
    *   A fact set where the range of <code>x</code> is <code>r</code>.
    */
  def range(e: Expr, r: Range): FactSet = {
    val updatedRange = rangeByExpr.get(e) match {
      case Some(oldRange) => oldRange.merge(r)
      case _              => r
    }
    FactSet(rangeByExpr + (e -> updatedRange))
  }

  /** Construct a new fact set in which the range of <code>e</code> is entirely
    * unknown.
    */
  def clearRange(e: Expr): FactSet = {
    FactSet(rangeByExpr.filter({ case (k, _) => !k.contains(e) }))
  }

  /** Construct a new fact set taking into account that <code>e</code> evaluates
    * to <code>True</code>.
    */
  def isTrue(e: Expr): FactSet = {
    e match {
      case Not(e) => isFalse(e)
      // If (x0 && ... && xn) = True, then xi = True for each i
      case And(terms @ _*) =>
        terms.foldLeft(this)({ case (acc, e) => acc.isTrue(e) })
      // TODO: Add some more cases?
      case LessThan(e1, e2) =>
        PartialEvalPass.partialEval(e1 - e2)(this) match {
          case Sum(Seq(IntCst(c), x: Param)) =>
            // c + x < 0 <==> x < -c
            this.range(x, ScalarRange(None, Some(-c)))
          case Sum(Seq(IntCst(c), Prod(Seq(IntCst(-1), x: Param)))) =>
            // c - x < 0 <==> x > c ==> x >= c + 1
            this.range(x, ScalarRange(Some(c + 1), None))
          case x: Param =>
            // x < 0
            this.range(x, ScalarRange(None, Some(0)))
          case _ => this
        }
      case Equal(x: Param, IntCst(c)) =>
        this.range(x, ScalarRange(Some(c), Some(c + 1)))
      case _ => this
    }
  }

  /** Construct a new fact set taking into account that <code>e</code> evaluates
    * to <code>False</code>.
    */
  def isFalse(e: Expr): FactSet = {
    e match {
      case Not(e) => isTrue(e)
      // If (x0 || ... || xn) = False, then xi = False for each i
      case Or(terms @ _*) =>
        terms.foldLeft(this)({ case (acc, e) => acc.isFalse(e) })
      // TODO: Add some more cases?
      case LessThan(e1, e2) =>
        PartialEvalPass.partialEval(e1 - e2)(this) match {
          case Sum(Seq(IntCst(c), x: Param)) =>
            // !(c + x < 0) <==> c + x >= 0 <==> x >= -c
            this.range(x, ScalarRange(Some(-c), None))
          case Sum(Seq(IntCst(c), Prod(Seq(IntCst(-1), x: Param)))) =>
            // !(c - x < 0) <==> c - x >= 0 <==> x <= c <==> x < c + 1
            this.range(x, ScalarRange(None, Some(c + 1)))
          case x: Param =>
            // !(x < 0) <==> x >= 0
            this.range(x, ScalarRange(Some(0), None))
          case _ => this
        }
      case _ => this
    }
  }
}

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
    e match {
      case t: Tuple =>
        // TODO: Move up?
        Tuple(t.elems.map(partialEval): _*)
      case TupleAccess(t: Expr, i: Expr) =>
        (partialEval(t), partialEval(i)) match {
          case (DontCare, _) | (_, DontCare) => DontCare
          case (tuple: Tuple, index: IntCst) =>
            partialEval(tuple.elems(index.i))
          case (IfThenElse(c, t, f), i) =>
            // Move TupleAccess inside IfThenElse in the hope that it'll
            // encounter a Tuple(...).
            // This seems reasonable even if m == MoveDown, since this is a
            // unary operation; therefore, the expression is not likely to grow
            // *that* large.
            partialEval(IfThenElse(c, TupleAccess(t, i), TupleAccess(f, i)))
          case (tuple @ _, index @ _) => TupleAccess(tuple, index)
        }

      case p: Param => p
      case Function(p, body) =>
        Function(p, partialEval(body)(facts.clearRange(p), m))
      case FunCall(f: Expr, arg: Expr) =>
        partialEval(f) match {
          case Function(x, body) =>
            partialEval(body.substitute(x -> partialEval(arg)))
          case DontCare => DontCare
          case fun @ _  => FunCall(fun, partialEval(arg))
        }

      case Sum(terms) =>
        val newChildren = terms.map(e => partialEval(e))
        m match {
          case MoveUp =>
            mergeIfThenElses(newChildren, x => y => x + y) match {
              case ite: IfThenElse => partialEval(ite)
              case e => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case HeuristicMotion =>
            ArithSimplifier.simplifyArithmetic(Sum(newChildren: _*))(facts)
        }
      case Prod(factors) =>
        val newChildren = factors.map(e => partialEval(e))
        m match {
          case MoveUp =>
            mergeIfThenElses(newChildren, x => y => x * y) match {
              case ite: IfThenElse => partialEval(ite)
              case e => ArithSimplifier.simplifyArithmetic(e)(facts)
            }
          case HeuristicMotion =>
            ArithSimplifier.simplifyArithmetic(Prod(newChildren: _*))(facts)
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
      case IntCst(_) => e

      case True  => True
      case False => False
      case ite: IfThenElse =>
        ArithSimplifier.simplifyArithmetic(ite)(facts) match {
          case IfThenElse(cond, trueE, falseE) =>
            partialEval(cond) match {
              case True     => partialEval(trueE)
              case False    => partialEval(falseE)
              case DontCare => DontCare
              case cond =>
                val t = {
                  val newFacts = facts.isTrue(cond)
                  val t = splitAnd(cond).foldLeft(trueE)({ case (acc, c) =>
                    acc.substitute(c -> True)
                  })
                  partialEval(t)(newFacts, m)
                }
                val f = {
                  val newFacts = facts.isFalse(cond)
                  val f = splitOr(cond).foldLeft(falseE)({ case (acc, c) =>
                    acc.substitute(c -> False)
                  })
                  partialEval(f)(newFacts, m)
                }
                (t, f) match {
                  case (_, DontCare) => t
                  case (DontCare, _) => f
                  case _ if t == f   => t
                  case (True, False) => cond
                  case (False, True) => partialEval(Not(cond))
                  case (StmNextK(s0, k0), StmNextK(s1, k1)) if s0 == s1 =>
                    partialEval(StmNextK(s0, IfThenElse(cond, k0, k1)))
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
                        val x = IfThenElse(cond, t, f)
                        if (
                          isBoolExpr(x).getOrElse(false) && !hasSideEffects(x)
                        ) {
                          partialEval((cond && t) || (Not(cond) && f))
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
      case Equal(e1: Expr, e2: Expr) =>
        // For Equal() and LessThan(), move IfThenElse up so that we can deal with things like
        // Min(t - 5, 5) < Min(t - 4, 5). This may cause the expression to explode in size, but since these are
        // booleans I imagine we'll often find opportunities for simplification (e.g., both branches being True, both
        // being False, IfThenElse(c, True, False) --> c).
        // If this ends up being problematic, we could always try putting a cap on the number of unique conditions in
        // an IfThenElse and avoid this transformation if the cap is exceeded.
        val newChildren =
          Seq(partialEval(e1)(facts, MoveUp), partialEval(e2)(facts, MoveUp))
        mergeIfThenElses(newChildren, x => y => x === y) match {
          case ite: IfThenElse => partialEval(ite)
          case e               => ArithSimplifier.simplifyArithmetic(e)(facts)
        }
      case LessThan(e1: Expr, e2: Expr) =>
        val newChildren =
          Seq(partialEval(e1)(facts, MoveUp), partialEval(e2)(facts, MoveUp))
        mergeIfThenElses(newChildren, x => y => x < y) match {
          case ite: IfThenElse => partialEval(ite)
          case e               => ArithSimplifier.simplifyArithmetic(e)(facts)
        }
      case And(terms @ _*) =>
        val newChildren = terms.map(e => partialEval(e))
        val e = m match {
          case MoveUp =>
            mergeIfThenElses(newChildren, x => y => x && y) match {
              case ite: IfThenElse => Left(partialEval(ite))
              case e               => Right(e)
            }
          case HeuristicMotion => Right(And(newChildren: _*))
        }
        e match {
          case Left(e) => e
          case Right(and: And) =>
            and.remove(True) match {
              case And()                                       => True
              case And(e)                                      => e
              case And(terms @ _*) if terms.contains(DontCare) => DontCare
              case And(terms @ _*) if terms.contains(False)    => False
              case And(LessThan(e1, IntCst(c1)), LessThan(e2, IntCst(c2)))
                  if e1 == e2 =>
                LessThan(e1, math.min(c1, c2))
              case And(LessThan(IntCst(c1), e1), LessThan(IntCst(c2), e2))
                  if e1 == e2 =>
                LessThan(math.max(c1, c2), e1)
              case e => e
            }
          case Right(e) => e
        }
      case Or(terms @ _*) =>
        val newChildren = terms.map(e => partialEval(e))
        val e = m match {
          case MoveUp =>
            mergeIfThenElses(newChildren, x => y => x || y) match {
              case ite: IfThenElse => Left(partialEval(ite))
              case e               => Right(e)
            }
          case HeuristicMotion => Right(Or(newChildren: _*))
        }
        e match {
          case Left(e) => e
          case Right(or: Or) =>
            or.remove(False) match {
              case Or()                                       => False
              case Or(e)                                      => e
              case Or(terms @ _*) if terms.contains(DontCare) => DontCare
              case Or(terms @ _*) if terms.contains(True)     => True
              case Or(LessThan(e1, IntCst(c1)), LessThan(e2, IntCst(c2)))
                  if e1 == e2 =>
                LessThan(e1, math.max(c1, c2))
              case Or(LessThan(IntCst(c1), e1), LessThan(IntCst(c2), e2))
                  if e1 == e2 =>
                LessThan(math.min(c1, c2), e1)
              case e => e
            }
          case Right(e) => e
        }
      case Not(e: Expr) =>
        partialEval(e) match {
          case True                => False
          case False               => True
          case DontCare            => DontCare
          case Not(e)              => e
          case IfThenElse(c, t, f) =>
            // Move the IfThenElse up in every case because
            //  (1) the Not() may cancel with something further down and
            //  (2) since this is a unary operator, the expression is unlikely
            //      to grow *that* big.
            partialEval(IfThenElse(c, Not(t), Not(f)))
          case e =>
            ArithSimplifier.simplifyArithmetic(Not(e))(facts)
        }

      case DontCare => DontCare

      case s @ StmBuild(length, seed, f) =>
        val len = partialEval(length)
        val onlyElem = len match {
          case IntCst(1) =>
            // Maybe we can find the first element statically and just return it directly!
            tryEvalStmNext(s) match {
              case Some((_, out)) if !hasSideEffects(out) => Some(out)
              case _                                      => None
            }
          case _ =>
            None
        }
        onlyElem match {
          case Some(e) =>
            StmBuild(1, Tuple(), (_: Expr) => Tuple(Tuple(), SSome(e)))
          case None =>
            // Do the actual analysis to find the ranges outside the partial evaluator because doing it in the partial
            // evaluator is waaaay too slow. In many cases, it's not needed.
            val accRanges = facts.rangeByExpr.get(s) match {
              case Some(StmAccRange(accRanges)) => accRanges
              case _                            => Seq()
            }
            val acc = f.param
            val newFacts =
              accRanges.zipWithIndex.foldLeft(facts.clearRange(acc))({
                case (facts, (r, i)) =>
                  facts.range(TupleAccess(acc, i), r)
              })
            StmBuild(
              len,
              partialEval(seed),
              Function(f.param, partialEval(f.body)(newFacts, m))
            )
        }
      case StmLength(s) =>
        partialEval(s) match {
          case s: StmBuild         => partialEval(s.length)
          case DontCare            => DontCare
          case IfThenElse(c, t, f) =>
            // Move the IfThenElse up in every case because
            //  (1) the StmLength() may cancel with something further down and
            //  (2) since this is a unary operator, the expression is unlikely
            //      to grow *that* big.
            partialEval(IfThenElse(c, StmLength(t), StmLength(f)))
          case s @ _ => StmLength(s)
        }
      case StmNext(s) =>
        partialEval(s) match {
          case s: StmBuild =>
            tryEvalStmNext(s) match {
              case Some((nextStm, out)) => Tuple(nextStm, out)
              case None                 => StmNext(s)
            }
          case IfThenElse(c, t, f) =>
            // Move the IfThenElse up in every case because
            //  (1) the StmNext() may cancel with something further down and
            //  (2) since this is a unary operator, the expression is unlikely
            //      to grow *that* big.
            partialEval(IfThenElse(c, StmNext(t), StmNext(f)))
          case s => StmNext(s)
        }
      case StmNextK(s, k) =>
        (partialEval(s), partialEval(k)) match {
          // There are probably some cases where we want to convert StmNextK(s, k + 1) to StmNext(StmNextK(s, k)).__0
          // and other times where we want to go the other way.
          // Therefore, don't handle that here.
          case (s, k) => StmNextK(s, k)
        }

      case VecBuild(n, f) =>
        partialEval(n) match {
          case n =>
            val indexRange = ScalarRange(Some(0), Some(n))
            val newFacts = facts.clearRange(f.param).range(f.param, indexRange)
            VecBuild(n, Function(f.param, partialEval(f.body)(newFacts, m)))
        }
      case VecLength(v) =>
        partialEval(v) match {
          case VecBuild(n, _)      => partialEval(n)
          case DontCare            => DontCare
          case IfThenElse(c, t, f) =>
            // Move the IfThenElse up in every case because
            //  (1) the VecLength() may cancel with something further down and
            //  (2) since this is a unary operator, the expression is unlikely
            //      to grow *that* big.
            partialEval(IfThenElse(c, VecLength(t), VecLength(f)))
          case v => VecLength(v)
        }
      case VecAccess(vec: Expr, i: Expr) =>
        (partialEval(vec), partialEval(i)) match {
          case (DontCare, _) | (_, DontCare) => DontCare
          case (IfThenElse(c, t, f), i)      =>
            // Move VecAccess inside IfThenElse in the hope that it'll
            // encounter a VecBuild(...)
            partialEval(IfThenElse(c, VecAccess(t, i), VecAccess(f, i)))
          case (v: VecBuild, i) => partialEval(FunCall(v.f, i))
          case (v, i)           => VecAccess(v, i)
        }
    }
  }

  private def mergeIfThenElses(
      exprs: Seq[Expr],
      op: Expr => Expr => Expr
  ): Expr = {
    exprs.tail.foldLeft(exprs.head)({ case (acc, e) =>
      (acc, e) match {
        case (IfThenElse(c1, t1, f1), IfThenElse(c2, t2, f2)) if (c1 == c2) =>
          IfThenElse(c1, op(t1)(t2), op(f1)(f2))
        case (IfThenElse(c1, t1, f1), IfThenElse(c2, t2, f2)) =>
          IfThenElse(
            c1,
            IfThenElse(c2, op(t1)(t2), op(t1)(f2)),
            IfThenElse(c2, op(f1)(t2), op(f1)(f2))
          )
        case (e1, IfThenElse(c, t, f)) =>
          IfThenElse(c, op(e1)(t), op(e1)(f))
        case (IfThenElse(c, t, f), e2) =>
          IfThenElse(c, op(t)(e2), op(f)(e2))
        case (e1, e2) =>
          op(e1)(e2)
      }
    })
  }

  @tailrec
  private def tryEvalStmNext(s: StmBuild): Option[(StmBuild, Expr)] = {
    s.length match {
      case IntCst(len) =>
        require(len > 0, "Attempt to call StmNext() on an empty stream.")
        partialEval(FunCall(s.nextF, s.seed)) match {
          case next: Tuple =>
            val n = next.elems.length
            require(
              n == 2,
              s"The function in StmBuild returned a ${n}-tuple instead of a 2-tuple."
            )
            partialEval(next.__1) match {
              case Tuple(e, True) =>
                // return the new stream and element
                Some(
                  StmBuild(
                    len - 1,
                    partialEval(next.__0),
                    // this function may have free parameters
                    partialEval(s.nextF).asInstanceOf[Function]
                  ),
                  partialEval(e)
                )
              case Tuple(_, False) =>
                // skip this element, look for the next one
                tryEvalStmNext(
                  StmBuild(
                    s.length,
                    partialEval(next.__0),
                    // this function may have free parameters
                    partialEval(s.nextF).asInstanceOf[Function]
                  )
                )
              case _ => None
            }
          case _ => None
        }
      case _ => None
    }
  }

  // TODO: just use the type checker for this
  private def isBoolExpr(e: Expr): Option[Boolean] = {
    e match {
      // Definitely evaluates to a bool
      case True | False | _: And | _: Or | _: Not | _: Equal | LessThan(_, _) =>
        Some(true)
      // Definitely not a bool
      case _: Tuple | _: StmBuild | _: VecBuild | _: IntExpr | _: Function |
          _: StmNext | _: StmNextK | _: VecLiteral | _: StmLiteral =>
        Some(false)
      // Not sure
      case _: Param | DontCare => None
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
      case VecAccess(_, _) => None
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
