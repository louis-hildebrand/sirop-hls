package mhir.ir

/** Functions for displaying an expression.
  */
object ExprPrinter {

  val Indent: String = "  "

  /** Converts the given expression to a string that may be multiple lines.
    *
    * @param e
    *   the expression to stringify.
    * @param maxWidth
    *   the maximum line width. A best effort will be made to keep the code
    *   within this limit, but it is not guaranteed.
    * @param parentPrecedence
    *   the precedence of the parent of the given expression. This will be used
    *   to determine whether the output should be wrapped in parentheses.
    */
  def display(
      e: Expr,
      maxWidth: Int = 80,
      parentPrecedence: Int = Precedence.Max
  ): String = {
    val oneLine = displayOneLine(e, parentPrecedence)
    if (oneLine.length <= maxWidth) {
      oneLine
    } else {
      displayMultiLine(e, maxWidth, parentPrecedence)
    }
  }

  /** Converts the given expression to a string in which the top-level
    * expression is split across multiple lines (if possible).
    *
    * @param e
    *   the expression to stringify.
    * @param maxWidth
    *   the maximum line width. A best effort will be made to keep the code
    *   within this limit, but it is not guaranteed.
    * @param parentPrecedence
    *   the precedence of the parent of the given expression. This will be used
    *   to determine whether the output should be wrapped in parentheses.
    */
  def displayMultiLine(
      e: Expr,
      maxWidth: Int,
      parentPrecedence: Int = Precedence.Max
  ): String = {
    val myPrecedence = Precedence(e)
    val str = e match {
      case _: Param | _: BoolCst | _: IntCst =>
        displayOneLine(e, parentPrecedence)

      case Tuple(elems @ _*) =>
        displayMultiLineSeq(elems, start = "(", end = ")", maxWidth = maxWidth)
      case VecLiteral(elems @ _*) =>
        displayMultiLineSeq(elems, start = "[", end = "]", maxWidth = maxWidth)
      case StmLiteral(elems @ _*) =>
        displayMultiLineSeq(
          elems,
          start = "{{",
          end = "}}",
          maxWidth = maxWidth
        )

      case FunCall(f, arg) =>
        // We can either split the function or the argument
        // First, try to split the function.
        // (If it's a variable then it won't split, if it's a literal function
        // it will split, etc.)
        val fStr = {
          val str = displayMultiLine(
            f,
            maxWidth = maxWidth,
            parentPrecedence = Precedence.Max
          )
          // Add one because we want
          //   f(x)(y)
          // rather than
          //   (f(x))(y)
          val paren = shouldParenthesize(myPrecedence + 1, Precedence(f))
          if (paren && str.contains("\n")) {
            // e.g.,
            // ((x : T) =>
            //    LONG FUNCTION BODY
            // )(ARG)
            s"($str\n)"
          } else if (paren) {
            s"($str)"
          } else {
            str
          }
        }
        val argStr = if (fStr.contains("\n")) {
          // Function split, so try to fit the argument in the remaining space
          val w = maxWidth - math.max(Indent.length, ")(".length)
          val str =
            display(arg, maxWidth = w, parentPrecedence = Precedence.Max)
          if (str.contains("\n")) {
            s"(\n${indent(str)}\n)"
          } else {
            s"($str)"
          }
        } else {
          // Function could not be split, so split the argument
          val str = display(
            arg,
            maxWidth = maxWidth - Indent.length,
            parentPrecedence = Precedence.Max
          )
          s"(\n${indent(str)}\n)"
        }
        s"$fStr$argStr"
      case VecAccess(vec, idx) =>
        // Similar strategy to FunCall
        // First, try to split the vector
        val vStr = displayMultiLine(
          vec,
          maxWidth = maxWidth,
          // Add one because we want
          //   v[i][j]
          // rather than
          //   (v[i])[j]
          parentPrecedence = myPrecedence + 1
        )
        val idxStr = if (vStr.contains("\n")) {
          // Vector split, so try to fit the argument in the remaining space
          val w = maxWidth - math.max(Indent.length, ")[".length)
          val str =
            display(idx, maxWidth = w, parentPrecedence = Precedence.Max)
          if (str.contains("\n")) {
            s"[\n${indent(str)}\n]"
          } else {
            s"[$str]"
          }
        } else {
          // Vector could not be split, so split the argument
          val str = display(
            idx,
            maxWidth = maxWidth - Indent.length,
            parentPrecedence = Precedence.Max
          )
          s"[\n${indent(str)}\n]"
        }
        s"$vStr$idxStr"
      case TupleAccess(t, IntCst(i)) =>
        val w = maxWidth - s".$i".length
        s"${display(t, maxWidth = w, parentPrecedence = myPrecedence)}.$i"

      case Function(x, body) =>
        val bodyStr = display(
          body,
          maxWidth = maxWidth - Indent.length,
          parentPrecedence = Precedence.Max
        )
        s"""(${x.name} : ${x.typ}) =>
           |${indent(bodyStr)}
           |""".stripMargin.stripTrailing

      case Sum(terms @ _*) =>
        displayMultiLineInfixOp(
          terms,
          op = "+",
          maxWidth = maxWidth,
          precedence = myPrecedence
        )
      case Prod(factors @ _*) =>
        displayMultiLineInfixOp(
          factors,
          op = "*",
          maxWidth = maxWidth,
          precedence = myPrecedence
        )
      case Div(e1, e2) =>
        displayMultiLineInfixOp(
          Seq(e1, e2),
          op = "/",
          maxWidth = maxWidth,
          precedence = myPrecedence
        )
      case Mod(e1, e2) =>
        displayMultiLineInfixOp(
          Seq(e1, e2),
          op = "%",
          maxWidth = maxWidth,
          precedence = myPrecedence
        )
      case Equal(e1, e2) =>
        displayMultiLineInfixOp(
          Seq(e1, e2),
          op = "==",
          maxWidth = maxWidth,
          precedence = myPrecedence
        )
      case Not(eq @ Equal(e1, e2)) =>
        displayMultiLineInfixOp(
          Seq(e1, e2),
          op = "!=",
          maxWidth = maxWidth,
          precedence = Precedence(eq)
        )
      case LessThan(e1, e2) =>
        displayMultiLineInfixOp(
          Seq(e1, e2),
          op = "<",
          maxWidth = maxWidth,
          precedence = myPrecedence
        )
      case Not(lt @ LessThan(e1, e2)) =>
        displayMultiLineInfixOp(
          Seq(e1, e2),
          op = ">=",
          maxWidth = maxWidth,
          precedence = Precedence(lt)
        )
      case And(terms @ _*) =>
        displayMultiLineInfixOp(
          terms,
          op = "&&",
          maxWidth = maxWidth,
          precedence = myPrecedence
        )
      case Or(terms @ _*) =>
        displayMultiLineInfixOp(
          terms,
          op = "||",
          maxWidth = maxWidth,
          precedence = myPrecedence
        )

      case Not(e) =>
        displayFunCallMultiLine("!", Seq(e), maxWidth)
      case PadTo(e, w) =>
        displayFunCallMultiLine(s"pad$w", Seq(e), maxWidth)
      case TruncateTo(e, w) =>
        displayFunCallMultiLine(s"trunc$w", Seq(e), maxWidth)
      case ToSigned(e) =>
        displayFunCallMultiLine("sgn", Seq(e), maxWidth)
      case ToUnsigned(e) =>
        displayFunCallMultiLine("usgn", Seq(e), maxWidth)
      case StmData(s) =>
        displayFunCallMultiLine("data", Seq(s), maxWidth)
      case VecBuild(n, f) =>
        displayFunCallMultiLine("vbuild", Seq(n, f), maxWidth)
      case StmNextK(s, k) =>
        displayFunCallMultiLine("snextk", Seq(s, k), maxWidth)

      case Mux(c, t, f) =>
        val cStr = {
          val w = maxWidth - "if () {".length
          val str =
            display(c, maxWidth = w, parentPrecedence = Precedence.Max)
          if (str.contains("\n")) {
            s"(\n${indent(str)}\n)"
          } else {
            s"($str)"
          }
        }
        val tStr = {
          val w = maxWidth - Indent.length
          val str =
            display(t, maxWidth = w, parentPrecedence = Precedence.Max)
          s"{\n${indent(str)}\n}"
        }
        val fStr = f match {
          case _: Mux =>
            // if (c1) then { e1 } else if (c2) then ...
            // rather than
            // if (c1) then { e1 } else { if (c2) then ... }
            // Furthermore, if the first part of the if-elseif chain is
            // wrapped, then the whole chain should be wrapped.
            // The first line may overflow the max width a bit (because of the
            // leading "} else "), but that's not a huge deal.
            displayMultiLine(
              f,
              maxWidth = maxWidth,
              parentPrecedence = Precedence.Max
            )
          case _ =>
            val w = maxWidth - Indent.length
            val str =
              display(f, maxWidth = w, parentPrecedence = Precedence.Max)
            s"{\n${indent(str)}\n}"
        }
        s"if $cStr then $tStr else $fStr"

      case StmBuild(n, data, valid, equations) =>
        val w1 = maxWidth - Indent.length - ";".length
        val nStr = display(n, maxWidth = w1, parentPrecedence = Precedence.Max)
        val dataStr =
          display(data, maxWidth = w1, parentPrecedence = Precedence.Max)
        val validStr =
          display(valid, maxWidth = w1, parentPrecedence = Precedence.Max)
        val equationsStr = equations.toSeq
          .sortBy({ case (x, _) => x.name })
          .map({ case (x, (z, next)) =>
            val zStr = display(
              z,
              maxWidth = maxWidth - 2 * Indent.length - ",".length,
              parentPrecedence = Precedence.Max
            )
            val nextStr = display(
              next,
              maxWidth = maxWidth - 2 * Indent.length,
              parentPrecedence = Precedence.Max
            )
            s"(${x.name} : ${x.typ}) = (\n${indent(zStr)},\n${indent(nextStr)}\n)"
          })
          .map(str => s"$str;")
          .mkString("\n")
        s"""sbuild(
           |${indent(nStr)};
           |${indent(dataStr)};
           |${indent(validStr)};
           |${indent(equationsStr)}
           |)
           |""".stripMargin.stripTrailing

      case e: SyntaxSugar =>
        e.displayMultiLine(maxWidth)
    }
    if (shouldParenthesize(parentPrecedence, myPrecedence)) {
      s"($str)"
    } else {
      str
    }
  }

  private def displayMultiLineSeq(
      elems: Seq[Expr],
      start: String,
      end: String,
      maxWidth: Int
  ): String = {
    elems
      .map(e =>
        display(
          e,
          maxWidth = maxWidth - Indent.length - ",".length,
          parentPrecedence = Precedence.Max
        )
      )
      .map(e => indent(e))
      .mkString(s"$start\n", ",\n", s"\n$end")
  }

  /** Display an infix operation as a multi-line string, with each operand on
    * separate lines.
    *
    * @param elems
    *   the operands.
    * @param op
    *   the symbol for the operation.
    * @param maxWidth
    *   the maximum line width. See [[display]].
    * @param precedence
    *   the precedence of the operation.
    */
  def displayMultiLineInfixOp(
      elems: Seq[Expr],
      op: String,
      maxWidth: Int,
      precedence: Int
  ): String = {
    require(elems.nonEmpty)
    elems.zipWithIndex
      .map({ case (e, i) =>
        val str = if (i == 0) {
          display(e, maxWidth = maxWidth, parentPrecedence = precedence)
        } else {
          display(
            e,
            maxWidth = maxWidth - Indent.length - op.length - " ".length,
            parentPrecedence = precedence
          )
        }
        val alreadyParenthesized = str.startsWith("(") && str.endsWith(")")
        val withParens = if (str.contains("\n") && !alreadyParenthesized) {
          s"(\n${indent(str)})"
        } else {
          str
        }
        if (i == 0) {
          withParens
        } else {
          indent(s"$op $withParens")
        }
      })
      .mkString("\n")
  }

  def indent(s: String): String = {
    s.split("\n").map(line => s"$Indent$line").mkString("\n")
  }

  /** Converts the given expression to a one-line string.
    *
    * @param e
    *   the expression to stringify.
    * @param parentPrecedence
    *   the precedence of the parent of the given expression. This will be used
    *   to determine whether the output should be wrapped in parentheses.
    */
  def displayOneLine(
      e: Expr,
      parentPrecedence: Int = Precedence.Max
  ): String = {
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
        // No need for parentheses in f(x)(y)
        displayFunCallOneLine(displayOneLine(f, myPrecedence + 1), Seq(arg))
      case c: IntCst =>
        s"${c.i}:${c.typ}"
      case Sum(terms @ _*) =>
        displayOneLineInfixOp(terms, "+", myPrecedence)
      case Prod(factors @ _*) =>
        displayOneLineInfixOp(factors, "*", myPrecedence)
      case Div(e1, e2) =>
        displayOneLineInfixOp(Seq(e1, e2), "/", myPrecedence)
      case Mod(e1, e2) =>
        displayOneLineInfixOp(Seq(e1, e2), "%", myPrecedence)
      case PadTo(e, w) =>
        s"pad$w(${displayOneLine(e, Precedence.Max)})"
      case TruncateTo(e, w) =>
        s"trunc$w(${displayOneLine(e, Precedence.Max)})"
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
        displayOneLineInfixOp(terms, "&&", myPrecedence)
      case Or(terms @ _*) =>
        displayOneLineInfixOp(terms, "||", myPrecedence)
      case Mux(c, t, f) =>
        val cStr = displayOneLine(c, Precedence.Max)
        val tStr = displayOneLine(t, Precedence.Max)
        val fStr = displayOneLine(f, Precedence.Max)
        f match {
          case _: Mux =>
            // if (c1) then { e1 } else if (c2) then ...
            // rather than
            // if (c1) then { e1 } else { if (c2) then ... }
            s"if ($cStr) then { $tStr } else $fStr"
          case _ =>
            s"if ($cStr) then { $tStr } else { $fStr }"
        }
      case StmBuild(n, data, valid, equations) =>
        val nStr = displayOneLine(n, Precedence.Max)
        val dataStr = displayOneLine(data, Precedence.Max)
        val validStr = displayOneLine(valid, Precedence.Max)
        val equationsStr = equations.toSeq
          .sortBy({ case (x, _) => x.name })
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
        // Add one because we want
        //   v[i][j]
        // rather than
        //   (v[i])[j]
        s"${displayOneLine(v, myPrecedence + 1)}[${displayOneLine(i, Precedence.Max)}]"
      case VecLiteral(elems @ _*) =>
        elems
          .map(e => displayOneLine(e, Precedence.Max))
          .mkString("[", ", ", "]")
      case StmLiteral(elems @ _*) =>
        elems
          .map(e => displayOneLine(e, Precedence.Max))
          .mkString("{{", ", ", "}}")
      case StmNextK(s, k) =>
        displayFunCallOneLine("snextk", Seq(s, k))
      case e: SyntaxSugar =>
        e.displayOneLine()
    }
    if (shouldParenthesize(parentPrecedence, myPrecedence)) {
      s"($str)"
    } else {
      str
    }
  }

  /** Display an infix operation as a single-line string.
    *
    * @param elems
    *   the operands.
    * @param op
    *   the symbol for the operation.
    * @param precedence
    *   the precedence of the operation.
    */
  def displayOneLineInfixOp(
      elems: Seq[Expr],
      op: String,
      precedence: Int
  ): String = {
    elems.zipWithIndex
      .map({ case (e, i) =>
        if (i == 0) {
          // If the precedence of the first operand is equal to the precedence
          // of this expression, then there's actually no need for parentheses
          // because the expression will be parsed left-to-right.
          displayOneLine(e, precedence + 1)
        } else {
          displayOneLine(e, precedence)
        }
      })
      .mkString(s" $op ")
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

  /** Converts a function call-like expression to a multi-line string.
    *
    * @param f
    *   the function name.
    * @param args
    *   the function arguments.
    * @param maxWidth
    *   the maximum line width. See [[display]].
    */
  def displayFunCallMultiLine(
      f: String,
      args: Seq[Expr],
      maxWidth: Int
  ): String = {
    val argList =
      displayMultiLineSeq(args, start = "(", end = ")", maxWidth = maxWidth)
    s"$f$argList"
  }

  private def shouldParenthesize(
      parentPrecedence: Int,
      childPrecedence: Int
  ): Boolean = {
    parentPrecedence < Precedence.Max && parentPrecedence <= childPrecedence
  }

  /** Produce Scala code that I can copy and paste (e.g., to take some large
    * expression and use it in a test case).
    */
  def showScala(e: Expr): String = {
    e match {
      case tup @ Tuple(elems @ _*) =>
        val children = elems.map(e => showScala(e))
        s"Tuple(${children.mkString(",")})(${showScala(tup.typ)})"
      case ta @ TupleAccess(t, i) =>
        i match {
          case IntCst(i) if 0 <= i && i <= 5 =>
            s"${showScalaWithParens(t)}.__$i"
          case _ =>
            s"TupleAccess(${showScala(t)},${showScala(i)})(${ta.typ})"
        }
      case x: Param => s"${x.name}"
      case Function(param, body) =>
        s"{ val ${param.name} = Param(${param.prefix})(${param.typ}) ; Function(${param.name}, ${showScala(body)})() }"
      case fc @ FunCall(f, arg) =>
        s"(${showScala(f)})(${showScala(arg)})(${showScala(fc.typ)})"
      case c: IntCst =>
        s"IntCst(${c.i}L)(${showScala(c.typ)})"
      case s @ Sum(terms @ _*) =>
        s"Sum(${terms.map(e => showScala(e)).mkString(",")})(${showScala(s.typ)})"
      case p @ Prod(factors @ _*) =>
        s"Prod(${factors.map(e => showScala(e)).mkString(",")})(${showScala(p.typ)})"
      case d @ Div(x, y) =>
        s"Div(${showScala(x)},${showScala(y)})(${showScala(d.typ)})"
      case m @ Mod(x, y) =>
        s"Mod(${showScala(x)},${showScala(y)})(${showScala(m.typ)})"
      case pt @ PadTo(x, w) =>
        s"PadTo(${showScala(x)},$w)(${showScala(pt.typ)})"
      case tt @ TruncateTo(x, w) =>
        s"TruncateTo(${showScala(x)},$w)(${showScala(tt.typ)})"
      case ts @ ToSigned(x) =>
        s"ToSigned(${showScala(x)})(${showScala(ts.typ)})"
      case tu @ ToUnsigned(x) =>
        s"ToUnsigned(${showScala(x)})(${showScala(tu.typ)})"
      case True  => "True"
      case False => "False"
      case eq @ Equal(x, y) =>
        s"Equal(${showScala(x)},${showScala(y)})(${showScala(eq.typ)})"
      case lt @ LessThan(x, y) =>
        s"LessThan(${showScala(x)},${showScala(y)})(${showScala(lt.typ)})"
      case n @ Not(e) => s"Not(${showScala(e)})(${n.typ})"
      case a @ And(terms @ _*) =>
        s"And(${terms.map(e => showScala(e)).mkString(",")})(${showScala(a.typ)})"
      case or @ Or(terms @ _*) =>
        s"Or(${terms.map(e => showScala(e)).mkString(",")})(${showScala(or.typ)})"
      case m @ Mux(c, t, f) =>
        s"Mux(${showScala(c)},${showScala(t)},${showScala(f)})(${showScala(m.typ)})"
      case s @ StmBuild(n, data, valid, eqns) =>
        val equationsStr =
          s"Map(${eqns.map({ case (x, (z, next)) =>
              s"${showScala(x)}->(${showScala(z)},${showScala(next)})"
            })})"
        s"StmBuild(${showScala(n)},${showScala(data)},${showScala(valid)},$equationsStr)(${showScala(s.typ)})"
      case s @ StmLiteral(elems @ _*) =>
        val children = elems.map(e => showScala(e))
        s"StmLiteral(${children.mkString(",")})(${showScala(s.typ)})"
      case sd @ StmData(s) =>
        s"StmData(${showScala(s)})(${showScala(sd.typ)})"
      case sn @ StmNextK(s, k) =>
        s"StmNextK(${showScala(s)},${showScala(k)})(${showScala(sn.typ)})"
      case vb @ VecBuild(n, f) =>
        s"VecBuild(${showScala(n)},${showScala(f)})(${showScala(vb.typ)})"
      case v @ VecLiteral(elems @ _*) =>
        val children = elems.map(e => showScala(e))
        s"VecLiteral(${children.mkString(",")})(${showScala(v.typ)})"
      case va @ VecAccess(v, i) =>
        s"VecAccess(${showScala(v)},${showScala(i)})(${showScala(va.typ)})"
      case Default(t) =>
        s"Default(${showScala(t)})"
      case e: SyntaxSugar =>
        val name = e.getClass.getSimpleName
        val children = e.children.map(showScala).mkString(", ")
        s"$name($children)(${showScala(e.typ)})"
    }
  }

  private def showScala(t: Type): String = {
    t match {
      case Missing                                   => "Missing"
      case t: TyUInt if COMMON_INT_TYPES.contains(t) => s"U${t.w}"
      case TyUInt(w)                                 => s"TyUInt($w)"
      case t: TySInt if COMMON_INT_TYPES.contains(t) => s"I${t.w}"
      case TySInt(w)                                 => s"TySInt($w)"
      case TyBool                                    => "TyBool"
      case TyArrow(t1, t2) => s"TyArrow(${showScala(t1)},${showScala(t2)})"
      case TyTuple(ts @ _*) =>
        s"TyTuple(${ts.map(t => showScala(t)).mkString(",")})"
      case TyVec(t, n) => s"TyVec(${showScala(t)},${showScala(n)})"
      case TyStm(t, n) => s"TyStm(${showScala(t)},${showScala(n)})"
    }
  }

  private def showScalaWithParens(e: Expr): String = {
    val str = showScala(e)
    if (shouldParenthesize(e)) s"($str)" else str
  }

  private def shouldParenthesize(e: Expr): Boolean = {
    e match {
      case _: IntCst | _: Param | _: TupleAccess | _: ToSigned | _: ToUnsigned |
          _: PadTo | _: TruncateTo =>
        false
      case _ => true
    }
  }
}
