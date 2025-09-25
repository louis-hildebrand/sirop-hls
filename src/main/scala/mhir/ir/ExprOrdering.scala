package mhir.ir

/** An [[scala.math.Ordering]] for expressions.
  *
  * In some cases, the order of terms within an expression doesn't matter (e.g.,
  * the terms within a [[Sum]] or the factors in a [[Prod]]). In such cases,
  * putting the terms in some "canonical" order is useful for making tests less
  * brittle.
  */
case object ExprOrdering extends Ordering[Expr] {
  override def compare(x: Expr, y: Expr): Int = {
    val c = classScore(x).compareTo(classScore(y))
    if (c != 0) {
      c
    } else {
      // The two objects must be from the same class since their class score
      // is the same
      (x, y) match {
        case (p1: Param, p2: Param) => p1.name.compareTo(p2.name)
        case (IntCst(a), IntCst(b)) => a.compareTo(b)
        case (False, True)          => false.compareTo(true)
        case (True, False)          => true.compareTo(false)
        case _ =>
          val comp = x.children
            .zip(y.children)
            .map({ case (xc, yc) => ExprOrdering.compare(xc, yc) })
            .foldLeft(0)((acc, x) => if (acc != 0) acc else x)
          if (comp != 0) {
            comp
          } else {
            x.children.length.compareTo(y.children.length)
          }
      }
    }
  }

  private def classScore(e: Expr): Int = {
    e match {
      case _: Undefined    => 0
      case False           => 1000
      case True            => 2000
      case _: And          => 3000
      case _: Or           => 4000
      case _: Not          => 5000
      case _: Equal        => 6000
      case _: LessThan     => 7000
      case _: IntCst       => 8000
      case _: Sum          => 9000
      case _: WrappingSum  => 10000
      case _: WrappingDiff => 11000
      case _: Prod         => 12000
      case _: WrappingProd => 13000
      case _: Div          => 14000
      case _: Mod          => 15000
      case _: PadTo        => 16000
      case _: TruncateTo   => 17000
      case _: ToSigned     => 18000
      case _: ToUnsigned   => 19000
      case _: LLShift      => 20000
      case _: LRShift      => 21000
      case _: FixCst       => 21500
      case _: IntFixProd   => 21750
      case _: Param        => 22000
      case _: TupleAccess  => 23000
      case _: FunCall      => 24000
      case _: Mux          => 25000
      case _: Tuple        => 26000
      case _: Function     => 27000
      case _: StmBuild     => 28000
      case _: StmData      => 29000
      case _: LetStm       => 30000
      case _: VecBuild     => 31000
      case _: VecAccess    => 32000
      case _: VecLiteral   => 33000
      case _: StmLiteral   => 34000
      case _: StmNextK     => 35000
      case _: SyntaxSugar  => 36000
    }
  }
}
