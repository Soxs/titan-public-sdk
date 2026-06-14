# Titan Java Plugin API

Public Java SDK for TitanClient plugins.

For a runnable standalone starter that plugin authors can copy or fork, see
[`Soxs/titan-java-sample-plugin`](https://github.com/Soxs/titan-java-sample-plugin).

## Consume The SDK

Published releases use Maven coordinates:

```gradle
repositories {
    maven { url = uri('https://raw.githubusercontent.com/Soxs/titan-public-sdk/main/maven/releases') }
    mavenCentral()
}

dependencies {
    compileOnly 'net.titan:titan-plugin-api:latest.release'
}
```

Use `compileOnly`: TitanClient's embedded Java runtime supplies the API classes at
runtime. Plugin JARs should contain plugin classes annotated with
`@PluginDescriptor`, not their own copy of the SDK or Guice.

Use `latest.release` while developing to pick up the newest published SDK.
For reproducible plugin releases, pin an exact SDK version such as `0.1.2`.

The SDK exposes Guice as a compile dependency so plugin authors can use:

```java
import com.google.inject.Inject;
```

TitanClient's embedded runtime provides the Guice runtime.

## Package Layout

- `net.titan.api`: game-facing services and types such as `Client`, `Player`,
  and `Logger`.
- `net.titan.api.plugins`: `Plugin` and `PluginDescriptor`.
- `net.titan.api.config`: config interfaces, annotations, sections, and
  setting metadata.
- `net.titan.api.events`: event classes such as `GameTick`.
- `net.titan.api.eventbus`: `EventBus` and `Subscribe`.

## Build And Publish

```powershell
.\gradlew.bat clean build
.\gradlew.bat publishToMavenLocal
```

Set `TITAN_MAVEN_REPOSITORY_URL`, `TITAN_MAVEN_USERNAME`, and
`TITAN_MAVEN_PASSWORD` to enable the configured remote Maven publication.
The canonical outbound sync publishes releases into
`Soxs/titan-public-sdk/maven/releases` automatically.

## Test Modified SDK Sources In TitanClient

Edit the included `gradle.properties` file and set `titanClientRoot` to the
folder that directly contains `controller.exe`:

```properties
titanClientRoot=C:/Program Files/TitanClient
```

That TitanClient folder must include the embedded Java runtime at:

```text
java/titan-java-embedded.jar
```

Then run:

```powershell
.\gradlew.bat runTitanClient
```

`runTitanClient` will also try common TitanClient install folders and local
source-build output folders if `titanClientRoot` is left blank.

The task builds this public API JAR, stages it under
`%USERPROFILE%\.titanclient\java-dev`, and launches:

```text
controller.exe --dev-mode --launch-new-client
```

TitanClient starts its embedded JVM with the staged API JAR first on the Java
classpath. This lets SDK contributors test API-compatible changes without
access to the private runtime source.

You can also provide the installed location as a one-off Gradle property:

```powershell
.\gradlew.bat runTitanClient "-PtitanClientRoot=C:\Program Files\TitanClient"
```

Or set it for the current PowerShell session:

```powershell
$env:TITAN_CLIENT_ROOT = "C:\Program Files\TitanClient"
.\gradlew.bat runTitanClient
```

For a local TitanClient source build, build `controller` first and use the
build output folder. Make sure Java runtime staging is enabled:

```powershell
cmake -S . -B build -DTITAN_BUILD_JAVA_RUNTIME=ON
cmake --build build --config Release --target controller
.\gradlew.bat runTitanClient "-PtitanClientRoot=C:\path\to\SoxClientOSRS\build\controller\Release"
```

Changes that require new embedded runtime behavior or native bridge methods
still need a TitanClient maintainer to update and distribute the runtime.
