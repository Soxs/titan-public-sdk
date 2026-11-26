package net.titan.runtime.spi;

import java.util.List;
import java.util.Set;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/** A separately distributed plugin format, discovered with {@link java.util.ServiceLoader}.
 * All dependency objects remain inside the provider's classloader. Inspection must not
 * start plugins or execute plugin static initializers. An empty list means unrecognized. */
public interface RuntimeProvider extends AutoCloseable {
    String id();
    default int apiVersion() { return 1; }

    /** Optional, per-game client for host-managed Java plugins. */
    default GameApi gameApi() { return null; }

    /** Initialize once before any inspection or creation. The host supplies a
     * trusted provider-specific Titan storage path; create it only as needed.
     * Synchronous initialization and catalog reads must use local/cache data only.
     * After capturing that snapshot, providers may enqueue asynchronous startup
     * or user-requested refresh work without changing the active admission catalog.
     * @param storageDirectory absolute provider-owned data directory
     * @throws Exception if local initialization fails */
    default void initialize(Path storageDirectory) throws Exception {}

    /** Initialize a provider-owned passive service context once. Older providers
     * overriding only the storage overload retain their existing behavior.
     * @param context provider lifetime, storage and read/observe services
     * @throws Exception if initialization fails */
    default void initialize(ProviderContext context) throws Exception { initialize(context.storageDirectory()); }

    /** One actual native client-tick callback, before the per-plugin client-tick
     * callbacks. This is not a server tick or a tick-end callback. Implementations
     * must not block; plugin work still needs its activation/owner fencing.
     * Inspection runtimes never invoke this method.
     * @throws Exception if provider-wide work fails */
    default void onClientTick() throws Exception {}

    /** Opt into provider-wide observational delivery. Snapshotted once after
     * initialization, then intersected with the host's qualified ingress types.
     * The base types are clientTick, gameTick, gameStateChanged and varbitChanged.
     * Additional game, actor and window observations depend on the installed host.
     * Providers can publish revisioned subscriber interest for optional events.
     * See the SPI README for capture requirements, payloads and delivery timing.
     * Matching per-plugin callbacks are suppressed only for qualified routes;
     * older hosts/providers retain per-plugin delivery. This declares no native
     * scanner demand and grants no plugin action authority.
     * @return immutable event-type set; empty retains per-plugin delivery */
    default Set<String> providerEventTypes() { return Set.of(); }

    /** Once per accepted native game-tick fanout for an opted-in provider.
     * No plugin scope is selected; providers fence each subscriber separately.
     * Do not block or wait for the EDT. Equal tick values are not deduplicated.
     * @param tick native tick value
     * @throws Exception if shared event delivery fails */
    default void onGameTick(int tick) throws Exception {}

    /** One qualified native observational event at its documented delivery phase.
     * Client ticks use onClientTick and game ticks use onGameTick instead.
     * Mutable menu/input/sound callbacks are excluded from this ingress.
     * No retries or per-plugin fallback occur after a callback failure.
     * @param type qualified native event name
     * @param json bounded native JSON object using its original field names
     * @throws Exception if shared event delivery fails */
    default void onEvent(String type, String json) throws Exception {}

    /** Opt into synchronous mutable menu ingress. Supported names are
     * PostMenuSort, MenuOpened and menuOptionClicked. EntryAdded is not qualified:
     * the native rebuild notification does not establish insertion-time semantics. The host
     * intersects this immutable declaration with its installed native protocol.
     * Each native event invokes the provider once at its selected Java-plugin
     * position; matching instance delivery is suppressed, including on failure.
     * @return requested menu event names; empty retains instance delivery */
    default Set<String> providerMenuEventTypes() { return Set.of(); }

    /** One synchronous native menu event. No plugin authority is selected:
     * dispatch each subscriber through its own HostServices.dispatchCallback.
     * Mutations require that subscriber's scoped host and the current native
     * transaction. Native callbacks can re-enter this method before returning.
     * Never wait for EDT work or defer a consume decision. Per-entry Click
     * callbacks remain owner-specific PluginInstance.onEvent deliveries.
     * @param type qualified native menu event name
     * @param json bounded native JSON object
     * @return bounded JSON object containing the immediate event result
     * @throws Exception if delivery fails; no instance retry occurs */
    default String onMenuEvent(String type, String json) throws Exception { return "{}"; }

    /** The observational events which this provider's plugin instances take one
     * by one through PluginInstance.onEvent. Snapshotted once after initialization.
     * Null, the default, keeps every event the host sends to each plugin. With any
     * other set the host sends none of the rest, before it builds a payload: an
     * event delivered to the provider does not need to reach each plugin as well.
     * mainLoop is one of them. Game ticks, and requests which need an answer (panels,
     * settings, key input, sounds), are always sent.
     * @return event names its instances handle, or null for every event */
    default Set<String> pluginEventTypes() { return null; }

    /** Opt into one provider render callback per qualified native layer pass.
     * Layers 1 and 2 use the original host ingress contract. Layer 0 additionally
     * requires the strict boolean aboveScene grant in that contract. Requested
     * layers and their qualification are captured at provider initialization;
     * a later grant requires a provider reload. Providers must also check the
     * current render capability and use geometry appropriate to their content.
     * Matching per-plugin render delivery is suppressed after qualification.
     * @return requested native layer IDs; empty keeps per-plugin rendering */
    default Set<Integer> providerRenderLayers() { return Set.of(); }

    /** Compose a provider-owned raster during an actual native render pass.
     * No plugin scope is selected: paint each plugin through its host's
     * dispatchCallback so the final composite can fence every contributor.
     * Publish then draw in this same pass; old frames cannot be replayed.
     * Never wait for the EDT or schedule asynchronous painting.
     * @param layer qualified native layer ID
     * @throws Exception if provider rendering fails */
    default void onRenderOverlay(int layer) throws Exception {}
    List<PluginDescriptor> inspect(PluginArtifact artifact) throws Exception;
    PluginInstance create(PluginArtifact artifact, PluginDescriptor descriptor,
                          PluginContext context) throws Exception;

    /** Optional entries shipped inside this provider's trusted distribution.
     * This is metadata inspection only: do not initialize plugin classes or
     * start work. IDs must be stable, globally unique, and distinct from IDs
     * assigned to separately delivered artifacts. The host always presents
     * bundled entries disabled until explicitly enabled by the user.
     * @return immutable catalog entries, or an empty list when unsupported
     * @throws Exception if provenance or metadata validation fails */
    default List<BundledPlugin> bundledPlugins() throws Exception { return List.of(); }

    /** Create a catalog-selected bundled entry in the provider's existing realm.
     * The entry is the exact object returned by bundledPlugins; it is not
     * supplied by an artifact or external catalog. No synthetic JAR is required.
     * Providers must revalidate the actual artifact hash before creating it;
     * cached inspection metadata alone does not prove unchanged provenance.
     * @param plugin the exact cached bundled catalog entry
     * @param context generation-owned services and initial state
     * @return the unstarted plugin instance
     * @throws Exception if creation or provenance validation fails */
    default PluginInstance createBundled(BundledPlugin plugin,
                                         PluginContext context) throws Exception {
        throw new UnsupportedOperationException("This runtime provider has no bundled plugins");
    }

    /** Snapshot locally installed entries from a provider-managed source.
     * Do not perform network requests, initialize plugin classes, or start
     * plugin work. After ProviderContext.installedPluginsChanged(), the host can
     * admit additional packages. It retains the original version of each known package.
     * @return installed entries, or an empty list when unsupported
     * @throws Exception if local catalog/provenance validation fails */
    default List<InstalledPlugin> installedPlugins() throws Exception { return List.of(); }

    /** Create the exact cached installed entry after revalidating its artifact
     * and source provenance. No host-supplied replacement JAR is accepted here.
     * @param plugin the catalog-selected installed entry
     * @param context generation-owned services and initial state
     * @return the unstarted plugin instance
     * @throws Exception if creation or provenance validation fails */
    default PluginInstance createInstalled(InstalledPlugin plugin,
                                           PluginContext context) throws Exception {
        throw new UnsupportedOperationException("This runtime provider has no managed installed plugins");
    }

    /** Stop provider-owned refresh workers and other distribution-wide work.
     * Complete only after owned work is quiescent; do not wait on EDT or game
     * threads. The host retains the realm until this stage succeeds.
     * @return completion of provider shutdown */
    default CompletionStage<Void> closeAsync() {
        try { close(); return CompletableFuture.completedFuture(null); }
        catch (Throwable failure) { return CompletableFuture.failedFuture(failure); }
    }
    @Override default void close() throws Exception {}
}
