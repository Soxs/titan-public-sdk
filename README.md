# Titan Public SDK

Public Java and JavaScript/TypeScript SDK surface for TitanClient plugins.

This repository is auto-mirrored from the canonical TitanClient source tree.
Direct commits to `main` may be overwritten by the next outbound sync.

## Contents

| Path | Purpose |
| --- | --- |
| `java/` | Public Java plugin API source, Gradle build, sources JAR, and Javadoc JAR. |
| `maven/releases/` | Generated Maven repository for `net.titan:titan-plugin-api`. |
| `titan-plugin-sdk.d.ts` | TypeScript declarations for QuickJS plugins. |

The native C++ SDK, ABI internals, loader handoff schemas, and controller/client
IPC headers are intentionally not published here.

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

## Java SDK Development

SDK contributors can work directly inside `java/`:

```powershell
cd java
.\gradlew.bat clean build
.\gradlew.bat publishToMavenLocal
```

To test an API-compatible SDK change against an installed TitanClient, edit
`java/gradle.properties` and set `titanClientRoot` to the folder that directly
contains `controller.exe`:

```properties
titanClientRoot=C:/Program Files/TitanClient
```

That TitanClient folder must include the embedded Java runtime at
`java/titan-java-embedded.jar`.

Then run:

```powershell
.\gradlew.bat runTitanClient
```

`runTitanClient` will also try common install folders if `titanClientRoot` is
left blank.

You can also use a one-off Gradle property:

```powershell
.\gradlew.bat runTitanClient "-PtitanClientRoot=C:\Program Files\TitanClient"
```

Changes that require new embedded runtime behavior or native bridge methods
still need a TitanClient maintainer to update the private runtime.

## JavaScript And TypeScript Plugins

Use `titan-plugin-sdk.d.ts` for IntelliSense and type checking:

```js
/// <reference path="./titan-plugin-sdk.d.ts" />
```

JavaScript plugins still run in TitanClient's QuickJS runtime; this repository
only publishes the type declarations and public Java SDK.
