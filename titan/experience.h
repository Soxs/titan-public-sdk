#pragma once

#include <array>
#include <cstdint>
#include <stdexcept>

namespace titan::experience {

inline constexpr int32_t maxRealLevel = 99;
inline constexpr int32_t maxVirtualLevel = 126;
inline constexpr int32_t maxSkillXp = 200000000;

namespace detail {
// Canonical integer thresholds from pinned RuneLite 1.13.6 Experience.
// No floating-point or host state is involved at runtime. Reproduce/verify with
// scripts/check_experience_parity.py against the hash-pinned original class.
inline constexpr std::array<int32_t, 126> xpForLevels = {
    0, 83, 174, 276, 388, 512, 650, 801,
    969, 1154, 1358, 1584, 1833, 2107, 2411, 2746,
    3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028,
    7842, 8740, 9730, 10824, 12031, 13363, 14833, 16456,
    18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224,
    41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014,
    91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040,
    203254, 224466, 247886, 273742, 302288, 333804, 368599, 407015,
    449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257,
    992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068,
    2192818, 2421087, 2673114, 2951373, 3258594, 3597792, 3972294, 4385776,
    4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577,
    10692629, 11805606, 13034431, 14391160, 15889109, 17542976, 19368992, 21385073,
    23611006, 26068632, 28782069, 31777943, 35085654, 38737661, 42769801, 47221641,
    52136869, 57563718, 63555443, 70170840, 77474828, 85539082, 94442737, 104273167,
    115126838, 127110260, 140341028, 154948977, 171077457, 188884740
};
} // namespace detail

/// Pure XP threshold for level 1..126. Throws std::invalid_argument outside it.
inline constexpr int32_t xpForLevel(int32_t level) {
    if (level < 1 || level > maxVirtualLevel)
        throw std::invalid_argument("level must be between 1 and 126");
    return detail::xpForLevels[static_cast<std::size_t>(level - 1)];
}

/// Pure virtual level 1..126. All nonnegative int32 XP is accepted, including
/// values above the informational 200M skill cap. Negative XP throws.
inline constexpr int32_t levelForXp(int32_t xp) {
    if (xp < 0) throw std::invalid_argument("xp must be nonnegative");
    std::size_t low = 0, high = detail::xpForLevels.size();
    while (low < high) {
        const auto middle = low + (high - low) / 2;
        if (detail::xpForLevels[middle] <= xp) low = middle + 1;
        else high = middle;
    }
    return static_cast<int32_t>(low);
}

} // namespace titan::experience
