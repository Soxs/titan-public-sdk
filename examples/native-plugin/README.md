# Titan native plugin starter

A standalone C++20/MSVC x64 project using the public Titan SDK. Rename
`my_plugin` in CMake and the source metadata to your plugin ID.

## Setup

Use Windows x64, MSVC 2022 or newer with the Windows SDK, and CMake 3.24+.
The SDK helpers select C++20 and the static MSVC runtime (`/MT`, `/MTd` in Debug).
If using the bundled `titan-public-sdk/examples/native-plugin` example, its SDK
path is discovered automatically; from that example directory run:

```powershell
cmake -S . -B build -A x64
cmake --build build --config Debug --target my_plugin
```

For a standalone copy of this project, clone the public SDK and select its path:

```powershell
git clone https://github.com/Soxs/titan-public-sdk.git C:/sdks/titan-public-sdk
cmake -S . -B build -A x64 -DTITAN_PLUGIN_SDK_ROOT="C:/sdks/titan-public-sdk"
cmake --build build --config Debug --target my_plugin
```

You can also set `TITAN_PLUGIN_SDK_ROOT` in your environment. For repeatable
builds, use a chosen SDK revision or submodule. An SDK source update does not
invalidate old DLLs solely because its release number changes: native ABI v1
and required capabilities determine compatibility.

Run uses your normal Titan installation, discovered through
`%USERPROFILE%/.titanclient/repository/state.json`. Keep it updated through
TitanLauncher; no separate runtime download is part of setup. A runtime is
needed for Run, not for configure/build. DEV mode requires your account's
`feature.debug_mode` entitlement. TitanClient 0.1.14 predates the new native
capabilities, which must ship through the regular updater before that client
can load/reload this plugin. Maintainers can optionally configure
`-DTITAN_CLIENT_ROOT="C:/path/to/runtime"` to select a compatible source build
or prerelease containing `controller.exe`.

## Build, run, reload

```powershell
cmake --build build --config Debug --target titan_run_my_plugin
# Edit code, then replace this plugin in the same game process:
cmake --build build --config Debug --target titan_reload_my_plugin
# Continuous build/reload after saves (start Run once first):
cmake --build build --config Debug --target titan_watch_my_plugin
```

Rerunning `titan_run_my_plugin` reuses the same live development tab. Reload
fails with a diagnostic if there is no matching tab or the plugin is busy.
Watch reports errors and retries on the next save; Ctrl+C stops watching.
`titan_stage_my_plugin` builds/stages without contacting a client.

The targets work in CLion, Visual Studio, and VS Code CMake Tools. Select them
in the IDE's target selector; configuring does not rewrite editor settings.

Each changed successful build snapshots the DLL and its PDB into
`build/.titan/dev/my_plugin/Debug/load/gen-N/`, then atomically updates that
configuration's `session.json`. Rebuilds need no CMake reconfigure. Debug and
Release have independent generations. After a successful stage, old snapshots
are cleaned automatically. The current snapshot stays; any locked old
generation is skipped as a whole and retried on a later build/stage. Failed
builds do not clean up generations, and generation numbers are never reused.

CMake-known dependent DLLs are staged automatically. Declare other runtime
files or directories explicitly:

```cmake
titan_plugin_runtime_file(TARGET my_plugin
    SOURCE "${CMAKE_CURRENT_SOURCE_DIR}/data" DESTINATION "data")
```

For a DLL with multiple registered plugins, use `SLUGS first second` instead
of `SLUG`; the first ID names the targets, and reload replaces the entire
bundle with the same IDs.

Replacing a third-party dependency DLL can require a client restart because
Windows may reuse an already loaded dependency. The reload contract covers
the plugin module, not arbitrary dependencies or object-state migration.

## Debugging and lifecycle

Run once, find the `osclient.exe` PID in the controller log, and attach your
IDE debugger to it. The default `DebuggableLoadLibrary` mode keeps matching
PDBs beside loaded DLLs. The PID remains stable through reload. `MemoryBlob`
is a production-loader smoke-test option, not the supported debugger/reload
workflow.

Keep service leases short and use the SDK's owner-bound scheduling and worker
helpers. `onUnload()` should stop/join long-lived workers. Reload cannot safely
replace a module with active work or service calls; the client reports that
condition instead of freeing executing code. Object state is recreated;
persisted settings are restored by the host.

See the SDK's [public API](https://github.com/Soxs/titan-public-sdk/blob/main/titan/PUBLIC_API.md)
and [native quickstart](https://github.com/Soxs/titan-public-sdk#native-c-plugins)
for the full contracts and public SDK layout.
