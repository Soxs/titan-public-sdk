package net.titan.api.events;

import net.titan.api.Projectile;

public final class ProjectileSpawned {
    private final Projectile projectile;

    public ProjectileSpawned(Projectile projectile) {
        this.projectile = projectile;
    }

    public Projectile projectile() { return projectile; }
}
