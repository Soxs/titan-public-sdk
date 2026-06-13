package net.titan.api.queries;

import net.titan.api.Actor;
import net.titan.api.Client;
import net.titan.api.EntityType;
import net.titan.api.Projectile;

public final class ProjectileQuery extends LocatableQuery<Projectile, ProjectileQuery> {
    public ProjectileQuery(Client client) {
        super(client, client == null ? null : client.projectiles());
    }

    public ProjectileQuery spotAnim(int animationId) {
        return where(projectile -> projectile.spotAnimId() == animationId);
    }

    public ProjectileQuery targetingEntity(int entityIndex) {
        return where(projectile -> projectile.targetEntity() == entityIndex);
    }

    public ProjectileQuery fromEntity(int entityIndex) {
        return where(projectile -> projectile.sourceEntity() == entityIndex);
    }

    public ProjectileQuery targetingActor(Actor actor) {
        int targetHash = NPCQuery.targetHash(actor);
        int targetType = NPCQuery.targetType(actor);
        return where(projectile -> targetHash >= 0
            && targetType != EntityType.NONE
            && projectile.targetEntity() == targetHash
            && projectile.targetEntityType() == targetType);
    }

    public ProjectileQuery fromActor(Actor actor) {
        int sourceHash = NPCQuery.targetHash(actor);
        int sourceType = NPCQuery.targetType(actor);
        return where(projectile -> sourceHash >= 0
            && sourceType != EntityType.NONE
            && projectile.sourceEntity() == sourceHash
            && projectile.sourceEntityType() == sourceType);
    }

    public ProjectileQuery startedAfterTick(int tick) {
        return where(projectile -> projectile.startTick() >= tick);
    }

    public ProjectileQuery endsBeforeTick(int tick) {
        return where(projectile -> projectile.endTick() <= tick);
    }

    public ProjectileQuery activeDuring(int tick) {
        return where(projectile -> projectile.startTick() <= tick && projectile.endTick() >= tick);
    }
}
