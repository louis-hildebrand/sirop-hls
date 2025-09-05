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
BAR_HATCH = ["", "xx", "++", ".."]
HATCH_WIDTH = 1


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
        "font.size": 10,
    })
    fig, ax = plt.subplots(
        nrows=1, ncols=1,
        figsize=(8, 2),
        layout="compressed",
    )
    artists = []
    for i, lvl in enumerate(OptimizationLevel):
        xs = [x + i * BAR_WIDTH for x in range(len(program_names))]
        ys = []
        for p in program_names:
            res = results.get(ProgramVariant(p, lvl))
            y = res.latency if res else 0
            ys.append(y)
        artist, *_ = ax.bar(
            xs, ys,
            width=BAR_WIDTH,
            label=str(lvl),
            hatch=BAR_HATCH[i],
            hatch_linewidth=HATCH_WIDTH,
        )
        artists.append(artist)
    ax.set_ylabel("Latency (cycles)")
    ax.set_xticks([])
    fig.legend(
        labels=[str(lvl) for lvl in OptimizationLevel],
        handles=artists,
        loc="lower center",
        bbox_to_anchor=(0.5, -0.15),
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
