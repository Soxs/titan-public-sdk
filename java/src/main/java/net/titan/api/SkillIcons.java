package net.titan.api;

import java.util.Optional;
import net.titan.api.internal.TitanRuntime;

/** Host-owned skill artwork copied into immutable images. No game cache or
 * RuneLite runtime is required, and no third-party images ship in the SDK. */
public final class SkillIcons {
    private SkillIcons() {}

    /** Empty for an invalid skill or an unavailable host asset. Small selects
     * the original small icon, not a resized normal image. */
    public static Optional<SpriteImage> get(int skillId, boolean small) {
        if (skillId < 0 || skillId >= Skill.COUNT) return Optional.empty();
        return Optional.ofNullable(TitanRuntime.getUiAssetsBackend().skillIcon(skillId, small));
    }
    public static Optional<SpriteImage> get(Skill skill, boolean small) {
        return skill == null ? Optional.empty() : get(skill.id(), small);
    }
    public static Optional<SpriteImage> get(int skillId) { return get(skillId, false); }
    public static Optional<SpriteImage> get(Skill skill) { return get(skill, false); }
}
