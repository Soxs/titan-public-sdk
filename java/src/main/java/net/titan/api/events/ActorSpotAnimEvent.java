package net.titan.api.events;

import net.titan.api.Actor;
import net.titan.api.NPC;
import net.titan.api.Player;

/**
 * Fired when the native client applies an actor-attached spot animation.
 * Clear/removal ids are filtered before dispatch. Added in SDK 76.
 */
public final class ActorSpotAnimEvent {
    private final Actor actor;
    private final int actorType;
    private final String kind;
    private final int indexOrId;
    private final String actorName;
    private final int slot;
    private final int id;
    private final int height;
    private final int delay;
    private final int cycle;
    private final int gameTick;

    public ActorSpotAnimEvent(Actor actor, int actorType, String kind, int indexOrId,
                    String actorName, int slot, int id, int height,
                    int delay, int cycle, int gameTick) {
        this.actor = actor;
        this.actorType = actorType;
        this.kind = kind == null ? "none" : kind;
        this.indexOrId = indexOrId;
        this.actorName = actorName == null ? "" : actorName;
        this.slot = slot;
        this.id = id;
        this.height = height;
        this.delay = delay;
        this.cycle = cycle;
        this.gameTick = gameTick;
    }

    public Actor actor() { return actor; }
    public Player player() { return actor instanceof Player ? (Player) actor : null; }
    public NPC npc() { return actor instanceof NPC ? (NPC) actor : null; }
    public int actorType() { return actorType; }
    public String kind() { return kind; }
    public int indexOrId() { return indexOrId; }
    public String actorName() { return actorName; }
    public int slot() { return slot; }
    public int id() { return id; }
    public int height() { return height; }
    public int delay() { return delay; }
    public int cycle() { return cycle; }
    public int gameTick() { return gameTick; }
}
