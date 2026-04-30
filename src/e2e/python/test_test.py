#!/usr/bin/env python3

"""
Functions for testing the Sirop compiler's ability to run Sirop test suites.
"""

from pathlib import Path
import subprocess

import constants as c

def uses_file(p: Path) -> bool:
    """
    Check whether the given file is used for testing the Sirop test runner.
    """
    return (
        p.name.endswith(".test.txt")
        and p.with_suffix("").with_suffix("").with_suffix(".sirop").is_file()
    )


def can_run(src: Path) -> bool:
    """
    Check whether there are any files describing expected outputs for the Sirop test runner.
    """
    return src.with_suffix(".test.txt").is_file()


def run(src: Path, cli_args: list[str]) -> bool:
    """
    Test that the Sirop test runner produces the expected outputs.
    """
    name = src.with_suffix("").relative_to(c.RESOURCES).as_posix()
    print(f"{name} (test) ... ", end="", flush=True)
    result = subprocess.run(
        [
            "java", "-jar", c.JAR.as_posix(),
            "-i", src.as_posix(),
            "--out:test",
        ] + cli_args,
        encoding="utf-8",
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    actual_out_file = c.ACTUAL_OUTPUTS / f"{name}.test.txt"
    if not actual_out_file.parent.exists():
        actual_out_file.parent.mkdir(exist_ok=True, parents=True)
    actual_out_file.write_text(result.stdout, encoding="utf-8")
    expected_code = 1 if src.parent.name.endswith("Error") else 0
    if result.returncode != expected_code:
        print(f"WRONG STATUS (expected {expected_code} but got {result.returncode})")
        return False
    expected_out_file = src.with_suffix(".test.txt")
    expected = expected_out_file.read_text(encoding="utf-8")
    if result.stdout != expected:
        print(
            f"WRONG OUTPUT (compare {expected_out_file.relative_to(c.ROOT)}"
            f" with {actual_out_file.relative_to(c.ROOT)})"
        )
        return False
    actual_out_file.unlink(missing_ok=True)
    print("OK")
    return True
