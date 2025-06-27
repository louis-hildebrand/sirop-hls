package mhir.ir

/** Thrown to indicate that invalid arguments were passed to the
  * [[mhir.ir.Expr.rebuild]] method.
  *
  * @param e
  *   the original expression that was being rebuilt.
  * @param args
  *   the new (invalid) children for that expression.
  */
class BadRebuildError(e: Expr, args: Seq[Expr])
    extends IllegalArgumentException(
      s"Wrong arguments passed to rebuild: node $e, args $args"
    )
