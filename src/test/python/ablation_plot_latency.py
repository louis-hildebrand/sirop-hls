#!/bin/python3

"""
This script plots the latencies for the ablation study.
"""

import sys

import matplotlib.pyplot as plt

import lib.ablation_results_crud as crud
import lib.constants as c
import lib.plt_utils as pu
from lib.latency import LatencyResult
from lib.optimization_level import OptimizationLevel
from lib.program_variant import ProgramVariant

LEVELS_TO_PLOT = [
    lvl
    for lvl in OptimizationLevel
    if lvl not in [OptimizationLevel.ALL_EXCEPT_SIMPL, OptimizationLevel.NONE]
]
BAR_SPACE = 0.2
BAR_WIDTH = (1 - BAR_SPACE) / len(LEVELS_TO_PLOT)
BAR_PADDING = 0.02
BAR_HATCH = ["//", "\\\\", "", "||"]
FACE_COLORS = ["#a6cee3", "#1f78b4", "#b2df8a", "#33a02c"]
EDGE_COLORS = ["black", "black", "black", "black"]
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
        "camera": 5,
    }.get(program_name, 6)


def plot_latency(results: dict[ProgramVariant, LatencyResult]) -> None:
    """
    Plot latency for each program.
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
    fig, ax = plt.subplots(
        nrows=1, ncols=1,
        figsize=(8, 1.1),
        layout="compressed",
    )
    # Baseline
    xlim = (
        -0.5*BAR_WIDTH - 0.5*BAR_SPACE,
        len(program_names) - 0.5*BAR_WIDTH - 0.5*BAR_SPACE
    )
    baseline_artist, *_ = ax.plot(
        list(xlim),
        [0, 0],
        linestyle=":",
        color=(0.5, 0.5, 0.5),
    )
    # Latency results
    artists = []
    for i, lvl in enumerate(LEVELS_TO_PLOT):
        xs = [x + i * BAR_WIDTH for x in range(len(program_names))]
        ys = []
        for p in program_names:
            baseline = results[ProgramVariant(p, OptimizationLevel.NONE)].latency
            if baseline is None:
                raise ValueError(f"Missing baseline for {p}")
            y = results[ProgramVariant(p, lvl)].latency or baseline
            ys.append((y - baseline) / baseline)
        artist = ax.bar(
            bottom=0,
            x=xs,
            height=ys,
            width=BAR_WIDTH - BAR_PADDING,
            label=str(lvl),
            hatch=BAR_HATCH[i],
            facecolor=FACE_COLORS[i],
            edgecolor=EDGE_COLORS[i],
            hatch_linewidth=HATCH_WIDTH,
        )
        artists.append(artist)
        labels = []
        for p in program_names:
            y = results[ProgramVariant(p, lvl)].latency
            if y is None:
                labels.append("-")
                continue
            baseline = results[ProgramVariant(p, OptimizationLevel.NONE)].latency
            if baseline is None:
                raise ValueError(f"Missing baseline for {p}")
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
        ax.bar_label(
            artist,
            labels=labels,
            padding=3,
        )
    # Display settings
    # ax.set_yscale("symlog")
    ax.set_ylabel("\\% change\nlatency")
    ax.set_yticks([-1, 0, 0.5], [r"-100\%", r"0\%", r"+50\%"])
    ax.set_ylim(-1.35, 0.55)
    ax.set_xticks(
        [x + (len(LEVELS_TO_PLOT) / 2 - 0.5) * BAR_WIDTH for x in range(len(program_names))],
        [f"\\texttt{{{p}}}" for p in program_names],
    )
    ax.set_xlim(xlim)
    # ax.set_yticks([y for y in ax.get_yticks() if y != 0])
    ax.tick_params(axis="x", which="both", length=0)
    legend_cols = 4
    legend_labels = (
        [OptimizationLevel.NONE.explanation]
            + [lvl.explanation for lvl in LEVELS_TO_PLOT]
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
