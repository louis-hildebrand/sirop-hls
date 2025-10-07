#!/bin/python3

"""
This script plots the latencies for the ablation study.
"""

import math
import sys

import matplotlib.pyplot as plt
import matplotlib.ticker as tick

import lib.ablation_plot_settings as aps
import lib.ablation_results_crud as crud
import lib.constants as c
import lib.plt_utils as pu
from lib.latency import LatencyResult
from lib.program_variant import ProgramVariant


def plot_latency(results: dict[ProgramVariant, LatencyResult]) -> None:
    """
    Plot latency for each program.
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
    # Latency results
    artists = []
    for i, lvl in enumerate(aps.LEVELS_TO_PLOT):
        xs = [x + i * aps.BAR_WIDTH for x in range(len(program_names))]
        ys = []
        for p in program_names:
            baseline = results.get(ProgramVariant(p, aps.BASELINE_LVL))
            baseline = baseline.latency if baseline is not None else None
            if baseline is None:
                print(f"WARNING: Missing baseline for {p}")
                ys.append(0)
                continue
            y = results[ProgramVariant(p, lvl)].latency or baseline
            ys.append((y - baseline) / baseline)
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
        labels = []
        for p in program_names:
            baseline = results.get(ProgramVariant(p, aps.BASELINE_LVL))
            baseline = baseline.latency if baseline is not None else None
            if baseline is None:
                print(f"WARNING: Missing baseline for {p}")
                labels.append("?")
                continue
            y = results.get(ProgramVariant(p, lvl))
            y = y.latency if y is not None else None
            if y is None:
                labels.append("x")
                continue
            percent_change = (y - baseline) / baseline
            if percent_change >= 0:
                label = f"+{percent_change:.0%}"
            else:
                label = f"{percent_change:.0%}"
            if label in {"+0%", "-0%"}:
                label = ""
            label = label.replace("%", r"\%")
            # diff = y - baseline
            # if diff == 0:
            #     label = ""
            # elif diff > 0:
            #     label = f"+{diff}"
            # else:
            #     label = f"{diff}"
            labels.append(label)
        # ax.bar_label(
        #     artist,
        #     labels=labels,
        #     padding=3,
        # )
    # Display settings
    # ax.set_yscale("symlog")
    ax.set_ylabel("\\% change\nlatency")
    ax.yaxis.set_major_formatter(tick.PercentFormatter(1))
    # ax.set_ylim(-1.35, 0.55)
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
