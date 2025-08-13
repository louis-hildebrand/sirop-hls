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
AETHERLING_MARKER = "^"
OUR_LABEL = "Aetherling \u2192 Min. IR \u2192 VHDL"
OUR_MARKER = "o"


def dedup(xs: list[str]) -> list[str]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


def plot_resource_usages(results: dict[BenchmarkImpl, ResourceUsage]) -> None:
    """
    Plot resource usage vs throughput for each benchmark.
    """
    plt.rcParams.update({
        "text.usetex": True
    })
    benchmark_names = dedup([res.bench.name for res in results.keys()])
    benchmark_names = sorted(benchmark_names, key=lb.benchmark_order)
    if not benchmark_names:
        raise ValueError("No benchmarks to plot.")
    fig, axes = plt.subplots(nrows=3, ncols=len(benchmark_names), squeeze=False)
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
        xscale = "log" if bench_name == "map" else "linear"
        yscale = xscale
        # Plot ALM usage
        alm_ax = axes[0][col]
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        ys = [results[b].alm for b in verilog_benchmarks]
        verilog_artist, = alm_ax.plot(xs, ys, marker=AETHERLING_MARKER, label=AETHERLING_LABEL)
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        ys = [results[b].alm for b in vhdl_benchmarks]
        vhdl_artist, = alm_ax.plot(xs, ys, marker=OUR_MARKER, label=OUR_LABEL)
        alm_ax.set_xscale(xscale)
        alm_ax.set_yscale(yscale)
        # Plot BRAM usage
        bram_ax = axes[1][col]
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        verilog_ys = [results[b].bram for b in verilog_benchmarks]
        bram_ax.plot(xs, verilog_ys, marker=AETHERLING_MARKER, label=AETHERLING_LABEL)
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        vhdl_ys = [results[b].bram for b in vhdl_benchmarks]
        bram_ax.plot(xs, vhdl_ys, marker=OUR_MARKER, label=OUR_LABEL)
        all_zero = all(y == 0 for y in verilog_ys) and all(y == 0 for y in vhdl_ys)
        if all_zero:
            bram_ax.set_ylim(-1, 1)
        bram_ax.set_xscale(xscale)
        if not all_zero:
            bram_ax.set_yscale(yscale)
        # Plot DSP usage
        dsp_ax = axes[2][col]
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        verilog_ys = [results[b].dsp for b in verilog_benchmarks]
        dsp_ax.plot(xs, verilog_ys, marker=AETHERLING_MARKER, label=AETHERLING_LABEL)
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        vhdl_ys = [results[b].dsp for b in vhdl_benchmarks]
        dsp_ax.plot(xs, vhdl_ys, marker=OUR_MARKER, label=OUR_LABEL)
        all_zero = all(y == 0 for y in verilog_ys) and all(y == 0 for y in vhdl_ys)
        if all_zero:
            dsp_ax.set_ylim(-1, 1)
        dsp_ax.set_xscale(xscale)
        if not all_zero:
            dsp_ax.set_yscale(yscale)
        # Settings for the whole column
        alm_ax.set_title(bench_name)
        dsp_ax.set_xlabel("Target throughput")
        alm_ax.set_xticks([])
        bram_ax.set_xticks([])
        dsp_ax.tick_params(axis="x", rotation=30)
    # Settings for entire rows
    axes[0][0].set_ylabel("ALMs")
    axes[1][0].set_ylabel("BRAMs")
    axes[2][0].set_ylabel("DSPs")
    if verilog_artist is None or vhdl_artist is None:
        raise RuntimeError("Cannot create legend due to missing artists.")
    fig.legend(
        [vhdl_artist, verilog_artist],
        [OUR_LABEL, AETHERLING_LABEL],
        loc="lower center",
        bbox_to_anchor=(0.5, -0.1)
    )
    fig.tight_layout()
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
