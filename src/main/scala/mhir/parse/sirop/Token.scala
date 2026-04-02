package mhir.parse.sirop

import mhir.ir.{TyAnyInt, TySInt, TyUInt}

sealed trait TokenCategory

/** A token produced by the lexer.
  */
sealed trait Token {
  def category: TokenCategory

  /** The token as it appears in the source code.
    */
  def original: String

  /** The token as it appears in the source code, plus double quotes.
    */
  def quot: String = "\"" + this.original + "\""
}

/** An identifier.
  */
case class IdentToken(ident: String) extends Token {
  override def category: TokenCategory = IdentToken

  override def original: String = ident
}

/** Companion object for [[IdentToken]].
  */
object IdentToken extends TokenCategory

/** A natural number.
  */
case class NatToken(n: Long)(val original: String) extends Token {
  override def category: TokenCategory = NatToken
}

/** Companion object for [[NatToken]].
  */
object NatToken extends TokenCategory

// Keywords --------------------------------------------------------------------

/** The keyword "if".
  */
object IfToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "if"
}

/** The keyword "then".
  */
object ThenToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "then"
}

/** The keyword "else".
  */
object ElseToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "else"
}

/** The keyword "letstm".
  */
object LetStmToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "letstm"
}

/** The keyword "let".
  */
object LetToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "let"
}

/** The keyword "in".
  */
object InToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "in"
}

/** The keyword "sign".
  */
object SignToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "sign"
}

/** The keyword "unsign".
  */
object UnsignToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "unsign"
}

/** The keyword "vbuild".
  */
object VbuildToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "vbuild"
}

/** The keyword "sbuild".
  */
object SbuildToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "sbuild"
}

/** The keyword "sdata".
  */
object SdataToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "sdata"
}

/** The keyword "undefined".
  */
object UndefinedToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "undefined"
}

/** The keyword "true".
  */
object TrueToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "true"
}

/** The keyword "false".
  */
object FalseToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "false"
}

/** The keyword "init".
  */
object InitToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "init"
}

/** The keyword "next".
  */
object NextToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "next"
}

/** The keyword "stm".
  */
object LittleStmToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "stm"
}

/** The keyword "Stm".
  */
object BigStmToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "Stm"
}

/** The keyword "Vec".
  */
object VecToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "Vec"
}

/** The keyword "ready".
  */
object ReadyToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "ready"
}

/** The keyword "bool".
  */
object BoolToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "bool"
}

// Keywords with natural number suffixes ---------------------------------------

/** The word "pad" followed by a natural number.
  */
case class PadToken(width: Int) extends Token {
  override def category: TokenCategory = PadToken

  override def original: String = s"pad$width"
}

/** Companion object for [[PadToken]].
  */
object PadToken extends TokenCategory

/** The word "truncate" followed by a natural number.
  */
case class TruncateToken(width: Int) extends Token {
  override def category: TokenCategory = TruncateToken

  override def original: String = s"truncate$width"
}

/** Companion object for [[TruncateToken]].
  */
object TruncateToken extends TokenCategory

/** The letter "u" followed by a natural number.
  */
case class UIntToken(width: Int) extends Token {
  override def category: TokenCategory = UIntToken

  override def original: String = s"u$width"
}

/** Companion object for [[UIntToken]].
  */
object UIntToken extends TokenCategory

/** The letter "i" followed by a natural number.
  */
case class SIntToken(width: Int) extends Token {
  override def category: TokenCategory = SIntToken

  override def original: String = s"i$width"
}

/** Companion object for [[SIntToken]].
  */
object SIntToken extends TokenCategory

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
object LeftParToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "("
}

/** The symbol ")".
  */
object RightParToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = ")"
}

/** The symbol "{".
  */
object LeftCurlyToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "{"
}

/** The symbol "}".
  */
object RightCurlyToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "}"
}

/** The symbol "[".
  */
object LeftSquareToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "["
}

/** The symbol "]v".
  */
object RightSquareVToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "]v"
}

/** The symbol "]s".
  */
object RightSquareSToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "]s"
}

/** The symbol "]".
  */
object RightSquareToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "]"
}

// Other symbols ---------------------------------------------------------------

/** The symbol "->".
  */
object SingleArrowToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "->"
}

/** The symbol "=>".
  */
object DoubleArrowToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "=>"
}

/** The symbol "==".
  */
object EqToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "=="
}

/** The symbol "!=".
  */
object NeqToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "!="
}

/** The symbol "=".
  */
object AssignToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "="
}

/** The symbol ":".
  */
object ColonToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = ":"
}

/** The symbol "||".
  */
object LogOrToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "||"
}

/** The symbol "&&".
  */
object LogAndToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "&&"
}

/** The symbol "<<<".
  */
object LLShiftToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "<<<"
}

/** The symbol ">>>".
  */
object LRShiftToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = ">>>"
}

/** The symbol "<=".
  */
object LeqToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "<="
}

/** The symbol ">=".
  */
object GeqToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = ">="
}

/** The symbol "<".
  */
object LtToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "<"
}

/** The symbol ">".
  */
object GtToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = ">"
}

/** The symbol "+%".
  */
object PlusPercentToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "+%"
}

/** The symbol "+".
  */
object PlusToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "+"
}

/** The symbol "-%".
  */
object MinusPercentToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "-%"
}

/** The symbol "-".
  */
object MinusToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "-"
}

/** The symbol "*%".
  */
object TimesPercentToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "*%"
}

/** The symbol "*".
  */
object TimesToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "*"
}

/** The symbol "/".
  */
object SlashToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "/"
}

/** The symbol "%".
  */
object PercentToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "%"
}

/** The symbol "!".
  */
object BangToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "!"
}

/** The symbol ".".
  */
object DotToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = "."
}

/** The symbol ",".
  */
object CommaToken extends Token with TokenCategory {
  override def category: TokenCategory = this

  override def original: String = ","
}
