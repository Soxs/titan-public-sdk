# Swing panels and Java2D overlays

Titan Java plugins can register a Swing component in the game tab's sidebar and
paint a HUD with a real `Graphics2D`. Both APIs are available in the Java
0.2.09 SDK and require a client build that supplies their native services.
The [Swing Counter plugin](../examples/java-swing/SwingHudSamplePlugin.java)
is a complete Java 11 example.

## Sidebar

Register the panel from each `onEnable()`. Give it a stable plugin-local ID,
a title for the sidebar, and a factory that returns a fresh, unparented Swing
component. The host runs the factory on the Swing event dispatch thread (EDT)
when the user selects the panel. A retained component keeps its state across tab
changes within the same activation. Use a separate model for state that must
survive disabling the plugin.

```java
panel = SwingPanels.of(this).register("counter", "Counter", () -> {
    JButton button = new JButton("Add one");
    button.addActionListener(e -> count.incrementAndGet());
    return button;
});
```

`panel.ready()` completes when the sidebar entry is registered.
`panel.requestOpen()` requests selection in the owning game tab. Use
`panel.close()` to start cleanup, or observe `panel.closeAsync()` for completion.
Keep the EDT and plugin lifecycle callbacks free to return. Schedule work that
needs live game state on Titan's client thread. The example shares a single
`AtomicInteger` between the EDT and the render callback.

## Anchors and Alt-drag

Create the HUD in `onEnable()` with a stable ID. For example, this places a
152 × 38 pixel counter in the top-center stack with priority 50.

```java
hud = Graphics2DOverlay.anchored("counter", 152, 38, OverlayPanelAnchor.TOP_CENTER, 50);
```

Paint during the plugin's `renderOverlay` callback. Coordinates are local to
the raster. Each callback receives a fresh graphics context and a transparent
image backed by reusable storage. Titan draws it at the rectangle it supplies
to native hit testing. Keep the graphics context inside the painter.

```java
@Override public void renderOverlay(OverlayLayer layer) {
    if (layer == OverlayLayer.ABOVE_WIDGETS) {
        int value = count.get();
        hud.renderCached(value, g -> {
            g.setColor(Color.WHITE);
            g.drawString("Count: " + value, 12, 24);
        });
    }
}
```

Call `renderCached` each visible frame when a value or revision captures
every change to the artwork. Titan reuses the last image at the current
position and repaints after a revision change, resize or re-enable. Use
`render` for painters that need to run every frame.

In a logged-in game viewport, hold **Alt** and drag with the left mouse button.
Drop in a highlighted zone to snap to top center, left center, right center
or above the chatbox. Drop elsewhere to keep a free position. Titan restores
placement by plugin ID and HUD ID, including after re-enabling the plugin.
Stacked HUDs follow their native anchor when the viewport or HUD changes size.
`DYNAMIC` starts at the top-left margin. `TOOLTIP` follows the pointer and is
excluded from dragging.

For a scene marker or another visual whose coordinates come from the plugin,
construct `new Graphics2DOverlay(id, width, height, layer)` and call
`render(x, y, painter)` in the matching layer. This uses game-canvas pixels
and the available native render phase. Anchored HUDs use `ABOVE_WIDGETS`.
Use `resize` when the raster dimensions change. If you create the HUD in
`onEnable`, close the registration in `onDisable` so the next enable can
reuse its ID. A HUD registered once per plugin load can keep the same Java
handle across enables. Titan releases the image and native surface on disable
and retires registrations on unload.

## Try Swing Counter in Debug

1. Put the sample JAR in a DEV plugin scan directory, such as the `plugins`
   directory beside the Debug `client.dll`, then start a fresh game tab.
2. Enable **Swing Counter** in **Plugin List**. Open **Swing Counter** in the
   sidebar and click **Add one**. The panel and in-game HUD show the same count.
3. Alt-drag the HUD to another anchor, resize the game, then disable and
   re-enable the plugin. The HUD returns to the saved anchor.
