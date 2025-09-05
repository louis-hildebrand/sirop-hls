#!/bin/python3

"""
This script plots the latencies for the ablation study.
"""

import sys

import matplotlib.pyplot as plt

import lib.ablation_results_crud as crud
import lib.benchmark as lb
import lib.constants as c
from lib.latency import LatencyResult
from lib.optimization_level import OptimizationLevel
from lib.program_variant import ProgramVariant

BAR_SPACE = 0.2
BAR_WIDTH = (1 - BAR_SPACE) / len(OptimizationLevel)
BAR_HATCH = ["..", "xx", "++", ""]
COLORS = ["red", "green", "orange", "blue"]
HATCH_WIDTH = 1
BOTTOM = 0


def dedup(xs: list[str]) -> list[str]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


def plot_latency(results: dict[ProgramVariant, LatencyResult]) -> None:
    """
    Plot latency for each program.
    """
    program_names = dedup([p.name for p in results.keys()])
    program_names = sorted(program_names, key=lb.benchmark_order)
    if not program_names:
        raise ValueError("Nothing to plot.")
    plt.rcParams.update({
        "text.usetex": True,
        "font.family": "Times New Roman",
        "font.size": 9,
    })
    fig, ax = plt.subplots(
        nrows=1, ncols=1,
        figsize=(8, 1.5),
        layout="compressed",
    )
    # Latency results
    artists = []
    for i, lvl in enumerate(OptimizationLevel):
        xs = [x + i * BAR_WIDTH for x in range(len(program_names))]
        ys = []
        for p in program_names:
            y = results[ProgramVariant(p, lvl)].latency
            baseline = results[ProgramVariant(p, OptimizationLevel.NONE)].latency
            if baseline is None:
                raise ValueError(f"Missing baseline latency for {p}")
            if y is None:
                ys.append(baseline)
            else:
                ys.append(y - baseline)
        artist = ax.bar(
            bottom=BOTTOM,
            x=xs,
            height=[y - BOTTOM for y in ys],
            width=BAR_WIDTH,
            label=str(lvl),
            hatch=BAR_HATCH[i],
            color=COLORS[i],
            hatch_linewidth=HATCH_WIDTH,
        )
        if lvl != OptimizationLevel.NONE:
            artists.append(artist)
        labels = []
        for p in program_names:
            y = results[ProgramVariant(p, lvl)].latency
            if y is None:
                labels.append("-")
            else:
                labels.append(f"{y:,}")
        ax.bar_label(
            artist,
            labels=labels,
            padding=3,
        )
    # Baseline
    baseline_artist, *_ = ax.plot(
        [-0.5*BAR_WIDTH, len(program_names) - 0.5*BAR_WIDTH],
        [BOTTOM, BOTTOM],
        linestyle=":",
        color=(0.5, 0.5, 0.5),
    )
    # Display settings
    ax.set_xlim(-0.5*BAR_WIDTH, len(program_names) - 0.5*BAR_WIDTH)
    ax.set_yscale("symlog")
    ax.set_ylim(-2 * 10**5, 2)
    ax.set_ylabel("Latency difference\n(cycles)")
    ax.set_xticks(
        [x + (len(program_names) / 2) * BAR_WIDTH for x in range(len(program_names))],
        program_names
    )
    ax.tick_params(axis="x", which="both", length=0)
    fig.legend(
        labels=(
            [OptimizationLevel.NONE.explanation]
                + [lvl.explanation for lvl in OptimizationLevel if lvl != OptimizationLevel.NONE]
        ),
        handles=[baseline_artist] + artists,
        loc="lower center",
        bbox_to_anchor=(0.5, -0.2),
        ncols=len(OptimizationLevel),
    )
    fig.savefig(c.ABLATION_LATENCY_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    results = crud.read_valid_latency_results(c.ABLATION_LATENCY_CSV)
    if not results:
        sys.exit("No results to plot.")
    plot_latency(results)


if __name__ == "__main__":
    main()
