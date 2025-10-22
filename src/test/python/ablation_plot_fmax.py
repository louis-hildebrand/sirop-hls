#!/bin/python3

"""
This script plots the max clock frequencies for the ablation study.
"""

import math
import sys

import matplotlib.pyplot as plt

import lib.ablation_plot_settings as aps
import lib.ablation_results_crud as crud
import lib.constants as c
import lib.plt_utils as pu
from lib.program_variant import ProgramVariant


def plot_fmax(results: dict[ProgramVariant, float]) -> None:
    """
    Plot fmax for each program.
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
    artists = []
    # Benchmark results
    for i, lvl in enumerate(aps.LEVELS_TO_PLOT):
        xs = [x + i * aps.BAR_WIDTH for x in range(len(program_names))]
        ys = [results.get(ProgramVariant(p, lvl), 0) for p in program_names]
        artist, *_ = ax.bar(
            xs, ys,
            width=aps.BAR_WIDTH - aps.BAR_PADDING,
            label=lvl.explanation,
            hatch=aps.BAR_HATCH[i],
            facecolor=aps.FACE_COLORS[i],
            edgecolor=aps.EDGE_COLORS[i],
            linestyle=aps.LINE_STYLES[i],
            hatch_linewidth=aps.HATCH_WIDTH,
        )
        artists.append(artist)
    # Target frequency
    xlim = (
        -0.5 * aps.BAR_WIDTH - 0.5*aps.BAR_SPACE,
        len(program_names) - 0.5 * aps.BAR_WIDTH - 0.5*aps.BAR_SPACE,
    )
    target_artist, *_ = ax.plot(
        list(xlim),
        [c.TARGET_FREQ, c.TARGET_FREQ],
        label="target",
        linestyle=":",
        color=(0.5, 0.5, 0.5),
    )
    ax.set_ylabel("fmax (MHz, log)")
    ax.set_yscale("log")
    ax.set_xlim(xlim)
    ax.set_xticks(
        [
            x + (len(aps.LEVELS_TO_PLOT) / 2 - 0.5) * aps.BAR_WIDTH
            for x in range(len(program_names))
        ],
        [aps.program_title(p) or "" for p in program_names],
    )
    ax.tick_params(axis="x", which="both", length=0)
    legend_cols = math.ceil( (len(aps.LEVELS_TO_PLOT) + 1) / aps.LEGEND_ROWS )
    legend_labels = ["target"] + [lvl.explanation for lvl in aps.LEVELS_TO_PLOT]
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
