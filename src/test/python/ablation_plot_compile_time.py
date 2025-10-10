#!/bin/python3

"""
This script plots the compile times for the ablation study.
"""

import math
import sys

import matplotlib.pyplot as plt
import matplotlib.ticker as tick

import lib.ablation_plot_settings as aps
import lib.ablation_results_crud as crud
import lib.constants as c
import lib.plt_utils as pu
from lib.compile_time import CompileTimeReport
from lib.program_variant import ProgramVariant


def plot(results: dict[ProgramVariant, CompileTimeReport]) -> None:
    """
    Plot compile time for each program.
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
    fig, ax = plt.subplots(
        nrows=1, ncols=1,
        figsize=(8, 2),
        layout="compressed",
    )
    # Baseline
    xlim = (
        -0.5*aps.BAR_WIDTH - 0.5*aps.BAR_SPACE,
        len(program_names) - 0.5*aps.BAR_WIDTH - 0.5*aps.BAR_SPACE
    )
    baseline_artist, *_ = ax.plot(
        list(xlim),
        [0, 0],
        linestyle=":",
        color=(0.5, 0.5, 0.5),
    )
    # Compile times
    artists = []
    for i, lvl in enumerate(aps.LEVELS_TO_PLOT):
        xs = [x + i * aps.BAR_WIDTH for x in range(len(program_names))]
        ys = []
        for p in program_names:
            baseline = results[ProgramVariant(p, aps.BASELINE_LVL)].total
            y = results[ProgramVariant(p, lvl)].total
            ys.append( (y - baseline) / baseline )
        artist = ax.bar(
            bottom=0,
            x=xs,
            height=ys,
            width=aps.BAR_WIDTH - aps.BAR_PADDING,
            label=str(lvl),
            hatch=aps.BAR_HATCH[i],
            facecolor=aps.FACE_COLORS[i],
            edgecolor=aps.EDGE_COLORS[i],
            hatch_linewidth=aps.HATCH_WIDTH,
        )
        artists.append(artist)
    # Display settings
    ax.set_ylabel("\\% change\ncompile time")
    ax.yaxis.set_major_formatter(tick.PercentFormatter(1))
    ax.set_xticks(
        [
            x + (len(aps.LEVELS_TO_PLOT) / 2 - 0.5) * aps.BAR_WIDTH
            for x in range(len(program_names))
        ],
        [aps.program_title(p) or "" for p in program_names],
    )
    ax.set_xlim(xlim)
    ax.tick_params(axis="x", which="both", length=0)
    legend_cols = math.ceil( (len(aps.LEVELS_TO_PLOT) + 1) / aps.LEGEND_ROWS )
    legend_labels = (
        [aps.BASELINE_LVL.explanation]
            + [lvl.explanation for lvl in aps.LEVELS_TO_PLOT]
    )
    legend_handles = [baseline_artist] + artists
    fig.legend(
        labels=pu.flip(legend_labels, legend_cols),
        handles=pu.flip(legend_handles, legend_cols),
        loc="upper center",
        bbox_to_anchor=(0.5, 0),
        ncols=legend_cols,
    )
    fig.savefig(c.ABLATION_COMPILE_TIME_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    results = crud.read_compile_times(c.ABLATION_COMPILE_TIME_CSV)
    if not results:
        sys.exit("No results to plot.")
    plot(results)


if __name__ == "__main__":
    main()
