package ir

import scala.language.implicitConversions

class InfiniteLoopError(msg: String) extends IllegalArgumentException(msg)

class EmptyStreamError
    extends IllegalArgumentException("Attempt to read from an empty stream.")

trait Eval {

  /** If a stream takes this many steps without producing a valid element,
    * assume it is stuck in an infinite loop.
    */
  private val MaxStepsWithoutValid = 100000

  def eval(e: Expr): Expr = {
    evalBigStepToplevel(e.tchk().lower())
  }

  private def evalBigStepToplevel(e: Expr): Expr = {
    require(
      e.typ != Missing,
      s"Expression to evaluate must have a type (got untyped expression $e)."
    )
    evalBigStep(e) match {
      case s @ StmBuild(n, z, f) =>
        n match {
          case IntCst(0) =>
            val t = s.typ.asInstanceOf[TyStm].t
            StmLiteral()(TyStm(t, 0))
          case IntCst(n) if n > 0 =>
            val (head, tail) = evalStmNext(s)(0)
            val tailElems = evalBigStepToplevel(tail) match {
              case s: StmLiteral => s.elems
              case v =>
                throw new IllegalArgumentException(
                  s"Tail of stream evaluated to $v. It must evaluate to a stream literal."
                )
            }
            StmLiteral(head +: tailElems: _*)(s.typ)
          case n =>
            throw new IllegalArgumentException(
              s"Stream length $n. Streams must have non-negative integer length."
            )
        }
      case e => e
    }
  }

  def evalBigStep(e: Expr): Expr = {
    require(
      e.typ != Missing,
      s"Expression to evaluate must have a type (got untyped expression $e)."
    )
    val v: Expr = e match {
      case x: Param =>
        throw new IllegalArgumentException(
          s"Free variable ${x.name}. Terms must be closed."
        )
      case f: Function => f
      case FunCall(f, arg) =>
        evalBigStep(f) match {
          case Function(x, body) =>
            val a = evalBigStep(arg)
            evalBigStep(body.subPreserveType(x -> a))
          case v =>
            throw new IllegalArgumentException(
              s"Left-hand side of function application evaluated to $v. It must evaluate to a function."
            )
        }

      case IntCst(n) => IntCst(n)
      case Sum(terms @ _*) =>
        val termValues = terms.map(e => evalBigStep(e))
        if (termValues.forall(e => e.isInstanceOf[IntCst])) {
          val xs = termValues.map(e => e.asInstanceOf[IntCst].i)
          IntCst(xs.sum)
        } else {
          throw new IllegalArgumentException(
            s"Terms of Sum evaluated to $termValues. They must each evaluate to an integer."
          )
        }
      case Prod(factors @ _*) =>
        val factorValues = factors.map(e => evalBigStep(e))
        if (factorValues.forall(e => e.isInstanceOf[IntCst])) {
          val xs = factorValues.map(e => e.asInstanceOf[IntCst].i)
          IntCst(xs.product)
        } else {
          throw new IllegalArgumentException(
            s"Terms of Prod evaluated to $factorValues. They must each evaluate to an integer."
          )
        }
      case Div(e1, e2) =>
        val numer = evalBigStep(e1)
        val denom = evalBigStep(e2)
        (numer, denom) match {
          case (IntCst(_), IntCst(0)) =>
            throw new IllegalArgumentException("Division by zero.")
          case (IntCst(n1), IntCst(n2)) => IntCst(n1 / n2)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Div evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case Mod(e1, e2) =>
        val numer = evalBigStep(e1)
        val denom = evalBigStep(e2)
        (numer, denom) match {
          case (IntCst(_), IntCst(0)) =>
            throw new IllegalArgumentException("Modulo by zero.")
          case (IntCst(n1), IntCst(n2)) => IntCst(n1 % n2)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Mod evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }

      case True  => True
      case False => False
      case Not(e) =>
        evalBigStep(e) match {
          case False => True
          case True  => False
          case v =>
            throw new IllegalArgumentException(
              s"Operand of Not evaluated to $v. It must evaluate to a boolean."
            )
        }
      case And(terms @ _*) =>
        val termValues = terms.map(e => evalBigStep(e))
        if (termValues.forall(e => e.isInstanceOf[BoolCst])) {
          if (termValues.contains(False)) False else True
        } else {
          throw new IllegalArgumentException(
            s"Terms of And evaluated to $termValues. They must all evaluate to booleans."
          )
        }
      case Or(terms @ _*) =>
        val termValues = terms.map(e => evalBigStep(e))
        if (termValues.forall(e => e.isInstanceOf[BoolCst])) {
          if (termValues.contains(True)) True else False
        } else {
          throw new IllegalArgumentException(
            s"Terms of Or evaluated to $termValues. They must all evaluate to booleans."
          )
        }
      case Equal(e1, e2) =>
        (evalBigStep(e1), evalBigStep(e2)) match {
          // Bool
          case (v1: BoolCst, v2) => v1 == v2
          case (v1, v2: BoolCst) => v1 == v2
          // Int
          case (v1: IntCst, v2) => v1 == v2
          case (v1, v2: IntCst) => v1 == v2
          // Tuple
          case (v1: Tuple, v2) => evalTupleEqual(v1, v2)
          case (v1, v2: Tuple) => evalTupleEqual(v2, v1)
          // Vector
          case (v1: VecLiteral, v2) => evalVecEqual(v1, v2)
          case (v1, v2: VecLiteral) => evalVecEqual(v2, v1)
          // It doesn't really make sense to compare functions or streams in
          // hardware
          case (e1, e2) =>
            throw new IllegalArgumentException(s"Cannot compare $e1 with $e2.")
        }
      case LessThan(e1, e2) =>
        (evalBigStep(e1), evalBigStep(e2)) match {
          case (IntCst(n1), IntCst(n2)) =>
            if (n1 < n2) True else False
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of LessThan evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case Mux(c, t, f) =>
        // Note that both branches are always evaluated!
        val tVal = evalBigStep(t)
        val fVal = evalBigStep(f)
        evalBigStep(c) match {
          case True  => tVal
          case False => fVal
          case v =>
            throw new IllegalArgumentException(
              s"Condition of Mux evaluated to $v. It must evaluate to a boolean."
            )
        }

      case Tuple(elems @ _*) => Tuple(elems.map(e => evalBigStep(e)): _*)()
      case TupleAccess(t, i) =>
        evalBigStep(t) match {
          case Tuple(elems @ _*) =>
            evalBigStep(i) match {
              case IntCst(i) if elems.indices.contains(i) => elems(i)
              case IntCst(i) =>
                throw new TypeError(
                  s"Index $i is out of bounds for a tuple with ${elems.length} elements."
                )
              case v =>
                throw new IllegalArgumentException(
                  s"Index of tuple access evaluated to $v. It must evaluate to an integer."
                )
            }
          case v =>
            throw new IllegalArgumentException(
              s"Tuple of tuple access evaluated to $v. It must evaluate to a tuple literal."
            )
        }

      case v @ VecBuild(n, f) =>
        assert(v.typ.isInstanceOf[TyVec])
        evalBigStep(n) match {
          case IntCst(n) if n >= 0 =>
            VecLiteral(
              (0 until n).map(i =>
                evalBigStep(FunCall(f, IntCst(i))().tchk())
              ): _*
            )(v.typ)
          case n =>
            throw new IllegalArgumentException(
              s"Vector length $n. Vectors must have non-negative integer length."
            )
        }
      case VecAccess(v, i) =>
        evalBigStep(v) match {
          case VecLiteral(elems @ _*) =>
            evalBigStep(i) match {
              case IntCst(i) if elems.indices.contains(i) => elems(i)
              case _: IntCst =>
                val t = v.tchk().typ.asInstanceOf[TyVec].t
                evalBigStep(Default(t).lower())
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
      case v: VecLiteral => v

      case StmBuild(n, out, equations) =>
        StmBuild(
          evalBigStep(n),
          out,
          equations.map({ case (x, (z, next)) =>
            x -> (evalBigStep(z), next)
          })
        )()
      case _: StmData =>
        throw new IllegalArgumentException(
          s"Invalid use of ${StmData.getClass.getSimpleName} (e.g., outside a stream or with incorrect arguments)."
        )
      case StmNextK(s, k) =>
        evalBigStep(k) match {
          case IntCst(k) if k <= 0 =>
            evalBigStep(s)
          case IntCst(k) if k > 0 =>
            evalBigStep(s) match {
              case s @ StmLiteral(vs @ _*) =>
                val t = s.typ.asInstanceOf[TyStm].t
                val elems = vs.drop(k)
                StmLiteral(elems: _*)(TyStm(t, elems.length))
              case s: StmBuild =>
                val (_, tail) = evalStmNext(s)(0)
                evalBigStep(StmNextK(tail, k - 1)().tchk())
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
      case v: StmLiteral => v

      case s: SyntaxSugar =>
        throw new IllegalArgumentException(
          s"There should be no more syntax sugar after lowering. Found $s."
        )
    }
    val typedV = v.tchk()
    assert(
      typedV.typ ~~= e.typ,
      s"evaluation should preserve type (expected ${e.typ}, got ${typedV.typ})"
    )
    typedV
  }

  private def evalTupleEqual(v1: Tuple, v2: Expr): Expr = {
    val v2Elems = v2 match {
      case Tuple(elems @ _*) => elems
      case _ =>
        v1.elems.indices.map(i => evalBigStep(TupleAccess(v2, i)().tchk()))
    }
    if (v1.elems.length != v2Elems.length) {
      False
    } else {
      evalBigStep(
        v1.elems
          .zip(v2Elems)
          .foldLeft(True: Expr)({ case (a, (e1, e2)) => a && (e1 === e2) })
      )
    }
  }

  private def evalVecEqual(v1: VecLiteral, v2: Expr): Expr = {
    val v2Elems = v2 match {
      case VecLiteral(elems @ _*) => elems
      case _ =>
        v1.elems.indices.map(i => evalBigStep(VecAccess(v2, i)().tchk()))
    }
    if (v1.elems.length != v2Elems.length) {
      False
    } else {
      evalBigStep(
        v1.elems
          .zip(v2Elems)
          .foldLeft(True: Expr)({ case (a, (e1, e2)) => a && (e1 === e2) })
      )
    }
  }

  /** @return
    *   (head, tail)
    */
  private def evalStmNext(s: Expr)(stepsWithoutValid: Int = 0): (Expr, Expr) = {
    if (stepsWithoutValid >= MaxStepsWithoutValid) {
      throw new InfiniteLoopError(
        s"A stream has taken $MaxStepsWithoutValid steps without producing a valid element."
          + " Is it stuck in an infinite loop?"
          + s" The stream is $s."
      )
    } else {
      s match {
        case StmLiteral() | StmBuild(IntCst(0), _, _) =>
          throw new EmptyStreamError
        case s @ StmBuild(IntCst(n), out, equations) if n > 0 =>
          val currentValByVar: Map[Expr, Expr] = s.seedByVar.toMap
          val inputStreams = equations.flatMap({
            case (x, (z, next)) if x.typ.isInstanceOf[TyStm] =>
              // TODO: Test this
              if (next.contains(classOf[StmData])) {
                throw new IllegalArgumentException(
                  s"${StmData.getClass.getSimpleName} cannot be used in the update expression for a stream-valued accumulator."
                )
              }
              evalBigStep(next.subPreserveType(currentValByVar)) match {
                case False =>
                  val t = x.typ.asInstanceOf[TyStm].t
                  val head = evalBigStep(Default(t).lower())
                  Some(x -> (head, z))
                case True =>
                  Some(x -> evalStmNext(z)(0))
                case v =>
                  throw new TypeError(
                    s"Next value for stream-valued accumulator $x evaluated to $v."
                      + " It must evaluate to a boolean."
                  )
              }
            case _ => None
          })
          val subs = inputStreams.foldLeft(currentValByVar)({
            case (acc, (x, (head, _))) => acc + (StmData(x)() -> head)
          })
          val nextEquations = equations.map({
            case (x, (_, next)) if x.typ.isInstanceOf[TyStm] =>
              val (_, tail) = inputStreams(x)
              x -> (tail, next)
            case (x, (_, next)) =>
              val evaluatedNext = evalBigStep(next.subPreserveType(subs))
              x -> (evaluatedNext, next)
          })
          val evaluatedOutput = evalBigStep(out.subPreserveType(subs))
          evaluatedOutput match {
            case Tuple(v, True) =>
              (v, StmBuild(n - 1, out, nextEquations)().tchk())
            case Tuple(_, False) =>
              evalStmNext(StmBuild(n, out, nextEquations)().tchk())(
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
        case s @ StmLiteral(v, vs @ _*) =>
          (v, StmLiteral(vs: _*)(s.typ))
        case e =>
          throw new IllegalArgumentException(
            s"Stream of StmNext evaluated to $e. It must evaluate to some kind of stream."
          )
      }
    }
  }
}
