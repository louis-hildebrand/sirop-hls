"""
Dataclass for describing the resource usage of a design.
"""

from dataclasses import dataclass


@dataclass(frozen=True)
class ResourceUsage:
    """
    The resource usage of a hardware design.
    """
    alm: int
    bram: int
    dsp: int
