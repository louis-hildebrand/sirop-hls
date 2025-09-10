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

LEVELS_TO_PLOT = [
    lvl
    for lvl in OptimizationLevel
    if lvl != OptimizationLevel.ALL_EXCEPT_SIMPL
]
BAR_SPACE = 0.2
BAR_WIDTH = (1 - BAR_SPACE) / len(LEVELS_TO_PLOT)
BAR_PADDING = 0.02
BAR_HATCH = ["", "//", r"\\", "||"]
FACE_COLORS = ["white", "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c"]
EDGE_COLORS = ["black", "black", "black", "black"]
LINE_STYLES = [":", "-", "-", "-"]
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
    # Resource usages
    artists = []
    for i, lvl in enumerate(LEVELS_TO_PLOT):
        xs = [x + i * BAR_WIDTH for x in range(len(program_names))]
        # ALM usage
        ys = [results[ProgramVariant(p, lvl)].alm for p in program_names]
        artist = alm_ax.bar(
            bottom=0,
            x=xs,
            height=ys,
            width=BAR_WIDTH - BAR_PADDING,
            label=str(lvl),
            hatch=BAR_HATCH[i],
            facecolor=FACE_COLORS[i],
            edgecolor=EDGE_COLORS[i],
            linestyle=LINE_STYLES[i],
            hatch_linewidth=HATCH_WIDTH,
        )
        artists.append(artist)
        labels = []
        for p in program_names:
            alm = results[ProgramVariant(p, lvl)].alm
            baseline = results[ProgramVariant(p, OptimizationLevel.NONE)].alm
            percent_change = (alm - baseline) / baseline
            if percent_change >= 0:
                label = f"+{percent_change:.0%}"
            else:
                label = f"{percent_change:.0%}"
            label = label.replace("%", r"\%")
            if label == r"+0\%":
                label = ""
            labels.append(label)
        alm_ax.bar_label(
            artist,
            labels=labels,
            padding=3,
        )
    # Display settings
    alm_ax.set_xlim(
        -0.5 * BAR_WIDTH - 0.5*BAR_SPACE,
        len(program_names) - 0.5 * BAR_WIDTH - 0.5*BAR_SPACE,
    )
    alm_ax.set_xticks(
        [x + ((len(program_names) - 1) / 2) * BAR_WIDTH for x in range(len(program_names))],
        program_names
    )
    alm_ax.tick_params(axis="x", which="both", length=0)
    alm_ax.set_yscale("log")
    alm_ax.set_ylabel("ALM usage (log)")
    (y_lo, y_hi) = alm_ax.get_ylim()
    alm_ax.set_ylim(y_lo, y_hi * 2)
    legend_cols = (len(LEVELS_TO_PLOT) + 1) // 2
    legend_labels=[lvl.explanation for lvl in LEVELS_TO_PLOT]
    legend_handles=artists
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
