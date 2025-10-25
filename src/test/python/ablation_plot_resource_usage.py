#!/bin/python3

"""
This script plots the resource usages for the ablation study.
"""

import math
import sys

import matplotlib.pyplot as plt
import matplotlib.ticker as tick
from matplotlib.patches import Polygon

import lib.ablation_plot_settings as aps
import lib.ablation_results_crud as crud
import lib.constants as c
import lib.plt_utils as pu
from lib.program_variant import ProgramVariant
from lib.resource_usage import ResourceUsage

BRAM_YMAX = 20


def plot_resource_usages(results: dict[ProgramVariant, ResourceUsage]) -> None:
    """
    Plot resource usage for each program.
    """
    program_names = pu.dedup([
        p.name
        for p in results.keys()
        if aps.program_title(p.name) is not None
    ])
    program_names = sorted(program_names, key=aps.program_order)
    if not program_names:
        raise ValueError("Nothing to plot.")
    plt.rcParams.update({
        "text.usetex": True,
        "font.family": "Times New Roman",
        "font.size": 8,
    })
    fig, (alm_ax, bram_ax) = plt.subplots(
        nrows=2, ncols=1,
        figsize=(8, 2.5),
        layout="compressed",
        sharex="col",
    )
    # Baseline
    xlim = (
        -0.5 * aps.BAR_WIDTH - 0.5*aps.BAR_SPACE,
        len(program_names) - 0.5 * aps.BAR_WIDTH - 0.5*aps.BAR_SPACE,
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
    for i, lvl in enumerate(aps.LEVELS_TO_PLOT):
        xs = [x + i * aps.BAR_WIDTH for x in range(len(program_names))]
        # ALM usage
        ys = []
        for p in program_names:
            y = results[ProgramVariant(p, lvl)].alm
            baseline = results[ProgramVariant(p, aps.BASELINE_LVL)].alm
            ys.append( (y - baseline) / baseline )
        artist = alm_ax.bar(
            bottom=0,
            x=xs,
            height=ys,
            width=aps.BAR_WIDTH - aps.BAR_PADDING,
            label=str(lvl),
            hatch=aps.BAR_HATCH[i],
            facecolor=aps.FACE_COLORS[i],
            edgecolor=aps.EDGE_COLORS[i],
            linestyle=aps.LINE_STYLES[i],
            hatch_linewidth=aps.HATCH_WIDTH,
        )
        artists.append(artist)
        # labels = []
        # for p in program_names:
        #     alm = results[ProgramVariant(p, lvl)].alm
        #     baseline = results[ProgramVariant(p, BASELINE_LVL)].alm
        #     percent_change = (alm - baseline) / baseline
        #     if percent_change >= 0:
        #         label = f"+{percent_change:.0%}"
        #     else:
        #         label = f"{percent_change:.0%}"
        #     label = label.replace("%", r"\%")
        #     if label == r"+0\%":
        #         label = ""
        #     labels.append(label)
        # alm_ax.bar_label(
        #     artist,
        #     labels=labels,
        #     padding=3,
        # )
        # BRAM usage
        ys = []
        for p in program_names:
            y = results[ProgramVariant(p, lvl)].bram
            baseline = results[ProgramVariant(p, aps.BASELINE_LVL)].bram
            if y == 0 and baseline == 0:
                ys.append(0)
                continue
            if baseline == 0:
                print(f"WARNING: baseline uses zero BRAMs for program {p}")
                ys.append(BRAM_YMAX)
                continue
            ys.append( (y - baseline) / baseline )
        artist = bram_ax.bar(
            bottom=0,
            x=xs,
            height=ys,
            width=aps.BAR_WIDTH - aps.BAR_PADDING,
            label=str(lvl),
            hatch=aps.BAR_HATCH[i],
            facecolor=aps.FACE_COLORS[i],
            edgecolor=aps.EDGE_COLORS[i],
            linestyle=aps.LINE_STYLES[i],
            hatch_linewidth=aps.HATCH_WIDTH,
        )
        labels = []
        for p in program_names:
            bram = results[ProgramVariant(p, lvl)].bram
            baseline = results[ProgramVariant(p, aps.BASELINE_LVL)].bram
            if baseline == 0 and bram != 0:
                label = r"$\infty$"
            else:
                label = ""
            labels.append(label)
        bram_ax.bar_label(
            artist,
            labels=labels,
            label_type="center",
            color="white",
        )
    # Display settings
    alm_ax.set_xlim(xlim)
    alm_ax.set_xticks(
        [
            x + (len(aps.LEVELS_TO_PLOT) / 2 - 0.5) * aps.BAR_WIDTH
            for x in range(len(program_names))
        ],
        [aps.program_title(p) for p in program_names],
    )
    alm_ax.tick_params(axis="x", which="both", length=0)
    alm_ax.set_ylabel("\\% change\nALM usage")
    alm_ax.set_yscale("symlog")
    alm_ax.yaxis.set_major_formatter(tick.PercentFormatter(1))
    alm_ax.set_ylim(-0.5, 40)
    bram_ax.tick_params(axis="x", which="both", length=0)
    bram_ax.set_ylabel("\\% change\nBRAM usage")
    bram_ax.set_yscale("symlog")
    bram_ax.yaxis.set_major_formatter(tick.PercentFormatter(1))
    bram_ax.set_ylim(-1.2, BRAM_YMAX)
    bram_ax.set_yticks([-1, 0, 1, 10])
    fig.text(0.0275, -0.03, "Lower is better")
    down_arrow = Polygon(
        [(0.0075, 0.005), (0.0175, 0.005), (0.0125, -0.03)],
        fill=True, color='black', zorder=1000,
        transform=fig.transFigure, figure=fig
    )
    fig.patches.extend([down_arrow])
    legend_cols = math.ceil( (len(aps.LEVELS_TO_PLOT) + 1) / aps.LEGEND_ROWS )
    legend_labels=[lvl.explanation for lvl in [aps.BASELINE_LVL] + aps.LEVELS_TO_PLOT]
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
