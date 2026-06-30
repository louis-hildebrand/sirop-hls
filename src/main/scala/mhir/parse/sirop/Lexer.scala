package mhir.parse.sirop

import mhir.parse.{SourcePoint, SyntaxError}
import os.Path

import java.io.File
import scala.annotation.tailrec
import scala.io.Source

object Lexer {

  def lex(p: Path): Stream[Token] = lex(p.toIO)

  private def lex(f: File): Stream[Token] = {
    val source = Source.fromFile(f)
    lex(source.toStream, SourcePoint(1, 1))
  }

  def lex(code: String): Stream[Token] = {
    lex(code.toCharArray.toStream, SourcePoint(1, 1))
  }

  private def lex(code: Stream[Char], p: SourcePoint): Stream[Token] = {
    if (code.isEmpty) {
      Stream.empty
    }
    // Comments, whitespace ----------------------------------------------------
    else if (code.startsWith("//")) {
      val (newCode, newPt) = consumeSingleLineComment(code, p)
      lex(newCode, newPt)
    } else if (code.startsWith("/*")) {
      val (newCode, newPt) = consumeMultiLineComment(code, p)
      lex(newCode, newPt)
    } else if (code.head.isWhitespace) {
      val (newCode, newPt) = consumeWhitespace(code, p)
      lex(newCode, newPt)
    }
    // Identifiers, keywords, numbers ------------------------------------------
    else if (code.head.isLetter || code.head == '_') {
      val (tok, newCode, newPt) =
        lexIdentifierOrKeyword(
          code.tail,
          p,
          p.moveRightBy(1),
          ident = new StringBuilder(code.head.toString)
        )
      tok #:: lex(newCode, newPt)
    } else if (code.head.isDigit) {
      val (tok, newCode, newPt) =
        lexNumber(
          code.tail,
          p,
          p.moveRightBy(1),
          num = new StringBuilder(code.head.toString)
        )
      tok #:: lex(newCode, newPt)
    }
    // Parentheses -------------------------------------------------------------
    else if (code.startsWith("(")) {
      LeftParToken(p) #:: lex(consume(code, "("), p.consume("("))
    } else if (code.startsWith(")")) {
      RightParToken(p) #:: lex(consume(code, ")"), p.consume(")"))
    } else if (code.startsWith("{")) {
      LeftCurlyToken(p) #:: lex(consume(code, "{"), p.consume("{"))
    } else if (code.startsWith("}")) {
      RightCurlyToken(p) #:: lex(consume(code, "}"), p.consume("}"))
    } else if (code.startsWith(":[")) {
      ColonLeftSquareToken(p) #:: lex(consume(code, ":["), p.consume(":["))
    } else if (code.startsWith("[")) {
      LeftSquareToken(p) #:: lex(consume(code, "["), p.consume("["))
    } else if (code.startsWith("]v")) {
      RightSquareVToken(p) #:: lex(consume(code, "]v"), p.consume("]v"))
    } else if (code.startsWith("]s")) {
      RightSquareSToken(p) #:: lex(consume(code, "]s"), p.consume("]s"))
    } else if (code.startsWith("]")) {
      RightSquareToken(p) #:: lex(consume(code, "]"), p.consume("]"))
    }
    // Other symbols -----------------------------------------------------------
    else if (code.startsWith("->")) {
      SingleArrowToken(p) #:: lex(consume(code, "->"), p.consume("->"))
    } else if (code.startsWith("=>")) {
      DoubleArrowToken(p) #:: lex(consume(code, "=>"), p.consume("=>"))
    } else if (code.startsWith("==`")) {
      EqTickToken(p) #:: lex(consume(code, "==`"), p.consume("==`"))
    } else if (code.startsWith("==")) {
      EqToken(p) #:: lex(consume(code, "=="), p.consume("=="))
    } else if (code.startsWith("!=")) {
      NeqToken(p) #:: lex(consume(code, "!="), p.consume("!="))
    } else if (code.startsWith("=")) {
      AssignToken(p) #:: lex(consume(code, "="), p.consume("="))
    } else if (code.startsWith(":")) {
      ColonToken(p) #:: lex(consume(code, ":"), p.consume(":"))
    } else if (code.startsWith("||")) {
      LogOrToken(p) #:: lex(consume(code, "||"), p.consume("||"))
    } else if (code.startsWith("|")) {
      BitOrToken(p) #:: lex(consume(code, "|"), p.consume("|"))
    } else if (code.startsWith("&&")) {
      LogAndToken(p) #:: lex(consume(code, "&&"), p.consume("%%"))
    } else if (code.startsWith("&")) {
      BitAndToken(p) #:: lex(consume(code, "&"), p.consume("&"))
    } else if (code.startsWith("<<")) {
      LShiftToken(p) #:: lex(consume(code, "<<"), p.consume("<<"))
    } else if (code.startsWith(">>>")) {
      LRShiftToken(p) #:: lex(consume(code, ">>>"), p.consume(">>>"))
    } else if (code.startsWith(">>")) {
      ARShiftToken(p) #:: lex(consume(code, ">>"), p.consume(">>"))
    } else if (code.startsWith("<=")) {
      LeqToken(p) #:: lex(consume(code, "<="), p.consume("<="))
    } else if (code.startsWith(">=")) {
      GeqToken(p) #:: lex(consume(code, ">="), p.consume(">="))
    } else if (code.startsWith("<`")) {
      LtTickToken(p) #:: lex(consume(code, "<`"), p.consume("<`"))
    } else if (code.startsWith("<")) {
      LtToken(p) #:: lex(consume(code, "<"), p.consume("<"))
    } else if (code.startsWith(">")) {
      GtToken(p) #:: lex(consume(code, ">"), p.consume(">"))
    } else if (code.startsWith("+^")) {
      PlusCaretToken(p) #:: lex(consume(code, "+^"), p.consume("+^"))
    } else if (code.startsWith("+%`")) {
      PlusPercentTickToken(p) #:: lex(consume(code, "+%`"), p.consume("+%`"))
    } else if (code.startsWith("+%")) {
      PlusPercentToken(p) #:: lex(consume(code, "+%"), p.consume("+%"))
    } else if (code.startsWith("+`")) {
      PlusTickToken(p) #:: lex(consume(code, "+`"), p.consume("+`"))
    } else if (code.startsWith("+")) {
      PlusToken(p) #:: lex(consume(code, "+"), p.consume("+"))
    } else if (code.startsWith("-^")) {
      MinusCaretToken(p) #:: lex(consume(code, "-^"), p.consume("-^"))
    } else if (code.startsWith("-%`")) {
      MinusPercentTickToken(p) #:: lex(consume(code, "-%`"), p.consume("-%`"))
    } else if (code.startsWith("-%")) {
      MinusPercentToken(p) #:: lex(consume(code, "-%"), p.consume("-%"))
    } else if (code.startsWith("-")) {
      MinusToken(p) #:: lex(consume(code, "-"), p.consume("-"))
    } else if (code.startsWith("*^")) {
      TimesCaretToken(p) #:: lex(consume(code, "*^"), p.consume("*^"))
    } else if (code.startsWith("*%`")) {
      TimesPercentTickToken(p) #:: lex(consume(code, "*%`"), p.consume("*%`"))
    } else if (code.startsWith("*%")) {
      TimesPercentToken(p) #:: lex(consume(code, "*%"), p.consume("*%"))
    } else if (code.startsWith("*`")) {
      TimesTickToken(p) #:: lex(consume(code, "*`"), p.consume("*`"))
    } else if (code.startsWith("*")) {
      TimesToken(p) #:: lex(consume(code, "*"), p.consume("*"))
    } else if (code.startsWith("/`")) {
      SlashTickToken(p) #:: lex(consume(code, "/`"), p.consume("/`"))
    } else if (code.startsWith("/")) {
      SlashToken(p) #:: lex(consume(code, "/"), p.consume("/"))
    } else if (code.startsWith("%`")) {
      PercentTickToken(p) #:: lex(consume(code, "%`"), p.consume("%`"))
    } else if (code.startsWith("%")) {
      PercentToken(p) #:: lex(consume(code, "%"), p.consume("%"))
    } else if (code.startsWith("!")) {
      BangToken(p) #:: lex(consume(code, "!"), p.consume("!"))
    } else if (code.startsWith("~")) {
      TildeToken(p) #:: lex(consume(code, "~"), p.consume("~"))
    } else if (code.startsWith(".")) {
      DotToken(p) #:: lex(consume(code, "."), p.consume("."))
    } else if (code.startsWith(",")) {
      CommaToken(p) #:: lex(consume(code, ","), p.consume(","))
    } else if (code.startsWith("@")) {
      AtToken(p) #:: lex(consume(code, "@"), p.consume("@"))
    } else {
      throw SyntaxError(s"unexpected character: '${code.head}'", p)
    }
  }

  @tailrec
  private def lexIdentifierOrKeyword(
      code: Stream[Char],
      start: SourcePoint,
      here: SourcePoint,
      ident: StringBuilder
  ): (Token, Stream[Char], SourcePoint) = {
    code.headOption match {
      case Some(c) if c == '_' || c.isLetterOrDigit =>
        ident.append(code.head)
        lexIdentifierOrKeyword(code.tail, start, here.moveRightBy(1), ident)
      case _ =>
        val token = ident.toString() match {
          case "if"          => IfToken(start)
          case "iff"         => IffToken(start)
          case "then"        => ThenToken(start)
          case "else"        => ElseToken(start)
          case "letstm"      => LetStmToken(start)
          case "let"         => LetToken(start)
          case "in"          => InToken(start)
          case "vbuild"      => VbuildToken(start)
          case "sbuild"      => SbuildToken(start)
          case "sdata"       => SdataToken(start)
          case "undefined"   => UndefinedToken(start)
          case "default"     => DefaultToken(start)
          case "true"        => TrueToken(start)
          case "false"       => FalseToken(start)
          case "init"        => InitToken(start)
          case "next"        => NextToken(start)
          case "stm"         => LittleStmToken(start)
          case "Stm"         => BigStmToken(start)
          case "Vec"         => VecToken(start)
          case "ready"       => ReadyToken(start)
          case "bool"        => BoolToken(start)
          case "exit"        => ExitToken(start)
          case "accelerator" => AcceleratorToken(start)
          case "const"       => ConstToken(start)
          case "assert"      => AssertToken(start)
          case "yields"      => YieldsToken(start)
          case "ignoring"    => IgnoringToken(start)
          case x if x.matches("u[0-9]+") =>
            val suffix = x.tail
            UIntToken(suffix.toInt)(start)
          case x if x.matches("i[0-9]+") =>
            val suffix = x.tail
            SIntToken(suffix.toInt)(start)
          case x => IdentToken(x)(start)
        }
        (token, code, here)
    }
  }

  @tailrec
  private def lexNumber(
      code: Stream[Char],
      start: SourcePoint,
      here: SourcePoint,
      num: StringBuilder
  ): (Token, Stream[Char], SourcePoint) = {
    code.headOption match {
      case Some('_') =>
        lexNumber(code.tail, start, here.moveRightBy(1), num)
      case Some(c) if c.isDigit =>
        num.append(code.head)
        lexNumber(code.tail, start, here.moveRightBy(1), num)
      case _ => (NatToken(num.toString.toLong)(num.toString, start), code, here)
    }
  }

  private def consume(code: Stream[Char], prefix: String): Stream[Char] = {
    assert(code.startsWith(prefix))
    code.drop(prefix.length)
  }

  private def consumeSingleLineComment(
      code: Stream[Char],
      pt: SourcePoint
  ): (Stream[Char], SourcePoint) = {
    @tailrec
    def consumeComment(
        code: Stream[Char],
        pt: SourcePoint
    ): (Stream[Char], SourcePoint) = {
      if (code.startsWith("\n")) {
        (code.tail, pt.moveDown())
      } else {
        consumeComment(code.tail, pt.moveRightBy(1))
      }
    }
    consumeComment(code, pt)
  }

  private def consumeMultiLineComment(
      code: Stream[Char],
      pt: SourcePoint
  ): (Stream[Char], SourcePoint) = {
    @tailrec
    def consumeComment(
        code: Stream[Char],
        pt: SourcePoint,
        lvl: Int
    ): (Stream[Char], SourcePoint) = {
      if (lvl == 0) {
        (code, pt)
      } else if (code.startsWith("/*")) {
        consumeComment(consume(code, "/*"), pt.consume("/*"), lvl = lvl + 1)
      } else if (code.startsWith("*/")) {
        consumeComment(consume(code, "*/"), pt.consume("*/"), lvl = lvl - 1)
      } else if (code.startsWith("\n")) {
        consumeComment(code.tail, pt.moveDown(), lvl)
      } else if (code.isEmpty) {
        throw SyntaxError("unclosed multiline comment", pt)
      } else {
        consumeComment(code.tail, pt.moveRightBy(1), lvl)
      }
    }
    consumeComment(consume(code, "/*"), pt.consume("/*"), lvl = 1)
  }

  @tailrec
  private def consumeWhitespace(
      code: Stream[Char],
      pt: SourcePoint
  ): (Stream[Char], SourcePoint) = {
    code.headOption match {
      case Some('\n') =>
        consumeWhitespace(code.tail, pt.moveDown())
      case Some(c) if c.isWhitespace =>
        consumeWhitespace(code.tail, pt.moveRightBy(1))
      case _ => (code, pt)
    }
  }
}
