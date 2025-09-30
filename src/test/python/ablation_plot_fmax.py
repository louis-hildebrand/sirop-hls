#!/bin/python3

"""
This script plots the max clock frequencies for the ablation study.
"""

import sys

import matplotlib.pyplot as plt

import lib.ablation_results_crud as crud
import lib.benchmark as lb
import lib.constants as c
import lib.plt_utils as pu
from lib.optimization_level import OptimizationLevel
from lib.program_variant import ProgramVariant

TARGET_FREQ = 175

BASELINE_LVL = OptimizationLevel.ALL
LEVELS_TO_PLOT = list(OptimizationLevel)
BAR_SPACE = 0.2
BAR_WIDTH = (1 - BAR_SPACE) / len(LEVELS_TO_PLOT)
BAR_PADDING = 0.02
BAR_HATCH = ["//", "\\\\", "||", "++", "--", "xx", "/", "\\", "|", "+", "-", "x"]
# pylint: disable-next=line-too-long
FACE_COLORS = ["#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6", "#6a3d9a"]
EDGE_COLORS = ["black" for _ in FACE_COLORS]
LINE_STYLES = ["-" for _ in FACE_COLORS]
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
        "font.size": 8,
    })
    fig, ax = plt.subplots(
        nrows=1, ncols=1,
        figsize=(8, 2),
        layout="compressed",
    )
    artists = []
    # Benchmark results
    for i, lvl in enumerate(OptimizationLevel):
        xs = [x + i * BAR_WIDTH for x in range(len(program_names))]
        ys = [results.get(ProgramVariant(p, lvl), 0) for p in program_names]
        artist, *_ = ax.bar(
            xs, ys,
            width=BAR_WIDTH - BAR_PADDING,
            label=lvl.explanation,
            hatch=BAR_HATCH[i],
            facecolor=FACE_COLORS[i],
            edgecolor=EDGE_COLORS[i],
            linestyle=LINE_STYLES[i],
            hatch_linewidth=HATCH_WIDTH,
        )
        artists.append(artist)
    # Target frequency
    xlim = (
        -0.5 * BAR_WIDTH - 0.5*BAR_SPACE,
        len(program_names) - 0.5 * BAR_WIDTH - 0.5*BAR_SPACE,
    )
    target_artist, *_ = ax.plot(
        list(xlim),
        [TARGET_FREQ, TARGET_FREQ],
        label="target frequency",
        linestyle=":",
        color=(0.25, 0.25, 0.25),
    )
    ax.set_ylabel("fmax (MHz)")
    ax.set_xlim(xlim)
    ax.set_xticks(
        [x + (len(LEVELS_TO_PLOT) / 2 - 0.5) * BAR_WIDTH for x in range(len(program_names))],
        [f"\\texttt{{{p}}}" for p in program_names],
    )
    ax.tick_params(axis="x", which="both", length=0)
    legend_cols = (len(OptimizationLevel) + 2) // 2
    legend_labels = ["target frequency"] + [lvl.explanation for lvl in OptimizationLevel]
    legend_handles = [target_artist] + artists
    fig.legend(
        labels=pu.flip(legend_labels, legend_cols),
        handles=pu.flip(legend_handles, legend_cols),
        loc="upper center",
        bbox_to_anchor=(0.5, 0),
        ncols=legend_cols,
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
