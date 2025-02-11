package ir

import scala.language.implicitConversions

/** Node of an extended IR which includes values for vectors and streams.
  */
sealed trait ExtExpr {
  def children: Seq[ExtExpr]
}
sealed trait Value extends ExtExpr

case class ExtParam(name: String) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq()
}
case class ExtFunction(x: ExtParam, body: ExtExpr) extends Value {
  override def children: Seq[ExtExpr] = Seq(x, body)
}
case class ExtFunCall(f: ExtExpr, arg: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(f, arg)
}

// This is a value because `DontCare` can temporarily appear in, for example,
// the output of the stream body (which contains an option and the `None`
// variant includes `None`).
case object ExtDontCare extends Value {
  override def children: Seq[ExtExpr] = Seq()
}

case class ExtIntCst(n: Int) extends Value {
  override def children: Seq[ExtExpr] = Seq()
}
case class ExtSum(terms: ExtExpr*) extends ExtExpr {
  override def children: Seq[ExtExpr] = terms
}
case class ExtProd(factors: ExtExpr*) extends ExtExpr {
  override def children: Seq[ExtExpr] = factors
}
case class ExtDiv(e1: ExtExpr, e2: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(e1, e2)
}
case class ExtMod(e1: ExtExpr, e2: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(e1, e2)
}

case object ExtTrue extends Value {
  override def children: Seq[ExtExpr] = Seq()
}
case object ExtFalse extends Value {
  override def children: Seq[ExtExpr] = Seq()
}
case class ExtNot(e: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(e)
}
case class ExtAnd(e1: ExtExpr, e2: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(e1, e2)
}
case class ExtOr(e1: ExtExpr, e2: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(e1, e2)
}
case class ExtEqual(e1: ExtExpr, e2: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(e1, e2)
}
case class ExtLessThan(e1: ExtExpr, e2: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(e1, e2)
}
case class ExtIfThenElse(c: ExtExpr, t: ExtExpr, f: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(c, t, f)
}

case class ExtTuple(elems: ExtExpr*) extends ExtExpr {
  override def children: Seq[ExtExpr] = elems
}
case class ExtTupleAccess(t: ExtExpr, i: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(t, i)
}
case class ExtTupleVal(elems: Value*) extends Value {
  override def children: Seq[ExtExpr] = elems
}

case class ExtVecBuild(n: ExtExpr, f: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(n, f)
}
case class ExtVecAccess(v: ExtExpr, i: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(v, i)
}
case class ExtVecLength(v: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(v)
}
case class ExtVecLiteral(elems: Value*) extends Value {
  override def children: Seq[ExtExpr] = elems
}
object ExtVecLiteral {
  def ints(elems: Int*): ExtVecLiteral = {
    ExtVecLiteral(elems.map(n => ExtIntCst(n)): _*)
  }
}

case class ExtStmBuild(n: ExtExpr, z: ExtExpr, f: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(n, z, f)
}
case class ExtStmNext(s: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(s)
}
case class ExtStmLength(s: ExtExpr) extends ExtExpr {
  override def children: Seq[ExtExpr] = Seq(s)
}
case class ExtStmLiteral(elems: Value*) extends Value {
  override def children: Seq[ExtExpr] = elems
  def flatten: ExtStmLiteral = {
    require(elems.forall(e => e.isInstanceOf[ExtStmLiteral]))
    ExtStmLiteral(elems.flatMap(e => e.asInstanceOf[ExtStmLiteral].elems): _*)
  }
}
object ExtStmLiteral {
  def ints(elems: Int*): ExtStmLiteral = {
    ExtStmLiteral(elems.map(n => ExtIntCst(n)): _*)
  }

  val nil: ExtStmLiteral = ExtStmLiteral(Seq[Value](): _*)
}

trait Eval {
  implicit def int2ExtIntCst(n: Int): ExtIntCst = ExtIntCst(n)

  private def toExtExpr(e: Expr): ExtExpr = {
    e match {
      case x: Param => ExtParam(x.name)
      case Function(x, body) =>
        ExtFunction(toExtExpr(x).asInstanceOf[ExtParam], toExtExpr(body))
      case FunCall(f, arg) => ExtFunCall(toExtExpr(f), toExtExpr(arg))

      case DontCare => ExtDontCare

      case IntCst(n)     => ExtIntCst(n)
      case Sum(terms)    => ExtSum(terms.map(e => toExtExpr(e)): _*)
      case Prod(factors) => ExtProd(factors.map(e => toExtExpr(e)): _*)
      case Div(e1, e2)   => ExtDiv(toExtExpr(e1), toExtExpr(e2))
      case Mod(e1, e2)   => ExtMod(toExtExpr(e1), toExtExpr(e2))

      case True             => ExtTrue
      case False            => ExtFalse
      case Not(e)           => ExtNot(toExtExpr(e))
      case And(e1, e2)      => ExtAnd(toExtExpr(e1), toExtExpr(e2))
      case Or(e1, e2)       => ExtOr(toExtExpr(e1), toExtExpr(e2))
      case Equal(e1, e2)    => ExtEqual(toExtExpr(e1), toExtExpr(e2))
      case LessThan(e1, e2) => ExtLessThan(toExtExpr(e1), toExtExpr(e2))
      case IfThenElse(c, t, f) =>
        ExtIfThenElse(toExtExpr(c), toExtExpr(t), toExtExpr(f))

      case Tuple(elems @ _*) => ExtTuple(elems.map(e => toExtExpr(e)): _*)
      case TupleAccess(t, i) => ExtTupleAccess(toExtExpr(t), toExtExpr(i))
      case VecBuild(n, f)    => ExtVecBuild(toExtExpr(n), toExtExpr(f))
      case VecAccess(v, i)   => ExtVecAccess(toExtExpr(v), toExtExpr(i))
      case VecLength(v)      => ExtVecLength(toExtExpr(v))
      case StmBuild(n, z, f) =>
        ExtStmBuild(toExtExpr(n), toExtExpr(z), toExtExpr(f))
      case StmNext(s)   => ExtStmNext(toExtExpr(s))
      case StmLength(s) => ExtStmLength(toExtExpr(s))
    }
  }

  def eval(e: Expr): Value = {
    eval(toExtExpr(e))
  }

  def eval(e: ExtExpr): Value = {
    e match {
      case x: ExtParam =>
        throw new IllegalArgumentException(
          s"Free variable ${x.name}. Terms must be closed."
        )
      case f: ExtFunction => f
      case ExtFunCall(f, arg) =>
        eval(f) match {
          case ExtFunction(x, body) =>
            eval(substitute(body)(eval(arg), x))
          case v =>
            throw new IllegalArgumentException(
              s"Left-hand side of function application evaluated to $v. It must evaluate to a function."
            )
        }

      case ExtDontCare => ExtDontCare

      case ExtIntCst(n) => ExtIntCst(n)
      case ExtSum(terms @ _*) =>
        val termValues = terms.map(e => eval(e))
        if (termValues.forall(e => e.isInstanceOf[ExtIntCst])) {
          val xs = termValues.map(e => e.asInstanceOf[ExtIntCst].n)
          ExtIntCst(xs.sum)
        } else {
          throw new IllegalArgumentException(
            s"Terms of Sum evaluated to $termValues. They must each evaluate to an integer."
          )
        }
      case ExtProd(factors @ _*) =>
        val factorValues = factors.map(e => eval(e))
        if (factorValues.forall(e => e.isInstanceOf[ExtIntCst])) {
          val xs = factorValues.map(e => e.asInstanceOf[ExtIntCst].n)
          ExtIntCst(xs.product)
        } else {
          throw new IllegalArgumentException(
            s"Terms of Prod evaluated to $factorValues. They must each evaluate to an integer."
          )
        }
      case ExtDiv(e1, e2) =>
        (eval(e1), eval(e2)) match {
          case (ExtIntCst(n1), ExtIntCst(n2)) => ExtIntCst(n1 / n2)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Div evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case ExtMod(e1, e2) =>
        (eval(e1), eval(e2)) match {
          case (ExtIntCst(n1), ExtIntCst(n2)) => ExtIntCst(n1 % n2)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Mod evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }

      case ExtTrue  => ExtTrue
      case ExtFalse => ExtFalse
      case ExtNot(e) =>
        eval(e) match {
          case ExtFalse => ExtTrue
          case ExtTrue  => ExtFalse
          case v =>
            throw new IllegalArgumentException(
              s"Operand of Not evaluated to $v. It must evaluate to a boolean."
            )
        }
      case ExtAnd(e1, e2) =>
        // TODO: Are And() and Or() short-circuiting? No, right?
        (eval(e1), eval(e2)) match {
          case (ExtFalse, ExtFalse) => ExtFalse
          case (ExtFalse, ExtTrue)  => ExtFalse
          case (ExtTrue, ExtFalse)  => ExtFalse
          case (ExtTrue, ExtTrue)   => ExtTrue
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of And evaluated to $v1 and $v2. They must each evaluate to a boolean."
            )
        }
      case ExtOr(e1, e2) =>
        (eval(e1), eval(e2)) match {
          case (ExtFalse, ExtFalse) => ExtFalse
          case (ExtFalse, ExtTrue)  => ExtTrue
          case (ExtTrue, ExtFalse)  => ExtTrue
          case (ExtTrue, ExtTrue)   => ExtTrue
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Or evaluated to $v1 and $v2. They must each evaluate to a boolean."
            )
        }
      case ExtEqual(e1, e2) => if (eval(e1) == eval(e2)) ExtTrue else ExtFalse
      case ExtLessThan(e1, e2) =>
        (eval(e1), eval(e2)) match {
          case (ExtIntCst(n1), ExtIntCst(n2)) =>
            if (n1 < n2) ExtTrue else ExtFalse
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of LessThan evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case ExtIfThenElse(c, t, f) =>
        eval(c) match {
          case ExtTrue  => eval(t)
          case ExtFalse => eval(f)
          case v =>
            throw new IllegalArgumentException(
              s"Condition of IfThenElse evaluated to $v. It must evaluate to a boolean."
            )
        }

      case ExtTuple(elems @ _*) =>
        ExtTupleVal(elems.map(e => eval(e)): _*)
      case ExtTupleAccess(t, i) =>
        eval(t) match {
          case ExtTupleVal(elems @ _*) =>
            eval(i) match {
              case ExtIntCst(i) => elems(i)
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
      case v: ExtTupleVal => v

      case ExtVecBuild(n, f) =>
        eval(n) match {
          case ExtIntCst(n) if n >= 0 =>
            ExtVecLiteral(
              (0 until n).map(i => eval(ExtFunCall(f, ExtIntCst(i)))): _*
            )
          case n =>
            throw new IllegalArgumentException(
              s"Vector length $n. Vectors must have non-negative integer length."
            )
        }
      case ExtVecAccess(v, i) =>
        eval(v) match {
          case ExtVecLiteral(elems @ _*) =>
            eval(i) match {
              case ExtIntCst(i) => elems(i)
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
      case ExtVecLength(v) =>
        eval(v) match {
          case ExtVecLiteral(elems @ _*) => ExtIntCst(elems.length)
          case v =>
            throw new IllegalArgumentException(
              s"Vector of vector length evaluated to $v. It must evaluate to a vector."
            )
        }
      case v: ExtVecLiteral => v

      case ExtStmBuild(n, z, f) =>
        eval(n) match {
          case ExtIntCst(0) => ExtStmLiteral.nil
          case ExtIntCst(n) if n > 0 =>
            eval(ExtFunCall(f, z)) match {
              case ExtTupleVal(newZ, ExtTupleVal(v, ExtTrue)) =>
                eval(ExtStmBuild(ExtIntCst(n - 1), newZ, f)) match {
                  case s: ExtStmLiteral => ExtStmLiteral(v +: s.elems: _*)
                  case _                => ???
                }
              case ExtTupleVal(newZ, ExtTupleVal(_, ExtFalse)) =>
                eval(ExtStmBuild(ExtIntCst(n), newZ, f)) match {
                  case s: ExtStmLiteral => s
                  case _                => ???
                }
              case v =>
                throw new IllegalArgumentException(
                  s"Body of StmBuild returned $v. The function must return a 2-tuple where the second element is an option."
                )
            }
          case n =>
            throw new IllegalArgumentException(
              s"Stream length $n. Streams must have non-negative integer length."
            )
        }
      case ExtStmNext(s) =>
        // TODO: What happens if the circuit tries to get the next element of an empty stream, but then discards that
        //       value (e.g., we construct a tuple containing this but then access a different value)? Will it get stuck?
        eval(s) match {
          case ExtStmLiteral() =>
            throw new IllegalArgumentException(
              "Attempt to call StmNext on an empty stream."
            )
          case ExtStmLiteral(v, vs @ _*) =>
            ExtTupleVal(ExtStmLiteral(vs: _*), v)
        }
      case ExtStmLength(s) =>
        eval(s) match {
          case ExtStmLiteral(elems @ _*) => ExtIntCst(elems.length)
          case v =>
            throw new IllegalArgumentException(
              s"Stream of stream length evaluated to $v. It must evaluate to a stream."
            )
        }
      case v: ExtStmLiteral => v
    }
  }

  /** Substitute <code>e2</code> for <code>x</code> in <code>e1</code>.
    */
  private def substitute(e1: ExtExpr)(e2: ExtExpr, x: ExtParam): ExtExpr = {
    e1 match {
      case y: ExtParam          => if (y == x) e2 else y
      case ExtFunction(y, body) =>
        // Avoid variable capture
        require(y != x)
        require(!freeVars(e2).contains(y))
        ExtFunction(y, substitute(body)(e2, x))
      case ExtFunCall(f, arg) =>
        ExtFunCall(substitute(f)(e2, x), substitute(arg)(e2, x))

      case ExtDontCare => ExtDontCare

      case ExtIntCst(n) => ExtIntCst(n)
      case ExtSum(terms @ _*) =>
        ExtSum(terms.map(e => substitute(e)(e2, x)): _*)
      case ExtProd(factors @ _*) =>
        ExtProd(factors.map(e => substitute(e)(e2, x)): _*)
      case ExtDiv(a, b) =>
        ExtDiv(substitute(a)(e2, x), substitute(b)(e2, x))
      case ExtMod(a, b) =>
        ExtMod(substitute(a)(e2, x), substitute(b)(e2, x))

      case ExtTrue  => ExtTrue
      case ExtFalse => ExtFalse
      case ExtNot(e) =>
        ExtNot(substitute(e)(e2, x))
      case ExtAnd(a, b) =>
        ExtAnd(substitute(a)(e2, x), substitute(b)(e2, x))
      case ExtOr(a, b) =>
        ExtOr(substitute(a)(e2, x), substitute(b)(e2, x))
      case ExtEqual(a, b) =>
        ExtEqual(substitute(a)(e2, x), substitute(b)(e2, x))
      case ExtLessThan(a, b) =>
        ExtLessThan(substitute(a)(e2, x), substitute(b)(e2, x))
      case ExtIfThenElse(c, t, f) =>
        ExtIfThenElse(
          substitute(c)(e2, x),
          substitute(t)(e2, x),
          substitute(f)(e2, x)
        )

      case ExtTuple(elems @ _*) =>
        ExtTuple(elems.map(e => substitute(e)(e2, x)): _*)
      case ExtTupleAccess(t, i) =>
        ExtTupleAccess(substitute(t)(e2, x), substitute(i)(e2, x))
      case v: ExtTupleVal => v

      case ExtVecBuild(n, f) =>
        ExtVecBuild(substitute(n)(e2, x), substitute(f)(e2, x))
      case ExtVecAccess(v, i) =>
        ExtVecAccess(substitute(v)(e2, x), substitute(i)(e2, x))
      case ExtVecLength(v) =>
        ExtVecLength(substitute(v)(e2, x))
      case v: ExtVecLiteral => v

      case ExtStmBuild(n, z, f) =>
        ExtStmBuild(
          substitute(n)(e2, x),
          substitute(z)(e2, x),
          substitute(f)(e2, x)
        )
      case ExtStmNext(s) =>
        ExtStmNext(substitute(s)(e2, x))
      case ExtStmLength(s) =>
        ExtStmLength(substitute(s)(e2, x))
      case v: ExtStmLiteral => v
    }
  }

  private def freeVars(e: ExtExpr): Set[ExtParam] = {
    e match {
      case x: ExtParam          => Set(x)
      case ExtFunction(y, body) => freeVars(body).diff(Set(y))
      case e =>
        e.children.foldLeft(Set[ExtParam]())((acc, e) => acc.union(freeVars(e)))
    }
  }
}
