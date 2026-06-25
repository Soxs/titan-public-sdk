package net.titan.api;

/// Native client game-state values exposed by {@code Client.getGameState()}.
/// Matches the C++ {@code titan::LoginGameState} enum.
public enum LoginGameState {
    UNKNOWN(-1),
    LOGIN_SCREEN(10),
    LOGIN_AUTHENTICATOR(11),
    LOGGING_IN(20),
    LOGGED_IN(30),
    HOPPING(45);

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
