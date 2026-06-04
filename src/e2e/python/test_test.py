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
    if (
        p.name.endswith(".test.txt")
        and p.with_suffix("").with_suffix("").with_suffix(".sirop").is_file()
    ):
        return True
    if (
        p.name.endswith(".test.expected.txt")
        and p.with_suffix("").with_suffix("").with_suffix(".sirop").is_file()
    ):
        return True
    if (
        p.name.endswith(".test.actual.txt")
        and p.with_suffix("").with_suffix("").with_suffix(".sirop").is_file()
    ):
        return True
    return False


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
    # Invoke compiler
    args = [
        "java", "-jar", c.JAR.as_posix(),
        "-i", src.as_posix(),
        "--out:test",
        "--overwrite",
    ]
    args += cli_args
    actual_compiler_output_file = c.ACTUAL_OUTPUTS / f"{name}.test.txt"
    actual_expected_output_file = c.ACTUAL_OUTPUTS / f"{name}.test.expected.txt"
    actual_actual_output_file = c.ACTUAL_OUTPUTS / f"{name}.test.actual.txt"
    if not actual_compiler_output_file.parent.exists():
        actual_compiler_output_file.parent.mkdir(exist_ok=True, parents=True)
    if src.with_suffix(".test.expected.txt").is_file():
        args += ["--out:test:expected", actual_expected_output_file]
    if src.with_suffix(".test.actual.txt").is_file():
        args += ["--out:test:actual", actual_actual_output_file]
    result = subprocess.run(
        args,
        encoding="utf-8",
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    actual_compiler_output_file.write_text(result.stdout, encoding="utf-8")
    # Check status code
    expected_code = 1 if src.parent.name.endswith("Error") else 0
    if result.returncode != expected_code:
        print(f"WRONG STATUS (expected {expected_code} but got {result.returncode})")
        return False
    # Check compiler messages
    expected_compiler_output_file = src.with_suffix(".test.txt")
    expected = expected_compiler_output_file.read_text(encoding="utf-8")
    if result.stdout != expected:
        print(
            f"WRONG OUTPUT (compare {expected_compiler_output_file.relative_to(c.ROOT)}"
            f" with {actual_compiler_output_file.relative_to(c.ROOT)})"
        )
        return False
    # Check dump of expected test outputs
    expected_expected_output_file = src.with_suffix(".test.expected.txt")
    if expected_expected_output_file.is_file():
        if not actual_expected_output_file.is_file():
            print(
                "MISSING EXPECTED DUMP"
                f" ({actual_expected_output_file.relative_to(c.ROOT)} should have been created)"
            )
            return False
        expected = expected_expected_output_file.read_text(encoding="utf-8")
        actual = actual_expected_output_file.read_text(encoding="utf-8")
        if actual != expected:
            print(
                "WRONG EXPECTED DUMP"
                f" (compare {expected_expected_output_file.relative_to(c.ROOT)})"
                f" with {actual_expected_output_file.relative_to(c.ROOT)})"
            )
            return False
    # Check dump of actual test outputs
    expected_actual_output_file = src.with_suffix(".test.actual.txt")
    if expected_actual_output_file.is_file():
        if not actual_actual_output_file.is_file():
            print(
                "MISSING ACTUAL DUMP"
                f" ({actual_actual_output_file.relative_to(c.ROOT)} should have been created)"
            )
            return False
        expected = expected_actual_output_file.read_text(encoding="utf-8")
        actual = actual_actual_output_file.read_text(encoding="utf-8")
        if actual != expected:
            print(
                "WRONG ACTUAL DUMP"
                f" (compare {expected_actual_output_file.relative_to(c.ROOT)})"
                f" with {actual_actual_output_file.relative_to(c.ROOT)})"
            )
            return False
    print("OK")
    return True
