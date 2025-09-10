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
    Nothing but partial evaluation.
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

    ALL_EXCEPT_SIMPL = "nosimpl"
    """
    All optimizations except stream simplification.
    """

    @property
    def explanation(self) -> str:
        """
        Return a short explanation of the optimizations included in this level.
        """
        if self == OptimizationLevel.NONE:
            return "only partial evaluation (PE)"
        if self == OptimizationLevel.SIMPLIFY:
            return "PE + stream simplification (SS)"
        if self == OptimizationLevel.MATCH_LATENCY:
            return "PE + SS + latency matching (LM)"
        if self == OptimizationLevel.FUSE:
            return "PE + SS + LM + fusion"
        if self == OptimizationLevel.ALL_EXCEPT_SIMPL:
            return "all except SS"
        raise NotImplementedError(f"no explanation for {self}")

    def __str__(self) -> str:
        return self.value

    def __lt__(self, other: OptimizationLevel) -> bool:
        return list(OptimizationLevel).index(self) < list(OptimizationLevel).index(other)
