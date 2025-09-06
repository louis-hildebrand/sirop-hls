"""
Helper functions for plots.
"""

from typing import TypeVar

T = TypeVar("T")


def flip(items: list[T], ncol: int) -> list[T]:
    """
    Change the order of the given items so that they are read left-to-right and then
    top-to-bottom (row-major order), rather than top-to-bottom and then left-to-right (column-major
    order).
    """
    return [x for i in range(ncol) for x in items[i::ncol]]


def dedup(xs: list[str]) -> list[str]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))
