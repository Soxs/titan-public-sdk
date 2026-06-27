package net.titan.api;

import net.titan.api.internal.TitanRuntime;

/// Snapshot of the current login-screen / account state. Mirrors the C++
/// {@code titan::LoginSnapshot} returned by {@code state::login().snapshot()}.
public final class LoginState {
    private boolean liveHandle = true;
    private int loginIndex = -1;
    private int gameState = -1;
    private int fieldToggle = -1;
    private boolean oauthSwitchAvailable;
    private boolean credentialSetAvailable;
    private boolean displayNameAvailable;
    private String username;
    private String displayName;

    private LoginState live() { return TitanRuntime.currentLive(this); }

    public int loginIndex() { return live().loginIndex; }
    public LoginGameState gameState() { return LoginGameState.fromId(live().gameState); }
    public int fieldToggle() { return live().fieldToggle; }
    public boolean oauthSwitchAvailable() { return live().oauthSwitchAvailable; }
    public boolean credentialSetAvailable() { return live().credentialSetAvailable; }
    public boolean displayNameAvailable() { return live().displayNameAvailable; }
    public String username() {
        String value = live().username;
        return value == null ? "" : value;
    }
    public String displayName() {
        String value = live().displayName;
        return value == null ? "" : value;
    }
    public boolean isWorldReady() {
        var client = Titan.client();
        return client.localPlayer().isPresent()
            && client.currentWorldViewPtr().isPresent()
            && client.sceneSizeX() > 0
            && client.sceneSizeY() > 0
            && client.topLevelSceneSizeX() > 0
            && client.topLevelSceneSizeY() > 0;
    }
    public boolean isLoggedIn() { return gameState() == LoginGameState.LOGGED_IN; }
    public boolean exists() { return TitanRuntime.liveExists(this); }
    public LoginState snapshot() { return TitanRuntime.snapshotLive(this); }
}
