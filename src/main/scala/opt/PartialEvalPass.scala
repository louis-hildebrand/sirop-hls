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
      case And(e1, e2) => this.isTrue(e1).isTrue(e2)
      // TODO: Add some more cases?
      case LessThan(x: Param, c: IntCst) =>
        this.range(x, ScalarRange(None, Some(c)))
      case LessThan(IntCst(c), x: Param) =>
        this.range(x, ScalarRange(Some(c + 1), None))
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
      case Or(e1, e2) => this.isFalse(e1).isFalse(e2)
      // TODO: Add some more cases?
      case LessThan(x: Param, c: IntCst) =>
        this.range(x, ScalarRange(Some(c), None))
      case LessThan(IntCst(c), x: Param) =>
        this.range(x, ScalarRange(None, Some(c + 1)))
      case _ => this
    }
  }
}

object PartialEvalPass {
  def partialEval(e: Expr)(implicit facts: FactSet = FactSet()): Expr = {
    e match {
      case t: Tuple => Tuple(t.elems.map(partialEval): _*)
      case TupleAccess(t: Expr, i: Expr) =>
        (partialEval(t), partialEval(i)) match {
          case (tuple: Tuple, index: IntCst) =>
            partialEval(tuple.elems(index.i))
          case (IfThenElse(c, t, f), i) =>
            // Move TupleAccess inside IfThenElse in the hope that it'll
            // encounter a Tuple(...)
            partialEval(IfThenElse(c, TupleAccess(t, i), TupleAccess(f, i)))
          case (DontCare, _) | (_, DontCare) => DontCare
          case (tuple @ _, index @ _)        => TupleAccess(tuple, index)
        }

      case p: Param => p
      case Function(p, body) =>
        Function(p, partialEval(body)(facts.clearRange(p)))
      case FunCall(f: Expr, arg: Expr) =>
        partialEval(f) match {
          case Function(x, body) =>
            partialEval(body.substitute(x -> partialEval(arg)))
          case DontCare => DontCare
          case fun @ _  => FunCall(fun, partialEval(arg))
        }

      case Sum(terms) =>
        ArithSimplifier.simplifyArithmetic(
          Sum(terms.map(e => partialEval(e)): _*)
        )(facts)
      case Prod(factors) =>
        ArithSimplifier.simplifyArithmetic(
          Prod(factors.map(e => partialEval(e)): _*)
        )(facts)
      case Div(e1: Expr, e2: Expr) =>
        ArithSimplifier.simplifyArithmetic(
          Div(partialEval(e1), partialEval(e2))
        )(facts)
      case Mod(e1: Expr, e2: Expr) =>
        ArithSimplifier.simplifyArithmetic(
          Mod(partialEval(e1), partialEval(e2))
        )(facts)
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
                  partialEval(trueE)(newFacts)
                }
                val f = {
                  val newFacts = facts.isFalse(cond)
                  partialEval(falseE)(newFacts)
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
        // TODO: Add a case for when at least one of the arguments is an IfThenElse?
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)      => e1.i == e2.i
          case (DontCare, _) | (_, DontCare) => DontCare
          case (False, e)                    => Not(e)
          case (e, False)                    => Not(e)
          case (True, e)                     => e
          case (e, True)                     => e
          case (e1 @ _, e2 @ _)              => Equal(e1, e2)
        }
      case LessThan(e1: Expr, e2: Expr) =>
        ArithSimplifier.simplifyArithmetic(
          LessThan(partialEval(e1), partialEval(e2))
        )(facts)
      case And(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (False, _)                    => False
          case (_, False)                    => False
          case (True, e)                     => e
          case (e, True)                     => e
          case (DontCare, _) | (_, DontCare) => DontCare
          case (e1, e2)                      => And(e1, e2)
        }
      case Or(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (True, _) | (_, True)         => True
          case (False, e)                    => e
          case (DontCare, _) | (_, DontCare) => DontCare
          case (e, False)                    => e
          case (e1, e2)                      => Or(e1, e2)
        }
      case Not(e: Expr) =>
        partialEval(e) match {
          case True     => False
          case False    => True
          case DontCare => DontCare
          case Not(e)   => e
          case e        => Not(e)
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
              Function(f.param, partialEval(f.body)(newFacts))
            )
        }

      case StmLength(s) =>
        partialEval(s) match {
          case s: StmBuild => partialEval(s.length)
          case DontCare    => DontCare
          case s @ _       => StmLength(s)
        }

      case StmNext(s) =>
        partialEval(s) match {
          case s: StmBuild =>
            tryEvalStmNext(s) match {
              case Some((nextStm, out)) => Tuple(nextStm, out)
              case None                 => StmNext(s)
            }
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
            VecBuild(n, Function(f.param, partialEval(f.body)(newFacts)))
        }
      case VecAccess(vec: Expr, i: Expr) =>
        partialEval(vec) match {
          case vec: VecBuild => partialEval(FunCall(vec.f, partialEval(i)))
          case DontCare      => DontCare
          case vec @ _       => VecAccess(vec, partialEval(i))
        }
      case VecLength(vec: Expr) =>
        partialEval(vec) match {
          case vec: VecBuild => partialEval(vec.len)
          case DontCare      => DontCare
          case vec @ _       => VecLength(vec)
        }
    }
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
      case True | False | And(_, _) | Or(_, _) | Not(_) | Equal(_, _) |
          LessThan(_, _) =>
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
      case And(x, y) => splitAnd(x) ++ splitAnd(y)
      case e         => Seq(e)
    }
  }

  private def splitOr(e: Expr): Seq[Expr] = {
    // TODO: Convert to SOP form first?
    e match {
      case Or(x, y) => splitOr(x) ++ splitOr(y)
      case e        => Seq(e)
    }
  }
}
