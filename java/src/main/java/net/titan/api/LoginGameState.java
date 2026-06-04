package net.titan.api;

/// Coarse login / game state mirroring RuneLite's {@code Client.getGameState()}.
/// Matches the C++ {@code titan::LoginGameState} enum.
public enum LoginGameState {
    UNKNOWN(-1),
    LOGIN_SCREEN(0),
    LOGIN_AUTHENTICATOR(1),
    LOGGING_IN(2),
    LOGGED_IN(3),
    HOPPING(4);

    private final int id;

    LoginGameState(int id) {
        this.id = id;
    }

    public int id() { return id; }

    public static LoginGameState fromId(int id) {
        for (LoginGameState value : values()) {
            if (value.id == id) return value;
        }
        return UNKNOWN;
    }
}
