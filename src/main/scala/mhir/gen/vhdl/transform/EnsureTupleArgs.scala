package mhir.gen.vhdl
package transform

import mhir.canonicalize._
import mhir.gen.vhdl.ir._
import mhir.ir._
import mhir.typecheck._

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
      producers = s.producers.map({ case (x, (p, ready)) =>
        x -> (p, this.apply(ready))
      }),
      intermediates = s.intermediates.map({ case (x, i) => this.apply(x, i) })
    )
  }

  private def apply(x: Param, i: Intermediate): (Param, Intermediate) = {
    i match {
      case i: FunctionIntermediate => this.updateLhs(x) -> this.apply(i)
      case i                       => x -> this.apply(i)
    }
  }

  private def apply(i: Intermediate): Intermediate = {
    i match {
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
            val (newX, newI) = this.apply(x, i)
            // TODO: Can I enforce this constraint statically?
            newX -> newI.asInstanceOf[SequentialIntermediate]
          }),
          this.apply(output)
        )
      case BlockIntermediate(locals, body) =>
        // Can't just use block.map(this.apply) here because there may be functions defined inside
        // the block, and we may need to update the type of the corresponding Params.
        BlockIntermediate(
          locals.map({ case (x, i) => this.apply(x, i) }),
          this.apply(body)
        )
      case ForGenerateIntermediate(vars, bounds, body) =>
        ForGenerateIntermediate(vars, bounds, this.apply(body))
      case i =>
        i.map(this.apply)
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
