package net.titan.api.pills;

import net.titan.api.panel.PanelTone;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * One preview pill (SDK 142): text, a {@link PanelTone} and an optional
 * countdown drawn after the text as {@code HH:MM:SS}. Immutable; the
 * {@code with} methods return a changed copy.
 *
 * <pre>{@code
 * PreviewPill.of("Breaking:")
 *     .withTone(PanelTone.INFO)
 *     .withCountdown(Duration.ofMinutes(12));   // "Breaking: 00:12:00", ticking down
 * }</pre>
 */
public final class PreviewPill {
    private final String text;
    private final PanelTone tone;
    private final Duration countdown;

    private PreviewPill(String text, PanelTone tone, Duration countdown) {
        this.text = text;
        this.tone = tone;
        this.countdown = countdown;
    }

    /** A neutral pill showing {@code text}. */
    public static PreviewPill of(String text) {
        return new PreviewPill(Objects.requireNonNull(text, "text"), PanelTone.NEUTRAL, null);
    }

    /** A neutral pill showing only a countdown of {@code remaining} from now. */
    public static PreviewPill countdown(Duration remaining) {
        return of("").withCountdown(Objects.requireNonNull(remaining, "remaining"));
    }

    /** This pill in {@code tone}. */
    public PreviewPill withTone(PanelTone tone) {
        return new PreviewPill(text, Objects.requireNonNull(tone, "tone"), countdown);
    }

    /**
     * This pill with a live countdown of {@code remaining} from the moment it
     * is set, drawn after the text. The controller ticks it, so set it once.
     * Negative counts as zero; past 999:59:59 is shortened. Null removes it.
     */
    public PreviewPill withCountdown(Duration remaining) {
        return new PreviewPill(text, tone, remaining);
    }

    /** Drawn first. May be empty when the pill has a countdown. */
    public String text() {
        return text;
    }

    public PanelTone tone() {
        return tone;
    }

    /** Time left, measured from when the pill is set; empty for no countdown. */
    public Optional<Duration> countdown() {
        return Optional.ofNullable(countdown);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof PreviewPill)) return false;
        PreviewPill that = (PreviewPill) other;
        return text.equals(that.text) && tone == that.tone
            && Objects.equals(countdown, that.countdown);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, tone, countdown);
    }

    @Override
    public String toString() {
        return "PreviewPill{text=" + text + ", tone=" + tone
            + (countdown == null ? "" : ", countdown=" + countdown) + "}";
    }
}
