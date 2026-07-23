package net.titan.api.utils;

import net.titan.api.MenuAction;
import net.titan.api.Prayer;
import net.titan.api.Titan;
import net.titan.gamevals.InterfaceID;

/**
 * Prayer active-state and widget-toggle helpers.
 *
 * <p>The widget mapping is deliberately literal. It does not inspect the
 * selected prayerbook, unlock state, widget name, or visibility before
 * dispatching the mapped prayer-book component.</p>
 */
public final class Prayers {
    private static final int[] PRAYER_WIDGETS = {
        // Standard prayer book.
        InterfaceID.Prayerbook.PRAYER1,   // THICK_SKIN
        InterfaceID.Prayerbook.PRAYER2,   // BURST_OF_STRENGTH
        InterfaceID.Prayerbook.PRAYER3,   // CLARITY_OF_THOUGHT
        InterfaceID.Prayerbook.PRAYER19,  // SHARP_EYE
        InterfaceID.Prayerbook.PRAYER22,  // MYSTIC_WILL
        InterfaceID.Prayerbook.PRAYER4,   // ROCK_SKIN
        InterfaceID.Prayerbook.PRAYER5,   // SUPERHUMAN_STRENGTH
        InterfaceID.Prayerbook.PRAYER6,   // IMPROVED_REFLEXES
        InterfaceID.Prayerbook.PRAYER7,   // RAPID_RESTORE
        InterfaceID.Prayerbook.PRAYER8,   // RAPID_HEAL
        InterfaceID.Prayerbook.PRAYER9,   // PROTECT_ITEM
        InterfaceID.Prayerbook.PRAYER20,  // HAWK_EYE
        InterfaceID.Prayerbook.PRAYER23,  // MYSTIC_LORE
        InterfaceID.Prayerbook.PRAYER10,  // STEEL_SKIN
        InterfaceID.Prayerbook.PRAYER11,  // ULTIMATE_STRENGTH
        InterfaceID.Prayerbook.PRAYER12,  // INCREDIBLE_REFLEXES
        InterfaceID.Prayerbook.PRAYER13,  // PROTECT_FROM_MAGIC
        InterfaceID.Prayerbook.PRAYER14,  // PROTECT_FROM_MISSILES
        InterfaceID.Prayerbook.PRAYER15,  // PROTECT_FROM_MELEE
        InterfaceID.Prayerbook.PRAYER21,  // EAGLE_EYE
        InterfaceID.Prayerbook.PRAYER24,  // MYSTIC_MIGHT
        InterfaceID.Prayerbook.PRAYER16,  // RETRIBUTION
        InterfaceID.Prayerbook.PRAYER17,  // REDEMPTION
        InterfaceID.Prayerbook.PRAYER18,  // SMITE
        InterfaceID.Prayerbook.PRAYER26,  // CHIVALRY
        InterfaceID.Prayerbook.PRAYER21,  // DEADEYE
        InterfaceID.Prayerbook.PRAYER24,  // MYSTIC_VIGOUR
        InterfaceID.Prayerbook.PRAYER27,  // PIETY
        InterfaceID.Prayerbook.PRAYER29,  // PRESERVE
        InterfaceID.Prayerbook.PRAYER25,  // RIGOUR
        InterfaceID.Prayerbook.PRAYER28,  // AUGURY

        // Ruinous Powers, in Prayer enum order.
        InterfaceID.Prayerbook.PRAYER12,  // RP_REJUVENATION
        InterfaceID.Prayerbook.PRAYER1,   // RP_ANCIENT_STRENGTH
        InterfaceID.Prayerbook.PRAYER2,   // RP_ANCIENT_SIGHT
        InterfaceID.Prayerbook.PRAYER3,   // RP_ANCIENT_WILL
        InterfaceID.Prayerbook.PRAYER24,  // RP_PROTECT_ITEM
        InterfaceID.Prayerbook.PRAYER13,  // RP_RUINOUS_GRACE
        InterfaceID.Prayerbook.PRAYER10,  // RP_DAMPEN_MAGIC
        InterfaceID.Prayerbook.PRAYER9,   // RP_DAMPEN_RANGED
        InterfaceID.Prayerbook.PRAYER8,   // RP_DAMPEN_MELEE
        InterfaceID.Prayerbook.PRAYER4,   // RP_TRINITAS
        InterfaceID.Prayerbook.PRAYER16,  // RP_BERSERKER
        InterfaceID.Prayerbook.PRAYER11,  // RP_PURGE
        InterfaceID.Prayerbook.PRAYER15,  // RP_METABOLISE
        InterfaceID.Prayerbook.PRAYER22,  // RP_REBUKE
        InterfaceID.Prayerbook.PRAYER23,  // RP_VINDICATION
        InterfaceID.Prayerbook.PRAYER5,   // RP_DECIMATE
        InterfaceID.Prayerbook.PRAYER6,   // RP_ANNIHILATE
        InterfaceID.Prayerbook.PRAYER7,   // RP_VAPORISE
        InterfaceID.Prayerbook.PRAYER17,  // RP_FUMUS_VOW
        InterfaceID.Prayerbook.PRAYER19,  // RP_UMBRA_VOW
        InterfaceID.Prayerbook.PRAYER18,  // RP_CRUORS_VOW
        InterfaceID.Prayerbook.PRAYER20,  // RP_GLACIES_VOW
        InterfaceID.Prayerbook.PRAYER14,  // RP_WRATH
        InterfaceID.Prayerbook.PRAYER21,  // RP_INTENSIFY
    };

    static {
        if (PRAYER_WIDGETS.length != Prayer.values().length) {
            throw new ExceptionInInitializerError(
                "Update the prayer widget table when Prayer changes");
        }
    }

    private Prayers() {}

    /** Return whether the requested prayer's active-state varbit is set. */
    public static boolean isActive(Prayer prayer) {
        return Boolean.TRUE.equals(activeState(prayer));
    }

    /**
     * Always click the widget slot associated with {@code prayer}.
     *
     * @return true when the interaction was queued
     */
    public static boolean toggle(Prayer prayer) {
        if (!valid(prayer)) return false;
        try {
            return Titan.client().widget(widgetPackedId(prayer))
                .filter(widget -> widget.exists())
                .map(widget -> widget.interact(MenuAction.CC_OP, 1, -1))
                .orElse(false);
        } catch (IllegalStateException ignored) {
            return false;
        }
    }

    /**
     * Idempotently set {@code prayer} to the requested active state.
     *
     * @return true when no change was needed or the interaction was queued
     */
    public static boolean setActive(Prayer prayer, boolean enabled) {
        if (!valid(prayer)) return false;
        Boolean active = activeState(prayer);
        if (active == null) return false;
        return active == enabled || toggle(prayer);
    }

    /** Idempotently enable {@code prayer}. */
    public static boolean enable(Prayer prayer) {
        return setActive(prayer, true);
    }

    /** Idempotently disable {@code prayer}. */
    public static boolean disable(Prayer prayer) {
        return setActive(prayer, false);
    }

    private static boolean valid(Prayer prayer) {
        return prayer != null && prayer.ordinal() < PRAYER_WIDGETS.length;
    }

    private static int widgetPackedId(Prayer prayer) {
        return PRAYER_WIDGETS[prayer.ordinal()];
    }

    private static Boolean activeState(Prayer prayer) {
        if (!valid(prayer)) return null;
        try {
            int value = Titan.client().varbit(prayer.varbitId());
            return value < 0 ? null : value != 0;
        } catch (IllegalStateException ignored) {
            return null;
        }
    }
}
