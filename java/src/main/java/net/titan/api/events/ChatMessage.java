package net.titan.api.events;

import java.util.Objects;
import java.util.Optional;

public final class ChatMessage {
    /** Complete strings and Unix second observed at the native insertion. */
    public static final class Insertion {
        private final String name, message, sender;
        private final int timestamp;

        public Insertion(String name, String message, String sender, int timestamp) {
            this.name = Objects.requireNonNull(name);
            this.message = Objects.requireNonNull(message);
            this.sender = Objects.requireNonNull(sender);
            this.timestamp = timestamp;
        }

        public String name() { return name; }
        public String message() { return message; }
        public String sender() { return sender; }
        public int timestamp() { return timestamp; }
    }

    private final int type;
    private final String name;
    private final String message;
    private final String sender;
    private final int gameTick;
    private final Insertion insertion;

    public ChatMessage(int type, String name, String message, String sender, int gameTick) {
        this(type, name, message, sender, gameTick, null);
    }

    public ChatMessage(int type, String name, String message, String sender, int gameTick, Insertion insertion) {
        this.type = type;
        this.name = name == null ? "" : name;
        this.message = message == null ? "" : message;
        this.sender = sender == null ? "" : sender;
        this.gameTick = gameTick;
        this.insertion = insertion;
    }

    public int type() { return type; }
    public String name() { return name; }
    public String message() { return message; }
    public String sender() { return sender; }
    public int gameTick() { return gameTick; }
    public Optional<Insertion> insertion() { return Optional.ofNullable(insertion); }
}
