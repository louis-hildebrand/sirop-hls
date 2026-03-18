package mhir.ir

import com.typesafe.scalalogging.Logger
import mhir.logging.time
import org.slf4j.event.Level

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
    def subPreserveType(subs: Map[Expr, Expr]): Expr = {
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
                time(
                  s"performing subs $subs in (${x.name} : ${x.typ}) => ...",
                  Level.TRACE
                ) {
                  val wouldCapture = subs.exists({ case (_, rhs) =>
                    rhs.freeVars.contains(x)
                  })
                  val newX = if (wouldCapture) x.freshCopy else x
                  val newSubs =
                    subs
                      // Substitutions with `x` free on the LHS will never match
                      // again, since `x` is now bound.
                      .filter({ case (lhs, _) => !lhs.freeVars.contains(x) })
                      // Rename the bound variable if necessary
                      .++(if (x == newX) Seq() else Seq(x -> newX))
                  Function(
                    // There may be substitutions to do within the type annotation
                    Param(newX.prefix, newX.id)(newX.typ.substitute(subs)),
                    body.subPreserveType(newSubs)
                  )(f.typ)
                }
              case let @ LetStm(bufSize, x, in, out) =>
                time(s"performing subs $subs in let $x = ...") {
                  val wouldCapture = subs.exists({ case (_, rhs) =>
                    rhs.freeVars.contains(x)
                  })
                  val newX = if (wouldCapture) x.freshCopy else x
                  val newSubs = {
                    subs
                      // Substitutions with `x` free on the LHS will never match
                      // again, since `x` is now bound.
                      .filter({ case (lhs, _) => !lhs.freeVars.contains(x) })
                      // Rename the bound variable if necessary
                      .++(if (x == newX) Seq() else Seq(x -> newX))
                  }
                  LetStm(
                    // `x` is not bound here, so use the old subs
                    bufSize.subPreserveType(subs),
                    // There may be substitutions to do within the type annotation
                    Param(newX.prefix, newX.id)(newX.typ.substitute(subs)),
                    // `x` is not bound here, so use the old subs
                    in.subPreserveType(subs),
                    // `x` is bound here, so use the new subs
                    out.subPreserveType(newSubs)
                  )(let.typ)
                }
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
                  )(s.typ, annotations = s.annotations)
                }
              case e: SyntaxSugar => e.sugarSubAndKeepType(subs)
              case e =>
                e.rebuild(e.typ, e.children.map(e => e.subPreserveType(subs)))
            }
        }
        if (this.expr.hasType) {
          assert(
            out.typ ~= this.expr.typ,
            s"the type should be preserved after substitution (expected ${this.expr.typ}, found ${out.typ} after substitutions $subs in ${this.expr})"
          )
        }
        // The expressions to replace may occur within the type (e.g., in the
        // length of a vector)
        val newType = out.typ.substitute(subs)
        out.rebuild(newType)
      }
    }

    /** See [[subPreserveType(subs*)]].
      */
    def subPreserveType(sub: (Expr, Expr)*): Expr = {
      subPreserveType(Map(sub: _*))
    }

    /** Perform the given substitutions in this expression while erasing the
      * type annotations.
      *
      * @param subs
      *   a map from old expressions (i.e., the ones to be replaced) to new
      *   expressions (i.e., what to replace the old expressions with).
      */
    def subAndEraseType(subs: Map[Expr, Expr]): Expr = {
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
                val wouldCapture = subs.exists({ case (_, rhs) =>
                  rhs.freeVars.contains(x)
                })
                val newX = if (wouldCapture) x.freshCopy else x
                val newSubs =
                  subs
                    // Substitutions with `x` free on the LHS will never match
                    // again, since `x` is now bound.
                    .filter({ case (lhs, _) => !lhs.freeVars.contains(x) })
                    // Rename the bound variable if necessary
                    .++(if (x == newX) Seq() else Seq(x -> newX))
                Function(
                  // There may be substitutions to do within the type annotation
                  Param(newX.prefix, newX.id)(newX.typ.substitute(subs)),
                  body.subAndEraseType(newSubs)
                )()
              case LetStm(bufSize, x, in, out) =>
                val wouldCapture = subs.exists({ case (_, rhs) =>
                  rhs.freeVars.contains(x)
                })
                val newX = if (wouldCapture) x.freshCopy else x
                val newSubs = {
                  subs
                    // Substitutions with `x` free on the LHS will never match
                    // again, since `x` is now bound.
                    .filter({ case (lhs, _) => !lhs.freeVars.contains(x) })
                    // Rename the bound variable if necessary
                    .++(if (x == newX) Seq() else Seq(x -> newX))
                }
                LetStm(
                  // `x` is not bound here, so use the old subs
                  bufSize.subAndEraseType(subs),
                  // There may be substitutions to do within the type annotation
                  Param(newX.prefix, newX.id)(newX.typ.substitute(subs)),
                  // `x` is not bound here, so use the old subs
                  in.subAndEraseType(subs),
                  // `x` is bound here, so use the new subs
                  out.subAndEraseType(newSubs)
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
    def subAndEraseType(sub: (Expr, Expr)*): Expr = {
      subAndEraseType(Map(sub: _*))
    }
  }
}
