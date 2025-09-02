#!/bin/python3

"""
This script plots the resource usages for the Aetherling benchmarks.
"""

import sys

import matplotlib.pyplot as plt

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


def benchmark_title(bench_name: str) -> str | None:
    """
    Return the title to put at the top of the column for the given benchmark, or `None` if the
    results for this benchmark should be omitted.
    """
    if bench_name.startswith("small"):
        return None
    if bench_name.startswith("big"):
        bench_name = bench_name[len("big"):]
    return bench_name


def dedup(xs: list[str]) -> list[str]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


def plot_resource_usages(results: dict[BenchmarkImpl, ResourceUsage]) -> None:
    """
    Plot resource usage vs throughput for each benchmark.
    """
    benchmark_names = dedup([res.bench.name for res in results.keys()])
    benchmark_names = sorted(benchmark_names, key=lb.benchmark_order)
    benchmark_names_to_plot = [b for b in benchmark_names if benchmark_title(b) is not None]
    if not benchmark_names:
        raise ValueError("No benchmarks to plot.")
    fig, axes = plt.subplots(
        nrows=3, ncols=len(benchmark_names_to_plot),
        squeeze=False,
        figsize=(8, 4),
        layout="compressed",
        sharey="row",
        sharex="col",
    )
    verilog_artist = None
    vhdl_artist = None
    for col, bench_name in enumerate(benchmark_names_to_plot):
        title = benchmark_title(bench_name)
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
    # Settings for entire rows
    axes[0][0].set_ylabel("ALMs")
    axes[0][0].set_yscale("log")
    axes[1][0].set_ylabel("BRAMs")
    axes[1][0].set_yscale("log")
    axes[2][0].set_ylabel("DSPs")
    axes[2][0].set_yscale("log")
    fig.supxlabel("Target throughput")
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
