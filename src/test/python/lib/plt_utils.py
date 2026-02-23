"""
Helper functions for plots.
"""

from typing import TypeVar

from matplotlib.figure import Figure
from matplotlib.lines import Line2D
from matplotlib.patches import Polygon

T = TypeVar("T")


def flip(items: list[T], ncol: int) -> list[T]:
    """
    Change the order of the given items so that they are read left-to-right and then
    top-to-bottom (row-major order), rather than top-to-bottom and then left-to-right (column-major
    order).
    """
    return [x for i in range(ncol) for x in items[i::ncol]]


def dedup(xs: list[str]) -> list[str]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


def draw_lower_is_better_message(
    fig: Figure,
    text_x: float,
    text_y: float,
    horiz_lines: list[float] | None = None,
    vert_lines: list[float] | None = None,
) -> None:
    """
    Add the message "Lower is better" at the given position with a little downward-pointing
    triangle next to it.
    """
    transform = fig.transFigure.inverted()
    text = fig.text(text_x, text_y, "L")
    bbox = text.get_window_extent()
    (x0, _) = transform.transform([bbox.x0, bbox.y0])
    (x1, y1) = transform.transform([bbox.x1, bbox.y1])
    first_letter_width = x1 - x0
    text = fig.text(text_x, text_y, "Lower is better")
    bbox = text.get_window_extent()
    (x0, _) = transform.transform([bbox.x0, bbox.y0])
    (x1, y1) = transform.transform([bbox.x1, bbox.y1])
    tri_height = y1 - text_y
    space = first_letter_width
    tri_width = tri_height * fig.get_figheight() / fig.get_figwidth()
    down_arrow = Polygon(
        [
            (text_x - space - tri_width, text_y + tri_height),
            (text_x - space, text_y + tri_height),
            (text_x - space - tri_width/2, text_y)
        ],
        fill=True, color='black', zorder=1000,
        transform=fig.transFigure,
    )
    fig.patches.extend([down_arrow])

    horiz_lines = horiz_lines or []
    for y in horiz_lines:
        fig.add_artist(Line2D([0, 1], [y, y], linewidth=1))
    vert_lines = vert_lines or []
    for x in vert_lines:
        fig.add_artist(Line2D([x, x], [-0.5, 1.5], linewidth=1))
