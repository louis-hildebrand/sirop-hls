package mhir.ir

import com.typesafe.scalalogging.Logger
import mhir.ir.Substitution.enterBinder
import mhir.logging.time

private[ir] trait Substitution {

  implicit class Substitute(expr: Expr) {

    private implicit val logger: Logger = Logger(getClass.getName)

    /** Perform the given substitutions in this expression while checking, at
      * each step, that the new type is compatible with the original one.
      *
      * @param subs
      *   a map from old expressions (i.e., the ones to be replaced) to new
      *   expressions (i.e., what to replace the old expressions with).
      */
    def subPreserveType(
        subs: Map[Expr, Expr]
    )(implicit c: Canonicalizer): Expr = {
      // !!!!!!!!!! WARNINGS !!!!!!!!!!
      //
      // There are a few tricky points to consider with substitution.
      //
      // (A) For any expression, there may be substitutions to do in the type.
      // For example, if we're performing the substitution n -> 4 in an
      // expression of type Vec[u8, n], we want the expression to have type
      // Vec[u8, 4] after substitution.
      //
      // (B) Suppose we have a variable binder like Function(x, body).
      // The same warnings apply for any expression which binds a variable x.
      //
      // Things to watch out for (correctness):
      //   (1) Variable capture (i.e., x appears free in the right-hand side of
      //       a substitution) and
      //   (2) Mistakenly replacing a bound variable (i.e., x appears free in
      //       the left-hand side of a substitution).
      //
      // Things to watch out for (performance):
      //   (1) Don't visit any child more than once! Otherwise, the
      //       runtime will be exponential in the number of binders.
      //       In some cases (particularly LetStm), it is common to have long
      //       chains of binders, so it would be disastrous to have exponential
      //       runtime here.
      val skip = subs.isEmpty ||
        this.expr.freeVars
          .union(this.expr.freeVarsInTypes)
          .intersect(
            subs
              .map({ case (lhs, _) => lhs.freeVars })
              .reduce(_ ++ _)
          )
          .isEmpty
      if (skip) {
        this.expr
      } else {
        val out = subs.get(this.expr) match {
          case Some(v) => v
          case None =>
            this.expr match {
              case f @ Function(x, body) =>
                val (Seq(newX), newSubs) = enterBinder(Seq(x), subs)
                Function(
                  newX,
                  body.subPreserveType(newSubs)
                )(f.typ.substitute(subs))
              case let @ LetStm(bufSize, x, in, out) =>
                val (Seq(newX), newSubs) = enterBinder(Seq(x), subs)
                LetStm(
                  // `x` is not bound here, so use the old subs
                  bufSize.subPreserveType(subs),
                  newX,
                  // `x` is not bound here, so use the old subs
                  in.subPreserveType(subs),
                  // `x` is bound here, so use the new subs
                  out.subPreserveType(newSubs)
                )(let.typ.substitute(subs))
              case s: StmBuild =>
                time(s"performing subs $subs in StmBuild...") {
                  val rhsFreeVars = subs.toSeq
                    .flatMap({ case (_, rhs) => rhs.freeVars })
                    .toSet
                  val renamings = s.accVars
                    .flatMap({ x =>
                      val wouldCapture = rhsFreeVars.contains(x)
                      if (wouldCapture) Some(x -> x.freshCopy) else None
                    })
                    .toMap
                  val newSubs =
                    subs
                      // Substitutions where an accumulator variable appears
                      // free on the left-hand side are no longer needed: that
                      // variable is bound now.
                      .filter({ case (lhs, _) =>
                        lhs.freeVars.intersect(s.accVars).isEmpty
                      })
                      .++(renamings)
                  StmBuild(
                    // The accumulator variables are bound in `data`, `valid`,
                    // and the next expression of each accumulator.
                    // In those cases, use the new subs; otherwise, use the old
                    // subs.
                    s.n.subPreserveType(subs),
                    s.data.subPreserveType(newSubs),
                    s.valid.subPreserveType(newSubs),
                    s.equations.map({ case (x, (z, next)) =>
                      // There may be substitutions to do in the type
                      val renamedX = renamings.getOrElse(x, x)
                      val newX = Param(renamedX.prefix, renamedX.id)(
                        renamedX.typ.substitute(subs)
                      )
                      val newZ = z.subPreserveType(subs)
                      val newNext = next.subPreserveType(newSubs)
                      newX -> (newZ, newNext)
                    })
                  )(s.typ.substitute(subs), annotations = s.annotations)
                }
              case ia @ InterpretAs(e, targetTyp) =>
                InterpretAs(
                  e.subPreserveType(subs),
                  targetTyp.substitute(subs)
                )(ia.typ.substitute(subs))
              case e: SyntaxSugar => e.sugarSubAndKeepType(subs)
              case e =>
                e.rebuild(
                  e.typ.substitute(subs),
                  e.children.map(e => e.subPreserveType(subs))
                )
            }
        }
        if (this.expr.hasType) {
          val expectedTyp = this.expr.typ.substitute(subs)
          assert(
            out.typ ~= expectedTyp,
            s"the type should be preserved after substitution (expected $expectedTyp, found ${out.typ} after substitutions $subs in ${this.expr})"
          )
        }
        out
      }
    }

    /** See [[subPreserveType(subs*)]].
      */
    def subPreserveType(sub: (Expr, Expr)*)(implicit c: Canonicalizer): Expr = {
      subPreserveType(Map(sub: _*))
    }

    /** Perform the given substitutions in this expression while erasing the
      * type annotations.
      *
      * @param subs
      *   a map from old expressions (i.e., the ones to be replaced) to new
      *   expressions (i.e., what to replace the old expressions with).
      */
    def subAndEraseType(
        subs: Map[Expr, Expr]
    )(implicit c: Canonicalizer): Expr = {
      // !!!!!!!!!! WARNINGS !!!!!!!!!!
      //
      // Suppose we have a variable binder like Function(x, body).
      // The same warnings apply for any expression which binds a variable x.
      //
      // Things to watch out for (correctness):
      //   (1) Variable capture (i.e., x appears free in the right-hand side of
      //       a substitution) and
      //   (2) Mistakenly replacing a bound variable (i.e., x appears free in
      //       the left-hand side of a substitution).
      //
      // Things to watch out for (performance):
      //   (1) Don't visit any child more than once! Otherwise, the
      //       runtime will be exponential in the number of binders.
      //       In some cases (particularly LetStm), it is common to have long
      //       chains of binders, so it would be disastrous to have exponential
      //       runtime here.
      if (subs.isEmpty) {
        this.expr
      } else {
        subs.get(this.expr) match {
          case Some(v) =>
            v
          case None =>
            this.expr match {
              case Function(x, body) =>
                val (Seq(newX), innerSubs) = enterBinder(Seq(x), subs)
                Function(newX, body.subAndEraseType(innerSubs))()
              case LetStm(bufSize, x, in, out) =>
                val (Seq(newX), innerSubs) = enterBinder(Seq(x), subs)
                LetStm(
                  // `x` is not bound here, so use the old subs
                  bufSize.subAndEraseType(subs),
                  newX,
                  // `x` is not bound here, so use the old subs
                  in.subAndEraseType(subs),
                  // `x` is bound here, so use the new subs
                  out.subAndEraseType(innerSubs)
                )()
              case s: StmBuild =>
                val rhsFreeVars = subs.toSeq
                  .flatMap({ case (_, rhs) => rhs.freeVars })
                  .toSet
                val renamings = s.accVars
                  .flatMap({ x =>
                    val wouldCapture = rhsFreeVars.contains(x)
                    if (wouldCapture) Some(x -> x.freshCopy) else None
                  })
                  .toMap
                val newSubs =
                  subs
                    // Substitutions where an accumulator variable appears
                    // free on the left-hand side are no longer needed: that
                    // variable is bound now.
                    .filter({ case (lhs, _) =>
                      lhs.freeVars.intersect(s.accVars).isEmpty
                    })
                    .++(renamings)
                StmBuild(
                  // The accumulator variables are bound in `data`, `valid`,
                  // and the next expression of each accumulator.
                  // In those cases, use the new subs; otherwise, use the old
                  // subs.
                  s.n.subAndEraseType(subs),
                  s.data.subAndEraseType(newSubs),
                  s.valid.subAndEraseType(newSubs),
                  s.equations.map({ case (x, (z, next)) =>
                    // There may be substitutions to do in the type
                    val renamedX = renamings.getOrElse(x, x)
                    val newX = Param(renamedX.prefix, renamedX.id)(
                      renamedX.typ.substitute(subs)
                    )
                    val newZ = z.subAndEraseType(subs)
                    val newNext = next.subAndEraseType(newSubs)
                    newX -> (newZ, newNext)
                  })
                )(annotations = s.annotations)
              case InterpretAs(e, targetTyp) =>
                InterpretAs(
                  e.subAndEraseType(subs),
                  targetTyp.substitute(subs)
                )()
              case e: SyntaxSugar =>
                e.sugarSubAndEraseType(subs)
              case Undefined(typ) =>
                Undefined(typ.substitute(subs))
              case e =>
                e.rebuildAndEraseType(
                  e.children.map(e => e.subAndEraseType(subs))
                )
            }
        }
      }
    }

    /** See [[subAndEraseType(subs*]].
      */
    def subAndEraseType(sub: (Expr, Expr)*)(implicit c: Canonicalizer): Expr = {
      subAndEraseType(Map(sub: _*))
    }
  }
}

object Substitution {

  /** Updates the parameters and substitutions for a new variable-binding
    * construct.
    *
    * There are two tricky cases to deal with when performing substitution. For
    * simplicity, consider the specific case of the function `f(x) = x + y`, but
    * they apply the same to any variable binder.
    *   1. The result of the substitution `x -> 42` should be `f(x) = x + y`,
    *      not `f(x) = 42 + y`. Occurrences of `x` within the body refer to the
    *      function parameter, not the `x` referred to by the substitution.
    *   1. The result of the substitution `y -> x` should be `f(x2) = x2 + x`,
    *      not `f(x) = x + x`. We must rename the function parameter to avoid
    *      capturing the occurrence of `x` on the right-hand side of the
    *      substitution.
    *
    * @param defs
    *   the variables defined by the binder.
    * @param subs
    *   the substitutions to perform outside the binder.
    * @return
    *   `(newParams, newSubs)` where `newParams` are the renamed variables for
    *   the binder and `newSubs` are the substitutions to perform inside the
    *   binder.
    */
  // TODO: Generalize this to work for things like StmBuild, where the defs are not ordered?
  def enterBinder(
      defs: Seq[Param],
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): (Seq[Param], Map[Expr, Expr]) = {
    val defSet = defs.toSet
    // (1) If the left-hand side refers to one of the variables in `defs`,
    //     then that substitution will never apply again.
    val filteredSubs = subs
      .filter({ case (lhs, _) => lhs.freeVars.intersect(defSet).isEmpty })
    // (2) Any variables in `defs` that appear free on the right-hand side of
    //     a substitution must be renamed, to avoid variable capture.
    val defsRenamings = defSet
      .intersect(subs.values.flatMap(_.freeVars).toSet)
      .map(x => x -> x.freshCopy)
      .toMap
    val newParams = defs.map({ x =>
      defsRenamings
        .getOrElse(x, x)
        .rebuild(x.typ.substitute(subs))
        .asInstanceOf[Param]
    })
    val newSubs = filteredSubs ++ defsRenamings
    (newParams, newSubs)
  }
}
