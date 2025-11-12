"""
Settings for the ablation study plots.
"""

from .optimization_level import OptimizationLevel

BASELINE_LVL = OptimizationLevel.ALL
LEVELS_TO_PLOT = [
    lvl
    for lvl in OptimizationLevel
    if lvl not in [
        BASELINE_LVL,
        OptimizationLevel.EXCEPT_STM_SIMPL,
        OptimizationLevel.SMALL_BUFFERS,
        OptimizationLevel.SMALL_BUFFERS_AND_LATMATCH
    ]
]
BAR_SPACE = 0.3
BAR_WIDTH = (1 - BAR_SPACE) / len(LEVELS_TO_PLOT)
BAR_PADDING = 0.04
BAR_HATCH = ["//", "\\\\", "--", "++", "||", "xx", "/", "\\", "-", "+", "|", "x"]
# pylint: disable-next=line-too-long
FACE_COLORS = ["#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6", "#6a3d9a"]
EDGE_COLORS = ["black" for _ in FACE_COLORS]
LINE_STYLES = ["-" for _ in FACE_COLORS]
HATCH_WIDTH = 1
LEGEND_ROWS = 1


def program_order(program_name: str) -> int:
    """
    Choose the order of the programs in the plots.
    """
    return {
        "map": 0,
        "dot": 1,
        "matvec_1": 2,
        "conv1d": 3,
        "conv2d": 4,
        "convb2b": 5,
        "sharpen": 6,
        "sobel": 7,
        "camera": 8,
    }.get(program_name, 9)


def program_title(program_name: str) -> str | None:
    """
    Return the title for the given program in the plots, or `None` if the program should be omitted.
    """
    if program_name == "sqrt":
        return None
    title = {
        "matvec_1": "matvec"
    }.get(program_name, program_name)
    return f"\\texttt{{{title}}}"
