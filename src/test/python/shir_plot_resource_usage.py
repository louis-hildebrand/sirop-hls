#!/bin/python3

"""
This script plots the resource usages for the ablation study.
"""

import statistics
from fractions import Fraction

import matplotlib.pyplot as plt
from matplotlib.patches import Polygon, Rectangle

import lib.constants as c
import lib.plt_utils as pu
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl, benchmark_order
from lib.resource_usage import ResourceUsage

BAR_SPACE = 0.2
BAR_WIDTH = 1 - BAR_SPACE
BAR_PADDING = 0.02

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
        figsize=(4, 1.9),
        layout="compressed",
        sharex="col",
        sharey="row",
    )

    # Baseline
    xlim = (
        -0.5 * BAR_WIDTH - 0.5*BAR_SPACE,
        len(program_names) + 2 - 0.5 * BAR_WIDTH - 0.5*BAR_SPACE,
    )
    for ax in (alm_ax, bram_ax, dsp_ax):
        ax.plot(
            list(xlim),
            [1, 1],
            linestyle=":",
            color=(0.5, 0.5, 0.5),
        )

    # Resource usages
    xs = list(range(len(program_names))) + [len(program_names) + 1]
    shir_fmax_ok = [
        fmax_results[BenchmarkImpl(Benchmark(prog, Fraction(-1)), "shir")] >= 175
        for prog in program_names
    ]
    sirop_fmax_ok = [
        fmax_results[BenchmarkImpl(Benchmark(prog, Fraction(-1)), "sirop")] >= 175
        for prog in program_names
    ]
    hatch = [
        "" if shir_ok and sirop_ok
        else "/" if shir_ok and not sirop_ok
        else "\\" if not shir_ok and sirop_ok
        else "x"
        for (shir_ok, sirop_ok) in zip(shir_fmax_ok, sirop_fmax_ok)
    ] + [""]
    # ALM usage
    ys = []
    for p in program_names:
        y = results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")].alm
        baseline = results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].alm
        ys.append( y / baseline )
    ys.append(statistics.geometric_mean(ys))
    container = alm_ax.bar(
        bottom=1,
        x=xs,
        height=[y - 1 for y in ys],
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.OUR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=hatch,
    )
    alm_ax.bar_label(
        container,
        labels=["" for _ in ys[:-1]] + [f"{ys[-1]:.2f}"],
        label_type="center",
    )
    # BRAM usage
    ys = []
    for p in program_names:
        y = results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")].bram
        baseline = results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].bram
        if baseline == 0 and y == 0:
            ys.append(1)
        elif baseline == 0:
            print(f"WARNING: {p}: BRAM usage is zero for SHIR but {y} for Sirop")
            ys.append(2)
        else:
            ys.append( y / baseline )
    ys.append(statistics.geometric_mean(ys))
    container = bram_ax.bar(
        bottom=1,
        x=xs,
        height=[y - 1 for y in ys],
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.OUR_COLOR,
        edgecolor="black",
        linestyle="-",
    )
    bram_ax.bar_label(
        container,
        labels=["" for _ in ys[:-1]] + [f"{ys[-1]:.2f}"],
        label_type="edge",
        padding=3,
    )
    # DSP usage
    ys = []
    for p in program_names:
        y = results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")].dsp
        baseline = results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].dsp
        if baseline == 0 and y == 0:
            ys.append(1)
        elif baseline == 0:
            print(f"WARNING: {p}: DSP usage is zero for SHIR but {y} for Sirop")
            ys.append(2)
        elif y == 0:
            print(f"WARNING: {p}: DSP usage is zero for Sirop but {y} for SHIR")
            ys.append(1e-10)
        else:
            ys.append( y / baseline )
    ys.append(statistics.geometric_mean(ys))
    container = dsp_ax.bar(
        bottom=1,
        x=xs,
        height=[y - 1 for y in ys],
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.OUR_COLOR,
        edgecolor="black",
        linestyle="-",
    )
    dsp_ax.bar_label(
        container,
        labels=["" for _ in ys[:-1]] + [f"{ys[-1]:.2f}"],
        label_type="center",
    )

    # Display settings
    alm_ax.set_xlim(xlim)
    alm_ax.set_xticks(
        xs,
        [benchmark_title(p) for p in program_names] + [r"\texttt{geomean}"],
    )
    alm_ax.tick_params(axis="x", which="both", length=0)
    alm_ax.set_ylabel("ALM\nratio")
    bram_ax.tick_params(axis="x", which="both", length=0)
    bram_ax.set_ylabel("BRAM\nratio")
    dsp_ax.tick_params(axis="x", which="both", length=0)
    dsp_ax.set_ylabel("DSP\nratio")

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
                label="SHIR low fmax",
                facecolor=c.OUR_COLOR,
                edgecolor="black",
                hatch="\\\\\\",
                hatch_linewidth=1,
            ),
            Rectangle(
                (0, 0), 1, 1,
                label="both ok",
                facecolor=c.OUR_COLOR,
                edgecolor="black",
                hatch="",
                hatch_linewidth=1,
            ),
            Rectangle(
                (0, 0), 1, 1,
                label=f"{c.OUR_LABEL} low fmax",
                facecolor=c.OUR_COLOR,
                edgecolor="black",
                hatch="///",
                hatch_linewidth=1,
            ),
            Rectangle(
                (0, 0), 1, 1,
                label="both low fmax",
                facecolor=c.OUR_COLOR,
                edgecolor="black",
                hatch="xxx",
                hatch_linewidth=1,
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
