package net.titan.api;

import java.util.Optional;

public interface Client {
    int tick();
    int plane();
    int localPlayerIndex();
    int playerCount();
    int baseX();
    int baseY();
    int sceneSizeX();
    int sceneSizeY();
    int runEnergy();
    int weight();
    boolean loggedIn();
    Optional<Player> localPlayer();
    boolean walkTo(int sceneX, int sceneY);
}
