package opt

import ir._

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

      case p: Param          => p
      case Function(p, body) => Function(p, partialEval(body))
      case FunCall(f: Expr, arg: Expr) =>
        partialEval(f) match {
          case fun: Function =>
            partialEval(
              substitute(fun.body)(Map(fun.param -> partialEval(arg)))
            )
          case DontCare => DontCare
          case fun @ _  => FunCall(fun, partialEval(arg))
        }

      case Sum(terms) =>
        ArithSimplifier.simplifyArithmetic(
          Sum(terms.map(e => partialEval(e)): _*)
        )
      case Prod(factors) =>
        ArithSimplifier.simplifyArithmetic(
          Prod(factors.map(e => partialEval(e)): _*)
        )
      case Div(e1: Expr, e2: Expr) =>
        ArithSimplifier.simplifyArithmetic(
          Div(partialEval(e1), partialEval(e2))
        )
      case Mod(e1: Expr, e2: Expr) =>
        ArithSimplifier.simplifyArithmetic(
          Mod(partialEval(e1), partialEval(e2))
        )
      case IntCst(_) => e

      case True  => True
      case False => False
      case IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) =>
        partialEval(cond) match {
          case True     => partialEval(trueE)
          case False    => partialEval(falseE)
          case DontCare => DontCare
          case cond     =>
            // If (x0 && ... && xn) = True, then xi = True for each i
            val t = partialEval(
              substitute(trueE)(splitAnd(cond).map(e => e -> True).toMap)
            )
            // If (x0 || ... || xn) = False, then xi = False for each i
            val f = partialEval(
              substitute(falseE)(splitOr(cond).map(e => e -> False).toMap)
            )
            if (f == DontCare) t
            else if (t == DontCare) f
            else if (t == f) t
            else if (t == True && f == False) cond
            else if (t == False && f == True) partialEval(Not(cond))
            else {
              cond match {
                // True branch is special case of false branch
                case Equal(p: Param, r)
                    if partialEval(substitute(f)(Map(p -> r))) == t =>
                  f
                // False branch is special case of true branch
                case Not(Equal(p: Param, r))
                    if partialEval(substitute(t)(Map(p -> r))) == f =>
                  t
                case _ =>
                  val x = IfThenElse(cond, t, f)
                  if (isBoolExpr(x).getOrElse(false) && !hasSideEffects(x)) {
                    partialEval((cond && t) || (Not(cond) && f))
                  } else {
                    x
                  }
              }
            }
        }
      case Equal(e1: Expr, e2: Expr) =>
        // TODO: Add a case for when at least one of the arguments is an IfThenElse?
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)      => e1.i == e2.i
          case (DontCare, _) | (_, DontCare) => DontCare
          case (e1 @ _, e2 @ _)              => Equal(e1, e2)
        }
      case LessThan(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)      => e1.i < e2.i
          case (DontCare, _) | (_, DontCare) => DontCare
          case (e1, e2) =>
            tryGetRange(partialEval(e1 - e2))(facts) match {
              case Some(ScalarRange(_, Finite(upper))) if upper < 0  => True
              case Some(ScalarRange(Finite(lower), _)) if lower >= 0 => False
              case _ => LessThan(e1, e2)
            }
        }
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

      case StmBuild(length, seed, f) =>
        StmBuild(
          partialEval(length),
          partialEval(seed),
          // ensures any free Param in f gets substituted
          partialEval(f).asInstanceOf[Function]
        )

      case StmLength(s) =>
        partialEval(s) match {
          case s: StmBuild => partialEval(s.length)
          case DontCare    => DontCare
          case s @ _       => StmLength(s)
        }

      case StmNext(s: Expr) =>
        partialEval(s) match {
          case s: StmBuild =>
            s.length match {
              case IntCst(len) =>
                assert(len > 0, "Attempt to call StmNext() on an empty stream.")
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
                        Tuple(
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
                        partialEval(
                          StmNext(
                            StmBuild(
                              s.length,
                              partialEval(next.__0),
                              // this function may have free parameters
                              partialEval(s.nextF).asInstanceOf[Function]
                            )
                          )
                        )
                      case _ =>
                        StmNext(s)
                    }
                  case _ => StmNext(s)
                }
              case _ => StmNext(s)
            }
          case s => StmNext(s)
        }

      case VecBuild(len: Expr, f) =>
        VecBuild(
          partialEval(len),
          partialEval(f) /* ensures any free Param in f gets substituted */
        )
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

  // TODO: just use the type checker for this
  private def isBoolExpr(e: Expr): Option[Boolean] = {
    e match {
      // Definitely evaluates to a bool
      case True | False | And(_, _) | Or(_, _) | Not(_) | Equal(_, _) |
          LessThan(_, _) =>
        Some(true)
      // Definitely not a bool
      case _: Tuple | _: StmBuild | _: VecBuild | _: IntExpr | _: Function =>
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
      case StmNext(_)      => None
      case VecAccess(_, _) => None
    }
  }

  private def hasSideEffects(e: Expr): Boolean = {
    e match {
      case _: Function     => false
      case _: StmLength    => false
      case _: StmNext      => true
      case _: VecBuild     => false
      case _: VecAccess    => false
      case _: VecLength    => false
      case FunCall(f, arg) => ???
      case _: StmBuild     => ???
      case e               => e.children.exists(c => hasSideEffects(c))
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

  private def tryGetRange(
      e: Expr
  )(implicit facts: FactSet): Option[Range] = {
    e match {
      // TODO: Do I need to specially handle tuples and vectors?
      case Tuple(elems @ _*) => Some(TupleRange(elems.map(e => tryGetRange(e))))
      case TupleAccess(t, IntCst(i)) =>
        tryGetRange(t) match {
          case Some(TupleRange(elemRanges)) => elemRanges(i)
          case _                            => None
        }
      case TupleAccess(t, i) =>
        // In theory, I could take the union of all the element ranges.
        // But this requires checking that they all have the same shape, and
        // what are the odds that this scenario will ever happen?
        None
      case _: VecAccess => None
      case x: Param     => facts.rangeByParam.get(x)
      case _: FunCall   =>
        // If this is a call to a function literal, then we could look for
        // bounds on the function body using facts about the argument, but the
        // partial evaluator will evaluate such function calls anyway so they
        // probably won't appear here
        None
      case IntCst(c)  => Some(ScalarRange(Finite(c), Finite(c)))
      case Sum(terms) =>
        //   If a1 <= x1 <= b1
        //  and a2 <= x2 <= b2
        // then a1 + a2 <= x1 + x2 <= b1 + b2
        val termRanges = terms.map(e =>
          tryGetRange(e).getOrElse(ScalarRange.full()).asInstanceOf[ScalarRange]
        )
        val r = termRanges.foldLeft(ScalarRange(0, 0))((acc, r) =>
          ScalarRange(acc.lower + r.lower, acc.upper + r.upper)
        )
        Some(r)
      case Prod(Seq(IntCst(c), x)) =>
        tryGetRange(x) match {
          case Some(r: ScalarRange) =>
            if (c >= 0) {
              //   If a <= x <= b
              //  and c >= 0
              // then c * a <= c * x <= c * b
              Some(ScalarRange(r.lower * c, r.upper * c))
            } else {
              //   If a <= x <= b
              //  and c < 0
              // then c * b <= c * x <= c * a
              Some(ScalarRange(r.upper * c, r.lower * c))
            }
          case _ => Some(ScalarRange.full())
        }
      case Prod(factors) =>
        // TODO: Implement the general case?
        // Suppose a1 <= x1 <= b1 and a2 <= x2 <= b2,
        //   where a1, a2 < 0
        //     and b1, b2 > 0
        //
        // Then [a1, b1] = {a1, -1} U {0} U {1, b1}
        //  and [a2, b2] = {a2, -1} U {0} U {1, b2}
        //
        // Find bounds per quadrant and then take the union.
        // It seems a bit gross to handle all the cases though :(
        // Furthermore, need to consider upper or lower bounds being missing
        // (infinite), rectangle not containing (0, 0), and so on.
        // Moreover, need to extend the result to more than two factors
        // (shouldn't be terribly hard, but it's extra tedium).
        Some(ScalarRange.full())
      case _: Div              => Some(ScalarRange.full())
      case _: Mod              => Some(ScalarRange.full())
      case IfThenElse(_, t, f) =>
        // TODO: Should I be using the condition here?
        (tryGetRange(t), tryGetRange(f)) match {
          case (Some(r1: ScalarRange), Some(r2: ScalarRange)) =>
            Some(r1.union(r2))
          case _ => None
        }
      case _: StmLength =>
        // If the partial evaluator didn't get rid of this, then what am I
        // supposed to do now?
        Some(ScalarRange.full())
      case _: VecLength =>
        // If the partial evaluator didn't get rid of this, then what am I
        // supposed to do now?
        Some(ScalarRange.full())
      case _: Tuple | _: Function | _: BoolExpr | DontCare | _: StmBuild |
          _: StmNext | _: VecBuild =>
        None
    }
  }
}
