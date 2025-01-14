package opt

import ir.*

object PartialEvalPass {

  def contains(e1: Expr, e2: Expr): Boolean = {
    e1 match {
      case _ if e1 == e2                                  => true
      case DontCare | True | False | _: IntCst | _: Param => false
      case Add(x, y)      => contains(x, e2) || contains(y, e2)
      case Sub(x, y)      => contains(x, e2) || contains(y, e2)
      case Mul(x, y)      => contains(x, e2) || contains(y, e2)
      case Div(x, y)      => contains(x, e2) || contains(y, e2)
      case Mod(x, y)      => contains(x, e2) || contains(y, e2)
      case Equal(x, y)    => contains(x, e2) || contains(y, e2)
      case NotEqual(x, y) => contains(x, e2) || contains(y, e2)
      case LessThan(x, y) => contains(x, e2) || contains(y, e2)
      case And(x, y)      => contains(x, e2) || contains(y, e2)
      case Or(x, y)       => contains(x, e2) || contains(y, e2)
      case Not(x)         => contains(x, e2)
      case IfThenElse(c, t, f) =>
        contains(c, e2) || contains(t, e2) || contains(f, e2)
      case Function(p, b)    => contains(p, e2) || contains(b, e2)
      case FunCall(f, a)     => contains(f, e2) || contains(a, e2)
      case Tuple(elems: _*)  => elems.exists(e => contains(e, e2))
      case TupleAccess(t, i) => contains(t, e2) || contains(i, e2)
      case VecBuild(n, f)    => contains(n, e2) || contains(f, e2)
      case VecAccess(v, i)   => contains(v, e2) || contains(i, e2)
      case VecLength(v)      => contains(v, e2)
      case StmBuild(n, z, f) =>
        contains(n, e2) || contains(z, e2) || contains(f, e2)
      case StmNext(s)   => contains(s, e2)
      case StmLength(s) => contains(s, e2)
    }
  }

  def substitute(
      e: Expr
  )(implicit substitutions: Map[Expr, Expr]): Expr = {
    substitutions.get(e) match {
      case Some(v) => v
      case None =>
        e match {
          case t: Tuple => Tuple(t.elems.toSeq.map(substitute(_)): _*)
          case TupleAccess(t: Expr, i: Expr) =>
            TupleAccess(substitute(t), substitute(i))

          case p: Param => p
          case f: Function => {
            val newParam = Param()
            Function(
              newParam,
              substitute(f.body)(substitutions + ((f.param, newParam)))
            )
            // when substituting the body, this might be come a new function if anything is susbstituted, therefore, we create a new Param
          }
          case FunCall(f: Expr, arg: Expr) =>
            FunCall(substitute(f), substitute(arg))

          case Add(e1: Expr, e2: Expr) => Add(substitute(e1), substitute(e2))
          case Sub(e1: Expr, e2: Expr) => Sub(substitute(e1), substitute(e2))
          case Mul(e1: Expr, e2: Expr) => Mul(substitute(e1), substitute(e2))
          case Div(e1: Expr, e2: Expr) => Div(substitute(e1), substitute(e2))
          case Mod(e1: Expr, e2: Expr) => Mod(substitute(e1), substitute(e2))
          case IntCst(_)               => e

          case True  => True
          case False => False
          case IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) =>
            IfThenElse(substitute(cond), substitute(trueE), substitute(falseE))
          case NotEqual(e1: Expr, e2: Expr) =>
            NotEqual(substitute(e1), substitute(e2))
          case Equal(e1: Expr, e2: Expr) =>
            Equal(substitute(e1), substitute(e2))
          case LessThan(e1: Expr, e2: Expr) =>
            LessThan(substitute(e1), substitute(e2))
          case And(e1: Expr, e2: Expr) => And(substitute(e1), substitute(e2))
          case Or(e1: Expr, e2: Expr)  => Or(substitute(e1), substitute(e2))
          case Not(e: Expr)            => Not(substitute(e))

          case DontCare => DontCare

          case StmBuild(lengths, seed, f) =>
            StmBuild(
              substitute(lengths),
              substitute(seed),
              substitute(f).asInstanceOf[Function]
            )
          case StmLength(s)     => StmLength(substitute(s))
          case StmNext(e: Expr) => StmNext(substitute(e))

          case VecBuild(len: Expr, f) =>
            VecBuild(substitute(len), substitute(f))
          case VecAccess(vec: Expr, i: Expr) =>
            VecAccess(substitute(vec), substitute(i))
          case VecLength(vec: Expr) => VecLength(substitute(vec))
        }
    }
  }

  def partialEval(e: Expr): Expr = {
    e match {

      case t: Tuple => Tuple(t.elems.toSeq.map(partialEval(_)): _*)
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

      case Add(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)       => e1.i + e2.i
          case (e, IntCst(0))                 => e
          case (IntCst(0), e)                 => e
          case (e, IntCst(n)) if n < 0        => e - IntCst(-n)
          case (Add(e, IntCst(a)), IntCst(b)) => partialEval(e + (a + b))
          case (Add(IntCst(a), e), IntCst(b)) => partialEval(e + (a + b))
          case (IntCst(b), Add(e, IntCst(a))) => partialEval(e + (a + b))
          case (IntCst(b), Add(IntCst(a), e)) => partialEval(e + (a + b))
          case (Sub(e, IntCst(a)), IntCst(b)) => partialEval(e + (b - a))
          case (Sub(IntCst(a), e), IntCst(b)) => partialEval(IntCst(a + b) - e)
          case (IntCst(b), Sub(e, IntCst(a))) => partialEval(e + (b - a))
          case (IntCst(b), Sub(IntCst(a), e)) => partialEval(IntCst(a + b) - e)
          case (DontCare, _) | (_, DontCare)  => DontCare
          // TODO: I don't like these rules because they're really just associativity + cancelling, which are separate
          //       rules. Maybe I can at least generalize by recognizing polynomials and grouping like terms.
          case (e1, Sub(e2, e3)) if e1 == e3 => e2
          case (Sub(e1, e2), e3) if e2 == e3 => e1
          case (e1 @ _, e2 @ _)              => Add(e1, e2)
        }
      case Sub(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)       => e1.i - e2.i
          case (x, y) if x == y               => 0
          case (e, IntCst(0))                 => e
          case (e, IntCst(n)) if n < 0        => e + IntCst(-n)
          case (Add(e, IntCst(a)), IntCst(b)) => partialEval(e + (a - b))
          case (Add(IntCst(a), e), IntCst(b)) => partialEval(e + (a - b))
          case (IntCst(b), Add(e, IntCst(a))) => partialEval(IntCst(b - a) - e)
          case (IntCst(b), Add(IntCst(a), e)) => partialEval(IntCst(b - a) - e)
          case (Sub(e, IntCst(a)), IntCst(b)) => partialEval(e - (a + b))
          case (Sub(IntCst(a), e), IntCst(b)) => partialEval(IntCst(a - b) - e)
          case (IntCst(b), Sub(e, IntCst(a))) => partialEval(IntCst(a + b) - e)
          case (IntCst(b), Sub(IntCst(a), e)) => partialEval(e + (b - a))
          case (Sub(x, y), z) if x == z       => partialEval(IntCst(0) - y)
          case (x, Sub(y, z)) if x == y       => z
          case (DontCare, _) | (_, DontCare)  => DontCare
          // TODO: I don't like these rules because they're really just associativity + cancelling, which are separate
          //       rules. Maybe I can at least generalize by recognizing polynomials and grouping like terms.
          case (Add(e1, e2), e3) if e1 == e3 => e2
          case (Add(e1, e2), e3) if e2 == e3 => e1
          case (e1 @ _, e2 @ _)              => Sub(e1, e2)
        }
      case Mul(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)        => e1.i * e2.i
          case (_, IntCst(0)) | (IntCst(0), _) => IntCst(0)
          case (e, IntCst(1))                  => e
          case (IntCst(1), e)                  => e
          case (DontCare, _) | (_, DontCare)   => DontCare
          case (e1 @ _, e2 @ _)                => Mul(e1, e2)
        }
      case Div(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)      => e1.i / e2.i
          case (e, IntCst(1))                => e
          case (DontCare, _) | (_, DontCare) => DontCare
          case (e1 @ _, e2 @ _)              => Div(e1, e2)
        }
      case Mod(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)      => e1.i % e2.i
          case (DontCare, _) | (_, DontCare) => DontCare
          case (e1 @ _, e2 @ _)              => Mod(e1, e2)
        }
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
                case NotEqual(p: Param, r)
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
      case NotEqual(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)      => e1.i != e2.i
          case (DontCare, _) | (_, DontCare) => DontCare
          case (e1 @ _, e2 @ _)              => NotEqual(e1, e2)
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
          case (e1 @ _, e2 @ _)              => LessThan(e1, e2)
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
                      n == 3,
                      s"The function in StmBuild returned a ${n}-tuple instead of a 3-tuple."
                    )
                    partialEval(next.__2) match {
                      case True =>
                        // return the new stream and the next element
                        Tuple(
                          StmBuild(
                            len - 1,
                            partialEval(next.__0),
                            // this function may have free parameters
                            partialEval(s.nextF).asInstanceOf[Function]
                          ),
                          partialEval(next.__1)
                        )
                      case False =>
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
          NotEqual(_, _) | LessThan(_, _) =>
        Some(true)
      // Definitely not a bool
      case _: Tuple | _: StmBuild | _: VecBuild | _: IntExpr | _: Function =>
        Some(false)
      // Not sure
      case _: Param | DontCare => None
      case TupleAccess(Tuple(elems: _*), _) =>
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
      case Tuple(elems: _*)  => elems.exists(e => hasSideEffects(e))
      case TupleAccess(t, i) => hasSideEffects(t) || hasSideEffects(i)
      case _: Param          => false
      case _: Function       => false
      case FunCall(f, arg)   => ???
      case _: IntCst         => false
      case Add(x, y)         => hasSideEffects(x) || hasSideEffects(y)
      case Sub(x, y)         => hasSideEffects(x) || hasSideEffects(y)
      case Mul(x, y)         => hasSideEffects(x) || hasSideEffects(y)
      case Div(x, y)         => hasSideEffects(x) || hasSideEffects(y)
      case Mod(x, y)         => hasSideEffects(x) || hasSideEffects(y)
      case True | False      => false
      case IfThenElse(cond, t, f) =>
        hasSideEffects(cond) || hasSideEffects(t) || hasSideEffects(f)
      case Equal(x, y)    => hasSideEffects(x) || hasSideEffects(y)
      case NotEqual(x, y) => hasSideEffects(x) || hasSideEffects(y)
      case LessThan(x, y) => hasSideEffects(x) || hasSideEffects(y)
      case Not(x)         => hasSideEffects(x)
      case And(x, y)      => hasSideEffects(x) || hasSideEffects(y)
      case Or(x, y)       => hasSideEffects(x) || hasSideEffects(y)
      case DontCare       => false
      case _: StmBuild    => ???
      case _: StmLength   => false
      case _: StmNext     => true
      case _: VecBuild    => false
      case _: VecAccess   => false
      case _: VecLength   => false
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
