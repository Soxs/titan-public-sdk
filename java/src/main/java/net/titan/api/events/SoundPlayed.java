package net.titan.api.events;

/**
 * Fired when the native client plays a sound. Covers two kinds (see
 * {@link #kind()}): queued synth/wave sound effects (captured at the queue
 * drain) and MIDI jingles (captured at {@code PlayJingle}). Call
 * {@link #consume()} to suppress that single sound's native playback. To mute
 * all sounds globally, use {@code client.setAudioPlaybackDisabled(true)}
 * instead. Added in SDK 69.
 *
 * <p>Field meaning by kind:
 * <ul>
 *   <li>Synth: {@code soundId} JagFX id, {@code loops} loop count,
 *       {@code packedPos} packed position/range, {@code durationMs} = -1.</li>
 *   <li>Jingle: {@code soundId} jingle id, {@code durationMs} ms,
 *       {@code loops}/{@code packedPos} = -1.</li>
 * </ul>
 */
public final class SoundPlayed {
    /** Queued JagFX/wave sound effect (combat, spells, NPCs, area sounds). */
    public static final int KIND_SYNTH = 0;
    /** MIDI jingle (level-ups, quests, music stings). */
    public static final int KIND_JINGLE = 1;

    private final int kind;
    private final int soundId;
    private final int loops;
    private final int durationMs;
    private final int packedPos;
    private final int gameTick;
    private boolean consumed;

    public SoundPlayed(int kind, int soundId, int loops, int durationMs,
                       int packedPos, int gameTick) {
        this(kind, soundId, loops, durationMs, packedPos, gameTick, false);
    }

    public SoundPlayed(int kind, int soundId, int loops, int durationMs,
                       int packedPos, int gameTick, boolean consumed) {
        this.kind = kind;
        this.soundId = soundId;
        this.loops = loops;
        this.durationMs = durationMs;
        this.packedPos = packedPos;
        this.gameTick = gameTick;
        this.consumed = consumed;
    }

    /** Source of the sound: {@link #KIND_SYNTH} or {@link #KIND_JINGLE}. */
    public int kind() { return kind; }
    public boolean isSynth() { return kind == KIND_SYNTH; }
    public boolean isJingle() { return kind == KIND_JINGLE; }

    /** Synth JagFX id or jingle id. */
    public int soundId() { return soundId; }
    /** Synth loop count; -1 for jingles. */
    public int loops() { return loops; }
    /** Jingle duration in ms; -1 for synths. */
    public int durationMs() { return durationMs; }
    /** Synth packed position/range; -1 for jingles. */
    public int packedPos() { return packedPos; }
    /** Current game tick captured at dispatch, or 0 if unavailable. */
    public int gameTick() { return gameTick; }

    /** Suppress this single sound: the native game playback call is skipped. */
    public void consume() { consumed = true; }
    public boolean consumed() { return consumed; }
}
