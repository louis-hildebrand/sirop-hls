package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

/** Transformations for moving [[mhir.ir.LetStm]] around within an expression.
  */
object LetStmMover {

  /** Move [[mhir.ir.LetStm]] up towards the root of the AST.
    *
    * @example
    *   We want to turn an expression like this:
    *   {{{
    *       StmMap(
    *           let s1 = (
    *               let s0 = StmZip(input, StmCount(n)) in
    *               StmMap(s0, (x, y) => x + y)
    *           ) in
    *           StmPrefix(s1, m),
    *           x => x + 5
    *       )
    *   }}}
    *   into something like
    *   {{{
    *       let s0 = StmZip(input, StmCount(n)) in
    *       let s1 = StmMap(s0, (x, y) => x + y) in
    *       StmMap(StmPrefix(s1, m), x => x + 5)
    *   }}}
    *   (However, this would all be done in terms of [[mhir.ir.StmBuild]], since
    *   expressions must be lowered before this transformation can be applied.)
    *
    * @param e
    *   the expression to transform.
    */
  def moveUp(e: Expr): Expr = {
    require(
      e.hasType,
      "Expression must be type checked before moving LetStm up."
        + s" (Found expression $e)"
    )
    require(
      !e.hasSyntaxSugar,
      "Syntax sugar must be removed before moving LetStm up."
        + s" (Found expression $e)"
      // ... otherwise, how would we know when it is legal to move the LetStm
      // up? In some cases (e.g., StmMap, StmZip) we would want to pull LetStm
      // out of an expression. But in other cases this would not be legal
      // (e.g., inside a function, since the LetStm may depend on the function
      // param).
    )
    val result = e match {
      case s: StmBuild =>
        def pullOutLet(s: StmBuild): Expr = {
          val x = s.seedByVar
            .find({
              case (_, _: LetStm) => true
              case _              => false
            })
            .map(_._1)
          x match {
            case Some(x) =>
              val LetStm(bufSize, y, in, out) =
                s.seedByVar(x).asInstanceOf[LetStm]
              val newY = y.freshCopy
              val newOut = out.tchk().subPreserveType(y -> newY)
              LetStm(
                bufSize,
                newY,
                in,
                pullOutLet(
                  StmBuild(
                    s.n,
                    s.data,
                    s.valid,
                    s.equations.map({ case (x2, (z, next)) =>
                      if (x2 == x) {
                        x2 -> (newOut, next)
                      } else {
                        x2 -> (z, next)
                      }
                    })
                  )()
                )
              )()
            case None =>
              s
          }
        }
        val withTransformedProducers =
          StmBuild(
            s.n,
            s.data,
            s.valid,
            s.equations.map({ case (x, (z, next)) =>
              x.typ match {
                case TyData(_) => x -> (z, next)
                case _: TyStm  => x -> (moveUp(z), next)
                case _         => ???
              }
            })
          )()
        pullOutLet(withTransformedProducers)
      case LetStm(bufSize, x, in, out) =>
        def pullOutLet(let: LetStm): Expr = {
          let match {
            case LetStm(
                  xBufSize,
                  x,
                  LetStm(yBufSize, y, innerIn, innerOut),
                  out
                ) =>
              val newY = y.freshCopy
              val newInnerOut = innerOut.tchk().subPreserveType(y -> newY)
              LetStm(
                yBufSize,
                newY,
                innerIn,
                pullOutLet(LetStm(xBufSize, x, newInnerOut, out)())
              )()
            case let =>
              let
          }
        }
        val withTransformedChildren =
          LetStm(bufSize, x, moveUp(in), moveUp(out))()
        pullOutLet(withTransformedChildren)
      case e =>
        e.map(moveUp)
    }
    val typedResult = result.tchk()
    assert(typedResult.typ ~= e.typ, "moving LetStm up should preserve type")
    typedResult
  }

  /** Move [[mhir.ir.LetStm]] down towards the leaves of the AST if possible.
    *
    * For example, we want to turn an expression like this:
    * {{{
    *   let stm s = ... in
    *   StmMap(StmZip(s, s), x => x + 5)
    * }}}
    * into an expression like this:
    * {{{
    *   StmMap(
    *     let stm s = ... in StmZip(s, s),
    *     x => x + 5
    *   )
    * }}}
    *
    * @param e
    *   the expression to transform.
    */
  def moveDown(e: Expr): Expr = {
    require(
      e.hasType,
      "Expression must be type checked before moving LetStm down."
        + s" (Found expression $e)"
    )
    require(
      !e.hasSyntaxSugar,
      "Syntax sugar must be removed before moving LetStm down."
        + s" (Found expression $e)"
      // ... otherwise, how would we know when it is legal to move the LetStm
      // down?
    )
    def move(e: Expr): Expr = {
      e match {
        case LetStm(bufSize, x, in, out) =>
          def pullOutStmBuild(let: LetStm): Expr = {
            let.out match {
              case s: StmBuild =>
                val count = s.seedByVar.count({ case (y, z) =>
                  y.typ.isInstanceOf[TyStm] && z.freeVars.contains(x)
                })
                if (count == 1) {
                  StmBuild(
                    s.n,
                    s.data,
                    s.valid,
                    s.equations.map({
                      case (y, (z, ready))
                          if y.typ.isInstanceOf[TyStm]
                            && z.freeVars.contains(x) =>
                        y -> (
                          pullOutStmBuild(
                            LetStm(let.bufSize, let.x, let.in, z)()
                          ),
                          ready
                        )
                      case eqn => eqn
                    })
                  )()
                } else {
                  let
                }
              case _ => let
            }
          }
          val withTransformedChildren =
            LetStm(bufSize, x, move(in), move(out))()
          pullOutStmBuild(withTransformedChildren)
        case e => e.map(move)
      }
    }
    val result = move(e)
    val checkedResult = result.tchk()
    assert(
      checkedResult.typ ~= e.typ,
      "moving LetStm down should preserve type"
    )
    checkedResult
  }
}
