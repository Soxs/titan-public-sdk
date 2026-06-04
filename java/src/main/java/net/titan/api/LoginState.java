package net.titan.api;

/// Snapshot of the current login-screen / account state. Mirrors the C++
/// {@code titan::LoginSnapshot} returned by {@code state::login().snapshot()}.
public final class LoginState {
    private int loginIndex = -1;
    private int gameState = -1;
    private int fieldToggle = -1;
    private boolean oauthSwitchAvailable;
    private boolean credentialSetAvailable;
    private boolean displayNameAvailable;
    private String username;
    private String displayName;

    public int loginIndex() { return loginIndex; }
    public LoginGameState gameState() { return LoginGameState.fromId(gameState); }
    public int fieldToggle() { return fieldToggle; }
    public boolean oauthSwitchAvailable() { return oauthSwitchAvailable; }
    public boolean credentialSetAvailable() { return credentialSetAvailable; }
    public boolean displayNameAvailable() { return displayNameAvailable; }
    public String username() { return username == null ? "" : username; }
    public String displayName() { return displayName == null ? "" : displayName; }
    public boolean isLoggedIn() { return gameState() == LoginGameState.LOGGED_IN; }
}
