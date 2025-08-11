#!/bin/python3

"""
This script plots the latency for the Aetherling benchmarks.
"""

import sys

import matplotlib.pyplot as plt

import lib.constants as c
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl

MIN_LABEL = "Best possible latency"
MIN_MARKER = ""
AETHERLING_LABEL = "Aetherling \u2192 Chisel \u2192 Verilog"
AETHERLING_MARKER = "s"
OUR_LABEL = "Aetherling \u2192 Min. IR \u2192 VHDL"
OUR_MARKER = "o"
DEFAULT_ERR = 10


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
    raise ValueError(f"The minimum latency for benchmark {bench} is unknown.")


def benchmark_order(bench_name: str) -> int:
    """
    Decide what order the benchmarks should be laid out in the plot.
    """
    return {
        "map": 0,
        "sum": 1,
        "dot": 2,
        "conv1d": 3
    }.get(bench_name, 4)



def dedup(xs: list[str]) -> list[str]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


def plot_latency(results: dict[BenchmarkImpl, int]) -> None:
    """
    Plot latency vs throughput for each benchmark.
    """
    plt.rcParams.update({
        "text.usetex": True
    })
    benchmark_names = dedup([res.bench.name for res in results.keys()])
    benchmark_names = sorted(benchmark_names, key=benchmark_order)
    if not benchmark_names:
        raise ValueError("No benchmarks to plot.")
    fig, axes = plt.subplots(nrows=1, ncols=len(benchmark_names), figsize=(7, 3), squeeze=False)
    axes = axes[0]
    min_artist = None
    verilog_artist = None
    vhdl_artist = None
    for col, bench_name in enumerate(benchmark_names):
        verilog_benchmarks = [
            b
            for b in results.keys()
            if b.bench.name == bench_name and b.language == "verilog"
        ]
        verilog_benchmarks = sorted(
            verilog_benchmarks,
            key=lambda b: b.bench.throughput,
        )
        vhdl_benchmarks = [
            b
            for b in results.keys()
            if b.bench.name == bench_name and b.language == "vhdl"
        ]
        vhdl_benchmarks = sorted(
            vhdl_benchmarks,
            key=lambda b: b.bench.throughput,
        )
        ax = axes[col]
        # Minimum results
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        ys = [min_latency(b.bench) for b in verilog_benchmarks]
        min_artist, = ax.plot(
            xs, ys,
            marker=MIN_MARKER,
            label=MIN_LABEL,
            linestyle=":",
            color=(0.5, 0.5, 0.5),
        )
        # Verilog results
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        ys = [results[b] for b in verilog_benchmarks]
        verilog_artist, = ax.plot(
            xs, ys,
            marker=AETHERLING_MARKER,
            label=AETHERLING_LABEL,
            linestyle="",
        )
        # VHDL results
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        ys = [results[b] for b in vhdl_benchmarks]
        vhdl_artist, = ax.plot(
            xs, ys,
            marker=OUR_MARKER,
            label=OUR_LABEL,
            linestyle="",
        )
        # Labels and whatnot
        ax.tick_params(axis="x", rotation=30)
        if bench_name == "map":
            ax.set_xscale("log")
        ax.set_title(bench_name)
        ax.set_xlabel("Target throughput")
    # Settings for entire rows
    axes[0].set_ylabel("Latency (cycles)")
    if min_artist is None or verilog_artist is None or vhdl_artist is None:
        raise RuntimeError("Cannot create legend due to missing artists.")
    fig.legend(
        [min_artist, vhdl_artist, verilog_artist],
        [MIN_LABEL, OUR_LABEL, AETHERLING_LABEL],
        loc="lower center",
        bbox_to_anchor=(0.5, -0.2)
    )
    fig.tight_layout()
    fig.savefig(c.LATENCY_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    results = crud.read_valid_latency_results(c.LATENCY_CSV)
    if not results:
        sys.exit("No results to plot.")
    plot_latency(results)


if __name__ == "__main__":
    main()
