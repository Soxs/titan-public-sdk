#!/usr/bin/env python3
"""Compare checked-in XP ports to the exact original RuneLite class, offline.

This is a developer check, never a build-time generator. Only javac/java run,
with output confined to --output; there is no Gradle, native build, or network.
"""
from __future__ import annotations
import argparse
import hashlib
from pathlib import Path
import shutil
import subprocess

API_SHA256 = "2c3899c109fe3ceea33baa96f277914ef869e387981a9c2d49c46cc78bea8be4"
ROOT = Path(__file__).resolve().parents[1]


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--api-jar", type=Path, required=True, help="Original runelite-api 1.13.6 JAR")
    parser.add_argument("--java-home", type=Path, help="JDK 11+ containing bin/java and bin/javac")
    parser.add_argument("--output", type=Path, default=ROOT / "build-runelite-integration" / "experience-audit")
    args = parser.parse_args()
    sdk = ROOT if (ROOT / "titan/experience.h").is_file() else ROOT / "shared"
    jar = args.api_jar.resolve(strict=True)
    if hashlib.sha256(jar.read_bytes()).hexdigest() != API_SHA256:
        parser.error("API JAR does not match the pinned RuneLite 1.13.6 SHA-256")
    suffix = ".exe" if __import__("os").name == "nt" else ""
    def tool(name: str) -> str:
        found = str(args.java_home / "bin" / (name + suffix)) if args.java_home else shutil.which(name)
        if not found or not Path(found).is_file():
            parser.error("A JDK 11+ is required; use --java-home")
        return found
    output = args.output.resolve()
    output.mkdir(parents=True, exist_ok=True)
    subprocess.run([tool("javac"), "--release", "11", "-encoding", "UTF-8", "-d", str(output),
                    str(sdk / "java/src/main/java/net/titan/api/Experience.java"),
                    str(ROOT / "scripts/ExperienceParity.java")], check=True)
    subprocess.run([tool("java"), "-cp", str(output), "ExperienceParity", str(jar),
                    str(sdk / "titan/experience.h")], check=True)


if __name__ == "__main__":
    main()
