package mhir.parse.sirop

import mhir.parse.{SourcePoint, SyntaxError}

import scala.annotation.tailrec
import scala.collection.immutable.Queue

object Lexer {

  def lex(code: String): Seq[Token] = {
    lex(code, Queue(), SourcePoint(1, 1))
  }

  @tailrec
  private def lex(
      code: String,
      tokens: Queue[Token],
      p: SourcePoint
  ): Seq[Token] = {
    if (code.isEmpty) {
      tokens
    }
    // Comments, whitespace ----------------------------------------------------
    else if (code.startsWith("/*")) {
      val (newCode, newPt) = consumeComment(code, p)
      lex(newCode, tokens, newPt)
    } else if (code.head.isWhitespace) {
      val (newCode, newPt) = consumeWhitespace(code, p)
      lex(newCode, tokens, newPt)
    }
    // Identifiers, keywords, numbers ------------------------------------------
    else if (code.head.isLetter || code.head == '_') {
      val (tok, newCode, newPt) =
        lexIdentifierOrKeyword(
          code.tail,
          p,
          p.moveRightBy(1),
          ident = code.head.toString
        )
      lex(newCode, tokens :+ tok, newPt)
    } else if (code.head.isDigit) {
      val (tok, newCode, newPt) =
        lexNumber(code.tail, p, p.moveRightBy(1), num = code.head.toString)
      lex(newCode, tokens :+ tok, newPt)
    }
    // Parentheses -------------------------------------------------------------
    else if (code.startsWith("(")) {
      lex(consume(code, "("), tokens :+ LeftParToken(p), p.consume("("))
    } else if (code.startsWith(")")) {
      lex(consume(code, ")"), tokens :+ RightParToken(p), p.consume(")"))
    } else if (code.startsWith("{")) {
      lex(consume(code, "{"), tokens :+ LeftCurlyToken(p), p.consume("{"))
    } else if (code.startsWith("}")) {
      lex(consume(code, "}"), tokens :+ RightCurlyToken(p), p.consume("}"))
    } else if (code.startsWith("[")) {
      lex(consume(code, "["), tokens :+ LeftSquareToken(p), p.consume("["))
    } else if (code.startsWith("]v")) {
      lex(consume(code, "]v"), tokens :+ RightSquareVToken(p), p.consume("]v"))
    } else if (code.startsWith("]s")) {
      lex(consume(code, "]s"), tokens :+ RightSquareSToken(p), p.consume("]s"))
    } else if (code.startsWith("]")) {
      lex(consume(code, "]"), tokens :+ RightSquareToken(p), p.consume("]"))
    }
    // Other symbols -----------------------------------------------------------
    else if (code.startsWith("->")) {
      lex(consume(code, "->"), tokens :+ SingleArrowToken(p), p.consume("->"))
    } else if (code.startsWith("=>")) {
      lex(consume(code, "=>"), tokens :+ DoubleArrowToken(p), p.consume("=>"))
    } else if (code.startsWith("==")) {
      lex(consume(code, "=="), tokens :+ EqToken(p), p.consume("=="))
    } else if (code.startsWith("!=")) {
      lex(consume(code, "!="), tokens :+ NeqToken(p), p.consume("!="))
    } else if (code.startsWith("=")) {
      lex(consume(code, "="), tokens :+ AssignToken(p), p.consume("="))
    } else if (code.startsWith(":")) {
      lex(consume(code, ":"), tokens :+ ColonToken(p), p.consume(":"))
    } else if (code.startsWith("||")) {
      lex(consume(code, "||"), tokens :+ LogOrToken(p), p.consume("||"))
    } else if (code.startsWith("&&")) {
      lex(consume(code, "&&"), tokens :+ LogAndToken(p), p.consume("%%"))
    } else if (code.startsWith("<<<")) {
      lex(consume(code, "<<<"), tokens :+ LLShiftToken(p), p.consume("<<<"))
    } else if (code.startsWith(">>>")) {
      lex(consume(code, ">>>"), tokens :+ LRShiftToken(p), p.consume(">>>"))
    } else if (code.startsWith("<=")) {
      lex(consume(code, "<="), tokens :+ LeqToken(p), p.consume("<="))
    } else if (code.startsWith(">=")) {
      lex(consume(code, ">="), tokens :+ GeqToken(p), p.consume(">="))
    } else if (code.startsWith("<")) {
      lex(consume(code, "<"), tokens :+ LtToken(p), p.consume("<"))
    } else if (code.startsWith(">")) {
      lex(consume(code, ">"), tokens :+ GtToken(p), p.consume(">"))
    } else if (code.startsWith("+%")) {
      lex(consume(code, "+%"), tokens :+ PlusPercentToken(p), p.consume("+%"))
    } else if (code.startsWith("+")) {
      lex(consume(code, "+"), tokens :+ PlusToken(p), p.consume("+"))
    } else if (code.startsWith("-%")) {
      lex(consume(code, "-%"), tokens :+ MinusPercentToken(p), p.consume("-%"))
    } else if (code.startsWith("-")) {
      lex(consume(code, "-"), tokens :+ MinusToken(p), p.consume("-"))
    } else if (code.startsWith("*%")) {
      lex(consume(code, "*%"), tokens :+ TimesPercentToken(p), p.consume("*%"))
    } else if (code.startsWith("*")) {
      lex(consume(code, "*"), tokens :+ TimesToken(p), p.consume("*"))
    } else if (code.startsWith("/")) {
      lex(consume(code, "/"), tokens :+ SlashToken(p), p.consume("/"))
    } else if (code.startsWith("%")) {
      lex(consume(code, "%"), tokens :+ PercentToken(p), p.consume("%"))
    } else if (code.startsWith("!")) {
      lex(consume(code, "!"), tokens :+ BangToken(p), p.consume("!"))
    } else if (code.startsWith(".")) {
      lex(consume(code, "."), tokens :+ DotToken(p), p.consume("."))
    } else if (code.startsWith(",")) {
      lex(consume(code, ","), tokens :+ CommaToken(p), p.consume(","))
    } else {
      throw SyntaxError(s"unexpected character: '${code.head}'", p)
    }
  }

  @tailrec
  private def lexIdentifierOrKeyword(
      code: String,
      start: SourcePoint,
      here: SourcePoint,
      ident: String
  ): (Token, String, SourcePoint) = {
    code.headOption match {
      case Some(c) if c == '_' || c.isLetterOrDigit =>
        lexIdentifierOrKeyword(
          code.tail,
          start,
          here.moveRightBy(1),
          ident + code.head
        )
      case _ =>
        val token = ident match {
          case "if"          => IfToken(start)
          case "then"        => ThenToken(start)
          case "else"        => ElseToken(start)
          case "letstm"      => LetStmToken(start)
          case "let"         => LetToken(start)
          case "in"          => InToken(start)
          case "sign"        => SignToken(start)
          case "unsign"      => UnsignToken(start)
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
          case x if x.matches("pad[0-9]+") =>
            val suffix = consume(x, "pad")
            PadToken(suffix.toInt)(start)
          case x if x.matches("truncate[0-9]+") =>
            val suffix = consume(x, "truncate")
            TruncateToken(suffix.toInt)(start)
          case x if x.matches("u[0-9]+") =>
            val suffix = consume(x, "u")
            UIntToken(suffix.toInt)(start)
          case x if x.matches("i[0-9]+") =>
            val suffix = consume(x, "i")
            SIntToken(suffix.toInt)(start)
          case x => IdentToken(x)(start)
        }
        (token, code, here)
    }
  }

  @tailrec
  private def lexNumber(
      code: String,
      start: SourcePoint,
      here: SourcePoint,
      num: String
  ): (Token, String, SourcePoint) = {
    code.headOption match {
      case Some('_') =>
        lexNumber(code.tail, start, here.moveRightBy(1), num)
      case Some(c) if c.isDigit =>
        lexNumber(code.tail, start, here.moveRightBy(1), num + code.head)
      case _ => (NatToken(num.toLong)(num, start), code, here)
    }
  }

  private def consume(code: String, prefix: String): String = {
    assert(code.startsWith(prefix))
    code.substring(prefix.length)
  }

  private def consumeComment(
      code: String,
      pt: SourcePoint
  ): (String, SourcePoint) = {
    @tailrec
    def consumeComment(
        code: String,
        pt: SourcePoint,
        lvl: Int
    ): (String, SourcePoint) = {
      if (lvl == 0) {
        (code, pt)
      } else if (code.startsWith("/*")) {
        consumeComment(consume(code, "/*"), pt.consume("/*"), lvl = lvl + 1)
      } else if (code.startsWith("*/")) {
        consumeComment(consume(code, "*/"), pt.consume("*/"), lvl = lvl - 1)
      } else if (code.startsWith("\n")) {
        consumeComment(code.tail, pt.moveDown(), lvl)
      } else {
        consumeComment(code.tail, pt.moveRightBy(1), lvl)
      }
    }
    consumeComment(consume(code, "/*"), pt.consume("/*"), lvl = 1)
  }

  @tailrec
  private def consumeWhitespace(
      code: String,
      pt: SourcePoint
  ): (String, SourcePoint) = {
    code.headOption match {
      case Some('\n') =>
        consumeWhitespace(code.tail, pt.moveDown())
      case Some(c) if c.isWhitespace =>
        consumeWhitespace(code.tail, pt.moveRightBy(1))
      case _ => (code, pt)
    }
  }
}
