package ir

sealed trait Value
case class ValFunc(x: Param, body: Expr) extends Value
case object ValTrue extends Value
case object ValFalse extends Value
case class ValIntCst(n: Int) extends Value
case class ValTuple(elems: Value*) extends Value
case class VecLiteral(elems: Value*) extends Value
case class StmLiteral(elems: Value*) extends Value
case object ValDontCare extends Value

case class Context(valByParam: Map[Param, Value]) {
  def +(x: (Param, Value)): Context = Context(valByParam + x)
}

trait Eval {
  def eval(e: Expr)(implicit ctxt: Context): Value = {
    e match {
      case x: Param =>
        ctxt.valByParam.get(x) match {
          case Some(v) => v
          case None =>
            throw new IllegalArgumentException(
              s"Free variable ${x.name}. Terms must be closed."
            )
        }
      case Function(x, body) => ValFunc(x, body)
      case FunCall(f, arg) =>
        eval(f) match {
          case ValFunc(x, body) => eval(body)(ctxt + (x -> eval(arg)))
          case ValDontCare      => ValDontCare
          case v =>
            throw new IllegalArgumentException(
              s"Left-hand side of function application evaluated to $v. It must evaluate to a function."
            )
        }
      case IntCst(n) => ValIntCst(n)
      case Sum(terms) =>
        val termValues = terms.map(e => eval(e))
        if (termValues.contains(ValDontCare)) {
          ValDontCare
        } else if (termValues.forall(e => e.isInstanceOf[ValIntCst])) {
          val xs = termValues.map(e => e.asInstanceOf[ValIntCst].n)
          ValIntCst(xs.sum)
        } else {
          throw new IllegalArgumentException(
            s"Terms of Sum evaluated to $termValues. They must each evaluate to an integer."
          )
        }
      case Prod(factors) =>
        val factorValues = factors.map(e => eval(e))
        if (factorValues.contains(ValDontCare)) {
          ValDontCare
        } else if (factorValues.forall(e => e.isInstanceOf[ValIntCst])) {
          val xs = factorValues.map(e => e.asInstanceOf[ValIntCst].n)
          ValIntCst(xs.product)
        } else {
          throw new IllegalArgumentException(
            s"Terms of Prod evaluated to $factorValues. They must each evaluate to an integer."
          )
        }
      case Div(e1, e2) =>
        (eval(e1), eval(e2)) match {
          case (ValDontCare, _) | (_, ValDontCare) => ValDontCare
          case (ValIntCst(n1), ValIntCst(n2))      => ValIntCst(n1 / n2)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Div evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case Mod(e1, e2) =>
        (eval(e1), eval(e2)) match {
          case (ValDontCare, _) | (_, ValDontCare) => ValDontCare
          case (ValIntCst(n1), ValIntCst(n2))      => ValIntCst(n1 % n2)
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Mod evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case True  => ValTrue
      case False => ValFalse
      case IfThenElse(c, t, f) =>
        eval(c) match {
          case ValTrue     => eval(t)
          case ValFalse    => eval(f)
          case ValDontCare => ValDontCare
          case v =>
            throw new IllegalArgumentException(
              s"Condition of IfThenElse evaluated to $v. It must evaluate to a boolean."
            )
        }
      case Equal(e1, e2) =>
        (eval(e1), eval(e2)) match {
          case (ValDontCare, _) | (_, ValDontCare) => ValDontCare
          case (v1, v2) => if (v1 == v2) ValTrue else ValFalse
        }
      case LessThan(e1, e2) =>
        (eval(e1), eval(e2)) match {
          case (ValDontCare, _) | (_, ValDontCare) => ValDontCare
          case (ValIntCst(n1), ValIntCst(n2)) =>
            if (n1 < n2) ValTrue else ValFalse
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of LessThan evaluated to $v1 and $v2. They must each evaluate to an integer."
            )
        }
      case Not(e) =>
        eval(e) match {
          case ValDontCare => ValDontCare
          case ValFalse    => ValTrue
          case ValTrue     => ValFalse
          case v =>
            throw new IllegalArgumentException(
              s"Operand of Not evaluated to $v. It must evaluate to a boolean."
            )
        }
      case And(e1, e2) =>
        // TODO: Are And() and Or() short-circuiting? No, right?
        (eval(e1), eval(e2)) match {
          case (ValDontCare, _) | (_, ValDontCare) => ValDontCare
          case (_, ValFalse) | (ValFalse, _)       => ValFalse
          case (ValTrue, ValTrue)                  => ValTrue
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of And evaluated to $v1 and $v2. They must each evaluate to a boolean."
            )
        }
      case Or(e1, e2) =>
        (eval(e1), eval(e2)) match {
          case (ValDontCare, _) | (_, ValDontCare) => ValDontCare
          case (_, ValTrue) | (ValTrue, _)         => ValTrue
          case (ValFalse, ValFalse)                => ValFalse
          case (v1, v2) =>
            throw new IllegalArgumentException(
              s"Operands of Or evaluated to $v1 and $v2. They must each evaluate to a boolean."
            )
        }
      case DontCare => ValDontCare
      case Tuple(elems @ _*) =>
        ValTuple(elems.map(e => eval(e)): _*)
      case TupleAccess(t, i) =>
        eval(t) match {
          case ValTuple(elems @ _*) =>
            eval(i) match {
              case ValIntCst(i) => elems(i)
              case ValDontCare  => ValDontCare
              case v =>
                throw new IllegalArgumentException(
                  s"Index of tuple access evaluated to $v. It must evaluate to a boolean."
                )
            }
          case ValDontCare => ValDontCare
          case v =>
            throw new IllegalArgumentException(
              s"Tuple of tuple access evaluated to $v. It must evaluate to a boolean."
            )
        }
      case VecBuild(n, f) =>
        eval(n) match {
          case ValIntCst(n) if n < 0 =>
            throw new IllegalArgumentException(
              s"Vector length $n. Vectors must have non-negative length."
            )
          case ValIntCst(n) if n >= 0 =>
            VecLiteral((0 until n).map(i => eval(FunCall(f, i))): _*)
        }
      case VecAccess(v, i) =>
        eval(v) match {
          case VecLiteral(elems @ _*) =>
            eval(i) match {
              case ValIntCst(i) => elems(i)
              case ValDontCare  => ValDontCare
              case v =>
                throw new IllegalArgumentException(
                  s"Index of vector access evaluated to $v. It must evaluate to a vector."
                )
            }
          case ValDontCare => ValDontCare
          case v =>
            throw new IllegalArgumentException(
              s"Vector of vector access evaluated to $v. It must evaluate to a vector."
            )
        }
      case VecLength(v) =>
        eval(v) match {
          case VecLiteral(elems @ _*) => ValIntCst(elems.length)
          case ValDontCare            => ValDontCare
          case v =>
            throw new IllegalArgumentException(
              s"Vector of vector length evaluated to $v. It must evaluate to a vector."
            )
        }
      case StmBuild(n, z, f) =>
        eval(n) match {
          case ValIntCst(0) => StmLiteral()
          case ValIntCst(n) if n > 0 =>
            eval(FunCall(f, z)) match {
              case ValTuple(s: StmLiteral, ValTuple(v, ValTrue)) =>
                StmLiteral(v +: s.elems: _*)
              case ValTuple(s: StmLiteral, ValTuple(v, ValFalse)) =>
                s
              case v =>
                throw new IllegalArgumentException(
                  s"Body of StmBuild returned $v. The function must return a 2-tuple where the first element is a stream and the second is an option."
                )
            }
          case ValIntCst(n) =>
            throw new IllegalArgumentException(
              s"Stream length $n. Streams must have non-negative length."
            )
          case ValDontCare => ValDontCare
        }
      case StmNext(s) =>
        // TODO: What happens if the circuit tries to get the next element of an empty stream, but then discards that
        //       value (e.g., we construct a tuple containing this but then discard it)? Will it get stuck?
        eval(s) match {
          case ValDontCare => ValDontCare
          case StmLiteral() =>
            throw new IllegalArgumentException(
              "Attempt to call StmNext on an empty stream."
            )
          case StmLiteral(v, vs @ _*) => ValTuple(v, StmLiteral(vs: _*))
        }
      case StmLength(s) =>
        eval(s) match {
          case StmLiteral(elems @ _*) => ValIntCst(elems.length)
          case ValDontCare            => ValDontCare
          case v =>
            throw new IllegalArgumentException(
              s"Stream of stream length evaluated to $v. It must evaluate to a stream."
            )
        }
    }
  }
}
