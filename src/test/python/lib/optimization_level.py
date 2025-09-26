"""
Enum with the possible optimization settings
"""

from __future__ import annotations

from enum import Enum

_LARGE_BUF_SIZE = 32
"""
Large value for the max buffer size in letstm.
"""

_SMALL_BUF_SIZE = 1
"""
Small value for the max buffer size in letstm.
"""


class OptimizationLevel(Enum):
    """
    Possible optimization settings for a program.
    """

    ONLY_PARTIAL_EVAL = "only_partial_eval"
    """
    Nothing but partial evaluation.
    """

    ALL = "all"
    """
    All useful optimization passes enabled.
    """

    EXCEPT_SBUILD_SIMPL = "except_sbuild_simpl"
    """
    Basic `StmBuild` simplifications disabled.
    """

    EXCEPT_LETSTM_SIMPL = "except_letstm_simpl"
    """
    Basic `LetStm` simplifications disabled.
    """

    EXCEPT_STM_SIMPL = "except_stm_simpl"
    """
    Both `StmBuild` and `LetStm` simplifications disabled.
    """

    EXCEPT_FUSE = "except_fuse"
    """
    Stream fusion disabled.
    """

    SMALL_BUFFERS = "small_buffers"
    """
    Very small max buffer size for `LetStm`.
    """

    SMALL_BUFFERS_AND_LATMATCH = "small_buffers_and_latmatch"
    """
    Very small max buffer size for `LetStm`, but latency matching is enabled.
    """

    @property
    def explanation(self) -> str:
        """
        Return a short explanation of the optimizations included in this level.
        """
        match self:
            case OptimizationLevel.ONLY_PARTIAL_EVAL:
                return "partial eval."
            case OptimizationLevel.ALL:
                return "all"
            case OptimizationLevel.EXCEPT_SBUILD_SIMPL:
                return "no sbuild simpl."
            case OptimizationLevel.EXCEPT_LETSTM_SIMPL:
                return "no letstm simpl."
            case OptimizationLevel.EXCEPT_STM_SIMPL:
                return "no sbuild/letstm simpl."
            case OptimizationLevel.EXCEPT_FUSE:
                return "no fusion"
            case OptimizationLevel.SMALL_BUFFERS:
                return "bufsize 1"
            case OptimizationLevel.SMALL_BUFFERS_AND_LATMATCH:
                return "bufsize 1, latmatch"

    @property
    def flags(self) -> str:
        """
        Return the compiler flags for this optimization level.
        """
        match self:
            case OptimizationLevel.ONLY_PARTIAL_EVAL:
                return (
                    " --opt:no-simplify-sbuild"
                    " --opt:no-inline-letstm"
                    " --opt:no-fuse"
                    " --opt:no-latmatch"
                    " --opt:no-static-buf-shrink"
                    f" --opt:max-let-buf-size {_LARGE_BUF_SIZE}"
                )
            case OptimizationLevel.ALL:
                return (
                    " --opt:no-latmatch"
                    " --opt:no-static-buf-shrink"
                    f" --opt:max-let-buf-size {_LARGE_BUF_SIZE}"
                )
            case OptimizationLevel.EXCEPT_SBUILD_SIMPL:
                return OptimizationLevel.ALL.flags + " --opt:no-simplify-sbuild"
            case OptimizationLevel.EXCEPT_LETSTM_SIMPL:
                return OptimizationLevel.ALL.flags + " --opt:no-inline-letstm"
            case OptimizationLevel.EXCEPT_STM_SIMPL:
                return (
                    OptimizationLevel.ALL.flags
                    + " --opt:no-simplify-sbuild --opt:no-inline-letstm"
                )
            case OptimizationLevel.EXCEPT_FUSE:
                return OptimizationLevel.ALL.flags + " --opt:no-fuse"
            case OptimizationLevel.SMALL_BUFFERS:
                return (
                    f" --opt:max-let-buf-size {_SMALL_BUF_SIZE}"
                    " --opt:no-latmatch"
                    " --opt:no-static-buf-shrink"
                )
            case OptimizationLevel.SMALL_BUFFERS_AND_LATMATCH:
                return (
                    f" --opt:max-let-buf-size {_SMALL_BUF_SIZE}"
                    " --opt:assume-throughputs-match"
                )

    def __str__(self) -> str:
        return self.value

    def __lt__(self, other: OptimizationLevel) -> bool:
        return list(OptimizationLevel).index(self) < list(OptimizationLevel).index(other)
