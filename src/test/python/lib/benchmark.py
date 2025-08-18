"""
Dataclasses for describing a benchmark.
"""

from __future__ import annotations

from dataclasses import dataclass
from fractions import Fraction


@dataclass(frozen=True, order=True)
class Benchmark:
    """
    A benchmark with a specific throughput.
    """
    name: str
    throughput: Fraction

    @classmethod
    def parse(cls, name: str) -> Benchmark:
        """
        Parse the given benchmark full name (e.g., 'map_20').
        """
        parts = name.split("_")
        if len(parts) == 2:
            name = parts[0]
            throughput = Fraction(int(parts[1]))
        elif len(parts) == 3:
            name = parts[0]
            throughput = Fraction(int(parts[1]), int(parts[2]))
        else:
            raise ValueError(f"Invalid benchmark name: {name}")
        return Benchmark(name=name, throughput=throughput)

    @property
    def full_name(self) -> str:
        """
        Return the full name of this benchmark (e.g., 'map_20').
        """
        if self.throughput.denominator == 1:
            return f"{self.name}_{self.throughput.numerator}"
        return f"{self.name}_{self.throughput.numerator}_{self.throughput.denominator}"

    @property
    def throughput_str(self) -> str:
        """
        Returns the throughput of this benchmark as a string (e.g., "1/105").
        """
        if self.throughput.denominator == 1:
            return str(self.throughput.numerator)
        return f"{self.throughput.numerator}/{self.throughput.denominator}"


@dataclass(frozen=True, order=True)
class BenchmarkImpl:
    """
    A VHDL or Verilog which implements a specific benchmark.
    """
    bench: Benchmark
    language: str


def min_latency(bench: Benchmark) -> int:
    """
    Compute the minimum possible latency for the given benchmark.
    """
    if bench.name == "map":
        # 200 inputs, 200 outputs
        return 200 // bench.throughput
    if bench.name == "sum":
        # 840 inputs, 1 output
        par = 840 * bench.throughput
        return 840 // par
    if bench.name == "dot":
        # 840 inputs, 1 output
        par = 840 * bench.throughput
        return 840 // par
    if bench.name == "conv1d":
        # 16 inputs, 16 outputs
        return 16 // bench.throughput
    if bench.name == "smallconv2d":
        # 16 inputs, 16 outputs
        return 16 // bench.throughput
    if bench.name == "smallconvb2b":
        # 16 inputs, 16 outputs
        return 16 // bench.throughput
    if bench.name == "smallsharpen":
        # 16 inputs, 16 outputs
        return 16 // bench.throughput
    raise ValueError(f"The minimum latency for benchmark {bench} is unknown.")


def benchmark_order(bench_name: str) -> int:
    """
    Decide what order the benchmarks should be laid out in the plots.
    """
    return {
        "map": 0,
        "sum": 1,
        "dot": 2,
        "conv1d": 3,
        "smallconv2d": 4,
        "smallconvb2b": 5,
        "smallsharpen": 6,
    }.get(bench_name, 7)
