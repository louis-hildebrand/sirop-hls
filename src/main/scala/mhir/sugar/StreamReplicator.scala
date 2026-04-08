package mhir.sugar

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

import scala.annotation.tailrec

/** Scope of a variable within a stream that is parallelized by replication.
  */
private sealed trait AccVarScope

/** Scope of an accumulator variable which depends on the vector index.
  */
private object PrivateScope extends AccVarScope

/** Scope of an accumulator variable which does not depend on the vector index.
  */
private object SharedScope extends AccVarScope

/** The stream replication transformation.
  *
  * To apply the transformation, use the extension method
  * [[StreamReplicator.StreamReplication.replicate]]. The implicit class
  * [[StreamReplicator.StreamReplication]] must be in scope.
  *
  * {{{
  *   import mhir.ir.StreamReplicator.StreamReplication
  *   stm.replicate(16, i, Set())
  * }}}
  */
object StreamReplicator {

  private val logger = Logger(getClass.getName)

  implicit class StreamReplication(stm: Expr) {

    /** Parallelize this stream by duplicating its body <code>m</code> times.
      *
      * @param m
      *   the number of instances of the body to create.
      * @param i
      *   the index within the vector.
      * @param varsToReplicate
      *   input stream variables that must also be replicated.
      * @return
      *   a stream of vectors of length <code>m</code>.
      */
    def replicate(
        m: Expr,
        i: Param,
        varsToReplicate: Set[Param]
    ): Expr = {
      logger.trace(
        s"performing stream replication on ${this.stm.className}: ${this.stm}"
      )
      require(
        m.typ.isInstanceOf[TyUInt],
        "Number of times to replicate body must be an unsigned integer."
      )
      require(
        this.stm.typ.isInstanceOf[TyStm],
        "Expression to replicate must have the type of a stream."
      )
      require(
        !this.stm.hasSyntaxSugar,
        "Expression to replicate must not have any syntax sugar."
      )
      val result = this.stm match {
        case x: Param =>
          if (varsToReplicate.contains(x)) {
            val TyStm(t, k) = x.typ.asInstanceOf[TyStm]
            x.rebuild(TyStm(TyVec(t, m), k))
          } else {
            val TyStm(elemTyp, n) = x.typ
            val s = Param("s")(TyStm(elemTyp, -1))
            StmBuild(
              n,
              VecBuild(m, m.typ ::+ (_ => StmData(s)()))(),
              True,
              Map[Param, (Expr, Expr)](
                s -> (x, True)
              )
            )()
              .annotate(NoInputsAfterLastOut)
              .annotateWithName("ReplicatedParam")
              .tchk()
          }
        case stm: StmBuild =>
          replicateStmBuild(
            stm,
            m = m,
            i = i,
            varsToReplicate = varsToReplicate
          )
        case let: LetStm =>
          replicateLetStm(let, m = m, i = i, varsToReplicate = varsToReplicate)
        case e =>
          throw new IllegalArgumentException(s"Cannot replicate expression: $e")
      }
      result.tchk()
    }
  }

  private def replicateStmBuild(
      stm: StmBuild,
      m: Expr,
      i: Param,
      varsToReplicate: Set[Param]
  ): StmBuild = {
    if (stm.n.freeVars.contains(i)) {
      throw new IllegalArgumentException(
        "Stream length must not vary with vector index."
          + s" (Found vector index $i and stream length ${stm.n}.)"
      )
    }
    // Some variables depend on the vector index i and must therefore be turned
    // into vectors.
    // Some variables do not depend on the vector index i and can therefore be
    // shared.
    val scopes = this.findScopeByVar(stm, i, varsToReplicate)
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
        case _: TyStm =>
          if (next.freeVars.contains(i)) {
            throw new IllegalArgumentException(
              "Input stream `ready` cannot depend on the vector index."
                + s" (Found vector index $i and `ready` expression $next.)"
            )
          }
          val newZ = scopes(x) match {
            case PrivateScope => z.replicate(m, i, varsToReplicate)
            case SharedScope  => z
          }
          newX -> (newZ, next)
        case TyData(_) =>
          scopes(x) match {
            case SharedScope => newX -> (z, next.subPreserveType(subs))
            case PrivateScope =>
              val newZ = z match {
                case _: Undefined => Undefined(TyVec(x.typ, m))
                case _            => VecBuild(m, Function(i, z)())()
              }
              newX -> (
                newZ,
                VecBuild(m, Function(i, next.subPreserveType(subs))())()
              )
          }
        case t =>
          throw new IllegalArgumentException(s"Invalid accumulator type $t.")
      }
    })
    val newData = {
      val validDependsOnI = stm.valid.freeVars
        .exists(x => x == i || scopes.get(x).contains(PrivateScope))
      if (validDependsOnI) {
        throw new IllegalArgumentException(
          "Stream `valid` must not depend on the vector index."
            + s" (Found vector index $i and stream `valid` expression ${stm.valid}.)"
        )
      }
      VecBuild(m, Function(i, stm.data.subPreserveType(subs))())()
    }
    val s = StmBuild(stm.n, newData, stm.valid, newEquations)(
      annotations = stm.annotations
    )
    assert(
      !s.freeVars.contains(i),
      "there should be no more free occurrences of the vector index i"
    )
    val expectedFreeVars = (stm.freeVars ++ m.freeVars) - i
    assert(
      s.freeVars.subsetOf(expectedFreeVars),
      "replication should not introduce any new free variables"
        + s" (expected $expectedFreeVars, found ${s.freeVars})"
    )
    s.tchk().asInstanceOf[StmBuild]
  }

  private def findScopeByVar(
      stm: StmBuild,
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
      x -> next.freeVars.intersect(stm.accVars)
    })
    propagateScopes(
      scopeByVar = stm.equations
        .map({ case (x, (z, next)) =>
          val dependsOnI = (next.freeVars.contains(i)
            || z.freeVars.intersect(varsToReplicate + i).nonEmpty)
          x -> (if (dependsOnI) PrivateScope else SharedScope)
        }),
      dependencies = dependencies,
      i = i
    )
  }

  private def replicateLetStm(
      let: LetStm,
      m: Expr,
      i: Param,
      varsToReplicate: Set[Param]
  ): LetStm = {
    val LetStm(bufSize, x, in, out) = let
    val replicateIn = in.freeVars.intersect(varsToReplicate + i).nonEmpty
    if (replicateIn) {
      val newX =
        x.replicate(m = m, i = i, varsToReplicate = varsToReplicate + x)
      val newIn =
        in.replicate(m = m, i = i, varsToReplicate = varsToReplicate)
      val newOut =
        out.replicate(m = m, i = i, varsToReplicate = varsToReplicate + x)
      LetStm(bufSize, newX.asInstanceOf[Param], newIn, newOut)()
    } else {
      val newOut =
        out.replicate(m = m, i = i, varsToReplicate = varsToReplicate)
      LetStm(bufSize, x, in, newOut)()
    }
  }
}
