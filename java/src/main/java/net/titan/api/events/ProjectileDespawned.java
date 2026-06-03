package net.titan.api.events;

import net.titan.api.Projectile;

public final class ProjectileDespawned {
    private final Projectile projectile;

    public ProjectileDespawned(Projectile projectile) {
        this.projectile = projectile;
    }

    public Projectile projectile() { return projectile; }
}
