package mhir.parse.sirop

/** A token produced by the lexer.
  */
sealed trait Token

/** An identifier.
  */
case class IdentToken(ident: String) extends Token

/** A natural number.
  */
case class NatToken(n: Long) extends Token

// Keywords --------------------------------------------------------------------

/** The keyword "if".
  */
object IfToken extends Token

/** The keyword "then".
  */
object ThenToken extends Token

/** The keyword "else".
  */
object ElseToken extends Token

/** The keyword "letstm".
  */
object LetStmToken extends Token

/** The keyword "in".
  */
object InToken extends Token

/** The keyword "sign".
  */
object SignToken extends Token

/** The keyword "unsign".
  */
object UnsignToken extends Token

/** The keyword "vbuild".
  */
object VbuildToken extends Token

/** The keyword "sbuild".
  */
object SbuildToken extends Token

/** The keyword "sdata".
  */
object SdataToken extends Token

/** The keyword "undefined".
  */
object UndefinedToken extends Token

/** The keyword "true".
  */
object TrueToken extends Token

/** The keyword "false".
  */
object FalseToken extends Token

/** The keyword "init".
  */
object InitToken extends Token

/** The keyword "next".
  */
object NextToken extends Token

/** The keyword "stm".
  */
object LittleStmToken extends Token

/** The keyword "Stm".
  */
object BigStmToken extends Token

/** The keyword "Vec".
  */
object VecToken extends Token

/** The keyword "ready".
  */
object ReadyToken extends Token

/** The keyword "bool".
  */
object BoolToken extends Token

// Keywords with natural number suffixes ---------------------------------------

/** The word "pad" followed by a natural number.
  */
case class PadToken(width: Int) extends Token

/** The word "truncate" followed by a natural number.
  */
case class TruncateToken(width: Int) extends Token

/** The letter "u" followed by a natural number.
  */
case class UIntToken(width: Int) extends Token

/** The letter "i" followed by a natural number.
  */
case class SIntToken(width: Int) extends Token

// Parentheses -----------------------------------------------------------------

/** The symbol "(".
  */
object LeftParToken extends Token

/** The symbol ")".
  */
object RightParToken extends Token

/** The symbol "{".
  */
object LeftCurlyToken extends Token

/** The symbol "}".
  */
object RightCurlyToken extends Token

/** The symbol "[".
  */
object LeftSquareToken extends Token

/** The symbol "]v".
  */
object RightSquareVToken extends Token

/** The symbol "]s".
  */
object RightSquareSToken extends Token

/** The symbol "]".
  */
object RightSquareToken extends Token

// Other symbols ---------------------------------------------------------------

/** The symbol "==".
  */
object EqToken extends Token

/** The symbol "!=".
  */
object NeqToken extends Token

/** The symbol "=".
  */
object AssignToken extends Token

/** The symbol ":".
  */
object ColonToken extends Token

/** The symbol "->".
  */
object SingleArrowToken extends Token

/** The symbol "=>".
  */
object DoubleArrowToken extends Token

/** The symbol "||".
  */
object LogOrToken extends Token

/** The symbol "&&".
  */
object LogAndToken extends Token

/** The symbol "<<<".
  */
object LLShiftToken extends Token

/** The symbol ">>>".
  */
object LRShiftToken extends Token

/** The symbol "<=".
  */
object LeqToken extends Token

/** The symbol ">=".
  */
object GeqToken extends Token

/** The symbol "<".
  */
object LtToken extends Token

/** The symbol ">".
  */
object GtToken extends Token

/** The symbol "+%".
  */
object PlusPercentToken extends Token

/** The symbol "+".
  */
object PlusToken extends Token

/** The symbol "-%".
  */
object MinusPercentToken extends Token

/** The symbol "-".
  */
object MinusToken extends Token

/** The symbol "*%".
  */
object TimesPercentToken extends Token

/** The symbol "*".
  */
object TimesToken extends Token

/** The symbol "/".
  */
object SlashToken extends Token

/** The symbol "%".
  */
object PercentToken extends Token

/** The symbol "!".
  */
object BangToken extends Token

/** The symbol ".".
  */
object DotToken extends Token

/** The symbol ",".
  */
object CommaToken extends Token
