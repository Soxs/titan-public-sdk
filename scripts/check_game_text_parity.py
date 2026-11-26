#!/usr/bin/env python3
"""Offline original-class check of game-text golden cases; no shared build/network."""
from __future__ import annotations
import argparse
import hashlib
from pathlib import Path
import shutil
import subprocess
import os

ROOT = Path(__file__).resolve().parents[1]
CLIENT_SHA256 = "a1384091c034f14b74a1ac7a22261b03ae9915f95b75f21071236f27c55fb3fe"


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--client-jar", type=Path, required=True, help="Original client 1.13.6 JAR")
    parser.add_argument("--dependencies", type=Path, required=True, help="Local pinned dependency directory")
    parser.add_argument("--java-home", type=Path)
    parser.add_argument("--output", type=Path, default=ROOT / "build-runelite-integration/game-text-audit")
    args = parser.parse_args()
    sdk = ROOT if (ROOT / "titan/text.h").is_file() else ROOT / "shared"
    client = args.client_jar.resolve(strict=True)
    if hashlib.sha256(client.read_bytes()).hexdigest() != CLIENT_SHA256:
        parser.error("Client JAR does not match the pinned RuneLite 1.13.6 SHA-256")
    dependencies = [args.dependencies / name for name in (
        "guava-23.2-jre.jar", "commons-text-1.2.jar", "commons-lang3-3.7.jar")]
    for dependency in dependencies:
        if not dependency.is_file(): parser.error("Missing local dependency: " + dependency.name)
    suffix = ".exe" if os.name == "nt" else ""
    def tool(name: str) -> str:
        found = str(args.java_home / "bin" / (name + suffix)) if args.java_home else shutil.which(name)
        if not found or not Path(found).is_file(): parser.error("JDK 11+ required; use --java-home")
        return found
    output = args.output.resolve()
    output.mkdir(parents=True, exist_ok=True)
    subprocess.run([tool("javac"), "--release", "11", "-encoding", "UTF-8", "-d", str(output),
                    str(sdk / "java/src/main/java/net/titan/api/Text.java"),
                    str(ROOT / "scripts/GameTextParity.java")], check=True)
    fixture = sdk / "java/src/test/resources/net/titan/api/game_text_golden.tsv"
    subprocess.run([tool("java"), "-cp", str(output), "GameTextParity", str(fixture),
                    str(client), *map(str, dependencies)], check=True)


if __name__ == "__main__":
    main()
