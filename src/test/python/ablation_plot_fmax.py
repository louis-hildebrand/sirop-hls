#!/bin/python3

"""
This script plots the max clock frequencies for the ablation study.
"""

import sys

import matplotlib.pyplot as plt

import lib.ablation_results_crud as crud
import lib.benchmark as lb
import lib.constants as c
from lib.optimization_level import OptimizationLevel
from lib.program_variant import ProgramVariant

BAR_SPACE = 0.2
BAR_WIDTH = (1 - BAR_SPACE) / len(OptimizationLevel)
BAR_HATCH = ["", "xx", "++", "", ".."]
COLORS = ["yellow", "green", "orange", "blue", "red"]
HATCH_WIDTH = 1


def dedup(xs: list[str]) -> list[str]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


def plot_fmax(results: dict[ProgramVariant, float]) -> None:
    """
    Plot fmax for each program.
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
        ys = [results.get(ProgramVariant(p, lvl), 0) for p in program_names]
        artist, *_ = ax.bar(
            xs, ys,
            width=BAR_WIDTH,
            label=str(lvl),
            hatch=BAR_HATCH[i],
            color=COLORS[i],
            hatch_linewidth=HATCH_WIDTH,
        )
        artists.append(artist)
    ax.set_ylabel("fmax (MHz)")
    ax.set_xticks([])
    fig.legend(
        labels=[lvl.explanation for lvl in OptimizationLevel],
        handles=artists,
        loc="lower center",
        bbox_to_anchor=(0.5, -0.15),
        ncols=len(OptimizationLevel),
    )
    fig.savefig(c.ABLATION_FMAX_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    results = crud.read_valid_fmax(c.ABLATION_FMAX_CSV)
    if not results:
        sys.exit("No results to plot.")
    plot_fmax(results)


if __name__ == "__main__":
    main()
