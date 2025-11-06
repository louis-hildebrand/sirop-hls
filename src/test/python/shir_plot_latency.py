#!/bin/python3

"""
This script plots the latencies for the SHIR benchmarks.
"""

from fractions import Fraction

import matplotlib.pyplot as plt
from matplotlib.patches import Polygon, Rectangle

import lib.constants as c
import lib.plt_utils as pu
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl, benchmark_order
from lib.latency import LatencyResult

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
        "font.size": 7,
    })
    fig, ax = plt.subplots(
        nrows=1, ncols=1,
        figsize=(4, 0.8),
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

    shir_sim_ok = [
        results[BenchmarkImpl(Benchmark(prog, Fraction(-1)), "shir")].sim_success
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
    shir_latency = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].latency or 0
        for p in program_names
    ]
    ax.bar(
        bottom=0,
        x=xs,
        height=shir_latency,
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.SHIR_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=SHIR_HATCH,
    )
    sirop_latency = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")].latency or 0
        for p in program_names
    ]
    ax.bar(
        bottom=0,
        x=[x + BAR_WIDTH for x in xs],
        height=sirop_latency,
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
    ax.set_xlim(xlim)
    ax.set_xticks(
        [x + BAR_WIDTH/2 for x in xs],
        [benchmark_title(p) or "NONE" for p in program_names]
    )
    ax.tick_params(axis="x", which="both", length=0)
    ax.set_yscale("log")
    ax.set_ylabel("Latency (log)")
    ymin, ymax = ax.get_ylim()
    ax.set_ylim(ymin, 1.5*ymax)

    # "Lower is better" message
    fig.text(0.05, -0.15, "Lower is better")
    down_arrow = Polygon(
        [(0.014, -0.075), (0.030, -0.075), (0.022, -0.14)],
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

    fig.savefig(c.SHIR_LATENCY_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    latency_results = crud.read_valid_latency_results(c.SHIR_LATENCY_CSV)
    fmax_results = crud.read_valid_fmax_estimates(c.SHIR_FMAX_CSV)
    plot_latencies(latency_results, fmax_results)


if __name__ == "__main__":
    main()
