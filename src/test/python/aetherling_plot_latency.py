#!/bin/python3

"""
This script plots the latency for the Aetherling benchmarks.
"""

import sys
from typing import TypeVar

import matplotlib.pyplot as plt
from matplotlib.axes import Axes

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


def dedup(xs: list[T]) -> list[T]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


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
            [r"$\frac{1}{3}$", "1", "2", "4", "8", "16"],
        )
    elif bench_name == "conv1d":
        ax.set_xticks(
            [1/3, 1, 2, 4, 8, 16],
            [r"$\frac{1}{3}$", "1", "2", "4", "8", "16"],
        )
    elif bench_name in {"sum", "dot"}:
        ax.set_xticks(
            [1/840, 2/840, 4/840, 8/840],
            [r"$\frac{1}{840}$", r"$\frac{2}{840}$", r"$\frac{4}{840}$", r"$\frac{8}{840}$"],
        )
    elif bench_name == "map":
        ax.set_xticks(
            [1, 4, 20, 200],
            ["1", "4", "20", "200"],
        )


def plot_latency(results: dict[BenchmarkImpl, LatencyResult]) -> None:
    """
    Plot latency vs throughput for each benchmark.
    """
    benchmark_names = dedup([res.bench.name for res in results.keys()])
    benchmark_names = sorted(benchmark_names, key=lb.benchmark_order)
    benchmark_names = [b for b in benchmark_names if lb.benchmark_title(b) is not None]
    if not benchmark_names:
        raise ValueError("No benchmarks to plot.")
    plt.rcParams.update({
        "text.usetex": True,
        "font.family": "Times New Roman",
        "font.size": 10,
    })
    fig, axes = plt.subplots(
        nrows=1, ncols=len(benchmark_names),
        figsize=(8, 2),
        squeeze=False,
        sharey="row",
        layout="compressed",
    )
    axes = axes[0]
    min_artist = None
    verilog_artist = None
    vhdl_artist = None
    for col, bench_name in enumerate(benchmark_names):
        title = lb.benchmark_title(bench_name)
        if title is None:
            continue
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
        # Labels and whatnot
        ax.set_xscale("log", base=2)
        ax.set_yscale("log", base=10)
        ax.set_title(title)
        set_ticks(ax, bench_name)

    # Settings for entire rows
    axes[0].set_ylabel("Latency (cycles)")
    fig.supxlabel("Target throughput")
    if min_artist is None or verilog_artist is None or vhdl_artist is None:
        raise RuntimeError("Cannot create legend due to missing artists.")
    fig.legend(
        [verilog_artist, vhdl_artist, min_artist],
        [AETHERLING_LABEL, OUR_LABEL, MIN_LABEL],
        loc="lower center",
        bbox_to_anchor=(0.5, -0.4)
    )
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
