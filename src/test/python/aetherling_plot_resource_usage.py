#!/bin/python3

"""
This script plots the resource usages for the Aetherling benchmarks.
"""

import sys

import matplotlib.pyplot as plt
from matplotlib.axes import Axes
from matplotlib.ticker import LogLocator

import lib.benchmark as lb
import lib.constants as c
import lib.results_crud as crud
from lib.benchmark import BenchmarkImpl
from lib.resource_usage import ResourceUsage

AETHERLING_LABEL = "Aetherling \u2192 Chisel \u2192 Verilog"
AETHERLING_MARKER = "s"
AETHERLING_MARKER_SIZE = 4
OUR_LABEL = "Aetherling \u2192 Min. IR \u2192 VHDL"
OUR_MARKER = "o"
OUR_MARKER_SIZE = 3


def dedup(xs: list[str]) -> list[str]:
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
            [1/4, 1, 2, 4],
            [r"$\frac{1}{4}$", "1", "2", "4"],
        )
    elif bench_name.startswith("big"):
        ax.set_xticks(
            [1/3, 1, 2, 4, 8],
            [r"$\frac{1}{3}$", "1", "2", "4", "8"]
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
            [1, 2, 4, 8, 20],
            ["1", "2", "4", "8", "20"]
        )


def plot_resource_usages(results: dict[BenchmarkImpl, ResourceUsage]) -> None:
    """
    Plot resource usage vs throughput for each benchmark.
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
        nrows=3, ncols=len(benchmark_names),
        squeeze=False,
        figsize=(8, 3.5),
        layout="compressed",
        sharey="row",
        sharex="col",
    )
    verilog_artist = None
    vhdl_artist = None
    for col, bench_name in enumerate(benchmark_names):
        title = lb.benchmark_title(bench_name)
        if title is None:
            continue
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
        # xscale = axis_scale(bench_name)
        # yscale = xscale
        # Plot ALM usage
        alm_ax = axes[0][col]
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        ys = [results[b].alm for b in verilog_benchmarks]
        verilog_artist, = alm_ax.plot(
            xs, ys,
            marker=AETHERLING_MARKER, markersize=AETHERLING_MARKER_SIZE,
            label=AETHERLING_LABEL,
        )
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        ys = [results[b].alm for b in vhdl_benchmarks]
        vhdl_artist, = alm_ax.plot(
            xs, ys,
            marker=OUR_MARKER, markersize=OUR_MARKER_SIZE,
            label=OUR_LABEL,
        )
        # Plot BRAM usage
        bram_ax = axes[1][col]
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        verilog_ys = [results[b].bram for b in verilog_benchmarks]
        bram_ax.plot(
            xs, verilog_ys,
            marker=AETHERLING_MARKER, markersize=AETHERLING_MARKER_SIZE,
            label=AETHERLING_LABEL,
        )
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        vhdl_ys = [results[b].bram for b in vhdl_benchmarks]
        bram_ax.plot(
            xs, vhdl_ys,
            marker=OUR_MARKER, markersize=OUR_MARKER_SIZE,
            label=OUR_LABEL,
        )
        # Plot DSP usage
        dsp_ax = axes[2][col]
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        verilog_ys = [results[b].dsp for b in verilog_benchmarks]
        dsp_ax.plot(
            xs, verilog_ys,
            marker=AETHERLING_MARKER, markersize=AETHERLING_MARKER_SIZE,
            label=AETHERLING_LABEL,
        )
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        vhdl_ys = [results[b].dsp for b in vhdl_benchmarks]
        dsp_ax.plot(
            xs, vhdl_ys,
            marker=OUR_MARKER, markersize=OUR_MARKER_SIZE,
            label=OUR_LABEL,
        )
        # Settings for the whole column
        alm_ax.set_title(title)
        alm_ax.set_xscale("log", base=2)
        set_ticks(alm_ax, bench_name)
    # Settings for entire rows
    axes[0][0].set_ylabel("ALMs")
    axes[0][0].set_yscale("log")
    axes[0][0].yaxis.set_major_locator(LogLocator(base=10))
    axes[1][0].set_ylabel("BRAMs")
    axes[1][0].set_yscale("log")
    axes[1][0].yaxis.set_major_locator(LogLocator(base=10))
    axes[2][0].set_ylabel("DSPs")
    axes[2][0].set_yscale("log")
    axes[2][0].yaxis.set_major_locator(LogLocator(base=10))
    fig.supxlabel("Target throughput (px/cycle)")
    if verilog_artist is None or vhdl_artist is None:
        raise RuntimeError("Cannot create legend due to missing artists.")
    fig.legend(
        [vhdl_artist, verilog_artist],
        [OUR_LABEL, AETHERLING_LABEL],
        loc="lower center",
        bbox_to_anchor=(0.5, -0.15)
    )
    fig.savefig(c.RESOURCE_USAGE_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    results = crud.read_valid_resource_usage_results(c.RESOURCE_USAGE_CSV)
    if not results:
        sys.exit("No results to plot.")
    plot_resource_usages(results)


if __name__ == "__main__":
    main()
