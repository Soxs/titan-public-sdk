# Titan Public SDK

Public C++, Java, and JavaScript/TypeScript SDK for TitanClient plugins.

This repository is auto-mirrored from the canonical TitanClient source tree.
Direct commits to `main` may be overwritten by the next outbound sync.

## Contents

| Path | Purpose |
| --- | --- |
| `CMakeLists.txt` | Native `titan_sdk` interface target and plugin/development helpers. |
| `cmake/` | Native DLL build, immutable staging, run, reload, and watch helpers. |
| `titan/` | Public C++ headers, Native ABI v1 contracts, and [API documentation](titan/PUBLIC_API.md). Include `<titan/plugin.h>` to start. |
| `plugin_sdk.h` | Deprecated low-level compatibility shim; new plugins use `<titan/plugin.h>`. |
| `examples/native-plugin/` | Complete C++ plugin starter with CMake development targets. |
| `java/` | Public Java plugin API source, Gradle build, sources JAR, and Javadoc JAR. |
| `maven/releases/` | Generated Maven repository for `net.titan:titan-plugin-api` and the `net.titan.dev` Gradle plugin. |
| `titan-plugin-sdk.d.ts` | TypeScript declarations for QuickJS plugins. |

Native plugin contracts are included; private controller/client IPC and loader
handoff headers are not needed to build plugins and are not published here.

## Native C++ Plugins

Use Windows x64, Visual Studio 2022 or newer with the MSVC C++ tools and Windows
SDK, CMake 3.24+, and Git 2.27+. The supported profile is C++20 with the static MSVC runtime
(`/MT`, or `/MTd` in Debug), selected by the SDK helpers.

Start with **[titan-plugin-template](https://github.com/Soxs/titan-plugin-template)**
and click **Use this template** to create your plugin repository. Clone your
new repository and run:

```powershell
cmake -S . -B build -A x64
cmake --build build --config Debug --target titan_run_my_plugin
# After editing, reload in the same game process:
cmake --build build --config Debug --target titan_reload_my_plugin
# Or watch and rebuild/reload on saves; Ctrl+C stops watching:
cmake --build build --config Debug --target titan_watch_my_plugin
```

The template fetches a pinned native SDK on its first configure and caches it
under `build/_deps`. Rename `my_plugin` in CMake and the source metadata to your
plugin ID. Use `-DTITAN_PLUGIN_SDK_ROOT=C:/path/to/titan-public-sdk` for local
SDK development or an offline checkout.

The bundled `examples/native-plugin/` is the same starter and automatically
uses this SDK checkout. Configure it with
`cmake -S examples/native-plugin -B build/native-plugin -A x64`.
See the [starter README](examples/native-plugin/README.md) for lifecycle,
debugger, runtime-asset, and IDE details.

Run discovers your normal Titan installation through the launcher's state at
`%USERPROFILE%/.titanclient/repository/state.json`. Keep Titan updated normally;
no separate runtime download is part of setup. Your account needs DEV
entitlement. `-DTITAN_CLIENT_ROOT=...` is an optional override for a compatible
source build or prerelease. Configure/build needs no installed runtime.
TitanClient 0.1.14 predates Native ABI v1 and the new native reload capabilities;
those client changes must ship through the regular updater. Finding an older
installation does not make it compatible.

Run starts or reuses the plugin's DEV tab. Each changed successful build stages
an immutable DLL/PDB generation; reload replaces the plugin while keeping the
game open. Watch retries after the next save when a build or reload fails.
Plugin state is recreated, and the host restores persisted settings. Workers
must stop/join during unload and service leases must be short-lived; see
[native ownership](titan/PUBLIC_API.md#native-ownership-and-service-lifetime).

Native ABI v1 is independent of the SDK source release number. Updating headers
does not by itself invalidate an older DLL: the bootstrap ABI and the plugin's
required capability IDs, major versions, and table sizes determine compatibility.
Missing optional capabilities remain unavailable. Legacy `TitanCreatePlugin`
DLLs need a one-time rebuild to use `TitanPlugin_QueryNative`. Pin an SDK commit
for reproducible builds; see [version compatibility](titan/PUBLIC_API.md#version-compatibility).

## Java Plugins

Use the API as a `compileOnly` dependency. TitanClient's embedded Java runtime
supplies the API and Guice classes at runtime.

```gradle
repositories {
    maven {
        url = uri('https://raw.githubusercontent.com/Soxs/titan-public-sdk/main/maven/releases')
    }
    mavenCentral()
}

dependencies {
    compileOnly 'net.titan:titan-plugin-api:latest.release'
}
```

Use `latest.release` while developing to pick up the newest published SDK.
For reproducible plugin releases, pin an exact SDK version such as `0.1.3`.

For a runnable project to copy or fork, start from
[`Soxs/titan-java-sample-plugin`](https://github.com/Soxs/titan-java-sample-plugin).

## Hot Reload

The `net.titan.dev` Gradle plugin wires up the TitanClient dev loop: it stages
your built JAR into a versioned dev session, writes the `session.json` manifest
the controller reads, and launches (or recycles) a dedicated DEV tab. After the
first launch, a plain `build` re-stages the next generation so the in-tab
refresh button hot-reloads it — no client restart.

Add it alongside the `java`/`java-library` plugin. It resolves from the same
published repo as the API, declared in `settings.gradle`:

```gradle
// settings.gradle
pluginManagement {
    repositories {
        maven {
            url = uri('https://raw.githubusercontent.com/Soxs/titan-public-sdk/main/maven/releases')
        }
        gradlePluginPortal()
    }
}
```

```gradle
// build.gradle
plugins {
    id 'java-library'
    id 'net.titan.dev' version 'latest.release'
}

dependencies {
    compileOnly 'net.titan:titan-plugin-api:latest.release'
}
```

Point the plugin at your TitanLauncher sync repository via `gradle.properties`.
The Gradle dev plugin resolves `state.json` to the current `releases/<version>` folder containing `controller.exe`:

```properties
titanClientRoot=%USERPROFILE%/.titanclient/repository
```

or the `titanDev` block (all values optional; they fall back to
`gradle.properties` / environment variables):

```gradle
titanDev {
    clientRoot = '%USERPROFILE%/.titanclient/repository'
    sessionSlug = 'my-plugin'   // defaults to the project name
    javaDebugPort = 5005        // used by runViaTitanDebug
}
```

The dev loop:

```powershell
# 1. Launch (or recycle) the DEV tab for this plugin.
.\gradlew.bat runViaTitan

# 2. Edit your plugin code, then rebuild. The post-build hook re-stages the
#    next generation into the active dev session.
.\gradlew.bat build

# 3. Click the circular refresh icon next to the plugin in the controller's
#    side panel. It shows "Reloading..." and swaps in the new code, keeping
#    your enabled/config state. Reload works at the login screen too.
```

`runViaTitanDebug` is the same as `runViaTitan` but enables a JDWP agent
(default port 5005) so you can attach a debugger to the embedded runtime.

If `titanClientRoot` is left blank the plugin also probes the launcher
repository, common install folders, and local source-build output
(`build/controller/<Config>`). A
failed or empty build that produces no JAR is detected and the currently
loaded plugins are kept — the reload just reports the failure rather than
unloading everything.

## Java SDK Development

SDK contributors can work directly inside `java/`:

```powershell
cd java
.\gradlew.bat clean build
.\gradlew.bat publishToMavenLocal
```

To test an API-compatible SDK change against TitanClient, edit
`java/gradle.properties` and set `titanClientRoot` to the TitanLauncher sync repository:

```properties
titanClientRoot=%USERPROFILE%/.titanclient/repository
```

The resolved synced runtime must include the embedded Java runtime at
`java/titan-java-embedded.jar`.

Then run:

```powershell
.\gradlew.bat runTitanClient
```

`runTitanClient` will also try the launcher repository and common install
folders if `titanClientRoot` is left blank.

You can also use a one-off Gradle property:

```powershell
.\gradlew.bat runTitanClient "-PtitanClientRoot=%USERPROFILE%/.titanclient/repository"
```

Changes that require new embedded runtime behavior or native bridge methods
still need a TitanClient maintainer to update the private runtime.

## JavaScript And TypeScript Plugins

Use `titan-plugin-sdk.d.ts` for IntelliSense and type checking:

```js
/// <reference path="./titan-plugin-sdk.d.ts" />
```

JavaScript plugins still run in TitanClient's QuickJS runtime; their public
SDK surface here is the type declarations. SDK 106 item cache
definitions and SDK 107 runtime item compositions expose `subOps` as a fixed
five-by-twenty nested string array with empty strings for unused slots.
Inventory-item `interact(action)` resolves ordinary actions first and then live
submenu labels through the existing cross-runtime HostApi path.
