#!/usr/bin/env python3

"""
Script to generate test cases for bitwise binary operations.
The Sirop assertions are printed to stdout.
To switch between OR and AND, call main() with the appropriate function.
"""

from collections.abc import Callable

from bitstring import Bits

N = 8

def u4_to_string(x: int) -> str:
    bits = Bits(uint=x, length=4).bin
    return f"{x:>2}:u4 /*{bits}*/"


def i3_to_string(x: int) -> str:
    bits = Bits(int=x, length=3).bin
    return f"{x:>2}:i3 /*{bits}*/"


def tuple_to_string(x: tuple[list[int], int, bool]) -> str:
    (v, n, b) = x
    v_elems = ", ".join([u4_to_string(x) for x in v])
    n = i3_to_string(n)
    b = "true " if b else "false"
    return f"([{v_elems}]v, {n}, {b})"


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


def main(
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

if __name__ == "__main__":
    main(bitwise_or)
