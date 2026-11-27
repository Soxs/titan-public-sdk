# A Titan plugin with the RuneLite API

Start one with **Use this template** on
[titan-runelite-plugin-template](https://github.com/Soxs/titan-runelite-plugin-template), or copy this folder.

A complete project. Nothing else needs to be set up: `build.gradle` is one line, and Titan's artifact brings
RuneLite's API, RuneLite's plugin framework, Titan's SDK and Lombok.

You need Java 11 or newer, and Titan: run TitanLauncher once and sign in with a developer account (Free, Semi or
Premium Developer). Titan loads a RuneLite plugin from your own computer for those accounts only.

## Run it

| | Windows | macOS or Linux |
|---|---|---|
| Open Titan with the plugin | `.\gradlew run` | `./gradlew run` |
| Hot reload it after a change | `.\gradlew reload` | `./gradlew reload` |
| Hot reload it on every save | `.\gradlew reload -t` | `./gradlew reload -t` |
| Debug it, on port 5005 | `.\gradlew runDebug` | `./gradlew runDebug` |
| Only build it | `.\gradlew build` | `./gradlew build` |

In IntelliJ IDEA open this folder and choose **Run in Titan** or **Reload in Titan**. In VS Code they are tasks.

`run` opens a Titan tab of its own for this plugin. Enable the plugin there once. Afterwards `run` and `reload`
replace it in that tab, with its settings kept, and the game stays open. A build which does not compile or does
not load leaves the tab open to try again.

The JAR to share is `build/libs/example-plugin-1.0.0.jar`.

## Make it yours

1. Rename the `com.example` package and the three `Example` classes.
2. Change `@PluginDescriptor` in the plugin and `@ConfigGroup` in its config.
3. Change `rootProject.name` in `settings.gradle`, `group` and `version` in `build.gradle`, and `author` in
   `gradle.properties`.

## Two SDKs in one plugin

It is a RuneLite plugin: `Plugin`, `@Subscribe`, `OverlayManager`, `ConfigManager`, `ClientThread` and the rest work
as RuneLite's guides describe. Titan's SDK is beside it:

```java
@Inject private net.runelite.api.Client client;   // RuneLite
@Inject private net.titan.api.Client titan;       // Titan
```

`net.titan.api.Titan` has the static entry points, such as `Titan.webWalk()`. RuneLite objects also have Titan's
`npc.interact("Talk-to")`, `player.interact(slot)` and `widget.interact("Activate")`.

Use RuneLite's events, overlays, panels, configuration and `ClientThread`. Titan's own event bus, overlays, panels,
settings and managed instances belong to Titan-style plugins, which apply `net.titan.dev` instead.

## What Titan supplies

Titan runs RuneLite plugins on the native game client, and some calls are not available there yet:
[the chart](https://github.com/Soxs/titan-public-sdk#runelite-support) shows how much. Every build
prints those this plugin makes, and `.\gradlew titanReport` prints them alone. The details are in
`build/reports/titan/runelite.json`.

## Another Titan

Titan is found by itself. To use another build of it, set `titanClientRoot` in `gradle.properties` to the folder
which holds `controller.exe`.
