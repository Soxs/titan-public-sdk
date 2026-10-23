# Plugin-local authorization for JavaScript and TypeScript

Compile the plugin as usual, then prepend the independently verified authorization SDK:

```text
python build.py --plugin my-plugin.js --output my-plugin.authorized.js
```

Publish the resulting artifact, or pass it to the usual Titan bytecode compiler.
The final artifact's hash must match its approved marketplace build. Do not inject
the prelude into the shared host runtime: each plugin must carry its own verifier
and pinned public keys. The existing Titan source wrapper remains unchanged.

`example.ts` shows ordinary Start/Stop/status panel controls. Bind once with
`titan.instance(this, bot)` and call `run.start()` from the user's Start action.
The Promise reports admission; protected startup runs only after local verification.
`bot.tick()` is dispatched while its permit is current. Disable/unload stops it.
`run.panel()` supplies a standard Start/Stop/status panel; embed controls in an
existing panel with `run.controls(panel)` and `run.control(actionId)`.

`titan.entitlements(this)` provides `check()`, `check("plugin.dependency_slug")`,
`list()`, `snapshot()`, and `onChanged(listener)`. This reads access; it does not
reserve an instance. Missing or unverifiable responses mean `unknown`.

The vendored verifier uses node-forge **1.4.0**, with no Node, browser, host crypto,
or network dependency at runtime. `build.py` validates `SHA256SUMS.json` before
bundling the selected upstream modules. Their upstream LICENSE is retained under
`vendor/forge-1.4.0`. `keys.json` contains public trust anchors only; update them
through the normal reviewed SDK release process before server key rotation.
Hashes use canonical LF text, so Git CRLF checkouts produce the same bundle.

Tests: `node test.cjs`. The tests use public server-generated receipts and ephemeral
test signing keys. Build deterministically with
`python build.py --output titan-authorization.js`.
