#!/bin/python3

"""
This script plots the compile time for the Aetherling benchmarks.
"""

import sys
from typing import TypeVar

import matplotlib.pyplot as plt

import lib.benchmark as lb
import lib.constants as c
import lib.plt_utils as pu
import lib.results_crud as crud
from lib.benchmark import Benchmark, set_ticks
from lib.compile_time import CompileTimeReport

T = TypeVar("T")


COLORS = ["#ffffcc", "#c7e9b4", "#7fcdbb", "#41b6c4", "#2c7fb8", "#253494"]
HATCH = ["//", "\\\\", ""]


def plot_compile_times(results: dict[Benchmark, CompileTimeReport]) -> None:
    """
    Plot compile time vs throughput for each benchmark.
    """
    benchmark_names = pu.dedup([b.name for b in results.keys()])
    benchmark_names = sorted(benchmark_names, key=lb.benchmark_order)
    benchmark_names = [b for b in benchmark_names if lb.benchmark_title(b) is not None]
    if not benchmark_names:
        raise ValueError("No benchmarks to plot.")
    plt.rcParams.update({
        "text.usetex": True,
        "font.family": "Times New Roman",
        "font.size": 8,
    })
    fig, axes = plt.subplots(
        nrows=1, ncols=len(benchmark_names),
        figsize=(8, 1.5),
        squeeze=False,
        sharey="row",
        layout="compressed",
    )
    axes = axes[0]
    vhdl_artist = []
    for col, bench_name in enumerate(benchmark_names):
        title = lb.benchmark_title(bench_name)
        if title is None:
            continue
        ax = axes[col]
        vhdl_benchmarks = [b for b in results.keys() if b.name == bench_name]
        vhdl_benchmarks = sorted(vhdl_benchmarks, key=lambda b: b.throughput)
        xs = [float(b.throughput) for b in vhdl_benchmarks]
        ys = {
            "parse": [results[b].parse / 1000 for b in vhdl_benchmarks],
            "typecheck": [results[b].typecheck / 1000 for b in vhdl_benchmarks],
            "lower": [results[b].lower / 1000 for b in vhdl_benchmarks],
            "make synth.": [results[b].make_synth / 1000 for b in vhdl_benchmarks],
            "optimize": [results[b].optimize / 1000 for b in vhdl_benchmarks],
            "codegen": [results[b].codegen / 1000 for b in vhdl_benchmarks],
        }
        vhdl_artist = ax.stackplot(
            xs,
            list(ys.values()),
            labels=list(ys.keys()),
            colors=COLORS,
            hatch=HATCH,
            hatch_linewidth=0.25,
        )
        # Labels and whatnot
        ax.set_xscale("log", base=2)
        ax.set_title(title)
        set_ticks(ax, bench_name)

    # Settings for entire rows
    axes[0].set_ylabel("Compile time (s)")
    fig.supxlabel("Target throughput (px/cycle)")
    ncol = 6
    fig.legend(
        handles=pu.flip(vhdl_artist, ncol), # pyright: ignore[reportArgumentType]
        loc="upper center",
        bbox_to_anchor=(0.5, 0),
        ncols=ncol,
    )
    fig.savefig(c.AETHERLING_COMPILE_TIME_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    results = crud.read_compile_times(c.AETHERLING_COMPILE_TIME_CSV)
    if not results:
        sys.exit("No results to plot.")
    plot_compile_times(results)


if __name__ == "__main__":
    main()
