package net.titan.runtime.spi;

import java.awt.Frame;
import java.awt.Component;
import java.util.function.Consumer;

/** Explicit host capability boundary. JSON is data, never code; unsupported operations
 * throw rather than fabricating a successful result. No Guice or runtime classes cross it. */
public interface HostServices {
    boolean supports(String capability);
    /** Invoke an explicitly qualified operation. Window attention uses
     * {@code titan.window.attention.request.v1} with an empty object and returns
     * status {@code queued} or {@code unavailable}; queued is not confirmation
     * of visible OS delivery. Only an active ready plugin may submit it, never
     * the passive provider host. Capability discovery uses
     * {@code titan.window.attention.capabilities.v1}. It does not request focus,
     * switch tabs, or display a tray notification.
     * @param operation versioned host operation
     * @param json owned JSON data
     * @return owned response JSON
     */
    String invoke(String operation, String json);
    /** Read the configured enable preference for an exact loaded entry of this
     * provider in the caller's game tab. A startup failure preserves the choice.
     * Capability: titan.plugins.enablePreference.v1.
     * @param pluginId host entry ID
     * @param loadGeneration exact host-issued generation of the queried entry
     * @return the selection used for this tab
     */
    default boolean pluginEnablePreference(String pluginId, long loadGeneration) {
        throw unavailable("titan.plugins.enablePreference.v1");
    }
    /** Readiness hint for externally driven callbacks. False during startup,
     * pending native admission, disable and retirement. The snapshot does not
     * authorize later execution; use dispatchCallback for final admission.
     * @return whether this owning plugin is currently ready */
    default boolean isCallbackReady() { return false; }
    /** Atomically admit finite synchronous work for this active, ready owner.
     * Executes on the calling thread under host owner/context-loader attribution;
     * it neither schedules work nor changes thread affinity. Cleanup starts
     * without waiting, but retirement waits for admitted work to leave. The
     * passive provider host denies admission. Capability: callbacks.dispatch.
     * @param callback finite work; must not synchronously wait for its own cleanup
     * @return true if work ran, false when unavailable or the owner is not ready */
    default boolean dispatchCallback(Runnable callback) { return false; }
    /** Versioned, owned binary snapshots.
     * @param capability versioned snapshot capability
     * @param json JSON request data
     * @return owned bytes, null when unavailable; empty may denote an unchanged generation
     */
    default byte[] snapshot(String capability, String json) { throw unavailable(capability); }
    /** A number which changes whenever the provider's shared configuration may have changed, in this game or in another
     * one. What `titan.config.shared.snapshot.v1` answered is what the configuration holds for as long as this is the
     * number which came with it, so a provider can keep that answer and read its settings from it.
     *
     * It is asked for every read of a setting and so does no work to speak of, and it throws nothing.
     * @return the number, or a negative one where the host cannot tell without looking at the file, or for a caller
     * whose read of a shared setting it would refuse: a provider then asks for every setting, and is answered or
     * refused, as before this existed
     */
    default long sharedConfigurationRevision() { return -1; }
    /** Poll all 24 named skills on the game thread. Capability titan.skills.snapshot.v1.
     * Supply one private, writable 304-byte direct buffer in LITTLE_ENDIAN order
     * per observer. Initialize it to zero and clear its first eight bytes on a
     * subscription change. Reuse it without concurrent access. Offsets 0..15
     * are opaque. Skill n has three signed ints starting at 16 + 12*n, in
     * experience, real level, boosted level order. First sample, session change
     * or missing client tick seeds a baseline silently.
     * @param observation reusable observer-owned storage, position zero, limit 304
     * @return 24-bit changed-skill mask, zero when seeded or unchanged, or -1
     * when current source, game thread, login or ownership is unavailable
     */
    default int pollSkillChanges(java.nio.ByteBuffer observation) { return -1; }
    /** Compare eight settled, nonnull offers on the game thread. Capability titan.grandExchange.changes.v1.
     * Use one writable 280-byte little-endian direct buffer per observer (position 0, limit 280).
     * Initialize to zero and clear its first eight bytes after any unavailable read or subscription gap.
     * The first 24 bytes are opaque. Each 32-byte slot contains signed int itemId, totalQuantity,
     * quantitySold at 0,4,8, unsigned status/type bytes at 12,13, and signed long price/spent at 16,24.
     * @param observation caller-owned reusable storage, accessed by one observer at a time
     * @return -2 for a fresh session/source/tick baseline, -1 if unavailable, 0 if unchanged,
     * or an eight-bit mask of changed slots
     */
    default int pollGrandExchangeChanges(java.nio.ByteBuffer observation) { return -1; }
    /** Queue on an actual host phase.
     * @param phase "clientTick" or "render"
     * @param callback finite work, fenced by the owning plugin generation
     */
    void schedule(String phase, Runnable callback);
    default long rasterCreate(int width, int height) { throw unavailable("raster"); }
    default boolean rasterPublish(long surface, int width, int height, long sequence, int[] argbPremultiplied) { throw unavailable("raster"); }
    /** Renew a byte-identical publication for this render pass. Supply the sequence
     * of the last successful full publish. False requires a fresh publication.
     * Available through titan.raster.reuse.v1. */
    default boolean rasterReuse(long surface, long publishedSequence) { return false; }
    /** Within a client paint: lets another thread publish what the paint has drawn until now, after it has ended too. What is
     * run by it publishes as this paint would, with its contributors, and does nothing once the provider has retired. Take one
     * when a surface is finished and run it once. Available through titan.raster.later.v1. */
    default java.util.function.Consumer<Runnable> rasterLater() { throw unavailable("titan.raster.later.v1"); }
    default boolean rasterDraw(long surface, int x, int y, float scale) { throw unavailable("raster"); }
    default void rasterClose(long surface) { throw unavailable("raster"); }
    /** Open a provider-owned group for bounded above-widgets frames. Anchor 0 uses native free placement. */
    default int infoBoxOpen(String name, int anchor, int priority) { throw unavailable("titan.hud.infoboxes.v1"); }
    /** Copy straight ARGB artwork once per change. Zero dimensions with an empty array release the key. */
    default boolean infoBoxSetIcon(int panel, int key, int width, int height, int[] argb) { throw unavailable("titan.hud.infoboxes.v1"); }
    /** Present up to 128 ordered tiles in this render callback. An unpublished group is hidden for that frame. */
    default boolean infoBoxPresent(int panel, boolean vertical, int size, java.util.List<InfoBoxTile> tiles) { throw unavailable("titan.hud.infoboxes.v1"); }
    default void infoBoxClose(int panel) { throw unavailable("titan.hud.infoboxes.v1"); }
    /** Open a provider-owned Alt-drag target for a manually positioned above-widgets painter. */
    default int canvasDragOpen(String name) { throw unavailable("titan.overlay.canvasDrag.v1"); }
    /** Present the painted bounds in canvas pixels. A null result means the supplied position stands.
     * Otherwise [x, y, state] gives the preview (1) or the released position (2) exactly once.
     * Change placementRevision when the source placement changes, independently of drag previews. */
    default int[] canvasDragFrame(int handle, int x, int y, int width, int height,
                                  int canvasWidth, int canvasHeight, long placementRevision) {
        throw unavailable("titan.overlay.canvasDrag.v1");
    }
    /** Present a canvas drag region that participates in Titan's HUD anchor layout.
     * The saved anchor is 0 (free), 1 (top center), 2 (left center), 3 (right center) or
     * 4 (above chatbox right). Null retains the supplied x/y. A reply [x,y,state,anchor]
     * supplies a drag preview (1), a drop to persist (2), or a layout adjustment (3).
     * Reuse the last resolved top-left for the same dock to avoid idle frame allocations. */
    default int[] canvasDockFrame(int handle, int x, int y, int width, int height,
                                  int canvasWidth, int canvasHeight, long placementRevision, int anchor) {
        throw unavailable("titan.overlay.canvasDock.v1");
    }
    default void canvasDragClose(int handle) { throw unavailable("titan.overlay.canvasDrag.v1"); }
    default Frame createEmbeddedFrame(long parentHwnd) { throw unavailable("swing.embedding"); }
    default void setEmbeddedBounds(Frame frame, int x, int y, int width, int height) { throw unavailable("swing.embedding"); }
    default void setEmbeddedPhysicalBounds(Frame frame, int x, int y, int width, int height) { throw unavailable("swing.embedding.physicalBounds"); }
    default void activateEmbeddedFrame(Frame frame, boolean active) { throw unavailable("swing.embedding"); }
    /** Admit a real modeless top-level AWT window to this plugin's game and activation.
     * Call on the EDT after creating its peer, before making it visible. The
     * native host keeps it hidden until the controller admits presentation for
     * the owning game; this method does not wait for the controller or show it.
     * A non-null AWT owner must belong to this same active plugin UI context.
     * A successful close from any thread revokes native presentation before
     * returning. The plugin must separately dispose the window on the EDT
     * and await that disposal before completing lifecycle cleanup. Disable and
     * unload also revoke this registration. This does not reparent the window,
     * grant native-peer access, or automatically admit other top-level windows.
     * A Dialog must remain modeless throughout its registered lifetime.
     * @param window hidden, displayable owned top-level window
     * @return idempotent presentation registration; capability: swing.window
     */
    default AutoCloseable registerWindow(java.awt.Window window) { throw unavailable("swing.window"); }
    default boolean inputOpen() { throw unavailable("input.observe"); }
    default String inputPoll() { throw unavailable("input.observe"); }
    default boolean inputComplete(long sequence, boolean consumed) { throw unavailable("input.consume"); }
    default boolean inputSubmit(String json) { throw unavailable("input.synthetic"); }
    default void inputClose() { throw unavailable("input.observe"); }
    default boolean registerPanel(PanelDescriptor descriptor) { throw unavailable("titan.panel.register.v1"); }
    default boolean removePanel(String panelId) { throw unavailable("titan.panel.remove.v1"); }
    default boolean openPanel(String panelId) { throw unavailable("titan.panel.open.v1"); }
    /** Remove only the exact command-capable registration, including during cleanup. */
    default boolean removePanel(String panelId, String registration) {
        if ("".equals(registration)) return removePanel(panelId);
        throw unavailable("titan.panel.commands.v1");
    }
    /** Open only the exact registration. Action-only entries never open a panel. */
    default boolean openPanel(String panelId, String registration) {
        if ("".equals(registration)) return openPanel(panelId);
        throw unavailable("titan.panel.commands.v1");
    }
    /** Bind an owned Swing root to its plugin activation. Register on the EDT;
     * close from any thread to revoke pending callbacks. The dispatcher must
     * invoke its argument synchronously on the calling EDT under the captured
     * plugin scope, or discard it when that scope has retired. Nested ordinary
     * invokeLater work inherits the binding; arbitrary threads do not.
     * Providers with private callback scope must use the three-argument form
     * plus rootless isolation to clear attribution in nested/modal dispatch.
     * @param root owned component subtree; register its Frame separately for owned dialogs
     * @param dispatcher synchronous owner-context dispatcher
     * @return idempotent registration, also closed automatically on disable/unload
     */
    default AutoCloseable registerUiContext(Component root, Consumer<Runnable> dispatcher) { throw unavailable("ui.context"); }
    /** Bind with an explicit scope clearer for unowned and nested/modal events.
     * The host calls live clearers before event dispatch, then enters the selected
     * root's dispatcher. The clearer must synchronously run its argument exactly
     * once with provider callback attribution removed, restoring it afterward.
     * @param root owned component subtree
     * @param dispatcher synchronous owner-context dispatcher
     * @param unownedDispatcher synchronous clear/restore callback; must not gate on plugin activation
     * @return idempotent registration, automatically revoked on disable/unload
     */
    default AutoCloseable registerUiContext(Component root, Consumer<Runnable> dispatcher,
                                           Consumer<Runnable> unownedDispatcher) { throw unavailable("ui.context.unowned"); }
    /** Register a scope clearer before constructing any UI root, including modal
     * dialogs opened during startup. Register on the EDT and renew per activation.
     * Rootless clearers grant no action authority. Closing during a pending
     * lifecycle stage defers their removal until that stage ends, so a suspended
     * startup modal cannot leak its private scope after disable/unload.
     * @param unownedDispatcher synchronous clear/restore callback, always runs work exactly once
     * @return idempotent registration, automatically removed after lifecycle cleanup
     */
    default AutoCloseable registerUiIsolation(Consumer<Runnable> unownedDispatcher) { throw unavailable("ui.context"); }
    /** Bind Swing invocations posted synchronously by this owner's admitted
     * {@link #dispatchCallback} work to this exact activation. Register once on
     * the EDT per activation; close revokes already-posted invocations. The
     * dispatcher runs only on the EDT and must recheck the captured provider
     * scope. Arbitrary threads and unowned posts gain no attribution. Nested
     * owned invocations retain the same registration. Capability: ui.context.callbacks.
     * @param dispatcher synchronous captured-owner EDT dispatcher
     * @param unownedDispatcher synchronous provider scope clearer
     * @return activation registration, also revoked automatically on disable/unload
     */
    default AutoCloseable registerUiCallbackContext(Consumer<Runnable> dispatcher,
                                                   Consumer<Runnable> unownedDispatcher) { throw unavailable("ui.context.callbacks"); }
    /**
     * Optional registration returned by {@link #registerUiCallbackContext} on a
     * host supporting ui.context.workerPosts.v1. A provider may attach this
     * exact activation to AWT invocations posted by its own managed worker.
     * This does not admit a host callback or grant the worker game-thread access.
     * Closing the registration revokes queued and subsequent posts, including
     * posts made by a task already running during retirement.
     */
    interface WorkerUiContext extends AutoCloseable {
        /** Run on the calling thread with this activation's AWT posting context. */
        void withUiPosts(Runnable work);
    }
    /** Bind one original, stopped Swing Timer to this activation, on the EDT.
     * Does not replace its listeners. Each posted invocation retains its original
     * registration; closing revokes queued callbacks before a later registration.
     * The owner must stop the timer on the EDT during cleanup. Timer subclasses,
     * already-running timers and unqualified desktop runtimes are unsupported.
     * @param timer exact owned timer, registered before its first start
     * @param dispatcher synchronous captured-owner dispatcher
     * @param unownedDispatcher synchronous provider scope clearer
     * @return activation registration; capability: ui.timer.context
     */
    default AutoCloseable registerUiTimerContext(javax.swing.Timer timer, Consumer<Runnable> dispatcher,
                                               Consumer<Runnable> unownedDispatcher) { throw unavailable("ui.timer.context"); }
    /** Titan's SDK for this plugin's own classes, or null where this host offers none.
     * @return the SDK of the plugin this host belongs to; a provider's own host has none */
    default TitanApi titanApi() { return null; }
    static UnsupportedOperationException unavailable(String capability) {
        return new UnsupportedOperationException("Host capability unavailable: " + capability);
    }
}
