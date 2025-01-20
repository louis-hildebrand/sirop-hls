package opt

import ir._

object PartialEvalPass {
  def partialEval(e: Expr): Expr = {
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

      case Sum(terms)    => simplifySum(terms.map(e => partialEval(e)))
      case Prod(factors) => simplifyProd(factors.map(e => partialEval(e)))
      case Div(e1: Expr, e2: Expr) => simplifyDiv(e1, e2)
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

  private def simplifySum(terms: Seq[Expr]): Expr = {
    val newTerms = if (terms.contains(DontCare)) {
      Seq(DontCare)
    } else {
      // Flatten addition tree to maximise opportunities for the following simplifications
      val flatTerms = terms.flatMap({
        case Sum(terms) => terms
        case e          => Seq(e)
      })
      // Combine constants (which also eliminates zeros)
      val const = flatTerms
        .flatMap({
          case IntCst(n) => Some(n)
          case _         => None
        })
        .sum
      val nonConstants = flatTerms.filter(e => !e.isInstanceOf[IntCst])
      // Combine like terms
      val coefficientsAndTerms = nonConstants.map({
        case Prod(factors) => coefficientAndRemaining(factors)
        case e             => (1, e)
      })
      val coefficientByTerm = coefficientsAndTerms
        .groupBy({ case (_, e) => e })
        .map({ case (e, coeffs) =>
          e -> coeffs.map({ case (c, _) => c }).sum
        })
      val newTerms = coefficientByTerm.toSeq
        .flatMap({
          case (_, 0)             => None
          case (e, 1)             => Some(e)
          case (Prod(factors), c) => Some(Prod(IntCst(c) +: factors))
          case (e, c)             => Some(Prod(Seq(IntCst(c), e)))
        })
      if (const == 0) {
        newTerms
      } else {
        IntCst(const) +: newTerms
      }
    }
    if (newTerms.isEmpty) {
      IntCst(0)
    } else if (newTerms.length == 1) {
      newTerms.head
    } else {
      Sum(newTerms)
    }
  }

  private def simplifyProd(factors: Seq[Expr]): Expr = {
    val newFactors = if (factors.contains(DontCare)) {
      Seq(DontCare)
    } else if (factors.contains(IntCst(0))) {
      Seq(IntCst(0))
    } else {
      // Flatten multiplication tree to maximise opportunities for the following simplifications
      val flatFactors = factors.flatMap({
        case Prod(factors) => factors
        case e             => Seq(e)
      })
      // Combine constants (which also eliminates ones)
      val const = flatFactors
        .flatMap({
          case IntCst(n) => Some(n)
          case _         => None
        })
        .product
      val nonConstants = flatFactors.filter(e => !e.isInstanceOf[IntCst])
      if (const == 1) {
        nonConstants
      } else {
        IntCst(const) +: nonConstants
      }
    }
    if (newFactors.isEmpty) {
      IntCst(1)
    } else if (newFactors.length == 1) {
      newFactors.head
    } else {
      Prod(newFactors)
    }
  }

  private def simplifyDiv(e1: Expr, e2: Expr): Expr = {
    val (numer, denom) = partialEval(e2) match {
      case IntCst(c) if c < 0 => (partialEval(-1 * e1), IntCst(-c))
      case e                  => (partialEval(e1), e)
    }
    (numer, denom) match {
      case (e1: IntCst, e2: IntCst) => e1.i / e2.i
      case (e, IntCst(1))           => e
      case (Prod(factors), IntCst(c)) =>
        val (newFactors, newDenom) =
          factors.foldRight((Seq[Expr](), c))((e, acc) =>
            e match {
              case IntCst(c) =>
                val gcd = BigInt(c).gcd(acc._2).toInt
                (IntCst(c / gcd) +: acc._1, acc._2 / gcd)
              case e => (e +: acc._1, acc._2)
            }
          )
        val newNumer = partialEval(Prod(newFactors))
        newDenom match {
          case 1 => newNumer
          case d => Div(newNumer, d)
        }
      case (DontCare, _) | (_, DontCare) => DontCare
      case (e1 @ _, e2 @ _)              => Div(e1, e2)
    }
  }

  /** Given a list of factors, find the constant coefficient (or one) and the
    * remaining term (or one).
    */
  private def coefficientAndRemaining(factors: Seq[Expr]): (Int, Expr) = {
    val const = factors
      .flatMap({
        case IntCst(n) => Some(n)
        case _         => None
      })
      .product
    val nonConst = factors.filter(e => !e.isInstanceOf[IntCst]) match {
      case Seq()   => IntCst(1)
      case Seq(x)  => x
      case factors => Prod(factors)
    }
    (const, nonConst)
  }
}
