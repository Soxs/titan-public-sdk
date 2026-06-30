package net.titan.api.utils;

import net.titan.api.GroundItem;
import net.titan.api.InventoryItem;
import net.titan.api.MenuAction;
import net.titan.api.NPC;
import net.titan.api.Player;
import net.titan.api.ScreenPoint;
import net.titan.api.Skill;
import net.titan.api.Titan;
import net.titan.api.TileObject;
import net.titan.api.VarPlayerId;
import net.titan.api.Widget;
import net.titan.gamevals.InterfaceID;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public final class Magic {
    public static final int SPELLBOOK_VARBIT = 4070;
    private static final int AUTOCAST_VARP = 108;
    private static final int ARDOUGNE_TELEPORT_VARP = 165;
    private static final int TROLLHEIM_TELEPORT_VARP = 335;
    private static final Duration HOME_TELEPORT_COOLDOWN = Duration.ofMinutes(30);
    private static final String CAST_ACTION_TEXT = "Cast";
    private static final String BLANK_TARGET_TEXT = "";

    private Magic() {}

    public enum SpellBook {
        STANDARD(0),
        ANCIENT(1),
        LUNAR(2),
        NECROMANCY(3);

        private final int id;

        SpellBook(int id) {
            this.id = id;
        }

        public int id() { return id; }

        public static SpellBook fromId(int id) {
            for (SpellBook value : values()) {
                if (value.id == id) return value;
            }
            return STANDARD;
        }
    }

    public enum Standard {
        HOME_TELEPORT,
        VARROCK_TELEPORT,
        LUMBRIDGE_TELEPORT,
        FALADOR_TELEPORT,
        TELEPORT_TO_HOUSE,
        CAMELOT_TELEPORT,
        ARDOUGNE_TELEPORT,
        WATCHTOWER_TELEPORT,
        TROLLHEIM_TELEPORT,
        TELEPORT_TO_APE_ATOLL,
        TELEPORT_TO_KOUREND,
        TELEOTHER_LUMBRIDGE,
        TELEOTHER_FALADOR,
        TELEPORT_TO_BOUNTY_TARGET,
        TELEOTHER_CAMELOT,
        WIND_STRIKE,
        WATER_STRIKE,
        EARTH_STRIKE,
        FIRE_STRIKE,
        WIND_BOLT,
        WATER_BOLT,
        EARTH_BOLT,
        FIRE_BOLT,
        WIND_BLAST,
        WATER_BLAST,
        EARTH_BLAST,
        FIRE_BLAST,
        WIND_WAVE,
        WATER_WAVE,
        EARTH_WAVE,
        FIRE_WAVE,
        WIND_SURGE,
        WATER_SURGE,
        EARTH_SURGE,
        FIRE_SURGE,
        SARADOMIN_STRIKE,
        CLAWS_OF_GUTHIX,
        FLAMES_OF_ZAMORAK,
        CRUMBLE_UNDEAD,
        IBAN_BLAST,
        MAGIC_DART,
        CONFUSE,
        WEAKEN,
        CURSE,
        BIND,
        SNARE,
        VULNERABILITY,
        ENFEEBLE,
        ENTANGLE,
        STUN,
        TELE_BLOCK,
        CHARGE,
        BONES_TO_BANANAS,
        LOW_LEVEL_ALCHEMY,
        SUPERHEAT_ITEM,
        HIGH_LEVEL_ALCHEMY,
        BONES_TO_PEACHES,
        LVL_1_ENCHANT,
        LVL_2_ENCHANT,
        LVL_3_ENCHANT,
        CHARGE_WATER_ORB,
        LVL_4_ENCHANT,
        CHARGE_EARTH_ORB,
        CHARGE_FIRE_ORB,
        CHARGE_AIR_ORB,
        LVL_5_ENCHANT,
        LVL_6_ENCHANT,
        LVL_7_ENCHANT,
        TELEKINETIC_GRAB
    }

    public enum Ancient {
        EDGEVILLE_HOME_TELEPORT,
        PADDEWWA_TELEPORT,
        SENNTISTEN_TELEPORT,
        KHARYRLL_TELEPORT,
        LASSAR_TELEPORT,
        DAREEYAK_TELEPORT,
        CARRALLANGER_TELEPORT,
        BOUNTY_TARGET_TELEPORT,
        ANNAKARL_TELEPORT,
        GHORROCK_TELEPORT,
        SMOKE_RUSH,
        SHADOW_RUSH,
        BLOOD_RUSH,
        ICE_RUSH,
        SMOKE_BURST,
        SHADOW_BURST,
        BLOOD_BURST,
        ICE_BURST,
        SMOKE_BLITZ,
        SHADOW_BLITZ,
        BLOOD_BLITZ,
        ICE_BLITZ,
        SMOKE_BARRAGE,
        SHADOW_BARRAGE,
        BLOOD_BARRAGE,
        ICE_BARRAGE
    }

    public enum Lunar {
        LUNAR_HOME_TELEPORT,
        MOONCLAN_TELEPORT,
        TELE_GROUP_MOONCLAN,
        OURANIA_TELEPORT,
        WATERBIRTH_TELEPORT,
        TELE_GROUP_WATERBIRTH,
        BARBARIAN_TELEPORT,
        TELE_GROUP_BARBARIAN,
        KHAZARD_TELEPORT,
        TELE_GROUP_KHAZARD,
        FISHING_GUILD_TELEPORT,
        TELE_GROUP_FISHING_GUILD,
        CATHERBY_TELEPORT,
        TELE_GROUP_CATHERBY,
        ICE_PLATEAU_TELEPORT,
        TELE_GROUP_ICE_PLATEAU,
        MONSTER_EXAMINE,
        CURE_OTHER,
        CURE_ME,
        CURE_GROUP,
        STAT_SPY,
        DREAM,
        STAT_RESTORE_POT_SHARE,
        BOOST_POTION_SHARE,
        ENERGY_TRANSFER,
        HEAL_OTHER,
        VENGEANCE_OTHER,
        VENGEANCE,
        HEAL_GROUP,
        BAKE_PIE,
        GEOMANCY,
        CURE_PLANT,
        NPC_CONTACT,
        HUMIDIFY,
        HUNTER_KIT,
        SPIN_FLAX,
        SUPERGLASS_MAKE,
        TAN_LEATHER,
        STRING_JEWELLERY,
        MAGIC_IMBUE,
        FERTILE_SOIL,
        PLANK_MAKE,
        RECHARGE_DRAGONSTONE,
        SPELLBOOK_SWAP
    }

    public enum Necromancy {
        ARCEUUS_HOME_TELEPORT,
        ARCEUUS_LIBRARY_TELEPORT,
        DRAYNOR_MANOR_TELEPORT,
        BATTLEFRONT_TELEPORT,
        MIND_ALTAR_TELEPORT,
        RESPAWN_TELEPORT,
        SALVE_GRAVEYARD_TELEPORT,
        FENKENSTRAINS_CASTLE_TELEPORT,
        WEST_ARDOUGNE_TELEPORT,
        HARMONY_ISLAND_TELEPORT,
        CEMETERY_TELEPORT,
        BARROWS_TELEPORT,
        APE_ATOLL_TELEPORT,
        GHOSTLY_GRASP,
        SKELETAL_GRASP,
        UNDEAD_GRASP,
        INFERIOR_DEMONBANE,
        SUPERIOR_DEMONBANE,
        DARK_DEMONBANE,
        LESSER_CORRUPTION,
        GREATER_CORRUPTION,
        RESURRECT_LESSER_GHOST,
        RESURRECT_LESSER_SKELETON,
        RESURRECT_LESSER_ZOMBIE,
        RESURRECT_SUPERIOR_GHOST,
        RESURRECT_SUPERIOR_SKELETON,
        RESURRECT_SUPERIOR_ZOMBIE,
        RESURRECT_GREATER_GHOST,
        RESURRECT_GREATER_SKELETON,
        RESURRECT_GREATER_ZOMBIE,
        DARK_LURE,
        MARK_OF_DARKNESS,
        WARD_OF_ARCEUUS,
        BASIC_REANIMATION,
        ADEPT_REANIMATION,
        EXPERT_REANIMATION,
        MASTER_REANIMATION,
        DEMONIC_OFFERING,
        SINISTER_OFFERING,
        SHADOW_VEIL,
        VILE_VIGOUR,
        DEGRIME,
        RESURRECT_CROPS,
        DEATH_CHARGE
    }

    public static final class SpellInfo {
        private final String name;
        private final int level;
        private final int widget;
        private final SpellBook book;
        private final boolean members;
        private final int menuEntryId;

        public SpellInfo(String name, int level, int widget,
                         SpellBook book, boolean members, int menuEntryId) {
            this.name = name == null ? "" : name;
            this.level = level;
            this.widget = widget;
            this.book = book == null ? SpellBook.STANDARD : book;
            this.members = members;
            this.menuEntryId = menuEntryId;
        }

        public String name() { return name; }
        public int level() { return level; }
        public int widget() { return widget; }
        public SpellBook book() { return book; }
        public boolean members() { return members; }
        public int menuEntryId() { return menuEntryId; }
    }

    public static final class WidgetTarget {
        private final int packedId;
        private final int childIndex;
        private final int itemId;

        public WidgetTarget(int packedId, int childIndex, int itemId) {
            this.packedId = packedId;
            this.childIndex = childIndex;
            this.itemId = itemId;
        }

        public int packedId() { return packedId; }
        public int childIndex() { return childIndex; }
        public int itemId() { return itemId; }

        public boolean castOn(SpellInfo spell) { return Magic.castOn(spell, this); }
        public boolean castOn(Standard spell) { return Magic.castOn(spell, this); }
        public boolean castOn(Ancient spell) { return Magic.castOn(spell, this); }
        public boolean castOn(Lunar spell) { return Magic.castOn(spell, this); }
        public boolean castOn(Necromancy spell) { return Magic.castOn(spell, this); }
    }

    private static final class TargetAction {
        private final int opcode;
        private final int identifier;
        private final int param0;
        private final int param1;
        private final boolean widgetInteract;
        private int worldViewId;
        private int targetPlane = -1;
        private int targetSizeX = 1;
        private int targetSizeY = 1;
        private int targetLayer = -1;
        private long targetEntityPtr;
        private long targetPackedId;

        private TargetAction(int opcode, int identifier, int param0, int param1) {
            this(opcode, identifier, param0, param1, false);
        }

        private TargetAction(int opcode, int identifier, int param0, int param1,
                             boolean widgetInteract) {
            this.opcode = opcode;
            this.identifier = identifier;
            this.param0 = param0;
            this.param1 = param1;
            this.widgetInteract = widgetInteract;
        }

        private TargetAction worldViewId(int value) {
            this.worldViewId = value;
            return this;
        }

        private TargetAction targetPlane(int value) {
            this.targetPlane = value;
            return this;
        }

        private TargetAction targetSize(int x, int y) {
            this.targetSizeX = Math.max(1, x);
            this.targetSizeY = Math.max(1, y);
            return this;
        }

        private TargetAction targetLayer(int value) {
            this.targetLayer = value;
            return this;
        }

        private TargetAction targetEntityPtr(long value) {
            this.targetEntityPtr = value;
            return this;
        }

        private TargetAction targetPackedId(long value) {
            this.targetPackedId = value;
            return this;
        }
    }

    public static SpellInfo info(Standard spell) {
        if (spell == null) return empty();
        switch (spell) {
            case HOME_TELEPORT: return spellInfo("HOME_TELEPORT", 0, InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD, SpellBook.STANDARD, false, 0);
            case VARROCK_TELEPORT: return spellInfo("VARROCK_TELEPORT", 25, InterfaceID.MagicSpellbook.VARROCK_TELEPORT, SpellBook.STANDARD, false, 0);
            case LUMBRIDGE_TELEPORT: return spellInfo("LUMBRIDGE_TELEPORT", 31, InterfaceID.MagicSpellbook.LUMBRIDGE_TELEPORT, SpellBook.STANDARD, false, 0);
            case FALADOR_TELEPORT: return spellInfo("FALADOR_TELEPORT", 37, InterfaceID.MagicSpellbook.FALADOR_TELEPORT, SpellBook.STANDARD, false, 0);
            case TELEPORT_TO_HOUSE: return spellInfo("TELEPORT_TO_HOUSE", 40, InterfaceID.MagicSpellbook.TELEPORT_TO_YOUR_HOUSE, SpellBook.STANDARD, true, 0);
            case CAMELOT_TELEPORT: return spellInfo("CAMELOT_TELEPORT", 45, InterfaceID.MagicSpellbook.CAMELOT_TELEPORT, SpellBook.STANDARD, true, 0);
            case ARDOUGNE_TELEPORT: return spellInfo("ARDOUGNE_TELEPORT", 51, InterfaceID.MagicSpellbook.ARDOUGNE_TELEPORT, SpellBook.STANDARD, true, 0);
            case WATCHTOWER_TELEPORT: return spellInfo("WATCHTOWER_TELEPORT", 58, InterfaceID.MagicSpellbook.WATCHTOWER_TELEPORT, SpellBook.STANDARD, true, 0);
            case TROLLHEIM_TELEPORT: return spellInfo("TROLLHEIM_TELEPORT", 61, InterfaceID.MagicSpellbook.TROLLHEIM_TELEPORT, SpellBook.STANDARD, true, 0);
            case TELEPORT_TO_APE_ATOLL: return spellInfo("TELEPORT_TO_APE_ATOLL", 64, InterfaceID.MagicSpellbook.APE_TELEPORT, SpellBook.STANDARD, true, 0);
            case TELEPORT_TO_KOUREND: return spellInfo("TELEPORT_TO_KOUREND", 69, InterfaceID.MagicSpellbook.KOUREND_TELEPORT, SpellBook.STANDARD, true, 0);
            case TELEOTHER_LUMBRIDGE: return spellInfo("TELEOTHER_LUMBRIDGE", 74, InterfaceID.MagicSpellbook.TELEOTHER_LUMBRIDGE, SpellBook.STANDARD, true, 0);
            case TELEOTHER_FALADOR: return spellInfo("TELEOTHER_FALADOR", 82, InterfaceID.MagicSpellbook.TELEOTHER_FALADOR, SpellBook.STANDARD, true, 0);
            case TELEPORT_TO_BOUNTY_TARGET: return spellInfo("TELEPORT_TO_BOUNTY_TARGET", 85, InterfaceID.MagicSpellbook.BOUNTY_TARGET, SpellBook.STANDARD, true, 0);
            case TELEOTHER_CAMELOT: return spellInfo("TELEOTHER_CAMELOT", 90, InterfaceID.MagicSpellbook.TELEOTHER_CAMELOT, SpellBook.STANDARD, true, 0);
            case WIND_STRIKE: return spellInfo("WIND_STRIKE", 1, InterfaceID.MagicSpellbook.WIND_STRIKE, SpellBook.STANDARD, false, 0);
            case WATER_STRIKE: return spellInfo("WATER_STRIKE", 5, InterfaceID.MagicSpellbook.WATER_STRIKE, SpellBook.STANDARD, false, 0);
            case EARTH_STRIKE: return spellInfo("EARTH_STRIKE", 9, InterfaceID.MagicSpellbook.EARTH_STRIKE, SpellBook.STANDARD, false, 0);
            case FIRE_STRIKE: return spellInfo("FIRE_STRIKE", 13, InterfaceID.MagicSpellbook.FIRE_STRIKE, SpellBook.STANDARD, false, 0);
            case WIND_BOLT: return spellInfo("WIND_BOLT", 17, InterfaceID.MagicSpellbook.WIND_BOLT, SpellBook.STANDARD, false, 0);
            case WATER_BOLT: return spellInfo("WATER_BOLT", 23, InterfaceID.MagicSpellbook.WATER_BOLT, SpellBook.STANDARD, false, 0);
            case EARTH_BOLT: return spellInfo("EARTH_BOLT", 29, InterfaceID.MagicSpellbook.EARTH_BOLT, SpellBook.STANDARD, false, 0);
            case FIRE_BOLT: return spellInfo("FIRE_BOLT", 35, InterfaceID.MagicSpellbook.FIRE_BOLT, SpellBook.STANDARD, false, 0);
            case WIND_BLAST: return spellInfo("WIND_BLAST", 41, InterfaceID.MagicSpellbook.WIND_BLAST, SpellBook.STANDARD, false, 0);
            case WATER_BLAST: return spellInfo("WATER_BLAST", 47, InterfaceID.MagicSpellbook.WATER_BLAST, SpellBook.STANDARD, false, 0);
            case EARTH_BLAST: return spellInfo("EARTH_BLAST", 53, InterfaceID.MagicSpellbook.EARTH_BLAST, SpellBook.STANDARD, false, 0);
            case FIRE_BLAST: return spellInfo("FIRE_BLAST", 59, InterfaceID.MagicSpellbook.FIRE_BLAST, SpellBook.STANDARD, false, 0);
            case WIND_WAVE: return spellInfo("WIND_WAVE", 62, InterfaceID.MagicSpellbook.WIND_WAVE, SpellBook.STANDARD, true, 0);
            case WATER_WAVE: return spellInfo("WATER_WAVE", 65, InterfaceID.MagicSpellbook.WATER_WAVE, SpellBook.STANDARD, true, 0);
            case EARTH_WAVE: return spellInfo("EARTH_WAVE", 70, InterfaceID.MagicSpellbook.EARTH_WAVE, SpellBook.STANDARD, true, 0);
            case FIRE_WAVE: return spellInfo("FIRE_WAVE", 75, InterfaceID.MagicSpellbook.FIRE_WAVE, SpellBook.STANDARD, true, 0);
            case WIND_SURGE: return spellInfo("WIND_SURGE", 81, InterfaceID.MagicSpellbook.WIND_SURGE, SpellBook.STANDARD, true, 0);
            case WATER_SURGE: return spellInfo("WATER_SURGE", 85, InterfaceID.MagicSpellbook.WATER_SURGE, SpellBook.STANDARD, true, 0);
            case EARTH_SURGE: return spellInfo("EARTH_SURGE", 90, InterfaceID.MagicSpellbook.EARTH_SURGE, SpellBook.STANDARD, true, 0);
            case FIRE_SURGE: return spellInfo("FIRE_SURGE", 95, InterfaceID.MagicSpellbook.FIRE_SURGE, SpellBook.STANDARD, true, 0);
            case SARADOMIN_STRIKE: return spellInfo("SARADOMIN_STRIKE", 60, InterfaceID.MagicSpellbook.SARADOMIN_STRIKE, SpellBook.STANDARD, true, 0);
            case CLAWS_OF_GUTHIX: return spellInfo("CLAWS_OF_GUTHIX", 60, InterfaceID.MagicSpellbook.CLAWS_OF_GUTHIX, SpellBook.STANDARD, true, 0);
            case FLAMES_OF_ZAMORAK: return spellInfo("FLAMES_OF_ZAMORAK", 60, InterfaceID.MagicSpellbook.FLAMES_OF_ZAMORAK, SpellBook.STANDARD, true, 0);
            case CRUMBLE_UNDEAD: return spellInfo("CRUMBLE_UNDEAD", 39, InterfaceID.MagicSpellbook.CRUMBLE_UNDEAD, SpellBook.STANDARD, false, 0);
            case IBAN_BLAST: return spellInfo("IBAN_BLAST", 50, InterfaceID.MagicSpellbook.IBAN_BLAST, SpellBook.STANDARD, true, 0);
            case MAGIC_DART: return spellInfo("MAGIC_DART", 50, InterfaceID.MagicSpellbook.MAGIC_DART, SpellBook.STANDARD, true, 0);
            case CONFUSE: return spellInfo("CONFUSE", 3, InterfaceID.MagicSpellbook.CONFUSE, SpellBook.STANDARD, false, 0);
            case WEAKEN: return spellInfo("WEAKEN", 11, InterfaceID.MagicSpellbook.WEAKEN, SpellBook.STANDARD, false, 0);
            case CURSE: return spellInfo("CURSE", 19, InterfaceID.MagicSpellbook.CURSE, SpellBook.STANDARD, false, 0);
            case BIND: return spellInfo("BIND", 20, InterfaceID.MagicSpellbook.BIND, SpellBook.STANDARD, false, 0);
            case SNARE: return spellInfo("SNARE", 50, InterfaceID.MagicSpellbook.SNARE, SpellBook.STANDARD, false, 0);
            case VULNERABILITY: return spellInfo("VULNERABILITY", 66, InterfaceID.MagicSpellbook.VULNERABILITY, SpellBook.STANDARD, true, 0);
            case ENFEEBLE: return spellInfo("ENFEEBLE", 73, InterfaceID.MagicSpellbook.ENFEEBLE, SpellBook.STANDARD, true, 0);
            case ENTANGLE: return spellInfo("ENTANGLE", 79, InterfaceID.MagicSpellbook.ENTANGLE, SpellBook.STANDARD, true, 0);
            case STUN: return spellInfo("STUN", 80, InterfaceID.MagicSpellbook.STUN, SpellBook.STANDARD, true, 0);
            case TELE_BLOCK: return spellInfo("TELE_BLOCK", 85, InterfaceID.MagicSpellbook.TELEPORT_BLOCK, SpellBook.STANDARD, false, 0);
            case CHARGE: return spellInfo("CHARGE", 80, InterfaceID.MagicSpellbook.CHARGE, SpellBook.STANDARD, true, 0);
            case BONES_TO_BANANAS: return spellInfo("BONES_TO_BANANAS", 15, InterfaceID.MagicSpellbook.BONES_BANANAS, SpellBook.STANDARD, false, 0);
            case LOW_LEVEL_ALCHEMY: return spellInfo("LOW_LEVEL_ALCHEMY", 21, InterfaceID.MagicSpellbook.LOW_ALCHEMY, SpellBook.STANDARD, false, 0);
            case SUPERHEAT_ITEM: return spellInfo("SUPERHEAT_ITEM", 43, InterfaceID.MagicSpellbook.SUPERHEAT, SpellBook.STANDARD, false, 0);
            case HIGH_LEVEL_ALCHEMY: return spellInfo("HIGH_LEVEL_ALCHEMY", 55, InterfaceID.MagicSpellbook.HIGH_ALCHEMY, SpellBook.STANDARD, false, 0);
            case BONES_TO_PEACHES: return spellInfo("BONES_TO_PEACHES", 60, InterfaceID.MagicSpellbook.BONES_PEACHES, SpellBook.STANDARD, true, 0);
            case LVL_1_ENCHANT: return spellInfo("LVL_1_ENCHANT", 7, InterfaceID.MagicSpellbook.ENCHANT_1, SpellBook.STANDARD, false, 0);
            case LVL_2_ENCHANT: return spellInfo("LVL_2_ENCHANT", 27, InterfaceID.MagicSpellbook.ENCHANT_2, SpellBook.STANDARD, false, 0);
            case LVL_3_ENCHANT: return spellInfo("LVL_3_ENCHANT", 49, InterfaceID.MagicSpellbook.ENCHANT_3, SpellBook.STANDARD, false, 0);
            case CHARGE_WATER_ORB: return spellInfo("CHARGE_WATER_ORB", 56, InterfaceID.MagicSpellbook.CHARGE_WATER_ORB, SpellBook.STANDARD, true, 0);
            case LVL_4_ENCHANT: return spellInfo("LVL_4_ENCHANT", 57, InterfaceID.MagicSpellbook.ENCHANT_4, SpellBook.STANDARD, false, 0);
            case CHARGE_EARTH_ORB: return spellInfo("CHARGE_EARTH_ORB", 60, InterfaceID.MagicSpellbook.CHARGE_EARTH_ORB, SpellBook.STANDARD, true, 0);
            case CHARGE_FIRE_ORB: return spellInfo("CHARGE_FIRE_ORB", 63, InterfaceID.MagicSpellbook.CHARGE_FIRE_ORB, SpellBook.STANDARD, true, 0);
            case CHARGE_AIR_ORB: return spellInfo("CHARGE_AIR_ORB", 66, InterfaceID.MagicSpellbook.CHARGE_AIR_ORB, SpellBook.STANDARD, true, 0);
            case LVL_5_ENCHANT: return spellInfo("LVL_5_ENCHANT", 68, InterfaceID.MagicSpellbook.ENCHANT_5, SpellBook.STANDARD, true, 0);
            case LVL_6_ENCHANT: return spellInfo("LVL_6_ENCHANT", 87, InterfaceID.MagicSpellbook.ENCHANT_6, SpellBook.STANDARD, true, 0);
            case LVL_7_ENCHANT: return spellInfo("LVL_7_ENCHANT", 93, InterfaceID.MagicSpellbook.ENCHANT_7, SpellBook.STANDARD, true, 0);
            case TELEKINETIC_GRAB: return spellInfo("TELEKINETIC_GRAB", 31, InterfaceID.MagicSpellbook.TELEGRAB, SpellBook.STANDARD, false, 0);
            default: return empty();
        }
    }

    public static SpellInfo info(Ancient spell) {
        if (spell == null) return empty();
        switch (spell) {
            case EDGEVILLE_HOME_TELEPORT: return spellInfo("EDGEVILLE_HOME_TELEPORT", 0, InterfaceID.MagicSpellbook.TELEPORT_HOME_ZAROS, SpellBook.ANCIENT, true, 0);
            case PADDEWWA_TELEPORT: return spellInfo("PADDEWWA_TELEPORT", 54, InterfaceID.MagicSpellbook.ZAROSTELEPORT1, SpellBook.ANCIENT, true, 0);
            case SENNTISTEN_TELEPORT: return spellInfo("SENNTISTEN_TELEPORT", 60, InterfaceID.MagicSpellbook.ZAROSTELEPORT2, SpellBook.ANCIENT, true, 0);
            case KHARYRLL_TELEPORT: return spellInfo("KHARYRLL_TELEPORT", 66, InterfaceID.MagicSpellbook.ZAROSTELEPORT3, SpellBook.ANCIENT, true, 0);
            case LASSAR_TELEPORT: return spellInfo("LASSAR_TELEPORT", 72, InterfaceID.MagicSpellbook.ZAROSTELEPORT4, SpellBook.ANCIENT, true, 0);
            case DAREEYAK_TELEPORT: return spellInfo("DAREEYAK_TELEPORT", 78, InterfaceID.MagicSpellbook.ZAROSTELEPORT5, SpellBook.ANCIENT, true, 0);
            case CARRALLANGER_TELEPORT: return spellInfo("CARRALLANGER_TELEPORT", 84, InterfaceID.MagicSpellbook.ZAROSTELEPORT6, SpellBook.ANCIENT, true, 0);
            case BOUNTY_TARGET_TELEPORT: return spellInfo("BOUNTY_TARGET_TELEPORT", 85, InterfaceID.MagicSpellbook.BOUNTY_TARGET, SpellBook.ANCIENT, true, 0);
            case ANNAKARL_TELEPORT: return spellInfo("ANNAKARL_TELEPORT", 90, InterfaceID.MagicSpellbook.ZAROSTELEPORT7, SpellBook.ANCIENT, true, 0);
            case GHORROCK_TELEPORT: return spellInfo("GHORROCK_TELEPORT", 96, InterfaceID.MagicSpellbook.ZAROSTELEPORT8, SpellBook.ANCIENT, true, 0);
            case SMOKE_RUSH: return spellInfo("SMOKE_RUSH", 50, InterfaceID.MagicSpellbook.SMOKE_RUSH, SpellBook.ANCIENT, true, 0);
            case SHADOW_RUSH: return spellInfo("SHADOW_RUSH", 52, InterfaceID.MagicSpellbook.SHADOW_RUSH, SpellBook.ANCIENT, true, 0);
            case BLOOD_RUSH: return spellInfo("BLOOD_RUSH", 56, InterfaceID.MagicSpellbook.BLOOD_RUSH, SpellBook.ANCIENT, true, 0);
            case ICE_RUSH: return spellInfo("ICE_RUSH", 58, InterfaceID.MagicSpellbook.ICE_RUSH, SpellBook.ANCIENT, true, 0);
            case SMOKE_BURST: return spellInfo("SMOKE_BURST", 62, InterfaceID.MagicSpellbook.SMOKE_BURST, SpellBook.ANCIENT, true, 0);
            case SHADOW_BURST: return spellInfo("SHADOW_BURST", 64, InterfaceID.MagicSpellbook.SHADOW_BURST, SpellBook.ANCIENT, true, 0);
            case BLOOD_BURST: return spellInfo("BLOOD_BURST", 68, InterfaceID.MagicSpellbook.BLOOD_BURST, SpellBook.ANCIENT, true, 0);
            case ICE_BURST: return spellInfo("ICE_BURST", 70, InterfaceID.MagicSpellbook.ICE_BURST, SpellBook.ANCIENT, true, 0);
            case SMOKE_BLITZ: return spellInfo("SMOKE_BLITZ", 74, InterfaceID.MagicSpellbook.SMOKE_BLITZ, SpellBook.ANCIENT, true, 0);
            case SHADOW_BLITZ: return spellInfo("SHADOW_BLITZ", 76, InterfaceID.MagicSpellbook.SHADOW_BLITZ, SpellBook.ANCIENT, true, 0);
            case BLOOD_BLITZ: return spellInfo("BLOOD_BLITZ", 80, InterfaceID.MagicSpellbook.BLOOD_BLITZ, SpellBook.ANCIENT, true, 0);
            case ICE_BLITZ: return spellInfo("ICE_BLITZ", 82, InterfaceID.MagicSpellbook.ICE_BLITZ, SpellBook.ANCIENT, true, 0);
            case SMOKE_BARRAGE: return spellInfo("SMOKE_BARRAGE", 86, InterfaceID.MagicSpellbook.SMOKE_BARRAGE, SpellBook.ANCIENT, true, 0);
            case SHADOW_BARRAGE: return spellInfo("SHADOW_BARRAGE", 88, InterfaceID.MagicSpellbook.SHADOW_BARRAGE, SpellBook.ANCIENT, true, 0);
            case BLOOD_BARRAGE: return spellInfo("BLOOD_BARRAGE", 92, InterfaceID.MagicSpellbook.BLOOD_BARRAGE, SpellBook.ANCIENT, true, 0);
            case ICE_BARRAGE: return spellInfo("ICE_BARRAGE", 94, InterfaceID.MagicSpellbook.ICE_BARRAGE, SpellBook.ANCIENT, true, 0);
            default: return empty();
        }
    }

    public static SpellInfo info(Lunar spell) {
        if (spell == null) return empty();
        switch (spell) {
            case LUNAR_HOME_TELEPORT: return spellInfo("LUNAR_HOME_TELEPORT", 0, InterfaceID.MagicSpellbook.TELEPORT_HOME_LUNAR, SpellBook.LUNAR, true, 0);
            case MOONCLAN_TELEPORT: return spellInfo("MOONCLAN_TELEPORT", 69, InterfaceID.MagicSpellbook.TELE_MOONCLAN, SpellBook.LUNAR, true, 0);
            case TELE_GROUP_MOONCLAN: return spellInfo("TELE_GROUP_MOONCLAN", 70, InterfaceID.MagicSpellbook.TELE_GROUP_MOONCLAN, SpellBook.LUNAR, true, 0);
            case OURANIA_TELEPORT: return spellInfo("OURANIA_TELEPORT", 71, InterfaceID.MagicSpellbook.OURANIA_TELEPORT, SpellBook.LUNAR, true, 0);
            case WATERBIRTH_TELEPORT: return spellInfo("WATERBIRTH_TELEPORT", 72, InterfaceID.MagicSpellbook.TELE_WATERBIRTH, SpellBook.LUNAR, true, 0);
            case TELE_GROUP_WATERBIRTH: return spellInfo("TELE_GROUP_WATERBIRTH", 73, InterfaceID.MagicSpellbook.TELE_GROUP_WATERBIRTH, SpellBook.LUNAR, true, 0);
            case BARBARIAN_TELEPORT: return spellInfo("BARBARIAN_TELEPORT", 75, InterfaceID.MagicSpellbook.TELE_BARB_OUT, SpellBook.LUNAR, true, 0);
            case TELE_GROUP_BARBARIAN: return spellInfo("TELE_GROUP_BARBARIAN", 76, InterfaceID.MagicSpellbook.TELE_GROUP_BARBARIAN, SpellBook.LUNAR, true, 0);
            case KHAZARD_TELEPORT: return spellInfo("KHAZARD_TELEPORT", 78, InterfaceID.MagicSpellbook.TELE_KHAZARD, SpellBook.LUNAR, true, 0);
            case TELE_GROUP_KHAZARD: return spellInfo("TELE_GROUP_KHAZARD", 79, InterfaceID.MagicSpellbook.TELE_GROUP_KHAZARD, SpellBook.LUNAR, true, 0);
            case FISHING_GUILD_TELEPORT: return spellInfo("FISHING_GUILD_TELEPORT", 85, InterfaceID.MagicSpellbook.TELE_FISH, SpellBook.LUNAR, true, 0);
            case TELE_GROUP_FISHING_GUILD: return spellInfo("TELE_GROUP_FISHING_GUILD", 86, InterfaceID.MagicSpellbook.TELE_GROUP_FISHING_GUILD, SpellBook.LUNAR, true, 0);
            case CATHERBY_TELEPORT: return spellInfo("CATHERBY_TELEPORT", 87, InterfaceID.MagicSpellbook.TELE_CATHER, SpellBook.LUNAR, true, 0);
            case TELE_GROUP_CATHERBY: return spellInfo("TELE_GROUP_CATHERBY", 88, InterfaceID.MagicSpellbook.TELE_GROUP_CATHERBY, SpellBook.LUNAR, true, 0);
            case ICE_PLATEAU_TELEPORT: return spellInfo("ICE_PLATEAU_TELEPORT", 89, InterfaceID.MagicSpellbook.TELE_GHORROCK, SpellBook.LUNAR, true, 0);
            case TELE_GROUP_ICE_PLATEAU: return spellInfo("TELE_GROUP_ICE_PLATEAU", 90, InterfaceID.MagicSpellbook.TELE_GROUP_GHORROCK, SpellBook.LUNAR, true, 0);
            case MONSTER_EXAMINE: return spellInfo("MONSTER_EXAMINE", 66, InterfaceID.MagicSpellbook.MONSTER_EXAMINE, SpellBook.LUNAR, true, 0);
            case CURE_OTHER: return spellInfo("CURE_OTHER", 66, InterfaceID.MagicSpellbook.CURE_OTHER, SpellBook.LUNAR, true, 0);
            case CURE_ME: return spellInfo("CURE_ME", 66, InterfaceID.MagicSpellbook.CURE_ME, SpellBook.LUNAR, true, 0);
            case CURE_GROUP: return spellInfo("CURE_GROUP", 66, InterfaceID.MagicSpellbook.CURE_GROUP, SpellBook.LUNAR, true, 0);
            case STAT_SPY: return spellInfo("STAT_SPY", 66, InterfaceID.MagicSpellbook.STAT_SPY, SpellBook.LUNAR, true, 0);
            case DREAM: return spellInfo("DREAM", 66, InterfaceID.MagicSpellbook.DREAM, SpellBook.LUNAR, true, 0);
            case STAT_RESTORE_POT_SHARE: return spellInfo("STAT_RESTORE_POT_SHARE", 66, InterfaceID.MagicSpellbook.REST_POT_SHARE, SpellBook.LUNAR, true, 0);
            case BOOST_POTION_SHARE: return spellInfo("BOOST_POTION_SHARE", 66, InterfaceID.MagicSpellbook.STREN_POT_SHARE, SpellBook.LUNAR, true, 0);
            case ENERGY_TRANSFER: return spellInfo("ENERGY_TRANSFER", 66, InterfaceID.MagicSpellbook.ENERGY_TRANS, SpellBook.LUNAR, true, 0);
            case HEAL_OTHER: return spellInfo("HEAL_OTHER", 66, InterfaceID.MagicSpellbook.HEAL_OTHER, SpellBook.LUNAR, true, 0);
            case VENGEANCE_OTHER: return spellInfo("VENGEANCE_OTHER", 66, InterfaceID.MagicSpellbook.VENGEANCE_OTHER, SpellBook.LUNAR, true, 0);
            case VENGEANCE: return spellInfo("VENGEANCE", 66, InterfaceID.MagicSpellbook.VENGEANCE, SpellBook.LUNAR, true, 0);
            case HEAL_GROUP: return spellInfo("HEAL_GROUP", 66, InterfaceID.MagicSpellbook.HEAL_GROUP, SpellBook.LUNAR, true, 0);
            case BAKE_PIE: return spellInfo("BAKE_PIE", 66, InterfaceID.MagicSpellbook.BAKE_PIE, SpellBook.LUNAR, true, 0);
            case GEOMANCY: return spellInfo("GEOMANCY", 66, InterfaceID.MagicSpellbook.GEOMANCY, SpellBook.LUNAR, true, 0);
            case CURE_PLANT: return spellInfo("CURE_PLANT", 66, InterfaceID.MagicSpellbook.CURE_PLANT, SpellBook.LUNAR, true, 0);
            case NPC_CONTACT: return spellInfo("NPC_CONTACT", 66, InterfaceID.MagicSpellbook.NPC_CONTACT, SpellBook.LUNAR, true, 0);
            case HUMIDIFY: return spellInfo("HUMIDIFY", 66, InterfaceID.MagicSpellbook.HUMIDIFY, SpellBook.LUNAR, true, 0);
            case HUNTER_KIT: return spellInfo("HUNTER_KIT", 66, InterfaceID.MagicSpellbook.HUNTER_KIT, SpellBook.LUNAR, true, 0);
            case SPIN_FLAX: return spellInfo("SPIN_FLAX", 66, InterfaceID.MagicSpellbook.SPIN_FLAX, SpellBook.LUNAR, true, 0);
            case SUPERGLASS_MAKE: return spellInfo("SUPERGLASS_MAKE", 66, InterfaceID.MagicSpellbook.SUPERGLASS, SpellBook.LUNAR, true, 0);
            case TAN_LEATHER: return spellInfo("TAN_LEATHER", 66, InterfaceID.MagicSpellbook.TAN_LEATHER, SpellBook.LUNAR, true, 0);
            case STRING_JEWELLERY: return spellInfo("STRING_JEWELLERY", 66, InterfaceID.MagicSpellbook.STRING_JEWEL, SpellBook.LUNAR, true, 0);
            case MAGIC_IMBUE: return spellInfo("MAGIC_IMBUE", 66, InterfaceID.MagicSpellbook.MAGIC_IMBUE, SpellBook.LUNAR, true, 0);
            case FERTILE_SOIL: return spellInfo("FERTILE_SOIL", 66, InterfaceID.MagicSpellbook.FERTILE_SOIL, SpellBook.LUNAR, true, 0);
            case PLANK_MAKE: return spellInfo("PLANK_MAKE", 66, InterfaceID.MagicSpellbook.PLANK_MAKE, SpellBook.LUNAR, true, 0);
            case RECHARGE_DRAGONSTONE: return spellInfo("RECHARGE_DRAGONSTONE", 66, InterfaceID.MagicSpellbook.RECHARGE_DRAGONSTONE, SpellBook.LUNAR, true, 0);
            case SPELLBOOK_SWAP: return spellInfo("SPELLBOOK_SWAP", 66, InterfaceID.MagicSpellbook.SPELLBOOK_SWAP, SpellBook.LUNAR, true, 0);
            default: return empty();
        }
    }

    public static SpellInfo info(Necromancy spell) {
        if (spell == null) return empty();
        switch (spell) {
            case ARCEUUS_HOME_TELEPORT: return spellInfo("ARCEUUS_HOME_TELEPORT", 1, InterfaceID.MagicSpellbook.TELEPORT_HOME_ARCEUUS, SpellBook.NECROMANCY, true, 0);
            case ARCEUUS_LIBRARY_TELEPORT: return spellInfo("ARCEUUS_LIBRARY_TELEPORT", 6, InterfaceID.MagicSpellbook.TELEPORT_ARCEUUS_LIBRARY, SpellBook.NECROMANCY, true, 0);
            case DRAYNOR_MANOR_TELEPORT: return spellInfo("DRAYNOR_MANOR_TELEPORT", 17, InterfaceID.MagicSpellbook.TELEPORT_DRAYNOR_MANOR, SpellBook.NECROMANCY, true, 0);
            case BATTLEFRONT_TELEPORT: return spellInfo("BATTLEFRONT_TELEPORT", 23, InterfaceID.MagicSpellbook.TELEPORT_BATTLEFRONT, SpellBook.NECROMANCY, true, 0);
            case MIND_ALTAR_TELEPORT: return spellInfo("MIND_ALTAR_TELEPORT", 28, InterfaceID.MagicSpellbook.TELEPORT_MIND_ALTAR, SpellBook.NECROMANCY, true, 0);
            case RESPAWN_TELEPORT: return spellInfo("RESPAWN_TELEPORT", 34, InterfaceID.MagicSpellbook.TELEPORT_RESPAWN, SpellBook.NECROMANCY, true, 0);
            case SALVE_GRAVEYARD_TELEPORT: return spellInfo("SALVE_GRAVEYARD_TELEPORT", 40, InterfaceID.MagicSpellbook.TELEPORT_SALVE_GRAVEYARD, SpellBook.NECROMANCY, true, 0);
            case FENKENSTRAINS_CASTLE_TELEPORT: return spellInfo("FENKENSTRAINS_CASTLE_TELEPORT", 48, InterfaceID.MagicSpellbook.TELEPORT_FENKENSTRAIN_CASTLE, SpellBook.NECROMANCY, true, 0);
            case WEST_ARDOUGNE_TELEPORT: return spellInfo("WEST_ARDOUGNE_TELEPORT", 61, InterfaceID.MagicSpellbook.TELEPORT_WEST_ARDOUGNE, SpellBook.NECROMANCY, true, 0);
            case HARMONY_ISLAND_TELEPORT: return spellInfo("HARMONY_ISLAND_TELEPORT", 65, InterfaceID.MagicSpellbook.TELEPORT_HARMONY_ISLAND, SpellBook.NECROMANCY, true, 0);
            case CEMETERY_TELEPORT: return spellInfo("CEMETERY_TELEPORT", 71, InterfaceID.MagicSpellbook.TELEPORT_CEMETERY, SpellBook.NECROMANCY, true, 0);
            case BARROWS_TELEPORT: return spellInfo("BARROWS_TELEPORT", 83, InterfaceID.MagicSpellbook.TELEPORT_BARROWS, SpellBook.NECROMANCY, true, 0);
            case APE_ATOLL_TELEPORT: return spellInfo("APE_ATOLL_TELEPORT", 90, InterfaceID.MagicSpellbook.TELEPORT_APE_ATOLL_DUNGEON, SpellBook.NECROMANCY, true, 0);
            case GHOSTLY_GRASP: return spellInfo("GHOSTLY_GRASP", 35, InterfaceID.MagicSpellbook.GHOSTLY_GRASP, SpellBook.NECROMANCY, true, 0);
            case SKELETAL_GRASP: return spellInfo("SKELETAL_GRASP", 56, InterfaceID.MagicSpellbook.SKELETAL_GRASP, SpellBook.NECROMANCY, true, 0);
            case UNDEAD_GRASP: return spellInfo("UNDEAD_GRASP", 79, InterfaceID.MagicSpellbook.UNDEAD_GRASP, SpellBook.NECROMANCY, true, 0);
            case INFERIOR_DEMONBANE: return spellInfo("INFERIOR_DEMONBANE", 44, InterfaceID.MagicSpellbook.INFERIOR_DEMONBANE, SpellBook.NECROMANCY, true, 0);
            case SUPERIOR_DEMONBANE: return spellInfo("SUPERIOR_DEMONBANE", 62, InterfaceID.MagicSpellbook.SUPERIOR_DEMONBANE, SpellBook.NECROMANCY, true, 0);
            case DARK_DEMONBANE: return spellInfo("DARK_DEMONBANE", 82, InterfaceID.MagicSpellbook.DARK_DEMONBANE, SpellBook.NECROMANCY, true, 0);
            case LESSER_CORRUPTION: return spellInfo("LESSER_CORRUPTION", 64, InterfaceID.MagicSpellbook.LESSER_CORRUPTION, SpellBook.NECROMANCY, true, 0);
            case GREATER_CORRUPTION: return spellInfo("GREATER_CORRUPTION", 85, InterfaceID.MagicSpellbook.GREATER_CORRUPTION, SpellBook.NECROMANCY, true, 0);
            case RESURRECT_LESSER_GHOST: return spellInfo("RESURRECT_LESSER_GHOST", 38, InterfaceID.MagicSpellbook.RESURRECT_LESSER_GHOST, SpellBook.NECROMANCY, true, 0);
            case RESURRECT_LESSER_SKELETON: return spellInfo("RESURRECT_LESSER_SKELETON", 38, InterfaceID.MagicSpellbook.RESURRECT_LESSER_SKELETON, SpellBook.NECROMANCY, true, 0);
            case RESURRECT_LESSER_ZOMBIE: return spellInfo("RESURRECT_LESSER_ZOMBIE", 38, InterfaceID.MagicSpellbook.RESURRECT_LESSER_ZOMBIE, SpellBook.NECROMANCY, true, 0);
            case RESURRECT_SUPERIOR_GHOST: return spellInfo("RESURRECT_SUPERIOR_GHOST", 57, InterfaceID.MagicSpellbook.RESURRECT_SUPERIOR_GHOST, SpellBook.NECROMANCY, true, 0);
            case RESURRECT_SUPERIOR_SKELETON: return spellInfo("RESURRECT_SUPERIOR_SKELETON", 57, InterfaceID.MagicSpellbook.RESURRECT_SUPERIOR_SKELETON, SpellBook.NECROMANCY, true, 0);
            case RESURRECT_SUPERIOR_ZOMBIE: return spellInfo("RESURRECT_SUPERIOR_ZOMBIE", 57, InterfaceID.MagicSpellbook.RESURRECT_SUPERIOR_ZOMBIE, SpellBook.NECROMANCY, true, 0);
            case RESURRECT_GREATER_GHOST: return spellInfo("RESURRECT_GREATER_GHOST", 76, InterfaceID.MagicSpellbook.RESURRECT_GREATER_GHOST, SpellBook.NECROMANCY, true, 0);
            case RESURRECT_GREATER_SKELETON: return spellInfo("RESURRECT_GREATER_SKELETON", 76, InterfaceID.MagicSpellbook.RESURRECT_GREATER_SKELETON, SpellBook.NECROMANCY, true, 0);
            case RESURRECT_GREATER_ZOMBIE: return spellInfo("RESURRECT_GREATER_ZOMBIE", 76, InterfaceID.MagicSpellbook.RESURRECT_GREATER_ZOMBIE, SpellBook.NECROMANCY, true, 0);
            case DARK_LURE: return spellInfo("DARK_LURE", 50, InterfaceID.MagicSpellbook.DARK_LURE, SpellBook.NECROMANCY, true, 0);
            case MARK_OF_DARKNESS: return spellInfo("MARK_OF_DARKNESS", 59, InterfaceID.MagicSpellbook.MARK_OF_DARKNESS, SpellBook.NECROMANCY, true, 0);
            case WARD_OF_ARCEUUS: return spellInfo("WARD_OF_ARCEUUS", 73, InterfaceID.MagicSpellbook.WARD_OF_ARCEUUS, SpellBook.NECROMANCY, true, 0);
            case BASIC_REANIMATION: return spellInfo("BASIC_REANIMATION", 16, InterfaceID.MagicSpellbook.REANIMATION_BASIC, SpellBook.NECROMANCY, true, 0);
            case ADEPT_REANIMATION: return spellInfo("ADEPT_REANIMATION", 41, InterfaceID.MagicSpellbook.REANIMATION_ADEPT, SpellBook.NECROMANCY, true, 0);
            case EXPERT_REANIMATION: return spellInfo("EXPERT_REANIMATION", 72, InterfaceID.MagicSpellbook.REANIMATION_EXPERT, SpellBook.NECROMANCY, true, 0);
            case MASTER_REANIMATION: return spellInfo("MASTER_REANIMATION", 90, InterfaceID.MagicSpellbook.REANIMATION_MASTER, SpellBook.NECROMANCY, true, 0);
            case DEMONIC_OFFERING: return spellInfo("DEMONIC_OFFERING", 84, InterfaceID.MagicSpellbook.DEMONIC_OFFERING, SpellBook.NECROMANCY, true, 0);
            case SINISTER_OFFERING: return spellInfo("SINISTER_OFFERING", 92, InterfaceID.MagicSpellbook.SINISTER_OFFERING, SpellBook.NECROMANCY, true, 0);
            case SHADOW_VEIL: return spellInfo("SHADOW_VEIL", 47, InterfaceID.MagicSpellbook.SHADOW_VEIL, SpellBook.NECROMANCY, true, 0);
            case VILE_VIGOUR: return spellInfo("VILE_VIGOUR", 66, InterfaceID.MagicSpellbook.VILE_VIGOUR, SpellBook.NECROMANCY, true, 0);
            case DEGRIME: return spellInfo("DEGRIME", 70, InterfaceID.MagicSpellbook.DEGRIME, SpellBook.NECROMANCY, true, 0);
            case RESURRECT_CROPS: return spellInfo("RESURRECT_CROPS", 78, InterfaceID.MagicSpellbook.RESURRECT_CROPS, SpellBook.NECROMANCY, true, 0);
            case DEATH_CHARGE: return spellInfo("DEATH_CHARGE", 80, InterfaceID.MagicSpellbook.DEATH_CHARGE, SpellBook.NECROMANCY, true, 0);
            default: return empty();
        }
    }

    public static SpellBook currentSpellBook() {
        return SpellBook.fromId(Titan.client().varbit(SPELLBOOK_VARBIT));
    }

    public static int magicLevel() {
        return Titan.client().realSkillLevel(Skill.MAGIC);
    }

    public static boolean isAutoCasting() {
        return Titan.client().varp(AUTOCAST_VARP) != 0;
    }

    public static Instant lastHomeTeleportUsage() {
        return Instant.EPOCH.plus(Duration.ofMinutes(Titan.client().varp(VarPlayerId.LAST_HOME_TELEPORT)));
    }

    public static boolean isHomeTeleportOnCooldown() {
        return lastHomeTeleportUsage().plus(HOME_TELEPORT_COOLDOWN).isAfter(Instant.now());
    }

    public static boolean canCast(SpellInfo spell) {
        if (spell == null || spell.name().isEmpty() || spell.widget() == 0) return false;
        if (currentSpellBook() != spell.book()) return false;
        if (spell.level() > Titan.client().realSkillLevel(Skill.MAGIC)
            || spell.level() > Titan.client().boostedSkillLevel(Skill.MAGIC)) {
            return false;
        }
        if (spell.book() == SpellBook.STANDARD) {
            if (spell.widget() == InterfaceID.MagicSpellbook.ARDOUGNE_TELEPORT
                && Titan.client().varp(ARDOUGNE_TELEPORT_VARP) < 30) {
                return false;
            }
            if (spell.widget() == InterfaceID.MagicSpellbook.TROLLHEIM_TELEPORT
                && Titan.client().varp(TROLLHEIM_TELEPORT_VARP) < 110) {
                return false;
            }
        }
        return true;
    }

    public static boolean canCast(Standard spell) { return canCast(info(spell)); }
    public static boolean canCast(Ancient spell) { return canCast(info(spell)); }
    public static boolean canCast(Lunar spell) { return canCast(info(spell)); }
    public static boolean canCast(Necromancy spell) { return canCast(info(spell)); }

    public static boolean select(SpellInfo spell) {
        return cast(spell);
    }

    public static boolean select(Standard spell) { return select(info(spell)); }
    public static boolean select(Ancient spell) { return select(info(spell)); }
    public static boolean select(Lunar spell) { return select(info(spell)); }
    public static boolean select(Necromancy spell) { return select(info(spell)); }

    public static boolean cast(SpellInfo spell) {
        return isValidSpell(spell) &&
            Titan.client().widgetInteract(MenuAction.WIDGET_TARGET, 0, -1, spell.widget());
    }

    public static boolean cast(SpellInfo spell, int actionIndex) {
        return cast(spell, actionIndex, MenuAction.CC_OP);
    }

    public static boolean cast(SpellInfo spell, int actionIndex, int opcode) {
        return actionIndex >= 0 && isValidSpell(spell) &&
            Titan.client().widgetInteract(opcode, actionIndex + 1, -1, spell.widget());
    }

    public static boolean cast(Standard spell) { return cast(info(spell)); }
    public static boolean cast(Ancient spell) { return cast(info(spell)); }
    public static boolean cast(Lunar spell) { return cast(info(spell)); }
    public static boolean cast(Necromancy spell) { return cast(info(spell)); }
    public static boolean cast(Standard spell, int actionIndex) { return cast(info(spell), actionIndex); }
    public static boolean cast(Ancient spell, int actionIndex) { return cast(info(spell), actionIndex); }
    public static boolean cast(Lunar spell, int actionIndex) { return cast(info(spell), actionIndex); }
    public static boolean cast(Necromancy spell, int actionIndex) { return cast(info(spell), actionIndex); }
    public static boolean cast(Standard spell, int actionIndex, int opcode) { return cast(info(spell), actionIndex, opcode); }
    public static boolean cast(Ancient spell, int actionIndex, int opcode) { return cast(info(spell), actionIndex, opcode); }
    public static boolean cast(Lunar spell, int actionIndex, int opcode) { return cast(info(spell), actionIndex, opcode); }
    public static boolean cast(Necromancy spell, int actionIndex, int opcode) { return cast(info(spell), actionIndex, opcode); }

    public static boolean castOn(SpellInfo spell, WidgetTarget target) {
        return castOnTarget(spell, targetAction(target));
    }

    public static boolean castOn(SpellInfo spell, Widget target) {
        return castOnTarget(spell, targetAction(target));
    }

    public static boolean castOn(SpellInfo spell, InventoryItem target) {
        return castOnTarget(spell, targetAction(target));
    }

    public static boolean castOn(SpellInfo spell, NPC target) {
        return castOnTarget(spell, targetAction(target));
    }

    public static boolean castOn(SpellInfo spell, Player target) {
        return castOnTarget(spell, targetAction(target));
    }

    public static boolean castOn(SpellInfo spell, GroundItem target) {
        return castOnTarget(spell, targetAction(target));
    }

    public static boolean castOn(SpellInfo spell, TileObject target) {
        return castOnTarget(spell, targetAction(target));
    }

    public static boolean castOn(Standard spell, WidgetTarget target) { return castOn(info(spell), target); }
    public static boolean castOn(Standard spell, Widget target) { return castOn(info(spell), target); }
    public static boolean castOn(Standard spell, InventoryItem target) { return castOn(info(spell), target); }
    public static boolean castOn(Standard spell, NPC target) { return castOn(info(spell), target); }
    public static boolean castOn(Standard spell, Player target) { return castOn(info(spell), target); }
    public static boolean castOn(Standard spell, GroundItem target) { return castOn(info(spell), target); }
    public static boolean castOn(Standard spell, TileObject target) { return castOn(info(spell), target); }

    public static boolean castOn(Ancient spell, WidgetTarget target) { return castOn(info(spell), target); }
    public static boolean castOn(Ancient spell, Widget target) { return castOn(info(spell), target); }
    public static boolean castOn(Ancient spell, InventoryItem target) { return castOn(info(spell), target); }
    public static boolean castOn(Ancient spell, NPC target) { return castOn(info(spell), target); }
    public static boolean castOn(Ancient spell, Player target) { return castOn(info(spell), target); }
    public static boolean castOn(Ancient spell, GroundItem target) { return castOn(info(spell), target); }
    public static boolean castOn(Ancient spell, TileObject target) { return castOn(info(spell), target); }

    public static boolean castOn(Lunar spell, WidgetTarget target) { return castOn(info(spell), target); }
    public static boolean castOn(Lunar spell, Widget target) { return castOn(info(spell), target); }
    public static boolean castOn(Lunar spell, InventoryItem target) { return castOn(info(spell), target); }
    public static boolean castOn(Lunar spell, NPC target) { return castOn(info(spell), target); }
    public static boolean castOn(Lunar spell, Player target) { return castOn(info(spell), target); }
    public static boolean castOn(Lunar spell, GroundItem target) { return castOn(info(spell), target); }
    public static boolean castOn(Lunar spell, TileObject target) { return castOn(info(spell), target); }

    public static boolean castOn(Necromancy spell, WidgetTarget target) { return castOn(info(spell), target); }
    public static boolean castOn(Necromancy spell, Widget target) { return castOn(info(spell), target); }
    public static boolean castOn(Necromancy spell, InventoryItem target) { return castOn(info(spell), target); }
    public static boolean castOn(Necromancy spell, NPC target) { return castOn(info(spell), target); }
    public static boolean castOn(Necromancy spell, Player target) { return castOn(info(spell), target); }
    public static boolean castOn(Necromancy spell, GroundItem target) { return castOn(info(spell), target); }
    public static boolean castOn(Necromancy spell, TileObject target) { return castOn(info(spell), target); }

    private static boolean castOnTarget(SpellInfo spell, TargetAction action) {
        return action != null && select(spell) && scheduleTargetAction(action);
    }

    private static SpellInfo spellInfo(String name, int level, int widget,
                                       SpellBook book, boolean members,
                                       int menuEntryId) {
        return new SpellInfo(name, level, widget, book, members, menuEntryId);
    }

    private static boolean isValidSpell(SpellInfo spell) {
        return spell != null && !spell.name().isEmpty() && spell.widget() != 0;
    }

    private static TargetAction targetAction(WidgetTarget target) {
        if (target == null || target.packedId() == 0) return null;
        return new TargetAction(
            MenuAction.WIDGET_TARGET_ON_WIDGET, 0, target.childIndex(), target.packedId(), true);
    }

    private static TargetAction targetAction(Widget target) {
        if (target == null || target.packedId() == 0) return null;
        return new TargetAction(
            MenuAction.WIDGET_TARGET_ON_WIDGET, 0, target.dynamicChildSlot(), target.packedId(), true);
    }

    private static TargetAction targetAction(InventoryItem target) {
        if (target == null || target.slot() < 0 || target.id() < 0) return null;
        return new TargetAction(
            MenuAction.WIDGET_TARGET_ON_WIDGET, 0, target.slot(), InterfaceID.Inventory.ITEMS, true);
    }

    private static TargetAction targetAction(NPC target) {
        if (target == null || target.hashIndex() < 0) return null;
        return new TargetAction(MenuAction.WIDGET_TARGET_ON_NPC, target.hashIndex(), 0, 0)
            .worldViewId(target.worldViewId())
            .targetPlane(target.plane())
            .targetSize(target.sizeX(), target.sizeY())
            .targetEntityPtr(target.entityPtr());
    }

    private static TargetAction targetAction(Player target) {
        if (target == null || target.hashIndex() < 0) return null;
        return new TargetAction(MenuAction.WIDGET_TARGET_ON_PLAYER, target.hashIndex(), 0, 0)
            .worldViewId(target.worldViewId())
            .targetPlane(target.plane())
            .targetEntityPtr(target.entityPtr());
    }

    private static TargetAction targetAction(GroundItem target) {
        if (target == null || target.id() < 0) return null;
        return new TargetAction(
            MenuAction.WIDGET_TARGET_ON_GROUND_ITEM, target.id(), target.tileX(), target.tileY())
            .worldViewId(target.worldViewId())
            .targetPlane(target.plane());
    }

    private static TargetAction targetAction(TileObject target) {
        if (target == null || target.id() < 0) return null;
        return new TargetAction(
            MenuAction.WIDGET_TARGET_ON_GAME_OBJECT, target.id(), target.tileX(), target.tileY())
            .worldViewId(target.worldViewId())
            .targetPlane(target.plane())
            .targetSize(target.sizeX(), target.sizeY())
            .targetLayer(target.layer())
            .targetEntityPtr(target.entityPtr())
            .targetPackedId(target.packedId());
    }

    private static boolean scheduleTargetAction(TargetAction action) {
        try {
            if (action.widgetInteract) {
                Titan.runOnClientTick(() -> Titan.client().widgetInteract(
                    action.opcode, action.identifier, action.param0, action.param1));
            } else {
                Titan.runOnClientTick(() -> {
                    Optional<ScreenPoint> click = Mouse.resolveActionClickPoint(
                        action.opcode, action.identifier, action.param0, action.param1,
                        action.worldViewId, action.targetPlane,
                        action.targetSizeX, action.targetSizeY,
                        action.targetLayer, action.targetEntityPtr,
                        action.targetPackedId);
                    if (!click.isPresent()) return;
                    ScreenPoint point = click.get();
                    long dispatchWorldViewId = action.worldViewId < 0
                        ? Titan.client().currentWorldViewId()
                        : action.worldViewId;
                    if (dispatchWorldViewId < 0) dispatchWorldViewId = 0;
                    Titan.client().invokeMenuAction(
                        action.opcode, action.identifier, action.param0, action.param1,
                        dispatchWorldViewId, point.x(), point.y(),
                        CAST_ACTION_TEXT, BLANK_TARGET_TEXT, false);
                });
            }
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static SpellInfo empty() {
        return new SpellInfo("", 0, 0, SpellBook.STANDARD, false, 0);
    }
}
