#!/bin/python3

"""
This script plots the maximum clock frequency for the Aetherling benchmarks.
"""

import sys

import matplotlib.pyplot as plt

import lib.benchmark as lb
import lib.constants as c
import lib.results_crud as crud
from lib.benchmark import BenchmarkImpl

TARGET_FREQ = 175


AETHERLING_LABEL = "Aetherling \u2192 Chisel \u2192 Verilog"
AETHERLING_MARKER = "s"
AETHERLING_MARKER_SIZE = 4
OUR_LABEL = "Aetherling \u2192 Min. IR \u2192 VHDL"
OUR_MARKER = "o"
OUR_MARKER_SIZE = 3
TARGET_LABEL = "Target frequency"


SCALE = "log"


def dedup(xs: list[str]) -> list[str]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


def plot_fmax(results: dict[BenchmarkImpl, float]) -> None:
    """
    Plot fmax vs throughput for each benchmark.
    """
    benchmark_names = dedup([res.bench.name for res in results.keys()])
    benchmark_names = sorted(benchmark_names, key=lb.benchmark_order)
    if not benchmark_names:
        raise ValueError("No benchmarks to plot.")
    fig, axes = plt.subplots(nrows=1, ncols=len(benchmark_names), figsize=(16, 2.5))
    verilog_artist = None
    vhdl_artist = None
    target_artist = None
    for col, bench_name in enumerate(benchmark_names):
        verilog_benchmarks = [
            b
            for b in results.keys()
            if b.bench.name == bench_name and b.language == "verilog"
        ]
        verilog_benchmarks = sorted(
            verilog_benchmarks,
            key=lambda b: (b.language, b.bench.throughput)
        )
        vhdl_benchmarks = [
            b
            for b in results.keys()
            if b.bench.name == bench_name and b.language == "vhdl"
        ]
        vhdl_benchmarks = sorted(
            vhdl_benchmarks,
            key=lambda b: (b.language, b.bench.throughput)
        )
        ax = axes[col]
        # Verilog
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        ys = [results[b] for b in verilog_benchmarks]
        verilog_artist, = ax.plot(
            xs, ys,
            marker=AETHERLING_MARKER,
            markersize=AETHERLING_MARKER_SIZE,
            label=AETHERLING_LABEL,
        )
        # VHDL
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        ys = [results[b] for b in vhdl_benchmarks]
        vhdl_artist, = ax.plot(
            xs, ys,
            marker=OUR_MARKER,
            markersize=OUR_MARKER_SIZE,
            label=OUR_LABEL,
        )
        # Target
        xs = sorted({b.bench.throughput for b in verilog_benchmarks + vhdl_benchmarks})
        ys = [TARGET_FREQ for b in xs]
        target_artist, = ax.plot(xs, ys, marker="none", linestyle=":", color=(0.5, 0.5, 0.5))
        # Title, etc.
        ax.set_title(bench_name)
        ax.set_xlabel("Target throughput")
        if bench_name not in {"sum", "dot"}:
            ax.set_xscale(SCALE)
    # Settings for entire rows
    axes[0].set_ylabel("fmax (MHz)")
    if verilog_artist is None or vhdl_artist is None or target_artist is None:
        raise RuntimeError("Cannot create legend due to missing artists.")
    fig.legend(
        [vhdl_artist, verilog_artist, target_artist],
        [OUR_LABEL, AETHERLING_LABEL, TARGET_LABEL],
        loc="lower center",
        bbox_to_anchor=(0.5, -0.3)
    )
    fig.tight_layout()
    fig.savefig(c.FMAX_ESTIMATE_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    results = crud.read_valid_fmax_estimates(c.FMAX_ESTIMATE_CSV)
    if not results:
        sys.exit("No results to plot.")
    plot_fmax(results)


if __name__ == "__main__":
    main()
