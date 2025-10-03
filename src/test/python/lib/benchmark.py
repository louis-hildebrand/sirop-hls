"""
Dataclasses for describing a benchmark.
"""

from __future__ import annotations

from dataclasses import dataclass
from fractions import Fraction

from matplotlib.axes import Axes


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
    if bench.name in {"conv1d", "smallconv2d", "smallconvb2b", "smallsharpen"}:
        # 16 inputs, 16 outputs
        return 16 // bench.throughput
    if bench.name in {"bigconv2d", "bigconvb2b", "bigsharpen"}:
        # 1920*4 inputs and outputs
        return 1920 * 4 // bench.throughput
    if bench.name == "bigcamera":
        # 1920*8 inputs and outputs
        return 1920 * 8 // bench.throughput
    if bench.name == "matvec":
        # In this case, the benchmark "throughput" is actually its parallelization factor
        return 256 * 256 // bench.throughput
    if bench.name == "sqrt":
        return 1020 // bench.throughput
    raise ValueError(f"The minimum latency for benchmark {bench} is unknown.")


def benchmark_order(bench_name: str) -> int:
    """
    Decide what order the benchmarks should be laid out in the plots.
    """
    return {
        "map": 0,
        "sum": 1,
        "dot": 2,
        "matvec": 3,
        "conv1d": 4,
        "smallconv2d": 5,
        "smallconvb2b": 6,
        "smallsharpen": 7,
        "bigconv2d": 8,
        "bigconvb2b": 9,
        "bigsharpen": 10,
        "bigcamera": 11,
        "sqrt": 12,
    }.get(bench_name, 12)


def benchmark_title(bench_name: str) -> str | None:
    """
    Return the title to put at the top of the column for the given benchmark, or `None` if the
    results for this benchmark should be omitted from the plots.
    """
    if bench_name.startswith("small"):
        return None
    if bench_name == "sum":
        return None
    if bench_name.startswith("big"):
        bench_name = bench_name[len("big"):]
    return f"\\texttt{{{bench_name}}}"


def set_ticks(ax: Axes, bench_name: str) -> None:
    """
    Set the x-axis ticks for the given benchmark.
    """
    if bench_name == "bigcamera":
        ax.set_xticks(
            [1/4, 1, 2, 4, 8, 16],
            [r"$\frac{1}{4}$", "1", "2", "4", "8", "16"],
        )
    elif bench_name.startswith("big"):
        ax.set_xticks(
            [1/3, 1, 2, 4, 8, 16],
            [r"$\frac{1}{3}$", "1", "2", "4", "8", "16"]
        )
    elif bench_name == "conv1d":
        ax.set_xticks(
            [1/3, 1, 2, 4, 8, 16],
            [r"$\frac{1}{3}$", "1", "2", "4", "8", "16"]
        )
    elif bench_name in {"sum", "dot"}:
        ax.set_xticks(
            [1/840, 2/840, 4/840, 8/840],
            [r"$\frac{1}{840}$", r"$\frac{2}{840}$", r"$\frac{4}{840}$", r"$\frac{8}{840}$"],
        )
    elif bench_name == "map":
        ax.set_xticks(
            [1, 4, 20, 200],
            ["1", "4", "20", "200"]
        )
    elif bench_name == "matvec":
        n = 256
        par = [1, 2, 4, 8, 16]
        ax.set_xticks(
            par,
            [f"$\\frac{{1}}{{{n//p}}}$" for p in par]
        )
