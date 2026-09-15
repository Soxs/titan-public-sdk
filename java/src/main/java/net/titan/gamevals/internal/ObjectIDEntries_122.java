package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

final class ObjectIDEntries_122 {
    private ObjectIDEntries_122() {}

    static GamevalEntry[] entries() {
        return new GamevalEntry[] {
            new GamevalEntry(62464, "CRAB_BASS_MULTI_CUTSCENE", "crab_bass_multi_cutscene", "loctypes"),
            new GamevalEntry(62465, "CRAB_DRUMS", "Coconut shell", "loctypes"),
            new GamevalEntry(62466, "CRAB_DRUMS_MULTI_1", "crab_drums_multi_1", "loctypes"),
            new GamevalEntry(62467, "CRAB_DRUMS_MULTI_2", "crab_drums_multi_2", "loctypes"),
            new GamevalEntry(62468, "CRAB_DRUMS_MULTI_3", "crab_drums_multi_3", "loctypes"),
            new GamevalEntry(62469, "CRAB_DRUMS_MULTI_4", "crab_drums_multi_4", "loctypes"),
            new GamevalEntry(62470, "CRAB_PALM_UPDATE01", "Palm", "loctypes"),
            new GamevalEntry(62471, "CRAB_PALM_UPDATE02", "Palm", "loctypes"),
            new GamevalEntry(62472, "CRAB_PALM_UPDATE03", "Palm", "loctypes"),
            new GamevalEntry(62473, "CRAB_MAGICTREE", "Investigate", "loctypes"),
            new GamevalEntry(62474, "CRAB_MAGICTREE_NOOP", "Magic tree", "loctypes"),
            new GamevalEntry(62475, "CRAB_AVIUM_TREE_1", "Investigate", "loctypes"),
            new GamevalEntry(62476, "CRAB_AVIUM_TREE_2", "Investigate", "loctypes"),
            new GamevalEntry(62477, "CRAB_GROUNDCOVER_PLANT4_WITHERED", "Investigate", "loctypes"),
            new GamevalEntry(62478, "CRAB_ENAKH_LEAFLESSBUSH", "Investigate", "loctypes"),
            new GamevalEntry(62479, "CRAB_CIRCLE_CRAB", "Circle", "loctypes"),
            new GamevalEntry(62480, "CRAB_CIRCLE_HUMAN", "Circle", "loctypes"),
            new GamevalEntry(62481, "CRAB_CIRCLE_CRAB_FINAL", "Nest", "loctypes"),
            new GamevalEntry(62482, "CRAB_CIRCLE_HUMAN_FINAL", "Nest", "loctypes"),
            new GamevalEntry(62483, "DOGQ_DEN_BED_MULTI", "dogq_den_bed_multi", "loctypes"),
            new GamevalEntry(62484, "DOGQ_DEN_BED_NOOP", "Rough bedding", "loctypes"),
            new GamevalEntry(62485, "DOGQ_DEN_BED", "Rough bedding", "loctypes"),
            new GamevalEntry(62486, "DOGQ_DEN_SCRAPS_MULTI", "dogq_den_scraps_multi", "loctypes"),
            new GamevalEntry(62487, "DOGQ_DEN_SCRAPS_NOOP", "Torn newspaper", "loctypes"),
            new GamevalEntry(62488, "DOGQ_DEN_SCRAPS", "Torn newspaper", "loctypes"),
            new GamevalEntry(62489, "DOGQ_DEN_BOX_MULTI", "dogq_den_box_multi", "loctypes"),
            new GamevalEntry(62490, "DOGQ_DEN_BOX_NOOP", "Chewed box", "loctypes"),
            new GamevalEntry(62491, "DOGQ_DEN_BOX", "Chewed box", "loctypes"),
            new GamevalEntry(62492, "DOGQ_GOBLIN_STUFFED_DOG_MULTI_QUEST", "dogq_goblin_stuffed_dog_multi_quest", "loctypes"),
            new GamevalEntry(62493, "DOGQ_GOBLIN_STUFFED_DOG_MULTI_OUTPOST", "dogq_goblin_stuffed_dog_multi_outpost", "loctypes"),
            new GamevalEntry(62494, "DOGQ_GOBLIN_STUFFED_DOG", "Stuffed dog", "loctypes"),
            new GamevalEntry(62495, "DOGQ_DOG_GUARD_HOUSE_MULTI", "dogq_dog_guard_house_multi", "loctypes"),
            new GamevalEntry(62496, "DOGQ_DOG_GUARD_BALL_MULTI", "dogq_dog_guard_ball_multi", "loctypes"),
            new GamevalEntry(62497, "DOGQ_DOG_GUARD_HOUSE", "dogq_dog_guard_house", "loctypes"),
            new GamevalEntry(62498, "DOGQ_DOGHOUSE01_DEFAULT01", "Doghouse", "loctypes"),
            new GamevalEntry(62499, "DOGQ_SHELTER01_HUMAN01", "Shelter", "loctypes"),
            new GamevalEntry(62500, "DOGQ_DOGHOUSE01_KENNEL01", "Kennel", "loctypes"),
            new GamevalEntry(62501, "DOGQ_DOGHOUSE01_BALLL01", "Ball", "loctypes"),
            new GamevalEntry(62502, "DOGQ_DOGHOUSE01_BALLL02", "Ball", "loctypes"),
            new GamevalEntry(62503, "DOGQ_BOWL01_FOOD01", "Food bowl", "loctypes"),
            new GamevalEntry(62504, "DOGQ_BOWL01_WATER01", "Water bowl", "loctypes"),
            new GamevalEntry(62505, "DOGQ_WHEEL01_BARROW01", "Wheelbarrow", "loctypes"),
            new GamevalEntry(62506, "DOGQ_FENCE_WOODEN01_PLANK01", "dogq_fence_wooden01_plank01", "loctypes"),
            new GamevalEntry(62507, "DOGQ_FENCE_WOODEN01_PLANK01_NONBLOCKING", "dogq_fence_wooden01_plank01_nonblocking", "loctypes"),
            new GamevalEntry(62508, "DOGQ_FENCE_WOODEN01_PLANK01_BROKEN01", "dogq_fence_wooden01_plank01_broken01", "loctypes"),
            new GamevalEntry(62509, "WOODENSUPPORT2", "woodensupport2", "loctypes"),
        };
    }

    static Optional<GamevalEntry> byId(int id) {
        for (GamevalEntry entry : entries()) {
            if (entry.id() == id) return Optional.of(entry);
        }
        return Optional.empty();
    }

}
