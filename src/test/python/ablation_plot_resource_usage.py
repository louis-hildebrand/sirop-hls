#!/bin/python3

"""
This script plots the resource usages for the ablation study.
"""

import sys

import matplotlib.pyplot as plt

import lib.ablation_results_crud as crud
import lib.benchmark as lb
import lib.constants as c
import lib.plt_utils as pu
from lib.optimization_level import OptimizationLevel
from lib.program_variant import ProgramVariant
from lib.resource_usage import ResourceUsage

BAR_SPACE = 0.2
BAR_WIDTH = (1 - BAR_SPACE) / (len(OptimizationLevel) - 1)
BAR_PADDING = 0.02
BAR_HATCH = ["//", "\\\\", "", "||"]
FACE_COLORS = ["#a6cee3", "#1f78b4", "#b2df8a", "#33a02c"]
EDGE_COLORS = ["black", "black", "black", "black"]
HATCH_WIDTH = 1

def plot_resource_usages(results: dict[ProgramVariant, ResourceUsage]) -> None:
    """
    Plot resource usage for each program.
    """
    program_names = pu.dedup([p.name for p in results.keys()])
    program_names = sorted(program_names, key=lb.benchmark_order)
    if not program_names:
        raise ValueError("Nothing to plot.")
    plt.rcParams.update({
        "text.usetex": True,
        "font.family": "Times New Roman",
        "font.size": 9,
    })
    fig, alm_ax = plt.subplots(
        nrows=1, ncols=1,
        figsize=(8, 1.5),
        layout="compressed",
        sharex="col",
    )
    # Baseline
    baseline_artist, *_ = alm_ax.plot(
        [-BAR_WIDTH, len(program_names)],
        [1, 1],
        linestyle=":",
        color=(0.5, 0.5, 0.5),
    )
    # Resource usages
    artists = []
    for i, lvl in enumerate([lvl for lvl in OptimizationLevel if lvl != OptimizationLevel.NONE]):
        xs = [x + i * BAR_WIDTH for x in range(len(program_names))]
        # ALM usage
        ys = []
        for p in program_names:
            y = results[ProgramVariant(p, lvl)].alm
            baseline = results[ProgramVariant(p, OptimizationLevel.NONE)].alm
            ys.append(y / baseline)
        artist = alm_ax.bar(
            bottom=1,
            x=xs,
            height=[y - 1 for y in ys],
            width=BAR_WIDTH - BAR_PADDING,
            label=str(lvl),
            hatch=BAR_HATCH[i],
            facecolor=FACE_COLORS[i],
            edgecolor=EDGE_COLORS[i],
            hatch_linewidth=HATCH_WIDTH,
        )
        if lvl != OptimizationLevel.NONE:
            artists.append(artist)
        # labels = [results[ProgramVariant(p, lvl)].alm for p in program_names]
        # labels = [f"{lab:,}" for lab in labels]
        # alm_ax.bar_label(
        #     artist,
        #     labels=labels,
        #     padding=3,
        # )
    # Display settings
    alm_ax.set_xlim(-0.5 * BAR_WIDTH, len(program_names) - 0.5 * BAR_WIDTH)
    alm_ax.set_ylim(0, 1.1)
    alm_ax.set_ylabel("ALM usage ratio")
    alm_ax.set_xticks(
        [x + (len(program_names) / 2) * BAR_WIDTH for x in range(len(program_names))],
        program_names
    )
    alm_ax.tick_params(axis="x", which="both", length=0)
    legend_cols = (len(OptimizationLevel) + 1) // 2
    legend_labels=(
        [OptimizationLevel.NONE.explanation]
        + [lvl.explanation for lvl in OptimizationLevel if lvl != OptimizationLevel.NONE]
    )
    legend_handles=[baseline_artist] + artists
    fig.legend(
        labels=pu.flip(legend_labels, legend_cols),
        handles=pu.flip(legend_handles, legend_cols),
        loc="upper center",
        bbox_to_anchor=(0.5, 0),
        ncols=legend_cols,
    )
    fig.savefig(c.ABLATION_RESOURCE_USAGE_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    results = crud.read_valid_resource_usage_results(c.ABLATION_RESOURCE_USAGE_CSV)
    if not results:
        sys.exit("No results to plot.")
    plot_resource_usages(results)


if __name__ == "__main__":
    main()
