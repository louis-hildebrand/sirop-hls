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
            "-s", "sirop",
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


def main() -> None:
    """
    Script entry point.
    """
    os.chdir(ROOT)
    subprocess.run(["sbt", "assembly"], check=True)
    test_sources = sorted(RESOURCES.rglob("**/*.sirop"))
    if not test_sources:
        print("No test cases found")
        sys.exit(1)
    print()
    print(f"Found {len(test_sources)} .sirop files to test")
    error_count = 0
    for test in test_sources:
        if (eval_output := test.with_suffix(".eval.txt")).is_file():
            ok = test_eval(eval_output)
            if not ok:
                error_count += 1
        else:
            print(f"ERROR: Nothing to do for file {test.relative_to(ROOT)}")
            error_count += 1
    # TODO: Also check for unused files?
    if error_count > 0:
        test_or_tests = "test" if error_count == 1 else "tests"
        print()
        print(f"{error_count} {test_or_tests} failed")
        sys.exit(1)
    print()
    print("All tests passed!")


if __name__ == "__main__":
    main()
