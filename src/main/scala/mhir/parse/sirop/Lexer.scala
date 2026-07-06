package mhir.parse.sirop

import mhir.parse.{SourcePoint, SyntaxError}
import os.Path

import scala.annotation.tailrec
import scala.collection.immutable.Queue
import scala.io.Source

object Lexer {

  def lex(f: Path): Seq[Token] = {
    lex(Source.fromFile(f.toIO).toStream, Queue(), SourcePoint(1, 1))
  }

  def lex(code: String): Seq[Token] = {
    lex(code.toStream, Queue(), SourcePoint(1, 1))
  }

  @tailrec
  private def lex(
      src: Stream[Char],
      tokens: Queue[Token],
      p: SourcePoint
  ): Seq[Token] = {
    if (src.isEmpty) {
      return tokens
    }
    val head = src.head
    val tail = src.tail

    head match {
      case c if c.isWhitespace =>
        val (newCode, newPt) = consumeWhitespace(src, p)
        lex(newCode, tokens, newPt)
      case c if c.isLetter || c == '_' =>
        val (tok, newSrc, newPt) =
          lexIdentifierOrKeyword(
            tail,
            p,
            p.moveRightBy(1),
            ident = head.toString
          )
        lex(newSrc, tokens :+ tok, newPt)
      case c if c.isDigit =>
        val (tok, newSrc, newPt) =
          lexNumber(tail, p, p.moveRightBy(1), num = head.toString)
        lex(newSrc, tokens :+ tok, newPt)
      case ',' =>
        lex(tail, tokens :+ CommaToken(p), p.moveRightBy(1))
      case '.' =>
        lex(tail, tokens :+ DotToken(p), p.moveRightBy(1))
      case '/' =>
        tail.headOption match {
          case Some('/') =>
            lex(consumeSingleLineComment(tail.tail), tokens, p.moveDown())
          case Some('*') =>
            val (newSrc, newPt) =
              consumeMultiLineComment(tail.tail, p.moveRightBy(2))
            lex(newSrc, tokens, newPt)
          case Some('`') =>
            lex(tail.tail, tokens :+ SlashTickToken(p), p.moveRightBy(2))
          case _ =>
            lex(tail, tokens :+ SlashToken(p), p.moveRightBy(1))
        }
      case '(' =>
        lex(tail, tokens :+ LeftParToken(p), p.moveRightBy(1))
      case ')' =>
        lex(tail, tokens :+ RightParToken(p), p.moveRightBy(1))
      case '{' =>
        lex(tail, tokens :+ LeftCurlyToken(p), p.moveRightBy(1))
      case '}' =>
        lex(tail, tokens :+ RightCurlyToken(p), p.moveRightBy(1))
      case ':' =>
        tail.headOption match {
          case Some('[') =>
            lex(tail.tail, tokens :+ ColonLeftSquareToken(p), p.moveRightBy(2))
          case _ =>
            lex(tail, tokens :+ ColonToken(p), p.moveRightBy(1))
        }
      case '[' =>
        lex(tail, tokens :+ LeftSquareToken(p), p.moveRightBy(1))
      case ']' =>
        tail.headOption match {
          case Some('v') =>
            lex(tail.tail, tokens :+ RightSquareVToken(p), p.moveRightBy(2))
          case Some('s') =>
            lex(tail.tail, tokens :+ RightSquareSToken(p), p.moveRightBy(2))
          case _ =>
            lex(tail, tokens :+ RightSquareToken(p), p.moveRightBy(1))
        }
      case '=' =>
        tail.headOption match {
          case Some('>') =>
            lex(tail.tail, tokens :+ DoubleArrowToken(p), p.moveRightBy(2))
          case Some('=') =>
            val tail2 = tail.tail
            tail2.headOption match {
              case Some('`') =>
                lex(tail2.tail, tokens :+ EqTickToken(p), p.moveRightBy(3))
              case _ =>
                lex(tail2, tokens :+ EqToken(p), p.moveRightBy(2))
            }
          case _ =>
            lex(tail, tokens :+ AssignToken(p), p.moveRightBy(1))
        }
      case '!' =>
        tail.headOption match {
          case Some('=') =>
            lex(tail.tail, tokens :+ NeqToken(p), p.moveRightBy(2))
          case _ =>
            lex(tail, tokens :+ BangToken(p), p.moveRightBy(1))
        }
      case '|' =>
        tail.headOption match {
          case Some('|') =>
            lex(tail.tail, tokens :+ LogOrToken(p), p.moveRightBy(2))
          case _ =>
            lex(tail, tokens :+ BitOrToken(p), p.moveRightBy(1))
        }
      case '&' =>
        tail.headOption match {
          case Some('&') =>
            lex(tail.tail, tokens :+ LogAndToken(p), p.moveRightBy(2))
          case _ =>
            lex(tail, tokens :+ BitAndToken(p), p.moveRightBy(1))
        }
      case '<' =>
        tail.headOption match {
          case Some('<') =>
            lex(tail.tail, tokens :+ LShiftToken(p), p.moveRightBy(2))
          case Some('=') =>
            lex(tail.tail, tokens :+ LeqToken(p), p.moveRightBy(2))
          case Some('`') =>
            lex(tail.tail, tokens :+ LtTickToken(p), p.moveRightBy(2))
          case _ =>
            lex(tail, tokens :+ LtToken(p), p.moveRightBy(1))
        }
      case '>' =>
        tail.headOption match {
          case Some('=') =>
            lex(tail.tail, tokens :+ GeqToken(p), p.moveRightBy(2))
          case Some('>') =>
            val tail2 = tail.tail
            tail2.headOption match {
              case Some('>') =>
                lex(tail2.tail, tokens :+ LRShiftToken(p), p.moveRightBy(3))
              case _ =>
                lex(tail2, tokens :+ ARShiftToken(p), p.moveRightBy(2))
            }
          case _ =>
            lex(tail, tokens :+ GtToken(p), p.moveRightBy(1))
        }
      case '+' =>
        tail.headOption match {
          case Some('`') =>
            lex(tail.tail, tokens :+ PlusTickToken(p), p.moveRightBy(2))
          case Some('^') =>
            lex(tail.tail, tokens :+ PlusCaretToken(p), p.moveRightBy(2))
          case Some('%') =>
            val tail2 = tail.tail
            tail2.headOption match {
              case Some('`') =>
                lex(
                  tail2.tail,
                  tokens :+ PlusPercentTickToken(p),
                  p.moveRightBy(3)
                )
              case _ =>
                lex(tail2, tokens :+ PlusPercentToken(p), p.moveRightBy(2))
            }
          case _ =>
            lex(tail, tokens :+ PlusToken(p), p.moveRightBy(1))
        }
      case '-' =>
        tail.headOption match {
          case Some('>') =>
            lex(tail.tail, tokens :+ SingleArrowToken(p), p.moveRightBy(2))
          case Some('^') =>
            lex(tail.tail, tokens :+ MinusCaretToken(p), p.moveRightBy(2))
          case Some('%') =>
            val tail2 = tail.tail
            tail2.headOption match {
              case Some('`') =>
                lex(
                  tail2.tail,
                  tokens :+ MinusPercentTickToken(p),
                  p.moveRightBy(3)
                )
              case _ =>
                lex(tail2, tokens :+ MinusPercentToken(p), p.moveRightBy(2))
            }
          case _ =>
            lex(tail, tokens :+ MinusToken(p), p.moveRightBy(1))
        }
      case '*' =>
        tail.headOption match {
          case Some('`') =>
            lex(tail.tail, tokens :+ TimesTickToken(p), p.moveRightBy(2))
          case Some('^') =>
            lex(tail.tail, tokens :+ TimesCaretToken(p), p.moveRightBy(2))
          case Some('%') =>
            val tail2 = tail.tail
            tail2.headOption match {
              case Some('`') =>
                lex(
                  tail2.tail,
                  tokens :+ TimesPercentTickToken(p),
                  p.moveRightBy(3)
                )
              case _ =>
                lex(tail2, tokens :+ TimesPercentToken(p), p.moveRightBy(2))
            }
          case _ =>
            lex(tail, tokens :+ TimesToken(p), p.moveRightBy(1))
        }
      case '%' =>
        tail.headOption match {
          case Some('`') =>
            lex(tail.tail, tokens :+ PercentTickToken(p), p.moveRightBy(2))
          case _ =>
            lex(tail, tokens :+ PercentToken(p), p.moveRightBy(1))
        }
      case '~' =>
        lex(tail, tokens :+ TildeToken(p), p.moveRightBy(1))
      case '@' =>
        lex(tail, tokens :+ AtToken(p), p.moveRightBy(1))
      case c =>
        throw SyntaxError(s"unexpected character: '$c'", p)
    }
  }

  @tailrec
  private def lexIdentifierOrKeyword(
      code: Stream[Char],
      start: SourcePoint,
      here: SourcePoint,
      ident: String
  ): (Token, Stream[Char], SourcePoint) = {
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
      num: String
  ): (Token, Stream[Char], SourcePoint) = {
    code.headOption match {
      case Some('_') =>
        lexNumber(code.tail, start, here.moveRightBy(1), num)
      case Some(c) if c.isDigit =>
        lexNumber(code.tail, start, here.moveRightBy(1), num + code.head)
      case _ => (NatToken(num.toLong)(num, start), code, here)
    }
  }

  @tailrec
  private def consumeSingleLineComment(code: Stream[Char]): Stream[Char] = {
    code.headOption match {
      case Some('\n') => code.tail
      case None       => code
      case _          => consumeSingleLineComment(code.tail)
    }
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
      } else {
        code.headOption match {
          case Some('/') =>
            val tail = code.tail
            tail.headOption match {
              case Some('*') =>
                consumeComment(tail.tail, pt.moveRightBy(2), lvl = lvl + 1)
              case _ =>
                consumeComment(tail, pt.moveRightBy(1), lvl)
            }
          case Some('*') =>
            val tail = code.tail
            tail.headOption match {
              case Some('/') =>
                consumeComment(tail.tail, pt.moveRightBy(2), lvl = lvl - 1)
              case _ =>
                consumeComment(tail, pt.moveRightBy(1), lvl)
            }
          case Some('\n') =>
            consumeComment(code.tail, pt.moveDown(), lvl)
          case None =>
            throw SyntaxError("unclosed multiline comment", pt)
          case _ =>
            consumeComment(code.tail, pt.moveRightBy(1), lvl)
        }
      }
    }
    consumeComment(code, pt, lvl = 1)
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
