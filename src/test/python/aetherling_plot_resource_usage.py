#!/bin/python3

"""
This script plots the resource usages for the Aetherling benchmarks.
"""

import sys

import matplotlib.pyplot as plt
from matplotlib.ticker import LogLocator

import lib.benchmark as lb
import lib.constants as c
import lib.results_crud as crud
from lib.benchmark import BenchmarkImpl, set_ticks
from lib.resource_usage import ResourceUsage


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
    benchmark_names = [b for b in benchmark_names if lb.benchmark_title(b) is not None]
    if not benchmark_names:
        raise ValueError("No benchmarks to plot.")
    plt.rcParams.update({
        "text.usetex": True,
        "font.family": "Times New Roman",
        "font.size": 7,
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
            marker=c.AETHERLING_MARKER, markersize=c.AETHERLING_MARKER_SIZE,
            color=c.AETHERLING_COLOR,
            label=c.AETHERLING_LABEL,
        )
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        ys = [results[b].alm for b in vhdl_benchmarks]
        vhdl_artist, = alm_ax.plot(
            xs, ys,
            marker=c.OUR_MARKER, markersize=c.OUR_MARKER_SIZE,
            color=c.OUR_COLOR,
            label=c.OUR_LABEL,
        )
        # Plot BRAM usage
        bram_ax = axes[1][col]
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        verilog_ys = [results[b].bram for b in verilog_benchmarks]
        bram_ax.plot(
            xs, verilog_ys,
            marker=c.AETHERLING_MARKER, markersize=c.AETHERLING_MARKER_SIZE,
            color=c.AETHERLING_COLOR,
            label=c.AETHERLING_LABEL,
        )
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        vhdl_ys = [results[b].bram for b in vhdl_benchmarks]
        bram_ax.plot(
            xs, vhdl_ys,
            marker=c.OUR_MARKER, markersize=c.OUR_MARKER_SIZE,
            color=c.OUR_COLOR,
            label=c.OUR_LABEL,
        )
        # Plot DSP usage
        dsp_ax = axes[2][col]
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        verilog_ys = [results[b].dsp for b in verilog_benchmarks]
        dsp_ax.plot(
            xs, verilog_ys,
            marker=c.AETHERLING_MARKER, markersize=c.AETHERLING_MARKER_SIZE,
            color=c.AETHERLING_COLOR,
            label=c.AETHERLING_LABEL,
        )
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        vhdl_ys = [results[b].dsp for b in vhdl_benchmarks]
        dsp_ax.plot(
            xs, vhdl_ys,
            marker=c.OUR_MARKER, markersize=c.OUR_MARKER_SIZE,
            color=c.OUR_COLOR,
            label=c.OUR_LABEL,
        )
        # Settings for the whole column
        alm_ax.set_title(title)
        alm_ax.set_xscale("log", base=2)
        set_ticks(alm_ax, bench_name)
        # HACK: get the axes to line up with the latency plot
        if bench_name == "map":
            alm_ax.plot([200], [100], color="#00000000")
        elif "big" in bench_name:
            alm_ax.plot([16], [100], color="#00000000")
    # Settings for entire rows
    axes[0][0].set_ylabel("ALM (log)")
    axes[0][0].set_yscale("log")
    axes[0][0].yaxis.set_major_locator(LogLocator(base=10))
    axes[1][0].set_ylabel("BRAM (log)")
    axes[1][-1].set_yscale("symlog")
    axes[1][-1].yaxis.set_major_locator(LogLocator(base=10))
    axes[1][-1].set_ylim(0.11, 10**3)
    axes[2][0].set_ylabel("DSP (log)")
    axes[2][-1].set_yscale("symlog")
    axes[2][-1].yaxis.set_major_locator(LogLocator(base=10))
    axes[2][-1].set_ylim(0.11, 500)
    fig.supxlabel("Target throughput (px/cycle)")
    if verilog_artist is None or vhdl_artist is None:
        raise RuntimeError("Cannot create legend due to missing artists.")
    fig.legend(
        [verilog_artist, vhdl_artist],
        [c.AETHERLING_LABEL, c.OUR_LABEL],
        loc="upper center",
        bbox_to_anchor=(0.5, 0),
        ncols=2,
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
