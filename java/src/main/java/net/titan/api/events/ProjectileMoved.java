package net.titan.api.events;

import net.titan.api.Projectile;

public final class ProjectileMoved {
    private final Projectile projectile;

    public ProjectileMoved(Projectile projectile) {
        this.projectile = projectile;
    }

    public Projectile projectile() { return projectile; }
}
