"""
Functions for parsing a Sirop signature.
"""

class SiropSyntaxError(ValueError):
    """A syntax error in a signature."""
    def __init__(self, message: str) -> None:
        super().__init__(message)


def signature(src: str) -> tuple[str, list[str], str, str]:
    (name, src) = ident(src)
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
    return (name, params, out_typ, comment)


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


def consume_until_right_paren(src: str) -> tuple[str, str]:
    left_count = 0
    i = 0
    while True:
        if not src:
            raise SiropSyntaxError("missing ')'")
        if src[i] == ")" and left_count == 0:
            # Consume the right square paren, but don't return it
            return (src[:i], src[i+1:])
        if src[i] == ")":
            left_count -= 1
            i += 1
            continue
        if src[i] == "(":
            left_count += 1
            i += 1
            continue
        i += 1


def consume_whitespace(src: str) -> str:
    i = 0
    while i < len(src) and src[i].isspace():
        i += 1
    return src[i:]
