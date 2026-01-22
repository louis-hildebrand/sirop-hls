#!/usr/bin/env python3

"""
This script plots the resource usages for the SHIR benchmarks.
"""

import statistics
from fractions import Fraction

import matplotlib.path as mpath
import matplotlib.pyplot as plt
from matplotlib.patches import ArrowStyle, FancyArrowPatch, Polygon, Rectangle

import lib.benchmark as lb
import lib.constants as c
import lib.plt_utils as pu
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl, benchmark_order
from lib.resource_usage import ResourceUsage

BAR_SPACE = 0.3
BAR_WIDTH = (1 - BAR_SPACE) / 2
BAR_PADDING = 0.07
SHIR_HATCH = "//"
OUR_HATCH = "\\\\"
SYNTH_FAIL = r"\textbf{\Large $\times$}"


def benchmark_title(bench_name: str) -> str | None:
    """
    Return the title to put at the top of the column for the given benchmark, or `None` if the
    results for this benchmark should be omitted from the plots.
    """
    if bench_name.endswith("sharpen"):
        bench_name = bench_name[:-2]
    return lb.benchmark_title(bench_name)


def plot_resource_usages(
    results: dict[BenchmarkImpl, ResourceUsage],
) -> None:
    """
    Plot resource usage for each program.
    """
    program_names = pu.dedup([
        b.bench.name
        for b in results.keys()
        if benchmark_title(b.bench.name) is not None
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
        figsize=(4, 1.8),
        height_ratios=[1.5, 1, 1],
        layout="compressed",
        sharex="col",
        sharey="row",
    )

    shir_alms = [
        (
            results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].alm
            if BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir") in results
            else None
        )
        for p in program_names
    ]
    sirop_alms = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")].alm
        for p in program_names
    ]
    alm_ratio_geomean = statistics.geometric_mean(
        [
            sirop / shir
            for (sirop, shir) in zip(sirop_alms, shir_alms)
            if shir is not None
        ]
    )
    print(f"ALM ratio geomean: {alm_ratio_geomean:.2f}")

    # Resource usages
    xs = list(range(len(program_names)))
    # ALM usage
    alm_ax.bar(
        bottom=0,
        x=[x for i, x in enumerate(xs) if shir_alms[i] is not None],
        height=[x for x in shir_alms if x is not None],
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.SHIR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=SHIR_HATCH,
        zorder=10,
    )
    alm_ax.bar(
        bottom=0,
        x=[x + BAR_WIDTH for x in xs],
        height=sirop_alms,
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.OUR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=OUR_HATCH,
        zorder=10,
    )
    # ALM usage ratios
    endpoint_lift = 1.2
    peak_lift = 5
    arrow_style = ArrowStyle("-|>", head_length=3, head_width=2)
    for x, (shir_alm, sirop_alm) in enumerate(zip(shir_alms, sirop_alms)):
        if shir_alm is None:
            continue
        tail = (x + BAR_WIDTH*0.16, endpoint_lift * shir_alm)
        peak = (x + BAR_WIDTH*0.55, peak_lift * max(shir_alm, sirop_alm))
        head = (x + BAR_WIDTH*0.84, endpoint_lift * sirop_alm)
        arrow_path = mpath.Path(
            [tail, peak, head],
            [mpath.Path.MOVETO, mpath.Path.CURVE3, mpath.Path.CURVE3]
        )
        arrow = FancyArrowPatch(path=arrow_path, arrowstyle=arrow_style, color="black", zorder=10)
        alm_ax.add_patch(arrow)
        label = f"{sirop_alm/shir_alm:.2f}"
        label = f"${label}\\times$"
        alm_ax.annotate(
            label, (x + 0.5*BAR_WIDTH, peak[1]),
            horizontalalignment="center",
            verticalalignment="center",
        )
    # BRAM usage
    shir_brams = [
        (
            BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir") in results
            and results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].bram
        )
        for p in program_names
    ]
    bram_ax.bar(
        bottom=0,
        x=[x for i, x in enumerate(xs) if shir_brams[i] is not None],
        height=[x for x in shir_brams if x is not None],
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.SHIR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=SHIR_HATCH,
        zorder=10,
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
        zorder=10,
    )
    # DSP usage
    shir_dsps = [
        (
            BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir") in results
            and results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].dsp
        )
        for p in program_names
    ]
    dsp_ax.bar(
        bottom=0,
        x=[x for i, x in enumerate(xs) if shir_dsps[i] is not None],
        height=[x for x in shir_dsps if x is not None],
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.SHIR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=SHIR_HATCH,
        zorder=10,
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
        zorder=10,
    )

    # Synthesis fail labels
    for i, shir_alm in enumerate(shir_alms):
        if shir_alm is not None:
            continue
        alm_ax.annotate(
            SYNTH_FAIL,
            (xs[i], 3),
            ha="center",
            color="red",
            zorder=999,
        )
        bram_ax.annotate(
            SYNTH_FAIL,
            (xs[i], 1),
            ha="center",
            color="red",
            zorder=999,
        )
        dsp_ax.annotate(
            SYNTH_FAIL,
            (xs[i], 1),
            ha="center",
            color="red",
            zorder=999,
        )

    # Display settings
    xlim = (
        -0.5*BAR_WIDTH - 0.2*BAR_SPACE,
        len(program_names) - 0.5*BAR_WIDTH - 0.5*BAR_SPACE
    )
    alm_ax.set_xlim(xlim)
    alm_ax.tick_params(axis="x", which="both", length=0)
    alm_ax.set_xticks(
        [x + BAR_WIDTH/2 for x in xs],
        [benchmark_title(p) or "NONE" for p in program_names]
    )
    alm_ax.grid(
        visible=True,
        which="major",
        axis="y",
        linewidth=0.2,
        color=(0.8, 0.8, 0.8)
    )
    alm_ax.set_yscale("log")
    alm_ax.set_ylabel("ALMs\n(log)")
    ymin, ymax = alm_ax.get_ylim()
    alm_ax.set_ylim(ymin, 7*ymax)
    alm_ax.set_yticks(
        [1, 10, 10**2, 10**3, 10**4],
    )
    alm_ax.set_zorder(10**6)

    bram_ax.tick_params(axis="x", which="both", length=0)
    bram_ax.grid(
        visible=True,
        which="major",
        axis="y",
        linewidth=0.2,
        color=(0.8, 0.8, 0.8)
    )
    bram_ax.set_yscale("symlog")
    bram_ax.set_ylabel("BRAMs\n(log)")
    bram_ax.set_yticks([1, 10, 100])
    bram_ax.set_ylim(0.5, 100)

    dsp_ax.tick_params(axis="x", which="both", length=0)
    dsp_ax.grid(
        visible=True,
        which="major",
        axis="y",
        linewidth=0.2,
        color=(0.8, 0.8, 0.8)
    )
    dsp_ax.set_yscale("symlog")
    dsp_ax.set_ylabel("DSPs\n(log)")
    dsp_ax.set_ylim(0.5, 100)

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
                hatch=SHIR_HATCH * 2,
            ),
            Rectangle(
                (0, 0), 1, 1,
                label=c.OUR_LABEL,
                facecolor=c.OUR_COLOR,
                edgecolor="black",
                hatch=OUR_HATCH * 2,
            ),
            Rectangle(
                (0, 0), 0, 0,
                label="synthesis fail",
                visible=False,
            ),
        ],
        loc="upper right",
        bbox_to_anchor=(1, 0),
        ncols=3,
    )
    fig.text(0.74, -0.105, SYNTH_FAIL, color="red", zorder=1000)

    fig.savefig(c.SHIR_RESOURCE_USAGE_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    area_results = crud.read_valid_resource_usage_results(c.SHIR_RESOURCE_USAGE_CSV)
    plot_resource_usages(area_results)


if __name__ == "__main__":
    main()
