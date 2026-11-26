package net.titan.runtime.spi;

/** One plugin instance owned by one host generation. Game/lifecycle callbacks are serialized by the host.
 * Implementations must drain their owned background work on unload; callbacks must not
 * synchronously wait for another host callback or Swing EDT while holding a host call. */
public interface PluginInstance {
    default void onLoad() throws Exception {}
    default void onUnload() throws Exception {}
    default void onEnable() throws Exception {}
    default void onDisable() throws Exception {}
    default java.util.concurrent.CompletionStage<Void> onLoadAsync() {
        try { onLoad(); return java.util.concurrent.CompletableFuture.completedFuture(null); }
        catch (Throwable error) { return java.util.concurrent.CompletableFuture.failedFuture(error); }
    }
    default java.util.concurrent.CompletionStage<Void> onEnableAsync() {
        try { onEnable(); return java.util.concurrent.CompletableFuture.completedFuture(null); }
        catch (Throwable error) { return java.util.concurrent.CompletableFuture.failedFuture(error); }
    }
    default java.util.concurrent.CompletionStage<Void> onDisableAsync() {
        try { onDisable(); return java.util.concurrent.CompletableFuture.completedFuture(null); }
        catch (Throwable error) { return java.util.concurrent.CompletableFuture.failedFuture(error); }
    }
    default java.util.concurrent.CompletionStage<Void> onUnloadAsync() {
        try { onUnload(); return java.util.concurrent.CompletableFuture.completedFuture(null); }
        catch (Throwable error) { return java.util.concurrent.CompletableFuture.failedFuture(error); }
    }
    default void onSettingChanged(String key, String valueJson) throws Exception {}
    default void onGameTick(int tick) throws Exception {}
    default void onRenderOverlay(int layer) throws Exception {}
    /** Asynchronous native side-panel lifecycle, entered on the Swing EDT outside
     * the game/lifecycle monitor. May overlap game/lifecycle callbacks; implementations
     * must confine their Swing state to the EDT and close it during disable/unload.
     * @param json native panel identity, activation/revision and geometry
     * @return completion JSON with status ready/error/stale and optional error; never wait for
     * another host callback or join an unfinished Swing future
     */
    default java.util.concurrent.CompletionStage<String> onPanelLifecycle(String json) {
        return java.util.concurrent.CompletableFuture.completedFuture("{\"status\":\"error\",\"error\":\"Panel lifecycle unavailable\"}");
    }
    /** A popup query or command for an exact registered sidebar entry. Entered on
     * the EDT outside the lifecycle monitor, counted through completion. Capture
     * popup callbacks under a fresh menu token and consume that snapshot before
     * executing a command. Never wait for another host callback. */
    default java.util.concurrent.CompletionStage<String> onPanelCommand(String json) {
        return java.util.concurrent.CompletableFuture.completedFuture("{\"status\":\"error\",\"error\":\"Panel commands unavailable\"}");
    }
    /** Native event names/payloads, including synchronous mutable input/menu events.
     * Unhandled events must not claim consumption.
     * @param type native event name
     * @param json native event data as a JSON object
     * @return a JSON object describing explicitly handled mutations
     * @throws Exception when the plugin callback fails
     */
    default String onEvent(String type, String json) throws Exception { return "{}"; }

    /** Handle a settings request for a loaded instance, including while disabled.
     * Return a snapshot or enqueue work without waiting for a UI thread. Use the
     * supplied dispatcher at execution time for deferred configuration work. It
     * runs the work on the calling thread only while the same load and lifecycle
     * revision can still edit settings. It grants no gameplay callback admission.
     * @param requestJson validated configuration management envelope
     * @param dispatchIfCurrent dispatcher bound to the requesting lifecycle revision
     * @return a bounded JSON object, or null when the provider has no settings editor
     * @throws Exception if the provider cannot process the request
     */
    default String onConfigurationRequest(String requestJson,
            java.util.function.Predicate<Runnable> dispatchIfCurrent) throws Exception { return null; }
}
