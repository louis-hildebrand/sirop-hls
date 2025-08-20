"""
Dataclasses for describing latency.
"""


from dataclasses import dataclass


@dataclass
class LatencyResult:
    """
    One latency measurement.
    """
    latency: int | None
    sim_success: bool
