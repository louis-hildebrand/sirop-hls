package mhir.ir

/** Functions for displaying an expression.
  */
object ExprPrinter {

  /** Converts the given expression to a one-line string.
    *
    * @param e
    *   the expression to stringify.
    * @param parentPrecedence
    *   the precedence of the parent of the given expression. This will be used
    *   to determine whether the output should be wrapped in parentheses.
    */
  def displayOneLine(e: Expr, parentPrecedence: Int = Int.MaxValue): String = {
    val myPrecedence = Precedence(e)
    val str = e match {
      case Tuple(e) =>
        s"(${displayOneLine(e)},)"
      case Tuple(elems @ _*) =>
        elems.map(e => displayOneLine(e)).mkString("(", ", ", ")")
      case TupleAccess(t, i) =>
        s"${displayOneLine(t, myPrecedence)}.${i.i}"
      case x: Param =>
        x.name
      case Function(x, body) =>
        s"(${x.name} : ${x.typ}) => ${displayOneLine(body)}"
      case FunCall(f, arg) =>
        displayFunCallOneLine(displayOneLine(f, myPrecedence), Seq(arg))
      case c: IntCst =>
        s"${c.i}:${c.typ}"
      case Sum(terms @ _*) =>
        terms.map(e => displayOneLine(e, myPrecedence)).mkString(" + ")
      case Prod(factors @ _*) =>
        factors.map(e => displayOneLine(e, myPrecedence)).mkString(" * ")
      case Div(e1, e2) =>
        // If the precedence of e1 is equal to the precedence of this
        // expression, then there's actually no need for parentheses
        // because the expression will be parsed left-to-right.
        val e1Str = displayOneLine(e1, myPrecedence + 1)
        val e2Str = displayOneLine(e2, myPrecedence)
        s"$e1Str / $e2Str"
      case Mod(e1, e2) =>
        // If the precedence of e1 is equal to the precedence of this
        // expression, then there's actually no need for parentheses
        // because the expression will be parsed left-to-right.
        val e1Str = displayOneLine(e1, myPrecedence + 1)
        val e2Str = displayOneLine(e2, myPrecedence)
        s"$e1Str % $e2Str"
      case PadTo(e, w) =>
        s"pad(${displayOneLine(e, Precedence.Max)}, $w)"
      case TruncateTo(e, w) =>
        s"trunc(${displayOneLine(e, Precedence.Max)}, $w)"
      case ToSigned(e) =>
        displayFunCallOneLine("sgn", Seq(e))
      case ToUnsigned(e) =>
        displayFunCallOneLine("usgn", Seq(e))
      case True  => "true"
      case False => "false"
      case Equal(e1, e2) =>
        s"${displayOneLine(e1, myPrecedence)} == ${displayOneLine(e2, myPrecedence)}"
      case Not(eq @ Equal(e1, e2)) =>
        s"${displayOneLine(e1, Precedence(eq))} != ${displayOneLine(e2, Precedence(eq))}"
      case LessThan(e1, e2) =>
        s"${displayOneLine(e1, myPrecedence)} < ${displayOneLine(e2, myPrecedence)}"
      case Not(lt @ LessThan(e1, e2)) =>
        s"${displayOneLine(e1, Precedence(lt))} >= ${displayOneLine(e2, Precedence(lt))}"
      case Not(e) =>
        s"!${displayOneLine(e, myPrecedence)}"
      case And(terms @ _*) =>
        terms.map(e => displayOneLine(e, myPrecedence)).mkString(" && ")
      case Or(terms @ _*) =>
        terms.map(e => displayOneLine(e, myPrecedence)).mkString(" || ")
      case Mux(c, t, f) =>
        val cStr = displayOneLine(c, Precedence.Max)
        val tStr = displayOneLine(t, Precedence.Max)
        val fStr = displayOneLine(f, Precedence.Max)
        s"if ($cStr) then { $tStr } else { $fStr }"
      case StmBuild(n, data, valid, equations) =>
        val nStr = displayOneLine(n, Precedence.Max)
        val dataStr = displayOneLine(data, Precedence.Max)
        val validStr = displayOneLine(valid, Precedence.Max)
        val equationsStr = equations
          .map({ case (x, (z, next)) =>
            s"(${x.name} : ${x.typ}) = (${displayOneLine(z, Precedence.Max)}, ${displayOneLine(next, Precedence.Max)})"
          })
          .mkString("; ")
        s"sbuild($nStr; $dataStr; $validStr; $equationsStr)"
      case StmData(s) =>
        displayFunCallOneLine("data", Seq(s))
      case VecBuild(n, f) =>
        displayFunCallOneLine("vbuild", Seq(n, f))
      case VecAccess(v, i) =>
        s"${displayOneLine(v, myPrecedence)}[${displayOneLine(i, Precedence.Max)}]"
      case VecLiteral(elems @ _*) =>
        elems
          .map(e => displayOneLine(e, Precedence.Max))
          .mkString("[[", ", ", "]]")
      case StmLiteral(elems @ _*) =>
        elems
          .map(e => displayOneLine(e, Precedence.Max))
          .mkString("{{", ", ", "}}")
      case StmNextK(s, k) =>
        displayFunCallOneLine("snextk", Seq(s, k))
      case e: SyntaxSugar =>
        e.displayOneLine()
    }
    if (parentPrecedence <= myPrecedence) {
      s"($str)"
    } else {
      str
    }
  }

  /** Converts a function call-like expression to a single-line string.
    *
    * Many expressions look like function calls: obviously there's [[FunCall]],
    * but there's also [[PadTo]], [[StmData]], etc.
    *
    * @param f
    *   the function name.
    * @param args
    *   the function arguments.
    */
  def displayFunCallOneLine(f: String, args: Seq[Expr]): String = {
    // Arguments are already wrapped in parentheses, so reset precedence
    s"$f(${args.map(e => displayOneLine(e, Precedence.Max)).mkString(", ")})"
  }
}
