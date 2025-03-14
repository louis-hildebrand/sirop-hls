package ir

import scala.annotation.tailrec
import scala.language.implicitConversions

class InfiniteLoopError(msg: String) extends IllegalArgumentException(msg)

trait Eval {

  /** If a stream takes this many steps without producing a valid element,
    * assume it is stuck in an infinite loop.
    */
  private val MaxStepsWithoutValid = 100000

  def eval(e: Expr): Expr = {
    evalBigStepToplevel(e)
  }

  def evalBigStepToplevel(e: Expr): Expr = {
    evalBigStep(e) match {
      case StmBuild(n, z, f) =>
        n match {
          case IntCst(0) => StmLiteral.nil
          case IntCst(n) if n > 0 =>
            evalBigStep(StmNext(StmBuild(n, z, f))) match {
              case Tuple(tail, head) =>
                val tailElems = evalBigStepToplevel(tail) match {
                  case s: StmLiteral => s.elems
                  case v =>
                    throw new IllegalArgumentException(
                      s"Tail of stream evaluated to $v. It must evaluate to a stream literal."
                    )
                }
                StmLiteral(head +: tailElems: _*)
              case _ => ???
            }
          case n =>
            throw new IllegalArgumentException(
              s"Stream length $n. Streams must have non-negative integer length."
            )
        }
      case e => e
    }
  }

  def evalBigStep(e: Expr): Expr = {
    e match {
      case x: Param =>
        throw new IllegalArgumentException(
          s"Free variable ${x.name}. Terms must be closed."
        )
      case f: Function => f
      case FunCall(f, arg) =>
        evalBigStep(f) match {
          case DontCare => DontCare
          case Function(x, body) =>
            evalBigStep(arg) match {
              case DontCare => DontCare
              case a        => evalBigStep(body.substitute(x -> a))
            }
          case v =>
            throw new IllegalArgumentException(
              s"Left-hand side of function application evaluated to $v. It must evaluate to a function."
            )
        }

      // DontCare may be returned from a function (e.g., if the function returns `Option<T>`), so don't throw an
      // exception if you encounter it
      case DontCare => DontCare

      case IntCst(n) => IntCst(n)
      case Sum(terms) =>
        val termValues = terms.map(e => evalBigStep(e))
        if (termValues.contains(DontCare)) {
          DontCare
        } else if (termValues.forall(e => e.isInstanceOf[IntCst])) {
          val xs = termValues.map(e => e.asInstanceOf[IntCst].i)
          IntCst(xs.sum)
        } else {
          throw new IllegalArgumentException(
            s"Terms of Sum evaluated to $termValues. They must each evaluate to an integer."
          )
        }
      case Prod(factors) =>
        val factorValues = factors.map(e => evalBigStep(e))
        if (factorValues.contains(DontCare)) {
          DontCare
        } else if (factorValues.forall(e => e.isInstanceOf[IntCst])) {
          val xs = factorValues.map(e => e.asInstanceOf[IntCst].i)
          IntCst(xs.product)
        } else {
          throw new IllegalArgumentException(
            s"Terms of Prod evaluated to $factorValues. They must each evaluate to an integer."
          )
        }
      case Div(e1, e2) =>
        (evalBigStep(e1), evalBigStep(e2)) match {
          case (DontCare, _) | (_, DontCare) => DontCare
          case (IntCst(n1), IntCst(n2))      => IntCst(n1 / n2)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Div evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case Mod(e1, e2) =>
        (evalBigStep(e1), evalBigStep(e2)) match {
          case (DontCare, _) | (_, DontCare) => DontCare
          case (IntCst(n1), IntCst(n2))      => IntCst(n1 % n2)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Mod evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }

      case True  => True
      case False => False
      case Not(e) =>
        evalBigStep(e) match {
          case DontCare => DontCare
          case False    => True
          case True     => False
          case v =>
            throw new IllegalArgumentException(
              s"Operand of Not evaluated to $v. It must evaluate to a boolean."
            )
        }
      case And(terms @ _*) =>
        // TODO: Are And() and Or() short-circuiting? No, right?
        val termValues = terms.map(e => evalBigStep(e))
        if (termValues.contains(DontCare)) {
          DontCare
        } else if (termValues.forall(e => e.isInstanceOf[BoolCst])) {
          if (termValues.contains(False)) False else True
        } else {
          throw new IllegalArgumentException(
            s"Terms of And evaluated to $termValues. They must all evaluate to booleans."
          )
        }
      case Or(terms @ _*) =>
        val termValues = terms.map(e => evalBigStep(e))
        if (termValues.contains(DontCare)) {
          DontCare
        } else if (termValues.forall(e => e.isInstanceOf[BoolCst])) {
          if (termValues.contains(True)) True else False
        } else {
          throw new IllegalArgumentException(
            s"Terms of Or evaluated to $termValues. They must all evaluate to booleans."
          )
        }
      case Equal(e1, e2) =>
        (evalBigStep(e1), evalBigStep(e2)) match {
          case (DontCare, _) | (_, DontCare) => DontCare
          case (e1, e2)                      => e1 == e2
        }
      case LessThan(e1, e2) =>
        (evalBigStep(e1), evalBigStep(e2)) match {
          case (DontCare, _) | (_, DontCare) => DontCare
          case (IntCst(n1), IntCst(n2)) =>
            if (n1 < n2) True else False
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of LessThan evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case IfThenElse(c, t, f) =>
        evalBigStep(c) match {
          case DontCare => DontCare
          case True     => evalBigStep(t)
          case False    => evalBigStep(f)
          case v =>
            throw new IllegalArgumentException(
              s"Condition of IfThenElse evaluated to $v. It must evaluate to a boolean."
            )
        }

      case Tuple(elems @ _*) =>
        // Don't evaluate the whole thing to DontCare if one of the elements is DontCare.
        // For example, None is represented by (DontCare, False) and it's important for the False to stay.
        Tuple(elems.map(e => evalBigStep(e)): _*)
      case TupleAccess(t, i) =>
        evalBigStep(t) match {
          case DontCare => DontCare
          case Tuple(elems @ _*) =>
            evalBigStep(i) match {
              case DontCare  => DontCare
              case IntCst(i) => elems(i)
              case v =>
                throw new IllegalArgumentException(
                  s"Index of tuple access evaluated to $v. It must evaluate to a boolean."
                )
            }
          case v =>
            throw new IllegalArgumentException(
              s"Tuple of tuple access evaluated to $v. It must evaluate to a tuple literal."
            )
        }

      case VecBuild(n, f) =>
        evalBigStep(n) match {
          case IntCst(n) if n >= 0 =>
            VecLiteral(
              (0 until n).map(i => evalBigStep(FunCall(f, IntCst(i)))): _*
            )
          case n =>
            throw new IllegalArgumentException(
              s"Vector length $n. Vectors must have non-negative integer length."
            )
        }
      case VecAccess(v, i) =>
        evalBigStep(v) match {
          case DontCare => DontCare
          case VecLiteral(elems @ _*) =>
            evalBigStep(i) match {
              case DontCare  => DontCare
              case IntCst(i) => elems(i)
              case v =>
                throw new IllegalArgumentException(
                  s"Index of vector access evaluated to $v. It must evaluate to a vector."
                )
            }
          case v =>
            throw new IllegalArgumentException(
              s"Vector of vector access evaluated to $v. It must evaluate to a vector."
            )
        }
      case VecLength(v) =>
        evalBigStep(v) match {
          case DontCare               => DontCare
          case VecLiteral(elems @ _*) => IntCst(elems.length)
          case v =>
            throw new IllegalArgumentException(
              s"Vector of vector length evaluated to $v. It must evaluate to a vector."
            )
        }
      case v: VecLiteral => v

      case StmBuild(n, out, equations) =>
        StmBuild(
          evalBigStep(n),
          out,
          equations.map({ case (x, (z, next)) =>
            x -> (evalBigStep(z), next)
          })
        )
      case StmNext(s) =>
        evalStmNext(s)(0)
      case StmNextK(s, k) =>
        evalBigStep(k) match {
          case DontCare           => DontCare
          case IntCst(k) if k < 0 =>
            // May occur temporarily during optimizations - for example, if you have something like
            // if (k >= 0) then StmNextK(s, k) else DontCare, which the partial evaluator may simplify to just
            // StmNextK(s, k)
            DontCare
          case IntCst(k) if k >= 0 =>
            evalBigStep(s) match {
              case DontCare => DontCare
              case StmLiteral(vs @ _*) =>
                StmLiteral(vs.drop(k): _*)
              case s: StmBuild =>
                k match {
                  case 0 => s
                  case _ =>
                    evalBigStep(StmNextK(StmNext(s).__0, k - 1))
                }
              case s =>
                throw new IllegalArgumentException(
                  s"Stream in StmNextK evaluated to $s. It must evaluate to a stream literal."
                )
            }
          case k =>
            throw new IllegalArgumentException(
              s"Index in StmNextK evaluated to $k. The index must be a non-negative integer."
            )
        }
      case StmLength(s) =>
        evalBigStep(s) match {
          case DontCare               => DontCare
          case StmBuild(n, _, _)      => n
          case StmLiteral(elems @ _*) => IntCst(elems.length)
          case v =>
            throw new IllegalArgumentException(
              s"Stream of stream length evaluated to $v. It must evaluate to a stream."
            )
        }
      case v: StmLiteral => v
    }
  }

  @tailrec
  private def evalStmNext(s: Expr)(stepsWithoutValid: Int = 0): Expr = {
    // TODO: What happens if the circuit tries to get the next element of an empty stream, but then discards that
    //       value (e.g., we construct a tuple containing this but then access a different value)? Will it get stuck?
    if (stepsWithoutValid >= MaxStepsWithoutValid) {
      throw new InfiniteLoopError(
        s"A stream has taken $MaxStepsWithoutValid steps without producing a valid element."
          + " Is it stuck in an infinite loop?"
          + s" The stream is $s."
      )
    } else {
      evalBigStep(s) match {
        case DontCare                                 => DontCare
        case StmLiteral() | StmBuild(IntCst(0), _, _) => DontCare
        case s @ StmBuild(IntCst(n), out, equations) if n > 0 =>
          val currentValByVar: Map[Expr, Expr] = s.seedByVar.toMap
          val nextEquations = equations.map({ case (x, (_, next)) =>
            val evaluatedNext = evalBigStep(next.substitute(currentValByVar))
            x -> (evaluatedNext, next)
          })
          val evaluatedOutput = evalBigStep(out.substitute(currentValByVar))
          evaluatedOutput match {
            case Tuple(v, True) =>
              Tuple(StmBuild(n - 1, out, nextEquations), v)
            case Tuple(_, False) =>
              evalStmNext(StmBuild(n, out, nextEquations))(
                stepsWithoutValid + 1
              )
            case v =>
              throw new IllegalArgumentException(
                s"Output of StmBuild evaluated to $v. It must evaluate to an option (i.e., a tuple whose second element is a boolean)."
              )
          }
        case StmBuild(n, _, _) =>
          throw new IllegalArgumentException(
            s"Stream length $n. Streams must have non-negative integer length."
          )
        case StmLiteral(v, vs @ _*) =>
          Tuple(StmLiteral(vs: _*), v)
        case e =>
          throw new IllegalArgumentException(
            s"Stream of StmNext evaluated to $e. It must evaluate to some kind of stream."
          )
      }
    }
  }
}
