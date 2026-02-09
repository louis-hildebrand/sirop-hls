#!/usr/bin/env python3

"""
This script plots the latencies for the SHIR benchmarks.
"""

from fractions import Fraction

import matplotlib.pyplot as plt
from matplotlib.patches import Polygon, Rectangle

import lib.benchmark as lb
import lib.constants as c
import lib.plt_utils as pu
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl, benchmark_order
from lib.latency import LatencyResult

BAR_SPACE = 0.4
BAR_WIDTH = (1 - BAR_SPACE) / 3
BAR_PADDING = 0.04
IHC_HATCH = "///"
SHIR_HATCH = "\\\\\\"
OUR_HATCH = ""
# pylint: disable-next=line-too-long
WARNING = r"\textbf{{\large $\triangle$}\hspace{-0.685em}\raisebox{0.125em}{\scriptsize!}}\hspace{0.5em}"
SYNTH_FAIL = r"\textbf{\Large $\times$}"


def benchmark_title(bench_name: str) -> str | None:
    """
    Return the title to put at the top of the column for the given benchmark, or `None` if the
    results for this benchmark should be omitted from the plots.
    """
    if bench_name.endswith("sharpen"):
        bench_name = bench_name[:-2]
    return lb.benchmark_title(bench_name)


def plot_latencies(
    results: dict[BenchmarkImpl, LatencyResult],
    fmax_results: dict[BenchmarkImpl, float],
) -> None:
    """
    Plot latency for each program.
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
    fig, ax = plt.subplots(
        nrows=1, ncols=1,
        figsize=(8, 0.8),
        layout="compressed",
        sharex="col",
        sharey="row",
    )

    ihc_fmax_ok = [
        (
            BenchmarkImpl(Benchmark(prog, Fraction(-1)), "ihc") in fmax_results
            and fmax_results[BenchmarkImpl(Benchmark(prog, Fraction(-1)), "ihc")] >= c.TARGET_FREQ
        )
        for prog in program_names
    ]
    shir_fmax_ok = [
        (
            BenchmarkImpl(Benchmark(prog, Fraction(-1)), "shir") in fmax_results
            and fmax_results[BenchmarkImpl(Benchmark(prog, Fraction(-1)), "shir")] >= c.TARGET_FREQ
        )
        for prog in program_names
    ]
    sirop_fmax_ok = [
        (
            BenchmarkImpl(Benchmark(prog, Fraction(-1)), "sirop") in fmax_results
            and fmax_results[BenchmarkImpl(Benchmark(prog, Fraction(-1)), "sirop")] >= c.TARGET_FREQ
        )
        for prog in program_names
    ]

    ihc_sim_ok = [
        (
            BenchmarkImpl(Benchmark(prog, Fraction(-1)), "ihc") not in results
            or results[BenchmarkImpl(Benchmark(prog, Fraction(-1)), "ihc")].sim_success
        )
        for prog in program_names
    ]
    assert all(ihc_sim_ok), "need to show Intel HLS sim failure somehow"
    shir_sim_ok = [
        (
            BenchmarkImpl(Benchmark(prog, Fraction(-1)), "shir") not in results
            or results[BenchmarkImpl(Benchmark(prog, Fraction(-1)), "shir")].sim_success
        )
        for prog in program_names
    ]
    assert all(shir_sim_ok), "need to show SHIR sim failure somehow"
    sirop_sim_ok = [
        results[BenchmarkImpl(Benchmark(prog, Fraction(-1)), "sirop")].sim_success
        for prog in program_names
    ]
    assert all(sirop_sim_ok), "need to show Sirop sim failure somehow"

    # Latency values
    xs = list(range(len(program_names)))
    ihc_latency = [
        (
            None
            if BenchmarkImpl(Benchmark(p, Fraction(-1)), "ihc") not in results
            else results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "ihc")].latency
        )
        for p in program_names
    ]
    shir_latency = [
        (
            None
            if BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir") not in results
            else results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].latency
        )
        for p in program_names
    ]
    sirop_latency = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")].latency or 0
        for p in program_names
    ]
    ax.bar(
        bottom=0,
        x=[x for i, x in enumerate(xs) if ihc_latency[i] is not None],
        height=[x for x in ihc_latency if x is not None],
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.IHC_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=IHC_HATCH,
        zorder=10,
    )
    ax.bar(
        bottom=0,
        x=[x + BAR_WIDTH for i, x in enumerate(xs) if shir_latency[i] is not None],
        height=[x for x in shir_latency if x is not None],
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.SHIR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=SHIR_HATCH,
        zorder=10,
    )
    ax.bar(
        bottom=0,
        x=[x + 2*BAR_WIDTH for x in xs],
        height=sirop_latency,
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.OUR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=OUR_HATCH,
        zorder=10,
    )

    # Synthesis fail warning labels
    for i, shir_lat in enumerate(shir_latency):
        if shir_lat is not None:
            continue
        ax.annotate(
            SYNTH_FAIL,
            (xs[i] + BAR_WIDTH, 15),
            ha="center",
            color="red",
            zorder=999,
        )

    # Fmax warning labels
    for x, ihc_ok, ihc_lat in zip(xs, ihc_fmax_ok, ihc_latency):
        if ihc_lat is None:
            continue
        if not ihc_ok:
            ax.annotate(
                WARNING,
                (x + BAR_WIDTH, 2500 + ihc_lat),
                ha="center",
                color="red",
                zorder=999,
            )
    for x, shir_ok, shir_lat in zip(xs, shir_fmax_ok, shir_latency):
        if shir_lat is None:
            continue
        if not shir_ok:
            ax.annotate(
                WARNING,
                (x + BAR_WIDTH, 2500 + shir_lat),
                ha="center",
                color="red",
                zorder=999,
            )
    for i, sirop_ok in enumerate(sirop_fmax_ok):
        if not sirop_ok:
            ax.annotate(
                WARNING,
                (xs[i] + 2*BAR_WIDTH, 2500 + sirop_latency[i]),
                ha="center",
                color="red",
                zorder=999
            )

    # Display settings
    xlim = (
        -0.5*BAR_WIDTH - 0.2*BAR_SPACE,
        len(program_names) - 0.5*BAR_WIDTH - 0.5*BAR_SPACE
    )
    ax.set_xlim(xlim)
    ax.set_xticks(
        [x + BAR_WIDTH/2 for x in xs],
        [benchmark_title(p) or "NONE" for p in program_names]
    )
    ax.tick_params(axis="x", which="both", length=0)
    ax.set_ylabel("Latency\n(log)")
    ax.set_yscale("log")
    ax.set_yticks([10**0, 10**2, 10**4, 10**6])
    ax.grid(
        visible=True,
        which="major",
        axis="y",
        linewidth=0.2,
        color=(0.8, 0.8, 0.8)
    )

    # "Lower is better" message
    fig.text(0.02, -0.24, "Lower is better")
    down_arrow = Polygon(
        [(0.004, -0.155), (0.014, -0.155), (0.009, -0.23)],
        fill=True, color='black', zorder=1000,
        transform=fig.transFigure, figure=fig
    )
    fig.patches.extend([down_arrow])

    # Legend
    fig.legend(
        handles=[
            Rectangle(
                (0, 0), 1, 1,
                label=c.IHC_LABEL,
                facecolor=c.IHC_COLOR,
                edgecolor="black",
                hatch=IHC_HATCH,
            ),
            Rectangle(
                (0, 0), 1, 1,
                label=c.SHIR_LABEL,
                facecolor=c.SHIR_COLOR,
                edgecolor="black",
                hatch=SHIR_HATCH,
            ),
            Rectangle(
                (0, 0), 1, 1,
                label=c.OUR_LABEL,
                facecolor=c.OUR_COLOR,
                edgecolor="black",
                hatch=OUR_HATCH,
            ),
            Rectangle(
                (0, 0), 0, 0,
                label="synthesis fail",
                visible=False,
            ),
        ],
        loc="upper right",
        bbox_to_anchor=(1, 0),
        ncols=4,
    )
    fig.text(0.871, -0.238, SYNTH_FAIL, color="red", zorder=1000)

    fig.savefig(c.IHC_LATENCY_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    latency_results = (
        crud.read_valid_latency_results(c.IHC_LATENCY_CSV)
        | crud.read_valid_latency_results(c.SHIR_LATENCY_CSV)
    )
    fmax_results = (
        crud.read_valid_fmax_estimates(c.IHC_FMAX_CSV)
        | crud.read_valid_fmax_estimates(c.SHIR_FMAX_CSV)
    )
    plot_latencies(latency_results, fmax_results)


if __name__ == "__main__":
    main()
