package mhir.parse.sirop

import mhir.parse.SyntaxError

import scala.annotation.tailrec
import scala.collection.immutable.Queue

object Lexer {

  def lex(code: String): Seq[Token] = lex(code, Queue())

  @tailrec
  private def lex(code: String, tokens: Queue[Token]): Seq[Token] = {
    if (code.isEmpty) {
      tokens
    }
    // Comments, whitespace ----------------------------------------------------
    else if (code.startsWith("/*")) {
      lex(consumeComment(code), tokens)
    } else if (code.head.isWhitespace) {
      lex(code.tail, tokens)
    }
    // Identifiers, keywords, numbers ------------------------------------------
    else if (code.head.isLetter || code.head == '_') {
      val (newCode, newToken) =
        lexIdentifierOrKeyword(code.tail, ident = code.head.toString)
      lex(newCode, tokens :+ newToken)
    } else if (code.head.isDigit) {
      val (newCode, newToken) = lexNumber(code.tail, num = code.head.toString)
      lex(newCode, tokens :+ newToken)
    }
    // Parentheses -------------------------------------------------------------
    else if (code.startsWith("(")) {
      lex(consume(code, "("), tokens :+ LeftParToken)
    } else if (code.startsWith(")")) {
      lex(consume(code, ")"), tokens :+ RightParToken)
    } else if (code.startsWith("{")) {
      lex(consume(code, "{"), tokens :+ LeftCurlyToken)
    } else if (code.startsWith("}")) {
      lex(consume(code, "}"), tokens :+ RightCurlyToken)
    } else if (code.startsWith("[")) {
      lex(consume(code, "["), tokens :+ LeftSquareToken)
    } else if (code.startsWith("]v")) {
      lex(consume(code, "]v"), tokens :+ RightSquareVToken)
    } else if (code.startsWith("]s")) {
      lex(consume(code, "]s"), tokens :+ RightSquareSToken)
    } else if (code.startsWith("]")) {
      lex(consume(code, "]"), tokens :+ RightSquareToken)
    }
    // Other symbols -----------------------------------------------------------
    else if (code.startsWith("->")) {
      lex(consume(code, "->"), tokens :+ SingleArrowToken)
    } else if (code.startsWith("=>")) {
      lex(consume(code, "=>"), tokens :+ DoubleArrowToken)
    } else if (code.startsWith("==")) {
      lex(consume(code, "=="), tokens :+ EqToken)
    } else if (code.startsWith("!=")) {
      lex(consume(code, "!="), tokens :+ NeqToken)
    } else if (code.startsWith("=")) {
      lex(consume(code, "="), tokens :+ AssignToken)
    } else if (code.startsWith(":")) {
      lex(consume(code, ":"), tokens :+ ColonToken)
    } else if (code.startsWith("||")) {
      lex(consume(code, "||"), tokens :+ LogOrToken)
    } else if (code.startsWith("&&")) {
      lex(consume(code, "&&"), tokens :+ LogAndToken)
    } else if (code.startsWith("<<<")) {
      lex(consume(code, "<<<"), tokens :+ LLShiftToken)
    } else if (code.startsWith(">>>")) {
      lex(consume(code, ">>>"), tokens :+ LRShiftToken)
    } else if (code.startsWith("<=")) {
      lex(consume(code, "<="), tokens :+ LeqToken)
    } else if (code.startsWith(">=")) {
      lex(consume(code, ">="), tokens :+ GeqToken)
    } else if (code.startsWith("<")) {
      lex(consume(code, "<"), tokens :+ LtToken)
    } else if (code.startsWith(">")) {
      lex(consume(code, ">"), tokens :+ GtToken)
    } else if (code.startsWith("+%")) {
      lex(consume(code, "+%"), tokens :+ PlusPercentToken)
    } else if (code.startsWith("+")) {
      lex(consume(code, "+"), tokens :+ PlusToken)
    } else if (code.startsWith("-%")) {
      lex(consume(code, "-%"), tokens :+ MinusPercentToken)
    } else if (code.startsWith("-")) {
      lex(consume(code, "-"), tokens :+ MinusToken)
    } else if (code.startsWith("*%")) {
      lex(consume(code, "*%"), tokens :+ TimesPercentToken)
    } else if (code.startsWith("*")) {
      lex(consume(code, "*"), tokens :+ TimesToken)
    } else if (code.startsWith("/")) {
      lex(consume(code, "/"), tokens :+ SlashToken)
    } else if (code.startsWith("%")) {
      lex(consume(code, "%"), tokens :+ PercentToken)
    } else if (code.startsWith("!")) {
      lex(consume(code, "!"), tokens :+ BangToken)
    } else if (code.startsWith(".")) {
      lex(consume(code, "."), tokens :+ DotToken)
    } else if (code.startsWith(",")) {
      lex(consume(code, ","), tokens :+ CommaToken)
    } else {
      throw new SyntaxError(s"unexpected character: '${code.head}'")
    }
  }

  @tailrec
  private def lexIdentifierOrKeyword(
      code: String,
      ident: String
  ): (String, Token) = {
    code.headOption match {
      case Some(c) if c == '_' || c.isLetterOrDigit =>
        lexIdentifierOrKeyword(code.tail, ident + code.head)
      case _ =>
        val token = ident match {
          case "if"        => IfToken
          case "then"      => ThenToken
          case "else"      => ElseToken
          case "letstm"    => LetStmToken
          case "in"        => InToken
          case "sign"      => SignToken
          case "unsign"    => UnsignToken
          case "vbuild"    => VbuildToken
          case "sbuild"    => SbuildToken
          case "sdata"     => SdataToken
          case "undefined" => UndefinedToken
          case "true"      => TrueToken
          case "false"     => FalseToken
          case "init"      => InitToken
          case "next"      => NextToken
          case "stm"       => LittleStmToken
          case "Stm"       => BigStmToken
          case "Vec"       => VecToken
          case "ready"     => ReadyToken
          case "bool"      => BoolToken
          case x if x.matches("pad[0-9]+") =>
            val suffix = consume(x, "pad")
            PadToken(suffix.toInt)
          case x if x.matches("truncate[0-9]+") =>
            val suffix = consume(x, "truncate")
            TruncateToken(suffix.toInt)
          case x if x.matches("u[0-9]+") =>
            val suffix = consume(x, "u")
            UIntToken(suffix.toInt)
          case x if x.matches("i[0-9]+") =>
            val suffix = consume(x, "i")
            SIntToken(suffix.toInt)
          case x => IdentToken(x)
        }
        (code, token)
    }
  }

  @tailrec
  private def lexNumber(code: String, num: String): (String, Token) = {
    code.headOption match {
      case Some('_')            => lexNumber(code.tail, num)
      case Some(c) if c.isDigit => lexNumber(code.tail, num + code.head)
      case _                    => (code, NatToken(num.toLong))
    }
  }

  private def consume(code: String, prefix: String): String = {
    assert(code.startsWith(prefix))
    code.substring(prefix.length)
  }

  private def consumeComment(code: String): String = {
    @tailrec
    def consumeComment(code: String, lvl: Int): String = {
      if (lvl == 0) {
        code
      } else if (code.startsWith("/*")) {
        consumeComment(consume(code, "/*"), lvl = lvl + 1)
      } else if (code.startsWith("*/")) {
        consumeComment(consume(code, "*/"), lvl = lvl - 1)
      } else {
        consumeComment(code.tail, lvl)
      }
    }
    consumeComment(consume(code, "/*"), lvl = 1)
  }
}
