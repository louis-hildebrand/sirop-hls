#!/bin/python3

"""
This script plots the latency for the Aetherling benchmarks.
"""

import sys
from typing import TypeVar

import matplotlib.pyplot as plt

import lib.benchmark as lb
import lib.constants as c
import lib.results_crud as crud
from lib.benchmark import BenchmarkImpl
from lib.latency import LatencyResult

DEFAULT_COLORS = plt.rcParams["axes.prop_cycle"].by_key()["color"]


MIN_LABEL = "Best possible latency"
MIN_MARKER = ""
AETHERLING_LABEL = "Aetherling \u2192 Chisel \u2192 Verilog"
AETHERLING_MARKER = "s"
AETHERLING_COLOR = DEFAULT_COLORS[0]
AETHERLING_MARKER_SIZE = 5
OUR_LABEL = "Aetherling \u2192 Min. IR \u2192 VHDL"
OUR_MARKER = "o"
OUR_COLOR = DEFAULT_COLORS[1]
OUR_MARKER_SIZE = 3


T = TypeVar("T")


def axis_scale(bench_name: str) -> str:
    """
    Decide whether the given benchmark should be plotted with a log scale or linear scale.
    """
    if bench_name in {"sum", "dot"}:
        return "linear"
    return "log"


def dedup(xs: list[T]) -> list[T]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


def plot_latency(results: dict[BenchmarkImpl, LatencyResult]) -> None:
    """
    Plot latency vs throughput for each benchmark.
    """
    benchmark_names = dedup([res.bench.name for res in results.keys()])
    benchmark_names = sorted(benchmark_names, key=lb.benchmark_order)
    if not benchmark_names:
        raise ValueError("No benchmarks to plot.")
    fig, axes = plt.subplots(nrows=1, ncols=len(benchmark_names), figsize=(10, 2.5), squeeze=False)
    axes = axes[0]
    min_artist = None
    verilog_artist = None
    vhdl_artist = None
    for col, bench_name in enumerate(benchmark_names):
        ax = axes[col]
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
        # Minimum results
        all_benchmarks = dedup(vhdl_benchmarks + verilog_benchmarks)
        all_benchmarks = sorted(all_benchmarks, key=lambda b: b.bench.throughput)
        xs = [float(b.bench.throughput) for b in all_benchmarks]
        ys = [lb.min_latency(b.bench) for b in all_benchmarks]
        min_artist, = ax.plot(
            xs, ys,
            marker=MIN_MARKER,
            label=MIN_LABEL,
            linestyle=":",
            color=(0.5, 0.5, 0.5),
        )
        # Verilog results (successful simulation)
        xs = [float(b.bench.throughput) for b in verilog_benchmarks if results[b].sim_success]
        ys = [results[b].latency for b in verilog_benchmarks if results[b].sim_success]
        verilog_artist, = ax.plot(
            xs, ys,
            marker=AETHERLING_MARKER,
            label=AETHERLING_LABEL,
            color=AETHERLING_COLOR,
            linestyle="",
            markersize=AETHERLING_MARKER_SIZE,
        )
        # Verilog results (failed simulation)
        xs = [float(b.bench.throughput) for b in verilog_benchmarks if not results[b].sim_success]
        ys = [results[b].latency for b in verilog_benchmarks if not results[b].sim_success]
        verilog_artist, = ax.plot(
            xs, ys,
            marker=AETHERLING_MARKER,
            label=AETHERLING_LABEL,
            markerfacecolor=ax.get_facecolor(),
            markeredgecolor=AETHERLING_COLOR,
            linestyle="",
            markersize=AETHERLING_MARKER_SIZE,
        )
        # VHDL results (successful simulation)
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks if results[b].sim_success]
        ys = [results[b].latency for b in vhdl_benchmarks if results[b].sim_success]
        vhdl_artist, = ax.plot(
            xs, ys,
            marker=OUR_MARKER,
            label=OUR_LABEL,
            color=OUR_COLOR,
            linestyle="",
            markersize=OUR_MARKER_SIZE,
        )
        # VHDL results (failed simulation)
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks if not results[b].sim_success]
        ys = [results[b].latency for b in vhdl_benchmarks if not results[b].sim_success]
        vhdl_artist, = ax.plot(
            xs, ys,
            marker=OUR_MARKER,
            label=OUR_LABEL,
            markerfacecolor=ax.get_facecolor(),
            markeredgecolor=OUR_COLOR,
            linestyle="",
            markersize=OUR_MARKER_SIZE,
        )
        # Labels and whatnot
        ax.tick_params(axis="x", rotation=30)
        if axis_scale(bench_name) == "log":
            ax.set_xscale("log", base=2)
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
        bbox_to_anchor=(0.5, -0.3)
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
