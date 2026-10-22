"""
Functions for parsing a Sirop signature.
"""

class SiropSyntaxError(ValueError):
    """A syntax error in a signature."""
    def __init__(self, message: str) -> None:
        super().__init__(message)


def signature(src: str) -> tuple[str, str | None, list[str] | None, list[str], str, str]:
    (name, src) = ident(src)
    if src and src[0] == "{":
        (inside_curly, src) = consume_until_right_curly(src[1:])
    else:
        inside_curly = None
    if src and src[0] == ":":
        src = src[1:]
        src = expect_exactly(src, "[")
        (typ_params, src) = consume_until_right_square(src)
        typ_params = [t.strip() for t in typ_params.split(",")]
    else:
        typ_params = None
    src = expect_exactly(src, "(")
    # Parameter list
    params = []
    (inside, src) = consume_until_right_paren(src)
    params = [p.strip() for p in inside.split(",")]
    # Return type
    src = expect_exactly(src, ":")
    src = consume_whitespace(src)
    try:
        index_of_extras = src.index("::")
        out_typ = src[:index_of_extras].strip()
        comment = src[index_of_extras+len("::"):].strip()
    except ValueError:
        (out_typ, comment) = (src, "")
    return (name, inside_curly, typ_params, params, out_typ, comment)


def ident(src: str) -> tuple[str, str]:
    if not src:
        raise SiropSyntaxError("expected an identifier, but reached end of source code")
    if not (src[0] == "_" or src[0].isalpha()):
        raise SiropSyntaxError(f"expected an identifier, but found '{src[0]}'")
    i = 1
    while True:
        if i >= len(src):
            break
        if not (src[i] == "_" or src[i].isalpha() or src[i].isdigit()):
            break
        i += 1
    return (src[:i], src[i:])


def expect_exactly(actual: str, expected: str) -> str:
    assert len(expected) == 1
    if not actual:
        raise SiropSyntaxError(f"expected '{expected}', but reached end of source code")
    if actual[0] != expected:
        raise SiropSyntaxError(f"expected '{expected}', but found '{actual}'")
    return actual[1:]


def consume_until_matching(src: str, left: str, right: str) -> tuple[str, str]:
    if len(right) != 1:
        raise ValueError(f'invalid right: "{right}"')
    if len(left) > 1 or left == right:
        raise ValueError(f'invalid left: "{left}"')
    left_count = 0
    i = 0
    while True:
        if i >= len(src):
            raise SiropSyntaxError(f"missing '{right}'")
        if src[i] == right and left_count == 0:
            # Consume the 'right' character, but don't return it
            return (src[:i], src[i+1:])
        if src[i] == right:
            left_count -= 1
            i += 1
            continue
        if src[i] == left:
            left_count += 1
            i += 1
            continue
        i += 1


def consume_until_right_paren(src: str) -> tuple[str, str]:
    return consume_until_matching(src, "(", ")")


def consume_until_right_curly(src: str) -> tuple[str, str]:
    return consume_until_matching(src, "{", "}")


def consume_until_right_square(src: str) -> tuple[str, str]:
    return consume_until_matching(src, "[", "]")


def consume_whitespace(src: str) -> str:
    i = 0
    while i < len(src) and src[i].isspace():
        i += 1
    return src[i:]
