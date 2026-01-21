#!/usr/bin/env python3

"""
This script plots the latency for the Aetherling benchmarks.
"""

from typing import TypeVar

import matplotlib.pyplot as plt
from matplotlib.lines import Line2D
from matplotlib.patches import Polygon

import lib.benchmark as lb
import lib.constants as c
import lib.results_crud as crud
from lib.benchmark import BenchmarkImpl, set_ticks
from lib.latency import LatencyResult

DEFAULT_COLORS = plt.rcParams["axes.prop_cycle"].by_key()["color"]


MIN_LABEL = "Best possible latency"
MIN_MARKER = ""
LINEWIDTH = 1


T = TypeVar("T")


def dedup(xs: list[T]) -> list[T]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


def plot_latency(
    results: dict[BenchmarkImpl, LatencyResult],
    fmax_results: dict[BenchmarkImpl, float],
) -> None:
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
        "font.size": 7,
    })
    fig, axes = plt.subplots(
        nrows=1, ncols=len(benchmark_names),
        figsize=(8, 1.175),
        squeeze=False,
        sharey="row",
        layout="compressed",
    )
    axes = axes[0]
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
        verilog_fmax_ok = [
            b in fmax_results and fmax_results[b] >= c.TARGET_FREQ
            for b in verilog_benchmarks
        ]
        vhdl_benchmarks = [
            b
            for b in results.keys()
            if b.bench.name == bench_name and b.language == "vhdl"
        ]
        vhdl_benchmarks = sorted(
            vhdl_benchmarks,
            key=lambda b: b.bench.throughput,
        )
        vhdl_fmax_ok = [
            b in fmax_results and fmax_results[b] >= c.TARGET_FREQ
            for b in vhdl_benchmarks
        ]
        # Minimum results
        all_benchmarks = dedup(vhdl_benchmarks + verilog_benchmarks)
        all_benchmarks = sorted(all_benchmarks, key=lambda b: b.bench.throughput)
        xs = [float(b.bench.throughput) for b in all_benchmarks]
        ys = [lb.min_latency(b.bench) for b in all_benchmarks]
        ax.plot(
            xs, ys,
            marker=MIN_MARKER,
            label=MIN_LABEL,
            linestyle="-",
            linewidth=LINEWIDTH,
            color="black",
            zorder=0,
        )
        # Verilog results (only successful simulation)
        xs = [float(b.bench.throughput) for b in verilog_benchmarks if results[b].sim_success]
        ys = [results[b].latency for b in verilog_benchmarks if results[b].sim_success]
        fmax_ok = [
            ok
            for (ok, b) in zip(verilog_fmax_ok, verilog_benchmarks)
            if results[b].sim_success
        ]
        ax.scatter(
            xs, ys,
            marker=c.AETHERLING_MARKER,
            s=c.AETHERLING_MARKER_SIZE,
            edgecolor=c.AETHERLING_COLOR,
            linewidth=1,
            facecolor=[c.AETHERLING_COLOR if ok else "white" for ok in fmax_ok],
            label=c.AETHERLING_LABEL,
            zorder=10,
        )
        # VHDL results (only successful simulation)
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks if results[b].sim_success]
        ys = [results[b].latency for b in vhdl_benchmarks if results[b].sim_success]
        fmax_ok = [ok for (ok, b) in zip(vhdl_fmax_ok, vhdl_benchmarks) if results[b].sim_success]
        ax.scatter(
            xs, ys,
            marker=c.OUR_MARKER,
            s=c.OUR_MARKER_SIZE,
            edgecolor=c.OUR_COLOR,
            linewidth=1,
            facecolor=[c.OUR_COLOR if ok else "white" for ok in vhdl_fmax_ok],
            label=c.OUR_LABEL,
            zorder=11,
        )
        # Labels and whatnot
        ax.set_xscale("log", base=2)
        ax.set_title(title)
        set_ticks(ax, bench_name)

    # Settings for entire rows
    axes[0].set_yscale("log", base=10)
    axes[0].set_ylabel("Latency (log)\t.")
    _, y_hi = axes[0].get_ylim()
    axes[0].set_ylim(1, y_hi * 2)
    axes[0].set_yticks([10, 10**3, 10**5, 10**7])
    for ax in axes:
        ax.grid(
            visible=True,
            which="major",
            axis="y",
            linewidth=0.2,
            color=(0.8, 0.8, 0.8)
        )
    fig.supxlabel("Target throughput (px/cycle)")
    fig.text(0.0275, 0.07, "Lower is better")
    down_arrow = Polygon(
        [(0.0075, 0.13), (0.0175, 0.13), (0.0125, 0.07)],
        fill=True, color='black', zorder=1000,
        transform=fig.transFigure, figure=fig
    )
    fig.patches.extend([down_arrow])
    fig.legend(
        handles=[
            Line2D(
                [0], [0],
                label="Best possible latency",
                linestyle="-",
                linewidth=LINEWIDTH,
                color="black",
            ),
            Line2D(
                [0], [0],
                label=c.OUR_LABEL,
                color=c.OUR_COLOR,
                linewidth=LINEWIDTH,
                marker=c.OUR_MARKER,
                markeredgecolor=c.OUR_COLOR,
                markerfacecolor=c.OUR_COLOR,
            ),
            Line2D(
                [0], [0],
                label=c.OUR_LABEL_BLANK,
                color=c.OUR_COLOR,
                linewidth=LINEWIDTH,
                marker=c.OUR_MARKER,
                markeredgecolor=c.OUR_COLOR,
                markerfacecolor="white",
            ),
            Line2D(
                [0], [0],
                label=c.AETHERLING_LABEL,
                color=c.AETHERLING_COLOR,
                linewidth=LINEWIDTH,
                marker=c.AETHERLING_MARKER,
                markeredgecolor=c.AETHERLING_COLOR,
                markerfacecolor=c.AETHERLING_COLOR,
            ),
            Line2D(
                [0], [0],
                label=c.AETHERLING_LABEL_BLANK,
                color=c.AETHERLING_COLOR,
                linewidth=LINEWIDTH,
                marker=c.AETHERLING_MARKER,
                markeredgecolor=c.AETHERLING_COLOR,
                markerfacecolor="white",
            ),
        ],
        loc="upper center",
        bbox_to_anchor=(0.5, 0),
        ncols=5,
    )
    fig.savefig(c.LATENCY_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    latency_results = crud.read_valid_latency_results(c.LATENCY_CSV)
    fmax_results = crud.read_valid_fmax_estimates(c.FMAX_ESTIMATE_CSV)
    plot_latency(latency_results, fmax_results)


if __name__ == "__main__":
    main()
