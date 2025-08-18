package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

private sealed trait VecBuildDepth {
  def next: VecBuildDepth = {
    this match {
      case DepthZero           => DepthOne
      case DepthOne | DepthTwo => DepthTwo
    }
  }
}
private object DepthZero extends VecBuildDepth
private object DepthOne extends VecBuildDepth
private object DepthTwo extends VecBuildDepth

/** Transformation to simplify variable names in an expression.
  */
object NameSimplifier {

  private val logger = Logger(getClass.getName)

  /** Simplify the variable names in the given expression by removing the
    * numerical suffixes where possible.
    *
    * This is useful when you want to print an expression and save it to a file.
    * The numerical suffixes in the variable names can vary a lot depending on
    * the order of transformations you apply. Dropping those suffixes avoids
    * this problem, although in some cases it is not possible to drop all
    * suffixes due to variable capture.
    *
    * @param e
    *   the expression to simplify.
    * @return
    *   an expression that is equivalent to the original, but possibly with
    *   simpler variable names.
    */
  def simplify(e: Expr): Expr = {
    def simplify(e: Expr)(implicit depth: VecBuildDepth): Expr = {
      val result = e match {
        case Function(x, body) =>
          val newX = Param(x.prefix, -1)(x.typ)
          if (body.freeVars().contains(newX)) {
            // Play it safe and don't rename
            Function(x, simplify(body))()
          } else {
            Function(newX, simplify(body.subPreserveType(x -> newX)))()
          }
        case LetStm(x, in, out) =>
          val newX = Param(x.prefix, -1)(x.typ)
          if (out.freeVars().contains(newX)) {
            // Play it safe and don't rename
            LetStm(x, simplify(in), simplify(out))()
          } else {
            LetStm(
              newX,
              simplify(in),
              simplify(out.subPreserveType(x -> newX))
            )()
          }
        case s: StmBuild =>
          val prefixGroups = s.accVars.groupBy(x => x.prefix)
          val renamed =
            prefixGroups.foldLeft(s)({ case (acc, (prefix, xs)) =>
              if (xs.size == 1) {
                val x = xs.head
                val newX = Param(prefix, -1)(x.typ)
                // Avoid variable capture
                val willCapture = (
                  s.data.freeVars().contains(newX)
                    || s.valid.freeVars().contains(newX)
                    || s.nextByVar.exists({ case (_, next) =>
                      next.freeVars().contains(newX)
                    })
                )
                if (willCapture) {
                  acc
                } else {
                  acc.renameVars(Map(x -> newX))
                }
              } else {
                // Many accumulators have this prefix, so don't rename
                acc
              }
            })
          StmBuild(
            n = simplify(renamed.n),
            data = simplify(renamed.data),
            valid = simplify(renamed.valid),
            equations = renamed.equations
              .map({ case (x, (z, next)) =>
                x -> (simplify(z), simplify(next))
              })
          )()
        case VecBuild(n, f) =>
          // Rename function parameter because the t ::+ (x => ...) Scala
          // shorthand just chooses a prefix of `x`, but `i` or `j` would be
          // more informative
          val g = f match {
            case Function(x, body) =>
              val prefix = depth match {
                case DepthZero => "i"
                case DepthOne  => "j"
                case DepthTwo  => "k"
              }
              val newX = Param(prefix)(x.typ)
              assert(!body.freeVars().contains(newX))
              Function(newX, body.subPreserveType(x -> newX))()
          }
          VecBuild(
            simplify(n)(depth.next),
            simplify(g)(depth.next).asInstanceOf[Function]
          )()
        case e =>
          e.map(simplify)
      }
      if (e.hasType) {
        result.tchk()
      } else {
        result
      }
    }
    logger.trace(s"simplifying names: $e")
    val result = simplify(e)(DepthZero)
    logger.trace(s"done simplifying names: $result")
    // TODO: It would be nice to have this assertion just in case.
    //       Unfortunately, it's waaaay too slow.
//    assert(
//      {
//        logger.trace("checking that new expression is equivalent to original")
//        val equiv = result == e
//        logger.trace("done checking equivalence")
//        equiv
//      },
//      "simplifying names should yield an expression that is equal to the original"
//    )
    result
  }
}
