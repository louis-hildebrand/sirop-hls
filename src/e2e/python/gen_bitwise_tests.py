#!/usr/bin/env python3

"""
Script to generate test cases for bitwise binary operations.
The Sirop assertions are printed to stdout.
To switch between OR and AND, call main() with the appropriate function.
"""

from collections.abc import Callable

from bitstring import Bits

N = 8


def uint2str(x: int, bitwidth: int) -> str:
    bits = Bits(uint=x, length=bitwidth).bin
    w = len(str(2**bitwidth-1))
    return f"{x:>{w}}:u{bitwidth} /*{bits}*/"


def int2str(x: int, bitwidth: int) -> str:
    bits = Bits(int=x, length=bitwidth).bin
    w = max(len(str(-2**(bitwidth-1))), len(str(2**(bitwidth-1)-1)))
    return f"{x:>{w}}:i{bitwidth} /*{bits}*/"


def tuple_to_string(x: tuple[list[int], int, bool]) -> str:
    (v, n, b) = x
    v_elems = ", ".join([uint2str(x, 4) for x in v])
    n = int2str(n, 3)
    b = "true " if b else "false"
    return f"([{v_elems}]v, {n}, {b})"


def vvb2str(x: tuple[list[int], list[int], bool]) -> str:
    """
    (Vec[u8], Vec[i8], bool) to string.
    """
    (v1, v2, b) = x
    v1 = "[" + ", ".join([uint2str(x, 8) for x in v1]) + "]v"
    v2 = "[" + ", ".join([int2str(x, 8) for x in v2]) + "]v"
    b = "true " if b else "false"
    return f"({v1}, {v2}, {b})"


def vvb2boolvec(x: tuple[list[int], list[int], bool]) -> str:
    """
    (Vec[u8], Vec[i8], bool) to Vec[bool].
    """
    (v1, v2, b) = x
    bits = ",".join([
        ",".join([
            "T" if bit else "F"
            # Pyright complains that Bits is not iterable, but in practice it is
            for bit in Bits(uint=x, length=8) # pyright: ignore[reportGeneralTypeIssues]
        ])
        for x in v1
    ])
    bits += "," + ",".join([
        ",".join([
            "T" if bit else "F"
            # Pyright complains that Bits is not iterable, but in practice it is
            for bit in Bits(int=x, length=8) # pyright: ignore[reportGeneralTypeIssues]
        ])
        for x in v2
    ])
    bits += "," + ("T" if b else "F")
    return "[" + bits + "]v"


def bitwise_and(
    x: tuple[list[int], int, bool],
    y: tuple[list[int], int, bool]
) -> tuple[list[int], int, bool]:
    (xv, xn, xb) = x
    (yv, yn, yb) = y
    # z is the result
    zv = [
        (Bits(uint=x, length=4) & Bits(uint=y, length=4)).uint
        for (x, y) in zip(xv, yv)
    ]
    zn = (Bits(int=xn, length=3) & Bits(int=yn, length=3)).int
    zb = xb and yb
    return (zv, zn, zb)


def bitwise_or(
    x: tuple[list[int], int, bool],
    y: tuple[list[int], int, bool]
) -> tuple[list[int], int, bool]:
    (xv, xn, xb) = x
    (yv, yn, yb) = y
    # z is the result
    zv = [
        (Bits(uint=x, length=4) | Bits(uint=y, length=4)).uint
        for (x, y) in zip(xv, yv)
    ]
    zn = (Bits(int=xn, length=3) | Bits(int=yn, length=3)).int
    zb = xb and yb
    return (zv, zn, zb)


def main_binop(
    f: Callable[
        [tuple[list[int], int, bool], tuple[list[int], int, bool]],
        tuple[list[int], int, bool]
    ]
) -> None:
    for i in range(-N//2, N//2):
        i_pos = i + N//2
        a_vals = [
            ([2*i_pos, 2*i_pos, 2*i_pos+1, 2*i_pos+1], i, j % 4 >= 2)
            for j in range(-N//2, N//2)
        ]
        print("assert {")
        print("    a = [")
        for j in range(N):
            print(f"        {tuple_to_string(a_vals[j])}", end="")
            print("" if j == N-1 else ",")
        print("    ]s,")
        b_vals = [
            ([j+N//2, 8+j+N//2, j+N//2, 8+j+N//2], j, j % 2 == 1)
            for j in range(-N//2, N//2)
        ]
        print("    b = [")
        for j in range(N):
            print(f"        {tuple_to_string(b_vals[j])}", end="")
            print("" if j == N-1 else ",")
        print("    ]s")
        print("}")
        out_vals = [
            f(a, b)
            for (a, b) in zip(a_vals, b_vals)
        ]
        print("yields [")
        for j, x in enumerate(out_vals):
            print(f"    {tuple_to_string(x)}", end="")
            print("" if j == N-1 else ",")
        print("]s")
        if i != N//2 - 1:
            print()


def main_to_bits(flip: bool = False) -> None:
    """
    Generate test cases for the to_bits primitive (by default)
    or for the bits_to primitive (when flip=True).
    """
    for i in range(8):
        uint_start = i * 4 * N
        int_start = uint_start - 128
        tuples = [
            (
                [4*j + uint_start + k for k in range(4)],
                [4*j + int_start + k for k in range(4)],
                j % 2 == 0
            )
            for j in range(8)
        ]
        bool_vectors = [vvb2boolvec(x) for x in tuples]
        if flip:
            inputs = bool_vectors
            outputs = [vvb2str(x) for x in tuples]
        else:
            inputs = [vvb2str(x) for x in tuples]
            outputs = bool_vectors
        print("assert {")
        print("    s = [")
        for j, x in enumerate(inputs):
            print(f"        {x}", end="")
            print("" if j == 7 else ",")
        print("    ]s")
        print("}")
        print("yields [")
        for j, x in enumerate(outputs):
            print(f"    {x}", end="")
            print("" if j == 7 else ",")
        print("]s")
        if i != 7:
            print()


if __name__ == "__main__":
    # main_binop(bitwise_or)
    main_to_bits(flip=True)
