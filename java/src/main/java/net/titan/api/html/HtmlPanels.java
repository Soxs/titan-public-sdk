package net.titan.api.html;

import net.titan.api.internal.HtmlUiBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.Plugin;
import java.util.Objects;

/**
 * Owner-bound HTML side panels. Keep {@code HtmlPanels.of(this)} on the plugin
 * or pass it to helpers. Calls are safe from any thread and never borrow a
 * thread-local plugin ID. Constructor calls return STALE until loading binds
 * the instance; onLoad may set state.
 *
 * <p>State replaces the previous JSON value (maximum 64 KiB UTF-8) and remains
 * across panel switches and disable/enable until the plugin is unloaded.
 * Transient messages require an active surface. They are ordered, bounded to
 * 64 messages/1 MiB, and discarded on deactivation. Overflow rejects the new
 * message. A successful write is acceptance, not proof the page handled it.</p>
 *
 * <p>Declare HTML surfaces unconditionally. Renderer startup is lazy, and the
 * controller provides native diagnostics when a requested surface cannot start.
 * An unused HTML feature does not start a helper to probe availability.</p>
 */
public final class HtmlPanels {
    public static final int MAX_JSON_BYTES = 64 * 1024;
    private final Plugin owner;
    private HtmlPanels(Plugin owner) { this.owner = owner; }
    public static HtmlPanels of(Plugin owner) {
        return new HtmlPanels(Objects.requireNonNull(owner, "owner"));
    }
    public HtmlUiResult setState(String id, String json) {
        return TitanRuntime.getHtmlUiBackend().setState(owner, HtmlUiBackend.SIDE_PANEL, id, json);
    }
    public HtmlUiResult postMessage(String id, String type, String payloadJson) {
        return postMessage(id, type, payloadJson, null);
    }
    public HtmlUiResult postMessage(String id, String type, String payloadJson, String correlationId) {
        return TitanRuntime.getHtmlUiBackend().postMessage(owner, HtmlUiBackend.SIDE_PANEL,
            id, type, payloadJson, correlationId);
    }
}
