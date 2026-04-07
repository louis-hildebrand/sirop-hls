#!/usr/bin/env python3

"""
Script for running end-to-end tests.
"""

from pathlib import Path
import os
import subprocess
import sys


ROOT = Path(__file__).resolve().parent.parent.parent.parent
JAR = ROOT / "target" / "scala-2.12" / "sirop.jar"
RESOURCES = ROOT / "src" / "e2e" / "resources"
ACTUAL_OUTPUTS = RESOURCES / "actual"


def look_for_unused_files() -> None:
    """
    Scan the resources directory and exit with an error if any files are unused.
    """
    error_count = 0
    for (root, _, files) in os.walk(RESOURCES):
        root = Path(root)
        if root.is_relative_to(ACTUAL_OUTPUTS):
            continue
        files = [root.joinpath(f) for f in files]
        for f in files:
            if f.name.endswith(".sirop"):
                continue
            if f.name.endswith(".eval.txt") and f.with_suffix("").with_suffix(".sirop").is_file():
                continue
            if f.name.endswith(".repl.txt") and f.with_suffix("").with_suffix(".sirop").is_file():
                continue
            print(f"File {f.relative_to(ROOT)} is not used for testing")
            error_count += 1
    if error_count > 0:
        file_or_files = "file" if error_count == 1 else "files"
        is_or_are = "is" if error_count == 1 else "are"
        it_or_them = "it" if error_count == 1 else "them"
        print()
        print(
            f"{error_count} {file_or_files} within {RESOURCES.relative_to(ROOT)}"
            f" {is_or_are} not used for testing."
            f" Consider deleting or moving {it_or_them}."
        )
        sys.exit(1)


def test_eval(eval_output: Path) -> bool:
    """
    Test that evaluating the program produces the expected output from the given file.
    """
    name = eval_output.with_suffix("").with_suffix("").relative_to(RESOURCES).as_posix()
    print(f"{name} (eval) ... ", end="", flush=True)
    source_path = eval_output.with_suffix("").with_suffix(".sirop")
    result = subprocess.run(
        [
            "java", "-jar", JAR.as_posix(),
            "-i", source_path.as_posix(),
            "--out:eval",
            "--quiet",
        ],
        encoding="utf-8",
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    actual_out_file = ACTUAL_OUTPUTS / f"{name}.eval.txt"
    if not actual_out_file.parent.exists():
        actual_out_file.parent.mkdir(exist_ok=True, parents=True)
    actual_out_file.write_text(result.stdout, encoding="utf-8")
    expected_code = 1 if eval_output.parent.name.endswith("Error") else 0
    if result.returncode != expected_code:
        print(f"WRONG STATUS (expected {expected_code} but got {result.returncode})")
        return False
    expected = eval_output.read_text(encoding="utf-8")
    if result.stdout != expected:
        print(
            f"WRONG OUTPUT (compare {eval_output.relative_to(ROOT)}"
            f" with {actual_out_file.relative_to(ROOT)})"
        )
        return False
    actual_out_file.unlink(missing_ok=True)
    print("OK")
    return True


def test_repl(repl_output: Path, compiler_version: str) -> bool:
    """
    Test that an interactive session produces the expected output from the given file.
    """
    name = repl_output.with_suffix("").with_suffix("").relative_to(RESOURCES).as_posix()
    print(f"{name} (REPL) ... ", end="", flush=True)
    source_path = repl_output.with_suffix("").with_suffix(".sirop")
    with open(source_path, "r", encoding="utf-8") as f:
        result = subprocess.run(
            [
                "java",
                # If this argument is omitted, the JVM emits a warning "An illegal reflactive
                # access operation has occurred".
                # Yet when the argument --illegal-access=deny is passed, everything works fine (?!)
                # What's also strange is that I am unable to reproduce this behaviour in bash using
                #     cat file.sirop | java -jar sirop.jar
                # or
                #     java -jar sirop.jar < file.sirop
                # No warning is printed in either case.
                "--illegal-access=deny",
                "-jar", JAR.as_posix()
            ],
            encoding="utf-8",
            stdin=f,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            check=False,
        )
    actual_out_file = ACTUAL_OUTPUTS / f"{name}.repl.txt"
    if not actual_out_file.parent.exists():
        actual_out_file.parent.mkdir(exist_ok=True, parents=True)
    actual_out_file.write_text(result.stdout, encoding="utf-8")
    expected_code = 0
    if result.returncode != expected_code:
        print(f"WRONG STATUS (expected {expected_code} but got {result.returncode})")
        return False
    expected = (
        repl_output.read_text(encoding="utf-8")
            .replace("${COMPILER_VERSION}", compiler_version)
    )
    if result.stdout.rstrip() != expected.rstrip():
        print(
            f"WRONG OUTPUT (compare {repl_output.relative_to(ROOT)}"
            f" with {actual_out_file.relative_to(ROOT)})"
        )
        return False
    actual_out_file.unlink(missing_ok=True)
    print("OK")
    return True


def main() -> None:
    """
    Script entry point.
    """
    look_for_unused_files()
    os.chdir(ROOT)
    print("Building Scala project...")
    subprocess.run(["sbt", "assembly"], check=True, capture_output=True)
    compiler_version = subprocess.run(
        ["java", "-jar", JAR.as_posix(), "--version"],
        capture_output=True,
        encoding="utf-8",
        check=True
    )
    compiler_version = compiler_version.stdout.strip()
    print(f"Testing v{compiler_version}")
    test_sources = sorted(RESOURCES.rglob("**/*.sirop"))
    if not test_sources:
        print("No test cases found")
        sys.exit(1)
    print(f"Found {len(test_sources)} .sirop files to test")
    print()
    error_count = 0
    for test in test_sources:
        ran = False
        if (eval_output := test.with_suffix(".eval.txt")).is_file():
            ran = True
            ok = test_eval(eval_output)
            if not ok:
                error_count += 1
        if (repl_output := test.with_suffix(".repl.txt")).is_file():
            ran = True
            ok = test_repl(repl_output, compiler_version)
            if not ok:
                error_count += 1
        if not ran:
            print(f"ERROR: Nothing to do for file {test.relative_to(ROOT)}")
            error_count += 1
    if error_count > 0:
        test_or_tests = "test" if error_count == 1 else "tests"
        print()
        print(f"{error_count} {test_or_tests} failed")
        sys.exit(1)
    print()
    print("All tests passed!")


if __name__ == "__main__":
    main()
