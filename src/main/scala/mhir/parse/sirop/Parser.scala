package mhir.parse.sirop

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.parse.{SourcePoint, SyntaxError}
import mhir.sugar._
import mhir.typecheck.{NameError, TypeCheck}
import os.Path

import scala.annotation.tailrec
import scala.collection.immutable.Queue

object Parser {

  private implicit val logger: Logger = Logger(getClass.getName)

  def parse(f: Path, constOverrides: Map[String, String] = Map()): Program = {
    parse(Lexer.lex(f), constOverrides)
  }

  def parse(code: String): Program = {
    parse(Lexer.lex(code), Map[String, String]())
  }

  private def parse(
      tokens: Seq[Token],
      constOverrides: Map[String, String]
  ): Program = {
    val (prog, remainingTokens) = parseProgram(tokens, constOverrides)
    if (remainingTokens.nonEmpty) {
      val loc = remainingTokens.head.loc
      throw SyntaxError("unexpected tokens remaining at end of file", loc)
    }
    val unusedConstOverrides = constOverrides.keySet
      .diff(prog.constants.map({ case ConstDecl(x, _) => x.name }).toSet)
      .diff(prog.test.collect({ case ConstDecl(x, _) => x.name }).toSet)
    if (unusedConstOverrides.nonEmpty) {
      val str =
        unusedConstOverrides.toSeq.sorted.map(x => s"'$x'").mkString(", ")
      throw NameError(
        s"the following constant(s) mentioned in -c command-line argument do not exist: $str"
      )
    }
    prog
  }

  private def parseProgram(
      tokens: Seq[Token],
      constOverrides: Map[String, String]
  ): (Program, Seq[Token]) = {
    tokens.headOption match {
      case Some(tok) if FirstMainDecl.contains(tok.category) =>
        val (constants, rest1) = parseConstDecls(tokens, Seq(), constOverrides)
        val (accel, rest2) = parseAcceleratorDecl(rest1, constants)
        val (test, rest3) = parseTestDecls(
          rest2,
          constants.map({ case ConstDecl(x, _) => x -> x.typ }).toMap,
          Seq(),
          constOverrides
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
      testDecls: Seq[TestDecl],
      constOverrides: Map[String, String]
  ): (Seq[TestDecl], Seq[Token]) = {
    tokens.headOption match {
      case Some(tok) if FirstTestDecl.contains(tok.category) =>
        val (testDecl, rest1) = parseTestDecl(tokens, constants, constOverrides)
        val newConstants = testDecl match {
          case ConstDecl(x, _) =>
            assert(x.hasType)
            constants + (x -> x.typ)
          case _ =>
            constants
        }
        parseTestDecls(
          rest1,
          newConstants,
          testDecls :+ testDecl,
          constOverrides
        )
      case _ =>
        (testDecls, tokens)
    }
  }

  private def parseTestDecl(
      tokens: Seq[Token],
      constants: Map[Param, Type],
      constOverrides: Map[String, String]
  ): (TestDecl, Seq[Token]) = {
    tokens.headOption match {
      case Some(_: ConstToken) =>
        parseConstDecl(tokens, constants, constOverrides)
      case Some(_: AssertToken) => parseAssertion(tokens, constants)
      case _ =>
        throw new IllegalArgumentException(
          s"parseTestDecl called with invalid arguments (next token: ${constants.headOption})"
        )
    }
  }

  @tailrec
  private def parseConstDecls(
      tokens: Seq[Token],
      constants: Seq[ConstDecl],
      constOverrides: Map[String, String]
  ): (Seq[ConstDecl], Seq[Token]) = {
    tokens.headOption match {
      case Some(_: ConstToken) =>
        val (decl, rest) = parseConstDecl(
          tokens,
          constants.map({ case ConstDecl(x, _) => x -> x.typ }).toMap,
          constOverrides
        )
        parseConstDecls(rest, constants :+ decl, constOverrides)
      case _ =>
        (constants, tokens)
    }
  }

  private def parseConstDecl(
      tokens: Seq[Token],
      constants: Map[Param, Type],
      constOverrides: Map[String, String]
  ): (ConstDecl, Seq[Token]) = {
    logger.trace(s"(${loc(tokens)}) parsing const_decl")
    val (_, rest1) = expect(ConstToken, tokens)
    val (nameTok @ IdentToken(name), rest2) = expect(IdentToken, rest1)
    val rest3 = rest2.headOption match {
      case Some(_: ColonToken) => rest2.tail
      case _ =>
        throw SyntaxError(
          "missing type annotation. All const declarations require a type annotation.",
          nameTok.loc
        )
    }
    val (typ, rest4) = parseTyp(rest3, constants)
    val (_, rest5) = expect(AssignToken, rest4)
    // Need to consume the right-hand side even if an override is provided
    val (sourceVal, rest6) = parseExpr(rest5, constants)
    val rhs = constOverrides.get(name) match {
      case None => sourceVal
      case Some(overrideSrc) =>
        logger.info(
          s"replacing value of const $name with override: $overrideSrc"
        )
        val tokens = Lexer.lex(overrideSrc)
        val (overrideVal, remainingTokens) = parseExpr(tokens, constants)
        if (remainingTokens.nonEmpty) {
          val loc = remainingTokens.head.loc
          throw SyntaxError(
            s"in override for const $name: unexpected tokens remaining at end of expression",
            loc
          )
        }
        overrideVal
    }
    val x = Param(name, -1)(typ)
    (ConstDecl(x, ReshapeData(rhs, typ)()), rest6)
  }

  private def parseAcceleratorDecl(
      tokens: Seq[Token],
      constants: Seq[ConstDecl]
  ): (AccelDecl, Seq[Token]) = {
    logger.trace(s"(${loc(tokens)}) parsing accel_decl")
    val (_, rest1) = expect(AcceleratorToken, tokens)
    val (annotations, rest2) = rest1.headOption match {
      case Some(_: LeftSquareToken) =>
        val rest2 = rest1.tail
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
    tokens.headOption match {
      case Some(_: IdentToken) =>
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
    val (value, rest2) = rest1.headOption match {
      case Some(_: AssignToken) =>
        val rest2 = rest1.tail
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
    val (ignore, rest5) = rest4.headOption match {
      case Some(_: IgnoringToken) =>
        val rest4_1 = rest4.tail
        val (e, rest4_2) = parseExpr(rest4_1, constants)
        (Some(e), rest4_2)
      case _ =>
        (None, rest4)
    }
    (Assertion(inputs, expectedOutput, ignore), rest5)
  }

  private def parseInputSpecs(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Map[Param, Expr], Seq[Token]) = {
    tokens.headOption match {
      case Some(_: LeftCurlyToken) =>
        val rest1 = tokens.tail
        rest1.headOption match {
          case Some(_: RightCurlyToken) =>
            (Map(), rest1.tail)
          case _ =>
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
        }
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
    tokens.headOption match {
      case Some(_: ExitToken) =>
        (ExitStmt, tokens.tail)
      case Some(IdentToken(x)) =>
        val rest1 = tokens.tail
        rest1.headOption match {
          case Some(_: AssignToken) =>
            val rest2 = rest1.tail
            val (e, rest3) = parseExpr(rest2, constants)
            (SetStmt(Param(x, -1)(Missing), e), rest3)
          case _ =>
            val (e, rest) = parseExpr(tokens, constants)
            (ExprStmt(e), rest)
        }
      case _ =>
        val (e, rest) = parseExpr(tokens, constants)
        (ExprStmt(e), rest)
    }
  }

  private val FirstExpr0: Set[TokenCategory] = Set(
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
    LeftSquareToken
  )
  private val FirstExpr100: Set[TokenCategory] =
    Set(VbuildToken, SbuildToken, SdataToken) ++ FirstExpr0
  private val FirstExpr200: Set[TokenCategory] =
    Set(BangToken, TildeToken) ++ FirstExpr100
  private val FirstExpr: Set[TokenCategory] =
    Set(
      // expr1200
      IfToken,
      IffToken,
      AtToken,
      LetStmToken,
      LetToken
    ) ++ FirstExpr200

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
    tokens.headOption match {
      case Some(_: IfToken) =>
        val rest1 = tokens.tail
        val (c, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(ThenToken, rest2)
        val (t, rest4) = parseExpr(rest3, constants)
        val (_, rest5) = expect(ElseToken, rest4)
        val (f, rest6) = parseExpr(rest5, constants)
        (SmartIf(c, t, f)(), rest6)
      case Some(_: IffToken) =>
        val rest1 = tokens.tail
        val (c, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(ThenToken, rest2)
        val (t, rest4) = parseExpr(rest3, constants)
        val (_, rest5) = expect(ElseToken, rest4)
        val (f, rest6) = parseExpr(rest5, constants)
        (Mux(c, t, f)(), rest6)
      case Some(_: LetToken) =>
        val rest1 = tokens.tail
        val (IdentToken(x), rest2) = expect(IdentToken, rest1)
        val (typ, rest3) = rest2.headOption match {
          case Some(_: ColonToken) => parseTyp(rest2.tail, constants)
          case _                   => (Missing, rest2)
        }
        val (_, rest4) = expect(AssignToken, rest3)
        val (in, rest5) = parseExpr(rest4, constants)
        val (_, rest6) = expect(InToken, rest5)
        val (out, rest7) = parseExpr(rest6, constants)
        (Let(Param(x, -1)(typ), in, out)(), rest7)
      case Some(_: LetStmToken) =>
        val rest1 = tokens.tail
        val (_, rest2) = expect(LeftSquareToken, rest1)
        val (bufSize, rest3) = parseExpr(rest2, constants)
        val (_, rest4) = expect(RightSquareToken, rest3)
        val (IdentToken(ident), rest5) = expect(IdentToken, rest4)
        val (typ, rest6) = rest5.headOption match {
          case Some(_: ColonToken) => parseTyp(rest5.tail, constants)
          case _                   => (Missing, rest5)
        }
        val (_, rest7) = expect(AssignToken, rest6)
        val (in, rest8) = parseExpr(rest7, constants)
        val (_, rest9) = expect(InToken, rest8)
        val (out, rest10) = parseExpr(rest9, constants)
        (LetStm(bufSize, Param(ident, -1)(typ), in, out)(), rest10)
      case Some(_: AtToken) =>
        val rest1 = tokens.tail
        val (pat, rest2) = parsePattern(rest1, constants)
        val (_, rest3) = expect(DoubleArrowToken, rest2)
        val (body, rest4) = parseExpr(rest3, constants)
        (PatternFunction(pat, body)(), rest4)
      case Some(IdentToken(param)) =>
        val rest1 = tokens.tail
        rest1.headOption match {
          case Some(_: DoubleArrowToken) =>
            val rest2 = rest1.tail
            val (body, rest3) = parseExpr(rest2, constants)
            (Function(Param(param, -1)(Missing), body)(), rest3)
          case _ => parseExpr1100(tokens, constants)
        }
      case Some(_: LeftParToken) /* ( */ =>
        val rest1 = tokens.tail
        rest1.headOption match {
          case Some(IdentToken(param)) /* (x */ =>
            val rest2 = rest1.tail
            rest2.headOption match {
              case Some(_: ColonToken) /* (x: */ =>
                val rest3 = rest2.tail
                val (typ, rest4) = parseTyp(rest3, constants)
                val (_, rest5) = expect(RightParToken, rest4)
                val (_, rest6) = expect(DoubleArrowToken, rest5)
                val (body, rest7) = parseExpr(rest6, constants)
                (Function(Param(param, -1)(typ), body)(), rest7)
              case Some(_: RightParToken) /* (x) */ =>
                val rest3 = rest2.tail
                rest3.headOption match {
                  case Some(_: DoubleArrowToken) /* (x) => */ =>
                    val rest4 = rest3.tail
                    val (body, rest5) = parseExpr(rest4, constants)
                    (Function(Param(param, -1)(Missing), body)(), rest5)
                  case _ => parseExpr1100(tokens, constants)
                }
              case _ => parseExpr1100(tokens, constants)
            }
          case _ => parseExpr1100(tokens, constants)
        }
      case _ => parseExpr1100(tokens, constants)
    }
  }

  private def parsePattern(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Pattern, Seq[Token]) = {
    tokens.headOption match {
      case Some(IdentToken(name)) =>
        val rest1 = tokens.tail
        rest1.headOption match {
          case Some(_: ColonToken) =>
            val rest2 = rest1.tail
            val (typ, rest3) = parseTyp(rest2, constants)
            (ParamPattern(Param(name, -1)(typ)), rest3)
          case _ =>
            (ParamPattern(Param(name, -1)(Missing)), rest1)
        }
      case Some(_: LeftParToken) =>
        parseTuplePattern(tokens, constants)
      case Some(tok) =>
        throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
      case None => throw SyntaxError("unexpected end of file", None)
    }
  }

  private def parseTuplePattern(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Pattern, Seq[Token]) = {
    def handleRest(
        lpar: LeftParToken,
        firstPattern: Pattern,
        tokens: Seq[Token]
    ): (Pattern, Seq[Token]) = {
      var rest1 = tokens
      var elems = Seq(firstPattern)
      while (rest1.headOption.exists(_.category == CommaToken)) {
        val (nextElem, rest4) = parsePattern(rest1.tail, constants)
        rest1 = rest4
        elems = elems :+ nextElem
      }
      val (_, rest2) = expect(RightParToken, rest1)
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
      (pat, rest2)
    }

    val (lpar: LeftParToken, rest1) = expect(LeftParToken, tokens)
    rest1.headOption match {
      case Some(_: RightParToken) =>
        val rest2 = rest1.tail
        (TuplePattern(), rest2)
      case _ =>
        val (firstPat, rest2) = parsePattern(rest1, constants)
        rest2.headOption match {
          case Some(_: RightParToken) =>
            throw SyntaxError(
              "invalid pattern."
                + s" To match a 1-tuple, add a comma, as in ($firstPat,)."
                + " Otherwise, omit the parentheses.",
              lpar.loc
            )
          case Some(_: CommaToken) =>
            val rest3 = rest2.tail
            rest3.headOption match {
              case Some(_: RightParToken) =>
                (TuplePattern(firstPat), rest3.tail)
              case _ => handleRest(lpar, firstPat, rest2)
            }
          case _ => handleRest(lpar, firstPat, rest2)
        }
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
    tokens.headOption match {
      case Some(_: LogOrToken) =>
        val rest1 = tokens.tail
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
    tokens.headOption match {
      case Some(_: LogAndToken) =>
        val rest1 = tokens.tail
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
    tokens.headOption match {
      case Some(_: BitOrToken) =>
        val rest1 = tokens.tail
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
    tokens.headOption match {
      case Some(_: BitAndToken) =>
        val rest1 = tokens.tail
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
    tokens.headOption match {
      case Some(_: EqToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr600(rest1, constants)
        parseExpr700Prime(SmartEqual(e1, e2)(), rest2, constants)
      case Some(_: EqTickToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr600(rest1, constants)
        parseExpr700Prime(Equal(e1, e2)(), rest2, constants)
      case Some(_: NeqToken) =>
        val rest1 = tokens.tail
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
    tokens.headOption match {
      case Some(_: LtTickToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr500(rest1, constants)
        parseExpr600Prime(LessThan(e1, e2)(), rest2, constants)
      case Some(_: LtToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr500(rest1, constants)
        parseExpr600Prime(SmartLessThan(e1, e2)(), rest2, constants)
      case Some(_: GtToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr500(rest1, constants)
        parseExpr600Prime(SmartGreaterThan(e1, e2)(), rest2, constants)
      case Some(_: LeqToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr500(rest1, constants)
        parseExpr600Prime(SmartLessThanOrEqual(e1, e2)(), rest2, constants)
      case Some(_: GeqToken) =>
        val rest1 = tokens.tail
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
    tokens.headOption match {
      case Some(_: LShiftToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr400(rest1, constants)
        parseExpr500Prime(LShift(e1, e2)(), rest2, constants)
      case Some(_: ARShiftToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr400(rest1, constants)
        parseExpr500Prime(ARShift(e1, e2)(), rest2, constants)
      case Some(_: LRShiftToken) =>
        val rest1 = tokens.tail
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
    tokens.headOption match {
      case Some(_: PlusToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(SmartSum(e1, e2)(), rest2, constants)
      case Some(_: PlusTickToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(Sum(e1, e2)(), rest2, constants)
      case Some(_: PlusPercentToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(SmartWrappingSum(e1, e2)(), rest2, constants)
      case Some(_: PlusPercentTickToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(WrappingSum(e1, e2)(), rest2, constants)
      case Some(_: PlusCaretToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(SafeSum(e1, e2)(), rest2, constants)
      case Some(_: MinusToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(SmartDiff(e1, e2)(), rest2, constants)
      case Some(_: MinusPercentToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(SmartWrappingDiff(e1, e2)(), rest2, constants)
      case Some(_: MinusPercentTickToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr300(rest1, constants)
        parseExpr400Prime(WrappingDiff(e1, e2)(), rest2, constants)
      case Some(_: MinusCaretToken) =>
        val rest1 = tokens.tail
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
    tokens.headOption match {
      case Some(_: TimesToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(SmartProd(e1, e2)(), rest2, constants)
      case Some(_: TimesTickToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(Prod(e1, e2)(), rest2, constants)
      case Some(_: TimesPercentToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(SmartWrappingProd(e1, e2)(), rest2, constants)
      case Some(_: TimesPercentTickToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(WrappingProd(e1, e2)(), rest2, constants)
      case Some(_: TimesCaretToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(SafeProd(e1, e2)(), rest2, constants)
      case Some(_: SlashToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(SmartDiv(e1, e2)(), rest2, constants)
      case Some(_: SlashTickToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(Div(e1, e2)(), rest2, constants)
      case Some(_: PercentToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(SmartMod(e1, e2)(), rest2, constants)
      case Some(_: PercentTickToken) =>
        val rest1 = tokens.tail
        val (e2, rest2) = parseExpr200(rest1, constants)
        parseExpr300Prime(Mod(e1, e2)(), rest2, constants)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr200(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens.headOption match {
      case Some(_: BangToken) =>
        val rest1 = tokens.tail
        if (
          rest1.headOption.exists(tok => FirstExpr200.contains(tok.category))
        ) {
          val (e, rest2) = parseExpr200(rest1, constants)
          (Not(e)(), rest2)
        } else {
          val x = Param("x", -1)(Missing)
          (Function(x, Not(x)())(), rest1)
        }
      case Some(_: TildeToken) =>
        val rest1 = tokens.tail
        if (
          rest1.headOption.exists(tok => FirstExpr200.contains(tok.category))
        ) {
          val (e, rest2) = parseExpr200(rest1, constants)
          (BitwiseNot(e)(), rest2)
        } else {
          val x = Param("x", -1)(Missing)
          (Function(x, BitwiseNot(x)())(), rest1)
        }
      case _ => parseExpr100(tokens, constants)
    }
  }

  private def parseExpr100(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    val (e, rest) = tokens.headOption match {
      case Some(_: SdataToken) =>
        val rest1 = tokens.tail
        val (_, rest2) = expect(LeftParToken, rest1)
        val (e, rest3) = parseExpr(rest2, constants)
        val (_, rest4) = expect(RightParToken, rest3)
        (StmData(e)(), rest4)
      case Some(_: VbuildToken) =>
        val rest1 = tokens.tail
        val (_, rest2) = expect(LeftParToken, rest1)
        val (len, rest3) = parseExpr(rest2, constants)
        val (_, rest4) = expect(RightParToken, rest3)
        val (_, rest5) = expect(LeftCurlyToken, rest4)
        val (f, rest6) = parseExpr(rest5, constants)
        f match {
          case f: Function =>
            val (_, rest7) = expect(RightCurlyToken, rest6)
            (VecBuild(len, f)(), rest7)
          case e =>
            throw SyntaxError(
              s"expected a function literal in body of vbuild but found $e",
              rest5.headOption.map(_.loc).getOrElse(SourcePoint(0, 0))
            )
        }
      case Some(_: SbuildToken) => parseSbuild(tokens, constants)
      case _                    => parseExpr0(tokens, constants)
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
    tokens.headOption match {
      case Some(_: LeftParToken) =>
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
    tokens.headOption match {
      case Some(_: LeftParToken) =>
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
    tokens.headOption match {
      case Some(lsq: ColonLeftSquareToken) =>
        val rest1 = tokens.tail
        val (typArg, rest2) = parseTyp(rest1, constants)
        val (_, rest3) = expect(RightSquareToken, rest2)
        val (_, rest4) = expect(LeftParToken, rest3)
        val (args, rest5) = parseExprList(rest4, constants)
        val (_, rest6) = expect(RightParToken, rest5)
        parseExpr100Prime(
          BuiltinFunctions.parseFunCall(e, Seq(typArg), args, lsq.loc),
          rest6,
          constants
        )
      case Some(lpar: LeftParToken) =>
        val rest1 = tokens.tail
        val (args, rest2) = parseExprList(rest1, constants)
        val (_, rest3) = expect(RightParToken, rest2)
        parseExpr100Prime(
          BuiltinFunctions.parseFunCall(e, Seq(), args, lpar.loc),
          rest3,
          constants
        )
      case Some(_: DotToken) =>
        val rest1 = tokens.tail
        rest1.headOption match {
          case Some(NatToken(i)) =>
            val rest2 = rest1.tail
            parseExpr100Prime(TupleAccess(e, i.toInt)(), rest2, constants)
          case Some(IdentToken(op)) =>
            val rest2 = rest1.tail
            val (lsq, typArgs, rest3) = rest2.headOption match {
              case Some(lsq: ColonLeftSquareToken) =>
                val rest2_1 = rest2.tail
                val (typ, rest2_2) = parseTyp(rest2_1, constants)
                val (_, rest2_3) = expect(RightSquareToken, rest2_2)
                (Some(lsq), Seq(typ), rest2_3)
              case _ =>
                (None, Seq(), rest2)
            }
            val (lpar, rest4) = expect(LeftParToken, rest3)
            val (args, rest5) = parseExprList(rest4, constants)
            val (_, rest6) = expect(RightParToken, rest5)
            val loc = lsq.map(_.loc).getOrElse(lpar.loc)
            parseExpr100Prime(
              BuiltinFunctions
                .parseFunCall(Param(op, -1)(Missing), typArgs, e +: args, loc),
              rest6,
              constants
            )
          case Some(tok) =>
            throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
          case None => throw SyntaxError("unexpected end of file", None)
        }
      case Some(_: LeftSquareToken) =>
        val rest1 = tokens.tail
        rest1.headOption match {
          case Some(_: ColonToken) =>
            // v[:...
            val rest2 = rest1.tail
            val start = Tuple()()
            val (len, step, rest3) =
              parseVecSliceAfterStartColon(rest2, constants)
            parseExpr100Prime(VecSlice(e, start, len, step)(), rest3, constants)
          case _ =>
            val (i, rest2) = parseExpr(rest1, constants)
            rest2.headOption match {
              case Some(_: RightSquareToken) =>
                // v[i]
                val rest3 = rest2.tail
                parseExpr100Prime(VecAccess(e, i)(), rest3, constants)
              case Some(_: ColonToken) =>
                // v[i:...
                val rest3 = rest2.tail
                val (len, step, rest4) =
                  parseVecSliceAfterStartColon(rest3, constants)
                parseExpr100Prime(VecSlice(e, i, len, step)(), rest4, constants)
              case Some(tok) =>
                throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
              case None => throw SyntaxError("unexpected end of file", None)
            }
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
    tokens.headOption match {
      case Some(_: RightSquareToken) =>
        // v[start:]
        (Tuple()(), Tuple()(), tokens.tail)
      case Some(_: ColonToken) =>
        // v[start::step]
        val (step, rest2) = parseVecSliceAfterLen(tokens, constants)
        (Tuple()(), step, rest2)
      case _ =>
        // v[start:len:...
        val (len, rest1) = parseExpr(tokens, constants)
        val (step, rest2) = parseVecSliceAfterLen(rest1, constants)
        (len, step, rest2)
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
    tokens.headOption match {
      case Some(_: RightSquareToken) =>
        // v[start:len]
        (Tuple()(), tokens.tail)
      case Some(_: ColonToken) =>
        // v[start:len:
        val rest1 = tokens.tail
        rest1.headOption match {
          case Some(_: RightSquareToken) =>
            // v[start:len:]
            (Tuple()(), rest1.tail)
          case _ =>
            // v[start:len:...
            val (step, rest2) = parseExpr(rest1, constants)
            val (_, rest3) = expect(RightSquareToken, rest2)
            (step, rest3)
        }
      case Some(tok) =>
        throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
      case None => throw SyntaxError("unexpected end of file", None)
    }
  }

  private def parseExpr0(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    tokens.headOption match {
      case Some(_: LeftParToken) =>
        val rest1 = tokens.tail
        rest1.headOption match {
          case Some(_: RightParToken) =>
            val rest2 = rest1.tail
            (Tuple()(), rest2)
          case _ =>
            val (e, rest2) = parseExpr(rest1, constants)
            // (e,) is a 1-tuple
            val oneTupleResult = rest2.headOption match {
              case Some(_: CommaToken) =>
                val rest3 = rest2.tail
                rest3.headOption match {
                  case Some(_: RightParToken) =>
                    val rest4 = rest3.tail
                    Some((Tuple(e)(), rest4))
                  case _ => None
                }
              case _ => None
            }
            oneTupleResult
              .getOrElse(rest2.headOption match {
                case Some(_: RightParToken) =>
                  (e, rest2.tail)
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
              })
        }
      case Some(_: LeftCurlyToken) =>
        val rest1 = tokens.tail
        val (e, rest2) = parseExpr(rest1, constants)
        val (_, rest3) = expect(RightCurlyToken, rest2)
        (e, rest3)
      case Some(IdentToken(ident)) =>
        val rest = tokens.tail
        (Param(ident, -1)(Missing), rest)
      case Some(_: UndefinedToken) =>
        val rest1 = tokens.tail
        val (_, rest2) = expect(LeftSquareToken, rest1)
        val (typ, rest3) = parseTyp(rest2, constants)
        val (_, rest4) = expect(RightSquareToken, rest3)
        (Undefined(typ), rest4)
      case Some(_: DefaultToken) =>
        val rest1 = tokens.tail
        val (_, rest2) = expect(LeftSquareToken, rest1)
        val (typ, rest3) = parseTyp(rest2, constants)
        val (_, rest4) = expect(RightSquareToken, rest3)
        val typStr = reconstructSource(rest1.take(rest1.length - rest3.length))
        logger.warn(
          s"the syntax default[$typStr] is deprecated."
            + s" Please use zeros:[$typStr]() instead."
        )
        (AllZero(typ), rest4)
      case Some(_: TrueToken) =>
        val rest = tokens.tail
        (True, rest)
      case Some(_: FalseToken) =>
        val rest = tokens.tail
        (False, rest)
      case Some(_: NatToken) =>
        parseIntCst(tokens)
      case Some(_: PlusToken | _: MinusToken) =>
        val rest1 = tokens.tail
        rest1.headOption match {
          case Some(_: NatToken) => parseIntCst(tokens)
          case _                 => parseBinopSymbolExpr(tokens)
        }
      case Some(_: LeftSquareToken) =>
        parseSequenceLiteral(tokens, constants)
      case _ =>
        parseBinopSymbolExpr(tokens)
    }
  }

  private def parseIntCst(tokens: Seq[Token]): (IntCst, Seq[Token]) = {
    val (e, rest1) = tokens.headOption match {
      case Some(NatToken(n)) => (IntCst(n)(), tokens.tail)
      case Some(_: PlusToken) =>
        val rest1 = tokens.tail
        val (NatToken(n), rest2) = expect(NatToken, rest1)
        (IntCst(n)(), rest2)
      case Some(_: MinusToken) =>
        val rest1 = tokens.tail
        val (NatToken(n), rest2) = expect(NatToken, rest1)
        (IntCst(-n)(), rest2)
      case Some(tok) =>
        throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
      case None => throw SyntaxError("unexpected end of file", None)
    }
    rest1.headOption match {
      case Some(colon: ColonToken) =>
        val rest2 = rest1.tail
        rest2.headOption match {
          case Some(IntToken(typ)) =>
            val rest3 = rest2.tail
            if (!typ.contains(e.i)) {
              throw SyntaxError(
                s"constant ${e.i} does not fit in type $typ",
                colon.loc
              )
            }
            (e.rebuild(typ).asInstanceOf[IntCst], rest3)
          case _ => (e, rest1)
        }
      case _ => (e, rest1)
    }
  }

  private def parseBinopSymbolExpr(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    tokens.headOption match {
      case Some(_: LogOrToken) =>
        (makePatternFunction(Or(_, _)()), tokens.tail)
      case Some(_: LogAndToken) =>
        (makePatternFunction(And(_, _)()), tokens.tail)
      case Some(_: BitOrToken) =>
        (makePatternFunction(BitwiseOr(_, _)()), tokens.tail)
      case Some(_: BitAndToken) =>
        (makePatternFunction(BitwiseAnd(_, _)()), tokens.tail)
      case Some(_: EqToken) =>
        (makePatternFunction(SmartEqual(_, _)()), tokens.tail)
      case Some(_: EqTickToken) =>
        (makePatternFunction(Equal(_, _)()), tokens.tail)
      case Some(_: NeqToken) =>
        (makePatternFunction(SmartNotEqual(_, _)()), tokens.tail)
      case Some(_: LtToken) =>
        (makePatternFunction(SmartLessThan(_, _)()), tokens.tail)
      case Some(_: LtTickToken) =>
        (makePatternFunction(LessThan(_, _)()), tokens.tail)
      case Some(_: GtToken) =>
        (makePatternFunction(SmartGreaterThan(_, _)()), tokens.tail)
      case Some(_: LeqToken) =>
        (makePatternFunction(SmartLessThanOrEqual(_, _)()), tokens.tail)
      case Some(_: GeqToken) =>
        (makePatternFunction(SmartGreaterThanOrEqual(_, _)()), tokens.tail)
      case Some(_: LShiftToken) =>
        (makePatternFunction(LShift(_, _)()), tokens.tail)
      case Some(_: ARShiftToken) =>
        (makePatternFunction(ARShift(_, _)()), tokens.tail)
      case Some(_: LRShiftToken) =>
        (makePatternFunction(LRShift(_, _)()), tokens.tail)
      case Some(_: PlusToken) =>
        (makePatternFunction(SmartSum(_, _)()), tokens.tail)
      case Some(_: PlusTickToken) =>
        (makePatternFunction(Sum(_, _)()), tokens.tail)
      case Some(_: PlusPercentToken) =>
        (makePatternFunction(SmartWrappingSum(_, _)()), tokens.tail)
      case Some(_: PlusPercentTickToken) =>
        (makePatternFunction(WrappingSum(_, _)()), tokens.tail)
      case Some(_: PlusCaretToken) =>
        (makePatternFunction(SafeSum(_, _)()), tokens.tail)
      case Some(_: MinusToken) =>
        (makePatternFunction(SmartDiff(_, _)()), tokens.tail)
      case Some(_: MinusPercentToken) =>
        (makePatternFunction(SmartWrappingDiff(_, _)()), tokens.tail)
      case Some(_: MinusPercentTickToken) =>
        (makePatternFunction(WrappingDiff(_, _)()), tokens.tail)
      case Some(_: MinusCaretToken) =>
        (makePatternFunction(SafeDiff(_, _)()), tokens.tail)
      case Some(_: TimesToken) =>
        (makePatternFunction(SmartProd(_, _)()), tokens.tail)
      case Some(_: TimesTickToken) =>
        (makePatternFunction(Prod(_, _)()), tokens.tail)
      case Some(_: TimesPercentToken) =>
        (makePatternFunction(SmartWrappingProd(_, _)()), tokens.tail)
      case Some(_: TimesPercentTickToken) =>
        (makePatternFunction(WrappingProd(_, _)()), tokens.tail)
      case Some(_: TimesCaretToken) =>
        (makePatternFunction(SafeProd(_, _)()), tokens.tail)
      case Some(_: SlashToken) =>
        (makePatternFunction(SmartDiv(_, _)()), tokens.tail)
      case Some(_: SlashTickToken) =>
        (makePatternFunction(Div(_, _)()), tokens.tail)
      case Some(_: PercentToken) =>
        (makePatternFunction(SmartMod(_, _)()), tokens.tail)
      case Some(_: PercentTickToken) =>
        (makePatternFunction(Mod(_, _)()), tokens.tail)
      case Some(tok) =>
        throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
      case None => throw SyntaxError("unexpected end of file", None)
    }
  }

  private def makePatternFunction(op: (Expr, Expr) => Expr): Expr = {
    val x = Param("x", -1)(Missing)
    val y = Param("y", -1)(Missing)
    val pat = TuplePattern(ParamPattern(x), ParamPattern(y))
    PatternFunction(pat, op(x, y))()
  }

  private def parseSequenceLiteral(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Expr, Seq[Token]) = {
    val (lsq, rest1) = expect(LeftSquareToken, tokens)
    rest1.headOption match {
      case Some(_: RightSquareVToken) =>
        // Empty vector
        val rest2 = rest1.tail
        val rest3 = rest2.headOption match {
          case Some(_: ColonToken) => rest2.tail
          case _ =>
            throw SyntaxError(
              "missing type annotation for empty Vec literal",
              lsq.loc
            )
        }
        val (vecTyp, rest4) = parseVecTyp(rest3, constants)
        if (vecTyp.n != C(0)()) {
          throw SyntaxError(
            s"wrong length in Vec type annotation: ${vecTyp.n} (expected 0)",
            lsq.loc
          )
        }
        (VecLiteral()(vecTyp), rest4)
      case Some(_: RightSquareSToken) =>
        // Empty stream
        val rest2 = rest1.tail
        val rest3 = rest2.headOption match {
          case Some(_: ColonToken) => rest2.tail
          case _ =>
            throw SyntaxError(
              "missing type annotation for empty Stm literal",
              lsq.loc
            )
        }
        val (stmTyp, rest4) = parseStmTyp(rest3, constants)
        if (stmTyp.n != C(0)()) {
          throw SyntaxError(
            s"wrong length in Stm type annotation: ${stmTyp.n} (expected 0)",
            lsq.loc
          )
        }
        (StmLiteral()(stmTyp), rest4)
      case _ =>
        // Non-empty vector or stream
        val (elems, rest2) = parseExprList(rest1, constants)
        rest2.headOption match {
          case Some(_: RightSquareVToken) =>
            val rest3 = rest2.tail
            rest3.headOption match {
              case Some(_: ColonToken) =>
                throw SyntaxError(
                  "type annotations are forbidden for non-empty Vec literals",
                  lsq.loc
                )
              case _ => ()
            }
            (VecLiteral(elems: _*)(), rest3)
          case Some(_: RightSquareSToken) =>
            val rest3 = rest2.tail
            rest3.headOption match {
              case Some(_: ColonToken) =>
                throw SyntaxError(
                  "type annotations are forbidden for non-empty Stm literals",
                  lsq.loc
                )
              case _ => ()
            }
            (StmLiteral(elems: _*)(), rest3)
          case Some(tok) =>
            throw SyntaxError(s"unexpected token: ${tok.quot}", tok.loc)
          case None => throw SyntaxError("unexpected end of file", None)
        }
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
    val (typ, rest) = tokens.headOption match {
      case Some(_: BoolToken)  => (TyBool, tokens.tail)
      case Some(IntToken(typ)) => (typ, tokens.tail)
      case Some(_: LeftParToken) =>
        val rest1 = tokens.tail
        rest1.headOption match {
          case Some(_: RightParToken) =>
            (TyTuple(), rest1.tail)
          case _ =>
            val (typ, rest2) = parseTyp(rest1, constants)
            // (T,) is the type of a 1-tuple whose element has type T
            val oneTupleResult: Option[(Type, Seq[Token])] =
              rest2.headOption match {
                case Some(_: CommaToken) =>
                  val rest3 = rest2.tail
                  rest3.headOption match {
                    case Some(_: RightParToken) =>
                      val rest4 = rest3.tail
                      Some((TyTuple(typ), rest4))
                    case _ => None
                  }
                case _ => None
              }
            oneTupleResult
              .getOrElse(rest2.headOption match {
                case Some(_: RightParToken) =>
                  (typ, rest2.tail)
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
              })
        }
      case Some(_: VecToken)    => parseVecTyp(tokens, constants)
      case Some(_: BigStmToken) => parseStmTyp(tokens, constants)
      case Some(tok) =>
        throw SyntaxError(s"expected a type but found ${tok.quot}", tok.loc)
      case None =>
        throw SyntaxError("expected a type but reached end of file", None)
    }
    parseTypPrime(typ, rest, constants)
  }

  private def parseTypPrime(
      typ: Type,
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (Type, Seq[Token]) = {
    tokens.headOption match {
      case Some(_: SingleArrowToken) =>
        val rest1 = tokens.tail
        val (returnTyp, rest2) = parseTyp(rest1, constants)
        (TyArrow(typ, returnTyp), rest2)
      case _ => (typ, tokens)
    }
  }

  private def parseVecTyp(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (TyVec, Seq[Token]) = {
    val rest1 = tokens.headOption match {
      case Some(_: VecToken) => tokens.tail
      case Some(tok) =>
        throw SyntaxError(s"expected a Vec type but found ${tok.quot}", tok.loc)
      case None =>
        throw SyntaxError("expected a Vec type but reached end of file", None)
    }
    val (_, rest2) = expect(LeftSquareToken, rest1)
    val (typ, rest3) = parseTyp(rest2, constants)
    val (_, rest4) = expect(CommaToken, rest3)
    val (len, rest5) = parseExpr(rest4, constants)
    val (_, rest6) = expect(RightSquareToken, rest5)
    // Constructing a TyVec involves canonicalizing the length, and
    // canonicalization requires type checking.
    // But type checking something like Vec[u8, n] in isolation will fail,
    // since n is free here!
    // Therefore, type check the length beforehand, with the set of
    // constants as the typing context.
    val checkedLen = len.tchk(constants, Map())
    (TyVec(typ, checkedLen), rest6)
  }

  private def parseStmTyp(
      tokens: Seq[Token],
      constants: Map[Param, Type]
  ): (TyStm, Seq[Token]) = {
    val rest1 = tokens.headOption match {
      case Some(_: BigStmToken) => tokens.tail
      case Some(tok) =>
        throw SyntaxError(s"expected a Stm type but found ${tok.quot}", tok.loc)
      case None =>
        throw SyntaxError("expected a Stm type but reached end of file", None)
    }
    val (_, rest2) = expect(LeftSquareToken, rest1)
    val (typ, rest3) = parseTyp(rest2, constants)
    val (_, rest4) = expect(CommaToken, rest3)
    val (len, rest5) = parseExpr(rest4, constants)
    val (_, rest6) = expect(RightSquareToken, rest5)
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
          rest4.head.loc
        )
      }
      len.tchk(constants, Map())
    }
    (TyStm(typ, checkedLen), rest6)
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
    tokens.headOption match {
      case Some(t) =>
        val rest = tokens.tail
        if (t.category != tc) {
          throw SyntaxError(s"expected ${tc.name} but found ${t.quot}", t.loc)
        }
        (t, rest)
      case None =>
        throw SyntaxError(s"expected ${tc.name} but reached end of file", None)
    }
  }

  private def loc(tokens: Seq[Token]): String = {
    tokens.headOption.map(x => x.loc.toString).getOrElse("")
  }

  private def reconstructSource(tokens: Seq[Token]): String = {
    // TODO: Keep track of whitespace too?
    tokens.foldLeft("")({ case (src, tok) => src + tok.original })
  }
}
