package net.titan.api.events;

import net.titan.api.Actor;
import net.titan.api.NPC;
import net.titan.api.Player;

/**
 * Fired when the native client accepts an actor animation field change.
 * Same-animation resets and rejected native requests are filtered before
 * dispatch. Added in SDK 78.
 */
public final class AnimationChanged {
    private final Actor actor;
    private final int actorType;
    private final String kind;
    private final int indexOrId;
    private final String actorName;
    private final int oldAnimation;
    private final int newAnimation;
    private final int gameTick;

    public AnimationChanged(Actor actor, int actorType, String kind, int indexOrId,
                            String actorName, int oldAnimation, int newAnimation,
                            int gameTick) {
        this.actor = actor;
        this.actorType = actorType;
        this.kind = kind == null ? "none" : kind;
        this.indexOrId = indexOrId;
        this.actorName = actorName == null ? "" : actorName;
        this.oldAnimation = oldAnimation;
        this.newAnimation = newAnimation;
        this.gameTick = gameTick;
    }

    /** Resolved actor, or null if the host could not resolve it. */
    public Actor actor() { return actor; }
    /** Resolved player, or null when the actor is not a player. */
    public Player player() { return actor instanceof Player ? (Player) actor : null; }
    /** Resolved NPC, or null when the actor is not an NPC. */
    public NPC npc() { return actor instanceof NPC ? (NPC) actor : null; }
    /** EntityType constant: PLAYER, NPC, or NONE. */
    public int actorType() { return actorType; }
    /** Lowercase actor kind: player, npc, or none. */
    public String kind() { return kind; }
    /** Player hash index for players, NPC id for NPCs, or -1 when unresolved. */
    public int indexOrId() { return indexOrId; }
    /** Resolved actor name, or empty when unresolved. */
    public String actorName() { return actorName; }
    /** Previous raw Actor.Animation id. */
    public int oldAnimation() { return oldAnimation; }
    /** New raw Actor.Animation id accepted by the native setter. */
    public int newAnimation() { return newAnimation; }
    /** Current game tick captured at dispatch, or 0 if unavailable. */
    public int gameTick() { return gameTick; }
}

