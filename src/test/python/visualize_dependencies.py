#!/usr/bin/env python3

"""
This script generates a .dot file showing the dependencies between Scala packages.

This is only a crude approximation of the actual project dependencies using grep, not
necessarily a perfect representation of the real dependencies.
"""

import os
import subprocess
from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent.parent.parent
SCALA_DIR = ROOT.joinpath("src", "main", "scala")
SKIP_PACKAGES = {
    "mhir",
    "mhir.gen",
    "mhir.main",
    "mhir.main.aetherling",
    "mhir.main.bf",
    "mhir.main.repl",
    "mhir.main.shared",
    "mhir.main.sirop",
    "mhir.main.stored",
}


def list_packages() -> list[str]:
    """
    List all Scala packages (mhir.ir, mhir.gen.vhdl, etc.).
    """
    packages = []
    for (root, dirs, _) in os.walk(SCALA_DIR):
        root = Path(root)
        for d in dirs:
            d = root.joinpath(d)
            name = d.relative_to(SCALA_DIR).as_posix().replace("/", ".")
            if name in SKIP_PACKAGES:
                continue
            packages.append(name)
    return packages


def dependency_exists(src: str, target: str, target_children: list[str]) -> bool:
    """
    Return `True` if package `src` depends on `target` and `False` otherwise.
    """
    if target_children:
        for c in target_children:
            assert c.startswith(target + ".")
        target_children = [c[len(target + "."):] for c in target_children]
        lookahead = "|".join(target_children)
        lookahead = r"(?!\.(" + lookahead + "))"
    else:
        lookahead = ""
    pattern = target.replace(".", r"\.") + lookahead
    dir_to_search = SCALA_DIR.joinpath(src.replace(".", "/"))
    files_to_search = list(dir_to_search.glob("*.scala"))
    if not files_to_search:
        return False
    args = [
        "grep", "-P",
        "-e", pattern,
    ] + [p.as_posix() for p in files_to_search]
    result = subprocess.run(
        args,
        check=False,
        stdout=subprocess.DEVNULL,
    )
    return result.returncode == 0


def list_dependencies(packages: list[str]) -> list[tuple[str, str]]:
    """
    Return the list of all dependencies on the Scala project.
    """
    edges = []
    for p1 in packages:
        for p2 in packages:
            if p1 == p2:
                # No need to show self-loops; every package will have them
                continue
            p2_children = [p for p in packages if p.startswith(p2 + ".")]
            if dependency_exists(p1, p2, p2_children):
                edges.append( (p1, p2) )
    return edges


def main() -> None:
    """
    Script entry point.
    """
    print("digraph {")
    nodes = list_packages()
    for p in nodes:
        print(f'    "{p}";')
    edges = list_dependencies(nodes)
    for (u, v) in edges:
        print(f'    "{u}" -> "{v}";')
    print("}")


if __name__ == "__main__":
    main()
