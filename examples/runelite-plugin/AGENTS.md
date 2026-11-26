# Working on this plugin

A RuneLite-style plugin which runs in Titan, a native OSRS client. These instructions are for any coding agent.

## Commands

| Command (`.\gradlew` on Windows) | Use |
|---|---|
| `./gradlew build` | Compile, check the JAR, and list the RuneLite calls Titan does not supply. Run it after every change. |
| `./gradlew titanReport` | That list alone. Details: `build/reports/titan/runelite.json`. |
| `./gradlew run` | Open Titan with the plugin. It opens a game client on the developer's computer: ask first. |
| `./gradlew reload` | Hot reload the plugin in the tab `run` opened. Safe to repeat. Fails with the reason when there is no tab. |

A green `build` without a "Not supplied" list is the check you can make yourself. Only the developer sees the game.

## The build

- `build.gradle` is complete. `net.titan.runelite` adds RuneLite, Titan's SDK, Lombok and the tasks above. Do not add
  RuneLite repositories or dependencies, and do not put RuneLite or Titan classes in the JAR.
- Another library goes in `dependencies { implementation ... }` and must be put in the JAR by you. Prefer none: Guava,
  Gson, OkHttp and commons-text are already there.
- Java 11. Keep to its language and library.

## The code

- One class is annotated with `net.runelite.client.plugins.PluginDescriptor` and extends
  `net.runelite.client.plugins.Plugin`. `shutDown` undoes everything `startUp` did: hot reload runs both.
- Events: `net.runelite.client.eventbus.Subscribe` with `net.runelite.api.events.*` or `net.runelite.client.events.*`.
- The game is read and changed on the client thread: event handlers, overlays, or `ClientThread.invoke`. Never block it
  and never sleep on it. Slow work goes to the injected `ScheduledExecutorService`.
- Keep handlers of `ClientTick`, `BeforeRender` and overlay `render` cheap. They run for every frame or 50 times a second.
- Constants: `net.runelite.api.gameval.*` (`ItemID`, `NpcID`, `ObjectID`, `InterfaceID`, `VarbitID`, ...). Look them up in
  the attached sources. Do not write numbers from memory.
- A call Titan does not supply throws `UnsupportedOperationException` with its name. Catch it only where the plugin can
  really go on without it.

## Titan's SDK

- `@Inject private net.titan.api.Client titan;` or the static `net.titan.api.Titan`. Its types share names with
  RuneLite's, so write the Titan ones in full.
- Use it for what RuneLite does not do: `Titan.webWalk()`, `Titan.itemPrices()`, the helpers in `net.titan.api.utils`
  (`Walk`, `Bank`, `Inventory`, `Equipment`, `Dialogue`, `Shop`, `Ge`, `Magic`, `Prayers`, ...) and `net.titan.api.queries`.
  RuneLite objects also have `npc.interact("Talk-to")`, `player.interact(slot)` and `widget.interact("Activate")`. A `true`
  result means the request was accepted: watch the game's state for its outcome.
- Do not use `net.titan.api.plugins`, `eventbus`, `config`, `overlay`, `panel` or `Titan.getInjector()` here. Those belong to
  Titan-style plugins, and RuneLite's equivalents work.
- Signatures: the sources of `net.titan:titan-plugin-api` and `net.titan:titan-runelite` are attached in the IDE, and are
  the `-sources.jar` files beside the JARs in the Gradle cache.
