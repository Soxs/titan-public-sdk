"""Compare the compiled spell catalogue with OSRS Wiki infoboxes or a saved audit.

Usage: python tests/validate_spell_wiki.py <magic_catalog_dump.exe> [--refresh <audit.json>]
Offline: python tests/validate_spell_wiki.py <magic_catalog_dump.exe> --audit <audit.json>
Refresh writes independently fetched evidence, never SDK source or expected values copied from it.
"""

import argparse
from concurrent.futures import ThreadPoolExecutor
import html
import json
from pathlib import Path
import re
import subprocess
import urllib.parse
import urllib.request


ALIASES = {
    "HOME_TELEPORT": "Lumbridge Home Teleport",
    "TELEPORT_TO_KOUREND": "Kourend Castle Teleport",
    "TELEPORT_TO_APE_ATOLL": "Ape Atoll Teleport (standard)",
    "APE_ATOLL_TELEPORT": "Ape Atoll Teleport (Arceuus)",
    "FENKENSTRAINS_CASTLE_TELEPORT": "Fenkenstrain's Castle Teleport",
    "TELEPORT_TO_BOUNTY_TARGET": "Teleport to Target",
    "BOUNTY_TARGET_TELEPORT": "Teleport to Target",
    "NPC_CONTACT": "Astral Contact",
    "HUNTER_KIT": "Hunter Kit (spell)",
}
RUNES = "AIR WATER EARTH FIRE MIND BODY COSMIC CHAOS NATURE LAW DEATH ASTRAL BLOOD SOUL WRATH".split()


def title(name):
    if name in ALIASES:
        return ALIASES[name]
    if name.startswith("LVL_"):
        return name.replace("LVL_", "Lvl-").replace("_ENCHANT", " Enchant")
    if name.startswith("ENCHANT_BOLTS_"):
        return "Enchant Crossbow Bolt (" + name.removeprefix("ENCHANT_BOLTS_").replace("_", " ").title() + ")"
    return " ".join(word.lower() if word in ("TO", "OF", "ILLA") else word.title() for word in name.split("_"))


def plain(fragment):
    fragment = re.sub(r'<img\b[^>]*alt="([^"]*)"[^>]*>', r" \1 ", fragment)
    return " ".join(html.unescape(re.sub(r"<[^>]+>", " ", fragment)).split())


def fetch_spell(page):
    url = "https://oldschool.runescape.wiki/w/" + urllib.parse.quote(page.replace(" ", "_"))
    request = urllib.request.Request(url, headers={"User-Agent": "TitanSDK-spell-audit/1.0 (manual development validation)"})
    with urllib.request.urlopen(request, timeout=60) as response:
        source = response.read().decode("utf-8")
    rows = [plain(row) for row in re.findall(r"<tr\b[^>]*>(.*?)</tr>", source, re.S)]
    member = next((row for row in rows if re.fullmatch(r"Members (Yes|No)", row)), None)
    level = next((re.fullmatch(r"Level (?:Magic )?(\d+)", row) for row in rows
                  if re.fullmatch(r"Level (?:Magic )?(\d+)", row)), None)
    rune_row = next((row for row in rows if row.startswith("Runes ")), None)
    revision = re.search(r'"wgRevisionId":(\d+)', source)
    if not (member and level and rune_row and revision):
        raise ValueError(f"Unrecognized Wiki infobox: {page}: {rows[:20]}")
    costs = {element.upper(): int(count) for count, element in
             re.findall(r"(\d+) (" + "|".join(word.title() for word in RUNES) + r")\b", rune_row)}
    if not costs and "None" not in rune_row:
        raise ValueError(f"Unrecognized rune cost: {page}: {rune_row}")
    return {"page": page, "revision": int(revision[1]), "url": url,
            "level": int(level[1]), "members": member == "Members Yes", "runes": costs}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("catalogue")
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument("--refresh", type=Path)
    group.add_argument("--audit", type=Path)
    args = parser.parse_args()
    catalogue = []
    for line in subprocess.check_output([args.catalogue], text=True).splitlines():
        book, name, level, members, costs = line.split("\t")
        catalogue.append({"book": book, "name": name, "page": title(name), "level": int(level),
                          "members": members == "1", "runes": {k: int(v) for k, v in
                          (cost.split(":") for cost in costs.split(",") if cost)}})
    if args.refresh:
        # Duplicate spellbook entries share one Wiki page and one request.
        args.refresh.parent.mkdir(parents=True, exist_ok=True)
        pages = sorted({entry["page"] for entry in catalogue})
        with ThreadPoolExecutor(max_workers=3) as pool:
            evidence = list(pool.map(fetch_spell, pages))
        args.refresh.write_text(json.dumps(evidence, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    else:
        evidence = json.loads(args.audit.read_text(encoding="utf-8"))
    by_page = {entry["page"]: entry for entry in evidence}
    failures = []
    for entry in catalogue:
        expected = by_page.get(entry["page"])
        if expected is None:
            failures.append(f"Missing Wiki evidence: {entry['name']}")
            continue
        for field in ("level", "members", "runes"):
            if entry[field] != expected[field]:
                failures.append(f"{entry['book']}/{entry['name']} {field}: SDK={entry[field]}, Wiki={expected[field]}")
    if failures:
        raise SystemExit("\n".join(failures))
    print(f"Validated {len(catalogue)} spell entries against {len(by_page)} Wiki infoboxes: levels, membership, rune costs.")


if __name__ == "__main__":
    main()
