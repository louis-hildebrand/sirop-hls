#!/bin/python3

"""
This script plots the resource usages for the ablation study.
"""

import sys

import matplotlib.pyplot as plt

import lib.ablation_results_crud as crud
import lib.constants as c
import lib.plt_utils as pu
from lib.optimization_level import OptimizationLevel
from lib.program_variant import ProgramVariant
from lib.resource_usage import ResourceUsage

LEVELS_TO_PLOT = [
    lvl
    for lvl in OptimizationLevel
    if lvl not in [OptimizationLevel.NONE, OptimizationLevel.ALL_EXCEPT_SIMPL]
]
BAR_SPACE = 0.2
BAR_WIDTH = (1 - BAR_SPACE) / len(LEVELS_TO_PLOT)
BAR_PADDING = 0.02
BAR_HATCH = ["//", r"\\", ""]
FACE_COLORS = ["#a6cee3", "#1f78b4", "#b2df8a", "#33a02c"]
EDGE_COLORS = ["black", "black", "black", "black"]
LINE_STYLES = ["-", "-", "-", "-"]
HATCH_WIDTH = 1


def program_order(program_name: str) -> int:
    """
    Choose the order of the programs in the plot.
    """
    return {
        "map": 0,
        "dot": 1,
        "conv1d": 2,
        "conv2d": 3,
        "convb2b": 4,
        "sharpen": 5,
        "camera": 6,
    }.get(program_name, 7)


def plot_resource_usages(results: dict[ProgramVariant, ResourceUsage]) -> None:
    """
    Plot resource usage for each program.
    """
    program_names = pu.dedup([p.name for p in results.keys()])
    program_names = sorted(program_names, key=program_order)
    if not program_names:
        raise ValueError("Nothing to plot.")
    plt.rcParams.update({
        "text.usetex": True,
        "font.family": "Times New Roman",
        "font.size": 8,
    })
    fig, (alm_ax, bram_ax) = plt.subplots(
        nrows=2, ncols=1,
        figsize=(8, 3),
        layout="compressed",
        sharex="col",
    )
    # Baseline
    xlim = (
        -0.5 * BAR_WIDTH - 0.5*BAR_SPACE,
        len(program_names) - 0.5 * BAR_WIDTH - 0.5*BAR_SPACE,
    )
    baseline_artist, *_ = alm_ax.plot(
        list(xlim),
        [0, 0],
        linestyle=":",
        color=(0.5, 0.5, 0.5),
    )
    baseline_artist, *_ = bram_ax.plot(
        list(xlim),
        [0, 0],
        linestyle=":",
        color=(0.5, 0.5, 0.5),
    )
    # Resource usages
    artists = []
    for i, lvl in enumerate(LEVELS_TO_PLOT):
        xs = [x + i * BAR_WIDTH for x in range(len(program_names))]
        # ALM usage
        ys = []
        for p in program_names:
            y = results[ProgramVariant(p, lvl)].alm
            baseline = results[ProgramVariant(p, OptimizationLevel.NONE)].alm
            ys.append( (y - baseline) / baseline )
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
        # BRAM usage
        ys = []
        for p in program_names:
            y = results[ProgramVariant(p, lvl)].bram
            baseline = results[ProgramVariant(p, OptimizationLevel.NONE)].bram
            if y == 0 and baseline == 0:
                ys.append(0)
                continue
            ys.append( (y - baseline) / baseline )
        artist = bram_ax.bar(
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
        labels = []
        for p in program_names:
            bram = results[ProgramVariant(p, lvl)].bram
            baseline = results[ProgramVariant(p, OptimizationLevel.NONE)].bram
            if bram == 0 and baseline == 0:
                percent_change = 0
            else:
                percent_change = (bram - baseline) / baseline
            if percent_change >= 0:
                label = f"+{percent_change:.0%}"
            else:
                label = f"{percent_change:.0%}"
            label = label.replace("%", r"\%")
            if label == r"+0\%":
                label = ""
            labels.append(label)
        bram_ax.bar_label(
            artist,
            labels=labels,
            padding=3,
        )
    # Display settings
    alm_ax.set_xlim(xlim)
    alm_ax.set_xticks(
        [x + (len(LEVELS_TO_PLOT) / 2 - 0.5) * BAR_WIDTH for x in range(len(program_names))],
        [f"\\texttt{{{p}}}" for p in program_names],
    )
    alm_ax.tick_params(axis="x", which="both", length=0)
    alm_ax.set_ylabel("\\% change\nALM usage")
    alm_ax.set_yticks([-1, 0, 1], [r"-100\%", r"0\%", r"100\%"])
    alm_ax.set_ylim(-1.25, 1)
    bram_ax.set_ylabel("\\% change\nBRAM usage")
    bram_ax.set_yticks([-1, 0], [r"-100\%", r"0\%"])
    bram_ax.set_ylim(-1.25, 0.1)
    legend_cols = 4
    legend_labels=[lvl.explanation for lvl in [OptimizationLevel.NONE] + LEVELS_TO_PLOT]
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
