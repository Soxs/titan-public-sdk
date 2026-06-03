package net.titan.api.events;

public final class ChatMessage {
    private final int type;
    private final String name;
    private final String message;
    private final String sender;
    private final int gameTick;

    public ChatMessage(int type, String name, String message, String sender, int gameTick) {
        this.type = type;
        this.name = name == null ? "" : name;
        this.message = message == null ? "" : message;
        this.sender = sender == null ? "" : sender;
        this.gameTick = gameTick;
    }

    public int type() { return type; }
    public String name() { return name; }
    public String message() { return message; }
    public String sender() { return sender; }
    public int gameTick() { return gameTick; }
}
