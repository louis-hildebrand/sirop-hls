package mhir.ir

import mhir.ir.Lowering.TypeLowering
import mhir.ir.TypeChecker.TypeCheck

import scala.annotation.tailrec

object StreamReplicator {
  implicit class StreamReplication(stm: StmBuild) {

    /** Parallelize this stream by duplicating its body <code>m</code> times.
      *
      * @param m
      *   The number of instances of the body to create.
      * @param i
      *   The index within the vector.
      * @param varsToReplicate
      *   Input stream variables that must also be replicated.
      * @return
      *   A stream of vectors of length <code>m</code>.
      */
    def replicate(
        m: Expr,
        i: Param,
        varsToReplicate: Set[Param]
    ): StmBuild = {
      if (stm.n.freeVars().contains(i)) {
        throw new IllegalArgumentException(
          "Stream length must not vary with vector index."
            + s" (Found vector index $i and stream length ${stm.n}.)"
        )
      }
      // Some variables depend on the vector index i and must therefore be turned
      // into vectors.
      // Some variables do not depend on the vector index i and can therefore be
      // shared.
      val scopes = this.findScopeByVar(i, varsToReplicate)
      // If the type of the variables may change, then it seems like a good idea
      // to just introduce a whole new variable.
      // This way I can check that no uses of the original variable were
      // accidentally left behind and there aren't multiple variables with the
      // same name but different types floating around.
      val oldToNewVar = scopes
        .map({
          case (x, PrivateScope) =>
            x -> x.freshCopy.rebuild(TyVec(x.typ, m).lower).asInstanceOf[Param]
          case (x, SharedScope) => x -> x
        })
      val subs: Map[Expr, Expr] = scopes.flatMap({
        case (x, PrivateScope) =>
          val newX = oldToNewVar(x)
          x.typ match {
            case TyStm(t, _) =>
              Some(StmData(x)() -> VecAccess(StmData(newX)(TyVec(t, m)), i)(t))
            case t if t.isData =>
              Some(x -> VecAccess(newX, i)(x.typ))
            case t =>
              throw new IllegalArgumentException(
                s"Invalid accumulator type $t."
              )
          }
        case (_, SharedScope) => None
      })
      val newEquations = stm.equations.map({ case (x, (z, next)) =>
        val newX = oldToNewVar(x)
        x.typ match {
          case TyStm(t, k) =>
            if (next.freeVars().contains(i)) {
              throw new IllegalArgumentException(
                "Input stream `ready` cannot depend on the vector index."
                  + s" (Found vector index $i and `ready` expression $next.)"
              )
            }
            val newZ = scopes(x) match {
              case PrivateScope =>
                z match {
                  case z: StmBuild => z.replicate(m, i, varsToReplicate)
                  case z: Param    => z.rebuild(TyStm(TyVec(t, m), k))
                  case z =>
                    throw new IllegalArgumentException(
                      s"Invalid initial value for input stream: $z."
                    )
                }
              case SharedScope => z
            }
            newX -> (newZ, next)
          case t if t.isData =>
            scopes(x) match {
              case SharedScope => newX -> (z, next.subPreserveType(subs))
              case PrivateScope =>
                newX -> (
                  VecBuild(m, Function(i, z)())(),
                  VecBuild(m, Function(i, next.subPreserveType(subs))())()
                )
            }
          case t =>
            throw new IllegalArgumentException(s"Invalid accumulator type $t.")
        }
      })
      val newData = {
        val validDependsOnI = stm.valid
          .freeVars()
          .exists(x => x == i || scopes.get(x).contains(PrivateScope))
        if (validDependsOnI) {
          throw new IllegalArgumentException(
            "Stream `valid` must not depend on the vector index."
              + s" (Found vector index $i and stream `valid` expression ${stm.valid}.)"
          )
        }
        VecBuild(m, Function(i, stm.data.subPreserveType(subs))())()
      }
      val s = StmBuild(stm.n, newData, stm.valid, newEquations)()
      assert(
        !s.freeVars().contains(i),
        "there should be no more free occurrences of the vector index i"
      )
      val expectedFreeVars = (stm.freeVars() ++ m.freeVars()) - i
      assert(
        s.freeVars().subsetOf(expectedFreeVars),
        "replication should not introduce any new free variables"
          + s" (expected $expectedFreeVars, found ${s.freeVars()})"
      )
      s.tchk().asInstanceOf[StmBuild]
    }

    private def findScopeByVar(
        i: Param,
        varsToReplicate: Set[Param]
    ): Map[Param, AccVarScope] = {
      // If an accumulator variable depends on private variables, then it must also be private
      @tailrec
      def propagateScopes(
          scopeByVar: Map[Param, AccVarScope],
          dependencies: Map[Param, Set[Param]],
          i: Param
      ): Map[Param, AccVarScope] = {

        require(scopeByVar.keySet == dependencies.keySet)
        val newScopeByVar = dependencies.map({ case (x, deps) =>
          scopeByVar(x) match {
            case PrivateScope => x -> PrivateScope
            case SharedScope =>
              val anyDepsPrivate =
                deps.exists(y => scopeByVar(y) == PrivateScope)
              x -> (if (anyDepsPrivate) PrivateScope else SharedScope)
          }
        })
        if (newScopeByVar == scopeByVar) {
          scopeByVar
        } else {
          propagateScopes(newScopeByVar, dependencies, i)
        }
      }

      val dependencies = stm.equations.map({ case (x, (_, next)) =>
        x -> next.freeVars().intersect(stm.accVars)
      })
      propagateScopes(
        scopeByVar = stm.equations
          .map({ case (x, (z, next)) =>
            val dependsOnI = next.freeVars().contains(i) || z
              .freeVars()
              .intersect(varsToReplicate + i)
              .nonEmpty
            x -> (if (dependsOnI) PrivateScope else SharedScope)
          }),
        dependencies = dependencies,
        i = i
      )
    }
  }
}
