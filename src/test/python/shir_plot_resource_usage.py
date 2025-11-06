#!/bin/python3

"""
This script plots the resource usages for the SHIR benchmarks.
"""

import statistics
from fractions import Fraction

import matplotlib.pyplot as plt
import matplotlib.ticker as tick
from matplotlib.patches import Polygon, Rectangle

import lib.constants as c
import lib.plt_utils as pu
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl, benchmark_order
from lib.resource_usage import ResourceUsage

BAR_SPACE = 0.2
BAR_WIDTH = (1 - BAR_SPACE) / 2
BAR_PADDING = 0.02
SHIR_HATCH = "/"
OUR_HATCH = "\\"


def benchmark_title(bench_name: str) -> str | None:
    """
    Return the title to put at the top of the column for the given benchmark, or `None` if the
    results for this benchmark should be omitted from the plots.
    """
    if bench_name.startswith("small"):
        return None
    if bench_name in {"sum", "sqrt"}:
        return None
    if bench_name.startswith("big"):
        bench_name = bench_name[len("big"):]
    if bench_name == "mvm":
        bench_name = "matvec"
    return f"\\texttt{{{bench_name}}}"


def plot_resource_usages(
    results: dict[BenchmarkImpl, ResourceUsage],
    fmax_results: dict[BenchmarkImpl, float],
) -> None:
    """
    Plot resource usage for each program.
    """
    program_names = pu.dedup([
        b.bench.name
        for b in results.keys()
    ])
    program_names = sorted(program_names, key=benchmark_order)
    if not program_names:
        raise ValueError("Nothing to plot.")
    plt.rcParams.update({
        "text.usetex": True,
        "font.family": "Times New Roman",
        "font.size": 8,
    })
    fig, (alm_ax, bram_ax, dsp_ax) = plt.subplots(
        nrows=3, ncols=1,
        figsize=(4, 2.1),
        layout="compressed",
        sharex="col",
        sharey="row",
    )

    shir_fmax_ok = [
        fmax_results[BenchmarkImpl(Benchmark(prog, Fraction(-1)), "shir")] >= 175
        for prog in program_names
    ]
    assert all(shir_fmax_ok), "need to show SHIR fmax somehow"
    sirop_fmax_ok = [
        fmax_results[BenchmarkImpl(Benchmark(prog, Fraction(-1)), "sirop")] >= 175
        for prog in program_names
    ]
    assert all(sirop_fmax_ok), "need to show Sirop fmax somehow"

    shir_alms = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].alm
        for p in program_names
    ]
    sirop_alms = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")].alm
        for p in program_names
    ]
    alm_ratio_geomean = statistics.geometric_mean(
        [sirop / shir for (sirop, shir) in zip(sirop_alms, shir_alms)]
    )
    print(f"ALM ratio geomean: {alm_ratio_geomean:.2f}")

    # Resource usages
    xs = list(range(len(program_names)))
    # ALM usage
    alm_ax.bar(
        bottom=0,
        x=xs,
        height=shir_alms,
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.SHIR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=SHIR_HATCH,
    )
    sirop_alm_container = alm_ax.bar(
        bottom=0,
        x=[x + BAR_WIDTH for x in xs],
        height=sirop_alms,
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.OUR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=OUR_HATCH,
    )
    # ALM usage ratios
    labels = []
    for (shir_alm, sirop_alm) in zip(shir_alms, sirop_alms):
        label = f"${sirop_alm/shir_alm:.2f}\\times$"
        labels.append(label)
    alm_ax.bar_label(
        sirop_alm_container,
        labels=labels,
        label_type="edge",
    )
    # BRAM usage
    shir_brams = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].bram
        for p in program_names
    ]
    bram_ax.bar(
        bottom=0,
        x=xs,
        height=shir_brams,
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.SHIR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=SHIR_HATCH,
    )
    sirop_brams = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")].bram
        for p in program_names
    ]
    bram_ax.bar(
        bottom=0,
        x=[x + BAR_WIDTH for x in xs],
        height=sirop_brams,
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.OUR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=OUR_HATCH,
    )
    # DSP usage
    shir_dsps = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].dsp
        for p in program_names
    ]
    dsp_ax.bar(
        bottom=0,
        x=xs,
        height=shir_dsps,
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.SHIR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=SHIR_HATCH,
    )
    sirop_dsps = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")].dsp
        for p in program_names
    ]
    dsp_ax.bar(
        bottom=0,
        x=[x + BAR_WIDTH for x in xs],
        height=sirop_dsps,
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.OUR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=OUR_HATCH,
    )

    # Display settings
    xlim = (
        -0.5*BAR_WIDTH - 0.2*BAR_SPACE,
        len(program_names) - 0.5*BAR_WIDTH - 0.5*BAR_SPACE
    )
    alm_ax.set_xlim(xlim)
    alm_ax.set_xticks(
        [x + BAR_WIDTH/2 for x in xs],
        [benchmark_title(p) or "NONE" for p in program_names]
    )
    alm_ax.tick_params(axis="x", which="both", length=0)
    alm_ax.set_yscale("log")
    alm_ax.set_ylabel("ALMs (log)")
    ymin, ymax = alm_ax.get_ylim()
    alm_ax.set_ylim(ymin, 1.5*ymax)
    bram_ax.tick_params(axis="x", which="both", length=0)
    # bram_ax.set_yscale("symlog")
    bram_ax.set_ylabel("BRAMs")
    bram_ax.yaxis.set_major_locator(tick.MaxNLocator(integer=True))
    bram_ax.yaxis.set_major_formatter(tick.ScalarFormatter())
    dsp_ax.tick_params(axis="x", which="both", length=0)
    # dsp_ax.set_yscale("symlog")
    dsp_ax.set_ylabel("DSPs")
    dsp_ax.yaxis.set_major_locator(tick.MaxNLocator(integer=True))
    dsp_ax.yaxis.set_major_formatter(tick.ScalarFormatter())
    fig.align_ylabels()

    # "Lower is better" message
    fig.text(0.0375, -0.03, "Lower is better")
    down_arrow = Polygon(
        [(0.01, 0.005), (0.026, 0.005), (0.018, -0.025)],
        fill=True, color='black', zorder=1000,
        transform=fig.transFigure, figure=fig
    )
    fig.patches.extend([down_arrow])

    # Legend
    fig.legend(
        handles=[
            Rectangle(
                (0, 0), 1, 1,
                label=c.SHIR_LABEL,
                facecolor=c.SHIR_COLOR,
                edgecolor="black",
                hatch=SHIR_HATCH * 3,
            ),
            Rectangle(
                (0, 0), 1, 1,
                label=c.OUR_LABEL,
                facecolor=c.OUR_COLOR,
                edgecolor="black",
                hatch=OUR_HATCH * 3,
            ),
        ],
        loc="upper right",
        bbox_to_anchor=(1, 0),
        ncols=2,
    )

    fig.savefig(c.SHIR_RESOURCE_USAGE_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    area_results = crud.read_valid_resource_usage_results(c.SHIR_RESOURCE_USAGE_CSV)
    fmax_results = crud.read_valid_fmax_estimates(c.SHIR_FMAX_CSV)
    plot_resource_usages(area_results, fmax_results)


if __name__ == "__main__":
    main()
