package mhir.parse.sirop

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.parse.{SourcePoint, SyntaxError}
import mhir.sugar._
import mhir.typecheck.TypeCheck
import os.Path

import scala.annotation.tailrec
import scala.collection.immutable.Queue

object Parser {

  private implicit val logger: Logger = Logger(getClass.getName)

  def parse(f: Path): Program = parse(os.read(f))

  def parse(code: String): Program = {
    val (prog, remainingTokens) = parseProgram(Lexer.lex(code).toList)
    if (remainingTokens.nonEmpty) {
      val loc = remainingTokens.head.loc
      throw SyntaxError("unexpected tokens remaining at end of file", loc)
    }
    prog
  }

  private def parseProgram(tokens: Seq[Token]): (Program, Seq[Token]) = {
    tokens match {
      case Seq(tok, _*) if FirstMainDecl.contains(tok.category) =>
        val (constants, rest1) = parseConstDecls(tokens, Seq())
        val (accel, rest2) = parseAcceleratorDecl(rest1, constants)
        val (test, rest3) = parseTestDecls(
          rest2,
          constants.map({ case ConstDecl(x, _) => x -> x.typ }).toMap,
          Seq()
        )
        val prog = Program(constants, accel, test)
        (prog, rest3)
      case _ =>
        val (e, rest) = parseExpr(tokens, Map())
        (Program(e), rest)
    }
  }

  private val FirstMainDecl: Set[TokenCategory] =
    Set(ConstToken, AcceleratorToken)

  private val FirstTestDecl: Set[TokenCategory] = Set(ConstToken, AssertToken)

  @tailrec
  private def parseTestDecls(
      tokens: Seq[Token],
      constants: Map[Param, Type],
      testDecls: Seq[TestDecl]
  ): (Seq[TestDecl], Seq[Token]) = {
    tokens match {
      case Seq(tok, _*) if FirstTestDecl.contains(tok.category) =>
        val (testDecl, rest1) = parseTestDecl(tokens, constants)
        val newConstants = testDecl match {
          case ConstDecl(x, _) =>
            assert(x.hasType)
            constants + (x -> x.typ)
          case _ =>
            constants
        }
        parseTestDecls(rest1, newConstants, testDecls :+ testDecl)
      case _ =>
        (testDecls, tokens)
    }
  }

  private def parseTestDecl(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (TestDecl, Seq[Token]) = {
    tokens match {
      case Seq(_: ConstToken, _*)  => parseConstDecl(tokens, constants)
      case Seq(_: AssertToken, _*) => parseAssertion(tokens, constants)
      case _ =>
        throw new IllegalArgumentException(
          s"parseTestDecl called with invalid arguments (next token: ${constants.headOption})"
        )
    }
  }

  @tailrec
  private def parseConstDecls(
      tokens: Seq[Token],
      constants: Seq[ConstDecl]
  ): (Seq[ConstDecl], Seq[Token]) = {
    tokens match {
      case Seq(_: ConstToken, _*) =>
        val (decl, rest) = parseConstDecl(
          tokens,
          constants.map({ case ConstDecl(x, _) => x -> x.typ }).toMap
        )
        parseConstDecls(rest, constants :+ decl)
      case _ =>
        (constants, tokens)
    }
  }

  private def parseConstDecl(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (ConstDecl, Seq[Token]) = {
    logger.trace(s"(${loc(tokens)}) parsing const_decl")
    val (_, rest1) = expect(ConstToken, tokens)
    val (nameToken: IdentToken, rest2) = expect(IdentToken, rest1)
    val rest3 = rest2 match {
      case Seq(_: ColonToken, rest @ _*) => rest
      case _ =>
        throw SyntaxError(
          "missing type annotation. All const declarations require a type annotation.",
          nameToken.loc
        )
    }
    val (typ, rest4) = parseTyp(rest3, constants)
    val (_, rest5) = expect(AssignToken, rest4)
    val (e, rest6) = parseExpr(rest5, constants)
    val x = Param(nameToken.ident, -1)(typ)
    (ConstDecl(x, ReshapeData(e, typ)()), rest6)
  }

  private def parseAcceleratorDecl(
      tokens: Seq[Token],
      constants: Seq[ConstDecl]
  ): (AccelDecl, Seq[Token]) = {
    logger.trace(s"(${loc(tokens)}) parsing accel_decl")
    val (_, rest1) = expect(AcceleratorToken, tokens)
    val (annotations, rest2) = rest1 match {
      case Seq(_: LeftSquareToken, rest2 @ _*) =>
        val (annotations, rest3) = parseAcceleratorAnnotations(rest2)
        val (_, rest4) = expect(RightSquareToken, rest3)
        (annotations, rest4)
      case _ => (Map[String, Expr](), rest1)
    }
    val (IdentToken(name), rest3) = expect(IdentToken, rest2)
    val (_, rest4) = expect(AssignToken, rest3)
    val (body, rest5) =
      parseExpr(
        rest4,
        constants.map({ case ConstDecl(x, _) => x -> x.typ }).toMap
      )
    (AccelDecl(name, body, annotations), rest5)
  }

  private def parseAcceleratorAnnotations(
      tokens: Seq[Token]
  ): (Map[String, Expr], Seq[Token]) = {
    tokens match {
      case Seq(_: IdentToken, _*) =>
        val (acc, rest1) = parseAcceleratorAnnotation(tokens)
        var annotations = Map(acc)
        var rest2 = rest1
        while (rest2.headOption.exists(_.category == CommaToken)) {
          val (acc, rest3) = parseAcceleratorAnnotation(rest2.tail)
          annotations = annotations + acc
          rest2 = rest3
        }
        (annotations, rest2)
      case _ => (Map(), tokens)
    }
  }

  private def parseAcceleratorAnnotation(
      tokens: Seq[Token]
  ): ((String, Expr), Seq[Token]) = {
    val (keyTok @ IdentToken(key), rest1) = expect(IdentToken, tokens)
    val (value, rest2) = rest1 match {
      case Seq(_: AssignToken, rest2 @ _*) =>
        val (e, rest3) = parseExpr(rest2, Map())
        (Some(e), rest3)
      case _ => (None, rest1)
    }
    Program.checkAnnotation(
      key,
      value,
      msg => throw SyntaxError(msg, keyTok.loc)
    )
    ((key, value.getOrElse(True)), rest2)
  }

  private def parseAssertion(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Assertion, Seq[Token]) = {
    val (_, rest1) = expect(AssertToken, tokens)
    val (inputs, rest2) = parseInputSpecs(rest1, constants)
    val (_, rest3) = expect(YieldsToken, rest2)
    val (expectedOutput, rest4) = parseExpr(rest3, constants)
    (Assertion(inputs, expectedOutput), rest4)
  }

  private def parseInputSpecs(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Map[Param, Expr], Seq[Token]) = {
    tokens match {
      case Seq(_: LeftCurlyToken, _: RightCurlyToken, rest @ _*) =>
        (Map(), rest)
      case Seq(_: LeftCurlyToken, rest1 @ _*) =>
        val (IdentToken(x), rest2) = expect(IdentToken, rest1)
        val (_, rest3) = expect(AssignToken, rest2)
        var (e, rest4) = parseExpr(rest3, constants)
        var inputs = Map(Param(x, -1)(Missing) -> e)
        while (rest4.headOption.exists(_.category == CommaToken)) {
          val (_, rest5) = expect(CommaToken, rest4)
          val (IdentToken(x), rest6) = expect(IdentToken, rest5)
          val (_, rest7) = expect(AssignToken, rest6)
          val (e, rest8) = parseExpr(rest7, constants)
          inputs += (Param(x, -1)(Missing) -> e)
          rest4 = rest8
        }
        val (_, rest5) = expect(RightCurlyToken, rest4)
        (inputs, rest5)
      case _ =>
        (Map(), tokens)
    }
  }

  def parseStmt(code: String, constants: Map[Param, Type]): Stmt = {
    val (s, remainingTokens) = parseStmt(Lexer.lex(code).toList, constants)
    if (remainingTokens.nonEmpty) {
      val loc = remainingTokens.head.loc
      throw SyntaxError("unexpected tokens remaining at end of file", loc)
    }
    s
  }

  private def parseStmt(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Stmt, Seq[Token]) = {
    tokens match {
      case Seq(_: ExitToken, rest @ _*) =>
        (ExitStmt, rest)
      case Seq(IdentToken(x), _: AssignToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1, constants)
        (SetStmt(Param(x, -1)(Missing), e), rest2)
      case _ =>
        val (e, rest) = parseExpr(tokens, constants)
        (ExprStmt(e), rest)
    }
  }

  private val FirstExpr: Set[TokenCategory] =
    Set(
      // expr0
      LeftParToken,
      LeftCurlyToken,
      IdentToken,
      UndefinedToken,
      DefaultToken,
      TrueToken,
      FalseToken,
      PlusToken,
      MinusToken,
      NatToken,
      LeftSquareToken,
      // expr100
      PadToken,
      TruncateToken,
      SignToken,
      UnsignToken,
      VbuildToken,
      SbuildToken,
      SdataToken,
      // expr200
      BangToken,
      // expr1200
      IfToken,
      LeftParToken,
      LetStmToken,
      AtToken
    )

  private def parseExpr(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    logger.trace(s"(${loc(tokens)}) parsing expr")
    parseExpr1200(tokens, constants)
  }

  private def parseExpr1200(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: IfToken, rest1 @ _*) =>
        val (c, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(ThenToken, rest2)
        val (t, rest4) = parseExpr(rest3, constants)
        val (_, rest5) = expect(ElseToken, rest4)
        val (f, rest6) = parseExpr(rest5, constants)
        (Mux(c, t, f)(), rest6)
      case Seq(_: LeftParToken, IdentToken(param), _: ColonToken, rest1 @ _*) =>
        val (typ, rest2) = parseTyp(rest1, constants)
        val (_, rest3) = expect(RightParToken, rest2)
        val (_, rest4) = expect(DoubleArrowToken, rest3)
        val (body, rest5) = parseExpr(rest4, constants)
        (Function(Param(param, -1)(typ), body)(), rest5)
      case Seq(
            _: LeftParToken,
            IdentToken(param),
            _: RightParToken,
            _: DoubleArrowToken,
            rest1 @ _*
          ) =>
        val (body, rest2) = parseExpr(rest1, constants)
        (Function(Param(param, -1)(Missing), body)(), rest2)
      case Seq(_: AtToken, rest1 @ _*) =>
        val (pat, rest2) = parsePattern(rest1, constants)
        val (_, rest3) = expect(DoubleArrowToken, rest2)
        val (body, rest4) = parseExpr(rest3, constants)
        (PatternFunction(pat, body)(), rest4)
      case Seq(_: LetStmToken, _: LeftSquareToken, rest1 @ _*) =>
        val (bufSize, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(RightSquareToken, rest2)
        val (IdentToken(ident), rest4) = expect(IdentToken, rest3)
        val (typ, rest5) = rest4 match {
          case Seq(_: ColonToken, rest @ _*) => parseTyp(rest, constants)
          case _                             => (Missing, rest4)
        }
        val (_, rest6) = expect(AssignToken, rest5)
        val (in, rest7) = parseExpr(rest6, constants)
        val (_, rest8) = expect(InToken, rest7)
        val (out, rest9) = parseExpr(rest8, constants)
        (LetStm(bufSize, Param(ident, -1)(typ), in, out)(), rest9)
      case Seq(_: LetToken, IdentToken(x), rest1 @ _*) =>
        val (typ, rest2) = rest1 match {
          case Seq(_: ColonToken, rest @ _*) => parseTyp(rest, constants)
          case _                             => (Missing, rest1)
        }
        val (_, rest3) = expect(AssignToken, rest2)
        val (in, rest4) = parseExpr(rest3, constants)
        val (_, rest5) = expect(InToken, rest4)
        val (out, rest6) = parseExpr(rest5, constants)
        (Let(Param(x, -1)(typ), in, out)(), rest6)
      case _ => parseExpr1100(tokens, constants)
    }
  }

  private def parsePattern(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Pattern, Seq[Token]) = {
    tokens match {
      case Seq(IdentToken(name), _: ColonToken, rest1 @ _*) =>
        val (typ, rest2) = parseTyp(rest1, constants)
        (ParamPattern(Param(name, -1)(typ)), rest2)
      case Seq(IdentToken(name), rest @ _*) =>
        (ParamPattern(Param(name, -1)(Missing)), rest)
      case Seq(_: LeftParToken, _: RightParToken, rest @ _*) =>
        (TuplePattern(), rest)
      case Seq(lpar: LeftParToken, rest1 @ _*) =>
        val (p, rest2) = parsePattern(rest1, constants)
        rest2 match {
          case Seq(_: RightParToken, _*) =>
            throw SyntaxError(
              "invalid pattern."
                + s" To match a 1-tuple, add a comma, as in ($p,)."
                + " Otherwise, omit the parentheses.",
              lpar.loc
            )
          case Seq(_: CommaToken, _: RightParToken, rest3 @ _*) =>
            (TuplePattern(p), rest3)
          case _ =>
            var rest3 = rest2
            var elems = Seq(p)
            while (rest3.headOption.exists(_.category == CommaToken)) {
              val (nextElem, rest4) = parsePattern(rest3.tail, constants)
              rest3 = rest4
              elems = elems :+ nextElem
            }
            val (_, rest4) = expect(RightParToken, rest3)
            val pat = TuplePattern(elems: _*)
            val duplicateNames = pat.paramPrefixes
              .groupBy(x => x)
              .collect({ case (x, Seq(_, _, _*)) => x })
              .toSeq
              .sorted
            if (duplicateNames.nonEmpty) {
              val list = duplicateNames.mkString(", ")
              throw SyntaxError(
                s"duplicate parameter(s) in pattern: $list",
                lpar.loc
              )
            }
            (pat, rest4)
        }
      case Seq(tok, _*) =>
        throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
      case Seq() => throw SyntaxError("unexpected end of file", None)
    }
  }

  private def parseExpr1100(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr1000(tokens, constants)
    parseExpr1100Prime(e, rest, constants)
  }

  @tailrec
  private def parseExpr1100Prime(
      e1: Expr,
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: LogOrToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr1000(rest1, constants)
        parseExpr1100Prime(e1 || e2, rest2, constants)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr1000(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr900(tokens, constants)
    parseExpr1000Prime(e, rest, constants)
  }

  @tailrec
  private def parseExpr1000Prime(
      e1: Expr,
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: LogAndToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr900(rest1, constants)
        parseExpr1000Prime(e1 && e2, rest2, constants)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr900(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr800(tokens, constants)
    parseExpr900Prime(e, rest, constants)
  }

  @tailrec
  private def parseExpr900Prime(
      e1: Expr,
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: BitOrToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr800(rest1, constants)
        parseExpr900Prime(BitwiseOr(e1, e2)(), rest2, constants)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr800(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr700(tokens, constants)
    parseExpr800Prime(e, rest, constants)
  }

  @tailrec
  private def parseExpr800Prime(
      e1: Expr,
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: BitAndToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr700(rest1, constants)
        parseExpr800Prime(BitwiseAnd(e1, e2)(), rest2, constants)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr700(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr600(tokens, constants)
    parseExpr700Prime(e, rest, constants)
  }

  @tailrec
  private def parseExpr700Prime(
      e1: Expr,
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: EqToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr600(rest1, constants)
        parseExpr700Prime(SmartEqual(e1, e2)(), rest2, constants)
      case Seq(_: EqTickToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr600(rest1, constants)
        parseExpr700Prime(Equal(e1, e2)(), rest2, constants)
      case Seq(_: NeqToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr600(rest1, constants)
        parseExpr700Prime(SmartNotEqual(e1, e2)(), rest2, constants)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr600(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr500(tokens, constants)
    parseExpr600Prime(e, rest, constants)
  }

  @tailrec
  private def parseExpr600Prime(
      e1: Expr,
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: LtTickToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr500(rest1, constants)
        parseExpr600Prime(LessThan(e1, e2)(), rest2, constants)
      case Seq(_: LtToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr500(rest1, constants)
        parseExpr600Prime(SmartLessThan(e1, e2)(), rest2, constants)
      case Seq(_: GtToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr500(rest1, constants)
        parseExpr600Prime(SmartGreaterThan(e1, e2)(), rest2, constants)
      case Seq(_: LeqToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr500(rest1, constants)
        parseExpr600Prime(SmartLessThanOrEqual(e1, e2)(), rest2, constants)
      case Seq(_: GeqToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr500(rest1, constants)
        parseExpr600Prime(SmartGreaterThanOrEqual(e1, e2)(), rest2, constants)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr500(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr400(tokens, constants)
    parseExpr500Prime(e, rest, constants)
  }

  @tailrec
  private def parseExpr500Prime(
      e1: Expr,
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: LShiftToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr400(rest1, constants)
        parseExpr500Prime(LShift(e1, e2)(), rest2, constants)
      case Seq(_: ARShiftToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr400(rest1, constants)
        parseExpr500Prime(ARShift(e1, e2)(), rest2, constants)
      case Seq(_: LRShiftToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr400(rest1, constants)
        parseExpr500Prime(LRShift(e1, e2)(), rest2, constants)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr400(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr300(tokens, constants)
    parseExpr400Prime(e, rest, constants)
  }

  @tailrec
  private def parseExpr400Prime(
      e1: Expr,
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: PlusToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(SmartSum(e1, e2)(), rest2, constants)
      case Seq(_: PlusTickToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(Sum(e1, e2)(), rest2, constants)
      case Seq(_: PlusPercentToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(SmartWrappingSum(e1, e2)(), rest2, constants)
      case Seq(_: PlusPercentTickToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(WrappingSum(e1, e2)(), rest2, constants)
      case Seq(_: PlusCaretToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(SafeSum(e1, e2)(), rest2, constants)
      case Seq(_: MinusToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(SmartDiff(e1, e2)(), rest2, constants)
      case Seq(_: MinusPercentToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(SmartWrappingDiff(e1, e2)(), rest2, constants)
      case Seq(_: MinusPercentTickToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(WrappingDiff(e1, e2)(), rest2, constants)
      case Seq(_: MinusCaretToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(SafeDiff(e1, e2)(), rest2, constants)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr300(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr200(tokens, constants)
    parseExpr300Prime(e, rest, constants)
  }

  @tailrec
  private def parseExpr300Prime(
      e1: Expr,
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: TimesToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(SmartProd(e1, e2)(), rest2, constants)
      case Seq(_: TimesTickToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(Prod(e1, e2)(), rest2, constants)
      case Seq(_: TimesPercentToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(SmartWrappingProd(e1, e2)(), rest2, constants)
      case Seq(_: TimesPercentTickToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(WrappingProd(e1, e2)(), rest2, constants)
      case Seq(_: TimesCaretToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(SafeProd(e1, e2)(), rest2, constants)
      case Seq(_: SlashToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(SmartDiv(e1, e2)(), rest2, constants)
      case Seq(_: SlashTickToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(Div(e1, e2)(), rest2, constants)
      case Seq(_: PercentToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(SmartMod(e1, e2)(), rest2, constants)
      case Seq(_: PercentTickToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(Mod(e1, e2)(), rest2, constants)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr200(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: BangToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr200(rest1, constants)
        (Not(e)(), rest2)
      case Seq(_: TildeToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr200(rest1, constants)
        (BitwiseNot(e)(), rest2)
      case _ => parseExpr100(tokens, constants)
    }
  }

  private def parseExpr100(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    val (e, rest) = tokens match {
      case Seq(PadToken(w), _: LeftParToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(RightParToken, rest2)
        (PadTo(e, w)(), rest3)
      case Seq(TruncateToken(w), _: LeftParToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(RightParToken, rest2)
        (TruncateTo(e, w)(), rest3)
      case Seq(_: SignToken, _: LeftParToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(RightParToken, rest2)
        (ToSigned(e)(), rest3)
      case Seq(_: UnsignToken, _: LeftParToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(RightParToken, rest2)
        (ToUnsigned(e)(), rest3)
      case Seq(_: SdataToken, _: LeftParToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(RightParToken, rest2)
        (StmData(e)(), rest3)
      case Seq(_: VbuildToken, _: LeftParToken, rest1 @ _*) =>
        val (len, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(RightParToken, rest2)
        val (_, rest4) = expect(LeftCurlyToken, rest3)
        val (f, rest5) = parseExpr(rest4, constants)
        f match {
          case f: Function =>
            val (_, rest6) = expect(RightCurlyToken, rest5)
            (VecBuild(len, f)(), rest6)
          case e =>
            throw SyntaxError(
              s"expected a function literal in body of vbuild but found $e",
              rest4.headOption.map(_.loc).getOrElse(SourcePoint(0, 0))
            )
        }
      case Seq(_: SbuildToken, _*) => parseSbuild(tokens, constants)
      case _                       => parseExpr0(tokens, constants)
    }
    parseExpr100Prime(e, rest, constants)
  }

  private def parseSbuild(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (StmBuild, Seq[Token]) = {
    val (_, rest1) = expect(SbuildToken, tokens)
    val (_, rest2) = expect(LeftParToken, rest1)
    val (n, rest3) = parseExpr(rest2, constants)
    val (_, rest4) = expect(RightParToken, rest3)
    val (_, rest5) = expect(LeftParToken, rest4)
    val (data, rest6) = parseExpr(rest5, constants)
    val (_, rest7) = expect(CommaToken, rest6)
    val (valid, rest8) = parseExpr(rest7, constants)
    val (_, rest9) = expect(RightParToken, rest8)
    val (_, rest10) = expect(LeftCurlyToken, rest9)
    val (accumulators, rest11) = parseAccumulators(rest10, constants)
    val (_, rest12) = expect(RightCurlyToken, rest11)
    val (_, rest13) = expect(LeftCurlyToken, rest12)
    val (producers, rest14) = parseProducers(rest13, constants)
    for (x <- producers.keySet) {
      assert(
        x.typ.isInstanceOf[TyStm],
        "all producers should have a Stm type annotation"
      )
    }
    val (_, rest15) = expect(RightCurlyToken, rest14)
    val sbuild = StmBuild(n, data, valid, accumulators ++ producers)()
    (sbuild, rest15)
  }

  private def parseAccumulators(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Map[Param, (Expr, Expr)], Seq[Token]) = {
    tokens match {
      case Seq(_: LeftParToken, _*) =>
        val (acc, rest1) = parseAccumulator(tokens, constants)
        var accumulators = Map(acc)
        var rest2 = rest1
        while (rest2.headOption.exists(_.category == CommaToken)) {
          val (acc, rest3) = parseAccumulator(rest2.tail, constants)
          accumulators = accumulators + acc
          rest2 = rest3
        }
        (accumulators, rest2)
      case _ => (Map(), tokens)
    }
  }

  private def parseAccumulator(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): ((Param, (Expr, Expr)), Seq[Token]) = {
    val (_, rest1) = expect(LeftParToken, tokens)
    val (IdentToken(x), rest2) = expect(IdentToken, rest1)
    val (_, rest3) = expect(ColonToken, rest2)
    val (typ, rest4) = parseTyp(rest3, constants)
    val rest5 = expectMany(
      rest4,
      RightParToken,
      AssignToken,
      LeftCurlyToken,
      InitToken,
      ColonToken
    )
    val (z, rest6) = parseExpr(rest5, constants)
    val rest7 = expectMany(rest6, CommaToken, NextToken, ColonToken)
    val (next, rest8) = parseExpr(rest7, constants)
    val (_, rest9) = expect(RightCurlyToken, rest8)
    val acc = Param(x, -1)(typ) -> (z, next)
    (acc, rest9)
  }

  private def parseProducers(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Map[Param, (Expr, Expr)], Seq[Token]) = {
    tokens match {
      case Seq(_: LeftParToken, _*) =>
        val (prod, rest1) = parseProducer(tokens, constants)
        var producers = Map(prod)
        var rest2 = rest1
        while (rest2.headOption.exists(_.category == CommaToken)) {
          val (acc, rest3) = parseProducer(rest2.tail, constants)
          producers = producers + acc
          rest2 = rest3
        }
        (producers, rest2)
      case _ => (Map(), tokens)
    }
  }

  private def parseProducer(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): ((Param, (Expr, Expr)), Seq[Token]) = {
    val (_, rest1) = expect(LeftParToken, tokens)
    val (IdentToken(x), rest2) = expect(IdentToken, rest1)
    val (_, rest3) = expect(ColonToken, rest2)
    val (typ, rest4) = parseStmTyp(rest3, constants)
    val rest5 = expectMany(
      rest4,
      RightParToken,
      AssignToken,
      LeftCurlyToken,
      LittleStmToken,
      ColonToken
    )
    val (stm, rest6) = parseExpr(rest5, constants)
    val rest7 = expectMany(rest6, CommaToken, ReadyToken, ColonToken)
    val (ready, rest8) = parseExpr(rest7, constants)
    val (_, rest9) = expect(RightCurlyToken, rest8)
    val prod = Param(x, -1)(typ) -> (stm, ready)
    (prod, rest9)
  }

  @tailrec
  private def parseExpr100Prime(
      e: Expr,
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(lsq: ColonLeftSquareToken, rest1 @ _*) =>
        val (typArg, rest2) = parseTyp(rest1, constants)
        val (_, rest3) = expect(RightSquareToken, rest2)
        val (_, rest4) = expect(LeftParToken, rest3)
        val (args, rest5) = parseExprList(rest4, constants)
        val (_, rest6) = expect(RightParToken, rest5)
        parseExpr100Prime(
          parseFunCall(e, Seq(typArg), args, lsq.loc),
          rest6,
          constants
        )
      case Seq(lpar: LeftParToken, rest1 @ _*) =>
        val (args, rest2) = parseExprList(rest1, constants)
        val (_, rest3) = expect(RightParToken, rest2)
        parseExpr100Prime(
          parseFunCall(e, Seq(), args, lpar.loc),
          rest3,
          constants
        )
      case Seq(_: DotToken, IdentToken(op), rest1 @ _*) =>
        val (lsq, typArgs, rest2) = rest1 match {
          case Seq(lsq: ColonLeftSquareToken, rest2 @ _*) =>
            val (typ, rest3) = parseTyp(rest2, constants)
            val (_, rest4) = expect(RightSquareToken, rest3)
            (Some(lsq), Seq(typ), rest4)
          case rest =>
            (None, Seq(), rest)
        }
        val (lpar, rest3) = expect(LeftParToken, rest2)
        val (args, rest4) = parseExprList(rest3, constants)
        val (_, rest5) = expect(RightParToken, rest4)
        val loc = lsq.map(_.loc).getOrElse(lpar.loc)
        parseExpr100Prime(
          parseFunCall(Param(op, -1)(Missing), typArgs, e +: args, loc),
          rest5,
          constants
        )
      case Seq(_: DotToken, NatToken(i), rest @ _*) =>
        parseExpr100Prime(TupleAccess(e, i.toInt)(), rest, constants)
      case Seq(_: LeftSquareToken, _: ColonToken, rest1 @ _*) =>
        // v[:...
        val start = Tuple()()
        val (len, step, rest2) = parseVecSliceAfterStartColon(rest1, constants)
        parseExpr100Prime(VecSlice(e, start, len, step)(), rest2, constants)
      case Seq(_: LeftSquareToken, rest1 @ _*) =>
        val (i, rest2) = parseExpr(rest1, constants)
        rest2 match {
          case Seq(_: RightSquareToken, rest3 @ _*) =>
            // v[i]
            parseExpr100Prime(VecAccess(e, i)(), rest3, constants)
          case Seq(_: ColonToken, rest3 @ _*) =>
            // v[i:...
            val (len, step, rest4) =
              parseVecSliceAfterStartColon(rest3, constants)
            parseExpr100Prime(VecSlice(e, i, len, step)(), rest4, constants)
          case Seq(tok, _*) =>
            throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
          case Seq() => throw SyntaxError("unexpected end of file", None)
        }
      case _ => (e, tokens)
    }
  }

  /** Parse what comes after something like `v[start:`.
    *
    * @return
    *   `(len, step, rest)`, where `len` is the length of the slice, `step` is
    *   the step size of the slice, and `rest` is the sequence of tokens after
    *   the slice.
    */
  private def parseVecSliceAfterStartColon(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: RightSquareToken, rest1 @ _*) =>
        // v[start:]
        (Tuple()(), Tuple()(), rest1)
      case Seq(_: ColonToken, _*) =>
        // v[start::step]
        val (step, rest2) = parseVecSliceAfterLen(tokens, constants)
        (Tuple()(), step, rest2)
      case rest1 =>
        // v[start:len:...
        val (len, rest2) = parseExpr(rest1, constants)
        val (step, rest3) = parseVecSliceAfterLen(rest2, constants)
        (len, step, rest3)
    }
  }

  /** Parse what comes after something like `v[start:len`.
    *
    * @return
    *   `(step, rest)`, where `step` is the step size of the slice and `rest` is
    *   the sequence of tokens after the slice.
    */
  private def parseVecSliceAfterLen(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: RightSquareToken, rest1 @ _*) =>
        // v[start:len]
        (Tuple()(), rest1)
      case Seq(_: ColonToken, _: RightSquareToken, rest1 @ _*) =>
        // v[start:len:]
        (Tuple()(), rest1)
      case Seq(_: ColonToken, rest1 @ _*) =>
        // v[start:len:...
        val (step, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(RightSquareToken, rest2)
        (step, rest3)
      case Seq(tok, _*) =>
        throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
      case Seq() => throw SyntaxError("unexpected end of file", None)
    }
  }

  private def parseFunCall(
      f: Expr,
      typArgs: Seq[Type],
      args: Seq[Expr],
      loc: SourcePoint
  ): Expr = {
    val error = { (f: Param) =>
      throw SyntaxError(s"wrong number of arguments for $f", loc)
    }
    val combinedArgs = (typArgs, args)
    f match {
      // Arithmetic operators ----------------------------------------------
      case f @ Param("min", -1) =>
        combinedArgs match {
          case (Seq(), Seq(x, y)) => Min(x, y)()
          case _                  => error(f)
        }
      case f @ Param("max", -1) =>
        combinedArgs match {
          case (Seq(), Seq(x, y)) => Max(x, y)()
          case _                  => error(f)
        }
      case f @ Param("bits", -1) =>
        combinedArgs match {
          case (Seq(), Seq(e)) => Bits(e)()
          case _               => error(f)
        }
      case f @ Param("interpret_as", -1) =>
        combinedArgs match {
          case (Seq(targetTyp), Seq(e)) => InterpretAs(e, targetTyp)()
          case _                        => error(f)
        }
      case f @ Param("zeros", -1) =>
        combinedArgs match {
          case (Seq(typ), Seq()) => AllZero(typ)
          case _                 => error(f)
        }
      case f @ Param("ones", -1) =>
        combinedArgs match {
          case (Seq(typ), Seq()) => AllOne(typ)
          case _                 => error(f)
        }
      // Vector operators --------------------------------------------------
      case f @ Param("VecLength", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecLength(v)()
          case _               => error(f)
        }
      case f @ Param("Vec2Stm", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => Vec2Stm(v)()
          case _               => error(f)
        }
      case f @ Param("VecMap", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v, f)) => VecMap(v, f)()
          case _                  => error(f)
        }
      case f @ Param("VecMap2", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v1, v2, f)) => VecMap2(v1, v2, f)()
          case _                       => error(f)
        }
      case f @ Param("VecZip", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v1, v2)) => VecZip(v1, v2)()
          case _                    => error(f)
        }
      case f @ Param("VecReduce", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v, f)) => VecReduceComb(v, f)()
          case _                  => error(f)
        }
      case f @ Param("VecFold", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v, z, f)) => VecFoldComb(v, z, f)()
          case _                     => error(f)
        }
      case f @ Param("VecAll", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecAll(v)()
          case _               => error(f)
        }
      case f @ Param("VecAny", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecAny(v)()
          case _               => error(f)
        }
      case f @ Param("VecSum", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecSum(v)()
          case _               => error(f)
        }
      case f @ Param("VecSplit", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, m)) => VecSplit(s, m)()
          case _                  => error(f)
        }
      case f @ Param("VecJoin", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecJoin(v)()
          case _               => error(f)
        }
      case f @ Param("VecConcat", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v1, v2)) => VecConcat(v1, v2)()
          case _                    => error(f)
        }
      case f @ Param("VecShiftLeft", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v, e)) => VecShiftLeft(v, e)()
          case _                  => error(f)
        }
      case f @ Param("VecCst", -1) =>
        combinedArgs match {
          case (Seq(), Seq(n, c)) => VecCst(n, c)()
          case _                  => error(f)
        }
      case f @ Param("VecRange", -1) =>
        combinedArgs match {
          case (Seq(), Seq(n, z, delta)) => VecRange(n, z, delta)()
          case _                         => error(f)
        }
      case f @ Param("VecReverse", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecReverse(v)
          case _               => error(f)
        }
      case f @ Param("VecTranspose", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecTranspose(v)()
          case _               => error(f)
        }
      // Stream operators --------------------------------------------------
      case f @ Param("Stm2Vec", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s)) => Stm2Vec(s)()
          case _               => error(f)
        }
      case f @ Param("StmMap", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, f)) => StmMap(s, f)()
          case _                  => error(f)
        }
      case f @ Param("StmMap2", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s1, s2, f)) => StmMap2(s1, s2, f)()
          case _                       => error(f)
        }
      case f @ Param("StmZip", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s1, s2)) => StmZip(s1, s2)()
          case _                    => error(f)
        }
      case f @ Param("StmReduce", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, f)) => StmReduce(s, f)()
          case _                  => error(f)
        }
      case f @ Param("StmFold1D", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, z, f)) => StmFold1D(s, z, f)()
          case _                     => error(f)
        }
      case f @ Param("StmAll", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s)) => StmAll(s)()
          case _               => error(f)
        }
      case f @ Param("StmAny", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s)) => StmAny(s)()
          case _               => error(f)
        }
      case f @ Param("StmSum", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s)) => StmSum(s)()
          case _               => error(f)
        }
      case f @ Param("StmSplit", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, m)) => StmSplit(s, m)()
          case _                  => error(f)
        }
      case f @ Param("StmJoin", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s)) => StmJoin(s)()
          case _               => error(f)
        }
      case f @ Param("StmConcat", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s1, s2)) => StmConcat(s1, s2)()
          case _                    => error(f)
        }
      case f @ Param("StmShiftLeft", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, e)) => StmShiftLeft(s, e)()
          case _                  => error(f)
        }
      case f @ Param("StmCst", -1) =>
        combinedArgs match {
          case (Seq(), Seq(n, c)) => StmCst(n, c)()
          case _                  => error(f)
        }
      case f @ Param("StmRange", -1) =>
        combinedArgs match {
          case (Seq(), Seq(n, z, delta)) => StmRange(n, z, delta)()
          case _                         => error(f)
        }
      case f @ Param("StmCount2D", -1) =>
        combinedArgs match {
          case (Seq(), Seq(n, m)) => StmCount2D(n, m)()
          case _                  => error(f)
        }
      case f @ Param("StmSlide", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, w)) => StmSlide(s, w)()
          case _                  => error(f)
        }
      case f @ Param("StmSlideStartingWith", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, z)) => StmSlideStartingWith(s, z)()
          case _                  => error(f)
        }
      case f @ Param("StmSlide2D", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, h, w)) => StmSlide2D(s, h, w)()
          case _                     => error(f)
        }
      case f @ Param("StmAccess", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, i)) => StmAccess(s, i)()
          case _                  => error(f)
        }
      case f @ Param("StmPrefix", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, k)) => StmPrefix(s, k)()
          case _                  => error(f)
        }
      case f @ Param("StmSuffix", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, k)) => StmSuffix(s, k)()
          case _                  => error(f)
        }
      case f @ Param("MulAddCascaded", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s1, s2)) => MulAddCascaded(s1, s2)()
          case _                    => error(f)
        }
      case _ =>
        combinedArgs match {
          case (Seq(), Seq(x)) => FunCall(f, x)()
          case (Seq(), args)   => FunCall(f, Tuple(args: _*)())()
          case _ =>
            throw SyntaxError("callee does not accept type arguments", loc)
        }
    }
  }

  private def parseExpr0(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: LeftParToken, _: RightParToken, rest @ _*) =>
        (Tuple()(), rest)
      case Seq(_: LeftParToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1, constants)
        rest2 match {
          case Seq(_: CommaToken, _: RightParToken, rest3 @ _*) =>
            (Tuple(e)(), rest3)
          case Seq(_: RightParToken, rest3 @ _*) =>
            (e, rest3)
          case _ =>
            var rest3 = rest2
            var elems = Seq(e)
            while (rest3.headOption.exists(_.category == CommaToken)) {
              val (nextElem, rest4) = parseExpr(rest3.tail, constants)
              rest3 = rest4
              elems = elems :+ nextElem
            }
            val (_, rest4) = expect(RightParToken, rest3)
            (Tuple(elems: _*)(), rest4)
        }
      case Seq(_: LeftCurlyToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(RightCurlyToken, rest2)
        (e, rest3)
      case Seq(IdentToken(ident), rest @ _*) =>
        (Param(ident, -1)(Missing), rest)
      case Seq(_: UndefinedToken, _: LeftSquareToken, rest1 @ _*) =>
        val (typ, rest2) = parseTyp(rest1, constants)
        val (_, rest3) = expect(RightSquareToken, rest2)
        (Undefined(typ), rest3)
      case Seq(_: DefaultToken, _: LeftSquareToken, rest1 @ _*) =>
        val (typ, rest2) = parseTyp(rest1, constants)
        val (_, rest3) = expect(RightSquareToken, rest2)
        logger.warn(
          s"the syntax default[$typ] is deprecated."
            + s" Please use zeros:[$typ]() instead."
        )
        (AllZero(typ), rest3)
      case Seq(_: TrueToken, rest @ _*)  => (True, rest)
      case Seq(_: FalseToken, rest @ _*) => (False, rest)
      case Seq(_: NatToken, _*) | Seq(_: PlusToken, _*) |
          Seq(_: MinusToken, _*) =>
        parseIntCst(tokens)
      case Seq(
            lsq: LeftSquareToken,
            _: RightSquareVToken,
            _: ColonToken,
            rest1 @ _*
          ) =>
        val (vecTyp, rest2) = parseVecTyp(rest1, constants)
        if (vecTyp.n != C(0)()) {
          throw SyntaxError(
            s"wrong length in Vec type annotation: ${vecTyp.n} (expected 0)",
            lsq.loc
          )
        }
        (VecLiteral()(vecTyp), rest2)
      case Seq(lsq: LeftSquareToken, _: RightSquareVToken, _*) =>
        throw SyntaxError(
          "missing type annotation for empty Vec literal",
          lsq.loc
        )
      case Seq(
            lsq: LeftSquareToken,
            _: RightSquareSToken,
            _: ColonToken,
            rest1 @ _*
          ) =>
        val (stmTyp, rest2) = parseStmTyp(rest1, constants)
        if (stmTyp.n != C(0)()) {
          throw SyntaxError(
            s"wrong length in Stm type annotation: ${stmTyp.n} (expected 0)",
            lsq.loc
          )
        }
        (StmLiteral()(stmTyp), rest2)
      case Seq(lsq: LeftSquareToken, _: RightSquareSToken, _*) =>
        throw SyntaxError(
          "missing type annotation for empty Stm literal",
          lsq.loc
        )
      case Seq(lsq: LeftSquareToken, rest1 @ _*) =>
        val (elems, rest2) = parseExprList(rest1, constants)
        rest2 match {
          case Seq(_: RightSquareVToken, _: ColonToken, _*) =>
            throw SyntaxError(
              "type annotations are forbidden for non-empty Vec literals",
              lsq.loc
            )
          case Seq(_: RightSquareSToken, _: ColonToken, _*) =>
            throw SyntaxError(
              "type annotations are forbidden for non-empty Stm literals",
              lsq.loc
            )
          case Seq(_: RightSquareVToken, rest3 @ _*) =>
            (VecLiteral(elems: _*)(), rest3)
          case Seq(_: RightSquareSToken, rest3 @ _*) =>
            (StmLiteral(elems: _*)(), rest3)
          case Seq(tok, _*) =>
            throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
          case Seq() => throw SyntaxError("unexpected end of file", None)
        }
      case Seq(tok, _*) =>
        throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
      case Seq() => throw SyntaxError("unexpected end of file", None)
    }
  }

  private def parseIntCst(tokens: Seq[Token]): (IntCst, Seq[Token]) = {
    val (e, rest1) = tokens match {
      case Seq(NatToken(n), rest @ _*)                => (IntCst(n)(), rest)
      case Seq(_: PlusToken, NatToken(n), rest @ _*)  => (IntCst(n)(), rest)
      case Seq(_: MinusToken, NatToken(n), rest @ _*) => (IntCst(-n)(), rest)
      case Seq(tok, _ @_*) =>
        throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
      case Seq() => throw SyntaxError("unexpected end of file", None)
    }
    rest1 match {
      case Seq(colon: ColonToken, IntToken(typ), rest2 @ _*) =>
        if (!typ.contains(e.i)) {
          throw SyntaxError(
            s"constant ${e.i} does not fit in type $typ",
            colon.loc
          )
        }
        (e.rebuild(typ).asInstanceOf[IntCst], rest2)
      case _ =>
        (e, rest1)
    }
  }

  private def parseExprList(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Seq[Expr], Seq[Token]) = {
    tokens.headOption match {
      case None                                           => (Seq(), tokens)
      case Some(tok) if !FirstExpr.contains(tok.category) => (Seq(), tokens)
      case _ =>
        val (e0, rest1) = parseExpr(tokens, constants)
        var elems = Queue(e0)
        var rest2 = rest1
        while (rest2.headOption.exists(_.category == CommaToken)) {
          val (nextElem, rest3) = parseExpr(rest2.tail, constants)
          elems = elems :+ nextElem
          rest2 = rest3
        }
        (elems, rest2)
    }
  }

  private def parseTyp(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Type, Seq[Token]) = {
    logger.trace(s"(${loc(tokens)}) parsing typ")
    val (typ, rest) = tokens match {
      case Seq(_: BoolToken, rest @ _*)  => (TyBool, rest)
      case Seq(IntToken(typ), rest @ _*) => (typ, rest)
      case Seq(_: LeftParToken, _: RightParToken, rest @ _*) =>
        (TyTuple(), rest)
      case Seq(_: LeftParToken, rest1 @ _*) =>
        val (typ, rest2) = parseTyp(rest1, constants)
        rest2 match {
          case Seq(_: CommaToken, _: RightParToken, rest3 @ _*) =>
            (TyTuple(typ), rest3)
          case Seq(_: RightParToken, rest3 @ _*) =>
            (typ, rest3)
          case _ =>
            var rest3 = rest2
            var elemTypes = Seq(typ)
            while (rest3.headOption.exists(_.category == CommaToken)) {
              val (nextTyp, rest4) = parseTyp(rest3.tail, constants)
              rest3 = rest4
              elemTypes = elemTypes :+ nextTyp
            }
            val (_, rest4) = expect(RightParToken, rest3)
            (TyTuple(elemTypes: _*), rest4)
        }
      case Seq(_: VecToken, _*)    => parseVecTyp(tokens, constants)
      case Seq(_: BigStmToken, _*) => parseStmTyp(tokens, constants)
      case Seq(tok, _*) =>
        throw SyntaxError(s"expected a type but found ${tok.quot}", tok.loc)
      case Seq() =>
        throw SyntaxError("expected a type but reached end of file", None)
    }
    parseTypPrime(typ, rest, constants)
  }

  private def parseTypPrime(
      typ: Type,
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Type, Seq[Token]) = {
    tokens match {
      case Seq(_: SingleArrowToken, rest1 @ _*) =>
        val (returnTyp, rest2) = parseTyp(rest1, constants)
        (TyArrow(typ, returnTyp), rest2)
      case _ => (typ, tokens)
    }
  }

  private def parseVecTyp(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (TyVec, Seq[Token]) = {
    tokens match {
      case Seq(_: VecToken, _: LeftSquareToken, rest1 @ _*) =>
        val (typ, rest2) = parseTyp(rest1, constants)
        val (_, rest3) = expect(CommaToken, rest2)
        val (len, rest4) = parseExpr(rest3, constants)
        val (_, rest5) = expect(RightSquareToken, rest4)
        // Constructing a TyVec involves canonicalizing the length, and
        // canonicalization requires type checking.
        // But type checking something like Vec[u8, n] in isolation will fail,
        // since n is free here!
        // Therefore, type check the length beforehand, with the set of
        // constants as the typing context.
        val checkedLen = len.tchk(constants, Map())
        (TyVec(typ, checkedLen), rest5)
      case Seq(tok, _*) =>
        throw SyntaxError(s"expected a Vec type but found ${tok.quot}", tok.loc)
      case Seq() =>
        throw SyntaxError("expected a Vec type but reached end of file", None)
    }
  }

  private def parseStmTyp(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (TyStm, Seq[Token]) = {
    tokens match {
      case Seq(_: BigStmToken, _: LeftSquareToken, rest1 @ _*) =>
        val (typ, rest2) = parseTyp(rest1, constants)
        val (_, rest3) = expect(CommaToken, rest2)
        val (len, rest4) = parseExpr(rest3, constants)
        val (_, rest5) = expect(RightSquareToken, rest4)
        // Constructing a TyStm involves canonicalizing the length, and
        // canonicalization requires type checking.
        // But type checking something like Stm[u8, n] in isolation will fail,
        // since n is free here!
        // Therefore, type check the length beforehand, with the set of
        // constants as the typing context.
        val checkedLen = {
          val freeVars = len.freeVars.diff(constants.keySet)
          if (freeVars.nonEmpty) {
            val freeVarList = freeVars.map(_.name).toSeq.sorted.mkString(", ")
            throw SyntaxError(
              s"type has free variable(s): $freeVarList."
                + " All variables in types should be const.",
              rest3.head.loc
            )
          }
          len.tchk(constants, Map())
        }
        (TyStm(typ, checkedLen), rest5)
      case Seq(tok, _*) =>
        throw SyntaxError(s"expected a Stm type but found ${tok.quot}", tok.loc)
      case Seq() =>
        throw SyntaxError("expected a Stm type but reached end of file", None)
    }
  }

  @tailrec
  private def expectMany(
      actual: Seq[Token],
      expected: TokenCategory*
  ): Seq[Token] = {
    expected match {
      case Seq() => actual
      case Seq(x, xs @ _*) =>
        val (_, newActual) = expect(x, actual)
        expectMany(newActual, xs: _*)
    }
  }

  private def expect(
      tc: TokenCategory,
      tokens: Seq[Token]
  ): (Token, Seq[Token]) = {
    tokens match {
      case Seq(t, rest @ _*) =>
        if (t.category != tc) {
          throw SyntaxError(s"expected ${tc.name} but found ${t.quot}", t.loc)
        }
        (t, rest)
      case Seq() =>
        throw SyntaxError(s"expected ${tc.name} but reached end of file", None)
    }
  }

  private def loc(tokens: Seq[Token]): String = {
    tokens.headOption.map(x => x.loc.toString).getOrElse("")
  }
}
