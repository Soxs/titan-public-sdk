package net.titan.api.utils;

import net.titan.api.LoginGameState;
import net.titan.api.LoginState;
import net.titan.api.Titan;

import java.util.Optional;

/// Login / account-switch helpers. Mirrors the C++ {@code titan::state::login()}
/// facade. All methods delegate to the live {@link net.titan.api.Client}.
public final class Login {
    private Login() {}

    public static Optional<LoginState> snapshot() {
        return Titan.client().loginState();
    }

    public static LoginGameState state() {
        return snapshot().map(LoginState::gameState).orElse(LoginGameState.UNKNOWN);
    }

    public static int index() {
        return snapshot().map(LoginState::loginIndex).orElse(-1);
    }

    public static boolean isLoggedIn() {
        return state() == LoginGameState.LOGGED_IN;
    }

    public static boolean isWorldReady() {
        var client = Titan.client();
        return client.localPlayer().isPresent()
            && client.currentWorldViewPtr().isPresent()
            && client.sceneSizeX() > 0
            && client.sceneSizeY() > 0
            && client.topLevelSceneSizeX() > 0
            && client.topLevelSceneSizeY() > 0;
    }

    public static boolean setUsername(String username) {
        return Titan.client().setLoginUsername(username);
    }

    public static boolean setPassword(String password) {
        return Titan.client().setLoginPassword(password);
    }

    public static boolean setAuthenticator(String code) {
        return Titan.client().setLoginAuthenticator(code);
    }

    public static boolean setIndex(int loginIndex) {
        return Titan.client().setLoginIndex(loginIndex);
    }

    public static boolean setDisplayName(String displayName) {
        return Titan.client().setLoginDisplayName(displayName);
    }

    public static boolean setOAuth2Credentials(String accessToken, String refreshToken) {
        return Titan.client().setLoginOAuth2Credentials(accessToken, refreshToken);
    }

    public static boolean setGameSessionCredentials(String sessionId, String characterId) {
        return Titan.client().setLoginGameSessionCredentials(sessionId, characterId);
    }

    public static boolean setCharacter(String displayName, String characterId, String sessionId) {
        return Titan.client().setLoginCharacter(displayName, characterId, sessionId);
    }

    public static boolean resetCharacter() {
        return Titan.client().resetLoginCharacter();
    }
}
