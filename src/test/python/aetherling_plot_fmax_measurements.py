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
from lib.fmax import Fmax

AETHERLING_LABEL = "Aetherling \u2192 Chisel \u2192 Verilog"
AETHERLING_MARKER = "s"
OUR_LABEL = "Aetherling \u2192 Min. IR \u2192 VHDL"
OUR_MARKER = "o"
DEFAULT_ERR = 10


def dedup(xs: list[str]) -> list[str]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


def plot_fmax(results: dict[BenchmarkImpl, Fmax]) -> None:
    """
    Plot fmax vs throughput for each benchmark.
    """
    benchmark_names = dedup([res.bench.name for res in results.keys()])
    benchmark_names = sorted(benchmark_names, key=lb.benchmark_order)
    if not benchmark_names:
        raise ValueError("No benchmarks to plot.")
    fig, axes = plt.subplots(nrows=1, ncols=len(benchmark_names), squeeze=False)
    axes = axes[0]
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
        # Plot fmax
        ax = axes[col]
        xs = []
        ys = []
        loerr = []
        uperr = []
        lolims = []
        uplims = []
        for b in verilog_benchmarks:
            xs.append(float(b.bench.throughput))
            fmax = results[b]
            ys.append(fmax.lower)
            if fmax.lower is None:
                assert fmax.upper is not None
                loerr.append(DEFAULT_ERR)
                uperr.append(0)
                lolims.append(False)
                uplims.append(True)
            elif fmax.upper is None:
                loerr.append(0)
                uperr.append(DEFAULT_ERR)
                lolims.append(True)
                uplims.append(False)
            else:
                loerr.append(0)
                uperr.append(fmax.upper - fmax.lower - 1)
                lolims.append(False)
                uplims.append(False)
        verilog_artist, *_ = ax.errorbar(
            xs,
            ys,
            yerr=[loerr, uperr],
            lolims=lolims,
            uplims=uplims,
            marker=AETHERLING_MARKER,
            label=AETHERLING_LABEL,
        )
        xs = []
        ys = []
        loerr = []
        uperr = []
        lolims = []
        uplims = []
        for b in vhdl_benchmarks:
            xs.append(float(b.bench.throughput))
            fmax = results[b]
            ys.append(fmax.lower)
            if fmax.lower is None:
                assert fmax.upper is not None
                loerr.append(DEFAULT_ERR)
                uperr.append(0)
                lolims.append(False)
                uplims.append(True)
            elif fmax.upper is None:
                loerr.append(0)
                uperr.append(DEFAULT_ERR)
                lolims.append(True)
                uplims.append(False)
            else:
                loerr.append(0)
                uperr.append(fmax.upper - fmax.lower - 1)
                lolims.append(False)
                uplims.append(False)
        vhdl_artist, *_ = ax.errorbar(
            xs,
            ys,
            yerr=[loerr, uperr],
            lolims=lolims,
            uplims=uplims,
            marker=OUR_MARKER,
            label=OUR_LABEL,
        )
        ax.set_title(bench_name)
        ax.set_xlabel("Target throughput")
        ax.tick_params(axis="x", rotation=30)
    # Settings for entire rows
    axes[0].set_ylabel("fmax")
    if verilog_artist is None or vhdl_artist is None:
        raise RuntimeError("Cannot create legend due to missing artists.")
    fig.legend(
        [vhdl_artist, verilog_artist],
        [OUR_LABEL, AETHERLING_LABEL],
        loc="lower center",
        bbox_to_anchor=(0.5, -0.1)
    )
    fig.tight_layout()
    fig.savefig(c.FMAX_MEASUREMENT_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    results = crud.read_valid_fmax_measurements(c.FMAX_MEASUREMENT_CSV)
    if not results:
        sys.exit("No results to plot.")
    plot_fmax(results)


if __name__ == "__main__":
    main()
