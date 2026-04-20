package mhir.parse.sirop

import mhir.canonicalize._
import mhir.ir._
import mhir.parse.SyntaxError
import mhir.sugar._
import os.Path

import scala.annotation.tailrec
import scala.collection.immutable.Queue

object Parser {

  def parse(f: Path): Program = parse(os.read(f))

  def parse(code: String): Program = {
    val (prog, remainingTokens) = parseProgram(Lexer.lex(code).toList)
    if (remainingTokens.nonEmpty) {
      val loc = remainingTokens.head.loc
      throw SyntaxError("unexpected tokens remaining at end of file", loc)
    }
    prog
  }

  def parseProgram(tokens: Seq[Token]): (Program, Seq[Token]) = {
    tokens match {
      case Seq(
            _: AcceleratorToken,
            IdentToken(name),
            _: AssignToken,
            rest1 @ _*
          ) =>
        val (e, rest2) = parseExpr(rest1)
        (Program(name, e), rest2)
      case rest1 =>
        val (e, rest2) = parseExpr(rest1)
        (Program(e), rest2)
    }
  }

  def parseStmt(code: String): Stmt = {
    val (s, remainingTokens) = parseStmt(Lexer.lex(code).toList)
    if (remainingTokens.nonEmpty) {
      val loc = remainingTokens.head.loc
      throw SyntaxError("unexpected tokens remaining at end of file", loc)
    }
    s
  }

  private def parseStmt(tokens: Seq[Token]): (Stmt, Seq[Token]) = {
    tokens match {
      case Seq(_: ExitToken, rest @ _*) =>
        (ExitStmt, rest)
      case Seq(IdentToken(x), _: AssignToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1)
        (SetStmt(Param(x, -1)(Missing), e), rest2)
      case _ =>
        val (e, rest) = parseExpr(tokens)
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
      TrueToken,
      FalseToken,
      PlusToken,
      MinusToken,
      NatToken,
      LeftSquareToken,
      // expr1
      PadToken,
      TruncateToken,
      SignToken,
      UnsignToken,
      VbuildToken,
      SbuildToken,
      SdataToken,
      // expr2
      BangToken,
      // expr10
      IfToken,
      LeftParToken,
      LetStmToken
    )

  private def parseExpr(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    parseExpr10(tokens)
  }

  private def parseExpr10(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: IfToken, rest1 @ _*) =>
        val (c, rest2) = parseExpr(rest1)
        val (_, rest3) = expect(ThenToken, rest2)
        val (t, rest4) = parseExpr(rest3)
        val (_, rest5) = expect(ElseToken, rest4)
        val (f, rest6) = parseExpr(rest5)
        (Mux(c, t, f)(), rest6)
      case Seq(_: LeftParToken, IdentToken(param), _: ColonToken, rest1 @ _*) =>
        val (typ, rest2) = parseTyp(rest1)
        val (_, rest3) = expect(RightParToken, rest2)
        val (_, rest4) = expect(DoubleArrowToken, rest3)
        val (body, rest5) = parseExpr(rest4)
        (Function(Param(param, -1)(typ), body)(), rest5)
      case Seq(
            _: LeftParToken,
            IdentToken(param),
            _: RightParToken,
            _: DoubleArrowToken,
            rest1 @ _*
          ) =>
        val (body, rest2) = parseExpr(rest1)
        (Function(Param(param, -1)(Missing), body)(), rest2)
      case Seq(_: LetStmToken, _: LeftSquareToken, rest1 @ _*) =>
        val (bufSize, rest2) = parseExpr(rest1)
        val (_, rest3) = expect(RightSquareToken, rest2)
        val (IdentToken(ident), rest4) = expect(IdentToken, rest3)
        val (typ, rest5) = rest4 match {
          case Seq(_: ColonToken, rest @ _*) => parseTyp(rest)
          case _                             => (Missing, rest4)
        }
        val (_, rest6) = expect(AssignToken, rest5)
        val (in, rest7) = parseExpr(rest6)
        val (_, rest8) = expect(InToken, rest7)
        val (out, rest9) = parseExpr(rest8)
        (LetStm(bufSize, Param(ident, -1)(typ), in, out)(), rest9)
      case Seq(_: LetToken, IdentToken(x), rest1 @ _*) =>
        val (typ, rest2) = rest1 match {
          case Seq(_: ColonToken, rest @ _*) => parseTyp(rest)
          case _                             => (Missing, rest1)
        }
        val (_, rest3) = expect(AssignToken, rest2)
        val (in, rest4) = parseExpr(rest3)
        val (_, rest5) = expect(InToken, rest4)
        val (out, rest6) = parseExpr(rest5)
        (Let(Param(x, -1)(typ), in, out)(), rest6)
      case _ => parseExpr9(tokens)
    }
  }

  private def parseExpr9(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr8(tokens)
    parseExpr9Prime(e, rest)
  }

  @tailrec
  private def parseExpr9Prime(
      e1: Expr,
      tokens: Seq[Token]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: LogOrToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr8(rest1)
        parseExpr9Prime(e1 || e2, rest2)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr8(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr7(tokens)
    parseExpr8Prime(e, rest)
  }

  @tailrec
  private def parseExpr8Prime(
      e1: Expr,
      tokens: Seq[Token]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: LogAndToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr7(rest1)
        parseExpr8Prime(e1 && e2, rest2)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr7(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr6(tokens)
    parseExpr7Prime(e, rest)
  }

  @tailrec
  private def parseExpr7Prime(
      e1: Expr,
      tokens: Seq[Token]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: EqToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr6(rest1)
        parseExpr7Prime(e1 equ e2, rest2)
      case Seq(_: NeqToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr6(rest1)
        parseExpr7Prime(e1 nequ e2, rest2)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr6(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr5(tokens)
    parseExpr6Prime(e, rest)
  }

  @tailrec
  private def parseExpr6Prime(
      e1: Expr,
      tokens: Seq[Token]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: LtToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr5(rest1)
        parseExpr6Prime(e1 lt e2, rest2)
      case Seq(_: GtToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr5(rest1)
        parseExpr6Prime(e1 gt e2, rest2)
      case Seq(_: LeqToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr5(rest1)
        parseExpr6Prime(e1 leq e2, rest2)
      case Seq(_: GeqToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr5(rest1)
        parseExpr6Prime(e1 geq e2, rest2)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr5(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr4(tokens)
    parseExpr5Prime(e, rest)
  }

  @tailrec
  private def parseExpr5Prime(
      e1: Expr,
      tokens: Seq[Token]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: LLShiftToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr4(rest1)
        parseExpr5Prime(LLShift(e1, e2)(), rest2)
      case Seq(_: LRShiftToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr4(rest1)
        parseExpr5Prime(LRShift(e1, e2)(), rest2)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr4(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr3(tokens)
    parseExpr4Prime(e, rest)
  }

  @tailrec
  private def parseExpr4Prime(
      e1: Expr,
      tokens: Seq[Token]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: PlusToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr3(rest1)
        parseExpr4Prime(Sum(e1, e2)(), rest2)
      case Seq(_: PlusPercentToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr3(rest1)
        parseExpr4Prime(WrappingSum(e1, e2)(), rest2)
      case Seq(_: MinusPercentToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr3(rest1)
        parseExpr4Prime(WrappingDiff(e1, e2)(), rest2)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr3(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    val (e, rest) = parseExpr2(tokens)
    parseExpr3Prime(e, rest)
  }

  @tailrec
  private def parseExpr3Prime(
      e1: Expr,
      tokens: Seq[Token]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: TimesToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr2(rest1)
        parseExpr3Prime(Prod(e1, e2)(), rest2)
      case Seq(_: TimesPercentToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr2(rest1)
        parseExpr3Prime(WrappingProd(e1, e2)(), rest2)
      case Seq(_: SlashToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr2(rest1)
        parseExpr3Prime(Div(e1, e2)(), rest2)
      case Seq(_: PercentToken, rest1 @ _*) =>
        val (e2, rest2) = parseExpr2(rest1)
        parseExpr3Prime(Mod(e1, e2)(), rest2)
      case _ => (e1, tokens)
    }
  }

  private def parseExpr2(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: BangToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr2(rest1)
        (Not(e)(), rest2)
      case _ => parseExpr1(tokens)
    }
  }

  private def parseExpr1(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    val (e, rest) = tokens match {
      case Seq(PadToken(w), _: LeftParToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1)
        val (_, rest3) = expect(RightParToken, rest2)
        (PadTo(e, w)(), rest3)
      case Seq(TruncateToken(w), _: LeftParToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1)
        val (_, rest3) = expect(RightParToken, rest2)
        (TruncateTo(e, w)(), rest3)
      case Seq(_: SignToken, _: LeftParToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1)
        val (_, rest3) = expect(RightParToken, rest2)
        (ToSigned(e)(), rest3)
      case Seq(_: UnsignToken, _: LeftParToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1)
        val (_, rest3) = expect(RightParToken, rest2)
        (ToUnsigned(e)(), rest3)
      case Seq(_: SdataToken, _: LeftParToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1)
        val (_, rest3) = expect(RightParToken, rest2)
        (StmData(e)(), rest3)
      case Seq(_: VbuildToken, _: LeftParToken, rest1 @ _*) =>
        val (len, rest2) = parseExpr(rest1)
        val (_, rest3) = expect(RightParToken, rest2)
        val (_, rest4) = expect(LeftCurlyToken, rest3)
        val (f, rest5) = parseExpr(rest4)
        f match {
          case f: Function =>
            val (_, rest6) = expect(RightCurlyToken, rest5)
            (VecBuild(len, f)(), rest6)
          case e =>
            throw SyntaxError(
              s"expected a function literal in body of vbuild but found $e"
            )
        }
      case Seq(_: SbuildToken, _*) => parseSbuild(tokens)
      case _                       => parseExpr0(tokens)
    }
    parseExpr1Prime(e, rest)
  }

  private def parseSbuild(tokens: Seq[Token]): (StmBuild, Seq[Token]) = {
    val (_, rest1) = expect(SbuildToken, tokens)
    val (_, rest2) = expect(LeftParToken, rest1)
    val (n, rest3) = parseExpr(rest2)
    val (_, rest4) = expect(RightParToken, rest3)
    val (_, rest5) = expect(LeftParToken, rest4)
    val (data, rest6) = parseExpr(rest5)
    val (_, rest7) = expect(CommaToken, rest6)
    val (valid, rest8) = parseExpr(rest7)
    val (_, rest9) = expect(RightParToken, rest8)
    val (_, rest10) = expect(LeftCurlyToken, rest9)
    val (accumulators, rest11) = parseAccumulators(rest10)
    val (_, rest12) = expect(RightCurlyToken, rest11)
    val (_, rest13) = expect(LeftCurlyToken, rest12)
    val (producers, rest14) = parseProducers(rest13)
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
      tokens: Seq[Token]
  ): (Map[Param, (Expr, Expr)], Seq[Token]) = {
    tokens match {
      case Seq(_: LeftParToken, _*) =>
        val (acc, rest1) = parseAccumulator(tokens)
        var accumulators = Map(acc)
        var rest2 = rest1
        while (rest2.headOption.exists(_.category == CommaToken)) {
          val (acc, rest3) = parseAccumulator(rest2.tail)
          accumulators = accumulators + acc
          rest2 = rest3
        }
        (accumulators, rest2)
      case _ => (Map(), tokens)
    }
  }

  private def parseAccumulator(
      tokens: Seq[Token]
  ): ((Param, (Expr, Expr)), Seq[Token]) = {
    val (_, rest1) = expect(LeftParToken, tokens)
    val (IdentToken(x), rest2) = expect(IdentToken, rest1)
    val (_, rest3) = expect(ColonToken, rest2)
    val (typ, rest4) = parseTyp(rest3)
    val rest5 = expectMany(
      rest4,
      RightParToken,
      AssignToken,
      LeftCurlyToken,
      InitToken,
      ColonToken
    )
    val (z, rest6) = parseExpr(rest5)
    val rest7 = expectMany(rest6, CommaToken, NextToken, ColonToken)
    val (next, rest8) = parseExpr(rest7)
    val (_, rest9) = expect(RightCurlyToken, rest8)
    val acc = Param(x, -1)(typ) -> (z, next)
    (acc, rest9)
  }

  private def parseProducers(
      tokens: Seq[Token]
  ): (Map[Param, (Expr, Expr)], Seq[Token]) = {
    tokens match {
      case Seq(_: LeftParToken, _*) =>
        val (prod, rest1) = parseProducer(tokens)
        var producers = Map(prod)
        var rest2 = rest1
        while (rest2.headOption.exists(_.category == CommaToken)) {
          val (acc, rest3) = parseProducer(rest2.tail)
          producers = producers + acc
          rest2 = rest3
        }
        (producers, rest2)
      case _ => (Map(), tokens)
    }
  }

  private def parseProducer(
      tokens: Seq[Token]
  ): ((Param, (Expr, Expr)), Seq[Token]) = {
    val (_, rest1) = expect(LeftParToken, tokens)
    val (IdentToken(x), rest2) = expect(IdentToken, rest1)
    val (_, rest3) = expect(ColonToken, rest2)
    val (typ, rest4) = parseStmTyp(rest3)
    val rest5 = expectMany(
      rest4,
      RightParToken,
      AssignToken,
      LeftCurlyToken,
      LittleStmToken,
      ColonToken
    )
    val (stm, rest6) = parseExpr(rest5)
    val rest7 = expectMany(rest6, CommaToken, ReadyToken, ColonToken)
    val (ready, rest8) = parseExpr(rest7)
    val (_, rest9) = expect(RightCurlyToken, rest8)
    val prod = Param(x, -1)(typ) -> (stm, ready)
    (prod, rest9)
  }

  @tailrec
  private def parseExpr1Prime(
      e: Expr,
      tokens: Seq[Token]
  ): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: LeftParToken, rest1 @ _*) =>
        val (args, rest2) = parseExprList(rest1)
        val (_, rest3) = expect(RightParToken, rest2)
        parseExpr1Prime(parseFunCall(e, args), rest3)
      case Seq(_: DotToken, IdentToken(op), _: LeftParToken, rest1 @ _*) =>
        val (args, rest2) = parseExprList(rest1)
        val (_, rest3) = expect(RightParToken, rest2)
        parseExpr1Prime(parseFunCall(Param(op, -1)(Missing), e +: args), rest3)
      case Seq(_: DotToken, NatToken(i), rest @ _*) =>
        parseExpr1Prime(TupleAccess(e, i.toInt)(), rest)
      case Seq(_: LeftSquareToken, rest1 @ _*) =>
        val (i, rest2) = parseExpr(rest1)
        val (_, rest3) = expect(RightSquareToken, rest2)
        parseExpr1Prime(VecAccess(e, i)(), rest3)
      case _ => (e, tokens)
    }
  }

  private def parseFunCall(f: Expr, args: Seq[Expr]): Expr = {
    f match {
      // Arithmetic operators ----------------------------------------------
      case f @ Param("min", -1) =>
        args match {
          case Seq(x, y) => Min(x, y)()
          case _         => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("max", -1) =>
        args match {
          case Seq(x, y) => Max(x, y)()
          case _         => throw SyntaxError(s"invalid arguments to $f")
        }
      // Vector operators --------------------------------------------------
      case f @ Param("VecLength", -1) =>
        args match {
          case Seq(v) => VecLength(v)()
          case _      => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("Vec2Stm", -1) =>
        args match {
          case Seq(v) => Vec2Stm(v)()
          case _      => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("VecMap", -1) =>
        args match {
          case Seq(v, f) => VecMap(v, f)()
          case _         => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("VecZip", -1) =>
        args match {
          case Seq(v1, v2) => VecZip(v1, v2)()
          case _           => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("VecReduce", -1) =>
        args match {
          case Seq(v, f) => VecReduceComb(v, f)()
          case _         => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("VecReverse", -1) =>
        args match {
          case Seq(v) => VecReverse(v)
          case _      => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("VecSplit", -1) =>
        args match {
          case Seq(s, m) => VecSplit(s, m)()
          case _         => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("VecJoin", -1) =>
        args match {
          case Seq(v) => VecJoin(v)()
          case _      => throw SyntaxError(s"invalid arguments to $f")
        }
      // Stream operators --------------------------------------------------
      case f @ Param("Stm2Vec", -1) =>
        args match {
          case Seq(s) => Stm2Vec(s)()
          case _      => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("StmRange", -1) =>
        args match {
          case Seq(n, z, delta) => StmRange(n, z, delta)()
          case _                => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("StmCount2D", -1) =>
        args match {
          case Seq(n, m) => StmCount2D(n, m)()
          case _         => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("StmMap", -1) =>
        args match {
          case Seq(s, f: Function) => StmMap(s, f)()
          case _ => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("StmReduce", -1) =>
        args match {
          case Seq(s, f) => StmReduce(s, f)()
          case _         => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("StmMap2", -1) =>
        args match {
          case Seq(s1, s2, f: Function) => StmMap2(s1, s2, f)()
          case _ => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("StmZip", -1) =>
        args match {
          case Seq(s1, s2) => StmZip(s1, s2)()
          case _           => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("StmSlide", -1) =>
        args match {
          case Seq(s, w) => StmSlideV(s, w)()
          case _         => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("StmSlide2D", -1) =>
        args match {
          case Seq(s, h, w) => StmSlide2D(s, h, w)()
          case _            => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("StmAccess", -1) =>
        args match {
          case Seq(s, i) => StmAccess(s, i)()
          case _         => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("StmSplit", -1) =>
        args match {
          case Seq(s, m) => StmSplit(s, m)()
          case _         => throw SyntaxError(s"invalid arguments to $f")
        }
      case f @ Param("StmJoin", -1) =>
        args match {
          case Seq(s) => StmJoin(s)()
          case _      => throw SyntaxError(s"invalid arguments to $f")
        }
      case _ =>
        args match {
          case Seq(x) => FunCall(f, x)()
          case _      => FunCall(f, Tuple(args: _*)())()
        }
    }
  }

  private def parseExpr0(tokens: Seq[Token]): (Expr, Seq[Token]) = {
    tokens match {
      case Seq(_: LeftParToken, _: RightParToken, rest @ _*) =>
        (Tuple()(), rest)
      case Seq(_: LeftParToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1)
        rest2 match {
          case Seq(_: CommaToken, _: RightParToken, rest3 @ _*) =>
            (Tuple(e)(), rest3)
          case Seq(_: RightParToken, rest3 @ _*) =>
            (e, rest3)
          case _ =>
            var rest3 = rest2
            var elems = Seq(e)
            while (rest3.headOption.exists(_.category == CommaToken)) {
              val (nextElem, rest4) = parseExpr(rest3.tail)
              rest3 = rest4
              elems = elems :+ nextElem
            }
            val (_, rest4) = expect(RightParToken, rest3)
            (Tuple(elems: _*)(), rest4)
        }
      case Seq(_: LeftCurlyToken, rest1 @ _*) =>
        val (e, rest2) = parseExpr(rest1)
        val (_, rest3) = expect(RightCurlyToken, rest2)
        (e, rest3)
      case Seq(IdentToken(ident), rest @ _*) =>
        (Param(ident, -1)(Missing), rest)
      case Seq(_: UndefinedToken, _: LeftSquareToken, rest1 @ _*) =>
        val (typ, rest2) = parseTyp(rest1)
        val (_, rest3) = expect(RightSquareToken, rest2)
        (Undefined(typ), rest3)
      case Seq(_: TrueToken, rest @ _*)  => (True, rest)
      case Seq(_: FalseToken, rest @ _*) => (False, rest)
      case Seq(_: NatToken, _*) | Seq(_: PlusToken, _*) |
          Seq(_: MinusToken, _*) =>
        parseIntCst(tokens)
      case Seq(
            _: LeftSquareToken,
            _: RightSquareVToken,
            _: ColonToken,
            rest1 @ _*
          ) =>
        val (vecTyp, rest2) = parseVecTyp(rest1)
        if (vecTyp.n != C(0)()) {
          throw SyntaxError(
            s"wrong length in Vec type annotation: ${vecTyp.n} (expected 0)"
          )
        }
        (VecLiteral()(vecTyp), rest2)
      case Seq(_: LeftSquareToken, _: RightSquareVToken, _*) =>
        throw SyntaxError("missing type annotation for empty Vec literal")
      case Seq(
            _: LeftSquareToken,
            _: RightSquareSToken,
            _: ColonToken,
            rest1 @ _*
          ) =>
        val (stmTyp, rest2) = parseStmTyp(rest1)
        if (stmTyp.n != C(0)()) {
          throw SyntaxError(
            s"wrong length in Stm type annotation: ${stmTyp.n} (expected 0)"
          )
        }
        (StmLiteral()(stmTyp), rest2)
      case Seq(_: LeftSquareToken, _: RightSquareSToken, _*) =>
        throw SyntaxError("missing type annotation for empty Stm literal")
      case Seq(_: LeftSquareToken, rest1 @ _*) =>
        val (elems, rest2) = parseExprList(rest1)
        rest2 match {
          case Seq(_: RightSquareVToken, _: ColonToken, _*) =>
            throw SyntaxError(
              "type annotations are forbidden for non-empty Vec literals"
            )
          case Seq(_: RightSquareSToken, _: ColonToken, _*) =>
            throw SyntaxError(
              "type annotations are forbidden for non-empty Stm literals"
            )
          case Seq(_: RightSquareVToken, rest3 @ _*) =>
            (VecLiteral(elems: _*)(), rest3)
          case Seq(_: RightSquareSToken, rest3 @ _*) =>
            (StmLiteral(elems: _*)(), rest3)
          case Seq(tok, _*) =>
            throw SyntaxError(s"unexpected token: ${tok.quot}")
          case Seq() => throw SyntaxError("unexpected end of file")
        }
      case Seq(tok, _*) =>
        throw SyntaxError(s"unexpected token: ${tok.quot}")
      case Seq() => throw SyntaxError("unexpected end of file")
    }
  }

  private def parseIntCst(tokens: Seq[Token]): (IntCst, Seq[Token]) = {
    val (e, rest1) = tokens match {
      case Seq(NatToken(n), rest @ _*)                => (IntCst(n)(), rest)
      case Seq(_: PlusToken, NatToken(n), rest @ _*)  => (IntCst(n)(), rest)
      case Seq(_: MinusToken, NatToken(n), rest @ _*) => (IntCst(-n)(), rest)
      case Seq(tok, _ @_*) =>
        throw SyntaxError(s"unexpected token: ${tok.quot}")
      case Seq() => throw SyntaxError("unexpected end of file")
    }
    rest1 match {
      case Seq(_: ColonToken, IntToken(typ), rest2 @ _*) =>
        if (!typ.contains(e.i)) {
          throw SyntaxError(s"type $typ is not valid for constant ${e.i}")
        }
        (e.rebuild(typ).asInstanceOf[IntCst], rest2)
      case _ =>
        (e, rest1)
    }
  }

  private def parseExprList(tokens: Seq[Token]): (Seq[Expr], Seq[Token]) = {
    tokens.headOption match {
      case None                                           => (Seq(), tokens)
      case Some(tok) if !FirstExpr.contains(tok.category) => (Seq(), tokens)
      case _ =>
        val (e0, rest1) = parseExpr(tokens)
        var elems = Queue(e0)
        var rest2 = rest1
        while (rest2.headOption.exists(_.category == CommaToken)) {
          val (nextElem, rest3) = parseExpr(rest2.tail)
          elems = elems :+ nextElem
          rest2 = rest3
        }
        (elems, rest2)
    }
  }

  private def parseTyp(tokens: Seq[Token]): (Type, Seq[Token]) = {
    val (typ, rest) = tokens match {
      case Seq(_: BoolToken, rest @ _*)  => (TyBool, rest)
      case Seq(IntToken(typ), rest @ _*) => (typ, rest)
      case Seq(_: LeftParToken, _: RightParToken, rest @ _*) =>
        (TyTuple(), rest)
      case Seq(_: LeftParToken, rest1 @ _*) =>
        val (typ, rest2) = parseTyp(rest1)
        rest2 match {
          case Seq(_: CommaToken, _: RightParToken, rest3 @ _*) =>
            (TyTuple(typ), rest3)
          case Seq(_: RightParToken, rest3 @ _*) =>
            (typ, rest3)
          case _ =>
            var rest3 = rest2
            var elemTypes = Seq(typ)
            while (rest3.headOption.exists(_.category == CommaToken)) {
              val (nextTyp, rest4) = parseTyp(rest3.tail)
              rest3 = rest4
              elemTypes = elemTypes :+ nextTyp
            }
            val (_, rest4) = expect(RightParToken, rest3)
            (TyTuple(elemTypes: _*), rest4)
        }
      case Seq(_: VecToken, _*)    => parseVecTyp(tokens)
      case Seq(_: BigStmToken, _*) => parseStmTyp(tokens)
      case Seq(tok, _*) =>
        throw SyntaxError(s"expected a type but found ${tok.quot}")
      case Seq() =>
        throw SyntaxError("expected a type but reached end of file")
    }
    parseTypPrime(typ, rest)
  }

  private def parseTypPrime(
      typ: Type,
      tokens: Seq[Token]
  ): (Type, Seq[Token]) = {
    tokens match {
      case Seq(_: SingleArrowToken, rest1 @ _*) =>
        val (returnTyp, rest2) = parseTyp(rest1)
        (TyArrow(typ, returnTyp), rest2)
      case _ => (typ, tokens)
    }
  }

  private def parseVecTyp(tokens: Seq[Token]): (TyVec, Seq[Token]) = {
    tokens match {
      case Seq(_: VecToken, _: LeftSquareToken, rest1 @ _*) =>
        val (typ, rest2) = parseTyp(rest1)
        val (_, rest3) = expect(CommaToken, rest2)
        val (len, rest4) = parseExpr(rest3)
        val (_, rest5) = expect(RightSquareToken, rest4)
        (TyVec(typ, len), rest5)
      case Seq(tok, _*) =>
        throw SyntaxError(s"expected a Vec type but found ${tok.quot}")
      case Seq() =>
        throw SyntaxError("expected a Vec type but reached end of file")
    }
  }

  private def parseStmTyp(tokens: Seq[Token]): (TyStm, Seq[Token]) = {
    tokens match {
      case Seq(_: BigStmToken, _: LeftSquareToken, rest1 @ _*) =>
        val (typ, rest2) = parseTyp(rest1)
        val (_, rest3) = expect(CommaToken, rest2)
        val (len, rest4) = parseExpr(rest3)
        val (_, rest5) = expect(RightSquareToken, rest4)
        (TyStm(typ, len), rest5)
      case Seq(tok, _*) =>
        throw SyntaxError(s"expected a Stm type but found ${tok.quot}")
      case Seq() =>
        throw SyntaxError("expected a Stm type but reached end of file")
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
        throw SyntaxError(s"expected ${tc.name} but reached end of file")
    }
  }
}
