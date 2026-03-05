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
        OptimizationLevel.SMALL_BUFFERS,
        OptimizationLevel.SMALL_BUFFERS_AND_LATMATCH
    ]
]
BAR_SPACE = 0.3
BAR_WIDTH = (1 - BAR_SPACE) / len(LEVELS_TO_PLOT)
BAR_PADDING = 0.04
BAR_HATCH = ["///", "\\\\\\", "---", "+++", "||", "xx", "/", "\\", "-", "+", "|", "x"]
# pylint: disable-next=line-too-long
FACE_COLORS = ["#fdbe85", "#fd8d3c", "#e6550d", "#a63603"]
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
        "matmat": 3,
        "conv1d": 4,
        "conv2d": 5,
        "convb2b": 6,
        "sharpen": 7,
        "sobel": 8,
        "camera": 9,
    }.get(program_name, 10)


def program_title(program_name: str) -> str | None:
    """
    Return the title for the given program in the plots, or `None` if the program should be omitted.
    """
    if program_name == "sqrt":
        return None
    title = {
        "matvec_1": "mv",
        "matmat": "mm"
    }.get(program_name, program_name)
    return f"\\texttt{{{title}}}"
