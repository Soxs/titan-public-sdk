package net.titan.api.events;

import net.titan.api.Actor;
import java.util.Objects;

/** One accepted player/NPC utterance, including identical and empty text. Expiry is excluded. */
public final class OverheadTextChanged {
    private final Actor actor;
    private final String overheadText;
    private final int gameTick;
    public OverheadTextChanged(Actor actor, String overheadText, int gameTick) {
        this.actor = Objects.requireNonNull(actor, "actor");
        this.overheadText = Objects.requireNonNull(overheadText, "overheadText");
        this.gameTick = gameTick;
    }
    /** Actor identity captured with the accepted utterance, without slot re-resolution. */
    public Actor getActor() { return actor; }
    public Actor actor() { return actor; }
    /** Owned text, including embedded NULs and accepted empty strings. */
    public String getOverheadText() { return overheadText; }
    public String overheadText() { return overheadText; }
    public int gameTick() { return gameTick; }
}
