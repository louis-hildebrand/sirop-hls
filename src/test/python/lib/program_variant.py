"""
Dataclass representing a program with a particular set of optimizations.
"""

from dataclasses import dataclass

from .optimization_level import OptimizationLevel


@dataclass(frozen=True, order=True)
class ProgramVariant:
    """
    A program compiled with a given set of optimizations.
    """

    name: str
    """
    The program name.
    """
    lvl: OptimizationLevel
    """
    The optimizations that were applied to the program.
    """
