package opt

import ir._
import scala.annotation.tailrec

object StmFusePass {

  /** Fuse a `StmBuild` with its statically-known stream inputs until it has no
    * more statically-known stream inputs.
    */
  @tailrec
  def fuseCompletely(stm: Expr /* Stm<A; n> */ ): StmBuild /* Stm<A; n> */ = {
    val s = StmCanonPass.canonicalize(
      PartialEvalPass.partialEval(stm).asInstanceOf[StmBuild]
    )
    s.seed.asInstanceOf[Tuple].elems.headOption match {
      case Some(_: StmBuild) => fuseCompletely(fuseCanonicalWithFirst(s))
      case _                 => s
    }
  }

  /** Fuse a <code>StmBuild</code> with its first input stream.
    */
  def fuseWithFirst(stm: Expr /* Stm<A; n> */ ): Expr /* Stm<A; n> */ = {
    val s = StmCanonPass.canonicalize(
      PartialEvalPass.partialEval(stm).asInstanceOf[StmBuild]
    )
    fuseCanonicalWithFirst(s)
  }

  /** Fuse a `StmBuild` that is already in canonical form with its first stream
    * input.
    */
  private def fuseCanonicalWithFirst(
      s: StmBuild /* Stm<A; n> */
  ): Expr /* Stm<A; n> */ = {
    val outerSeed = s.seed.asInstanceOf[Tuple]
    val inputStm = outerSeed.elems.head match {
      case s: StmBuild => StmCanonPass.canonicalize(s)
      case _ =>
        throw new IllegalArgumentException(
          "No input streams found to fuse with."
        )
    }
    StmBuild(
      s.length,
      // Replace the inner stream with an empty tuple in the new seed.
      // This minimizes the need for updating the indices in tuple access
      // expressions.
      Tuple(Tuple(Tuple() +: outerSeed.elems.tail: _*), inputStm.seed), {
        val acc = Param("acc")
        Function(
          acc,
          fuseFunctionBodies(
            newAcc = acc,
            oldAcc = s.nextF.param,
            outerBody = s.nextF.body,
            innerNextF = inputStm.nextF,
            innerStmAccArity = inputStm.seed.asInstanceOf[Tuple].elems.length,
            outerStmAccArity = s.seed.asInstanceOf[Tuple].elems.length
          )
        )
      }
    )
  }

  private def fuseFunctionBodies(
      newAcc: Param,
      oldAcc: Param,
      outerBody: Expr,
      innerNextF: Function,
      innerStmAccArity: Int,
      outerStmAccArity: Int
  ): Expr = {
    val e = outerBody match {
      case IfThenElse(cond, trueE, falseE) =>
        IfThenElse(
          // TODO: What if the condition somehow uses `acc.__0`?
          cond,
          fuseFunctionBodies(
            newAcc,
            oldAcc,
            trueE,
            innerNextF,
            innerStmAccArity,
            outerStmAccArity
          ),
          fuseFunctionBodies(
            newAcc,
            oldAcc,
            falseE,
            innerNextF,
            innerStmAccArity,
            outerStmAccArity
          )
        )
      case _: TupleAccess | _: VecAccess | _: StmNext | _: FunCall | _: Param =>
        ???
      case DontCare => DontCare
      case Tuple(a, out) =>
        a match {
          case Tuple(
                TupleAccess(StmNext(TupleAccess(p, IntCst(0))), IntCst(0)),
                as @ _*
              ) if p == oldAcc =>
            // CASE 1: StmNext() called.
            //         Update the inner accumulator.
            val innerNext = Param("innerNext")
            // HACK: Expand all tuples manually.
            // Ideally this would be a canonicalization pass, but I think
            // figuring out the arity of a tuple-valued param requires input
            // from the type system.
            val innerNext0 = tupleExpand(innerNext.__0, innerStmAccArity)
            val newAcc0 = tupleExpand(newAcc.__0, outerStmAccArity)
            val subInnerNextData =
              Map[Expr, Expr](
                StmNext(oldAcc.__0).__1 -> OptionAccess(
                  innerNext.__1,
                  (e: Expr) => e,
                  (_: Expr) => DontCare
                )
              )
            Let(
              innerNext,
              FunCall(innerNextF, newAcc.__1),
              OptionAccess(
                innerNext.__1,
                // CASE 1a: Received next element from inner stream.
                //          Update the outer accumulator.
                (_: Expr) =>
                  Tuple(
                    Tuple(
                      Tuple(
                        Tuple() +: as.map(a =>
                          a.substitute(subInnerNextData)
                        ): _*
                      ),
                      innerNext0
                    ),
                    out.substitute(subInnerNextData)
                  ),
                // CASE 1b: Inner stream did not produce element yet
                //          Leave the outer accumulator as-is.
                (_: Expr) => Tuple(Tuple(newAcc0, innerNext0), NNone)
              )
            )
          case Tuple(TupleAccess(p, IntCst(0)), as @ _*) if p == oldAcc =>
            // HACK: Expand tuple manually, as above.
            val newAcc1 = tupleExpand(newAcc.__1, innerStmAccArity)
            // CASE 2: StmNext() not called, so leave the inner accumulator
            //         as-is.
            Tuple(Tuple(Tuple(Tuple() +: as: _*), newAcc1), out)
          case Tuple(x, _ @_*) =>
            throw new IllegalArgumentException(
              s"I can't tell whether StmNext() is being called in ${x} (where oldAcc = ${oldAcc})."
            )
          case _ => ???
        }
      case _: IntExpr | _: BoolExpr | _: VecBuild | _: StmBuild | _: Function |
          _: Tuple =>
        throw new IllegalArgumentException(
          "Could not fuse function bodies due to an apparent type error."
        )
    }
    e.substitute(oldAcc -> TupleAccess(newAcc, 0))
  }

  /** Expand an expression that evaluates to a tuple. For example, `acc.__0` may
    * be expanded to `Tuple(acc.__0.__0, acc.__0.__1)`.
    *
    * @param e
    *   Expression to expand
    * @param n
    *   Number of elements in the expanded tuple
    */
  private def tupleExpand(e: Expr, n: Int): Expr = {
    Tuple((0 until n).map(i => TupleAccess(e, i)): _*)
  }
}
