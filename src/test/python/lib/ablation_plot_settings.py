"""
Settings for the ablation study plots.
"""

from .optimization_level import OptimizationLevel

BASELINE_LVL = OptimizationLevel.ALL
LEVELS_TO_PLOT = [
    lvl
    for lvl in OptimizationLevel
    if lvl not in [BASELINE_LVL, OptimizationLevel.EXCEPT_STM_SIMPL]
]
BAR_SPACE = 0.2
BAR_WIDTH = (1 - BAR_SPACE) / len(LEVELS_TO_PLOT)
BAR_PADDING = 0.02
BAR_HATCH = ["//", "\\\\", "--", "++", "||", "xx", "/", "\\", "-", "+", "|", "x"]
# pylint: disable-next=line-too-long
FACE_COLORS = ["#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6", "#6a3d9a"]
EDGE_COLORS = ["black" for _ in FACE_COLORS]
LINE_STYLES = ["-" for _ in FACE_COLORS]
HATCH_WIDTH = 1
LEGEND_ROWS = 1


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
