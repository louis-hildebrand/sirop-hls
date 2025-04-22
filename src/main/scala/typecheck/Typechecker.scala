package typecheck

import ir._

object Typechecker {
  def typecheck(e: Expr, funInTyp: Option[Type] = None)(implicit
      context: Map[Param, Type]
  ): Expr = {
    e match {
      case e if e.typ != Missing => e

      case x: Param =>
        context.get(x) match {
          case Some(t) => x.rebuild(t)
          case None    => throw new TypeError(s"Free variable: $x.")
        }
      case Function(_, x, t, body) =>
        val inTyp = t match {
          case Missing =>
            funInTyp match {
              case Some(t) => t
              case None =>
                throw new TypeError(
                  s"Missing input type annotation on function."
                )
            }
          case t => t
        }
        val newBody = typecheck(body)(context + (x -> inTyp))
        val newX = x.rebuild(inTyp).asInstanceOf[Param]
        new Function(TyArrow(inTyp, newBody.typ), newX, inTyp, newBody)
      case fc @ FunCall(_, f, arg) =>
        val newF = typecheck(f)
        val newArg = typecheck(arg)
        newF.typ match {
          case TyArrow(t1, t2) =>
            if (isCompatible(newArg.typ, t1)) {
              fc.rebuild(t2, Seq(newF, newArg))
            } else {
              throw new TypeError(
                s"Left-hand side of function call expects input of type $t1, but ${newArg.typ} was provided."
              )
            }
          case t =>
            throw new TypeError(
              s"Left-hand side of function call has type $t. It should be a function."
            )
        }

      case n: IntCst => n.rebuild(TyInt)
      case s @ Sum(terms) =>
        val newTerms = terms.map(e => typecheck(e))
        for ((t, i) <- newTerms.zipWithIndex) {
          if (t.typ != TyInt) {
            throw new TypeError(s"Term $i of sum has type ${t.typ}.")
          }
        }
        s.rebuild(TyInt, newTerms)
      case p @ Prod(factors) =>
        val newFactors = factors.map(e => typecheck(e))
        for ((t, i) <- newFactors.zipWithIndex) {
          if (t.typ != TyInt) {
            throw new TypeError(s"Term $i of sum has type ${t.typ}.")
          }
        }
        p.rebuild(TyInt, newFactors)
      case d @ Div(_, e1, e2) =>
        val newLhs = typecheck(e1)
        newLhs.typ match {
          case TyInt => ()
          case t => throw new TypeError(s"Expected type $TyInt but found $t.")
        }
        val newRhs = typecheck(e2)
        newRhs.typ match {
          case TyInt => ()
          case t => throw new TypeError(s"Expected type $TyInt but found $t.")
        }
        d.rebuild(TyInt, Seq(newLhs, newRhs))
      case m @ Mod(_, e1, e2) =>
        val newLhs = typecheck(e1)
        newLhs.typ match {
          case TyInt => ()
          case t => throw new TypeError(s"Expected type $TyInt but found $t.")
        }
        val newRhs = typecheck(e2)
        newRhs.typ match {
          case TyInt => ()
          case t => throw new TypeError(s"Expected type $TyInt but found $t.")
        }
        m.rebuild(TyInt, Seq(newLhs, newRhs))

      case True  => True
      case False => False
      case ite @ IfThenElse(c, t, f) =>
        val newC = typecheck(c)
        newC.typ match {
          case TyBool => ()
          case t => throw new TypeError(s"Expected type $TyBool but found $t.")
        }
        val newT = typecheck(t)
        val newF = typecheck(f)
        if (isCompatible(newT.typ, newF.typ)) {
          ite.rebuild(newT.typ, Seq(newC, newT, newF))
        } else {
          throw new TypeError(
            s"True branch of if-then-else has type ${newT.typ} but false branch has type ${newF.typ}."
          )
        }
      case a @ And(terms @ _*) =>
        val newTerms = terms.map(e => typecheck(e))
        for ((t, i) <- newTerms.zipWithIndex) {
          if (t.typ != TyBool) {
            throw new TypeError(s"Term $i of AND has type ${t.typ}.")
          }
        }
        a.rebuild(TyBool, newTerms)
      case or @ Or(terms @ _*) =>
        val newTerms = terms.map(e => typecheck(e))
        for ((t, i) <- newTerms.zipWithIndex) {
          if (t.typ != TyBool) {
            throw new TypeError(s"Term $i of OR has type ${t.typ}.")
          }
        }
        or.rebuild(TyBool, newTerms)
      case n @ Not(_, e) =>
        val newE = typecheck(e)
        newE.typ match {
          case TyBool => ()
          case t => throw new TypeError(s"Expected type $TyBool but found $t.")
        }
        n.rebuild(TyBool, Seq(newE))
      case eq @ Equal(_, e1, e2) =>
        val newE1 = typecheck(e1)
        newE1.typ match {
          case _: TyStm | _: TyArrow =>
            throw new TypeError(s"Cannot compare value of type ${newE1.typ}.")
          case _ => ()
        }
        val newE2 = typecheck(e2)
        newE2.typ match {
          case _: TyStm | _: TyArrow =>
            throw new TypeError(s"Cannot compare value of type ${newE2.typ}.")
          case _ => ()
        }
        if (isCompatible(newE1.typ, newE2.typ)) {
          eq.rebuild(TyBool, Seq(newE1, newE2))
        } else {
          throw new TypeError(
            s"Left-hand side of Equals has type ${newE1.typ} but right-hand side has type ${newE2.typ}."
          )
        }
      case lt @ LessThan(_, e1, e2) =>
        val newLhs = typecheck(e1)
        newLhs.typ match {
          case TyInt => ()
          case t => throw new TypeError(s"Expected type $TyInt but found $t.")
        }
        val newRhs = typecheck(e2)
        newRhs.typ match {
          case TyInt => ()
          case t => throw new TypeError(s"Expected type $TyInt but found $t.")
        }
        lt.rebuild(TyBool, Seq(newLhs, newRhs))

      case t @ Tuple(_, elems @ _*) =>
        val newElems = elems.map(e => typecheck(e))
        t.rebuild(TyTuple(newElems.map(e => e.typ): _*), newElems)
      case ta @ TupleAccess(_, t, IntCst(i)) =>
        val newT = typecheck(t)
        newT.typ match {
          case TyTuple(ts @ _*) =>
            ta.rebuild(ts(i), Seq(newT, IntCst(i)))
          case t =>
            throw new TypeError(s"Left-hand side of tuple access has type $t.")
        }

      case Default => ???

      case vb @ VecBuild(_, n, f) =>
        val newN = typecheck(n)
        newN.typ match {
          case TyInt => ()
          case t     => throw new TypeError(s"Length of VecBuild has type $t.")
        }
        val newF = typecheck(f, funInTyp = Some(TyInt))
        // TODO: Properly enforce restrictions on contents of vector (e.g., no streams, no functions)
        val vecT = newF.typ match {
          case TyArrow(TyInt, t) => t
          case t => throw new TypeError(s"Function of VecBuild has type $t.")
        }
        vb.rebuild(TyVec(vecT, newN), Seq(newN, newF))
      case va @ VecAccess(_, v, i) =>
        val newV = typecheck(v)
        val vecT = newV.typ match {
          case TyVec(t, _) => t
          case t =>
            throw new TypeError(s"Left-hand side of VecAccess has type $t.")
        }
        val newI = typecheck(i)
        newI.typ match {
          case TyInt => ()
          case t =>
            throw new TypeError(s"Right-hand side of VecAccess has type $t.")
        }
        va.rebuild(vecT, Seq(newV, newI))
      case vl @ VecLength(_, v) =>
        val newV = typecheck(v)
        newV.typ match {
          case _: TyVec => ()
          case t =>
            throw new TypeError(s"Argument of VecLength has type $t.")
        }
        vl.rebuild(TyInt, Seq(newV))
      case _: VecLiteral => ???

      case s: StmBuild =>
        val newN = typecheck(s.n)
        newN.typ match {
          case TyInt => ()
          case t     => throw new TypeError(s"Length of StmBuild has type $t.")
        }
        val newSeedByVar = s.seedByVar.map({ case (x, z) => x -> typecheck(z) })
        val newContext = newSeedByVar.foldLeft(context)({ case (ctx, (x, z)) =>
          ctx + (x -> z.typ)
        })
        val newNextByVar = s.nextByVar.map({ case (x, next) =>
          val newNext = typecheck(next)(newContext)
          val initTyp = newSeedByVar(x).typ
          if (isCompatible(initTyp, newNext.typ)) {
            x -> newNext
          } else {
            throw new TypeError(
              s"Next value for accumulator $x has type ${newNext.typ} but the initial value has type $initTyp."
            )
          }
        })
        val newEquations = s.accVars
          .map(x => {
            val z = newSeedByVar(x)
            val newX = x.rebuild(z.typ).asInstanceOf[Param]
            newX -> (z, newNextByVar(x))
          })
          .toMap
        val newOutput = typecheck(s.output)(newContext)
        val stmT = newOutput.typ match {
          case TyTuple(t, TyBool) => t
          case t => throw new TypeError(s"Output of StmBuild has type $t.")
        }
        StmBuild(TyStm(stmT, newN), newN, newOutput, newEquations)
      case sn @ StmNext(_, s) =>
        val newS = typecheck(s)
        newS.typ match {
          case TyStm(t, n) =>
            sn.rebuild(TyTuple(TyStm(t, n - 1), t), Seq(newS))
          case t => throw new TypeError(s"Argument of StmNext has type $t.")
        }
      case sn @ StmNextK(_, s, k) =>
        val newK = typecheck(k)
        newK.typ match {
          case TyInt => ()
          case t     => throw new TypeError(s"Index of StmNextK has type $t.")
        }
        val newS = typecheck(s)
        newS.typ match {
          case TyStm(t, n) =>
            sn.rebuild(TyStm(t, n - k), Seq(newS, newK))
          case t => throw new TypeError(s"Stream of StmNext has type $t.")
        }
      case sl @ StmLength(_, s) =>
        val newS = typecheck(s)
        newS.typ match {
          case _: TyStm => sl.rebuild(TyInt, Seq(newS))
          case t => throw new TypeError(s"Argument of StmNext has type $t.")
        }
      case StmLiteral(typ, elems @ _*) => ???
    }
  }

  /** Check whether two types are "compatible," i.e., will have the same shape
    * in hardware.
    */
  private def isCompatible(t1: Type, t2: Type): Boolean = {
    (t1, t2) match {
      case (TyBool, TyBool) => true
      case (TyInt, TyInt)   => true
      case (TyArrow(t1, t2), TyArrow(t3, t4)) =>
        isCompatible(t1, t3) && isCompatible(t2, t4)
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) =>
        (ts1.length == ts2.length
        && ts1
          .zip(ts2)
          .forall({ case (t1, t2) => isCompatible(t1, t2) }))
      case (TyVec(t1, n1), TyVec(t2, n2)) =>
        // TODO: Improve check for equality of lengths?
        isCompatible(t1, t2) && n1 == n2
      case (TyStm(t1, _), TyStm(t2, _)) =>
        // Two streams are compatible even if they have different lengths!
        isCompatible(t1, t2)
      case _ => false
    }
  }
}
