#!/usr/bin/env python3

"""
This script plots the resource usages for the Aetherling benchmarks.
"""

import matplotlib.pyplot as plt
from matplotlib.lines import Line2D
from matplotlib.ticker import LogLocator

import lib.benchmark as lb
import lib.constants as c
import lib.results_crud as crud
from lib.benchmark import BenchmarkImpl, set_ticks
from lib.resource_usage import ResourceUsage

LINEWIDTH = 1


def dedup(xs: list[str]) -> list[str]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


def plot_resource_usages(
    results: dict[BenchmarkImpl, ResourceUsage],
    fmax_results: dict[BenchmarkImpl, float]
) -> None:
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
        figsize=(8, 2.5),
        layout="compressed",
        sharey="row",
        sharex="col",
    )
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
        verilog_fmax_ok = [fmax_results[b] >= c.TARGET_FREQ for b in verilog_benchmarks]
        vhdl_benchmarks = [
            b
            for b in results.keys()
            if b.bench.name == bench_name and b.language == "vhdl"
        ]
        vhdl_benchmarks = sorted(
            vhdl_benchmarks,
            key=lambda b: (b.language, b.bench.throughput)
        )
        vhdl_fmax_ok = [fmax_results[b] >= c.TARGET_FREQ for b in vhdl_benchmarks]
        # Plot ALM usage
        alm_ax = axes[0][col]
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        ys = [results[b].alm for b in verilog_benchmarks]
        alm_ax.plot(  # line
            xs, ys,
            color=c.AETHERLING_COLOR,
            label=c.AETHERLING_LABEL,
            zorder=10,
        )
        alm_ax.scatter(  # markers (separate so that some can be filled and some not)
            xs, ys,
            marker=c.AETHERLING_MARKER,
            s=c.AETHERLING_MARKER_SIZE,
            edgecolor=c.AETHERLING_COLOR,
            linewidth=LINEWIDTH,
            facecolor=[c.AETHERLING_COLOR if ok else "white" for ok in verilog_fmax_ok],
            label=c.AETHERLING_LABEL,
            zorder=11,
        )
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        ys = [results[b].alm for b in vhdl_benchmarks]
        alm_ax.plot(  # line
            xs, ys,
            color=c.OUR_COLOR,
            label=c.OUR_LABEL,
            zorder=12,
        )
        alm_ax.scatter(  # markers
            xs, ys,
            marker=c.OUR_MARKER,
            s=c.OUR_MARKER_SIZE,
            edgecolor=c.OUR_COLOR,
            linewidth=LINEWIDTH,
            facecolor=[c.OUR_COLOR if ok else "white" for ok in vhdl_fmax_ok],
            label=c.OUR_LABEL,
            zorder=13,
        )
        # Plot BRAM usage
        bram_ax = axes[1][col]
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        verilog_ys = [results[b].bram for b in verilog_benchmarks]
        bram_ax.plot(  # line
            xs, verilog_ys,
            color=c.AETHERLING_COLOR,
            label=c.AETHERLING_LABEL,
            zorder=10,
        )
        bram_ax.scatter(  # markers
            xs, verilog_ys,
            marker=c.AETHERLING_MARKER,
            s=c.AETHERLING_MARKER_SIZE,
            edgecolor=c.AETHERLING_COLOR,
            linewidth=LINEWIDTH,
            facecolor=[c.AETHERLING_COLOR if ok else "white" for ok in verilog_fmax_ok],
            label=c.AETHERLING_LABEL,
            zorder=11,
        )
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        vhdl_ys = [results[b].bram for b in vhdl_benchmarks]
        bram_ax.plot(  # line
            xs, vhdl_ys,
            color=c.OUR_COLOR,
            label=c.OUR_LABEL,
            zorder=12,
        )
        bram_ax.scatter(  # markers
            xs, vhdl_ys,
            marker=c.OUR_MARKER,
            s=c.OUR_MARKER_SIZE,
            edgecolor=c.OUR_COLOR,
            linewidth=LINEWIDTH,
            facecolor=[c.OUR_COLOR if ok else "white" for ok in vhdl_fmax_ok],
            label=c.OUR_LABEL,
            zorder=13,
        )
        # Plot DSP usage
        dsp_ax = axes[2][col]
        xs = [float(b.bench.throughput) for b in verilog_benchmarks]
        verilog_ys = [results[b].dsp for b in verilog_benchmarks]
        dsp_ax.plot(  # line
            xs, verilog_ys,
            color=c.AETHERLING_COLOR,
            label=c.AETHERLING_LABEL,
            zorder=10,
        )
        dsp_ax.scatter(  # markers
            xs, verilog_ys,
            marker=c.AETHERLING_MARKER,
            s=c.AETHERLING_MARKER_SIZE,
            edgecolor=c.AETHERLING_COLOR,
            linewidth=LINEWIDTH,
            facecolor=[c.AETHERLING_COLOR if ok else "white" for ok in verilog_fmax_ok],
            label=c.AETHERLING_LABEL,
            zorder=11,
        )
        xs = [float(b.bench.throughput) for b in vhdl_benchmarks]
        vhdl_ys = [results[b].dsp for b in vhdl_benchmarks]
        dsp_ax.plot(  # line
            xs, vhdl_ys,
            color=c.OUR_COLOR,
            label=c.OUR_LABEL,
            zorder=12,
        )
        dsp_ax.scatter(  # markers
            xs, vhdl_ys,
            marker=c.OUR_MARKER,
            s=c.OUR_MARKER_SIZE,
            edgecolor=c.OUR_COLOR,
            linewidth=1,
            facecolor=[c.OUR_COLOR if ok else "white" for ok in vhdl_fmax_ok],
            label=c.OUR_LABEL,
            zorder=13,
        )
        # Settings for the whole column
        alm_ax.set_title(title)
        alm_ax.set_xscale("log", base=2)
        set_ticks(alm_ax, bench_name)
        # HACK: get the axes to line up with the latency plot
        if bench_name == "map":
            alm_ax.plot([200], [100], color="#00000000")
        elif "big" in bench_name and bench_name not in {"bigmvm", "bigmmm"}:
            alm_ax.plot([16], [100], color="#00000000")
    # Settings for entire rows
    axes[0][0].set_ylabel("ALM (log)")
    axes[0][0].set_yscale("log")
    # axes[0][0].yaxis.set_major_locator(LogLocator(base=10))
    axes[0][0].set_yticks([10, 10**2, 10**3, 10**4])
    for ax in axes[0]:
        ax.grid(
            visible=True,
            which="major",
            axis="y",
            linewidth=0.2,
            color=(0.8, 0.8, 0.8)
        )
    axes[1][0].set_ylabel("BRAM (log)")
    axes[1][-1].set_yscale("symlog")
    axes[1][-1].yaxis.set_major_locator(LogLocator(base=10))
    axes[1][-1].set_ylim(0.11, 10**3)
    for ax in axes[1]:
        ax.grid(
            visible=True,
            which="major",
            axis="y",
            linewidth=0.2,
            color=(0.8, 0.8, 0.8)
        )
    axes[2][0].set_ylabel("DSP (log)")
    axes[2][-1].set_yscale("symlog")
    axes[2][-1].yaxis.set_major_locator(LogLocator(base=10))
    axes[2][-1].set_ylim(0.11, 500)
    for ax in axes[2]:
        ax.grid(
            visible=True,
            which="major",
            axis="y",
            linewidth=0.2,
            color=(0.8, 0.8, 0.8)
        )
    fig.supxlabel("Target throughput (px/cycle)")
    fig.legend(
        handles=[
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
        ncols=4,
    )
    fig.savefig(c.RESOURCE_USAGE_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    area_results = crud.read_valid_resource_usage_results(c.RESOURCE_USAGE_CSV)
    fmax_results = crud.read_valid_fmax_estimates(c.FMAX_ESTIMATE_CSV)
    plot_resource_usages(area_results, fmax_results)


if __name__ == "__main__":
    main()
