"""
Enum with the possible optimization settings
"""

from __future__ import annotations

from enum import Enum


class OptimizationLevel(Enum):
    """
    Possible optimization settings for a program.
    """

    NONE = "none"
    """
    No optimizations.
    """

    SIMPLIFY = "simpl"
    """
    Only partial evaluation and basic stream simplification.
    """

    MATCH_LATENCY = "latmatch"
    """
    `SIMPLIFY` plus latency matching.
    """

    FUSE = "fuse"
    """
    `MATCH_LATENCY` plus greedy fusion.
    """

    def __str__(self) -> str:
        return self.value

    def __lt__(self, other: OptimizationLevel) -> bool:
        return list(OptimizationLevel).index(self) < list(OptimizationLevel).index(other)
