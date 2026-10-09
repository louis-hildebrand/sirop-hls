"""
Syntax highlighting for Sirop source code.
"""

from pygments.lexer import RegexLexer, include, default
from pygments import token

class SiropLexer(RegexLexer):
    """
    Class that provides syntax highlighting for Sirop source code with Pygments.
    """

    name = "Sirop"
    aliases = ["sirop"]
    filenames = ["*.sirop"]

    tokens = {
        "root": [
            # Check for a prompt; if there is none, treat the text as a full
            # Sirop program
            default(("multi_line_expr", "checkprompt"))
        ],
        # When we reach a new line, we may need to switch modes (e.g.,
        # previously we were ignoring compiler outputs, now we might need to
        # start reading REPL inputs).
        # This state handles that.
        "checkprompt": [
            # For lines starting with '$ ', interpret the rest of the line as
            # a CLI command
            (r"\$( |$)", token.Generic.Output, ("#pop", "#pop", "cli")),
            # For lines starting with '> ', interpret the rest of the line as
            # an expression
            (r">( |$)", token.Generic.Output, ("#pop", "#pop", "single_line_expr")),
            # diff output, e.g.,
            #
            #     $ diff expected.txt actual.txt
            #     3c3
            #     <   70:u16
            #     ---
            #     >   36:u16
            (r"---\n>", token.Generic.Output, ("#pop", "#pop", "output")),
            # If there are no $ or > prompts, stay in the same state
            default(("#pop")),
        ],
        "output": [
            # Check for a prompt; if there is none, continue treating the text as output
            (r"\n", token.Whitespace, "checkprompt"),
            # pylint: disable=fixme
            # TODO: consume many at once (also in the other states)?
            # It seems like writing .+ also matches the newline, despite
            # re.DOTALL not being set as far as I can tell.
            (r".", token.Generic.Output),
        ],
        "cli": [
            (r"#", token.Comment, ("#pop", "clicomment")),
            # Check for a prompt; if there is none, treat the text as output
            (r"\n", token.Whitespace, ("output", "checkprompt")),
            (r".", token.Text),
        ],
        "clicomment": [
            # Check for a prompt; if there is none, treat the text as output
            (r"\n", token.Whitespace, ("output", "checkprompt")),
            (r".", token.Comment),
        ],
        "single_line_expr": [
            include("expr"),
            # Check for a prompt; if there is none, treat the next line as output
            (r"\n", token.Whitespace, ("output", "checkprompt")),
            (r".", token.Text)
        ],
        "multi_line_expr": [
            include("expr"),
            # Check for a prompt; if there is none, continue treating the text
            # as a Sirop program
            (r"\n", token.Whitespace, "checkprompt"),
            (r".", token.Text)
        ],
        "expr": [
            # Keywords
            (r"\bif\b", token.Keyword),
            (r"\bthen\b", token.Keyword),
            (r"\belse\b", token.Keyword),
            (r"\bletstm\b", token.Keyword),
            (r"\blet\b", token.Keyword),
            (r"\bin\b", token.Keyword),
            (r"\bconst\b", token.Keyword),
            (r"\baccelerator\b", token.Keyword),
            (r"\bassert\b", token.Keyword),
            (r"\byields\b", token.Keyword),
            (r"\bignoring\b", token.Keyword),
            (r"\bwith\b", token.Keyword),
            (r"\bprefix\b", token.Keyword),
            (r"\bexit\b", token.Keyword),
            (r"\btype\b", token.Keyword),
            # Type keywords
            (r"\bbool\b", token.Keyword.Type),
            (r"\bu[0-9]+\b", token.Keyword.Type),
            (r"\bi[0-9]+\b", token.Keyword.Type),
            (r"\bStm\b", token.Keyword.Type),
            (r"\bVec\b", token.Keyword.Type),
            # Built-in functions
            (r"\bpad[0-9]+\b", token.Name.Builtin),
            (r"\btruncate[0-9]+\b", token.Name.Builtin),
            (r"\bsign\b", token.Name.Builtin),
            (r"\bunsign\b", token.Name.Builtin),
            (r"\bmin\b", token.Name.Builtin),
            (r"\bmax\b", token.Name.Builtin),
            (r"\bbits\b", token.Name.Builtin),
            (r"\binterpret_as\b", token.Name.Builtin),
            (r"\bzeros\b", token.Name.Builtin),
            (r"\bones\b", token.Name.Builtin),
            (r"\bStmAll\b", token.Name.Builtin),
            (r"\bStmAny\b", token.Name.Builtin),
            (r"\bStmCascade\b", token.Name.Builtin),
            (r"\bStmConcat\b", token.Name.Builtin),
            (r"\bStmCount\b", token.Name.Builtin),
            (r"\bStmCount2D\b", token.Name.Builtin),
            (r"\bStmCst\b", token.Name.Builtin),
            (r"\bStmDrop\b", token.Name.Builtin),
            (r"\bStmFold\b", token.Name.Builtin),
            (r"\bStmJoin\b", token.Name.Builtin),
            (r"\bStmMap\b", token.Name.Builtin),
            (r"\bStmMap2", token.Name.Builtin),
            (r"\bStmMapDot", token.Name.Builtin),
            (r"\bStmMapDotCascaded", token.Name.Builtin),
            (r"\bStmReduce", token.Name.Builtin),
            (r"\bStmSlide\b", token.Name.Builtin),
            (r"\bStmSplit\b", token.Name.Builtin),
            (r"\bStmSum\b", token.Name.Builtin),
            (r"\bStmTake\b", token.Name.Builtin),
            (r"\bStmZip\b", token.Name.Builtin),
            (r"\bVecAll\b", token.Name.Builtin),
            (r"\bVecAny\b", token.Name.Builtin),
            (r"\bVecConcat\b", token.Name.Builtin),
            (r"\bVecCount\b", token.Name.Builtin),
            (r"\bVecCst\b", token.Name.Builtin),
            (r"\bVecDrop\b", token.Name.Builtin),
            (r"\bVecDropRight\b", token.Name.Builtin),
            (r"\bVecFold\b", token.Name.Builtin),
            (r"\bVecJoin\b", token.Name.Builtin),
            (r"\bVecMap\b", token.Name.Builtin),
            (r"\bVecMap2\b", token.Name.Builtin),
            (r"\bVecReduce\b", token.Name.Builtin),
            (r"\bVecReverse\b", token.Name.Builtin),
            (r"\bVecShiftLeft\b", token.Name.Builtin),
            (r"\bVecSplit\b", token.Name.Builtin),
            (r"\bVecSum\b", token.Name.Builtin),
            (r"\bVecTake\b", token.Name.Builtin),
            (r"\bVecTakeRight\b", token.Name.Builtin),
            (r"\bVecTranspose\b", token.Name.Builtin),
            (r"\bVecZip\b", token.Name.Builtin),
            # Literals
            (r"[0-9]+", token.Literal.Number),
            (r"\btrue\b", token.Literal),
            (r"\bfalse\b", token.Literal),
            (r"\bundefined\b", token.Literal),
            # Punctuation
            (r"\[", token.Punctuation),
            (r"\]", token.Punctuation),
            (r"\]v", token.Punctuation),
            (r"\]s", token.Punctuation),
            (r"\+\+", token.Punctuation),
            (r"=>", token.Punctuation),
            # Operators
            (r"\*", token.Operator),
            (r"\+", token.Operator),
            (r"-", token.Operator),
            (r"&&", token.Operator),
            (r"\|\|", token.Operator),
            (r"!", token.Operator),
            (r"==", token.Operator),
            (r"<", token.Operator),
            (r"<=", token.Operator),
            (r">", token.Operator),
            (r">=", token.Operator),
            # Other
            (r"/\*", token.Comment.Multiline, "comment"),
            (r"//.*", token.Comment.Singleline),
            (r"[a-zA-Z]+", token.Name),
        ],
        "comment": [
            (r"[^*/]+", token.Comment.Multiline),
            (r"/\*", token.Comment.Multiline, "#push"),
            (r"\*/", token.Comment.Multiline, "#pop"),
            (r"[*/]", token.Comment.Multiline),
        ]
    }
