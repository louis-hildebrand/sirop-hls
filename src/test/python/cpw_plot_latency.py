#!/usr/bin/env python3

"""
This script plots the latencies for the SHIR benchmarks.
"""

from fractions import Fraction

import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle

import lib.benchmark as lb
import lib.constants as c
import lib.plt_utils as pu
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl, benchmark_order
from lib.latency import LatencyResult

BAR_SPACE = 0.3
BAR_WIDTH = (1 - BAR_SPACE) / 4
BAR_PADDING = 0.04
IHC_HATCH = "xxx"
SHIR_HATCH = "///"
AETHERLING_HATCH = "\\\\\\"
OUR_HATCH = ""
# pylint: disable-next=line-too-long
SYNTH_FAIL = r"\textbf{{\large $\triangle$}\hspace{-0.685em}\raisebox{0.125em}{\scriptsize!}}\hspace{0.5em}"
SIM_FAIL = r"\textbf{\Large $\times$}"


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
        figsize=(8, 0.65),
        layout="compressed",
        sharex="col",
        sharey="row",
    )

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
    aetherling_latency = [
        (
            None
            if BenchmarkImpl(Benchmark(p, Fraction(-1)), "aetherling") not in results
            else results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "aetherling")].latency
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
        x=[x + 2*BAR_WIDTH for i, x in enumerate(xs) if aetherling_latency[i] is not None],
        height=[x for x in aetherling_latency if x is not None],
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.AETHERLING_COLOR,
        edgecolor="black",
        linestyle="-",
        hatch=AETHERLING_HATCH,
        zorder=10,
    )
    ax.bar(
        bottom=0,
        x=[x + 3*BAR_WIDTH for x in xs],
        height=sirop_latency,
        width=BAR_WIDTH - BAR_PADDING,
        facecolor=c.OUR_COLOR_PALE,
        edgecolor="black",
        linestyle="-",
        hatch=OUR_HATCH,
        zorder=10,
    )

    # Fmax warning labels
    ihc_fmax = [
        (
            None
            if BenchmarkImpl(Benchmark(p, Fraction(-1)), "ihc") not in fmax_results
            else fmax_results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "ihc")]
        )
        for p in program_names
    ]
    shir_fmax = [
        (
            None
            if BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir") not in fmax_results
            else fmax_results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")]
        )
        for p in program_names
    ]
    aetherling_fmax = [
        (
            None
            if BenchmarkImpl(Benchmark(p, Fraction(-1)), "aetherling") not in fmax_results
            else fmax_results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "aetherling")]
        )
        for p in program_names
    ]
    sirop_fmax = [
        fmax_results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")]
        for p in program_names
    ]
    for x, fmax, lat in zip(xs, ihc_fmax, ihc_latency):
        if fmax is not None and fmax >= c.TARGET_FREQ:
            continue
        ax.annotate(
            SYNTH_FAIL,
            (x, 2 * (lat or 1)),
            ha="center",
            color="red",
            zorder=999,
        )
    for x, fmax, lat in zip(xs, shir_fmax, shir_latency):
        if fmax is not None and fmax >= c.TARGET_FREQ:
            continue
        ax.annotate(
            SYNTH_FAIL,
            (x + BAR_WIDTH, 2 * (lat or 1)),
            ha="center",
            color="red",
            zorder=999,
        )
    for x, fmax, lat in zip(xs, aetherling_fmax, aetherling_latency):
        if fmax is not None and fmax >= c.TARGET_FREQ:
            continue
        ax.annotate(
            SYNTH_FAIL,
            (x + 2*BAR_WIDTH, 2 * (lat or 1)),
            ha="center",
            color="red",
            zorder=999,
        )
    for x, fmax, lat in zip(xs, sirop_fmax, sirop_latency):
        if fmax is not None and fmax >= c.TARGET_FREQ:
            continue
        ax.annotate(
            SYNTH_FAIL,
            (x + 3*BAR_WIDTH, 2 * (lat or 1)),
            ha="center",
            color="red",
            zorder=999,
        )

    # Simulation fail warning labels
    for i, (prog, lat) in enumerate(zip(program_names, aetherling_latency)):
        bi = BenchmarkImpl(Benchmark(prog, Fraction(-1)), "aetherling")
        if results[bi].sim_success:
            continue
        ax.annotate(
            SIM_FAIL,
            (xs[i] + 2*BAR_WIDTH, 2 * (lat or 1)),
            ha="center",
            color="red",
            zorder=999,
        )

    # Display settings
    xlim = (
        -0.5*BAR_WIDTH - 0.2*BAR_SPACE,
        len(program_names) - 0.5*BAR_WIDTH - 0.5*BAR_SPACE
    )
    ax.set_xlim(xlim)
    ax.set_xticks(
        [x + 1.5*BAR_WIDTH for x in xs],
        [lb.benchmark_title(p) or "NONE" for p in program_names]
    )
    ax.tick_params(axis="x", which="both", length=0)
    ax.set_ylabel("Latency\n(log)")
    ax.set_yscale("log")
    ax.set_yticks([10**0, 10**2, 10**4, 10**6])
    ax.set_ylim(1, 4*10**8)
    ax.grid(
        visible=True,
        which="major",
        axis="y",
        linewidth=0.2,
        color=(0.8, 0.8, 0.8)
    )

    pu.draw_lower_is_better_message(fig, 0.024, -0.24)

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
                label="Aetherling",
                facecolor=c.AETHERLING_COLOR,
                edgecolor="black",
                hatch=AETHERLING_HATCH,
            ),
            Rectangle(
                (0, 0), 1, 1,
                label=c.OUR_LABEL,
                facecolor=c.OUR_COLOR_PALE,
                edgecolor="black",
                hatch=OUR_HATCH,
            ),
            Rectangle(
                (0, 0), 0, 0,
                label="timing requirements not met",
                visible=False,
            ),
            Rectangle(
                (0, 0), 0, 0,
                label="simulation fail",
                visible=False,
            )
        ],
        loc="upper right",
        bbox_to_anchor=(1, 0),
        ncols=6,
    )
    fig.text(0.611, -0.320, SYNTH_FAIL, color="red", zorder=1000)
    fig.text(0.863, -0.310, SIM_FAIL, color="red", zorder=1000)

    fig.savefig(c.CPW_LATENCY_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    (_, latency_results, fmax_results) = crud.read_combined_results(
        c.RESOURCE_USAGE_CSV,
        c.LATENCY_CSV,
        c.FMAX_ESTIMATE_CSV,
        c.SHIR_RESOURCE_USAGE_CSV,
        c.SHIR_LATENCY_CSV,
        c.SHIR_FMAX_CSV,
        c.IHC_RESOURCE_USAGE_CSV,
        c.IHC_LATENCY_CSV,
        c.IHC_FMAX_CSV,
    )
    plot_latencies(latency_results, fmax_results)


if __name__ == "__main__":
    main()
