package mhir.parse.sirop

import mhir.ir.{TyAnyInt, TySInt, TyUInt}
import mhir.parse.SourcePoint

sealed trait TokenCategory {
  def name: String
}

sealed abstract class KeywordCategory(keyword: String) extends TokenCategory {
  override def name: String = s"keyword '$keyword'"
}

sealed abstract class SymbolCategory(symbol: String) extends TokenCategory {
  override def name: String = s"'$symbol'"
}

/** A token produced by the lexer.
  */
sealed trait Token {

  /** The location of the first character in the token within the source code.
    */
  def loc: SourcePoint

  def category: TokenCategory

  /** The token as it appears in the source code.
    */
  def original: String

  /** The token as it appears in the source code, plus quotes.
    */
  def quot: String = "'" + this.original + "'"
}

/** An identifier.
  */
case class IdentToken(ident: String)(val loc: SourcePoint) extends Token {
  override def category: TokenCategory = IdentToken
  override def original: String = ident
}

/** Category of [[IdentToken]].
  */
object IdentToken extends TokenCategory {
  override def name: String = "an identifier"
}

/** A natural number.
  */
case class NatToken(n: Long)(val original: String, val loc: SourcePoint)
    extends Token {
  override def category: TokenCategory = NatToken
}

/** Category of [[NatToken]].
  */
object NatToken extends TokenCategory {
  override def name: String = "a number"
}

// Keywords --------------------------------------------------------------------

/** The keyword "if".
  */
case class IfToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = IfToken
  override def original: String = "if"
}

/** Category of [[IfToken]].
  */
object IfToken extends KeywordCategory("if")

/** The keyword "then".
  */
case class ThenToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = ThenToken
  override def original: String = "then"
}

/** Category of [[ThenToken]].
  */
object ThenToken extends KeywordCategory("then")

/** The keyword "else".
  */
case class ElseToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = ElseToken
  override def original: String = "else"
}

/** Category of [[ElseToken]].
  */
object ElseToken extends KeywordCategory("else")

/** The keyword "letstm".
  */
case class LetStmToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LetStmToken
  override def original: String = "letstm"
}

/** Category of [[LetStmToken]].
  */
object LetStmToken extends KeywordCategory("letstm")

/** The keyword "let".
  */
case class LetToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LetToken
  override def original: String = "let"
}

/** Category of [[LetToken]].
  */
object LetToken extends KeywordCategory("let")

/** The keyword "in".
  */
case class InToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = InToken
  override def original: String = "in"
}

/** Category of [[InToken]].
  */
object InToken extends KeywordCategory("in")

/** The keyword "vbuild".
  */
case class VbuildToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = VbuildToken
  override def original: String = "vbuild"
}

/** Category of [[VbuildToken]].
  */
object VbuildToken extends KeywordCategory("vbuild")

/** The keyword "sbuild".
  */
case class SbuildToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = SbuildToken
  override def original: String = "sbuild"
}

/** Category of [[SbuildToken]].
  */
object SbuildToken extends KeywordCategory("sbuild")

/** The keyword "sdata".
  */
case class SdataToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = SdataToken
  override def original: String = "sdata"
}

/** Category of [[SdataToken]].
  */
object SdataToken extends KeywordCategory("sdata")

/** The keyword "undefined".
  */
case class UndefinedToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = UndefinedToken
  override def original: String = "undefined"
}

/** Category of [[UndefinedToken]].
  */
object UndefinedToken extends KeywordCategory("undefined")

/** The keyword "default".
  */
case class DefaultToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = DefaultToken
  override def original: String = "default"
}

/** Category of [[DefaultToken]].
  */
object DefaultToken extends KeywordCategory("default")

/** The keyword "true".
  */
case class TrueToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = TrueToken
  override def original: String = "true"
}

/** Category of [[TrueToken]].
  */
object TrueToken extends KeywordCategory("true")

/** The keyword "false".
  */
case class FalseToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = FalseToken
  override def original: String = "false"
}

/** Category of [[FalseToken]].
  */
object FalseToken extends KeywordCategory("false")

/** The keyword "init".
  */
case class InitToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = InitToken
  override def original: String = "init"
}

/** Category of [[InitToken]].
  */
object InitToken extends KeywordCategory("init")

/** The keyword "next".
  */
case class NextToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = NextToken
  override def original: String = "next"
}

/** Category of [[NextToken]].
  */
object NextToken extends KeywordCategory("next")

/** The keyword "stm".
  */
case class LittleStmToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LittleStmToken
  override def original: String = "stm"
}

/** Category of [[LittleStmToken]].
  */
object LittleStmToken extends KeywordCategory("stm")

/** The keyword "Stm".
  */
case class BigStmToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = BigStmToken
  override def original: String = "Stm"
}

/** Category of [[BigStmToken]].
  */
object BigStmToken extends KeywordCategory("Stm")

/** The keyword "Vec".
  */
case class VecToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = VecToken
  override def original: String = "Vec"
}

/** Category of [[VecToken]].
  */
object VecToken extends KeywordCategory("Vec")

/** The keyword "ready".
  */
case class ReadyToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = ReadyToken
  override def original: String = "ready"
}

/** Category of [[ReadyToken]].
  */
object ReadyToken extends KeywordCategory("ready")

/** The keyword "bool".
  */
case class BoolToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = BoolToken
  override def original: String = "bool"
}

/** Category of [[BoolToken]].
  */
object BoolToken extends KeywordCategory("bool")

/** The keyword "exit".
  */
case class ExitToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = ExitToken
  override def original: String = "exit"
}

/** Category of [[ExitToken]].
  */
object ExitToken extends KeywordCategory("exit")

/** The keyword "accelerator".
  */
case class AcceleratorToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = AcceleratorToken
  override def original: String = "accelerator"
}

/** Category of [[AcceleratorToken]].
  */
object AcceleratorToken extends KeywordCategory("accelerator")

/** The keyword "const".
  */
case class ConstToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = ConstToken
  override def original: String = "const"
}

/** Category of [[ConstToken]].
  */
object ConstToken extends KeywordCategory("const")

/** The keyword "assert".
  */
case class AssertToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = AssertToken
  override def original: String = "assert"
}

/** Category of [[AssertToken]].
  */
object AssertToken extends KeywordCategory("assert")

/** The keyword "yields".
  */
case class YieldsToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = YieldsToken
  override def original: String = "yields"
}

/** Category of [[YieldsToken]].
  */
object YieldsToken extends KeywordCategory("yields")

/** The keyword "ignoring".
  */
case class IgnoringToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = IgnoringToken
  override def original: String = "ignoring"
}

/** Category of [[IgnoringToken]].
  */
object IgnoringToken extends KeywordCategory("ignoring")

// Keywords with natural number suffixes ---------------------------------------

/** The letter "u" followed by a natural number.
  */
case class UIntToken(width: Int)(val loc: SourcePoint) extends Token {
  override def category: TokenCategory = UIntToken
  override def original: String = s"u$width"
}

/** Companion object for [[UIntToken]].
  */
object UIntToken extends TokenCategory {
  override def name: String = "an unsigned int type"
}

/** The letter "i" followed by a natural number.
  */
case class SIntToken(width: Int)(val loc: SourcePoint) extends Token {
  override def category: TokenCategory = SIntToken
  override def original: String = s"i$width"
}

/** Companion object for [[SIntToken]].
  */
object SIntToken extends TokenCategory {
  override def name: String = "a signed int type"
}

/** Extractor for integer type tokens.
  */
object IntToken {
  def unapply(token: Token): Option[TyAnyInt] = {
    token match {
      case UIntToken(w) => Some(TyUInt(w))
      case SIntToken(w) => Some(TySInt(w))
      case _            => None
    }
  }
}

// Parentheses -----------------------------------------------------------------

/** The symbol "(".
  */
case class LeftParToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LeftParToken
  override def original: String = "("
}

/** Category of [[LeftParToken]].
  */
object LeftParToken extends SymbolCategory("(")

/** The symbol ")".
  */
case class RightParToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = RightParToken
  override def original: String = ")"
}

/** Category of [[RightParToken]].
  */
object RightParToken extends SymbolCategory(")")

/** The symbol "{".
  */
case class LeftCurlyToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LeftCurlyToken
  override def original: String = "{"
}

/** Category of [[LeftCurlyToken]].
  */
object LeftCurlyToken extends SymbolCategory("{")

/** The symbol "}".
  */
case class RightCurlyToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = RightCurlyToken
  override def original: String = "}"
}

/** Category of [[RightCurlyToken]].
  */
object RightCurlyToken extends SymbolCategory("}")

/** The symbol ":[".
  */
case class ColonLeftSquareToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = ColonLeftSquareToken
  override def original: String = ":["
}

/** Category of [[ColonLeftSquareToken]].
  */
object ColonLeftSquareToken extends SymbolCategory(":[")

/** The symbol "[".
  */
case class LeftSquareToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LeftSquareToken
  override def original: String = "["
}

/** Category of [[LeftSquareToken]].
  */
object LeftSquareToken extends SymbolCategory("[")

/** The symbol "]v".
  */
case class RightSquareVToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = RightSquareVToken
  override def original: String = "]v"
}

/** Category of [[RightSquareVToken]].
  */
object RightSquareVToken extends SymbolCategory("]v")

/** The symbol "]s".
  */
case class RightSquareSToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = RightSquareSToken
  override def original: String = "]s"
}

/** Category of [[RightSquareSToken]].
  */
object RightSquareSToken extends SymbolCategory("]s")

/** The symbol "]".
  */
case class RightSquareToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = RightSquareToken
  override def original: String = "]"
}

/** Category of [[RightSquareToken]].
  */
object RightSquareToken extends SymbolCategory("]")

// Other symbols ---------------------------------------------------------------

/** The symbol "->".
  */
case class SingleArrowToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = SingleArrowToken
  override def original: String = "->"
}

/** Category of [[SingleArrowToken]].
  */
object SingleArrowToken extends SymbolCategory("->")

/** The symbol "=>".
  */
case class DoubleArrowToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = DoubleArrowToken
  override def original: String = "=>"
}

/** Category of [[DoubleArrowToken]].
  */
object DoubleArrowToken extends SymbolCategory("=>")

/** The symbol "==".
  */
case class EqToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = EqToken
  override def original: String = "=="
}

/** Category of [[EqToken]].
  */
object EqToken extends SymbolCategory("==")

/** The symbol "==&#96;".
  */
case class EqTickToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = EqTickToken
  override def original: String = "==`"
}

/** Category of [[EqTickToken]].
  */
object EqTickToken extends SymbolCategory("==`")

/** The symbol "!=".
  */
case class NeqToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = NeqToken
  override def original: String = "!="
}

/** Category of [[NeqToken]].
  */
object NeqToken extends SymbolCategory("!=")

/** The symbol "=".
  */
case class AssignToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = AssignToken
  override def original: String = "="
}

/** Category of [[AssignToken]].
  */
object AssignToken extends SymbolCategory("=")

/** The symbol ":".
  */
case class ColonToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = ColonToken
  override def original: String = ":"
}

object ColonToken extends SymbolCategory(":")

/** The symbol "||".
  */
case class LogOrToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LogOrToken
  override def original: String = "||"
}

/** Category of [[LogOrToken]].
  */
object LogOrToken extends SymbolCategory("||")

/** The symbol "|".
  */
case class BitOrToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = BitOrToken
  def original: String = "|"
}

/** Category of [[BitOrToken.]]
  */
object BitOrToken extends SymbolCategory("|")

/** The symbol "&&".
  */
case class LogAndToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LogAndToken
  override def original: String = "&&"
}

/** Category of [[LogAndToken]].
  */
object LogAndToken extends SymbolCategory("&&")

/** The symbol "&".
  */
case class BitAndToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = BitAndToken
  override def original: String = "&"
}

/** Category of [[BitAndToken]].
  */
object BitAndToken extends SymbolCategory("&")

/** The symbol "&lt;&lt;".
  */
case class LShiftToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LShiftToken
  override def original: String = "<<"
}

/** Category of [[LShiftToken]].
  */
object LShiftToken extends SymbolCategory("<<")

/** The symbol ">>".
  */
case class ARShiftToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LRShiftToken
  override def original: String = ">>"
}

/** Category of [[ARShiftToken]].
  */
object ARShiftToken extends SymbolCategory(">>>")

/** The symbol ">>>".
  */
case class LRShiftToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LRShiftToken
  override def original: String = ">>>"
}

/** Category of [[LRShiftToken]].
  */
object LRShiftToken extends SymbolCategory(">>>")

/** The symbol "&lt;=".
  */
case class LeqToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LeqToken
  override def original: String = "<="
}

/** Category of [[LeqToken]].
  */
object LeqToken extends SymbolCategory("<=")

/** The symbol ">=".
  */
case class GeqToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = GeqToken
  override def original: String = ">="
}

/** Category of [[GeqToken]].
  */
object GeqToken extends SymbolCategory(">=")

/** The symbol "&lt;".
  */
case class LtToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LtToken
  override def original: String = "<"
}

/** Category of [[LtToken]].
  */
object LtToken extends SymbolCategory("<")

/** The symbol "&lt;&#96;".
  */
case class LtTickToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = LtTickToken
  override def original: String = "<`"
}

/** Category of [[LtTickToken]].
  */
object LtTickToken extends SymbolCategory("<`")

/** The symbol ">".
  */
case class GtToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = GtToken
  override def original: String = ">"
}

/** Category of [[GtToken]].
  */
object GtToken extends SymbolCategory(">")

/** The symbol "+&#94;".
  */
case class PlusCaretToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = PlusCaretToken
  override def original: String = "+^"
}

/** Category of [[PlusCaretToken]].
  */
object PlusCaretToken extends SymbolCategory("+^")

/** The symbol "+%".
  */
case class PlusPercentToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = PlusPercentToken
  override def original: String = "+%"
}

/** Category of [[PlusPercentToken]].
  */
object PlusPercentToken extends SymbolCategory("+%")

/** The symbol "+%&#96;".
  */
case class PlusPercentTickToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = PlusPercentTickToken
  override def original: String = "+%`"
}

/** Category of [[PlusPercentTickToken]].
  */
object PlusPercentTickToken extends SymbolCategory("+%`")

/** The symbol "+&#96;".
  */
case class PlusTickToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = PlusTickToken
  override def original: String = "+`"
}

/** The category of [[PlusTickToken]].
  */
object PlusTickToken extends SymbolCategory("+`")

/** The symbol "+".
  */
case class PlusToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = PlusToken
  override def original: String = "+"
}

/** Category of [[PlusToken]].
  */
object PlusToken extends SymbolCategory("+")

/** The symbol "-&#94;".
  */
case class MinusCaretToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = MinusCaretToken
  override def original: String = "-^"
}

/** Category of [[MinusCaretToken]].
  */
object MinusCaretToken extends SymbolCategory("-^")

/** The symbol "-%&#96;".
  */
case class MinusPercentTickToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = MinusPercentTickToken
  override def original: String = "-%`"
}

/** Category of [[MinusPercentTickToken]].
  */
object MinusPercentTickToken extends SymbolCategory("-%`")

/** The symbol "-%".
  */
case class MinusPercentToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = MinusPercentToken
  override def original: String = "-%"
}

/** Category of [[MinusPercentToken]].
  */
object MinusPercentToken extends SymbolCategory("-%")

/** The symbol "-".
  */
case class MinusToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = MinusToken
  override def original: String = "-"
}

/** Category of [[MinusToken]].
  */
object MinusToken extends SymbolCategory("-")

/** The symbol "*&#94;".
  */
case class TimesCaretToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = TimesCaretToken
  override def original: String = "*^"
}

/** Category of [[TimesCaretToken]].
  */
object TimesCaretToken extends SymbolCategory("*^")

/** The symbol "*%&#96;".
  */
case class TimesPercentTickToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = TimesPercentTickToken
  override def original: String = "*%`"
}

/** Category of [[TimesPercentTickToken]].
  */
object TimesPercentTickToken extends SymbolCategory("*%`")

/** The symbol "*%".
  */
case class TimesPercentToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = TimesPercentToken
  override def original: String = "*%"
}

/** Category of [[TimesPercentToken]].
  */
object TimesPercentToken extends SymbolCategory("*%")

/** The symbol "*&#96;".
  */
case class TimesTickToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = TimesTickToken
  override def original: String = "*`"
}

/** Category of [[TimesTickToken]].
  */
object TimesTickToken extends SymbolCategory("*`")

/** The symbol "*".
  */
case class TimesToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = TimesToken
  override def original: String = "*"
}

/** Category of [[TimesToken]].
  */
object TimesToken extends SymbolCategory("*")

/** The symbol "/&#96;".
  */
case class SlashTickToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = SlashTickToken
  override def original: String = "/`"
}

/** Category of [[SlashTickToken]].
  */
object SlashTickToken extends SymbolCategory("/`")

/** The symbol "/".
  */
case class SlashToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = SlashToken
  override def original: String = "/"
}

/** Category of [[SlashToken]].
  */
object SlashToken extends SymbolCategory("/")

/** The symbol "%&#96;".
  */
case class PercentTickToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = PercentTickToken
  override def original: String = "%`"
}

/** Category of [[PercentTickToken]].
  */
object PercentTickToken extends SymbolCategory("%`")

/** The symbol "%".
  */
case class PercentToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = PercentToken
  override def original: String = "%"
}

/** Category of [[PercentToken]].
  */
object PercentToken extends SymbolCategory("%")

/** The symbol "!".
  */
case class BangToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = BangToken
  override def original: String = "!"
}

/** Category of [[BangToken]].
  */
object BangToken extends SymbolCategory("!")

/** The symbol "~".
  */
case class TildeToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = TildeToken
  override def original: String = "~"
}

/** Category of [[TildeToken]].
  */
object TildeToken extends SymbolCategory("~")

/** The symbol ".".
  */
case class DotToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = DotToken
  override def original: String = "."
}

/** Category of [[DotToken]].
  */
object DotToken extends SymbolCategory(".")

/** The symbol ",".
  */
case class CommaToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = CommaToken
  override def original: String = ","
}

/** Category of [[CommaToken]].
  */
object CommaToken extends SymbolCategory(",")

/** The symbol "@".
  */
case class AtToken(loc: SourcePoint) extends Token {
  override def category: TokenCategory = AtToken
  override def original: String = "@"
}

/** Category of [[AtToken]].
  */
object AtToken extends SymbolCategory("@")
