#!/usr/bin/env python3

"""
This script plots the resource usages for the ablation study.
"""

import math

import matplotlib.pyplot as plt
import matplotlib.ticker as tick
from matplotlib.patches import Polygon

import lib.ablation_plot_settings as aps
import lib.ablation_results_crud as crud
import lib.constants as c
import lib.plt_utils as pu
from lib.program_variant import ProgramVariant
from lib.resource_usage import ResourceUsage

BRAM_YMAX = 23
# pylint: disable-next=line-too-long
WARNING = r"\textbf{{\Large $\triangle$}\hspace{-0.785em}\raisebox{0.2em}{\scriptsize!}}\hspace{0.5em}"


def plot_resource_usages(
    results: dict[ProgramVariant, ResourceUsage],
    fmax_results: dict[ProgramVariant, float],
) -> None:
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
        "text.latex.preamble": r"\usepackage{color}",
        "font.family": "Times New Roman",
        "font.size": 8,
    })
    fig, alm_ax = plt.subplots(
        nrows=1, ncols=1,
        figsize=(8, 1.0),
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
        color=(0, 0, 0),
        zorder=5,
    )
    # Resource usages
    artists = []
    for i, lvl in enumerate(aps.LEVELS_TO_PLOT):
        xs = [x + i * aps.BAR_WIDTH for x in range(len(program_names))]
        # ALM usage
        ys = []
        for p in program_names:
            pv = ProgramVariant(p, lvl)
            if pv not in results:
                print(f"WARNING: no results for {pv}")
                ys.append(0)
                continue
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
            zorder=10,
        )
        artists.append(artist)
        # Fmax warning labels
        for i, p in enumerate(program_names):
            pv = ProgramVariant(p, lvl)
            if pv not in fmax_results or fmax_results[pv] < c.TARGET_FREQ:
                alm_ax.annotate(WARNING, (xs[i], 0.25), ha="center", color="red")
    # Display settings
    alm_ax.grid(
        visible=True,
        which="major",
        axis="y",
        linewidth=0.2,
        color=(0.8, 0.8, 0.8)
    )
    alm_ax.set_xlim(xlim)
    alm_ax.set_xticks(
        [
            x + (len(aps.LEVELS_TO_PLOT) / 2 - 0.5) * aps.BAR_WIDTH
            for x in range(len(program_names))
        ],
        [aps.program_title(p) or "NONE" for p in program_names],
    )
    alm_ax.tick_params(axis="x", which="both", length=0)
    alm_ax.set_ylabel("\\% change\nALMs (log)")
    alm_ax.set_yscale("symlog")
    alm_ax.yaxis.set_major_formatter(tick.PercentFormatter(1))
    alm_ax.set_yticks([-1, 0, 1, 2, 10])
    alm_ax.set_ylim(-1, 40)

    fig.text(0.02, -0.13, "Lower is better")
    down_arrow = Polygon(
        [(0.005, -0.06), (0.015, -0.06), (0.01, -0.12)],
        fill=True, color='black', zorder=1000,
        transform=fig.transFigure, figure=fig
    )
    fig.patches.extend([down_arrow])
    legend_cols = 1 + math.ceil( (len(aps.LEVELS_TO_PLOT) + 1) / aps.LEGEND_ROWS )
    legend_labels=(
        [lvl.explanation for lvl in [aps.BASELINE_LVL] + aps.LEVELS_TO_PLOT]
            + ["timing requirements not met"]
    )
    empty_handle, *_ = alm_ax.plot([0], [0], linestyle="")
    legend_handles=[baseline_artist] + artists + [empty_handle]
    fig.legend(
        labels=pu.flip(legend_labels, legend_cols),
        handles=pu.flip(legend_handles, legend_cols),
        loc="upper right",
        bbox_to_anchor=(1.0, 0.04),
        ncols=legend_cols,
        handlelength=1.5,
    )
    fig.text(0.77, -0.18, WARNING, color="red", zorder=1000)

    fig.savefig(c.ABLATION_RESOURCE_USAGE_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    area_results = crud.read_valid_resource_usage_results(c.ABLATION_RESOURCE_USAGE_CSV)
    fmax_results = crud.read_valid_fmax(c.ABLATION_FMAX_CSV)
    plot_resource_usages(area_results, fmax_results)


if __name__ == "__main__":
    main()
