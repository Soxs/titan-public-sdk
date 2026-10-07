package net.titan.api.html;

import java.util.Objects;
import java.util.Optional;

/**
 * A validated v1 page message. Payload is owned, serialized JSON, independent
 * of any particular JSON library. Callbacks run under the plugin lifecycle
 * gate on the game thread; page code receives no game API or host objects.
 */
public final class HtmlMessage {
    private final String type;
    private final String payloadJson;
    private final String correlationId;

    public HtmlMessage(String type, String payloadJson, String correlationId) {
        this.type = Objects.requireNonNull(type, "type");
        this.payloadJson = Objects.requireNonNull(payloadJson, "payloadJson");
        this.correlationId = correlationId;
    }

    public int version() { return 1; }
    public String type() { return type; }
    public String payloadJson() { return payloadJson; }
    public Optional<String> correlationId() { return Optional.ofNullable(correlationId); }
}
