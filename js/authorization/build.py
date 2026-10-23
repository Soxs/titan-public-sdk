"""Build the deterministic plugin-local authorization prelude; no network access."""
import argparse
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parent


def prelude():
    vendor = ROOT / "vendor" / "forge-1.4.0"
    hashes = json.loads((vendor / "SHA256SUMS.json").read_text(encoding="utf-8"))
    modules = []
    for name, expected in sorted(hashes.items()):
        # Canonical upstream text is LF; Windows Git checkouts may use CRLF.
        data = (vendor / name).read_bytes().replace(b"\r\n", b"\n")
        if hashlib.sha256(data).hexdigest() != expected:
            raise ValueError("Pinned forge source hash mismatch: " + name)
        if name.endswith(".js"):
            modules.append(json.dumps("./" + name[:-3]) + ": function(require,module,exports){\n" + data.decode("utf-8") + "\n}")
    keys = (ROOT / "keys.json").read_text(encoding="utf-8")
    runtime = (ROOT / "runtime.js").read_text(encoding="utf-8")
    return ("// Titan plugin-local authorization. Includes node-forge 1.4.0 (BSD-3-Clause/GPL-2.0).\n"
            "titan = (function(host) {\n'use strict';\n"
            "const self = {}, window = undefined, process = undefined;\n"
            "const factories = {" + ",\n".join(modules) + "};\n"
            "const cache = Object.create(null);\n"
            "function require(name) { if(cache[name])return cache[name].exports; "
            "if(!factories[name])throw new Error('Unsupported verifier module '+name); "
            "const module={exports:{}};cache[name]=module;factories[name](require,module,module.exports);return module.exports;}\n"
            "require('./rsa'); require('./sha256'); const forge=require('./forge');\n"
            + runtime + "\nreturn installAuthorization(host,forge,Object.freeze(" + keys + "));\n})(titan);\n")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plugin", type=Path, help="Compiled plugin JavaScript to append")
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    source = prelude()
    if args.plugin:
        if args.output.resolve() == args.plugin.resolve():
            parser.error("Output must differ from input")
        source += "\n" + args.plugin.read_text(encoding="utf-8")
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(source, encoding="utf-8", newline="\n")
