package mhir.ir

class BadRebuildError(e: Expr, args: Seq[Expr])
    extends IllegalArgumentException(
      s"Wrong arguments passed to rebuild: node $e, args $args"
    )
