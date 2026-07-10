package mhir.gen.vhdl
package transform

import mhir.canonicalize._
import mhir.gen.vhdl.GenStmBuild
import mhir.ir._
import mhir.typecheck.TypeCheck

import scala.collection.immutable.ListMap

/** This transformation adds explicit arguments in functions and function calls
  * to represent the free variables accessed by the function.
  *
  * After this transformation, no function should have any free variables.
  *
  * @note
  *   this transformation assumes functions do not return other functions, so a
  *   function cannot escape the scope in which it is declared. This is
  *   reasonable because the rest of the VHDL generator makes the same
  *   assumption; after all, how would that work in hardware?
  */
object MakeFreeVarsIntoParams {

  def apply(s: GenStmBuild): GenStmBuild = {
    val s1 = EnsureTupleArgs(s)
    val s2 = UnNestFunctions(s1)
    val functionContexts = Analysis.computeContexts(s2.intermediates)
    val pass = new MakeFreeVarsIntoParams(functionContexts)
    val result = GenStmBuild(
      data = pass.apply(s2.data),
      valid = pass.apply(s2.valid),
      accumulators = s2.accumulators.map({ case (x, (z, next)) =>
        x -> (pass.apply(z), pass.apply(next))
      }),
      producers = s2.producers.map({ case (x, (p, ready)) =>
        x -> (p, pass.apply(ready))
      }),
      intermediates = s2.intermediates.map({
        case i @ (_, _: StmDataIntermediate) => i
        case (_, _: IpBlockInst) =>
          throw new AssertionError(
            "there shouldn't be any IP blocks yet at this compilation stage"
          )
        case (x, DataIntermediate(e)) => x -> DataIntermediate(pass.apply(e))
        case (f, i: FunctionIntermediate) => pass.apply(f, i)
      })
    )
    result.intermediates
      .collect({ case (x, f: FunctionIntermediate) => x -> f })
      .foreach({ case (f, i: FunctionIntermediate) =>
        val freeVars = i.freeVars
          .filterNot(_.typ.isInstanceOf[TyArrow])
        assert(
          freeVars.isEmpty,
          s"function $f should be closed now"
            + s" (found free vars ${freeVars.map(_.name).mkString(",")})"
        )
      })
    result
  }

  def apply(outer: FunctionIntermediate): FunctionIntermediate = {
    val contextArgs = Analysis
      .computeContexts(outer.intermediates)
      // For now, only add the parameters bound by the outer function.
      // The others will be added when we get to the full GenStmBuild scope.
      .mapValues(_.intersect(outer.params))
    val pass = new MakeFreeVarsIntoParams(contextArgs)
    FunctionIntermediate(
      outer.params,
      outer.intermediates.map({
        case (x, DataIntermediate(e)) =>
          x -> DataIntermediate(pass.apply(e))
        case (x, inner: FunctionIntermediate) =>
          pass.apply(x, inner)
      }),
      pass.apply(outer.output)
    )
  }
}

/** Transformation that adds free variables as explicit parameters, both at the
  * function declaration and at all call sites.
  *
  * @param functionContexts
  *   for each function, which variables to add to the parameter list.
  */
private class MakeFreeVarsIntoParams(
    functionContexts: Map[Param, Seq[Param]]
) {

  def apply(e: Expr): Expr = {
    e match {
      case FunCall(f: Param, arg) =>
        this.functionContexts.get(f) match {
          case Some(contextArgs) =>
            FunCall(
              updateLhs(f, contextArgs),
              updateRhs(this.apply(arg), contextArgs)
            )().tchk()
          case None =>
            // We might be in a nested function and calling a function from the
            // outer scope.
            // No need to add any params here, but there might be work to do in
            // the argument.
            FunCall(f, this.apply(arg))().tchk()
        }
      case e => e.map(this.apply).tchk()
    }
  }

  def apply(
      f: Param,
      i: FunctionIntermediate
  ): (Param, FunctionIntermediate) = {
    val FunctionIntermediate(params, intermediates, body) = i
    val contextArgs = functionContexts(f)
    val newF = updateLhs(f, contextArgs)
    // Watch out for cases like
    //     f = (x) => 1 + g(x)
    //     g = (y) => x + y    // <-- g refers to variable x from outer scope!
    // We don't want to transform f into
    //     f = (x, x) => 1 + g(x, x)
    // since duplicate parameter names are not allowed.
    // Instead, we want something like
    //     f = (x2, x) => 1 + g(x2, x)
    val shadowedParams = contextArgs.intersect(params)
    val (renamedParams, renamedBody) =
      if (contextArgs.intersect(params).nonEmpty) {
        val renamings = shadowedParams.map(x => x -> x.freshCopy).toMap
        val newParams = params.map(x => renamings.getOrElse(x, x))
        val newBody = body.subPreserveType(renamings.toMap[Expr, Expr])
        (newParams, newBody)
      } else {
        (params, body)
      }
    newF -> FunctionIntermediate(
      renamedParams ++ contextArgs,
      intermediates.map({
        case (x, DataIntermediate(e)) =>
          x -> DataIntermediate(this.apply(e))
        case (_, _: FunctionIntermediate) =>
          throw new AssertionError(
            "nested functions should have been removed by now"
          )
      }),
      this.apply(renamedBody)
    )
  }

  private def updateLhs(f: Param, contextArgs: Seq[Param]): Param = {
    val TyArrow(TyTuple(inTypes @ _*), outTyp) = f.typ
    val newTyp = TyArrow(TyTuple(inTypes ++ contextArgs.map(_.typ): _*), outTyp)
    f.rebuild(newTyp).asInstanceOf[Param]
  }

  private def updateRhs(explicitArg: Expr, contextArgs: Seq[Param]): Expr = {
    val Tuple(explicitArgs @ _*) = explicitArg
    Tuple(explicitArgs ++ contextArgs: _*)().tchk()
  }
}

/** Analysis to find which free variables will need to be added to each
  * function's parameter list.
  */
private object Analysis {

  def computeContexts(
      intermediates: Map[Param, Intermediate]
  ): Map[Param, Seq[Param]] = {
    val callGraph = computeCallGraph(intermediates)
    callGraph
      // Process callees before caller
      .topologicalOrder()
      // Map each function name to the variables it implicitly accesses
      .foldLeft(Map[Param, Set[Param]]())({ case (ctx, f) =>
        val contextParams = intermediates(f).freeVars
          .flatMap({ g =>
            g.typ match {
              case _: TyArrow =>
                // f calls another function g, so whatever variables g accesses
                // implicitly are also accessed implicitly by f
                ctx(g)
              case _ =>
                // Just a normal variable
                Set(g)
            }
          })
        ctx + (f -> contextParams)
      })
      .mapValues(_.toSeq.sortBy(_.name))
  }

  private def computeCallGraph(
      intermediates: Map[Param, Intermediate]
  ): DiGraph[Param] = {
    DiGraph(
      nodes = intermediates
        .collect({ case (x, _: FunctionIntermediate) => x })
        .toSet,
      edges = intermediates
        .collect({ case (x, i: FunctionIntermediate) =>
          val callees = i.freeVars.filter(_.typ.isInstanceOf[TyArrow])
          callees.map(x -> _)
        })
        .flatten
        .toSet
    )
  }
}

/** Transform all the functions and function calls so the arguments are passed
  * as a tuple.
  *
  * After this transformation, all functions will have type `(A, B, ...) -> C`
  * and all function calls will look like `f((a, b, ...))`.
  */
private object EnsureTupleArgs {

  def apply(s: GenStmBuild): GenStmBuild = {
    GenStmBuild(
      data = this.apply(s.data),
      valid = this.apply(s.valid),
      accumulators = s.accumulators.map({ case (x, (z, next)) =>
        x -> (this.apply(z), this.apply(next))
      }),
      producers = s.producers.map({ case (x, (p, ready)) =>
        x -> (p, this.apply(ready))
      }),
      intermediates = s.intermediates.map({
        case (f, i: FunctionIntermediate) => updateLhs(f) -> this.apply(i)
        case (x, i)                       => x -> this.apply(i)
      })
    )
  }

  private def apply(i: Intermediate): Intermediate = {
    i match {
      case i: StmDataIntermediate => i
      case _: IpBlockInst =>
        throw new AssertionError(
          "there shouldn't be any IP blocks yet at this compilation stage"
        )
      case DataIntermediate(e) => DataIntermediate(this.apply(e))
      case FunctionIntermediate(params, intermediates, output) =>
        assert(
          params.length == 1,
          "each function should only have 1 parameter at this point"
          // otherwise I need to distinguish between those that I need to
          // transform and those that have already been transformed, which
          // would require some care regarding scopes (e.g., what if you have
          // a function called f which already has multiple params, but
          // inside f you have another function called f that has only one
          // param?)
        )
        FunctionIntermediate(
          params,
          intermediates.map({ case (x, i) =>
            val newX = i match {
              case _: FunctionIntermediate => updateLhs(x)
              case _                       => x
            }
            val newI = this
              .apply(i)
              .asInstanceOf[Intermediate with AllowedInFunction]
            newX -> newI
          }),
          this.apply(output)
        )
    }
  }

  private def apply(e: Expr): Expr = {
    e match {
      case FunCall(f: Param, arg) =>
        FunCall(updateLhs(f), updateRhs(this.apply(arg)))().tchk()
      case e =>
        e.map(this.apply).tchk()
    }
  }

  private def updateLhs(f: Param): Param = {
    val TyArrow(inTyp, outTyp) = f.typ
    f.rebuild(TyArrow(TyTuple(inTyp), outTyp)).asInstanceOf[Param]
  }

  private def updateRhs(arg: Expr): Expr = {
    Tuple(arg)().tchk()
  }
}

/** Pull all nested functions out of their containing function, recursively.
  *
  * After this transformation, no function should contain another function.
  */
private object UnNestFunctions {

  def apply(s: GenStmBuild): GenStmBuild = {
    GenStmBuild(
      data = s.data,
      valid = s.valid,
      accumulators = s.accumulators,
      producers = s.producers,
      intermediates = s.intermediates.flatMap({ case (x, i) =>
        this.apply(x, i)
      })
    )
  }

  private def apply(x: Param, i: Intermediate): ListMap[Param, Intermediate] = {
    i match {
      case outer0: FunctionIntermediate =>
        // Recurse
        val outer1 = FunctionIntermediate(
          outer0.params,
          outer0.intermediates.flatMap({ case (x, i) =>
            this
              .apply(x, i)
              .mapValues(_.asInstanceOf[Intermediate with AllowedInFunction])
          }),
          outer0.output
        )
        // Add outer params to the param lists of the nested functions.
        // This must happen now; once we pull the inner function out it will no
        // longer have access to the outer params at all.
        val outer2 = MakeFreeVarsIntoParams(outer1)
        // Remove all the nested functions
        val outer3 = FunctionIntermediate(
          outer2.params,
          outer2.intermediates.filter({
            case (_, _: FunctionIntermediate) => false
            case _                            => true
          }),
          outer2.output
        )
        // Return the nested functions along with the updated outer function
        outer2.intermediates
          .collect({ case (x, i: FunctionIntermediate) =>
            assert(
              i.freeVars.intersect(outer2.params.toSet).isEmpty,
              "the inner function still seems to access variables from the outer function"
                + " that do not explicitly appear in the inner function's parameter list"
            )
            x -> i
          })
          .+(x -> outer3)
      case i => ListMap(x -> i)
    }
  }
}
