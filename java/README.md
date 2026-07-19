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
For reproducible plugin releases, pin an exact SDK version such as `0.1.24`.

The SDK exposes Guice as a compile dependency so plugin authors can use:

```java
import com.google.inject.Inject;
```

TitanClient's embedded runtime provides the Guice runtime.

## Hot Reload For Plugin Authors

The `net.titan.dev` Gradle plugin (published into the same Maven repo as the
API) provides the TitanClient dev loop: it stages your built JAR into a
versioned dev session, writes the `session.json` manifest the controller reads,
and launches/recycles a dedicated DEV tab. After the first launch a plain
`build` re-stages the next generation so the in-tab refresh button hot-reloads
it without restarting the client.

Declare the plugin repo in `settings.gradle` and apply the plugin in
`build.gradle`:

```gradle
// settings.gradle
pluginManagement {
    repositories {
        maven { url = uri('https://raw.githubusercontent.com/Soxs/titan-public-sdk/main/maven/releases') }
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

Run TitanLauncher once so it syncs TitanClient into
`%USERPROFILE%\.titanclient\repository`. The Gradle dev plugin auto-detects
that synced release. Use `titanClientRoot` or the `titanDev` block only when
you want to target a custom install or local source build:

```gradle
titanDev {
    clientRoot = '%USERPROFILE%/.titanclient/repository'
    sessionSlug = 'my-plugin'   // defaults to the project name
    javaDebugPort = 5005        // used by runViaTitanDebug
}
```

Loop:

```powershell
.\gradlew.bat runViaTitan        # launch/recycle the DEV tab
.\gradlew.bat build              # rebuild; post-build hook re-stages the next gen
# click the refresh icon next to the plugin in the controller side panel
```

`runViaTitanDebug` is identical but enables a JDWP agent (default port 5005).
Reload works at the login screen as well as in-game, and an empty/failed build
that produces no JAR keeps the currently loaded plugins instead of unloading
them. This is the recommended path for plugin authors; the
`runTitanClient`/`publishToMavenLocal` flow below is for SDK contributors
editing the API itself.

## Package Layout

- `net.titan.api`: game-facing services, core types, and catalogs such as
  `Client`, `Player`, `Logger`, `Prayer`, `Varbits`, `InterfaceId`,
  `ChatMessageType`, `ScriptId`, and `QuestId`.
- `net.titan.api.plugins`: `Plugin` and `PluginDescriptor`.
- `net.titan.api.config`: config interfaces, annotations, sections, and
  setting metadata.
- `net.titan.api.events`: event classes such as `GameTick`.
- `net.titan.api.eventbus`: `EventBus` and `Subscribe`.
- `net.titan.api.queries`: fluent NPC, player, object, item, projectile,
  graphics-object, inventory, and widget queries.
- `net.titan.api.utils`: composed helpers including `Bank`, `Inventory`,
  `Equipment`, `Combat`, `Walk`, `Dialogue`, `Login`, `Plugins`, `Script`,
  `Cache`, and `Magic`.
- `net.titan.api.overlay`: in-game overlay draw and panel helpers, including
  WorldView-aware projection/draw methods.

## Coordinated Breaks (0.1.24+)

`net.titan.api.BreakHandler` mirrors the native and JavaScript Break Handler
utility. Every call takes the exact `Plugin` object loaded by TitanClient.
Helper classes may retain that owner and forward it; the embedded runtime
validates object identity and the host stores only stable plugin id/load
generation.

```java
import com.google.inject.Inject;
import net.titan.api.BreakCommand;
import net.titan.api.BreakHandler;
import net.titan.api.Client;
import net.titan.api.eventbus.Subscribe;
import net.titan.api.events.ClientTick;
import net.titan.api.plugins.Plugin;

public final class MyPlugin implements Plugin {
    @Inject
    private Client client;

    @Override
    public void onEnable() {
        BreakHandler.register(this); // true: configurable schedule owner
        BreakHandler.start(this);
        BreakHandler.poll(this);
        BreakHandler.running(this);
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        BreakCommand command = BreakHandler.poll(this);
        if (command.shouldBreak()) {
            stopIssuingNewWork();
            if (hasReachedSafeBoundary()) {
                BreakHandler.paused(this);
            }
        } else if (command.shouldResume() && clientIsWorldReady()) {
            resumeWork();
            BreakHandler.running(this);
        }
    }

    @Override
    public void onDisable() {
        BreakHandler.stop(this);
        BreakHandler.unregister(this);
    }

    private boolean clientIsWorldReady() {
        return client.loggedIn()
            && client.localPlayer().isPresent()
            && client.currentWorldViewPtr().isPresent()
            && client.sceneSizeX() > 0
            && client.sceneSizeY() > 0;
    }

    private void stopIssuingNewWork() {
        // Cancel/quiet this plugin's workers without starting another action.
    }

    private boolean hasReachedSafeBoundary() {
        // Replace with the plugin-specific worker/player-idle checks.
        return false;
    }

    private void resumeWork() {
        // Restart this plugin's workers after the world-ready check.
    }
}
```

Pass `false` to `register(plugin, false)` for a participant-only plugin:
it joins global pause/resume quorums but does not own a schedule. `stop` keeps
the registration visible; `unregister` implies stop and removes it.
`poll`, `shouldBreak`, `isBreakActive`, and `shouldResume` record the epoch
used by subsequent `paused`, `defer`, `error`, and `running` reports. Stale
reports fail closed. Cleanup is idempotent and the host also unregisters on
disable, fault, reload, or unload.

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

Run TitanLauncher once so it syncs a release into
`%USERPROFILE%\.titanclient\repository`, then run:

```powershell
.\gradlew.bat runTitanClient
```

The auto-detected TitanClient folder must include the embedded Java runtime at:

```text
java/titan-java-embedded.jar
```

You can still edit the included `gradle.properties` file and set
`titanClientRoot` to the folder that directly contains `controller.exe` when
testing a custom/source build:

```properties
titanClientRoot=%USERPROFILE%/.titanclient/repository
```

`runTitanClient` will also try the launcher repository, common TitanClient
install folders, and local source-build output folders if `titanClientRoot` is
left blank.

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
.\gradlew.bat runTitanClient "-PtitanClientRoot=%USERPROFILE%/.titanclient/repository"
```

Or set it for the current PowerShell session:

```powershell
$env:TITAN_CLIENT_ROOT = "%USERPROFILE%/.titanclient/repository"
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
