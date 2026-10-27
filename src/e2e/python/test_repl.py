#!/usr/bin/env python3

"""
Script for checking a REPL session, similar to Python's doctest.
"""

from __future__ import annotations

import os
import re
import subprocess
import sys
import time
from argparse import ArgumentParser, Namespace
from dataclasses import dataclass
from pathlib import Path

import colorama
from colorama import Fore, Style

import constants as c


@dataclass
class ReplTest:
    """
    Separate inputs and expected outputs for a REPL session.
    """

    input_lines: list[str]
    """
    The lines of code to provide as input to the REPL.
    """

    expected_output_lines: list[str]
    """
    The lines of output that should be produced by the REPL.
    """

    omit_welcome: bool
    """
    This test has omitted the welcome message at the beginning; it starts immediately with the
    first prompt.
    When checking the actual output, the welcome message should be ignored.
    """

    omit_goodbye: bool
    """
    This test has omitted the goodbye message at the end.
    When checking the actual output, the goodbye message should be ignored.
    """

    sirop_version_with_hash: str
    """
    The full Sirop compiler version, possibly including the commit hash (e.g., "2.3.0-f6383969").
    """

    simple_sirop_version: str
    """
    The Sirop compiler version, without the commit hash (e.g., "2.3.0").
    """

    @classmethod
    def parse_expected(cls, expected_session: str, sirop_version: str) -> ReplTest:
        """
        Parse a raw REPL session (with interleaved inputs and outputs) to a ReplTest instance.
        """
        all_lines = expected_session.splitlines()
        if not all_lines:
            raise ValueError("no lines found to test")
        input_lines = [
            line.removeprefix(">").removeprefix(" ")
            for line in all_lines
            if line.startswith(">")
        ]
        expected_output_lines = []
        for line in all_lines:
            if line == "$ sirop":
                pass
            elif line.startswith(">"):
                # Notice that we always expect a trailing space even if the file omits it
                expected_output_lines.append("> ")
            else:
                expected_output_lines.append(line.replace("${COMPILER_VERSION}", sirop_version))
        simple_sirop_version = sirop_version
        if (m := re.fullmatch(r"([0-9]+\.[0-9]+\.[0-9]+)-[a-zA-Z0-9]+", sirop_version)):
            simple_sirop_version = m[1]
        else:
            simple_sirop_version = sirop_version
        return ReplTest(
            input_lines=input_lines,
            expected_output_lines=expected_output_lines,
            omit_welcome=(not all_lines or all_lines[0].startswith(">")),
            omit_goodbye=(not all_lines or all_lines[-1] != "Goodbye"),
            sirop_version_with_hash=sirop_version,
            simple_sirop_version=simple_sirop_version,
        )

    def parse_actual(self, output: str) -> list[str]:
        actual_output_lines = []
        for line in output.splitlines():
            while line.startswith(">"):
                if line.startswith("> "):
                    actual_output_lines.append("> ")
                    line = line[2:]
                else:
                    # No space after the >
                    print("WARNING: missing space after REPL prompt", file=sys.stderr)
                    actual_output_lines.append(">")
                    line = line[1:]
            if line:
                actual_output_lines.append(line)
        # Filter out welcome message
        if self.omit_welcome:
            while actual_output_lines and not actual_output_lines[0].startswith(">"):
                actual_output_lines = actual_output_lines[1:]
        # Filter out goodbye message
        if self.omit_goodbye and actual_output_lines and actual_output_lines[-1] == "Goodbye":
            actual_output_lines = actual_output_lines[:-1]
            if actual_output_lines and actual_output_lines[-1] == "> ":
                actual_output_lines = actual_output_lines[:-1]
        return actual_output_lines

    def reconstruct_session(self, actual_output_lines: list[str]) -> str:
        session_lines = []
        next_input_line = 0
        for line in actual_output_lines:
            if line == "> ":
                if next_input_line < len(self.input_lines):
                    expr = self.input_lines[next_input_line]
                    next_input_line += 1
                else:
                    expr = ""
                session_lines.append("> " + expr)
            else:
                session_lines.append(line)
        return "".join(line + "\n" for line in session_lines)

    def matches_expected(self, actual_output_lines: list[str]) -> bool:
        remove_whitespace = False
        i = 0 # actual
        j = 0 # expected
        while True:
            # Skip any comments in the expected output sequence
            while j < len(self.expected_output_lines):
                expected_line = self.expected_output_lines[j]
                if expected_line.startswith("/// test_repl: remove_whitespace"):
                    remove_whitespace = True
                    j += 1
                elif expected_line.startswith("///"):
                    j += 1
                else:
                    break

            # Check if we're done
            if i >= len(actual_output_lines) and j >= len(self.expected_output_lines):
                # Both sequences exhausted at the same time: good!
                return True
            if j >= len(self.expected_output_lines):
                # Expected sequence is empty but actual sequence is still non-empty: bad!
                assert i < len(actual_output_lines)
                return False
            if i >= len(actual_output_lines):
                # Actual sequence is empty but expected sequence is still non-empty: bad!
                assert j < len(self.expected_output_lines)
                return False

            actual_line = actual_output_lines[i]
            expected_line = self.expected_output_lines[j]
            if expected_line.startswith(">"):
                # Reset directives at each prompt
                remove_whitespace = False
            # Apply directives
            if remove_whitespace:
                actual_line = re.sub(r"\s+", "", actual_line)
                expected_line = re.sub(r"\s+", "", expected_line)

            # Compare lines
            if actual_line != expected_line:
                # Maybe it's just the compiler version that differs.
                # See if replacing the version+commit_hash with just the version fixes it.
                #
                # It would be really annoying to need to update the docs on every single commit.
                # However, it would still be nice to keep the version up to date.
                simplified_actual_line = actual_line.replace(
                    self.sirop_version_with_hash,
                    self.simple_sirop_version,
                )
                if simplified_actual_line != expected_line:
                    return False

            i += 1
            j += 1


def run(
    name: str,
    expected_session: Path,
    actual_file: Path,
    sirop_version: str,
    cli_args: list[str],
    save: bool,
) -> bool:
    print(f"{name} (REPL) ... ", end="", flush=True)

    test = ReplTest.parse_expected(
        expected_session.read_text(encoding="utf-8"),
        sirop_version=sirop_version,
    )

    result = subprocess.run(
        [
            "java",
            # If this argument is omitted, the JVM emits a warning "An illegal reflective
            # access operation has occurred".
            # Yet when the argument --illegal-access=deny is passed, everything works fine (?!)
            # What's also strange is that I am unable to reproduce this behaviour in bash using
            #     cat file.sirop | java -jar sirop.jar
            # or
            #     java -jar sirop.jar < file.sirop
            # No warning is printed in either case.
            "--illegal-access=deny",
            "-jar", c.JAR.as_posix()
        ] + cli_args,
        input="".join(line + "\n" for line in test.input_lines),
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        encoding="utf-8",
        check=False,
    )

    if not actual_file.parent.exists():
        actual_file.parent.mkdir(exist_ok=True, parents=True)

    if result.returncode != 0:
        actual_file.write_text(result.stdout, encoding="utf-8")
        print(Fore.RED + f"NONZERO EXIT CODE ({result.returncode})" + Style.RESET_ALL)
        return False

    actual_output_lines = test.parse_actual(result.stdout)
    actual_session = test.reconstruct_session(actual_output_lines)

    actual_file.write_text(actual_session, encoding="utf-8")

    if not test.matches_expected(actual_output_lines):
        if save:
            expected_session.write_text(actual_session, encoding="utf-8")
        else:
            print(Fore.RED + "WRONG OUTPUT" + Style.RESET_ALL)
            return False

    print(Fore.GREEN + "OK" + Style.RESET_ALL)
    return True


def main(files: list[Path], actual_dir: Path, save: bool) -> None:
    start_timestamp = time.monotonic()
    # Find compiler version
    sirop_version = subprocess.run(
        ["java", "-jar", c.JAR.as_posix(), "--version"],
        capture_output=True,
        encoding="utf-8",
        check=True
    )
    assert not sirop_version.stderr
    sirop_version = sirop_version.stdout.strip()
    # Replace directories by files within them
    expanded_files = []
    for f in files:
        if f.is_dir():
            expanded_files += f.rglob("*.repl.txt")
        elif f.is_file():
            expanded_files.append(f)
        else:
            raise ValueError(f"{f.as_posix()} is neither a file nor a directory")
    files = expanded_files
    # Run tests
    common_directory = os.path.commonpath([p.parent for p in files])
    errors = 0
    for p in files:
        name = p.relative_to(common_directory).as_posix()
        actual_file = actual_dir.joinpath(name).resolve()
        actual_file.parent.mkdir(exist_ok=True, parents=True)
        ok = run(
            name=name,
            expected_session=p,
            actual_file=actual_file,
            sirop_version=sirop_version,
            cli_args=[],
            save=save,
        )
        if not ok:
            errors += 1
    end_timestamp = time.monotonic()
    elapsed_time = end_timestamp - start_timestamp
    elapsed_time = f" (in {elapsed_time:.1f} seconds)"
    print()
    if errors > 0:
        print(Fore.RED + f"{errors} tests failed" + Style.RESET_ALL)
        sys.exit(1)
    print(Fore.GREEN + "All tests passed!" + Style.RESET_ALL + elapsed_time)


def _parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description="test that the REPL's output matches a given session",
    )
    parser.add_argument(
        "files",
        nargs="*",
        type=Path,
        help="paths of the files to test",
    )
    parser.add_argument(
        "--actual-dir",
        required=True,
        type=Path,
        help="path of the directory to use for storing the actual outputs",
    )
    parser.add_argument(
        "--save",
        action="store_true",
        help="overwrite the expected results with whatever the compiler currently outputs",
    )
    parser.add_argument(
        "--no-color",
        action="store_true",
        help="suppress colored output",
    )
    args = parser.parse_args()
    args.files = [p.resolve() for p in args.files]
    return args


if __name__ == "__main__":
    _args = _parse_args()
    colorama.init(strip=True if _args.no_color else None)
    main(files=_args.files, actual_dir=_args.actual_dir, save=_args.save)
