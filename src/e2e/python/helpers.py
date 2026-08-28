"""
Utility functions.
"""

from pathlib import Path
import filecmp
import shutil

import constants as c


class TestFailed(Exception):
    """
    A test failed.
    """

    def __init__(self, message: str) -> None:
        super().__init__(message)


def assert_equals(name: str, actual_path: Path, expected_path: Path, *, save: bool) -> None:
    """
    Check that the contents of the two files match; raise `TestFailed` if not.
    If `save` is `True`, then overwrite `expected_path` with the contents of `actual_path`.
    """
    if not actual_path.is_file():
        raise TestFailed(
            f"MISSING {name.upper()}"
            + f" ({actual_path.relative_to(c.ROOT)} should have been created)"
        )
    if save:
        shutil.copy(src=actual_path, dst=expected_path)
    equal = filecmp.cmp(actual_path, expected_path, shallow=False)
    if not equal:
        raise TestFailed(
            f"WRONG {name.upper()}"
            + f" (compare {expected_path.relative_to(c.ROOT)}"
            + f" with {actual_path.relative_to(c.ROOT)})"
        )
