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
      case False          => 1
      case True           => 2
      case _: And         => 3
      case _: Or          => 4
      case _: Not         => 5
      case _: Equal       => 6
      case _: LessThan    => 7
      case _: IntCst      => 8
      case _: Sum         => 9
      case _: Prod        => 10
      case _: Div         => 11
      case _: Mod         => 12
      case _: PadTo       => 13
      case _: TruncateTo  => 14
      case _: ToSigned    => 15
      case _: ToUnsigned  => 16
      case _: Param       => 17
      case _: TupleAccess => 18
      case _: FunCall     => 19
      case _: Mux         => 20
      case _: Tuple       => 21
      case _: Function    => 22
      case _: StmBuild    => 23
      case _: StmData     => 24
      case _: VecBuild    => 25
      case _: VecAccess   => 26
      case _: VecLiteral  => 27
      case _: StmLiteral  => 28
      case _: StmNextK    => 29
      case _: SyntaxSugar => 30
    }
  }
}
