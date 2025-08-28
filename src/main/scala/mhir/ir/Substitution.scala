package mhir.ir

private[ir] trait Substitution {
  implicit class Substitution(expr: Expr) {

    /** Perform the given substitutions in this expression while checking, at
      * each step, that the new type is compatible with the original one.
      *
      * @param subs
      *   a map from old expressions (i.e., the ones to be replaced) to new
      *   expressions (i.e., what to replace the old expressions with).
      */
    def subPreserveType(subs: Map[Expr, Expr]): Expr = {
      val out = if (subs.isEmpty) {
        this.expr
      } else {
        subs.get(this.expr) match {
          case Some(v) => v
          case None =>
            this.expr match {
              case f @ Function(x, body) =>
                // Rename both
                //   (1) to avoid variable capture and
                //   (2) in case f.param appears free in the old value of a
                //       substitution (i.e., the value to be replaced)
                val newX = x.freshCopy
                Function(
                  // There may be substitutions to do within the type annotation
                  newX.subPreserveType(subs).asInstanceOf[Param],
                  body.subPreserveType(x -> newX).subPreserveType(subs)
                )(f.typ)
              case let @ LetStm(x, in, out) =>
                // Rename both
                //   (1) to avoid variable capture and
                //   (2) in case f.param appears free in the old value of a
                //       substitution (i.e., the value to be replaced)
                val newX = x.freshCopy
                LetStm(
                  // There may be substitutions to do within the type annotation
                  newX.subPreserveType(subs).asInstanceOf[Param],
                  in.subPreserveType(subs),
                  out.subPreserveType(x -> newX).subPreserveType(subs)
                )(let.typ)
              case s: StmBuild =>
                // Rename both
                //   (1) to avoid variable capture and
                //   (2) in case an accumulator variable appears free in the old
                //       value of a substitution (i.e., the value to be replaced)
                val renamed = s.renameVars
                StmBuild(
                  renamed.n.subPreserveType(subs),
                  renamed.data.subPreserveType(subs),
                  renamed.valid.subPreserveType(subs),
                  renamed.equations.map({ case (x, (z, next)) =>
                    val newX = x.subPreserveType(subs).asInstanceOf[Param]
                    val newZ = z.subPreserveType(subs)
                    val newNext = next.subPreserveType(subs)
                    newX -> (newZ, newNext)
                  })
                )(s.typ)
              case e: SyntaxSugar => e.sugarSubAndKeepType(subs)
              case e =>
                e.rebuild(e.typ, e.children.map(e => e.subPreserveType(subs)))
            }
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
      if (subs.isEmpty) {
        this.expr
      } else {
        subs.get(this.expr) match {
          case Some(v) =>
            v
          case None =>
            this.expr match {
              case Function(x, body) =>
                // Rename both
                //   (1) to avoid variable capture and
                //   (2) in case f.param appears free in the old value of a
                //       substitution (i.e., the value to be replaced)
                val newX = x.freshCopy
                Function(
                  // TODO: What about substitutions in the type annotation,
                  //       like with subPreserveType?
                  newX,
                  body.subAndEraseType(x -> newX).subAndEraseType(subs)
                )()
              case LetStm(x, in, out) =>
                // Rename both
                //   (1) to avoid variable capture and
                //   (2) in case f.param appears free in the old value of a
                //       substitution (i.e., the value to be replaced)
                val newX = x.freshCopy
                LetStm(
                  newX,
                  // TODO: What about substitutions in the type annotation,
                  //       like with subPreserveType?
                  in.subAndEraseType(subs),
                  out.subAndEraseType(x -> newX).subAndEraseType(subs)
                )()
              case s: StmBuild =>
                // Rename both
                //   (1) to avoid variable capture and
                //   (2) in case an accumulator variable appears free in the old
                //       value of a substitution (i.e., the value to be replaced)
                val renamingSubs: Map[Expr, Expr] =
                  s.accVars.map(x => x -> x.freshCopy).toMap
                StmBuild(
                  s.n.subAndEraseType(subs),
                  s.data.subAndEraseType(renamingSubs).subAndEraseType(subs),
                  s.valid.subAndEraseType(renamingSubs).subAndEraseType(subs),
                  s.equations.map({ case (x, (z, next)) =>
                    val newX = renamingSubs(x).asInstanceOf[Param]
                    val newZ = z.subAndEraseType(subs)
                    val newNext =
                      next.subAndEraseType(renamingSubs).subAndEraseType(subs)
                    newX -> (newZ, newNext)
                  })
                )()
              case e: SyntaxSugar =>
                e.sugarSubAndEraseType(subs)
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
