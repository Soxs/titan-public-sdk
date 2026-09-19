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

## Main Loop Event (0.1.43+)

`MainLoop` fires from the client's outer loop in every state, including the
title/login screens. Static cache definitions are always available. Gate live
client, entity, widget, scene, and projection queries on world readiness:

```java
import com.google.inject.Inject;
import net.titan.api.Client;
import net.titan.api.eventbus.Subscribe;
import net.titan.api.events.MainLoop;
import net.titan.api.utils.Cache;
import net.titan.api.utils.Login;

public final class MyPlugin {
    @Inject private Client client;

    @Subscribe
    public void onMainLoop(MainLoop event) {
        Cache.item(4151); // safe in every state
        if (!Login.isWorldReady()) return;
        client.localPlayer().ifPresent(player -> use(player));
    }
}
```

## Grand Exchange Offers (0.1.60+, native SDK 136)

`Client.getGrandExchangeOffers()` returns immutable snapshots of the local
player's slots in slot order, including empty slots. `getGrandExchangeOffer(slot)`
returns an `Optional` for one zero-based slot. Use
`client.isGrandExchangeAvailable()` to distinguish unavailable native data from
an empty offer. These APIs are also available through `Titan.client()`.

```java
import net.titan.api.GrandExchangeOffer;
import net.titan.api.eventbus.Subscribe;
import net.titan.api.events.GrandExchangeOfferChanged;

@Subscribe
public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged event) {
    GrandExchangeOffer offer = event.getGrandExchangeOffer();
    int slot = event.getSlot();
    long price = offer.getPrice();
    long completedGold = offer.getSpent();
    // Retaining this offer is safe: later updates cannot change the snapshot.
}
```

States follow the RuneLite names: `EMPTY`, `BUYING`, `BOUGHT`, `SELLING`, `SOLD`,
`CANCELLED_BUY`, `CANCELLED_SELL`, and `UNKNOWN`. `getQuantitySold()` is the
completed quantity on both buy and sell offers; `getSpent()` is completed gold
on either side. Price and completed gold are Java `long`, preserving the native
client's 64-bit values. This API reads existing offers; it does not submit or
cancel trades.

On initial observation after attach/login, events first provide a synthetic
`EMPTY` baseline for every slot, followed by observed offers. This initialization
does not mean a trade was cancelled or collected. Later native updates produce
events even when the offer values are unchanged. Delivery occurs on the game
thread at the next safe main-loop phase; use the event's retained snapshot when
processing that update, since a fresh client query may already reflect a later
offer.

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

## Checkbox Matrix Config Items (0.1.59+)

A `boolean[][]`-returning `@ConfigItem` with `columns()` and `rows()` renders as
a checkbox grid instead of a run of separate booleans. Each `@MatrixRow` names
the columns it has, so a column a row omits renders as a blank gap and can never
be checked; `checked` seeds the initial state.

```java
@ConfigItem(keyName = "obstacles", name = "Obstacles", description = "...",
    columns = {"Bridge", "Grapple", "Brazier", "Portal"},
    rows = {
        @MatrixRow(label = "Floor 1", cells = {"Bridge", "Grapple"}),
        @MatrixRow(label = "Floor 2", cells = {"Grapple", "Brazier"}),
        @MatrixRow(label = "Floor 3", cells = {"Bridge", "Portal"},
                   checked = {"Portal"}),
    })
boolean[][] obstacles();
```

Unlike every other config item the method is **abstract**: its cells and their
initial state come from the annotation, so there is no body to read a default
from. Every name in `cells` / `checked` is resolved against `columns` at scan
time, so a misspelling fails the load rather than moving a checkbox one cell
over. The value is a cell bitmask (`bit = row * columns().length + column`),
capped at 31 cells. `config.obstacles()` returns a fresh row-major snapshot on
every call -- hoist it out of per-tick loops.

## Item Sub-operations (0.1.32+)

`ItemDefinition.subOps()` exposes raw cache opcode-43 submenu labels as a
deeply unmodifiable five-by-twenty list. Empty strings preserve the original
parent/submenu slot indexes.

Java 0.1.33 adds the same deeply unmodifiable shape to
`ItemComposition.subOps()`. Runtime-resolved compositions use the live ItemDef
vectors; cache fallback uses the raw opcode-43 matrix. Existing
`InventoryItem.interact(action)` calls transparently resolve ordinary actions
first and then these live submenu labels.

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
