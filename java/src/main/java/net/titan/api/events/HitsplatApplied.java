package net.titan.api.events;

import net.titan.api.Actor;
import net.titan.api.NPC;
import net.titan.api.Player;

/**
 * Fired when the native client applies a visible hitsplat to a player or NPC.
 * Added in SDK 74; native signature corrected in SDK 76.
 */
public final class HitsplatApplied {
    private final Actor actor;
    private final int actorType;
    private final String kind;
    private final int indexOrId;
    private final String actorName;
    private final int type;
    private final int value;
    private final int limit;
    private final int delay;
    private final int cycle;
    private final int gameTick;

    public HitsplatApplied(Actor actor, int actorType, String kind,
                           int indexOrId, String actorName, int type,
                           int value, int limit, int delay, int cycle,
                           int gameTick) {
        this.actor = actor;
        this.actorType = actorType;
        this.kind = kind == null ? "none" : kind;
        this.indexOrId = indexOrId;
        this.actorName = actorName == null ? "" : actorName;
        this.type = type;
        this.value = value;
        this.limit = limit;
        this.delay = delay;
        this.cycle = cycle;
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

    /** Native hitsplat type id. */
    public int type() { return type; }
    /** Damage/value payload. */
    public int value() { return value; }
    /** Alias for {@link #value()}. */
    public int damage() { return value; }
    /** Native limit field from the real hitsplat adder. */
    public int limit() { return limit; }
    /** Legacy alias for {@link #limit()}. */
    public int aux() { return limit; }
    /** Native delay field. */
    public int delay() { return delay; }
    /** Native cycle field. */
    public int cycle() { return cycle; }
    /** Current game tick captured at dispatch, or 0 if unavailable. */
    public int gameTick() { return gameTick; }
}
